import java.text.SimpleDateFormat;
import java.util.Calendar;

class RomKlokke{
    public static void main(String[] args) throws InterruptedException {
        String timeStamp, klokkkeslett, time, minutt, sekund;
        System.out.println();
        System.out.printf(" |%s:%s:%s|\n", center("time",10), center("minutt", 10), center("sekund", 10));
        System.out.printf("  --------------------------------\n");
        while(true){
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            klokkkeslett = timeStamp.split("_")[1];
            time = convert(reformat(klokkkeslett, 0, 2));
            minutt = convert(reformat(klokkkeslett, 2, 4));
            sekund = convert(reformat(klokkkeslett, 4, 6));

            System.out.printf(" |%s:%s:%s|\r", time, minutt, sekund);
            Thread.sleep(1000);
        }
    }

    static int reformat(String s, int start, int end){
        return Integer.parseInt(s.substring(start, end));
    }

    static String convert(int number) {
        return center(RomeConverter.convert(number), 10);
    }


    private static String center(String str, int size) {
        if (str.length() >= size) return str;

        int padding = size - str.length();
        String main = "";

        for (int i = 0; i < padding / 2; i++) main += " ";
        main += str;
        for (int i = 0; i < padding / 2; i++) main += " ";
        if (padding % 2 != 0) main += " ";
        return main;
    }
}
