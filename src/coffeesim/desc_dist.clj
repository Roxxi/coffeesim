(ns coffeesim.desc-dist
  (:use coffeesim.desc)
  (:require [clojure.math.numeric-tower :as math]))

;; # Part 3 support: Distance between descriptions
;;
;; So how do we define the distance between two descriptions?
;;
;; Well, our descriptions are made up of several attributes:
;; [decaf? organic? fair-trade? adjectives origin]
;;
;; As indicated by the '?', the first three attributes are boolean in nature
;; and our last two are simply strings.
;;
;; With the boolean values a distance calculation is straight forward:
;;
;; If the two values are the same, we can assign the distance to be 0,
;; and if they are different, then 1.
;;
;; With the string values, we would need some sort of table or over arching rule
;; about how to translate difference between say, "Guatemala" and "Columbia"
;; or "Bold and Spicy" and "Bright and Light"
;;
;; Since at this time, I lack any kind of guidence, I'll just put some simple,
;; easily replaceable rules, so that if any better rules or look ups are
;; ever defined we can simply swap out the implemtation of the
;; difference function for these attributes.
;;
;; Though, before moving on, it's worth saying that whatever values these
;; two attributes return, they should be roughly balanceable with
;; the values of our boolean attributes, so that we can get reasonable closeness.
;;
;;


(defn boolean-dist [b1 b2]
  (if (= b1 b2) 0 1))

;; Note, this is arbitrary and can be changed later
(defn text-dist [text1 text2]
  (let [val1 (apply + (map int text1))
        val2 (apply + (map int text2))]
    (/ (math/abs (- val1 val2))
       (math/sqrt (+ (math/expt val1 2) (math/expt val2 2))))))

(defn country-dist [country1 country2]
  (text-dist country1 country2))

(defn adjective-dist [adjective1 adjective2]
  (text-dist adjective1 adjective2))

(defmacro weighted-distance
  ([distance-fn val-fn]
     `(weighted-distance ~distance-fn ~val-fn 1))
  ([distance-fn val-fn weight]
     `(fn weighted-distance-body [x1# x2#]
        (* ~weight (~distance-fn (~val-fn x1#) (~val-fn x2#))))))

(defn desc-similarity [d1 d2]
  (+
   ((weighted-distance boolean-dist :decaf?) d1 d2)
   ((weighted-distance boolean-dist :organic?) d1 d2)
   ((weighted-distance boolean-dist :fair-trade?) d1 d2)
   ((weighted-distance adjective-dist :adjectives) d1 d2)
   ((weighted-distance country-dist :origin) d1 d2)))

  
  