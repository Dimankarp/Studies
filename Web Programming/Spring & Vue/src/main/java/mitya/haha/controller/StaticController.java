package mitya.haha.controller;

import mitya.haha.service.TokenService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class StaticController {

    private TokenService tokenService;

    public StaticController(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @RequestMapping(value={"/", "/register"}, method = RequestMethod.GET, produces = "text/html")
    public String openAppPages(){
        return "index.html";
    }

    /*
    Unfortunately I couldn't find a way to add a TokenExtractor to current Spring Oauth2 filters, so
    this is a rough equivalent. (Same method is used with refreshToken in AuthController
    */
    @RequestMapping(value={"/shot"}, method = RequestMethod.GET, produces = "text/html")
    public String protectedAppPages(@CookieValue("refresh-token") String refreshTokenValue)
            throws JwtValidationException {
        Jwt token = tokenService.validateToken(refreshTokenValue);
        return "index.html";
    }


}
