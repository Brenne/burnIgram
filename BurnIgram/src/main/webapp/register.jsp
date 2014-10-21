<%@page import="uk.ac.dundee.computing.kb.burnigram.beans.Globals"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="include/head.jsp" />
<title>${Globals.app_name}</title>
</head>
<body>
	<jsp:include page="include/header.jsp" />
	<jsp:include page="include/navigation.jsp" />
	<div class="content">

		<h2>Register as a new user</h2>
		<c:if test="${requestScope.errorMessage != null}">
			<div id="error">${requestScope.errorMessage}</div>
		</c:if>

		<form method="POST" action="Register" id="formRegister"
			class="register">
			<ul>
				<li><label>first name</label><input type="text"
					name="firstname"></li>
				<li><label>last name</label><input type="text"
					name="secondname"></li>
				<li><label>user name</label><input type="text" id="username"
					name="username"> <span class="userexists">User name
						already exists</span></li>
				<li><label>password</label><input type="password"
					name="password" id="password"></li>
				<li><label>repeat password</label><input type="password"
					name="password1" id="password1"><br>
				<br></li>
				<li><label>E-Mail</label><input type="email" name="email"></li>
			</ul>

			<input id="btnregister" type="submit" value="Register">
		</form>
	</div>
	<script type="text/javascript"
		src="${Globals.root_path}/js/register.js.jsp"></script>
	<jsp:include page="include/footer.jsp" />
</body>
</html>
