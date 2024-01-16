package mitya.haha.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final int TOKEN_LIFETIME_SECONDS;
    private final int REFRESH_TOKEN_LIFETIME_SECONDS;

    public int getRefreshTokenLifetime(){
        return REFRESH_TOKEN_LIFETIME_SECONDS;
    }
    private JwtDecoder decoder;
    private JwtEncoder encoder;

    public TokenService(JwtDecoder decoder, JwtEncoder encoder,
                        @Value("${jwt.token.lifetime.seconds:300}") int tokenLifetime,
                        @Value("${jwt.refresh_token.lifetime.seconds:600}") int refreshTokenLifetime) {
        this.decoder = decoder;
        this.encoder = encoder;
        TOKEN_LIFETIME_SECONDS = tokenLifetime;
        REFRESH_TOKEN_LIFETIME_SECONDS = refreshTokenLifetime;
    }

    private Jwt generateJwtToken(Authentication authentication, int lifetimeSeconds) {
        Instant now = Instant.now();

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        //@formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(lifetimeSeconds))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        //@formatter:on
        return this.encoder.encode(JwtEncoderParameters.from(claims));
    }

    public String generateToken(Authentication authentication) {
        return generateJwtToken(authentication, TOKEN_LIFETIME_SECONDS).getTokenValue();
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateJwtToken(authentication, REFRESH_TOKEN_LIFETIME_SECONDS).getTokenValue();
    }

    public Jwt validateToken(String token) throws JwtValidationException{
        return decoder.decode(token);
    }

}
