;; ============================================
;; LESSON 11: NAMESPACES AND ORGANIZATION
;; ============================================
;; Namespaces are Clojure's way of organizing code
;; and avoiding naming conflicts. Understanding them
;; is essential for writing real Clojure applications.

;; ============================================
;; 1. WHAT IS A NAMESPACE?
;; ============================================

;; A namespace is a container for definitions (vars).
;; It provides:
;; - Organization: Group related functions together
;; - Isolation: Avoid naming conflicts
;; - Modularity: Control what's public vs private

;; Every Clojure file should declare a namespace
;; The namespace name should match the file path!

;; File: src/myapp/core.clj → Namespace: myapp.core
;; File: src/myapp/utils/strings.clj → Namespace: myapp.utils.strings

;; Note: Use underscores in filenames for hyphens in namespaces
;; File: src/my_app/user_utils.clj → Namespace: my-app.user-utils

;; ============================================
;; 2. DECLARING A NAMESPACE WITH `ns`
;; ============================================

;; Basic namespace declaration
(ns myapp.core)

;; The ns macro should be the first form in any file
;; (after any comments)

;; Full ns with various clauses:
(ns myapp.core
  "Documentation string for this namespace.
   Describes what this namespace contains."
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:import [java.util Date UUID]))

;; ============================================
;; 3. THE :require CLAUSE
;; ============================================

;; :require loads other Clojure namespaces

;; Basic require - must use full name
(ns example.one
  (:require [clojure.string]))
;; Usage: (clojure.string/upper-case "hello")

;; With alias using :as
(ns example.two
  (:require [clojure.string :as str]))
;; Usage: (str/upper-case "hello")

;; Refer specific symbols into current namespace
(ns example.three
  (:require [clojure.string :refer [upper-case lower-case]]))
;; Usage: (upper-case "hello") - no prefix needed!

;; Refer all (generally discouraged - pollutes namespace)
(ns example.four
  (:require [clojure.string :refer :all]))

;; Multiple requires
(ns example.five
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.java.io :as io]))

;; Nested requires (same prefix)
(ns example.six
  (:require [myapp.utils
             [strings :as strings]
             [numbers :as numbers]
             [dates :as dates]]))
;; Equivalent to:
;; [myapp.utils.strings :as strings]
;; [myapp.utils.numbers :as numbers]
;; [myapp.utils.dates :as dates]

;; ============================================
;; 4. THE :import CLAUSE
;; ============================================

;; :import is for Java classes

;; Single class
(ns example.seven
  (:import java.util.Date))
;; Usage: (Date.) - creates new Date

;; Multiple classes from same package
(ns example.eight
  (:import [java.util Date UUID ArrayList]
           [java.io File FileReader]))
;; Usage: (Date.), (UUID/randomUUID), (File. "path")

;; ============================================
;; 5. CREATING AND SWITCHING NAMESPACES (REPL)
;; ============================================

;; In the REPL, you can create/switch namespaces:

;; Create and switch to namespace
;; (in-ns 'my-new-namespace)

;; Create namespace with basic Clojure core available
;; (ns my-new-namespace)

;; Go back to user namespace
;; (in-ns 'user)

;; Check current namespace
*ns*  ;; Returns current namespace object

;; ============================================
;; 6. LOADING AND REQUIRING AT REPL
;; ============================================

;; require - load a namespace
;; (require '[clojure.string :as str])

;; require with reload
;; (require '[myapp.core :as core] :reload)

;; require with reload-all (reload dependencies too)
;; (require '[myapp.core :as core] :reload-all)

;; use - require + refer all (deprecated style)
;; (use 'clojure.string)  ; Don't do this in real code

;; refer - add symbols from already-loaded namespace
;; (refer 'clojure.string)

;; ============================================
;; 7. PRIVATE DEFINITIONS
;; ============================================

;; By default, all defs are public
(def public-var 42)  ; Accessible from other namespaces

;; Use defn- for private functions
(defn- private-helper [x]
  (* x 2))

;; Use ^:private metadata for other defs
(def ^:private secret-value "shh")

;; Private vars can still be accessed (but shouldn't be)
;; @#'some.namespace/private-var

;; ============================================
;; 8. ACCESSING VARS FROM OTHER NAMESPACES
;; ============================================

;; Fully qualified name
(clojure.string/upper-case "hello")
;; => "HELLO"

;; With alias
;; (assuming (:require [clojure.string :as str]))
;; (str/upper-case "hello")

;; With referred symbol
;; (assuming (:require [clojure.string :refer [upper-case]]))
;; (upper-case "hello")

;; ============================================
;; 9. PROJECT STRUCTURE
;; ============================================

;; Typical Clojure project structure:
;;
;; my-project/
;; ├── project.clj        ; or deps.edn (project config)
;; ├── src/
;; │   └── myapp/
;; │       ├── core.clj          ; myapp.core
;; │       ├── config.clj        ; myapp.config
;; │       ├── db.clj            ; myapp.db
;; │       └── utils/
;; │           ├── strings.clj   ; myapp.utils.strings
;; │           └── dates.clj     ; myapp.utils.dates
;; ├── test/
;; │   └── myapp/
;; │       ├── core_test.clj     ; myapp.core-test
;; │       └── db_test.clj       ; myapp.db-test
;; └── resources/
;;     └── config.edn

;; ============================================
;; 10. deps.edn (MODERN APPROACH)
;; ============================================

;; deps.edn is the modern way to manage dependencies
;; Example deps.edn:

;; {:deps {org.clojure/clojure {:mvn/version "1.11.1"}
;;         cheshire/cheshire {:mvn/version "5.11.0"}}
;;
;;  :paths ["src" "resources"]
;;
;;  :aliases {:test {:extra-paths ["test"]
;;                   :extra-deps {io.github.cognitect-labs/test-runner
;;                                {:git/tag "v0.5.1"
;;                                 :git/sha "dfb30dd"}}}
;;            :dev {:extra-paths ["dev"]}}}

;; Run with: clj -M:test

;; ============================================
;; 11. project.clj (LEININGEN)
;; ============================================

;; Leiningen is an older but still popular build tool
;; Example project.clj:

;; (defproject myapp "0.1.0-SNAPSHOT"
;;   :description "My awesome app"
;;   :url "https://example.com/myapp"
;;   :license {:name "EPL-2.0"}
;;   :dependencies [[org.clojure/clojure "1.11.1"]
;;                  [cheshire "5.11.0"]]
;;   :main ^:skip-aot myapp.core
;;   :target-path "target/%s"
;;   :profiles {:uberjar {:aot :all}})

;; Run with: lein run

;; ============================================
;; 12. PRACTICAL NAMESPACE ORGANIZATION
;; ============================================

;; Example: Web Application Structure

;; myapp.core - Entry point, main function
(ns myapp.core
  (:require [myapp.config :as config]
            [myapp.server :as server]
            [myapp.routes :as routes])
  (:gen-class))  ; Needed for -main in uberjar

(defn -main [& args]
  (let [port (or (first args) 8080)]
    (server/start! (config/load-config) port)))

;; myapp.config - Configuration handling
(ns myapp.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn load-config []
  (-> "config.edn" io/resource slurp edn/read-string))

;; myapp.db - Database operations
(ns myapp.db
  (:require [myapp.config :as config]))

(defn get-connection [] ...)
(defn query [sql params] ...)

;; myapp.models.user - User-specific logic
(ns myapp.models.user
  (:require [myapp.db :as db]))

(defn find-by-id [id] ...)
(defn create! [user-data] ...)
(defn update! [id changes] ...)

;; myapp.handlers.auth - Authentication handlers
(ns myapp.handlers.auth
  (:require [myapp.models.user :as user]
            [myapp.utils.crypto :as crypto]))

(defn login [request] ...)
(defn logout [request] ...)

;; ============================================
;; 13. NAMESPACE INTROSPECTION
;; ============================================

;; Get all public vars in a namespace
(keys (ns-publics 'clojure.string))
;; => (blank? ends-with? includes? ...)

;; Get all vars (including private)
(keys (ns-interns 'clojure.string))

;; Get all referred vars (from other namespaces)
(keys (ns-refers 'user))

;; Get all aliases
(ns-aliases *ns*)
;; => {str #namespace[clojure.string], ...}

;; Get a var's metadata
(meta #'clojure.string/upper-case)
;; => {:doc "...", :arglists ([s]), ...}

;; Find which namespace a symbol is from
(resolve 'upper-case)  ; Returns nil or the var

;; ============================================
;; 14. RELOADING CODE DURING DEVELOPMENT
;; ============================================

;; Method 1: require with :reload
;; (require '[myapp.core :as core] :reload)

;; Method 2: Use tools.namespace (recommended)
;; Add to deps: [org.clojure/tools.namespace "1.4.4"]

;; (require '[clojure.tools.namespace.repl :refer [refresh]])
;; (refresh)  ; Reloads all changed namespaces

;; Method 3: Load file directly
;; (load-file "src/myapp/core.clj")

;; ============================================
;; 15. COMMON PATTERNS AND BEST PRACTICES
;; ============================================

;; 1. One namespace per file, matching file path

;; 2. Order your ns clauses consistently:
(ns myapp.feature
  "Docstring describing the namespace."
  (:require [clojure.string :as str]       ; Clojure core libs first
            [clojure.set :as set]
            [ring.middleware.json :as json] ; Third-party libs
            [cheshire.core :as json]
            [myapp.config :as config]       ; Your own libs last
            [myapp.db :as db])
  (:import [java.util Date UUID]))          ; Java imports last

;; 3. Prefer :as over :refer for clarity
;; Good: (str/upper-case s)
;; Okay: (upper-case s) - less clear where it comes from

;; 4. Use :refer only for very common functions
(ns example
  (:require [clojure.test :refer [deftest is testing]]))

;; 5. Group related functions in the same namespace

;; 6. Keep namespaces focused - split if too large

;; 7. Use descriptive namespace names
;; Good: myapp.auth.oauth2
;; Bad: myapp.stuff

;; ============================================
;; EXERCISES
;; ============================================

;; Exercise 1: Create a namespace declaration for a file at
;; src/my_app/utils/validation.clj that requires clojure.string
;; as str and imports java.util.regex.Pattern

;; Exercise 2: Write a namespace that has one public function
;; 'greet' and one private helper function 'format-name'

;; Exercise 3: Create two namespaces where the second requires
;; the first and uses its functions with an alias

;; Exercise 4: Use ns-publics to explore what functions are
;; available in clojure.set namespace

;; Exercise 5: Set up a basic deps.edn for a project that uses
;; clojure 1.11.1 and cheshire for JSON parsing

;; ============================================
;; KEY TAKEAWAYS
;; ============================================
;; 1. Every file should have a namespace matching its path
;; 2. Use :require to load Clojure namespaces
;; 3. Use :import for Java classes
;; 4. Prefer :as aliases over :refer for clarity
;; 5. Use defn- for private functions
;; 6. Use deps.edn or project.clj for dependency management
;; 7. Organize namespaces by feature/domain
;; 8. Keep namespaces focused and cohesive
;; 9. Use tools.namespace for smooth development reloading

;; Next lesson: State and Atoms
