(ns tile-game.board-test
  (:require [clojure.test :refer :all]
            [tile-game
             [board :as b]
             [fixtures :refer :all]]))

(deftest coordinates
  (are [tile coord]
      (= (b/tile->coords a-board tile) coord)
    1 [0 0] 2 [1 0] 3 [2 0]
    4 [0 1] 0 [1 1] 5 [2 1]
    6 [0 2] 7 [1 2] 8 [2 2]))

(deftest tile-adjacency
  (is (= '(2 5 7 4) (b/adjacent-tiles a-board 0)))
  (is (= '(3 2 1) (b/adjacent-tiles a-board-2 0))))

(deftest slide-a-direction
  (are [arg result] (= (b/slide a-board arg) result)
    0      a-board
    :up    a-board-2
    :down  a-board-7
    :right a-board-5
    :left  a-board-4
    2      a-board-2
    5      a-board-5
    4      a-board-4
    7      a-board-7))

(deftest legal-moves
  (are [arg result] (= (b/legal-move? a-board-2 arg) result)
    :up false
    :down true
    :right true
    :left true
    0 false
    1 true
    2 true
    3 true
    4 false))

(deftest tile-direction
  (are [direction result]
      (= (b/direction->tile a-board-2 direction) result)
    :up nil
    :down 2
    :right 3
    :left 1))

;; (deftest solution-for-tile
;;   (is (= (solve-tile a-board 5) '(5))))
