import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisPatternExample {

    public static Map<String, Map<String, String>> retrieveDataAsMapOfMaps(String pattern) {
        Jedis jedis = new Jedis("localhost", 6379);  // Connect to Redis

        Map<String, Map<String, String>> mapOfMaps = new HashMap<>();

        ScanParams scanParams = new ScanParams().match(pattern);
        String cursor = "0";
        do {
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
            List<String> keys = scanResult.getResult();
            cursor = scanResult.getStringCursor();

            for (String key : keys) {
                Map<String, String> innerMap = jedis.hgetAll(key);  // Retrieve key-value pairs using HGETALL
                mapOfMaps.put(key, innerMap);
            }
        } while (!cursor.equals("0"));

        jedis.close();

        return mapOfMaps;
    }

    public static void main(String[] args) {
        String pattern = "your_pattern_here:*";
        Map<String, Map<String, String>> result = retrieveDataAsMapOfMaps(pattern);
        
        // Print the result
        for (Map.Entry<String, Map<String, String>> entry : result.entrySet()) {
            System.out.println("Key: " + entry.getKey());
            for (Map.Entry<String, String> innerEntry : entry.getValue().entrySet()) {
                System.out.println("  Inner Key: " + innerEntry.getKey() + ", Value: " + innerEntry.getValue());
            }
        }
    }
}
