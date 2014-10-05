<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.kb.burnigram.stores.*"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=Globals.APP_NAME%></title>
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" href="<%=Globals.ROOT_PATH%>/Styles.css" />
</head>
<body>
	<jsp:include page="include/header.jsp" />
	<jsp:include page="include/navigation.jsp" />

	<article>
		<h1>Your Pics</h1>
		<%
			java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request
					.getAttribute("Pics");
			if (lsPics == null) {
		%>
		<p>No Pictures found</p>
		<%
			} else {
				Iterator<Pic> iterator;
				iterator = lsPics.iterator();
				while (iterator.hasNext()) {
					Pic p = (Pic) iterator.next();
		%><p>
		<a href="<%=Globals.ROOT_PATH%>/Image/<%=p.getSUUID()%>"> 
		 <img id="<%=p.getSUUID()%>" src="<%=Globals.ROOT_PATH%>/Thumb/<%=p.getSUUID()%>"> 
		</a><br />
		<button  class="profilepic" value="Use as my profile picture">my profile pic</button>
		<button class="delete">Delete this pic</button>
		</p>
		<%
			}
			}
		%>
	</article>
	
<script type="text/javascript">
$(".profilepic").click(function(){
	var picid = $(this).parent().find("img").attr("id");


	$.ajax({
		url:"<%=Globals.ROOT_PATH%>/Profile/Profilepic/"+picid,
		type:"PUT"
	})
});

$(".delete").click(function(){
	var picid = $(this).parent().find("img").attr("id");
	$.ajax({
		url:"<%=Globals.ROOT_PATH%>/Image/"+picid,
		type:"DELETE"
	})
});


</script>
</body>
</html>
