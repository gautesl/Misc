public class RomeConverter {

    static String[] rom = new String[]{"I", "IV", "V", "IX", "X", "XL", "L", "XC","C", "CD", "D", "CM", "M"};
    static int[] verdier = new int[]{1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};

    public static String convert(int tall) {
        if(tall <= 0) return "nulla";
        String romTall = "";
        int i;
        for(i = 0; i<verdier.length; i++){
            if(verdier[i] > tall) break;

        }
        i--;
        while(i>= 0){
            while(tall>=verdier[i]){
                romTall += rom[i];
                tall-=verdier[i];
            }
            i--;
        }
        return romTall;
    }


}
