public class Test {
    public static void main(String[] args) {
        String test = "@R0 //hello world\n@R1 //hello again";
        System.out.println(test.replaceAll("//.*", ""));
    }
}