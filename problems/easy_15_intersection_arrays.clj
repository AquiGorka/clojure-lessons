;; ============================================================================
;; EASY 15: Intersection of Two Arrays
;; ============================================================================
;; Source: LeetCode #349 - Intersection of Two Arrays
;; Difficulty: Easy
;; Topics: Array, Hash Table, Two Pointers, Binary Search, Sorting
;;
;; A fundamental problem that showcases the power of sets for efficient
;; membership testing and set operations.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given two integer arrays nums1 and nums2, return an array of their
;; intersection. Each element in the result must be unique and you may
;; return the result in any order.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: nums1 = [1, 2, 2, 1], nums2 = [2, 2]
;;   Output: [2]
;;
;; Example 2:
;;   Input: nums1 = [4, 9, 5], nums2 = [9, 4, 9, 8, 4]
;;   Output: [9, 4] or [4, 9]
;;   Explanation: [4, 9] is also accepted.

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= nums1.length, nums2.length <= 1000
;; - 0 <= nums1[i], nums2[i] <= 1000

;; ============================================================================
;; SOLUTION 1: Using Set Intersection (Most Idiomatic)
;; ============================================================================
;; Time Complexity: O(n + m)
;; Space Complexity: O(n + m)

(require '[clojure.set :as set])

(defn intersection
  "Find intersection of two arrays using set intersection."
  [nums1 nums2]
  (vec (set/intersection (set nums1) (set nums2))))

(intersection [1 2 2 1] [2 2])
;; => [2]

(intersection [4 9 5] [9 4 9 8 4])
;; => [4 9] (order may vary)

;; ============================================================================
;; SOLUTION 2: Using filter with set membership
;; ============================================================================

(defn intersection-filter
  "Find intersection using filter and set membership."
  [nums1 nums2]
  (let [set2 (set nums2)]
    (vec (distinct (filter set2 nums1)))))

(intersection-filter [1 2 2 1] [2 2])
;; => [2]

;; ============================================================================
;; SOLUTION 3: Using reduce
;; ============================================================================

(defn intersection-reduce
  "Find intersection using reduce."
  [nums1 nums2]
  (let [set2 (set nums2)]
    (vec (reduce (fn [result x]
                   (if (and (set2 x) (not (result x)))
                     (conj result x)
                     result))
                 #{}
                 nums1))))

(intersection-reduce [4 9 5] [9 4 9 8 4])
;; => [9 4]

;; ============================================================================
;; SOLUTION 4: Using for comprehension
;; ============================================================================

(defn intersection-for
  "Find intersection using for comprehension."
  [nums1 nums2]
  (let [set1 (set nums1)
        set2 (set nums2)]
    (vec (for [x set1 :when (set2 x)] x))))

(intersection-for [1 2 2 1] [2 2])
;; => [2]

;; ============================================================================
;; SOLUTION 5: Sorting and Two Pointers
;; ============================================================================
;; Time Complexity: O(n log n + m log m)
;; Space Complexity: O(1) excluding output

(defn intersection-sorted
  "Find intersection using sorted arrays and two pointers."
  [nums1 nums2]
  (let [s1 (sort (distinct nums1))
        s2 (sort (distinct nums2))]
    (loop [i 0
           j 0
           result []]
      (cond
        (or (>= i (count s1)) (>= j (count s2)))
        result

        (= (nth s1 i) (nth s2 j))
        (recur (inc i) (inc j) (conj result (nth s1 i)))

        (< (nth s1 i) (nth s2 j))
        (recur (inc i) j result)

        :else
        (recur i (inc j) result)))))

(intersection-sorted [4 9 5] [9 4 9 8 4])
;; => [4 9]

;; ============================================================================
;; SOLUTION 6: One-liner
;; ============================================================================

(defn intersect [a b]
  (vec (set/intersection (set a) (set b))))

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. clojure.set namespace provides set operations
(set/intersection #{1 2 3} #{2 3 4})  ;; => #{2 3}
(set/union #{1 2 3} #{2 3 4})         ;; => #{1 2 3 4}
(set/difference #{1 2 3} #{2 3 4})    ;; => #{1}

;; 2. Sets can be used as functions (membership test)
(#{1 2 3} 2)    ;; => 2 (truthy)
(#{1 2 3} 5)    ;; => nil (falsy)

;; 3. `set` constructor removes duplicates
(set [1 2 2 3 3 3])  ;; => #{1 2 3}

;; 4. `distinct` returns lazy sequence of unique elements
(distinct [1 2 2 3 3 3])  ;; => (1 2 3)

;; 5. Filter with a set is idiomatic
(filter #{2 3} [1 2 3 4 5])  ;; => (2 3)

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(intersection [] [1 2 3])
;; => []

(intersection [1 2 3] [])
;; => []

(intersection [1 2 3] [4 5 6])
;; => [] (no common elements)

(intersection [1 1 1] [1 1 1])
;; => [1] (duplicates don't matter)

(intersection [1] [1])
;; => [1]

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Set Intersection (Solution 1):
;;   Time:  O(n + m) - creating sets is O(n) and O(m), intersection is O(min(n,m))
;;   Space: O(n + m) - storing both sets
;;
;; Filter with Set (Solution 2):
;;   Time:  O(n + m) - O(m) to create set, O(n) to filter
;;   Space: O(m) - only storing one set
;;
;; Two Pointers (Solution 5):
;;   Time:  O(n log n + m log m) - dominated by sorting
;;   Space: O(1) excluding output
;;
;; The set-based approach is optimal for this problem.

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Forgetting to remove duplicates from result
;;    The problem asks for unique elements

;; 2. Not importing clojure.set namespace
;;    (require '[clojure.set :as set]) is needed

;; 3. Confusing set/intersection with filter
;;    set/intersection works on sets, not sequences

;; 4. Using contains? incorrectly
;;    (contains? [1 2 3] 2) checks INDEX, not value!
;;    Use a set instead: (#{1 2 3} 2) or (contains? #{1 2 3} 2)

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Intersection of Two Arrays II (with duplicates)
;; Each element should appear as many times as it shows in both arrays
(defn intersection-ii
  "Return intersection with duplicates counted."
  [nums1 nums2]
  (let [freq1 (frequencies nums1)
        freq2 (frequencies nums2)]
    (vec (mapcat (fn [[k v1]]
                   (repeat (min v1 (get freq2 k 0)) k))
                 freq1))))

(intersection-ii [1 2 2 1] [2 2])
;; => [2 2]

(intersection-ii [4 9 5] [9 4 9 8 4])
;; => [4 9]

;; Variation 2: Intersection of multiple arrays
(defn intersection-multi
  "Find intersection of multiple arrays."
  [& arrays]
  (vec (apply set/intersection (map set arrays))))

(intersection-multi [1 2 3] [2 3 4] [2 3 5])
;; => [2 3]

;; Variation 3: Union of two arrays
(defn union
  "Find union of two arrays (unique elements from both)."
  [nums1 nums2]
  (vec (set/union (set nums1) (set nums2))))

(union [1 2 3] [2 3 4])
;; => [1 2 3 4]

;; Variation 4: Difference (elements in first but not second)
(defn difference
  "Find elements in nums1 that are not in nums2."
  [nums1 nums2]
  (vec (set/difference (set nums1) (set nums2))))

(difference [1 2 3 4] [2 4])
;; => [1 3]

;; Variation 5: Symmetric difference (elements in either but not both)
(defn symmetric-difference
  "Find elements in either array but not both."
  [nums1 nums2]
  (let [s1 (set nums1)
        s2 (set nums2)]
    (vec (set/union (set/difference s1 s2)
                    (set/difference s2 s1)))))

(symmetric-difference [1 2 3] [2 3 4])
;; => [1 4]

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Intersection Size
;; Return the size of the intersection without computing the full intersection.

;; Exercise 2: K Common Elements
;; Find if there are at least k common elements between two arrays.

;; Exercise 3: Intersection of Sorted Arrays
;; Optimize for already-sorted input arrays.

;; Exercise 4: Streaming Intersection
;; Find intersection when arrays are too large to fit in memory.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing intersection...")

  ;; Basic tests
  (assert (= #{2} (set (intersection [1 2 2 1] [2 2])))
          "Test 1: Basic case")
  (assert (= #{4 9} (set (intersection [4 9 5] [9 4 9 8 4])))
          "Test 2: Multiple common elements")

  ;; Edge cases
  (assert (= [] (intersection [] [1 2 3]))
          "Test 3: First array empty")
  (assert (= [] (intersection [1 2 3] []))
          "Test 4: Second array empty")
  (assert (= [] (intersection [1 2 3] [4 5 6]))
          "Test 5: No common elements")
  (assert (= [1] (intersection [1 1 1] [1 1 1]))
          "Test 6: All same elements")

  ;; Single elements
  (assert (= [1] (intersection [1] [1]))
          "Test 7: Single same element")
  (assert (= [] (intersection [1] [2]))
          "Test 8: Single different elements")

  ;; Test other implementations
  (assert (= #{2} (set (intersection-filter [1 2 2 1] [2 2])))
          "Test 9: filter version")
  (assert (= #{2} (set (intersection-reduce [1 2 2 1] [2 2])))
          "Test 10: reduce version")
  (assert (= #{2} (set (intersection-for [1 2 2 1] [2 2])))
          "Test 11: for version")
  (assert (= [2] (intersection-sorted [1 2 2 1] [2 2]))
          "Test 12: sorted version")

  ;; Variations
  (assert (= [2 2] (sort (intersection-ii [1 2 2 1] [2 2])))
          "Test 13: intersection with duplicates")
  (assert (= #{2 3} (set (intersection-multi [1 2 3] [2 3 4] [2 3 5])))
          "Test 14: multi-array intersection")
  (assert (= #{1 2 3 4} (set (union [1 2 3] [2 3 4])))
          "Test 15: union")
  (assert (= #{1 3} (set (difference [1 2 3 4] [2 4])))
          "Test 16: difference")
  (assert (= #{1 4} (set (symmetric-difference [1 2 3] [2 3 4])))
          "Test 17: symmetric difference")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. `clojure.set/intersection` is the idiomatic solution
;;    (set/intersection (set a) (set b))
;;
;; 2. Sets can be used as predicates for membership testing
;;    (filter #{2 3} coll) filters elements that are in the set
;;
;; 3. `frequencies` is useful for the duplicate-counting variant
;;
;; 4. clojure.set provides: intersection, union, difference, subset?, superset?
;;
;; 5. Creating a set automatically removes duplicates
;;
;; 6. The two-pointer approach works well if arrays are already sorted
;;
;; 7. Remember: `contains?` on vectors checks INDICES, not values!
;;    Use sets for value membership testing
;;
;; ============================================================================
;; CONGRATULATIONS!
;; ============================================================================
;;
;; You've completed all 15 Easy problems! You should now be comfortable with:
;; - Basic Clojure syntax and data structures
;; - Using reduce, map, filter effectively
;; - Working with sets and maps
;; - Common algorithmic patterns (two pointers, hash maps, etc.)
;;
;; NEXT: Move on to the Medium problems starting with medium_01_group_anagrams.clj
