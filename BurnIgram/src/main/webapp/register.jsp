<%@page import="uk.ac.dundee.computing.kb.burnigram.stores.Globals"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jsp:include page="include/head.jsp" />
        <title>${Globals.app_name}</title>
    </head>
    <body>
        <jsp:include page="include/header.jsp" />
        <jsp:include page="include/navigation.jsp" />
       
        
            <h3>Register as a new user</h3>
            <form method="POST"  action="Register" class="register">
                <ul>
                	<li><label>first name</label><input type="text" name="firstname"></li>
                	<li><label>last name</label><input type="text" name="secondname"></li>
                    <li><label>user name</label><input type="text" name="username"></li>
                    <li><label>password</label><input type="password" name="password"></li>
                    <li><label>repeat password</label><input type="password" name="password1"></li>
                    <li><label>primary E-Mail</label><input type="email" name="email"></li>
                </ul>
                <br/>
                <input type="submit" value="Register"> 
            </form>

        
       <jsp:include page="include/footer.jsp" />
    </body>
</html>
