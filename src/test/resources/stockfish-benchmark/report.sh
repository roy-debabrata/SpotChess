#!/bin/bash

function s_print() {
    echo $* | awk '{ printf "%-15s %-10s %-10s %-10s %-10s\n", $1, $2, $3, $4, $5}'
}

function stockfish_result() {
    time yes "position fen $1
bench 0 1 $2 current perft
quit"  | stockfish
}

function spot_result() {
    $(cd /mnt/d/Projects/SpotChessWin/SpotChess/build/classes/java/main; time java com.debabrata.spotchess.Spot "$1" "$2" "$3")
}

function to_millis() {
    TEMP=$(echo $1 | cut -dm -f1)
    RES=$((TEMP * 60))
    TEMP=$(echo $1 | cut -dm -f2 | cut -d. -f1)
    RES=$(($((RES + TEMP)) * 1000))
    TEMP=$(echo $1 | cut -dm -f2 | cut -d. -f2 | cut -ds -f1 | sed 's/^0*//g')
    echo $((RES + TEMP))
}

function find_diff() {
    SP_SEC=$(to_millis $1)
    SF_SEC=$(to_millis $2)
    echo $((SP_SEC - SF_SEC))
}

function find_percent() {
    DIFF=$1
    SF_SEC=$(to_millis $2)
    awk 'BEGIN{printf("%0.2f", 0 - ('$DIFF' / '$SF_SEC' * 100))}'
}

function timer() {
    SF_RES=$(stockfish_result "$2" "$3" 2>&1 | grep user | cut -f2)
    SP_RES=$(spot_result "$2" "$3" "$4" 2>&1 | grep user | cut -f2)
    DIFF=$(find_diff $SP_RES $SF_RES)
    PERC=$(find_percent $DIFF $SF_RES)
    s_print "$1" $SP_RES $SF_RES "${DIFF}ms" "$PERC%"
}

function run_timer() {
    s_print "#" "Spot" "Stockfish" "Difference" "Percent "
    while read LINE;do
        if ! echo $LINE | grep -q "^#";then
            POS=$(echo $LINE | cut -d# -f1)
            FEN=$(echo $LINE | cut -d# -f2)
            DEP=$(echo $LINE | cut -d# -f3)
            RES=$(echo $LINE | cut -d# -f4)
            timer "$POS" "$FEN" "$DEP" "$RES"
        fi
    done < positions.list
}

run_timer