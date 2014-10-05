<%@page import="uk.ac.dundee.computing.kb.burnigram.stores.Globals"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=Globals.APP_NAME%> - Profile</title>

</head>
<body>
	<jsp:useBean id="loggedIn"
		class="uk.ac.dundee.computing.kb.burnigram.stores.LoggedIn"
		scope="session" />
	<jsp:include page="include/header.jsp" />
	<jsp:include page="include/navigation.jsp" />
	Welcome to your profile
	<%=loggedIn.getUser().getUsername() %>

</body>
</html>