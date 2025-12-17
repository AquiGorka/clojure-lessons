;; ============================================================================
;; MEDIUM 02: Longest Substring Without Repeating Characters
;; ============================================================================
;; Source: LeetCode #3 - Longest Substring Without Repeating Characters
;; Difficulty: Medium
;; Topics: Hash Table, String, Sliding Window
;;
;; A classic sliding window problem that demonstrates efficient string
;; processing and the importance of maintaining a window of valid elements.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given a string s, find the length of the longest substring without
;; repeating characters.
;;
;; A substring is a contiguous sequence of characters within a string.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: s = "abcabcbb"
;;   Output: 3
;;   Explanation: The answer is "abc", with the length of 3.
;;
;; Example 2:
;;   Input: s = "bbbbb"
;;   Output: 1
;;   Explanation: The answer is "b", with the length of 1.
;;
;; Example 3:
;;   Input: s = "pwwkew"
;;   Output: 3
;;   Explanation: The answer is "wke", with the length of 3.
;;   Notice that "pwke" is a subsequence, not a substring.

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 0 <= s.length <= 5 * 10^4
;; - s consists of English letters, digits, symbols and spaces.

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; Key insight: Use a sliding window that maintains the invariant
;; "no repeated characters in the window."
;;
;; When we encounter a character that's already in the window:
;; - Shrink the window from the left until the duplicate is removed
;;
;; We track:
;; - Current window boundaries (left, right)
;; - Characters in the current window (using a set or map)
;; - Maximum length seen so far
;;
;; Visual trace for "abcabcbb":
;;   [a]bcabcbb       window="a", len=1, max=1
;;   [ab]cabcbb       window="ab", len=2, max=2
;;   [abc]abcbb       window="abc", len=3, max=3
;;   a[bca]bcbb       'a' duplicate, slide left, window="bca", len=3, max=3
;;   ab[cab]cbb       'b' duplicate, slide left, window="cab", len=3, max=3
;;   abc[abc]bb       'c' duplicate, slide left, window="abc", len=3, max=3
;;   abca[bc]bb       'b' duplicate, slide left, window="bc", len=2, max=3
;;   abcab[cb]b       'b' duplicate, slide left, window="cb", len=2, max=3
;;   abcabc[b]b       'b' duplicate, slide left, window="b", len=1, max=3
;;   Result: 3

;; ============================================================================
;; SOLUTION 1: Sliding Window with Set (Clear and Educational)
;; ============================================================================
;; Time Complexity: O(n) - each character visited at most twice
;; Space Complexity: O(min(m, n)) where m is charset size

(defn length-of-longest-substring
  "Find length of longest substring without repeating characters."
  [s]
  (if (empty? s)
    0
    (loop [left 0
           right 0
           char-set #{}
           max-len 0]
      (if (>= right (count s))
        max-len
        (let [c (nth s right)]
          (if (contains? char-set c)
            ;; Duplicate found: shrink window from left
            (recur (inc left)
                   right
                   (disj char-set (nth s left))
                   max-len)
            ;; No duplicate: expand window
            (recur left
                   (inc right)
                   (conj char-set c)
                   (max max-len (- (inc right) left)))))))))

(length-of-longest-substring "abcabcbb")
;; => 3

(length-of-longest-substring "bbbbb")
;; => 1

(length-of-longest-substring "pwwkew")
;; => 3

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; Input: "abcabcbb"
;;
;; Step 1: left=0, right=0, char='a'
;;   'a' not in #{} -> add 'a', expand
;;   char-set=#{a}, max-len=1
;;
;; Step 2: left=0, right=1, char='b'
;;   'b' not in #{a} -> add 'b', expand
;;   char-set=#{a b}, max-len=2
;;
;; Step 3: left=0, right=2, char='c'
;;   'c' not in #{a b} -> add 'c', expand
;;   char-set=#{a b c}, max-len=3
;;
;; Step 4: left=0, right=3, char='a'
;;   'a' in #{a b c} -> remove s[0]='a', shrink
;;   char-set=#{b c}, left=1
;;
;; Step 5: left=1, right=3, char='a'
;;   'a' not in #{b c} -> add 'a', expand
;;   char-set=#{a b c}, max-len=3
;;
;; ... continues until right >= 8
;; Final: max-len=3

;; ============================================================================
;; SOLUTION 2: Optimized with Character Index Map
;; ============================================================================
;; Instead of removing characters one by one, jump directly to after
;; the previous occurrence of the duplicate character.
;; Time Complexity: O(n)
;; Space Complexity: O(min(m, n))

(defn length-of-longest-substring-optimized
  "Optimized version using character position map."
  [s]
  (if (empty? s)
    0
    (loop [left 0
           right 0
           char-idx {}  ; Maps character to its most recent index
           max-len 0]
      (if (>= right (count s))
        max-len
        (let [c (nth s right)
              prev-idx (get char-idx c -1)]
          (if (>= prev-idx left)
            ;; Duplicate in current window: jump left past the duplicate
            (recur (inc prev-idx)
                   (inc right)
                   (assoc char-idx c right)
                   max-len)
            ;; No duplicate in window: expand
            (recur left
                   (inc right)
                   (assoc char-idx c right)
                   (max max-len (- (inc right) left)))))))))

(length-of-longest-substring-optimized "abcabcbb")
;; => 3

;; ============================================================================
;; SOLUTION 3: Using reduce with state
;; ============================================================================

(defn length-of-longest-substring-reduce
  "Using reduce to process characters."
  [s]
  (if (empty? s)
    0
    (let [[max-len _ _]
          (reduce
           (fn [[max-len left char-idx] [right c]]
             (let [prev-idx (get char-idx c -1)
                   new-left (if (>= prev-idx left)
                              (inc prev-idx)
                              left)
                   new-len (- (inc right) new-left)]
               [(max max-len new-len)
                new-left
                (assoc char-idx c right)]))
           [0 0 {}]
           (map-indexed vector s))]
      max-len)))

(length-of-longest-substring-reduce "abcabcbb")
;; => 3

;; ============================================================================
;; SOLUTION 4: Using frequencies with sliding window
;; ============================================================================

(defn length-of-longest-substring-freq
  "Using frequency map to track window."
  [s]
  (if (empty? s)
    0
    (loop [left 0
           right 0
           freq {}
           max-len 0]
      (if (>= right (count s))
        max-len
        (let [c (nth s right)
              new-freq (update freq c (fnil inc 0))]
          (if (> (new-freq c) 1)
            ;; Have duplicate: shrink from left
            (let [left-char (nth s left)]
              (recur (inc left)
                     right
                     (update freq left-char dec)
                     max-len))
            ;; No duplicate: expand
            (recur left
                   (inc right)
                   new-freq
                   (max max-len (- (inc right) left)))))))))

(length-of-longest-substring-freq "abcabcbb")
;; => 3

;; ============================================================================
;; SOLUTION 5: Elegant functional version
;; ============================================================================

(defn longest-unique-substring
  "Elegant functional implementation."
  [s]
  (letfn [(valid-window? [chars]
            (= (count chars) (count (set chars))))]
    (if (empty? s)
      0
      (->> (range 1 (inc (count s)))  ; All possible lengths
           (mapcat #(partition % 1 s))  ; All substrings of each length
           (filter valid-window?)
           (map count)
           (apply max 0)))))

;; Note: This is O(n^3) and only for educational purposes!

;; ============================================================================
;; SOLUTION 6: Using reductions to show all states
;; ============================================================================

(defn longest-substring-trace
  "Show all intermediate states."
  [s]
  (reductions
   (fn [[max-len left char-idx] [right c]]
     (let [prev-idx (get char-idx c -1)
           new-left (if (>= prev-idx left)
                      (inc prev-idx)
                      left)
           new-len (- (inc right) new-left)]
       [(max max-len new-len)
        new-left
        (assoc char-idx c right)]))
   [0 0 {}]
   (map-indexed vector s)))

(longest-substring-trace "abcabcbb")
;; Shows all intermediate states for debugging

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `loop/recur` for efficient iteration with state
;;    Clojure optimizes tail recursion with recur

;; 2. Sets for O(1) membership testing
(contains? #{:a :b :c} :b)  ;; => true
(conj #{:a :b} :c)          ;; => #{:a :b :c}
(disj #{:a :b :c} :b)       ;; => #{:a :c}

;; 3. Maps for O(1) lookup of last seen positions
(get {:a 0 :b 1} :a)        ;; => 0
(get {:a 0 :b 1} :c -1)     ;; => -1 (default)

;; 4. `nth` for string character access
(nth "hello" 1)             ;; => \e

;; 5. `fnil` for handling nil in update
((fnil inc 0) nil)          ;; => 1
((fnil inc 0) 5)            ;; => 6

;; 6. `map-indexed` to get index-value pairs
(map-indexed vector "abc")  ;; => ([0 \a] [1 \b] [2 \c])

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(length-of-longest-substring "")
;; => 0 (empty string)

(length-of-longest-substring "a")
;; => 1 (single character)

(length-of-longest-substring "au")
;; => 2 (two different characters)

(length-of-longest-substring "aa")
;; => 1 (two same characters)

(length-of-longest-substring "aab")
;; => 2 ("ab")

(length-of-longest-substring " ")
;; => 1 (space is a character)

(length-of-longest-substring "dvdf")
;; => 3 ("vdf")

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Solution 1 (Set-based):
;;   Time:  O(2n) = O(n) - each char visited at most twice (add and remove)
;;   Space: O(min(m, n)) - m is charset size (128 ASCII or 26 letters)
;;
;; Solution 2 (Index Map):
;;   Time:  O(n) - each character visited exactly once
;;   Space: O(min(m, n))
;;
;; The index map approach is slightly faster because it jumps directly
;; to the position after the duplicate, rather than removing one by one.
;;
;; For strings with many duplicates, the optimized version is significantly
;; faster.

;; ============================================================================
;; SLIDING WINDOW PATTERN
;; ============================================================================
;;
;; The sliding window pattern is used when:
;; 1. Input is linear (array, string, linked list)
;; 2. Looking for subarray/substring that satisfies a condition
;; 3. Window has a measurable property (sum, unique elements, etc.)
;;
;; Template:
;; (loop [left 0, right 0, state initial-state, result initial-result]
;;   (if (done?)
;;     result
;;     (let [new-state (update-state state (get-element right))]
;;       (if (window-valid? new-state)
;;         (recur left (inc right) new-state (update-result result))
;;         (recur (inc left) right (shrink-state state left) result)))))

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Off-by-one in window length calculation
;;    Length = right - left + 1 (when right is inclusive)
;;    Or = right - left (when right is exclusive/next position)

;; 2. Not handling empty string
;;    Always check for empty input

;; 3. Forgetting that space is a valid character
;;    " " should return 1

;; 4. Using wrong default for character lookup
;;    Use -1 (not 0) as default so position 0 is valid

;; 5. Not updating left pointer correctly
;;    Left should be max(left, prev-idx + 1), not just prev-idx + 1
;;    (we never move left backwards)

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Return the actual substring, not just length
(defn longest-unique-substr
  "Return the actual longest substring without repeating chars."
  [s]
  (if (empty? s)
    ""
    (loop [left 0
           right 0
           char-idx {}
           best-start 0
           best-len 0]
      (if (>= right (count s))
        (subs s best-start (+ best-start best-len))
        (let [c (nth s right)
              prev-idx (get char-idx c -1)
              new-left (if (>= prev-idx left) (inc prev-idx) left)
              new-len (- (inc right) new-left)
              [new-best-start new-best-len]
              (if (> new-len best-len)
                [new-left new-len]
                [best-start best-len])]
          (recur new-left
                 (inc right)
                 (assoc char-idx c right)
                 new-best-start
                 new-best-len))))))

(longest-unique-substr "abcabcbb")
;; => "abc"

;; Variation 2: At most K distinct characters
(defn longest-k-distinct
  "Find longest substring with at most k distinct characters."
  [s k]
  (if (or (empty? s) (<= k 0))
    0
    (loop [left 0
           right 0
           freq {}
           max-len 0]
      (if (>= right (count s))
        max-len
        (let [c (nth s right)
              new-freq (update freq c (fnil inc 0))
              distinct-count (count new-freq)]
          (if (> distinct-count k)
            ;; Too many distinct: shrink from left
            (let [left-char (nth s left)
                  updated-freq (update new-freq left-char dec)
                  cleaned-freq (if (zero? (updated-freq left-char))
                                 (dissoc updated-freq left-char)
                                 updated-freq)]
              (recur (inc left) right cleaned-freq max-len))
            ;; Valid window: expand
            (recur left (inc right) new-freq
                   (max max-len (- (inc right) left)))))))))

(longest-k-distinct "eceba" 2)
;; => 3 ("ece")

(longest-k-distinct "aa" 1)
;; => 2 ("aa")

;; Variation 3: Longest substring with at most 2 distinct characters
(defn longest-two-distinct [s]
  (longest-k-distinct s 2))

;; Variation 4: All unique substrings
(defn all-unique-substrings
  "Find all substrings without repeating characters."
  [s]
  (loop [left 0
         right 0
         char-set #{}
         results #{}]
    (if (>= right (count s))
      (vec results)
      (let [c (nth s right)]
        (if (contains? char-set c)
          (recur (inc left)
                 right
                 (disj char-set (nth s left))
                 results)
          (let [new-set (conj char-set c)
                new-results (into results
                                  (for [i (range left (inc right))]
                                    (subs s i (inc right))))]
            (recur left (inc right) new-set new-results)))))))

(all-unique-substrings "abc")
;; => ["a" "ab" "abc" "b" "bc" "c"]

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Longest Repeating Character Replacement (LeetCode #424)
;; Find longest substring containing same letter after at most k changes.

;; Exercise 2: Minimum Window Substring (LeetCode #76)
;; Find minimum window in s that contains all characters of t. (Hard!)

;; Exercise 3: Substring with Concatenation of All Words (LeetCode #30)
;; Find all starting indices of substrings that are concatenation of words.

;; Exercise 4: Longest Substring with At Least K Repeating Characters
;; Every character appears at least k times.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing length-of-longest-substring...")

  ;; Basic tests
  (assert (= 3 (length-of-longest-substring "abcabcbb"))
          "Test 1: abcabcbb")
  (assert (= 1 (length-of-longest-substring "bbbbb"))
          "Test 2: bbbbb")
  (assert (= 3 (length-of-longest-substring "pwwkew"))
          "Test 3: pwwkew")

  ;; Edge cases
  (assert (= 0 (length-of-longest-substring ""))
          "Test 4: empty string")
  (assert (= 1 (length-of-longest-substring "a"))
          "Test 5: single char")
  (assert (= 2 (length-of-longest-substring "au"))
          "Test 6: two different")
  (assert (= 1 (length-of-longest-substring "aa"))
          "Test 7: two same")
  (assert (= 1 (length-of-longest-substring " "))
          "Test 8: space")

  ;; Tricky cases
  (assert (= 3 (length-of-longest-substring "dvdf"))
          "Test 9: dvdf")
  (assert (= 2 (length-of-longest-substring "aab"))
          "Test 10: aab")
  (assert (= 5 (length-of-longest-substring "tmmzuxt"))
          "Test 11: tmmzuxt")

  ;; Test other implementations
  (assert (= 3 (length-of-longest-substring-optimized "abcabcbb"))
          "Test 12: optimized version")
  (assert (= 3 (length-of-longest-substring-reduce "abcabcbb"))
          "Test 13: reduce version")
  (assert (= 3 (length-of-longest-substring-freq "abcabcbb"))
          "Test 14: freq version")

  ;; Test returning actual substring
  (assert (= "abc" (longest-unique-substr "abcabcbb"))
          "Test 15: return substring")

  ;; Test k-distinct variation
  (assert (= 3 (longest-k-distinct "eceba" 2))
          "Test 16: k-distinct")
  (assert (= 2 (longest-k-distinct "aa" 1))
          "Test 17: k-distinct single char")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. The sliding window pattern is perfect for substring problems
;;    - Maintain a window with some property
;;    - Expand when valid, shrink when invalid
;;
;; 2. Use a set for "contains unique elements" checks
;;    Use a map for "position of last occurrence" lookups
;;
;; 3. Optimization: Instead of shrinking one-by-one, jump directly
;;    to the position after the duplicate
;;
;; 4. State management in Clojure: pack state into a vector
;;    [max-len left char-idx] and use reduce or loop/recur
;;
;; 5. `fnil` is handy for update operations with defaults
;;
;; 6. This pattern extends to many problems:
;;    - Longest substring with k distinct characters
;;    - Minimum window substring
;;    - Longest repeating character replacement
;;
;; NEXT: medium_03_product_except_self.clj
