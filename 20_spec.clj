;; ============================================
;; LESSON 20: CLOJURE.SPEC
;; ============================================
;; Spec is Clojure's built-in library for describing
;; the structure of data and functions. It enables
;; validation, testing, documentation, and more.

;; ============================================
;; 1. WHAT IS SPEC?
;; ============================================

;; Spec allows you to:
;; - Describe the shape of your data
;; - Validate data at runtime
;; - Generate test data automatically
;; - Instrument functions for testing
;; - Document your code with precision

;; Spec is NOT a type system - it's a runtime specification
;; system that embraces Clojure's dynamic nature.

;; ============================================
;; 2. GETTING STARTED
;; ============================================

;; Require the spec namespaces:
(require '[clojure.spec.alpha :as s])
(require '[clojure.spec.gen.alpha :as gen])
(require '[clojure.spec.test.alpha :as stest])

;; ============================================
;; 3. BASIC PREDICATES AS SPECS
;; ============================================

;; Any predicate function can be a spec!

;; Check if data conforms to a spec:
(s/valid? string? "hello")        ;; => true
(s/valid? string? 123)            ;; => false

(s/valid? int? 42)                ;; => true
(s/valid? pos? 5)                 ;; => true
(s/valid? even? 4)                ;; => true

;; Combining predicates:
(s/valid? #(> % 10) 15)           ;; => true

;; ============================================
;; 4. REGISTERING SPECS
;; ============================================

;; Register specs in a global registry with namespaced keywords:

(s/def ::name string?)
(s/def ::age pos-int?)
(s/def ::email #(re-matches #".+@.+\..+" %))

;; Use registered specs:
(s/valid? ::name "Alice")         ;; => true
(s/valid? ::age 30)               ;; => true
(s/valid? ::age -5)               ;; => false
(s/valid? ::email "alice@example.com")  ;; => true

;; ============================================
;; 5. EXPLAIN - UNDERSTANDING FAILURES
;; ============================================

;; explain shows why data doesn't conform:
(s/explain ::age -5)
;; Prints: -5 - failed: pos-int? spec: :user/age

(s/explain ::email "invalid")
;; Prints: "invalid" - failed: (re-matches #".+@.+\..+" %) spec: :user/email

;; explain-str returns a string instead of printing:
(s/explain-str ::age "not a number")
;; => "\"not a number\" - failed: pos-int? spec: :user/age\n"

;; explain-data returns structured data about failures:
(s/explain-data ::age -5)
;; => {:clojure.spec.alpha/problems [...], :clojure.spec.alpha/spec :user/age, ...}

;; ============================================
;; 6. COMPOSITE SPECS: s/and, s/or
;; ============================================

;; s/and - all specs must be satisfied
(s/def ::positive-even (s/and int? even? pos?))

(s/valid? ::positive-even 4)      ;; => true
(s/valid? ::positive-even 3)      ;; => false (not even)
(s/valid? ::positive-even -4)     ;; => false (not positive)

;; s/or - one of the specs must be satisfied (with labels)
(s/def ::identifier (s/or :id int?
                          :name string?
                          :uuid uuid?))

(s/valid? ::identifier 123)       ;; => true
(s/valid? ::identifier "user-1")  ;; => true

;; conform returns the matched branch:
(s/conform ::identifier 123)
;; => [:id 123]

(s/conform ::identifier "user-1")
;; => [:name "user-1"]

;; ============================================
;; 7. COLLECTION SPECS
;; ============================================

;; s/coll-of - collection of items matching a spec
(s/def ::names (s/coll-of string?))
(s/valid? ::names ["Alice" "Bob" "Carol"])  ;; => true
(s/valid? ::names ["Alice" 123])            ;; => false

;; With options:
(s/def ::exactly-three-strings
  (s/coll-of string? :count 3))

(s/def ::unique-ids
  (s/coll-of int? :distinct true :min-count 1))

(s/def ::vector-of-ints
  (s/coll-of int? :kind vector?))

;; s/every - like coll-of but samples for performance
;; Use for large/infinite collections
(s/def ::large-collection (s/every int? :min-count 1000))

;; s/map-of - maps with key and value specs
(s/def ::scores (s/map-of string? int?))
(s/valid? ::scores {"Alice" 95 "Bob" 87})  ;; => true

;; s/tuple - fixed-length with position-specific specs
(s/def ::point (s/tuple number? number?))
(s/valid? ::point [10 20])        ;; => true
(s/valid? ::point [10 20 30])     ;; => false (wrong length)

;; ============================================
;; 8. MAP SPECS WITH s/keys
;; ============================================

;; s/keys defines specs for maps:

(s/def ::person
  (s/keys :req [::name ::age]           ; Required keys
          :opt [::email ::phone]))       ; Optional keys

(s/valid? ::person {::name "Alice" ::age 30})
;; => true

(s/valid? ::person {::name "Alice"})
;; => false (missing ::age)

(s/valid? ::person {::name "Alice" ::age 30 ::email "a@b.com"})
;; => true

;; :req-un and :opt-un for unqualified keys:
(s/def :unq/name string?)
(s/def :unq/age pos-int?)

(s/def ::person-unqualified
  (s/keys :req-un [:unq/name :unq/age]
          :opt-un [:unq/email]))

(s/valid? ::person-unqualified {:name "Bob" :age 25})
;; => true (uses unqualified keys)

;; ============================================
;; 9. NESTED MAP SPECS
;; ============================================

(s/def ::street string?)
(s/def ::city string?)
(s/def ::zip (s/and string? #(re-matches #"\d{5}" %)))

(s/def ::address
  (s/keys :req-un [::street ::city ::zip]))

(s/def ::person-with-address
  (s/keys :req-un [::name ::age]
          :opt-un [::address]))

(s/valid? ::person-with-address
          {:name "Alice"
           :age 30
           :address {:street "123 Main St"
                     :city "Springfield"
                     :zip "12345"}})
;; => true

;; ============================================
;; 10. SEQUENCES WITH s/cat AND REGEX OPS
;; ============================================

;; s/cat describes sequences with named parts:
(s/def ::ingredient (s/cat :quantity number?
                           :unit keyword?
                           :item string?))

(s/valid? ::ingredient [2 :cups "flour"])     ;; => true
(s/conform ::ingredient [2 :cups "flour"])
;; => {:quantity 2, :unit :cups, :item "flour"}

;; Regex operators for sequence patterns:

;; s/* - zero or more
(s/def ::zero-or-more-ints (s/* int?))
(s/valid? ::zero-or-more-ints [])            ;; => true
(s/valid? ::zero-or-more-ints [1 2 3])       ;; => true

;; s/+ - one or more
(s/def ::one-or-more-strings (s/+ string?))
(s/valid? ::one-or-more-strings [])          ;; => false
(s/valid? ::one-or-more-strings ["a" "b"])   ;; => true

;; s/? - zero or one
(s/def ::maybe-keyword (s/? keyword?))
(s/valid? ::maybe-keyword [])                ;; => true
(s/valid? ::maybe-keyword [:foo])            ;; => true
(s/valid? ::maybe-keyword [:foo :bar])       ;; => false

;; Combining regex ops:
(s/def ::command
  (s/cat :operation keyword?
         :args (s/* any?)
         :opts (s/? map?)))

(s/conform ::command [:create "user" {:admin true}])
;; => {:operation :create, :args ["user"], :opts {:admin true}}

(s/conform ::command [:delete])
;; => {:operation :delete, :args [], :opts nil}

;; ============================================
;; 11. FUNCTION SPECS
;; ============================================

;; s/fdef specifies function arguments, return value, and relationships:

(defn add [x y]
  (+ x y))

(s/fdef add
  :args (s/cat :x number? :y number?)
  :ret number?
  :fn (fn [{:keys [args ret]}]
        (= ret (+ (:x args) (:y args)))))

;; Multi-arity function spec:
(defn greet
  ([] "Hello!")
  ([name] (str "Hello, " name "!"))
  ([title name] (str "Hello, " title " " name "!")))

(s/fdef greet
  :args (s/alt :nullary (s/cat)
               :unary (s/cat :name string?)
               :binary (s/cat :title string? :name string?))
  :ret string?)

;; ============================================
;; 12. INSTRUMENTATION
;; ============================================

;; Instrument functions to check args at runtime:

;; Instrument a specific function:
;; (stest/instrument `add)

;; Now calling add with wrong args throws an error:
;; (add "a" "b")  ;; => Error!

;; Unstrument when done:
;; (stest/unstrument `add)

;; Instrument all spec'd functions:
;; (stest/instrument)

;; ============================================
;; 13. GENERATIVE TESTING
;; ============================================

;; Specs can generate test data:

(gen/sample (s/gen int?))
;; => (0 -1 0 1 -4 3 -1 0 -1 -4)

(gen/sample (s/gen string?))
;; => ("" "" "T" "h3" "61jG" ...)

(gen/sample (s/gen ::positive-even))
;; => (2 4 2 8 4 ...)

;; Generate from complex specs:
(gen/generate (s/gen ::person-unqualified))
;; => {:name "x7Qp", :age 42}

(gen/sample (s/gen ::point) 5)
;; => ([-1.0 0.0] [0.5 -2.0] [0.75 1.5] ...)

;; ============================================
;; 14. CUSTOM GENERATORS
;; ============================================

;; Sometimes you need custom generators:

(s/def ::email-with-gen
  (s/with-gen
    #(re-matches #".+@.+\..+" %)
    #(gen/fmap (fn [[name domain tld]]
                 (str name "@" domain "." tld))
               (gen/tuple (gen/string-alphanumeric)
                          (gen/string-alphanumeric)
                          (gen/elements ["com" "org" "net"])))))

(gen/sample (s/gen ::email-with-gen))
;; => ("a@b.com" "xy@abc.org" ...)

;; Use gen/such-that for filtering:
(s/def ::small-even
  (s/with-gen
    (s/and int? even? #(< % 100))
    #(gen/such-that (fn [n] (< n 100))
                    (gen/fmap #(* 2 %) (gen/choose 0 50)))))

;; ============================================
;; 15. TESTING WITH SPEC
;; ============================================

;; Run generative tests on spec'd functions:
;; (stest/check `add)

;; This will:
;; 1. Generate random inputs based on :args spec
;; 2. Call the function
;; 3. Check :ret spec
;; 4. Check :fn relationship
;; 5. Report any failures

;; Test with options:
;; (stest/check `add {:clojure.spec.test.check/opts {:num-tests 1000}})

;; ============================================
;; 16. s/conform AND s/unform
;; ============================================

;; conform parses data according to spec:
(s/def ::config
  (s/cat :command keyword?
         :options (s/* (s/cat :flag keyword? :value any?))))

(def parsed (s/conform ::config [:run :verbose true :output "file.txt"]))
;; => {:command :run,
;;     :options [{:flag :verbose, :value true}
;;               {:flag :output, :value "file.txt"}]}

;; unform converts back to original format:
(s/unform ::config parsed)
;; => (:run :verbose true :output "file.txt")

;; ============================================
;; 17. s/nilable AND s/nonconforming
;; ============================================

;; s/nilable - value or nil
(s/def ::optional-name (s/nilable string?))
(s/valid? ::optional-name nil)          ;; => true
(s/valid? ::optional-name "Alice")      ;; => true

;; s/nonconforming - prevent conform transformation
(s/def ::id-no-tag
  (s/nonconforming
    (s/or :int int? :str string?)))

(s/conform ::id-no-tag 123)
;; => 123 (not [:int 123])

;; ============================================
;; 18. MULTI-SPEC FOR POLYMORPHIC DATA
;; ============================================

;; When data shape varies based on a tag:

(defmulti event-type :type)

(defmethod event-type :user/created [_]
  (s/keys :req-un [::type ::user-id ::email]))

(defmethod event-type :user/deleted [_]
  (s/keys :req-un [::type ::user-id]))

(defmethod event-type :order/placed [_]
  (s/keys :req-un [::type ::order-id ::items ::total]))

(s/def ::type keyword?)
(s/def ::user-id int?)
(s/def ::order-id int?)
(s/def ::items vector?)
(s/def ::total number?)

(s/def ::event (s/multi-spec event-type :type))

(s/valid? ::event {:type :user/created :user-id 1 :email "a@b.com"})
;; => true

(s/valid? ::event {:type :order/placed :order-id 100 :items [] :total 99.99})
;; => true

;; ============================================
;; 19. PRACTICAL EXAMPLE: API VALIDATION
;; ============================================

;; Define specs for an API:

(s/def ::id pos-int?)
(s/def ::username (s/and string? #(re-matches #"[a-z0-9_]+" %)))
(s/def ::password (s/and string? #(>= (count %) 8)))
(s/def ::role #{:admin :user :guest})

(s/def ::create-user-request
  (s/keys :req-un [::username ::password]
          :opt-un [::role]))

(s/def ::update-user-request
  (s/keys :opt-un [::username ::password ::role]))

(defn validate-request [spec data]
  (if (s/valid? spec data)
    {:valid true :data data}
    {:valid false :errors (s/explain-data spec data)}))

(validate-request ::create-user-request
                  {:username "alice" :password "secret123"})
;; => {:valid true, :data {...}}

(validate-request ::create-user-request
                  {:username "INVALID!" :password "short"})
;; => {:valid false, :errors {...}}

;; ============================================
;; 20. SPEC BEST PRACTICES
;; ============================================

;; 1. Use namespaced keywords for specs
;;    (s/def ::my-app.user/name string?)

;; 2. Keep specs close to the code they describe

;; 3. Prefer s/keys for maps - it's self-documenting

;; 4. Use s/and for refined predicates
;;    (s/def ::percentage (s/and number? #(<= 0 % 100)))

;; 5. Provide custom generators for complex specs

;; 6. Use s/explain during development to debug

;; 7. Instrument functions during testing, not production

;; 8. Use spec for documentation - it's precise!

;; 9. Don't over-spec - focus on boundaries and APIs

;; 10. Remember: spec is optional and gradual

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Create specs for a blog post:
;; - title (string, 1-100 chars)
;; - content (string)
;; - author (string)
;; - tags (vector of keywords)
;; - published? (boolean)

;; Exercise 2: Create a spec for a chess move:
;; [:move :e2 :e4] or [:castle :kingside]

;; Exercise 3: Write an fdef for a function that
;; calculates the area of a rectangle (width * height)
;; with appropriate specs and :fn constraint

;; Exercise 4: Create a multi-spec for different
;; geometric shapes (circle, rectangle, triangle)
;; each with appropriate required keys

;; Exercise 5: Use s/cat and regex ops to spec
;; a simple DSL: [:if condition :then action :else action]

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; 1. Specs describe data shape, not types
;; 2. Any predicate can be a spec
;; 3. s/def registers specs globally with namespaced keywords
;; 4. s/valid? checks, s/explain explains failures
;; 5. s/and, s/or compose specs
;; 6. s/keys for maps, s/coll-of for collections
;; 7. s/cat and regex ops for sequences
;; 8. s/fdef specifies functions
;; 9. stest/instrument adds runtime arg checking
;; 10. stest/check does generative testing
;; 11. Spec is gradual - add it where it helps most

;; Next lesson: Macros - Writing Code that Writes Code
