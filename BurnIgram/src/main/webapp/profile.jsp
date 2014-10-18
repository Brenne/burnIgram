<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="include/head.jsp" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${Globals.app_name}-Profile</title>
</head>
<body>
	<jsp:include page="include/header.jsp" />
	<jsp:include page="include/navigation.jsp" />
	<div class="content">
		Welcome to your profile
		<p>
			${loggedIn.user.username}<br> ${loggedIn.user.firstname}
			${loggedIn.user.lastname}<br> ${loggedIn.user.email}
		</p>
	<c:if test="${not empty loggedIn.user.profilepicId}">
		<img src="${Globals.root_path}/Thumb/${loggedIn.user.profilepicId}">
	</c:if>
	</div>
	<jsp:include page="include/footer.jsp"></jsp:include>
</body>
</html>