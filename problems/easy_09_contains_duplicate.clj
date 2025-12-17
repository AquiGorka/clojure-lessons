;; ============================================================================
;; EASY 09: Contains Duplicate
;; ============================================================================
;; Source: LeetCode #217 - Contains Duplicate
;; Difficulty: Easy
;; Topics: Array, Hash Table, Sorting
;;
;; A simple but important problem that demonstrates the power of sets for
;; O(1) membership testing and introduces multiple solution strategies.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given an integer array `nums`, return `true` if any value appears at least
;; twice in the array, and return `false` if every element is distinct.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: nums = [1, 2, 3, 1]
;;   Output: true
;;   Explanation: 1 appears twice.
;;
;; Example 2:
;;   Input: nums = [1, 2, 3, 4]
;;   Output: false
;;   Explanation: All elements are distinct.
;;
;; Example 3:
;;   Input: nums = [1, 1, 1, 3, 3, 4, 3, 2, 4, 2]
;;   Output: true

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= nums.length <= 10^5
;; - -10^9 <= nums[i] <= 10^9

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; We need to determine if there are any duplicate values.
;;
;; Approaches:
;; 1. Use a set - if set size < array size, there are duplicates
;; 2. Sort and check adjacent elements
;; 3. Use a hash set while iterating (early termination)
;; 4. Use frequencies map

;; ============================================================================
;; SOLUTION 1: Set Size Comparison (Most Idiomatic)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n)

(defn contains-duplicate?
  "Check if array contains duplicates using set comparison."
  [nums]
  (not= (count nums) (count (set nums))))

(contains-duplicate? [1 2 3 1])
;; => true

(contains-duplicate? [1 2 3 4])
;; => false

(contains-duplicate? [1 1 1 3 3 4 3 2 4 2])
;; => true

;; ============================================================================
;; DETAILED EXPLANATION
;; ============================================================================
;;
;; (set nums) creates a set from the array, removing duplicates.
;; If the set has fewer elements than the original array,
;; some elements must have been duplicates.
;;
;; Example: [1, 2, 3, 1]
;;   count of array = 4
;;   set = #{1 2 3}
;;   count of set = 3
;;   4 != 3 -> true (contains duplicate)

;; ============================================================================
;; SOLUTION 2: Using distinct (Alternative)
;; ============================================================================

(defn contains-duplicate-distinct?
  "Using distinct function to remove duplicates."
  [nums]
  (not= (count nums) (count (distinct nums))))

(contains-duplicate-distinct? [1 2 3 1])
;; => true

;; ============================================================================
;; SOLUTION 3: Early Termination with reduce
;; ============================================================================
;; Time Complexity: O(n) worst case, but can terminate early
;; Space Complexity: O(n)
;;
;; This is more efficient when duplicates appear early in the array.

(defn contains-duplicate-early?
  "Check for duplicates with early termination."
  [nums]
  (reduce
   (fn [seen num]
     (if (contains? seen num)
       (reduced true)  ; Found duplicate, stop early
       (conj seen num)))
   #{}
   nums)
  ;; If we didn't return early, reduce returns the set, which is truthy
  ;; We need to check if the result is `true` (found duplicate)
  )

;; Fixed version:
(defn contains-duplicate-early-v2?
  "Check for duplicates with early termination (fixed)."
  [nums]
  (true?
   (reduce
    (fn [seen num]
      (if (contains? seen num)
        (reduced true)
        (conj seen num)))
    #{}
    nums)))

(contains-duplicate-early-v2? [1 2 3 1])
;; => true

(contains-duplicate-early-v2? [1 2 3 4])
;; => false

;; ============================================================================
;; SOLUTION 4: Using loop/recur
;; ============================================================================

(defn contains-duplicate-loop?
  "Using explicit loop with early exit."
  [nums]
  (loop [remaining nums
         seen #{}]
    (if (empty? remaining)
      false
      (let [num (first remaining)]
        (if (contains? seen num)
          true
          (recur (rest remaining) (conj seen num)))))))

(contains-duplicate-loop? [1 2 3 1])
;; => true

;; ============================================================================
;; SOLUTION 5: Using frequencies
;; ============================================================================

(defn contains-duplicate-freq?
  "Using frequencies to count occurrences."
  [nums]
  (some #(> % 1) (vals (frequencies nums))))

(contains-duplicate-freq? [1 2 3 1])
;; => true (returns truthy value)

(boolean (contains-duplicate-freq? [1 2 3 4]))
;; => false

;; ============================================================================
;; SOLUTION 6: Using Sorting (O(1) space if sort is in-place)
;; ============================================================================
;; Time Complexity: O(n log n)
;; Space Complexity: O(n) in Clojure (creates new sorted seq)

(defn contains-duplicate-sort?
  "Check for duplicates by sorting and comparing adjacent."
  [nums]
  (let [sorted (sort nums)]
    (some true?
          (map = sorted (rest sorted)))))

(boolean (contains-duplicate-sort? [1 2 3 1]))
;; => true

(boolean (contains-duplicate-sort? [1 2 3 4]))
;; => false

;; ============================================================================
;; SOLUTION 7: One-liner with some
;; ============================================================================

(defn contains-dup? [nums]
  (not= (count nums) (count (set nums))))

;; Even shorter in a REPL:
;; #(not= (count %) (count (set %)))

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. Sets for uniqueness
;;    Creating a set automatically removes duplicates
(set [1 2 2 3 3 3])
;; => #{1 2 3}

;; 2. `distinct` returns a lazy sequence of unique elements
(distinct [1 2 2 3 3 3])
;; => (1 2 3)

;; 3. `frequencies` returns a map of value -> count
(frequencies [1 2 2 3 3 3])
;; => {1 1, 2 2, 3 3}

;; 4. Sets as predicates
;;    A set can be used as a function to test membership
(#{1 2 3} 2)   ;; => 2 (truthy)
(#{1 2 3} 5)   ;; => nil (falsy)

;; 5. `contains?` for set membership
(contains? #{1 2 3} 2)  ;; => true
(contains? #{1 2 3} 5)  ;; => false

;; 6. `some` finds first truthy result
(some even? [1 3 5 6 7])  ;; => true (6 is even)
(some even? [1 3 5 7])    ;; => nil

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(contains-duplicate? [1])
;; => false (single element)

(contains-duplicate? [1 1])
;; => true (two same elements)

(contains-duplicate? [])
;; => false (empty array - vacuously true that all elements are distinct)

(contains-duplicate? [0 0])
;; => true (zeros)

(contains-duplicate? [-1 -1])
;; => true (negatives)

(contains-duplicate? [1000000000 1000000000])
;; => true (large numbers)

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Set-based (Solution 1):
;;   Time:  O(n) - building set is O(n) average
;;   Space: O(n) - set stores up to n elements
;;
;; Early termination (Solution 3):
;;   Time:  O(n) worst case, O(k) where k is position of first duplicate
;;   Space: O(k) - only stores elements until duplicate found
;;
;; Sorting (Solution 6):
;;   Time:  O(n log n) - dominated by sort
;;   Space: O(n) in Clojure (creates new sequence)
;;
;; Frequencies (Solution 5):
;;   Time:  O(n) - single pass to build map + pass through values
;;   Space: O(n) - stores all elements in map
;;
;; The set-based solution is optimal and most idiomatic in Clojure.

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Using `contains?` on a vector incorrectly
;;    `contains?` checks for INDEX presence in vectors, not value!
(contains? [1 2 3] 2)   ;; => true (index 2 exists)
(contains? [1 2 3] 5)   ;; => false (index 5 doesn't exist)
;; For value checking, use `some` or convert to set

;; 2. Confusing `some` with `any?`
;;    `some` returns the first truthy result, not a boolean
(some #{1} [1 2 3])     ;; => 1 (not true)
(boolean (some #{1} [1 2 3]))  ;; => true

;; 3. Not considering empty arrays
;;    Empty arrays have no duplicates by definition

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Contains Duplicate II
;; Check if there are duplicates within distance k of each other
(defn contains-nearby-duplicate?
  "Return true if nums[i] == nums[j] and |i - j| <= k."
  [nums k]
  (let [result
        (reduce-kv
         (fn [seen idx num]
           (if (and (contains? seen num)
                    (<= (- idx (seen num)) k))
             (reduced true)
             (assoc seen num idx)))
         {}
         (vec nums))]
    (true? result)))

(contains-nearby-duplicate? [1 2 3 1] 3)
;; => true (indices 0 and 3, distance 3)

(contains-nearby-duplicate? [1 0 1 1] 1)
;; => true (indices 2 and 3, distance 1)

(contains-nearby-duplicate? [1 2 3 1 2 3] 2)
;; => false (nearest duplicates are distance 3 apart)

;; Variation 2: Contains Duplicate III
;; Check if there exist indices i, j such that:
;; |i - j| <= indexDiff and |nums[i] - nums[j]| <= valueDiff
;; This is more complex, typically solved with TreeSet/bucket sort

;; Variation 3: Find all duplicates
(defn find-all-duplicates
  "Return all elements that appear more than once."
  [nums]
  (->> nums
       frequencies
       (filter #(> (val %) 1))
       (map key)))

(find-all-duplicates [1 2 3 1 2 4])
;; => (1 2)

;; Variation 4: Count duplicates
(defn count-duplicates
  "Count how many elements appear more than once."
  [nums]
  (->> nums
       frequencies
       vals
       (filter #(> % 1))
       count))

(count-duplicates [1 2 3 1 2 4])
;; => 2

;; Variation 5: Remove duplicates (keep first occurrence)
(defn remove-duplicates
  "Remove duplicate elements, keeping first occurrence."
  [nums]
  (distinct nums))

(remove-duplicates [1 2 3 1 2 4])
;; => (1 2 3 4)

;; Variation 6: Find the duplicate (when exactly one number is duplicated)
(defn find-duplicate
  "Find the one number that appears twice (others appear once)."
  [nums]
  (first
   (for [[k v] (frequencies nums)
         :when (> v 1)]
     k)))

(find-duplicate [1 3 4 2 2])
;; => 2

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: First Duplicate
;; Find the first element that has a duplicate later in the array.
;; Return -1 if no duplicates exist.

;; Exercise 2: K-Distinct Elements
;; Check if the array has exactly k distinct elements.

;; Exercise 3: Majority Element
;; Find the element that appears more than n/2 times.
;; (Boyer-Moore Voting Algorithm)

;; Exercise 4: Single Number
;; All elements appear twice except one. Find the single one.
;; (Hint: XOR)

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing contains-duplicate?...")

  ;; Basic tests
  (assert (= true (contains-duplicate? [1 2 3 1]))
          "Test 1: Has duplicate")
  (assert (= false (contains-duplicate? [1 2 3 4]))
          "Test 2: All distinct")
  (assert (= true (contains-duplicate? [1 1 1 3 3 4 3 2 4 2]))
          "Test 3: Multiple duplicates")

  ;; Edge cases
  (assert (= false (contains-duplicate? [1]))
          "Test 4: Single element")
  (assert (= true (contains-duplicate? [1 1]))
          "Test 5: Two same elements")
  (assert (= false (contains-duplicate? []))
          "Test 6: Empty array")

  ;; Various values
  (assert (= true (contains-duplicate? [0 0]))
          "Test 7: Zeros")
  (assert (= true (contains-duplicate? [-1 -1]))
          "Test 8: Negatives")
  (assert (= true (contains-duplicate? [1000000000 1000000000]))
          "Test 9: Large numbers")
  (assert (= false (contains-duplicate? [-1 0 1]))
          "Test 10: Mixed signs, distinct")

  ;; Test other implementations
  (assert (= true (contains-duplicate-distinct? [1 2 3 1]))
          "Test 11: distinct version")
  (assert (= true (contains-duplicate-early-v2? [1 2 3 1]))
          "Test 12: early termination version")
  (assert (= true (contains-duplicate-loop? [1 2 3 1]))
          "Test 13: loop version")
  (assert (= true (boolean (contains-duplicate-freq? [1 2 3 1])))
          "Test 14: frequencies version")
  (assert (= true (boolean (contains-duplicate-sort? [1 2 3 1])))
          "Test 15: sort version")

  ;; Variations
  (assert (= true (contains-nearby-duplicate? [1 2 3 1] 3))
          "Test 16: nearby duplicate")
  (assert (= false (contains-nearby-duplicate? [1 2 3 1 2 3] 2))
          "Test 17: nearby duplicate - false")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. Sets are perfect for duplicate detection
;;    - O(1) membership testing
;;    - Automatically deduplicate when created from collection
;;
;; 2. The idiomatic Clojure solution is beautifully simple:
;;    (not= (count coll) (count (set coll)))
;;
;; 3. `distinct` is a lazy alternative to `set`
;;
;; 4. For early termination, use `reduce` with `reduced`
;;
;; 5. `frequencies` is useful for counting occurrences
;;
;; 6. Remember: `contains?` on vectors checks indices, not values!
;;
;; 7. This pattern extends to many duplicate-related problems
;;
;; NEXT: easy_10_reverse_list.clj
