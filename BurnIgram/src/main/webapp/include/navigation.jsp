<jsp:useBean id="loggedIn"
	class="uk.ac.dundee.computing.kb.burnigram.stores.LoggedIn"
	scope="session" />
<%@ page import="uk.ac.dundee.computing.kb.burnigram.stores.Globals" %>

<div id="nav">
	<ul>
		<li><a href="${Globals.root_path}/index.jsp">Home</a></li>

		
		<%
			if (!loggedIn.getLogedin()) {
		%>
		<li><a href="${Globals.root_path}/register.jsp">Sign up</a></li>
		<li><a href="${Globals.root_path}/login.jsp">Sign in</a></li>
		<%
			//if logged in
			} else {
		%>
		<li><a href="${Globals.root_path}/profile.jsp">Profile</a></li>
		<li><a href="${Globals.root_path}/upload.jsp">Upload</a></li>
		<li><a href="${Globals.root_path}/Images/<%=loggedIn.getUser().getUsername()%>">
				Your Images</a></li>
		<li><a href="${Globals.root_path}/Logout">Sign Out</a></li>
		<%
			}
		%>
		
	</ul>
</div>