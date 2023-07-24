import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.WebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange()
                .pathMatchers("/public/**").permitAll()
                .anyExchange().authenticated()
                .and()
            .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .httpBasic()
                .and()
            .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        UserDetails user1 = User.withUsername("user1")
                .password("{bcrypt}" + passwordEncoder().encode("password1"))
                .roles("USER")
                .build();

        UserDetails user2 = User.withUsername("user2")
                .password("{bcrypt}" + passwordEncoder().encode("password2"))
                .roles("USER", "ADMIN")
                .build();

        return new MapReactiveUserDetailsService(user1, user2);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        ReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        return new JwtAuthenticationFilter(authenticationManager);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
