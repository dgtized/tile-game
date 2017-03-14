(ns tile-game.grid
  (:require [reagent.core :as r]
            [tile-game.board :as b]))

(enable-console-print!)

(def colors
  (cycle ["#00B000" "#808080" "#C0C0C0" "#3030B0"
          "#800000" "#FF0000" "#808000" "#FFFF00"
          "#008000" "#00FF00" "#008080" "#00FFFF"
          "#0000C0" "#3030FF" "#800080" "#FF00FF"]))

(defonce app-state
  (r/atom {:board (b/create-board 4 :shuffle)
           :size 4
           :analysis-mode false}))

(defn new-board! [& args]
  #(swap! app-state assoc :board (apply b/create-board args)))

(defn slide! [arg]
  (swap! app-state update-in [:board] b/slide arg))

(defn board-size-slider [size]
  [:div
   [:button {:on-click (new-board! size)} "Reset"]
   [:button {:on-click (new-board! size :shuffle)} "Shuffle"]
   [:label (str "Board size: " size)]
   [:input {:type "range" :style {:width "25%"}
            :value size :min 2 :max 8
            :on-change (fn [e] (swap! app-state assoc :size (.-target.value e)))}]])

(defn render-tile [[x y] tile]
  (if-not (zero? tile)
    (let [fill-color (nth colors tile)]
      [:g {:key (str "tile-" tile) :on-click #(slide! tile)}
       [:rect {:x (+ x 0.05) :y (+ y 0.05) :width 0.9 :height 0.9 :fill fill-color}]
       [:text {:x (+ 0.5 x) :y (+ 0.65 y)
               :font-family "Verdana" :font-size 0.4 :text-anchor "middle"}
        (str tile)]])))

(defn tile-grid []
  (let [{:keys [board size analysis-mode]} @app-state
        dim (b/dimension board)]
    [:center
     [:h1 "Tile Puzzle"]
     [:svg {:view-box (str "0 0 " dim " " dim) :width 800 :height 800}
      (for [x (range dim) y (range dim)]
        (let [tile (nth board (+ (* y dim) x))]
          (render-tile [x y] tile)))]
     [:h4 (if (b/solved? board) "Solved!" "Slide tiles with arrow keys or clicking on tile")]
     (board-size-slider size)
     [:div
      [:label "Analysis Mode"]
      [:input {:type "checkbox" :checked analysis-mode
               :on-click #(swap! app-state update-in [:analysis-mode] not)}]
      [:br]
      (when analysis-mode
        (if (b/solved? board)
          "Solved!"
          (str "Suggested Moves: " (b/solve-next board))))]
     [:p
      "© 2017 Charles L.G. Comstock "
      [:a {:href "https://github.com/dgtized/tile-game"} "(github)"]]]))

(def codename
  {37 :left
   39 :right
   38 :up
   40 :down})

(defn handle-keydown [e]
  (when-let [direction (codename (.-keyCode e))]
    (.preventDefault e)
    (slide! direction)))

(defn init []
  ;; Rebind onkeydown with set! so figwheel can always update
  (set! (.-onkeydown js/window) handle-keydown)
  (r/render-component [tile-grid] (. js/document (getElementById "grid"))))

(init)
