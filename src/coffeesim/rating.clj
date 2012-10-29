(ns coffeesim.rating
  (:use coffeesim.desc
        [clojure.string :only (split, join)])
  (:import [java.lang Comparable]))

(defn parse-int [s]
  (Integer. (re-find  #"\d+" s)))

(defrecord Rating [person-id description rating] Comparable
  (compareTo ^int [_ other-rating]
    (if (> (- rating (:rating other-rating)) 0)
      -1
      1))
  (toString ^String [_] (str person-id ", " description ", " rating)))



(defn make-rating [person-id desc #^Integer rating]
  (Rating. person-id desc rating))


(defn extract-ratings-from-file [file-path]
  (with-open [rdr (clojure.java.io/reader file-path)]
    (let [lines (line-seq rdr)]
      (loop [line (first lines)
             next-lines (rest lines)
             ratings '()]
        (let [[person-id description rate] (split line #"\t")
              rating (make-rating person-id
                                  (parse-description description)
                                  rate)
              ratings (cons rating ratings)]          
          (if (empty? next-lines)
            ratings
            (recur (first next-lines) (rest next-lines) ratings)))))))