<%-- 
    Document   : getState
    Created on : May 8, 2014, 10:29:57 AM
    Author     : Peter Withers <peter.withers@mpi.nl>
--%>
<%@page contentType="application/json" pageEncoding="UTF-8"%>
<%
    response.setContentType("application/json");
    response.setHeader("Content-Disposition", "inline");
%>
{
"contextPath": "<%= request.getContextPath()%>",
"getRemoteUser": "<%= request.getRemoteUser()%>",
"getAttribute": "<%= session.getAttribute("userid")%>"
}