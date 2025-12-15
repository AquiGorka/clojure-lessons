;; ==============================================================================
;; LESSON 02: Basic Data Types in Clojure
;; ==============================================================================
;; Clojure has a rich set of built-in data types. Let's explore them all!

;; ==============================================================================
;; 1. NUMBERS
;; ==============================================================================

;; Integers (arbitrary precision by default)
42          ; A simple integer
-17         ; Negative integer
0           ; Zero

;; Clojure automatically uses BigInteger for large numbers
999999999999999999999999999999  ; This just works!

;; You can explicitly create a BigInt with N suffix
42N         ; BigInt literal

;; Longs (64-bit, most common for performance)
42          ; Actually stored as a Long when it fits

;; Floating-point numbers (doubles by default)
3.14        ; Double-precision float
-0.5        ; Negative float
2.998e8     ; Scientific notation (speed of light in m/s)

;; BigDecimal for precise decimal arithmetic (use M suffix)
3.14159265358979323846M   ; Precise decimal, great for money!

;; Ratios - Clojure has built-in rational numbers!
1/3         ; One third - stays as a ratio, not 0.333...
22/7        ; Approximation of pi as a ratio
-3/4        ; Negative ratio

;; Try these in the REPL:
(println "=== Numbers ===")
(println "Integer:" 42)
(println "Big number:" 999999999999999999999999999999N)
(println "Float:" 3.14)
(println "Ratio:" 1/3)
(println "Ratio arithmetic:" (+ 1/3 1/6))  ; => 1/2 (not 0.5!)

;; ==============================================================================
;; 2. STRINGS
;; ==============================================================================

;; Strings are Java strings, enclosed in double quotes
"Hello, World!"
"Clojure is fun!"
""              ; Empty string

;; Strings can span multiple lines
"This is a
multi-line
string"

;; Escape sequences work as expected
"Tab:\tNewline:\nQuote:\""

;; String functions (from clojure.string namespace)
(println "\n=== Strings ===")
(println "A string:" "Hello, Clojure!")
(println "Length:" (count "Hello"))           ; => 5
(println "Concatenation:" (str "Hello" " " "World"))  ; => "Hello World"
(println "Upper case:" (clojure.string/upper-case "hello"))
(println "Split:" (clojure.string/split "a,b,c" #","))  ; Split on comma

;; ==============================================================================
;; 3. CHARACTERS
;; ==============================================================================

;; Characters are prefixed with backslash
\a          ; The character 'a'
\Z          ; The character 'Z'
\newline    ; Newline character
\space      ; Space character
\tab        ; Tab character
\u03BB      ; Unicode lambda: λ

(println "\n=== Characters ===")
(println "Character a:" \a)
(println "Lambda:" \u03BB)
(println "Char to int:" (int \a))    ; => 97
(println "Int to char:" (char 65))   ; => \A

;; ==============================================================================
;; 4. BOOLEANS
;; ==============================================================================

true        ; Boolean true
false       ; Boolean false

;; In Clojure, only `false` and `nil` are falsy
;; Everything else is truthy (including 0, "", [], etc.)

(println "\n=== Booleans ===")
(println "true is truthy:" (if true "yes" "no"))
(println "false is falsy:" (if false "yes" "no"))
(println "nil is falsy:" (if nil "yes" "no"))
(println "0 is truthy!:" (if 0 "yes" "no"))        ; "yes" - 0 is truthy!
(println "Empty string is truthy!:" (if "" "yes" "no"))  ; "yes"
(println "Empty vector is truthy!:" (if [] "yes" "no"))  ; "yes"

;; Boolean operations
(println "and:" (and true false))    ; => false
(println "or:" (or true false))      ; => true
(println "not:" (not true))          ; => false

;; ==============================================================================
;; 5. NIL
;; ==============================================================================

;; nil represents "nothing" or "no value" (like null in other languages)
nil

(println "\n=== Nil ===")
(println "nil value:" nil)
(println "nil? check:" (nil? nil))    ; => true
(println "nil? on 0:" (nil? 0))       ; => false
(println "some? check:" (some? nil))  ; => false (opposite of nil?)
(println "some? on 0:" (some? 0))     ; => true

;; ==============================================================================
;; 6. KEYWORDS
;; ==============================================================================

;; Keywords start with a colon - they evaluate to themselves
;; They're like symbols that refer to themselves (great for map keys!)
:name
:age
:user/email      ; Namespaced keyword
::local-keyword  ; Auto-namespaced to current namespace

(println "\n=== Keywords ===")
(println "Keyword:" :hello)
(println "Namespaced:" :user/name)
(println "Keyword equality:" (= :foo :foo))  ; => true
(println "Keyword from string:" (keyword "my-key"))  ; => :my-key
(println "Name of keyword:" (name :my-key))  ; => "my-key"

;; Keywords are often used as functions to look up values in maps
(def person {:name "Alice" :age 30})
(println "Keyword as function:" (:name person))  ; => "Alice"

;; ==============================================================================
;; 7. SYMBOLS
;; ==============================================================================

;; Symbols are identifiers that refer to something else (like variables)
;; They don't start with a colon (unlike keywords)

'hello           ; Quoted symbol (prevents evaluation)
'my-function     ; Another symbol
'clojure.core/+  ; Namespaced symbol

(println "\n=== Symbols ===")
(println "Symbol:" 'hello)
(println "Symbol from string:" (symbol "my-sym"))
(println "Symbol name:" (name 'my-sym))
(println "Namespaced symbol:" 'clojure.core/map)

;; ==============================================================================
;; 8. REGULAR EXPRESSIONS
;; ==============================================================================

;; Regex literals start with #
#"hello"         ; Simple pattern
#"\d+"           ; One or more digits
#"[a-zA-Z]+"     ; One or more letters
#"(?i)hello"     ; Case-insensitive

(println "\n=== Regular Expressions ===")
(println "Match found:" (re-find #"\d+" "abc123def"))     ; => "123"
(println "All matches:" (re-seq #"\d+" "a1b2c3"))         ; => ("1" "2" "3")
(println "Matches?:" (re-matches #"\d+" "123"))           ; => "123"
(println "No match:" (re-matches #"\d+" "abc"))           ; => nil
(println "Replace:" (clojure.string/replace "hello" #"l" "L"))  ; => "heLLo"

;; ==============================================================================
;; 9. TYPE CHECKING
;; ==============================================================================

;; Check types with predicates (functions ending in ?)
(println "\n=== Type Checking ===")
(println "number?:" (number? 42))        ; => true
(println "integer?:" (integer? 42))      ; => true
(println "float?:" (float? 3.14))        ; => true
(println "ratio?:" (ratio? 1/3))         ; => true
(println "string?:" (string? "hello"))   ; => true
(println "char?:" (char? \a))            ; => true
(println "boolean?:" (boolean? true))    ; => true
(println "nil?:" (nil? nil))             ; => true
(println "keyword?:" (keyword? :foo))    ; => true
(println "symbol?:" (symbol? 'foo))      ; => true

;; Get the type/class of a value
(println "\nTypes:")
(println "Type of 42:" (type 42))
(println "Type of 3.14:" (type 3.14))
(println "Type of \"hi\":" (type "hi"))
(println "Type of :key:" (type :key))
(println "Type of 1/3:" (type 1/3))

;; ==============================================================================
;; 10. TYPE COERCION
;; ==============================================================================

(println "\n=== Type Coercion ===")

;; String conversions
(println "To string:" (str 42))            ; => "42"
(println "To string:" (str 3.14))          ; => "3.14"
(println "To string:" (str :keyword))      ; => ":keyword"

;; Number conversions
(println "Parse int:" (Integer/parseInt "42"))     ; => 42
(println "Parse float:" (Double/parseDouble "3.14"))  ; => 3.14
(println "To int:" (int 3.7))              ; => 3 (truncates)
(println "To long:" (long 42))
(println "To double:" (double 42))         ; => 42.0
(println "To float:" (float 42))           ; => 42.0

;; Ratio to decimal
(println "Ratio to double:" (double 1/3))  ; => 0.3333...

;; ==============================================================================
;; EXERCISES
;; ==============================================================================

;; Try these exercises in your REPL:

;; 1. Create a ratio representing 3/8 and add it to 1/4. What's the result?

;; 2. What is the type of 999999999999999999999?

;; 3. Create a keyword with your name and extract just the name part

;; 4. Use a regex to find all words (sequences of letters) in "hello123world456"

;; 5. What's the difference between (= 1 1.0) and (== 1 1.0)?
;;    Hint: = checks type equality, == checks numeric equality

;; 6. Is the empty list () truthy or falsy?

;; 7. Convert the string "3.14159" to a number and multiply it by 2

;; ==============================================================================
;; KEY TAKEAWAYS
;; ==============================================================================

;; 1. Clojure has rich numeric types: integers, floats, ratios, BigInt, BigDecimal
;; 2. Only false and nil are falsy - everything else is truthy!
;; 3. Keywords (:like-this) are great for map keys and are often used as functions
;; 4. Symbols (like-this) are names that refer to other things
;; 5. Strings are Java strings, characters are prefixed with \
;; 6. Regex literals use #"pattern" syntax
;; 7. Type predicates end in ? (number?, string?, nil?, etc.)

;; Next up: Lesson 03 - Collections (Lists, Vectors, Maps, and Sets)
