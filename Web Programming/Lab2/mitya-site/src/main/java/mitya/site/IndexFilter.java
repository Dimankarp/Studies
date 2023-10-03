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
        String requestPath = req.getRequestURI().substring(req.getContextPath().length());
        System.out.println(requestPath);
        if(requestPath.startsWith("/resources")){
            chain.doFilter(req, res);
        }
        else {
            req.getRequestDispatcher("/pages"+requestPath).forward(req, res);
        }

    }

}
