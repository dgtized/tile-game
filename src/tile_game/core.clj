(ns tile-game.core
  (:use [tile-game board])
  (:import [javax.swing JFrame JPanel]
           [java.awt Color Graphics Graphics2D Dimension Font]
           [java.awt.event KeyAdapter KeyEvent]))

(set! *warn-on-reflection* true)

(def scale 150)

(def colors [Color/RED Color/ORANGE Color/YELLOW Color/GREEN
             Color/BLUE Color/CYAN Color/GRAY Color/PINK])

(defn render-tile [#^Graphics g board [x y]]
  (let [tile (coords->tile board [x y])
        color (if (= tile 0)
                Color/WHITE
                (colors (rem tile (count colors))))
        [cx cy] [(* x scale) (* y scale)]]
       (doto g
         (.setColor color)
         (.fillRect cx cy scale scale)
         (.setColor Color/BLACK)
         (.setFont (Font. "Serif" (. Font PLAIN) 32)))
       (when (> tile 0) (.drawString g (format "%d" tile)
                                     (int (+ cx (quot scale 2)))
                                     (int (+ cy (quot scale 2)))))))

(def board (ref (create-board 2)))

(defn start-gui [dim]
  (let [size (* dim scale)
        #^JFrame frame (JFrame.)
        #^JPanel panel (proxy [JPanel] []
                         (paint [g]
                           (doseq [x (range dim) y (range dim)]
                             (render-tile g @board [x y]))))
        slide! (fn [& moves]
                 (prn :slide! moves)
                 (send (agent moves)
                       (fn [moves]
                         (doseq [piece moves]
                           (dosync (alter board slide piece))
                           (.repaint panel)
                           (Thread/sleep 150)))))
        shuffle-board! (fn [dim] (dosync (ref-set board
                                                  (create-board dim :shuffle))))]
    (shuffle-board! dim)
    (doto panel
      (.setPreferredSize (Dimension. size size))
      (.setFocusable true)
      (.addKeyListener
       (proxy [KeyAdapter] []
         (keyPressed [#^KeyEvent e]
                     (condp = (.getKeyCode e)
                         KeyEvent/VK_LEFT  (slide! :left)
                         KeyEvent/VK_RIGHT (slide! :right)
                         KeyEvent/VK_UP    (slide! :up)
                         KeyEvent/VK_DOWN  (slide! :down)
                         KeyEvent/VK_Q     (.dispose #^JFrame frame)
                         KeyEvent/VK_R     (do (shuffle-board! dim) (.repaint panel))
                         KeyEvent/VK_S     (apply slide! (solve-next @board))
                         true)))))
    (doto frame (.setContentPane panel) .pack .show)
    slide!))

(defn -main [& [num]]
  (if num (start-gui (Integer/parseInt num))
      (start-gui 4))
  nil)

;(def slide! (main 5))

