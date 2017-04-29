(ns tile-game.board
  (:require [tile-game.coordinates :as coords]))

(defn create-board [dim & opt]
  (let [board (vec (concat (range 1 (* dim dim)) '(0)))]
    (if (and opt (> dim 2))
      (shuffle board)
      board)))

(def dir-delta {:up    [ 0 -1]
                :down  [ 0  1]
                :left  [-1  0]
                :right [ 1  0]})
(def direction? (set (keys dir-delta)))

(defn dimension [board]
  (int (Math/sqrt (count board))))

;; (defn print-board [board]
;;   (let [dim (dimension board)
;;         fmt (string/join " " (repeat dim "%3d"))]
;;     (doseq [row (partition dim board)]
;;       (apply printf (cons (str fmt "\n") row)))))

(defn- bounded-coords? [board [x y]]
  (let [dim (dimension board)]
    (and (>= x 0) (< x dim) (>= y 0) (< y dim))))

(defn coords->index [board [x y]]
  (+ (* y (dimension board)) x))

(defn coords->tile [board [x y]]
  (nth board (coords->index board [x y])))

(defn tile->index [board tile]
  (first (keep-indexed (fn [i x] (when (= x tile) i))
                       board)))

(defn index->coords [board idx]
  (let [dim (dimension board)]
    [(rem idx dim) (quot idx dim)]))

(defn tile->coords [board tile]
  (index->coords board (tile->index board tile)))

(defn adjacent-coords [board p]
  (filter (partial bounded-coords? board) (coords/adjacent-to p)))

(defn adjacent-tiles [board tile]
  (map (partial coords->tile board)
       (adjacent-coords board (tile->coords board tile))))

(defn direction->coords [board arg]
  (coords/add (tile->coords board 0) (dir-delta arg)))

(defn direction->tile [board direction]
  (let [pos (direction->coords board direction)]
    (when (bounded-coords? board pos)
      (coords->tile board pos))))

(defn legal-move? [board arg]
  (cond
    (direction? arg) (bounded-coords? board (direction->coords board arg))
    :else (boolean (some #{arg} (adjacent-tiles board 0)))))

(defn slide-tile [board tile]
  (let [empty-pos (tile->index board 0)
        pos (tile->index board tile)]
    (if (some #{tile} (adjacent-tiles board 0))
      (assoc board
             empty-pos tile
             pos 0)
      board)))

(defn slide-direction [board direction]
  (if-let [tile (direction->tile board direction)]
    (slide-tile board tile)
    board))

(defn slide [board arg]
  (if (direction? arg)
    (slide-direction board arg)
    (slide-tile board arg)))

(defn impassable? [board dist-map tile]
  (> (nth dist-map (tile->index board tile)) (* 2 (dimension board))))

(defn tile-at-coord? [board tile coord]
  (= (tile->coords board tile) coord))
