import java.util.*;

class HashTest {

    public static void main(String [] inp) {
        ArrayList<String> fasit = new ArrayList<>();

        Random r = new Random(123456789);
        Hashmap<String, Integer> hm = new Hashmap<>(10000);
        HashMap<String, Integer> map = new HashMap<>();
        List<String> arraylist = new ArrayList<>();
        List<String> list = new LinkedList<>();

        // fyller beholderne
        for (int i = 0; i < 100000; i++) {
            int tall = r.nextInt();
            String streng = "" + tall;
            fasit.add(streng);
            hm.put(streng, tall);
            map.put(streng, tall);
            arraylist.add(streng);
            list.add(streng);
        }

        time(s -> s.equals(arraylist.get(arraylist.indexOf(s))), fasit, "ArrayList");
        time(s -> s.equals(list.get(list.indexOf(s))), fasit, "LinkedList");
        time(s -> s.equals(map.get(s)+""), fasit, "HashMap");
        time(s -> s.equals(hm.get(s)+""), fasit, "Egen Hashmap");
    }

    static void fault() {
        System.out.println("Noe gikk feil");
        System.exit(-1);
    }

    static double timeDiff(long time) {
        return (System.nanoTime() - time) / 1000000.0;
    }

    static void time(Predicate<String> p, List<String> fasit, String text) {
        long time = System.nanoTime();
        for (String s : fasit) {
            if (!p.test(s)) fault();
        }
        System.out.printf("%s: %f\n", text, timeDiff(time));
    }

    interface Predicate<T> {
        public boolean test(T t);
    }
}
