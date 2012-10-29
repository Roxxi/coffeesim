(ns coffeesim.recommend
  (:use coffeesim.utils
        coffeesim.rating
        coffeesim.desc
        coffeesim.desc-dist
        roxxi.utils.print
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
    "The UserTable abstracts around the kinds of questions we could answer
through a purpose driven data source like a SQL database, etc"
  (user->ratings [_ user] "returns ratings for a particular user")
  (get-users [_] "returns a list of users"))

(defprotocol RatingTable
  "The RatingTable abstracts around the kinds of questions we could answer
through a purpose driven data source like a SQL database, etc"
  (description->average-rating [_ description]
    "Returns the average rating that is associcated to a particular description")
  (rating->descriptions-ordered-by-similarity [_ rating]
    "Returns descriptions sorted by descending
     similarity score for a given rating"))

(deftype MemoryUserTable [user=>ratings]
  UserTable  
  (user->ratings [_ user]
    (get user=>ratings user))
  (get-users [_]
    (keys user=>ratings))
  (toString ^String [_] (str user=>ratings)))

(deftype MemoryRatingTable [ratings]
  RatingTable
  ;; The results of both of these methods be cached by rating
  ;; for performance improvement, but its too early to optimize :)
  ;; and I proobably would be using a database of some sort
  ;; in the real world.
  (description->average-rating [_ description]
    (let [all-descs (map :description ratings)
          same-rating? #(= (:description %) description)
          ratings  (map parse-int (map :rating (filter same-rating? ratings)))]
      (double
       (/ (reduce + 0 ratings)
          (count ratings)))))
  (rating->descriptions-ordered-by-similarity [_ rating]
    (let [this-desc (:description rating)
          unique-descs (set (map :description ratings))]
      (sort
       #(- (:score %) (:score %2))
       (map (fn [d]
              {:score (desc-similarity this-desc d)
               :desc d})
            unique-descs)))))

(defn make-user-table-from-ratings [ratings]
  (MemoryUserTable. (extract-map ratings
                                 :key-extractor :person-id
                                 :list-values true)))

(defn make-rating-table-from-ratings [ratings]
  (MemoryRatingTable. ratings))


(defn user->recommendation-descriptions [user user-table rating-table]
  (let [user-ratings (user->ratings user-table user)
        closenesses (map
                     #(rating->descriptions-ordered-by-similarity rating-table %)
                     user-ratings)
        user-descriptions (map :description user-ratings)
        user-already-tasted? (fn [scored-desc]
                               (some #(= (:desc scored-desc) %)
                                     user-descriptions))
        ;; take out everything that the user has already tasted
        new-candidates (map #(remove user-already-tasted? %) closenesses)]
    (loop [top3 ()
           all-recs new-candidates]
      (cond
        ;; if we run out of recommendations...
        (every? empty? all-recs) top3,
        ;; if we find the three best...
        (= (count top3) 3) top3,
        ;; else:
        ;;   - look at the first possible candidates from each set of recs
        ;;   - figure out the score of the best one
        ;;   - figure out which one has the best score
        ;;   - add the one with the best score to our final reccomendation
        ;;     & do this all again, with that one removed from all
        ;;     potential candidate pools
        :else
        (let [first-choices (map first all-recs)
              best-score (apply min (map :score first-choices))
              best-choice (first
                           (filter
                            #(= (:score %) best-score)
                            first-choices))
              best-desc (:desc best-choice)]
          (recur (cons best-desc top3)
                 (map #(if (= best-desc (:desc (first %)))
                         (rest %)
                         %)
                      all-recs)))))))


(defn user->recommendations [user user-table rating-table]
  (let [rec-descs
        (user->recommendation-descriptions user user-table rating-table)
        recommendations (map #(make-rating
                               user
                               %
                               (description->average-rating rating-table %))
                             rec-descs)
        sorted-recs (sort recommendations)]
    sorted-recs))
    
(defn users->recommendations [users user-table rating-table]
  (apply concat (map #(user->recommendations % user-table rating-table)
       users)))

(defn generate-recs [filepath]
  (let [ratings (extract-ratings-from-file filepath)
        user-table (make-user-table-from-ratings ratings)
        rating-table  (make-rating-table-from-ratings ratings)]
    (users->recommendations (get-users user-table) user-table rating-table)))



