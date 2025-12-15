;; ============================================
;; LESSON 17: CONCURRENCY WITH REFS AND AGENTS
;; ============================================
;; Beyond atoms, Clojure provides Refs for coordinated
;; state changes (STM) and Agents for asynchronous updates.
;; These complete Clojure's concurrency toolkit.

;; ============================================
;; PART 1: REFS AND SOFTWARE TRANSACTIONAL MEMORY
;; ============================================

;; ============================================
;; 1. THE PROBLEM: COORDINATED STATE
;; ============================================

;; Atoms work great for independent state, but what if
;; you need to update multiple pieces of state atomically?

;; Example: Bank transfer between two accounts
;; - Debit account A
;; - Credit account B
;; Both MUST succeed or BOTH must fail!

;; With atoms, you can't guarantee this:
;; (swap! account-a - 100)  ; What if this succeeds...
;; (swap! account-b + 100)  ; ...but this fails?

;; Refs solve this with Software Transactional Memory (STM)

;; ============================================
;; 2. CREATING REFS
;; ============================================

;; Create refs with `ref`
(def account-a (ref 1000))
(def account-b (ref 500))

;; Multiple refs for a more complex example
(def inventory (ref {:apples 100 :oranges 50 :bananas 75}))
(def orders (ref []))
(def revenue (ref 0))

;; With validators and metadata
(def balance
  (ref 0
       :validator #(>= % 0)  ; Can't go negative
       :meta {:created (java.util.Date.)}))

;; ============================================
;; 3. READING REFS: deref / @
;; ============================================

;; Same as atoms - use deref or @
@account-a        ;; => 1000
(deref account-b) ;; => 500

@inventory        ;; => {:apples 100, :oranges 50, :bananas 75}

;; ============================================
;; 4. TRANSACTIONS WITH dosync
;; ============================================

;; All ref modifications MUST happen inside a transaction
;; Transactions are created with `dosync`

;; Basic transfer:
(defn transfer! [from to amount]
  (dosync
    (alter from - amount)
    (alter to + amount)))

(transfer! account-a account-b 200)

@account-a  ;; => 800
@account-b  ;; => 700

;; The dosync block ensures:
;; - Both changes succeed, or neither does
;; - No other transaction sees partial changes
;; - Consistent view of all refs during transaction

;; ============================================
;; 5. UPDATING REFS: alter
;; ============================================

;; `alter` is like swap! but for refs
;; Syntax: (alter ref fn args...)

(dosync
  (alter inventory update :apples + 50))

@inventory  ;; => {:apples 150, :oranges 50, :bananas 75}

;; Multiple alters in one transaction:
(dosync
  (alter inventory update :oranges - 10)
  (alter inventory update :bananas - 5)
  (alter revenue + 25))

@inventory  ;; => {:apples 150, :oranges 40, :bananas 70}
@revenue    ;; => 25

;; ============================================
;; 6. UPDATING REFS: ref-set
;; ============================================

;; `ref-set` replaces the value entirely (like reset!)

(dosync
  (ref-set orders [{:id 1 :item :apple :qty 5}]))

@orders  ;; => [{:id 1, :item :apple, :qty 5}]

;; ============================================
;; 7. COMMUTATIVE UPDATES: commute
;; ============================================

;; `commute` is like alter but for commutative operations
;; Operations that can be reordered: +, *, conj, etc.

;; commute allows more concurrency by not requiring
;; strict ordering of operations

(def page-views (ref 0))

;; Many threads can increment without contention:
(dosync
  (commute page-views inc))

;; Use commute when:
;; - Order of updates doesn't matter
;; - You want better performance under contention
;; - Operations are commutative (a + b = b + a)

;; Use alter when:
;; - Order matters
;; - You need the exact value during transaction
;; - Operations are not commutative

;; ============================================
;; 8. TRANSACTION SEMANTICS
;; ============================================

;; Transactions provide ACI properties:
;; - Atomicity: All or nothing
;; - Consistency: Validators always respected
;; - Isolation: Transactions don't see each other's changes

;; Transactions may RETRY if there's contention!
;; Your functions should be FREE OF SIDE EFFECTS!

;; BAD - side effects in transaction:
#_(dosync
    (println "Transferring...")  ; May print multiple times!
    (alter account-a - 100)
    (alter account-b + 100))

;; GOOD - side effects outside transaction:
#_(let [result (dosync
                 (alter account-a - 100)
                 (alter account-b + 100)
                 :success)]
    (println "Transfer complete:" result))

;; ============================================
;; 9. ensure: PREVENT WRITE SKEW
;; ============================================

;; `ensure` protects a ref from being modified by other
;; transactions, even if you're just reading it

(def max-total (ref 2000))

(defn safe-transfer! [from to amount]
  (dosync
    (ensure max-total)  ; Lock this ref
    (let [total (+ @from @to)]
      (when (<= total @max-total)
        (alter from - amount)
        (alter to + amount)))))

;; Without ensure, another transaction could change
;; max-total between reading it and committing

;; ============================================
;; 10. PRACTICAL EXAMPLE: INVENTORY SYSTEM
;; ============================================

(def products (ref {:widget {:price 10 :stock 100}
                    :gadget {:price 25 :stock 50}
                    :gizmo {:price 15 :stock 75}}))

(def order-history (ref []))
(def total-revenue (ref 0))

(defn place-order! [product-id quantity]
  (dosync
    (let [product (get @products product-id)]
      (when (and product (>= (:stock product) quantity))
        (let [order-total (* (:price product) quantity)
              order {:id (java.util.UUID/randomUUID)
                     :product product-id
                     :quantity quantity
                     :total order-total
                     :timestamp (java.util.Date.)}]
          ;; Update all refs atomically
          (alter products update-in [product-id :stock] - quantity)
          (alter order-history conj order)
          (alter total-revenue + order-total)
          order)))))

(place-order! :widget 5)
;; => {:id #uuid"...", :product :widget, :quantity 5, :total 50, ...}

@products
;; => {:widget {:price 10, :stock 95}, ...}

@total-revenue
;; => 50

;; ============================================
;; PART 2: AGENTS FOR ASYNCHRONOUS STATE
;; ============================================

;; ============================================
;; 11. WHAT ARE AGENTS?
;; ============================================

;; Agents manage independent state with ASYNCHRONOUS updates
;; - Updates happen in background threads
;; - Order is preserved per-agent
;; - Great for I/O, logging, accumulating results

;; ============================================
;; 12. CREATING AGENTS
;; ============================================

(def log-agent (agent []))
(def stats-agent (agent {:requests 0 :errors 0}))
(def file-writer (agent nil))

;; With error handler and mode
(def robust-agent
  (agent 0
         :error-handler (fn [ag ex]
                          (println "Agent error:" (.getMessage ex)))
         :error-mode :continue))

;; ============================================
;; 13. READING AGENTS: deref / @
;; ============================================

@log-agent    ;; => []
@stats-agent  ;; => {:requests 0, :errors 0}

;; Reading is always immediate (current value)

;; ============================================
;; 14. SENDING TO AGENTS: send and send-off
;; ============================================

;; `send` - for CPU-bound actions (uses fixed thread pool)
(send stats-agent update :requests inc)

@stats-agent  ;; => {:requests 1, :errors 0} (eventually)

;; `send-off` - for I/O-bound actions (uses expandable pool)
(send-off log-agent conj {:event "startup" :time (java.util.Date.)})

;; send returns immediately - action happens asynchronously!

;; Multiple sends are queued and processed in order:
(send stats-agent update :requests inc)
(send stats-agent update :requests inc)
(send stats-agent update :requests inc)

;; All three will happen, in order, eventually

;; ============================================
;; 15. WAITING FOR AGENTS: await and await-for
;; ============================================

;; `await` blocks until all pending actions complete
(send stats-agent update :requests inc)
(await stats-agent)
@stats-agent  ;; Guaranteed to have the inc applied

;; `await-for` with timeout (milliseconds)
(send stats-agent update :requests inc)
(await-for 1000 stats-agent)  ; Wait up to 1 second
;; Returns logical false if timeout, logical true if complete

;; await multiple agents:
(await stats-agent log-agent)

;; ============================================
;; 16. ERROR HANDLING IN AGENTS
;; ============================================

;; By default, errors put agent in failed state
(def error-prone (agent 0))

(send error-prone (fn [_] (throw (Exception. "Oops!"))))

(agent-error error-prone)
;; => #error{:cause "Oops!", ...}

;; Agent is now "failed" - further sends are rejected
;; (send error-prone inc)  ; Throws exception!

;; Restart the agent:
(restart-agent error-prone 0)

@error-prone  ;; => 0 (reset value)
(send error-prone inc)  ;; Works again!

;; Better: Set error mode to :continue
(def resilient (agent 0 :error-mode :continue))

(send resilient (fn [_] (throw (Exception. "Ignored"))))
(send resilient inc)  ; This still works!

;; Or use :error-handler
(def logged-agent
  (agent 0
         :error-handler (fn [ag ex]
                          (println "Error in agent:" (.getMessage ex)))))

;; ============================================
;; 17. AGENTS IN TRANSACTIONS
;; ============================================

;; You CAN send to agents inside dosync!
;; The send happens only IF the transaction commits.

(def audit-log (agent []))

(defn audited-transfer! [from to amount]
  (dosync
    (alter from - amount)
    (alter to + amount)
    ;; This send only happens if transfer succeeds
    (send audit-log conj {:from from :to to :amount amount
                          :time (java.util.Date.)})))

;; This is SAFE because:
;; - send is held until transaction commits
;; - If transaction retries, sends don't duplicate
;; - If transaction fails, sends don't happen

;; ============================================
;; 18. PRACTICAL EXAMPLE: ASYNC LOGGER
;; ============================================

(def logger (agent nil))

(defn log! [level message]
  (send-off logger
            (fn [_]
              (let [timestamp (java.time.LocalDateTime/now)
                    entry (format "[%s] %s: %s%n" timestamp level message)]
                (print entry)
                (flush)
                ;; Could also write to file here
                entry))))

(log! :INFO "Application started")
(log! :DEBUG "Loading configuration")
(log! :WARN "Config file not found, using defaults")
(log! :INFO "Server listening on port 8080")

;; All logging happens asynchronously, in order

;; ============================================
;; 19. PRACTICAL EXAMPLE: PARALLEL ACCUMULATOR
;; ============================================

(defn parallel-process [items process-fn]
  (let [results (agent [])]
    ;; Send all items for processing
    (doseq [item items]
      (send-off results
                (fn [acc]
                  (conj acc (process-fn item)))))
    ;; Wait for completion
    (await results)
    @results))

(defn slow-double [n]
  (Thread/sleep 100)  ; Simulate slow operation
  (* n 2))

;; (parallel-process [1 2 3 4 5] slow-double)
;; => [2 4 6 8 10]

;; ============================================
;; 20. SHUTDOWN AGENTS
;; ============================================

;; When your app exits, shut down agent thread pools:
;; (shutdown-agents)

;; This lets the JVM exit cleanly.
;; Call this in your shutdown hook or main exit.

;; ============================================
;; 21. COMPARISON: ATOMS VS REFS VS AGENTS
;; ============================================

;; ATOMS:
;; - Independent, synchronous state
;; - swap!/reset! - immediate
;; - Best for: Simple counters, caches, single values
;; - No coordination with other state

;; REFS:
;; - Coordinated, synchronous state
;; - alter/ref-set in dosync
;; - Best for: Multiple values that change together
;; - Transactional guarantees (ACI)

;; AGENTS:
;; - Independent, asynchronous state
;; - send/send-off - queued
;; - Best for: I/O, logging, background tasks
;; - Actions processed in order per-agent

;; SUMMARY TABLE:
;; | Feature      | Atom | Ref | Agent |
;; |--------------|------|-----|-------|
;; | Synchronous  | Yes  | Yes | No    |
;; | Coordinated  | No   | Yes | No    |
;; | Retries      | Yes  | Yes | No    |
;; | Blocking     | No   | Yes | No    |
;; | Best for     | Solo | STM | I/O   |

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Create a multi-account banking system with refs.
;; Implement transfer!, deposit!, withdraw!, and total-balance.
;; Ensure you can't overdraw accounts.

;; Exercise 2: Create an agent-based web scraper that:
;; - Takes a list of URLs
;; - Fetches each asynchronously
;; - Accumulates results in an agent
;; - Logs progress to a logging agent

;; Exercise 3: Implement a simple STM-based shopping cart:
;; - Product catalog (ref)
;; - User cart (ref)
;; - Inventory (ref)
;; - Adding to cart decrements inventory atomically

;; Exercise 4: Create a rate-limited API client using agents:
;; - Queue requests to an agent
;; - Process with delays between calls
;; - Accumulate responses

;; Exercise 5: Build a "saga" pattern with refs:
;; - Multiple steps that must all succeed
;; - Compensating actions if any step fails
;; - Audit logging via agents

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; REFS:
;; 1. Use dosync for all ref modifications
;; 2. alter for standard updates, commute for commutative
;; 3. Transactions may retry - keep them pure!
;; 4. Use ensure to prevent write skew
;; 5. Great for coordinated state changes

;; AGENTS:
;; 1. send for CPU-bound, send-off for I/O-bound
;; 2. Actions are queued and processed in order
;; 3. Use await/await-for when you need results
;; 4. Handle errors with :error-mode and :error-handler
;; 5. Safe to send from within transactions

;; GENERAL:
;; - Start simple (atoms), add coordination only when needed
;; - Keep state transitions pure
;; - Use the right tool for the job

;; Next lesson: core.async and Channels
