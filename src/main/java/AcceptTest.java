public class AcceptTest {
    public static int reverse(int x) {
        int rsl = 0;
        while (x != 0) {
            rsl = x % 10 + rsl * 10;
            x = x / 10;
        }
        return rsl;
    }

    public static void main(String[] args) {
        System.out.println(" 123 --> " + reverse(123));
        System.out.println(" -123 --> " + reverse(-123));
        System.out.println(" 100 --> " + reverse(100));
    }
}
