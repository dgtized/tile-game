(ns tile-game.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [tile-game.board :as board]
            [tile-game.coordinates :as coords]))

(s/def ::direction board/direction?)
(s/def ::coord-delta #{-1 0 1})
(s/def ::delta (s/tuple ::coord-delta ::coord-delta))
(s/def ::cardinal-delta (s/and ::delta (fn [[x y]] (= 1 (coords/abs (+ x y))))))
(s/def ::coordinate (s/and int? pos?))
(s/def ::point (s/tuple ::coordinate ::coordinate))
(comment
  (gen/sample (s/gen ::direction))
  (gen/sample (s/gen ::point))
  (gen/sample (s/gen ::cardinal-delta)))


