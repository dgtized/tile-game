(defproject tile-game "1.0.0-SNAPSHOT"
  :description "A Tile Puzzle Game and Solver"
  :min-lein-version "2.7.1"
  :main tile-game.core
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/core.async "0.4.500"]
                 [reagent "0.8.1"]
                 [com.bhauman/figwheel-main "0.2.3"]
                 [com.bhauman/rebel-readline-cljs "0.1.4"]]
  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [test2junit "1.3.3"]]
  :sources-paths ["src"]
  :resource-paths ["resources" "target"]
  :test2junit-output-dir "target/test2junit"
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [org.clojure/test.check "0.9.0" :scope "test"]]}}
  :clean-targets ^{:protect false}
  ["resources/public/js/out"
   "resources/public/js/release"
   "resources/public/js/tile-game.js"
   :target-path]
  :cljsbuild {:builds
              {"release"
               {:source-paths ["src" "resources"]
                :compiler {:main tile-game.grid
                           :output-to "resources/public/js/tile-game.js"
                           :output-dir "resources/public/js/release"
                           :optimizations :advanced
                           :source-map "resources/public/js/tile-game.js.map"}}}}
  :aliases
  {"figwheel" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]})
