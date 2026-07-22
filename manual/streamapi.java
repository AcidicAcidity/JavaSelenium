import java.util.*;
import java.util.stream.Collectors;

public class streamapi {
   public static void main(String[] args) {
        List<String> names = List.of("Anna", "Ivan", "Petr", "Alex");

        List<String> longNames = names.stream()
                .filter(n -> n.length() > 3)
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());

        long count = names.stream().filter(n -> n.startsWith("A")).count();

        boolean anyMatch = names.stream().anyMatch(n -> n.equals("Ivan"));


        System.out.println(count);
        System.out.println(anyMatch);

        for (String item : longNames) {
            System.out.println(item);
        }
   } 
}
