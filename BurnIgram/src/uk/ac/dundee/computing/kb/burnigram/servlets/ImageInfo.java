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

import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;
import uk.ac.dundee.computing.kb.burnigram.models.PicModel;
import uk.ac.dundee.computing.kb.burnigram.stores.Comment;
import uk.ac.dundee.computing.kb.burnigram.stores.LoggedIn;
import uk.ac.dundee.computing.kb.burnigram.stores.Pic;

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
		PicModel picModel = new PicModel();
		Pic pic = picModel.getPicFromDB(Convertors.DISPLAY_PROCESSED, picid);
		
		List<Comment> comments = Comment.getCommentListFromDbByPicid(picid);
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
