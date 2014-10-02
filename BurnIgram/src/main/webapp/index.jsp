<%-- 
    Document   : index
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.kb.burnigram.stores.*" %>
<jsp:useBean id="loggedIn" class="uk.ac.dundee.computing.kb.burnigram.stores.LoggedIn" scope="session" />
<!DOCTYPE html>
<html>
<head>
<title>Instagrim</title>
<link rel="stylesheet" type="text/css" href="Styles.css" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
	<header>
		<h1>InstaGrim !</h1>
		<h2>Your world in Black and White</h2>
		
	</header>
	<div id="nav">
		<ul>


			<li><a href="upload.jsp">Upload</a></li>
			
			<%  if (loggedIn.getLogedin()) {
            %>

						<li><a href="/BrunIgram/Images/<%=loggedIn.getUsername()%>">
							Your Images</a>
						</li>
			<%		
                }else{
                                %>
			<li><a href="register.jsp">Register</a></li>
			<li><a href="login.jsp">Login</a></li>
			<% }
                                        
                            
                    %>
		</ul>
	</div>
	<footer>
		<ul>
			<li class="footer"><a href="/Burnigram">Home</a></li>
			<li>&COPY; Andy C</li>
		</ul>
	</footer>
</body>
</html>
