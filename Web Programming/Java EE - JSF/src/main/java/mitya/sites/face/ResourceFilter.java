package mitya.sites.face;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static mitya.sites.face.ServletUtils.getInnerUrl;

public class ResourceFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String requestPath = getInnerUrl(req);
        if(requestPath.startsWith("/resources")){
            chain.doFilter(req, res);
        }
        else {
            req.getRequestDispatcher("/faces"+requestPath).forward(req, res);
        }

    }

}
