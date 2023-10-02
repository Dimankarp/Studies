package mitya.site;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class IndexFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!req.getMethod().equals("GET")) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            getServletContext().getRequestDispatcher("/error").forward(req, res);
        }
        else {
            chain.doFilter(req, res);
        }
    }

}
