package mitya.haha.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import mitya.haha.model.LoginRequest;
import mitya.haha.service.ShooterService;
import mitya.haha.service.TokenService;
import mitya.haha.service.UserAlreadyRegisteredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final ShooterService userService;
    private final TokenService tokenService;
    private final AuthenticationManager authManager;

    @Autowired
    public AuthController(TokenService tokenService, ShooterService userService, AuthenticationManager authManager){
        this.tokenService = tokenService;
        this.userService = userService;
        this.authManager = authManager;
    }

    @PostMapping("/token")
    public String tokenByLogin(LoginRequest loginRequest, HttpServletResponse response) throws JwtValidationException {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        String refreshToken = tokenService.generateRefreshToken(authentication);
        response.addCookie(getRefreshTokenCookie(refreshToken));

        return tokenService.generateToken(authentication);

    }
    @GetMapping("/token")
    public String tokenByRefreshToken(@CookieValue("refresh-token") String refreshTokenValue, HttpServletResponse response)
            throws JwtValidationException{
        Jwt token = tokenService.validateToken(refreshTokenValue);
        Authentication authentication =  new JwtAuthenticationToken(token);
        String refreshToken = tokenService.generateRefreshToken(authentication);
        response.addCookie(getRefreshTokenCookie(refreshToken));

        return tokenService.generateToken(authentication);
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username, @RequestParam("password") String password) throws UserAlreadyRegisteredException {
        userService.registerUser(username, password);
        return "Successfully registered!";
    }

    @PostMapping("/logout")
    public String logout(@CookieValue("refresh-token") String refreshTokenValue, HttpServletResponse response) {
        Cookie removedCookie = getRefreshTokenCookie(null);
        removedCookie.setMaxAge(0);
        response.addCookie(removedCookie);
        return "Successfully logged out!";
    }

    private Cookie getRefreshTokenCookie(String refreshToken){
        Cookie cookie = new Cookie("refresh-token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(tokenService.getRefreshTokenLifetime());
        /*
        Note: Might be better to move all token-expecting urls
        under a single protected url.
         */
        cookie.setPath("/");
        return cookie;
    }



}
