;; ============================================================================
;; EASY 11: Fizz Buzz
;; ============================================================================
;; Source: LeetCode #412 - Fizz Buzz
;; Difficulty: Easy
;; Topics: Math, String, Simulation
;;
;; A classic programming interview question that tests basic control flow
;; and modular arithmetic. Simple but reveals coding style and attention
;; to detail.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given an integer n, return a string array answer (1-indexed) where:
;;
;; - answer[i] == "FizzBuzz" if i is divisible by 3 and 5.
;; - answer[i] == "Fizz" if i is divisible by 3.
;; - answer[i] == "Buzz" if i is divisible by 5.
;; - answer[i] == i (as a string) if none of the above conditions are true.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: n = 3
;;   Output: ["1", "2", "Fizz"]
;;
;; Example 2:
;;   Input: n = 5
;;   Output: ["1", "2", "Fizz", "4", "Buzz"]
;;
;; Example 3:
;;   Input: n = 15
;;   Output: ["1", "2", "Fizz", "4", "Buzz", "Fizz", "7", "8", "Fizz", "Buzz",
;;            "11", "Fizz", "13", "14", "FizzBuzz"]

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= n <= 10^4

;; ============================================================================
;; SOLUTION 1: Using cond (Traditional approach)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n) for the result

(defn fizz-buzz
  "Generate FizzBuzz sequence from 1 to n."
  [n]
  (mapv (fn [i]
          (cond
            (zero? (mod i 15)) "FizzBuzz"
            (zero? (mod i 3))  "Fizz"
            (zero? (mod i 5))  "Buzz"
            :else              (str i)))
        (range 1 (inc n))))

(fizz-buzz 3)
;; => ["1" "2" "Fizz"]

(fizz-buzz 5)
;; => ["1" "2" "Fizz" "4" "Buzz"]

(fizz-buzz 15)
;; => ["1" "2" "Fizz" "4" "Buzz" "Fizz" "7" "8" "Fizz" "Buzz" "11" "Fizz" "13" "14" "FizzBuzz"]

;; ============================================================================
;; SOLUTION 2: String concatenation approach
;; ============================================================================
;; More extensible - easily add more conditions

(defn fizz-buzz-concat
  "FizzBuzz using string concatenation."
  [n]
  (mapv (fn [i]
          (let [result (str (when (zero? (mod i 3)) "Fizz")
                            (when (zero? (mod i 5)) "Buzz"))]
            (if (empty? result)
              (str i)
              result)))
        (range 1 (inc n))))

(fizz-buzz-concat 15)
;; => ["1" "2" "Fizz" "4" "Buzz" "Fizz" "7" "8" "Fizz" "Buzz" "11" "Fizz" "13" "14" "FizzBuzz"]

;; ============================================================================
;; SOLUTION 3: Using for comprehension
;; ============================================================================

(defn fizz-buzz-for
  "FizzBuzz using for comprehension."
  [n]
  (vec
   (for [i (range 1 (inc n))]
     (cond
       (and (zero? (mod i 3)) (zero? (mod i 5))) "FizzBuzz"
       (zero? (mod i 3)) "Fizz"
       (zero? (mod i 5)) "Buzz"
       :else (str i)))))

(fizz-buzz-for 5)
;; => ["1" "2" "Fizz" "4" "Buzz"]

;; ============================================================================
;; SOLUTION 4: Using a map of divisors (Most Extensible)
;; ============================================================================
;; Easy to add new conditions like "Jazz" for 7

(defn fizz-buzz-extensible
  "Extensible FizzBuzz with configurable divisors."
  ([n] (fizz-buzz-extensible n [[3 "Fizz"] [5 "Buzz"]]))
  ([n divisors]
   (mapv (fn [i]
           (let [result (->> divisors
                             (filter (fn [[d _]] (zero? (mod i d))))
                             (map second)
                             (apply str))]
             (if (empty? result)
               (str i)
               result)))
         (range 1 (inc n)))))

(fizz-buzz-extensible 15)
;; => ["1" "2" "Fizz" "4" "Buzz" "Fizz" "7" "8" "Fizz" "Buzz" "11" "Fizz" "13" "14" "FizzBuzz"]

;; With custom divisors:
(fizz-buzz-extensible 21 [[3 "Fizz"] [5 "Buzz"] [7 "Jazz"]])
;; => ["1" "2" "Fizz" "4" "Buzz" "Fizz" "Jazz" "8" "Fizz" "Buzz" "11" "Fizz" "13" "Jazz" "FizzBuzz" "16" "17" "Fizz" "19" "Buzz" "FizzJazz"]

;; ============================================================================
;; SOLUTION 5: Using reduce
;; ============================================================================

(defn fizz-buzz-reduce
  "FizzBuzz using reduce."
  [n]
  (reduce (fn [acc i]
            (conj acc
                  (cond
                    (zero? (mod i 15)) "FizzBuzz"
                    (zero? (mod i 3))  "Fizz"
                    (zero? (mod i 5))  "Buzz"
                    :else              (str i))))
          []
          (range 1 (inc n))))

(fizz-buzz-reduce 5)
;; => ["1" "2" "Fizz" "4" "Buzz"]

;; ============================================================================
;; SOLUTION 6: Lazy infinite sequence
;; ============================================================================

(def fizz-buzz-seq
  "Infinite lazy FizzBuzz sequence."
  (map (fn [i]
         (cond
           (zero? (mod i 15)) "FizzBuzz"
           (zero? (mod i 3))  "Fizz"
           (zero? (mod i 5))  "Buzz"
           :else              (str i)))
       (iterate inc 1)))

(take 15 fizz-buzz-seq)
;; => ("1" "2" "Fizz" "4" "Buzz" "Fizz" "7" "8" "Fizz" "Buzz" "11" "Fizz" "13" "14" "FizzBuzz")

(defn fizz-buzz-lazy
  "FizzBuzz using lazy sequence."
  [n]
  (vec (take n fizz-buzz-seq)))

;; ============================================================================
;; SOLUTION 7: Pattern-based (No division!)
;; ============================================================================
;; Uses the fact that FizzBuzz has a repeating pattern of length 15

(def fizz-buzz-pattern
  "The repeating pattern of FizzBuzz (indices 0-14 correspond to 1-15)."
  [nil nil "Fizz" nil "Buzz" "Fizz" nil nil "Fizz" "Buzz" nil "Fizz" nil nil "FizzBuzz"])

(defn fizz-buzz-pattern-based
  "FizzBuzz using pattern matching - no modulo needed!"
  [n]
  (mapv (fn [i]
          (or (fizz-buzz-pattern (mod (dec i) 15))
              (str i)))
        (range 1 (inc n))))

(fizz-buzz-pattern-based 15)
;; => ["1" "2" "Fizz" "4" "Buzz" "Fizz" "7" "8" "Fizz" "Buzz" "11" "Fizz" "13" "14" "FizzBuzz"]

;; ============================================================================
;; SOLUTION 8: Using cycle for infinite pattern
;; ============================================================================

(defn fizz-buzz-cycle
  "FizzBuzz using cycle for the repeating pattern."
  [n]
  (vec
   (map (fn [i pattern-val]
          (or pattern-val (str i)))
        (range 1 (inc n))
        (cycle fizz-buzz-pattern))))

(fizz-buzz-cycle 20)
;; => ["1" "2" "Fizz" "4" "Buzz" "Fizz" "7" "8" "Fizz" "Buzz" "11" "Fizz" "13" "14" "FizzBuzz" "16" "17" "Fizz" "19" "Buzz"]

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `zero?` is cleaner than `(= 0 ...)`
(zero? (mod 15 3))  ;; => true
(= 0 (mod 15 3))    ;; => true (but less idiomatic)

;; 2. `cond` for multiple conditions
;;    Returns first truthy branch, or nil if none match

;; 3. `when` returns nil if condition is false (useful for str concatenation)
(str (when true "Hello") (when false "World"))
;; => "Hello"

;; 4. `mapv` returns a vector (vs `map` which returns a lazy seq)

;; 5. Threading macro `->>` for readability
(->> [3 5]
     (filter #(zero? (mod 15 %)))
     (map {3 "Fizz" 5 "Buzz"})
     (apply str))
;; => "FizzBuzz"

;; 6. `cycle` creates infinite repeating sequence
(take 10 (cycle [1 2 3]))
;; => (1 2 3 1 2 3 1 2 3 1)

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(fizz-buzz 1)
;; => ["1"]

(fizz-buzz 0)
;; => [] (range 1 1 is empty)

(fizz-buzz 2)
;; => ["1" "2"]

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; All Solutions:
;;   Time:  O(n) - process each number once
;;   Space: O(n) - store n results
;;
;; Pattern-based approach avoids division operations, which might be
;; slightly faster on some architectures, but the difference is negligible.
;;
;; The extensible approach is O(n * m) where m is the number of divisors,
;; but for the standard FizzBuzz (m=2), it's still O(n).

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Checking 3 and 5 before 15
;;    If you check divisible by 3 first, you'll never reach FizzBuzz!
;;    Solution: Check 15 first, OR use string concatenation approach

;; 2. Returning integers instead of strings
;;    The problem asks for strings

;; 3. Off-by-one: forgetting that it's 1-indexed
;;    Range should be (range 1 (inc n)), not (range n)

;; 4. Using `mod` incorrectly
;;    (mod 15 3) = 0 means 15 IS divisible by 3
;;    (mod 15 4) = 3 means 15 is NOT divisible by 4

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: FizzBuzzWuzz (add another word)
(defn fizz-buzz-wuzz
  "FizzBuzz with an additional word for divisibility by 7."
  [n]
  (fizz-buzz-extensible n [[3 "Fizz"] [5 "Buzz"] [7 "Wuzz"]]))

(take 21 (fizz-buzz-wuzz 21))
;; Includes "Wuzz" for 7, 14, 21 and combinations

;; Variation 2: Sum of FizzBuzz
(defn fizz-buzz-sum
  "Sum of all numbers that are NOT Fizz, Buzz, or FizzBuzz."
  [n]
  (->> (range 1 (inc n))
       (filter #(and (pos? (mod % 3)) (pos? (mod % 5))))
       (reduce +)))

(fizz-buzz-sum 15)
;; => 60 (1+2+4+7+8+11+13+14)

;; Variation 3: Count of each type
(defn fizz-buzz-counts
  "Count occurrences of each type."
  [n]
  (frequencies
   (map (fn [i]
          (cond
            (zero? (mod i 15)) :fizzbuzz
            (zero? (mod i 3))  :fizz
            (zero? (mod i 5))  :buzz
            :else              :number))
        (range 1 (inc n)))))

(fizz-buzz-counts 15)
;; => {:number 8, :fizz 4, :buzz 2, :fizzbuzz 1}

;; Variation 4: FizzBuzz with prime check
(defn prime? [n]
  (and (> n 1)
       (not-any? #(zero? (mod n %)) (range 2 (inc (Math/sqrt n))))))

(defn fizz-buzz-prime
  "FizzBuzz that also marks primes."
  [n]
  (mapv (fn [i]
          (str (cond
                 (zero? (mod i 15)) "FizzBuzz"
                 (zero? (mod i 3))  "Fizz"
                 (zero? (mod i 5))  "Buzz"
                 :else              i)
               (when (prime? i) "*")))
        (range 1 (inc n))))

(fizz-buzz-prime 15)
;; => ["1" "2*" "Fizz*" "4" "Buzz*" "Fizz" "7*" "8" "Fizz" "Buzz" "11*" "Fizz" "13*" "14" "FizzBuzz"]

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Reverse FizzBuzz
;; Given a FizzBuzz output, determine what n was used.

;; Exercise 2: FizzBuzz Range
;; Generate FizzBuzz for a range [start, end] instead of [1, n].

;; Exercise 3: FizzBuzz in Different Base
;; Generate FizzBuzz but output numbers in binary, octal, or hex.

;; Exercise 4: Configurable FizzBuzz
;; Allow user to specify the words and their corresponding divisors.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing fizz-buzz...")

  ;; Basic tests
  (assert (= ["1" "2" "Fizz"] (fizz-buzz 3))
          "Test 1: n=3")
  (assert (= ["1" "2" "Fizz" "4" "Buzz"] (fizz-buzz 5))
          "Test 2: n=5")
  (assert (= ["1" "2" "Fizz" "4" "Buzz" "Fizz" "7" "8" "Fizz" "Buzz"
              "11" "Fizz" "13" "14" "FizzBuzz"]
             (fizz-buzz 15))
          "Test 3: n=15")

  ;; Edge cases
  (assert (= ["1"] (fizz-buzz 1))
          "Test 4: n=1")
  (assert (= [] (fizz-buzz 0))
          "Test 5: n=0")

  ;; Check specific values
  (assert (= "Fizz" (nth (fizz-buzz 20) 2))
          "Test 6: 3 is Fizz")
  (assert (= "Buzz" (nth (fizz-buzz 20) 4))
          "Test 7: 5 is Buzz")
  (assert (= "FizzBuzz" (nth (fizz-buzz 20) 14))
          "Test 8: 15 is FizzBuzz")
  (assert (= "FizzBuzz" (nth (fizz-buzz 30) 29))
          "Test 9: 30 is FizzBuzz")

  ;; Test other implementations
  (assert (= (fizz-buzz 15) (fizz-buzz-concat 15))
          "Test 10: concat version")
  (assert (= (fizz-buzz 15) (fizz-buzz-for 15))
          "Test 11: for version")
  (assert (= (fizz-buzz 15) (fizz-buzz-extensible 15))
          "Test 12: extensible version")
  (assert (= (fizz-buzz 15) (fizz-buzz-reduce 15))
          "Test 13: reduce version")
  (assert (= (fizz-buzz 15) (fizz-buzz-lazy 15))
          "Test 14: lazy version")
  (assert (= (fizz-buzz 15) (fizz-buzz-pattern-based 15))
          "Test 15: pattern-based version")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. Check divisibility by 15 FIRST, or use string concatenation
;;
;; 2. Multiple idiomatic ways in Clojure:
;;    - `cond` for conditional logic
;;    - `mapv` for transforming sequences
;;    - `when` returns nil (useful for string building)
;;
;; 3. The string concatenation approach is most extensible
;;
;; 4. Lazy sequences allow infinite FizzBuzz generation
;;
;; 5. Pattern-based approach avoids modulo operations entirely
;;
;; 6. FizzBuzz is a simple problem that reveals coding style
;;
;; NEXT: easy_12_roman_to_integer.clj
