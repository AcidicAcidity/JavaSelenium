public class oop {
    public static void Oop(String[] args) {
        interface Animal {
            void makeSound();
            default void sleep() {
                System.out.println("Zzzz");
            }
        }

        class Dog implements Animal {
            private String name;

            public Dog(String name) {
                this.name = name;
            }

            @Override
            public void makeSound() {
                System.out.println(name + " says Woof");
            }

            public void sleepTime() {
                sleep();
            }
        }

        class Puppy extends Dog {
            public Puppy(String name) {
                super(name);
                makeSound();
                sleepTime();
            }
        }
    }
}