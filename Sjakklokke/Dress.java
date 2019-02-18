public class Dress {
    static int color;
    public static void main(String[] args) {
        if (args[0] == "TIE") color = 234;
        if (args[0] == "LIGHT") color = 22;
        System.out.println("Dress should be of type: " + parsecolor(color));
    }

    private static String parsecolor(int c) {
        return c > 100 ? "Formal" : "Informal";
    }
}
