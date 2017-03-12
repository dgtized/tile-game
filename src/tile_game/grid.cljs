(ns tile-game.grid)

(defn get-canvas-context-from-id
  "Gets the drawing context from the id of the canvas element.
   Actual context is in a map with the canvas element and some
   other info."
  [id]
  (let [canvas (.getElementById js/document id)]
    {:canvas canvas
     :width (.-width canvas)
     :height (.-height canvas)
     :ctx (.getContext canvas "2d")}))

(def context (get-canvas-context-from-id "grid"))

(aset (:ctx context) "fillStyle" "green")
(.fillRect (:ctx context) 10 10 1000 100)
