package uk.ac.dundee.computing.kb.burnigram.servlets;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.dundee.computing.kb.burnigram.beans.Comment;
import uk.ac.dundee.computing.kb.burnigram.beans.Pic;
import uk.ac.dundee.computing.kb.burnigram.dbHelpers.CommentDbHelper;
import uk.ac.dundee.computing.kb.burnigram.dbHelpers.PicDbHelper;
import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;

/**
 * Servlet implementation class ImageInfo
 */
@WebServlet({ "/ImageInfo", "/ImageInfo/*" })
public class ImageInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String args[] = Convertors.SplitRequestPath(request);
		UUID picid = UUID.fromString(args[2]);
		PicDbHelper picDbHelper = new PicDbHelper();
		Pic pic = picDbHelper.getPicFromDB(Convertors.DISPLAY_PROCESSED, picid);
		
		CommentDbHelper dbHelper = new CommentDbHelper();
		List<Comment> comments = dbHelper.getCommentListByPicid(picid);
        RequestDispatcher rd = request.getRequestDispatcher("/imageinfo.jsp");
        request.setAttribute("Pic", pic);
        request.setAttribute("Comments", comments);
        rd.forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
