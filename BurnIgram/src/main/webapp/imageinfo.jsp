<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%>
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
		<a href="${Globals.root_path}/Image/${Pic.SUUID}"> <img id="thumb"
			src="${Globals.root_path}/Thumb/${Pic.SUUID}">
		</a> <br> Name: ${Pic.name} <br> Owner: ${Pic.user.username} <br>
		<a href="${Globals.root_path}/OriginalImage/${Pic.SUUID}">Display
			Original Image </a> 
			<button class="original" id="original">Revert to original</button><br>
		<h3>Manipulations: </h3>
		<button class="rotate" id="left">Rotate left</button>
		<button class="rotate" id="right">Rotate right</button>
		
			<button class="brightness" id="dark">Darker</button>
			<button class="brightness" id="bright">Brighter</button>
		
		
		<p>Magic Filter (experimental)<br>
			<button class="magic" id="sketch">Sketch</button>
		</p>
		
		<h2>Comments:</h2>
		<c:if test="${loggedIn.logedin}">
			<p>
				<strong>Add a new comment:</strong>
			</p>
			<form method="POST"
				action="${Globals.root_path}/Comment/${Pic.SUUID}">
				<input type="text" value="" name="contents" /><br> <input
					type="submit" value="comment" name="sendComment" />
			</form>
		</c:if>
		<div id="comments">
			<c:forEach items="${Comments}" var="Comment">
				<c:set value="${Comment.user.username}" var="commenter"/>
				<p class="comment">
					<a class="commenter" title="Go to ${commenter}'s image page"
					href="${Globals.root_path}/Images/${commenter}">${commenter}</a> added ${Comment.createdS}<br> <span
						class="commentContents">${Comment.content}</span>
				</p>
			</c:forEach>
		</div>
	</div>
	<jsp:include page="include/footer.jsp"></jsp:include>
	<script type="text/javascript">
		var picid = "${Pic.SUUID}"
	</script>
	<script type="text/javascript"
		src="${Globals.root_path}/js/imageinfo.js.jsp"></script>

</body>
</html>