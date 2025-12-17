;; ============================================================================
;; EASY 10: Reverse Linked List
;; ============================================================================
;; Source: LeetCode #206 - Reverse Linked List
;; Difficulty: Easy
;; Topics: Linked List, Recursion
;;
;; A fundamental problem that teaches list manipulation and recursion.
;; In Clojure, we'll explore multiple approaches including the built-in
;; reverse function and custom implementations.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given the head of a singly linked list, reverse the list, and return
;; the reversed list.
;;
;; In Clojure, we represent linked lists as sequences (lists or vectors).

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: head = [1, 2, 3, 4, 5]
;;   Output: [5, 4, 3, 2, 1]
;;
;; Example 2:
;;   Input: head = [1, 2]
;;   Output: [2, 1]
;;
;; Example 3:
;;   Input: head = []
;;   Output: []

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - The number of nodes in the list is in the range [0, 5000].
;; - -5000 <= Node.val <= 5000

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; We need to reverse the order of elements in a linked list.
;;
;; Traditional approaches:
;; 1. Iterative: Use three pointers (prev, curr, next)
;; 2. Recursive: Reverse rest, then append head to end
;;
;; In Clojure:
;; 1. Use built-in `reverse` function
;; 2. Use `reduce` to build reversed list
;; 3. Use recursion with cons
;; 4. Implement traditional pointer-based approach with loop/recur

;; ============================================================================
;; SOLUTION 1: Built-in reverse (Idiomatic Clojure)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n)

(defn reverse-list
  "Reverse a list using built-in reverse function."
  [lst]
  (reverse lst))

(reverse-list [1 2 3 4 5])
;; => (5 4 3 2 1)

(reverse-list [1 2])
;; => (2 1)

(reverse-list [])
;; => ()

;; ============================================================================
;; SOLUTION 2: Using reduce (Most Educational)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n)
;;
;; Key insight: cons adds to the FRONT of a list, so repeatedly
;; consing elements naturally reverses them.

(defn reverse-list-reduce
  "Reverse using reduce and cons."
  [lst]
  (reduce (fn [acc x] (cons x acc))
          '()
          lst))

(reverse-list-reduce [1 2 3 4 5])
;; => (5 4 3 2 1)

;; Trace:
;; Start: acc = (), lst = [1 2 3 4 5]
;; x=1: (cons 1 ()) = (1)
;; x=2: (cons 2 '(1)) = (2 1)
;; x=3: (cons 3 '(2 1)) = (3 2 1)
;; x=4: (cons 4 '(3 2 1)) = (4 3 2 1)
;; x=5: (cons 5 '(4 3 2 1)) = (5 4 3 2 1)

;; ============================================================================
;; SOLUTION 3: Using conj with lists
;; ============================================================================
;; Note: conj adds to FRONT of lists, END of vectors

(defn reverse-list-conj
  "Reverse using conj (works because conj adds to front of lists)."
  [lst]
  (reduce conj '() lst))

(reverse-list-conj [1 2 3 4 5])
;; => (5 4 3 2 1)

;; ============================================================================
;; SOLUTION 4: Recursive approach
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n) call stack

(defn reverse-list-recursive
  "Reverse using recursion."
  [lst]
  (if (empty? lst)
    '()
    (concat (reverse-list-recursive (rest lst))
            [(first lst)])))

(reverse-list-recursive [1 2 3 4 5])
;; => (5 4 3 2 1)

;; Note: This is O(n²) because concat is O(n)!
;; Not recommended for large lists.

;; ============================================================================
;; SOLUTION 5: Tail-recursive with accumulator
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n) for result, O(1) stack

(defn reverse-list-tail
  "Tail-recursive reverse with accumulator."
  ([lst] (reverse-list-tail lst '()))
  ([lst acc]
   (if (empty? lst)
     acc
     (recur (rest lst) (cons (first lst) acc)))))

(reverse-list-tail [1 2 3 4 5])
;; => (5 4 3 2 1)

;; ============================================================================
;; SOLUTION 6: Using loop/recur (Explicit iteration)
;; ============================================================================

(defn reverse-list-loop
  "Reverse using explicit loop."
  [lst]
  (loop [remaining lst
         reversed '()]
    (if (empty? remaining)
      reversed
      (recur (rest remaining)
             (cons (first remaining) reversed)))))

(reverse-list-loop [1 2 3 4 5])
;; => (5 4 3 2 1)

;; ============================================================================
;; SOLUTION 7: Using into (Another idiomatic approach)
;; ============================================================================

(defn reverse-list-into
  "Reverse using into with a list as target."
  [lst]
  (into '() lst))

(reverse-list-into [1 2 3 4 5])
;; => (5 4 3 2 1)

;; `into` uses conj repeatedly, and conj on a list adds to front

;; ============================================================================
;; TRADITIONAL LINKED LIST REPRESENTATION
;; ============================================================================
;; For completeness, let's implement with explicit node structure

(defn make-node [val next]
  {:val val :next next})

(defn list->linked
  "Convert a sequence to linked list representation."
  [coll]
  (reduce (fn [next val] (make-node val next))
          nil
          (reverse coll)))

(defn linked->list
  "Convert linked list to sequence."
  [node]
  (loop [n node
         result []]
    (if (nil? n)
      result
      (recur (:next n) (conj result (:val n))))))

(list->linked [1 2 3])
;; => {:val 1, :next {:val 2, :next {:val 3, :next nil}}}

(linked->list (list->linked [1 2 3]))
;; => [1 2 3]

;; Reverse for explicit linked list
(defn reverse-linked-list
  "Reverse an explicitly linked list."
  [head]
  (loop [prev nil
         curr head]
    (if (nil? curr)
      prev
      (let [next-node (:next curr)]
        (recur (assoc curr :next prev)
               next-node)))))

(linked->list (reverse-linked-list (list->linked [1 2 3 4 5])))
;; => [5 4 3 2 1]

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `reverse` is built-in and efficient
(reverse [1 2 3])   ;; => (3 2 1)
(reverse '(1 2 3))  ;; => (3 2 1)

;; 2. `cons` always adds to front, returns a seq
(cons 0 [1 2 3])    ;; => (0 1 2 3)
(cons 0 '(1 2 3))   ;; => (0 1 2 3)

;; 3. `conj` adds in the "natural" position
(conj [1 2 3] 0)    ;; => [1 2 3 0] (end for vectors)
(conj '(1 2 3) 0)   ;; => (0 1 2 3) (front for lists)

;; 4. `into` uses conj repeatedly
(into '() [1 2 3])  ;; => (3 2 1) (reversed!)
(into [] '(1 2 3))  ;; => [1 2 3] (same order)

;; 5. Lists are linked lists in Clojure
;;    - first is O(1)
;;    - rest is O(1)
;;    - nth is O(n)
;;    - cons is O(1)

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(reverse-list [])
;; => ()

(reverse-list [1])
;; => (1)

(reverse-list [1 2])
;; => (2 1)

(reverse-list [1 1 1])
;; => (1 1 1)

(reverse-list (range 1000))
;; => Works fine, returns reversed sequence

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Built-in reverse:
;;   Time:  O(n) - visits each element once
;;   Space: O(n) - creates new sequence
;;
;; Reduce with cons:
;;   Time:  O(n) - cons is O(1)
;;   Space: O(n) - builds new list
;;
;; Naive recursion with concat:
;;   Time:  O(n²) - concat is O(n), called n times
;;   Space: O(n) - call stack
;;
;; Tail-recursive:
;;   Time:  O(n)
;;   Space: O(n) for result, O(1) stack (tail call optimized)
;;
;; All solutions except naive recursion are O(n) time.

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Using concat in recursion (O(n²))
;;    concat traverses its first argument, making it slow

;; 2. Forgetting that conj behaves differently for lists vs vectors
;;    (conj [1 2] 3) => [1 2 3]
;;    (conj '(1 2) 3) => (3 1 2)

;; 3. Not handling empty list case

;; 4. Mutating in place (not possible in Clojure's immutable data)

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Reverse in groups of k
(defn reverse-k-group
  "Reverse nodes in k-group."
  [lst k]
  (if (< (count lst) k)
    lst
    (concat (reverse (take k lst))
            (reverse-k-group (drop k lst) k))))

(reverse-k-group [1 2 3 4 5] 2)
;; => (2 1 4 3 5)

(reverse-k-group [1 2 3 4 5] 3)
;; => (3 2 1 4 5)

;; Variation 2: Reverse between positions m and n
(defn reverse-between
  "Reverse list between positions m and n (1-indexed)."
  [lst m n]
  (let [v (vec lst)
        before (subvec v 0 (dec m))
        middle (subvec v (dec m) n)
        after (subvec v n)]
    (concat before (reverse middle) after)))

(reverse-between [1 2 3 4 5] 2 4)
;; => (1 4 3 2 5)

;; Variation 3: Check if list is palindrome
(defn palindrome-list?
  "Check if list is a palindrome."
  [lst]
  (= (seq lst) (reverse lst)))

(palindrome-list? [1 2 3 2 1])
;; => true

(palindrome-list? [1 2 3 4 5])
;; => false

;; Variation 4: Rotate list by k positions
(defn rotate-list
  "Rotate list to the right by k positions."
  [lst k]
  (if (empty? lst)
    lst
    (let [n (count lst)
          k (mod k n)]
      (concat (take-last k lst) (drop-last k lst)))))

(rotate-list [1 2 3 4 5] 2)
;; => (4 5 1 2 3)

;; Variation 5: Swap nodes in pairs
(defn swap-pairs
  "Swap every two adjacent nodes."
  [lst]
  (if (< (count lst) 2)
    lst
    (concat [(second lst) (first lst)]
            (swap-pairs (drop 2 lst)))))

(swap-pairs [1 2 3 4])
;; => (2 1 4 3)

(swap-pairs [1 2 3 4 5])
;; => (2 1 4 3 5)

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Reverse Alternate K Nodes
;; Reverse alternate groups of k nodes.
;; Example: [1 2 3 4 5 6 7 8] k=2 -> [2 1 3 4 6 5 7 8]

;; Exercise 2: Reverse Doubly Linked List
;; Implement a doubly linked list and reverse it.

;; Exercise 3: Reverse Only Odd Positions
;; Reverse elements at odd indices (1, 3, 5, ...).

;; Exercise 4: Check if Reversing a Sublist Makes Sorted
;; Determine if there's a contiguous sublist that when reversed makes
;; the entire list sorted.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing reverse-list...")

  ;; Basic tests
  (assert (= '(5 4 3 2 1) (reverse-list [1 2 3 4 5]))
          "Test 1: Basic reverse")
  (assert (= '(2 1) (reverse-list [1 2]))
          "Test 2: Two elements")
  (assert (= '() (seq (reverse-list [])))
          "Test 3: Empty list")

  ;; Edge cases
  (assert (= '(1) (reverse-list [1]))
          "Test 4: Single element")
  (assert (= '(1 1 1) (reverse-list [1 1 1]))
          "Test 5: All same")

  ;; Test other implementations
  (assert (= '(5 4 3 2 1) (reverse-list-reduce [1 2 3 4 5]))
          "Test 6: reduce version")
  (assert (= '(5 4 3 2 1) (reverse-list-conj [1 2 3 4 5]))
          "Test 7: conj version")
  (assert (= '(5 4 3 2 1) (reverse-list-tail [1 2 3 4 5]))
          "Test 8: tail-recursive version")
  (assert (= '(5 4 3 2 1) (reverse-list-loop [1 2 3 4 5]))
          "Test 9: loop version")
  (assert (= '(5 4 3 2 1) (reverse-list-into [1 2 3 4 5]))
          "Test 10: into version")

  ;; Test linked list version
  (assert (= [5 4 3 2 1]
             (linked->list (reverse-linked-list (list->linked [1 2 3 4 5]))))
          "Test 11: explicit linked list")

  ;; Variations
  (assert (= '(2 1 4 3 5) (reverse-k-group [1 2 3 4 5] 2))
          "Test 12: k-group reverse")
  (assert (= '(1 4 3 2 5) (reverse-between [1 2 3 4 5] 2 4))
          "Test 13: reverse between")
  (assert (= true (palindrome-list? [1 2 3 2 1]))
          "Test 14: palindrome check")
  (assert (= '(4 5 1 2 3) (rotate-list [1 2 3 4 5] 2))
          "Test 15: rotate list")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. Clojure has a built-in `reverse` function - use it!
;;
;; 2. `reduce` with `cons` is an elegant way to reverse
;;    - cons adds to front, naturally reversing order
;;
;; 3. `into '()` is another idiomatic way to reverse
;;    - into uses conj, which adds to front for lists
;;
;; 4. Avoid naive recursion with concat - it's O(n²)
;;
;; 5. For explicit linked lists, use loop/recur with prev/curr pointers
;;
;; 6. Lists in Clojure ARE linked lists with O(1) first/rest/cons
;;
;; 7. conj behaves differently for lists (front) vs vectors (end)
;;
;; NEXT: easy_11_fizz_buzz.clj
