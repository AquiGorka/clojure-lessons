;; ============================================================================
;; EASY 05: Valid Palindrome
;; ============================================================================
;; Source: LeetCode #125 - Valid Palindrome
;; Difficulty: Easy
;; Topics: String, Two Pointers
;;
;; A fundamental string problem that teaches character filtering and
;; the two-pointer technique. Clojure's sequence operations make this elegant.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; A phrase is a palindrome if, after converting all uppercase letters into
;; lowercase letters and removing all non-alphanumeric characters, it reads
;; the same forward and backward. Alphanumeric characters include letters
;; and numbers.
;;
;; Given a string `s`, return `true` if it is a palindrome, or `false` otherwise.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: s = "A man, a plan, a canal: Panama"
;;   Output: true
;;   Explanation: "amanaplanacanalpanama" is a palindrome.
;;
;; Example 2:
;;   Input: s = "race a car"
;;   Output: false
;;   Explanation: "raceacar" is not a palindrome.
;;
;; Example 3:
;;   Input: s = " "
;;   Output: true
;;   Explanation: s is an empty string "" after removing non-alphanumeric
;;                characters. Since an empty string reads the same forward
;;                and backward, it is a palindrome.

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= s.length <= 2 * 10^5
;; - s consists only of printable ASCII characters.

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; Steps to solve:
;; 1. Filter out non-alphanumeric characters
;; 2. Convert to lowercase
;; 3. Check if the result equals its reverse
;;
;; Or, use two pointers:
;; 1. Start from both ends
;; 2. Skip non-alphanumeric characters
;; 3. Compare characters (case-insensitive)
;; 4. Move pointers toward center

;; ============================================================================
;; HELPER FUNCTIONS
;; ============================================================================

(defn alphanumeric?
  "Check if a character is alphanumeric."
  [c]
  (or (Character/isLetter c)
      (Character/isDigit c)))

;; Alternative without Java interop:
(def alphanumeric-set
  (set (concat
        (map char (range (int \a) (inc (int \z))))
        (map char (range (int \A) (inc (int \Z))))
        (map char (range (int \0) (inc (int \9)))))))

(defn alphanumeric-v2?
  "Check if a character is alphanumeric (pure Clojure)."
  [c]
  (contains? alphanumeric-set c))

;; ============================================================================
;; SOLUTION 1: Simple and Idiomatic (Most Clojure-like)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(n)

(defn valid-palindrome?
  "Check if string is a valid palindrome (ignoring non-alphanumeric, case-insensitive)."
  [s]
  (let [cleaned (->> s
                     (filter alphanumeric?)
                     (map #(Character/toLowerCase %)))]
    (= cleaned (reverse cleaned))))

(valid-palindrome? "A man, a plan, a canal: Panama")
;; => true

(valid-palindrome? "race a car")
;; => false

(valid-palindrome? " ")
;; => true

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; Input: "A man, a plan, a canal: Panama"
;;
;; Step 1: Filter alphanumeric
;;   "AmanaplanacanalPanama"
;;
;; Step 2: Convert to lowercase
;;   "amanaplanacanalpanama"
;;
;; Step 3: Compare with reverse
;;   "amanaplanacanalpanama" == "amanaplanacanalpanama" ✓
;;
;; Result: true

;; ============================================================================
;; SOLUTION 2: Two Pointers (More Efficient Space)
;; ============================================================================
;; Time Complexity: O(n)
;; Space Complexity: O(1) - no extra string created
;;
;; This avoids creating a new string by comparing in place.

(defn valid-palindrome-two-pointers?
  "Check palindrome using two pointers."
  [s]
  (let [s (vec s)  ; Convert to vector for O(1) indexed access
        n (count s)]
    (loop [left 0
           right (dec n)]
      (cond
        ;; Pointers crossed - it's a palindrome!
        (>= left right)
        true

        ;; Skip non-alphanumeric on left
        (not (alphanumeric? (s left)))
        (recur (inc left) right)

        ;; Skip non-alphanumeric on right
        (not (alphanumeric? (s right)))
        (recur left (dec right))

        ;; Compare characters (case-insensitive)
        (= (Character/toLowerCase (s left))
           (Character/toLowerCase (s right)))
        (recur (inc left) (dec right))

        ;; Mismatch found
        :else
        false))))

(valid-palindrome-two-pointers? "A man, a plan, a canal: Panama")
;; => true

(valid-palindrome-two-pointers? "race a car")
;; => false

;; ============================================================================
;; SOLUTION 3: Using reduce
;; ============================================================================

(defn valid-palindrome-reduce?
  "Using reduce to build and check simultaneously."
  [s]
  (let [cleaned (->> s
                     (filter alphanumeric?)
                     (map #(Character/toLowerCase %))
                     vec)
        n (count cleaned)
        half (quot n 2)]
    (every? true?
            (map =
                 (take half cleaned)
                 (take half (reverse cleaned))))))

(valid-palindrome-reduce? "A man, a plan, a canal: Panama")
;; => true

;; ============================================================================
;; SOLUTION 4: Pure Clojure (No Java Interop)
;; ============================================================================

(require '[clojure.string :as str])

(defn valid-palindrome-pure?
  "Pure Clojure solution without Java interop."
  [s]
  (let [cleaned (str/lower-case (str/replace s #"[^a-zA-Z0-9]" ""))]
    (= cleaned (str/reverse cleaned))))

(valid-palindrome-pure? "A man, a plan, a canal: Panama")
;; => true

;; ============================================================================
;; SOLUTION 5: One-liner
;; ============================================================================

(defn palindrome? [s]
  (let [c (filter #(Character/isLetterOrDigit %) (str/lower-case s))]
    (= c (reverse c))))

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. Strings are sequences of characters
;;    You can use sequence functions directly on strings
(seq "hello")
;; => (\h \e \l \l \o)

(filter #(not= % \l) "hello")
;; => (\h \e \o)

;; 2. Java interop for character operations
;;    Clojure runs on JVM, so Java methods are available
(Character/isLetter \a)      ;; => true
(Character/isDigit \5)       ;; => true
(Character/toLowerCase \A)   ;; => \a
(Character/isLetterOrDigit \!) ;; => false

;; 3. clojure.string namespace
;;    Provides string-specific functions
(str/lower-case "Hello")     ;; => "hello"
(str/upper-case "Hello")     ;; => "HELLO"
(str/reverse "Hello")        ;; => "olleH"
(str/replace "Hello" #"l" "L") ;; => "HeLLo"

;; 4. Regular expressions
;;    #"pattern" is a regex literal
(re-seq #"[a-z]" "a1b2c3")   ;; => ("a" "b" "c")
(str/replace "a1b2" #"[0-9]" "") ;; => "ab"

;; 5. Threading macros for readability
;;    ->> threads through as last argument
(->> "Hello, World!"
     str/lower-case
     (filter #(Character/isLetter %))
     (apply str))
;; => "helloworld"

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(valid-palindrome? "")
;; => true (empty is palindrome by definition)

(valid-palindrome? " ")
;; => true (spaces are filtered out)

(valid-palindrome? "a")
;; => true (single character)

(valid-palindrome? ".,")
;; => true (no alphanumeric chars -> empty -> palindrome)

(valid-palindrome? "aa")
;; => true

(valid-palindrome? "ab")
;; => false

(valid-palindrome? "Aa")
;; => true (case-insensitive)

(valid-palindrome? "0P")
;; => false (0 != P)

;; Numbers are alphanumeric!
(valid-palindrome? "12321")
;; => true

(valid-palindrome? "A1B2B1A")
;; => true

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Solution 1 (Simple):
;;   Time:  O(n) - filter, map, reverse all O(n)
;;   Space: O(n) - creates new sequence of filtered chars
;;
;; Solution 2 (Two Pointers):
;;   Time:  O(n) - single pass through string
;;   Space: O(n) for vec conversion, O(1) if using String charAt
;;
;; Solution 4 (Regex):
;;   Time:  O(n) - regex replace and reverse
;;   Space: O(n) - creates new strings
;;
;; All solutions are O(n) time. The two-pointer approach can be O(1) space
;; in languages with O(1) string indexing, but in Clojure we typically
;; convert to a vector first.

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Forgetting that numbers are alphanumeric
;;    "a1b2b1a" should be true

;; 2. Not handling empty/whitespace-only strings
;;    These should return true

;; 3. Case sensitivity
;;    "Aa" should be true

;; 4. Using (reverse s) directly on a string
;;    This returns a seq, not a string!
(reverse "hello")  ;; => (\o \l \l \e \h)
;; Compare sequences, or use str/reverse for strings

;; 5. Confusing = and .equals for string comparison
;;    In Clojure, = works correctly for value equality

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Check if palindrome by removing at most one character
(defn valid-palindrome-ii?
  "Can become palindrome by removing at most one character."
  [s]
  (let [is-palindrome? (fn [chars]
                         (= chars (reverse chars)))
        chars (vec (map #(Character/toLowerCase %)
                        (filter alphanumeric? s)))
        n (count chars)]
    (loop [left 0
           right (dec n)]
      (cond
        (>= left right) true

        (= (chars left) (chars right))
        (recur (inc left) (dec right))

        :else
        ;; Try removing left or right character
        (or (is-palindrome? (concat (subvec chars 0 left)
                                    (subvec chars (inc left) (inc right))))
            (is-palindrome? (concat (subvec chars left right))))))))

(valid-palindrome-ii? "abca")
;; => true (remove 'c' or 'b')

(valid-palindrome-ii? "abc")
;; => false

;; Variation 2: Longest Palindromic Prefix
(defn longest-palindrome-prefix
  "Find the longest palindromic prefix of the string."
  [s]
  (let [cleaned (->> s
                     (filter alphanumeric?)
                     (map #(Character/toLowerCase %))
                     vec)]
    (loop [end (count cleaned)]
      (if (<= end 0)
        ""
        (let [prefix (subvec cleaned 0 end)]
          (if (= prefix (vec (reverse prefix)))
            (apply str prefix)
            (recur (dec end))))))))

(longest-palindrome-prefix "abacaba123")
;; => "abacaba"

;; Variation 3: Count palindromic substrings
(defn count-palindromic-substrings
  "Count all palindromic substrings."
  [s]
  (let [s (vec s)
        n (count s)
        expand-around-center
        (fn [left right]
          (loop [l left r right count 0]
            (if (and (>= l 0) (< r n) (= (s l) (s r)))
              (recur (dec l) (inc r) (inc count))
              count)))]
    (reduce +
            (for [i (range n)]
              (+ (expand-around-center i i)       ; Odd length
                 (expand-around-center i (inc i))))))) ; Even length

(count-palindromic-substrings "aaa")
;; => 6 ("a" at 0, "a" at 1, "a" at 2, "aa" at 0-1, "aa" at 1-2, "aaa")

;; Variation 4: Make palindrome by adding characters
(defn min-chars-to-palindrome
  "Minimum characters to add at end to make palindrome."
  [s]
  (let [s (vec s)
        n (count s)]
    (loop [i 0]
      (if (= (subvec s i) (vec (reverse (subvec s i))))
        i
        (recur (inc i))))))

(min-chars-to-palindrome "abc")
;; => 2 (add "ba" to get "abcba")

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Palindrome Permutation
;; Given a string, determine if a permutation of it could form a palindrome.
;; Hint: At most one character can have an odd count.

;; Exercise 2: Shortest Palindrome
;; Find the shortest palindrome by adding characters to the front.
;; Example: "aacecaaa" -> "aaacecaaa"

;; Exercise 3: Palindrome Pairs
;; Given a list of unique words, find all pairs (i, j) such that
;; words[i] + words[j] is a palindrome.

;; Exercise 4: Break Palindrome
;; Given a palindrome, replace exactly one character to make it
;; NOT a palindrome and lexicographically smallest.

;; ============================================================================
;; TESTING
;; ============================================================================

(defn run-tests []
  (println "Testing valid-palindrome?...")

  ;; Basic tests
  (assert (= true (valid-palindrome? "A man, a plan, a canal: Panama"))
          "Test 1: Classic palindrome")
  (assert (= false (valid-palindrome? "race a car"))
          "Test 2: Not a palindrome")
  (assert (= true (valid-palindrome? " "))
          "Test 3: Empty after filtering")

  ;; Edge cases
  (assert (= true (valid-palindrome? ""))
          "Test 4: Empty string")
  (assert (= true (valid-palindrome? "a"))
          "Test 5: Single char")
  (assert (= true (valid-palindrome? ".,!@#"))
          "Test 6: No alphanumeric")

  ;; Case sensitivity
  (assert (= true (valid-palindrome? "Aa"))
          "Test 7: Case insensitive")
  (assert (= true (valid-palindrome? "AbBa"))
          "Test 8: Mixed case")

  ;; Numbers
  (assert (= true (valid-palindrome? "12321"))
          "Test 9: Numeric palindrome")
  (assert (= true (valid-palindrome? "A1b2B1a"))
          "Test 10: Alphanumeric mix")
  (assert (= false (valid-palindrome? "0P"))
          "Test 11: Zero vs letter")

  ;; Complex cases
  (assert (= true (valid-palindrome? "Was it a car or a cat I saw?"))
          "Test 12: Another classic")
  (assert (= false (valid-palindrome? "hello world"))
          "Test 13: Not palindrome")

  ;; Test other implementations
  (assert (= true (valid-palindrome-two-pointers? "A man, a plan, a canal: Panama"))
          "Test 14: Two pointers version")
  (assert (= true (valid-palindrome-pure? "A man, a plan, a canal: Panama"))
          "Test 15: Pure Clojure version")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. Strings are sequences in Clojure - use filter, map, etc. directly
;;
;; 2. Java interop provides Character class methods for char operations
;;
;; 3. clojure.string namespace has string-specific functions
;;
;; 4. The simple "filter, lowercase, compare to reverse" approach is idiomatic
;;
;; 5. Two-pointer technique works but is less "Clojure-like"
;;
;; 6. Regex with str/replace is a powerful alternative
;;
;; 7. Empty strings and strings with no alphanumeric chars are palindromes
;;
;; NEXT: easy_06_single_number.clj
