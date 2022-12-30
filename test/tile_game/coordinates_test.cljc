(ns tile-game.coordinates-test
  (:require [clojure.test :as t :refer [deftest is] :include-macros true]
            [tile-game.coordinates :as sut]))

(deftest adjacency-list
  (is (= (sut/adjacent-to [1 1])
         '([1 0] [2 1] [1 2] [0 1])))
  (is (= (sut/adjacent-to [0 0])
         '([0 -1] [1 0] [0 1] [-1 0]))))

(deftest circumfrence-coordinates
  (is (= (sut/circumfrence [0 0] [1 1])
         '([1 0] [1 1] [0 1] [0 0])))
  (is (= (sut/circumfrence [0 0] [2 0])
         '([1 0] [2 0] [1 0] [0 0])))
  (is (= (sut/circumfrence [0 0] [0 1])
         '([0 1] [0 0])))
  (is (= (sut/circumfrence [0 0] [2 2])
         '([1 0] [2 0] [2 1] [2 2]
           [1 2] [0 2] [0 1] [0 0]))))

(deftest closest-adjacent-coordinates
  (is (= (sut/closest-adjacent [0 0] [0 0]) [0 0]))
  (is (= (sut/closest-adjacent [0 0] [0 1]) [0 1]))
  (is (= (sut/closest-adjacent [0 0] [0 2]) [0 1]))
  (is (= (sut/closest-adjacent [0 0] [1 1]) [1 0]))
  (is (= (sut/closest-adjacent [0 0] [1 0]) [1 0]))
  (is (= (sut/closest-adjacent [0 0] [2 2]) [1 0])))

(deftest paths-to
  (is (= (sut/path-to [0 0] [0 0]) '()))
  (is (= (sut/path-to [0 0] [0 1]) '([0 1])))
  (is (= (sut/path-to [0 0] [0 2]) '([0 1] [0 2]))))
