#!/bin/bash -e

lein clean
lein cljsbuild once release

rm -rf resources/public/.git
pushd resources/public
git init
git add .
git commit -m "Deploy to GitHub Pages"
git push --force --quiet "git@github.com:dgtized/tile-game.git" master:gh-pages
popd
