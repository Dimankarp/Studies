package mitya.site;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

public class AreaCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public AreaCheckServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		try {
			int xCoord = Integer.parseInt(request.getParameter("xCoord"));
			double yCoord = Double.parseDouble(request.getParameter("yCoord"));
			int radius = Integer.parseInt(request.getParameter("radius"));
			
			Shot shot = new Shot(xCoord, yCoord, radius, new Date());
			HttpSession session = request.getSession();
			Object shotsArrObject = session.getAttribute("SHOTS");
			try {
					if(shotsArrObject == null) throw new IllegalStateException();
					LinkedList<Shot> shots = (LinkedList<Shot>) shotsArrObject;	
					shots.addFirst(shot);
				}
				catch (ClassCastException|IllegalStateException e) {
					LinkedList<Shot> shots = new LinkedList<Shot>();	
					shots.addFirst(shot);
					session.setAttribute("SHOTS", shots);	
			}
			request.setAttribute("isHit", shot.isHit());
			servletContext.getRequestDispatcher("/WEB-INF/shot.jsp").forward(request, response);
			
		}
		catch (IllegalArgumentException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			servletContext.getRequestDispatcher("/error").forward(request, response);
		}
	}


}
