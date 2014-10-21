<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.kb.burnigram.beans.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="include/head.jsp" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${Globals.app_name}-Pics of ${Username}</title>
</head>
<body>
	<jsp:include page="include/header.jsp" />
	<jsp:include page="include/navigation.jsp" />
	<div class="content">
		<h2>
			<c:choose>
				<c:when test="${Username==loggedIn.user.username}">
				Your Pics
			</c:when>
				<c:otherwise>
				Pics of ${Username}
			</c:otherwise>
			</c:choose>
		</h2>
		<div class="success"></div>
		<c:forEach items="${Pics}" var="pic">
			<p class="pic">
				<a href="${Globals.root_path}/Image/${pic.SUUID}"> <img
					id="${pic.SUUID}" src="${Globals.root_path}/Thumb/${pic.SUUID}">
				</a><br />
				<button class="profilepic" value="Use as my profile picture">my
					profile pic</button>
				<button class="delete">Delete this pic</button>
				<br> <a href="${Globals.root_path}/ImageInfo/${pic.SUUID}">Image
					info</a>
			</p>
		</c:forEach>
	</div>
	<script type="text/javascript"
		src="${Globals.root_path}/js/Userpics.js.jsp"></script>
	<jsp:include page="include/footer.jsp" />

</body>
</html>
