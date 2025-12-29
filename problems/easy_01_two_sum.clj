;; ============================================================================
;; EASY 01: Two Sum
;; ============================================================================
;; Source: LeetCode #1 - Two Sum
;; Difficulty: Easy
;; Topics: Array, Hash Map
;;
;; This is often the first problem people solve on LeetCode, and it's a great
;; introduction to using hash maps for O(1) lookup optimization.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given a vector of integers `nums` and an integer `target`, return the indices
;; of the two numbers such that they add up to `target`.
;;
;; You may assume that each input would have exactly one solution, and you may
;; not use the same element twice.
;;
;; You can return the answer in any order.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: nums = [2, 7, 11, 15], target = 9
;;   Output: [0, 1]
;;   Explanation: nums[0] + nums[1] = 2 + 7 = 9
;;
;; Example 2:
;;   Input: nums = [3, 2, 4], target = 6
;;   Output: [1, 2]
;;   Explanation: nums[1] + nums[2] = 2 + 4 = 6
;;
;; Example 3:
;;   Input: nums = [3, 3], target = 6
;;   Output: [0, 1]

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 2 <= nums.length <= 10^4
;; - -10^9 <= nums[i] <= 10^9
;; - -10^9 <= target <= 10^9
;; - Only one valid answer exists.

;; ============================================================================
;; SOLUTION 1: Brute Force (for understanding)
;; ============================================================================
;; Time Complexity: O(n²)
;; Space Complexity: O(1)

(defn two-sum-brute
  "Brute force approach: check every pair of numbers."
  [nums target]
  (let [n (count nums)]
    ;; Use `for` comprehension with a guard (:when clause)
    (first
     (for [i (range n)
           j (range (inc i) n)
           :when (= target (+ (nums i) (nums j)))]
       [i j]))))

;; Let's trace through Example 1: nums = [2, 7, 11, 15], target = 9
;; i=0, j=1: nums[0] + nums[1] = 2 + 7 = 9 ✓ Found!
;; Returns [0, 1]

(two-sum-brute [2 7 11 15] 9)
;; => [0 1]

;; Why is this O(n²)?
;; We're checking every pair: n*(n-1)/2 pairs = O(n²)

;; ============================================================================
;; SOLUTION 2: Hash Map - Two Pass
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n)
;;
;; Key insight: For each number x, we need to find (target - x).
;; If we store all numbers in a hash map with their indices,
;; we can look up the complement in O(1) time.

(defn two-sum-two-pass
  "Two-pass hash map approach."
  [nums target]
  ;; First pass: build a map of {value -> index}
  (let [num->idx (into {} (map-indexed (fn [idx val] [val idx]) nums))]
    ;; Second pass: for each number, look for its complement
    (first
     (for [i (range (count nums))
           :let [complement (- target (nums i))
                 j (get num->idx complement)]
           ;; Make sure complement exists AND it's not the same element
           :when (and j (not= i j))]
       [i j]))))

(two-sum-two-pass [2 7 11 15] 9)
;; => [0 1]

;; Trace:
;; num->idx = {2 0, 7 1, 11 2, 15 3}
;; i=0: complement = 9 - 2 = 7, num->idx has 7 at index 1, return [0, 1]

;; ============================================================================
;; SOLUTION 3: Hash Map - One Pass (Optimal)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n)
;;
;; Even better: we can build the map AS we search.
;; For each element, check if its complement is already in the map.
;; If yes, we found our pair. If no, add the current element to the map.

(defn two-sum
  "One-pass hash map approach - optimal solution."
  [nums target]
  (reduce
   (fn [seen [idx num]]
     (let [complement (- target num)]
       (if-let [complement-idx (get seen complement)]
         ;; Found! Return early using `reduced`
         (reduced [complement-idx idx])
         ;; Not found, add current number to seen map
         (assoc seen num idx))))
   {}  ; Initial empty map
   (map-indexed vector nums)))  ; Create [index, value] pairs

(two-sum [2 7 11 15] 9)
;; => [0 1]

(two-sum [3 2 4] 6)
;; => [1 2]

(two-sum [3 3] 6)
;; => [0 1]

;; Let's trace through Example 1 step by step:
;; nums = [2, 7, 11, 15], target = 9
;;
;; Step 1: idx=0, num=2
;;   complement = 9 - 2 = 7
;;   seen = {}, 7 not in seen
;;   seen = {2: 0}
;;
;; Step 2: idx=1, num=7
;;   complement = 9 - 7 = 2
;;   seen = {2: 0}, 2 IS in seen at index 0!
;;   Return [0, 1]

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `reduce` with `reduced` for early termination
;;    Unlike imperative loops where you can `break`, in Clojure we use `reduced`
;;    to short-circuit a reduce operation.

;; 2. `map-indexed` creates [index, value] pairs
(map-indexed vector [:a :b :c])
;; => ([0 :a] [1 :b] [2 :c])

;; 3. `if-let` for conditional binding
;;    (if-let [x (get map key)] use-x else-clause)
;;    Only executes the "then" branch if the value is truthy.

;; 4. Hash maps in Clojure have O(log32 n) ≈ O(1) lookup
;;    They're implemented as Hash Array Mapped Tries (HAMTs).

;; ============================================================================
;; ALTERNATIVE SOLUTIONS
;; ============================================================================

;; Using loop/recur for explicit iteration control
(defn two-sum-loop
  "Using loop/recur for explicit control flow."
  [nums target]
  (loop [i 0
         seen {}]
    (when (< i (count nums))
      (let [num (nums i)
            complement (- target num)]
        (if-let [j (seen complement)]
          [j i]
          (recur (inc i) (assoc seen num i)))))))

(two-sum-loop [2 7 11 15] 9)
;; => [0 1]

;; A more "Clojure-y" approach using some
(defn two-sum-some
  "Using `some` to find the first match."
  [nums target]
  (let [indexed (map-indexed vector nums)]
    (first
     (reduce
      (fn [seen [i num]]
        (let [complement (- target num)]
          (if-let [j (get (first seen) complement)]
            (reduced [[j i]])  ; Wrap in vector so first works
            [(assoc (first seen) num i)])))
      [{}]
      indexed))))

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Brute Force:
;;   Time:  O(n²) - check all pairs
;;   Space: O(1)  - no extra space
;;
;; Two-Pass Hash Map:
;;   Time:  O(n) - two linear passes
;;   Space: O(n) - store all elements in hash map
;;
;; One-Pass Hash Map (Optimal):
;;   Time:  O(n) - single pass
;;   Space: O(n) - hash map grows to at most n elements
;;
;; The one-pass solution is optimal because:
;; - We must look at each element at least once -> Ω(n)
;; - Hash map gives O(1) lookup for complements

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Returning values instead of indices
;;    Wrong: [2, 7]
;;    Right: [0, 1]

;; 2. Using the same element twice
;;    For nums = [3, 2, 4], target = 6
;;    Don't return [0, 0] even though 3 + 3 = 6
;;    The same ELEMENT can't be used twice (same INDEX)

;; 3. Off-by-one errors with indices
;;    Clojure is 0-indexed, like most languages

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Return the VALUES instead of indices
(defn two-sum-values
  "Return the actual values that sum to target."
  [nums target]
  (first
   (for [x nums
         y nums
         :when (and (= target (+ x y))
                    (not= x y))]  ; Simplified; doesn't handle duplicates perfectly
     [x y])))

;; Better version using the hash map approach
(defn two-sum-values-v2
  "Return values using hash map approach."
  [nums target]
  (let [num-set (set nums)]
    (some (fn [x]
            (let [complement (- target x)]
              (when (and (num-set complement)
                         (or (not= x complement)
                             (> (count (filter #{x} nums)) 1)))
                [x complement])))
          nums)))

;; Variation 2: Count all pairs that sum to target
(defn count-pairs-with-sum
  "Count how many pairs sum to target."
  [nums target]
  (let [freq (frequencies nums)]
    (reduce (fn [count num]
              (let [complement (- target num)
                    complement-count (get freq complement 0)]
                (cond
                  ;; Same number: choose 2 from n = n*(n-1)/2
                  (= num complement)
                  (+ count (/ (* complement-count (dec complement-count)) 2))

                  ;; Different numbers: multiply counts (divide by 2 later)
                  (< num complement)
                  (+ count (* (freq num) complement-count))

                  :else count)))
            0
            (keys freq))))

;; Variation 3: Find if ANY pair sums to target (return boolean)
(defn has-pair-with-sum?
  "Check if any pair sums to target."
  [nums target]
  (boolean
   (reduce
    (fn [seen num]
      (if (seen (- target num))
        (reduced true)
        (conj seen num)))
    #{}
    nums)))

(has-pair-with-sum? [2 7 11 15] 9)
;; => true

(has-pair-with-sum? [2 7 11 15] 100)
;; => false

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Three Sum Target
;; Given nums and target, find THREE numbers that sum to target.
;; Return their indices.
;; Hint: Fix one number and use two-sum for the remaining.

;; Exercise 2: Two Sum - Sorted Array
;; If the input array is SORTED, can you solve it in O(1) space?
;; Hint: Two pointers from both ends.

;; Exercise 3: Two Sum - Multiple Solutions
;; Modify the function to return ALL pairs of indices that sum to target.
;; Example: [1, 1, 2, 2], target = 3 -> [[0, 2], [0, 3], [1, 2], [1, 3]]

;; Exercise 4: Closest Two Sum
;; Find two numbers whose sum is CLOSEST to the target.
;; Return the pair of values (not indices).

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing two-sum...")

  ;; Test case 1
  (assert (= [0 1] (two-sum [2 7 11 15] 9))
          "Test 1 failed: [2,7,11,15] target=9")

  ;; Test case 2
  (assert (= [1 2] (two-sum [3 2 4] 6))
          "Test 2 failed: [3,2,4] target=6")

  ;; Test case 3
  (assert (= [0 1] (two-sum [3 3] 6))
          "Test 3 failed: [3,3] target=6")

  ;; Test case 4: negative numbers
  (assert (= [0 2] (two-sum [-1 -2 -3 -4 -5] -4))
          "Test 4 failed: negative numbers")

  ;; Test case 5: zero
  (assert (= [0 3] (two-sum [0 4 3 0] 0))
          "Test 5 failed: zeros")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. Hash maps are your friend for O(1) lookup problems
;;
;; 2. `reduce` with `reduced` enables early termination in Clojure
;;
;; 3. The "complement" pattern: for x + y = target, look for (target - x)
;;
;; 4. Building data structures AS you iterate is often more efficient
;;    than building them all upfront
;;
;; 5. `map-indexed` is useful when you need both index and value
;;
;; NEXT: easy_02_valid_parentheses.clj
