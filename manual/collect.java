import java.util.Map;

public class Collect {
    public static void collect(String[] args) {
        import java.util.*;

        List<String> names = new ArrayList<>();
        names.add("Anna");
        names.add("Ivan");
        
        Map<String, Integer> ages = new HashMap<>();
        ages.put("Anna", 30);
        int annaAge = ages.get("Anna");

        Set<String> uniqueNames = new HashSet<>();
        uniqueNames.add("Anna");
    }
}