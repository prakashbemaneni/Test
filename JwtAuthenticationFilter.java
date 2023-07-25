import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter extends AbstractAuthenticationWebFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(ReactiveAuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> filter(ServerWebExchange exchange, AuthenticationFilterChain chain) {
        String token = extractTokenFromHeader(exchange.getRequest().getHeaders().getFirst("Authorization"));
        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = User.withUsername(username).roles("USER").build();
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
            return getAuthenticationManager().authenticate(authentication);
        } else {
            return chain.filter(exchange);
        }
    }

    private String extractTokenFromHeader(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
