;; ============================================================================
;; LESSON 03: Collections
;; ============================================================================
;; Clojure has four primary collection types, all of which are:
;; - Immutable (changes create new collections)
;; - Persistent (structural sharing for efficiency)
;; - Support common sequence operations
;; ============================================================================

;; ============================================================================
;; 1. LISTS - Linked lists, optimized for sequential access
;; ============================================================================

;; Creating lists (note the quote ' to prevent evaluation)
'(1 2 3 4 5)                    ; => (1 2 3 4 5)
(list 1 2 3 4 5)                ; => (1 2 3 4 5) - using list function

;; Lists are evaluated as function calls, so we need to quote them
;; '(1 2 3) prevents Clojure from trying to call 1 as a function

;; Adding to lists - conj adds to the FRONT (most efficient for linked lists)
(conj '(2 3 4) 1)               ; => (1 2 3 4)

;; First element
(first '(1 2 3 4))              ; => 1

;; Rest of the list (everything except first)
(rest '(1 2 3 4))               ; => (2 3 4)

;; Peek and pop for stack-like behavior
(peek '(1 2 3))                 ; => 1 (like first for lists)
(pop '(1 2 3))                  ; => (2 3) (like rest for lists)

;; ============================================================================
;; 2. VECTORS - Indexed collections, optimized for random access
;; ============================================================================

;; Creating vectors
[1 2 3 4 5]                     ; => [1 2 3 4 5]
(vector 1 2 3 4 5)              ; => [1 2 3 4 5]
(vec '(1 2 3 4 5))              ; => [1 2 3 4 5] - convert list to vector

;; Vectors are the most commonly used collection in Clojure!

;; Adding to vectors - conj adds to the END (most efficient for vectors)
(conj [1 2 3] 4)                ; => [1 2 3 4]

;; Accessing by index (vectors are functions of their indices!)
([10 20 30 40] 2)               ; => 30 (index 2)
(get [10 20 30 40] 2)           ; => 30 (same thing)
(get [10 20 30] 10)             ; => nil (out of bounds)
(get [10 20 30] 10 :not-found)  ; => :not-found (default value)

;; nth - like get but throws exception on out of bounds
(nth [10 20 30] 1)              ; => 20
;; (nth [10 20 30] 10)          ; => ERROR!

;; First and rest work on vectors too
(first [1 2 3])                 ; => 1
(rest [1 2 3])                  ; => (2 3) - returns a sequence, not vector!

;; Last element
(last [1 2 3 4])                ; => 4

;; Peek and pop for vectors (from the end)
(peek [1 2 3])                  ; => 3 (last element)
(pop [1 2 3])                   ; => [1 2] (all but last)

;; Update a value at an index
(assoc [10 20 30] 1 99)         ; => [10 99 30]

;; Subvectors
(subvec [0 1 2 3 4 5] 2)        ; => [2 3 4 5] (from index 2 to end)
(subvec [0 1 2 3 4 5] 2 4)      ; => [2 3] (from index 2 to 4, exclusive)

;; ============================================================================
;; 3. MAPS - Key-value pairs (like dictionaries/hash tables)
;; ============================================================================

;; Creating maps
{:name "Alice" :age 30}                    ; => {:name "Alice", :age 30}
(hash-map :a 1 :b 2)                       ; => {:a 1, :b 2}

;; Keywords as keys (most common)
{:first-name "John" :last-name "Doe"}

;; Strings as keys
{"name" "Alice" "age" 30}

;; Any value can be a key!
{1 "one" 2 "two" [1 2] "vector-key"}

;; Accessing values
(get {:a 1 :b 2} :a)            ; => 1
(get {:a 1 :b 2} :c)            ; => nil
(get {:a 1 :b 2} :c "default")  ; => "default"

;; Maps are functions of their keys!
({:a 1 :b 2} :a)                ; => 1

;; Keywords are functions too! (most idiomatic way)
(:a {:a 1 :b 2})                ; => 1
(:c {:a 1 :b 2})                ; => nil
(:c {:a 1 :b 2} "default")      ; => "default"

;; Adding/updating entries
(assoc {:a 1} :b 2)             ; => {:a 1, :b 2}
(assoc {:a 1} :a 99)            ; => {:a 99} (update existing)

;; Adding multiple entries
(assoc {:a 1} :b 2 :c 3)        ; => {:a 1, :b 2, :c 3}

;; Removing entries
(dissoc {:a 1 :b 2 :c 3} :b)    ; => {:a 1, :c 3}
(dissoc {:a 1 :b 2 :c 3} :a :c) ; => {:b 2}

;; Merging maps
(merge {:a 1 :b 2} {:c 3 :d 4}) ; => {:a 1, :b 2, :c 3, :d 4}
(merge {:a 1} {:a 99})          ; => {:a 99} (later values win)

;; Getting all keys and values
(keys {:a 1 :b 2})              ; => (:a :b)
(vals {:a 1 :b 2})              ; => (1 2)

;; Check if key exists
(contains? {:a 1 :b 2} :a)      ; => true
(contains? {:a 1 :b 2} :c)      ; => false

;; Find returns a map entry (key-value pair)
(find {:a 1 :b 2} :a)           ; => [:a 1]

;; Update a value with a function
(update {:a 1 :b 2} :a inc)     ; => {:a 2, :b 2}
(update {:a 1 :b 2} :a + 10)    ; => {:a 11, :b 2}

;; Nested maps
(def person {:name "Alice"
             :address {:city "NYC"
                       :zip "10001"}})

;; Access nested values
(get-in person [:address :city])           ; => "NYC"
(get-in person [:address :country] "USA")  ; => "USA" (default)

;; Update nested values
(assoc-in person [:address :city] "LA")
; => {:name "Alice", :address {:city "LA", :zip "10001"}}

(update-in person [:address :zip] str "-1234")
; => {:name "Alice", :address {:city "NYC", :zip "10001-1234"}}

;; ============================================================================
;; 4. SETS - Collections of unique values
;; ============================================================================

;; Creating sets
#{1 2 3 4 5}                    ; => #{1 4 3 2 5} (order not guaranteed)
(set [1 2 2 3 3 3])             ; => #{1 2 3} (duplicates removed)
(hash-set 1 2 3)                ; => #{1 3 2}

;; Sorted sets
(sorted-set 3 1 4 1 5 9)        ; => #{1 3 4 5 9}

;; Adding elements
(conj #{1 2 3} 4)               ; => #{1 4 3 2}
(conj #{1 2 3} 2)               ; => #{1 3 2} (no change, already exists)

;; Removing elements
(disj #{1 2 3} 2)               ; => #{1 3}

;; Sets are functions! (check membership)
(#{1 2 3} 2)                    ; => 2 (truthy, element exists)
(#{1 2 3} 4)                    ; => nil (falsy, not found)

;; Contains?
(contains? #{1 2 3} 2)          ; => true
(contains? #{1 2 3} 4)          ; => false

;; Set operations (require clojure.set)
(require '[clojure.set :as set])

(set/union #{1 2} #{2 3})       ; => #{1 2 3}
(set/intersection #{1 2 3} #{2 3 4})  ; => #{2 3}
(set/difference #{1 2 3} #{2})  ; => #{1 3}
(set/subset? #{1 2} #{1 2 3})   ; => true
(set/superset? #{1 2 3} #{1 2}) ; => true

;; ============================================================================
;; 5. COMMON OPERATIONS ON ALL COLLECTIONS
;; ============================================================================

;; Count - number of elements
(count [1 2 3 4 5])             ; => 5
(count {:a 1 :b 2})             ; => 2
(count #{1 2 3})                ; => 3

;; Empty check
(empty? [])                     ; => true
(empty? [1])                    ; => false
(empty? {})                     ; => true

;; Get empty collection of same type
(empty [1 2 3])                 ; => []
(empty {:a 1})                  ; => {}

;; Into - pour one collection into another
(into [] '(1 2 3))              ; => [1 2 3]
(into #{} [1 2 2 3])            ; => #{1 2 3}
(into {} [[:a 1] [:b 2]])       ; => {:a 1, :b 2}
(into [1 2] [3 4])              ; => [1 2 3 4]

;; Concatenation
(concat [1 2] [3 4] [5 6])      ; => (1 2 3 4 5 6)

;; Reverse
(reverse [1 2 3 4])             ; => (4 3 2 1)
(reverse "hello")               ; => (\o \l \l \e \h)

;; Sort
(sort [3 1 4 1 5 9])            ; => (1 1 3 4 5 9)
(sort > [3 1 4 1 5 9])          ; => (9 5 4 3 1 1)
(sort-by count ["aa" "b" "ccc"]) ; => ("b" "aa" "ccc")

;; ============================================================================
;; 6. SEQUENCES - The Great Abstraction
;; ============================================================================

;; All collections can be treated as sequences
;; seq returns a sequence view of any collection

(seq [1 2 3])                   ; => (1 2 3)
(seq {:a 1 :b 2})               ; => ([:a 1] [:b 2])
(seq #{1 2 3})                  ; => (1 3 2)
(seq "hello")                   ; => (\h \e \l \l \o)

;; Empty collections return nil
(seq [])                        ; => nil
(seq {})                        ; => nil

;; This is useful for conditional checks
(if (seq [1 2 3])
  "has elements"
  "empty")                      ; => "has elements"

;; ============================================================================
;; 7. EXERCISES
;; ============================================================================

;; Exercise 1: Create a vector of your favorite numbers and:
;; a) Add the number 42 to the end
;; b) Get the third element
;; c) Update the first element to be 100

;; Exercise 2: Create a map representing a book with :title, :author, and :year
;; a) Add a :genre key
;; b) Update the year to current year
;; c) Get the author's name

;; Exercise 3: Given these two sets, find their union, intersection, and difference
;; #{:a :b :c :d}
;; #{:c :d :e :f}

;; Exercise 4: Convert this list to a set to remove duplicates:
;; '(1 2 2 3 3 3 4 4 4 4)

;; Exercise 5: Create a nested map for a person with address, and:
;; a) Use get-in to access the street name
;; b) Use assoc-in to add a phone number under :contact :phone

;; ============================================================================
;; SUMMARY
;; ============================================================================
;; Lists:   '(1 2 3)     - Linked list, add to front, good for sequential access
;; Vectors: [1 2 3]      - Indexed, add to end, good for random access (MOST USED)
;; Maps:    {:a 1 :b 2}  - Key-value pairs, fast lookup
;; Sets:    #{1 2 3}     - Unique values, membership testing
;;
;; Common operations: conj, first, rest, count, empty?, into, seq
;; Map specific: get, assoc, dissoc, update, keys, vals, get-in, assoc-in
;; Set specific: disj, union, intersection, difference
;;
;; Remember: All collections are IMMUTABLE - operations return NEW collections!
;; ============================================================================
