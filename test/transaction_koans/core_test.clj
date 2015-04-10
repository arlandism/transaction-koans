(ns transaction-koans.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as adapter]
            [clojure.edn :as edn]))

(def project-root (.getCanonicalPath (clojure.java.io/file ".")))

(def db-spec (edn/read-string
               (slurp
                 (clojure.java.io/file (str project-root "/config/database.edn")))))

(adapter/with-db-connection [db-conn db-spec]
 (deftest repeatable-read
  (is (= 0 1)))

(deftest serializable
  (is (= 1 2)))

(deftest read-committed
  (is (= "seven" "eight")))

(deftest read-uncommited
  (is (= "nine" 10))))
