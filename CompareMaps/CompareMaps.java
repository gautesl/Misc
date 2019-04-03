import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.*;

/**
    Compares the time it takes to insert, extract, remove and lookup
    items in HashMap and TreeMap. Note that it matters for the insertion
    times whether the integer is before or after the string in the key.
    Insertion time will be more equal (slower for HashMap) if it is before.

    This program was made to explore the time difference of a selection
    of classes implementing the Map<K,V> interface.

    It has also been an exercise in implementing an argparse system.

    @author Gaute Svanes Lunde
*/
class CompareMaps {
    private final static int DEFAULT = 100000;
    private static int n, seed = 123456789;
    private static boolean verbose;
    private static Map<String, Integer> map1, map2;
    private static Map<String, Map<String, Integer>> maps =
        new HashMap<String, Map<String, Integer>>() {{
            put("concurrentskiplistmap", new ConcurrentSkipListMap<>());
            put("concurrenthashmap", new ConcurrentHashMap<>());
            put("hashmap", new HashMap<>());
            put("hashtable", new Hashtable<>());
            put("linkedhashmap", new LinkedHashMap<>());
            put("treemap", new TreeMap<>());
            put("weakhashmap", new WeakHashMap<>());
        }};

    public static void main(String[] args) {
        parseArgs(args);
        msg("seed = " + seed + "\n");

        Random insert = new Random(seed),
               get = new Random(seed),
               remove = new Random(seed),
               contain = new Random(seed);

        double[] insertTimes = new double[2],
                 getTimes = new double[2],
                 removeTimes = new double[2],
                 containTimes = new double[2];

        msg("Inserting...");
        for (int i = 0; i < n; i++) {
            int val = insert.nextInt();
            String key = "Foo: " + val;
            insertTimes[0] += time(() -> map1.put(key, val));
            insertTimes[1] += time(() -> map2.put(key, val));
        }

        msg("Getting...");
        for (int i = 0; i < n; i++) {
            String key = "Foo: " + get.nextInt();
            getTimes[0] += time(() -> map1.get(key));
            getTimes[1] += time(() -> map2.get(key));
        }

        msg("Removing...");
        for (int i = 0; i < n; i++) {
            String key = "Foo: " + remove.nextInt();
            removeTimes[0] += time(() -> map1.remove(key));
            removeTimes[1] += time(() -> map2.remove(key));
        }

        msg("Containing...");
        for (int i = 0; i < n; i++) {
            int val = contain.nextInt();
            containTimes[0] += time(() -> map1.containsValue(val));
            containTimes[1] += time(() -> map2.containsValue(val));
        }

        printResult("Insertion", insertTimes);
        printResult("Extraction", getTimes);
        printResult("Remove", removeTimes);
        printResult("Contain", containTimes);
    }

    private static void printResult(String title, double[] array) {
        System.out.printf("\n%s times\n%s: %.2fms\n%s: %.2fms\n",
                          title, map1.getClass().getName(), array[0],
                                 map2.getClass().getName(), array[1]);
        System.out.printf("speedup: %.2f\n", array[1] / array[0]);
    }

    private static double time(Function f) {
        long time = System.nanoTime();
        f.call();
        return (System.nanoTime() - time) / 1000000.0;
    }

    private static void msg(String msg) {
        if (verbose) System.out.println(msg);
    }

    private static void parseArgs(String[] a) {
        List<String> args = new ArrayList<>(Arrays.asList(a));

        if (searchOption(args, "-o", true) != null) usage();

        verbose = searchOption(args, "-v", true) != null;

        String mapName = searchOption(args, "-m1", false);
        if (mapName == null) mapName = "treemap";
        map1 = getMap(mapName);

        mapName = searchOption(args, "-m2", false);
        if (mapName == null) mapName = "hashmap";
        map2 = getMap(mapName);

        String userSeed = searchOption(args, "-s", false);

        try {
            if (userSeed != null)
                seed = Integer.parseInt(userSeed);
            n = Integer.parseInt(args.get(0));
        } catch (IndexOutOfBoundsException ioobe) {
            n = DEFAULT;
        } catch (NumberFormatException nfe) {
            usage();
        }

        System.out.printf("Comparing %s to %s with N=%d\n",
                map1.getClass().getName(), map2.getClass().getName(), n);
    }

    private static String searchOption(List<String> args, String option, boolean single) {
        if (!args.contains(option)) return null;

        int index = args.indexOf(option);
        if (!single && args.size() <= index + 1) usage();

        option = args.get(index + (single ? 0 : 1)).toLowerCase();
        args.remove(index);
        if (!single) args.remove(index);

        return option;
    }

    private static Map<String, Integer> getMap(String mapName) {
        if (!maps.containsKey(mapName)) {
            System.out.println(mapName + " is not a supported map");
            System.exit(-1);
        }
        return maps.get(mapName);
    }

    private static void usage() {
        System.out.println("usage: java CompareMaps [n] [options]");
        System.out.println("\nOptions: ");
        System.out.println("    -o              Print out this menu");
        System.out.println("    -v              Verbose");
        System.out.println("    -s <seed>       The seed to generate random numbers");
        System.out.println("    -m1 <map name>  Name of map 1 (defaults to TreeMap)");
        System.out.println("    -m2 <map name>  Name of map 2 (defaults to HashMap)");
        System.out.println("\nAvailable maps are: ConcurrentHashMap, " +
                           "ConcurrentSkipListMap, HashMap, TreeMap, Hashtable, " +
                           "LinkedHashMap and WeakHashMap");
        System.exit(-1);
    }

    interface Function {
        public void call();
    }
}
