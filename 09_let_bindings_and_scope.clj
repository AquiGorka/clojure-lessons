;; ============================================
;; LESSON 09: LET BINDINGS AND SCOPE
;; ============================================
;; Understanding how to create local bindings
;; and manage scope in Clojure.

;; ============================================
;; 1. BASIC `let` BINDINGS
;; ============================================
;; `let` creates local, immutable bindings that
;; exist only within its scope.

;; Syntax: (let [bindings] body)

(let [x 10]
  x)
;; => 10

(let [x 10
      y 20]
  (+ x y))
;; => 30

;; Bindings are paired: [name1 value1, name2 value2, ...]
(let [name "Alice"
      age 30
      greeting (str "Hello, " name)]
  greeting)
;; => "Hello, Alice"

;; The body can have multiple expressions
;; Only the last one is returned
(let [x 5]
  (println "x is" x)      ; side effect
  (println "x squared is" (* x x))
  (* x x))                 ; returned value
;; Prints two lines, returns 25

;; ============================================
;; 2. SEQUENTIAL BINDING
;; ============================================
;; Later bindings can reference earlier ones.
;; Bindings are evaluated in order!

(let [a 1
      b (+ a 1)      ; b uses a
      c (+ a b)      ; c uses both a and b
      d (* c c)]     ; d uses c
  [a b c d])
;; => [1 2 3 9]

;; This is very useful for step-by-step computation
(let [radius 5
      pi 3.14159
      diameter (* 2 radius)
      circumference (* pi diameter)
      area (* pi radius radius)]
  {:radius radius
   :diameter diameter
   :circumference circumference
   :area area})
;; => {:radius 5, :diameter 10, :circumference 31.4159, :area 78.53975}

;; ============================================
;; 3. SHADOWING
;; ============================================
;; Inner bindings can "shadow" outer ones.
;; The inner binding takes precedence.

(let [x 1]
  (let [x 2]
    (println "Inner x:" x))  ; => 2
  (println "Outer x:" x))    ; => 1

;; You can even shadow within the same let
(let [x 1
      x (+ x 10)    ; shadows previous x
      x (* x 2)]    ; shadows again
  x)
;; => 22

;; Useful for transformation pipelines
(let [data "  HELLO WORLD  "
      data (clojure.string/trim data)
      data (clojure.string/lower-case data)
      data (clojure.string/replace data " " "-")]
  data)
;; => "hello-world"

;; ============================================
;; 4. DESTRUCTURING IN `let`
;; ============================================
;; You can destructure directly in let bindings!

;; Vector destructuring
(let [[a b c] [1 2 3]]
  (+ a b c))
;; => 6

;; With rest
(let [[first second & rest] [1 2 3 4 5]]
  {:first first :second second :rest rest})
;; => {:first 1, :second 2, :rest (3 4 5)}

;; Ignoring values with _
(let [[_ second _] [1 2 3]]
  second)
;; => 2

;; Nested destructuring
(let [[[a b] [c d]] [[1 2] [3 4]]]
  (+ a b c d))
;; => 10

;; Map destructuring
(let [{name :name age :age} {:name "Alice" :age 30}]
  (str name " is " age))
;; => "Alice is 30"

;; Shorthand with :keys
(let [{:keys [name age city]} {:name "Bob" :age 25 :city "NYC"}]
  (str name " lives in " city))
;; => "Bob lives in NYC"

;; With defaults using :or
(let [{:keys [name age country]
       :or {country "Unknown"}}
      {:name "Carol" :age 28}]
  (str name " is from " country))
;; => "Carol is from Unknown"

;; Keep the original with :as
(let [{:keys [x y] :as point} {:x 10 :y 20 :z 30}]
  (println "Point:" point)
  (println "Coordinates:" x y)
  (assoc point :distance (Math/sqrt (+ (* x x) (* y y)))))
;; => {:x 10, :y 20, :z 30, :distance 22.360679774997898}

;; String keys with :strs
(let [{:strs [name age]} {"name" "Dave" "age" 35}]
  (str name " is " age))
;; => "Dave is 35"

;; ============================================
;; 5. SCOPE RULES
;; ============================================

;; let bindings are lexically scoped
;; They only exist within the let body

(let [secret 42]
  (println "Inside:" secret))
;; (println secret)  ; ERROR! secret doesn't exist here

;; Nested scopes
(let [outer 1]
  (let [inner 2]
    (println "Can see both:" outer inner))
  ;; inner is not accessible here
  (println "Only outer:" outer))

;; Functions defined in let can access let bindings (closure)
(let [multiplier 3]
  (defn triple [x]
    (* x multiplier)))

(triple 10)  ;; => 30

;; ============================================
;; 6. `let` vs `def`
;; ============================================

;; def creates global bindings in the namespace
(def global-value 100)
global-value  ;; => 100 (accessible anywhere)

;; let creates local bindings
(let [local-value 200]
  local-value)  ;; => 200 (only here)

;; local-value  ;; ERROR! Not defined outside let

;; Use def for:
;; - Constants
;; - Functions (via defn)
;; - Configuration
;; - Anything that needs to be accessed from multiple places

;; Use let for:
;; - Intermediate calculations
;; - Local variables in functions
;; - Avoiding repeated computations

;; ============================================
;; 7. `if-let` AND `when-let`
;; ============================================
;; Combine binding with conditional testing

;; if-let: bind and test, with else branch
(if-let [result (get {:a 1 :b 2} :a)]
  (str "Found: " result)
  "Not found")
;; => "Found: 1"

(if-let [result (get {:a 1 :b 2} :c)]
  (str "Found: " result)
  "Not found")
;; => "Not found"

;; when-let: bind and test, no else branch
(when-let [users (seq [1 2 3])]
  (println "Processing" (count users) "users")
  users)
;; => (1 2 3)

(when-let [users (seq [])]
  (println "This won't print")
  users)
;; => nil

;; Practical example
(defn process-user [user-id]
  (let [users {:1 {:name "Alice"} :2 {:name "Bob"}}]
    (if-let [user (get users (keyword (str user-id)))]
      (str "Processing: " (:name user))
      (str "User " user-id " not found"))))

(process-user 1)  ;; => "Processing: Alice"
(process-user 9)  ;; => "User 9 not found"

;; ============================================
;; 8. `if-some` AND `when-some`
;; ============================================
;; Like if-let/when-let but only test for nil
;; (false is considered a valid value)

(if-some [val false]
  (str "Value is: " val)
  "Was nil")
;; => "Value is: false"

(if-let [val false]
  (str "Value is: " val)
  "Was nil")
;; => "Was nil" (false is falsy!)

;; Useful when false is a valid value
(defn get-setting [settings key]
  (if-some [value (get settings key)]
    {:found true :value value}
    {:found false}))

(get-setting {:debug false :verbose true} :debug)
;; => {:found true, :value false}

(get-setting {:debug false :verbose true} :missing)
;; => {:found false}

;; ============================================
;; 9. `letfn` - LOCAL FUNCTIONS
;; ============================================
;; Define local functions that can call each other
;; (mutual recursion)

(letfn [(double-it [x] (* 2 x))
        (add-one [x] (+ 1 x))
        (process [x] (add-one (double-it x)))]
  (process 5))
;; => 11

;; Mutual recursion example
(letfn [(is-even? [n]
          (if (zero? n)
            true
            (is-odd? (dec n))))
        (is-odd? [n]
          (if (zero? n)
            false
            (is-even? (dec n))))]
  [(is-even? 10) (is-odd? 10)])
;; => [true false]

;; ============================================
;; 10. `binding` - DYNAMIC SCOPE
;; ============================================
;; For special dynamic vars (defined with ^:dynamic)

(def ^:dynamic *debug* false)

(defn log [msg]
  (when *debug*
    (println "[DEBUG]" msg)))

(log "This won't print")

(binding [*debug* true]
  (log "This WILL print"))
;; Prints: [DEBUG] This WILL print

;; Dynamic vars are thread-local
;; Useful for configuration, context passing

(def ^:dynamic *current-user* nil)

(defn get-user-name []
  (or *current-user* "Anonymous"))

(get-user-name)  ;; => "Anonymous"

(binding [*current-user* "Alice"]
  (get-user-name))  ;; => "Alice"

;; ============================================
;; 11. PRACTICAL PATTERNS
;; ============================================

;; Pattern 1: Computed values in functions
(defn calculate-stats [numbers]
  (let [n (count numbers)
        sum (reduce + numbers)
        mean (/ sum n)
        squares (map #(* % %) numbers)
        sum-squares (reduce + squares)
        variance (- (/ sum-squares n) (* mean mean))]
    {:count n
     :sum sum
     :mean mean
     :variance variance
     :std-dev (Math/sqrt variance)}))

(calculate-stats [1 2 3 4 5])

;; Pattern 2: Early return with when-let chain
(defn process-order [order]
  (when-let [items (:items order)]
    (when-let [total (reduce + (map :price items))]
      (when (pos? total)
        {:order-id (:id order)
         :total total
         :status :processed}))))

;; Pattern 3: Destructure and transform
(defn format-address [{:keys [street city state zip]
                       :or {state "Unknown"}}]
  (str street "\n" city ", " state " " zip))

(format-address {:street "123 Main St"
                 :city "Springfield"
                 :zip "12345"})

;; Pattern 4: Pipeline with shadowing
(defn clean-and-process [raw-data]
  (let [data (clojure.string/trim raw-data)
        data (clojure.string/lower-case data)
        words (clojure.string/split data #"\s+")
        words (filter #(> (count %) 2) words)
        word-count (count words)]
    {:original raw-data
     :cleaned data
     :words words
     :count word-count}))

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Use let to calculate the area and perimeter
;; of a rectangle given width and height
;; Return a map with :width :height :area :perimeter

;; Exercise 2: Use destructuring to extract the first name
;; and last name from a person map, then create a full name
;; Handle missing values with defaults

;; Exercise 3: Write a function using if-let that safely
;; looks up a value in a nested map structure
;; Example: (safe-get {:a {:b {:c 1}}} [:a :b :c]) => 1
;; Return nil if any level is missing

;; Exercise 4: Create a letfn with three local functions:
;; - square: squares a number
;; - sum: sums a list
;; - sum-of-squares: uses both to sum squares of a list
;; Then call sum-of-squares on [1 2 3 4 5]

;; Exercise 5: Write a function that uses sequential let
;; bindings to build up a complex data structure step by step

;; ============================================
;; KEY TAKEAWAYS
;; ============================================
;; 1. `let` creates immutable, lexically scoped bindings
;; 2. Bindings are evaluated sequentially - later ones can use earlier
;; 3. Shadowing allows rebinding names in nested or sequential bindings
;; 4. Destructuring works directly in let bindings
;; 5. `if-let`/`when-let` combine binding with conditional testing
;; 6. `if-some`/`when-some` only test for nil, not falseness
;; 7. `letfn` allows mutually recursive local functions
;; 8. `binding` provides dynamic scope for special vars
;; 9. Use `let` liberally - it makes code clearer and avoids repetition

;; Next lesson: Namespaces and Code Organization
