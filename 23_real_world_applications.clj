;; ============================================
;; LESSON 23: BUILDING REAL WORLD APPLICATIONS
;; ============================================
;; Now that you've learned the fundamentals, let's put
;; it all together and build real applications in Clojure.

;; ============================================
;; 1. PROJECT STRUCTURE
;; ============================================

;; A typical Clojure project structure:
;;
;; my-app/
;; ├── deps.edn              ; Dependencies (or project.clj for Leiningen)
;; ├── src/
;; │   └── my_app/
;; │       ├── core.clj      ; Main entry point
;; │       ├── config.clj    ; Configuration handling
;; │       ├── db.clj        ; Database layer
;; │       ├── handlers.clj  ; Request handlers
;; │       └── routes.clj    ; Routing
;; ├── test/
;; │   └── my_app/
;; │       ├── core_test.clj
;; │       └── db_test.clj
;; ├── resources/
;; │   ├── config.edn
;; │   └── public/           ; Static files
;; └── README.md

;; ============================================
;; 2. DEPS.EDN FOR A WEB APPLICATION
;; ============================================

;; Example deps.edn:
;; {:paths ["src" "resources"]
;;  :deps {org.clojure/clojure {:mvn/version "1.11.1"}
;;
;;         ;; Web server
;;         ring/ring-core {:mvn/version "1.10.0"}
;;         ring/ring-jetty-adapter {:mvn/version "1.10.0"}
;;         ring/ring-json {:mvn/version "0.5.1"}
;;
;;         ;; Routing
;;         metosin/reitit {:mvn/version "0.7.0-alpha5"}
;;
;;         ;; Database
;;         com.github.seancorfield/next.jdbc {:mvn/version "1.3.894"}
;;         org.postgresql/postgresql {:mvn/version "42.6.0"}
;;
;;         ;; JSON
;;         cheshire/cheshire {:mvn/version "5.12.0"}
;;
;;         ;; Logging
;;         ch.qos.logback/logback-classic {:mvn/version "1.4.11"}
;;         org.clojure/tools.logging {:mvn/version "1.2.4"}}
;;
;;  :aliases
;;  {:dev {:extra-paths ["dev"]
;;         :extra-deps {nrepl/nrepl {:mvn/version "1.0.0"}}}
;;   :test {:extra-paths ["test"]
;;          :extra-deps {lambdaisland/kaocha {:mvn/version "1.87.1366"}}}
;;   :build {:deps {io.github.clojure/tools.build {:mvn/version "0.9.6"}}
;;           :ns-default build}}}

;; ============================================
;; 3. CONFIGURATION MANAGEMENT
;; ============================================

(ns my-app.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

;; Load configuration from EDN file
(defn load-config
  "Load configuration from config.edn resource file."
  []
  (-> "config.edn"
      io/resource
      slurp
      edn/read-string))

;; Environment-aware configuration
(defn get-env
  "Get environment variable with optional default."
  [key & [default]]
  (or (System/getenv key) default))

(defn load-config-with-env
  "Load config and override with environment variables."
  []
  (let [base-config (load-config)]
    (-> base-config
        (assoc-in [:database :url]
                  (get-env "DATABASE_URL"
                           (get-in base-config [:database :url])))
        (assoc-in [:server :port]
                  (Integer/parseInt
                    (get-env "PORT"
                             (str (get-in base-config [:server :port]))))))))

;; Example config.edn:
;; {:server {:port 8080
;;           :host "0.0.0.0"}
;;  :database {:url "jdbc:postgresql://localhost:5432/myapp"
;;             :user "dbuser"
;;             :password "dbpass"
;;             :pool-size 10}
;;  :features {:enable-signup true
;;             :max-upload-size 10485760}}

;; ============================================
;; 4. DATABASE LAYER
;; ============================================

(ns my-app.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [next.jdbc.sql :as sql]))

;; Connection pool setup
(defonce ^:private datasource (atom nil))

(defn init-db!
  "Initialize database connection pool."
  [config]
  (reset! datasource
          (jdbc/get-datasource
            {:dbtype "postgresql"
             :jdbcUrl (:url config)
             :user (:user config)
             :password (:password config)
             :maximumPoolSize (:pool-size config 10)})))

(defn get-connection []
  @datasource)

;; Generic CRUD operations
(defn find-all
  "Find all records in a table."
  [table]
  (sql/query (get-connection)
             [(str "SELECT * FROM " (name table))]
             {:builder-fn rs/as-unqualified-maps}))

(defn find-by-id
  "Find a record by ID."
  [table id]
  (first
    (sql/query (get-connection)
               [(str "SELECT * FROM " (name table) " WHERE id = ?") id]
               {:builder-fn rs/as-unqualified-maps})))

(defn insert!
  "Insert a new record."
  [table data]
  (sql/insert! (get-connection) table data
               {:return-keys true
                :builder-fn rs/as-unqualified-maps}))

(defn update!
  "Update a record by ID."
  [table id data]
  (sql/update! (get-connection) table data {:id id}))

(defn delete!
  "Delete a record by ID."
  [table id]
  (sql/delete! (get-connection) table {:id id}))

;; Transactions
(defn with-transaction
  "Execute function within a transaction."
  [f]
  (jdbc/with-transaction [tx (get-connection)]
    (f tx)))

;; Example: Complex query
(defn find-users-with-orders []
  (jdbc/execute! (get-connection)
    ["SELECT u.*, COUNT(o.id) as order_count
      FROM users u
      LEFT JOIN orders o ON u.id = o.user_id
      GROUP BY u.id
      ORDER BY order_count DESC"]
    {:builder-fn rs/as-unqualified-maps}))

;; ============================================
;; 5. DOMAIN MODELS
;; ============================================

(ns my-app.models.user
  (:require [my-app.db :as db]
            [clojure.spec.alpha :as s]))

;; Specs for validation
(s/def ::id pos-int?)
(s/def ::username (s/and string? #(re-matches #"[a-z0-9_]{3,20}" %)))
(s/def ::email (s/and string? #(re-matches #".+@.+\..+" %)))
(s/def ::password (s/and string? #(>= (count %) 8)))

(s/def ::user-create
  (s/keys :req-un [::username ::email ::password]))

(s/def ::user
  (s/keys :req-un [::id ::username ::email]
          :opt-un [::created_at ::updated_at]))

;; Model functions
(defn find-all []
  (db/find-all :users))

(defn find-by-id [id]
  (db/find-by-id :users id))

(defn find-by-email [email]
  (first
    (db/query (db/get-connection)
              ["SELECT * FROM users WHERE email = ?" email])))

(defn create! [user-data]
  {:pre [(s/valid? ::user-create user-data)]}
  (let [hashed-password (hash-password (:password user-data))]
    (db/insert! :users
                (-> user-data
                    (dissoc :password)
                    (assoc :password_hash hashed-password
                           :created_at (java.time.Instant/now))))))

(defn update! [id user-data]
  (db/update! :users id
              (assoc user-data :updated_at (java.time.Instant/now))))

(defn delete! [id]
  (db/delete! :users id))

;; Password hashing (use a real library like buddy in production)
(defn- hash-password [password]
  ;; In real code: (buddy.hashers/derive password)
  (str "hashed:" password))

(defn verify-password [password hash]
  ;; In real code: (buddy.hashers/check password hash)
  (= (str "hashed:" password) hash))

;; ============================================
;; 6. HTTP HANDLERS
;; ============================================

(ns my-app.handlers.users
  (:require [my-app.models.user :as user]
            [ring.util.response :as response]
            [clojure.spec.alpha :as s]))

(defn list-users
  "GET /api/users"
  [request]
  (response/response {:users (user/find-all)}))

(defn get-user
  "GET /api/users/:id"
  [request]
  (let [id (-> request :path-params :id Integer/parseInt)]
    (if-let [user (user/find-by-id id)]
      (response/response {:user user})
      (response/not-found {:error "User not found"}))))

(defn create-user
  "POST /api/users"
  [request]
  (let [user-data (:body request)]
    (if (s/valid? ::user/user-create user-data)
      (let [created (user/create! user-data)]
        (-> (response/response {:user created})
            (response/status 201)))
      (-> (response/response
            {:error "Validation failed"
             :details (s/explain-str ::user/user-create user-data)})
          (response/status 400)))))

(defn update-user
  "PUT /api/users/:id"
  [request]
  (let [id (-> request :path-params :id Integer/parseInt)
        user-data (:body request)]
    (if (user/find-by-id id)
      (do (user/update! id user-data)
          (response/response {:user (user/find-by-id id)}))
      (response/not-found {:error "User not found"}))))

(defn delete-user
  "DELETE /api/users/:id"
  [request]
  (let [id (-> request :path-params :id Integer/parseInt)]
    (if (user/find-by-id id)
      (do (user/delete! id)
          (response/response {:message "User deleted"}))
      (response/not-found {:error "User not found"}))))

;; ============================================
;; 7. ROUTING WITH REITIT
;; ============================================

(ns my-app.routes
  (:require [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [muuntaja.core :as m]
            [my-app.handlers.users :as users]
            [my-app.handlers.auth :as auth]
            [my-app.middleware :as mw]))

(def api-routes
  ["/api"
   {:middleware [mw/wrap-auth]}  ; Apply auth to all /api routes

   ["/users"
    ["" {:get users/list-users
         :post users/create-user}]
    ["/:id" {:get users/get-user
             :put users/update-user
             :delete users/delete-user}]]

   ["/auth"
    {:middleware []}  ; Override - no auth required
    ["/login" {:post auth/login}]
    ["/register" {:post auth/register}]
    ["/logout" {:post auth/logout}]]])

(def routes
  [["/" {:get (fn [_] {:status 200 :body {:message "Welcome to My App API"}})}]
   ["/health" {:get (fn [_] {:status 200 :body {:status "ok"}})}]
   api-routes])

(def app
  (ring/ring-handler
    (ring/router
      routes
      {:data {:coercion reitit.coercion.spec/coercion
              :muuntaja m/instance
              :middleware [parameters/parameters-middleware
                           muuntaja/format-middleware
                           coercion/coerce-exceptions-middleware
                           coercion/coerce-request-middleware
                           coercion/coerce-response-middleware
                           mw/wrap-logging
                           mw/wrap-error-handling]}})
    (ring/routes
      (ring/create-resource-handler {:path "/"})
      (ring/create-default-handler))))

;; ============================================
;; 8. MIDDLEWARE
;; ============================================

(ns my-app.middleware
  (:require [clojure.tools.logging :as log]
            [ring.util.response :as response]))

(defn wrap-logging
  "Log all requests."
  [handler]
  (fn [request]
    (let [start (System/currentTimeMillis)
          response (handler request)
          duration (- (System/currentTimeMillis) start)]
      (log/info (format "%s %s %d %dms"
                        (:request-method request)
                        (:uri request)
                        (:status response)
                        duration))
      response)))

(defn wrap-error-handling
  "Catch exceptions and return error responses."
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch clojure.lang.ExceptionInfo e
        (let [data (ex-data e)]
          (case (:type data)
            :not-found (response/not-found {:error (:message data)})
            :unauthorized (-> (response/response {:error (:message data)})
                              (response/status 401))
            :forbidden (-> (response/response {:error (:message data)})
                           (response/status 403))
            :validation (-> (response/response {:error (:message data)
                                                :details (:details data)})
                            (response/status 400))
            (do
              (log/error e "Unhandled exception")
              (-> (response/response {:error "Internal server error"})
                  (response/status 500))))))
      (catch Exception e
        (log/error e "Unexpected error")
        (-> (response/response {:error "Internal server error"})
            (response/status 500))))))

(defn wrap-auth
  "Verify authentication token."
  [handler]
  (fn [request]
    (if-let [token (get-in request [:headers "authorization"])]
      (if-let [user (verify-token token)]
        (handler (assoc request :user user))
        (-> (response/response {:error "Invalid token"})
            (response/status 401)))
      (-> (response/response {:error "Authentication required"})
          (response/status 401)))))

(defn- verify-token [token]
  ;; In real code: verify JWT, look up session, etc.
  (when (clojure.string/starts-with? token "Bearer ")
    {:id 1 :username "testuser"}))

(defn wrap-cors
  "Add CORS headers."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (response/header "Access-Control-Allow-Origin" "*")
          (response/header "Access-Control-Allow-Methods" "GET,POST,PUT,DELETE,OPTIONS")
          (response/header "Access-Control-Allow-Headers" "Content-Type,Authorization")))))

;; ============================================
;; 9. APPLICATION LIFECYCLE
;; ============================================

(ns my-app.core
  (:require [my-app.config :as config]
            [my-app.db :as db]
            [my-app.routes :as routes]
            [ring.adapter.jetty :as jetty]
            [clojure.tools.logging :as log])
  (:gen-class))

(defonce server (atom nil))

(defn start-server!
  "Start the HTTP server."
  [config]
  (let [port (get-in config [:server :port] 8080)]
    (log/info (str "Starting server on port " port))
    (reset! server
            (jetty/run-jetty routes/app
                             {:port port
                              :join? false}))))

(defn stop-server!
  "Stop the HTTP server."
  []
  (when @server
    (log/info "Stopping server...")
    (.stop @server)
    (reset! server nil)))

(defn init!
  "Initialize application."
  []
  (let [config (config/load-config-with-env)]
    (log/info "Initializing application...")
    (db/init-db! (:database config))
    (start-server! config)
    (log/info "Application started successfully")))

(defn shutdown!
  "Shutdown application gracefully."
  []
  (log/info "Shutting down...")
  (stop-server!)
  (log/info "Shutdown complete"))

(defn -main
  "Main entry point."
  [& args]
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. ^Runnable shutdown!))
  (init!)
  ;; Keep main thread alive
  @(promise))

;; ============================================
;; 10. REPL-DRIVEN DEVELOPMENT
;; ============================================

;; In dev/user.clj:
(ns user
  (:require [my-app.core :as core]
            [my-app.config :as config]
            [my-app.db :as db]
            [clojure.tools.namespace.repl :refer [refresh]]))

(defn go
  "Start the system."
  []
  (core/init!))

(defn halt
  "Stop the system."
  []
  (core/shutdown!))

(defn reset
  "Stop, reload code, and restart."
  []
  (halt)
  (refresh :after 'user/go))

;; REPL workflow:
;; 1. (go) - start system
;; 2. Make code changes
;; 3. (reset) - reload and restart
;; 4. Test in REPL or browser
;; 5. Repeat!

;; ============================================
;; 11. TESTING
;; ============================================

(ns my-app.handlers.users-test
  (:require [clojure.test :refer :all]
            [my-app.handlers.users :as handlers]
            [my-app.test-helpers :refer [with-test-db]]))

(use-fixtures :each with-test-db)

(deftest test-list-users
  (testing "returns list of users"
    (let [response (handlers/list-users {})]
      (is (= 200 (:status response)))
      (is (vector? (get-in response [:body :users]))))))

(deftest test-get-user-not-found
  (testing "returns 404 for non-existent user"
    (let [response (handlers/get-user {:path-params {:id "9999"}})]
      (is (= 404 (:status response))))))

(deftest test-create-user
  (testing "creates user with valid data"
    (let [response (handlers/create-user
                     {:body {:username "testuser"
                             :email "test@example.com"
                             :password "password123"}})]
      (is (= 201 (:status response)))
      (is (some? (get-in response [:body :user :id]))))))

(deftest test-create-user-validation
  (testing "returns 400 for invalid data"
    (let [response (handlers/create-user
                     {:body {:username "ab"  ; too short
                             :email "invalid"
                             :password "short"}})]
      (is (= 400 (:status response))))))

;; Test helper
(ns my-app.test-helpers
  (:require [my-app.db :as db]
            [next.jdbc :as jdbc]))

(defn with-test-db [f]
  (db/init-db! {:url "jdbc:h2:mem:test"
                :user "sa"
                :password ""})
  ;; Run migrations for test db
  (jdbc/execute! (db/get-connection)
    ["CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        username VARCHAR(50),
        email VARCHAR(100),
        password_hash VARCHAR(100),
        created_at TIMESTAMP,
        updated_at TIMESTAMP)"])
  (try
    (f)
    (finally
      ;; Cleanup
      (jdbc/execute! (db/get-connection) ["DROP TABLE users"]))))

;; ============================================
;; 12. BUILDING AND DEPLOYING
;; ============================================

;; build.clj for creating uberjars:
;; (ns build
;;   (:require [clojure.tools.build.api :as b]))
;;
;; (def lib 'my-app/my-app)
;; (def version "0.1.0")
;; (def class-dir "target/classes")
;; (def basis (b/create-basis {:project "deps.edn"}))
;; (def uber-file (format "target/%s-%s.jar" (name lib) version))
;;
;; (defn clean [_]
;;   (b/delete {:path "target"}))
;;
;; (defn uber [_]
;;   (clean nil)
;;   (b/copy-dir {:src-dirs ["src" "resources"]
;;                :target-dir class-dir})
;;   (b/compile-clj {:basis basis
;;                   :src-dirs ["src"]
;;                   :class-dir class-dir})
;;   (b/uber {:class-dir class-dir
;;            :uber-file uber-file
;;            :basis basis
;;            :main 'my-app.core}))

;; Build: clj -T:build uber
;; Run: java -jar target/my-app-0.1.0.jar

;; Dockerfile:
;; FROM clojure:openjdk-17-tools-deps AS builder
;; WORKDIR /app
;; COPY deps.edn .
;; RUN clojure -P
;; COPY . .
;; RUN clj -T:build uber
;;
;; FROM openjdk:17-slim
;; COPY --from=builder /app/target/my-app-0.1.0.jar /app.jar
;; EXPOSE 8080
;; CMD ["java", "-jar", "/app.jar"]

;; ============================================
;; 13. BACKGROUND JOBS
;; ============================================

(ns my-app.jobs
  (:require [clojure.tools.logging :as log])
  (:import [java.util.concurrent Executors TimeUnit ScheduledExecutorService]))

(defonce ^ScheduledExecutorService scheduler
  (Executors/newScheduledThreadPool 2))

(defn schedule-job!
  "Schedule a job to run periodically."
  [name f interval-ms]
  (log/info (str "Scheduling job: " name))
  (.scheduleAtFixedRate scheduler
                        (fn []
                          (try
                            (log/debug (str "Running job: " name))
                            (f)
                            (catch Exception e
                              (log/error e (str "Job failed: " name)))))
                        0
                        interval-ms
                        TimeUnit/MILLISECONDS))

(defn shutdown-scheduler! []
  (.shutdown scheduler))

;; Example jobs
(defn cleanup-expired-sessions! []
  (log/info "Cleaning up expired sessions...")
  ;; Implementation here
  )

(defn send-pending-emails! []
  (log/info "Sending pending emails...")
  ;; Implementation here
  )

(defn init-jobs! []
  (schedule-job! "session-cleanup" cleanup-expired-sessions! (* 1000 60 15))  ; Every 15 min
  (schedule-job! "email-sender" send-pending-emails! (* 1000 60)))            ; Every minute

;; ============================================
;; 14. CACHING
;; ============================================

(ns my-app.cache
  (:require [clojure.core.cache.wrapped :as cache]))

;; Simple in-memory cache
(def user-cache (cache/ttl-cache-factory {} :ttl (* 5 60 1000)))  ; 5 min TTL

(defn cached-get-user [id fetch-fn]
  (cache/lookup-or-miss user-cache id fetch-fn))

(defn invalidate-user [id]
  (cache/evict user-cache id))

;; LRU cache for API responses
(def response-cache (cache/lru-cache-factory {} :threshold 1000))

(defn cache-response [key response]
  (cache/miss response-cache key response))

(defn get-cached-response [key]
  (cache/lookup response-cache key))

;; ============================================
;; 15. PUTTING IT ALL TOGETHER
;; ============================================

;; Application startup sequence:
;; 1. Load configuration
;; 2. Initialize database pool
;; 3. Run database migrations
;; 4. Start background jobs
;; 5. Initialize caches
;; 6. Start HTTP server
;; 7. Log startup complete

;; Application shutdown sequence:
;; 1. Stop accepting new requests
;; 2. Wait for in-flight requests
;; 3. Stop background jobs
;; 4. Close database connections
;; 5. Log shutdown complete

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Add a /api/products endpoint with full CRUD
;; operations and appropriate specs

;; Exercise 2: Implement JWT authentication with buddy-auth

;; Exercise 3: Add pagination to the list endpoints
;; (page, per_page query params)

;; Exercise 4: Implement rate limiting middleware

;; Exercise 5: Add integration tests that hit the HTTP API

;; ============================================
;; KEY TAKEAWAYS
;; ============================================

;; 1. Structure projects by feature/domain
;; 2. Use deps.edn for dependency management
;; 3. Configuration should be environment-aware
;; 4. Database layer should be thin and generic
;; 5. Middleware handles cross-cutting concerns
;; 6. REPL-driven development is powerful
;; 7. Test at multiple levels (unit, integration, e2e)
;; 8. Build uberjars for deployment
;; 9. Consider background jobs for async work
;; 10. Cache strategically for performance

;; ============================================
;; RECOMMENDED LIBRARIES
;; ============================================

;; Web:
;; - ring (HTTP abstraction)
;; - reitit (routing)
;; - muuntaja (content negotiation)
;; - buddy (security, auth)

;; Database:
;; - next.jdbc (JDBC wrapper)
;; - honeysql (SQL DSL)
;; - migratus (migrations)

;; Testing:
;; - kaocha (test runner)
;; - test.check (property-based)
;; - mock-clj (mocking)

;; Other:
;; - component/integrant (system management)
;; - timbre (logging)
;; - cheshire (JSON)
;; - clj-http (HTTP client)

;; CONGRATULATIONS! You've completed the Clojure learning path!
