(ns tile-game.board
  (:require (clojure.contrib seq math)))

(def dir-delta {:up [0 -1]
                :down [0 1]
                :left [-1 0]
                :right [1 0]})

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
