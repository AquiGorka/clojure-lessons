;; ============================================================================
;; HARD 01: Median of Two Sorted Arrays
;; ============================================================================
;; Source: LeetCode #4 - Median of Two Sorted Arrays
;; Difficulty: Hard
;; Topics: Array, Binary Search, Divide and Conquer
;;
;; One of the classic hard problems that tests understanding of binary search
;; and the median concept. The key insight is partitioning both arrays.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given two sorted arrays nums1 and nums2 of size m and n respectively,
;; return the median of the two sorted arrays.
;;
;; The overall run time complexity should be O(log(m+n)).

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: nums1 = [1, 3], nums2 = [2]
;;   Output: 2.0
;;   Explanation: merged array = [1, 2, 3] and median is 2.
;;
;; Example 2:
;;   Input: nums1 = [1, 2], nums2 = [3, 4]
;;   Output: 2.5
;;   Explanation: merged array = [1, 2, 3, 4] and median is (2 + 3) / 2 = 2.5

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - nums1.length == m
;; - nums2.length == n
;; - 0 <= m <= 1000
;; - 0 <= n <= 1000
;; - 1 <= m + n <= 2000
;; - -10^6 <= nums1[i], nums2[i] <= 10^6

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; The median is the middle value in a sorted list. If the list has an even
;; number of elements, the median is the average of the two middle values.
;;
;; Naive approach: Merge arrays and find median -> O(m+n)
;; Optimal approach: Binary search on partition -> O(log(min(m,n)))
;;
;; Key insight: We need to partition both arrays such that:
;; 1. Left half has (m+n+1)/2 elements
;; 2. All elements in left half <= all elements in right half
;;
;; If we partition nums1 at position i, we must partition nums2 at j = half - i
;; Then we check if the partition is valid and adjust using binary search.

;; ============================================================================
;; SOLUTION 1: Merge and Find (Simple but O(m+n))
;; ============================================================================
;; Time Complexity: O(m+n)
;; Space Complexity: O(m+n)

(defn find-median-merge
  "Find median by merging arrays (not optimal but clear)."
  [nums1 nums2]
  (let [merged (sort (concat nums1 nums2))
        n (count merged)
        mid (quot n 2)]
    (if (odd? n)
      (double (nth merged mid))
      (/ (+ (nth merged (dec mid)) (nth merged mid)) 2.0))))

(find-median-merge [1 3] [2])
;; => 2.0

(find-median-merge [1 2] [3 4])
;; => 2.5

;; ============================================================================
;; SOLUTION 2: Two Pointers Merge (O(m+n) time, O(1) space)
;; ============================================================================
;; Don't actually merge, just count to the median position

(defn find-median-two-pointers
  "Find median using two pointers without full merge."
  [nums1 nums2]
  (let [m (count nums1)
        n (count nums2)
        total (+ m n)
        mid (quot total 2)]
    (loop [i 0
           j 0
           prev 0
           curr 0
           count 0]
      (if (> count mid)
        (if (odd? total)
          (double curr)
          (/ (+ prev curr) 2.0))
        (let [v1 (if (< i m) (nth nums1 i) Integer/MAX_VALUE)
              v2 (if (< j n) (nth nums2 j) Integer/MAX_VALUE)
              [ni nj val] (if (<= v1 v2)
                            [(inc i) j v1]
                            [i (inc j) v2])]
          (recur ni nj curr val (inc count)))))))

(find-median-two-pointers [1 3] [2])
;; => 2.0

(find-median-two-pointers [1 2] [3 4])
;; => 2.5

;; ============================================================================
;; SOLUTION 3: Binary Search (Optimal O(log(min(m,n))))
;; ============================================================================
;; Time Complexity: O(log(min(m,n)))
;; Space Complexity: O(1)

(defn find-median-sorted-arrays
  "Find median using binary search on partition."
  [nums1 nums2]
  ;; Ensure nums1 is the shorter array
  (let [[nums1 nums2] (if (<= (count nums1) (count nums2))
                        [nums1 nums2]
                        [nums2 nums1])
        m (count nums1)
        n (count nums2)
        half (quot (+ m n 1) 2)]  ; Left half size (ceiling division)

    (if (zero? m)
      ;; Edge case: first array is empty
      (let [mid (quot n 2)]
        (if (odd? n)
          (double (nth nums2 mid))
          (/ (+ (nth nums2 (dec mid)) (nth nums2 mid)) 2.0)))

      ;; Binary search on partition of nums1
      (loop [lo 0
             hi m]
        (let [i (quot (+ lo hi) 2)  ; Partition index for nums1
              j (- half i)           ; Partition index for nums2

              ;; Elements around the partition
              nums1-left  (if (pos? i) (nth nums1 (dec i)) Integer/MIN_VALUE)
              nums1-right (if (< i m) (nth nums1 i) Integer/MAX_VALUE)
              nums2-left  (if (pos? j) (nth nums2 (dec j)) Integer/MIN_VALUE)
              nums2-right (if (< j n) (nth nums2 j) Integer/MAX_VALUE)]

          (cond
            ;; nums1-left is too big, move partition left
            (> nums1-left nums2-right)
            (recur lo (dec i))

            ;; nums2-left is too big, move partition right
            (> nums2-left nums1-right)
            (recur (inc i) hi)

            ;; Valid partition found!
            :else
            (let [max-left (max nums1-left nums2-left)
                  min-right (min nums1-right nums2-right)]
              (if (odd? (+ m n))
                (double max-left)
                (/ (+ max-left min-right) 2.0)))))))))

(find-median-sorted-arrays [1 3] [2])
;; => 2.0

(find-median-sorted-arrays [1 2] [3 4])
;; => 2.5

(find-median-sorted-arrays [] [1])
;; => 1.0

(find-median-sorted-arrays [2] [])
;; => 2.0

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; Input: nums1 = [1, 3], nums2 = [2]
;; m = 2, n = 1, total = 3 (odd)
;; half = (3+1)/2 = 2 (we want 2 elements on left side)
;;
;; Binary search on nums1:
;; lo=0, hi=2
;; i = 1, j = 2 - 1 = 1
;;
;; nums1: [1 | 3]     partition at index 1
;; nums2: [2 |  ]     partition at index 1
;;
;; nums1-left = nums1[0] = 1
;; nums1-right = nums1[1] = 3
;; nums2-left = nums2[0] = 2
;; nums2-right = MAX_VALUE
;;
;; Check: nums1-left(1) <= nums2-right(MAX)? YES
;; Check: nums2-left(2) <= nums1-right(3)? YES
;;
;; Valid partition!
;; max-left = max(1, 2) = 2
;; Total is odd, so median = max-left = 2.0

;; ============================================================================
;; SOLUTION 4: Using lazy-cat for elegant merge
;; ============================================================================

(defn merge-sorted-lazy
  "Lazily merge two sorted sequences."
  [xs ys]
  (lazy-seq
   (cond
     (empty? xs) ys
     (empty? ys) xs
     (<= (first xs) (first ys))
     (cons (first xs) (merge-sorted-lazy (rest xs) ys))
     :else
     (cons (first ys) (merge-sorted-lazy xs (rest ys))))))

(defn find-median-lazy
  "Find median using lazy merge (O(m+n) but lazy)."
  [nums1 nums2]
  (let [merged (merge-sorted-lazy nums1 nums2)
        total (+ (count nums1) (count nums2))
        mid (quot total 2)]
    (if (odd? total)
      (double (nth merged mid))
      (/ (+ (nth merged (dec mid)) (nth merged mid)) 2.0))))

(find-median-lazy [1 3] [2])
;; => 2.0

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. Integer/MIN_VALUE and Integer/MAX_VALUE for boundaries
Integer/MIN_VALUE  ;; => -2147483648
Integer/MAX_VALUE  ;; => 2147483647

;; 2. `quot` for integer division (towards zero)
(quot 7 2)  ;; => 3
(quot -7 2) ;; => -3

;; 3. Destructuring for swapping arrays
(let [[a b] (if condition? [x y] [y x])]
  ;; a and b are now in the desired order
  )

;; 4. Lazy sequences for efficient partial computation
(take 5 (merge-sorted-lazy (range 1000000) (range 1000000)))
;; Only computes first 5 elements!

;; 5. `odd?` and `even?` for parity checks
(odd? 5)   ;; => true
(even? 4)  ;; => true

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(find-median-sorted-arrays [] [1])
;; => 1.0 (one empty array)

(find-median-sorted-arrays [2] [])
;; => 2.0 (other empty array)

(find-median-sorted-arrays [1] [1])
;; => 1.0 (both single, same element)

(find-median-sorted-arrays [1 1 1] [1 1 1])
;; => 1.0 (all same elements)

(find-median-sorted-arrays [1 2] [3 4 5 6])
;; => 3.5 ((3+4)/2)

(find-median-sorted-arrays [1 2 3 4 5] [6 7 8 9 10])
;; => 5.5 ((5+6)/2)

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Merge Approach (Solution 1):
;;   Time:  O(m+n) - merge both arrays
;;   Space: O(m+n) - store merged array
;;
;; Two Pointers (Solution 2):
;;   Time:  O(m+n) - traverse to median position
;;   Space: O(1) - only track pointers and values
;;
;; Binary Search (Solution 3):
;;   Time:  O(log(min(m,n))) - binary search on smaller array
;;   Space: O(1) - constant extra space
;;
;; The binary search approach is optimal and meets the O(log(m+n)) requirement.

;; ============================================================================
;; WHY BINARY SEARCH WORKS
;; ============================================================================
;;
;; We're finding a partition of the combined array into left and right halves.
;;
;; If we partition nums1 at index i, we take i elements from nums1's left.
;; We must then take (half - i) elements from nums2's left.
;;
;; Valid partition means:
;;   nums1[i-1] <= nums2[j]  (left of nums1 <= right of nums2)
;;   nums2[j-1] <= nums1[i]  (left of nums2 <= right of nums1)
;;
;; If nums1[i-1] > nums2[j]: i is too big, decrease it
;; If nums2[j-1] > nums1[i]: i is too small, increase it
;;
;; Binary search finds the correct i in O(log(min(m,n))) time.

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Not ensuring the smaller array is used for binary search
;;    Binary search should be on the smaller array for correctness

;; 2. Off-by-one errors in partition calculation
;;    half = (m + n + 1) / 2 for correct left half size

;; 3. Not handling empty arrays
;;    Check for empty arrays before binary search

;; 4. Incorrect boundary values
;;    Use MIN_VALUE and MAX_VALUE for elements outside array bounds

;; 5. Integer division issues
;;    Use `quot` for integer division, divide by 2.0 for float result

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Find kth element of two sorted arrays
(defn find-kth-element
  "Find the kth smallest element (1-indexed)."
  [nums1 nums2 k]
  (let [merged (merge-sorted-lazy nums1 nums2)]
    (nth merged (dec k))))

(find-kth-element [1 3 5] [2 4 6] 4)
;; => 4

;; Variation 2: Median of k sorted arrays
(defn median-k-arrays
  "Find median of k sorted arrays."
  [arrays]
  (let [merged (sort (apply concat arrays))
        n (count merged)
        mid (quot n 2)]
    (if (odd? n)
      (double (nth merged mid))
      (/ (+ (nth merged (dec mid)) (nth merged mid)) 2.0))))

(median-k-arrays [[1 5 9] [2 6 10] [3 7 11]])
;; => 6.0

;; Variation 3: Check if median would change after adding element
(defn median-would-change?
  "Check if adding x would change the median."
  [nums1 nums2 x]
  (let [current (find-median-sorted-arrays nums1 nums2)
        with-x (find-median-sorted-arrays (sort (conj (vec nums1) x)) nums2)]
    (not= current with-x)))

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Median of Data Stream (LeetCode #295)
;; Design a data structure that supports adding numbers and finding median.

;; Exercise 2: Sliding Window Median (LeetCode #480)
;; Find median of each window of size k sliding through an array.

;; Exercise 3: Kth Smallest Element in Sorted Matrix (LeetCode #378)
;; Matrix where each row and column is sorted.

;; Exercise 4: Find K Pairs with Smallest Sums (LeetCode #373)
;; Given two sorted arrays, find k pairs with smallest sums.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing find-median-sorted-arrays...")

  ;; Basic tests
  (assert (= 2.0 (find-median-sorted-arrays [1 3] [2]))
          "Test 1: [1,3] and [2]")
  (assert (= 2.5 (find-median-sorted-arrays [1 2] [3 4]))
          "Test 2: [1,2] and [3,4]")

  ;; Edge cases - empty arrays
  (assert (= 1.0 (find-median-sorted-arrays [] [1]))
          "Test 3: empty and [1]")
  (assert (= 2.0 (find-median-sorted-arrays [2] []))
          "Test 4: [2] and empty")

  ;; Single elements
  (assert (= 1.0 (find-median-sorted-arrays [1] [1]))
          "Test 5: [1] and [1]")
  (assert (= 1.5 (find-median-sorted-arrays [1] [2]))
          "Test 6: [1] and [2]")

  ;; Different sizes
  (assert (= 3.5 (find-median-sorted-arrays [1 2] [3 4 5 6]))
          "Test 7: [1,2] and [3,4,5,6]")
  (assert (= 5.5 (find-median-sorted-arrays [1 2 3 4 5] [6 7 8 9 10]))
          "Test 8: [1-5] and [6-10]")

  ;; Overlapping ranges
  (assert (= 4.0 (find-median-sorted-arrays [1 3 5 7] [2 4 6 8]))
          "Test 9: interleaved")
  (assert (= 3.0 (find-median-sorted-arrays [1 2 3] [4 5 6]))
          "Test 10: non-overlapping odd total")

  ;; All same elements
  (assert (= 1.0 (find-median-sorted-arrays [1 1 1] [1 1 1]))
          "Test 11: all same")

  ;; Test other implementations
  (assert (= 2.0 (find-median-merge [1 3] [2]))
          "Test 12: merge version")
  (assert (= 2.0 (find-median-two-pointers [1 3] [2]))
          "Test 13: two-pointer version")
  (assert (= 2.0 (find-median-lazy [1 3] [2]))
          "Test 14: lazy version")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. Binary search on partition is the key insight
;;    - Partition both arrays so left half has (m+n+1)/2 elements
;;    - All left elements <= all right elements
;;
;; 2. Always binary search on the smaller array
;;    - Ensures j = half - i is always valid
;;
;; 3. Use MIN_VALUE and MAX_VALUE as sentinels for boundary conditions
;;
;; 4. The median is:
;;    - Odd total: max of left halves
;;    - Even total: (max of left + min of right) / 2
;;
;; 5. This O(log(min(m,n))) solution is optimal
;;
;; 6. Lazy sequences in Clojure are useful for partial computation
;;
;; NEXT: hard_02_regex_matching.clj
