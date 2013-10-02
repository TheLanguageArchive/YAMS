<!--
/*
 * Copyright (C) 2013 Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
-->
<%@page import="java.net.MalformedURLException"%>
<%@page import="nl.mpi.flap.kinnate.entityindexer.QueryException;"%>
<%@page import="nl.mpi.yaas.common.db.DataBaseManager"%>
<%@page import="nl.mpi.yaas.common.db.RestDbAdaptor"%>
<%@page import="java.net.URL"%>
<!DOCTYPE html>
<html>
    <head>
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
        <script type="text/javascript">
            google.load("visualization", "1", {packages: ["corechart"]});
            google.setOnLoadCallback(drawChart);
            function drawChart() {
                drawVisualization1();
                drawVisualization2();
            }

            function handleQueryResponse(response) {
                if (response.isError()) {
                    alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
                    return;
                }

                var data = response.getDataTable();
                visualization = new google.visualization.IntensityMap(document.getElementById('visualization'));
                visualization.draw(data, null);
            }


            function drawVisualization1() {
                var data = google.visualization.arrayToDataTable([
                    ['Date Time', 'Records', 'Query Time'],
                    ['2004', 1000, 400],
                    ['2005', 1170, 460],
                    ['2006', 660, 1120],
                    ['2007', 1030, 540]
                ]);

                var options = {
                    title: 'Database Crawing Stats'
                };

                var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
                chart.draw(data, options);
            }

            function drawVisualization2() {
            <%
                String jsonData;
                String queryString = " ('[[\"DB\", \"Date Crawled\", \"Time Taken\", \"Time Per Document\", \"Document Count\"]',\n"
                        + "for $dbName in db:list()\n"
                        + "let $maxDate := max(collection($dbName)/CrawlerStats/@timestamp/string())\n"
                        + "let $minDate := min(collection($dbName)/CrawlerStats/@timestamp/string())\n"
                        + "let $jsDateMin := string-join(('new Date(', substring($minDate, 1, 4), ',', substring($minDate, 5, 2), ',', substring($minDate, 7, 2), ',', substring($minDate, 9, 2), ',', substring($minDate, 11, 2), ',', substring($minDate, 13, 2),')'),'')\n"//',',substring($minDate, 15, 2),
                        + "let $jsDateMax := string-join(('new Date(', substring($maxDate, 1, 4), ',', substring($maxDate, 5, 2), ',', substring($maxDate, 7, 2), ',', substring($maxDate, 9, 2), ',', substring($maxDate, 11, 2), ',', substring($maxDate, 13, 2),')'),'')\n"//',',substring($maxDate, 15, 2),
                        //                        + "let $facetDocumentCount := count(collection($dbName)/Facets)\n"
                        + "let $documentCount := count(collection($dbName))\n"
                        + "let $timePerDoc := string-join(('(',$jsDateMax,'-',$jsDateMin,')','/',string($documentCount)),'')\n"
                        + " return (',[',string-join((\n"
                        + "string-join(('\"',$dbName,'\"'),''),\n"
                        + "$minDate,\n"
                        + "string-join(($jsDateMax,'-',$jsDateMin),''),\n"
                        //                        + "'0',\n"                        
                        //                        + "string-join(('\"',$dbName,'\"'),''),\n"
                        //                        + "$minDate,\n"
                        //                        + "$maxDate,\n"
                        //                        + "string-join(('\"',$minDate,'-',$maxDate,'\"'),''),\n"
                        + "string($timePerDoc),\n"
                        + "string($documentCount)\n"
                        + "),','),\n"
                        + "']\n'),']')\n";
                try {
                    //            final String basexRestUrl = getInitParameter("basexRestUrl");
                    final String basexRestUrl = "http://tlatest03:8984/rest/";
                    RestDbAdaptor restDbAdaptor = new RestDbAdaptor(new URL(basexRestUrl), DataBaseManager.guestUser, DataBaseManager.guestUserPass);
                    jsonData = restDbAdaptor.executeQuery(DataBaseManager.defaultDataBase, queryString);
                } catch (MalformedURLException exception2) {
                    jsonData = "[[Error Getting Data][" + exception2.getMessage() + "]]";
                } catch (QueryException exception2) {
                    jsonData = "[[Error Getting Data][" + exception2.getMessage() + "][" + queryString + "]]";
                }
            %>
                var data = google.visualization.arrayToDataTable(<%=jsonData%>);

                var options = {
                    title: 'Crawl Statistics For All Test Databases',
                    hAxis: {title: 'Date Crawled'},
                    vAxis: {title: 'Time Taken'},
                    bubble: {textStyle: {fontSize: 11}}
                };

                // Create and draw the visualization.
                var chart2 = new google.visualization.BubbleChart(document.getElementById('visualization'));
                chart2.draw(data, options);
            }
        </script>
    </head>
    <body>
        <div id="chart_div" style="width: 900px; height: 500px;"></div>
        <div id="visualization" style="width: 900px; height: 500px;"></div>
    </body>
</html>
