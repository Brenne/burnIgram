<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="include/head.jsp" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${Globals.app_name}</title>
</head>
<body>
	<jsp:include page="include/header.jsp" />
	<jsp:include page="include/navigation.jsp" />
	<div class="content">

		<h3>File Upload</h3>
		<form method="POST" enctype="multipart/form-data" action="Image">
			File to upload: <input type="file" name="upfile"><br /> <br />
			<input type="submit" value="Press"> to upload the file!
		</form>
	</div>
	<jsp:include page="include/footer.jsp" />
</body>
</html>
