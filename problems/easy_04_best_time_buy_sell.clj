;; ============================================================================
;; EASY 04: Best Time to Buy and Sell Stock
;; ============================================================================
;; Source: LeetCode #121 - Best Time to Buy and Sell Stock
;; Difficulty: Easy
;; Topics: Array, Dynamic Programming, Sliding Window
;;
;; A classic problem that introduces tracking minimum values and
;; demonstrates how to maintain state while traversing a sequence.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; You are given an array `prices` where `prices[i]` is the price of a given
;; stock on the ith day.
;;
;; You want to maximize your profit by choosing a single day to buy one stock
;; and choosing a different day in the future to sell that stock.
;;
;; Return the maximum profit you can achieve from this transaction.
;; If you cannot achieve any profit, return 0.
;;
;; Note: You must buy BEFORE you sell (can't sell on day 0 and buy on day 5).

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: prices = [7, 1, 5, 3, 6, 4]
;;   Output: 5
;;   Explanation: Buy on day 2 (price = 1) and sell on day 5 (price = 6).
;;                Profit = 6 - 1 = 5.
;;                Note: Buying on day 2 and selling on day 1 is not allowed
;;                because you must buy before you sell.
;;
;; Example 2:
;;   Input: prices = [7, 6, 4, 3, 1]
;;   Output: 0
;;   Explanation: In this case, no transactions are done and max profit = 0.
;;                (Prices only decrease, so any buy-sell would result in loss)

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= prices.length <= 10^5
;; - 0 <= prices[i] <= 10^4

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; Key insight: For each day, we want to know:
;; "What's the maximum profit if I sell TODAY?"
;;
;; To maximize profit when selling on day i:
;; profit = prices[i] - (minimum price from day 0 to day i-1)
;;
;; So we need to track the minimum price seen SO FAR.
;;
;; Visual trace for [7, 1, 5, 3, 6, 4]:
;;   Day 0: price=7, min=7, can't sell yet
;;   Day 1: price=1, min=1, profit=1-7=-6 (or 0), update min to 1
;;   Day 2: price=5, min=1, profit=5-1=4
;;   Day 3: price=3, min=1, profit=3-1=2
;;   Day 4: price=6, min=1, profit=6-1=5 ← Maximum!
;;   Day 5: price=4, min=1, profit=4-1=3
;;
;; Maximum profit = 5

;; ============================================================================
;; SOLUTION 1: Brute Force (for understanding)
;; ============================================================================
;; Time Complexity: O(n²)
;; Space Complexity: O(1)

(defn max-profit-brute
  "Check every possible buy-sell pair."
  [prices]
  (let [n (count prices)]
    (if (< n 2)
      0
      (apply max 0
             (for [buy (range n)
                   sell (range (inc buy) n)]
               (- (prices sell) (prices buy)))))))

(max-profit-brute [7 1 5 3 6 4])
;; => 5

;; This works but is O(n²) - too slow for large inputs!

;; ============================================================================
;; SOLUTION 2: One Pass with reduce (Optimal)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(1)

(defn max-profit
  "Track minimum price and maximum profit in a single pass."
  [prices]
  (if (< (count prices) 2)
    0
    (let [[max-profit _]
          (reduce
           (fn [[max-profit min-price] price]
             [(max max-profit (- price min-price))
              (min min-price price)])
           [0 (first prices)]  ; Initial: profit=0, min=first price
           (rest prices))]     ; Process remaining prices
      max-profit)))

(max-profit [7 1 5 3 6 4])
;; => 5

(max-profit [7 6 4 3 1])
;; => 0

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; prices = [7, 1, 5, 3, 6, 4]
;; Initial state: [max-profit=0, min-price=7]
;;
;; price=1:
;;   profit = 1 - 7 = -6
;;   max-profit = max(0, -6) = 0
;;   min-price = min(7, 1) = 1
;;   state: [0, 1]
;;
;; price=5:
;;   profit = 5 - 1 = 4
;;   max-profit = max(0, 4) = 4
;;   min-price = min(1, 5) = 1
;;   state: [4, 1]
;;
;; price=3:
;;   profit = 3 - 1 = 2
;;   max-profit = max(4, 2) = 4
;;   min-price = min(1, 3) = 1
;;   state: [4, 1]
;;
;; price=6:
;;   profit = 6 - 1 = 5
;;   max-profit = max(4, 5) = 5
;;   min-price = min(1, 6) = 1
;;   state: [5, 1]
;;
;; price=4:
;;   profit = 4 - 1 = 3
;;   max-profit = max(5, 3) = 5
;;   min-price = min(1, 4) = 1
;;   state: [5, 1]
;;
;; Final: max-profit = 5

;; ============================================================================
;; SOLUTION 3: Using reductions (See all intermediate states)
;; ============================================================================
;; `reductions` is like reduce but returns all intermediate values

(defn max-profit-trace
  "Same algorithm but shows all intermediate states."
  [prices]
  (reductions
   (fn [[max-profit min-price] price]
     [(max max-profit (- price min-price))
      (min min-price price)])
   [0 (first prices)]
   (rest prices)))

(max-profit-trace [7 1 5 3 6 4])
;; => ([0 7] [0 1] [4 1] [4 1] [5 1] [5 1])
;; Each pair is [max-profit-so-far, min-price-so-far]

;; ============================================================================
;; SOLUTION 4: Using loop/recur
;; ============================================================================

(defn max-profit-loop
  "Explicit loop version."
  [prices]
  (if (< (count prices) 2)
    0
    (loop [remaining (rest prices)
           min-price (first prices)
           max-profit 0]
      (if (empty? remaining)
        max-profit
        (let [price (first remaining)
              profit (- price min-price)]
          (recur (rest remaining)
                 (min min-price price)
                 (max max-profit profit)))))))

(max-profit-loop [7 1 5 3 6 4])
;; => 5

;; ============================================================================
;; SOLUTION 5: Using iterate (Functional State Machine)
;; ============================================================================

(defn max-profit-iterate
  "Using iterate to generate state transitions."
  [prices]
  (if (< (count prices) 2)
    0
    (let [initial-state {:idx 1
                         :min-price (first prices)
                         :max-profit 0}
          step (fn [{:keys [idx min-price max-profit]}]
                 (if (>= idx (count prices))
                   nil  ; Signal completion
                   (let [price (prices idx)]
                     {:idx (inc idx)
                      :min-price (min min-price price)
                      :max-profit (max max-profit (- price min-price))})))
          states (take-while some? (iterate step initial-state))]
      (:max-profit (last states)))))

(max-profit-iterate [7 1 5 3 6 4])
;; => 5

;; ============================================================================
;; SOLUTION 6: Elegant one-liner
;; ============================================================================

(defn max-profit-elegant
  "Concise version using map and reductions."
  [prices]
  (if (< (count prices) 2)
    0
    (apply max 0
           (map - (rest prices)
                (reductions min prices)))))

;; Explanation:
;; (reductions min prices) gives running minimum: [7 1 1 1 1 1]
;; (rest prices) gives prices starting from day 1: [1 5 3 6 4]
;; (map - ...) subtracts: [-6 4 2 5 3]
;; (apply max 0 ...) finds the maximum, at least 0

(max-profit-elegant [7 1 5 3 6 4])
;; => 5

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `reduce` with compound state
;;    We pack multiple pieces of state into a vector [max-profit min-price].
;;    This is a common pattern when you need to track multiple values.

;; 2. `reductions` for debugging/understanding
;;    Shows all intermediate states of a reduce operation.
;;    Very useful for debugging and understanding algorithms.

(reductions + [1 2 3 4 5])
;; => (1 3 6 10 15) - running sum

(reductions min [7 1 5 3 6 4])
;; => (7 1 1 1 1 1) - running minimum

;; 3. Destructuring in function parameters
;;    [[max-profit min-price] price] unpacks the accumulator vector

;; 4. `apply` with variadic functions
;;    (apply max 0 coll) ensures we get at least 0 even if coll is empty

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(max-profit [])
;; => 0 (no days)

(max-profit [5])
;; => 0 (single day, can't buy and sell)

(max-profit [1 2])
;; => 1 (buy day 0, sell day 1)

(max-profit [2 1])
;; => 0 (prices decrease, no profit possible)

(max-profit [3 3 3 3])
;; => 0 (all same price)

(max-profit [1 2 3 4 5])
;; => 4 (monotonically increasing)

(max-profit [5 4 3 2 1])
;; => 0 (monotonically decreasing)

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Brute Force:
;;   Time:  O(n²) - check all pairs
;;   Space: O(1)
;;
;; Optimal Solution:
;;   Time:  O(n) - single pass through the array
;;   Space: O(1) - only track two values (min-price, max-profit)
;;
;; Why can't we do better than O(n)?
;;   - We must look at each price at least once
;;   - The minimum could be at any position
;;   - The maximum profit sell point could be anywhere after the minimum

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Forgetting the order constraint (must buy before sell)
;;    Can't just find (max - min) of the array!
;;    Example: [3, 2, 6, 5, 0, 3]
;;    max=6, min=0, but 0 comes AFTER 6!

;; 2. Not handling edge cases (empty, single element)

;; 3. Returning negative profit instead of 0
;;    If no profitable transaction exists, return 0

;; 4. Off-by-one: starting min at infinity instead of first price
;;    Could work, but starting at first price is clearer

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Return the buy and sell days (not just profit)
(defn best-transaction
  "Return [buy-day sell-day profit] or nil if no profit possible."
  [prices]
  (if (< (count prices) 2)
    nil
    (let [[result _]
          (reduce
           (fn [[best min-info] [idx price]]
             (let [profit (- price (:price min-info))
                   new-best (if (> profit (:profit best))
                              {:buy (:day min-info)
                               :sell idx
                               :profit profit}
                              best)
                   new-min (if (< price (:price min-info))
                             {:day idx :price price}
                             min-info)]
               [new-best new-min]))
           [{:buy 0 :sell 0 :profit 0}
            {:day 0 :price (first prices)}]
           (map-indexed vector (rest prices)))]
      (when (pos? (:profit result))
        [(:buy result) (inc (:sell result)) (:profit result)]))))

(best-transaction [7 1 5 3 6 4])
;; => [1 4 5] - buy on day 1, sell on day 4, profit 5

;; Variation 2: Best Time to Buy and Sell Stock II (multiple transactions)
;; You can make multiple transactions (buy-sell-buy-sell...)
(defn max-profit-multiple
  "Maximum profit with unlimited transactions."
  [prices]
  (->> prices
       (partition 2 1)  ; consecutive pairs
       (map (fn [[a b]] (- b a)))  ; daily changes
       (filter pos?)    ; keep only positive changes
       (reduce + 0)))   ; sum them up

(max-profit-multiple [7 1 5 3 6 4])
;; => 7 (buy@1, sell@5: +4, buy@3, sell@6: +3, total=7)

;; Variation 3: With transaction fee
(defn max-profit-with-fee
  "Maximum profit with a transaction fee per trade."
  [prices fee]
  (if (< (count prices) 2)
    0
    (let [[cash hold]
          (reduce
           (fn [[cash hold] price]
             [(max cash (+ hold price (- fee)))  ; sell
              (max hold (- cash price))])        ; buy
           [0 (- (first prices))]
           (rest prices))]
      cash)))

(max-profit-with-fee [1 3 2 8 4 9] 2)
;; => 8

;; Variation 4: With cooldown (must wait 1 day after selling)
(defn max-profit-with-cooldown
  "Maximum profit with 1-day cooldown after selling."
  [prices]
  (if (< (count prices) 2)
    0
    (let [[sold held reset]
          (reduce
           (fn [[sold held reset] price]
             [(+ held price)           ; sell today
              (max held (- reset price))  ; buy today (from reset state)
              (max reset sold)])       ; cooldown
           [0 (- (first prices)) 0]
           (rest prices))]
      (max sold reset))))

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Best Time with K Transactions
;; You may complete at most k transactions.
;; Design an algorithm to find the maximum profit.

;; Exercise 2: Best Time with Short Selling
;; You can also "short sell" (sell first, buy later).
;; Find the maximum profit.

;; Exercise 3: Find All Profitable Transactions
;; Return all [buy-day sell-day profit] triples where profit > 0.

;; Exercise 4: Moving Window Maximum Profit
;; For each position i, find the maximum profit achievable
;; in the window [i, i+k).

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing max-profit...")

  ;; Basic tests
  (assert (= 5 (max-profit [7 1 5 3 6 4]))
          "Test 1: Basic case")
  (assert (= 0 (max-profit [7 6 4 3 1]))
          "Test 2: Decreasing prices")

  ;; Edge cases
  (assert (= 0 (max-profit []))
          "Test 3: Empty array")
  (assert (= 0 (max-profit [5]))
          "Test 4: Single element")
  (assert (= 1 (max-profit [1 2]))
          "Test 5: Two elements, profit")
  (assert (= 0 (max-profit [2 1]))
          "Test 6: Two elements, no profit")

  ;; All same
  (assert (= 0 (max-profit [3 3 3 3]))
          "Test 7: All same price")

  ;; Monotonic
  (assert (= 4 (max-profit [1 2 3 4 5]))
          "Test 8: Increasing")
  (assert (= 0 (max-profit [5 4 3 2 1]))
          "Test 9: Decreasing")

  ;; Complex cases
  (assert (= 6 (max-profit [3 2 6 5 0 3]))
          "Test 10: Complex case")
  (assert (= 9 (max-profit [1 2 4 2 5 7 2 4 9 0]))
          "Test 11: Complex case 2")

  ;; Test other implementations
  (assert (= 5 (max-profit-loop [7 1 5 3 6 4]))
          "Test 12: Loop version")
  (assert (= 5 (max-profit-elegant [7 1 5 3 6 4]))
          "Test 13: Elegant version")
  (assert (= 5 (max-profit-brute [7 1 5 3 6 4]))
          "Test 14: Brute force version")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. Track running state (min-price) while computing result (max-profit)
;;
;; 2. `reduce` with compound state [a b] is a powerful pattern
;;
;; 3. `reductions` shows all intermediate states - great for debugging
;;
;; 4. The elegant solution uses `map` with multiple sequences
;;    (map f seq1 seq2) applies f pairwise
;;
;; 5. Order constraints (buy before sell) require careful thinking
;;
;; 6. This "track minimum while scanning" pattern appears in many problems
;;
;; NEXT: easy_05_valid_palindrome.clj
