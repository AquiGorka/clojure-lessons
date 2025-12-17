;; ============================================================================
;; EASY 03: Merge Two Sorted Lists
;; ============================================================================
;; Source: LeetCode #21 - Merge Two Sorted Lists
;; Difficulty: Easy
;; Topics: Linked List, Recursion
;;
;; A fundamental problem that teaches merging sorted sequences.
;; In Clojure, we'll use lazy sequences instead of traditional linked lists,
;; showcasing the elegance of functional programming.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; You are given the heads of two sorted linked lists `list1` and `list2`.
;;
;; Merge the two lists into one sorted list. The list should be made by
;; splicing together the nodes of the first two lists.
;;
;; Return the head of the merged linked list.
;;
;; In Clojure, we'll represent linked lists as sequences (lists or vectors).

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: list1 = [1, 2, 4], list2 = [1, 3, 4]
;;   Output: [1, 1, 2, 3, 4, 4]
;;
;; Example 2:
;;   Input: list1 = [], list2 = []
;;   Output: []
;;
;; Example 3:
;;   Input: list1 = [], list2 = [0]
;;   Output: [0]

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - The number of nodes in both lists is in the range [0, 50].
;; - -100 <= Node.val <= 100
;; - Both list1 and list2 are sorted in non-decreasing order.

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; The merge step is the same as in merge sort:
;; 1. Compare the first elements of both lists
;; 2. Take the smaller one and add it to the result
;; 3. Repeat until one list is empty
;; 4. Append the remaining list
;;
;; Visual trace for [1, 2, 4] and [1, 3, 4]:
;;   Compare 1 vs 1 -> take 1 from list1, result: [1]
;;   Compare 2 vs 1 -> take 1 from list2, result: [1, 1]
;;   Compare 2 vs 3 -> take 2 from list1, result: [1, 1, 2]
;;   Compare 4 vs 3 -> take 3 from list2, result: [1, 1, 2, 3]
;;   Compare 4 vs 4 -> take 4 from list1, result: [1, 1, 2, 3, 4]
;;   list1 empty, append list2: result: [1, 1, 2, 3, 4, 4]

;; ============================================================================
;; SOLUTION 1: Recursive Approach (Most Elegant)
;; ============================================================================
;; Time Complexity: O(n + m) where n, m are lengths of the lists
;; Space Complexity: O(n + m) for the result (O(n+m) call stack in naive recursion)

(defn merge-two-lists
  "Merge two sorted lists into one sorted list using recursion."
  [list1 list2]
  (cond
    ;; Base cases: if either list is empty, return the other
    (empty? list1) list2
    (empty? list2) list1

    ;; Compare first elements, take smaller, recurse
    (<= (first list1) (first list2))
    (cons (first list1) (merge-two-lists (rest list1) list2))

    :else
    (cons (first list2) (merge-two-lists list1 (rest list2)))))

(merge-two-lists [1 2 4] [1 3 4])
;; => (1 1 2 3 4 4)

(merge-two-lists [] [])
;; => []

(merge-two-lists [] [0])
;; => [0]

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; merge-two-lists [1 2 4] [1 3 4]
;;   1 <= 1, so cons 1 onto merge-two-lists [2 4] [1 3 4]
;;     2 > 1, so cons 1 onto merge-two-lists [2 4] [3 4]
;;       2 <= 3, so cons 2 onto merge-two-lists [4] [3 4]
;;         4 > 3, so cons 3 onto merge-two-lists [4] [4]
;;           4 <= 4, so cons 4 onto merge-two-lists [] [4]
;;             list1 empty, return [4]
;;           => (4 4)
;;         => (3 4 4)
;;       => (2 3 4 4)
;;     => (1 2 3 4 4)
;;   => (1 1 2 3 4 4)

;; ============================================================================
;; SOLUTION 2: Lazy Sequence (Clojure Idiomatic)
;; ============================================================================
;; Using lazy-seq, we can create an infinite merge that only computes
;; elements as needed. This is more memory-efficient for large sequences.

(defn merge-two-lists-lazy
  "Lazy version that only computes elements on demand."
  [list1 list2]
  (lazy-seq
   (cond
     (empty? list1) list2
     (empty? list2) list1
     (<= (first list1) (first list2))
     (cons (first list1) (merge-two-lists-lazy (rest list1) list2))
     :else
     (cons (first list2) (merge-two-lists-lazy list1 (rest list2))))))

;; The lazy version looks the same but wraps in lazy-seq
;; This means elements are computed only when accessed
(take 3 (merge-two-lists-lazy [1 2 4] [1 3 4]))
;; => (1 1 2)  -- only computed what was needed!

;; ============================================================================
;; SOLUTION 3: Using loop/recur (Tail Recursive)
;; ============================================================================
;; This avoids stack overflow for very large lists by using tail recursion.
;; Note: We build the result in reverse and flip at the end.

(defn merge-two-lists-loop
  "Iterative version using loop/recur for tail recursion."
  [list1 list2]
  (loop [l1 (seq list1)
         l2 (seq list2)
         result []]
    (cond
      ;; Both empty - we're done
      (and (empty? l1) (empty? l2))
      result

      ;; One empty - concat the other
      (empty? l1)
      (into result l2)

      (empty? l2)
      (into result l1)

      ;; Both have elements - take smaller
      (<= (first l1) (first l2))
      (recur (rest l1) l2 (conj result (first l1)))

      :else
      (recur l1 (rest l2) (conj result (first l2))))))

(merge-two-lists-loop [1 2 4] [1 3 4])
;; => [1 1 2 3 4 4]

;; ============================================================================
;; SOLUTION 4: Using reduce
;; ============================================================================
;; We can think of merging as reducing over one list while maintaining
;; a position in the other.

(defn merge-two-lists-reduce
  "Using reduce with a clever state management."
  [list1 list2]
  (let [[result remaining]
        (reduce
         (fn [[result l2] x]
           ;; Take all elements from l2 that are smaller than x
           (let [smaller (take-while #(<= % x) l2)
                 rest-l2 (drop-while #(<= % x) l2)]
             [(into (conj (vec result) ) (conj (vec smaller) x)) rest-l2]))
         [[] (seq list2)]
         list1)]
    ;; Append any remaining elements from list2
    (concat result remaining)))

;; Actually, let's do a cleaner reduce version:
(defn merge-two-lists-reduce-v2
  "Cleaner reduce-based merge."
  [list1 list2]
  (loop [l1 list1
         l2 list2
         result []]
    (cond
      (empty? l1) (into result l2)
      (empty? l2) (into result l1)
      :else
      (let [[smaller l1' l2']
            (if (<= (first l1) (first l2))
              [(first l1) (rest l1) l2]
              [(first l2) l1 (rest l2)])]
        (recur l1' l2' (conj result smaller))))))

;; ============================================================================
;; SOLUTION 5: One-liner using sort (Not Optimal but Concise!)
;; ============================================================================
;; This is O(n log n) instead of O(n), but shows Clojure's expressiveness.

(defn merge-two-lists-simple
  "Simple but not optimal - just concatenate and sort."
  [list1 list2]
  (sort (concat list1 list2)))

(merge-two-lists-simple [1 2 4] [1 3 4])
;; => (1 1 2 3 4 4)

;; This works but wastes the fact that inputs are already sorted!

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. Lists in Clojure are Linked Lists
;;    Unlike vectors, Clojure lists are singly-linked lists.
;;    - (first lst) is O(1)
;;    - (rest lst) is O(1)
;;    - (nth lst n) is O(n)

;; 2. Lazy Sequences
;;    Clojure's lazy-seq allows us to define potentially infinite sequences.
;;    Elements are computed only when needed (demanded).

(def infinite-ones (repeat 1))  ; Infinite sequence of 1s
(take 5 infinite-ones)
;; => (1 1 1 1 1)

;; 3. `cons` vs `conj`
;;    - cons: always adds to the front, returns a seq
;;    - conj: adds in the "natural" position (front for lists, end for vectors)

(cons 0 [1 2 3])   ;; => (0 1 2 3) - returns a seq
(conj [1 2 3] 0)   ;; => [1 2 3 0] - adds to end
(conj '(1 2 3) 0)  ;; => (0 1 2 3) - adds to front

;; 4. Pattern matching with `cond`
;;    Clojure's cond is very expressive for multiple conditions

;; 5. Sequence Abstraction
;;    The same merge function works on lists, vectors, and any seq-able!

(merge-two-lists '(1 2 4) '(1 3 4))  ;; Lists
;; => (1 1 2 3 4 4)

(merge-two-lists [1 2 4] [1 3 4])    ;; Vectors
;; => (1 1 2 3 4 4)

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(merge-two-lists [] [])
;; => []

(merge-two-lists [1 2 3] [])
;; => (1 2 3)

(merge-two-lists [] [1 2 3])
;; => [1 2 3]

(merge-two-lists [1] [1])
;; => (1 1)

(merge-two-lists [-10 -5 0] [-7 -3 10])
;; => (-10 -7 -5 -3 0 10)

;; All same elements
(merge-two-lists [2 2 2] [2 2])
;; => (2 2 2 2 2)

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Time Complexity: O(n + m)
;;   - We visit each element exactly once
;;   - Each comparison and cons/conj is O(1)
;;
;; Space Complexity:
;;   - Recursive: O(n + m) for the call stack + result
;;   - Loop/recur: O(n + m) for the result only (no stack growth)
;;   - Lazy: O(1) if consuming one at a time, O(n + m) if realizing all
;;
;; The simple sort solution is O((n+m) log(n+m)) time, which is worse.

;; ============================================================================
;; LINKED LIST REPRESENTATION IN CLOJURE
;; ============================================================================
;;
;; Traditional linked lists have nodes with value and next pointer.
;; In Clojure, we can represent this several ways:

;; Approach 1: Use built-in lists (most idiomatic)
(def list1 '(1 2 4))
(def list2 '(1 3 4))

;; Approach 2: Nested maps (explicit node structure)
(defn make-node [val next]
  {:val val :next next})

(def linked-list-1
  (make-node 1
             (make-node 2
                        (make-node 4 nil))))

;; Approach 3: Recursive definition
(defn list->linked [coll]
  (when (seq coll)
    {:val (first coll)
     :next (list->linked (rest coll))}))

(list->linked [1 2 4])
;; => {:val 1, :next {:val 2, :next {:val 4, :next nil}}}

;; Merge for explicit linked list representation
(defn merge-linked-lists
  "Merge two linked lists in {:val :next} format."
  [l1 l2]
  (cond
    (nil? l1) l2
    (nil? l2) l1
    (<= (:val l1) (:val l2))
    {:val (:val l1) :next (merge-linked-lists (:next l1) l2)}
    :else
    {:val (:val l2) :next (merge-linked-lists l1 (:next l2))}))

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Merge K Sorted Lists
;; This is a Hard problem we'll cover later!
(defn merge-k-lists
  "Merge k sorted lists using divide and conquer."
  [lists]
  (cond
    (empty? lists) '()
    (= 1 (count lists)) (first lists)
    :else
    (let [mid (quot (count lists) 2)
          left (merge-k-lists (take mid lists))
          right (merge-k-lists (drop mid lists))]
      (merge-two-lists left right))))

(merge-k-lists [[1 4 5] [1 3 4] [2 6]])
;; => (1 1 2 3 4 4 5 6)

;; Variation 2: Merge and Remove Duplicates
(defn merge-unique
  "Merge two sorted lists, removing duplicates."
  [list1 list2]
  (distinct (merge-two-lists list1 list2)))

(merge-unique [1 2 4] [1 3 4])
;; => (1 2 3 4)

;; Variation 3: Merge in Descending Order
(defn merge-descending
  "Merge two sorted lists into descending order."
  [list1 list2]
  (reverse (merge-two-lists list1 list2)))

(merge-descending [1 2 4] [1 3 4])
;; => (4 4 3 2 1 1)

;; Variation 4: Intersection of Sorted Lists
(defn intersect-sorted
  "Find common elements in two sorted lists."
  [list1 list2]
  (loop [l1 (seq list1)
         l2 (seq list2)
         result []]
    (cond
      (or (empty? l1) (empty? l2)) result
      (= (first l1) (first l2))
      (recur (rest l1) (rest l2) (conj result (first l1)))
      (< (first l1) (first l2))
      (recur (rest l1) l2 result)
      :else
      (recur l1 (rest l2) result))))

(intersect-sorted [1 2 4] [1 3 4])
;; => [1 4]

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Merge Two Sorted Lists in Place
;; Modify list1 to contain the merged result (not really possible in
;; immutable Clojure, but think about how you'd do it with atoms).

;; Exercise 2: Find the Median of Two Sorted Lists
;; Given two sorted lists, find the median of their combined elements.
;; Can you do it in O(log(n+m)) time?

;; Exercise 3: Merge Sort Implementation
;; Use merge-two-lists as the merge step in a full merge sort algorithm.

;; Exercise 4: Difference of Sorted Lists
;; Find elements in list1 that are NOT in list2.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing merge-two-lists...")

  ;; Basic test
  (assert (= '(1 1 2 3 4 4) (merge-two-lists [1 2 4] [1 3 4]))
          "Test 1: Basic merge")

  ;; Empty lists
  (assert (= '() (seq (merge-two-lists [] [])))
          "Test 2: Both empty")
  (assert (= [0] (merge-two-lists [] [0]))
          "Test 3: First empty")
  (assert (= '(0) (merge-two-lists [0] []))
          "Test 4: Second empty")

  ;; Single elements
  (assert (= '(1 2) (merge-two-lists [1] [2]))
          "Test 5: Single elements")
  (assert (= '(1 1) (merge-two-lists [1] [1]))
          "Test 6: Same single element")

  ;; Negative numbers
  (assert (= '(-3 -2 -1 0 1 2) (merge-two-lists [-2 0 2] [-3 -1 1]))
          "Test 7: Negative numbers")

  ;; One list is subset of other's range
  (assert (= '(1 2 3 4 5) (merge-two-lists [1 5] [2 3 4]))
          "Test 8: Interleaved")

  ;; All same
  (assert (= '(5 5 5 5 5) (merge-two-lists [5 5 5] [5 5]))
          "Test 9: All same elements")

  ;; Test loop version
  (assert (= [1 1 2 3 4 4] (merge-two-lists-loop [1 2 4] [1 3 4]))
          "Test 10: Loop version")

  ;; Test lazy version
  (assert (= '(1 1 2 3 4 4) (doall (merge-two-lists-lazy [1 2 4] [1 3 4])))
          "Test 11: Lazy version")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. Recursion is natural for linked list problems
;;    - Base case: empty list
;;    - Recursive case: process head, recurse on tail
;;
;; 2. Clojure's sequence abstraction unifies different collection types
;;    - The same algorithm works on lists, vectors, and lazy sequences
;;
;; 3. lazy-seq enables memory-efficient processing of large/infinite sequences
;;
;; 4. Use loop/recur for tail recursion to avoid stack overflow
;;
;; 5. cons builds sequences efficiently (O(1) prepend)
;;
;; 6. This merge pattern is the foundation of merge sort
;;
;; NEXT: easy_04_best_time_buy_sell.clj
