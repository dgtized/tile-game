(ns tile-game.coordinates
  (require [clojure.math.numeric-tower :as math]))

(defn manhattan [p1 p2]
  (map #(math/abs (- %1 %2)) p1 p2))

(defn distance [p1 p2]
  (apply + (manhattan p1 p2)))

(defn adjacent-to
  "Lists adjacent coordinates in clockwise order"
  [[x y]]
  (map (fn [[dx dy]] [(+ x dx) (+ y dy)])
       [[0 -1] [1 0] [0 1] [-1 0]]))

(defn closest-adjacent
  "Given two points p1, p2 in the form [x y], return coordinate
  adjacent to p1 that is closest to p2"
  [p1 p2]
  (if (= p1 p2)
    p2
    (apply min-key
           (partial distance p2)
           (adjacent-to p1))))

(defn path-to
  "Shortest path from p1 to p2"
  [p1 p2]
  (take (distance p1 p2)
        (drop 1 (iterate #(closest-adjacent % p2) p1))))

(defn circumfrence
  "Lists coordinates on the circumfrence of largest rectangle
  described by p1, p2 in clockwise order"
  [p1 p2]
  (let [xs    (map first [p1 p2])
        ys    (map second [p1 p2])
        min-x (apply min xs)
        max-x (apply max xs)
        min-y (apply min ys)
        max-y (apply max ys)]
    (concat (path-to [min-x min-y] [max-x min-y])
            (path-to [max-x min-y] [max-x max-y])
            (path-to [max-x max-y] [min-x max-y])
            (path-to [min-x max-y] [min-x min-y]))))

