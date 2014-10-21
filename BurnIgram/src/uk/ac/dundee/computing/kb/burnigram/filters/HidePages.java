package uk.ac.dundee.computing.kb.burnigram.filters;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uk.ac.dundee.computing.kb.burnigram.servlets.Login;
import uk.ac.dundee.computing.kb.burnigram.stores.Globals;
import uk.ac.dundee.computing.kb.burnigram.stores.LoggedIn;

/**
 * Servlet Filter implementation class HidePages
 */
@WebFilter(
		dispatcherTypes = {
				DispatcherType.REQUEST, 
				DispatcherType.FORWARD
		}
					, 
		urlPatterns = {"/register.jsp","/login.jsp","/Register","/Login"}
		)
public class HidePages implements Filter {

    private FilterConfig filterConfig;

	/**
     * Default constructor. 
     */
    public HidePages() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		this.filterConfig = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession(false);
		if(session != null){
			LoggedIn lg = (LoggedIn) session.getAttribute(Login.SESSION_NAME_LOGIN);
			if(lg != null && lg.getLogedin()){
				if(Globals.DEBUG)
					System.out.println("forward to index.jsp");
				RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
				rd.forward(request, response);
			}
		}

		// pass the request along the filter chain
		try {
			chain.doFilter(request, response);
		} catch (Throwable t) {		
			t.printStackTrace();
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		this.filterConfig = fConfig;
	}

}
