(ns tile-game.test.core
  (:use [tile-game.core] :reload)
  (:use [clojure.test]))

(deftest is-solved
  (is (solved? '(1 2 3 0)))
  (is (not (solved? '(0 1 2 3))))
  (is (not (solved? '(1 3 2 0)))))

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
       (= (coords a-board tile) coord)
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
