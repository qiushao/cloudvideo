#!/bin/sh

taskid=$1
echo "taskid = $taskid"
sed -i 's/^'$taskid'>\(.*\)>0/'$taskid'>\1>1/p' tasklist

