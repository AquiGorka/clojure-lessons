;; ============================================
;; LESSON 13: ATOMS AND STATE MANAGEMENT
;; ============================================
;; Clojure embraces immutability, but real programs
;; need state. Atoms provide safe, synchronous state
;; management for independent values.

;; ============================================
;; 1. THE PHILOSOPHY OF STATE IN CLOJURE
;; ============================================

;; In most languages, variables are mutable:
;;   x = 1
;;   x = 2  // mutated!
;;
;; In Clojure, values are immutable. We don't mutate values,
;; we manage IDENTITY over TIME.
;;
;; Think of it like this:
;; - Your bank account BALANCE is a value (immutable)
;; - Your bank account IDENTITY persists over time
;; - The account's state TRANSITIONS between values
;;
;; Clojure provides reference types to manage identity:
;; - Atoms: Independent, synchronous state
;; - Refs: Coordinated, synchronous state (STM)
;; - Agents: Independent, asynchronous state
;; - Vars: Thread-local, dynamic state
;;
;; This lesson focuses on ATOMS - the most commonly used.

;; ============================================
;; 2. CREATING ATOMS
;; ============================================

;; Create an atom with `atom`
(def counter (atom 0))

;; Atoms can hold any value
(def user-name (atom "Anonymous"))
(def settings (atom {:theme "dark" :font-size 14}))
(def items (atom []))
(def active-users (atom #{}))

;; With metadata and validators (covered later)
(def validated-counter
  (atom 0
        :validator pos?  ; Must always be positive
        :meta {:created (java.util.Date.)}))

;; ============================================
;; 3. READING ATOM VALUES: deref / @
;; ============================================

;; Use `deref` or the @ reader macro
(def my-atom (atom 42))

(deref my-atom)    ;; => 42
@my-atom           ;; => 42 (shorthand, preferred)

;; Reading is instantaneous - you get the current value
(def state (atom {:count 0 :name "Test"}))

@state                  ;; => {:count 0, :name "Test"}
(:count @state)         ;; => 0
(:name @state)          ;; => "Test"

;; Reading never blocks or causes contention

;; ============================================
;; 4. UPDATING ATOMS: swap!
;; ============================================

;; `swap!` applies a function to the current value
;; Syntax: (swap! atom fn args...)

(def counter (atom 0))

(swap! counter inc)     ;; => 1
@counter                ;; => 1

(swap! counter inc)     ;; => 2
(swap! counter inc)     ;; => 3
@counter                ;; => 3

;; With additional arguments
(swap! counter + 10)    ;; => 13 (applies (+ current-value 10))
@counter                ;; => 13

(swap! counter - 5)     ;; => 8
@counter                ;; => 8

;; With anonymous functions
(swap! counter #(* % 2))  ;; => 16
@counter                  ;; => 16

;; Complex example with maps
(def user (atom {:name "Alice" :age 30 :visits 0}))

(swap! user update :visits inc)
;; => {:name "Alice", :age 30, :visits 1}

(swap! user assoc :email "alice@example.com")
;; => {:name "Alice", :age 30, :visits 1, :email "alice@example.com"}

(swap! user update :age inc)
;; => {:name "Alice", :age 31, :visits 1, :email "alice@example.com"}

(swap! user dissoc :visits)
;; => {:name "Alice", :age 31, :email "alice@example.com"}

;; ============================================
;; 5. SETTING ATOMS: reset!
;; ============================================

;; `reset!` replaces the value entirely (ignores current value)

(def config (atom {:debug false}))

(reset! config {:debug true :log-level "info"})
@config  ;; => {:debug true, :log-level "info"}

;; reset! when you don't care about the current value
;; swap! when you need to transform the current value

;; Return value: reset! returns the new value
(reset! config {:fresh true})  ;; => {:fresh true}

;; ============================================
;; 6. ATOMIC COMPARE-AND-SET: compare-and-set!
;; ============================================

;; Only updates if current value equals expected value
;; Syntax: (compare-and-set! atom expected-value new-value)

(def val (atom 100))

(compare-and-set! val 100 200)  ;; => true (was 100, now 200)
@val                            ;; => 200

(compare-and-set! val 100 300)  ;; => false (wasn't 100, unchanged)
@val                            ;; => 200 (still!)

;; Useful for lock-free algorithms
;; Usually swap! is preferred

;; ============================================
;; 7. HOW ATOMS HANDLE CONCURRENCY
;; ============================================

;; Atoms use compare-and-swap (CAS) internally:
;; 1. Read current value
;; 2. Apply function to get new value
;; 3. Atomically compare-and-swap
;; 4. If another thread changed it, RETRY from step 1

;; This means:
;; - Your swap! function might be called multiple times!
;; - Your function MUST be pure (no side effects)
;; - Operations are always consistent

;; WRONG - side effects in swap!
(def counter (atom 0))
#_(swap! counter (fn [n]
                   (println "Called!")  ; Might print multiple times!
                   (inc n)))

;; RIGHT - pure function
(swap! counter inc)

;; Demonstrate retry behavior (conceptually):
(def shared (atom 0))

;; If two threads do this simultaneously:
;; Thread A: (swap! shared inc)
;; Thread B: (swap! shared inc)
;;
;; Both will succeed, result will be 2
;; One of them might retry, but the final result is correct

;; ============================================
;; 8. VALIDATORS
;; ============================================

;; Validators ensure atom values always meet criteria

(def age (atom 25 :validator #(and (integer? %) (>= % 0))))

(swap! age inc)      ;; => 26 (valid)
@age                 ;; => 26

;; (swap! age - 100) ;; ERROR! IllegalStateException
;; -74 fails the validator

;; (reset! age -5)   ;; ERROR! Same thing

;; Add validator to existing atom
(def score (atom 100))
(set-validator! score #(>= % 0))

(swap! score - 50)   ;; => 50 (valid)
;; (swap! score - 100) ;; ERROR! Would be -50

;; Remove validator
(set-validator! score nil)
(swap! score - 100)  ;; => -50 (now allowed)

;; Complex validator
(def user (atom {:name "" :age 0}
                :validator (fn [u]
                             (and (string? (:name u))
                                  (integer? (:age u))
                                  (>= (:age u) 0)))))

;; ============================================
;; 9. WATCHES
;; ============================================

;; Watches are callbacks triggered when atom changes

(def monitored (atom 0))

;; Add a watch
;; (add-watch atom key fn)
;; fn receives: key, atom-ref, old-value, new-value
(add-watch monitored :logger
           (fn [key ref old-val new-val]
             (println (str "Changed from " old-val " to " new-val))))

(swap! monitored inc)
;; Prints: Changed from 0 to 1

(swap! monitored + 10)
;; Prints: Changed from 1 to 11

;; Remove a watch
(remove-watch monitored :logger)

(swap! monitored inc)  ;; No output - watch removed

;; Practical example: Auto-save
(def document (atom {:title "Untitled" :content "" :saved false}))

(add-watch document :auto-save
           (fn [_ _ old new]
             (when (not= (:content old) (:content new))
               (println "Content changed, saving...")
               ;; In real code: (save-to-disk! new)
               )))

(swap! document assoc :content "Hello World")
;; Prints: Content changed, saving...

(swap! document assoc :title "My Doc")
;; No output - content didn't change

;; ============================================
;; 10. swap-vals! AND reset-vals!
;; ============================================

;; These return BOTH old and new values (Clojure 1.9+)

(def x (atom 10))

(swap-vals! x inc)
;; => [10 11] - [old-value new-value]

(swap-vals! x * 2)
;; => [11 22]

(reset-vals! x 100)
;; => [22 100]

;; Useful when you need to act on the old value
(def queue (atom [1 2 3]))

(let [[old new] (swap-vals! queue rest)]
  (println "Processed:" (first old))
  (println "Remaining:" new))
;; Processed: 1
;; Remaining: (2 3)

;; ============================================
;; 11. PRACTICAL PATTERNS
;; ============================================

;; Pattern 1: Counter
(defn make-counter []
  (let [count (atom 0)]
    {:increment (fn [] (swap! count inc))
     :decrement (fn [] (swap! count dec))
     :reset     (fn [] (reset! count 0))
     :value     (fn [] @count)}))

(def my-counter (make-counter))
((:increment my-counter))  ;; => 1
((:increment my-counter))  ;; => 2
((:value my-counter))      ;; => 2
((:reset my-counter))      ;; => 0

;; Pattern 2: Cache
(def cache (atom {}))

(defn cached-compute [key compute-fn]
  (if-let [cached (get @cache key)]
    cached
    (let [result (compute-fn)]
      (swap! cache assoc key result)
      result)))

(cached-compute :expensive (fn [] (Thread/sleep 1000) 42))
;; Takes 1 second first time

(cached-compute :expensive (fn [] (Thread/sleep 1000) 42))
;; Instant! Returns cached 42

;; Pattern 3: Application State
(def app-state
  (atom {:user nil
         :page :home
         :notifications []
         :settings {:theme "light"}}))

(defn login! [user]
  (swap! app-state assoc :user user))

(defn logout! []
  (swap! app-state assoc :user nil))

(defn navigate! [page]
  (swap! app-state assoc :page page))

(defn add-notification! [msg]
  (swap! app-state update :notifications conj msg))

(defn clear-notifications! []
  (swap! app-state assoc :notifications []))

;; Pattern 4: ID Generator
(def next-id (atom 0))

(defn generate-id []
  (swap! next-id inc))

(generate-id)  ;; => 1
(generate-id)  ;; => 2
(generate-id)  ;; => 3

;; Pattern 5: Rate Limiter
(defn make-rate-limiter [max-requests window-ms]
  (let [requests (atom [])]
    (fn []
      (let [now (System/currentTimeMillis)
            window-start (- now window-ms)]
        (swap! requests (fn [reqs]
                          (conj (filterv #(> % window-start) reqs) now)))
        (<= (count @requests) max-requests)))))

(def limiter (make-rate-limiter 3 1000))
(limiter)  ;; => true
(limiter)  ;; => true
(limiter)  ;; => true
(limiter)  ;; => false (rate limited!)

;; ============================================
;; 12. ATOMS VS OTHER APPROACHES
;; ============================================

;; When to use Atoms:
;; - Single independent value
;; - Synchronous updates needed
;; - No coordination with other state required

;; When NOT to use Atoms:
;; - Multiple values that must change together → Use Refs
;; - Asynchronous/background updates → Use Agents
;; - Thread-local state → Use dynamic Vars

;; Atoms are the workhorse of Clojure state management
;; Start with atoms, only use refs/agents when needed

;; ============================================
;; 13. COMMON MISTAKES TO AVOID
;; ============================================

;; MISTAKE 1: Side effects in swap!
;; BAD:
#_(swap! counter (fn [n]
                   (send-email!)  ; Side effect!
                   (inc n)))

;; GOOD:
#_(let [new-val (swap! counter inc)]
    (send-email!))  ; Side effect outside swap!

;; MISTAKE 2: Reading then writing (race condition potential)
;; BAD:
#_(when (> @counter 10)
    (reset! counter 0))  ; Value might have changed!

;; GOOD:
#_(swap! counter #(if (> % 10) 0 %))

;; MISTAKE 3: Nested deref in swap!
;; BAD:
#_(swap! atom1 (fn [v] (+ v @atom2)))  ; atom2 read not atomic!

;; BETTER (if truly independent):
#_(let [v2 @atom2]
    (swap! atom1 + v2))

;; MISTAKE 4: Overusing atoms
;; Don't make everything an atom. Most data should be immutable.
;; Only use atoms for true mutable state.

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Create an atom-backed stack with push!, pop!, and peek!
;; functions. pop! should return the popped value.

;; Exercise 2: Create a "bank account" with deposit!, withdraw!, and
;; balance functions. Use a validator to prevent negative balance.

;; Exercise 3: Implement a simple observer pattern using watches.
;; Create a "temperature" atom that notifies when temp goes above 100.

;; Exercise 4: Create a memoization function using atoms that caches
;; function results.

;; Exercise 5: Build a simple state machine using an atom.
;; States: :idle -> :loading -> :success/:error -> :idle
;; Include transition validation.

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; 1. Atoms provide thread-safe, synchronous state management
;; 2. @atom or (deref atom) to read
;; 3. (swap! atom fn args...) to update based on current value
;; 4. (reset! atom value) to replace entirely
;; 5. Swap functions MUST be pure - they may retry!
;; 6. Validators ensure state invariants
;; 7. Watches provide change notifications
;; 8. Atoms are for independent state - no coordination
;; 9. Most Clojure code should be pure; atoms are for the edges

;; Next lesson: Refs and Software Transactional Memory (STM)
