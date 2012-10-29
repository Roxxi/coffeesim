(ns coffeesim.core
  (:use coffeesim.desc
        coffeesim.report
        coffeesim.rating
        coffeesim.recommend
        [clojure.string :only (split, join)]
        clojure.pprint)
  (:gen-class))

;; # Part 1

(defn parse [string]
  (let [desc (parse-description string)]
    (println (:description desc))
    (println (str "Decaf " (:decaf? desc)))
    (println (str "Organic " (:organic? desc)))
    (println (str "Fair Trade " (:fair-trade? desc)))
    (println (str "Adjective " (:adjectives desc)))
    (println (str "Country " (:origin desc)))))
    
;; # Part 2

(defn summarize [filepath]
  (let [content (compile-report (extract-ratings-from-file filepath))]    
    (println (str "Total people " (:people content)))
    (println (str "Total coffee types " (:coffee-types content)))
    (println "Decaf")
    (println (str "\tTrue " (:true (:decaf content))))
    (println (str "\tFalse " (:false (:decaf content))))
    (println "Organic")
    (println (str "\tTrue " (:true (:organic content))))
    (println (str "\tFalse " (:false (:organic content))))
    (println "Fair Trade")
    (println (str "\tTrue " (:true (:fair-trade content))))
    (println (str "\tFalse " (:false (:fair-trade content))))
    (println "Adjective")
    (doseq [adj-cnt (:adjectives content)]
      (when (key adj-cnt) ;; skip items with no adjectives
        (println (str "\t" (key adj-cnt) " " (val adj-cnt)))))
    (println "Country")
    (doseq [country-cnt (:countries content)]
      (when (key country-cnt) ;; skip items that have no country name
        (println (str "\t" (key country-cnt) " " (val country-cnt)))))))


;; # Part 3

(defn recommend [filepath]
  (let [recs (generate-recs filepath)]
    (doseq [rec recs]
      (println
       (str (:person-id rec)
            "\t"
            (:description (:description rec))
            "\t"
            (:rating rec))))))
    



(defn -main [& args]
  (cond
    (= (first args) "parse") (parse (join " " (rest args))),
    (= (first args) "summarize") (summarize (second args)),
    (= (first args) "recommend") (recommend (second args))
    :else
    (println (str "Unknown command. 'parse <word word word>'"
                  "and 'summarize <absolute-file-path>' are the"
                  "supported comments))"))))