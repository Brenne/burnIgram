<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${Globals.app_name}</title>
</head>
<body>
<jsp:include page="include/header.jsp"/>
<jsp:include page="include/navigation.jsp"/>
<img src="${Globals.root_path}/Image/${Pic.SUUID}" width="50%" height="50%"><br>
Name:${Pic.name}<br>
Owner:${Pic.user.username}
</body>
</html>