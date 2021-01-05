#!/bin/bash -e

function cleanup() {
    rm -rf resources/public/{.git,js} target
}

cleanup

clojure -m figwheel.main --build-once release

mkdir -p resources/public/js
cp target/public/js/tile-game.* resources/public/js

pushd resources/public
git init
git add .
git commit -m "Deploy to GitHub Pages"
git push --force --quiet "git@github.com:dgtized/tile-game.git" master:gh-pages
popd

cleanup
