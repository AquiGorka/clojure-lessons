;; ============================================================================
;; EASY 08: Maximum Subarray
;; ============================================================================
;; Source: LeetCode #53 - Maximum Subarray
;; Difficulty: Easy
;; Topics: Array, Dynamic Programming, Divide and Conquer
;;
;; A classic problem that introduces Kadane's Algorithm - one of the most
;; elegant algorithms for finding maximum subarrays in linear time.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given an integer array `nums`, find the subarray with the largest sum,
;; and return its sum.
;;
;; A subarray is a contiguous non-empty sequence of elements within an array.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
;;   Output: 6
;;   Explanation: The subarray [4, -1, 2, 1] has the largest sum = 6.
;;
;; Example 2:
;;   Input: nums = [1]
;;   Output: 1
;;   Explanation: The subarray [1] has the largest sum = 1.
;;
;; Example 3:
;;   Input: nums = [5, 4, -1, 7, 8]
;;   Output: 23
;;   Explanation: The subarray [5, 4, -1, 7, 8] has the largest sum = 23.

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= nums.length <= 10^5
;; - -10^4 <= nums[i] <= 10^4

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; Key insight (Kadane's Algorithm):
;; At each position, we decide: should we extend the current subarray,
;; or start fresh from this position?
;;
;; current_sum = max(nums[i], current_sum + nums[i])
;;
;; If current_sum + nums[i] < nums[i], that means current_sum < 0,
;; so it's better to start fresh.
;;
;; Visual trace for [-2, 1, -3, 4, -1, 2, 1, -5, 4]:
;;   i=0: num=-2, current=max(-2, -2)=-2, max=-2
;;   i=1: num=1,  current=max(1, -2+1)=1, max=1
;;   i=2: num=-3, current=max(-3, 1-3)=-2, max=1
;;   i=3: num=4,  current=max(4, -2+4)=4, max=4
;;   i=4: num=-1, current=max(-1, 4-1)=3, max=4
;;   i=5: num=2,  current=max(2, 3+2)=5, max=5
;;   i=6: num=1,  current=max(1, 5+1)=6, max=6
;;   i=7: num=-5, current=max(-5, 6-5)=1, max=6
;;   i=8: num=4,  current=max(4, 1+4)=5, max=6
;;
;; Maximum sum = 6

;; ============================================================================
;; SOLUTION 1: Kadane's Algorithm with reduce (Optimal)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(1)

(defn max-subarray
  "Find maximum subarray sum using Kadane's algorithm."
  [nums]
  (let [[max-sum _]
        (reduce
         (fn [[max-sum current-sum] num]
           (let [new-current (max num (+ current-sum num))]
             [(max max-sum new-current) new-current]))
         [(first nums) (first nums)]  ; Initial: both are first element
         (rest nums))]                 ; Process rest
    max-sum))

(max-subarray [-2 1 -3 4 -1 2 1 -5 4])
;; => 6

(max-subarray [1])
;; => 1

(max-subarray [5 4 -1 7 8])
;; => 23

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
;; Initial state: [max-sum=-2, current-sum=-2]
;;
;; num=1:
;;   new-current = max(1, -2+1) = max(1, -1) = 1
;;   state = [max(-2, 1), 1] = [1, 1]
;;
;; num=-3:
;;   new-current = max(-3, 1-3) = max(-3, -2) = -2
;;   state = [max(1, -2), -2] = [1, -2]
;;
;; num=4:
;;   new-current = max(4, -2+4) = max(4, 2) = 4
;;   state = [max(1, 4), 4] = [4, 4]
;;
;; num=-1:
;;   new-current = max(-1, 4-1) = max(-1, 3) = 3
;;   state = [max(4, 3), 3] = [4, 3]
;;
;; num=2:
;;   new-current = max(2, 3+2) = max(2, 5) = 5
;;   state = [max(4, 5), 5] = [5, 5]
;;
;; num=1:
;;   new-current = max(1, 5+1) = max(1, 6) = 6
;;   state = [max(5, 6), 6] = [6, 6]
;;
;; num=-5:
;;   new-current = max(-5, 6-5) = max(-5, 1) = 1
;;   state = [max(6, 1), 1] = [6, 1]
;;
;; num=4:
;;   new-current = max(4, 1+4) = max(4, 5) = 5
;;   state = [max(6, 5), 5] = [6, 5]
;;
;; Final: max-sum = 6

;; ============================================================================
;; SOLUTION 2: Using reductions (Show intermediate states)
;; ============================================================================

(defn max-subarray-trace
  "Kadane's algorithm showing all intermediate states."
  [nums]
  (reductions
   (fn [[max-sum current-sum] num]
     (let [new-current (max num (+ current-sum num))]
       [(max max-sum new-current) new-current]))
   [(first nums) (first nums)]
   (rest nums)))

(max-subarray-trace [-2 1 -3 4 -1 2 1 -5 4])
;; => ([-2 -2] [1 1] [1 -2] [4 4] [4 3] [5 5] [6 6] [6 1] [6 5])

;; ============================================================================
;; SOLUTION 3: Using loop/recur
;; ============================================================================

(defn max-subarray-loop
  "Kadane's algorithm with explicit loop."
  [nums]
  (loop [remaining (rest nums)
         max-sum (first nums)
         current-sum (first nums)]
    (if (empty? remaining)
      max-sum
      (let [num (first remaining)
            new-current (max num (+ current-sum num))]
        (recur (rest remaining)
               (max max-sum new-current)
               new-current)))))

(max-subarray-loop [-2 1 -3 4 -1 2 1 -5 4])
;; => 6

;; ============================================================================
;; SOLUTION 4: Simplified using reduce with single accumulator
;; ============================================================================

(defn max-subarray-v2
  "Simplified version - track max-ending-here, update global max."
  [nums]
  (second
   (reduce
    (fn [[current-max global-max] num]
      (let [new-current (max num (+ current-max num))]
        [new-current (max global-max new-current)]))
    [0 Long/MIN_VALUE]
    nums)))

(max-subarray-v2 [-2 1 -3 4 -1 2 1 -5 4])
;; => 6

;; ============================================================================
;; SOLUTION 5: Brute Force (for understanding)
;; ============================================================================
;; Time Complexity: O(n²)
;; Space Complexity: O(1)

(defn max-subarray-brute
  "Check all possible subarrays."
  [nums]
  (let [n (count nums)]
    (apply max
           (for [i (range n)
                 j (range i n)]
             (reduce + (subvec (vec nums) i (inc j)))))))

(max-subarray-brute [-2 1 -3 4 -1 2 1 -5 4])
;; => 6

;; This is O(n²) and inefficient, but helps understand the problem.

;; ============================================================================
;; SOLUTION 6: Divide and Conquer (Alternative O(n log n))
;; ============================================================================
;; This approach splits the array and considers:
;; 1. Max subarray entirely in left half
;; 2. Max subarray entirely in right half
;; 3. Max subarray crossing the middle

(defn max-crossing-sum
  "Find max sum that crosses the midpoint."
  [nums left mid right]
  (let [;; Max sum going left from mid
        left-sum (reduce (fn [[max-sum sum] i]
                          (let [new-sum (+ sum (nums i))]
                            [(max max-sum new-sum) new-sum]))
                        [Long/MIN_VALUE 0]
                        (range mid (dec left) -1))
        ;; Max sum going right from mid+1
        right-sum (reduce (fn [[max-sum sum] i]
                           (let [new-sum (+ sum (nums i))]
                             [(max max-sum new-sum) new-sum]))
                         [Long/MIN_VALUE 0]
                         (range (inc mid) (inc right)))]
    (+ (first left-sum) (first right-sum))))

(defn max-subarray-dc
  "Divide and conquer approach."
  ([nums] (max-subarray-dc (vec nums) 0 (dec (count nums))))
  ([nums left right]
   (if (= left right)
     (nums left)
     (let [mid (quot (+ left right) 2)
           left-max (max-subarray-dc nums left mid)
           right-max (max-subarray-dc nums (inc mid) right)
           cross-max (max-crossing-sum nums left mid right)]
       (max left-max right-max cross-max)))))

(max-subarray-dc [-2 1 -3 4 -1 2 1 -5 4])
;; => 6

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `reduce` with compound state
;;    Pack multiple values into a vector and destructure in the function

;; 2. `reductions` to see all intermediate states
(reductions + [1 2 3 4 5])
;; => (1 3 6 10 15)

;; 3. `max` with multiple arguments
(max 1 5 3 8 2)
;; => 8

;; 4. Using `first` as default value for single-element arrays
;;    Works naturally because first element is always part of the answer

;; 5. Long/MIN_VALUE for initial "negative infinity"
Long/MIN_VALUE
;; => -9223372036854775808

;; ============================================================================
;; RETURN INDICES VERSION
;; ============================================================================
;; Often you need to return the actual subarray, not just the sum

(defn max-subarray-indices
  "Return [start-idx end-idx max-sum] for the maximum subarray."
  [nums]
  (let [[result _]
        (reduce
         (fn [[best current] [idx num]]
           (let [{:keys [sum start]} current
                 ;; Decide: extend or start fresh?
                 [new-sum new-start] (if (> num (+ sum num))
                                       [num idx]
                                       [(+ sum num) start])
                 new-current {:sum new-sum :start new-start}
                 ;; Update best if we found a better sum
                 new-best (if (> new-sum (:sum best))
                            {:sum new-sum :start new-start :end idx}
                            best)]
             [new-best new-current]))
         [{:sum Long/MIN_VALUE :start 0 :end 0}
          {:sum 0 :start 0}]
         (map-indexed vector nums))]
    [(:start result) (:end result) (:sum result)]))

(max-subarray-indices [-2 1 -3 4 -1 2 1 -5 4])
;; => [3 6 6] (indices 3 to 6 inclusive, sum 6)

(subvec (vec [-2 1 -3 4 -1 2 1 -5 4]) 3 7)
;; => [4 -1 2 1]

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(max-subarray [1])
;; => 1 (single element)

(max-subarray [-1])
;; => -1 (single negative)

(max-subarray [1 2 3 4])
;; => 10 (all positive - take entire array)

(max-subarray [-1 -2 -3 -4])
;; => -1 (all negative - take largest single element)

(max-subarray [0 0 0])
;; => 0 (all zeros)

(max-subarray [-2 1])
;; => 1

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Kadane's Algorithm:
;;   Time:  O(n) - single pass through array
;;   Space: O(1) - only track two values
;;
;; Brute Force:
;;   Time:  O(n²) or O(n³) - check all subarrays
;;   Space: O(1)
;;
;; Divide and Conquer:
;;   Time:  O(n log n) - recurrence T(n) = 2T(n/2) + O(n)
;;   Space: O(log n) - recursion depth
;;
;; Kadane's algorithm is optimal.

;; ============================================================================
;; WHY KADANE'S WORKS
;; ============================================================================
;;
;; The key insight: at each position, we make a local decision.
;;
;; current_sum represents the maximum sum ending at the current position.
;;
;; For each new element num:
;;   - If current_sum + num > num, extend the subarray
;;   - If current_sum + num <= num, start a new subarray from here
;;   - This simplifies to: new_current = max(num, current_sum + num)
;;
;; Why is starting fresh optimal when current_sum < 0?
;;   - A negative prefix can only hurt us
;;   - Any subarray starting after a negative prefix will be better
;;
;; Global maximum is simply the best local maximum we've seen.

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Initializing max to 0 instead of first element
;;    Fails for all-negative arrays: [-3, -2, -1] should return -1, not 0

;; 2. Forgetting that subarray must be non-empty
;;    We must include at least one element

;; 3. Not handling single-element arrays

;; 4. Confusing subarray (contiguous) with subsequence (not necessarily contiguous)

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Maximum Product Subarray
;; The catch: negatives can become positives when multiplied!
(defn max-product-subarray
  "Find the contiguous subarray with largest product."
  [nums]
  (let [[max-prod _ _]
        (reduce
         (fn [[global-max local-max local-min] num]
           (let [candidates [(* local-max num) (* local-min num) num]
                 new-max (apply max candidates)
                 new-min (apply min candidates)]
             [(max global-max new-max) new-max new-min]))
         [(first nums) (first nums) (first nums)]
         (rest nums))]
    max-prod))

(max-product-subarray [2 3 -2 4])
;; => 6 ([2, 3])

(max-product-subarray [-2 0 -1])
;; => 0

(max-product-subarray [-2 3 -4])
;; => 24 (entire array)

;; Variation 2: Maximum Circular Subarray Sum
;; Array is circular - can wrap around
(defn max-subarray-circular
  "Maximum subarray sum in a circular array."
  [nums]
  (let [total (reduce + nums)
        max-sum (max-subarray nums)
        ;; Min subarray = total - (max of negated array)
        min-sum (- (max-subarray (map - nums)))]
    (if (= min-sum total)
      ;; All negative - can't use wrap-around trick
      max-sum
      (max max-sum (- total min-sum)))))

(max-subarray-circular [1 -2 3 -2])
;; => 3

(max-subarray-circular [5 -3 5])
;; => 10 (wrap: [5, 5])

;; Variation 3: Maximum Subarray with at least K elements
(defn max-subarray-k
  "Maximum subarray sum with at least k elements."
  [nums k]
  (let [n (count nums)
        prefix-sum (vec (reductions + 0 nums))
        ;; For each ending position, we need max sum with at least k elements
        window-sum (fn [end]
                    (- (prefix-sum (inc end))
                       (prefix-sum (- (inc end) k))))]
    (loop [i (dec k)
           max-sum (window-sum (dec k))
           prev-max 0]  ; max sum ending at i-k or before
      (if (>= i n)
        max-sum
        (let [;; Sum of k elements ending at i
              base (window-sum i)
              ;; Can extend with prev-max if positive
              with-ext (if (pos? prev-max) (+ base prev-max) base)
              new-max (max max-sum with-ext)
              ;; Update prev-max for next iteration
              new-prev (max 0 (+ prev-max (nums (- i (dec k)))))]
          (recur (inc i) new-max new-prev))))))

;; Variation 4: Count subarrays with sum equal to K
(defn count-subarrays-sum-k
  "Count subarrays that sum to exactly k."
  [nums k]
  (let [[count _]
        (reduce
         (fn [[cnt prefix-counts] prefix-sum]
           (let [need (- prefix-sum k)
                 matches (get prefix-counts need 0)]
             [(+ cnt matches)
              (update prefix-counts prefix-sum (fnil inc 0))]))
         [0 {0 1}]
         (reductions + nums))]
    count))

(count-subarrays-sum-k [1 1 1] 2)
;; => 2 ([1,1] at positions 0-1 and 1-2)

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Maximum Sum of Two Non-Overlapping Subarrays
;; Given L and M, find max sum of two non-overlapping subarrays of lengths L and M.

;; Exercise 2: Shortest Subarray with Sum at Least K
;; Find the length of the shortest subarray with sum >= k.

;; Exercise 3: Maximum Subarray Sum After One Deletion
;; You can delete at most one element. Find maximum subarray sum.

;; Exercise 4: K-Concatenation Maximum Sum
;; Array is concatenated k times. Find maximum subarray sum (modulo 10^9 + 7).

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing max-subarray...")

  ;; Basic tests
  (assert (= 6 (max-subarray [-2 1 -3 4 -1 2 1 -5 4]))
          "Test 1: Main example")
  (assert (= 1 (max-subarray [1]))
          "Test 2: Single element")
  (assert (= 23 (max-subarray [5 4 -1 7 8]))
          "Test 3: All positive sum")

  ;; Edge cases
  (assert (= -1 (max-subarray [-1]))
          "Test 4: Single negative")
  (assert (= -1 (max-subarray [-3 -2 -1 -4]))
          "Test 5: All negative")
  (assert (= 10 (max-subarray [1 2 3 4]))
          "Test 6: All positive")
  (assert (= 0 (max-subarray [0 0 0]))
          "Test 7: All zeros")

  ;; Various patterns
  (assert (= 1 (max-subarray [-2 1]))
          "Test 8: Negative then positive")
  (assert (= 3 (max-subarray [1 -1 1 -1 1 -1 1 -1 1]))
          "Test 9: Alternating")
  (assert (= 100 (max-subarray [-50 100 -50]))
          "Test 10: Middle peak")

  ;; Test other implementations
  (assert (= 6 (max-subarray-loop [-2 1 -3 4 -1 2 1 -5 4]))
          "Test 11: Loop version")
  (assert (= 6 (max-subarray-dc [-2 1 -3 4 -1 2 1 -5 4]))
          "Test 12: Divide and conquer")
  (assert (= 6 (max-subarray-brute [-2 1 -3 4 -1 2 1 -5 4]))
          "Test 13: Brute force")

  ;; Test indices version
  (let [[start end sum] (max-subarray-indices [-2 1 -3 4 -1 2 1 -5 4])]
    (assert (= 6 sum) "Test 14: Indices version - sum")
    (assert (= [3 6] [start end]) "Test 14: Indices version - bounds"))

  ;; Variations
  (assert (= 6 (max-product-subarray [2 3 -2 4]))
          "Test 15: Max product")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. Kadane's Algorithm is elegant: one pass, O(1) space
;;    - current = max(num, current + num)
;;    - global = max(global, current)
;;
;; 2. The key insight: negative prefix can only hurt, so start fresh
;;
;; 3. `reduce` with compound state [max-sum current-sum] is idiomatic
;;
;; 4. Initialize with first element, not 0 or MIN_VALUE (handle all-negative)
;;
;; 5. This pattern extends to many problems:
;;    - Maximum product subarray (track min too)
;;    - Circular arrays (use min subarray trick)
;;    - Count subarrays with sum K (prefix sums + hash map)
;;
;; 6. `reductions` is great for debugging and understanding state changes
;;
;; NEXT: easy_09_contains_duplicate.clj
