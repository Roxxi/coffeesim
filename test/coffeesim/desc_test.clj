(ns coffeesim.desc-test
  (:use clojure.test
        coffeesim.desc))


(deftest description-extraction-test
  (testing "Are all these strings properly caffeinated?"
    (is (decaf? "This is decaf"))
    (is (decaf? "This is Decaf"))
    (is (decaf? "This is DECAF"))
    (is (decaf? "Decaf is This"))
    (is (decaf? "is Decaf This")))
  (testing "Are all these strings organic?"
    (is (organic? "This is organic"))
    (is (organic? "This is Organic"))
    (is (organic? "This is ORGANIC"))
    (is (organic? "ORGANIC is This"))
    (is (organic? "is Organic This")))
  (testing "Are all these strings fair-trade??"
    (is (fair-trade? "This is fair trade"))
    (is (fair-trade? "This is FAIR Trade"))
    (is (fair-trade? "This is Fair Trade"))
    (is (fair-trade? "Fair Trade is This"))
    (is (fair-trade? "Fair Trade This"))
    (is (not (fair-trade? "Trade Fair This"))))
  (testing "Are all of these countries?"   
    (is (countryable? "Balinese"))
    (is (countryable? "Bolivian"))
    (is (countryable? "Brazilian"))
    (is (countryable?    "Costa Rican"))
    (is (countryable? "Dominican"))
    (is (countryable? "Salvadorean"))
    (is (countryable? "Ethiopian"))
    (is (countryable? "Guatemalan"))
    (is (countryable?    "Indian"))
    (is (countryable? "Kenyan"))
    (is (countryable? "Malian"))
    (is (countryable? "Mexican"))
    (is (countryable? "Panamanian"))
    (is (countryable?    "Peruvian"))
    (is (countryable? "Sumatran")))
  (testing "Are these not countries?"
    (is (every? #(not (countryable? %))
               ["Decaf" "Fair Trade" "Organic" "Kenya"
                "Mexico" "Amazing" "Bright" "Bold"])))
  (testing "Can we read countries out?"
    (is (= (extract-country "Black Satin Balinese") "Bali"))
    (is (= (extract-country "Black Satin") nil))
    (is (= (extract-country "Mexican Black Satin Balinese") "Mexico, Bali")))

  (testing "Can we filter out fair-trade from strings?"
    (is (= (unfair-tradify-description "This is a fair trade") "This is a "))
    (is (= (unfair-tradify-description "This is a fair trade coffee") "This is a coffee"))
    (is (= (unfair-tradify-description "Fair trade is this coffee") "is this coffee"))
    (is (= (unfair-tradify-description "Fair is this coffee trade")
           "Fair is this coffee trade")))
  (testing "These aren't adjectives, right?"
    (is (not (adj? "decaf")))
    (is (not (adj? "organic")))
    (is (not (adj? "Mexican")))
    (is (adj? "fair trade")))
  (testing "Can we get some adjectives out?"
    (is (= (extract-adj "Organic Fair Trade Decaf Longberry Kenyan")
           "Longberry"))
    (is (= (extract-adj "Organic Fair Trade Decaf Caturra Panamanian")
           "Caturra"))
    (is (= (extract-adj "Organic Fair Decaf Trade Caturra Panamanian")
           "Fair Trade Caturra"))))
  
(deftest desc-parsing
  (is (= (parse-description "Organic Fair Trade Decaf Caturra Sumatran")
         #coffeesim.desc.CoffeeDesc{:description "Organic Fair Trade Decaf Caturra Sumatran",
                                    :decaf? true,
                                    :organic? true,
                                    :fair-trade? true,
                                    :adjectives "Caturra",
                                    :origin "Sumatra"}))
  (is (= (parse-description "Organic Fair Trade Decaf Caturra Sumatran")
         (parse-description "Organic Fair Trade Decaf Caturra Sumatran")))
  (is (not (= (parse-description "Organic Fair Trade Decaf Sumatran")
              (parse-description "Organic Fair Trade Decaf Caturra Sumatran")))))
  )







