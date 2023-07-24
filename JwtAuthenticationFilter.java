import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter extends AuthenticationWebFilter {
    private final ReactiveAuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        setServerAuthenticationConverter(new JwtAuthenticationConverter());
        setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/secure/**"));
    }

    private static class JwtAuthenticationConverter implements ServerAuthenticationConverter {
        @Override
        public Mono<Authentication> convert(ServerWebExchange exchange) {
            String token = extractTokenFromHeader(exchange.getRequest().getHeaders().getFirst("Authorization"));
            if (token != null && JwtUtil.validateToken(token)) {
                String username = JwtUtil.extractUsername(token);
                UserDetails userDetails = User.withUsername(username).roles("USER").build();
                return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities()));
            }
            return Mono.empty();
        }

        private String extractTokenFromHeader(String header) {
            if (header != null && header.startsWith("Bearer ")) {
                return header.substring(7);
            }
            return null;
        }
    }
}
