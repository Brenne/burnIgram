package uk.ac.dundee.computing.kb.burnigram.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;
import uk.ac.dundee.computing.kb.burnigram.models.PicModel;
import uk.ac.dundee.computing.kb.burnigram.models.User;
import uk.ac.dundee.computing.kb.burnigram.stores.LoggedIn;
import uk.ac.dundee.computing.kb.burnigram.stores.Pic;

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
		
       
        
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String args[] = Convertors.SplitRequestPath(request);
		LoggedIn loggedIn = (LoggedIn) request.getSession().getAttribute("loggedIn");
		if(loggedIn.getUser()==null){
			return;
		}
		switch(args[2]){
			case "Profilepic":
				//TODO check if args[3] is not empty or null
				UUID pictureId = UUID.fromString(args[3]);
				PicModel pictureModel = new PicModel();
				Pic picture = pictureModel.getPicFromDB(Convertors.DISPLAY_ORIGINAL_IMAGE, pictureId);
				loggedIn.getUser().setProfilepic(picture.getUUID());
				loggedIn.getUser().updateUser();
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
					loggedIn.getUser().updateUser();
				}
				
		}
      
		
	}

}
