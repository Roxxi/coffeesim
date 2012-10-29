(ns coffeesim.utils)

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