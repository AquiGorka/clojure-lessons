;; ============================================
;; LESSON 16: MACROS
;; ============================================
;; Macros are Clojure's most powerful feature.
;; They let you extend the language itself by
;; writing code that writes code.

;; ============================================
;; 1. WHAT ARE MACROS?
;; ============================================

;; Functions operate on VALUES at runtime.
;; Macros operate on CODE at compile time.

;; When Clojure compiles your code:
;; 1. Reader converts text to data structures
;; 2. Macros transform those data structures
;; 3. Compiler produces bytecode

;; This means macros can:
;; - Create new syntax
;; - Control evaluation (unlike functions)
;; - Generate repetitive code
;; - Build DSLs (Domain-Specific Languages)

;; ============================================
;; 2. CODE IS DATA (HOMOICONICITY)
;; ============================================

;; Clojure code is made of Clojure data structures!

;; This is code:
(+ 1 2 3)

;; This is data (a list):
'(+ 1 2 3)

;; They look the same because they ARE the same structure.
;; The quote ' prevents evaluation.

(type '(+ 1 2 3))       ;; => clojure.lang.PersistentList
(first '(+ 1 2 3))      ;; => +
(rest '(+ 1 2 3))       ;; => (1 2 3)
(count '(+ 1 2 3))      ;; => 4

;; We can manipulate code as data:
(def my-code '(+ 1 2 3))
(conj my-code 4)        ;; => (4 + 1 2 3)
(concat my-code [4 5])  ;; => (+ 1 2 3 4 5)

;; And then evaluate it:
(eval '(+ 1 2 3))       ;; => 6
(eval (concat '(+) [1 2 3 4 5]))  ;; => 15

;; ============================================
;; 3. YOUR FIRST MACRO
;; ============================================

;; defmacro is like defn, but returns code to be executed

(defmacro my-when
  "A simple when macro."
  [condition & body]
  (list 'if condition (cons 'do body)))

;; Let's see what it generates:
(macroexpand-1 '(my-when true (println "hello") (println "world")))
;; => (if true (do (println "hello") (println "world")))

;; Using it:
(my-when true
  (println "This")
  (println "works!"))
;; Prints: This
;;         works!

;; Key insight: The macro returns a LIST that represents code

;; ============================================
;; 4. MACRO EXPANSION
;; ============================================

;; macroexpand-1: Expand one level
(macroexpand-1 '(my-when true (println "hi")))
;; => (if true (do (println "hi")))

;; macroexpand: Expand until no more macros (top level)
(macroexpand '(my-when true (println "hi")))
;; => (if true (do (println "hi")))

;; clojure.walk/macroexpand-all: Expand everything recursively
(require '[clojure.walk :as walk])
(walk/macroexpand-all '(my-when true (my-when false (println "nested"))))
;; Expands both my-when calls

;; Always use macroexpand to debug your macros!

;; ============================================
;; 5. SYNTAX QUOTE (QUASI-QUOTE)
;; ============================================

;; Building lists with 'list' and 'cons' is tedious.
;; Syntax quote (`) makes it easier!

;; Regular quote:
'(a b c)                ;; => (a b c)

;; Syntax quote - fully qualifies symbols:
`(a b c)                ;; => (user/a user/b user/c)

;; Unquote (~) inserts values:
(let [x 10]
  `(a b ~x))            ;; => (user/a user/b 10)

;; Unquote-splicing (~@) inserts and unpacks:
(let [items [1 2 3]]
  `(a ~@items b))       ;; => (user/a 1 2 3 user/b)

;; Compare:
(let [items [1 2 3]]
  `(a ~items b))        ;; => (user/a [1 2 3] user/b)

(let [items [1 2 3]]
  `(a ~@items b))       ;; => (user/a 1 2 3 user/b)

;; ============================================
;; 6. IMPROVED my-when WITH SYNTAX QUOTE
;; ============================================

(defmacro my-when-v2
  [condition & body]
  `(if ~condition
     (do ~@body)))

(macroexpand-1 '(my-when-v2 true (println "hello") (println "world")))
;; => (if true (do (println "hello") (println "world")))

;; Much cleaner! The template looks like the output.

;; ============================================
;; 7. GENSYM AND AUTO-GENSYM
;; ============================================

;; Problem: Variable capture
;; Consider a broken macro:

(defmacro broken-twice [x]
  `(let [result ~x]
     (+ result result)))

;; This might cause issues if caller uses 'result'

;; Solution 1: gensym creates unique symbols
(defmacro safe-twice-v1 [x]
  (let [result-sym (gensym "result")]
    `(let [~result-sym ~x]
       (+ ~result-sym ~result-sym))))

;; Solution 2: auto-gensym (symbol ending with #)
(defmacro safe-twice [x]
  `(let [result# ~x]
     (+ result# result#)))

(macroexpand-1 '(safe-twice (+ 1 2)))
;; => (let [result__12345__auto__ (+ 1 2)]
;;      (+ result__12345__auto__ result__12345__auto__))

;; The # automatically creates a unique symbol!

;; ============================================
;; 8. COMMON MACRO: unless
;; ============================================

(defmacro unless
  "Execute body if condition is false."
  [condition & body]
  `(if (not ~condition)
     (do ~@body)))

(unless false
  (println "This prints because condition is false"))

;; Alternative implementation:
(defmacro unless-v2 [condition & body]
  `(when (not ~condition)
     ~@body))

;; ============================================
;; 9. CONTROL FLOW MACROS
;; ============================================

;; Macros can control evaluation - functions cannot!

;; Short-circuiting AND
(defmacro my-and
  ([] true)
  ([x] x)
  ([x & rest]
   `(if ~x
      (my-and ~@rest)
      false)))

(my-and true true false (println "Never reached!"))
;; => false (println not executed)

;; Short-circuiting OR
(defmacro my-or
  ([] nil)
  ([x] x)
  ([x & rest]
   `(let [val# ~x]
      (if val#
        val#
        (my-or ~@rest)))))

(my-or nil false "found it!" (println "Never reached!"))
;; => "found it!"

;; ============================================
;; 10. TIMING MACRO
;; ============================================

;; A practical example: measure execution time

(defmacro timed
  "Execute body and return [result elapsed-ms]."
  [& body]
  `(let [start# (System/currentTimeMillis)
         result# (do ~@body)
         end# (System/currentTimeMillis)]
     {:result result#
      :elapsed-ms (- end# start#)}))

(timed
  (Thread/sleep 100)
  (+ 1 2))
;; => {:result 3, :elapsed-ms 100}

;; Enhanced version with reporting:
(defmacro with-timing
  "Execute body and print timing info."
  [label & body]
  `(let [start# (System/nanoTime)
         result# (do ~@body)
         elapsed# (/ (- (System/nanoTime) start#) 1000000.0)]
     (println (format "%s took %.2f ms" ~label elapsed#))
     result#))

(with-timing "Calculation"
  (reduce + (range 10000)))
;; Prints: Calculation took X.XX ms
;; => 49995000

;; ============================================
;; 11. DEBUG MACRO
;; ============================================

(defmacro dbg
  "Print expression and its value, return the value."
  [expr]
  `(let [result# ~expr]
     (println "DBG:" '~expr "=>" result#)
     result#))

(+ 1 (dbg (* 2 3)))
;; Prints: DBG: (* 2 3) => 6
;; => 7

;; Enhanced version showing file and line:
(defmacro dbg+
  [expr]
  `(let [result# ~expr]
     (println (str "[" ~(str *ns*) "] "
                   '~expr " => " result#))
     result#))

;; ============================================
;; 12. BUILDING A DSL: HTML
;; ============================================

(defmacro html-tag
  [tag & content]
  `(str "<" ~(name tag) ">"
        (str ~@content)
        "</" ~(name tag) ">"))

(defmacro html
  [& body]
  `(str ~@body))

(defmacro defhtml-tag [tag]
  `(defmacro ~tag [& content#]
     `(str "<" ~~(name tag) ">"
           (str ~@content#)
           "</" ~~(name tag) ">")))

;; Simple approach - functions work fine here:
(defn tag [name & content]
  (str "<" name ">" (apply str content) "</" name ">"))

(defn div [& content] (apply tag "div" content))
(defn p [& content] (apply tag "p" content))
(defn span [& content] (apply tag "span" content))
(defn h1 [& content] (apply tag "h1" content))

(div
  (h1 "Welcome")
  (p "Hello, " (span "World") "!"))
;; => "<div><h1>Welcome</h1><p>Hello, <span>World</span>!</p></div>"

;; ============================================
;; 13. ANAPHORIC MACROS
;; ============================================

;; Anaphoric macros intentionally capture a symbol (usually 'it')

(defmacro aif
  "Anaphoric if - binds result to 'it'."
  [condition then else]
  `(let [~'it ~condition]
     (if ~'it ~then ~else)))

;; Note: ~'it prevents namespace qualification

(aif (first [1 2 3])
     (str "Found: " it)
     "Not found")
;; => "Found: 1"

(aif (first [])
     (str "Found: " it)
     "Not found")
;; => "Not found"

;; Use sparingly - can be confusing!

;; ============================================
;; 14. MACRO COMPOSITION
;; ============================================

;; Macros can use other macros

(defmacro when-let*
  "Like when-let but with multiple bindings."
  [bindings & body]
  (if (empty? bindings)
    `(do ~@body)
    `(when-let [~(first bindings) ~(second bindings)]
       (when-let* ~(vec (drop 2 bindings)) ~@body))))

(when-let* [a (first [1 2])
            b (first [3 4])
            c (first [5 6])]
  (+ a b c))
;; => 9

(when-let* [a (first [1 2])
            b (first [])    ; nil here
            c (first [5 6])]
  (+ a b c))
;; => nil

;; ============================================
;; 15. COMMON MACRO PATTERNS
;; ============================================

;; Pattern 1: with-* (resource management)
(defmacro with-out-file
  [filename & body]
  `(with-open [writer# (clojure.java.io/writer ~filename)]
     (binding [*out* writer#]
       ~@body)))

;; Pattern 2: def-* (definition helpers)
(defmacro defcached
  "Define a memoized function."
  [name args & body]
  `(def ~name
     (memoize (fn ~args ~@body))))

(defcached slow-fib [n]
  (if (<= n 1)
    n
    (+ (slow-fib (- n 1))
       (slow-fib (- n 2)))))

;; Pattern 3: do-* (iteration)
(defmacro dotimes+
  "Like dotimes but binds both index and total."
  [[i n] & body]
  `(let [total# ~n]
     (dotimes [~i total#]
       (let [~'total total#]
         ~@body))))

;; ============================================
;; 16. MACRO BEST PRACTICES
;; ============================================

;; 1. Don't write a macro if a function will do
;;    Macros are harder to debug, can't be passed as values

;; 2. Always use syntax quote (`) unless you have a reason not to

;; 3. Always use auto-gensym (symbol#) for local bindings

;; 4. Use macroexpand to verify output

;; 5. Keep macros simple - delegate to functions
(defn do-the-work [x y]
  (+ x y))

(defmacro my-macro [x y]
  `(do-the-work ~x ~y))

;; 6. Document macro behavior clearly

;; 7. Consider if you really need a macro

;; ============================================
;; 17. WHEN TO USE MACROS
;; ============================================

;; Use macros when you need to:
;; - Control evaluation (short-circuit, lazy, conditional)
;; - Transform syntax (DSLs)
;; - Reduce boilerplate that CAN'T be reduced with functions
;; - Add compile-time checking
;; - Wrap code with setup/teardown (with-* pattern)

;; DON'T use macros when:
;; - A function would work
;; - You just want to avoid typing
;; - You're not sure why you need one

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Write a macro `if-not-nil` that works like if
;; but only executes then-branch if condition is not nil
;; (false should still execute then-branch)

;; Exercise 2: Write a macro `with-retry` that retries a body
;; n times if it throws an exception
;; (with-retry 3 (risky-operation))

;; Exercise 3: Write a macro `defonce+` that works like defonce
;; but prints a message if the var was already defined

;; Exercise 4: Write a macro `time-it` that returns
;; {:result _ :time-ms _} for any expression

;; Exercise 5: Write a macro `cond-let` that combines cond and let:
;; (cond-let
;;   [x (get-x)] (use x)
;;   [y (get-y)] (use y)
;;   :else default)

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; 1. Macros operate on code (data) at compile time
;; 2. Code is data - lists, vectors, symbols, etc.
;; 3. Syntax quote (`) creates templates, ~ and ~@ insert values
;; 4. Use auto-gensym (symbol#) to avoid variable capture
;; 5. macroexpand helps debug macros
;; 6. Prefer functions over macros when possible
;; 7. Macros enable DSLs and control flow constructs
;; 8. The `with-*` pattern is common for resource management

;; Next lesson: Java Interop
