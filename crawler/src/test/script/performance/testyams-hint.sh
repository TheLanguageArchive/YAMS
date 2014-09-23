#!/bin/sh
#
# Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
#


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
