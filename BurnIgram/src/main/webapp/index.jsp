<%-- 
    Document   : index
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.kb.burnigram.stores.Globals" %>
<jsp:useBean id="loggedIn" class="uk.ac.dundee.computing.kb.burnigram.stores.LoggedIn" scope="session" />
<!DOCTYPE html>
<html>
<head>
<title><%=Globals.ROOT_PATH%></title>
<link rel="stylesheet" type="text/css" href="Styles.css" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
	<jsp:include page="include/header.jsp" />
	<jsp:include page="include/navigation.jsp" />
	
</body>
</html>
