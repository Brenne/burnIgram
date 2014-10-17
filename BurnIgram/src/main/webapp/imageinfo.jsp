<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<jsp:include page="include/head.jsp" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${Globals.app_name}</title>
</head>
<body>
	<jsp:include page="include/header.jsp" />
	<jsp:include page="include/navigation.jsp" />
	<a href="${Globals.root_path}/Image/${Pic.SUUID}"> <img id="thumb"
		src="${Globals.root_path}/Thumb/${Pic.SUUID}">
	</a>
	<br> Name: ${Pic.name}
	<br> Owner: ${Pic.user.username}
	<br>
	<a href="${Globals.root_path}/OriginalImage/${Pic.SUUID}">Display
		Original Image</a>
	<br>

	<button class="rotate" id="left">Rotate left</button>
	<button class="rotate" id="right">Rotate right</button>
	<p>
	<button class="brightness" id="dark">Darker</button>
	<button class="brightness" id="bright">Brighter</button>
	<h3>Comments:</h3>
	<c:if test="${loggedIn.logedin}">
	<p><strong>Add a new comment:</strong></p>
	<form method="POST" action="${Globals.root_path}/Comment/${Pic.SUUID}">
		<input type="text" value="" name="contents"/><br>
		<input type="submit" value="comment" name="sendComment"/>
	</form>
	</c:if>
	<div id="comments">
	<c:forEach items="${Comments}" var="Comment">
		<p class="comment">
		${Comment.user.username} added ${Comment.created}<br>
		<span class="commentContents">${Comment.content}</span>
		</p>
	</c:forEach>
	</div>
	<script type="text/javascript">var picid ="${Pic.SUUID}"</script>
	<script type="text/javascript" src="${Globals.root_path}/js/imageinfo.js.jsp"></script>
	
</body>
</html>