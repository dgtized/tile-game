(ns tile-game.grid
  (:require [reagent.core :as r]))

(enable-console-print!)

(def colors ["#000000" "#808080" "#C0C0C0" "#FFFFFF"
             "#800000" "#FF0000" "#808000" "#FFFF00"
             "#008000" "#00FF00" "#008080" "#00FFFF"
             "#000080" "#0000FF" "#800080" "#FF00FF"])

(defonce app-state
  (r/atom {:board (range 0 16)
           :dim 4}))

(defn tile-grid []
  (let [{:keys [board dim]} @app-state]
    [:center
     [:h1 "Tile Grid"]
     [:svg {:view-box "0 0 4 4" :width 800 :height 800}
      (for [x (range dim) y (range dim)]
        (let [tile (nth board (+ (* y dim) x))
              fill-color (colors tile)]
          [:g {:key (str "tile-" tile)}
           [:rect {:x x :y y :width 0.9 :height 0.9 :fill fill-color}]
           [:text {:x (+ 0.45 x) :y (+ 0.6 y)
                   :font-family "Verdana" :font-size 0.4 :text-anchor "middle"}
            (str tile)]]))]]))

(r/render-component [tile-grid] (. js/document (getElementById "grid")))
