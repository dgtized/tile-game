(defproject tile-game "1.0.0-SNAPSHOT"
  :description "A Tile Puzzle Game and Solver"
  :min-lein-version "2.0.0"
  :main tile-game.core
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/math.numeric-tower "0.0.2"]
                 [org.clojure/clojurescript "1.9.494"]
                 [rm-hull/monet "0.3.0"]]
  :plugins [[lein-figwheel "0.5.9"]]
  :sources-paths ["src"]
  :clean-targets ^{:protect false}
  ["resources/public/js/out"
   "resources/public/js/tile-game.js"
   :target-path]
  :cljsbuild
  { :builds [{:id "tile-game"
              :source-paths ["src"]
              :figwheel true
              :compiler {:main "tile-game.grid"
                         :asset-path "js/out"
                         :output-to "resources/public/js/tile-game.js"
                         :output-dir "resources/public/js/out"
                         :source-map-timestamp true}}]}
  :figwheel { :css-dirs ["resources/public/css"]
              :open-file-command "emacsclient" })
