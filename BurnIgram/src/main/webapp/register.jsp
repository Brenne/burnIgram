<%-- 
    Document   : register.jsp
    Created on : Sep 28, 2014, 6:29:51 PM
    Author     : Administrator
--%>

<%@page import="uk.ac.dundee.computing.kb.burnigram.stores.Globals"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><%=Globals.APP_NAME%></title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
    </head>
    <body>
        <jsp:include page="include/header.jsp" />
        <jsp:include page="include/navigation.jsp" />
       
        
            <h3>Register as a new user</h3>
            <form method="POST"  action="Register">
                <ul>
                	<li>first name<input type="text" name="firstname"></li>
                	<li>Last name<input type="text" name="secondname"></li>
                    <li>User Name <input type="text" name="username"></li>
                    <li>Password <input type="password" name="password"></li>
                    <li>Repeat Password <input type="password" name="password1"></li>
                    <li>primary E-Mail<input type="email" name="email"></li>
                </ul>
                <br/>
                <input type="submit" value="Register"> 
            </form>

        
        <footer>
           
        </footer>
    </body>
</html>
