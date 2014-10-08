package uk.ac.dundee.computing.kb.burnigram.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;
import uk.ac.dundee.computing.kb.burnigram.models.PicModel;
import uk.ac.dundee.computing.kb.burnigram.models.User;
import uk.ac.dundee.computing.kb.burnigram.stores.Globals;
import uk.ac.dundee.computing.kb.burnigram.stores.LoggedIn;
import uk.ac.dundee.computing.kb.burnigram.stores.Pic;

/**
 * Servlet implementation class Image
 */
@WebServlet(urlPatterns = { "/Image/*", "/Thumb/*", "/Images", "/Images/*",
		"/OriginalImage/*" })
@MultipartConfig
public class Image extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> CommandsMap = new HashMap<String, Integer>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Image() {
		super();
		// TODO Auto-generated constructor stub
		CommandsMap.put("Image", 1);
		CommandsMap.put("Images", 2);
		CommandsMap.put("Thumb", 3);
		CommandsMap.put("OriginalImage", 4);

	}

	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String args[] = Convertors.SplitRequestPath(request);
		if (args.length <= 2) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}
		int command;
		try {
			command = (Integer) CommandsMap.get(args[1]);
		} catch (Exception et) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		switch (command) {
		case 1:
			DisplayImage(Convertors.DISPLAY_PROCESSED, args[2], response);
			break;
		case 2:
			if (User.userNameExists(args[2])) {
				DisplayImageList(args[2], request, response);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			break;
		case 3:
			DisplayImage(Convertors.DISPLAY_THUMB, args[2], response);
			break;
		case 4:
			DisplayImage(Convertors.DISPLAY_ORIGINAL_IMAGE, args[2], response);
		default:
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse resp)
			throws ServletException, IOException {
		LoggedIn loggedIn = (LoggedIn) request.getSession().getAttribute(
				"loggedIn");
		if(loggedIn == null || !loggedIn.getLogedin()){
			//unauthorized user can not delete pictures
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		String args[] = Convertors.SplitRequestPath(request);
		PicModel picModel = new PicModel();
		Pic pic;
		try{
			pic = picModel.getPicFromDB(Convertors.DISPLAY_ORIGINAL_IMAGE,
				UUID.fromString(args[2]));
			if(pic == null){
				//no picture found
				return;
			}
		}catch(ArrayIndexOutOfBoundsException | IllegalArgumentException ex){
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		if(!pic.getUser().getUsername().equals(loggedIn.getUser().getUsername())){
			//user can not delete pictures of other users
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		picModel.deletePic(pic);
	}

	private void DisplayImageList(String username, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (!User.userNameExists(username)) {
			response.sendRedirect(Globals.ROOT_PATH + "/index.jsp");
		} else {
			PicModel tm = new PicModel();
			LinkedList<Pic> pictureList = tm.getPicsForUser(username);
			RequestDispatcher rd = request
					.getRequestDispatcher("/UsersPics.jsp");
			request.setAttribute("Pics", pictureList);
			request.setAttribute("Username", username);
			rd.forward(request, response);
		}

	}

	private void DisplayImage(int type, String Image,
			HttpServletResponse response) throws ServletException, IOException {
		PicModel tm = new PicModel();
		Pic p = null;
		try {
			p = tm.getPicFromDB(type, java.util.UUID.fromString(Image));
		} catch (IllegalArgumentException Ex) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		if (p == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			OutputStream out = response.getOutputStream();

			response.setContentType(p.getType());
			response.setContentLength(p.getLength());

			InputStream is = new ByteArrayInputStream(p.getBytes());
			BufferedInputStream input = new BufferedInputStream(is);
			byte[] buffer = new byte[8192];
			for (int length = 0; (length = input.read(buffer)) > 0;) {
				out.write(buffer, 0, length);
			}
			out.close();
		}

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		for (Part part : request.getParts()) {
			System.out.println("Part Name " + part.getName());

			String contentType = part.getContentType();
			String filename = part.getSubmittedFileName();

			InputStream is = request.getPart(part.getName()).getInputStream();
			int i = is.available();
			HttpSession session = request.getSession();
			LoggedIn lg = (LoggedIn) session
					.getAttribute(Login.SESSION_NAME_LOGIN);
			String username = "majed";
			if (lg.getLogedin()) {
				username = lg.getUser().getUsername();
			}
			if (i > 0) {
				byte[] b = new byte[i + 1];
				is.read(b);
				System.out.println("Length : " + b.length);
				PicModel tm = new PicModel();

				tm.insertPic(b, contentType, filename, username);

				is.close();
			}
			RequestDispatcher rd = request.getRequestDispatcher("/upload.jsp");
			rd.forward(request, response);
		}

	}

	@Override
    protected void doPut(HttpServletRequest request, HttpServletResponse resp)
    		throws ServletException, IOException {
		LoggedIn loggedIn = (LoggedIn) request.getSession().getAttribute("loggedIn");
		if(loggedIn == null || !loggedIn.getLogedin()){
			//not put request without logged in
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
    	String args[] = Convertors.SplitRequestPath(request);
    	//TODO replace these lines with: request.getParameter("rotation")?
    	BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
    	String data = br.readLine();
    	br.close();
    	String[] operationArray = data.split(",");
    	Entry<String,String> operation = null;
    	PicModel picModel = new PicModel();
    	Pic pic;
    	try{
    		//generate a new Entry
    		operation = new AbstractMap.SimpleEntry<String, String>(operationArray[0],operationArray[1]);
    		pic = picModel.getPicFromDB(Convertors.DISPLAY_ORIGINAL_IMAGE, UUID.fromString(args[2]));
    	}catch(IndexOutOfBoundsException | IllegalArgumentException ex){
    		resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    		return;
    	}

   	 	if(loggedIn.getUser() == null || !loggedIn.getUser().getUsername().equalsIgnoreCase(pic.getUser().getUsername()) ){
   	 		//you can only change your own picture
   	 		resp.sendError(HttpServletResponse.SC_FORBIDDEN);		
   	 	}else{
   	 		picModel.updatePic(pic,operation);
   	 	}
    }
}
