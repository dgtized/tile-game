(ns tile-game.grid
  (:require [monet.canvas :as canvas]))

(defonce canvas-dom (.getElementById js/document "grid"))
(defonce monet-canvas (canvas/init canvas-dom "2d"))

(canvas/add-entity monet-canvas :background
                   (canvas/entity {:x 10 :y 10 :w 100 :h 100} ; val
                                  nil
                                  (fn [ctx val]
                                    (-> ctx (canvas/fill-style "green")
                                        (canvas/fill-rect val)
                                        (canvas/fill-style "white")
                                        (canvas/text {:text "Five" :x 50 :y 50})))))

