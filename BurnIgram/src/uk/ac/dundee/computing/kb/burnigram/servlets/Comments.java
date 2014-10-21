package uk.ac.dundee.computing.kb.burnigram.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.dundee.computing.kb.burnigram.beans.Comment;
import uk.ac.dundee.computing.kb.burnigram.beans.Globals;
import uk.ac.dundee.computing.kb.burnigram.beans.LoggedIn;
import uk.ac.dundee.computing.kb.burnigram.dbHelpers.CommentDbHelper;
import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;

/**
 * Servlet implementation class Comment
 * currently only the onPost method is supported
 * 
 */
@WebServlet("/Comment/*")
public class Comments extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Comments() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String args[] = Convertors.SplitRequestPath(request);
//		UUID picid = UUID.fromString(args[2]);

		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoggedIn loggedIn = (LoggedIn) request.getSession().getAttribute(Login.SESSION_NAME_LOGIN);
//		if(loggedIn == null || !loggedIn.getLogedin()){
//			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
//			return;
//		}
		String content = (String) request.getParameter("contents");
		String args[] = Convertors.SplitRequestPath(request);
		UUID picid = null;
		try{
			picid = UUID.fromString(args[2]);
		}catch(ArrayIndexOutOfBoundsException | IllegalArgumentException ex){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		Date currentTime = new Date();
		Comment comment = new Comment(picid, currentTime, loggedIn.getUser(), content);
		CommentDbHelper dbHelper = new CommentDbHelper();
		if(dbHelper.insertComment(comment)){
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.sendRedirect(Globals.ROOT_PATH+"/ImageInfo/"+picid.toString());
			
		}else{
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "comment not saved");
		}
		 

	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doDelete(req, resp);
	}

}
