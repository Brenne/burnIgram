/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.kb.burnigram.servlets;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.dundee.computing.kb.burnigram.models.User;
import uk.ac.dundee.computing.kb.burnigram.stores.Globals;

/**
 *
 * @author Administrator
 */
@WebServlet(name = "Register", urlPatterns = { "/Register" })
public class Register extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String NAME_ERROR_REQUEST = "errorMessage";

	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub

	}

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
		String username   = request.getParameter("username");
		String password   = request.getParameter("password");
		String email 	  = request.getParameter("email");
		String firstname  = request.getParameter("firstname");
		String lastname = request.getParameter("secondname");
		Set<String> emailSet = new LinkedHashSet<String>(1);
		emailSet.add(email);
		User user = new User(username,firstname,lastname,emailSet);
		RequestDispatcher rd = request
				.getRequestDispatcher("register.jsp");
		
		try {
			if (user.registerUser(password)) {
				response.sendRedirect(Globals.ROOT_PATH);
			} else {
				request.setAttribute(NAME_ERROR_REQUEST, "Please try Again");
				rd.forward(request, response);
			}
		} catch (Throwable error) {
			request.setAttribute(NAME_ERROR_REQUEST, error.getMessage());
			rd.forward(request, response);
		}

	}

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
