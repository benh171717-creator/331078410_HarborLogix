#!/bin/bash
# HarborLogix build script (macOS / Linux)
set -e
rm -rf out && mkdir -p out
javac -d out $(find src -name "*.java")
echo "Build OK."
echo
echo "  ./run.sh test      - acceptance test"
echo "  ./run.sh app       - your demo program"
echo "  ./run.sh check     - open/closed check on Yard.java"
