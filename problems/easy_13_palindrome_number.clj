;; ============================================================================
;; EASY 13: Palindrome Number
;; ============================================================================
;; Source: LeetCode #9 - Palindrome Number
;; Difficulty: Easy
;; Topics: Math
;;
;; A fundamental problem that can be solved with string conversion or pure
;; math. The mathematical approach teaches number manipulation techniques.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given an integer x, return true if x is a palindrome, and false otherwise.
;;
;; An integer is a palindrome when it reads the same backward as forward.
;; For example, 121 is a palindrome while 123 is not.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: x = 121
;;   Output: true
;;   Explanation: 121 reads as 121 from left to right and from right to left.
;;
;; Example 2:
;;   Input: x = -121
;;   Output: false
;;   Explanation: From left to right, it reads -121. From right to left,
;;                it becomes 121-. Therefore it is not a palindrome.
;;
;; Example 3:
;;   Input: x = 10
;;   Output: false
;;   Explanation: Reads 01 from right to left. Therefore it is not a palindrome.

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - -2^31 <= x <= 2^31 - 1
;;
;; Follow up: Could you solve it without converting the integer to a string?

;; ============================================================================
;; SOLUTION 1: String Conversion (Simple and Idiomatic)
;; ============================================================================
;; Time Complexity: O(n) where n is number of digits
;; Space Complexity: O(n) for the string

(defn palindrome-number?
  "Check if a number is a palindrome using string conversion."
  [x]
  (let [s (str x)]
    (= s (apply str (reverse s)))))

(palindrome-number? 121)
;; => true

(palindrome-number? -121)
;; => false

(palindrome-number? 10)
;; => false

;; ============================================================================
;; SOLUTION 2: Using clojure.string/reverse
;; ============================================================================

(require '[clojure.string :as str])

(defn palindrome-number-str?
  "Check palindrome using clojure.string/reverse."
  [x]
  (let [s (str x)]
    (= s (str/reverse s))))

(palindrome-number-str? 121)
;; => true

;; ============================================================================
;; SOLUTION 3: Mathematical Approach (No String Conversion)
;; ============================================================================
;; Time Complexity: O(log₁₀(n))
;; Space Complexity: O(1)
;;
;; Key insight: Reverse half of the number and compare.
;; We only need to reverse half to avoid overflow issues.

(defn palindrome-number-math?
  "Check palindrome without string conversion."
  [x]
  (cond
    ;; Negative numbers are not palindromes
    (neg? x) false

    ;; Numbers ending in 0 are not palindromes (except 0 itself)
    (and (pos? x) (zero? (mod x 10))) false

    :else
    ;; Reverse half the number
    (loop [num x
           reversed 0]
      (if (< num reversed)
        ;; We've passed the middle
        (or (= num reversed)           ; Even number of digits
            (= num (quot reversed 10))) ; Odd number of digits
        (recur (quot num 10)
               (+ (* reversed 10) (mod num 10)))))))

(palindrome-number-math? 121)
;; => true

(palindrome-number-math? -121)
;; => false

(palindrome-number-math? 10)
;; => false

(palindrome-number-math? 12321)
;; => true

;; ============================================================================
;; DETAILED TRACE (Mathematical Approach)
;; ============================================================================
;;
;; Input: x = 12321
;;
;; Initial: num = 12321, reversed = 0
;;
;; Step 1: num(12321) >= reversed(0)
;;   reversed = 0 * 10 + 12321 % 10 = 1
;;   num = 12321 / 10 = 1232
;;
;; Step 2: num(1232) >= reversed(1)
;;   reversed = 1 * 10 + 1232 % 10 = 12
;;   num = 1232 / 10 = 123
;;
;; Step 3: num(123) >= reversed(12)
;;   reversed = 12 * 10 + 123 % 10 = 123
;;   num = 123 / 10 = 12
;;
;; Step 4: num(12) < reversed(123)
;;   Check: num(12) == reversed/10(12)? YES!
;;
;; Result: true (odd-length palindrome)

;; ============================================================================
;; SOLUTION 4: Full Number Reversal
;; ============================================================================
;; Reverse the entire number and compare (simpler but potential overflow)

(defn reverse-number
  "Reverse the digits of a non-negative integer."
  [n]
  (loop [num n
         reversed 0]
    (if (zero? num)
      reversed
      (recur (quot num 10)
             (+ (* reversed 10) (mod num 10))))))

(defn palindrome-number-full?
  "Check palindrome by reversing the entire number."
  [x]
  (and (>= x 0)
       (= x (reverse-number x))))

(palindrome-number-full? 121)
;; => true

(palindrome-number-full? 12321)
;; => true

;; ============================================================================
;; SOLUTION 5: Extract Digits and Compare
;; ============================================================================

(defn get-digits
  "Extract digits of a number as a sequence."
  [n]
  (if (zero? n)
    [0]
    (loop [num (Math/abs n)
           digits '()]
      (if (zero? num)
        digits
        (recur (quot num 10)
               (cons (mod num 10) digits))))))

(defn palindrome-number-digits?
  "Check palindrome by extracting and comparing digits."
  [x]
  (if (neg? x)
    false
    (let [digits (get-digits x)]
      (= digits (reverse digits)))))

(palindrome-number-digits? 121)
;; => true

(get-digits 12345)
;; => (1 2 3 4 5)

;; ============================================================================
;; SOLUTION 6: Two-Pointer Style (on digits)
;; ============================================================================

(defn palindrome-number-two-ptr?
  "Check palindrome using two-pointer approach on digits."
  [x]
  (if (neg? x)
    false
    (let [digits (vec (get-digits x))
          n (count digits)]
      (loop [left 0
             right (dec n)]
        (cond
          (>= left right) true
          (not= (digits left) (digits right)) false
          :else (recur (inc left) (dec right)))))))

(palindrome-number-two-ptr? 12321)
;; => true

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `str` converts anything to string
(str 123)      ;; => "123"
(str -456)     ;; => "-456"

;; 2. `reverse` works on any sequence
(reverse "hello")           ;; => (\o \l \l \e \h)
(apply str (reverse "hello"))  ;; => "olleh"

;; 3. `clojure.string/reverse` works directly on strings
(str/reverse "hello")       ;; => "olleh"

;; 4. Integer division and modulo
(quot 123 10)  ;; => 12 (integer division)
(mod 123 10)   ;; => 3  (remainder)
(rem 123 10)   ;; => 3  (same for positive numbers)

;; 5. `neg?`, `pos?`, `zero?` predicates
(neg? -5)      ;; => true
(pos? 5)       ;; => true
(zero? 0)      ;; => true

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(palindrome-number? 0)
;; => true (0 is a palindrome)

(palindrome-number? -1)
;; => false (negative numbers are not palindromes)

(palindrome-number? 1)
;; => true (single digit)

(palindrome-number? 11)
;; => true (two same digits)

(palindrome-number? 10)
;; => false (ends in 0)

(palindrome-number? 1000021)
;; => false

(palindrome-number? 1000001)
;; => true

;; Large numbers
(palindrome-number? 2147447412)
;; => true

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; String Conversion:
;;   Time:  O(n) where n is number of digits
;;   Space: O(n) for the string
;;
;; Mathematical (Half Reversal):
;;   Time:  O(n/2) = O(n) where n is number of digits
;;   Space: O(1) - only stores a few integers
;;
;; Full Reversal:
;;   Time:  O(n)
;;   Space: O(1)
;;
;; The mathematical approach is preferred when:
;; - Memory is constrained
;; - String operations are expensive
;; - The problem explicitly asks to avoid string conversion

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Forgetting negative numbers
;;    -121 is NOT a palindrome (the minus sign doesn't mirror)

;; 2. Not handling numbers ending in 0
;;    10, 100, 1000 are not palindromes (leading zeros don't exist)

;; 3. Integer overflow when reversing
;;    Reversing 2147483647 would overflow in 32-bit
;;    The half-reversal approach avoids this

;; 4. Off-by-one in digit extraction
;;    Make sure to handle 0 correctly

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Check if any permutation is palindrome
(defn can-be-palindrome?
  "Check if digits can be rearranged to form a palindrome."
  [x]
  (if (neg? x)
    false
    (let [digit-freq (frequencies (get-digits x))
          odd-counts (count (filter odd? (vals digit-freq)))]
      (<= odd-counts 1))))

(can-be-palindrome? 123)   ;; => false
(can-be-palindrome? 1221)  ;; => true
(can-be-palindrome? 1212)  ;; => true (can rearrange to 1221 or 2112)

;; Variation 2: Nearest palindrome
(defn nearest-palindrome
  "Find the nearest palindrome to n (not equal to n)."
  [n]
  (loop [diff 1]
    (cond
      (palindrome-number? (+ n diff)) (+ n diff)
      (and (pos? (- n diff)) (palindrome-number? (- n diff))) (- n diff)
      :else (recur (inc diff)))))

(nearest-palindrome 123)   ;; => 121
(nearest-palindrome 100)   ;; => 99

;; Variation 3: Count palindromes in range
(defn count-palindromes-in-range
  "Count palindrome numbers in [start, end]."
  [start end]
  (count (filter palindrome-number? (range start (inc end)))))

(count-palindromes-in-range 1 100)
;; => 18 (1-9: 9 palindromes, 11,22,33,44,55,66,77,88,99: 9 more)

;; Variation 4: Generate all n-digit palindromes
(defn n-digit-palindromes
  "Generate all n-digit palindromic numbers."
  [n]
  (if (= n 1)
    (range 1 10)
    (let [half-len (quot (inc n) 2)
          start (long (Math/pow 10 (dec half-len)))
          end (long (Math/pow 10 half-len))]
      (for [half (range start end)
            :let [s (str half)
                  mirror (if (odd? n)
                           (str s (str/reverse (subs s 0 (dec half-len))))
                           (str s (str/reverse s)))
                  num (Long/parseLong mirror)]
            :when (= n (count (str num)))]
        num))))

(n-digit-palindromes 3)
;; => (101 111 121 131 141 151 161 171 181 191 202 ... 999)

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Super Palindrome
;; A super-palindrome is a palindrome whose square root is also a palindrome.
;; Count super-palindromes in range [left, right].

;; Exercise 2: Palindrome Pairs
;; Given a list of words, find all pairs (i, j) where words[i] + words[j]
;; forms a palindrome.

;; Exercise 3: Largest Palindrome Product
;;Find the largest palindrome made from the product of two n-digit numbers.

;; Exercise 4: Prime Palindrome
;; Find the smallest prime palindrome greater than or equal to n.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing palindrome-number?...")

  ;; Basic tests
  (assert (= true (palindrome-number? 121)) "Test 1: 121")
  (assert (= false (palindrome-number? -121)) "Test 2: -121")
  (assert (= false (palindrome-number? 10)) "Test 3: 10")

  ;; Edge cases
  (assert (= true (palindrome-number? 0)) "Test 4: 0")
  (assert (= true (palindrome-number? 1)) "Test 5: 1")
  (assert (= true (palindrome-number? 11)) "Test 6: 11")
  (assert (= false (palindrome-number? -1)) "Test 7: -1")

  ;; Various palindromes
  (assert (= true (palindrome-number? 12321)) "Test 8: 12321")
  (assert (= true (palindrome-number? 1234321)) "Test 9: 1234321")
  (assert (= true (palindrome-number? 123454321)) "Test 10: 123454321")

  ;; Non-palindromes
  (assert (= false (palindrome-number? 123)) "Test 11: 123")
  (assert (= false (palindrome-number? 100)) "Test 12: 100")
  (assert (= false (palindrome-number? 1000021)) "Test 13: 1000021")

  ;; Test other implementations
  (assert (= true (palindrome-number-str? 121)) "Test 14: str version")
  (assert (= true (palindrome-number-math? 121)) "Test 15: math version")
  (assert (= true (palindrome-number-full? 121)) "Test 16: full reverse")
  (assert (= true (palindrome-number-digits? 121)) "Test 17: digits version")
  (assert (= true (palindrome-number-two-ptr? 121)) "Test 18: two-ptr version")

  ;; Test math version edge cases
  (assert (= false (palindrome-number-math? -121)) "Test 19: math negative")
  (assert (= false (palindrome-number-math? 10)) "Test 20: math ends in 0")
  (assert (= true (palindrome-number-math? 0)) "Test 21: math zero")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. String conversion is simplest and most readable in Clojure
;;    (= s (str/reverse s))
;;
;; 2. Mathematical approach avoids string allocation:
;;    - Reverse half the number
;;    - Compare with the other half
;;    - Handle odd-length numbers specially
;;
;; 3. Negative numbers are never palindromes
;;
;; 4. Numbers ending in 0 (except 0 itself) are never palindromes
;;
;; 5. `quot` and `mod` for integer arithmetic
;;
;; 6. The half-reversal approach prevents integer overflow
;;
;; NEXT: easy_14_move_zeroes.clj
