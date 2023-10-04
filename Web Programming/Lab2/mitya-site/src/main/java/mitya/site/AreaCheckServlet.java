package mitya.site;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mitya.site.model.Shot;
import mitya.site.model.Target;
import mitya.site.model.TargetBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

public class AreaCheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Target target;

    @Override
    public void init() {
        TargetBuilder builder = new TargetBuilder();
        /*
        Place for setting up the target.
        Because the default setting are used now,
        no configuration is done
         */
        target = builder.buildTarget();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        if(target == null){
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            servletContext.getRequestDispatcher("/error").forward(request, response);
        }
        else {
            try {
                int xCoord = Integer.parseInt(request.getParameter("xCoord"));
                double yCoord = Double.parseDouble(request.getParameter("yCoord"));
                int radius = Integer.parseInt(request.getParameter("radius"));

                Shot shot = target.processShot(xCoord, yCoord, radius);
                HttpSession session = request.getSession();
                Object shotsArrObject = session.getAttribute("SHOTS");
                try {
                    if (shotsArrObject == null) throw new IllegalStateException();
                    LinkedList<Shot> shots = (LinkedList<Shot>) shotsArrObject;
                    shots.addFirst(shot);
                } catch (ClassCastException | IllegalStateException e) {
                    LinkedList<Shot> shots = new LinkedList<Shot>();
                    shots.addFirst(shot);
                    session.setAttribute("SHOTS", shots);
                }
                request.setAttribute("isHit", shot.isHit());
                servletContext.getRequestDispatcher("/WEB-INF/shot.jsp").forward(request, response);

            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                servletContext.getRequestDispatcher("/error").forward(request, response);
            }
        }
    }


}
