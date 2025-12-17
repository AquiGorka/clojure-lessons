# LeetCode-Style Problems in Clojure

Master algorithmic problem-solving using Clojure's powerful functional programming features! This collection of problems will help you practice coding interviews while learning idiomatic Clojure.

## 🎯 Why Solve LeetCode Problems in Clojure?

1. **Functional Thinking** - Learn to approach problems with immutable data and pure functions
2. **Concise Solutions** - Clojure's rich standard library often leads to elegant one-liners
3. **Interview Prep** - Practice common patterns while deepening your Clojure knowledge
4. **Different Perspective** - See how functional solutions compare to imperative ones

---

## 📚 Problem Index

### Easy Problems (15)
| # | File | Problem | Key Concepts |
|---|------|---------|--------------|
| 01 | `easy_01_two_sum.clj` | Two Sum | Hash maps, reduce, indexed iteration |
| 02 | `easy_02_valid_parentheses.clj` | Valid Parentheses | Stack simulation, reduce |
| 03 | `easy_03_merge_sorted_lists.clj` | Merge Two Sorted Lists | Lazy sequences, recursion |
| 04 | `easy_04_best_time_buy_sell.clj` | Best Time to Buy/Sell Stock | Reductions, state threading |
| 05 | `easy_05_valid_palindrome.clj` | Valid Palindrome | String manipulation, filtering |
| 06 | `easy_06_single_number.clj` | Single Number | Bit manipulation, reduce |
| 07 | `easy_07_climbing_stairs.clj` | Climbing Stairs | Dynamic programming, iterate |
| 08 | `easy_08_maximum_subarray.clj` | Maximum Subarray | Kadane's algorithm, reductions |
| 09 | `easy_09_contains_duplicate.clj` | Contains Duplicate | Sets, frequencies |
| 10 | `easy_10_reverse_list.clj` | Reverse Linked List | Recursion, reduce, cons |
| 11 | `easy_11_fizz_buzz.clj` | Fizz Buzz | Conditionals, map, lazy sequences |
| 12 | `easy_12_roman_to_integer.clj` | Roman to Integer | Maps, reduce, partition |
| 13 | `easy_13_palindrome_number.clj` | Palindrome Number | Math, sequences, string conversion |
| 14 | `easy_14_move_zeroes.clj` | Move Zeroes | Filtering, concatenation, sort-by |
| 15 | `easy_15_intersection_arrays.clj` | Intersection of Two Arrays | Sets, set operations |

### Medium Problems (15)
| # | File | Problem | Key Concepts |
|---|------|---------|--------------|
| 01 | `medium_01_group_anagrams.clj` | Group Anagrams | group-by, sorting, frequencies |
| 02 | `medium_02_longest_substring.clj` | Longest Substring Without Repeating | Sliding window, sets, reduce |
| 03 | `medium_03_product_except_self.clj` | Product of Array Except Self | Prefix/suffix products, reductions |
| 04 | `medium_04_three_sum.clj` | 3Sum | Two pointers, sorting, deduplication |
| 05 | `medium_05_container_water.clj` | Container With Most Water | Two pointers, loop/recur |
| 06 | `medium_06_coin_change.clj` | Coin Change | Dynamic programming, iterate |
| 07 | `medium_07_longest_palindrome.clj` | Longest Palindromic Substring | Expand around center |
| 08 | `medium_08_subsets.clj` | Subsets | Recursion, reduce, bit manipulation |
| 09 | `medium_09_permutations.clj` | Permutations | Recursion, for comprehension |
| 10 | `medium_10_word_search.clj` | Word Search | Backtracking, recursion |
| 11 | `medium_11_rotate_image.clj` | Rotate Image | Matrix manipulation, transposition |
| 12 | `medium_12_merge_intervals.clj` | Merge Intervals | Sorting, reduce |
| 13 | `medium_13_spiral_matrix.clj` | Spiral Matrix | Recursion, matrix slicing |
| 14 | `medium_14_top_k_frequent.clj` | Top K Frequent Elements | frequencies, sorting, partial |
| 15 | `medium_15_longest_increasing.clj` | Longest Increasing Subsequence | DP, binary search, patience sort |

### Hard Problems (10)
| # | File | Problem | Key Concepts |
|---|------|---------|--------------|
| 01 | `hard_01_median_sorted_arrays.clj` | Median of Two Sorted Arrays | Binary search, partitioning |
| 02 | `hard_02_regex_matching.clj` | Regular Expression Matching | Recursion, memoization |
| 03 | `hard_03_merge_k_lists.clj` | Merge K Sorted Lists | Divide & conquer, lazy sequences |
| 04 | `hard_04_trapping_rain_water.clj` | Trapping Rain Water | Two pointers, prefix max |
| 05 | `hard_05_n_queens.clj` | N-Queens | Backtracking, sets |
| 06 | `hard_06_word_ladder.clj` | Word Ladder | BFS, graph traversal |
| 07 | `hard_07_minimum_window.clj` | Minimum Window Substring | Sliding window, frequencies |
| 08 | `hard_08_longest_valid_parens.clj` | Longest Valid Parentheses | Stack, dynamic programming |
| 09 | `hard_09_largest_rectangle.clj` | Largest Rectangle in Histogram | Monotonic stack |
| 10 | `hard_10_edit_distance.clj` | Edit Distance | Dynamic programming, memoization |

---

## 🚀 How to Use These Lessons

### 1. Understand the Problem
Each file starts with a clear problem statement, examples, and constraints.

### 2. Try It Yourself First
Before looking at the solution, attempt to solve it! Load the file in your REPL:
```clojure
clj
(load-file "problems/easy_01_two_sum.clj")
```

### 3. Study the Solution
Each solution includes:
- Step-by-step explanation
- Multiple approaches (when applicable)
- Time and space complexity analysis
- Clojure-specific insights

### 4. Practice Variations
Many problems include follow-up exercises and variations.

---

## 🔧 Common Patterns in Clojure

### Hash Map for O(1) Lookup
```clojure
;; Build index as you iterate
(reduce (fn [seen x]
          (if (seen (- target x))
            (reduced [found!])
            (assoc seen x idx)))
        {}
        coll)
```

### Sliding Window
```clojure
;; Using loop/recur with state
(loop [left 0, right 0, window #{}, max-len 0]
  (cond
    (>= right n) max-len
    (window (s right)) (recur (inc left) right (disj window (s left)) max-len)
    :else (recur left (inc right) (conj window (s right)) (max max-len (- right left -1)))))
```

### Dynamic Programming
```clojure
;; Bottom-up with iterate
(nth (iterate step-fn initial-state) n)

;; Or with reduce
(reduce step-fn initial-state (range n))

;; Memoization for top-down
(def solve (memoize (fn [args] ...)))
```

### Backtracking
```clojure
(defn backtrack [state choices]
  (if (solution? state)
    [state]
    (mapcat #(backtrack (add-choice state %) (remaining-choices %))
            (valid-choices state choices))))
```

### Two Pointers
```clojure
(loop [left 0, right (dec n), result []]
  (cond
    (>= left right) result
    (condition?) (recur (inc left) right (conj result ...))
    :else (recur left (dec right) result)))
```

---

## 📊 Complexity Cheat Sheet

| Operation | Clojure | Complexity |
|-----------|---------|------------|
| Vector lookup | `(get v i)` or `(v i)` | O(log32 n) ≈ O(1) |
| Vector append | `(conj v x)` | O(log32 n) ≈ O(1) |
| Vector update | `(assoc v i x)` | O(log32 n) ≈ O(1) |
| List prepend | `(cons x lst)` | O(1) |
| List lookup | `(nth lst i)` | O(n) |
| Hash map lookup | `(get m k)` | O(log32 n) ≈ O(1) |
| Hash map assoc | `(assoc m k v)` | O(log32 n) ≈ O(1) |
| Set contains | `(contains? s x)` | O(log32 n) ≈ O(1) |
| Sort | `(sort coll)` | O(n log n) |
| Filter/Map | `(filter pred coll)` | O(n) |
| Reduce | `(reduce f coll)` | O(n) |

---

## 💡 Tips for Problem Solving in Clojure

1. **Start with `reduce`** - Many problems are naturally reductions over a collection
2. **Use immutability** - Don't fight it; build new state rather than mutating
3. **Think lazily** - Use lazy sequences for memory-efficient processing
4. **Leverage destructuring** - Makes code readable and handles complex data
5. **Threading macros** - Use `->` and `->>` for readable pipelines
6. **Sets for membership** - O(1) lookup beats filtering
7. **`frequencies`** - Your friend for counting problems
8. **`group-by`** - Instant grouping by any function
9. **`partition`** - Sliding windows and chunking
10. **`iterate`** - Generate sequences from repeated function application
11. **`reduced`** - Early termination in reduce
12. **`reductions`** - See all intermediate states (great for debugging)

---

## 🎓 Prerequisites

These problems assume you've completed (or are familiar with) the main Clojure lessons:
- Lessons 1-5: Basic syntax and data structures
- Lessons 6-7: Sequences and higher-order functions
- Lesson 8: Destructuring
- Lesson 10: Threading macros

---

## 📈 Suggested Learning Path

### Week 1-2: Easy Problems
Complete all 15 easy problems. Focus on:
- Getting comfortable with Clojure syntax
- Learning common patterns (reduce, map, filter)
- Understanding immutable data structures

### Week 3-4: Medium Problems
Work through medium problems. Focus on:
- Algorithm design in functional style
- Efficiency and complexity
- More advanced patterns (sliding window, two pointers, DP)

### Week 5-6: Hard Problems
Tackle hard problems. Focus on:
- Complex algorithm implementation
- Optimization techniques
- Combining multiple patterns

---

## 🧪 Running Tests

Each problem file includes a `run-tests` function:

```clojure
;; In REPL after loading a file:
(run-tests)
```

---

## 📖 Additional Resources

### Practice
- [4Clojure](https://4clojure.oxal.org/) - Clojure-specific problems
- [Exercism Clojure Track](https://exercism.org/tracks/clojure)
- [Advent of Code](https://adventofcode.com/) - Great for Clojure practice

### Reference
- [ClojureDocs](https://clojuredocs.org/) - Community documentation with examples
- [Clojure Cheatsheet](https://clojure.org/api/cheatsheet)

---

Happy problem solving! 🧩

*"Programs must be written for people to read, and only incidentally for machines to execute."* — Harold Abelson