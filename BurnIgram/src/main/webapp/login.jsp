<%@page contentType="text/html" pageEncoding="UTF-8"%>
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

	<h3>Login</h3>
	<form method="POST" action="Login">
		<ul>
			<li>User Name <input type="text" name="username"></li>
			<li>Password <input type="password" name="password"></li>
		</ul>
		<br /> <input type="submit" value="Login">
	</form>


	<jsp:include page="include/footer.jsp"/>
</body>
</html>
