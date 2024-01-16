package mitya.site;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import com.google.gson.Gson;
import mitya.site.model.Shot;

import static mitya.site.ServletUtils.getInnerUrl;

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

        if (request.getMethod().equals("GET")) doGet(request, response);
        else {
            ServletContext servletContext = getServletContext();
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED); //The Request is not a GET
            servletContext.getRequestDispatcher("/error").forward(request, response);
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(getInnerUrl(request));
        if (       request.getParameter("xCoord") != null
                && request.getParameter("yCoord") != null
                && request.getParameter("radius") != null) {
            doShotCheck(request, response);
        } else if (getInnerUrl(request).startsWith("/pages/get-shots")) {
            doSendShots(request, response);
        } else {
			/* Uncomment if the logic of Controller Servlet controlling literally  everything has changed
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		servletContext.getRequestDispatcher("/error").forward(request, response);
		*/
            doDefaultIndex(request, response);
        }
    }

    private void doShotCheck(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        servletContext.getRequestDispatcher("/checker").forward(request, response);
    }

    private void doDefaultIndex(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        servletContext.getRequestDispatcher("/index").forward(request, response);
    }
    private void doSendShots(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object shotsArrObject = session.getAttribute("SHOTS");
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            if (shotsArrObject == null) throw new IllegalStateException();
            /*
            I know that if with exceptions instead of else is a bad tone
             - but I have a Collection with generic casting inside, so
             having one catch is kinda beneficial...
            */

            LinkedList<Shot> shots = (LinkedList<Shot>) shotsArrObject;
            String shotsArr = new Gson().toJson(shots.toArray());
            out.print(shotsArr);
            out.flush();
        } catch (ClassCastException | IllegalStateException e) {
            System.out.println("Giving out Empty JSON");
            out.print(new Gson().toJson(new Shot[0]));
            out.flush();
        }
    }



}
	


