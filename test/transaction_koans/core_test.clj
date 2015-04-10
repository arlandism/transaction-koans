(ns transaction-koans.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as adapter]
            [clojure.edn :as edn]))

(def project-root (.getCanonicalPath (clojure.java.io/file ".")))

(def db-spec (slurp
               (clojure.java.io/file (str project-root "/config/database.edn"))))

(with-db-connection [db-spec ]
 (deftest repeatable-read
  (is (= 0 1)))

(deftest serializable
  (is (= 1 2)))

(deftest read-committed
  (is (= "seven" "eight")))

(deftest read-uncommited
  (is (= "nine" 10))))
