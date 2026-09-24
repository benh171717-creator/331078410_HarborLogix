#!/bin/bash
case "$1" in
  test)  java -cp out harborlogix.tests.AcceptanceTest ;;
  app)   java -cp out harborlogix.app.TerminalApp ;;
  check) java -cp out harborlogix.tools.OpenClosedCheck src/harborlogix/ops/Yard.java ;;
  *)     echo "usage: ./run.sh [test|app|check]" ;;
esac
