;; ============================================================================
;; EASY 02: Valid Parentheses
;; ============================================================================
;; Source: LeetCode #20 - Valid Parentheses
;; Difficulty: Easy
;; Topics: String, Stack
;;
;; A classic problem that introduces the stack data structure pattern.
;; In Clojure, we'll see how to elegantly simulate a stack using reduce.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given a string `s` containing just the characters '(', ')', '{', '}',
;; '[' and ']', determine if the input string is valid.
;;
;; An input string is valid if:
;; 1. Open brackets must be closed by the same type of brackets.
;; 2. Open brackets must be closed in the correct order.
;; 3. Every close bracket has a corresponding open bracket of the same type.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: s = "()"
;;   Output: true
;;
;; Example 2:
;;   Input: s = "()[]{}"
;;   Output: true
;;
;; Example 3:
;;   Input: s = "(]"
;;   Output: false
;;
;; Example 4:
;;   Input: s = "([)]"
;;   Output: false
;;   Explanation: The brackets are not closed in the correct order.
;;
;; Example 5:
;;   Input: s = "{[]}"
;;   Output: true

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= s.length <= 10^4
;; - s consists of parentheses only '()[]{}'

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; Think of it like nesting: every time we see an opening bracket, we need
;; to remember it. When we see a closing bracket, it MUST match the most
;; recent unmatched opening bracket.
;;
;; This is exactly what a stack does:
;; - Push opening brackets onto the stack
;; - Pop and check when we see closing brackets
;; - Valid if stack is empty at the end
;;
;; Visual trace for "{[]}":
;;   char='{' -> stack: ['{']
;;   char='[' -> stack: ['{', '[']
;;   char=']' -> pop '[', matches! -> stack: ['{']
;;   char='}' -> pop '{', matches! -> stack: []
;;   End: stack empty -> VALID

;; ============================================================================
;; SOLUTION 1: Using reduce with a list as stack
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n)

(def matching-bracket
  "Maps closing brackets to their opening counterparts."
  {\) \(
   \] \[
   \} \{})

(def opening-bracket?
  "Set of opening brackets for O(1) lookup."
  #{\( \[ \{})

(defn valid-parentheses?
  "Check if the string has valid matching parentheses."
  [s]
  (let [result
        (reduce
         (fn [stack char]
           (cond
             ;; Opening bracket: push onto stack
             (opening-bracket? char)
             (conj stack char)

             ;; Closing bracket: check if it matches top of stack
             :else
             (if (= (peek stack) (matching-bracket char))
               (pop stack)  ; Match! Remove from stack
               (reduced :invalid))))  ; No match, invalid

         '()  ; Use a list as our stack (conj adds to front)
         s)]

    ;; Valid only if we didn't get :invalid AND stack is empty
    (and (not= result :invalid)
         (empty? result))))

(valid-parentheses? "()")
;; => true

(valid-parentheses? "()[]{}")
;; => true

(valid-parentheses? "(]")
;; => false

(valid-parentheses? "([)]")
;; => false

(valid-parentheses? "{[]}")
;; => true

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; Let's trace through "([)]" to see why it's invalid:
;;
;; Initial: stack = ()
;;
;; char='(' -> opening bracket
;;   stack = '(' conj onto () = '(\()
;;
;; char='[' -> opening bracket
;;   stack = '[' conj onto '(\() = '(\[ \()
;;
;; char=')' -> closing bracket
;;   peek stack = '[', matching-bracket[')'] = '('
;;   '[' != '(' -> MISMATCH! Return :invalid
;;
;; Result: :invalid -> false
;;
;; The '[' should have been closed before ')'

;; ============================================================================
;; SOLUTION 2: Using loop/recur for explicit control
;; ============================================================================

(defn valid-parentheses-loop?
  "Using explicit loop/recur."
  [s]
  (loop [remaining (seq s)
         stack '()]
    (if (empty? remaining)
      ;; End of string: valid if stack is empty
      (empty? stack)

      (let [char (first remaining)]
        (cond
          ;; Opening bracket
          (opening-bracket? char)
          (recur (rest remaining) (conj stack char))

          ;; Closing bracket matches top of stack
          (= (peek stack) (matching-bracket char))
          (recur (rest remaining) (pop stack))

          ;; Mismatch or empty stack with closing bracket
          :else
          false)))))

(valid-parentheses-loop? "{[]}")
;; => true

;; ============================================================================
;; SOLUTION 3: Elegant recursive approach
;; ============================================================================

(defn valid-parentheses-recursive?
  "Recursive solution - more functional style."
  ([s] (valid-parentheses-recursive? (seq s) '()))
  ([chars stack]
   (if (empty? chars)
     (empty? stack)
     (let [c (first chars)
           remaining (rest chars)]
       (cond
         (opening-bracket? c)
         (recur remaining (conj stack c))

         (= (peek stack) (matching-bracket c))
         (recur remaining (pop stack))

         :else
         false)))))

;; ============================================================================
;; SOLUTION 4: Using reduce with vectors (alternative stack)
;; ============================================================================
;; Vectors can also be used as stacks with conj/peek/pop
;; conj adds to END, peek/pop work on END

(defn valid-parentheses-vec?
  "Using a vector as the stack."
  [s]
  (let [result
        (reduce
         (fn [stack char]
           (cond
             (opening-bracket? char)
             (conj stack char)

             (and (seq stack)
                  (= (peek stack) (matching-bracket char)))
             (pop stack)

             :else
             (reduced :invalid)))
         []
         s)]
    (and (vector? result) (empty? result))))

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. Lists vs Vectors as Stacks
;;
;; Lists: conj adds to FRONT, peek/pop work on FRONT
(conj '(2 3) 1)  ;; => (1 2 3)
(peek '(1 2 3))  ;; => 1
(pop '(1 2 3))   ;; => (2 3)

;; Vectors: conj adds to END, peek/pop work on END
(conj [1 2] 3)   ;; => [1 2 3]
(peek [1 2 3])   ;; => 3
(pop [1 2 3])    ;; => [1 2]

;; Both work as stacks! Lists are traditional, vectors are often faster.

;; 2. Sets as predicates
;; A set can be used as a function that tests membership
(#{\a \b \c} \b)  ;; => \b (truthy)
(#{\a \b \c} \x)  ;; => nil (falsy)

;; This is why (opening-bracket? char) works - it's a set lookup!

;; 3. `reduced` for early termination
;; In reduce, calling (reduced value) immediately stops and returns value.
;; This is how we "break" out of the loop when we find an invalid sequence.

;; 4. Characters in Clojure
;; Clojure has a character literal syntax: \a, \b, \(, etc.
;; A string is a sequence of characters:
(seq "abc")  ;; => (\a \b \c)

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(valid-parentheses? "")
;; => true (empty string is valid)

(valid-parentheses? "(")
;; => false (unclosed opening bracket)

(valid-parentheses? ")")
;; => false (unmatched closing bracket)

(valid-parentheses? "((()))")
;; => true (deeply nested)

(valid-parentheses? "((())")
;; => false (missing closing bracket)

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Time Complexity: O(n)
;;   - We iterate through each character exactly once
;;   - Each stack operation (conj, peek, pop) is O(1) for both lists and vectors
;;
;; Space Complexity: O(n)
;;   - In the worst case (all opening brackets), the stack holds n/2 elements
;;   - Example: "((((((((" would have 8 elements on stack
;;
;; Can we do better?
;;   - Time: No, we must examine each character
;;   - Space: No, we need to remember the opening brackets

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Forgetting to check for empty stack before pop
;;    ")" with empty stack should return false, not throw an error

;; 2. Not checking if stack is empty at the end
;;    "(((" has no mismatches but is still invalid

;; 3. Confusing list and vector behavior with conj
;;    Lists add to front, vectors add to end

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Return the INDEX of the first invalid character
(defn first-invalid-index
  "Returns the index of first invalid bracket, or nil if valid."
  [s]
  (let [result
        (reduce
         (fn [[stack idx] char]
           (cond
             (opening-bracket? char)
             [(conj stack [char idx]) (inc idx)]

             (and (seq stack)
                  (= (first (peek stack)) (matching-bracket char)))
             [(pop stack) (inc idx)]

             :else
             (reduced idx)))  ; Return the index
         [[] 0]
         s)]

    (cond
      (number? result) result  ; Found invalid closing bracket
      (seq (first result)) (second (peek (first result)))  ; Unclosed opening bracket
      :else nil)))  ; Valid

(first-invalid-index "()[]{}")  ;; => nil (valid)
(first-invalid-index "([)]")    ;; => 2 (the ')' at index 2)
(first-invalid-index "(((")     ;; => 2 (the last unclosed '(')

;; Variation 2: Count minimum brackets to remove for validity
(defn min-removals-for-validity
  "Count minimum brackets to remove to make string valid."
  [s]
  (let [[open-count close-count]
        (reduce
         (fn [[opens unmatched-closes] char]
           (cond
             (opening-bracket? char)
             [(inc opens) unmatched-closes]

             (pos? opens)  ; Have opening bracket to match
             [(dec opens) unmatched-closes]

             :else  ; Unmatched closing bracket
             [opens (inc unmatched-closes)]))
         [0 0]
         s)]
    (+ open-count close-count)))

(min-removals-for-validity "((()")    ;; => 2 (remove two '(')
(min-removals-for-validity "())")     ;; => 1 (remove one ')')
(min-removals-for-validity "()()")    ;; => 0 (already valid)

;; Variation 3: Handle only one type of bracket (simplest case)
(defn valid-single-type?
  "Check validity with just () - simpler, no stack needed!"
  [s]
  (let [result
        (reduce
         (fn [count char]
           (let [new-count (case char
                            \( (inc count)
                            \) (dec count)
                            count)]
             (if (neg? new-count)
               (reduced :invalid)
               new-count)))
         0
         s)]
    (= result 0)))

(valid-single-type? "(())")  ;; => true
(valid-single-type? ")(")    ;; => false

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Longest Valid Parentheses Substring
;; Given a string containing just '(' and ')', find the length of the
;; longest valid parentheses substring.
;; Example: "(()" -> 2, ")()())" -> 4
;; (This is actually a Hard problem!)

;; Exercise 2: Generate Parentheses
;; Given n pairs of parentheses, write a function to generate all
;; combinations of well-formed parentheses.
;; Example: n=3 -> ["((()))", "(()())", "(())()", "()(())", "()()()"]

;; Exercise 3: Remove Invalid Parentheses
;; Remove the minimum number of invalid parentheses to make the string valid.
;; Return all possible results.
;; Example: "()())()" -> ["(())()", "()()()"]

;; Exercise 4: Score of Parentheses
;; () has score 1
;; AB has score A + B
;; (A) has score 2 * A
;; Example: "(())" -> 2, "()()" -> 2, "(()(()))" -> 6

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing valid-parentheses?...")

  ;; Basic valid cases
  (assert (= true (valid-parentheses? "()")) "Test 1: ()")
  (assert (= true (valid-parentheses? "()[]{}")) "Test 2: ()[]{}")
  (assert (= true (valid-parentheses? "{[]}")) "Test 3: {[]}")
  (assert (= true (valid-parentheses? "")) "Test 4: empty string")

  ;; Invalid cases
  (assert (= false (valid-parentheses? "(]")) "Test 5: (]")
  (assert (= false (valid-parentheses? "([)]")) "Test 6: ([)]")
  (assert (= false (valid-parentheses? "(")) "Test 7: single (")
  (assert (= false (valid-parentheses? ")")) "Test 8: single )")
  (assert (= false (valid-parentheses? "((()")) "Test 9: unbalanced")

  ;; Complex valid cases
  (assert (= true (valid-parentheses? "(((())))")) "Test 10: deep nesting")
  (assert (= true (valid-parentheses? "(){}[](){}[]")) "Test 11: alternating")
  (assert (= true (valid-parentheses? "{[()]}")) "Test 12: mixed nesting")

  ;; Complex invalid cases
  (assert (= false (valid-parentheses? "((((((")) "Test 13: all opening")
  (assert (= false (valid-parentheses? "))))))")) "Test 14: all closing")
  (assert (= false (valid-parentheses? "({)}")) "Test 15: interleaved wrong")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. The stack pattern is perfect for matching nested structures
;;
;; 2. In Clojure, both lists and vectors can be used as stacks
;;    - Lists: conj/peek/pop work on FRONT
;;    - Vectors: conj/peek/pop work on END
;;
;; 3. Maps are great for defining relationships (closing -> opening brackets)
;;
;; 4. Sets can be used directly as predicates for membership testing
;;
;; 5. `reduce` with `reduced` elegantly handles early termination
;;
;; 6. Always consider edge cases: empty input, unbalanced brackets
;;
;; NEXT: easy_03_merge_sorted_lists.clj
