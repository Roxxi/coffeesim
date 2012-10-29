(ns coffeesim.desc-dist-test
  (:use clojure.test
        coffeesim.desc
        coffeesim.desc-dist))

(def d1 (parse-description "Organic Fair Trade Decaf Longberry Kenyan"))
(def d2 (parse-description "Organic Fair Trade Decaf Kenyan"))
(def d3 (parse-description "Organic Fair Trade Swiss Water Malian"))
(def d4 (parse-description "Cuzcachapa Ethiopian"))

(deftest distance-tests
  (testing "Identity"
    (is (= (desc-similarity d1 d1) 0.0)))
  
  (testing "Distances"
    (is (= (desc-similarity d1 d2) 1.0))
    (is (= (desc-similarity d2 d3) 2.184124156794664))
    (is (= (desc-similarity d3 d4) 2.5261582914535263))
    (is (= (desc-similarity d4 d1) 3.3730174597280764))))
