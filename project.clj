(defproject tile-game "1.0.0-SNAPSHOT"
  :description "A Tile Puzzle Game and Solver"
  :min-lein-version "2.7.1"
  :main tile-game.core
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [org.clojure/core.async "0.4.474"]
                 [reagent "0.8.1"]]
  :plugins [[lein-figwheel "0.5.13"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [test2junit "1.3.3"]]
  :sources-paths ["src"]
  :test2junit-output-dir "target/test2junit"
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  [org.clojure/test.check "0.9.0" :scope "test"]
                                  [figwheel-sidecar "0.5.16"]]
                   :source-paths ["src" "dev"] }}
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :clean-targets ^{:protect false}
  ["resources/public/js/out"
   "resources/public/js/release"
   "resources/public/js/tile-game.js"
   :target-path]
  :cljsbuild {:builds
              {"dev"
               {:source-paths ["src"]
                :figwheel {:on-jsload "tile-game.grid/on-js-reload",
                           :open-urls ["http://localhost:3449/index.html"]}
                :compiler {:main tile-game.grid
                           :asset-path "js/out"
                           :output-to "resources/public/js/tile-game.js"
                           :output-dir "resources/public/js/out"
                           :optimizations :none
                           :source-map-timestamp true
                           :preloads [devtools.preload]}}
               "release"
               {:source-paths ["src"]
                :compiler {:main tile-game.grid
                           :output-to "resources/public/js/tile-game.js"
                           :output-dir "resources/public/js/release"
                           :optimizations :advanced
                           :source-map "resources/public/js/tile-game.js.map"}}}}
  :figwheel {:css-dirs ["resources/public/css"]
             :open-file-command "emacsclient"})
