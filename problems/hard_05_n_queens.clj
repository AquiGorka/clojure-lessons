;; ============================================================================
;; HARD 05: N-Queens
;; ============================================================================
;; Source: LeetCode #51 - N-Queens
;; Difficulty: Hard
;; Topics: Array, Backtracking
;;
;; A classic backtracking problem that demonstrates constraint satisfaction
;; and recursive search. The N-Queens puzzle is a foundational computer
;; science problem with elegant solutions.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; The n-queens puzzle is the problem of placing n queens on an n x n
;; chessboard such that no two queens attack each other.
;;
;; Given an integer n, return all distinct solutions to the n-queens puzzle.
;; You may return the answer in any order.
;;
;; Each solution contains a distinct board configuration of the n-queens'
;; placement, where 'Q' and '.' both indicate a queen and an empty space,
;; respectively.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: n = 4
;;   Output: [[".Q..",   ["..Q.",
;;             "...Q",    "Q...",
;;             "Q...",    "...Q",
;;             "..Q."],   ".Q.."]]
;;
;; Example 2:
;;   Input: n = 1
;;   Output: [["Q"]]

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= n <= 9

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; Queens attack along:
;; 1. Rows (horizontal)
;; 2. Columns (vertical)
;; 3. Diagonals (both directions)
;;
;; Key insight: Since each row must have exactly one queen, we can place
;; queens row by row, tracking which columns and diagonals are occupied.
;;
;; Diagonal tracking:
;; - Main diagonal (↘): row - col is constant for each diagonal
;; - Anti-diagonal (↙): row + col is constant for each diagonal
;;
;; For a 4x4 board:
;;   row-col values:        row+col values:
;;   0  -1  -2  -3          0   1   2   3
;;   1   0  -1  -2          1   2   3   4
;;   2   1   0  -1          2   3   4   5
;;   3   2   1   0          3   4   5   6

;; ============================================================================
;; SOLUTION 1: Backtracking with Sets (Idiomatic Clojure)
;; ============================================================================
;; Time Complexity: O(n!)
;; Space Complexity: O(n²) for storing solutions

(defn solve-n-queens
  "Find all solutions to the N-Queens problem."
  [n]
  (letfn [(safe? [row col cols diag1 diag2]
            ;; Check if placing a queen at (row, col) is safe
            (and (not (cols col))           ; Column not occupied
                 (not (diag1 (- row col)))   ; Main diagonal not occupied
                 (not (diag2 (+ row col))))) ; Anti-diagonal not occupied

          (build-board [queens]
            ;; Convert list of column positions to board representation
            (mapv (fn [col]
                    (apply str (map #(if (= % col) \Q \.) (range n))))
                  queens))

          (solve [row cols diag1 diag2 queens]
            ;; Recursive backtracking
            (if (= row n)
              ;; Found a solution - convert to board format
              [(build-board (reverse queens))]
              ;; Try each column in current row
              (mapcat (fn [col]
                        (when (safe? row col cols diag1 diag2)
                          (solve (inc row)
                                 (conj cols col)
                                 (conj diag1 (- row col))
                                 (conj diag2 (+ row col))
                                 (cons col queens))))
                      (range n))))]

    (solve 0 #{} #{} #{} '())))

(solve-n-queens 4)
;; => [[".Q.." "...Q" "Q..." "..Q."]
;;     ["..Q." "Q..." "...Q" ".Q.."]]

(solve-n-queens 1)
;; => [["Q"]]

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; For n = 4:
;;
;; Row 0: Try col 0
;;   Place queen at (0,0)
;;   cols=#{0}, diag1=#{0}, diag2=#{0}
;;
;;   Row 1: Try col 0 - blocked (col)
;;          Try col 1 - blocked (diag2: 0+0=0, 1+1=2 ✓, but diag1: 0-0=0, 1-1=0 ✗)
;;          Try col 2 - safe
;;          Place queen at (1,2)
;;          cols=#{0,2}, diag1=#{0,-1}, diag2=#{0,3}
;;
;;          Row 2: Try all - all blocked!
;;          Backtrack...
;;
;;          Try col 3 - safe
;;          Place queen at (1,3)
;;          ...continue recursively...
;;
;; Eventually finds both solutions.

;; ============================================================================
;; SOLUTION 2: Using loop/recur (More Explicit)
;; ============================================================================

(defn solve-n-queens-loop
  "Solve N-Queens using explicit loop with stack-based backtracking."
  [n]
  (let [build-board (fn [queens]
                      (mapv (fn [col]
                              (apply str (map #(if (= % col) \Q \.) (range n))))
                            queens))]
    (loop [stack [[0 #{} #{} #{} []]]  ; [row cols diag1 diag2 queens]
           solutions []]
      (if (empty? stack)
        solutions
        (let [[row cols diag1 diag2 queens] (peek stack)
              stack (pop stack)]
          (if (= row n)
            ;; Found solution
            (recur stack (conj solutions (build-board queens)))
            ;; Try each valid column
            (let [valid-cols (filter (fn [col]
                                       (and (not (cols col))
                                            (not (diag1 (- row col)))
                                            (not (diag2 (+ row col)))))
                                     (range n))
                  new-states (map (fn [col]
                                    [(inc row)
                                     (conj cols col)
                                     (conj diag1 (- row col))
                                     (conj diag2 (+ row col))
                                     (conj queens col)])
                                  valid-cols)]
              (recur (into stack new-states) solutions))))))))

(solve-n-queens-loop 4)
;; => [[".Q.." "...Q" "Q..." "..Q."] ["..Q." "Q..." "...Q" ".Q.."]]

;; ============================================================================
;; SOLUTION 3: Using for comprehension (Elegant but potentially slower)
;; ============================================================================

(defn solve-n-queens-for
  "Solve N-Queens using nested for comprehension."
  [n]
  (let [safe? (fn [queens col]
                (let [row (count queens)]
                  (every? (fn [[r c]]
                            (and (not= c col)
                                 (not= (Math/abs (- row r))
                                       (Math/abs (- col c)))))
                          (map-indexed vector queens))))

        build-board (fn [queens]
                      (mapv (fn [col]
                              (apply str (map #(if (= % col) \Q \.) (range n))))
                            queens))

        solve (fn solve [queens]
                (if (= (count queens) n)
                  [queens]
                  (for [col (range n)
                        :when (safe? queens col)
                        solution (solve (conj queens col))]
                    solution)))]

    (mapv build-board (solve []))))

(solve-n-queens-for 4)
;; => [[".Q.." "...Q" "Q..." "..Q."] ["..Q." "Q..." "...Q" ".Q.."]]

;; ============================================================================
;; SOLUTION 4: Return count only (N-Queens II - LeetCode #52)
;; ============================================================================

(defn total-n-queens
  "Count the number of distinct N-Queens solutions."
  [n]
  (letfn [(solve [row cols diag1 diag2]
            (if (= row n)
              1
              (reduce + 0
                      (for [col (range n)
                            :when (and (not (cols col))
                                       (not (diag1 (- row col)))
                                       (not (diag2 (+ row col))))]
                        (solve (inc row)
                               (conj cols col)
                               (conj diag1 (- row col))
                               (conj diag2 (+ row col)))))))]
    (solve 0 #{} #{} #{})))

(total-n-queens 4)
;; => 2

(total-n-queens 8)
;; => 92

;; ============================================================================
;; SOLUTION 5: Optimized with bit manipulation (for performance)
;; ============================================================================

(defn solve-n-queens-bits
  "Solve N-Queens using bit manipulation for faster constraint checking."
  [n]
  (let [all-cols (dec (bit-shift-left 1 n))  ; n ones: 1111 for n=4

        build-board (fn [queens]
                      (mapv (fn [col]
                              (apply str (map #(if (= % col) \Q \.) (range n))))
                            queens))

        bit-pos (fn [bits]
                  ;; Find position of lowest set bit
                  (loop [pos 0 b bits]
                    (if (odd? b) pos (recur (inc pos) (bit-shift-right b 1)))))

        solve (fn solve [cols diag1 diag2 queens]
                (if (= cols all-cols)
                  [(reverse queens)]
                  (let [available (bit-and-not all-cols (bit-or cols diag1 diag2))]
                    (loop [avail available
                           results []]
                      (if (zero? avail)
                        results
                        (let [col-bit (bit-and avail (- avail))  ; Lowest set bit
                              col (bit-pos col-bit)
                              new-results (solve (bit-or cols col-bit)
                                                 (bit-shift-left (bit-or diag1 col-bit) 1)
                                                 (bit-shift-right (bit-or diag2 col-bit) 1)
                                                 (cons col queens))]
                          (recur (bit-xor avail col-bit)
                                 (into results new-results))))))))]

    (mapv build-board (solve 0 0 0 '()))))

(solve-n-queens-bits 4)
;; Works but board output may be in different order

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. Sets for O(1) constraint checking
(def cols #{0 2})
(cols 0)        ;; => 0 (truthy - column occupied)
(cols 1)        ;; => nil (falsy - column free)

;; 2. `mapcat` for flat-mapping results from recursive calls
(mapcat (fn [x] [x (* x x)]) [1 2 3])
;; => (1 1 2 4 3 9)

;; 3. `cons` for building lists (O(1) prepend)
(cons 0 '(1 2 3))
;; => (0 1 2 3)

;; 4. `letfn` for mutually recursive local functions
(letfn [(even? [n] (or (zero? n) (odd? (dec n))))
        (odd? [n] (and (pos? n) (even? (dec n))))]
  (even? 4))
;; => true

;; 5. `for` comprehension with `:when` guard
(for [x (range 5) :when (even? x)] x)
;; => (0 2 4)

;; 6. Destructuring in loop bindings
(loop [[x & xs] [1 2 3]]
  (when x
    (println x)
    (recur xs)))

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(solve-n-queens 1)
;; => [["Q"]]

(solve-n-queens 2)
;; => [] (no solution exists)

(solve-n-queens 3)
;; => [] (no solution exists)

(total-n-queens 1)
;; => 1

(total-n-queens 2)
;; => 0

(total-n-queens 3)
;; => 0

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Time Complexity: O(n!)
;;   - In the worst case, we explore n * (n-1) * (n-2) * ... * 1 = n! positions
;;   - Actually better due to pruning, but upper bound is O(n!)
;;
;; Space Complexity: O(n)
;;   - Recursion depth is n (one level per row)
;;   - Sets store at most n elements each
;;   - Solution storage is O(S * n²) where S is number of solutions
;;
;; Number of solutions for various n:
;;   n=1: 1,  n=2: 0,  n=3: 0,   n=4: 2
;;   n=5: 10, n=6: 4,  n=7: 40,  n=8: 92
;;   n=9: 352, n=10: 724

;; ============================================================================
;; BACKTRACKING PATTERN
;; ============================================================================
;;
;; General backtracking template:
;;
;; (defn backtrack [state]
;;   (if (is-solution? state)
;;     [state]                           ; Found a solution
;;     (mapcat (fn [choice]
;;               (when (valid-choice? state choice)
;;                 (backtrack (make-choice state choice))))
;;             (get-choices state))))
;;
;; Key elements:
;; 1. Base case: Check if current state is a complete solution
;; 2. Generate choices: All possible next moves
;; 3. Validate: Check if choice leads to valid state
;; 4. Recurse: Make choice and continue
;; 5. Backtrack: Automatic via recursion (no explicit undo needed in functional style!)

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Forgetting diagonal constraints
;;    Queens attack diagonally too!

;; 2. Wrong diagonal formula
;;    Main diagonal: row - col (constant)
;;    Anti-diagonal: row + col (constant)

;; 3. Building board incorrectly
;;    Queens list should match row order in final board

;; 4. Not handling n=2 and n=3 (no solutions exist)

;; 5. Inefficient constraint checking
;;    Use sets O(1) instead of list search O(n)

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: N-Queens with obstacles
(defn solve-n-queens-obstacles
  "Solve N-Queens with blocked cells."
  [n blocked-cells]
  (let [blocked (set blocked-cells)
        safe? (fn [row col cols diag1 diag2]
                (and (not (blocked [row col]))
                     (not (cols col))
                     (not (diag1 (- row col)))
                     (not (diag2 (+ row col)))))]
    ;; Similar to main solution but with blocked cell check
    ;; Implementation left as exercise
    ))

;; Variation 2: N-Rooks (only row and column constraints)
(defn solve-n-rooks
  "Place n rooks so no two attack each other."
  [n]
  ;; Much simpler - just need unique columns
  ;; Number of solutions = n!
  (let [build-board (fn [perm]
                      (mapv (fn [col]
                              (apply str (map #(if (= % col) \R \.) (range n))))
                            perm))
        permutations (fn perms [items]
                       (if (empty? items)
                         [[]]
                         (mapcat (fn [item]
                                   (map #(cons item %)
                                        (perms (remove #{item} items))))
                                 items)))]
    (mapv build-board (permutations (range n)))))

;; Variation 3: First solution only (optimization)
(defn solve-n-queens-first
  "Find just the first solution (early termination)."
  [n]
  (letfn [(solve [row cols diag1 diag2 queens]
            (if (= row n)
              queens
              (some (fn [col]
                      (when (and (not (cols col))
                                 (not (diag1 (- row col)))
                                 (not (diag2 (+ row col))))
                        (solve (inc row)
                               (conj cols col)
                               (conj diag1 (- row col))
                               (conj diag2 (+ row col))
                               (conj queens col))))
                    (range n))))]
    (when-let [queens (solve 0 #{} #{} #{} [])]
      (mapv (fn [col]
              (apply str (map #(if (= % col) \Q \.) (range n))))
            queens))))

(solve-n-queens-first 8)
;; => First valid solution for 8-queens

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: N-Queens with Knights
;; Place n queens and k knights such that no piece attacks another.

;; Exercise 2: Minimum Queens to Cover Board
;; Find minimum number of queens needed so every square is attacked.

;; Exercise 3: N-Queens Completion
;; Given some queens already placed, complete the solution if possible.

;; Exercise 4: Count Attack Pairs
;; Given queen positions, count how many pairs attack each other.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn valid-solution?
  "Verify that a board configuration is a valid N-Queens solution."
  [board]
  (let [n (count board)
        queens (for [row (range n)
                     col (range n)
                     :when (= \Q (get-in board [row col]))]
                 [row col])]
    (and (= n (count queens))
         (every? (fn [[r1 c1]]
                   (every? (fn [[r2 c2]]
                             (or (and (= r1 r2) (= c1 c2))
                                 (and (not= c1 c2)
                                      (not= (- r1 c1) (- r2 c2))
                                      (not= (+ r1 c1) (+ r2 c2)))))
                           queens))
                 queens))))

(defn run-tests []
  (println "Testing solve-n-queens...")

  ;; Test n=1
  (let [solutions (solve-n-queens 1)]
    (assert (= 1 (count solutions)) "Test 1: n=1 should have 1 solution")
    (assert (every? valid-solution? solutions) "Test 1: solutions should be valid"))

  ;; Test n=2 (no solution)
  (assert (= 0 (count (solve-n-queens 2))) "Test 2: n=2 should have 0 solutions")

  ;; Test n=3 (no solution)
  (assert (= 0 (count (solve-n-queens 3))) "Test 3: n=3 should have 0 solutions")

  ;; Test n=4
  (let [solutions (solve-n-queens 4)]
    (assert (= 2 (count solutions)) "Test 4: n=4 should have 2 solutions")
    (assert (every? valid-solution? solutions) "Test 4: solutions should be valid"))

  ;; Test n=5
  (let [solutions (solve-n-queens 5)]
    (assert (= 10 (count solutions)) "Test 5: n=5 should have 10 solutions")
    (assert (every? valid-solution? solutions) "Test 5: solutions should be valid"))

  ;; Test n=8
  (let [solutions (solve-n-queens 8)]
    (assert (= 92 (count solutions)) "Test 6: n=8 should have 92 solutions")
    (assert (every? valid-solution? solutions) "Test 6: solutions should be valid"))

  ;; Test counting function
  (assert (= 2 (total-n-queens 4)) "Test 7: count n=4")
  (assert (= 92 (total-n-queens 8)) "Test 8: count n=8")

  ;; Test loop version
  (let [solutions (solve-n-queens-loop 4)]
    (assert (= 2 (count solutions)) "Test 9: loop version n=4"))

  ;; Test for version
  (let [solutions (solve-n-queens-for 4)]
    (assert (= 2 (count solutions)) "Test 10: for version n=4"))

  ;; Test first solution
  (let [solution (solve-n-queens-first 8)]
    (assert (valid-solution? solution) "Test 11: first solution should be valid"))

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. Backtracking is natural in functional style
;;    - Recursion handles the "undo" automatically
;;    - Use `mapcat` to collect results from all branches
;;
;; 2. Use sets for O(1) constraint checking
;;    - Track occupied columns, main diagonals, anti-diagonals
;;
;; 3. Diagonal encoding:
;;    - Main diagonal (↘): row - col is constant
;;    - Anti-diagonal (↙): row + col is constant
;;
;; 4. Place one queen per row to reduce search space
;;    - We only need to find valid column for each row
;;
;; 5. Early termination with `some` for first solution only
;;
;; 6. The pattern generalizes to many constraint satisfaction problems:
;;    - Sudoku
;;    - Graph coloring
;;    - Subset sum
;;    - Permutation generation
;;
;; NEXT: hard_06_word_ladder.clj
