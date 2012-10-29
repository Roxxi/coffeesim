(ns coffeesim.desc
  (:use [clojure.string :only (split, join)]))



(defrecord CoffeeDesc
    [description decaf? organic? fair-trade? adjectives origin])

;; Some nice constants from java-land

(def countries
  {"Balinese" "Bali",
   "Bolivian" "Bolivia",
   "Brazilian" "Brazil",
   "Costa Rican" "Costa Rican",
   "Dominican" "Dominican Republic",
   "Salvadorean" "El Salvador",
   "Ethiopian" "Ethiopia",
   "Guatemalan" "Guatemala",
   "Indian" "India",
   "Kenyan" "Kenya",
   "Malian" "Mali",
   "Mexican" "Mexico",
   "Panamanian" "Panama",
   "Peruvian" "Peru",
   "Sumatran" "Sumatra"})

;; Parsing functions. Given some description string, let's extract some information from it...

(defn decaf? [description]
  (boolean (re-find #"(?i)decaf" description)))

(defn organic? [description]
  (boolean (re-find #"(?i)organic" description)))

(defn fair-trade? [description]
  (boolean (re-find #"(?i)fair\s*trade" description)))

(defn countryable? [some-string]
  (contains? countries some-string))

(defn ->country [countryable-str]
  (countries countryable-str))

(defn extract-country [description]
  (let [countries
        (map ->country (filter countryable? (split description #"\s")))]
    (cond
      (= (count countries) 0) nil
      (= (count countries) 1) (first countries)
      :else (join ", " countries))))


;; well, by the parameters of the requirements, you're an adjective if you're
;; neither "decaf", "organic", "fair trade", nor any thing that a country
;; can be derived from. So let's go with that.
(defn adj? [some-string]
  (not (or (decaf? some-string)
           (organic? some-string)
           (countryable? some-string))))

;; We need a function to handle removing "fair trade" first,
;; since it's two words and we're doing word-by-word analysis.
(defn unfair-tradify-description [desc]
  (if (fair-trade? desc)
    (join "" (split desc #"(?i)fair\s*trade\s?"))
    desc))

;; After "fair trade" is removed (if its present)
;; we can inspect everything else word by word.
(defn extract-adj [description]  
  (let [unfair-desc (unfair-tradify-description description)
        adjectives (filter adj? (split unfair-desc #"\s"))]
    (join " " adjectives)))
         
;; per the requirement
(defn parse-description [desc-str]
  (CoffeeDesc. desc-str
               (decaf? desc-str)
               (organic? desc-str)
               (fair-trade? desc-str)
               (extract-adj desc-str)
               (extract-country desc-str)))


