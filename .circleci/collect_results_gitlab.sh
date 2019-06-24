#!/usr/bin/env bash

# Save all important reports and artifacts into (project-root)/results
# This folder will be saved by gitlab and available after test runs.

# set -e

TEST_RESULTS_DIR=./results
mkdir -p $TEST_RESULTS_DIR >/dev/null 2>&1

echo "saving test results into $TEST_RESULTS_DIR/results"
mkdir -p $TEST_RESULTS_DIR/results
find $CI_PROJECT_DIR/**/build/test-results -name \*.xml -exec cp {} $TEST_RESULTS_DIR/results \; || true
echo find $CI_PROJECT_DIR/**/build/test-results -name \*.xml || true
