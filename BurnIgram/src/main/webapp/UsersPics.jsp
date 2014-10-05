<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.kb.burnigram.stores.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=Globals.APP_NAME%></title>
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="<%=Globals.ROOT_PATH%>/Styles.css" />
</head>
<body>
	<jsp:include page="include/header.jsp" />
	<jsp:include page="include/navigation.jsp" />


	<h1>Your Pics</h1>

	<c:forEach items="${Pics}" var="pic">
		<p>
			<a href="${Globals.root_path}/Image/${pic.SUUID}"> <img
				id="${pic.SUUID}" src="${Globals.root_path}/Thumb/${pic.SUUID}">
			</a><br />
			<button class="profilepic" value="Use as my profile picture">my
				profile pic</button>
			<button class="delete">Delete this pic</button><br>
			<a href="${Globals.root_path}/ImageInfo/${pic.SUUID}">Image info</a>
		</p>
	</c:forEach>


	<script type="text/javascript" src="${Globals.root_path}/js/Userpics.js.jsp"></script>

	
</body>
</html>
