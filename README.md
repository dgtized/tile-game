# Tile Puzzle Game and Solver

A Platform and Solver for ordering NxN tiles into the correct order

In 2001, I took a Scheme based class where one of the final projects
was to write a program to visualize and solve a 4x4 tile game, aka the
15 tile puzzle.  I wrote up a solution for the class, but the AI had
no planning ability.  It was a very heuristic heavy solution, and I
always wanted to improve on it.  Recently I decided to learn Clojure
and thought this would be a nice project to dig my teeth into.

## Usage

lein run [dim]

### Commands

 - *Arrow keys* move the empty tile.
 - *R* randomizes the tiles for the current dimension
 - *S* will execute solve-next which is a heuristic based solver which
  does not actually complete.
 - *Q* will quit

## Note/Disclaimer

The solver is unfinished, I got caught up in other projects before
completing it. It will solve a portion of the problem with a
heuristic, but will not search for a general solution. This was also
my first experience with the language, so it's definitely not
idiomatic Clojure.

## License

Copyright (C) 2011-2017 Charles L.G. Comstock

Distributed under the MIT Public License, see LICENSE file

