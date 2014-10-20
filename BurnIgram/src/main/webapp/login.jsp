<%@page contentType="text/html" pageEncoding="UTF-8"%><%@ taglib
	prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="include/head.jsp" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${Globals.app_name }</title>
</head>
<body>
	<jsp:include page="include/header.jsp"></jsp:include>
	<jsp:include page="include/navigation.jsp"></jsp:include>
	<div class="content">
		<h3>Login</h3>
		<c:if test="${requestScope.errorMessage != null}">
			<div id="error">${requestScope.errorMessage}</div>
		</c:if>
		<form method="POST" id="loginForm" class="login" action="${Globals.root_path}/Login">
			<ul>
				<li><label>user name </label><input type="text" id="username"
					name="username"></li>
				<li><label>password </label><input type="password"
					id="password" name="password"></li>
			</ul>
			<input type="hidden" id="hidden" name="hidden" /> <input
				id="btnlogin" type="submit" value="Login">
		</form>
	</div>
	<script type="text/javascript"
		src="${Globals.root_path}/js/login.js.jsp"></script>
	<script type="text/javascript" src="${Globals.root_path}/js/sha1.js"></script>
	<jsp:include page="include/footer.jsp" />
</body>
</html>
