;; ============================================
;; LESSON 08: DESTRUCTURING
;; ============================================
;; Destructuring is one of Clojure's most powerful features.
;; It allows you to bind names to parts of data structures
;; in a concise and readable way.

;; ============================================
;; 1. WHY DESTRUCTURING?
;; ============================================

;; Without destructuring (verbose):
(def point [10 20])
(let [x (first point)
      y (second point)]
  (str "x=" x ", y=" y))
;; => "x=10, y=20"

;; With destructuring (elegant):
(let [[x y] point]
  (str "x=" x ", y=" y))
;; => "x=10, y=20"

;; Destructuring works in:
;; - let bindings
;; - function parameters
;; - loop bindings
;; - for comprehensions
;; - anywhere bindings occur

;; ============================================
;; 2. SEQUENTIAL DESTRUCTURING (VECTORS/LISTS)
;; ============================================

;; Basic positional binding
(let [[a b c] [1 2 3]]
  [a b c])
;; => [1 2 3]

;; Extra elements are ignored
(let [[a b] [1 2 3 4 5]]
  [a b])
;; => [1 2]

;; Missing elements become nil
(let [[a b c d] [1 2]]
  [a b c d])
;; => [1 2 nil nil]

;; Nested destructuring
(let [[[a b] [c d]] [[1 2] [3 4]]]
  [a b c d])
;; => [1 2 3 4]

;; ============================================
;; 3. THE & REST SYNTAX
;; ============================================

;; Capture remaining elements with &
(let [[first second & rest] [1 2 3 4 5]]
  {:first first
   :second second
   :rest rest})
;; => {:first 1, :second 2, :rest (3 4 5)}

;; rest will be nil if no remaining elements
(let [[a b & more] [1 2]]
  more)
;; => nil

;; Useful for head/tail processing
(let [[head & tail] [1 2 3 4 5]]
  (str "Head: " head ", Tail: " tail))
;; => "Head: 1, Tail: (2 3 4 5)"

;; ============================================
;; 4. THE :as KEYWORD - KEEP ORIGINAL
;; ============================================

;; Sometimes you need both parts AND the whole
(let [[x y :as coords] [10 20]]
  {:x x
   :y y
   :original coords})
;; => {:x 10, :y 20, :original [10 20]}

;; Useful when passing the original along
(defn process-point [[x y :as point]]
  (println "Processing point:" point)
  [(* x 2) (* y 2)])

(process-point [5 10])
;; Prints: Processing point: [5 10]
;; => [10 20]

;; ============================================
;; 5. MAP DESTRUCTURING
;; ============================================

;; Basic map destructuring with :keys
(let [{:keys [name age]} {:name "Alice" :age 30}]
  (str name " is " age))
;; => "Alice is 30"

;; Without :keys (more verbose)
(let [{name :name age :age} {:name "Alice" :age 30}]
  (str name " is " age))
;; => "Alice is 30"

;; Use different local names
(let [{person-name :name person-age :age} {:name "Alice" :age 30}]
  (str person-name " is " person-age))
;; => "Alice is 30"

;; String keys (use :strs)
(let [{:strs [name age]} {"name" "Bob" "age" 25}]
  (str name " is " age))
;; => "Bob is 25"

;; Symbol keys (use :syms)
(let [{:syms [name age]} {'name "Carol" 'age 35}]
  (str name " is " age))
;; => "Carol is 35"

;; ============================================
;; 6. DEFAULT VALUES WITH :or
;; ============================================

;; Provide defaults for missing keys
(let [{:keys [name age country]
       :or {country "Unknown"}}
      {:name "Alice" :age 30}]
  (str name " from " country))
;; => "Alice from Unknown"

;; Multiple defaults
(let [{:keys [host port timeout]
       :or {host "localhost"
            port 8080
            timeout 5000}}
      {:port 3000}]
  {:host host :port port :timeout timeout})
;; => {:host "localhost", :port 3000, :timeout 5000}

;; ============================================
;; 7. THE :as KEYWORD FOR MAPS
;; ============================================

;; Keep reference to original map
(let [{:keys [name] :as person}
      {:name "Alice" :age 30 :email "alice@example.com"}]
  (assoc person :greeted true))
;; => {:name "Alice", :age 30, :email "alice@example.com", :greeted true}

;; ============================================
;; 8. NESTED DESTRUCTURING
;; ============================================

;; Destructure nested maps
(let [{:keys [name]
       {:keys [city zip]} :address}
      {:name "Alice"
       :address {:city "NYC" :zip "10001"}}]
  (str name " lives in " city))
;; => "Alice lives in NYC"

;; Alternative syntax (more explicit)
(let [{{:keys [city zip]} :address
       name :name}
      {:name "Alice"
       :address {:city "NYC" :zip "10001"}}]
  (str name ", " city " " zip))
;; => "Alice, NYC 10001"

;; Deeply nested
(let [{{{:keys [lat lng]} :coordinates} :location}
      {:location {:coordinates {:lat 40.7128 :lng -74.0060}}}]
  [lat lng])
;; => [40.7128 -74.006]

;; ============================================
;; 9. COMBINING SEQUENTIAL AND MAP DESTRUCTURING
;; ============================================

;; Vector of maps
(let [[{:keys [name]} {:keys [name]}]
      [{:name "Alice"} {:name "Bob"}]]
  (str name " and... wait, both are 'name'!"))
;; Note: Second binding shadows first!

;; Better approach
(let [[first-person second-person]
      [{:name "Alice"} {:name "Bob"}]]
  (str (:name first-person) " and " (:name second-person)))
;; => "Alice and Bob"

;; Map containing vectors
(let [{:keys [name]
       [first-score second-score] :scores}
      {:name "Alice" :scores [95 87 92]}]
  (str name " scored " first-score " and " second-score))
;; => "Alice scored 95 and 87"

;; ============================================
;; 10. DESTRUCTURING IN FUNCTION PARAMETERS
;; ============================================

;; Sequential destructuring in params
(defn distance [[x1 y1] [x2 y2]]
  (let [dx (- x2 x1)
        dy (- y2 y1)]
    (Math/sqrt (+ (* dx dx) (* dy dy)))))

(distance [0 0] [3 4])
;; => 5.0

;; Map destructuring in params
(defn greet [{:keys [name title]
              :or {title "Mr/Ms"}}]
  (str "Hello, " title " " name "!"))

(greet {:name "Smith" :title "Dr."})
;; => "Hello, Dr. Smith!"

(greet {:name "Jones"})
;; => "Hello, Mr/Ms Jones!"

;; Common pattern: options map
(defn connect [{:keys [host port timeout ssl?]
                :or {host "localhost"
                     port 5432
                     timeout 30000
                     ssl? false}
                :as options}]
  (println "Connecting with options:" options)
  {:connected true
   :host host
   :port port
   :timeout timeout
   :ssl? ssl?})

(connect {:host "db.example.com" :ssl? true})
;; => {:connected true, :host "db.example.com", :port 5432, ...}

;; ============================================
;; 11. DESTRUCTURING IN for AND doseq
;; ============================================

;; Destructure in for comprehension
(def people [{:name "Alice" :age 30}
             {:name "Bob" :age 25}
             {:name "Carol" :age 35}])

(for [{:keys [name age]} people]
  (str name " is " age " years old"))
;; => ("Alice is 30 years old" "Bob is 25 years old" ...)

;; Destructure pairs
(def scores {"Alice" 95 "Bob" 87 "Carol" 92})

(for [[name score] scores]
  (str name ": " score))
;; => ("Alice: 95" "Bob: 87" "Carol: 92")

;; In doseq (for side effects)
(doseq [[name score] scores]
  (println name "scored" score))
;; Prints each person's score

;; ============================================
;; 12. DESTRUCTURING IN loop
;; ============================================

;; Destructure in loop binding
(loop [[head & tail] [1 2 3 4 5]
       sum 0]
  (if head
    (recur tail (+ sum head))
    sum))
;; => 15

;; Process pairs
(loop [[[k v] & more] [[:a 1] [:b 2] [:c 3]]
       result {}]
  (if k
    (recur more (assoc result k (* v 2)))
    result))
;; => {:a 2, :b 4, :c 6}

;; ============================================
;; 13. NAMESPACED KEYS
;; ============================================

;; Clojure 1.9+ supports namespaced key destructuring
(let [{:keys [user/name user/email]}
      {:user/name "Alice" :user/email "alice@example.com"}]
  (str name " <" email ">"))
;; => "Alice <alice@example.com>"

;; Using namespace prefix shorthand
(let [{:keys [::name ::age]}  ; :: means current namespace
      {::name "Alice" ::age 30}]
  [name age])

;; Specify namespace for all keys
(let [{:user/keys [name email age]}
      {:user/name "Alice"
       :user/email "alice@example.com"
       :user/age 30}]
  {:name name :email email :age age})
;; => {:name "Alice", :email "alice@example.com", :age 30}

;; ============================================
;; 14. PRACTICAL EXAMPLES
;; ============================================

;; Example 1: Configuration handling
(defn start-server [{:keys [host port workers]
                     :or {host "0.0.0.0"
                          port 8080
                          workers 4}
                     :as config}]
  (println "Starting server with config:" config)
  {:status :running
   :host host
   :port port
   :workers workers})

;; Example 2: API response handling
(defn process-api-response
  [{:keys [status]
    {:keys [items total]} :data
    {:keys [message code]} :error}]
  (case status
    200 {:success true :count total :items items}
    {:success false :error-code code :message message}))

(process-api-response
  {:status 200
   :data {:items [{:id 1} {:id 2}] :total 2}})
;; => {:success true, :count 2, :items [{:id 1} {:id 2}]}

;; Example 3: Working with pairs/tuples
(defn update-inventory [inventory changes]
  (reduce (fn [inv [item delta]]
            (update inv item (fnil + 0) delta))
          inventory
          changes))

(update-inventory
  {:apples 10 :bananas 5}
  [[:apples 5] [:oranges 3] [:bananas -2]])
;; => {:apples 15, :bananas 3, :oranges 3}

;; Example 4: Multi-level config
(defn get-db-url
  [{{:keys [host port database user password]} :database
    {:keys [ssl-mode]} :security
    :or {ssl-mode "disable"}}]
  (str "postgresql://" user ":" password
       "@" host ":" port "/" database
       "?sslmode=" ssl-mode))

(get-db-url
  {:database {:host "localhost"
              :port 5432
              :database "myapp"
              :user "admin"
              :password "secret"}
   :security {:ssl-mode "require"}})
;; => "postgresql://admin:secret@localhost:5432/myapp?sslmode=require"

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Write a function that takes a vector [x y z]
;; and returns a map {:x x :y y :z z}

;; Exercise 2: Write a function that takes a person map with
;; :first-name, :last-name, and optional :middle-name
;; Returns the full name as a string

;; Exercise 3: Write a function that takes a nested structure:
;; {:user {:profile {:settings {:theme "dark"}}}}
;; and extracts the theme (with default "light")

;; Exercise 4: Given a list of [name score] pairs, write a
;; function that returns only names with score > 80
;; Input: [["Alice" 95] ["Bob" 75] ["Carol" 88]]
;; Output: ["Alice" "Carol"]

;; Exercise 5: Write a function that takes an options map
;; for an HTTP request with :method, :url, :headers, :body
;; with sensible defaults, and returns a formatted request map

;; ============================================
;; SOLUTIONS
;; ============================================

;; Solution 1
(defn vec-to-map [[x y z]]
  {:x x :y y :z z})

;; Solution 2
(defn full-name [{:keys [first-name middle-name last-name]}]
  (clojure.string/join " "
    (filter some? [first-name middle-name last-name])))

;; Solution 3
(defn get-theme [{{{{:keys [theme] :or {theme "light"}} :settings} :profile} :user}]
  theme)
;; Or more readable:
(defn get-theme-alt [config]
  (get-in config [:user :profile :settings :theme] "light"))

;; Solution 4
(defn high-scorers [scores]
  (for [[name score] scores
        :when (> score 80)]
    name))

;; Solution 5
(defn make-request [{:keys [method url headers body]
                     :or {method :get
                          headers {"Content-Type" "application/json"}}}]
  {:method method
   :url url
   :headers headers
   :body body})

;; ============================================
;; KEY TAKEAWAYS
;; ============================================
;; 1. Sequential: [a b c] - positional binding
;; 2. Rest: [a & more] - capture remaining elements
;; 3. Maps: {:keys [a b]} - extract by keyword
;; 4. Defaults: :or {key default} - provide fallbacks
;; 5. Original: :as name - keep reference to whole structure
;; 6. Nesting: Destructuring composes infinitely
;; 7. Works everywhere bindings occur
;;
;; Destructuring makes your code more readable and concise!

;; Next lesson: Namespaces and Project Organization
