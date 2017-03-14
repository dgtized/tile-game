(defproject tile-game "1.0.0-SNAPSHOT"
  :description "A Tile Puzzle Game and Solver"
  :min-lein-version "2.0.0"
  :main tile-game.core
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.494"]
                 [org.clojure/core.async "0.3.441"]
                 [reagent "0.6.1"]]
  :plugins [[lein-figwheel "0.5.9"]
            [lein-cljsbuild "1.1.5"]]
  :sources-paths ["src"]
  :clean-targets ^{:protect false}
  ["resources/public/js/out"
   "resources/public/js/release"
   "resources/public/js/tile-game.js"
   :target-path]
  :cljsbuild {:builds
              {"dev"
               {:source-paths ["src"]
                :figwheel {}
                :compiler {:main tile-game.grid
                           :asset-path "js/out"
                           :output-to "resources/public/js/tile-game.js"
                           :output-dir "resources/public/js/out"
                           :optimizations :none
                           :source-map-timestamp true}}
               "release"
               {:source-paths ["src"]
                :compiler {:main tile-game.grid
                           :output-to "resources/public/js/tile-game.js"
                           :output-dir "resources/public/js/release"
                           :optimizations :advanced
                           :source-map "resources/public/js/tile-game.js.map"}}}}
  :figwheel {:css-dirs ["resources/public/css"]
             :open-file-command "emacsclient"})
