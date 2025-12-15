;; ============================================
;; LESSON 14: PROTOCOLS AND RECORDS
;; ============================================
;; Protocols and records provide Clojure's approach
;; to polymorphism and structured data types.
;; They offer performance and interop benefits
;; while staying true to functional principles.

;; ============================================
;; 1. THE PROBLEM: AD-HOC POLYMORPHISM
;; ============================================

;; Sometimes we need different behavior based on type.
;; We could use multimethods, but they have overhead.
;; Protocols provide fast, type-based dispatch.

;; Consider: We want to serialize different types to JSON
;; Maps, vectors, strings, numbers all need different handling.

;; ============================================
;; 2. DEFINING PROTOCOLS
;; ============================================

;; A protocol is a named set of functions with their signatures.
;; Think of it like an interface in Java or a trait in Rust.

(defprotocol Stringable
  "Protocol for things that can be converted to strings."
  (stringify [this] "Convert this to a string representation."))

;; Protocols can have multiple methods:
(defprotocol Serializable
  "Protocol for serialization."
  (serialize [this] "Serialize to a string.")
  (deserialize [this data] "Deserialize from a string."))

;; Methods can have multiple arities:
(defprotocol Cacheable
  "Protocol for cacheable objects."
  (cache-key [this] "Generate a cache key.")
  (cache [this] [this ttl] "Cache the object, optionally with TTL."))

;; ============================================
;; 3. IMPLEMENTING PROTOCOLS WITH extend-type
;; ============================================

;; Extend existing types to implement a protocol:

(extend-type String
  Stringable
  (stringify [this]
    (str "\"" this "\"")))

(extend-type Long
  Stringable
  (stringify [this]
    (str this)))

(extend-type clojure.lang.PersistentVector
  Stringable
  (stringify [this]
    (str "[" (clojure.string/join ", " (map stringify this)) "]")))

(extend-type clojure.lang.PersistentArrayMap
  Stringable
  (stringify [this]
    (str "{"
         (clojure.string/join ", "
           (map (fn [[k v]]
                  (str (stringify k) ": " (stringify v)))
                this))
         "}")))

;; Now we can use it:
(stringify "hello")           ;; => "\"hello\""
(stringify 42)                ;; => "42"
(stringify [1 2 3])           ;; => "[1, 2, 3]"
(stringify {:a 1 :b 2})       ;; => "{:a: 1, :b: 2}"

;; ============================================
;; 4. IMPLEMENTING PROTOCOLS WITH extend-protocol
;; ============================================

;; extend-protocol lets you implement one protocol for multiple types:

(defprotocol Measurable
  "Things that have a measurable size."
  (size [this] "Return the size of this thing."))

(extend-protocol Measurable
  String
  (size [this] (count this))

  clojure.lang.PersistentVector
  (size [this] (count this))

  clojure.lang.PersistentArrayMap
  (size [this] (count this))

  clojure.lang.PersistentHashSet
  (size [this] (count this))

  nil
  (size [_] 0)

  Object  ; Default for anything else
  (size [this] 1))

(size "hello")     ;; => 5
(size [1 2 3])     ;; => 3
(size {:a 1})      ;; => 1
(size nil)         ;; => 0
(size 42)          ;; => 1

;; ============================================
;; 5. RECORDS: STRUCTURED DATA TYPES
;; ============================================

;; Records are like maps but:
;; - Have a defined set of fields
;; - Are their own type (for protocol dispatch)
;; - Have faster field access
;; - Can implement protocols inline

;; Define a record:
(defrecord Person [first-name last-name email age])

;; Creating records:

;; Positional constructor (generated automatically):
(def alice (->Person "Alice" "Smith" "alice@example.com" 30))

;; Map constructor (generated automatically):
(def bob (map->Person {:first-name "Bob"
                       :last-name "Jones"
                       :email "bob@example.com"
                       :age 25}))

;; Records work like maps:
(:first-name alice)          ;; => "Alice"
(:age bob)                   ;; => 25
(get alice :email)           ;; => "alice@example.com"

;; Records are associative:
(assoc alice :age 31)        ;; => #Person{:first-name "Alice", ..., :age 31}
(update bob :age inc)        ;; => #Person{..., :age 26}

;; Adding new keys returns a record:
(assoc alice :nickname "Al") ;; Still a Person record

;; Dissoc on defined fields returns a map (not a record):
(dissoc alice :age)          ;; => regular map, not Person

;; Records are their own type:
(type alice)                 ;; => user.Person
(instance? Person alice)     ;; => true

;; ============================================
;; 6. RECORDS WITH INLINE PROTOCOL IMPLEMENTATION
;; ============================================

;; This is the preferred way: implement protocols when defining the record

(defprotocol Greetable
  (greet [this] "Return a greeting."))

(defprotocol Contactable
  (primary-contact [this] "Return primary contact method."))

(defrecord Employee [id name email department]
  Greetable
  (greet [this]
    (str "Hello, I'm " name " from " department))

  Contactable
  (primary-contact [this]
    email))

(def emp (->Employee 1 "Carol" "carol@company.com" "Engineering"))

(greet emp)              ;; => "Hello, I'm Carol from Engineering"
(primary-contact emp)    ;; => "carol@company.com"

;; ============================================
;; 7. IMPLEMENTING JAVA INTERFACES
;; ============================================

;; Records can also implement Java interfaces:

(defrecord Point [x y]
  Stringable
  (stringify [this]
    (str "Point(" x ", " y ")"))

  ;; Implement Java's Comparable interface
  java.lang.Comparable
  (compareTo [this other]
    (let [dist-this (Math/sqrt (+ (* x x) (* y y)))
          dist-other (Math/sqrt (+ (* (:x other) (:x other))
                                   (* (:y other) (:y other))))]
      (compare dist-this dist-other))))

(def p1 (->Point 3 4))   ; distance 5
(def p2 (->Point 1 1))   ; distance ~1.41

(.compareTo p1 p2)       ;; => 1 (p1 is farther from origin)
(sort [p1 p2])           ;; => [p2 p1]

;; ============================================
;; 8. CONSTRUCTOR FUNCTIONS
;; ============================================

;; It's good practice to create smart constructor functions:

(defrecord User [id username email created-at])

;; Smart constructor with validation and defaults:
(defn create-user
  "Create a new User with validation."
  [{:keys [id username email] :as attrs}]
  {:pre [(string? username)
         (string? email)
         (re-matches #".+@.+\..+" email)]}
  (map->User (merge {:created-at (java.time.Instant/now)}
                    attrs)))

;; (create-user {:id 1 :username "alice" :email "alice@example.com"})
;; => #User{:id 1, :username "alice", :email "alice@example.com", :created-at #inst...}

;; (create-user {:id 1 :username "alice" :email "invalid"})
;; => AssertionError (precondition failed)

;; Factory functions for different scenarios:
(defn create-guest-user []
  (map->User {:id nil
              :username "guest"
              :email "guest@example.com"
              :created-at (java.time.Instant/now)}))

;; ============================================
;; 9. PROTOCOL INTROSPECTION
;; ============================================

;; Check if a type satisfies a protocol:
(satisfies? Greetable emp)          ;; => true
(satisfies? Greetable "string")     ;; => false

;; Check if a protocol is extended to a type:
(extends? Measurable String)        ;; => true
(extends? Measurable Integer)       ;; => false (unless extended)

;; Get all types that extend a protocol:
(extenders Measurable)
;; => (java.lang.String clojure.lang.PersistentVector ...)

;; ============================================
;; 10. REIFY: ANONYMOUS PROTOCOL IMPLEMENTATION
;; ============================================

;; Create a one-off implementation without defining a type:

(defn make-counter [initial]
  (let [count (atom initial)]
    (reify
      Stringable
      (stringify [_]
        (str "Counter: " @count))

      clojure.lang.IDeref  ; Implement IDeref to support @
      (deref [_]
        @count)

      clojure.lang.IFn     ; Make it callable
      (invoke [_]
        (swap! count inc)))))

(def c (make-counter 0))
@c                  ;; => 0
(c)                 ;; => 1 (increments)
(c)                 ;; => 2
@c                  ;; => 2
(stringify c)       ;; => "Counter: 2"

;; ============================================
;; 11. PRACTICAL EXAMPLE: SHAPE HIERARCHY
;; ============================================

(defprotocol Shape
  "Protocol for geometric shapes."
  (area [this] "Calculate the area.")
  (perimeter [this] "Calculate the perimeter.")
  (scale [this factor] "Scale the shape."))

(defrecord Rectangle [width height]
  Shape
  (area [_]
    (* width height))
  (perimeter [_]
    (* 2 (+ width height)))
  (scale [_ factor]
    (->Rectangle (* width factor) (* height factor))))

(defrecord Circle [radius]
  Shape
  (area [_]
    (* Math/PI radius radius))
  (perimeter [_]  ; circumference
    (* 2 Math/PI radius))
  (scale [_ factor]
    (->Circle (* radius factor))))

(defrecord Triangle [a b c]  ; side lengths
  Shape
  (area [_]
    (let [s (/ (+ a b c) 2)]  ; semi-perimeter
      (Math/sqrt (* s (- s a) (- s b) (- s c)))))
  (perimeter [_]
    (+ a b c))
  (scale [_ factor]
    (->Triangle (* a factor) (* b factor) (* c factor))))

;; Usage:
(def shapes [(->Rectangle 10 5)
             (->Circle 7)
             (->Triangle 3 4 5)])

(map area shapes)
;; => (50 153.93804002589985 6.0)

(map perimeter shapes)
;; => (30 43.982297150257104 12)

;; Total area of all shapes:
(reduce + (map area shapes))
;; => ~209.94

;; Scale all shapes by 2:
(map #(scale % 2) shapes)
;; => [#Rectangle{...} #Circle{...} #Triangle{...}]

;; ============================================
;; 12. PRACTICAL EXAMPLE: DATA STORAGE
;; ============================================

(defprotocol DataStore
  "Protocol for data storage backends."
  (store! [this key value] "Store a value.")
  (fetch [this key] "Fetch a value.")
  (delete! [this key] "Delete a value.")
  (list-keys [this] "List all keys."))

;; In-memory implementation:
(defrecord MemoryStore [data]
  DataStore
  (store! [this key value]
    (swap! data assoc key value)
    this)
  (fetch [_ key]
    (get @data key))
  (delete! [this key]
    (swap! data dissoc key)
    this)
  (list-keys [_]
    (keys @data)))

(defn create-memory-store []
  (->MemoryStore (atom {})))

;; Usage:
(def store (create-memory-store))

(-> store
    (store! :user-1 {:name "Alice"})
    (store! :user-2 {:name "Bob"}))

(fetch store :user-1)       ;; => {:name "Alice"}
(list-keys store)           ;; => (:user-1 :user-2)

;; You could easily add other implementations:
;; (defrecord RedisStore [connection] ...)
;; (defrecord FileStore [directory] ...)

;; ============================================
;; 13. RECORDS VS MAPS
;; ============================================

;; When to use Records:
;; - You need to dispatch on type (protocols)
;; - You want faster field access
;; - You have a fixed, known set of fields
;; - You want Java interop benefits
;; - You want to implement interfaces

;; When to use Maps:
;; - Data is dynamic or has varying fields
;; - You need easy serialization (maps serialize simply)
;; - You're doing exploratory coding
;; - Fields change frequently during development

;; Performance comparison:
(def person-map {:first-name "Alice" :last-name "Smith"})
(def person-rec (->Person "Alice" "Smith" "a@b.com" 30))

;; Field access is faster on records:
;; (:first-name person-rec)  ; ~3x faster than map lookup

;; ============================================
;; 14. PROTOCOL BEST PRACTICES
;; ============================================

;; 1. Keep protocols small and focused
(defprotocol Identifiable
  (id [this]))

(defprotocol Timestamped
  (created-at [this])
  (updated-at [this]))

;; 2. Use protocols for behavior, not data access
;; Bad: (defprotocol HasName (get-name [this]))
;; Good: Just use (:name thing)

;; 3. Provide default implementations when sensible
(defprotocol Printable
  (print-it [this]))

(extend-type Object
  Printable
  (print-it [this]
    (println (str this))))

;; 4. Document protocols thoroughly
(defprotocol Validatable
  "Protocol for objects that can validate themselves.

   Implementations should return either:
   - {:valid true} for valid objects
   - {:valid false :errors [...]} for invalid objects"
  (validate [this] "Validate and return result map."))

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Define a protocol `Describable` with a method
;; `describe` that returns a human-readable description.
;; Implement it for String, Number, and Vector.

;; Exercise 2: Create a `Product` record with fields
;; `:id`, `:name`, `:price`, `:quantity`.
;; Implement a protocol for calculating total value and
;; checking if in stock.

;; Exercise 3: Create a `Logger` protocol with methods
;; `log-info`, `log-warn`, `log-error`.
;; Implement a `ConsoleLogger` and a `FileLogger` record.

;; Exercise 4: Define a protocol `Reversible` with method
;; `reverse-it`. Implement for String and Vector.
;; Then create a record `Pair` that reverses its two elements.

;; Exercise 5: Create a simple `Queue` record that implements
;; protocols for `enqueue`, `dequeue`, and `peek-front`.

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; 1. Protocols define a set of functions for polymorphic dispatch
;; 2. extend-type adds protocol support to one type
;; 3. extend-protocol adds one protocol to multiple types
;; 4. Records are map-like types with fixed fields
;; 5. Records can implement protocols inline (preferred)
;; 6. Records provide faster field access than maps
;; 7. reify creates anonymous protocol implementations
;; 8. Use protocols for behavior, not data access
;; 9. satisfies? checks if a value implements a protocol
;; 10. Records + Protocols = Clojure's answer to OOP polymorphism

;; Next lesson: Namespaces and Project Structure
