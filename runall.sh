#!/bin/bash

# Ajeite essa variável como feito no Makefile
ANTLR_PATH=antlr

DATA=./src/test/resources
IN=$DATA/semanticExamples
ERRORIN=$DATA/semanticExamples/withErrors
OUT=$DATA/semanticExamples/out
ERROROUT=$OUT/withErrors

rm -rf $OUT
mkdir $OUT
mvn compile
for infile in `ls $IN/*.go`; do
    base=$(basename $infile)
    outfile=$OUT/${base/.go/.out}
    dotfile=$OUT/${base/.go/.dot}
    pngfile=$OUT/${base/.go/.png}
    echo Running $base
    mvn exec:java -Dexec.args="$infile"  1> $outfile 2> $dotfile
    dot -Tpng $dotfile -o $pngfile
done

echo; echo ---Running with errors---; echo

mkdir $ERROROUT
for infile in `ls $ERRORIN/*.go`; do
    base=$(basename $infile)
    outfile=$ERROROUT/${base/.go/.out}
    dotfile=$ERROROUT/${base/.go/.dot}
    pngfile=$OUT/${base/.go/.png}
    echo Running $base
    mvn exec:java -Dexec.args="$infile"  1> $outfile 2> $dotfile
    dot -Tpng $dotfile -o $pngfile
done