(ns coffeesim.core
  (:use coffeesim.desc
        [clojure.string :only (split)]))


(defn -main [args]
  (println args))

;; # Part 1

(defn describe [string]
  (let [desc (parse-description string)]
    (println (:description desc))
    (println (str "Decaf " (:decaf? desc)))
    (println (str "Organic " (:organic? desc)))
    (println (str "Fair Trade " (:fair-trade? desc)))
    (println (str "Adjective " (:adjectives desc)))
    (println (str "Country " (:origin desc)))))
    
;; # Part 2

(defrecord Rating [person-id description rating])

(defn extract-ratings-from-file [file-path]
  (with-open [rdr (clojure.java.io/reader file-path)]
    (let [lines (line-seq rdr)]
      (loop [line (first lines)
             next-lines (rest lines)
             ratings '()]
        (let [[person-id description rate] (split line #"\t")
              rating (Rating. person-id (parse-description description) rate)
              ratings (cons rating ratings)]          
          (if (empty? next-lines)
            ratings
            (recur (first next-lines) (rest next-lines) ratings)))))))

;; ## Preparing the report

(defn count-unique [val-fn items]
  (count (set (map val-fn items))))

;; we have to keep track of how many trues and falses we come across
;; here's a function that generates a function that expects a boolean map
;; (i.e. a map of {:true val :false val}) and increments the vals based
;; on the return value of the provided val-function applied to an item.
(defn make-boolean-field-incrementer [val-fn]
  (fn [bool-map item]
    (if (val-fn item)
      (update-in bool-map [:true] inc)
      (update-in bool-map [:false] inc))))

;; reduce boiler-plate
(defmacro count-over-boolean-fields [val-fn items]
  `(reduce (make-boolean-field-incrementer ~val-fn)
           {:true 0 :false 0} ~items))

(defn one-or-inc [x]
  (if (nil? x) 1 (inc x)))

(defn make-counter-by-key  [key-from-item-fn]
  (fn [map-to-update item]
    (let [key (key-from-item-fn item)]
      (update-in map-to-update [key] one-or-inc))))

(defn make-report [ratings]
  {:people (count-unique :person-id ratings),
   :coffee-types (count-unique #(:description (:description %)) ratings),
   :decaf (count-over-boolean-fields #(:decaf? (:description %)) ratings),
   :organic (count-over-boolean-fields #(:organic? (:description %)) ratings),
   :fair-trade (count-over-boolean-fields #(:fair-trade? (:description %)) ratings),
   :adjectives (reduce (make-counter-by-key #(:adjectives (:description %))) {} ratings)
   :countries (reduce (make-counter-by-key #(:origin (:description %))) {} ratings)
   })



         

