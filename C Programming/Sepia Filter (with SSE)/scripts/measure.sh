#!/bin/bash
prog="./image-transformer"
elapsed_regex="\[TRNSF_ELAPSED\]: ([0-9]+)"
rm -f ./fast.log ./slow.log
for i in {0..50}
do
    ${prog} -f ./input.bmp ./output.bmp 2> ./err.log
    log=$(cat err.log)
    if [[ $log =~ $elapsed_regex ]]
    then
        elapsed="${BASH_REMATCH[1]}"
        echo "${elapsed}" >> ./slow.log
    fi
    ${prog} -fF ./input.bmp ./output.bmp 2> ./err.log
    log=$(cat err.log)
      if [[ $log =~ $elapsed_regex ]]
      then
        elapsed="${BASH_REMATCH[1]}"
        echo "${elapsed}" >> ./fast.log
    fi
done
