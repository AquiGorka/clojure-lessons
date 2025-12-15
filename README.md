# Learn Clojure: From Zero to Hero

Welcome to your comprehensive Clojure learning journey! This collection of 23 sequential lessons will take you from absolute beginner to advanced Clojure developer.

## 🚀 Getting Started

### Prerequisites
1. **Java JDK 8+** - Clojure runs on the JVM
   ```bash
   java -version
   ```
2. **Clojure CLI** - Install from [clojure.org](https://clojure.org/guides/install)
   ```bash
   # macOS
   brew install clojure/tools/clojure
   
   # Linux
   curl -O https://download.clojure.org/install/linux-install-1.11.1.1435.sh
   chmod +x linux-install-1.11.1.1435.sh
   sudo ./linux-install-1.11.1.1435.sh
   ```

### Starting the REPL
```bash
clj
```

### Recommended Editors
- **VS Code + Calva** (beginner-friendly)
- **Emacs + CIDER** (powerful)
- **IntelliJ + Cursive** (great for Java developers)
- **Vim + Conjure** (lightweight)

---

## 📚 Learning Path

### Part 1: Foundations (Lessons 1-5)
| Lesson | File | Topics |
|--------|------|--------|
| 01 | `01_introduction_and_setup.clj` | What is Clojure, installation, REPL basics |
| 02 | `02_basic_data_types.clj` | Numbers, strings, keywords, symbols, booleans |
| 03 | `03_collections.clj` | Lists, vectors, maps, sets |
| 04 | `04_functions.clj` | defn, anonymous functions, arity, destructuring |
| 05 | `05_control_flow.clj` | if, when, cond, case, boolean operators |

### Part 2: Core Concepts (Lessons 6-10)
| Lesson | File | Topics |
|--------|------|--------|
| 06 | `06_sequences_and_laziness.clj` | Sequences, lazy evaluation, infinite sequences |
| 07 | `07_higher_order_functions.clj` | map, filter, reduce, comp, partial |
| 08 | `08_destructuring.clj` | Sequential & map destructuring, nested patterns |
| 09 | `09_let_bindings_and_scope.clj` | let, letfn, if-let, when-let, dynamic scope |
| 10 | `10_threading_macros.clj` | ->, ->>, as->, some->, cond-> |

### Part 3: Real-World Clojure (Lessons 11-15)
| Lesson | File | Topics |
|--------|------|--------|
| 11 | `11_namespaces_and_organization.clj` | ns, require, import, project structure |
| 12 | `12_error_handling.clj` | try/catch, ex-info, ex-data, assertions |
| 13 | `13_atoms_and_state.clj` | Atoms, swap!, reset!, validators, watches |
| 14 | `14_protocols_and_records.clj` | defprotocol, defrecord, extend-type, reify |
| 15 | `15_multimethods.clj` | defmulti, defmethod, hierarchies |

### Part 4: Advanced Topics (Lessons 16-23)
| Lesson | File | Topics |
|--------|------|--------|
| 16 | `16_macros.clj` | defmacro, syntax quote, macroexpand |
| 17 | `17_refs_and_agents.clj` | STM, dosync, Refs, Agents, concurrency |
| 18 | `18_java_interop.clj` | Calling Java, creating objects, interop patterns |
| 19 | `19_transducers.clj` | Composable transformations, performance |
| 20 | `20_spec.clj` | Data validation, generative testing |
| 21 | `21_core_async.clj` | Channels, go blocks, CSP-style concurrency |
| 22 | `22_testing.clj` | clojure.test, fixtures, mocking, property-based testing |
| 23 | `23_real_world_applications.clj` | Project structure, web apps, databases, deployment |

---

## 🎯 How to Use These Lessons

1. **Read the lesson file** - Each file is heavily commented with explanations
2. **Try the code in REPL** - Copy/paste examples and experiment
3. **Do the exercises** - Found at the end of each lesson
4. **Move to the next lesson** - They build on each other

### Loading a Lesson in REPL
```clojure
;; Start REPL in this directory
clj

;; Load a lesson file
(load-file "01_introduction_and_setup.clj")
```

---

## 🗺️ Quick Reference

### Essential Functions
```clojure
;; Collections
(first coll)    (rest coll)     (cons x coll)
(conj coll x)   (into to from)  (count coll)
(get coll key)  (assoc m k v)   (update m k f)

;; Sequences
(map f coll)    (filter pred coll)    (reduce f init coll)
(take n coll)   (drop n coll)         (range)

;; Functions
(defn name [args] body)
(fn [args] body)
#(+ % 1)

;; Flow Control
(if test then else)
(when test & body)
(cond & clauses)
```

### Truthiness
- **Falsy**: `false` and `nil`
- **Truthy**: Everything else (including `0`, `""`, `[]`)

### Threading Macros
```clojure
(-> x (f a) (g b))   ; Thread first: (g (f x a) b)
(->> x (f a) (g b))  ; Thread last:  (g b (f a x))
```

---

## 📖 Additional Resources

### Official
- [Clojure.org](https://clojure.org/) - Official site
- [ClojureDocs](https://clojuredocs.org/) - Community documentation
- [Clojure Cheatsheet](https://clojure.org/api/cheatsheet)

### Books
- "Clojure for the Brave and True" by Daniel Higginbotham (free online)
- "Programming Clojure" by Alex Miller
- "The Joy of Clojure" by Michael Fogus

### Practice
- [4Clojure](https://4clojure.oxal.org/) - Clojure problems
- [Exercism Clojure Track](https://exercism.org/tracks/clojure)
- [Advent of Code](https://adventofcode.com/) - in Clojure

### Community
- [Clojurians Slack](https://clojurians.slack.com/)
- [Clojure Subreddit](https://reddit.com/r/Clojure)
- [ClojureVerse](https://clojureverse.org/)

---

## 💡 Tips for Success

1. **Embrace the REPL** - It's your playground and debugging tool
2. **Think in transformations** - Data in, data out
3. **Start simple** - Clojure rewards small, composable functions
4. **Read error messages** - They're usually helpful
5. **Use the docs** - `(doc fn-name)` in REPL
6. **Practice daily** - Even 15 minutes helps

---

Happy learning! 🎉

*"Clojure is the result of many years of thinking about programming, and realizing that most of what I was doing wasn't programming, but rather working around programming language shortcomings."* — Rich Hickey