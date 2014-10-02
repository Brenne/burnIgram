<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.kb.burnigram.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
    </head>
    <body>
       <jsp:include page="include/header.jsp" />
       <jsp:include page="include/navigation.jsp" />
 
        <article>
            <h1>Your Pics</h1>
        <%
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
            if (lsPics == null) {
        %>
        <p>No Pictures found</p>
        <%
        } else {
            Iterator<Pic> iterator;
            iterator = lsPics.iterator();
            while (iterator.hasNext()) {
                Pic p = (Pic) iterator.next();

        %>
        <a href="<%=Globals.ROOT_PATH%>/Image/<%=p.getSUUID()%>" ><img src="<%=Globals.ROOT_PATH%>/Thumb/<%=p.getSUUID()%>"></a><br/><%

            }
            }
        %>
        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/BurnIgram">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
