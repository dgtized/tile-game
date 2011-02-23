(ns tile-game.test.board
  (:use [tile-game.board] :reload)
  (:use [clojure.test]))

(deftest is-solved
  (is (solved? (create-board 3)))
  (is (solved? (vec '(1 2 3 0))))
  (is (not (solved? (vec '(0 1 2 3)))))
  (is (not (solved? (vec '(1 3 2 0))))))

(def a-board (vec '(1 2 3
                    4 0 5
                    6 7 8)))
(def a-board-2 (vec '(1 0 3
                      4 2 5
                      6 7 8)))
(def a-board-4 (vec '(1 2 3
                      0 4 5
                      6 7 8)))
(def a-board-5 (vec '(1 2 3
                      4 5 0
                      6 7 8)))
(def a-board-7 (vec '(1 2 3
                      4 7 5
                      6 0 8)))

(deftest coordinates
  (are [tile coord]
       (= (tile->coords a-board tile) coord)
       1 [0 0] 2 [1 0] 3 [2 0]
       4 [0 1] 0 [1 1] 5 [2 1]
       6 [0 2] 7 [1 2] 8 [2 2]))

(deftest slide-a-direction
  (are [arg result] (= (slide a-board arg) result)
       0      a-board
       :up    a-board-2
       :down  a-board-7
       :right a-board-5
       :left  a-board-4
       2      a-board-2
       5      a-board-5
       4      a-board-4
       7      a-board-7
       ))

(deftest solved-to
  (is (= (set '(1 2 3 0)) (solved-tiles (create-board 2))))
  (is (= (set '(1 2 3 4)) (solved-tiles a-board-7)))
  (is (= (set '(1 2 3 4)) (solved-tiles a-board))))

(deftest distance-mapper
  (is (= (distance-map a-board [0 0] #{0}) [0 1 2 1 9 3 2 3 4]))
  (is (= (distance-map a-board [0 0] #{1}) [9 9 9 9 9 9 9 9 9])))

(deftest calculate-path
  (is (= (move-to a-board-7 0 [0 0])        '(6 4 1)))
  (is (= (move-to a-board-7 0 [0 0] #{7})   '(6 4 1)))
  (is (= (move-to a-board-7 0 [0 0] #{7 6}) '(8 5 3 2 1)))
  (is (= (move-to a-board-7 0 [0 0] #{7 6 5}) '())))

(deftest move-a-tile-once
  (is (= (move-to a-board 7 [1 1]) '(7)))
  (is (= (move-to a-board 5 [2 0]) '(2 3 5)))
  (is (= (move-to a-board 5 [2 1]) '())))

(deftest move-a-tile-multi
  (is (= (move-to a-board 5 [2 1]) '()))
  (is (= (move-to a-board 0 [1 1]) '()))
  (is (= (move-to a-board 0 [1 0]) '(2)))
  (is (= (move-to a-board 5 [2 0]) '(2 3 5))))

(deftest move-a-tile-to-location
  (are [tile coord]
       (tile-at-coord? (reduce slide a-board (move-to a-board tile coord))
                       tile coord)
       5 [0 0]
       5 [1 1]
       0 [0 0]))

(deftest goal-coords
  (is (= (goal-coord a-board 0) [2 2]))
  (is (= (goal-coord a-board 1) [0 0])))

(deftest last-tile-on-row
  (is (last-on-row? a-board (goal-coord a-board 3)))
  (is (last-on-row? a-board (goal-coord a-board 0)))
  (is (not (last-on-row? a-board (goal-coord a-board 1)))))

;; (deftest solution-for-tile
;;   (is (= (solve-tile a-board 5) '(5))))
