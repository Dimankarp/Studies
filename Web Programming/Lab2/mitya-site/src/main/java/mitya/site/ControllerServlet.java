package mitya.site;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.function.ToDoubleFunction;
import com.google.gson.Gson;

public class ControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	

    public ControllerServlet() {
        super();
    }
    
    
  /*
  Overriding service method (which is not prohibited but discouraged) to
  dispatch unimplemented request methods to error pages.
  */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	if(request.getMethod().equals("GET"))doGet(request, response);
    	else {
    		ServletContext servletContext = getServletContext();
    		response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED); //The Request is not a GET
    		servletContext.getRequestDispatcher("/error").forward(request, response);
    	}
    }
    

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println(request);
		System.out.println("Responding with STARTING INDEX");
		ServletContext servletContext = getServletContext();
		if(	   request.getParameter("xCoord") != null
			&& request.getParameter("yCoord") != null
			&& request.getParameter("radius") != null) {
			servletContext.getRequestDispatcher("/checker").forward(request, response);
			return;
		}
		if(request.getHeader("accept_json") != null && request.getHeader("accept_json").equals("true")) {
			HttpSession session = request.getSession();
			Object shotsArrObject = session.getAttribute("SHOTS");
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
					System.out.println("Giving out JSON");
					if(shotsArrObject == null) throw new IllegalStateException();
					LinkedList<Shot> shots = (LinkedList<Shot>) shotsArrObject;	
					String shotsArr = new Gson().toJson(shots.toArray());
					out.print(shotsArr) ;
					out.flush();
			}
				catch (ClassCastException|IllegalStateException e) {
					System.out.println("Giving out Empty JSON");
					out.print(new Gson().toJson(new Shot[0]));
					out.flush();
				}
			return;
		}
		/* Uncomment if the logic of Controller Servlet controlling literally  everything has changed
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		servletContext.getRequestDispatcher("/error").forward(request, response);
		*/
		System.out.println("Responding with INDEX");
		servletContext.getRequestDispatcher("/index").forward(request, response);
		}
		

	
}
	


