package mitya.haha.controller;

import mitya.haha.service.ShooterDetailsService;
import mitya.haha.service.UserAlreadyRegisteredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final long TOKEN_LIFETIME = 300L;
    private final JwtEncoder encoder;
    private final ShooterDetailsService userService;

    @Autowired
    public AuthController(JwtEncoder encoder, ShooterDetailsService userService){
        this.encoder = encoder;
        this.userService = userService;
    }

    @PostMapping("/token")
    public String token(Authentication authentication) {
        Instant now = Instant.now();

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        //@formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(TOKEN_LIFETIME))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        //@formatter:on
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username, @RequestParam("password") String password) throws UserAlreadyRegisteredException {
            userService.registerUser(username, password);
            return "Successfully registered!";
    }


}
