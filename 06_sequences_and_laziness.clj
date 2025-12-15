;; ============================================
;; LESSON 06: SEQUENCES AND LAZY EVALUATION
;; ============================================
;; Sequences are one of Clojure's most powerful
;; abstractions. Learn how they work and why
;; laziness is a superpower!

;; ============================================
;; 1. WHAT IS A SEQUENCE?
;; ============================================

;; A sequence (seq) is a logical list - an abstraction
;; over any sequential data structure.

;; The key operations on any seq:
;; - first: get the first element
;; - rest:  get everything except the first
;; - cons:  construct a new seq by adding to front

;; Many things can be viewed as sequences:
(seq [1 2 3])           ;; => (1 2 3)
(seq '(1 2 3))          ;; => (1 2 3)
(seq #{1 2 3})          ;; => (1 3 2) - order not guaranteed
(seq {:a 1 :b 2})       ;; => ([:a 1] [:b 2])
(seq "hello")           ;; => (\h \e \l \l \o)
(seq (range 5))         ;; => (0 1 2 3 4)

;; Empty collections return nil
(seq [])                ;; => nil
(seq "")                ;; => nil

;; This is useful for conditionals!
(if (seq [1 2 3])
  "has elements"
  "empty")              ;; => "has elements"

(if (seq [])
  "has elements"
  "empty")              ;; => "empty"

;; ============================================
;; 2. SEQUENCE OPERATIONS
;; ============================================

;; first - get the first element
(first [1 2 3])         ;; => 1
(first "hello")         ;; => \h
(first {:a 1 :b 2})     ;; => [:a 1]
(first [])              ;; => nil

;; rest - get all but first (returns empty seq, not nil)
(rest [1 2 3])          ;; => (2 3)
(rest [1])              ;; => ()
(rest [])               ;; => ()

;; next - like rest but returns nil instead of empty seq
(next [1 2 3])          ;; => (2 3)
(next [1])              ;; => nil
(next [])               ;; => nil

;; second, last
(second [1 2 3 4])      ;; => 2
(last [1 2 3 4])        ;; => 4

;; nth - get by index (0-based)
(nth [10 20 30] 1)      ;; => 20

;; take - get first n elements
(take 3 [1 2 3 4 5])    ;; => (1 2 3)
(take 10 [1 2 3])       ;; => (1 2 3) - doesn't error

;; drop - skip first n elements
(drop 2 [1 2 3 4 5])    ;; => (3 4 5)

;; take-while / drop-while - based on predicate
(take-while pos? [2 1 0 -1 2])  ;; => (2 1)
(drop-while pos? [2 1 0 -1 2])  ;; => (0 -1 2)

;; ============================================
;; 3. CREATING SEQUENCES
;; ============================================

;; range - numbers from start to end
(range 5)               ;; => (0 1 2 3 4)
(range 1 6)             ;; => (1 2 3 4 5)
(range 0 10 2)          ;; => (0 2 4 6 8) - with step
(range 10 0 -1)         ;; => (10 9 8 7 6 5 4 3 2 1) - descending

;; repeat - repeat a value
(repeat 5 "hello")      ;; => ("hello" "hello" "hello" "hello" "hello")
(take 3 (repeat "x"))   ;; => ("x" "x" "x") - infinite without count!

;; repeatedly - call function repeatedly
(repeatedly 3 #(rand-int 100))  ;; => (42 17 88) - 3 random numbers

;; cycle - repeat a collection infinitely
(take 7 (cycle [1 2 3]))  ;; => (1 2 3 1 2 3 1)

;; iterate - apply function repeatedly
(take 5 (iterate inc 0))        ;; => (0 1 2 3 4)
(take 5 (iterate #(* 2 %) 1))   ;; => (1 2 4 8 16)

;; concat - join sequences
(concat [1 2] [3 4] [5 6])      ;; => (1 2 3 4 5 6)

;; interleave - alternate between sequences
(interleave [:a :b :c] [1 2 3]) ;; => (:a 1 :b 2 :c 3)

;; interpose - insert separator between elements
(interpose :sep [1 2 3])        ;; => (1 :sep 2 :sep 3)

;; ============================================
;; 4. TRANSFORMING SEQUENCES
;; ============================================

;; map - transform each element
(map inc [1 2 3])               ;; => (2 3 4)
(map str [1 2 3])               ;; => ("1" "2" "3")
(map #(* % %) [1 2 3 4])        ;; => (1 4 9 16)

;; map with multiple collections
(map + [1 2 3] [10 20 30])      ;; => (11 22 33)
(map vector [:a :b :c] [1 2 3]) ;; => ([:a 1] [:b 2] [:c 3])

;; filter - keep elements matching predicate
(filter even? [1 2 3 4 5 6])    ;; => (2 4 6)
(filter pos? [-2 -1 0 1 2])     ;; => (1 2)

;; remove - opposite of filter
(remove even? [1 2 3 4 5 6])    ;; => (1 3 5)

;; keep - map + remove nils
(keep #(when (even? %) (* % %)) [1 2 3 4 5])
;; => (4 16) - only squares of even numbers

;; mapcat - map then concatenate results (flatMap)
(mapcat #(repeat 3 %) [:a :b])  ;; => (:a :a :a :b :b :b)
(mapcat range [1 2 3])          ;; => (0 0 1 0 1 2)

;; flatten - completely flatten nested structure
(flatten [[1 2] [3 [4 5]]])     ;; => (1 2 3 4 5)

;; distinct - remove duplicates
(distinct [1 2 1 3 2 4 3])      ;; => (1 2 3 4)

;; sort - sort elements
(sort [3 1 4 1 5 9])            ;; => (1 1 3 4 5 9)
(sort > [3 1 4 1 5 9])          ;; => (9 5 4 3 1 1)
(sort-by count ["aaa" "b" "cc"]) ;; => ("b" "cc" "aaa")

;; reverse - reverse a sequence
(reverse [1 2 3 4])             ;; => (4 3 2 1)

;; ============================================
;; 5. LAZY SEQUENCES
;; ============================================

;; Most sequence operations return LAZY sequences.
;; Elements are computed only when needed!

;; This is an infinite sequence (won't crash!)
(def natural-numbers (iterate inc 0))

;; Only computed when we ask for values
(take 10 natural-numbers)       ;; => (0 1 2 3 4 5 6 7 8 9)

;; Why laziness matters:
;; 1. Work with infinite data structures
;; 2. Only compute what you need
;; 3. Chain operations efficiently

;; Example: Find the first 5 prime numbers
(defn prime? [n]
  (and (> n 1)
       (not-any? #(zero? (mod n %))
                 (range 2 (inc (Math/sqrt n))))))

(take 5 (filter prime? (iterate inc 2)))
;; => (2 3 5 7 11)

;; Without laziness, this would try to check ALL numbers first!

;; Demonstrating laziness with side effects
(defn noisy-inc [x]
  (println "Computing inc of" x)
  (inc x))

(def lazy-seq (map noisy-inc [1 2 3 4 5]))
;; Nothing printed yet!

(first lazy-seq)
;; Prints "Computing inc of 1", returns 2

(take 2 lazy-seq)
;; May print more depending on chunking

;; ============================================
;; 6. FORCING EVALUATION (REALIZING SEQS)
;; ============================================

;; Sometimes you need to force evaluation

;; doall - force evaluation, return lazy seq
(doall (map println [1 2 3]))
;; Prints 1, 2, 3 and returns (nil nil nil)

;; dorun - force evaluation, return nil (for side effects)
(dorun (map println [1 2 3]))
;; Prints 1, 2, 3, returns nil

;; into - pour into a concrete collection
(into [] (map inc [1 2 3]))     ;; => [2 3 4]
(into #{} (map inc [1 2 3]))    ;; => #{4 3 2}

;; vec, set - convert to vector/set
(vec (map inc [1 2 3]))         ;; => [2 3 4]

;; realized? - check if lazy seq has been realized
(def lazy-range (range 100))
(realized? lazy-range)          ;; => false
(doall lazy-range)
(realized? lazy-range)          ;; => true

;; ============================================
;; 7. CHUNKED SEQUENCES
;; ============================================

;; For efficiency, many lazy seqs are "chunked"
;; They process elements in groups of 32

(first (map #(do (print %) %) (range 100)))
;; Prints 0-31 (whole chunk), returns 0

;; This is usually fine, but be aware if side effects matter!

;; ============================================
;; 8. COMMON PATTERNS
;; ============================================

;; Transform and filter pipeline
(->> (range 1 20)
     (filter even?)
     (map #(* % %))
     (take 5))
;; => (4 16 36 64 100)

;; Partition - group into chunks
(partition 3 [1 2 3 4 5 6 7 8])
;; => ((1 2 3) (4 5 6))  - drops incomplete

(partition 3 3 [:pad] [1 2 3 4 5 6 7 8])
;; => ((1 2 3) (4 5 6) (7 8 :pad))  - with padding

(partition-all 3 [1 2 3 4 5 6 7 8])
;; => ((1 2 3) (4 5 6) (7 8))  - keeps incomplete

;; Partition-by - group by predicate changes
(partition-by even? [1 1 2 2 3 3 4])
;; => ((1 1) (2 2) (3 3) (4))

;; Group-by - group by function result
(group-by even? [1 2 3 4 5 6])
;; => {false [1 3 5], true [2 4 6]}

(group-by count ["a" "bb" "c" "ddd" "ee"])
;; => {1 ["a" "c"], 2 ["bb" "ee"], 3 ["ddd"]}

;; Frequencies - count occurrences
(frequencies [:a :b :a :c :a :b])
;; => {:a 3, :b 2, :c 1}

;; ============================================
;; 9. CREATING YOUR OWN LAZY SEQUENCES
;; ============================================

;; lazy-seq - the foundation of lazy sequences
(defn my-range [start end]
  (lazy-seq
    (when (< start end)
      (cons start (my-range (inc start) end)))))

(my-range 0 5)                  ;; => (0 1 2 3 4)

;; Infinite Fibonacci sequence
(defn fibs
  ([] (fibs 0 1))
  ([a b] (lazy-seq (cons a (fibs b (+ a b))))))

(take 10 (fibs))
;; => (0 1 1 2 3 5 8 13 21 34)

;; Lazy file reading (conceptual)
(defn lazy-file-lines [filename]
  (lazy-seq
    ;; Would actually use clojure.java.io
    ;; Each line read only when needed
    ))

;; ============================================
;; 10. SEQUENCE PREDICATES
;; ============================================

;; every? - all elements satisfy predicate
(every? even? [2 4 6 8])        ;; => true
(every? even? [2 4 5 8])        ;; => false

;; some - find first truthy result
(some even? [1 3 5 6 7])        ;; => true (found 6)
(some even? [1 3 5 7])          ;; => nil

;; Common idiom: some with set (membership test)
(some #{:a :b} [:x :y :a :z])   ;; => :a
(some #{:a :b} [:x :y :z])      ;; => nil

;; not-every? - at least one doesn't satisfy
(not-every? even? [2 4 5 6])    ;; => true

;; not-any? - none satisfy
(not-any? even? [1 3 5 7])      ;; => true

;; empty?
(empty? [])                     ;; => true
(empty? [1])                    ;; => false

;; ============================================
;; 11. REDUCE: THE UNIVERSAL TRANSFORMER
;; ============================================

;; reduce can implement almost any sequence operation!

;; Sum
(reduce + [1 2 3 4 5])          ;; => 15
(reduce + 0 [1 2 3 4 5])        ;; => 15 (with initial)

;; Product
(reduce * [1 2 3 4])            ;; => 24

;; Build a string
(reduce str ["a" "b" "c"])      ;; => "abc"

;; Find max
(reduce max [3 1 4 1 5 9])      ;; => 9

;; Implementing map with reduce
(defn my-map [f coll]
  (reduce (fn [acc x] (conj acc (f x)))
          []
          coll))

(my-map inc [1 2 3])            ;; => [2 3 4]

;; Implementing filter with reduce
(defn my-filter [pred coll]
  (reduce (fn [acc x]
            (if (pred x)
              (conj acc x)
              acc))
          []
          coll))

(my-filter even? [1 2 3 4 5])   ;; => [2 4]

;; ============================================
;; 12. REDUCTIONS
;; ============================================

;; reductions - like reduce but returns all intermediate steps
(reductions + [1 2 3 4 5])
;; => (1 3 6 10 15) - running totals

(reductions * [1 2 3 4])
;; => (1 2 6 24) - running products

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Create an infinite sequence of powers of 2
;; (1, 2, 4, 8, 16, ...) and take the first 10

;; Exercise 2: Write a function that returns the first n
;; Fibonacci numbers using lazy sequences

;; Exercise 3: Given a sequence of numbers, return only
;; the ones that are both positive and even

;; Exercise 4: Use reduce to find the longest string in a
;; collection of strings

;; Exercise 5: Create a function that takes a sequence and
;; returns a map of {element count} (implement frequencies)

;; Exercise 6: Write a function that takes n and returns
;; the sum of the first n prime numbers

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; 1. Sequences are a uniform interface over collections
;; 2. seq returns nil for empty collections (useful for conditionals)
;; 3. Most seq operations are lazy - computed on demand
;; 4. Laziness enables infinite sequences and efficiency
;; 5. Use doall/dorun when you need to force side effects
;; 6. reduce is the swiss army knife of sequence processing
;; 7. lazy-seq lets you create custom lazy sequences
;; 8. Predicates: every?, some, not-any?, not-every?

;; Next lesson: Higher-Order Functions and Functional Patterns
