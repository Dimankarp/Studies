package mitya.sites.face;

import jakarta.servlet.http.HttpServletRequest;

public final class ServletUtils {

    private ServletUtils(){};
    public static String getInnerUrl(HttpServletRequest request){
        return request.getRequestURI().substring(request.getContextPath().length());
    }
}
