(ns tile-game.core
  (:require (clojure.contrib seq math))
  (:import (javax.swing JFrame JPanel )
           (java.awt Color Graphics Graphics2D Dimension Font)
           (java.awt.event KeyAdapter KeyEvent)))

(set! *warn-on-reflection* true)

(def dim 5)
(def scale 150)

(def colors [Color/RED Color/ORANGE Color/YELLOW Color/GREEN
             Color/BLUE Color/CYAN Color/GRAY Color/PINK])

(defn bounded-coords? [[x y]]
  (and (>= x 0) (< x dim) (>= y 0) (< y dim)))

(defn manhattan [p1 p2]
  (map #(clojure.contrib.math/abs (- %1 %2)) p1 p2))

(defn distance [p1 p2]
  (apply + (manhattan p1 p2)))

(defn location [board [x y]]
  (nth board (+ (* x dim) y)))

(defn position [board piece]
  (first (clojure.contrib.seq/positions #(= piece %) board)))

(defn coords [board piece]
  (let [pos (position board piece)]
    [(quot pos dim) (rem pos dim)]))

(defn move [board piece]
  (let [empty-pos (position board 0)
        pos (position board piece)]
      (assoc board
        empty-pos piece
        pos 0)))

(def dir-delta {:up [0 -1] :down [0 1]
                :left [-1 0] :right [1 0]})

(defn move-direction [board direction]
  (let [[x y] (coords board 0)
        [dx dy] (dir-delta direction)
        coords [(+ x dx) (+ y dy)]]
    (if (bounded-coords? coords)
      (location board coords)
      0)))

(defn adjacent-coords [board [x y]]
  (let [adj-loc (map (fn [[dx dy]] [(+ x dx) (+ y dy)])
                     (vals dir-delta))]
    (filter bounded-coords? adj-loc)))

(defn adjacent-pieces [board piece]
  (map (partial location board)
       (adjacent-coords board (coords board piece))))

(defn adjacent? [board p1 p2]
  (= 1 (distance (coords board p1) (coords board p2))))

(defn can-move? [board piece]
  (adjacent? board 0 piece))

(defn legal-moves [board]
  (filter #(can-move? board %) board))

(defn solved? [board]
  (= (concat (range 1 (count board)) '(0)) board))

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

;; (defn path-to
;;   [board piece [goal-x goal-y :as goal]]
;;   (let [[empty-x empty-y :as empty] (coords board 0)
;;         [x y :as current] (coords board piece)]
;;     (if (= current goal)
;;       '()
;;       (cons (path-to board ))))
;;   [board goal]
;;   ())

(defn path-to [board goal]
  (let [current (coords board 0)]
    (if (= current goal)
      ()
      (let [next (first (sort-by #(distance goal (coords board %))
                                 (legal-moves board)))]
        (cons next (path-to (move board next) goal))))))

(defn render-tile [#^Graphics g board [x y]]
  (let [tile (location board [x y])
        color (if (= tile 0)
                Color/WHITE
                (colors (rem tile (count colors))))
        [cx cy] [(* x scale) (* y scale)]]
       (doto g
         (.setColor color)
         (.fillRect cx cy scale scale)
         (.setColor Color/BLACK)
         (.setFont (Font. "Serif" (. Font PLAIN) 32)))
       (when (> tile 0) (.drawString g (format "%d" tile)
                                     (int (+ cx (quot scale 2)))
                                     (int (+ cy (quot scale 2)))))))

(def *board* (ref (shuffle (vec (range (* dim dim))))))

(defn main []
  (let [size (* dim scale)
        #^JFrame frame (JFrame.)
        #^JPanel panel (proxy [JPanel] []
                         (paint [g]
                                (dorun (for [x (range dim) y (range dim)]
                                         (render-tile g @*board* [x y])))))
        move! (fn
                ([piece]
                   (dosync (when (can-move? @*board* piece)
                             (alter *board* move piece)))
                   (.repaint panel))
                ([piece & moves]
                   (prn piece moves)
                   (move! piece)
                   (send (agent moves) #(do
                                         (Thread/sleep 500)
                                         (apply move! %1)))))]
    (doto panel
      (.setPreferredSize (Dimension. size size))
      (.setFocusable true)
      (.addKeyListener
       (proxy [KeyAdapter] []
         (keyPressed [#^KeyEvent e]
                     (let [dir-move (comp move! (partial move-direction @*board*))]
                       (condp = (.getKeyCode e)
                           KeyEvent/VK_LEFT  (dir-move :left)
                           KeyEvent/VK_RIGHT (dir-move :right)
                           KeyEvent/VK_UP    (dir-move :up)
                           KeyEvent/VK_DOWN  (dir-move :down)
                           KeyEvent/VK_Q     (.dispose #^JFrame frame)
                           KeyEvent/VK_S     (apply move! (path-to @*board* [0 0]))
                           true))))))
    (doto frame (.setContentPane panel) .pack .show)
    move!))

(comment
  (def move! (main)))
