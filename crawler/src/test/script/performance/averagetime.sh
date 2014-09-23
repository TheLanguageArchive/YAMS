#!/bin/bash
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

# adapted from http://cacodaemon.de/index.php?id=11
URL="$1"
MAX_RUNS="$2"
SUM_TIME="0"

i="0"
while [ $i -lt $MAX_RUNS ]; do
  TIME=`curl $URL -o /dev/null -s -w %{time_total}`  
  TIME="${TIME/,/.}"
  echo ${i}: $TIME
  SUM_TIME=`echo "scale=5; $TIME + $SUM_TIME" | bc`
  i=$[$i+1]
done

TIME_AVERAGE=`echo "scale=5; $SUM_TIME / $MAX_RUNS" | bc`
echo "Sum: $SUM_TIME"
echo "Avg: $TIME_AVERAGE"
