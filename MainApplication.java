import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MainApplication.class, args);
        RedisTemplate<String, String> redisTemplate = context.getBean(RedisTemplate.class);

        // Set up RedisKeyValueReader with cache pattern "12345:prakash*"
        RedisKeyValueReader keyValueReader = new RedisKeyValueReader(redisTemplate, "12345:prakash");

        // Get keys and their values matching the pattern
        Map<String, Map<String, String>> keyValues = keyValueReader.getKeysAndValuesMatchingPattern();
        System.out.println(keyValues);
    }
}
