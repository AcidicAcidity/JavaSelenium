public class Main {
    public static void main(String[] args) {
        int age = 25;
        String name = "Ivan";
        double price = 293.192;
        boolean isActive = true;

        System.out.println("Hello, " + name + "! Age: " + age);

        if (age >= 18) {
            System.out.println("Adult");
        } else {
            System.out.println("Minor");
        }

        for (int i = 0; i < 5; i++) {
            System.out.println(i);
        }

        for (String item : List.of("a", "b", "c")) { //Like foreach
            System.out.println(item);
        }

        int i = 0;
        while (i < 3) {
            i++;
        }
    }
}

//Simple syntax manual