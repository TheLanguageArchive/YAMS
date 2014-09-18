#!/bin/sh

###
# Usage example:
#
# ./testyams.sh http://lux16.mpi.nl/ds/yams-basex-connector/rest "comic" 5
#
# Will do a search request for the term "comic" 5 times and output the average


BASE=$1 #YAMS BaseX connector service base, e.g. http://lux16.mpi.nl/ds/yams-basex-connector/rest
TERM=$2 #SEARCH TERM
N=$3 #number of times to download, e.g. 5
echo Retrieval of \'${TERM}\':
./averagetime.sh ${BASE}/hints/YAMS-DB?type=\&path=\&text=${TERM}\&max=20 $N
