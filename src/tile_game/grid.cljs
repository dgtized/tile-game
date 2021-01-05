(ns tile-game.grid
  (:require [cljs.core.async :as async]
            [reagent.core :as r]
            [tile-game.board :as b]
            [tile-game.graphics :refer [colors]]
            [tile-game.solver :as solve])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(enable-console-print!)

(defonce app-state
  (r/atom {:board (b/create-board 4 :shuffle)
           :moves []
           :size 4
           :analysis-mode false}))

(defn new-board! [& args]
  (swap! app-state assoc
         :board (apply b/create-board (:size @app-state) args)
         :moves []))

(defn slide! [arg]
  (let [{:keys [board moves]} @app-state
        tile (if (b/direction? arg)
               (b/direction->tile board arg)
               arg)]
    (when (b/legal-move? board arg)
      (swap! app-state assoc
             :board (b/slide board tile)
             :moves (conj moves tile)))))

(defn board-size-slider [size command]
  [:div
   [:div
    [:label (str "Board size: " size)]
    [:input {:type "range" :value size :min 2 :max 8
             :on-change (fn [e] (swap! app-state assoc :size (.-target.value e)))
             :style {:width "25%"}}]]
   [:div
    [:button {:on-click #(async/put! command :reset)
              :title "Generate a new solved board at selected size."}
     "Reset"]
    [:button {:on-click #(async/put! command :new-game)
              :title "Generate a new shuffled board at the selected size."}
     "Shuffle"]]])

(defn render-tile [[x y] tile command]
  (if-not (zero? tile)
    (let [fill-color (nth colors tile)]
      [:g {:key (str "tile-" tile) :on-click #(async/put! command tile)}
       [:rect {:x (+ x 0.05) :y (+ y 0.05) :width 0.9 :height 0.9 :fill fill-color}]
       [:text {:x (+ 0.5 x) :y (+ 0.65 y)
               :font-family "Verdana" :font-size 0.4 :text-anchor "middle"}
        (str tile)]])))

(defn render-board [board dim command]
  [:svg {:view-box (str "0 0 " dim " " dim) :width 800 :height 800}
   (for [x (range dim) y (range dim)]
     (let [tile (b/coords->tile board [x y])]
       (render-tile [x y] tile command)))])

(defn help-screen []
  [:table {:style {:width "25%"}}
   [:thead
    [:tr [:th "Key"] [:th "Action"]]]
   [:tbody
    [:tr [:td "Left"] [:td "Move blank tile left"]]
    [:tr [:td "Right"] [:td "Move blank tile right"]]
    [:tr [:td "Up"] [:td "Move blank tile up"]]
    [:tr [:td "Down"] [:td "Move blank tile down"]]
    [:tr [:td "N"] [:td "Shuffle Board"]]
    [:tr [:td "R"] [:td "Reset Board"]]
    [:tr [:td "S"] [:td "Solve next piece"]]]])

(defn tile-grid [command]
  (let [{:keys [board size analysis-mode moves]} @app-state
        dim (b/dimension board)]
    [:center
     [:h1 "Tile Puzzle"]
     (render-board board dim command)
     [:h4 (if (solve/solved? board) "Solved!" "Slide tiles with arrow keys or clicking on tile")]
     [:details [:summary (str (count moves) " moves.")] (str moves)]
     (board-size-slider size command)
     [:div
      [:label "Analysis Mode"]
      [:input {:type "checkbox" :checked analysis-mode
               :on-click #(swap! app-state update-in [:analysis-mode] not)}]
      [:br]
      (when analysis-mode
        (if (solve/solved? board)
          "Solved!"
          [:div
           (str "Suggested Moves: " (solve/solve-next board))
           [:div [:button {:on-click #(async/put! command :solve)} "Run solver!"]]]))]
     [:details [:summary "Help"] (help-screen)]
     [:p
      "© 2017-2018 Charles L.G. Comstock "
      [:a {:href "https://github.com/dgtized/tile-game"} "(github)"]]]))

(defn playback-moves
  "Plays recorded moves to board with a delay and ability to cancel playback"
  [moves cancel delay]
  (go-loop [[move & remaining] moves]
    (if move
      (let [[_ c] (async/alts! [cancel (async/timeout delay)])]
        (when (not= c cancel)
          (slide! move)
          (recur remaining)))
      (async/close! cancel)))
  cancel)

(defn ui-event-loop [command]
  (go-loop [cancel (async/chan 1)]
    (when-let [key (async/<! command)]
      (async/close! cancel)
      (let [new-cancel (async/chan 1)]
        (condp = key
          :new-game (new-board! :shuffle)
          :reset (new-board!)
          :solve (playback-moves (solve/solve-next (:board @app-state))
                                 new-cancel 250)
          (slide! key))
        (recur new-cancel)))
    (recur cancel)))

(def codename
  {37 :left
   39 :right
   38 :up
   40 :down
   78 :new-game
   82 :reset
   83 :solve})

(defn handle-keydown [command]
  (fn [e]
    (when-let [key (codename (.-keyCode e))]
      (.preventDefault e)
      (async/put! command key))))

(defn init []
  (let [command (async/chan)]
    ;; Rebind onkeydown with set! so figwheel can always update
    (set! (.-onkeydown js/window) (handle-keydown command))
    (ui-event-loop command)
    (r/render-component [(fn [] (tile-grid command))]
                        (.getElementById js/document "grid"))))

(init)
