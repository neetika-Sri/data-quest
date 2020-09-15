#!/bin/bash

URL="https://download.bls.gov/pub/time.series/pr/"
BUCKET="rearcquest"
DEST_FOLDER="./resources" # Must have read write priviledge

clear
$JAVA_HOME/bin/java -jar data_quest-1.0-SNAPSHOT-jar-with-dependencies.jar $URL ${DEST_FOLDER} $BUCKET