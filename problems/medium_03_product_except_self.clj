;; ============================================================================
;; MEDIUM 03: Product of Array Except Self
;; ============================================================================
;; Source: LeetCode #238 - Product of Array Except Self
;; Difficulty: Medium
;; Topics: Array, Prefix Sum
;;
;; A clever problem that teaches prefix/suffix product patterns and how to
;; avoid division while achieving O(n) time complexity.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given an integer array nums, return an array answer such that answer[i]
;; is equal to the product of all the elements of nums except nums[i].
;;
;; The product of any prefix or suffix of nums is guaranteed to fit in a
;; 32-bit integer.
;;
;; You must write an algorithm that runs in O(n) time and without using
;; the division operation.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: nums = [1, 2, 3, 4]
;;   Output: [24, 12, 8, 6]
;;   Explanation:
;;     answer[0] = 2 * 3 * 4 = 24
;;     answer[1] = 1 * 3 * 4 = 12
;;     answer[2] = 1 * 2 * 4 = 8
;;     answer[3] = 1 * 2 * 3 = 6
;;
;; Example 2:
;;   Input: nums = [-1, 1, 0, -3, 3]
;;   Output: [0, 0, 9, 0, 0]

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 2 <= nums.length <= 10^5
;; - -30 <= nums[i] <= 30
;; - The product of any prefix or suffix of nums is guaranteed to fit in a
;;   32-bit integer.

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; For each position i, we need: product of all elements EXCEPT nums[i]
;;
;; Key insight: answer[i] = (product of elements before i) * (product of elements after i)
;;              answer[i] = prefix_product[i] * suffix_product[i]
;;
;; We can compute:
;; - prefix_product[i] = nums[0] * nums[1] * ... * nums[i-1]
;; - suffix_product[i] = nums[i+1] * nums[i+2] * ... * nums[n-1]
;;
;; Visual for [1, 2, 3, 4]:
;;   Index:    0    1    2    3
;;   Prefix:   1    1    2    6   (product of elements before)
;;   Suffix:  24   12    4    1   (product of elements after)
;;   Answer:  24   12    8    6   (prefix * suffix)

;; ============================================================================
;; SOLUTION 1: Two-Pass with Prefix and Suffix Arrays
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n)

(defn product-except-self
  "Compute product of all elements except self using prefix/suffix products."
  [nums]
  (let [n (count nums)
        ;; Build prefix products: prefix[i] = product of nums[0..i-1]
        prefix (vec (reductions * 1 (butlast nums)))
        ;; Build suffix products: suffix[i] = product of nums[i+1..n-1]
        suffix (vec (reverse (reductions * 1 (reverse (rest nums)))))]
    ;; Multiply prefix and suffix at each position
    (mapv * prefix suffix)))

(product-except-self [1 2 3 4])
;; => [24 12 8 6]

(product-except-self [-1 1 0 -3 3])
;; => [0 0 9 0 0]

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; nums = [1, 2, 3, 4]
;;
;; Prefix products (product of elements BEFORE each index):
;;   (reductions * 1 [1 2 3]) ; butlast removes the last element
;;   => (1 1 2 6)
;;   prefix = [1, 1, 2, 6]
;;
;; Suffix products (product of elements AFTER each index):
;;   (rest nums) = [2 3 4]
;;   (reverse [2 3 4]) = (4 3 2)
;;   (reductions * 1 (4 3 2)) = (1 4 12 24)
;;   (reverse ...) = (24 12 4 1)
;;   suffix = [24, 12, 4, 1]
;;
;; Final answer: prefix * suffix element-wise
;;   [1*24, 1*12, 2*4, 6*1] = [24, 12, 8, 6]

;; ============================================================================
;; SOLUTION 2: Single Array with Two Passes (O(1) Extra Space)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(1) - output array doesn't count as extra space

(defn product-except-self-optimized
  "Optimized version using output array for intermediate storage."
  [nums]
  (let [n (count nums)
        ;; First pass: fill with prefix products
        result (loop [i 0
                      prefix 1
                      res (vec (repeat n 1))]
                 (if (>= i n)
                   res
                   (recur (inc i)
                          (* prefix (nums i))
                          (assoc res i prefix))))
        ;; Second pass: multiply by suffix products
        result (loop [i (dec n)
                      suffix 1
                      res result]
                 (if (< i 0)
                   res
                   (recur (dec i)
                          (* suffix (nums i))
                          (update res i * suffix))))]
    result))

(product-except-self-optimized [1 2 3 4])
;; => [24 12 8 6]

;; ============================================================================
;; SOLUTION 3: Using reduce with indexed accumulation
;; ============================================================================

(defn product-except-self-reduce
  "Using reduce to build prefix and suffix products."
  [nums]
  (let [n (count nums)
        ;; Build prefix products
        prefixes (vec (reductions * 1 nums))
        ;; Build suffix products (from right)
        suffixes (vec (reverse (reductions * 1 (reverse nums))))]
    ;; answer[i] = prefix[i] * suffix[i+1]
    (mapv (fn [i]
            (* (prefixes i) (suffixes (inc i))))
          (range n))))

(product-except-self-reduce [1 2 3 4])
;; => [24 12 8 6]

;; ============================================================================
;; SOLUTION 4: Elegant functional approach
;; ============================================================================

(defn product-except-self-elegant
  "Elegant functional solution using scan from both directions."
  [nums]
  (let [left-products (reductions * 1 (butlast nums))
        right-products (reverse (reductions * 1 (reverse (rest nums))))]
    (mapv * left-products right-products)))

(product-except-self-elegant [1 2 3 4])
;; => [24 12 8 6]

;; ============================================================================
;; SOLUTION 5: With division (if allowed - NOT meeting constraints)
;; ============================================================================
;; This solution uses division and doesn't handle zeros properly,
;; but shows the intuitive approach.

(defn product-except-self-division
  "Using division - doesn't handle zeros, shown for comparison."
  [nums]
  (let [total-product (reduce * nums)]
    (mapv #(/ total-product %) nums)))

;; Only works when no zeros:
(product-except-self-division [1 2 3 4])
;; => [24 12 8 6]

;; Fails with zeros:
;; (product-except-self-division [1 2 0 4]) => ArithmeticException

;; ============================================================================
;; SOLUTION 6: Handle zeros explicitly with division
;; ============================================================================

(defn product-except-self-with-zeros
  "Handle zeros properly when using division approach."
  [nums]
  (let [zero-count (count (filter zero? nums))
        non-zero-product (reduce * (remove zero? nums))]
    (cond
      ;; More than one zero: all products are zero
      (> zero-count 1)
      (vec (repeat (count nums) 0))

      ;; Exactly one zero: only the zero position has non-zero product
      (= zero-count 1)
      (mapv #(if (zero? %) non-zero-product 0) nums)

      ;; No zeros: use division
      :else
      (mapv #(/ non-zero-product %) nums))))

(product-except-self-with-zeros [-1 1 0 -3 3])
;; => [0 0 9 0 0]

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `reductions` is the key function here - it's like reduce but keeps
;;    all intermediate values
(reductions + [1 2 3 4 5])
;; => (1 3 6 10 15) - running sum

(reductions * 1 [2 3 4])
;; => (1 2 6 24) - running product starting from 1

;; 2. `butlast` removes the last element
(butlast [1 2 3 4])
;; => (1 2 3)

;; 3. `rest` removes the first element
(rest [1 2 3 4])
;; => (2 3 4)

;; 4. `mapv` with multiple collections applies function element-wise
(mapv * [1 2 3] [4 5 6])
;; => [4 10 18]

;; 5. `reverse` works on any sequence
(reverse [1 2 3])
;; => (3 2 1)

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(product-except-self [1 1])
;; => [1 1]

(product-except-self [0 0])
;; => [0 0]

(product-except-self [1 0])
;; => [0 1]

(product-except-self [-1 1])
;; => [1 -1]

(product-except-self [2 3])
;; => [3 2]

;; With zeros
(product-except-self [1 2 3 0])
;; => [0 0 0 6]

(product-except-self [0 0 1 2])
;; => [0 0 0 0]

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Solution 1 (Two Arrays):
;;   Time:  O(n) - three passes through the array
;;   Space: O(n) - storing prefix and suffix arrays
;;
;; Solution 2 (Optimized):
;;   Time:  O(n) - two passes
;;   Space: O(1) - only the output array (not counted as extra space)
;;
;; Why O(n) time?
;;   - Computing prefix products: O(n)
;;   - Computing suffix products: O(n)
;;   - Combining them: O(n)
;;   Total: O(3n) = O(n)
;;
;; Why no division?
;;   - Division by zero would cause issues
;;   - The problem explicitly forbids it
;;   - The prefix/suffix approach is more elegant

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Using division (violates constraints and fails with zeros)

;; 2. Off-by-one errors in prefix/suffix calculation
;;    prefix[i] should NOT include nums[i]
;;    suffix[i] should NOT include nums[i]

;; 3. Not handling the boundary cases
;;    prefix[0] = 1 (empty product)
;;    suffix[n-1] = 1 (empty product)

;; 4. Forgetting that product can be negative
;;    Odd number of negatives = negative product

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Sum except self
(defn sum-except-self
  "Sum of all elements except self."
  [nums]
  (let [total (reduce + nums)]
    (mapv #(- total %) nums)))

(sum-except-self [1 2 3 4])
;; => [9 8 7 6]

;; Variation 2: Product of all pairs
(defn product-pairs
  "For each pair (i,j) where i < j, compute nums[i] * nums[j]."
  [nums]
  (for [i (range (count nums))
        j (range (inc i) (count nums))]
    (* (nums i) (nums j))))

(product-pairs [1 2 3])
;; => (2 3 6)

;; Variation 3: Maximum product except self
(defn max-product-except-self
  "Maximum value in product-except-self result."
  [nums]
  (apply max (product-except-self nums)))

(max-product-except-self [1 2 3 4])
;; => 24

;; Variation 4: Product in range except index
(defn product-range-except
  "Product of nums[start..end] except nums[idx]."
  [nums start end idx]
  (reduce * (concat (subvec nums start idx)
                    (subvec nums (inc idx) (inc end)))))

(product-range-except [1 2 3 4 5] 1 4 2)
;; => 40 (2 * 4 * 5, excluding 3)

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Product of Array Except Self with Modulo
;; Return the result modulo 10^9 + 7.

;; Exercise 2: Minimum Product Except Self
;; Find the minimum value in the product-except-self array.

;; Exercise 3: Count Zeros Except Self
;; For each position, count zeros in the array excluding that position.

;; Exercise 4: XOR Except Self
;; Compute XOR of all elements except self (much easier due to XOR properties!)

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing product-except-self...")

  ;; Basic tests
  (assert (= [24 12 8 6] (product-except-self [1 2 3 4]))
          "Test 1: [1,2,3,4]")
  (assert (= [0 0 9 0 0] (product-except-self [-1 1 0 -3 3]))
          "Test 2: [-1,1,0,-3,3]")

  ;; Edge cases
  (assert (= [1 1] (product-except-self [1 1]))
          "Test 3: [1,1]")
  (assert (= [0 0] (product-except-self [0 0]))
          "Test 4: [0,0]")
  (assert (= [0 1] (product-except-self [1 0]))
          "Test 5: [1,0]")
  (assert (= [1 -1] (product-except-self [-1 1]))
          "Test 6: [-1,1]")
  (assert (= [3 2] (product-except-self [2 3]))
          "Test 7: [2,3]")

  ;; With zeros
  (assert (= [0 0 0 6] (product-except-self [1 2 3 0]))
          "Test 8: single zero")
  (assert (= [0 0 0 0] (product-except-self [0 0 1 2]))
          "Test 9: multiple zeros")

  ;; Negative numbers
  (assert (= [-6 3 2] (product-except-self [1 -2 3]))
          "Test 10: negative numbers")
  (assert (= [6 -3 -2] (product-except-self [-1 2 3]))
          "Test 11: negative at start")

  ;; Test other implementations
  (assert (= [24 12 8 6] (product-except-self-optimized [1 2 3 4]))
          "Test 12: optimized version")
  (assert (= [24 12 8 6] (product-except-self-reduce [1 2 3 4]))
          "Test 13: reduce version")
  (assert (= [24 12 8 6] (product-except-self-elegant [1 2 3 4]))
          "Test 14: elegant version")
  (assert (= [0 0 9 0 0] (product-except-self-with-zeros [-1 1 0 -3 3]))
          "Test 15: zeros handling version")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. The prefix/suffix product pattern avoids division
;;    answer[i] = prefix[i] * suffix[i]
;;
;; 2. `reductions` is perfect for computing running products/sums
;;    (reductions * 1 [a b c]) => (1 a ab abc)
;;
;; 3. Process from both ends when you need "everything except current"
;;
;; 4. This pattern appears in many problems:
;;    - Trapping rain water (max from left * max from right)
;;    - Stock problems (min before, max after)
;;
;; 5. `mapv` with multiple sequences applies function element-wise
;;
;; 6. Handle zeros as a special case when using multiplication
;;
;; NEXT: medium_04_three_sum.clj
