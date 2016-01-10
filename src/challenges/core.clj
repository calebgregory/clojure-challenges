(ns challenges.core
  (:use [clojure.string :only [capitalize join split]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;; If we list all the natural numbers below 10 that are multiples of 3 or 5, we get 3, 5, 6 and 9. The sum of these multiples is 23.
;; Find the sum of all the multiples of 3 or 5 below 1000.
(defn sum-of-multiples-of-3-5
  "Find sum of all multiples of 3 and 5 from 0 to 1000"
  []
  (reduce +
          (filter
           #(or
             (zero? (mod % 3))
             (zero? (mod % 5)))
           (range 1000))))

;; Each new term in the Fibonacci sequence is generated by adding the previous two terms. By starting with 1 and 2, the first 10 terms will be:
;; 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, ...
;; By considering the terms in the Fibonacci sequence whose values do not exceed four million, find the sum of the even-valued terms.
(defn fib
  [a b]
  (cons a (lazy-seq (fib b (+ b a)))))

(defn sum-of-even-fib
  "Take sum of even fib numbers in range [0,4000000]"
  []
  (reduce +
          (filter even?
           (take 34 (fib 0 1)))))

;; The prime factors of 13195 are 5, 7, 13 and 29.
;; What is the largest prime factor of the number 600851475143 ?
(defn factors-of
  [x]
  (filter #(zero? (mod x %)) (range 1 x)))

(defn prime?
  [x]
  (= (last (factors-of x)) 1))

(defn prime-factors-of
  [x]
  (filter prime? (factors-of x)))

(defn greatest-prime-factor-of
  "Considering primes within one [n, n+1000] range at a time,
  see what primes the big-number is divisible by (= 0 (mod big-number prime)),
  then take new-big-number = big-number/prime,
  and find what primes _it_ is divisible by.
  Once it's iterated 10 times, just call it a day and return the highest of these."
  [x]
  (let [start 1000]
    (loop [iteration 1
           big-number x
           primes (filter prime? (range start))
           prime-factors []]
      (if (> iteration 10)
        (last prime-factors)
        (if-let
            [prime (first (filter #(zero? (mod big-number %)) primes))]
          (recur (inc iteration)
                 (/ big-number prime)
                 primes
                 (conj prime-factors prime))
          (recur (inc iteration)
                 big-number
                 (filter prime?
                         (range
                          (* iteration start)
                          (+ (* iteration start) start)))
                 prime-factors))))))

;; A palindromic number reads the same both ways.
;; The largest palindrome made from the product of two 2-digit numbers is
;; 9009 = 91 × 99.

;; Find the largest palindrome made from the product of two 3-digit numbers.

(defn palindrome?
  [x]
  (=
   (clojure.string/reverse (str x))
   (str x)))

(def products-of-three-digit-nums
  (for [x (range 100 1000)
        y (range 100 1000)]
    (* x y)))

(def largest-palindrome-product
  (loop [largest 0
         largest? 0
         remaining products-of-three-digit-nums]
    (if (empty? remaining)
      largest
      (if (and (palindrome? largest?)
               (> largest? largest))
        (recur largest? (first remaining) (rest remaining))
        (recur largest (first remaining) (rest remaining))))))

(def largest-palindrome-product-2
  (apply max (filter palindrome? products-of-three-digit-nums)))

;; 2520 is the smallest number that can be divided by each of the
;; numbers from 1 to 10 without any remainder.

;; What is the smallest positive number that is evenly divisible
;; by all of the numbers from 1 to 20?

(defn divides?
  "This reads 'does x divide y?'"
  [x y]
  (= (mod y x) 0))

'(def smallest-evenly-divisible-1-to-20
  (let [divisors (range 1 20)] ; (= 20 (* 2 10))
    (loop [result 1
           remaining-divisors divisors]
      (let [[divisor & more] remaining-divisors]
        (if (divides? divisor result)
          (if (empty? more)
            result
            (recur result more))
          (recur (inc result) divisors))))))

;; Write a function which removes consecutive duplicates from a sequence.
;; e.g., "Leeeeeeeeeeroy" => "Leroy"

(defn compress-seq
  [coll]
  (loop [result []
         more coll]
    (if (empty? more)
      result
      (if (= (last result) (first more))
        (recur result (rest more))
        (recur (conj result (first more)) (rest more))))))

(defn compress-seq-2
  [xs]
  (map first (partition-by identity xs)))

;; Write a function which takes a variable number of parameters and returns
;; the maximum value.

(defn my-max
  [x & xs]
  (cond
   (nil? xs) x
   (> x (first xs)) (recur x (next xs))
   :else (recur (first xs) (next xs))))

(defn my-max-2
  [& args]
  (apply (comp last sort list) args))

(defn my-max-3
  [& xs]
  (reduce #(if (> %1 %2) %1 %2) xs))

(defn my-max-4
  [& xs]
  (-> xs sort last))

(defn my-interleave-b
  [c1 c2]
  (apply concat
         (map-indexed
          (fn [idx itm]
            (if-let [other-itm (nth c2 idx)]
              [itm other-itm]
              nil))
          c1)))

(defn my-interleave
  ([] '())
  ([c1 c2]
   (cons (first c1)
         (cons (first c2)
               (my-interleave (rest c1) (rest c2))))))

(defn exp
  [base pow]
  (apply * (repeat pow base)))

(defn is-power-of-4?
  [x]
  (loop [n x]
    (or (= 1 n)
        (if (zero? (mod n 4))
          (recur (/ n 4))
          false))))


; accum("abcd") --> "A-Bb-Ccc-Dddd"
; accum("RqaEzty") --> "R-Qq-Aaa-Eeee-Zzzzz-Tttttt-Yyyyyyy"
; accum("cwAt") --> "C-Ww-Aaa-Tttt"

(defn accum
  [str]
  (->> (map-indexed vector str)
       (map #(repeat (inc (first %)) (second %)))
       (map join)
       (map capitalize)
       (join #"-")))

(defn accum1
  [str]
  (let [enum (map-indexed vector str)]
    (join \-
          (map (fn [[i c]]
                 (->>
                  c
                  (repeat (inc i))
                  join
                  capitalize))
               enum))))

;; A number m of the form 10x + y is divisible by 7 if and only if x −
;; 2y is divisible by 7. In other words, subtract twice the last digit
;; from the number formed by the remaining digits.

;; Continue to do this until a number known to be divisible or not by
;; 7 is obtained; you can stop when this number has at most 2 digits
;; because you are supposed to know if a number of at most 2 digits is
;; divisible by 7 or not.

;; The original number is divisible by 7 if and only if the last
;; number obtained using this procedure is divisible by7.

;; Examples:

;; 1 : m = 371 -> 37 − (2×1) -> 37 − 2 = 35
;;  thus, since 35 is divisible by 7, 371 is divisible by 7.
;;  The number of steps to get the result is 1.
;; 2 : m = 1603 -> 160 - (2 x 3) -> 154 -> 15 - 8 = 7
;;  and 7 is divisible by 7.
;; 3 : m = 372 -> 37 − (2×2) -> 37 − 4 = 33
;;  thus, since 33 is not divisible by 7, 372 is not divisible by 7.
;;  The number of steps to get the result is 1.
;; 4 : m = 477557101->47755708->4775554->477547->47740->4774->469->28
;;  and 28 is divisible by 7, so is 477557101.
;;  The number of steps is 7.

;; Task:

;; Your task is to return to the function seven(m) (m integer >= 0) an
;; array (or a pair, depending on the language) of numbers, the first
;; being the last number m with at most 2 digits obtained by your
;; function (this last m will be divisible or not by 7), the second
;; one being the number of steps to get the result.

(defn num-digits
  [n]
  (if (= 0 n)
    0
    (inc (num-digits (quot n 10)))))

(defn seven
  [m]
  (loop [m m i 0]
    (if (<= (num-digits m) 2)
      [m i]
      (let [x (quot m 10)
            y (rem m 10)
            n (- x (* 2 y))]
        (recur n (inc i))))))

;; One of the first algorithms used for approximating the integer square
;;        root of a positive integer n is known as \"Hero's method\", named after
;;        the first-century Greek mathematician Hero of Alexandria who gave the
;;        first description of the method. Hero's method can be obtained from
;;        Newton's method which came 16 centuries after.

;;        We approximate the square root of a number n by taking an initial
;;        guess x, an error e and repeatedly calculating a new approximate value
;;        x using: (x + n / x) / 2 ; we are finished when the previous x and the
;;        new x have an absolute difference less than e.

;;        We supply to a function (int_rac) a number n (positive integer) and a
;;        parameter guess (positive integer) which will be our initial x. For
;;        this kata the parameter 'e' is set to 1.

;;        Hero's algorithm is not always going to come to an exactly correct
;;        result! For instance: if n = 25 we get 5 but for n = 26 we also get
;;        5. Nevertheless 5 is the integer square root of 26.

;;        The kata is to return the count of the progression of approximations
;;        that the algorithm makes. 

;; n = n,
;;     x = initial-guess,
;;     e = 1

;;     approximate-value = (n, x) => (x + n / x) / 2
;;    done when | x - prev x | < e

(defn abs [x] (if (> x 0) x (- x)))

(defn int-rac
  [n guess]
  (loop [x guess
         acc [x]]
    (let [approx (int (/ (+ x (/ n x)) 2))]
      (if (< (abs (- x approx)) 1)
        (count acc)
        (recur approx (conj acc approx))))))

(defn int-rac1
  [n guess]
  (loop [x guess
         i 1]
    (let [approx (bigint (/ (+' x (/ n x)) 2))]
      (if (> 1 (abs (- x approx)))
        i
        (recur approx (inc i))))))

;; A -> 1; B -> 2; ...; Z -> 26; AA -> 27

(defn title-to-nb
  [s]
  (->> (map int s)
       (map #(- % 64))
       (reverse)
       (map-indexed vector)
       (map (fn [[i x]] (* (exp 26 i) x)))
       (reduce + 0)))

(defn title-to-nb1
  [s]
  (reduce #(+ (* %1 26) (- (int %2) 64)) 0 s))

;; ABC...
;;  123...
;;  26*10^n (x_n)

;; A    = 1
;; AA   = 27 = 26 + 1
;; AZ   = 52 = 26 + 26
;; BA   = 53 = 26 + 26 + 1
;; BZ   = 78 = 26 + 26 + 26
;; CA   = 79 = 26 + 26 + 26 + 1

(defn cart
  [colls]
  (if (empty? colls)
    '(())
    (for [x (first colls)
          xs (cart (rest colls))]
      (cons x xs))))

(title-to-nb "CA")
(title-to-nb "Z")

;; Flat rate vs. decaying rate...

;; A movie theater offers two options -

;; System A : buy a ticket (15 dollars) every time
;; System B : buy a card (500 dollars) and every time
;;              buy a ticket the price of which is 0.90 times the price
;;              paid for the previous one.

;; for example,

;; System A : 15 * 3 = 45
;; System B : 500 + 15 * 0.90 + (15 * 0.90) * 0.90 + (15 * 0.90 * 0.90) * 0.90

;; cost = 15 * n
;; cost = 500 + (sum as i -> n of 15 * .9 ^ i)

(defn movie
  [card ticket perc]
  (loop [i 0
         a 0
         b card]
    (println (str "i: " i " A: " a " B: " b))
    (if (< (Math/ceil b) a)
      i
      (recur (inc i)
             (+ a ticket)
             (+ b (* ticket (apply * (repeat (inc i) perc))))))))

;;     15 * n                    = 500 + 15 * ( 0.9 ^ n )
;; =>  15 * n - 15 * ( 0.9 ^ n ) = 500
;; =>  15 * ( n - 0.9 ^ n )      = 500
;; =>  n - 0.9 ^ n               = 100 / 3

;;     15 * n = 500 + 15 * ( 0.9 ^ n )
;; =>  0 = 15 * ( 0.9 ^ n ) - 15 * n + 500
;;       = 15 * ( 0.9 ^ n - 1 ) + 500
;; =>  0 = 0.9 ^ n - n + (100/3)
;; f'(n) = (0.9 ^ n) * ln (0.9) - 1

;; Using the Newton-Raphson method,
;; x[i+1] = x[i] - f(x[i])/f'(x[i])

;; x[i+1] = x[i] - (0.9^x[i] - x[i] + (100/3))/((0.9 ^ n) * ln (0.9) - 1

(defn movie1
  [card ticket perc]
  (loop [i 1
         acc '(5000)] ; some 'guess' value
    (let [x (- (first acc)
               (/ (+ card (* ticket (- (apply * (repeat i perc)) 1)))
                  (- (* (apply * (repeat i perc)) (Math/log perc) 1))))]
      (println (str "i " i " | x " x))
      (if (> i 10)
        nil
        (recur (inc i) (cons x acc))))))

;; Take an integer n (n >= 0) and a digit d (0 <= d <= 9) as an
;; integer. Square all numbers k (0 <= k <= n) between 0 and n. Count the

;; (or nbDig or ...) the function taking n and d as parameters and
;; returning this count.

;; for example,
;; n = 10, d = 1, the k*k are 0, 1, 4, 9, 16, 25, 36, 49, 64, 81, 100
;; We are using the digit 1 in 1, 16, 81, 100. The total count is then 4.


(defn to-dig
  [x]
  (if (zero? x)
    [x]
    (loop [x x acc []]
      (if (zero? x)
        acc
        (recur (quot x 10) (conj acc (rem x 10)))))))

(defn nb-dig
  [n d]
  (->> (range 0 (inc n))
       (map #(* % %))
       (map to-dig)
       (flatten)
       (filter #(= d %))
       (count)))

(defn to-dig2
  ([x] (if (= 0 x) [x] (to-dig2 x [])))
  ([x acc]
   (if (zero? x)
     acc
     (to-dig2 (quot x 10) (conj acc (rem x 10))))))
