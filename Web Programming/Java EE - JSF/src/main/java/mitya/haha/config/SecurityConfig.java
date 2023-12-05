package mitya.haha.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import mitya.haha.service.ShooterDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;


@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${jwt.public.key}")
    RSAPublicKey pub_key;

    @Value("${jwt.private.key}")
    RSAPrivateKey priv_key;


    @Bean
    @Order(1)
    public SecurityFilterChain registeringSecurityFilterChain(HttpSecurity http) throws Exception {
        //@formatter:off
        http
                .securityMatcher("/api/auth/register")
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //@formatter:on
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain authApiSecurityFilterChain(HttpSecurity http) throws Exception {
        //@formatter:off
        http
                .securityMatcher("/api/auth/*")
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated())
                .csrf((csrf) -> csrf.ignoringRequestMatchers("/api/auth/*"))
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults());
        //@formatter:on
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        //@formatter:off
            http
                    .securityMatcher("/api/**")
                    .authorizeHttpRequests((authorize) -> authorize
                            .requestMatchers("/api/open/**").permitAll()
                            .anyRequest().authenticated())
                    .csrf((csrf) -> csrf.ignoringRequestMatchers("/api/auth/*"))
                    .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .oauth2ResourceServer((oauth2ResourceServer) ->
                            oauth2ResourceServer
                            .jwt((jwt) ->
                                    jwt
                                            .decoder(jwtDecoder())
                            ));
            //@formatter:on
        return http.build();
    }



    @Autowired
    protected void configureUserDetailsService(AuthenticationManagerBuilder auth, ShooterDetailsService userService) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bcryptPassEncoder());
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
}
