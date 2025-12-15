;; ============================================
;; LESSON 18: JAVA INTEROPERABILITY
;; ============================================
;; Clojure runs on the JVM and has seamless access
;; to the entire Java ecosystem. Understanding Java
;; interop opens up millions of libraries!

;; ============================================
;; 1. WHY JAVA INTEROP MATTERS
;; ============================================

;; - Access to millions of Java libraries
;; - Use battle-tested code (logging, HTTP, databases, etc.)
;; - Integrate with existing Java codebases
;; - Performance-critical code can use Java classes
;; - No need to reinvent the wheel

;; ============================================
;; 2. IMPORTING JAVA CLASSES
;; ============================================

;; In the ns form (preferred):
(ns myapp.core
  (:import [java.util Date UUID ArrayList HashMap]
           [java.io File FileReader BufferedReader]
           [java.time LocalDate LocalDateTime Instant]))

;; At the REPL or elsewhere:
(import 'java.util.Date)
(import '[java.util Date UUID])

;; After importing, use short class names:
;; Date instead of java.util.Date

;; ============================================
;; 3. CREATING JAVA OBJECTS
;; ============================================

;; Using the new special form:
(new java.util.Date)
;; => #inst "2024-01-15T..."

;; Using the Class. syntax (preferred):
(java.util.Date.)
;; => #inst "2024-01-15T..."

;; After import:
(Date.)
;; => #inst "2024-01-15T..."

;; With constructor arguments:
(java.io.File. "/path/to/file.txt")
(java.util.ArrayList. 100)  ; Initial capacity
(String. "hello")

;; StringBuilder example:
(StringBuilder. "Hello")
;; => #object[java.lang.StringBuilder "Hello"]

;; ============================================
;; 4. CALLING INSTANCE METHODS
;; ============================================

;; Using the . (dot) special form:
(. "hello" toUpperCase)
;; => "HELLO"

;; Using the .method syntax (preferred):
(.toUpperCase "hello")
;; => "HELLO"

(.length "hello")
;; => 5

(.contains "hello world" "world")
;; => true

;; With arguments:
(.substring "hello world" 0 5)
;; => "hello"

(.replace "hello" "l" "L")
;; => "heLLo"

;; Chaining methods (not ideal):
(.toUpperCase (.substring "hello world" 0 5))
;; => "HELLO"

;; Better: use threading macro
(-> "hello world"
    (.substring 0 5)
    .toUpperCase)
;; => "HELLO"

;; ============================================
;; 5. CALLING STATIC METHODS
;; ============================================

;; Using Class/method syntax:
(Math/sqrt 16)
;; => 4.0

(Math/pow 2 10)
;; => 1024.0

(Math/random)
;; => 0.7234... (random number)

(System/currentTimeMillis)
;; => 1705312345678

(Integer/parseInt "42")
;; => 42

(Double/parseDouble "3.14")
;; => 3.14

(String/valueOf 42)
;; => "42"

(String/format "Hello %s, you are %d years old"
               (object-array ["Alice" 30]))
;; => "Hello Alice, you are 30 years old"

;; UUID generation:
(java.util.UUID/randomUUID)
;; => #uuid "550e8400-e29b-41d4-a716-446655440000"

;; ============================================
;; 6. ACCESSING STATIC FIELDS
;; ============================================

;; Use Class/FIELD syntax:
Math/PI
;; => 3.141592653589793

Math/E
;; => 2.718281828459045

Integer/MAX_VALUE
;; => 2147483647

Long/MIN_VALUE
;; => -9223372036854775808

java.io.File/separator
;; => "/" (or "\\" on Windows)

;; ============================================
;; 7. ACCESSING INSTANCE FIELDS
;; ============================================

;; Use .-field or (. obj field) syntax:
(def point (java.awt.Point. 10 20))

(.-x point)
;; => 10

(.-y point)
;; => 20

;; Note: Most Java code uses getters instead of public fields
;; But when fields are public, .- works

;; ============================================
;; 8. SETTING FIELDS (MUTATION)
;; ============================================

;; Using set! for mutable Java objects:
(def point (java.awt.Point. 0 0))

(set! (.-x point) 100)
(.-x point)
;; => 100

;; More common: use setter methods
(def list (java.util.ArrayList.))
(.add list "item1")
(.add list "item2")
(.size list)
;; => 2

;; ============================================
;; 9. WORKING WITH JAVA COLLECTIONS
;; ============================================

;; ArrayList:
(def alist (java.util.ArrayList.))
(.add alist "one")
(.add alist "two")
(.add alist "three")
(.get alist 0)
;; => "one"

;; Convert Java collection to Clojure:
(vec alist)
;; => ["one" "two" "three"]

(into [] alist)
;; => ["one" "two" "three"]

(seq alist)
;; => ("one" "two" "three")

;; HashMap:
(def hmap (java.util.HashMap.))
(.put hmap "name" "Alice")
(.put hmap "age" 30)
(.get hmap "name")
;; => "Alice"

(into {} hmap)
;; => {"name" "Alice", "age" 30}

;; HashSet:
(def hset (java.util.HashSet.))
(.add hset "a")
(.add hset "b")
(.add hset "a")  ; duplicate ignored
(into #{} hset)
;; => #{"a" "b"}

;; ============================================
;; 10. CLOJURE COLLECTIONS IN JAVA
;; ============================================

;; Clojure collections implement Java interfaces!

;; Vectors implement java.util.List:
(def v [1 2 3])
(.get v 1)
;; => 2

(.contains v 2)
;; => true

(.indexOf v 3)
;; => 2

;; Maps implement java.util.Map:
(def m {:a 1 :b 2})
(.get m :a)
;; => 1

(.containsKey m :b)
;; => true

;; This means Clojure collections can be passed
;; directly to Java methods expecting collections!

;; ============================================
;; 11. ARRAYS
;; ============================================

;; Creating arrays:
(make-array String 10)        ; String array, length 10
(make-array Integer/TYPE 5)   ; int[] primitive array

;; Easier: type-specific array creators
(int-array 5)                 ; int[5]
(int-array [1 2 3 4 5])       ; int[] with values

(long-array 10)
(double-array [1.0 2.0 3.0])
(float-array 5)
(byte-array 1024)
(boolean-array 3)
(char-array "hello")          ; char[] from string

(object-array 10)             ; Object[10]
(object-array ["a" "b" "c"])  ; Object[] with values

;; Convert to array from collection:
(into-array String ["a" "b" "c"])
;; => #object["[Ljava.lang.String;" ...]

(into-array Integer [1 2 3])
;; => Integer[] (boxed)

;; Reading arrays:
(def arr (int-array [10 20 30 40 50]))
(aget arr 0)    ;; => 10
(aget arr 2)    ;; => 30

;; Getting length:
(alength arr)   ;; => 5

;; Modifying arrays:
(aset arr 0 100)
(aget arr 0)    ;; => 100

;; Array operations:
(def arr (int-array [1 2 3 4 5]))
(java.util.Arrays/sort arr)
(vec arr)
;; => [1 2 3 4 5]

;; ============================================
;; 12. TYPE HINTS FOR PERFORMANCE
;; ============================================

;; Without type hints, Clojure uses reflection (slower):
(defn string-length [s]
  (.length s))  ; Uses reflection

;; With type hint (faster):
(defn string-length-fast [^String s]
  (.length s))  ; Direct method call

;; Check for reflection warnings:
(set! *warn-on-reflection* true)

;; Type hints on return values:
(defn ^String greeting [^String name]
  (str "Hello, " name))

;; Type hints on let bindings:
(let [^StringBuilder sb (StringBuilder.)]
  (.append sb "Hello")
  (.append sb " World")
  (.toString sb))

;; Common type hints:
;; ^String, ^Long, ^Double
;; ^"[B" for byte[]
;; ^"[I" for int[]
;; ^java.util.List
;; ^java.util.Map

;; ============================================
;; 13. HANDLING JAVA EXCEPTIONS
;; ============================================

;; try/catch works with Java exceptions:
(try
  (Integer/parseInt "not-a-number")
  (catch NumberFormatException e
    (str "Invalid number: " (.getMessage e))))
;; => "Invalid number: For input string: \"not-a-number\""

;; Multiple catch clauses:
(try
  (/ 1 0)
  (catch ArithmeticException e
    "Division by zero!")
  (catch Exception e
    (str "Other error: " (.getMessage e))))

;; finally clause:
(try
  (println "Doing something")
  (throw (Exception. "Oops"))
  (catch Exception e
    (println "Caught:" (.getMessage e)))
  (finally
    (println "Cleanup here")))

;; Throwing Java exceptions:
(throw (IllegalArgumentException. "Bad argument!"))
(throw (RuntimeException. "Something went wrong"))

;; ============================================
;; 14. IMPLEMENTING JAVA INTERFACES
;; ============================================

;; Using reify for one-off implementations:
(def my-runnable
  (reify java.lang.Runnable
    (run [this]
      (println "Running!"))))

(.run my-runnable)
;; Prints: Running!

;; Implementing Comparable:
(def my-comparator
  (reify java.util.Comparator
    (compare [this a b]
      (- (count a) (count b)))))

(sort my-comparator ["aaa" "b" "cc"])
;; => ("b" "cc" "aaa")

;; Multiple interfaces with reify:
(defn make-closeable-reader [filename]
  (let [reader (java.io.BufferedReader.
                 (java.io.FileReader. filename))]
    (reify
      java.io.Closeable
      (close [this]
        (.close reader))

      java.lang.Iterable
      (iterator [this]
        (.iterator (line-seq reader))))))

;; ============================================
;; 15. PROXY FOR EXTENDING CLASSES
;; ============================================

;; proxy creates a subclass of a Java class:
(def my-thread
  (proxy [Thread] []  ; [SuperClass] [constructor-args]
    (run []
      (println "Thread running!")
      (Thread/sleep 1000)
      (println "Thread done!"))))

(.start my-thread)

;; Overriding methods with proxy:
(def custom-list
  (proxy [java.util.ArrayList] []
    (add [item]
      (println "Adding:" item)
      (proxy-super add item))))  ; Call super method

(.add custom-list "hello")
;; Prints: Adding: hello

;; ============================================
;; 16. GEN-CLASS FOR AOT COMPILATION
;; ============================================

;; gen-class creates a real Java class (for main methods, etc.)

(ns myapp.main
  (:gen-class))  ; This ns will compile to a Java class

(defn -main [& args]
  (println "Hello from Clojure!")
  (println "Args:" args))

;; The resulting class can be called from Java:
;; java -cp classes:clojure.jar myapp.main arg1 arg2

;; ============================================
;; 17. COMMON JAVA INTEROP PATTERNS
;; ============================================

;; Reading a file:
(slurp "filename.txt")  ; Clojure wrapper

;; Or using Java directly:
(with-open [rdr (java.io.BufferedReader.
                  (java.io.FileReader. "filename.txt"))]
  (reduce str (line-seq rdr)))

;; HTTP request (using Java's HttpClient):
(import '[java.net URI]
        '[java.net.http HttpClient HttpRequest HttpResponse$BodyHandlers])

(defn http-get [url]
  (let [client (HttpClient/newHttpClient)
        request (-> (HttpRequest/newBuilder)
                    (.uri (URI/create url))
                    (.build))]
    (-> client
        (.send request (HttpResponse$BodyHandlers/ofString))
        (.body))))

;; Working with dates (Java 8+ Time API):
(import '[java.time LocalDate LocalDateTime ZonedDateTime]
        '[java.time.format DateTimeFormatter])

(def today (LocalDate/now))
(def formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd"))
(.format today formatter)
;; => "2024-01-15"

;; Parsing dates:
(LocalDate/parse "2024-12-25" formatter)

;; Working with files (NIO):
(import '[java.nio.file Files Paths])

(defn read-all-lines [path]
  (-> (Paths/get path (into-array String []))
      Files/readAllLines
      vec))

;; ============================================
;; 18. DOTO MACRO FOR JAVA BUILDERS
;; ============================================

;; doto is perfect for Java's builder pattern:
(doto (StringBuilder.)
  (.append "Hello")
  (.append " ")
  (.append "World"))
;; => #object[StringBuilder "Hello World"]

(doto (java.util.ArrayList.)
  (.add "one")
  (.add "two")
  (.add "three"))
;; => ArrayList ["one", "two", "three"]

(doto (java.util.HashMap.)
  (.put :a 1)
  (.put :b 2)
  (.put :c 3))
;; => HashMap {:a 1, :b 2, :c 3}

;; ============================================
;; 19. BEAN FUNCTION
;; ============================================

;; Convert Java beans to Clojure maps:
(bean (java.util.Date.))
;; => {:day 1, :month 0, :year 124, :hours 12, ...}

(bean (java.io.File. "/tmp"))
;; => {:name "tmp", :parent "/", :path "/tmp", ...}

;; Very useful for inspecting Java objects!

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Create a UUID and convert it to a string

;; Exercise 2: Use StringBuilder to efficiently concatenate
;; the numbers 1-1000 with commas between them

;; Exercise 3: Create a method that parses a date string
;; in format "dd/MM/yyyy" and returns a LocalDate

;; Exercise 4: Implement Runnable using reify to print
;; "Tick" every second, 5 times

;; Exercise 5: Create a function that lists all files
;; in a directory using java.io.File

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; 1. (ClassName.) creates new instances
;; 2. (.method obj args) calls instance methods
;; 3. (Class/staticMethod args) calls static methods
;; 4. Class/FIELD accesses static fields
;; 5. (.-field obj) accesses instance fields
;; 6. Use type hints (^Type) for performance
;; 7. Clojure collections implement Java interfaces
;; 8. reify implements interfaces anonymously
;; 9. proxy extends classes
;; 10. doto is great for builder patterns
;; 11. bean converts Java objects to maps

;; Next lesson: Macros
