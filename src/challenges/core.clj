(ns challenges.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn sum-of-multiples-of-3-5
  "Find sum of all multiples of 3 and 5 from 0 to 1000"
  []
  (reduce +
          (filter
           #(or
             (zero? (mod % 3))
             (zero? (mod % 5)))
           (range 1000))))

(defn fib
  [a b]
  (cons a (lazy-seq (fib b (+ b a)))))

(defn sum-of-even-fib
  "Take sum of even fib numbers in range [0,4000000]"
  []
  (reduce +
          (filter even?
           (take 34 (fib 0 1)))))
