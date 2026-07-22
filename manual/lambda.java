import java.util.Comparator;

public class lambda {
    public static void main(String[] args) {
        Runnable task = () -> System.out.println("Running");

        Comparator<String> byLenght = (a, b) -> a.length() - b.length();

        // wait.until(driver -> driver.findElement(By.id("submit")).isDisplayed());
    }
}
