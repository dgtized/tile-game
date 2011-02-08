(ns tile-game.core
  (:require (clojure.contrib seq math))
  (:import (javax.swing JFrame JPanel )
           (java.awt Color Graphics Graphics2D Dimension Font)))

(def dim 3)
(def scale 150)

(def colors [Color/RED Color/ORANGE Color/YELLOW Color/GREEN
             Color/BLUE Color/CYAN Color/GRAY Color/PINK])

(defn bounded-coords? [[x y]]
  (and (>= x 0) (< x dim) (>= y 0) (< y dim)))

(defn location [board [x y]]
  (nth board (+ (* x dim) y)))

(defn position [board piece]
  (.indexOf board piece))

(defn coords [board piece]
  (let [pos (position board piece)]
    [(quot pos dim) (rem pos dim)]))

(defn move [board piece]
  (let [empty-pos (position board 0)
        pos (position board piece)]
      (assoc board
        empty-pos piece
        pos 0)))

(def dir-delta {:up [0 1] :down [0 -1]
                :left [-1 0] :right [1 0]})

(defn adjacent-coords [board [x y]]
  (let [adj-loc (map (fn [[dx dy]] [(+ x dx) (+ y dy)])
                     (vals dir-delta))]
    (filter bounded-coords? adj-loc)))

(defn adjacent-pieces [board piece]
  (map (partial location board)
       (adjacent-coords board (coords board piece))))

(defn adjacent? [board p1 p2]
  (let [[dx dy] (map #(Math/abs (- %1 %2))
                     (coords board p1)
                     (coords board p2))]
    (= 1 (+ dx dy))))

(defn can-move? [board piece]
  (adjacent? board 0 piece))

(defn legal-moves [board]
  (filter #(can-move? board %) board))

(defn solved? [board]
  (not-any? false? (keep-indexed #(= %1 %2) board)))

(defn solver [best-move history board]
  (if (solved? board)
    nil
    (lazy-seq (let [best (best-move history
                                    board
                                    (legal-moves board))]
                (cons best (solver best-move
                                   (cons best history)
                                   (move board best)))))))

(defn random-walk [board piece history depth]
  (take depth (solver (fn [h _ ms]
                        (rand-nth (remove #(= (first h) %) ms)))
                      history
                      (move board piece))))

(defn best-carlo [history board moves]
  (first (sort-by (fn [m]
                    (count (random-walk board m history 100)))
                  (remove #(= (first history) %) moves))))

(def solve (partial solver best-carlo '()))

(defn run-solution [solution]
  (doseq [next solution]
    (render-move next)))

;; (defn path-to
;;   [board piece location]
;;   (let [empty-pos (position board 0)
;;         pos (position board piece)]
;;     (if (= empty-pos pos)
;;       '()
;;       (path-to board location)))
;;   [board location]
;;   (cons ))

(defn render [#^Graphics2D g]
  (dorun
   (for [x (range dim) y (range dim)]
     (let [tile (location @*board* [x y])
           color (if (= tile 0)
             Color/WHITE
             (colors (rem tile (count colors))))
           [cx cy] [(* x scale) (* y scale)]]
       (doto g
         (.setColor color)
         (.fillRect cx cy scale scale)
         (.setColor (if (= Color/BLACK color) Color/WHITE Color/BLACK))
         (.setFont (Font. "Serif" (. Font PLAIN) 24))
         (.drawString (.toString tile)
                      (+ cx (quot scale 2))
                      (+ cy (quot scale 2))
                      ))))))

(def *board* (ref (shuffle (vec (range 0 (* dim dim))))))

(def panel
  (let [size (* dim scale)]
    (doto (proxy [JPanel] []
            (paint [g] (render g)))
      (.setPreferredSize (Dimension. size size)))))

(defn render-move [piece]
  (dosync (when (can-move? @*board* piece)
            (alter *board* move piece)))
  (. panel (repaint)))

(def frame (doto (new JFrame) (.add panel) .pack .show))


