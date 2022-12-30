(ns tile-game.board-test
  (:require [clojure.test :as t :refer [deftest is are] :include-macros true]
            [tile-game.board :as b]
            [tile-game.fixtures :as f]))

(deftest coordinates
  (are [tile coord]
      (= (b/tile->coords f/a-board tile) coord)
    1 [0 0] 2 [1 0] 3 [2 0]
    4 [0 1] 0 [1 1] 5 [2 1]
    6 [0 2] 7 [1 2] 8 [2 2]))

(deftest tile-adjacency
  (is (= '(2 5 7 4) (b/adjacent-tiles f/a-board 0)))
  (is (= '(3 2 1) (b/adjacent-tiles f/a-board-2 0))))

(deftest slide-a-direction
  (are [arg result] (= (b/slide f/a-board arg) result)
    0      f/a-board
    :up    f/a-board-2
    :down  f/a-board-7
    :right f/a-board-5
    :left  f/a-board-4
    2      f/a-board-2
    5      f/a-board-5
    4      f/a-board-4
    7      f/a-board-7))

(deftest legal-moves
  (are [arg result] (= (b/legal-move? f/a-board-2 arg) result)
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
      (= (b/direction->tile f/a-board-2 direction) result)
    :up nil
    :down 2
    :right 3
    :left 1))

;; (deftest solution-for-tile
;;   (is (= (solve-tile a-board 5) '(5))))
