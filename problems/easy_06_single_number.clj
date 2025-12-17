;; ============================================================================
;; EASY 06: Single Number
;; ============================================================================
;; Source: LeetCode #136 - Single Number
;; Difficulty: Easy
;; Topics: Array, Bit Manipulation
;;
;; A classic problem that showcases the power of XOR (exclusive or) operation.
;; This problem has an elegant O(1) space solution using bit manipulation.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given a non-empty array of integers `nums`, every element appears twice
;; except for one. Find that single one.
;;
;; You must implement a solution with a linear runtime complexity and use
;; only constant extra space.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: nums = [2, 2, 1]
;;   Output: 1
;;
;; Example 2:
;;   Input: nums = [4, 1, 2, 1, 2]
;;   Output: 4
;;
;; Example 3:
;;   Input: nums = [1]
;;   Output: 1

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= nums.length <= 3 * 10^4
;; - -3 * 10^4 <= nums[i] <= 3 * 10^4
;; - Each element appears exactly twice except for one element which appears once.

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; The key insight is using XOR (exclusive or) properties:
;;
;; XOR Properties:
;; 1. a XOR 0 = a         (identity)
;; 2. a XOR a = 0         (self-inverse)
;; 3. a XOR b = b XOR a   (commutative)
;; 4. (a XOR b) XOR c = a XOR (b XOR c)  (associative)
;;
;; If we XOR all numbers together:
;; - Pairs cancel out (a XOR a = 0)
;; - The single number remains (result XOR 0 = result)
;;
;; Example: [4, 1, 2, 1, 2]
;; 4 XOR 1 XOR 2 XOR 1 XOR 2
;; = 4 XOR (1 XOR 1) XOR (2 XOR 2)  (reorder due to commutativity)
;; = 4 XOR 0 XOR 0
;; = 4

;; ============================================================================
;; SOLUTION 1: XOR (Optimal - O(n) time, O(1) space)
;; ============================================================================

(defn single-number
  "Find the element that appears only once using XOR."
  [nums]
  (reduce bit-xor nums))

(single-number [2 2 1])
;; => 1

(single-number [4 1 2 1 2])
;; => 4

(single-number [1])
;; => 1

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; nums = [4, 1, 2, 1, 2]
;;
;; reduce bit-xor:
;;   4 XOR 1 = 5  (binary: 100 XOR 001 = 101)
;;   5 XOR 2 = 7  (binary: 101 XOR 010 = 111)
;;   7 XOR 1 = 6  (binary: 111 XOR 001 = 110)
;;   6 XOR 2 = 4  (binary: 110 XOR 010 = 100)
;;
;; Result: 4
;;
;; Binary breakdown:
;;   4 = 100
;;   1 = 001
;;   2 = 010
;;   1 = 001
;;   2 = 010
;;   --------
;;   XOR all = 100 = 4 (the 1s and 2s cancel out!)

;; ============================================================================
;; SOLUTION 2: Using frequencies (O(n) time, O(n) space)
;; ============================================================================
;; This doesn't meet the O(1) space requirement but is intuitive.

(defn single-number-freq
  "Find single number using frequency map."
  [nums]
  (let [freq (frequencies nums)]
    (ffirst (filter #(= 1 (val %)) freq))))

(single-number-freq [4 1 2 1 2])
;; => 4

;; Alternative using some
(defn single-number-freq-v2
  "Find single number using frequencies and some."
  [nums]
  (some (fn [[k v]] (when (= v 1) k))
        (frequencies nums)))

;; ============================================================================
;; SOLUTION 3: Using sets (O(n) time, O(n) space)
;; ============================================================================
;; Mathematical approach: 2*(sum of unique) - sum of all = single number

(defn single-number-math
  "Using mathematical formula with sets."
  [nums]
  (- (* 2 (reduce + (set nums)))
     (reduce + nums)))

(single-number-math [4 1 2 1 2])
;; => 4

;; Explanation:
;; unique = {4, 1, 2}, sum = 7
;; 2 * 7 = 14
;; total sum = 4 + 1 + 2 + 1 + 2 = 10
;; 14 - 10 = 4

;; ============================================================================
;; SOLUTION 4: Using reduce with a set (toggle membership)
;; ============================================================================

(defn single-number-toggle
  "Toggle elements in a set - remaining element is the answer."
  [nums]
  (first
   (reduce (fn [seen num]
             (if (seen num)
               (disj seen num)  ; Remove if present
               (conj seen num))) ; Add if not present
           #{}
           nums)))

(single-number-toggle [4 1 2 1 2])
;; => 4

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `bit-xor` is Clojure's XOR function
(bit-xor 5 3)      ;; => 6  (101 XOR 011 = 110)
(bit-xor 5 5)      ;; => 0  (a XOR a = 0)
(bit-xor 0 5)      ;; => 5  (a XOR 0 = a)

;; 2. Other bitwise operations in Clojure
(bit-and 5 3)      ;; => 1  (101 AND 011 = 001)
(bit-or 5 3)       ;; => 7  (101 OR 011 = 111)
(bit-not 5)        ;; => -6 (flips all bits, including sign)
(bit-shift-left 5 2)   ;; => 20 (101 << 2 = 10100)
(bit-shift-right 20 2) ;; => 5  (10100 >> 2 = 101)

;; 3. `frequencies` returns a map of value -> count
(frequencies [1 2 1 3 2 1])
;; => {1 3, 2 2, 3 1}

;; 4. `reduce` without initial value
;;    Uses first element as initial value
(reduce + [1 2 3 4])  ;; => 10
(reduce bit-xor [4 1 2 1 2])  ;; => 4

;; 5. Sets for O(1) membership testing
(def s #{1 2 3})
(s 2)   ;; => 2 (truthy, element exists)
(s 5)   ;; => nil (falsy, element doesn't exist)

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(single-number [1])
;; => 1 (single element)

(single-number [0 0 5])
;; => 5 (with zeros)

(single-number [-1 -1 -2])
;; => -2 (negative numbers)

(single-number [1 2 1 2 3])
;; => 3 (single at end)

(single-number [3 1 2 1 2])
;; => 3 (single at start)

;; Large numbers
(single-number [30000 30000 -30000])
;; => -30000

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; XOR Solution (Optimal):
;;   Time:  O(n) - single pass through array
;;   Space: O(1) - only stores the running XOR result
;;
;; Frequency Map:
;;   Time:  O(n) - build map + find single
;;   Space: O(n) - stores all unique elements
;;
;; Math Solution:
;;   Time:  O(n) - create set + two sums
;;   Space: O(n) - stores unique elements in set
;;
;; Toggle Set:
;;   Time:  O(n) - single pass
;;   Space: O(n/2) on average - set contains unpaired elements
;;
;; The XOR solution is optimal because it achieves O(1) space.

;; ============================================================================
;; WHY XOR WORKS
;; ============================================================================
;;
;; XOR operates bit by bit. For each bit position:
;; - If a bit appears an even number of times (from pairs), it XORs to 0
;; - If a bit appears an odd number of times, it remains
;;
;; Since every number except one appears twice:
;; - All bits from paired numbers cancel out (appear even times)
;; - Only bits from the single number remain (appear odd times)
;;
;; This works regardless of the order of numbers!

;; Let's visualize:
;; [4, 1, 2, 1, 2] in binary:
;;   4 = 100
;;   1 = 001
;;   2 = 010
;;   1 = 001
;;   2 = 010
;;
;; Count 1s at each position:
;;   Position 0 (rightmost): 0+1+0+1+0 = 2 (even) -> 0
;;   Position 1: 0+0+1+0+1 = 2 (even) -> 0
;;   Position 2: 1+0+0+0+0 = 1 (odd) -> 1
;;
;; Result: 100 = 4

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Thinking XOR is only for positive numbers
;;    XOR works on all integers, including negative (two's complement)

;; 2. Forgetting XOR is commutative and associative
;;    Order doesn't matter: 1 XOR 2 XOR 3 = 3 XOR 1 XOR 2

;; 3. Using sum instead of XOR
;;    Sum doesn't work because we'd need to know the duplicate values

;; 4. Confusing XOR with OR
;;    OR: 1 OR 1 = 1
;;    XOR: 1 XOR 1 = 0

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Single Number II - every element appears THREE times except one
;; The single element appears once. Find it.
(defn single-number-ii
  "Every element appears 3 times except one (appears once)."
  [nums]
  ;; Count bits: if count % 3 != 0, that bit belongs to single number
  (reduce
   (fn [result bit-pos]
     (let [bit-count (reduce (fn [cnt num]
                               (+ cnt (bit-and 1 (bit-shift-right num bit-pos))))
                             0
                             nums)]
       (if (zero? (mod bit-count 3))
         result
         (bit-or result (bit-shift-left 1 bit-pos)))))
   0
   (range 32)))

(single-number-ii [2 2 3 2])
;; => 3

(single-number-ii [0 1 0 1 0 1 99])
;; => 99

;; Variation 2: Single Number III - TWO numbers appear once, others twice
(defn single-number-iii
  "Two elements appear once, others appear twice. Find both."
  [nums]
  (let [;; XOR all numbers - result is xor of the two single numbers
        xor-all (reduce bit-xor nums)
        ;; Find any bit that differs (rightmost set bit)
        diff-bit (bit-and xor-all (- xor-all))
        ;; Partition numbers by this bit and XOR each group
        [a b] (reduce
               (fn [[a b] num]
                 (if (zero? (bit-and num diff-bit))
                   [(bit-xor a num) b]
                   [a (bit-xor b num)]))
               [0 0]
               nums)]
    [a b]))

(single-number-iii [1 2 1 3 2 5])
;; => [3 5] or [5 3]

;; Variation 3: Find the duplicate (one number appears twice, others once)
(defn find-duplicate-xor
  "Find the number that appears twice when nums contains 1 to n with one dup."
  [nums]
  ;; XOR nums with 1..n, pairs cancel leaving the duplicate
  (let [n (dec (count nums))
        xor-nums (reduce bit-xor nums)
        xor-range (reduce bit-xor (range 1 (inc n)))]
    (bit-xor xor-nums xor-range)))

(find-duplicate-xor [1 3 4 2 2])
;; => 2

;; Variation 4: Missing Number (nums contains 0 to n except one)
(defn missing-number
  "Find the missing number in 0..n."
  [nums]
  (let [n (count nums)]
    (bit-xor (reduce bit-xor nums)
             (reduce bit-xor (range (inc n))))))

(missing-number [3 0 1])
;; => 2

(missing-number [0 1])
;; => 2

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Single Number in Sorted Array
;; Given a sorted array where every element appears twice except one,
;; find the single element in O(log n) time.
;; Hint: Binary search - pairs are (even, odd) indices before single element.

;; Exercise 2: Element Appearing More Than N/2 Times
;; Find the element that appears more than n/2 times.
;; Can you do it in O(1) space? (Boyer-Moore Voting Algorithm)

;; Exercise 3: Majority Element II
;; Find all elements that appear more than n/3 times.

;; Exercise 4: First Unique Character
;; Given a string, find the first non-repeating character.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing single-number...")

  ;; Basic tests
  (assert (= 1 (single-number [2 2 1]))
          "Test 1: Basic case")
  (assert (= 4 (single-number [4 1 2 1 2]))
          "Test 2: Multiple pairs")
  (assert (= 1 (single-number [1]))
          "Test 3: Single element")

  ;; Edge cases
  (assert (= 5 (single-number [0 0 5]))
          "Test 4: With zeros")
  (assert (= -2 (single-number [-1 -1 -2]))
          "Test 5: Negative numbers")
  (assert (= 0 (single-number [1 0 1]))
          "Test 6: Zero is single")

  ;; Order variations
  (assert (= 3 (single-number [3 1 2 1 2]))
          "Test 7: Single at start")
  (assert (= 3 (single-number [1 2 1 2 3]))
          "Test 8: Single at end")
  (assert (= 3 (single-number [1 3 2 1 2]))
          "Test 9: Single in middle")

  ;; Large numbers
  (assert (= 30000 (single-number [-30000 -30000 30000]))
          "Test 10: Large numbers")

  ;; Test other implementations
  (assert (= 4 (single-number-freq [4 1 2 1 2]))
          "Test 11: Frequency version")
  (assert (= 4 (single-number-math [4 1 2 1 2]))
          "Test 12: Math version")
  (assert (= 4 (single-number-toggle [4 1 2 1 2]))
          "Test 13: Toggle version")

  ;; Variations
  (assert (= 3 (single-number-ii [2 2 3 2]))
          "Test 14: Single Number II")
  (assert (= 2 (missing-number [3 0 1]))
          "Test 15: Missing Number")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. XOR is incredibly useful for "find the unique" problems
;;    - a XOR a = 0 (pairs cancel)
;;    - a XOR 0 = a (identity)
;;
;; 2. `bit-xor` in Clojure, reduce makes it elegant
;;
;; 3. XOR is commutative and associative - order doesn't matter
;;
;; 4. This pattern extends to:
;;    - Finding missing numbers
;;    - Finding duplicates
;;    - Finding two unique elements (with extra trick)
;;
;; 5. The frequency/set solutions work but use O(n) space
;;
;; 6. Bit manipulation often provides O(1) space solutions
;;
;; NEXT: easy_07_climbing_stairs.clj
