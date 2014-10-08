<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="include/head.jsp" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${Globals.app_name }</title>
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
</head>
<body>
	<jsp:include page="include/header.jsp"></jsp:include>
	<jsp:include page="include/navigation.jsp"></jsp:include>

	<h3>Login</h3>
	<form method="POST" id="loginForm" action="Login">
		<ul>
			<li>User Name <input type="text" id="username" name="username"></li>
			<li>Password <input type="password" id="password" name="password"></li>
		</ul><input type="text" id="hidden" name="hidden"/>
		<br /> <input type="submit" value="Login">
	</form>
	<script type="text/javascript">
		$(document).ready(function() {

			$('#loginForm').submit(function() {
				
				var username = $('#username').val();
				var pass = $('#password').val();
				if (username.empty || pass.empty) {
					return false;
				}else{
					$.ajax({
						url : '${Globals.root_path}/Login',
						async : false,
						data : {
							saltfor : username
						},
						type : 'POST',
						datatype : 'json',

						success : function(data) {

							var response = $.parseJSON(data);
							if (response.salt == 'false') {
								return false;
							}
							//substitute password with 0s
							var value = Array(pass.length + 1).join("0");
							$('#password').val(value);
							pass = SHA1(pass);
							var hash = SHA1(response.salt + pass);
							$('#hidden').val(hash);

						},
						failure : function() {
							message('true', 'No connection to server');
						}
					});
					//to finally submit the form
					return true;
				}

				
			});
		});
	</script>
	<script type="text/javascript" src="${Globals.root_path}/js/sha1.js"></script>
	<jsp:include page="include/footer.jsp" />
</body>
</html>
