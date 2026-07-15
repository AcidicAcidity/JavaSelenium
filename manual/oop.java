public class Oop {
    public static void oop(String[] args) {
        public  interface Animal {
            void makeSound();
            default void sleep() {
                System.out.println("Zzzz");
            }
        }

        public class Dog implements Animal {
            private String name;

            public Dog(String name) {
                this.name = name;
            }

            @Override
            public void makeSound() {
                System.out.println(name + " says Woof");
            }
        }

        public class Puppy extends Dog {
            public Puppy(String name) {
                super(name);
            }
        }
    }
}