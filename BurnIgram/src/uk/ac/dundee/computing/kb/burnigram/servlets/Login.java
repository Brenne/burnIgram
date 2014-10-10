/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.kb.burnigram.servlets;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;

import javax.json.JsonObject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uk.ac.dundee.computing.kb.burnigram.models.User;
import uk.ac.dundee.computing.kb.burnigram.stores.Globals;
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

		if (saltFor != null && !saltFor.isEmpty()) {
			// salt process
			if (!User.userNameExists(saltFor)) {
				// no username so no salt
				return;
			} else {
				
				String str = randomString();
				String responseString = "{\"salt\":\"" + str + "\"}";
				HttpSession session = request.getSession();
				session.setAttribute("salt", str);
				response.setContentType("text/html");
				response.getWriter().write(responseString);
			}
		} else if(request.isRequestedSessionIdFromCookie()) {
			// general login process
			String username = request.getParameter("username");
			if (username == null || username.isEmpty())
				return;
			String saltetPw = request.getParameter("hidden");
			HttpSession session = request.getSession();
			String salt = (String) session.getAttribute("salt");
			session.removeAttribute("salt");
			User user = new User(username);
			if (user.isValidUser(saltetPw,salt)) {
				user.initUserFromDB();
				
				System.out.println("Session in servlet " + session);
				LoggedIn lg = new LoggedIn();
				lg.setLogedin();
				lg.setUser(user);
				// request.setAttribute("LoggedIn", lg);

				session.setAttribute(SESSION_NAME_LOGIN, lg);
				System.out.println("Session in servlet " + session);
				RequestDispatcher rd = request
						.getRequestDispatcher("index.jsp");
				rd.forward(request, response);

			} else {
				System.out.println("invalid user");
				response.sendRedirect(Globals.ROOT_PATH + "/login.jsp");
			}
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
