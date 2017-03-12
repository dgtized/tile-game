(ns tile-game.grid
  (:require [reagent.core :as r]
            [tile-game.board :as b]))

(enable-console-print!)

(def colors ["#000000" "#808080" "#C0C0C0" "#FFFFFF"
             "#800000" "#FF0000" "#808000" "#FFFF00"
             "#008000" "#00FF00" "#008080" "#00FFFF"
             "#000080" "#0000FF" "#800080" "#FF00FF"])

(defonce app-state
  (r/atom {:board (b/create-board 4)
           :dim 4}))

(defn render-tile [[x y] tile]
  (if-not (zero? tile)
    (let [fill-color (colors tile)]
      [:g {:key (str "tile-" tile)
           :on-click #(swap! app-state update-in [:board] b/slide-tile tile)}
       [:rect {:x (+ x 0.05) :y (+ y 0.05) :width 0.9 :height 0.9 :fill fill-color}]
       [:text {:x (+ 0.5 x) :y (+ 0.65 y)
               :font-family "Verdana" :font-size 0.4 :text-anchor "middle"}
        (str tile)]])))

(defn tile-grid []
  (let [{:keys [board dim]} @app-state]
    [:center
     [:h1 "Tile Grid"]
     [:svg {:view-box "0 0 4 4" :width 800 :height 800}
      (for [x (range dim) y (range dim)]
        (let [tile (nth board (+ (* y dim) x))]
          (render-tile [x y] tile)))]]))

(r/render-component [tile-grid] (. js/document (getElementById "grid")))
