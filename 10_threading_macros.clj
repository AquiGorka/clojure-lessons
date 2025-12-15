;; ============================================
;; LESSON 10: THREADING MACROS
;; ============================================
;; Threading macros make nested function calls
;; readable by showing data flow linearly.
;; They're one of Clojure's secret weapons!

;; ============================================
;; THE PROBLEM: NESTED FUNCTION CALLS
;; ============================================

;; Consider this data transformation:
(def person {:name "  ALICE SMITH  " :age 30})

;; To get a cleaned-up, lowercase first name, we might write:
(first
  (clojure.string/split
    (clojure.string/lower-case
      (clojure.string/trim
        (:name person)))
    #" "))
;; => "alice"

;; This is hard to read! You have to read from inside-out.
;; Threading macros fix this.

;; ============================================
;; 1. THREAD-FIRST: ->
;; ============================================
;; Inserts each result as the FIRST argument of the next form.
;; Great for object-like transformations.

;; Syntax: (-> value form1 form2 form3 ...)

;; Simple example:
(-> 5
    (+ 3)      ; becomes (+ 5 3) => 8
    (* 2)      ; becomes (* 8 2) => 16
    (- 1))     ; becomes (- 16 1) => 15
;; => 15

;; Without threading:
(- (* (+ 5 3) 2) 1)  ;; Same result, harder to follow

;; The name example becomes much clearer:
(-> person
    :name                          ; (:name person) => "  ALICE SMITH  "
    clojure.string/trim            ; (trim "  ALICE SMITH  ") => "ALICE SMITH"
    clojure.string/lower-case      ; (lower-case "ALICE SMITH") => "alice smith"
    (clojure.string/split #" ")    ; (split "alice smith" #" ") => ["alice" "smith"]
    first)                         ; (first ["alice" "smith"]) => "alice"
;; => "alice"

;; Perfect for map transformations:
(-> {:a 1}
    (assoc :b 2)       ; (assoc {:a 1} :b 2)
    (assoc :c 3)       ; (assoc {:a 1 :b 2} :c 3)
    (update :a inc)    ; (update {:a 1 :b 2 :c 3} :a inc)
    (dissoc :b))       ; (dissoc {:a 2 :b 2 :c 3} :b)
;; => {:a 2, :c 3}

;; String operations:
(-> "hello world"
    clojure.string/upper-case
    (clojure.string/replace "WORLD" "CLOJURE")
    (str "!!!"))
;; => "HELLO CLOJURE!!!"

;; ============================================
;; 2. THREAD-LAST: ->>
;; ============================================
;; Inserts each result as the LAST argument of the next form.
;; Great for sequence operations.

;; Syntax: (->> value form1 form2 form3 ...)

;; Simple example:
(->> 5
     (+ 3)      ; becomes (+ 3 5) => 8
     (* 2)      ; becomes (* 2 8) => 16
     (- 1))     ; becomes (- 1 16) => -15
;; => -15  (Note: different from -> because of argument position!)

;; Perfect for collection pipelines:
(->> [1 2 3 4 5 6 7 8 9 10]
     (filter even?)           ; (filter even? [...])
     (map #(* % %))           ; (map #(* % %) '(2 4 6 8 10))
     (reduce +))              ; (reduce + '(4 16 36 64 100))
;; => 220

;; Without threading:
(reduce +
  (map #(* % %)
    (filter even?
      [1 2 3 4 5 6 7 8 9 10])))
;; Same result, but reads inside-out

;; Processing a list of users:
(def users
  [{:name "Alice" :age 30 :active true}
   {:name "Bob" :age 25 :active false}
   {:name "Carol" :age 35 :active true}
   {:name "Dave" :age 28 :active true}])

(->> users
     (filter :active)
     (map :name)
     (map clojure.string/upper-case)
     (clojure.string/join ", "))
;; => "ALICE, CAROL, DAVE"

;; Generate a range and process it:
(->> (range 1 20)
     (filter #(zero? (mod % 3)))
     (map #(* % 2))
     (take 5))
;; => (6 12 18 24 30)

;; ============================================
;; 3. WHEN TO USE -> VS ->>
;; ============================================

;; Use -> (thread-first) for:
;; - Map/hash-map operations (assoc, update, dissoc, get)
;; - String operations in clojure.string
;; - Java interop method calls
;; - Generally when data is the "object being acted upon"

(-> {:name "Alice"}
    (assoc :role "admin")
    (update :name clojure.string/upper-case))
;; => {:name "ALICE", :role "admin"}

;; Use ->> (thread-last) for:
;; - Sequence operations (map, filter, reduce, take, etc.)
;; - When data is a collection being transformed
;; - Functions where the collection is the last argument

(->> [1 2 3 4 5]
     (map inc)
     (filter even?)
     (reduce +))
;; => 12

;; ============================================
;; 4. THREAD-AS: as->
;; ============================================
;; When you need to place the value in different positions.
;; You name the threaded value and use it anywhere.

;; Syntax: (as-> initial-value name form1 form2 ...)

(as-> {:name "Alice" :scores [85 90 78]} data
      (:scores data)                    ; Get scores
      (map #(* % 1.1) data)             ; Curve by 10% (last position)
      (filter #(>= % 90) data)          ; Keep scores >= 90 (last position)
      {:curved-scores data              ; Use in map value
       :count (count data)})            ; And in another expression
;; => {:curved-scores (93.50000000000001 99.00000000000001), :count 2}

;; Mixing first and last argument positions:
(as-> "hello" s
      (clojure.string/upper-case s)    ; (upper-case "hello")
      (str s "!!!")                    ; (str "HELLO" "!!!")
      (clojure.string/split s #"")     ; (split "HELLO!!!" #"")
      (count s))                       ; (count [...])
;; => 8

;; ============================================
;; 5. CONDITIONAL THREADING: cond-> and cond->>
;; ============================================
;; Thread through forms conditionally.
;; Only applies transformations when conditions are true.

;; Syntax: (cond-> value test1 form1 test2 form2 ...)

(defn process-order [order]
  (cond-> order
    (:premium order)        (update :total * 0.9)   ; 10% discount for premium
    (:coupon order)         (update :total - 5)     ; $5 off with coupon
    (> (:items order) 10)   (assoc :bulk true)))    ; Mark as bulk order

(process-order {:total 100 :items 5 :premium true})
;; => {:total 90.0, :items 5, :premium true}

(process-order {:total 100 :items 15 :coupon true})
;; => {:total 95, :items 15, :coupon true, :bulk true}

(process-order {:total 100 :items 5})
;; => {:total 100, :items 5}

;; cond->> threads to last position:
(defn filter-numbers [nums opts]
  (cond->> nums
    (:only-positive opts)  (filter pos?)
    (:only-even opts)      (filter even?)
    (:sorted opts)         (sort)
    (:limit opts)          (take (:limit opts))))

(filter-numbers [-3 1 4 -1 5 9 2 -6 5 3]
                {:only-positive true :only-even true :sorted true})
;; => (2 4)

;; ============================================
;; 6. SOME THREADING: some-> and some->>
;; ============================================
;; Short-circuits and returns nil if any step returns nil.
;; Great for avoiding NullPointerExceptions!

;; Syntax: (some-> value form1 form2 ...)

(defn get-user-city [db user-id]
  (some-> db
          (get user-id)           ; Get user (might be nil)
          :address                ; Get address (might be nil)
          :city                   ; Get city (might be nil)
          clojure.string/upper-case))

(def db {1 {:name "Alice" :address {:city "New York" :zip "10001"}}
         2 {:name "Bob" :address nil}
         3 {:name "Carol"}})

(get-user-city db 1)  ;; => "NEW YORK"
(get-user-city db 2)  ;; => nil (address is nil, stops there)
(get-user-city db 3)  ;; => nil (no :address key)
(get-user-city db 99) ;; => nil (user doesn't exist)

;; Without some->, you'd need lots of nil checks:
(defn get-user-city-verbose [db user-id]
  (when-let [user (get db user-id)]
    (when-let [address (:address user)]
      (when-let [city (:city address)]
        (clojure.string/upper-case city)))))

;; some->> for last-position threading with nil safety:
(some->> [1 2 3]
         (filter even?)
         first
         (* 10))
;; => 20

(some->> [1 3 5]
         (filter even?)
         first            ; Returns nil (no even numbers)
         (* 10))          ; Not executed
;; => nil

;; ============================================
;; 7. PRACTICAL EXAMPLES
;; ============================================

;; API response processing:
(defn process-api-response [response]
  (some-> response
          :body
          (cheshire.core/parse-string true)  ; Imagine this parses JSON
          :data
          :users
          first
          :email))

;; Building a query map:
(defn build-query [base-query options]
  (cond-> base-query
    (:search options)    (assoc :q (:search options))
    (:limit options)     (assoc :limit (:limit options))
    (:offset options)    (assoc :offset (:offset options))
    (:sort-by options)   (assoc :sort (:sort-by options))
    (:desc options)      (assoc :order "desc")))

(build-query {:type "users"}
             {:search "alice" :limit 10 :sort-by "name"})
;; => {:type "users", :q "alice", :limit 10, :sort "name"}

;; Data pipeline:
(def raw-data
  [{:id 1 :name " Alice " :score "85" :active "true"}
   {:id 2 :name " Bob " :score "90" :active "false"}
   {:id 3 :name " Carol " :score "78" :active "true"}])

(defn clean-record [record]
  (-> record
      (update :name clojure.string/trim)
      (update :score #(Integer/parseInt %))
      (update :active #(= % "true"))))

(defn process-data [data]
  (->> data
       (map clean-record)
       (filter :active)
       (sort-by :score >)
       (map #(select-keys % [:name :score]))))

(process-data raw-data)
;; => ({:name "Alice", :score 85} {:name "Carol", :score 78})

;; ============================================
;; 8. COMBINING THREADING WITH OTHER FORMS
;; ============================================

;; Using let inside a thread:
(-> {:items [{:price 10} {:price 20} {:price 15}]}
    :items
    (->> (map :price))
    ((fn [prices]
       {:total (reduce + prices)
        :average (/ (reduce + prices) (count prices))})))
;; => {:total 45, :average 15}

;; Better: Use as-> for complex cases
(as-> {:items [{:price 10} {:price 20} {:price 15}]} data
      (:items data)
      (map :price data)
      {:total (reduce + data)
       :average (/ (reduce + data) (count data))})
;; => {:total 45, :average 15}

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Use -> to transform this map:
;; {:first "john" :last "doe" :age 30}
;; Into: {:full-name "JOHN DOE", :age 31}
;; (uppercase full name, increment age)

;; Exercise 2: Use ->> to process this list:
;; [1 2 3 4 5 6 7 8 9 10]
;; Get the sum of squares of odd numbers.

;; Exercise 3: Use some-> to safely navigate this structure:
;; (def company {:departments {:engineering {:lead {:name "Alice"}}}})
;; Get the uppercase name of the engineering lead, or nil if any part is missing.

;; Exercise 4: Use cond-> to build a URL query string:
;; (build-url {:host "api.example.com"} {:page 2 :limit 10 :search "clojure"})
;; Should produce query params only for keys that exist in options.

;; Exercise 5: Rewrite this nested expression using threading:
;; (reduce + (map inc (filter even? (range 1 20))))

;; ============================================
;; KEY TAKEAWAYS
;; ============================================
;; 1. -> threads as first arg (great for maps, strings, objects)
;; 2. ->> threads as last arg (great for sequences)
;; 3. as-> lets you name the value and place it anywhere
;; 4. cond-> and cond->> thread conditionally
;; 5. some-> and some->> short-circuit on nil
;; 6. Threading macros make code read top-to-bottom, left-to-right
;; 7. They're macros, so they transform at compile time (no runtime cost)

;; Next lesson: Laziness and Lazy Sequences
