(ns tile-game.core
  (:require [clojure.core.async :as a]
            [tile-game
             [board :as b]
             [graphics :refer [colors]]
             [solver :as solve]])
  (:import [java.awt Color Dimension Font Graphics]
           [java.awt.event KeyAdapter KeyEvent]
           [javax.swing JFrame JPanel]))

(set! *warn-on-reflection* true)

(def scale 150)

(defn render-tile [#^Graphics g board [x y]]
  (let [tile (b/coords->tile board [x y])
        color (if (zero? tile)
                Color/WHITE
                (Color/decode (nth colors tile)))
        [cx cy] [(* x scale) (* y scale)]]
    (doto g
      (.setColor color)
      (.fillRect cx cy scale scale)
      (.setColor Color/BLACK)
      (.setFont (Font. "Serif" (Font/PLAIN) 32)))
    (when (pos? tile)
      (.drawString g (format "%d" tile)
                   (int (+ cx (quot scale 2)))
                   (int (+ cy (quot scale 2)))))))

(def board (ref (b/create-board 4 :shuffle)))

(defn shuffle-board! [dim render]
  (dosync (ref-set board (b/create-board dim :shuffle)))
  (a/put! render :render))

(defn slide!
  [render & moves]
  (prn :slide! moves)
  (a/go
    (doseq [piece moves]
      (dosync (alter board b/slide piece))
      (a/>! render :render)
      (Thread/sleep 150))))

(defn start-gui []
  (let [dim (b/dimension @board)
        size (* dim scale)
        #^JFrame frame (JFrame.)
        #^JPanel panel (proxy [JPanel] []
                         (paint [g]
                           (doseq [x (range dim) y (range dim)]
                             (render-tile g @board [x y]))))
        render (a/chan)]
    (doto panel
      (.setPreferredSize (Dimension. size size))
      (.setFocusable true)
      (.addKeyListener
       (proxy [KeyAdapter] []
         (keyPressed [#^KeyEvent e]
           (condp = (.getKeyCode e)
             KeyEvent/VK_LEFT  (slide! render :left)
             KeyEvent/VK_RIGHT (slide! render :right)
             KeyEvent/VK_UP    (slide! render :up)
             KeyEvent/VK_DOWN  (slide! render :down)
             KeyEvent/VK_Q     (do (.dispose #^JFrame frame) (a/close! render))
             KeyEvent/VK_R     (shuffle-board! dim render)
             KeyEvent/VK_S     (apply slide! (cons render (solve/solve-next @board)))
             true)))))
    (doto frame (.setContentPane panel) .pack .show)
    (a/go (while (a/<! render)
            (.repaint panel)))
    render))

(defn -main [& [num]]
  (start-gui))

;(def slide! (main 5))
