;; ============================================================================
;; EASY 14: Move Zeroes
;; ============================================================================
;; Source: LeetCode #283 - Move Zeroes
;; Difficulty: Easy
;; Topics: Array, Two Pointers
;;
;; A classic in-place array manipulation problem that introduces the
;; two-pointer technique for partitioning elements.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given an integer array nums, move all 0's to the end of it while maintaining
;; the relative order of the non-zero elements.
;;
;; Note: You must do this in-place without making a copy of the array.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: nums = [0, 1, 0, 3, 12]
;;   Output: [1, 3, 12, 0, 0]
;;
;; Example 2:
;;   Input: nums = [0]
;;   Output: [0]

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= nums.length <= 10^4
;; - -2^31 <= nums[i] <= 2^31 - 1

;; ============================================================================
;; CLOJURE PERSPECTIVE
;; ============================================================================
;;
;; In Clojure, we work with immutable data structures, so "in-place" doesn't
;; quite apply. However, we can still demonstrate the algorithms and return
;; a new collection with the desired arrangement.

;; ============================================================================
;; SOLUTION 1: Filter and Concatenate (Most Idiomatic)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n)

(defn move-zeroes
  "Move all zeroes to the end, maintaining order of non-zeroes."
  [nums]
  (let [non-zeroes (filter #(not= 0 %) nums)
        zero-count (- (count nums) (count non-zeroes))]
    (vec (concat non-zeroes (repeat zero-count 0)))))

(move-zeroes [0 1 0 3 12])
;; => [1 3 12 0 0]

(move-zeroes [0])
;; => [0]

;; ============================================================================
;; SOLUTION 2: Using remove and concat
;; ============================================================================

(defn move-zeroes-remove
  "Move zeroes using remove function."
  [nums]
  (let [non-zeroes (remove zero? nums)
        zero-count (count (filter zero? nums))]
    (vec (concat non-zeroes (repeat zero-count 0)))))

(move-zeroes-remove [0 1 0 3 12])
;; => [1 3 12 0 0]

;; ============================================================================
;; SOLUTION 3: Using reduce
;; ============================================================================

(defn move-zeroes-reduce
  "Move zeroes using reduce to build result."
  [nums]
  (let [[non-zeroes zeroes]
        (reduce (fn [[nz z] x]
                  (if (zero? x)
                    [nz (conj z x)]
                    [(conj nz x) z]))
                [[] []]
                nums)]
    (vec (concat non-zeroes zeroes))))

(move-zeroes-reduce [0 1 0 3 12])
;; => [1 3 12 0 0]

;; ============================================================================
;; SOLUTION 4: Using group-by
;; ============================================================================

(defn move-zeroes-group
  "Move zeroes using group-by."
  [nums]
  (let [grouped (group-by zero? nums)]
    (vec (concat (get grouped false [])
                 (get grouped true [])))))

(move-zeroes-group [0 1 0 3 12])
;; => [1 3 12 0 0]

;; ============================================================================
;; SOLUTION 5: Two-Pointer Simulation (Educational)
;; ============================================================================
;; This shows how the classic two-pointer algorithm would work

(defn move-zeroes-two-pointer
  "Simulate two-pointer approach with immutable data."
  [nums]
  (let [v (vec nums)
        n (count v)]
    (loop [v v
           write-idx 0
           read-idx 0]
      (if (>= read-idx n)
        ;; Fill remaining with zeros
        (vec (concat (take write-idx v) (repeat (- n write-idx) 0)))
        (if (not= 0 (v read-idx))
          ;; Non-zero: write it at write-idx
          (recur (assoc v write-idx (v read-idx))
                 (inc write-idx)
                 (inc read-idx))
          ;; Zero: just advance read pointer
          (recur v write-idx (inc read-idx)))))))

(move-zeroes-two-pointer [0 1 0 3 12])
;; => [1 3 12 0 0]

;; ============================================================================
;; SOLUTION 6: Using partition-by (preserves groups)
;; ============================================================================

(defn move-zeroes-partition
  "Note: This doesn't preserve relative order within groups!"
  [nums]
  (let [parts (partition-by zero? nums)]
    ;; This approach is tricky because partition-by creates groups
    ;; We need to be careful about ordering
    (vec (concat (remove zero? nums)
                 (filter zero? nums)))))

;; ============================================================================
;; SOLUTION 7: One-liner
;; ============================================================================

(defn move-zeroes-short [nums]
  (vec (concat (remove zero? nums) (filter zero? nums))))

(move-zeroes-short [0 1 0 3 12])
;; => [1 3 12 0 0]

;; ============================================================================
;; SOLUTION 8: Using sort-by (stable sort)
;; ============================================================================
;; Clojure's sort is stable, so this works!

(defn move-zeroes-sort
  "Move zeroes using stable sort."
  [nums]
  (vec (sort-by zero? nums)))

(move-zeroes-sort [0 1 0 3 12])
;; => [1 3 12 0 0]

;; sort-by zero? puts falsey (non-zeros) first, truthy (zeros) last
;; Stable sort preserves relative order within each group

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `zero?` is a built-in predicate
(zero? 0)    ;; => true
(zero? 5)    ;; => false

;; 2. `remove` is the complement of `filter`
(remove even? [1 2 3 4 5])  ;; => (1 3 5)
(filter even? [1 2 3 4 5])  ;; => (2 4)

;; 3. `repeat` creates a lazy infinite (or finite) sequence
(repeat 5 0)      ;; => (0 0 0 0 0)
(take 3 (repeat 0))  ;; => (0 0 0)

;; 4. `concat` joins sequences lazily
(concat [1 2] [3 4])  ;; => (1 2 3 4)

;; 5. `group-by` partitions by a function's return value
(group-by even? [1 2 3 4 5])
;; => {false [1 3 5], true [2 4]}

;; 6. Clojure's sort is stable (preserves order of equal elements)
(sort-by :priority [{:name "a" :priority 1}
                    {:name "b" :priority 2}
                    {:name "c" :priority 1}])
;; c comes after a because stable sort

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(move-zeroes [0])
;; => [0]

(move-zeroes [1])
;; => [1]

(move-zeroes [0 0 0])
;; => [0 0 0]

(move-zeroes [1 2 3])
;; => [1 2 3]

(move-zeroes [])
;; => []

(move-zeroes [0 0 1])
;; => [1 0 0]

(move-zeroes [1 0 0])
;; => [1 0 0]

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; All Solutions:
;;   Time:  O(n) - single pass through the array
;;   Space: O(n) - creating new collections (Clojure immutability)
;;
;; The imperative two-pointer approach achieves O(1) space in mutable
;; languages, but Clojure's immutable data structures require O(n) space.
;;
;; The sort-by approach is O(n log n) time but works elegantly due to
;; stable sorting.

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Not preserving relative order of non-zero elements
;;    [0, 1, 0, 3, 12] should become [1, 3, 12, 0, 0], not [12, 3, 1, 0, 0]

;; 2. Using partition-by incorrectly
;;    partition-by creates consecutive groups, which may split non-zeros

;; 3. Forgetting that filter/remove return lazy sequences
;;    Wrap in vec if you need a vector

;; 4. Using unstable sort (would break relative order)
;;    Clojure's sort is stable, so this isn't an issue

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Move all instances of a specific value to the end
(defn move-value-to-end
  "Move all instances of val to the end."
  [nums val]
  (vec (concat (remove #(= % val) nums)
               (filter #(= % val) nums))))

(move-value-to-end [1 2 3 2 4 2] 2)
;; => [1 3 4 2 2 2]

;; Variation 2: Move zeroes to the beginning
(defn move-zeroes-to-front
  "Move all zeroes to the beginning."
  [nums]
  (vec (concat (filter zero? nums)
               (remove zero? nums))))

(move-zeroes-to-front [0 1 0 3 12])
;; => [0 0 1 3 12]

;; Variation 3: Count minimum swaps needed
(defn min-swaps-to-move-zeroes
  "Count minimum swaps to move all zeroes to end."
  [nums]
  (let [zero-positions (keep-indexed #(when (zero? %2) %1) nums)
        non-zero-count (count (remove zero? nums))]
    ;; Each zero that's before position non-zero-count needs one swap
    (count (filter #(< % non-zero-count) zero-positions))))

(min-swaps-to-move-zeroes [0 1 0 3 12])
;; => 2 (swap positions 0 and 2)

;; Variation 4: Segregate odd and even (similar pattern)
(defn segregate-even-odd
  "Move all even numbers before odd numbers."
  [nums]
  (vec (concat (filter even? nums)
               (remove even? nums))))

(segregate-even-odd [1 2 3 4 5 6])
;; => [2 4 6 1 3 5]

;; Variation 5: Three-way partition (Dutch National Flag)
(defn three-way-partition
  "Partition into <pivot, =pivot, >pivot."
  [nums pivot]
  (let [less (filter #(< % pivot) nums)
        equal (filter #(= % pivot) nums)
        greater (filter #(> % pivot) nums)]
    (vec (concat less equal greater))))

(three-way-partition [3 1 4 1 5 9 2 6 5 3 5] 5)
;; => [3 1 4 1 2 3 5 5 5 9 6]

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Move Zeroes with Minimum Operations
;; Return both the result and the count of moves/swaps needed.

;; Exercise 2: Interleave Zeroes
;; Distribute zeroes evenly throughout the array if possible.

;; Exercise 3: Sort Colors (LeetCode #75)
;; Given array with 0s, 1s, and 2s, sort in-place.

;; Exercise 4: Remove Duplicates from Sorted Array
;; Remove duplicates in-place and return new length.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing move-zeroes...")

  ;; Basic tests
  (assert (= [1 3 12 0 0] (move-zeroes [0 1 0 3 12]))
          "Test 1: Basic case")
  (assert (= [0] (move-zeroes [0]))
          "Test 2: Single zero")

  ;; Edge cases
  (assert (= [] (move-zeroes []))
          "Test 3: Empty array")
  (assert (= [1] (move-zeroes [1]))
          "Test 4: Single non-zero")
  (assert (= [0 0 0] (move-zeroes [0 0 0]))
          "Test 5: All zeroes")
  (assert (= [1 2 3] (move-zeroes [1 2 3]))
          "Test 6: No zeroes")

  ;; Order preservation
  (assert (= [1 0 0] (move-zeroes [1 0 0]))
          "Test 7: Already correct")
  (assert (= [1 0 0] (move-zeroes [0 0 1]))
          "Test 8: All zeroes first")
  (assert (= [2 1 0 0] (move-zeroes [0 2 0 1]))
          "Test 9: Interleaved")

  ;; Test other implementations
  (assert (= [1 3 12 0 0] (move-zeroes-remove [0 1 0 3 12]))
          "Test 10: remove version")
  (assert (= [1 3 12 0 0] (move-zeroes-reduce [0 1 0 3 12]))
          "Test 11: reduce version")
  (assert (= [1 3 12 0 0] (move-zeroes-group [0 1 0 3 12]))
          "Test 12: group-by version")
  (assert (= [1 3 12 0 0] (move-zeroes-two-pointer [0 1 0 3 12]))
          "Test 13: two-pointer version")
  (assert (= [1 3 12 0 0] (move-zeroes-short [0 1 0 3 12]))
          "Test 14: short version")
  (assert (= [1 3 12 0 0] (move-zeroes-sort [0 1 0 3 12]))
          "Test 15: sort version")

  ;; Variations
  (assert (= [1 3 4 2 2 2] (move-value-to-end [1 2 3 2 4 2] 2))
          "Test 16: move value to end")
  (assert (= [0 0 1 3 12] (move-zeroes-to-front [0 1 0 3 12]))
          "Test 17: move zeroes to front")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. The idiomatic Clojure solution: filter non-zeros, concat zeros
;;    (concat (remove zero? nums) (filter zero? nums))
;;
;; 2. `zero?` is a built-in predicate for checking zero
;;
;; 3. `remove` is the complement of `filter`
;;
;; 4. Clojure's sort is stable - useful for partitioning while preserving order
;;
;; 5. `group-by` creates a map partitioned by a function's result
;;
;; 6. In-place operations aren't natural in Clojure's immutable world,
;;    but the algorithms can still be understood and demonstrated
;;
;; 7. `repeat` creates a sequence of repeated values
;;
;; NEXT: easy_15_intersection_arrays.clj
