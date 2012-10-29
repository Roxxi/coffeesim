(ns coffeesim.recomend
  (:use coffeesim.rating
        coffeesim.desc
        [clojure.string :only (split, join)]
        clojure.pprint))

;; # Baby's first recommendation engine.
;;
;; This is my first attempt at writing a recommendation engine, ever.
;; It's also the first time I've ever looked to see how one would work.
;; As such, the goal here isn't to make something that optimized or scalable,
;; but rather to make a first forray into understanding how this whole
;; "similarity" thing works.
;;
;; ## k-NN now on tape
;;
;; After about 20 minutes of googling for "How to write a
;;  recommendation engine?" "How does a recommendation engine work?"
;; and "Recommendation engine algorithm," I came across
;; [this video](http://www.youtube.com/watch?v=4ObVzTuFivY);
;; watched it once by myself; once with my roommate (who was
;; only slightly more informed on the subject than I was); 
;; and talked about how to use this algorithm in the context of
;; answering the question at hand.
;;
;; Here's what I came up with:
;;
;; ```
;; Let U ::= be the set of all users
;;
;; Let D ::= be the set of all Descriptions
;;
;; Let rating->description ::= a function that given a rating returns
;;                             a description
;;
;; Let description->distances ::= a function that given a description provides
;;                                a set of descriptions ordered by their distance
;;
;; Let description->average-rating ::= a function that takes a description and
;;                                     returns the average rating
;;
;; Let user->rating ::= a function that given a user returns their ratings
;;
;; Let user->recommendation ::= a function that takes a user (u)
;;                              and returns a recommendation defined as follows:
;;   Let descs ::= (map rating->description (sort (user->ratings u)))
;;   Let reccomendations ::=
;;     Concatenate the following values:
;;       Let (already-tasted? x) ::= true iff x is in descs
;;         For each desc in descs:
;;           (take-n 3 (remove (already-tasted? (description->distances desc)))
;;   Let closest3 ::= (take-n 3 (sort reccomendations))
;;   Let recommend3 ::=
;;                (map #(Rating. u % (description->average-rating %)) closest3)
;; ```
;; Let users->recomendations ::= (map user->recommendation U)
;;
;; So starting with this, let's give it a go and see where we end up!
;; Discussion throughout!




(defprotocol UserTable
  (user->ratings [_ user] "returns ratings for a particular user")
  (get-users [_] "returns a list of users"))

(defprotocol RatingTable
  (rating->average-rating [_ rating]
    "Returns the average rating that is associcated to a particular rating")
  (rating->three-similar-ratings [_ rating]
    "Returns three most similar ratings for a given rating"))



(defn extract-map [some-seq
                   & {:keys [xform
                             key-extractor
                             value-extractor
                             list-values
                             value-kons
                             value-knil]
                      :or {xform identity
                           key-extractor identity,
                           value-extractor identity,
                           list-values false,
                           value-kons cons,
                           value-knil nil}}]
  (let [xform-assoc!
        (if list-values
          (fn xform-assoc-list-values! [some-map elem]
            (let [xformed (xform elem)
                  the-key (key-extractor xformed)
                  the-val (value-extractor xformed)
                  prev-val (get some-map the-key)]
              (if (nil? prev-val)
                (assoc! some-map the-key (value-kons the-val value-knil))
                (assoc! some-map the-key (value-kons the-val prev-val)))))
          (fn simple-xform-assoc! [some-map elem]
            (let [xformed (xform elem)]
              (assoc! some-map
                      (key-extractor xformed)
                      (value-extractor xformed)))))]       
    (persistent!
     (loop [elems some-seq
            new-map (transient {})]
       (if (empty? elems)
         new-map
         (recur (rest elems)
                (xform-assoc! new-map (first elems))))))))


(deftype MemoryUserTable [user=>ratings]
  UserTable  
  (user->ratings [_ user]
    (get user=>ratings user))
  (get-users [_]
    (keys user=>ratings))
  (toString ^String [_] (str user=>ratings)))

(deftype MemoryRatingTable [ratings]
  RatingTable
  (rating->average-rating [_ rating]
    "Returns the average rating that is associcated to a particular rating")
  (rating->three-similar-ratings [_ rating]
    "Returns three most similar ratings for a given rating"))

(defn make-user-table-from-ratings [ratings]
  (MemoryUserTable. (extract-map ratings
                                 :key-extractor :person-id
                                 :list-values true)))


;; ## Boostrap phase
;; 1. build table of users 
;; 2. build table ratings

(defn bootstrap [filepath]
  (let [user-table (make-user-table-from-ratings ratings)]
    user-table))
