(ns tile-game.board
  (:require [tile-game.coordinates :as coords]
            [clojure.set :as set]
            [clojure.string :as s]))

(defn create-board [dim & opt]
  (let [board (vec (concat (range 1 (* dim dim)) '(0)))]
    (if (and opt (> dim 2))
      (shuffle board)
      board)))

(def dir-delta {:up    [ 0 -1]
                :down  [ 0  1]
                :left  [-1  0]
                :right [ 1  0]})

(defn dimension [board]
  (int (Math/sqrt (count board))))

;; (defn print-board [board]
;;   (let [dim (dimension board)
;;         fmt (s/join " " (repeat dim "%3d"))]
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

(defn- adjacent-tiles [board tile]
  (map (partial coords->tile board)
       (adjacent-coords board (tile->coords board tile))))

(defn slide-tile [board tile]
  (let [empty-pos (tile->index board 0)
        pos (tile->index board tile)]
    (if (some #{tile} (adjacent-tiles board 0))
      (assoc board
        empty-pos tile
        pos 0)
      board)))

(defn slide-direction [board direction]
  (let [current-pos (tile->coords board 0)
        new-pos (coords/add (dir-delta direction) current-pos)]
    (if (bounded-coords? board new-pos)
      (slide-tile board (coords->tile board new-pos))
      board)))

(defn slide [board arg]
  (if (contains? dir-delta arg)
    (slide-direction board arg)
    (slide-tile board arg)))

(defn dijkstra-map [dmap coords seen cost]
  (if (empty? (set/difference coords seen))
    dmap
    (let [costs (mapcat #(list (coords->index dmap %) cost) coords)
          edges (reduce set/union #{}
                        (map #(set (adjacent-coords dmap %)) coords))
          next (set/difference edges seen)]
      (recur (apply assoc dmap costs)
             next
             (set/union seen coords)
             (+ cost 1)))))

(defn distance-map [board goal avoid]
  (dijkstra-map (vec (repeat (count board) (count board)))
                (set (list goal))
                (set (map (partial tile->coords board) avoid))
                0))

(defn impassable? [board dist-map tile]
  (> (nth dist-map (tile->index board tile)) (* 2 (dimension board))))

(defn tile-at-coord? [board tile coord]
  (= (tile->coords board tile) coord))

(defn ranked-moves [board dist-map tile avoid]
  (if (impassable? board dist-map tile)
    nil
    (let [moves (set/difference (set (adjacent-tiles board tile)) avoid)]
      (sort-by #(nth dist-map (tile->index board %)) moves))))

(defn move-to [board tile goal & [avoid]]
  (let [dist-map (distance-map board goal avoid)]
    (loop [path '() board board]
      (if (tile-at-coord? board tile goal)
        path
        (when-let [best-move (first (ranked-moves board dist-map tile avoid))]
          (when-let [moves (if (= tile 0)
                             (list best-move)
                             (when-let [zero-path
                                        (move-to board 0
                                                 (tile->coords board best-move)
                                                 (conj (set avoid) tile))]
                               (concat zero-path (list tile))))]
            (recur (concat path moves)
                   (reduce slide-tile board moves))))))))

(defn solved-tiles [board]
  (let [solution (create-board (dimension board))]
    (take-while #(not (nil? %))
                (map #(if (= %1 %2) %1 nil) board solution))))

(defn solved? [board]
  (= (count board) (count (solved-tiles board))))

(defn goal-coord [board tile]
  (index->coords board (- (if (= tile 0)
                            (count board)
                            tile) 1)))

(defn last-on-row? [board [x y]]
  (= x (- (dimension board) 1)))

(defn move-sequence [board [tile goal solved] & more]
  (let [path (move-to board tile goal solved)]
    (if (empty? more)
      path
      (concat path (apply move-sequence (reduce slide-tile board path) more)))))

(defn solve-tile [board tile]
  (let [solved (set (solved-tiles board))
        [x y :as goal] (goal-coord board tile)]
    (if (solved tile)
      '()
      (if (last-on-row? board goal)
        (let [row-min (coords->tile board [0 y])]
          (move-sequence board
                         [tile [x (+ y 1)] solved]
                         [0 [0 y] (conj (disj solved row-min) tile)]
                         [0 [x y] #{}]
                         [tile [x y] solved]
                         [0 [(- x 1) y] #{tile}]
                         [row-min [0 y] #{tile}]
                         ))
        (move-to board tile goal solved)))))

(defn solve-next [board]
  (if (solved? board)
    []
    (let [next (+ 1 (count (solved-tiles board)))]
      (solve-tile board next))))
