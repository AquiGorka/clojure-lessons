;; ============================================================================
;; EASY 07: Climbing Stairs
;; ============================================================================
;; Source: LeetCode #70 - Climbing Stairs
;; Difficulty: Easy
;; Topics: Dynamic Programming, Memoization, Math
;;
;; A classic introduction to dynamic programming. This problem demonstrates
;; how overlapping subproblems lead to the Fibonacci pattern.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; You are climbing a staircase. It takes `n` steps to reach the top.
;;
;; Each time you can either climb 1 or 2 steps. In how many distinct ways
;; can you climb to the top?

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: n = 2
;;   Output: 2
;;   Explanation: There are two ways to climb to the top:
;;     1. 1 step + 1 step
;;     2. 2 steps
;;
;; Example 2:
;;   Input: n = 3
;;   Output: 3
;;   Explanation: There are three ways to climb to the top:
;;     1. 1 step + 1 step + 1 step
;;     2. 1 step + 2 steps
;;     3. 2 steps + 1 step
;;
;; Example 3:
;;   Input: n = 4
;;   Output: 5
;;   Explanation:
;;     1. 1+1+1+1
;;     2. 1+1+2
;;     3. 1+2+1
;;     4. 2+1+1
;;     5. 2+2

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= n <= 45

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; Key insight: To reach step n, you must have come from either:
;;   - Step n-1 (by taking 1 step), OR
;;   - Step n-2 (by taking 2 steps)
;;
;; So: ways(n) = ways(n-1) + ways(n-2)
;;
;; This is the Fibonacci sequence!
;;
;; Base cases:
;;   ways(1) = 1 (only one way: take 1 step)
;;   ways(2) = 2 (two ways: 1+1 or 2)
;;
;; Building up:
;;   ways(3) = ways(2) + ways(1) = 2 + 1 = 3
;;   ways(4) = ways(3) + ways(2) = 3 + 2 = 5
;;   ways(5) = ways(4) + ways(3) = 5 + 3 = 8
;;   ...

;; ============================================================================
;; SOLUTION 1: Naive Recursion (Exponential - Don't Use!)
;; ============================================================================
;; Time Complexity: O(2^n) - exponential!
;; Space Complexity: O(n) - call stack depth

(defn climb-stairs-naive
  "Naive recursive solution - exponential time complexity."
  [n]
  (cond
    (<= n 1) 1
    (= n 2) 2
    :else (+ (climb-stairs-naive (- n 1))
             (climb-stairs-naive (- n 2)))))

(climb-stairs-naive 5)
;; => 8 (but very slow for large n)

;; This is slow because we recalculate the same subproblems many times:
;;   climb(5) calls climb(4) and climb(3)
;;   climb(4) calls climb(3) and climb(2)  <- climb(3) calculated twice!
;;   ...

;; ============================================================================
;; SOLUTION 2: Using iterate (Optimal - Clojure Idiomatic)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(1)

(defn climb-stairs
  "Calculate ways to climb n stairs using iterate."
  [n]
  (if (<= n 2)
    n
    (let [;; Generate Fibonacci-like sequence: [a, b] -> [b, a+b]
          step (fn [[a b]] [b (+ a b)])
          ;; Start with [1, 2] representing ways(1) and ways(2)
          ;; Take n-1 steps to get to ways(n)
          [_ result] (nth (iterate step [1 2]) (- n 2))]
      result)))

(climb-stairs 2)
;; => 2

(climb-stairs 3)
;; => 3

(climb-stairs 4)
;; => 5

(climb-stairs 5)
;; => 8

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; climb-stairs(5):
;;   Start: [1, 2] (ways(1), ways(2))
;;
;;   iterate step [1, 2] produces:
;;     [1, 2]       <- step 0 (initial)
;;     [2, 3]       <- step 1: [2, 1+2] = ways(2), ways(3)
;;     [3, 5]       <- step 2: [3, 2+3] = ways(3), ways(4)
;;     [5, 8]       <- step 3: [5, 3+5] = ways(4), ways(5)
;;
;;   nth ... (5-2) = nth ... 3 = [5, 8]
;;   Second element = 8
;;
;; Result: 8

;; ============================================================================
;; SOLUTION 3: Using reduce
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(1)

(defn climb-stairs-reduce
  "Calculate ways using reduce."
  [n]
  (if (<= n 2)
    n
    (second
     (reduce (fn [[a b] _] [b (+ a b)])
             [1 2]
             (range 2 n)))))

(climb-stairs-reduce 5)
;; => 8

;; ============================================================================
;; SOLUTION 4: Using loop/recur
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(1)

(defn climb-stairs-loop
  "Calculate ways using explicit loop."
  [n]
  (if (<= n 2)
    n
    (loop [i 2
           prev 1    ; ways(i-1)
           curr 2]   ; ways(i)
      (if (= i n)
        curr
        (recur (inc i) curr (+ prev curr))))))

(climb-stairs-loop 5)
;; => 8

;; ============================================================================
;; SOLUTION 5: Memoized Recursion
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n)

(def climb-stairs-memo
  "Memoized recursive solution."
  (memoize
   (fn [n]
     (cond
       (<= n 1) 1
       (= n 2) 2
       :else (+ (climb-stairs-memo (- n 1))
                (climb-stairs-memo (- n 2)))))))

(climb-stairs-memo 5)
;; => 8

;; ============================================================================
;; SOLUTION 6: Closed-form Formula (Binet's Formula)
;; ============================================================================
;; Time Complexity: O(1)
;; Space Complexity: O(1)
;;
;; The Fibonacci sequence has a closed-form solution using the golden ratio.
;; F(n) = (φ^n - ψ^n) / √5
;; where φ = (1 + √5) / 2 and ψ = (1 - √5) / 2

(defn climb-stairs-math
  "Calculate using Binet's formula (closed-form)."
  [n]
  (let [sqrt5 (Math/sqrt 5)
        phi (/ (+ 1 sqrt5) 2)
        psi (/ (- 1 sqrt5) 2)
        ;; We want F(n+1) since our sequence is shifted
        fib-n+1 (/ (- (Math/pow phi (inc n))
                      (Math/pow psi (inc n)))
                   sqrt5)]
    (Math/round fib-n+1)))

(climb-stairs-math 5)
;; => 8

;; Note: This can have precision issues for very large n

;; ============================================================================
;; SOLUTION 7: Generate all Fibonacci numbers lazily
;; ============================================================================

(def fibs
  "Lazy infinite sequence of Fibonacci numbers."
  ((fn fib [a b]
     (lazy-seq (cons a (fib b (+ a b)))))
   1 1))

(defn climb-stairs-lazy
  "Use pre-generated lazy Fibonacci sequence."
  [n]
  (nth fibs n))

(climb-stairs-lazy 5)
;; => 8

(take 10 fibs)
;; => (1 1 2 3 5 8 13 21 34 55)

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `iterate` generates an infinite lazy sequence
(take 5 (iterate inc 0))
;; => (0 1 2 3 4)

(take 5 (iterate (fn [[a b]] [b (+ a b)]) [0 1]))
;; => ([0 1] [1 1] [1 2] [2 3] [3 5])

;; 2. `memoize` wraps a function to cache results
(def slow-fn (memoize (fn [x] (Thread/sleep 1000) (* x x))))
;; First call is slow, subsequent calls with same arg are instant

;; 3. Lazy sequences for infinite series
(def naturals (iterate inc 0))
(take 5 naturals)
;; => (0 1 2 3 4)

;; 4. Destructuring in function parameters
(fn [[a b]] [b (+ a b)])  ; Unpacks a vector into a and b

;; 5. `nth` for random access into sequences
(nth [10 20 30] 1)
;; => 20

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(climb-stairs 1)
;; => 1

(climb-stairs 2)
;; => 2

(climb-stairs 45)
;; => 1836311903 (maximum per constraints)

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Naive Recursion:
;;   Time:  O(2^n) - each call branches into two calls
;;   Space: O(n)   - maximum call stack depth
;;
;; Iterate/Reduce/Loop:
;;   Time:  O(n) - linear iterations
;;   Space: O(1) - only track two values
;;
;; Memoization:
;;   Time:  O(n) - each subproblem computed once
;;   Space: O(n) - cache stores n results
;;
;; Closed-form:
;;   Time:  O(1) - direct calculation
;;   Space: O(1) - constant space
;;   Note: Potential floating-point precision issues
;;
;; The iterate/reduce/loop solutions are optimal for this problem.

;; ============================================================================
;; DYNAMIC PROGRAMMING PATTERNS
;; ============================================================================
;;
;; This problem demonstrates key DP concepts:
;;
;; 1. OVERLAPPING SUBPROBLEMS
;;    climb(5) needs climb(4) and climb(3)
;;    climb(4) needs climb(3) and climb(2)
;;    climb(3) is needed multiple times!
;;
;; 2. OPTIMAL SUBSTRUCTURE
;;    The solution to climb(n) is built from solutions to climb(n-1) and climb(n-2)
;;
;; 3. TWO APPROACHES:
;;    a. Top-down with memoization (recursive + caching)
;;    b. Bottom-up (iterative, building from base cases)
;;
;; The bottom-up approach (iterate/reduce) is preferred in Clojure because:
;; - More idiomatic functional style
;; - No risk of stack overflow
;; - O(1) space instead of O(n)

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Off-by-one errors in base cases
;;    Make sure to handle n=1 and n=2 correctly

;; 2. Using naive recursion without memoization
;;    This leads to exponential time complexity

;; 3. Integer overflow for large n
;;    Clojure uses arbitrary precision integers by default, so this isn't
;;    usually a problem, but watch out in other languages

;; 4. Starting iterate with wrong initial values
;;    [1, 2] corresponds to ways(1) and ways(2)

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Climb with 1, 2, or 3 steps
(defn climb-stairs-3
  "Ways to climb with 1, 2, or 3 steps."
  [n]
  (cond
    (<= n 1) 1
    (= n 2) 2
    (= n 3) 4
    :else
    (let [step (fn [[a b c]] [b c (+ a b c)])]
      (nth (nth (iterate step [1 2 4]) (- n 3)) 2))))

(climb-stairs-3 4)
;; => 7 (1111, 112, 121, 211, 13, 31, 22)

;; Variation 2: Climb with any steps from a given set
(defn climb-stairs-k
  "Ways to climb with steps from the given set."
  [n steps]
  (let [dp (vec (concat [1] (repeat n 0)))]
    (reduce (fn [dp i]
              (reduce (fn [dp step]
                        (if (>= i step)
                          (update dp i + (dp (- i step)))
                          dp))
                      dp
                      steps))
            dp
            (range 1 (inc n)))))

(last (climb-stairs-k 5 [1 2]))
;; => 8

(last (climb-stairs-k 5 [1 2 3]))
;; => 13

;; Variation 3: Minimum cost climbing stairs
(defn min-cost-climbing
  "Minimum cost to climb, where each step has a cost."
  [costs]
  (let [n (count costs)]
    (if (<= n 2)
      (apply min costs)
      (loop [i 2
             prev2 (costs 0)
             prev1 (costs 1)]
        (if (= i n)
          (min prev1 prev2)
          (recur (inc i)
                 prev1
                 (+ (costs i) (min prev1 prev2))))))))

(min-cost-climbing [10 15 20])
;; => 15 (climb from step 1 directly to top)

(min-cost-climbing [1 100 1 1 1 100 1 1 100 1])
;; => 6

;; Variation 4: Count paths in a grid (related problem)
(defn unique-paths
  "Number of unique paths in an m x n grid (can only go right or down)."
  [m n]
  (let [step (fn [row]
               (reductions + row))]
    (last (nth (iterate step (repeat n 1)) (dec m)))))

(unique-paths 3 7)
;; => 28

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Decode Ways
;; A message of digits can be decoded where 'A'=1, 'B'=2, ..., 'Z'=26.
;; Given a string of digits, count the number of ways to decode it.
;; Example: "12" -> 2 ("AB" or "L")

;; Exercise 2: House Robber
;; Given an array of house values, find max value you can rob without
;; robbing adjacent houses. Similar DP structure!

;; Exercise 3: Jump Game
;; Given an array where nums[i] is the max jump from position i,
;; determine if you can reach the last index.

;; Exercise 4: Tribonacci
;; T(n) = T(n-1) + T(n-2) + T(n-3), with T(0)=0, T(1)=T(2)=1

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing climb-stairs...")

  ;; Basic tests
  (assert (= 1 (climb-stairs 1)) "Test 1: n=1")
  (assert (= 2 (climb-stairs 2)) "Test 2: n=2")
  (assert (= 3 (climb-stairs 3)) "Test 3: n=3")
  (assert (= 5 (climb-stairs 4)) "Test 4: n=4")
  (assert (= 8 (climb-stairs 5)) "Test 5: n=5")

  ;; Larger values
  (assert (= 13 (climb-stairs 6)) "Test 6: n=6")
  (assert (= 21 (climb-stairs 7)) "Test 7: n=7")
  (assert (= 89 (climb-stairs 10)) "Test 8: n=10")
  (assert (= 1836311903 (climb-stairs 45)) "Test 9: n=45 (max)")

  ;; Test other implementations
  (assert (= 8 (climb-stairs-reduce 5)) "Test 10: reduce version")
  (assert (= 8 (climb-stairs-loop 5)) "Test 11: loop version")
  (assert (= 8 (climb-stairs-memo 5)) "Test 12: memo version")
  (assert (= 8 (climb-stairs-math 5)) "Test 13: math version")
  (assert (= 8 (climb-stairs-lazy 5)) "Test 14: lazy version")

  ;; Variations
  (assert (= 7 (climb-stairs-3 4)) "Test 15: 3-step variant")
  (assert (= 15 (min-cost-climbing [10 15 20])) "Test 16: min cost")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. Recognize the Fibonacci pattern in DP problems
;;    - ways(n) depends on ways(n-1) and ways(n-2)
;;
;; 2. `iterate` is perfect for generating sequences from recurrences
;;    - (iterate f x) produces x, f(x), f(f(x)), ...
;;
;; 3. Bottom-up DP (iterate/reduce) is more idiomatic in Clojure than memoization
;;
;; 4. Track only what you need - O(1) space by keeping just two previous values
;;
;; 5. `memoize` is available for quick top-down DP when needed
;;
;; 6. This pattern extends to many DP problems:
;;    - House Robber
;;    - Decode Ways
;;    - Minimum Cost Climbing
;;
;; NEXT: easy_08_maximum_subarray.clj
