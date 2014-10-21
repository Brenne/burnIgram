/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.kb.burnigram.servlets;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uk.ac.dundee.computing.kb.burnigram.dbHelpers.UserDbHelper;
import uk.ac.dundee.computing.kb.burnigram.models.User;
import uk.ac.dundee.computing.kb.burnigram.stores.LoggedIn;

/**
 *
 * @author Administrator
 */
@WebServlet(name = "Login", urlPatterns = { "/Login" })
public class Login extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String SESSION_NAME_LOGIN = "loggedIn";

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String saltFor = request.getParameter("saltfor");
		HttpSession session = request.getSession();
		if (saltFor != null && !saltFor.isEmpty()) {
			if (!User.userNameExists(saltFor)) {
				RequestDispatcher rd = request
						.getRequestDispatcher("login.jsp");
				/*
				 * we know that the username is wrong but don't want to reveal the information
				 * that this user exists. Therefore we respond with a more general error message 
				 */
				request.setAttribute("errorMessage", "Invalid username or password");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				rd.forward(request, response);
				return;
			} else {
				
				String str = randomString();
				String responseString = "{\"salt\":\"" + str + "\"}";
				session.setAttribute("salt", str);
				response.setContentType("text/html");
				response.getWriter().write(responseString);
			}
		} else if(request.isRequestedSessionIdFromCookie() && 
				session.getAttribute("salt") != null) {
			// general login process
			String username = request.getParameter("username");
			if (username == null || username.isEmpty())
				return;
			String saltetPw = request.getParameter("hidden");	
			String salt = (String) session.getAttribute("salt");
			session.removeAttribute("salt");
			UserDbHelper userDbHelper = new UserDbHelper();
			
			if (userDbHelper.isValidUser(username,saltetPw,salt)) {
				User user = User.initUserFromDB(username);
				
				System.out.println("Session in servlet " + session);
				LoggedIn lg = new LoggedIn();
				lg.setLogedin();
				lg.setUser(user);
				// request.setAttribute("LoggedIn", lg);

				session.setAttribute(SESSION_NAME_LOGIN, lg);
				System.out.println("Session in servlet " + session);
//				RequestDispatcher rd = request
//						.getRequestDispatcher("index.jsp");
//				rd.forward(request, response);
				response.sendRedirect("index.jsp");

			} else {
				System.out.println("invalid user");
				RequestDispatcher rd = request
						.getRequestDispatcher("login.jsp");
				request.setAttribute("errorMessage", "Invalid username or password");
				rd.forward(request, response);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.sendRedirect("login.jsp");
		}

	}
	
	//from http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string?answertab=votes#tab-top
	private String randomString() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	// public byte[] stringToByte(String input) {
	// if (Base64.isBase64(input)) {
	// return Base64.decodeBase64(input);
	//
	// } else {
	// return Base64.encodeBase64(input.getBytes());
	// }
	// }

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
