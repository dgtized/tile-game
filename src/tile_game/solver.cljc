(ns tile-game.solver
  (:require [clojure.set :as set]
            [tile-game.board :as b]))

(defn dijkstra-map [dmap coords seen cost]
  (if (empty? (set/difference coords seen))
    dmap
    (let [costs (mapcat #(list (b/coords->index dmap %) cost) coords)
          edges (reduce set/union #{}
                        (map #(set (b/adjacent-coords dmap %)) coords))
          next (set/difference edges seen)]
      (recur (apply assoc dmap costs)
             next
             (set/union seen coords)
             (inc cost)))))

(defn distance-map [board goal avoid]
  (dijkstra-map (vec (repeat (count board) (count board)))
                (set (list goal))
                (set (map (partial b/tile->coords board) avoid))
                0))

(defn ranked-moves [board dist-map tile avoid]
  (if (b/impassable? board dist-map tile)
    nil
    (let [moves (set/difference (set (b/adjacent-tiles board tile)) avoid)]
      (sort-by #(nth dist-map (b/tile->index board %)) moves))))

(defn move-to [board tile goal & [avoid]]
  (let [dist-map (distance-map board goal avoid)]
    (loop [path '() board board]
      (if (b/tile-at-coord? board tile goal)
        path
        (when-let [best-move (first (ranked-moves board dist-map tile avoid))]
          (when-let [moves (if (zero? tile)
                             (list best-move)
                             (when-let [zero-path
                                        (move-to board 0
                                                 (b/tile->coords board best-move)
                                                 (conj (set avoid) tile))]
                               (concat zero-path (list tile))))]
            (recur (concat path moves)
                   (reduce b/slide-tile board moves))))))))

(defn goal-coord [board tile]
  (b/index->coords board (dec (if (zero? tile)
                                (count board)
                                tile))))

(defn last-on-row? [board [x y]]
  (= x (dec (b/dimension board))))

(defn move-sequence [board [tile goal solved] & more]
  (let [path (move-to board tile goal solved)]
    (if (empty? more)
      path
      (concat path (apply move-sequence (reduce b/slide-tile board path) more)))))

(defn solved-tiles [board]
  (let [solution (b/create-board (b/dimension board))]
    (take-while (comp not nil?)
                (map #(if (= %1 %2) %1 nil) board solution))))

(defn solve-tile [board tile]
  (let [solved (set (solved-tiles board))
        [x y :as goal] (goal-coord board tile)]
    (if (solved tile)
      '()
      (if (last-on-row? board goal)
        (let [row-min (b/coords->tile board [0 y])]
          (move-sequence board
                         [tile [x (inc y)] solved]
                         [0 [0 y] (conj (disj solved row-min) tile)]
                         [0 [x y] #{}]
                         [tile [x y] solved]
                         [0 [(dec x) y] #{tile}]
                         [row-min [0 y] #{tile}]
                         ))
        (move-to board tile goal solved)))))

(defn solved? [board]
  (= (count board) (count (solved-tiles board))))

(defn solve-next [board]
  (if (solved? board)
    []
    (let [next (inc (count (solved-tiles board)))]
      (solve-tile board next))))
