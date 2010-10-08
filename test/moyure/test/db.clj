(ns moyure.test.db
    (:use [moyure.db])
    (:use [clojure.test]))

(deftest db-t
    (testing "db"
        
         (testing "should return 1 in the first insertion and 2 in the second."
            (is (= 1 (insert-meet {:title "test" :when "today"})))
            (is (= 2 (insert-meet {:title "doc" :when "saturday"})))
            
            (testing "And and then nextval should return 2."
                (is (= 3 (nextval)))))
         
         (testing "Should return all entries (2) with unspecified id."
            (is (= 2 (count (find))))
         (testing "But should return one entry using id as argument."
            (is (= 2 (:id (find 2))))))))

