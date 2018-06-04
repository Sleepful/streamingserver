#!/bin/bash

trap "trap - SIGTERM && kill -- -$$" SIGINT SIGTERM EXIT

find video -type f -print0 | xargs -0 ./mp4 > stream.log & ./server > server.log & tail -f server.log
