(ns coffeesim.report-test
  (:use clojure.test
        coffeesim.report))

(def test-file-path
  (str (System/getProperty "user.dir")
       "/test/data/report_test_data.tsv"))


(is (= (compile-report (extract-ratings-from-file test-file-path))
       {:people 10,
        :coffee-types 10,
        :decaf {:true 25, :false 25},
        :organic {:true 32, :false 18},
        :fair-trade {:true 18, :false 32},
        :adjectives {"Cuzcachapa" 3,
                     "Supremo" 12,
                     "Sidamo" 2,
                     "Longberry" 4,
                     "Mandheling" 7,
                     "Swiss Water" 14,
                     "Caturra" 8},
        :countries {"Ethiopia" 4,
                    "Dominican Republic" 4,
                    "Bolivia" 5,
                    "Guatemala" 2,
                    "Panama" 4,
                    "Brazil" 7,
                    "Mali" 16,
                    "Sumatra" 8}}))

