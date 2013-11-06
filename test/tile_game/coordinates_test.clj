(ns tile-game.coordinates-test
  (:require [tile-game.coordinates :refer :all]
            [clojure.test :refer :all]))

(def a-board (tile-game.board/create-board 3))

(deftest circumfrence-coordinates
  (is (= (circumfrence a-board [0 0] [1 1])
         '([1 0] [1 1] [0 1] [0 0])))
  (is (= (circumfrence a-board [0 0] [2 0])
         '([1 0] [2 0] [1 0] [0 0])))
  (is (= (circumfrence a-board [0 0] [0 1])
         '([0 1] [0 0])))
  (is (= (circumfrence a-board [0 0] [2 2])
         '([1 0] [2 0]
             [2 1] [2 2]
               [1 2] [0 2]
                 [0 1] [0 0]))))

(deftest closest-adjacent-coordinates
  (is (= (closest-adjacent-coordinate a-board [0 0] [0 0]) [0 0]))
  (is (= (closest-adjacent-coordinate a-board [0 0] [0 1]) [0 1]))
  (is (= (closest-adjacent-coordinate a-board [0 0] [0 2]) [0 1]))
  (is (= (closest-adjacent-coordinate a-board [0 0] [1 1]) [0 1]))
  (is (= (closest-adjacent-coordinate a-board [0 0] [1 0]) [1 0])))

(deftest paths-to
  (is (= (path-to a-board [0 0] [0 0]) '()))
  (is (= (path-to a-board [0 0] [0 1]) '([0 1])))
  (is (= (path-to a-board [0 0] [0 2]) '([0 1] [0 2]))))
