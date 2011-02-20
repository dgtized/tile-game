(ns tile-game.core
  (:require (clojure.contrib seq math))
  (:import (javax.swing JFrame JPanel )
           (java.awt Color Graphics Graphics2D Dimension Font)
           (java.awt.event KeyAdapter KeyEvent)))

(set! *warn-on-reflection* true)

(def scale 150)
(def dir-delta {:up [0 -1]
                :down [0 1]
                :left [-1 0]
                :right [1 0]})

(def colors [Color/RED Color/ORANGE Color/YELLOW Color/GREEN
             Color/BLUE Color/CYAN Color/GRAY Color/PINK])

(defn dimension [board]
  (int (Math/sqrt (count board))))

(defn bounded-coords? [board [x y]]
  (let [dim (dimension board)]
    (and (>= x 0) (< x dim) (>= y 0) (< y dim))))

(defn manhattan [p1 p2]
  (map #(clojure.contrib.math/abs (- %1 %2)) p1 p2))

(defn distance [p1 p2]
  (apply + (manhattan p1 p2)))

(defn location [board [x y]]
  (nth board (+ (* y (dimension board)) x)))

(defn position [board piece]
  (first (clojure.contrib.seq/positions #(= piece %) board)))

(defn coords [board piece]
  (let [dim (dimension board)
        pos (position board piece)]
    [(rem pos dim) (quot pos dim)]))

(defn adjacent-coords [board [x y]]
  (let [adj-loc (map (fn [[dx dy]] [(+ x dx) (+ y dy)])
                     (vals dir-delta))]
    (filter (partial bounded-coords? board) adj-loc)))

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

(defn move [board piece]
  (let [empty-pos (position board 0)
        pos (position board piece)]
    (if (can-move? board piece)
      (assoc board
        empty-pos piece
        pos 0)
      board)))

(defn move-direction [board direction]
  (let [[x y] (coords board 0)
        [dx dy] (dir-delta direction)
        coords [(+ x dx) (+ y dy)]]
    (if (bounded-coords? board coords)
      (location board coords)
      0)))

(defn slide [board arg]
  (move board (if (contains? dir-delta arg)
                (move-direction board arg)
                arg)))

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

(def *board* (ref (vec '(0 1 2 3))))

(defn main [dim]
  (let [size (* dim scale)
        #^JFrame frame (JFrame.)
        #^JPanel panel (proxy [JPanel] []
                         (paint [g]
                                (dorun (for [x (range dim) y (range dim)]
                                         (render-tile g @*board* [x y])))))
        move! (fn ([piece]
                    (dosync (alter *board* move piece))
                    (.repaint panel)))
        mover! (fn [& moves]
                (send (agent moves)
                      (fn [moves]
                        (doseq [m moves]
                          (move! m)
                          (Thread/sleep 300)))))]
    (dosync (ref-set *board* (vec (concat (range 1 (* dim dim)) '(0))))
            (when (> dim 2)
              (alter *board* shuffle)))
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
                           KeyEvent/VK_S     (apply mover! (path-to @*board* [0 0]))
                           true))))))
    (doto frame (.setContentPane panel) .pack .show)
    mover!))

(comment
  (def move! (main 3)))
