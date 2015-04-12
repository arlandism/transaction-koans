(ns transaction-koans.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as adapter]
            [clojure.edn :as edn]))

(def project-root (.getCanonicalPath (clojure.java.io/file ".")))

(def db-spec (edn/read-string
               (slurp
                 (clojure.java.io/file (str project-root "/config/database.edn")))))

(def conn-one (adapter/get-connection db-spec))
(def conn-two (adapter/get-connection db-spec))

(defn age-of-person [person]
  (:age (first (adapter/query
                 db-spec
                 ["select age from people where name = ?" person]))))

(testing "repeatable-read is handy, but sometimes we don't care about serialization"
  (.setAutoCommit conn-one false)
  (.setAutoCommit conn-two false)
  (.setTransactionIsolation conn-one java.sql.Connection/TRANSACTION_REPEATABLE_READ)
  (.setTransactionIsolation conn-two java.sql.Connection/TRANSACTION_REPEATABLE_READ) ; fix me
  (.execute (adapter/prepare-statement conn-one "update people set age = 27 where name = 'arlandis'"))
  ; attach a callback to the second thread so that we can be closer to guaranteeing
  ; it's attempted a lock grab before the first transaction commits,
  ; triggering the serializable error
  (def t2-lock-attempt (promise))
  (def t2 (future
            (deliver t2-lock-attempt "done")
            (.execute
              (adapter/prepare-statement
                conn-two
                "update people set age = 25 where name = 'arlandis'"))))
  @t2-lock-attempt ; block for t2
  (.commit conn-one)
  (try
    @t2
    (.commit conn-two)
    (catch Exception e)
    (finally
      (.close conn-one)
      (.close conn-two)
      (is (= 25 (age-of-person "arlandis"))))))
