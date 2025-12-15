;; ============================================
;; LESSON 15: MULTIMETHODS
;; ============================================
;; Multimethods provide flexible, extensible polymorphism
;; based on arbitrary dispatch functions.
;; They're one of Clojure's approaches to "expression problem".

;; ============================================
;; 1. WHAT ARE MULTIMETHODS?
;; ============================================

;; Multimethods allow you to define a function that
;; dispatches to different implementations based on
;; the result of a "dispatch function".

;; Unlike class-based polymorphism (Java, Python, etc.),
;; you can dispatch on:
;; - Any attribute of the arguments
;; - Multiple attributes combined
;; - Computed values
;; - Multiple arguments at once

;; ============================================
;; 2. BASIC MULTIMETHOD SYNTAX
;; ============================================

;; Step 1: Define the multimethod with defmulti
;; Step 2: Define implementations with defmethod

;; defmulti syntax:
;; (defmulti name dispatch-fn)

;; defmethod syntax:
;; (defmethod name dispatch-value [args] body)

;; ============================================
;; 3. SIMPLE EXAMPLE: SHAPE AREAS
;; ============================================

;; Define a multimethod that dispatches on :shape key
(defmulti area
  "Calculate the area of a shape."
  :shape)  ; dispatch function - extracts :shape from argument

;; Define implementations for different shapes
(defmethod area :circle
  [{:keys [radius]}]
  (* Math/PI radius radius))

(defmethod area :rectangle
  [{:keys [width height]}]
  (* width height))

(defmethod area :triangle
  [{:keys [base height]}]
  (/ (* base height) 2))

(defmethod area :square
  [{:keys [side]}]
  (* side side))

;; Using the multimethod:
(area {:shape :circle :radius 5})
;; => 78.53981633974483

(area {:shape :rectangle :width 4 :height 6})
;; => 24

(area {:shape :triangle :base 10 :height 5})
;; => 25

(area {:shape :square :side 4})
;; => 16

;; ============================================
;; 4. DEFAULT IMPLEMENTATIONS
;; ============================================

;; Handle unknown dispatch values with :default
(defmethod area :default
  [shape]
  (throw (ex-info "Unknown shape type"
                  {:shape shape})))

;; Now calling with unknown shape gives helpful error:
;; (area {:shape :hexagon :sides 6})
;; => ExceptionInfo Unknown shape type {:shape {:shape :hexagon, :sides 6}}

;; ============================================
;; 5. DISPATCH ON COMPUTED VALUES
;; ============================================

;; The dispatch function can be any function!

(defmulti greeting
  "Greet based on time of day."
  (fn [context]
    (let [hour (:hour context)]
      (cond
        (< hour 12) :morning
        (< hour 17) :afternoon
        (< hour 21) :evening
        :else :night))))

(defmethod greeting :morning [ctx]
  (str "Good morning, " (:name ctx) "!"))

(defmethod greeting :afternoon [ctx]
  (str "Good afternoon, " (:name ctx) "!"))

(defmethod greeting :evening [ctx]
  (str "Good evening, " (:name ctx) "!"))

(defmethod greeting :night [ctx]
  (str "Hello, " (:name ctx) ". Working late?"))

;; Usage:
(greeting {:name "Alice" :hour 9})
;; => "Good morning, Alice!"

(greeting {:name "Bob" :hour 14})
;; => "Good afternoon, Bob!"

(greeting {:name "Carol" :hour 22})
;; => "Hello, Carol. Working late?"

;; ============================================
;; 6. DISPATCH ON MULTIPLE ARGUMENTS
;; ============================================

;; Dispatch function can look at multiple args

(defmulti encounter
  "What happens when two creatures meet?"
  (fn [creature1 creature2]
    [(:type creature1) (:type creature2)]))

(defmethod encounter [:cat :mouse]
  [cat mouse]
  (str (:name cat) " chases " (:name mouse) "!"))

(defmethod encounter [:mouse :cat]
  [mouse cat]
  (str (:name mouse) " runs from " (:name cat) "!"))

(defmethod encounter [:cat :cat]
  [cat1 cat2]
  (str (:name cat1) " and " (:name cat2) " have a staring contest."))

(defmethod encounter [:dog :cat]
  [dog cat]
  (str (:name dog) " barks at " (:name cat) "!"))

(defmethod encounter :default
  [c1 c2]
  (str (:name c1) " and " (:name c2) " ignore each other."))

;; Usage:
(encounter {:type :cat :name "Whiskers"}
           {:type :mouse :name "Jerry"})
;; => "Whiskers chases Jerry!"

(encounter {:type :mouse :name "Jerry"}
           {:type :cat :name "Tom"})
;; => "Jerry runs from Tom!"

(encounter {:type :bird :name "Tweety"}
           {:type :fish :name "Nemo"})
;; => "Tweety and Nemo ignore each other."

;; ============================================
;; 7. TYPE-BASED DISPATCH
;; ============================================

;; Dispatch on actual Clojure/Java types

(defmulti stringify
  "Convert anything to a nice string."
  type)  ; `type` returns the class of the argument

(defmethod stringify java.lang.String [s]
  (str "String: \"" s "\""))

(defmethod stringify java.lang.Long [n]
  (str "Number: " n))

(defmethod stringify clojure.lang.PersistentVector [v]
  (str "Vector with " (count v) " elements"))

(defmethod stringify clojure.lang.PersistentArrayMap [m]
  (str "Map with keys: " (keys m)))

(defmethod stringify clojure.lang.Keyword [k]
  (str "Keyword: " (name k)))

(defmethod stringify :default [x]
  (str "Unknown type: " (type x)))

;; Usage:
(stringify "hello")
;; => "String: \"hello\""

(stringify 42)
;; => "Number: 42"

(stringify [1 2 3])
;; => "Vector with 3 elements"

(stringify {:a 1 :b 2})
;; => "Map with keys: (:a :b)"

;; ============================================
;; 8. HIERARCHIES
;; ============================================

;; Multimethods support ad-hoc hierarchies for
;; "is-a" relationships between dispatch values.

;; Create a hierarchy
(def animal-hierarchy
  (-> (make-hierarchy)
      (derive :dog :mammal)
      (derive :cat :mammal)
      (derive :parrot :bird)
      (derive :eagle :bird)
      (derive :mammal :animal)
      (derive :bird :animal)
      (derive :fish :animal)))

;; Define multimethod with hierarchy
(defmulti speak
  "Make an animal speak."
  :species
  :hierarchy #'animal-hierarchy)

(defmethod speak :dog [_]
  "Woof!")

(defmethod speak :cat [_]
  "Meow!")

(defmethod speak :parrot [animal]
  (str "Squawk! " (:phrase animal)))

(defmethod speak :mammal [_]
  "*generic mammal sounds*")

(defmethod speak :bird [_]
  "*chirp chirp*")

(defmethod speak :animal [_]
  "*animal noises*")

;; Usage - dispatch finds most specific match:
(speak {:species :dog})
;; => "Woof!"

(speak {:species :cat})
;; => "Meow!"

(speak {:species :parrot :phrase "Polly wants a cracker"})
;; => "Squawk! Polly wants a cracker"

;; Falls back to parent when no specific method:
(speak {:species :eagle})
;; => "*chirp chirp*"  (matches :bird)

(speak {:species :fish})
;; => "*animal noises*"  (matches :animal)

;; ============================================
;; 9. USING THE GLOBAL HIERARCHY
;; ============================================

;; You can also use derive/isa? with the global hierarchy
;; (without creating your own)

(derive ::admin ::user)
(derive ::superadmin ::admin)

(isa? ::admin ::user)      ;; => true
(isa? ::superadmin ::user) ;; => true
(isa? ::user ::admin)      ;; => false

(defmulti can-access?
  (fn [user resource] (:role user)))

(defmethod can-access? ::user [user resource]
  (= (:owner resource) (:id user)))

(defmethod can-access? ::admin [user resource]
  true)  ; Admins can access everything

;; superadmin inherits from admin, so it also has access
(can-access? {:role ::superadmin :id 1} {:owner 2})
;; => true

;; ============================================
;; 10. PREFER-METHOD
;; ============================================

;; When multiple methods could match (ambiguity),
;; use prefer-method to resolve it.

(derive ::rect ::shape)
(derive ::rect ::colorable)

(defmulti render :type)

(defmethod render ::shape [s]
  (str "Drawing shape: " s))

(defmethod render ::colorable [s]
  (str "Coloring: " s))

;; Without prefer-method, calling (render {:type ::rect})
;; would be ambiguous. We must specify preference:

(prefer-method render ::shape ::colorable)

(render {:type ::rect})
;; => "Drawing shape: {:type :user/rect}"

;; ============================================
;; 11. INTROSPECTION
;; ============================================

;; Examine a multimethod:

(methods area)
;; => {:circle #fn, :rectangle #fn, :triangle #fn, ...}

(get-method area :circle)
;; => #function[...]

(prefers render)
;; => {::shape #{::colorable}}

;; Remove a method:
;; (remove-method area :circle)

;; Remove all methods:
;; (remove-all-methods area)

;; ============================================
;; 12. PRACTICAL EXAMPLE: SERIALIZATION
;; ============================================

(defmulti serialize
  "Serialize data to different formats."
  (fn [data format] format))

(defmethod serialize :json
  [data _]
  ;; In real code, use cheshire or jsonista
  (str "{\"data\": \"" data "\"}"))

(defmethod serialize :xml
  [data _]
  (str "<data>" data "</data>"))

(defmethod serialize :edn
  [data _]
  (pr-str data))

(defmethod serialize :csv
  [data _]
  (if (sequential? data)
    (clojure.string/join "," (map str data))
    (str data)))

;; Usage:
(serialize {:name "Alice" :age 30} :edn)
;; => "{:name \"Alice\", :age 30}"

(serialize [1 2 3] :csv)
;; => "1,2,3"

;; ============================================
;; 13. PRACTICAL EXAMPLE: EVENT HANDLING
;; ============================================

(defmulti handle-event
  "Handle application events."
  :type)

(defmethod handle-event :user/created
  [{:keys [user]}]
  (println "Welcome email sent to:" (:email user))
  {:action :email-sent :to (:email user)})

(defmethod handle-event :user/deleted
  [{:keys [user-id]}]
  (println "Cleaning up data for user:" user-id)
  {:action :cleanup :user-id user-id})

(defmethod handle-event :order/placed
  [{:keys [order]}]
  (println "Processing order:" (:id order))
  {:action :process-order :order-id (:id order)})

(defmethod handle-event :order/shipped
  [{:keys [order tracking]}]
  (println "Order" (:id order) "shipped. Tracking:" tracking)
  {:action :notify-customer :tracking tracking})

(defmethod handle-event :default
  [event]
  (println "Unknown event type:" (:type event))
  {:action :ignored})

;; Usage:
(handle-event {:type :user/created
               :user {:email "alice@example.com"}})
;; Prints: Welcome email sent to: alice@example.com
;; => {:action :email-sent, :to "alice@example.com"}

;; ============================================
;; 14. MULTIMETHODS VS PROTOCOLS
;; ============================================

;; When to use Multimethods:
;; - Dispatch on arbitrary values (not just type)
;; - Multiple dispatch (based on multiple args)
;; - Ad-hoc hierarchies
;; - Open extension by value
;; - When you need maximum flexibility

;; When to use Protocols:
;; - Dispatch on type of first argument only
;; - Better performance (direct dispatch)
;; - Interface-like grouping of functions
;; - Interop with Java interfaces
;; - When type-based dispatch is sufficient

;; Multimethods: More flexible, slightly slower
;; Protocols: Less flexible, faster

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Create a multimethod `format-data` that
;; dispatches on :format key and formats a number:
;; {:format :currency :value 1234.56} => "$1,234.56"
;; {:format :percent :value 0.856} => "85.6%"
;; {:format :scientific :value 12345} => "1.23e4"

;; Exercise 2: Create a multimethod `validate` that
;; validates different types of form fields:
;; {:type :email :value "test@example.com"}
;; {:type :phone :value "555-1234"}
;; {:type :zip :value "12345"}

;; Exercise 3: Create a hierarchy for vehicles and a
;; `fuel-efficiency` multimethod that returns different
;; values for cars, trucks, motorcycles, etc.

;; Exercise 4: Create a multimethod that dispatches on
;; two arguments: operation type and data type
;; (process :transform :text "hello")
;; (process :validate :number 42)

;; Exercise 5: Create an extensible logging multimethod
;; that can log to :console, :file, or :remote

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; 1. defmulti defines the dispatch function
;; 2. defmethod provides implementations for dispatch values
;; 3. :default handles unmatched dispatch values
;; 4. Dispatch can be on any computed value, not just type
;; 5. Multiple dispatch is possible (dispatch on multiple args)
;; 6. Hierarchies allow "is-a" relationships between values
;; 7. prefer-method resolves ambiguous dispatches
;; 8. Multimethods are open - anyone can add new methods
;; 9. Use multimethods for flexibility, protocols for performance

;; Next lesson: Protocols and Records
