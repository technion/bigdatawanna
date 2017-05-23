(defn my-data
  []
  (clojure.string/split (slurp "inputfixed.txt") #"\n"))

(defn issafe
  [[name state]]
  (if (= state "vulnerable") true false)
)

(defn myrun
  []
  (->> (my-data)
  (map #(rest (re-find #"\| ([.a-zA-Z]+)\|.+(vulnerable|safe)" %)))
  (into () (filter issafe))
  (group-by #(nth % 0))
  (map (fn [[word occurrences]] [word (count occurrences)]))
  )
)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println(pr-str (myrun))))
