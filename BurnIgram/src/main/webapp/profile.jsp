<%@page import="uk.ac.dundee.computing.kb.burnigram.stores.Globals"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="include/head.jsp" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${Globals.app_name}- Profile</title>
</head>
<body>
	<jsp:useBean id="loggedIn"
		class="uk.ac.dundee.computing.kb.burnigram.stores.LoggedIn"
		scope="session" />
	<jsp:include page="include/header.jsp" />
	<jsp:include page="include/navigation.jsp" />
	Welcome to your profile
	<p>
		${loggedIn.user.username}<br> ${loggedIn.user.firstname}
		${loggedIn.user.lastname}<br> ${loggedIn.user.email}
	</p>
	<img src="${Globals.root_path}/Thumb/${loggedIn.user.profilepicId}">

</body>
</html>