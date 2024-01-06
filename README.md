# Tile Puzzle Game and Solver

[![CircleCI](https://circleci.com/gh/dgtized/tile-game.svg?style=svg)](https://circleci.com/gh/dgtized/tile-game)

A Platform and Solver for ordering NxN tiles into the correct order

https://dgtized.github.io/tile-game

In 2001, I took a Scheme based class where one of the final projects
was to write a program to visualize and solve a 4x4 tile game, aka the
15 tile puzzle.  I wrote up a solution for the class, but the AI had
no planning ability.  It was a very heuristic heavy solution, and I
always wanted to improve on it.  Recently I decided to learn Clojure
and thought this would be a nice project to dig my teeth into.

### Commands

 - *Arrow keys* move the empty tile.
 - *R* randomizes the tiles for the current dimension
 - *S* will execute solve-next which is a heuristic based solver which
  does not actually complete.
 - *Q* will quit

## Update March 2017

Ported it to cross-compile to clojurescript for the browser. Experimenting with
using core.async instead of agents for playing back a solution asynchronously.
This allows
for
[canceling in-process asynchronous](http://blog.lauripesonen.com/go-concurrency-patterns-in-core-async-pipelines-and-cancellation/) actions
by blocking on read from a cancel or timeout channel before processing each
event.

```clojure
(go-loop [[move & remaining] moves]
  (if move
    (do
      (let [[_ c] (async/alts! [cancel (async/timeout delay)])]
        (when (not= c cancel)
          (slide! move)
          (recur remaining))))
    (async/close! cancel)))
```

In the main event loop any new events first close the cancel channel
causing the go-loop above to exit.

## Local Usage

For java client:

    clojure -m tile-game.core

For web client

    clojure -m figwheel.main -b dev -r

visit http://localhost:9500

## Note/Disclaimer

The solver is unfinished, I got caught up in other projects before
completing it. It will solve a portion of the problem with a
heuristic, but will not search for a general solution. This was also
my first experience with the language, so it's definitely not
idiomatic Clojure.

## License

Copyright (C) 2011-2024 Charles L.G. Comstock

Distributed under the MIT Public License, see LICENSE file

