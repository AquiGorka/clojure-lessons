;; ============================================
;; LESSON 22: TESTING IN CLOJURE
;; ============================================
;; Testing is a first-class citizen in Clojure.
;; The clojure.test library is built-in and provides
;; everything you need for effective testing.

;; ============================================
;; 1. GETTING STARTED WITH clojure.test
;; ============================================

;; clojure.test is included with Clojure - no deps needed!
(require '[clojure.test :refer [deftest is are testing run-tests]])

;; Basic test structure:
;; - deftest defines a test
;; - is makes an assertion
;; - testing groups related assertions

;; ============================================
;; 2. BASIC ASSERTIONS WITH `is`
;; ============================================

;; The `is` macro is the fundamental assertion

(deftest basic-math-test
  (is (= 4 (+ 2 2)))
  (is (= 10 (* 2 5)))
  (is (not= 3 4)))

;; is with different predicates:
(deftest predicate-tests
  (is (true? (even? 4)))
  (is (false? (odd? 4)))
  (is (nil? (first [])))
  (is (some? "hello"))
  (is (empty? []))
  (is (pos? 5))
  (is (neg? -3)))

;; is with custom message (shown on failure):
(deftest documented-test
  (is (= 42 (* 6 7)) "6 times 7 should be 42")
  (is (pos? 5) "5 should be positive"))

;; ============================================
;; 3. TESTING FOR EXCEPTIONS
;; ============================================

;; Use `thrown?` to test that exceptions are thrown:
(deftest exception-tests
  (is (thrown? ArithmeticException (/ 1 0)))
  (is (thrown? NumberFormatException (Integer/parseInt "not-a-number")))
  (is (thrown? clojure.lang.ExceptionInfo
               (throw (ex-info "error" {:type :custom})))))

;; Check exception message with `thrown-with-msg?`:
(deftest exception-message-test
  (is (thrown-with-msg? Exception #"divide"
                        (throw (Exception. "Cannot divide by zero")))))

;; ============================================
;; 4. GROUPING WITH `testing`
;; ============================================

;; `testing` provides context and organization:
(deftest string-operations-test
  (testing "uppercase conversions"
    (is (= "HELLO" (clojure.string/upper-case "hello")))
    (is (= "WORLD" (clojure.string/upper-case "world"))))

  (testing "lowercase conversions"
    (is (= "hello" (clojure.string/lower-case "HELLO")))
    (is (= "world" (clojure.string/lower-case "WORLD"))))

  (testing "trimming whitespace"
    (is (= "hello" (clojure.string/trim "  hello  ")))
    (is (= "" (clojure.string/trim "   ")))))

;; Nested testing blocks:
(deftest nested-testing-example
  (testing "arithmetic operations"
    (testing "addition"
      (is (= 5 (+ 2 3)))
      (is (= 0 (+ -1 1))))
    (testing "multiplication"
      (is (= 6 (* 2 3)))
      (is (= 0 (* 0 100))))))

;; ============================================
;; 5. MULTIPLE ASSERTIONS WITH `are`
;; ============================================

;; `are` is like a template for multiple similar tests:
(deftest multiplication-table-test
  (are [x y result] (= result (* x y))
    2 3 6
    3 4 12
    5 5 25
    0 100 0
    -2 3 -6))

;; are with more complex assertions:
(deftest string-length-test
  (are [s len] (= len (count s))
    "" 0
    "a" 1
    "hello" 5
    "hello world" 11))

;; are with different predicates:
(deftest number-properties-test
  (are [n] (pos? n)
    1
    100
    0.5
    Integer/MAX_VALUE))

;; ============================================
;; 6. RUNNING TESTS
;; ============================================

;; Run all tests in current namespace:
;; (run-tests)

;; Run tests in specific namespace:
;; (run-tests 'myapp.core-test)

;; Run multiple namespaces:
;; (run-tests 'myapp.core-test 'myapp.utils-test)

;; Run a single test:
;; (basic-math-test)  ; Just call it as a function

;; Using test selectors (with test runners like Kaocha):
;; ^:integration, ^:unit, etc.

;; ============================================
;; 7. TEST FIXTURES
;; ============================================

;; Fixtures run setup/teardown code around tests

;; once fixture - runs once for the namespace:
(defn setup-database [f]
  (println "Setting up database...")
  (f)  ; Run the tests
  (println "Tearing down database..."))

;; each fixture - runs for each test:
(defn with-temp-file [f]
  (let [temp (java.io.File/createTempFile "test" ".tmp")]
    (try
      (f)
      (finally
        (.delete temp)))))

;; Register fixtures:
(clojure.test/use-fixtures :once setup-database)
(clojure.test/use-fixtures :each with-temp-file)

;; Fixture that provides data:
(def ^:dynamic *test-config* nil)

(defn with-config [f]
  (binding [*test-config* {:db-url "test://localhost"
                           :timeout 5000}]
    (f)))

;; (clojure.test/use-fixtures :once with-config)

;; Then in tests:
;; (deftest config-test
;;   (is (= "test://localhost" (:db-url *test-config*))))

;; ============================================
;; 8. TESTING PRIVATE FUNCTIONS
;; ============================================

;; Option 1: Use the #' reader macro
;; (deftest private-fn-test
;;   (is (= expected (#'myapp.core/private-fn arg))))

;; Option 2: Temporarily intern in test namespace
;; (def private-fn @#'myapp.core/private-fn)

;; Option 3: Don't test private functions directly!
;; Test them through the public API instead.

;; ============================================
;; 9. TESTING ASYNC CODE
;; ============================================

;; Use promises or atoms to capture async results:
(deftest async-test
  (let [result (promise)]
    (future
      (Thread/sleep 100)
      (deliver result 42))
    (is (= 42 (deref result 1000 :timeout)))))

;; Testing with atoms:
(deftest callback-test
  (let [results (atom [])]
    ;; Simulate async callback
    (future
      (Thread/sleep 50)
      (swap! results conj :done))
    (Thread/sleep 100)
    (is (= [:done] @results))))

;; ============================================
;; 10. TEST-DRIVEN DEVELOPMENT WORKFLOW
;; ============================================

;; 1. Write a failing test first:
(deftest calculator-add-test
  (testing "basic addition"
    (is (= 5 (calculator-add 2 3))))
  (testing "adding zero"
    (is (= 5 (calculator-add 5 0))))
  (testing "adding negative numbers"
    (is (= -1 (calculator-add 2 -3)))))

;; 2. Write the minimal code to pass:
(defn calculator-add [a b]
  (+ a b))

;; 3. Refactor if needed

;; 4. Repeat!

;; ============================================
;; 11. TESTING WITH MOCKS AND STUBS
;; ============================================

;; Use `with-redefs` to temporarily replace functions:
(defn fetch-user [id]
  ;; Imagine this calls a database
  {:id id :name "Real User"})

(defn greet-user [id]
  (let [user (fetch-user id)]
    (str "Hello, " (:name user) "!")))

(deftest greet-user-test
  (with-redefs [fetch-user (fn [id] {:id id :name "Mock User"})]
    (is (= "Hello, Mock User!" (greet-user 123)))))

;; Testing side effects:
(def emails-sent (atom []))

(defn send-email! [to subject body]
  ;; In real code, actually sends email
  (swap! emails-sent conj {:to to :subject subject :body body}))

(deftest email-sending-test
  (reset! emails-sent [])
  (send-email! "test@example.com" "Hello" "Body")
  (is (= 1 (count @emails-sent)))
  (is (= "test@example.com" (:to (first @emails-sent)))))

;; ============================================
;; 12. PROPERTY-BASED TESTING WITH test.check
;; ============================================

;; Add dependency: [org.clojure/test.check "1.1.1"]

(require '[clojure.test.check :as tc])
(require '[clojure.test.check.generators :as gen])
(require '[clojure.test.check.properties :as prop])
(require '[clojure.test.check.clojure-test :refer [defspec]])

;; Define properties that should always hold:
(defspec reverse-reverse-is-identity
  100  ; number of tests
  (prop/for-all [v (gen/vector gen/small-integer)]
    (= v (reverse (reverse v)))))

(defspec sort-is-idempotent
  100
  (prop/for-all [v (gen/vector gen/small-integer)]
    (= (sort v) (sort (sort v)))))

(defspec count-after-conj
  100
  (prop/for-all [v (gen/vector gen/any-printable)
                 x gen/any-printable]
    (= (inc (count v)) (count (conj v x)))))

;; Custom generators:
(def gen-user
  (gen/hash-map
    :id gen/nat
    :name gen/string-alphanumeric
    :age (gen/choose 0 120)))

(defspec user-processing-test
  50
  (prop/for-all [user gen-user]
    (let [processed (process-user user)]
      (and (contains? processed :id)
           (string? (:name processed))))))

(defn process-user [user]
  user)  ; Placeholder

;; ============================================
;; 13. TESTING DATA TRANSFORMATIONS
;; ============================================

(defn transform-user [user]
  (-> user
      (update :name clojure.string/upper-case)
      (assoc :processed true)
      (dissoc :password)))

(deftest transform-user-test
  (testing "transforms user correctly"
    (let [input {:name "alice" :password "secret" :age 30}
          result (transform-user input)]
      (is (= "ALICE" (:name result)))
      (is (true? (:processed result)))
      (is (nil? (:password result)))
      (is (= 30 (:age result))))))

;; Table-driven tests:
(def transformation-test-cases
  [{:input {:name "bob"} :expected {:name "BOB" :processed true}}
   {:input {:name "ALICE"} :expected {:name "ALICE" :processed true}}
   {:input {:name "" :x 1} :expected {:name "" :processed true :x 1}}])

(deftest transformation-table-test
  (doseq [{:keys [input expected]} transformation-test-cases]
    (testing (str "transforming " input)
      (is (= expected (transform-user input))))))

;; ============================================
;; 14. TEST ORGANIZATION
;; ============================================

;; Standard project structure:
;; my-project/
;; ├── src/
;; │   └── myapp/
;; │       ├── core.clj
;; │       └── utils.clj
;; └── test/
;;     └── myapp/
;;         ├── core_test.clj
;;         └── utils_test.clj

;; Test namespace naming convention:
;; src namespace:  myapp.core
;; test namespace: myapp.core-test

;; Test file example:
;; (ns myapp.core-test
;;   (:require [clojure.test :refer [deftest is testing]]
;;             [myapp.core :as core]))

;; ============================================
;; 15. INTEGRATION TESTS
;; ============================================

;; Mark integration tests with metadata:
(deftest ^:integration database-test
  (testing "database connection"
    ;; Real database test
    (is true)))

(deftest ^:integration api-test
  (testing "API endpoint"
    ;; Real API test
    (is true)))

;; Run only unit tests (exclude integration):
;; lein test :only myapp.core-test
;; Or configure in project.clj/deps.edn

;; ============================================
;; 16. CODE COVERAGE
;; ============================================

;; Use cloverage for code coverage:
;; Add to project.clj:
;; :plugins [[lein-cloverage "1.2.2"]]
;;
;; Run: lein cloverage

;; Or with deps.edn:
;; clj -M:test:cloverage

;; Coverage report shows:
;; - Lines covered/uncovered
;; - Branch coverage
;; - Function coverage

;; ============================================
;; 17. TEST RUNNERS
;; ============================================

;; Built-in: (run-tests)

;; Leiningen:
;; lein test

;; deps.edn with test-runner:
;; clj -X:test

;; Kaocha (popular test runner):
;; - Better output formatting
;; - Watch mode
;; - Test filtering
;; - Plugins for coverage, etc.

;; Run with: bin/kaocha

;; ============================================
;; 18. CONTINUOUS INTEGRATION
;; ============================================

;; GitHub Actions example (.github/workflows/test.yml):
;; name: Tests
;; on: [push, pull_request]
;; jobs:
;;   test:
;;     runs-on: ubuntu-latest
;;     steps:
;;       - uses: actions/checkout@v2
;;       - uses: actions/setup-java@v2
;;         with:
;;           distribution: 'temurin'
;;           java-version: '17'
;;       - uses: DeLaGuardo/setup-clojure@v1
;;         with:
;;           lein: 2.9.8
;;       - run: lein test

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Write tests for a function that validates
;; email addresses (must contain @ and .)

;; Exercise 2: Write tests for a shopping cart that can
;; add items, remove items, and calculate total

;; Exercise 3: Write property-based tests for a function
;; that merges two sorted lists into one sorted list

;; Exercise 4: Create a test fixture that sets up and
;; tears down a temporary directory for file tests

;; Exercise 5: Write tests with mocks for a function
;; that fetches data from an API and transforms it

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; 1. clojure.test is built-in - no dependencies needed
;; 2. deftest defines tests, is makes assertions
;; 3. testing groups related assertions with context
;; 4. are is great for data-driven/table tests
;; 5. thrown? tests for expected exceptions
;; 6. use-fixtures for setup/teardown
;; 7. with-redefs enables mocking
;; 8. test.check enables property-based testing
;; 9. Organize tests to mirror source structure
;; 10. Use metadata (^:integration) for test categories
;; 11. Consider Kaocha for better test running experience

;; Next lesson: Real-World Project Structure
