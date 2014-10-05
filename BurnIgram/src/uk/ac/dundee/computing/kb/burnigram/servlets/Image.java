package uk.ac.dundee.computing.kb.burnigram.servlets;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
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
@WebServlet(urlPatterns = {
    "/Image",
    "/Image/*",
    "/Thumb/*",
    "/Images",
    "/Images/*",
    "/OriginalImage/*"
})
@MultipartConfig

public class Image extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private HashMap<String,Integer> CommandsMap = new HashMap<String, Integer>();
    
    

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Image() {
        super();
        // TODO Auto-generated constructor stub
        CommandsMap.put("Image", 1);
        CommandsMap.put("Images", 2);
        CommandsMap.put("Thumb", 3);
        CommandsMap.put("OriginalImage",4);

    }

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
       
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        int command;
        try {
            command = (Integer) CommandsMap.get(args[1]);
        } catch (Exception et) {
            error("Bad Operator", response);
            return;
        }
        switch (command) {
            case 1:
                DisplayImage(Convertors.DISPLAY_PROCESSED,args[2], response);
                break;
            case 2:
            	//in this case args[2] should contain a username
                DisplayImageList(args[2], request, response);
                break;
            case 3:
                DisplayImage(Convertors.DISPLAY_THUMB,args[2],  response);
                break;
            case 4:
            	DisplayImage(Convertors.DISPLAY_IMAGE, args[2], response);
            default:
                error("Bad Operator", response);
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse resp)
    		throws ServletException, IOException {
    	 String args[] = Convertors.SplitRequestPath(request);
    	 LoggedIn loggedIn = (LoggedIn) request.getSession().getAttribute("loggedIn");
    	 PicModel picModel = new PicModel();
    	 picModel.setCluster();
    	 picModel.deletePic(UUID.fromString(args[2])); 
    }

    private void DisplayImageList(String username, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User();
        if(!user.userNameExists(username)){
        	response.sendRedirect(Globals.ROOT_PATH+"/index.jsp");
        }else{
        	PicModel tm = new PicModel();
            tm.setCluster();
            LinkedList<Pic> pictureList = tm.getPicsForUser(username);
            RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");
            request.setAttribute("Pics", pictureList);
            rd.forward(request, response);
        }
    	

    }

    private void DisplayImage(int type,String Image, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster();
  
        
        Pic p = tm.getPic(type,java.util.UUID.fromString(Image));
        
        OutputStream out = response.getOutputStream();

        response.setContentType(p.getType());
        response.setContentLength(p.getLength());
        //out.write(Image);
        InputStream is = new ByteArrayInputStream(p.getBytes());
        BufferedInputStream input = new BufferedInputStream(is);
        byte[] buffer = new byte[8192];
        for (int length = 0; (length = input.read(buffer)) > 0;) {
            out.write(buffer, 0, length);
        }
        out.close();
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        for (Part part : request.getParts()) {
            System.out.println("Part Name " + part.getName());

            String contentType = part.getContentType();
            String filename = part.getSubmittedFileName();
            
            InputStream is = request.getPart(part.getName()).getInputStream();
            int i = is.available();
            HttpSession session=request.getSession();
            LoggedIn lg= (LoggedIn)session.getAttribute(Login.SESSION_NAME_LOGIN);
            String username="majed";
            if (lg.getLogedin()){
                username=lg.getUser().getUsername();
            }
            if (i > 0) {
                byte[] b = new byte[i + 1];
                is.read(b);
                System.out.println("Length : " + b.length);
                PicModel tm = new PicModel();
                tm.setCluster();
                tm.insertPic(b, contentType, filename, username);

                is.close();
            }
            RequestDispatcher rd = request.getRequestDispatcher("/upload.jsp");
            rd.forward(request, response);
        }

    }

    private void error(String mess, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have an error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
        return;
    }
}
