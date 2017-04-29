(ns tile-game.board-test
  (:use tile-game.board
        clojure.test))

(deftest is-solved
  (is (solved? (create-board 3)))
  (is (solved? [1 2 3 0]))
  (is (not (solved? [0 1 2 3])))
  (is (not (solved? [1 3 2 0]))))

(def a-board [1 2 3
              4 0 5
              6 7 8])
(def a-board-2 [1 0 3
                4 2 5
                6 7 8])
(def a-board-4 [1 2 3
                0 4 5
                6 7 8])
(def a-board-5 [1 2 3
                4 5 0
                6 7 8])
(def a-board-7 [1 2 3
                4 7 5
                6 0 8])

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
    7      a-board-7))

(deftest legal-directions
  (are [arg result] (= (legal-direction? a-board-2 arg) result)
    :up false
    :down true
    :right true
    :left true))

(deftest distance-mapper
  (is (= [0 1 2 1 9 3 2 3 4] (distance-map a-board [0 0] #{0})))
  (is (= [9 9 9 9 9 9 9 9 9] (distance-map a-board [0 0] #{1}))))

(deftest move-a-tile
  (is (= '(7 4 1) (move-to a-board-7 0 [0 0])))
  (is (= '(6 4 1) (move-to a-board-7 0 [0 0] #{7})))
  (is (= '(8 5 3 2 1) (move-to a-board-7 0 [0 0] #{7 6})))
  (is (= nil (move-to a-board-7 0 [0 0] #{7 6 5})))

  (is (= '(7) (move-to a-board 7 [1 1])))
  (is (= '(2 3 5) (move-to a-board 5 [2 0])))
  (is (= '() (move-to a-board 5 [2 1])))

  (is (= nil (move-to a-board 0 [0 0] #{1})))
  (is (= nil (move-to a-board 5 [2 0] #{3})))

  (is (= '() (move-to a-board 5 [2 1])))
  (is (= '() (move-to a-board 0 [1 1])))
  (is (= '(2) (move-to a-board 0 [1 0])))
  (is (= '(2 3 5) (move-to a-board 5 [2 0])))

  (is (= nil (move-to [1 2 3 6 5 7 0 4 14 13 9 12 15 8 10 11] 4 [3 0] #{1 2 3}))))

(deftest move-a-tile-to-location
  (are [tile coord]
       (tile-at-coord? (reduce slide a-board (move-to a-board tile coord))
                       tile coord)
       5 [0 0]
       5 [1 1]
       0 [0 0]))

(deftest solved-to
  (is (= '(1 2 3 0) (solved-tiles (create-board 2))))
  (is (= '(1 2 3 4 5 6 7 8 0) (solved-tiles (create-board 3))))
  (is (= '(1 2 3 4) (solved-tiles a-board-7)))
  (is (= '(1 2 3 4) (solved-tiles a-board)))
  (is (= '(1 2) (solved-tiles [1 2 14 8 6 0 13 3 12 9 11 10 5 15 7 4]))))

(deftest goal-coords
  (is (= [2 2] (goal-coord a-board 0)))
  (is (= [0 0] (goal-coord a-board 1))))

(deftest last-tile-on-row
  (is (last-on-row? a-board (goal-coord a-board 3)))
  (is (last-on-row? a-board (goal-coord a-board 0)))
  (is (not (last-on-row? a-board (goal-coord a-board 1)))))

(deftest move-a-sequence
  (is (= '(2 3 5) (move-sequence a-board [5 [2 0] #{}])))
  (is (= '(2 3 5 2 3 1 4 3 1 4)
         (move-sequence a-board [5 [2 0] #{}] [4 [1 0] #{5}]))))

;; (deftest solution-for-tile
;;   (is (= (solve-tile a-board 5) '(5))))
