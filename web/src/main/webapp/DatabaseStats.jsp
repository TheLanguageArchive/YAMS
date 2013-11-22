<!--

    Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

-->
<%@page import="java.net.MalformedURLException"%>
<%@page import="nl.mpi.flap.kinnate.entityindexer.QueryException"%>
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
                drawOverviewVisualization();
                drawDetailedVisualization();
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

            function drawDetailedVisualization() {
            <%
                String errorMessage2 = "";
                if (request.getParameter("databaseName") != null) {
                    String jsonDataDetailed = "[[0,0,0,0]]";
                    String queryStringDetailed = " ('[[0,0,0,0,0,0]',\n" //[''timestamp'', ''linkcount'', ''documentcount'', ''queryms'']',\n"
                            + "let $dbName := '" + request.getParameter("databaseName") + "'\n"
                            + "for $crawlerStats in collection($dbName)/CrawlerStats\n"
                            // this order by seems to cause problems which might depend on which basex version is being used: + "order by $crawlerStats/@timestamp/string()\n"
                            + "let $dateTime := if (empty($crawlerStats/@timestamp)) then \n"
                            + " '20130000000000'\n"
                            + "else\n"
                            + " $crawlerStats/@timestamp/string()\n"
                            + "let $maxMemory := if (empty($crawlerStats/@maxMemory)) then \n"
                            + " '0'\n"
                            + "else\n"
                            + " string(($crawlerStats/@maxMemory) div 1048576.0)\n"
                            + "let $jsDateTime := string-join(('new Date(', substring($dateTime, 1, 4), ',', substring($dateTime, 5, 2), ',', substring($dateTime, 7, 2), ',', substring($dateTime, 9, 2), ',', substring($dateTime, 11, 2), ',', substring($dateTime, 13, 2),')'),'')\n"
                            + "let $linkcount := $crawlerStats/@linkcount/string()\n"
                            + "let $documentcount := $crawlerStats/@documentcount/string()\n"
                            + "let $querytime := string($crawlerStats/@queryms)\n"
                            + "let $freebytes := string(($crawlerStats/@freebytes) div 1048576.0)\n"
                            + "let $totalbytes := string(($crawlerStats/@totalbytes) div 1048576.0)\n"
                            + "return (',[',string-join(($jsDateTime,$linkcount,$documentcount,$querytime,$freebytes,$totalbytes,$maxMemory),','),']'),']')\n";
                    try {
                        RestDbAdaptor restDbAdaptor = new RestDbAdaptor(new URL(config.getServletContext().getInitParameter("basexRestUrl")), config.getServletContext().getInitParameter("basexUser"), config.getServletContext().getInitParameter("basexPass"));
                        jsonDataDetailed = restDbAdaptor.executeQuery(DataBaseManager.defaultDataBase, queryStringDetailed);
                    } catch (MalformedURLException exception2) {
                        errorMessage2 += exception2.getMessage();
                    } catch (QueryException exception2) {
                        errorMessage2 += exception2.getMessage();
                        errorMessage2 += "<pre>" + queryStringDetailed.replaceAll("\n", "<br>") + "</pre>";
                    }
            %>
//                var data = google.visualization.arrayToDataTable(<%=jsonDataDetailed%>);
                var data = new google.visualization.DataTable();
                data.addColumn('date', 'timestamp');
                data.addColumn('number', 'linkcount');
                data.addColumn('number', 'documentcount');
                data.addColumn('number', 'query ms');
                data.addColumn('number', 'mb free');
                data.addColumn('number', 'mb total');
                data.addColumn('number', 'mb available');
                data.addRows(<%=jsonDataDetailed%>.slice(1));
                var options = {
                    title: 'Crawing Stats for "<%=request.getParameter("databaseName")%>"'
                };

                var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
                chart.draw(data, options);
            <% } %>
            }

            function drawOverviewVisualization() {
            <%
                String errorMessage1 = "";
                String jsonData = "[[0][0]]";;
                String queryString = " ('[[''DB'', ''Date Crawled'', ''Time Taken (hours.minutes)'', ''Time Per Document'', ''Document Count'']',\n"
                        + "for $dbName in db:list()\n"
                        //                        + " where $dbName != '" + DataBaseManager.defaultDataBase + "'\n"
                        + "let $minDate := if (empty(collection($dbName)/CrawlerStats/@timestamp)) then \n"
                        + " '20130000000000'\n"
                        + "else\n"
                        + " min(collection($dbName)/CrawlerStats/@timestamp/string())\n"
                        + "let $maxDate := if(empty(collection($dbName)/CrawlerStats/@timestamp)) then \n"
                        + " '20130000000000'\n"
                        + "else\n"
                        + " max(collection($dbName)/CrawlerStats/@timestamp/string())\n"
                        + "let $jsDateMin := string-join(('new Date(', substring($minDate, 1, 4), ',', substring($minDate, 5, 2), ',', substring($minDate, 7, 2), ',', substring($minDate, 9, 2), ',', substring($minDate, 11, 2), ',', substring($minDate, 13, 2),')'),'')\n"//',',substring($minDate, 15, 2),
                        + "let $jsDateMax := string-join(('new Date(', substring($maxDate, 1, 4), ',', substring($maxDate, 5, 2), ',', substring($maxDate, 7, 2), ',', substring($maxDate, 9, 2), ',', substring($maxDate, 11, 2), ',', substring($maxDate, 13, 2),')'),'')\n"//',',substring($maxDate, 15, 2),
                        //                        + "let $facetDocumentCount := count(collection($dbName)/Facets)\n"
                        + "let $documentCount := count(collection($dbName))\n"
                        + "let $timePerDoc := if (empty(collection($dbName)/CrawlerStats/@timestamp)) then\n"
                        + "0\n"
                        + "else \n"
                        + " string-join(('(',$jsDateMax,'-',$jsDateMin,')','/',string($documentCount)),'')\n"
                        + " return (',[',string-join((\n"
                        + "string-join(('''',$dbName,''''),''),\n"
                        + "$jsDateMin,\n"
                        // this hours minutes value will not be quite correct on the graph, which expects a fraction
                        + "string-join(("
                        + "'(Math.floor(',$jsDateMax,'-',$jsDateMin,' / 3600) % 24)'"
                        + ",'+((Math.floor(',$jsDateMax,'-',$jsDateMin,' / 60) % 60)/100)'"
                        + "),''),\n"
                        //                        + "'0',\n"                        
                        //                        + "string-join(('''',$dbName,''''),''),\n"
                        //                        + "$minDate,\n"
                        //                        + "$maxDate,\n"
                        //                        + "string-join(('''',$minDate,'-',$maxDate,''''),''),\n"
                        + "string($timePerDoc),\n"
                        + "string($documentCount)\n"
                        + "),','),\n"
                        + "']\n'),']')\n";
                try {
                    RestDbAdaptor restDbAdaptor = new RestDbAdaptor(new URL(config.getServletContext().getInitParameter("basexRestUrl")), config.getServletContext().getInitParameter("basexUser"), config.getServletContext().getInitParameter("basexPass"));
                    jsonData = restDbAdaptor.executeQuery(DataBaseManager.defaultDataBase, queryString);
                } catch (MalformedURLException exception2) {
                    errorMessage1 += exception2.getMessage();
                } catch (QueryException exception2) {
                    errorMessage1 += exception2.getMessage();
                    errorMessage2 += "<pre>" + queryString.replaceAll("\n", "<br>") + "</pre>";
                }
            %>
//                var data = google.visualization.arrayToDataTable(<%=jsonData%>);
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'DB');
                data.addColumn('date', 'Date Crawled');
                data.addColumn('number', 'Time Taken (hours.minutes)');
                data.addColumn('number', 'Time Per Document');
                data.addColumn('number', 'Document Count');
                data.addRows(<%=jsonData%>.slice(1));

                var options = {
                    title: 'Crawl Statistics For All Test Databases (click below for details)',
                    hAxis: {title: 'Date Crawled'},
                    vAxis: {title: 'Time Taken (hours.minutes)'},
                    bubble: {textStyle: {fontSize: 11}}
                };

                // Create and draw the visualization.
                var chart2 = new google.visualization.BubbleChart(document.getElementById('visualization'));
                google.visualization.events.addListener(chart2, 'select', selectHandler);

                function selectHandler(e) {
                    var selectedItem = chart2.getSelection()[0];
                    var value = data.getValue(selectedItem.row, 0);
                    location.href = "?databaseName=" + value;
                }
                chart2.draw(data, options);
                document.getElementById("error_div1").innerHTML = "<%=errorMessage1%>";
                document.getElementById("error_div2").innerHTML = "<%=errorMessage2%>";
            }
        </script> 
    </head>
    <body>
        REST URL: <%=config.getServletContext().getInitParameter("basexRestUrl")%><br>
        <div id="error_div1"></div>
        <div id="error_div2"></div>
        <div id="visualization" style="width: 900px; height: 500px;"></div>   
        <% String buttonText;
            if (request.getParameter("databaseName") != null) {
                buttonText = "Search in the selected database '" + request.getParameter("databaseName") + "'";
            } else {
                buttonText = "Go To Search Page";
            }
        %>
        <a href='yaas.html?databaseName=<%=request.getParameter("databaseName")%>'><%=buttonText%></a>
        <div id="chart_div" style="width: 900px; height: 500px;"></div>
    </body>
</html>
