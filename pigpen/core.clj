(ns myapp.core
  (:gen-class))

(require '[pigpen.core :as pig])

(defn pig-data
    [input]
    (pig/load-string input)
)

(defn isvulnerable
    [[name state]]
    (if (= state "vulnerable") true false)
)

(defn mypigrun
    [input output]
    (->> (pig-data input)
    (pig/map #(rest (re-find #"\| ([.a-zA-Z]+)\|.+(vulnerable|safe)" %)))
    (pig/filter isvulnerable)
    (pig/group-by #(nth % 0))
    (pig/map (fn [[word occurrences]] [word (count occurrences)]))
    (pig/store-json output)
    )
)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
