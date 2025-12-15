;; ============================================
;; LESSON 12: ERROR HANDLING IN CLOJURE
;; ============================================
;; Learn how to handle errors gracefully in Clojure,
;; from Java exceptions to functional error patterns.

;; ============================================
;; 1. JAVA EXCEPTIONS IN CLOJURE
;; ============================================
;; Clojure runs on the JVM and uses Java's exception system.

;; Throwing exceptions:
(throw (Exception. "Something went wrong!"))

(throw (IllegalArgumentException. "Invalid argument"))

(throw (RuntimeException. "Runtime error"))

;; Creating exception with cause:
(throw (Exception. "High-level error"
                   (Exception. "Root cause")))

;; ============================================
;; 2. TRY-CATCH-FINALLY
;; ============================================
;; The basic error handling construct.

;; Basic try-catch:
(try
  (/ 10 0)
  (catch ArithmeticException e
    (str "Math error: " (.getMessage e))))
;; => "Math error: Divide by zero"

;; Multiple catch clauses (order matters - specific first):
(try
  (Integer/parseInt "not-a-number")
  (catch NumberFormatException e
    (str "Number format error: " (.getMessage e)))
  (catch IllegalArgumentException e
    (str "Illegal argument: " (.getMessage e)))
  (catch Exception e
    (str "General error: " (.getMessage e))))
;; => "Number format error: For input string: \"not-a-number\""

;; With finally clause (always runs):
(try
  (println "Trying something risky...")
  (/ 10 2)
  (catch Exception e
    (println "Caught an error!")
    nil)
  (finally
    (println "Cleanup code runs regardless!")))
;; Prints: Trying something risky...
;; Prints: Cleanup code runs regardless!
;; => 5

;; finally for resource cleanup:
(defn read-file-safely [filename]
  (let [reader (atom nil)]
    (try
      (reset! reader (clojure.java.io/reader filename))
      (slurp @reader)
      (catch java.io.FileNotFoundException e
        (str "File not found: " filename))
      (catch Exception e
        (str "Error reading file: " (.getMessage e)))
      (finally
        (when @reader
          (.close @reader)
          (println "Reader closed"))))))

;; ============================================
;; 3. EX-INFO AND EX-DATA
;; ============================================
;; Clojure's preferred way to create informative exceptions.

;; ex-info creates an ExceptionInfo with a message and data map:
(throw (ex-info "User not found"
                {:user-id 123
                 :searched-at (java.util.Date.)
                 :type :not-found}))

;; Catch and extract data:
(try
  (throw (ex-info "Validation failed"
                  {:field :email
                   :value "invalid"
                   :errors ["Invalid format" "Too short"]}))
  (catch clojure.lang.ExceptionInfo e
    (let [data (ex-data e)]
      {:message (.getMessage e)
       :field (:field data)
       :errors (:errors data)})))
;; => {:message "Validation failed", :field :email, :errors ["Invalid format" "Too short"]}

;; ex-data extracts the data map from ExceptionInfo:
(defn validate-user [{:keys [name email age]}]
  (cond
    (empty? name)
    (throw (ex-info "Validation error"
                    {:type :validation
                     :field :name
                     :error "Name is required"}))

    (not (re-matches #".+@.+\..+" (or email "")))
    (throw (ex-info "Validation error"
                    {:type :validation
                     :field :email
                     :error "Invalid email format"}))

    (or (nil? age) (< age 0))
    (throw (ex-info "Validation error"
                    {:type :validation
                     :field :age
                     :error "Age must be a positive number"}))

    :else {:valid true :user {:name name :email email :age age}}))

;; Using the validator:
(try
  (validate-user {:name "Alice" :email "invalid" :age 30})
  (catch clojure.lang.ExceptionInfo e
    (ex-data e)))
;; => {:type :validation, :field :email, :error "Invalid email format"}

;; ============================================
;; 4. EXCEPTION UTILITIES
;; ============================================

;; Get the cause of an exception:
(try
  (throw (ex-info "Outer error" {:level :outer}
                  (ex-info "Inner error" {:level :inner})))
  (catch Exception e
    {:message (.getMessage e)
     :cause-message (.getMessage (ex-cause e))
     :outer-data (ex-data e)
     :inner-data (ex-data (ex-cause e))}))
;; => {:message "Outer error", :cause-message "Inner error", ...}

;; Get the stack trace:
(try
  (throw (Exception. "Test"))
  (catch Exception e
    (count (.getStackTrace e))))
;; => (some number representing stack depth)

;; Print stack trace (for debugging):
(try
  (/ 1 0)
  (catch Exception e
    (.printStackTrace e)))

;; ============================================
;; 5. FUNCTIONAL ERROR HANDLING
;; ============================================
;; Many Clojure developers prefer returning values
;; over throwing exceptions.

;; Pattern 1: Return nil on failure
(defn safe-parse-int [s]
  (try
    (Integer/parseInt s)
    (catch NumberFormatException e
      nil)))

(safe-parse-int "42")     ;; => 42
(safe-parse-int "hello")  ;; => nil

;; Pattern 2: Return a result map
(defn divide-safe [a b]
  (if (zero? b)
    {:error "Cannot divide by zero"
     :type :division-by-zero}
    {:result (/ a b)}))

(divide-safe 10 2)   ;; => {:result 5}
(divide-safe 10 0)   ;; => {:error "Cannot divide by zero", :type :division-by-zero}

;; Pattern 3: Either-style (success/failure)
(defn parse-config [config-str]
  (try
    {:success true
     :value (read-string config-str)}  ; Simple example
    (catch Exception e
      {:success false
       :error (.getMessage e)})))

(defn process-config [config-str]
  (let [result (parse-config config-str)]
    (if (:success result)
      (str "Config loaded: " (:value result))
      (str "Failed to load config: " (:error result)))))

;; ============================================
;; 6. ASSERTIONS
;; ============================================
;; Quick checks that throw AssertionError if false.

;; Basic assert:
(assert (= 1 1))  ;; Passes silently
;; (assert (= 1 2))  ;; Throws AssertionError

;; Assert with message:
(assert (= 1 1) "One should equal one")
;; (assert (= 1 2) "One should equal one")  ;; AssertionError: One should equal one

;; Assertions can be disabled with *assert* binding
;; or by setting clojure.core/*assert* to false

;; ============================================
;; 7. PRE AND POST CONDITIONS
;; ============================================
;; Design-by-contract style assertions in functions.

(defn calculate-discount
  "Calculates discount. Price and rate must be positive.
   Rate must be between 0 and 1. Returns positive number."
  [price rate]
  {:pre [(number? price)
         (pos? price)
         (number? rate)
         (<= 0 rate 1)]
   :post [(number? %)
          (>= % 0)
          (<= % price)]}
  (* price rate))

(calculate-discount 100 0.2)   ;; => 20.0
;; (calculate-discount -100 0.2) ;; AssertionError: Assert failed: (pos? price)
;; (calculate-discount 100 1.5)  ;; AssertionError: Assert failed: (<= 0 rate 1)

;; Helpful for documentation and debugging:
(defn transfer-money
  [from-account to-account amount]
  {:pre [(map? from-account)
         (map? to-account)
         (number? amount)
         (pos? amount)
         (>= (:balance from-account) amount)]
   :post [(= (+ (:balance (:from %))
               (:balance (:to %)))
             (+ (:balance from-account)
                (:balance to-account)))]}
  {:from (update from-account :balance - amount)
   :to (update to-account :balance + amount)})

;; ============================================
;; 8. HANDLING ERRORS IN SEQUENCES
;; ============================================

;; Process collection, collecting errors:
(defn parse-numbers [strings]
  (reduce
    (fn [{:keys [results errors]} s]
      (try
        {:results (conj results (Integer/parseInt s))
         :errors errors}
        (catch NumberFormatException e
          {:results results
           :errors (conj errors {:input s :error (.getMessage e)})})))
    {:results [] :errors []}
    strings))

(parse-numbers ["1" "2" "three" "4" "five"])
;; => {:results [1 2 4],
;;     :errors [{:input "three", :error "For input string: \"three\""}
;;              {:input "five", :error "For input string: \"five\""}]}

;; keep with try-catch (silently skip failures):
(defn parse-numbers-quiet [strings]
  (keep (fn [s]
          (try
            (Integer/parseInt s)
            (catch Exception _ nil)))
        strings))

(parse-numbers-quiet ["1" "2" "three" "4" "five"])
;; => (1 2 4)

;; ============================================
;; 9. RETRY LOGIC
;; ============================================

(defn retry
  "Retries a function up to n times on failure."
  [n f]
  (loop [attempts n
         last-error nil]
    (if (zero? attempts)
      (throw (ex-info "All retries failed"
                      {:attempts n
                       :last-error last-error}))
      (let [result (try
                     {:success true :value (f)}
                     (catch Exception e
                       {:success false :error e}))]
        (if (:success result)
          (:value result)
          (recur (dec attempts) (:error result)))))))

;; Usage:
(def call-count (atom 0))

(defn flaky-operation []
  (swap! call-count inc)
  (if (< @call-count 3)
    (throw (Exception. "Not ready yet"))
    "Success!"))

;; (reset! call-count 0)
;; (retry 5 flaky-operation)  ;; => "Success!" (after 3 attempts)

;; Retry with exponential backoff:
(defn retry-with-backoff
  "Retries with exponential backoff."
  [n initial-delay-ms f]
  (loop [attempts n
         delay-ms initial-delay-ms]
    (let [result (try
                   {:success true :value (f)}
                   (catch Exception e
                     {:success false :error e}))]
      (cond
        (:success result)
        (:value result)

        (= attempts 1)
        (throw (:error result))

        :else
        (do
          (Thread/sleep delay-ms)
          (recur (dec attempts) (* delay-ms 2)))))))

;; ============================================
;; 10. WITH-OPEN FOR RESOURCE MANAGEMENT
;; ============================================
;; Ensures resources are closed even if exceptions occur.

;; with-open automatically closes resources:
(defn read-first-line [filename]
  (try
    (with-open [rdr (clojure.java.io/reader filename)]
      (first (line-seq rdr)))
    (catch java.io.FileNotFoundException e
      nil)))

;; Multiple resources:
(defn copy-file [src dest]
  (with-open [in (clojure.java.io/input-stream src)
              out (clojure.java.io/output-stream dest)]
    (clojure.java.io/copy in out)))

;; ============================================
;; 11. PRACTICAL ERROR HANDLING PATTERNS
;; ============================================

;; Pattern: Error boundary in web handlers
(defn wrap-error-handler [handler]
  (fn [request]
    (try
      (handler request)
      (catch clojure.lang.ExceptionInfo e
        (let [data (ex-data e)]
          (case (:type data)
            :not-found {:status 404 :body "Not found"}
            :unauthorized {:status 401 :body "Unauthorized"}
            :validation {:status 400 :body (:errors data)}
            {:status 500 :body "Internal error"})))
      (catch Exception e
        {:status 500
         :body "Internal server error"}))))

;; Pattern: Result type threading
(defn bind-result
  "Chains operations that might fail."
  [result f]
  (if (:error result)
    result
    (f (:value result))))

(defn parse-json [s]
  (try
    {:value (read-string s)}  ; Simplified
    (catch Exception e
      {:error "Invalid JSON"})))

(defn validate-data [data]
  (if (map? data)
    {:value data}
    {:error "Data must be a map"}))

(defn transform-data [data]
  {:value (assoc data :processed true)})

;; Chain operations:
(-> (parse-json "{:name \"Alice\"}")
    (bind-result validate-data)
    (bind-result transform-data))
;; => {:value {:name "Alice", :processed true}}

;; Pattern: Error accumulation
(defn validate-fields [data validations]
  (let [errors (reduce
                 (fn [errs [field validator message]]
                   (if (validator (get data field))
                     errs
                     (conj errs {:field field :message message})))
                 []
                 validations)]
    (if (empty? errors)
      {:valid true :data data}
      {:valid false :errors errors})))

(validate-fields
  {:name "" :email "bad" :age -5}
  [[:name #(not (empty? %)) "Name is required"]
   [:email #(re-matches #".+@.+\..+" (or % "")) "Invalid email"]
   [:age #(and (number? %) (pos? %)) "Age must be positive"]])
;; => {:valid false, :errors [{:field :name, :message "Name is required"} ...]}

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Write a safe-divide function that returns a result map
;; with either :result or :error keys.

;; Exercise 2: Create a function that parses a vector of strings to integers,
;; returning {:parsed [...] :failed [...]} with both successful parses
;; and the strings that failed.

;; Exercise 3: Write a function using pre/post conditions that calculates
;; the area of a rectangle (both dimensions must be positive).

;; Exercise 4: Create a retry function that takes a predicate to determine
;; if the error is retryable.

;; Exercise 5: Write a validate-user function using ex-info that checks
;; :name (required string), :age (positive int), :email (contains @).

;; ============================================
;; KEY TAKEAWAYS
;; ============================================
;; 1. Use try/catch/finally for Java-style exception handling
;; 2. Prefer ex-info for creating informative exceptions with data
;; 3. Use ex-data to extract information from ExceptionInfo
;; 4. Consider functional patterns: returning nil, result maps, or either types
;; 5. Use pre/post conditions for design-by-contract validation
;; 6. Use with-open for automatic resource cleanup
;; 7. Build retry logic for unreliable operations
;; 8. Accumulate errors when validating multiple fields

;; Next lesson: Namespaces and Project Organization
