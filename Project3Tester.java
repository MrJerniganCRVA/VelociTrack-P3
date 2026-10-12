import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * VelociTrack -- Project 3 Tester
 *
 * HOW TO USE:
 * This testing class ensures all requirements are met. No menu, no typing --
 * it builds its own libraries, pokes at your Project 3 methods, and prints
 * a report card at the end.
 *
 *   [PASS]  your code did what the directions ask for
 *   [FAIL]  something is broken -- read the hint underneath it
 *   [WARN]  not technically required, but it will bite you later
 *   [INFO]  just something worth knowing (and maybe writing about)
 *
 * This tester ONLY uses Song (not Podcast or Audiobook) so it compiles no
 * matter how you built your other media types, this might be something to check. 
 *
 * Methods this file expects to find in your MediaLibrary:
 *   public Playable hashSearch(String title)
 *   public void likeSong(Playable song)
 *   public boolean isLiked(Playable song)
 *   public void tagItem(String genre, Playable item)
 *   public List<Playable> getByGenre(String genre)
 *   linearSearch(String title)   <- still here from Project 1!
 *
 * If this file won't compile, the error is telling you which of those
 * methods is missing or has the wrong name/parameters. Fix that first.
 *
 * You do NOT need to modify this file.
 */
public class Project3Tester {

    private static int passed = 0;
    private static int failed = 0;
    private static int warnings = 0;

    public static void main(String[] args) {
        System.out.print("\u000C"); //clears screen in BlueJ
        System.out.println("=============================================");
        System.out.println("  VelociTrack -- Project 3 Tester");
        System.out.println("=============================================");

        //NEW SYNTAX
        // Project3Tester::testHashSearch is a method reference
        // It isn't saying "run this method now", instead it is saying "here is the method to test"
        // Then a different part of the code (runSafely for example) actually runs the method reference
        section("1. HashMap Fast Search");
        runSafely("hashSearch tests", Project3Tester::testHashSearch);


        //Notice this has a similar structure but passes a different method! 
        section("2. Liked Songs (HashSet)");
        runSafely("liked songs tests", Project3Tester::testLikedSongs);

        section("3. Genre Grouping (Map)");
        runSafely("genre tests", Project3Tester::testGenres);

        section("4. Which data structures did you use?");
        runSafely("field check", Project3Tester::checkFields);

        section("5. Benchmark: linearSearch vs hashSearch");
        runSafely("benchmark", Project3Tester::runBenchmark);

        printSummary();
    }

    // 1. hashSearch
    private static void testHashSearch() {
        MediaLibrary library = new MediaLibrary();
        Song drive = makeSong("t-1", "Midnight Drive", "Rock");
        Song skies = makeSong("t-2", "Paper Skies", "Pop");
        Song bloom = makeSong("t-3", "Electric Bloom", "Electronic");
        library.add(drive);
        library.add(skies);
        library.add(bloom);

        // Finding something that exists
        Playable found = library.hashSearch("Paper Skies");
        check(found == skies,
              "hashSearch finds an item that was added with add()",
              "hashSearch(\"Paper Skies\") returned " + describe(found) + ".\n"
            + "  Does your add() method ALSO put the item into your HashMap?\n"
            + "  The map has to be kept up to date every time something is added.");

        check(library.hashSearch("Midnight Drive") == drive
                && library.hashSearch("Electric Bloom") == bloom,
              "hashSearch finds the first AND last items added",
              "One of your first/last items wasn't found. Check add().");

        // Something that doesn't exist
        Playable missing = library.hashSearch("This Song Does Not Exist");
        check(missing == null,
              "hashSearch returns null for a title that isn't in the library",
              "Expected null, got " + describe(missing) + ".");

        // Empty library should not crash
        MediaLibrary empty = new MediaLibrary();
        Playable fromEmpty = empty.hashSearch("Anything");
        check(fromEmpty == null,
              "hashSearch on an EMPTY library returns null (no crash)",
              "Expected null, got " + describe(fromEmpty) + ".");

        // Case: not required, but linearSearch used equalsIgnoreCase
        Playable lower = library.hashSearch("paper skies");
        if (lower == skies) {
            info("hashSearch(\"paper skies\") found it -- your search ignores case.");
        } else {
            info("hashSearch(\"paper skies\") did NOT find \"Paper Skies\".\n"
               + "  Your linearSearch uses equalsIgnoreCase, but HashMap keys are exact.\n"
               + "  Not wrong! But think about how you'd make them behave the same.");
        }

        // remove() should also keep the map in sync
        library.remove(bloom);
        Playable afterRemove = library.hashSearch("Electric Bloom");
        if (afterRemove == null) {
            pass("After remove(), hashSearch no longer finds the removed item");
        } else {
            warn("After remove(), hashSearch STILL finds \"Electric Bloom\".",
                 "Your list and your map are out of sync. Should remove() update the map too?");
        }

        // Duplicate titles -- the teaching moment
        MediaLibrary dupes = new MediaLibrary();
        Song first = makeSong("dup-1", "Radio Silence", "Indie");
        Song second = makeSong("dup-2", "Radio Silence", "Rock");
        dupes.add(first);
        dupes.add(second);
        Playable which = dupes.hashSearch("Radio Silence");
        String whichOne = (which == first) ? "the FIRST one added (dup-1)"
                        : (which == second) ? "the SECOND one added (dup-2)"
                        : describe(which);
        info("Two different songs are both titled \"Radio Silence\".\n"
           + "  hashSearch returned " + whichOne + ".\n"
           + "  What happens to the old value when you put() a key that already exists?\n"
           + "  The sample data has LOTS of repeated titles -- how many songs can your map actually find?");
    }

    // =================================================================
    // 2. Liked songs
    // =================================================================
    private static void testLikedSongs() {
        MediaLibrary library = new MediaLibrary();
        Song liked = makeSong("like-1", "Neon Rain", "Pop");
        Song notLiked = makeSong("like-2", "Glass Horizon", "Indie");
        library.add(liked);
        library.add(notLiked);

        check(!library.isLiked(liked),
              "A brand-new song starts out NOT liked",
              "isLiked() returned true before likeSong() was ever called.");

        library.likeSong(liked);
        check(library.isLiked(liked),
              "isLiked() is true after likeSong()",
              "Called likeSong() and isLiked() still says false.");

        check(!library.isLiked(notLiked),
              "Liking one song doesn't make OTHER songs liked",
              "isLiked() returned true for a song that was never liked.");

        // Tap "like" a bunch of times
        for (int i = 0; i < 25; i++) {
            library.likeSong(liked);
        }
        check(library.isLiked(liked),
              "Liking the same song 25 more times doesn't break anything",
              "After liking repeatedly, isLiked() is false?");

        // Peek inside to prove the Set really has no duplicates
        Set<?> likedSet = findField(library, Set.class);
        if (likedSet != null) {
            check(likedSet.size() == 1,
                  "Your Set holds exactly 1 entry after 26 likes of the same song",
                  "Your Set has " + likedSet.size() + " entries. Duplicates got in!");
        } else {
            warn("Couldn't find a Set field in MediaLibrary to inspect.",
                 "If your liked songs live in another class that's OK -- but the\n"
               + "  directions ask for a HashSet. Make sure you're using one.");
        }

        // A different object that represents the SAME song (same id)
        Song sameIdCopy = makeSong("like-1", "Neon Rain", "Pop");
        if (library.isLiked(sameIdCopy)) {
            pass("A different Song object with the same id counts as liked");
        } else {
            warn("A second Song object with the SAME id (\"like-1\") is NOT considered liked.",
                 "Your Set is comparing object addresses, not songs. Either store the\n"
               + "  song's id in the Set, or override equals() AND hashCode().");
        }

        // equals / hashCode contract
        if (liked.equals(sameIdCopy)) {
            check(liked.hashCode() == sameIdCopy.hashCode(),
                  "equals() is overridden AND hashCode() agrees with it",
                  "equals() says the songs match but hashCode() is different.\n"
                + "  Rule: if a.equals(b), then a.hashCode() MUST == b.hashCode().\n"
                + "  Otherwise HashSet/HashMap will put them in different buckets.");
        } else {
            info("Two Songs with the same id are NOT equal() -- equals() isn't overridden.");
        }
    }

    // =================================================================
    // 3. Genre grouping
    // =================================================================
    private static void testGenres() {
        MediaLibrary library = new MediaLibrary();
        Song rock1 = makeSong("g-1", "Chrome Heart", "Rock");
        Song rock2 = makeSong("g-2", "Slow Burn", "Rock");
        Song rock3 = makeSong("g-3", "Backroad Static", "Rock");
        Song comedy = makeSong("g-4", "Low Fidelity", "Comedy");
        library.add(rock1);
        library.add(rock2);
        library.add(rock3);
        library.add(comedy);

        library.tagItem("Rock", rock1);
        library.tagItem("Rock", rock2);
        library.tagItem("Rock", rock3);
        library.tagItem("Comedy", comedy);

        List<Playable> rock = library.getByGenre("Rock");
        check(rock != null && rock.size() == 3,
              "getByGenre(\"Rock\") returns all 3 Rock items",
              "Expected 3 items, got " + (rock == null ? "null" : rock.size()) + ".\n"
            + "  When a genre already has a list, are you ADDING to it or replacing it?");

        check(rock != null && rock.contains(rock1) && rock.contains(rock2) && rock.contains(rock3),
              "The Rock list contains the right three songs",
              "The Rock list is missing at least one of the songs that was tagged.");

        List<Playable> comedyList = library.getByGenre("Comedy");
        check(comedyList != null && comedyList.size() == 1 && comedyList.contains(comedy),
              "getByGenre(\"Comedy\") returns only the Comedy item",
              "Comedy list was " + listTitles(comedyList) + ".");

        check(rock == null || !rock.contains(comedy),
              "Genres stay separate (Comedy item isn't in Rock)",
              "The Comedy song showed up in the Rock list.");

        // A genre nobody has tagged
        List<Playable> polka = library.getByGenre("Polka");
        if (polka != null && polka.isEmpty()) {
            pass("getByGenre on an unused genre returns an EMPTY list");
        } else if (polka == null) {
            warn("getByGenre(\"Polka\") returned null.",
                 "Anyone who loops over the result will crash with a NullPointerException.\n"
               + "  Returning an empty list is friendlier. (Hint: getOrDefault)");
        } else {
            check(false, "", "getByGenre(\"Polka\") returned items that were never tagged Polka: "
                           + listTitles(polka));
        }

        // An item can live in more than one group
        library.tagItem("Favorites", rock1);
        List<Playable> favs = library.getByGenre("Favorites");
        List<Playable> rockAgain = library.getByGenre("Rock");
        check(favs != null && favs.contains(rock1) && rockAgain != null && rockAgain.contains(rock1),
              "One item can be tagged with two different tags",
              "Tagging Chrome Heart as Favorites removed it from Rock (or didn't add it).");

        // Tagging the same item twice
        library.tagItem("Comedy", comedy);
        List<Playable> comedyTwice = library.getByGenre("Comedy");
        int comedySize = (comedyTwice == null) ? -1 : comedyTwice.size();
        info("Tagged the same Comedy item a 2nd time -> Comedy list now has " + comedySize + " item(s).\n"
           + "  Should a List allow that duplicate? Your Liked Songs Set wouldn't...");
    }

    // =================================================================
    // 4. Which structures are in MediaLibrary?
    // =================================================================
    private static void checkFields() {
        boolean foundHashMap = false;
        boolean foundHashSet = false;
        boolean foundOtherMap = false;
        MediaLibrary library = new MediaLibrary();
        for (Field f : MediaLibrary.class.getDeclaredFields()) {
            f.setAccessible(true);
            Object value;
            try {
                value = f.get(library);
            } catch (IllegalAccessException e) {
                continue;
            }
            String type = (value == null) ? f.getType().getSimpleName() + " (null!)"
                                          : value.getClass().getSimpleName();
            info("Field \"" + f.getName() + "\" is a " + type);
            if (value instanceof java.util.HashMap) {
                if (foundHashMap) {
                    foundOtherMap = true;
                }
                foundHashMap = true;
            } else if (value instanceof Map) {
                foundOtherMap = true;
            }
            if (value instanceof java.util.HashSet) {
                foundHashSet = true;
            }
        }
        check(foundHashMap,
              "MediaLibrary has a HashMap (for title search)",
              "No HashMap found in MediaLibrary. Is it declared but never created with new?");
        check(foundHashSet,
              "MediaLibrary has a HashSet (for liked songs)",
              "No HashSet found in MediaLibrary.");
        check(foundOtherMap,
              "MediaLibrary has a second Map (for genres)",
              "Only found one Map. Genre grouping needs its own Map<String, List<Playable>>.");
    }

    // =================================================================
    // 5. Benchmark
    // =================================================================
    private static void runBenchmark() {
        int[] sizes = {1_000, 10_000, 100_000};
        int lookups = 200;

        System.out.println("  Each library is filled with songs titled \"Track 0\", \"Track 1\", ...");
        System.out.println("  We search " + lookups + " times for the LAST song added (worst case for linear)");
        System.out.println("  and report the average time per search.");
        System.out.println("  (linearSearch's printing is hidden while it's being timed.)\n");
        System.out.printf("  %-12s %-20s %-20s %-10s%n", "Items", "linearSearch", "hashSearch", "Linear/Hash");
        System.out.println("  " + "-".repeat(64));

        // Warm-up: Java speeds code up after it runs a few times, so we
        // run a small test first that doesn't count.
        timeBoth(buildBigLibrary(1_000), "Track 999", 200);

        for (int size : sizes) {
            MediaLibrary library = buildBigLibrary(size);
            String target = "Track " + (size - 1);

            // Sanity check before trusting any numbers
            if (library.hashSearch(target) == null) {
                check(false, "", "hashSearch couldn't find \"" + target + "\" in a " + size
                               + "-item library, so the benchmark would be meaningless.");
                return;
            }

            long[] times = timeBoth(library, target, lookups);
            double ratio = (times[1] == 0) ? 0 : (double) times[0] / times[1];
            System.out.printf("  %-12s %-20s %-20s %-10s%n",
                    String.format("%,d", size),
                    formatNanos(times[0]),
                    formatNanos(times[1]),
                    String.format("%.0fx", ratio));
        }

        System.out.println();
        info("Questions for your write-up (put the answers in YOUR words):\n"
           + "  - When the library got 10x bigger, what happened to each column?\n"
           + "  - Which column matches O(n)? Which matches O(1)? How can you tell from the numbers?\n"
           + "  - Run this tester two or three times. Do the numbers change? Why might that be?");
    }

    /** @return {average linear nanos, average hash nanos} */
    private static long[] timeBoth(MediaLibrary library, String target, int lookups) {
        PrintStream realOut = System.out;
        long linearTotal;
        long hashTotal;
        try {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));

            long start = System.nanoTime();
            for (int i = 0; i < lookups; i++) {
                library.linearSearch(target);
            }
            linearTotal = System.nanoTime() - start;

            start = System.nanoTime();
            for (int i = 0; i < lookups; i++) {
                library.hashSearch(target);
            }
            hashTotal = System.nanoTime() - start;
        } finally {
            System.setOut(realOut);
        }
        return new long[] {linearTotal / lookups, hashTotal / lookups};
    }

    private static MediaLibrary buildBigLibrary(int size) {
        MediaLibrary library = new MediaLibrary();
        PrintStream realOut = System.out;
        try {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
            for (int i = 0; i < size; i++) {
                library.add(makeSong("bench-" + i, "Track " + i, "Rock"));
            }
        } finally {
            System.setOut(realOut);
        }
        return library;
    }

    private static String formatNanos(long nanos) {
        if (nanos >= 1_000_000) {
            return String.format("%.2f ms", nanos / 1_000_000.0);
        }
        if (nanos >= 1_000) {
            return String.format("%.2f us", nanos / 1_000.0);
        }
        return nanos + " ns";
    }

    // Helpers -- the tester's own plumbing. You don't need to read below.
    private static Song makeSong(String id, String title, String genre) {
        Artist artist = new Artist("Tester Band", "Exists only to be tested.");
        return new Song(id, title, 200, genre, "2026-10-12", 2026, artist, "Test Album");
    }

    /** Finds the first field in obj whose value is an instance of type. */
    @SuppressWarnings("unchecked")
    private static <T> T findField(Object obj, Class<T> type) {
        for (Field f : obj.getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                Object value = f.get(obj);
                if (type.isInstance(value)) {
                    return (T) value;
                }
            } catch (Exception e) {
                // skip fields we can't read
            }
        }
        return null;
    }

    /** Runs a group of tests; if your code throws, it's reported instead of crashing the tester. */
    private static void runSafely(String name, Runnable tests) {
        try {
            tests.run();
        } catch (Throwable t) {
            failed++;
            System.out.println("  [FAIL] Your code threw " + t.getClass().getSimpleName()
                    + " during the " + name + ".");
            if (t.getMessage() != null) {
                System.out.println("         Message: " + t.getMessage());
            }
            StackTraceElement[] trace = t.getStackTrace();
            for (StackTraceElement e : trace) {
                if (!e.getClassName().equals("Project3Tester")) {
                    System.out.println("         Look at " + e.getClassName() + "." + e.getMethodName()
                            + "() line " + e.getLineNumber());
                    break;
                }
            }
            System.out.println("         The rest of this section was skipped.");
        }
    }

    private static void check(boolean condition, String passMessage, String failHint) {
        if (condition) {
            pass(passMessage);
        } else {
            failed++;
            System.out.println("  [FAIL] " + (passMessage.isEmpty() ? "" : passMessage));
            System.out.println("         " + failHint.replace("\n", "\n       "));
        }
    }

    private static void pass(String message) {
        passed++;
        System.out.println("  [PASS] " + message);
    }

    private static void warn(String message, String hint) {
        warnings++;
        System.out.println("  [WARN] " + message);
        System.out.println("         " + hint.replace("\n", "\n       "));
    }

    private static void info(String message) {
        System.out.println("  [INFO] " + message.replace("\n", "\n       "));
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("--- " + title + " " + "-".repeat(Math.max(0, 40 - title.length())));
    }

    private static String describe(Playable p) {
        return (p == null) ? "null" : "\"" + p.getTitle() + "\"";
    }

    private static String listTitles(List<Playable> list) {
        if (list == null) {
            return "null";
        }
        List<String> titles = new ArrayList<>();
        for (Playable p : list) {
            titles.add(p.getTitle());
        }
        return titles.toString();
    }

    private static void printSummary() {
        System.out.println();
        System.out.println("=============================================");
        System.out.printf("  %d passed   %d failed   %d warnings%n", passed, failed, warnings);
        System.out.println("=============================================");
        if (failed == 0 && warnings == 0) {
            System.out.println("  Everything checks out. Don't forget your Big-O write-up!");
        } else if (failed == 0) {
            System.out.println("  No failures! Look over the warnings before you submit.");
        } else {
            System.out.println("  Fix the [FAIL]s from the top down -- an early bug can cause later ones.");
        }
    }
}
