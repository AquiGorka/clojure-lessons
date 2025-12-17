;; ============================================================================
;; MEDIUM 01: Group Anagrams
;; ============================================================================
;; Source: LeetCode #49 - Group Anagrams
;; Difficulty: Medium
;; Topics: Array, Hash Table, String, Sorting
;;
;; A classic problem that demonstrates the power of group-by and shows how
;; to use sorted characters or frequency counts as canonical keys.

;; ============================================================================
;; PROBLEM STATEMENT
;; ============================================================================
;;
;; Given an array of strings strs, group the anagrams together. You can return
;; the answer in any order.
;;
;; An Anagram is a word or phrase formed by rearranging the letters of a
;; different word or phrase, typically using all the original letters exactly
;; once.

;; ============================================================================
;; EXAMPLES
;; ============================================================================
;;
;; Example 1:
;;   Input: strs = ["eat", "tea", "tan", "ate", "nat", "bat"]
;;   Output: [["bat"], ["nat", "tan"], ["ate", "eat", "tea"]]
;;
;; Example 2:
;;   Input: strs = [""]
;;   Output: [[""]]
;;
;; Example 3:
;;   Input: strs = ["a"]
;;   Output: [["a"]]

;; ============================================================================
;; CONSTRAINTS
;; ============================================================================
;;
;; - 1 <= strs.length <= 10^4
;; - 0 <= strs[i].length <= 100
;; - strs[i] consists of lowercase English letters.

;; ============================================================================
;; UNDERSTANDING THE PROBLEM
;; ============================================================================
;;
;; Key insight: Two strings are anagrams if and only if:
;; 1. They have the same characters when sorted, OR
;; 2. They have the same character frequency count
;;
;; We need to group strings by their "canonical form" - either sorted
;; characters or frequency signature.

;; ============================================================================
;; SOLUTION 1: Using group-by with sorted key (Most Idiomatic)
;; ============================================================================
;; Time Complexity: O(n * k log k) where n = number of strings, k = max string length
;; Space Complexity: O(n * k)

(defn group-anagrams
  "Group anagrams using sorted characters as key."
  [strs]
  (vec (vals (group-by #(apply str (sort %)) strs))))

(group-anagrams ["eat" "tea" "tan" "ate" "nat" "bat"])
;; => [["eat" "tea" "ate"] ["tan" "nat"] ["bat"]]

(group-anagrams [""])
;; => [[""]]

(group-anagrams ["a"])
;; => [["a"]]

;; ============================================================================
;; DETAILED TRACE
;; ============================================================================
;;
;; Input: ["eat" "tea" "tan" "ate" "nat" "bat"]
;;
;; Step 1: For each string, compute sorted key
;;   "eat" -> (sort "eat") -> (\a \e \t) -> "aet"
;;   "tea" -> (sort "tea") -> (\a \e \t) -> "aet"
;;   "tan" -> (sort "tan") -> (\a \n \t) -> "ant"
;;   "ate" -> (sort "ate") -> (\a \e \t) -> "aet"
;;   "nat" -> (sort "nat") -> (\a \n \t) -> "ant"
;;   "bat" -> (sort "bat") -> (\a \b \t) -> "abt"
;;
;; Step 2: group-by creates map
;;   {"aet" ["eat" "tea" "ate"],
;;    "ant" ["tan" "nat"],
;;    "abt" ["bat"]}
;;
;; Step 3: vals extracts the groups
;;   [["eat" "tea" "ate"] ["tan" "nat"] ["bat"]]

;; ============================================================================
;; SOLUTION 2: Using frequencies as key (O(n * k) time)
;; ============================================================================
;; This is faster for long strings because we avoid sorting
;; Time Complexity: O(n * k)
;; Space Complexity: O(n * k)

(defn group-anagrams-freq
  "Group anagrams using character frequencies as key."
  [strs]
  (vec (vals (group-by frequencies strs))))

(group-anagrams-freq ["eat" "tea" "tan" "ate" "nat" "bat"])
;; => [["eat" "tea" "ate"] ["tan" "nat"] ["bat"]]

;; Note: frequencies returns a map like {\e 1, \a 1, \t 1}
;; Clojure maps with same content are equal, so this works as a key

;; ============================================================================
;; SOLUTION 3: Using reduce to build groups
;; ============================================================================

(defn group-anagrams-reduce
  "Group anagrams using reduce."
  [strs]
  (vec (vals
        (reduce (fn [groups s]
                  (let [key (apply str (sort s))]
                    (update groups key (fnil conj []) s)))
                {}
                strs))))

(group-anagrams-reduce ["eat" "tea" "tan" "ate" "nat" "bat"])
;; => [["eat" "tea" "ate"] ["tan" "nat"] ["bat"]]

;; ============================================================================
;; SOLUTION 4: Using character count array as key
;; ============================================================================
;; Create a fixed-size representation of character counts

(defn char-count-key
  "Create a vector of 26 character counts."
  [s]
  (let [counts (vec (repeat 26 0))]
    (reduce (fn [c ch]
              (update c (- (int ch) (int \a)) inc))
            counts
            s)))

(defn group-anagrams-count
  "Group anagrams using character count arrays."
  [strs]
  (vec (vals (group-by char-count-key strs))))

(group-anagrams-count ["eat" "tea" "tan" "ate" "nat" "bat"])
;; => [["eat" "tea" "ate"] ["tan" "nat"] ["bat"]]

;; ============================================================================
;; SOLUTION 5: Prime number multiplication (mathematical)
;; ============================================================================
;; Assign each letter a unique prime, multiply them together
;; Same product = anagram (Fundamental Theorem of Arithmetic)

(def char-primes
  "Map each letter to a unique prime number."
  (zipmap "abcdefghijklmnopqrstuvwxyz"
          [2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97 101]))

(defn prime-key
  "Calculate product of prime numbers for each character."
  [s]
  (reduce * 1 (map char-primes s)))

(defn group-anagrams-prime
  "Group anagrams using prime number products."
  [strs]
  (vec (vals (group-by prime-key strs))))

(group-anagrams-prime ["eat" "tea" "tan" "ate" "nat" "bat"])
;; => [["eat" "tea" "ate"] ["tan" "nat"] ["bat"]]

;; Note: This can overflow for long strings!

;; ============================================================================
;; CLOJURE-SPECIFIC INSIGHTS
;; ============================================================================

;; 1. `group-by` is incredibly powerful
(group-by even? [1 2 3 4 5 6])
;; => {false [1 3 5], true [2 4 6]}

(group-by count ["a" "bb" "ccc" "dd"])
;; => {1 ["a"], 2 ["bb" "dd"], 3 ["ccc"]}

;; 2. `sort` on a string returns a sequence of characters
(sort "eat")       ;; => (\a \e \t)
(apply str (sort "eat"))  ;; => "aet"

;; 3. `frequencies` returns a map of item -> count
(frequencies "hello")
;; => {\h 1, \e 1, \l 2, \o 1}

;; 4. Maps are equal if they have same key-value pairs
(= {\a 1 \b 2} {\b 2 \a 1})  ;; => true

;; 5. `vals` extracts values from a map
(vals {:a 1 :b 2 :c 3})  ;; => (1 2 3)

;; 6. `fnil` provides default value for nil
((fnil conj []) nil "item")  ;; => ["item"]
((fnil conj []) ["existing"] "item")  ;; => ["existing" "item"]

;; ============================================================================
;; EDGE CASES
;; ============================================================================

(group-anagrams [""])
;; => [[""]] (empty string)

(group-anagrams ["a"])
;; => [["a"]] (single character)

(group-anagrams ["abc" "def" "ghi"])
;; => [["abc"] ["def"] ["ghi"]] (no anagrams)

(group-anagrams ["aaa" "aaa" "aaa"])
;; => [["aaa" "aaa" "aaa"]] (all same)

(group-anagrams [])
;; => [] (empty input)

;; ============================================================================
;; COMPLEXITY ANALYSIS
;; ============================================================================
;;
;; Sorted Key (Solution 1):
;;   Time:  O(n * k log k) - sorting each string
;;   Space: O(n * k) - storing all strings in groups
;;
;; Frequencies Key (Solution 2):
;;   Time:  O(n * k) - counting characters is linear
;;   Space: O(n * k)
;;
;; Character Count Array (Solution 4):
;;   Time:  O(n * k) - counting is O(k), lookup is O(26) = O(1)
;;   Space: O(n * k)
;;
;; Prime Product (Solution 5):
;;   Time:  O(n * k) - multiply k primes
;;   Space: O(n * k)
;;   Caveat: Potential integer overflow for long strings
;;
;; For typical inputs, Solution 1 (sorted) is clearest.
;; For very long strings, Solutions 2-4 are faster.

;; ============================================================================
;; COMMON MISTAKES
;; ============================================================================

;; 1. Forgetting to convert sorted result back to string
;;    (sort "eat") returns (\a \e \t), not "aet"

;; 2. Not handling empty strings
;;    Empty string is a valid anagram of itself

;; 3. Using list instead of vector for groups
;;    group-by creates vectors by default, which is fine

;; 4. Prime overflow for long strings
;;    Use BigInteger or frequencies approach for long strings

;; ============================================================================
;; VARIATIONS AND FOLLOW-UPS
;; ============================================================================

;; Variation 1: Check if two strings are anagrams
(defn anagram?
  "Check if two strings are anagrams of each other."
  [s1 s2]
  (= (frequencies s1) (frequencies s2)))

(anagram? "listen" "silent")  ;; => true
(anagram? "hello" "world")    ;; => false

;; Variation 2: Find all anagram pairs
(defn anagram-pairs
  "Find all pairs of strings that are anagrams."
  [strs]
  (let [groups (group-anagrams strs)]
    (mapcat (fn [group]
              (for [i (range (count group))
                    j (range (inc i) (count group))]
                [(nth group i) (nth group j)]))
            (filter #(> (count %) 1) groups))))

(anagram-pairs ["eat" "tea" "tan" "ate" "nat" "bat"])
;; => (["eat" "tea"] ["eat" "ate"] ["tea" "ate"] ["tan" "nat"])

;; Variation 3: Count anagram groups
(defn count-anagram-groups
  "Count the number of anagram groups."
  [strs]
  (count (group-anagrams strs)))

(count-anagram-groups ["eat" "tea" "tan" "ate" "nat" "bat"])
;; => 3

;; Variation 4: Largest anagram group
(defn largest-anagram-group
  "Find the largest group of anagrams."
  [strs]
  (apply max-key count (group-anagrams strs)))

(largest-anagram-group ["eat" "tea" "tan" "ate" "nat" "bat"])
;; => ["eat" "tea" "ate"]

;; Variation 5: Group by anagram with custom comparator
(defn group-anagrams-case-insensitive
  "Group anagrams ignoring case."
  [strs]
  (vec (vals (group-by #(apply str (sort (clojure.string/lower-case %))) strs))))

(group-anagrams-case-insensitive ["Eat" "TEA" "tan" "ATE" "nat" "bat"])
;; => [["Eat" "TEA" "ATE"] ["tan" "nat"] ["bat"]]

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; Exercise 1: Valid Anagram (LeetCode #242)
;; Given two strings s and t, return true if t is an anagram of s.

;; Exercise 2: Find All Anagrams in a String (LeetCode #438)
;; Find all start indices of p's anagrams in s.

;; Exercise 3: Minimum Number of Steps to Make Two Strings Anagram
;; Find minimum steps to make t an anagram of s.

;; Exercise 4: Group Shifted Strings
;; Group strings that are "shifts" of each other (abc -> bcd -> cde).

;; ============================================================================
;; TESTING
;; ============================================================================

(defn sets-equal?
  "Compare two collections of collections as sets of sets."
  [a b]
  (= (set (map set a)) (set (map set b))))

(defn run-tests []
  (println "Testing group-anagrams...")

  ;; Basic tests
  (assert (sets-equal?
           [["bat"] ["nat" "tan"] ["ate" "eat" "tea"]]
           (group-anagrams ["eat" "tea" "tan" "ate" "nat" "bat"]))
          "Test 1: Basic case")

  (assert (sets-equal? [[""]] (group-anagrams [""]))
          "Test 2: Empty string")

  (assert (sets-equal? [["a"]] (group-anagrams ["a"]))
          "Test 3: Single character")

  ;; Edge cases
  (assert (= [] (group-anagrams []))
          "Test 4: Empty input")

  (assert (sets-equal?
           [["abc"] ["def"] ["ghi"]]
           (group-anagrams ["abc" "def" "ghi"]))
          "Test 5: No anagrams")

  (assert (sets-equal?
           [["aaa" "aaa" "aaa"]]
           (group-anagrams ["aaa" "aaa" "aaa"]))
          "Test 6: All same")

  ;; Test other implementations
  (assert (sets-equal?
           [["bat"] ["nat" "tan"] ["ate" "eat" "tea"]]
           (group-anagrams-freq ["eat" "tea" "tan" "ate" "nat" "bat"]))
          "Test 7: frequencies version")

  (assert (sets-equal?
           [["bat"] ["nat" "tan"] ["ate" "eat" "tea"]]
           (group-anagrams-reduce ["eat" "tea" "tan" "ate" "nat" "bat"]))
          "Test 8: reduce version")

  (assert (sets-equal?
           [["bat"] ["nat" "tan"] ["ate" "eat" "tea"]]
           (group-anagrams-count ["eat" "tea" "tan" "ate" "nat" "bat"]))
          "Test 9: count version")

  (assert (sets-equal?
           [["bat"] ["nat" "tan"] ["ate" "eat" "tea"]]
           (group-anagrams-prime ["eat" "tea" "tan" "ate" "nat" "bat"]))
          "Test 10: prime version")

  ;; Helper function tests
  (assert (= true (anagram? "listen" "silent"))
          "Test 11: anagram check true")
  (assert (= false (anagram? "hello" "world"))
          "Test 12: anagram check false")

  (println "All tests passed!"))

(run-tests)

;; ============================================================================
;; KEY TAKEAWAYS
;; ============================================================================
;;
;; 1. `group-by` is the perfect tool for grouping problems
;;    (group-by key-fn collection) -> {key [matching-items]}
;;
;; 2. Two canonical forms for anagrams:
;;    - Sorted characters: (apply str (sort s))
;;    - Frequency map: (frequencies s)
;;
;; 3. `frequencies` is more efficient for long strings (O(k) vs O(k log k))
;;
;; 4. Maps with same content are equal in Clojure
;;    This lets us use frequency maps directly as keys
;;
;; 5. `vals` extracts all values from a map
;;
;; 6. `fnil` provides defaults for nil values in update functions
;;
;; NEXT: medium_02_longest_substring.clj
