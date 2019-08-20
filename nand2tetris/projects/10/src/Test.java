import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner s = new Scanner("while (count <= 100) { /** some loop */");
        while (s.hasNext()) {
            System.out.println(s.next());
        }
    }
}
