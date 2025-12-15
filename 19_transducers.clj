;; ============================================
;; LESSON 19: TRANSDUCERS
;; ============================================
;; Transducers are composable algorithmic transformations.
;; They're decoupled from input/output sources and provide
;; excellent performance for data transformation pipelines.

;; ============================================
;; 1. THE PROBLEM WITH SEQUENCE OPERATIONS
;; ============================================

;; Traditional sequence operations create intermediate sequences:
(->> (range 1000000)
     (map inc)           ; Creates lazy seq
     (filter even?)      ; Creates another lazy seq
     (take 100)          ; Creates another lazy seq
     (reduce +))

;; Each step creates a new sequence. While lazy, this still has overhead.
;; What if we could fuse these operations together?

;; ============================================
;; 2. WHAT ARE TRANSDUCERS?
;; ============================================

;; A transducer is a transformation that:
;; - Takes a reducing function and returns a new reducing function
;; - Is independent of the source (works with seqs, channels, etc.)
;; - Can be composed without creating intermediate collections
;; - Processes one element at a time through all transformations

;; Creating transducers - call map, filter, etc. WITHOUT a collection:
(def inc-xf (map inc))              ; Transducer, not a lazy seq!
(def even-xf (filter even?))        ; Transducer
(def take-5-xf (take 5))            ; Transducer

;; ============================================
;; 3. COMPOSING TRANSDUCERS
;; ============================================

;; Use `comp` to compose transducers
;; NOTE: Unlike function composition, transducers compose LEFT-TO-RIGHT!

(def my-xf
  (comp
    (map inc)           ; First: increment
    (filter even?)      ; Second: keep evens
    (take 5)))          ; Third: take first 5

;; This reads naturally: increment, then filter, then take

;; ============================================
;; 4. USING TRANSDUCERS
;; ============================================

;; Method 1: transduce (like reduce, but with transducer)
;; (transduce xform f init coll)

(transduce
  (comp (map inc) (filter even?))   ; Transducer
  +                                  ; Reducing function
  0                                  ; Initial value
  (range 10))                        ; Input collection
;; => 30 (sum of 2, 4, 6, 8, 10)

;; Equivalent to, but more efficient than:
(->> (range 10)
     (map inc)
     (filter even?)
     (reduce + 0))

;; Method 2: into (pour transformed elements into collection)
;; (into to xform from)

(into []
      (comp (map inc) (filter even?))
      (range 10))
;; => [2 4 6 8 10]

(into #{}
      (map str)
      [1 2 3 2 1])
;; => #{"1" "2" "3"}

(into {}
      (map (fn [x] [x (* x x)]))
      [1 2 3 4 5])
;; => {1 1, 2 4, 3 9, 4 16, 5 25}

;; Method 3: sequence (create lazy sequence from transducer)
;; (sequence xform coll)

(sequence (map inc) [1 2 3])
;; => (2 3 4)

(sequence
  (comp (filter even?) (map #(* % 2)))
  (range 10))
;; => (0 4 8 12 16)

;; Method 4: eduction (create reducible, iterable view)
;; Doesn't cache results - good for one-time iteration

(def ed (eduction (map inc) (filter even?) (range 10)))
(reduce + ed)  ;; => 30
(into [] ed)   ;; => [2 4 6 8 10]

;; ============================================
;; 5. AVAILABLE TRANSDUCERS
;; ============================================

;; Most sequence functions have transducer arities:

;; Transforming
(map inc)               ; Transform each element
(map-indexed vector)    ; Include index
(replace {:a 1 :b 2})   ; Replace values from map

;; Filtering
(filter even?)          ; Keep matching elements
(remove odd?)           ; Remove matching elements
(keep identity)         ; Keep non-nil results
(keep-indexed (fn [i x] (when (even? i) x)))
(distinct)              ; Remove duplicates
(dedupe)                ; Remove consecutive duplicates

;; Taking/Dropping
(take 5)                ; Take first n
(take-while pos?)       ; Take while predicate true
(take-nth 3)            ; Take every nth
(drop 5)                ; Drop first n
(drop-while neg?)       ; Drop while predicate true

;; Partitioning
(partition-all 3)       ; Groups of n
(partition-by even?)    ; Group by predicate changes

;; Flattening
(cat)                   ; Concatenate nested colls
(mapcat seq)            ; Map then concatenate

;; Interleaving
(interpose :sep)        ; Insert separator

;; Miscellaneous
(halt-when pred)        ; Stop when predicate matches
(random-sample 0.5)     ; Randomly sample ~50%

;; ============================================
;; 6. COMPOSING COMPLEX PIPELINES
;; ============================================

;; Process user data efficiently
(def user-data
  [{:name "Alice" :age 30 :active true}
   {:name "Bob" :age 17 :active true}
   {:name "Carol" :age 45 :active false}
   {:name "Dave" :age 28 :active true}
   {:name "Eve" :age 22 :active true}])

(def adult-active-names
  (comp
    (filter :active)            ; Keep active users
    (filter #(>= (:age %) 18))  ; Adults only
    (map :name)                 ; Extract names
    (map clojure.string/upper-case)))  ; Uppercase

(into [] adult-active-names user-data)
;; => ["ALICE" "DAVE" "EVE"]

;; Process numbers
(def number-pipeline
  (comp
    (filter number?)
    (remove neg?)
    (map #(* % 2))
    (take-while #(< % 100))))

(into [] number-pipeline [1 -2 "skip" 3 4 50 60 70])
;; => [2 6 8 100] - stops at 100 because of take-while

;; ============================================
;; 7. TRANSDUCERS WITH MULTIPLE SOURCES
;; ============================================

;; map and cat can work with multiple collections:
(sequence (map vector) [1 2 3] [:a :b :c])
;; => ([1 :a] [2 :b] [3 :c])

;; mapcat flattens results:
(into [] (mapcat range) [1 2 3 4])
;; => [0 0 1 0 1 2 0 1 2 3]

;; ============================================
;; 8. STATEFUL TRANSDUCERS
;; ============================================

;; Some transducers maintain state:
;; - take, drop, take-while, drop-while
;; - partition-all, partition-by
;; - distinct, dedupe

;; They're still functional - state is encapsulated

;; Example: dedupe removes consecutive duplicates
(into [] (dedupe) [1 1 2 2 2 3 1 1])
;; => [1 2 3 1]

;; Example: partition-all groups elements
(into [] (partition-all 3) [1 2 3 4 5 6 7 8])
;; => [[1 2 3] [4 5 6] [7 8]]

;; Example: partition-by groups by predicate changes
(into [] (partition-by even?) [1 1 2 2 3 4 4 4])
;; => [[1 1] [2 2] [3] [4 4 4]]

;; ============================================
;; 9. CREATING CUSTOM TRANSDUCERS
;; ============================================

;; A transducer is a function that takes a reducing function
;; and returns a new reducing function.

;; Basic structure:
(defn my-map [f]
  (fn [rf]                     ; Takes reducing function
    (fn                        ; Returns new reducing function
      ([] (rf))                ; 0-arity: init
      ([result] (rf result))   ; 1-arity: completion
      ([result input]          ; 2-arity: step
       (rf result (f input))))))

;; Test it:
(transduce (my-map inc) + 0 [1 2 3])
;; => 9

;; Custom filter transducer:
(defn my-filter [pred]
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (if (pred input)
         (rf result input)
         result)))))

(into [] (my-filter even?) [1 2 3 4 5])
;; => [2 4]

;; Custom transducer with state: take-every-nth
(defn take-every-nth [n]
  (fn [rf]
    (let [counter (volatile! 0)]
      (fn
        ([] (rf))
        ([result] (rf result))
        ([result input]
         (let [c (vswap! counter inc)]
           (if (zero? (mod c n))
             (rf result input)
             result)))))))

(into [] (take-every-nth 3) (range 12))
;; => [2 5 8 11]

;; ============================================
;; 10. EARLY TERMINATION WITH reduced
;; ============================================

;; Transducers can signal early termination using `reduced`

(defn take-until [pred]
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (if (pred input)
         (reduced result)    ; Stop processing
         (rf result input))))))

(into [] (take-until #(> % 5)) (range 10))
;; => [0 1 2 3 4 5]

;; halt-when is built-in for this purpose:
(into [] (halt-when #(> % 5)) (range 10))
;; => [0 1 2 3 4 5]

;; ============================================
;; 11. PERFORMANCE COMPARISON
;; ============================================

;; Traditional (creates intermediate sequences):
(defn traditional [coll]
  (->> coll
       (map inc)
       (filter even?)
       (take 1000)
       (reduce +)))

;; With transducers (no intermediate collections):
(defn with-transducers [coll]
  (transduce
    (comp (map inc) (filter even?) (take 1000))
    +
    0
    coll))

;; Both produce same result, but transducers:
;; - Create no intermediate collections
;; - Process each element through entire pipeline
;; - Have less memory overhead
;; - Often faster for large collections

;; Benchmark (conceptual):
;; (time (traditional (range 1000000)))
;; (time (with-transducers (range 1000000)))
;; Transducers typically 2-5x faster

;; ============================================
;; 12. TRANSDUCERS WITH CORE.ASYNC
;; ============================================

;; Transducers work with core.async channels!
;; (Requires [org.clojure/core.async] dependency)

;; Example (conceptual):
;; (require '[clojure.core.async :as async])
;;
;; (def ch (async/chan 10 (comp (map inc) (filter even?))))
;;
;; (async/go
;;   (async/>! ch 1)   ; Becomes 2, passes filter
;;   (async/>! ch 2)   ; Becomes 3, filtered out
;;   (async/>! ch 3))  ; Becomes 4, passes filter
;;
;; (async/<!! ch)  ;; => 2
;; (async/<!! ch)  ;; => 4

;; Same transducer works with sequences AND channels!

;; ============================================
;; 13. PRACTICAL EXAMPLES
;; ============================================

;; Example 1: Log processing
(def log-entries
  ["INFO: User logged in"
   "ERROR: Connection failed"
   "DEBUG: Cache miss"
   "ERROR: Timeout"
   "INFO: Request completed"])

(def error-messages
  (comp
    (filter #(clojure.string/starts-with? % "ERROR"))
    (map #(subs % 7))))  ; Remove "ERROR: " prefix

(into [] error-messages log-entries)
;; => ["Connection failed" "Timeout"]

;; Example 2: Data normalization
(def raw-data
  [{:id 1 :value "  HELLO  "}
   {:id 2 :value nil}
   {:id 3 :value "world"}
   {:id 4 :value "  CLOJURE  "}])

(def normalize-data
  (comp
    (filter #(some? (:value %)))
    (map #(update % :value clojure.string/trim))
    (map #(update % :value clojure.string/lower-case))))

(into [] normalize-data raw-data)
;; => [{:id 1, :value "hello"} {:id 3, :value "world"} {:id 4, :value "clojure"}]

;; Example 3: Pagination helper
(defn paginate [page page-size]
  (comp
    (drop (* page page-size))
    (take page-size)))

(into [] (paginate 2 5) (range 100))
;; => [10 11 12 13 14]

;; Example 4: Statistics accumulator
(defn stats-reducer
  ([] {:count 0 :sum 0 :min nil :max nil})
  ([result] result)
  ([{:keys [count sum min max]} x]
   {:count (inc count)
    :sum (+ sum x)
    :min (if min (clojure.core/min min x) x)
    :max (if max (clojure.core/max max x) x)}))

(transduce
  (filter pos?)
  stats-reducer
  [1 -2 3 -4 5 6 -7 8])
;; => {:count 5, :sum 23, :min 1, :max 8}

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Create a transducer pipeline that:
;; - Takes numbers
;; - Removes negatives
;; - Squares them
;; - Keeps only those < 100
;; Test with (range -10 20)

;; Exercise 2: Create a custom transducer `batch` that
;; groups elements into batches of n, emitting each batch
;; (similar to partition-all but as custom impl)

;; Exercise 3: Process a list of maps representing products:
;; - Filter in-stock items
;; - Apply 10% discount to price
;; - Sort by price (hint: can't sort in transducer, do after)
;; - Take top 5 cheapest

;; Exercise 4: Create a transducer that implements
;; "throttling" - only emits elements at most every n items

;; Exercise 5: Use transducers to efficiently process
;; a large range (1 to 1 million):
;; - Keep multiples of 7
;; - Square them
;; - Take first 100
;; - Sum them

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; 1. Transducers are composable, reusable transformations
;; 2. Create by calling map, filter, etc. WITHOUT collection
;; 3. Compose with `comp` - reads LEFT to RIGHT
;; 4. Use with transduce, into, sequence, or eduction
;; 5. No intermediate collections = better performance
;; 6. Work with sequences, channels, and any reducible source
;; 7. Custom transducers take/return reducing functions
;; 8. Three arities: init (0), complete (1), step (2)
;; 9. Use `reduced` for early termination
;; 10. Same transducer works across different contexts

;; Next lesson: Macros - Extending the Language
