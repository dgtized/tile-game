(ns ^:figwheel-hooks tile-game.test-runner
  (:require
   [cljs-test-display.core :as td]
   [figwheel.main.testing :refer [run-tests-async run-tests]]
   [fipp.edn :as fedn]
   [goog.dom :as gdom]

   ;; require all the namespaces that you want to test
   tile-game.board-test
   tile-game.coordinates-test
   tile-game.core-test
   tile-game.solver-test
   ))

(enable-console-print!)

(defn prettier [content]
  (td/n :pre {}
        (td/n :code {} (with-out-str (fedn/pprint content {:width 90})))))

;; modified from https://github.com/bhauman/cljs-test-display/issues/5#issuecomment-619090019
;; pretty print expected vs actual with fedn/pprint
(set! td/comparison
      (fn comparison [{:keys [expected actual]}]
        (td/div
         (prettier expected)
         (td/div :actual-x (prettier actual)))))

(def style-overrides
  "pre {overflow-x: auto;}
.actual-x {border-top: 1.5px dashed rgb(236 196 196);}
")

;; to view, visit http://localhost:9400/figwheel-extra-main/tests
(defn ^:after-load test-run []
  (run-tests (cljs-test-display.core/init! "app-tests"))
  (when-not (gdom/getElement "style-override")
    (let [head (aget (gdom/getElementsByTagName "head") 0)]
      (gdom/appendChild head
                        (td/n :style {:id "style-override"}
                              style-overrides)))))

(defonce initialize-page
  (test-run))

(defn -main [& _args]
  (run-tests-async 5000))

