;; ============================================================================
;; EASY 12: Roman to Integer
;; ============================================================================
;; Source: LeetCode #13 - Roman to Integer
;; Difficulty: Easy
;; Topics: Hash Table, Math, String
;;
;; A classic problem that teaches working with maps and recognizing patterns
;; in sequences. The key insight is handling subtraction cases.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Roman numerals are represented by seven different symbols:
;;   I = 1, V = 5, X = 10, L = 50, C = 100, D = 500, M = 1000
;;
;; Given a roman numeral, convert it to an integer.
;;
;; Special cases (subtraction):
;;   - I before V (5) or X (10) means subtract 1
;;   - X before L (50) or C (100) means subtract 10
;;   - C before D (500) or M (1000) means subtract 100

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: s = "III"
;;   Output: 3
;;   Explanation: III = 1 + 1 + 1 = 3
;;
;; Example 2:
;;   Input: s = "LVIII"
;;   Output: 58
;;   Explanation: L = 50, V = 5, III = 3
;;
;; Example 3:
;;   Input: s = "MCMXCIV"
;;   Output: 1994
;;   Explanation: M = 1000, CM = 900, XC = 90, IV = 4

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= s.length <= 15
;; - s contains only the characters ('I', 'V', 'X', 'L', 'C', 'D', 'M')
;; - It is guaranteed that s is a valid roman numeral in range [1, 3999]

;; ============================================================================
;; SOLUTION 1: Using reduce with look-ahead
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(1)

(def roman-values
  "Map of Roman numeral characters to their values."
  {\I 1
   \V 5
   \X 10
   \L 50
   \C 100
   \D 500
   \M 1000})

(defn roman-to-int
  "Convert a Roman numeral string to an integer."
  [s]
  (let [values (map roman-values s)]
    (reduce +
            (map (fn [curr next]
                   (if (< curr next)
                     (- curr)  ; Subtraction case
                     curr))
                 values
                 (concat (rest values) [0])))))  ; Append 0 for last element

(roman-to-int "III")
;; => 3

(roman-to-int "LVIII")
;; => 58

(roman-to-int "MCMXCIV")
;; => 1994

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; Input: "MCMXCIV"
;; values: (1000 100 1000 10 100 1 5)
;;
;; Pair with next (appending 0):
;;   1000 vs 100  -> 1000 >= 100, add 1000
;;   100 vs 1000  -> 100 < 1000, subtract 100 (this is CM = 900)
;;   1000 vs 10   -> 1000 >= 10, add 1000
;;   10 vs 100    -> 10 < 100, subtract 10 (this is XC = 90)
;;   100 vs 1     -> 100 >= 1, add 100
;;   1 vs 5       -> 1 < 5, subtract 1 (this is IV = 4)
;;   5 vs 0       -> 5 >= 0, add 5
;;
;; Sum: 1000 - 100 + 1000 - 10 + 100 - 1 + 5 = 1994

;; ============================================================================
;; SOLUTION 2: Using partition for pairs
;; ============================================================================

(defn roman-to-int-partition
  "Convert using partition to examine pairs."
  [s]
  (let [values (mapv roman-values s)
        pairs (partition 2 1 [0] values)]  ; Sliding window of 2, pad with 0
    (reduce (fn [sum [curr next]]
              (if (< curr next)
                (- sum curr)
                (+ sum curr)))
            0
            pairs)))

(roman-to-int-partition "MCMXCIV")
;; => 1994

;; ============================================================================
;; SOLUTION 3: Using reduce with previous value
;; ============================================================================

(defn roman-to-int-reduce
  "Convert using reduce tracking previous value."
  [s]
  (let [[total _]
        (reduce
         (fn [[total prev] char]
           (let [curr (roman-values char)]
             (if (> curr prev)
               ;; Subtraction case: we added prev, now need to subtract 2*prev
               [(+ total curr (- (* 2 prev))) curr]
               [(+ total curr) curr])))
         [0 0]
         s)]
    total))

(roman-to-int-reduce "MCMXCIV")
;; => 1994

;; ============================================================================
;; SOLUTION 4: Right-to-left approach
;; ============================================================================
;; Process from right to left - if current < previous, subtract

(defn roman-to-int-rtl
  "Convert by processing right to left."
  [s]
  (let [[total _]
        (reduce
         (fn [[total prev] char]
           (let [curr (roman-values char)]
             (if (< curr prev)
               [(- total curr) curr]
               [(+ total curr) curr])))
         [0 0]
         (reverse s))]
    total))

(roman-to-int-rtl "MCMXCIV")
;; => 1994

;; ============================================================================
;; SOLUTION 5: Replace subtractive patterns first
;; ============================================================================

(require '[clojure.string :as str])

(defn roman-to-int-replace
  "Convert by replacing subtractive patterns with additive ones."
  [s]
  (let [expanded (-> s
                     (str/replace "CM" "DCCCC")   ; 900 = 500 + 400
                     (str/replace "CD" "CCCC")    ; 400
                     (str/replace "XC" "LXXXX")   ; 90 = 50 + 40
                     (str/replace "XL" "XXXX")    ; 40
                     (str/replace "IX" "VIIII")   ; 9 = 5 + 4
                     (str/replace "IV" "IIII"))]  ; 4
    (reduce + (map roman-values expanded))))

(roman-to-int-replace "MCMXCIV")
;; => 1994

;; ============================================================================
;; SOLUTION 6: Using loop/recur
;; ============================================================================

(defn roman-to-int-loop
  "Convert using explicit loop."
  [s]
  (loop [chars (seq s)
         total 0
         prev 0]
    (if (empty? chars)
      total
      (let [curr (roman-values (first chars))]
        (recur (rest chars)
               (if (> curr prev)
                 (+ total curr (- (* 2 prev)))
                 (+ total curr))
               curr)))))

(roman-to-int-loop "MCMXCIV")
;; => 1994

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. Characters in Clojure
;;    \I is a character literal, strings are sequences of characters
(first "ABC")  ;; => \A
(seq "ABC")    ;; => (\A \B \C)

;; 2. Maps as functions
;;    A map can be called as a function with a key
(roman-values \X)  ;; => 10
({\a 1 \b 2} \a)   ;; => 1

;; 3. `partition` with step and padding
(partition 2 1 [1 2 3])       ;; => ((1 2) (2 3))
(partition 2 1 [0] [1 2 3])   ;; => ((1 2) (2 3) (3 0)) - padded!

;; 4. `map` with multiple sequences
(map + [1 2 3] [10 20 30])    ;; => (11 22 33)

;; 5. Threading macro `->` for readability
(-> "hello"
    str/upper-case
    (str/replace "L" "X"))
;; => "HEXXO"

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(roman-to-int "I")
;; => 1

(roman-to-int "MMMCMXCIX")
;; => 3999 (maximum valid Roman numeral)

(roman-to-int "IV")
;; => 4

(roman-to-int "IX")
;; => 9

(roman-to-int "XL")
;; => 40

(roman-to-int "XC")
;; => 90

(roman-to-int "CD")
;; => 400

(roman-to-int "CM")
;; => 900

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; All Solutions:
;;   Time:  O(n) where n is the length of the string
;;   Space: O(1) - constant space for the map lookup
;;
;; Note: Roman numerals are bounded by 3999, so n <= 15, making this
;; effectively O(1) for valid inputs.

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Forgetting the subtraction cases
;;    IV is 4, not 6 (I + V)

;; 2. Not handling the last character correctly
;;    The last character has no "next" to compare with

;; 3. Using strings instead of characters for map keys
;;    "I" vs \I - they're different in Clojure

;; 4. Off-by-one in the subtraction logic
;;    When we see CM, we already added C(100), so we need to add
;;    M(1000) - 2*C(200) to get the net effect of -100 + 1000 = 900

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation: Integer to Roman (reverse problem)
(defn int-to-roman
  "Convert an integer to Roman numeral."
  [num]
  (let [mappings [[1000 "M"] [900 "CM"] [500 "D"] [400 "CD"]
                  [100 "C"] [90 "XC"] [50 "L"] [40 "XL"]
                  [10 "X"] [9 "IX"] [5 "V"] [4 "IV"] [1 "I"]]]
    (loop [n num
           result ""]
      (if (zero? n)
        result
        (let [[value roman] (first (filter #(<= (first %) n) mappings))]
          (recur (- n value) (str result roman)))))))

(int-to-roman 1994)
;; => "MCMXCIV"

(int-to-roman 58)
;; => "LVIII"

;; Validate Roman numeral
(defn valid-roman?
  "Check if a string is a valid Roman numeral."
  [s]
  (and (re-matches #"^M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$" s)
       (not (empty? s))))

(valid-roman? "MCMXCIV")  ;; => true
(valid-roman? "IIII")     ;; => false (should be IV)

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Roman Numeral Validator
;; Write a function that validates if a string is a correct Roman numeral.

;; Exercise 2: Roman Calculator
;; Add two Roman numerals and return the result as a Roman numeral.

;; Exercise 3: Roman Range
;; Generate all Roman numerals from 1 to n.

;; Exercise 4: Largest Roman Numeral
;; Find the largest Roman numeral that can be formed using exactly n characters.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing roman-to-int...")

  ;; Basic tests
  (assert (= 3 (roman-to-int "III")) "Test 1: III")
  (assert (= 58 (roman-to-int "LVIII")) "Test 2: LVIII")
  (assert (= 1994 (roman-to-int "MCMXCIV")) "Test 3: MCMXCIV")

  ;; Single characters
  (assert (= 1 (roman-to-int "I")) "Test 4: I")
  (assert (= 5 (roman-to-int "V")) "Test 5: V")
  (assert (= 10 (roman-to-int "X")) "Test 6: X")
  (assert (= 50 (roman-to-int "L")) "Test 7: L")
  (assert (= 100 (roman-to-int "C")) "Test 8: C")
  (assert (= 500 (roman-to-int "D")) "Test 9: D")
  (assert (= 1000 (roman-to-int "M")) "Test 10: M")

  ;; Subtraction cases
  (assert (= 4 (roman-to-int "IV")) "Test 11: IV")
  (assert (= 9 (roman-to-int "IX")) "Test 12: IX")
  (assert (= 40 (roman-to-int "XL")) "Test 13: XL")
  (assert (= 90 (roman-to-int "XC")) "Test 14: XC")
  (assert (= 400 (roman-to-int "CD")) "Test 15: CD")
  (assert (= 900 (roman-to-int "CM")) "Test 16: CM")

  ;; Maximum
  (assert (= 3999 (roman-to-int "MMMCMXCIX")) "Test 17: Maximum")

  ;; Test other implementations
  (assert (= 1994 (roman-to-int-partition "MCMXCIV")) "Test 18: partition")
  (assert (= 1994 (roman-to-int-reduce "MCMXCIV")) "Test 19: reduce")
  (assert (= 1994 (roman-to-int-rtl "MCMXCIV")) "Test 20: right-to-left")
  (assert (= 1994 (roman-to-int-replace "MCMXCIV")) "Test 21: replace")
  (assert (= 1994 (roman-to-int-loop "MCMXCIV")) "Test 22: loop")

  ;; Test int-to-roman
  (assert (= "MCMXCIV" (int-to-roman 1994)) "Test 23: int-to-roman")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. The key insight: if current < next, subtract; otherwise add
;;
;; 2. Multiple valid approaches:
;;    - Compare with next element
;;    - Process right-to-left
;;    - Replace subtractive patterns
;;
;; 3. Maps work as functions in Clojure: (my-map key)
;;
;; 4. `partition` with padding handles edge cases elegantly
;;
;; 5. Character literals use backslash: \I, \V, \X
;;
;; 6. This problem pairs well with the reverse: Integer to Roman
;;
;; NEXT: easy_13_palindrome_number.clj
