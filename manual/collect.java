import java.util.*;

public class collect {
    public static void Collect(String[] args) {

        List<String> names = new ArrayList<>();
        names.add("Anna");
        names.add("Ivan");
        
        Map<String, Integer> ages = new HashMap<>();
        ages.put("Anna", 30);
        int annaAge = ages.get("Anna");

        Set<String> uniqueNames = new HashSet<>();
        uniqueNames.add("Anna");

        System.out.println(annaAge);
    }
}