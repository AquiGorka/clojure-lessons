;; ============================================
;; LESSON 21: CORE.ASYNC
;; ============================================
;; core.async brings Communicating Sequential Processes (CSP)
;; to Clojure. It provides channels for communication and
;; go blocks for lightweight concurrent processes.

;; ============================================
;; 1. WHAT IS CORE.ASYNC?
;; ============================================

;; core.async provides:
;; - Channels: queues for passing messages
;; - go blocks: lightweight "threads" (actually state machines)
;; - Operations for putting/taking from channels
;; - Coordination without callbacks (no callback hell!)

;; It's inspired by Go's goroutines and channels,
;; and CSP (Communicating Sequential Processes).

;; ============================================
;; 2. SETUP
;; ============================================

;; Add to deps.edn:
;; {:deps {org.clojure/core.async {:mvn/version "1.6.681"}}}

;; Or project.clj:
;; [org.clojure/core.async "1.6.681"]

(require '[clojure.core.async :as async
           :refer [go go-loop chan <! >! <!! >!!
                   close! put! take! timeout
                   alts! alts!! alt! alt!!
                   thread pipe pipeline]])

;; ============================================
;; 3. CHANNELS - THE BASICS
;; ============================================

;; Create a channel:
(def ch (chan))          ; Unbuffered channel
(def ch-10 (chan 10))    ; Buffered channel (holds 10 items)

;; Buffered channels don't block puts until full
;; Unbuffered channels block puts until someone takes

;; Channel operations:
;; >!  - put (inside go block)
;; <!  - take (inside go block)
;; >!! - blocking put (outside go block)
;; <!! - blocking take (outside go block)

;; Example with blocking operations (from regular thread):
(def my-chan (chan 1))

(>!! my-chan "Hello")     ; Put "Hello" on channel
(<!! my-chan)             ; => "Hello" (take from channel)

;; Close a channel:
(close! my-chan)

;; Taking from closed channel returns nil
(<!! my-chan)             ; => nil

;; ============================================
;; 4. GO BLOCKS
;; ============================================

;; go blocks create lightweight processes
;; They "park" instead of blocking threads

(def ch (chan))

;; Producer in a go block
(go
  (>! ch "message 1")
  (>! ch "message 2")
  (>! ch "message 3")
  (close! ch))

;; Consumer in a go block
(go
  (println "Got:" (<! ch))
  (println "Got:" (<! ch))
  (println "Got:" (<! ch)))

;; go blocks return a channel with their result
(def result-chan
  (go
    (+ 1 2 3)))

(<!! result-chan)         ; => 6

;; ============================================
;; 5. GO-LOOP FOR CONTINUOUS PROCESSING
;; ============================================

;; go-loop is a go block with loop built in

(def messages (chan))

;; Start a worker
(go-loop []
  (when-let [msg (<! messages)]
    (println "Processing:" msg)
    (recur)))

;; Send messages
(go
  (>! messages "Task 1")
  (>! messages "Task 2")
  (>! messages "Task 3")
  (close! messages))

;; go-loop with state
(def counter (chan))
(def results (chan))

(go-loop [count 0]
  (let [msg (<! counter)]
    (if msg
      (do
        (>! results count)
        (recur (inc count)))
      (close! results))))

;; ============================================
;; 6. TIMEOUT CHANNELS
;; ============================================

;; timeout creates a channel that closes after n ms

(def ch (chan))

(go
  (let [result (alts! [ch (timeout 1000)])]
    (if (= (second result) ch)
      (println "Got:" (first result))
      (println "Timeout!"))))

;; After 1 second: "Timeout!" (nothing was put on ch)

;; Using timeout for delays
(go
  (println "Starting...")
  (<! (timeout 2000))  ; Wait 2 seconds
  (println "Done!"))

;; ============================================
;; 7. ALTS! - SELECTING FROM MULTIPLE CHANNELS
;; ============================================

;; alts! waits for the first channel ready to communicate

(def ch1 (chan))
(def ch2 (chan))

(go
  (>! ch1 "from ch1"))

(go
  (let [[value channel] (alts! [ch1 ch2])]
    (println "Got" value "from" (if (= channel ch1) "ch1" "ch2"))))
;; => "Got from ch1 from ch1"

;; alts! with timeout for non-blocking check:
(go
  (let [[value channel] (alts! [ch1 (timeout 100)])]
    (if value
      (println "Got:" value)
      (println "Nothing ready"))))

;; alts! with priority (check in order):
(go
  (let [[value channel] (alts! [ch1 ch2] :priority true)]
    (println "Got:" value)))

;; ============================================
;; 8. ALT! - CLEANER MULTI-CHANNEL SELECT
;; ============================================

;; alt! provides a cleaner syntax than alts!

(def ch1 (chan))
(def ch2 (chan))

(go
  (alt!
    ch1 ([v] (println "ch1 gave:" v))
    ch2 ([v] (println "ch2 gave:" v))
    (timeout 1000) (println "Timeout!")))

;; alt! with put operations:
(go
  (alt!
    [[ch1 "value"]] (println "Sent to ch1")
    [[ch2 "value"]] (println "Sent to ch2")))

;; alt! with default:
(go
  (alt!
    ch1 ([v] (println "Got:" v))
    :default (println "Nothing available")))

;; ============================================
;; 9. BUFFER TYPES
;; ============================================

;; Fixed buffer (drops puts when full - blocks)
(def fixed-ch (chan 10))

;; Dropping buffer (drops newest when full)
(def dropping-ch (chan (async/dropping-buffer 10)))

;; Sliding buffer (drops oldest when full)
(def sliding-ch (chan (async/sliding-buffer 10)))

;; Use dropping/sliding for "lossy" channels
;; where it's OK to miss messages

;; Example: Only keep latest 3 values
(def latest (chan (async/sliding-buffer 3)))

(go
  (doseq [i (range 10)]
    (>! latest i)))

;; Only 7, 8, 9 remain (oldest dropped)

;; ============================================
;; 10. TRANSDUCERS WITH CHANNELS
;; ============================================

;; Channels can have transducers!
;; Transform data as it passes through

(def xform (comp (filter even?)
                 (map #(* % 2))))

(def ch (chan 10 xform))

(go
  (doseq [i (range 10)]
    (>! ch i))
  (close! ch))

(go-loop []
  (when-let [v (<! ch)]
    (println "Got:" v)  ; Only transformed even numbers
    (recur)))
;; Got: 0, 4, 8, 12, 16

;; Error handling with transducers
(def ch-with-errors
  (chan 10
        (map inc)
        (fn [ex]
          (println "Error:" (.getMessage ex)))))

;; ============================================
;; 11. THREAD vs GO
;; ============================================

;; go - uses a fixed thread pool, parks on channel ops
;; thread - creates a real thread, blocks on channel ops

;; Use go for channel operations and light computation
;; Use thread for blocking I/O or heavy computation

;; thread returns a channel with result
(def result
  (thread
    (Thread/sleep 1000)  ; OK to block in thread
    "done"))

(<!! result)  ; => "done"

;; DON'T block in go blocks!
;; BAD:
#_(go
    (Thread/sleep 1000)  ; Don't do this!
    (<! ch))

;; GOOD:
(go
  (<! (timeout 1000))    ; Park, don't block
  (<! ch))

;; ============================================
;; 12. PIPELINE FOR PARALLEL PROCESSING
;; ============================================

;; pipeline processes items in parallel

(def in (chan))
(def out (chan))

;; Process with 4 parallel workers
(pipeline 4
          out
          (map #(do (Thread/sleep 100)
                    (* % %)))
          in)

(go
  (doseq [i (range 10)]
    (>! in i))
  (close! in))

(go-loop []
  (when-let [v (<! out)]
    (println "Result:" v)
    (recur)))

;; pipeline-blocking for blocking operations
;; pipeline-async for async operations

;; ============================================
;; 13. PUB/SUB
;; ============================================

;; Publish/subscribe pattern

(def input (chan))
(def publisher (async/pub input :topic))

;; Subscribers
(def sports-sub (chan))
(def tech-sub (chan))

(async/sub publisher :sports sports-sub)
(async/sub publisher :tech tech-sub)

;; Consumers
(go-loop []
  (when-let [msg (<! sports-sub)]
    (println "Sports:" (:content msg))
    (recur)))

(go-loop []
  (when-let [msg (<! tech-sub)]
    (println "Tech:" (:content msg))
    (recur)))

;; Publish messages
(go
  (>! input {:topic :sports :content "Game tonight!"})
  (>! input {:topic :tech :content "New release!"})
  (>! input {:topic :sports :content "Score update"}))

;; ============================================
;; 14. MULT FOR BROADCASTING
;; ============================================

;; mult broadcasts to multiple channels

(def source (chan))
(def mult (async/mult source))

;; Create tap channels
(def tap1 (chan))
(def tap2 (chan))

(async/tap mult tap1)
(async/tap mult tap2)

;; Both taps receive all messages
(go (>! source "broadcast message"))

(go (println "tap1:" (<! tap1)))
(go (println "tap2:" (<! tap2)))
;; Both print "broadcast message"

;; Untap when done
(async/untap mult tap1)

;; ============================================
;; 15. MERGE AND MIX
;; ============================================

;; merge combines multiple channels into one
(def ch1 (chan))
(def ch2 (chan))
(def ch3 (chan))

(def merged (async/merge [ch1 ch2 ch3]))

(go (>! ch1 "from 1"))
(go (>! ch2 "from 2"))
(go (>! ch3 "from 3"))

(go-loop []
  (when-let [v (<! merged)]
    (println "Merged:" v)
    (recur)))

;; mix for more control (mute, pause, solo)
(def out (chan))
(def mixer (async/mix out))

(async/admix mixer ch1)
(async/admix mixer ch2)

;; Mute a channel
(async/toggle mixer {ch1 {:mute true}})

;; Solo a channel (only hear from it)
(async/toggle mixer {ch2 {:solo true}})

;; ============================================
;; 16. PRACTICAL EXAMPLE: WORKER POOL
;; ============================================

(defn worker-pool [n in-ch out-ch]
  (dotimes [_ n]
    (go-loop []
      (when-let [job (<! in-ch)]
        (let [result ((:task job))]  ; Execute task
          (>! out-ch {:id (:id job) :result result}))
        (recur)))))

(def jobs (chan 100))
(def results (chan 100))

(worker-pool 4 jobs results)

;; Submit jobs
(go
  (doseq [i (range 10)]
    (>! jobs {:id i
              :task (fn []
                      (Thread/sleep 100)
                      (* i i))}))
  (close! jobs))

;; Collect results
(go-loop [received 0]
  (when (< received 10)
    (let [{:keys [id result]} (<! results)]
      (println "Job" id "=" result)
      (recur (inc received)))))

;; ============================================
;; 17. PRACTICAL EXAMPLE: RATE LIMITER
;; ============================================

(defn rate-limited-chan
  "Returns a channel that rate-limits to n items per second."
  [n source]
  (let [out (chan)
        interval (/ 1000 n)]
    (go-loop []
      (when-let [v (<! source)]
        (>! out v)
        (<! (timeout interval))
        (recur)))
    out))

(def requests (chan 100))
(def limited (rate-limited-chan 2 requests))  ; 2 per second

(go
  (doseq [i (range 10)]
    (>! requests i)))

(go-loop []
  (when-let [v (<! limited)]
    (println (System/currentTimeMillis) "Processing:" v)
    (recur)))

;; ============================================
;; 18. ERROR HANDLING
;; ============================================

;; Pattern 1: Try-catch in go block
(go
  (try
    (let [result (<! (async/thread
                       (throw (Exception. "Oops!"))))]
      (println "Result:" result))
    (catch Exception e
      (println "Error:" (.getMessage e)))))

;; Pattern 2: Error channel
(defn safe-worker [in-ch out-ch err-ch]
  (go-loop []
    (when-let [job (<! in-ch)]
      (try
        (>! out-ch ((:fn job)))
        (catch Exception e
          (>! err-ch {:job job :error e})))
      (recur))))

;; Pattern 3: Result wrapping
(defn wrap-result [f]
  (try
    {:ok (f)}
    (catch Exception e
      {:error e})))

;; ============================================
;; 19. COMMON PATTERNS
;; ============================================

;; Pattern: Request-Response
(defn request [ch request-data]
  (let [response-ch (chan)]
    (go (>! ch {:request request-data
                :response-ch response-ch}))
    response-ch))

(defn handle-requests [ch handler]
  (go-loop []
    (when-let [{:keys [request response-ch]} (<! ch)]
      (let [response (handler request)]
        (>! response-ch response)
        (close! response-ch))
      (recur))))

;; Pattern: Fan-out, Fan-in
(defn fan-out [in n]
  (let [outs (repeatedly n #(chan))]
    (go-loop [i 0]
      (when-let [v (<! in)]
        (>! (nth outs (mod i n)) v)
        (recur (inc i))))
    outs))

(defn fan-in [ins]
  (async/merge ins))

;; Pattern: Batch processor
(defn batch-processor [in batch-size timeout-ms]
  (let [out (chan)]
    (go-loop [batch []]
      (let [[v ch] (alts! [in (timeout timeout-ms)])]
        (cond
          (nil? v) (do (when (seq batch)
                         (>! out batch))
                       (close! out))
          (= ch in) (let [new-batch (conj batch v)]
                      (if (>= (count new-batch) batch-size)
                        (do (>! out new-batch)
                            (recur []))
                        (recur new-batch)))
          :else (do (when (seq batch)
                      (>! out batch))
                    (recur [])))))
    out))

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Create a channel that debounces input
;; (only emits after no input for n ms)

;; Exercise 2: Implement a simple chat system with
;; multiple users (channels) and a broadcast mechanism

;; Exercise 3: Create a circuit breaker that stops
;; forwarding after n failures, then retries after timeout

;; Exercise 4: Build a producer-consumer system where
;; producers can be dynamically added/removed

;; Exercise 5: Implement a priority queue using channels
;; (high priority messages processed first)

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; 1. chan creates channels, close! closes them
;; 2. >! and <! for go blocks, >!! and <!! outside
;; 3. go blocks are lightweight (not real threads)
;; 4. Don't block in go blocks - use timeout, not Thread/sleep
;; 5. alts!/alt! select from multiple channels
;; 6. Buffers: fixed, dropping, sliding
;; 7. Channels can have transducers
;; 8. pipeline for parallel processing
;; 9. pub/sub for topic-based messaging
;; 10. mult for broadcasting
;; 11. merge for combining channels
;; 12. Use thread for blocking I/O, go for channel ops

;; core.async enables elegant concurrent code without
;; callbacks, locks, or complex thread management!
