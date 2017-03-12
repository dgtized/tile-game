(ns tile-game.grid
  (:require [monet.canvas :as cv]))

(defonce canvas-dom (.getElementById js/document "grid"))
(defonce monet-canvas (cv/init canvas-dom "2d"))

(defn render-tile [])

(def colors ["#000000" "#808080" "#C0C0C0" "#FFFFFF"
             "#800000" "#FF0000" "#808000" "#FFFF00"
             "#008000" "#00FF00" "#008080" "#00FFFF"
             "#000080" "#0000FF" "#800080" "#FF00FF"])

(def dim 4)
(def scale 200)

(doseq [tile (range 0 (dec (* dim dim)))]
  (cv/add-entity monet-canvas (symbol (str "tile-" tile))
                 (cv/entity
                  {:x (+ 10 (* (rem tile dim) (+ scale 10)))
                   :y (+ 10 (* (quot tile dim) (+ scale 10)))
                   :w scale :h scale} ; val
                  nil
                  (fn [ctx val]
                    (-> ctx
                        (cv/fill-style (colors (inc tile)))
                        (cv/fill-rect val)
                        (cv/fill-style "black")
                        (cv/text-align "center")
                        (cv/text-baseline "middle")
                        (cv/font-style "bold 48px serif")
                        (cv/text {:text (str (inc tile))
                                  :x (+ (:x val) (/ (:w val) 2))
                                  :y (+ (:y val) (* (:h val) 0.50))}))))))
