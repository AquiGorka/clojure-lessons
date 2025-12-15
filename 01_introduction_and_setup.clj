;; ============================================================================
;; LESSON 01: Introduction to Clojure and Setup
;; ============================================================================
;; Welcome to your Clojure journey! This lesson covers what Clojure is,
;; why you might want to learn it, and how to get started.

;; ============================================================================
;; WHAT IS CLOJURE?
;; ============================================================================

;; Clojure is a modern, dynamic, functional programming language that runs
;; on the Java Virtual Machine (JVM). It was created by Rich Hickey and
;; released in 2007.

;; Key characteristics of Clojure:
;;
;; 1. LISP DIALECT - Clojure is a Lisp, meaning it uses prefix notation
;;    and has a simple, uniform syntax based on S-expressions (parentheses!)
;;
;; 2. FUNCTIONAL - Emphasizes immutable data and pure functions
;;
;; 3. HOSTED LANGUAGE - Runs on the JVM, giving access to Java libraries
;;    (Also available as ClojureScript for JavaScript environments)
;;
;; 4. DYNAMIC - Types are checked at runtime, not compile time
;;
;; 5. CONCURRENT - Built-in support for safe concurrent programming

;; ============================================================================
;; WHY LEARN CLOJURE?
;; ============================================================================

;; - Expressive: Write less code to do more
;; - Interactive development: REPL-driven development is a joy
;; - Immutability by default: Fewer bugs from unexpected state changes
;; - Excellent concurrency support: STM, atoms, agents, core.async
;; - Access to the entire Java ecosystem
;; - Great for data processing and transformation
;; - Active community and growing industry adoption

;; ============================================================================
;; SETUP INSTRUCTIONS
;; ============================================================================

;; STEP 1: Install Java
;; ---------------------
;; Clojure requires Java 8 or later. Check if you have Java:
;;   java -version
;;
;; If not installed, download from:
;;   https://adoptium.net/ (recommended)
;;   or use your system's package manager

;; STEP 2: Install Clojure
;; -----------------------
;;
;; macOS (using Homebrew):
;;   brew install clojure/tools/clojure
;;
;; Linux:
;;   curl -O https://download.clojure.org/install/linux-install-1.11.1.1435.sh
;;   chmod +x linux-install-1.11.1.1435.sh
;;   sudo ./linux-install-1.11.1.1435.sh
;;
;; Windows:
;;   Download and run the installer from:
;;   https://github.com/casselc/clj-msi/releases

;; STEP 3: Verify Installation
;; ---------------------------
;; Run this in your terminal:
;;   clj
;;
;; You should see a REPL prompt like:
;;   Clojure 1.11.1
;;   user=>

;; STEP 4: Choose an Editor
;; ------------------------
;; Recommended editors with Clojure support:
;;
;; - VS Code + Calva extension (beginner-friendly)
;; - Emacs + CIDER (powerful, steeper learning curve)
;; - IntelliJ IDEA + Cursive plugin (great for Java developers)
;; - Vim + vim-fireplace or Conjure
;; - Atom + Chlorine

;; ============================================================================
;; YOUR FIRST CLOJURE CODE
;; ============================================================================

;; This is a comment. Anything after a semicolon is ignored.

;; Let's write "Hello, World!" in Clojure:

(println "Hello, World!")

;; Try it! Start a REPL with `clj` and type the line above.

;; In Clojure, everything is an expression inside parentheses.
;; The first element is the function, followed by its arguments.

;; Here's some simple arithmetic:

(+ 1 2)        ;; => 3   (addition)
(- 10 4)       ;; => 6   (subtraction)
(* 3 4)        ;; => 12  (multiplication)
(/ 15 3)       ;; => 5   (division)

;; Notice the PATTERN: (function arg1 arg2 ...)
;; This is called PREFIX notation or Polish notation.

;; You can nest expressions:

(+ 1 (* 2 3))  ;; => 7   (1 + (2 * 3))

(* (+ 1 2) (- 10 5))  ;; => 15   ((1 + 2) * (10 - 5))

;; ============================================================================
;; THE REPL
;; ============================================================================

;; REPL stands for Read-Eval-Print-Loop:
;; - READ: Reads your input
;; - EVAL: Evaluates the expression
;; - PRINT: Prints the result
;; - LOOP: Goes back to waiting for input

;; The REPL is your best friend in Clojure development!
;; Use it to:
;; - Experiment with code
;; - Test functions as you write them
;; - Explore libraries
;; - Debug issues

;; Useful REPL commands:
;; (doc function-name)   - Show documentation for a function
;; (source function-name) - Show source code of a function
;; *1, *2, *3           - Reference last 3 results
;; (require '[namespace]) - Load a namespace

;; Try these in your REPL:

(doc +)        ;; Shows documentation for the + function
(doc println)  ;; Shows documentation for println

;; ============================================================================
;; EXERCISES
;; ============================================================================

;; 1. Start a REPL and calculate: 42 + 58
;;    Hint: (+ 42 58)

;; 2. Calculate: (100 - 20) * 3

;; 3. Calculate: 144 / 12

;; 4. Calculate: 2 + 3 * 4 + 5
;;    Remember: in Clojure you must explicitly nest operations

;; 5. Use (doc) to look up documentation for the `str` function
;;    Then try: (str "Hello" " " "World")

;; ============================================================================
;; WHAT'S NEXT?
;; ============================================================================

;; In the next lesson, we'll explore Clojure's basic data types:
;; numbers, strings, booleans, keywords, and symbols.
;;
;; Proceed to: 02_basic_data_types.clj

;; ============================================================================
;; SUMMARY
;; ============================================================================

;; - Clojure is a functional Lisp that runs on the JVM
;; - Syntax uses prefix notation: (function arg1 arg2 ...)
;; - The REPL is central to Clojure development
;; - Comments start with semicolons
;; - Use (doc fn) to get help on any function
