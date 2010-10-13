(ns moyure.test.db
    (:use [moyure.db])
    (:use [clojure.test]))

(deftest db-t
    (testing "db"
        
         (testing "should return 1 in the first insertion and 2 in the second."
            (is (= (inc @id) (insert-meet {:title "test" :when "today"})))
            (is (= (inc @id) (insert-meet {:title "doc" :when "saturday"})))
            
            (testing "And and then nextval should return 2."
                (is (= (inc @id) (nextval)))))
        
 
         (testing "Should return all entries (2) with unspecified id."
            (is (not= 0 (count (find))))
         (testing "But should return one entry using id as argument."
            (is (not= nil (find (dec @id))))))))

