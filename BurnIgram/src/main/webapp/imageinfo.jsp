<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${Globals.app_name}</title>
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
</head>
<body>
<jsp:include page="include/header.jsp"/>
<jsp:include page="include/navigation.jsp"/>
<a href="${Globals.root_path}/Image/${Pic.SUUID}">
	<img id="thumb" src="${Globals.root_path}/Thumb/${Pic.SUUID}">
</a><br>
Name: ${Pic.name}<br>
Owner: ${Pic.user.username}<br>
<a href="${Globals.root_path}/OriginalImage/${Pic.SUUID}">Display Original Image</a><br>

<button id="rotateleft"> Rotate left</button>
<button id="rotateight "> Rotate right</button>
<script type="text/javascript">

$("#rotateleft").click(function(){
	$.ajax({
		url:"${Globals.root_path}/Image/${Pic.SUUID}",
		dataType: "text",
		data:"rotate,left",
		type : "PUT"
	}).done(function() {
		
		  var d = new Date();
		  console.log("done");
		 
		  console.log(thumb);
		  $("#thumb").attr("src","${Globals.root_path}/Thumb/${Pic.SUUID}?"+d.getTime());
	})
	
		});

</script>
</body>
</html>