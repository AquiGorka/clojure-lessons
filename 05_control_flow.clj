;; ============================================
;; LESSON 05: CONTROL FLOW IN CLOJURE
;; ============================================
;; Learn how to make decisions and control
;; program execution in Clojure.

;; ============================================
;; 1. THE `if` EXPRESSION
;; ============================================
;; The most basic conditional. Returns one value
;; if true, another if false.

;; Syntax: (if test then-expr else-expr)

(if true
  "yes"
  "no")
;; => "yes"

(if false
  "yes"
  "no")
;; => "no"

(if (> 5 3)
  "five is greater"
  "five is not greater")
;; => "five is greater"

;; The else branch is optional (returns nil if false)
(if true "hello")
;; => "hello"

(if false "hello")
;; => nil

;; IMPORTANT: `if` can only have ONE expression per branch
;; Use `do` to group multiple expressions
(if (> 10 5)
  (do
    (println "10 is greater than 5")
    (println "This is the then branch")
    "result-true")
  (do
    (println "This won't print")
    "result-false"))
;; Prints two lines, returns "result-true"

;; ============================================
;; 2. TRUTHINESS AND FALSINESS
;; ============================================
;; In Clojure, ONLY two values are "falsy":
;;   - false
;;   - nil
;; EVERYTHING else is "truthy"!

(if nil "truthy" "falsy")    ;; => "falsy"
(if false "truthy" "falsy")  ;; => "falsy"

;; All of these are truthy:
(if 0 "truthy" "falsy")        ;; => "truthy" (unlike many languages!)
(if "" "truthy" "falsy")       ;; => "truthy" (empty string is truthy!)
(if [] "truthy" "falsy")       ;; => "truthy" (empty vector is truthy!)
(if '() "truthy" "falsy")      ;; => "truthy" (empty list is truthy!)
(if {} "truthy" "falsy")       ;; => "truthy" (empty map is truthy!)
(if :anything "truthy" "falsy") ;; => "truthy"

;; ============================================
;; 3. `when` - ONE-BRANCH CONDITIONAL
;; ============================================
;; Use `when` when you only care about the true case.
;; It implicitly wraps body in `do`, so multiple expressions are allowed.

(when true
  (println "First thing")
  (println "Second thing")
  "final result")
;; Prints two lines, returns "final result"

(when false
  (println "This won't print")
  "won't return this")
;; => nil (does nothing, returns nil)

;; `when-not` is the opposite
(when-not false
  "This runs because condition is false")
;; => "This runs because condition is false"

;; ============================================
;; 4. `cond` - MULTIPLE CONDITIONS
;; ============================================
;; Like if-else-if chains in other languages.
;; Tests conditions in order, returns first true.

(defn grade [score]
  (cond
    (>= score 90) "A"
    (>= score 80) "B"
    (>= score 70) "C"
    (>= score 60) "D"
    :else "F"))  ;; :else is just a truthy value (convention)

(grade 95)  ;; => "A"
(grade 82)  ;; => "B"
(grade 45)  ;; => "F"

;; Another example
(defn describe-number [n]
  (cond
    (neg? n) "negative"
    (zero? n) "zero"
    (even? n) "positive and even"
    :else "positive and odd"))

(describe-number -5)  ;; => "negative"
(describe-number 0)   ;; => "zero"
(describe-number 4)   ;; => "positive and even"
(describe-number 7)   ;; => "positive and odd"

;; ============================================
;; 5. `condp` - CONDITION WITH PREDICATE
;; ============================================
;; Like a switch statement with a predicate function.

;; Syntax: (condp pred expr & clauses)
;; Tests (pred test-val expr) for each clause

(defn describe-size [n]
  (condp < n        ;; Tests (< test-val n) for each
    100 "huge"      ;; (< 100 n) - is n > 100?
    50  "large"     ;; (< 50 n)  - is n > 50?
    20  "medium"    ;; (< 20 n)  - is n > 20?
    10  "small"     ;; (< 10 n)  - is n > 10?
    "tiny"))        ;; default

(describe-size 150)  ;; => "huge"
(describe-size 75)   ;; => "large"
(describe-size 5)    ;; => "tiny"

;; Common use: equality testing
(defn day-type [day]
  (condp = day
    :monday "start of week"
    :friday "almost weekend!"
    :saturday "weekend!"
    :sunday "weekend!"
    "regular day"))

(day-type :friday)    ;; => "almost weekend!"
(day-type :wednesday) ;; => "regular day"

;; ============================================
;; 6. `case` - FAST DISPATCH ON CONSTANTS
;; ============================================
;; Like switch in other languages. Uses compile-time
;; constants for fast O(1) dispatch.

(defn color-code [color]
  (case color
    :red 1
    :green 2
    :blue 3
    :yellow 4
    0))  ;; default value (no keyword needed)

(color-code :red)    ;; => 1
(color-code :purple) ;; => 0 (default)

;; Multiple values can map to same result
(defn weekend? [day]
  (case day
    (:saturday :sunday) true
    false))

(weekend? :saturday)  ;; => true
(weekend? :monday)    ;; => false

;; CAUTION: Without default, throws exception on no match
(defn strict-color [color]
  (case color
    :red "red"
    :blue "blue"))

;; (strict-color :green) ;; Throws IllegalArgumentException!

;; ============================================
;; 7. BOOLEAN OPERATORS
;; ============================================

;; `and` - returns first falsy value or last value
(and true true)      ;; => true
(and true false)     ;; => false
(and nil "hello")    ;; => nil
(and "a" "b" "c")    ;; => "c" (all truthy, returns last)
(and "a" nil "c")    ;; => nil (short-circuits at nil)

;; `or` - returns first truthy value or last value
(or false true)      ;; => true
(or nil false)       ;; => false (both falsy, returns last)
(or nil "hello")     ;; => "hello"
(or false nil "a")   ;; => "a" (first truthy)
(or "a" "b" "c")     ;; => "a" (short-circuits at "a")

;; `not` - returns boolean opposite
(not true)   ;; => false
(not false)  ;; => true
(not nil)    ;; => true
(not "hi")   ;; => false

;; Common pattern: default values with `or`
(defn greet [name]
  (str "Hello, " (or name "stranger") "!"))

(greet "Alice")  ;; => "Hello, Alice!"
(greet nil)      ;; => "Hello, stranger!"

;; ============================================
;; 8. `if-let` AND `when-let`
;; ============================================
;; Combine binding with conditional. Very useful
;; for checking if something exists before using it.

;; if-let binds and tests in one step
(if-let [x (first [1 2 3])]
  (str "First element is: " x)
  "List was empty")
;; => "First element is: 1"

(if-let [x (first [])]
  (str "First element is: " x)
  "List was empty")
;; => "List was empty" (first of [] is nil)

;; when-let for single branch
(when-let [user {:name "Alice" :age 30}]
  (println "Found user!")
  (:name user))
;; => "Alice"

;; Practical example: finding in a map
(defn get-user-email [users user-id]
  (if-let [user (get users user-id)]
    (:email user)
    "User not found"))

(def users {1 {:name "Alice" :email "alice@example.com"}
            2 {:name "Bob" :email "bob@example.com"}})

(get-user-email users 1)  ;; => "alice@example.com"
(get-user-email users 99) ;; => "User not found"

;; ============================================
;; 9. `if-some` AND `when-some`
;; ============================================
;; Like if-let/when-let, but only checks for nil
;; (false is still considered "some" value)

(if-some [x false]
  (str "Value is: " x)
  "Was nil")
;; => "Value is: false"

(if-let [x false]
  (str "Value is: " x)
  "Was nil")
;; => "Was nil" (false is falsy)

(if-some [x nil]
  (str "Value is: " x)
  "Was nil")
;; => "Was nil"

;; ============================================
;; 10. PRACTICAL EXAMPLES
;; ============================================

;; FizzBuzz
(defn fizzbuzz [n]
  (cond
    (zero? (mod n 15)) "FizzBuzz"
    (zero? (mod n 3)) "Fizz"
    (zero? (mod n 5)) "Buzz"
    :else n))

(map fizzbuzz (range 1 16))
;; => (1 2 "Fizz" 4 "Buzz" "Fizz" 7 8 "Fizz" "Buzz" 11 "Fizz" 13 14 "FizzBuzz")

;; Simple calculator
(defn calculate [op a b]
  (case op
    :add (+ a b)
    :subtract (- a b)
    :multiply (* a b)
    :divide (if (zero? b)
              "Cannot divide by zero"
              (/ a b))
    "Unknown operation"))

(calculate :add 5 3)      ;; => 8
(calculate :divide 10 2)  ;; => 5
(calculate :divide 10 0)  ;; => "Cannot divide by zero"
(calculate :power 2 3)    ;; => "Unknown operation"

;; Validate input
(defn validate-age [age]
  (cond
    (nil? age) {:error "Age is required"}
    (not (number? age)) {:error "Age must be a number"}
    (neg? age) {:error "Age cannot be negative"}
    (> age 150) {:error "Age seems unrealistic"}
    :else {:ok true :age age}))

(validate-age nil)    ;; => {:error "Age is required"}
(validate-age "25")   ;; => {:error "Age must be a number"}
(validate-age -5)     ;; => {:error "Age cannot be negative"}
(validate-age 25)     ;; => {:ok true, :age 25}

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Write a function that takes a number and returns:
;; - "hot" if temperature > 30
;; - "warm" if temperature > 20
;; - "cool" if temperature > 10
;; - "cold" otherwise

;; Exercise 2: Write a function that takes a letter (string) and
;; returns whether it's a vowel using `case`

;; Exercise 3: Write a function using `if-let` that safely gets
;; the second element of a collection, returning "no second element"
;; if the collection has fewer than 2 items

;; Exercise 4: Write a sign function that returns:
;; - 1 for positive numbers
;; - -1 for negative numbers
;; - 0 for zero

;; Exercise 5: Write a function that takes a map with optional keys
;; :name, :title, and returns a greeting using whichever exists:
;; "Hello, Dr. Smith" (if both)
;; "Hello, Alice" (if only name)
;; "Hello, Doctor" (if only title)
;; "Hello, friend" (if neither)

;; ============================================
;; KEY TAKEAWAYS
;; ============================================
;; 1. Only nil and false are falsy - everything else is truthy!
;; 2. Use `when` for single-branch conditions
;; 3. Use `cond` for multiple conditions
;; 4. Use `case` for fast dispatch on constants
;; 5. Use `if-let`/`when-let` to bind and test in one step
;; 6. `and` and `or` return values, not just booleans
;; 7. All conditionals are expressions that return values

;; Next lesson: Functions in Depth
