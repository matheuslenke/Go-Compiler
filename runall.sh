#!/bin/bash

# Ajeite essa variável como feito no Makefile
ANTLR_PATH=/usr/local/lib/antlr/antlr-4.9.3-complete.jar
CLASS_PATH_OPTION=-cp .:$ANTLR_PATH

DATA=$ROOT/src/test/resources
IN=$DATA/examples
OUT=out


rm -rf $OUT
mkdir $OUT
for infile in `ls $IN/*.go`; do
    base=$(basename $infile)
    outfile=$OUT/${base/.go/.out}
    dotfile=$OUT/${base/.go/.dot}
    pdffile=$OUT/${base/.go/.pdf}
    echo Running $base
    java $CLASS_PATH_OPTION:target GoCompiler $infile 1> $outfile 2> $dotfile
    dot -Tpdf $dotfile -o $pdffile
done
