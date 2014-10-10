<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<jsp:include page="include/head.jsp" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${Globals.app_name}</title>
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
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
	
	<p><strong>Add a new comment:</strong></p>
	<form method="POST" action="${Globals.root_path}/Comment/${Pic.SUUID}">
		<input type="text" value="" name="contents"/><br>
		<input type="submit" value="comment" name="sendComment"/>
	</form>
	<div id="comments">
	<c:forEach items="${Comments}" var="Comment">
		<p class="comment">
		${Comment.user.username} added ${Comment.created}<br>
		<span class="commentContents">${Comment.content}</span>
		</p>
	</c:forEach>
	</div>
	<script type="text/javascript">
	$(".brightness").click(
			function() {
				var value = this.id;

				$.ajax({
					url : "${Globals.root_path}/Image/${Pic.SUUID}",
					dataType : "text",
					data : "brightness," + value,
					type : "PUT"
				}).done(
						function() {
							var d = new Date();
							//the date is added as parameter to prevent browser from fetching image from cache
							$("#thumb").attr(
									"src",
									"${Globals.root_path}/Thumb/${Pic.SUUID}?"
											+ d.getTime());
						})

			});
	
	$(".rotate").click(
			function() {
				var direction = this.id;
				console.log(direction);
				$.ajax({
					url : "${Globals.root_path}/Image/${Pic.SUUID}",
					data : 	"rotate,"+direction,
					dataType : "text",
					type : "PUT"
				}).done(
						function() {
							var d = new Date();
							//the date is added as parameter to prevent browser from fetching image from cache
							$("#thumb").attr(
									"src",
									"${Globals.root_path}/Thumb/${Pic.SUUID}?"
											+ d.getTime());
						})

			});</script>
	
</body>
</html>