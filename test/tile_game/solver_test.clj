(ns tile-game.solver-test
  (:require [clojure.test :refer :all]
            [tile-game
             [board :as b]
             [fixtures :refer :all]
             [solver :as solve]]))

(deftest is-solved
  (is (solve/solved? (b/create-board 3)))
  (is (solve/solved? [1 2 3 0]))
  (is (not (solve/solved? [0 1 2 3])))
  (is (not (solve/solved? [1 3 2 0]))))

(deftest distance-mapper
  (is (= [0 1 2 1 9 3 2 3 4] (solve/distance-map a-board [0 0] #{0})))
  (is (= [9 9 9 9 9 9 9 9 9] (solve/distance-map a-board [0 0] #{1}))))

(deftest move-a-tile
  (is (= '(7 4 1) (solve/move-to a-board-7 0 [0 0])))
  (is (= '(6 4 1) (solve/move-to a-board-7 0 [0 0] #{7})))
  (is (= '(8 5 3 2 1) (solve/move-to a-board-7 0 [0 0] #{7 6})))
  (is (= nil (solve/move-to a-board-7 0 [0 0] #{7 6 5})))

  (is (= '(7) (solve/move-to a-board 7 [1 1])))
  (is (= '(2 3 5) (solve/move-to a-board 5 [2 0])))
  (is (= '() (solve/move-to a-board 5 [2 1])))

  (is (= nil (solve/move-to a-board 0 [0 0] #{1})))
  (is (= nil (solve/move-to a-board 5 [2 0] #{3})))

  (is (= '() (solve/move-to a-board 5 [2 1])))
  (is (= '() (solve/move-to a-board 0 [1 1])))
  (is (= '(2) (solve/move-to a-board 0 [1 0])))
  (is (= '(2 3 5) (solve/move-to a-board 5 [2 0])))

  (is (= nil (solve/move-to [1 2 3 6 5 7 0 4 14 13 9 12 15 8 10 11] 4 [3 0] #{1 2 3}))))

(deftest move-a-tile-to-location
  (are [tile coord]
      (b/tile-at-coord? (reduce b/slide a-board (solve/move-to a-board tile coord))
                        tile coord)
    5 [0 0]
    5 [1 1]
    0 [0 0]))

(deftest solved-to
  (is (= '(1 2 3 0) (solve/solved-tiles (b/create-board 2))))
  (is (= '(1 2 3 4 5 6 7 8 0) (solve/solved-tiles (b/create-board 3))))
  (is (= '(1 2 3 4) (solve/solved-tiles a-board-7)))
  (is (= '(1 2 3 4) (solve/solved-tiles a-board)))
  (is (= '(1 2) (solve/solved-tiles [1 2 14 8 6 0 13 3 12 9 11 10 5 15 7 4]))))

(deftest goal-coords
  (is (= [2 2] (solve/goal-coord a-board 0)))
  (is (= [0 0] (solve/goal-coord a-board 1))))

(deftest last-tile-on-row
  (is (solve/last-on-row? a-board (solve/goal-coord a-board 3)))
  (is (solve/last-on-row? a-board (solve/goal-coord a-board 0)))
  (is (not (solve/last-on-row? a-board (solve/goal-coord a-board 1)))))

(deftest move-a-sequence
  (is (= '(2 3 5) (solve/move-sequence a-board [5 [2 0] #{}])))
  (is (= '(2 3 5 2 3 1 4 3 1 4)
         (solve/move-sequence a-board [5 [2 0] #{}] [4 [1 0] #{5}]))))

