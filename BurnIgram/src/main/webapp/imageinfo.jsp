<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
	<script type="text/javascript">
	$(".rotate").click(
			function() {
				var direction = this.id;
				console.log(direction);
				$.ajax({
					url : "${Globals.root_path}/Image/${Pic.SUUID}",
					dataType : "text",
					data : "rotate," + direction,
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