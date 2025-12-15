;; ============================================
;; LESSON 07: HIGHER-ORDER FUNCTIONS
;; ============================================
;; Higher-order functions are functions that either:
;; 1. Take functions as arguments, or
;; 2. Return functions as results
;; They are the bread and butter of functional programming!

;; ============================================
;; 1. MAP - Transform Every Element
;; ============================================
;; (map f coll) applies f to each element

;; Basic map usage
(map inc [1 2 3 4 5])
;; => (2 3 4 5 6)

(map str [1 2 3 4 5])
;; => ("1" "2" "3" "4" "5")

(map clojure.string/upper-case ["hello" "world"])
;; => ("HELLO" "WORLD")

;; With anonymous functions
(map #(* % %) [1 2 3 4 5])
;; => (1 4 9 16 25) - squares

(map #(str "Hello, " %) ["Alice" "Bob" "Carol"])
;; => ("Hello, Alice" "Hello, Bob" "Hello, Carol")

;; Map with multiple collections (stops at shortest)
(map + [1 2 3] [10 20 30])
;; => (11 22 33)

(map + [1 2 3] [10 20 30] [100 200 300])
;; => (111 222 333)

(map vector [:a :b :c] [1 2 3])
;; => ([:a 1] [:b 2] [:c 3])

;; Practical example: extract values from maps
(def people [{:name "Alice" :age 30}
             {:name "Bob" :age 25}
             {:name "Carol" :age 35}])

(map :name people)
;; => ("Alice" "Bob" "Carol")

(map :age people)
;; => (30 25 35)

;; ============================================
;; 2. FILTER - Keep Elements That Match
;; ============================================
;; (filter pred coll) keeps elements where (pred elem) is truthy

(filter even? [1 2 3 4 5 6 7 8])
;; => (2 4 6 8)

(filter odd? [1 2 3 4 5 6 7 8])
;; => (1 3 5 7)

(filter pos? [-2 -1 0 1 2 3])
;; => (1 2 3)

(filter #(> (count %) 3) ["hi" "hello" "hey" "greetings"])
;; => ("hello" "greetings")

;; Filter with keywords on maps
(filter :active [{:name "Alice" :active true}
                 {:name "Bob" :active false}
                 {:name "Carol" :active true}])
;; => ({:name "Alice", :active true} {:name "Carol", :active true})

;; Combine filter and map
(->> [1 2 3 4 5 6 7 8 9 10]
     (filter even?)
     (map #(* % %)))
;; => (4 16 36 64 100) - squares of even numbers

;; ============================================
;; 3. REMOVE - Opposite of Filter
;; ============================================
;; (remove pred coll) removes elements where (pred elem) is truthy

(remove even? [1 2 3 4 5 6])
;; => (1 3 5)

(remove nil? [1 nil 2 nil 3])
;; => (1 2 3)

(remove empty? ["hello" "" "world" ""])
;; => ("hello" "world")

;; ============================================
;; 4. REDUCE - Combine All Elements
;; ============================================
;; (reduce f coll) or (reduce f initial coll)
;; Reduces a collection to a single value

;; Sum all elements
(reduce + [1 2 3 4 5])
;; => 15

;; With initial value
(reduce + 0 [1 2 3 4 5])
;; => 15

(reduce + 100 [1 2 3 4 5])
;; => 115

;; Product
(reduce * [1 2 3 4 5])
;; => 120

;; Find maximum
(reduce max [3 1 4 1 5 9 2 6])
;; => 9

;; Concatenate strings
(reduce str ["Hello" " " "World" "!"])
;; => "Hello World!"

;; Understanding reduce step by step:
;; (reduce + [1 2 3 4])
;; Step 1: (+ 1 2) => 3
;; Step 2: (+ 3 3) => 6
;; Step 3: (+ 6 4) => 10

;; Build a map from pairs
(reduce (fn [m [k v]] (assoc m k v))
        {}
        [[:a 1] [:b 2] [:c 3]])
;; => {:a 1, :b 2, :c 3}

;; Count occurrences (frequency)
(reduce (fn [counts item]
          (update counts item (fnil inc 0)))
        {}
        [:a :b :a :c :b :a])
;; => {:a 3, :b 2, :c 1}

;; Group items
(reduce (fn [groups {:keys [category item]}]
          (update groups category (fnil conj []) item))
        {}
        [{:category :fruit :item "apple"}
         {:category :veg :item "carrot"}
         {:category :fruit :item "banana"}])
;; => {:fruit ["apple" "banana"], :veg ["carrot"]}

;; ============================================
;; 5. REDUCE VARIATIONS
;; ============================================

;; reductions - shows all intermediate steps
(reductions + [1 2 3 4 5])
;; => (1 3 6 10 15)

;; reduced - short-circuit reduce
(reduce (fn [acc x]
          (if (> acc 10)
            (reduced acc)  ; Stop early!
            (+ acc x)))
        [1 2 3 4 5 6 7 8 9 10])
;; => 15 (stops when sum exceeds 10)

;; ============================================
;; 6. TAKE, DROP, AND FRIENDS
;; ============================================

;; take - get first n elements
(take 3 [1 2 3 4 5])
;; => (1 2 3)

;; drop - skip first n elements
(drop 3 [1 2 3 4 5])
;; => (4 5)

;; take-while - take while predicate is true
(take-while #(< % 5) [1 2 3 4 5 6 7])
;; => (1 2 3 4)

;; drop-while - drop while predicate is true
(drop-while #(< % 5) [1 2 3 4 5 6 7])
;; => (5 6 7)

;; take-last / drop-last
(take-last 3 [1 2 3 4 5])
;; => (3 4 5)

(drop-last 3 [1 2 3 4 5])
;; => (1 2)

;; take-nth - take every nth element
(take-nth 2 [1 2 3 4 5 6 7 8])
;; => (1 3 5 7)

(take-nth 3 (range 20))
;; => (0 3 6 9 12 15 18)

;; ============================================
;; 7. SOME, EVERY?, NOT-ANY?, NOT-EVERY?
;; ============================================

;; some - returns first truthy result of (pred item)
(some even? [1 3 5 6 7])
;; => true (6 is even)

(some even? [1 3 5 7])
;; => nil (none are even)

;; Useful pattern: some with a set (membership test)
(some #{:red :blue} [:green :yellow :red])
;; => :red (found!)

(some #{:red :blue} [:green :yellow])
;; => nil (not found)

;; every? - are all elements true for pred?
(every? even? [2 4 6 8])
;; => true

(every? even? [2 4 5 8])
;; => false

(every? string? ["a" "b" "c"])
;; => true

;; not-any? - are no elements true for pred?
(not-any? even? [1 3 5 7])
;; => true

(not-any? even? [1 3 4 7])
;; => false

;; not-every? - is at least one element false for pred?
(not-every? even? [2 4 5 6])
;; => true

(not-every? even? [2 4 6 8])
;; => false

;; ============================================
;; 8. PARTITION AND GROUP
;; ============================================

;; partition - split into groups of n
(partition 3 [1 2 3 4 5 6 7 8 9])
;; => ((1 2 3) (4 5 6) (7 8 9))

;; Drops incomplete final group
(partition 3 [1 2 3 4 5 6 7])
;; => ((1 2 3) (4 5 6))

;; partition-all - keeps incomplete groups
(partition-all 3 [1 2 3 4 5 6 7])
;; => ((1 2 3) (4 5 6) (7))

;; partition with step
(partition 2 1 [1 2 3 4 5])  ; Size 2, step 1
;; => ((1 2) (2 3) (3 4) (4 5))

;; partition-by - splits when predicate result changes
(partition-by even? [1 1 2 2 3 3 4 4])
;; => ((1 1) (2 2) (3 3) (4 4))

(partition-by #(< % 5) [1 2 3 6 7 8 2 3])
;; => ((1 2 3) (6 7 8) (2 3))

;; group-by - group by function result
(group-by even? [1 2 3 4 5 6])
;; => {false [1 3 5], true [2 4 6]}

(group-by count ["a" "bb" "ccc" "dd" "eee"])
;; => {1 ["a"], 2 ["bb" "dd"], 3 ["ccc" "eee"]}

(group-by :type [{:type :a :val 1}
                 {:type :b :val 2}
                 {:type :a :val 3}])
;; => {:a [{:type :a :val 1} {:type :a :val 3}]
;;     :b [{:type :b :val 2}]}

;; ============================================
;; 9. SORT AND COMPARE
;; ============================================

;; sort - natural order
(sort [3 1 4 1 5 9 2 6])
;; => (1 1 2 3 4 5 6 9)

(sort ["banana" "apple" "cherry"])
;; => ("apple" "banana" "cherry")

;; sort with comparator
(sort > [3 1 4 1 5 9 2 6])
;; => (9 6 5 4 3 2 1 1)

;; sort-by - sort by a key function
(sort-by count ["aaa" "b" "cc"])
;; => ("b" "cc" "aaa")

(sort-by :age [{:name "Alice" :age 30}
               {:name "Bob" :age 25}
               {:name "Carol" :age 35}])
;; => ({:name "Bob"...} {:name "Alice"...} {:name "Carol"...})

;; sort-by with custom comparator
(sort-by :age > [{:name "Alice" :age 30}
                 {:name "Bob" :age 25}
                 {:name "Carol" :age 35}])
;; => Carol (35), Alice (30), Bob (25)

;; ============================================
;; 10. APPLY - Spread Collection as Arguments
;; ============================================
;; (apply f coll) calls f with elements of coll as args

(apply + [1 2 3 4 5])
;; => 15  (same as (+ 1 2 3 4 5))

(apply str ["Hello" " " "World"])
;; => "Hello World"

(apply max [3 1 4 1 5 9])
;; => 9

;; With leading arguments
(apply str "Items: " ["a" ", " "b" ", " "c"])
;; => "Items: a, b, c"

;; ============================================
;; 11. COMP - Function Composition
;; ============================================
;; (comp f g h) creates fn that applies h, then g, then f
;; Reads right to left!

(def process (comp str inc #(* % 2)))
;; Equivalent to: (fn [x] (str (inc (* x 2))))

(process 5)
;; => "11"  (5 * 2 = 10, + 1 = 11, str = "11")

;; More readable examples
(def get-first-letter (comp first :name))
(get-first-letter {:name "Alice"})
;; => \A

(def count-words (comp count #(clojure.string/split % #"\s+")))
(count-words "Hello beautiful world")
;; => 3

;; ============================================
;; 12. PARTIAL - Fix Some Arguments
;; ============================================
;; (partial f arg1 arg2...) returns fn with some args pre-filled

(def add-10 (partial + 10))
(add-10 5)
;; => 15

(def greet (partial str "Hello, "))
(greet "World")
;; => "Hello, World"

(def loud-print (partial println "!!!"))
(loud-print "Important message")
;; Prints: !!! Important message

;; Useful with map
(map (partial * 2) [1 2 3 4 5])
;; => (2 4 6 8 10)

(map (partial str "user-") [1 2 3])
;; => ("user-1" "user-2" "user-3")

;; ============================================
;; 13. JUXT - Apply Multiple Functions
;; ============================================
;; (juxt f g h) returns fn that applies all fns and returns vector

(def stats (juxt count #(reduce + %) #(/ (reduce + %) (count %))))
(stats [1 2 3 4 5])
;; => [5 15 3] - count, sum, average

((juxt :name :age) {:name "Alice" :age 30 :city "NYC"})
;; => ["Alice" 30]

((juxt first last count) [1 2 3 4 5])
;; => [1 5 5]

;; Useful for extracting multiple values
(map (juxt :id :name) [{:id 1 :name "A"}
                       {:id 2 :name "B"}
                       {:id 3 :name "C"}])
;; => ([1 "A"] [2 "B"] [3 "C"])

;; ============================================
;; 14. COMPLEMENT - Negate a Predicate
;; ============================================

(def not-empty? (complement empty?))
(not-empty? [1 2 3])
;; => true

(filter (complement nil?) [1 nil 2 nil 3])
;; => (1 2 3)

;; Same as remove
(filter (complement even?) [1 2 3 4 5])
;; => (1 3 5)

;; ============================================
;; 15. CONSTANTLY - Always Return Same Value
;; ============================================

(def always-42 (constantly 42))
(always-42)        ;; => 42
(always-42 "arg")  ;; => 42
(always-42 1 2 3)  ;; => 42

;; Useful with map to create repeated values
(map (constantly :default) (range 5))
;; => (:default :default :default :default :default)

;; ============================================
;; 16. IDENTITY - Return Input Unchanged
;; ============================================

(identity 42)
;; => 42

(identity {:a 1})
;; => {:a 1}

;; Useful for filtering truthy values
(filter identity [1 nil 2 false 3 nil])
;; => (1 2 3)

;; ============================================
;; 17. PRACTICAL EXAMPLES
;; ============================================

;; Process sales data
(def sales [{:product "Apple" :quantity 10 :price 1.50}
            {:product "Banana" :quantity 15 :price 0.75}
            {:product "Orange" :quantity 8 :price 2.00}
            {:product "Apple" :quantity 5 :price 1.50}])

;; Total revenue
(reduce + (map #(* (:quantity %) (:price %)) sales))
;; => 53.25

;; Group by product and sum quantities
(->> sales
     (group-by :product)
     (map (fn [[product items]]
            [product (reduce + (map :quantity items))]))
     (into {}))
;; => {"Apple" 15, "Banana" 15, "Orange" 8}

;; Pipeline: filter, transform, aggregate
(->> (range 1 101)
     (filter #(zero? (mod % 3)))    ; Divisible by 3
     (map #(* % %))                  ; Square them
     (take 10)                       ; First 10
     (reduce +))                     ; Sum
;; => 1155

;; Word frequency counter
(defn word-frequencies [text]
  (->> (clojure.string/lower-case text)
       (re-seq #"\w+")
       (frequencies)))

(word-frequencies "The quick brown fox jumps over the lazy dog the fox")
;; => {"the" 3, "quick" 1, "brown" 1, "fox" 2, ...}

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Use map and filter to get squares of
;; odd numbers from 1 to 20

;; Exercise 2: Use reduce to find the longest string
;; in a collection

;; Exercise 3: Use group-by to group numbers 1-20
;; by their remainder when divided by 3

;; Exercise 4: Create a function using comp that:
;; - Takes a string
;; - Splits by spaces
;; - Counts the words
;; - Returns "short" if < 5, "medium" if < 10, "long" otherwise

;; Exercise 5: Use partition to pair up adjacent elements
;; and find all pairs that sum to more than 10
;; Input: [3 8 2 9 4 7 1 6]

;; ============================================
;; KEY TAKEAWAYS
;; ============================================
;; 1. map transforms, filter selects, reduce combines
;; 2. These functions return lazy sequences (evaluated on demand)
;; 3. comp composes functions (right to left)
;; 4. partial pre-fills function arguments
;; 5. juxt applies multiple functions, returning a vector
;; 6. Use threading macros (->, ->>) for readability
;; 7. Combine these functions for powerful data pipelines
