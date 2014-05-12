<%--

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

--%>
<%-- 
    Document   : login
    Created on : May 9, 2014, 1:26:43 PM
    Author     : Peter Withers <peter.withers@mpi.nl>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Shibboleth Login</title>
        <script type="text/javascript">
            //<![CDATA[
            parent.checkLoginState();
            //]]>
        </script>
    </head>
    <body>
        <h1>You are logged in as "<%= request.getRemoteUser()%>".</h1>
    </body>
</html>
