<%-- 
    Document   : login.jsp
    Created on : Sep 28, 2014, 12:04:14 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${Globals.app_name }</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />

    </head>
    <body>
        <jsp:include page="include/header.jsp"></jsp:include>
         <jsp:include page="include/navigation.jsp"></jsp:include>
       
        <article>
            <h3>Login</h3>
            <form method="POST"  action="Login">
                <ul>
                    <li>User Name <input type="text" name="username"></li>
                    <li>Password <input type="password" name="password"></li>
                </ul>
                <br/>
                <input type="submit" value="Login"> 
            </form>

        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Burnigram">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
