(ns tile-game.test.core
  (:use [tile-game.core] :reload)
  (:use [clojure.test]))

(deftest is-solved
  (is (solved? '(1 2 3 0)))
  (is (not (solved? '(0 1 2 3))))
  (is (not (solved? '(1 3 2 0)))))
