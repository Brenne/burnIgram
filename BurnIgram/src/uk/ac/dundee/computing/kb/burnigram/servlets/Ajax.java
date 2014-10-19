package uk.ac.dundee.computing.kb.burnigram.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;
import uk.ac.dundee.computing.kb.burnigram.models.User;

/**
 * Currently this servlet only provides a API for checking
 * if a username exists. More APIs used by ajax requests can be added
 * here in future
 */
@WebServlet({"/Username/","/Username/*"})
public class Ajax extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Ajax() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String args[] = Convertors.SplitRequestPath(request);
		String username =" ";
		try{
			username = args[2];
		}catch(ArrayIndexOutOfBoundsException ex){
			System.err.println("Invalid request to ajax servlet");
		}
		PrintWriter writer = response.getWriter();
		if( User.userNameExists(username)){
			writer.write("true");
		}else{
			writer.write("false");
		}
		writer.flush();
		writer.close();
	}

}
