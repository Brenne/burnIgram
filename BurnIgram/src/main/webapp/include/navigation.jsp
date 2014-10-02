<jsp:useBean id="loggedIn"
	class="uk.ac.dundee.computing.kb.burnigram.stores.LoggedIn"
	scope="session" />
<%@ page import="uk.ac.dundee.computing.kb.burnigram.stores.Globals" %>
<div id="nav">
	<ul>
		<li><a href="index.jsp">Home</a></li>

		
		<%
			if (!loggedIn.getLogedin()) {
		%>
		<li><a href="<%=Globals.ROOT_PATH %>/register.jsp">Sign up</a></li>
		<li><a href="<%=Globals.ROOT_PATH %>/login.jsp">Sign in</a></li>
		<%
			//if logged in
			} else {
		%>
		<li><a href="<%=Globals.ROOT_PATH %>/upload.jsp">Upload</a></li>
		<li><a href="<%=Globals.ROOT_PATH %>/Images/<%=loggedIn.getUsername()%>">
				Your Images</a></li>
		<li><a href="<%=Globals.ROOT_PATH %>/Logout">Sign Out</a></li>
		<%
			}
		%>
		
	</ul>
</div>