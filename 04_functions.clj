;; ============================================
;; LESSON 04: Functions
;; ============================================
;; Functions are the heart of Clojure. Everything you do in Clojure
;; revolves around functions. Let's master them!

;; ============================================
;; 1. Defining Functions with defn
;; ============================================

;; Basic function definition
(defn greet []
  "Hello, World!")

(greet)  ;=> "Hello, World!"

;; Function with one parameter
(defn greet-person [name]
  (str "Hello, " name "!"))

(greet-person "Alice")  ;=> "Hello, Alice!"

;; Function with multiple parameters
(defn add [a b]
  (+ a b))

(add 3 5)  ;=> 8

;; ============================================
;; 2. Function Documentation (Docstrings)
;; ============================================

;; Always document your functions!
(defn calculate-area
  "Calculates the area of a rectangle given width and height."
  [width height]
  (* width height))

;; View documentation
(doc calculate-area)

;; More detailed documentation
(defn calculate-bmi
  "Calculates Body Mass Index (BMI).

  Parameters:
    weight - weight in kilograms
    height - height in meters

  Returns:
    BMI as a floating point number"
  [weight height]
  (/ weight (* height height)))

(calculate-bmi 70 1.75)  ;=> ~22.86

;; ============================================
;; 3. Anonymous Functions (Lambdas)
;; ============================================

;; Using fn - the full form
(fn [x] (* x x))

;; Using it immediately
((fn [x] (* x x)) 5)  ;=> 25

;; Storing in a variable (same as defn)
(def square (fn [x] (* x x)))
(square 4)  ;=> 16

;; Shorthand: #() syntax
#(* % %)        ; Single argument: % refers to it
#(+ %1 %2)      ; Multiple arguments: %1, %2, %3, etc.
#(str %1 " " %2 " " %3)  ; Three arguments

;; Examples of shorthand
(#(* % %) 6)           ;=> 36
(#(+ %1 %2) 3 4)       ;=> 7
(#(str "Hello, " %) "World")  ;=> "Hello, World"

;; When to use which:
;; - Use #() for very short, simple functions
;; - Use fn for anything with multiple expressions
;; - Use defn for reusable named functions

;; ============================================
;; 4. Multiple Arities (Function Overloading)
;; ============================================

;; A function can have different behaviors based on
;; how many arguments it receives

(defn greet-flexible
  "Greets with different levels of formality."
  ([]                        ; No arguments
   "Hello!")
  ([name]                    ; One argument
   (str "Hello, " name "!"))
  ([title name]              ; Two arguments
   (str "Hello, " title " " name "!")))

(greet-flexible)                  ;=> "Hello!"
(greet-flexible "Alice")          ;=> "Hello, Alice!"
(greet-flexible "Dr." "Smith")    ;=> "Hello, Dr. Smith!"

;; Common pattern: default values
(defn power
  "Raises base to exponent. Defaults to squaring."
  ([base]
   (power base 2))       ; Call 2-arity version
  ([base exponent]
   (Math/pow base exponent)))

(power 3)      ;=> 9.0   (3 squared)
(power 2 10)   ;=> 1024.0 (2 to the 10th)

;; ============================================
;; 5. Variadic Functions (Variable Arguments)
;; ============================================

;; Use & to collect remaining arguments into a list
(defn sum-all
  "Sums any number of arguments."
  [& numbers]
  (reduce + 0 numbers))

(sum-all)           ;=> 0
(sum-all 1)         ;=> 1
(sum-all 1 2 3)     ;=> 6
(sum-all 1 2 3 4 5) ;=> 15

;; Combining regular and variadic parameters
(defn greet-everyone
  "Greets a primary person, then lists others."
  [main-person & others]
  (str "Hello, " main-person "! "
       (if (seq others)
         (str "And hello to: " (clojure.string/join ", " others))
         "You're the only one here.")))

(greet-everyone "Alice")
;=> "Hello, Alice! You're the only one here."

(greet-everyone "Alice" "Bob" "Carol")
;=> "Hello, Alice! And hello to: Bob, Carol"

;; ============================================
;; 6. Destructuring in Function Parameters
;; ============================================

;; Vector destructuring
(defn first-two [[a b]]
  (str "First: " a ", Second: " b))

(first-two [1 2 3 4])  ;=> "First: 1, Second: 2"

;; Map destructuring with :keys
(defn greet-user [{:keys [name age]}]
  (str name " is " age " years old"))

(greet-user {:name "Alice" :age 30})  ;=> "Alice is 30 years old"

;; With default values
(defn greet-user-default [{:keys [name age] :or {name "Guest" age "unknown"}}]
  (str name " is " age " years old"))

(greet-user-default {})  ;=> "Guest is unknown years old"

;; Keeping the original map with :as
(defn process-user [{:keys [name] :as user}]
  (println "Processing user:" name)
  (assoc user :processed true))

(process-user {:name "Alice" :age 30})
;=> {:name "Alice", :age 30, :processed true}

;; ============================================
;; 7. Higher-Order Functions
;; ============================================

;; Functions that take functions as arguments
;; or return functions as results

;; Taking a function as argument
(defn apply-twice [f x]
  (f (f x)))

(apply-twice inc 5)        ;=> 7 (5 -> 6 -> 7)
(apply-twice #(* % 2) 3)   ;=> 12 (3 -> 6 -> 12)

;; Returning a function
(defn make-adder [n]
  (fn [x] (+ x n)))

(def add-five (make-adder 5))
(add-five 10)   ;=> 15
(add-five 100)  ;=> 105

;; Another example: multiplier factory
(defn make-multiplier [n]
  #(* % n))

(def double-it (make-multiplier 2))
(def triple-it (make-multiplier 3))

(double-it 5)  ;=> 10
(triple-it 5)  ;=> 15

;; ============================================
;; 8. Closures
;; ============================================

;; Functions "close over" variables in their environment

(defn make-counter []
  (let [count (atom 0)]
    (fn []
      (swap! count inc))))

(def counter-a (make-counter))
(def counter-b (make-counter))

(counter-a)  ;=> 1
(counter-a)  ;=> 2
(counter-a)  ;=> 3
(counter-b)  ;=> 1 (separate counter!)

;; Each counter has its own private state

;; ============================================
;; 9. Partial Application
;; ============================================

;; partial pre-fills some arguments
(def add-10 (partial + 10))
(add-10 5)    ;=> 15
(add-10 100)  ;=> 110

(def greet-hello (partial str "Hello, "))
(greet-hello "World")  ;=> "Hello, World"

;; Useful for creating specialized functions
(defn log-message [level timestamp message]
  (str "[" level "] " timestamp ": " message))

(def log-error (partial log-message "ERROR"))
(def log-info (partial log-message "INFO"))

(log-error "2024-01-15" "Something went wrong")
;=> "[ERROR] 2024-01-15: Something went wrong"

;; ============================================
;; 10. Function Composition
;; ============================================

;; comp composes functions (right to left)
(def process (comp str inc #(* % 2)))
;; Equivalent to: (str (inc (* x 2)))

(process 5)  ;=> "11"  (5 * 2 = 10, + 1 = 11, -> "11")

;; More readable example
(def clean-string
  (comp clojure.string/trim
        clojure.string/lower-case))

(clean-string "  HELLO WORLD  ")  ;=> "hello world"

;; Pipeline with -> (threading macro, covered later)
;; is often clearer for many transformations

;; ============================================
;; 11. Pre and Post Conditions
;; ============================================

;; Add assertions to your functions
(defn divide [a b]
  {:pre [(not= b 0)]          ; Precondition: b can't be 0
   :post [(number? %)]}        ; Postcondition: result is a number
  (/ a b))

(divide 10 2)   ;=> 5
;; (divide 10 0) ;=> AssertionError

(defn adult? [age]
  {:pre [(integer? age)
         (>= age 0)]}
  (>= age 18))

(adult? 25)   ;=> true
(adult? 10)   ;=> false
;; (adult? -5) ;=> AssertionError (precondition failed)

;; ============================================
;; 12. Multi-methods (Preview)
;; ============================================

;; Dispatch on arbitrary criteria (polymorphism)
(defmulti area :shape)

(defmethod area :circle [{:keys [radius]}]
  (* Math/PI radius radius))

(defmethod area :rectangle [{:keys [width height]}]
  (* width height))

(defmethod area :triangle [{:keys [base height]}]
  (/ (* base height) 2))

(area {:shape :circle :radius 5})           ;=> ~78.54
(area {:shape :rectangle :width 4 :height 3}) ;=> 12
(area {:shape :triangle :base 10 :height 5})  ;=> 25

;; ============================================
;; 13. Recursion
;; ============================================

;; Simple recursion
(defn factorial [n]
  (if (<= n 1)
    1
    (* n (factorial (dec n)))))

(factorial 5)  ;=> 120

;; Tail recursion with recur (stack-safe!)
(defn factorial-tail [n]
  (loop [n n
         acc 1]
    (if (<= n 1)
      acc
      (recur (dec n) (* acc n)))))

(factorial-tail 5)    ;=> 120
(factorial-tail 1000) ;=> huge number (won't stack overflow!)

;; Always use recur for recursive calls when possible

;; ============================================
;; 14. Local Functions with letfn
;; ============================================

;; Define local helper functions
(defn process-data [data]
  (letfn [(double-it [x] (* 2 x))
          (add-one [x] (+ 1 x))]
    (map (comp add-one double-it) data)))

(process-data [1 2 3 4])  ;=> (3 5 7 9)

;; letfn allows mutual recursion
(letfn [(even? [n]
          (if (zero? n)
            true
            (odd? (dec n))))
        (odd? [n]
          (if (zero? n)
            false
            (even? (dec n))))]
  [(even? 10) (odd? 10)])  ;=> [true false]

;; ============================================
;; Exercises
;; ============================================

;; Exercise 1: Write a function that takes a name and returns
;; a greeting. If no name given, use "Stranger"
;; (greeting) => "Hello, Stranger!"
;; (greeting "Alice") => "Hello, Alice!"

;; Exercise 2: Write a variadic function 'average' that calculates
;; the average of any number of arguments
;; (average 1 2 3 4 5) => 3

;; Exercise 3: Create a function 'make-validator' that takes a predicate
;; and returns a function that returns {:valid true/false :value x}
;; (def positive? (make-validator pos?))
;; (positive? 5) => {:valid true :value 5}
;; (positive? -1) => {:valid false :value -1}

;; Exercise 4: Write a recursive function to calculate Fibonacci numbers
;; Then write a tail-recursive version using loop/recur
;; (fib 10) => 55

;; Exercise 5: Using destructuring, write a function that takes a map
;; with :first-name, :last-name, and optional :title (default "")
;; and returns a formatted full name
;; (format-name {:first-name "John" :last-name "Doe"}) => "John Doe"
;; (format-name {:first-name "Jane" :last-name "Smith" :title "Dr."})
;; => "Dr. Jane Smith"

;; ============================================
;; Solutions
;; ============================================

;; Solution 1
(defn greeting
  ([] (greeting "Stranger"))
  ([name] (str "Hello, " name "!")))

;; Solution 2
(defn average [& nums]
  (/ (reduce + nums) (count nums)))

;; Solution 3
(defn make-validator [pred]
  (fn [x]
    {:valid (pred x) :value x}))

;; Solution 4
(defn fib [n]
  (if (<= n 1)
    n
    (+ (fib (- n 1)) (fib (- n 2)))))

(defn fib-tail [n]
  (loop [a 0 b 1 cnt n]
    (if (zero? cnt)
      a
      (recur b (+ a b) (dec cnt)))))

;; Solution 5
(defn format-name [{:keys [first-name last-name title] :or {title ""}}]
  (clojure.string/trim (str title " " first-name " " last-name)))

;; ============================================
;; Next Lesson: Collections Deep Dive
;; We'll explore lists, vectors, maps, and sets in depth!
;; ============================================
