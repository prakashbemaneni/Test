import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MyController {

    @GetMapping("/public/hello")
    public Mono<String> publicHello() {
        return Mono.just("Hello from public endpoint!");
    }

    @GetMapping("/secure/hello")
    public Mono<String> secureHello() {
        return Mono.just("Hello from secure endpoint!");
    }
}
