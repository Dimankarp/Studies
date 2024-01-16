package mitya.haha.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import mitya.haha.handlers.LoginSuccessHandler;
import mitya.haha.service.ShooterDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${jwt.public.key}")
    RSAPublicKey pub_key;

    @Value("${jwt.private.key}")
    RSAPrivateKey priv_key;


    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        //@formatter:off
            http
                    .securityMatcher("/api/**")
                    .authorizeHttpRequests((authorize) -> authorize

                            .requestMatchers("/api/auth/register").permitAll()
                            .requestMatchers("/api/auth/logout").permitAll()
                            .requestMatchers( "/api/auth/token").permitAll()

                            .requestMatchers("/api/shots/{name}").access(new WebExpressionAuthorizationManager("#name == authentication.name"))

                            .requestMatchers("/api/open/**").permitAll()

                            .anyRequest().authenticated())
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .oauth2ResourceServer((oauth2ResourceServer) ->
                            oauth2ResourceServer
                            .jwt((jwt) ->
                                    jwt.decoder(jwtDecoder())
                            ));
            //@formatter:on
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(ShooterDetailsService shooterService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(shooterService);
        provider.setPasswordEncoder(bcryptPassEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder bcryptPassEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.pub_key).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.pub_key).privateKey(this.priv_key).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }


    @Bean
    public AuthenticationSuccessHandler loginAuthSuccessHandler() {
        return new LoginSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler loginAuthFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }

}
