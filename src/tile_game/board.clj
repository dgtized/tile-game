(ns tile-game.board
  (:require (clojure.contrib seq math)))

(defn create-board [dim & opt]
  (let [board (vec (concat (range 1 (* dim dim)) '(0)))]
    (if (and opt (> dim 2))
      (shuffle board)
      board)))

(def dir-delta {:up    [ 0 -1]
                :down  [ 0  1]
                :left  [-1  0]
                :right [ 1  0]})

(defn- dimension [board]
  (int (Math/sqrt (count board))))

(defn- bounded-coords? [board [x y]]
  (let [dim (dimension board)]
    (and (>= x 0) (< x dim) (>= y 0) (< y dim))))

(defn- manhattan [p1 p2]
  (map #(clojure.contrib.math/abs (- %1 %2)) p1 p2))

(defn- distance [p1 p2]
  (apply + (manhattan p1 p2)))

(defn coords->index [board [x y]]
  (+ (* y (dimension board)) x))

(defn coords->tile [board [x y]]
  (nth board (coords->index board [x y])))

(defn tile->index [board tile]
  (first (clojure.contrib.seq/positions #(= tile %) board)))

(defn index->coords [board idx]
  (let [dim (dimension board)]
    [(rem idx dim) (quot idx dim)]))

(defn tile->coords [board tile]
  (index->coords board (tile->index board tile)))

(defn- adjacent-coords [board [x y]]
  (let [adj-loc (map (fn [[dx dy]] [(+ x dx) (+ y dy)])
                     (vals dir-delta))]
    (filter (partial bounded-coords? board) adj-loc)))

(defn- adjacent-tiles [board tile]
  (map (partial coords->tile board)
       (adjacent-coords board (tile->coords board tile))))

(defn- adjacent? [board p1 p2]
  (= 1 (distance (tile->coords board p1) (tile->coords board p2))))

(defn- legal-moves [board]
  (set (filter #(adjacent? board 0 %) board)))

(defn- coord-add [board [x y] [dx dy]]
  (let [coords [(+ x dx) (+ y dy)]]
    (if (bounded-coords? board coords)
      coords
      [x y])))

(defn slide [board arg]
  (let [tile (if (contains? dir-delta arg)
               (coords->tile board
                             (coord-add board
                                        (tile->coords board 0)
                                        (dir-delta arg)))
               arg)
        empty-pos (tile->index board 0)
        pos (tile->index board tile)]
    (if (adjacent? board 0 tile)
      (assoc board
        empty-pos tile
        pos 0)
      board)))

(defn dijkstra-map [dmap coords seen cost]
  (if (empty? coords)
    dmap
    (let [costs (flatten (map #(list (coords->index dmap %) cost) coords))
          edges (reduce clojure.set/union #{}
                        (map #(set (adjacent-coords dmap %)) coords))
          next (clojure.set/difference edges seen)]
      (recur (apply assoc dmap costs)
             next
             (clojure.set/union seen coords next)
             (+ cost 1)))))

(defn distance-map [board goal avoid]
  (dijkstra-map (vec (repeat (count board) (count board)))
                (set (list goal))
                (set avoid)
                0))

(defn path-to [board goal & [avoid]]
  (let [dist-map (distance-map board goal
                               (map (partial tile->coords board) avoid))]
    (loop [path '() board board]
      (if (= (tile->coords board 0) goal)
        (reverse path)
        (let [best-moves (sort-by #(nth dist-map (tile->index board %))
                                  (legal-moves board))
              best-move (first best-moves)]
          (recur (cons best-move path)
                 (slide board best-move)))))))

(defn solved-tiles [board]
  (let [solution (create-board (dimension board))]
    (set (keep-indexed (fn [idx tile] (if (= tile (nth solution idx)) tile nil))
                       board))))

(defn solved? [board]
  (= (count board) (count (solved-tiles board))))

(defn move-tile [board tile direction & [avoid]]
  (let [[x y] (tile->coords board tile)
        goal (coord-add board [x y] (dir-delta direction))]
    (if (= [x y] goal) '()
        (concat (path-to board goal (conj (set avoid) tile))
                (list tile)))))

;; (defn solve-tile [board tile]
;;   (if ((solved-tiles board) tile)
;;     '()
;;     (let [direction :up
;;           moves (move-tile board tile direction)]
;;       (concat moves
;;               (recur (slide board moves) tile)))))

