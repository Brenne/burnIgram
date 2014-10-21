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
		<h2>Welcome to your profile</h2>
		<p>
			User name: ${loggedIn.user.username}<br> ${loggedIn.user.firstname}
			${loggedIn.user.lastname}
		</p>
		<p>	Your E-mails:<br>
			<c:forEach items="${loggedIn.user.email}" var="email">
				<input name="email" type="email" value="${email}"/><br>
			</c:forEach>
			<span id="lastEmail">
			New Email Address
			<br><input name="email" type="email" value=""/>
			</span>
			<span class="success">E-Mails updated</span>
		</p>
	<c:if test="${not empty loggedIn.user.profilepicId}">
		<img src="${Globals.root_path}/Thumb/${loggedIn.user.profilepicId}">
	</c:if>
	</div>
	
	<script type="text/javascript" src="js/profile.js.jsp"></script>
	<jsp:include page="include/footer.jsp"></jsp:include>
</body>
</html>