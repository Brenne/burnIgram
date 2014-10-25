package uk.ac.dundee.computing.kb.burnigram.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.dundee.computing.kb.burnigram.beans.LoggedIn;
import uk.ac.dundee.computing.kb.burnigram.beans.Pic;
import uk.ac.dundee.computing.kb.burnigram.beans.User;
import uk.ac.dundee.computing.kb.burnigram.dbHelpers.PicDbHelper;
import uk.ac.dundee.computing.kb.burnigram.dbHelpers.UserDbHelper;
import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;

/**
 * Servlet implementation class Profile
 */
@WebServlet({ "/Profile", "/Profile/*" })
public class Profile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> CommandsMap= new HashMap<String, Integer>();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Profile() {
        super();
        CommandsMap.put("Profile", 1);
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoggedIn loggedIn = (LoggedIn) request.getSession().getAttribute("loggedIn");
		if(loggedIn.getUser()==null){
			return;
		}
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
			RequestDispatcher rd = null;
			String username = args[2];
			if(username.equalsIgnoreCase(loggedIn.getUser().getUsername())){
				rd = request.getRequestDispatcher("/myprofile.jsp");
			}else{		 
				UserDbHelper userDBHelper = new UserDbHelper();
				if(userDBHelper.userNameExists(username)){
					User user = userDBHelper.getUserFromDb(username);
					request.setAttribute("user", user);
				}		
			
				rd = request.getRequestDispatcher("/profile.jsp");
			}
			rd.forward(request, response);
			break;
		
		default:
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
       
        
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String args[] = Convertors.SplitRequestPath(request);
		LoggedIn loggedIn = (LoggedIn) request.getSession().getAttribute("loggedIn");
		if(loggedIn.getUser()==null){
			return;
		}
		UserDbHelper userDbHelper = new UserDbHelper();
		switch(args[2]){
			case "Profilepic":
				//TODO check if args[3] is not empty or null
				UUID pictureId = UUID.fromString(args[3]);
				PicDbHelper picDbHelper = new PicDbHelper();
				Pic picture = picDbHelper.getPicFromDB(Convertors.DISPLAY_ORIGINAL_IMAGE, pictureId);
				loggedIn.getUser().setProfilepic(picture.getUUID());
				
				if(!userDbHelper.updateUser(loggedIn.getUser())){
					System.err.println("Can not update user in DB");
				}
				break;
			case "Email":
				BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
				String data = br.readLine();
				br.close();
				if(data.startsWith("email=")){
					data=URLDecoder.decode(data,"UTF-8");
					data=data.replace("email=", "");
					String[] emails = data.split(",");
					LinkedHashSet<String> emailSet = new LinkedHashSet<String>(Arrays.asList(emails));
					loggedIn.getUser().setEmail(emailSet);
					userDbHelper = new UserDbHelper();
					if(!userDbHelper.updateUser(loggedIn.getUser())){
						System.err.println("Can not update user in DB");
					}
				}
				
		}
      
		
	}

}
