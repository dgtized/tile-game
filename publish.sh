#!/bin/bash -e

lein clean
lein cljsbuild once release

pushd resources/public
git init
git add .
git commit -m "Deploy to GitHub Pages"
git push --force --quiet "git@github.com:dgtized/tile-game.git" master:gh-pages
popd
rm -rf resources/public/.git
