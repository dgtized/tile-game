(ns tile-game.coordinates-test
  (:require [tile-game.coordinates :refer :all]
            [clojure.test :refer :all]))

(deftest adjacency-list
  (is (= (adjacent-to [1 1]) '([1 0] [2 1] [1 2] [0 1]))))

(deftest circumfrence-coordinates
  (is (= (circumfrence [0 0] [1 1])
         '([1 0] [1 1] [0 1] [0 0])))
  (is (= (circumfrence [0 0] [2 0])
         '([1 0] [2 0] [1 0] [0 0])))
  (is (= (circumfrence [0 0] [0 1])
         '([0 1] [0 0])))
  (is (= (circumfrence [0 0] [2 2])
         '([1 0] [2 0]
             [2 1] [2 2]
               [1 2] [0 2]
                 [0 1] [0 0]))))

(deftest closest-adjacent-coordinates
  (is (= (closest-adjacent [0 0] [0 0]) [0 0]))
  (is (= (closest-adjacent [0 0] [0 1]) [0 1]))
  (is (= (closest-adjacent [0 0] [0 2]) [0 1]))
  (is (= (closest-adjacent [0 0] [1 1]) [0 1]))
  (is (= (closest-adjacent [0 0] [1 0]) [1 0])))

(deftest paths-to
  (is (= (path-to [0 0] [0 0]) '()))
  (is (= (path-to [0 0] [0 1]) '([0 1])))
  (is (= (path-to [0 0] [0 2]) '([0 1] [0 2]))))
