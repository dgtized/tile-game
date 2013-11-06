(ns tile-game.coordinates
  (require [tile-game.board :refer [distance adjacent-coords]]))

(defn closest-adjacent-coordinate
  "Given two points p1, p2 in the form [x y], return coordinate
  adjacent to p1 that is closest to p2"
  [board p1 p2]
  (if (= p1 p2)
    p2
    (apply min-key
           (partial distance p2)
           (adjacent-coords board p1))))

(defn path-to [board p1 p2]
  (take (distance p1 p2)
        (drop 1 (iterate #(closest-adjacent-coordinate board % p2) p1))))

(defn circumfrence [board p1 p2]
  (let [xs    (map first [p1 p2])
        ys    (map second [p1 p2])
        min-x (apply min xs)
        max-x (apply max xs)
        min-y (apply min ys)
        max-y (apply max ys)]
    (concat (path-to board [min-x min-y] [max-x min-y])
            (path-to board [max-x min-y] [max-x max-y])
            (path-to board [max-x max-y] [min-x max-y])
            (path-to board [min-x max-y] [min-x min-y]))))

