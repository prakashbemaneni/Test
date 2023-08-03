import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedisKeyValueReader {
    private final RedisTemplate<String, String> redisTemplate;
    private final String cachePattern;

    public RedisKeyValueReader(RedisTemplate<String, String> redisTemplate, String cachePattern) {
        this.redisTemplate = redisTemplate;
        this.cachePattern = cachePattern;
    }

    public Map<String, Map<String, String>> getKeysAndValuesMatchingPattern() {
        Map<String, Map<String, String>> result = new HashMap<>();
        // Use SCAN command instead of KEYS to retrieve keys that match the pattern
        Set<String> keys = redisTemplate.execute((connection) -> {
            Set<String> matchedKeys = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(cachePattern + "*").build())) {
                while (cursor.hasNext()) {
                    matchedKeys.add(new String(cursor.next()));
                }
            }
            return matchedKeys;
        });

        if (!keys.isEmpty()) {
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            for (String key : keys) {
                Map<String, String> values = hashOps.entries(key);
                result.put(key, values);
            }
        }

        return result;
    }
}
