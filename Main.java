import java.util.List;
import java.util.Scanner;

/**
 * VelociTrack -- Console Driver
 *
 * This menu covers the WHOLE semester, not just the project you're
 * currently on. Options for projects you haven't built yet will print a
 * "coming soon" message -- that's expected! As you complete each project,
 * come back to this file and replace the matching TODO with a real call
 * into your own code.
 *
 * You WILL edit this file throughout the semester. That's the point.
 */
public class Main {
    //This is the main way we are "testing" the program by making a bunch of semi-random songs, audiobooks, and podcasts
    //If you want to add in your own sounds, you can make a private static Song with proper information
    private static List<RawMediaRecord> sampleData = null;
    private static MediaLibrary library = new MediaLibrary();
    private static Playlist myPlaylist = new Playlist("My Cool Mix");
    private static PlaybackManager player = new PlaybackManager();

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        addFourSongsToPlaylist();
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Enter choice: ");
            //Decided to use a string instead of a number.
            //Doesn't need a try/catch to validate input is an int
            String choice = input.nextLine().trim(); //remove white space
            //When adding features, you can remove the comingSoon function call
            switch (choice) {
                case "1":
                    loadSampleData(input);
                    //add them to library
                    break;
                case "2":
                    // TODO (Project 1): print every item in your MediaLibrary
                    List<Playable> tempList = library.getAll();
                    if(tempList.isEmpty()){
                        System.out.println("Sorry no things! Please add things!");
                    } else {
                        System.out.println("Item Number: "+tempList.size());
                        for(Playable audio : tempList){
                            System.out.println("Title: "+audio.getTitle());
                            System.out.println("Creator: "+audio.getCreator());
                            System.out.println("------------------------------------");
                        }
                    }
                    break;
                case "3":
                    // TODO (Project 1): call your MediaLibrary's linearSearch(String title)
                    System.out.print("Enter title to find: ");
                    String searchTitle = input.nextLine().trim();
                    library.linearSearch(searchTitle);
                    break;
                case "4":
                    //decided to use a submenu for this feature
                    playlistMenu(input);
                    break;
                case "5":
                    // TODO (Project 2): show Recently Played (stack) and Up Next (queue)
                    playbackMenu(input);
                    break;
                case "6":
                    // TODO (Project 3): call your HashMap-based fast search
                    comingSoon("Project 3", "call your hashSearch(String title) method here.");
                    break;
                case "7":
                    // TODO (Project 3): show Liked Songs (Set) and genre tags (Map)
                    comingSoon("Project 3", "wire up your Liked Songs and genre tagging here.");
                    break;
                case "8":
                    // TODO (Project 4): sort the library by a chosen key
                    comingSoon("Project 4", "call your hand-written sort method here.");
                    break;
                case "9":
                    // TODO (Project 4): browse the BST in order
                    comingSoon("Project 4", "build your BST, then call its in-order traversal here.");
                    break;
                case "10":
                    // TODO (Project 5): show the heap-based Top 10 Trending list
                    comingSoon("Project 5", "call your getTrendingTop10() method here.");
                    break;
                case "11":
                    // TODO (Project 5): show BFS/DFS-based recommendations
                    comingSoon("Project 5", "call your getRecommendations(Playable item) method here.");
                    break;
                case "12":
                    loadRealSongs(input);
                    break;
                case "0":
                    running = false;
                    System.out.println("Thanks for using VelociTrack!");
                    break;
                default:
                    System.out.println("Not a valid option -- try again.\n");
            }
        }
        input.close();
    }
    
    private static void printMenu() {
        System.out.println("\n===== VelociTrack =====");
        System.out.println(" 1. Load Sample Data");
        System.out.println(" 2. View All Media");
        System.out.println(" 3. Search by Title (Linear Search)");
        System.out.println(" 4. View Playlist");
        System.out.println(" 5. Recently Played / Up Next");
        System.out.println(" 6. Fast Search (Hash Lookup)");
        System.out.println(" 7. Liked Songs & Genre Tags");
        System.out.println(" 8. Sort Library");
        System.out.println(" 9. Browse by Genre / Decade (BST)");
        System.out.println("10. Trending Top 10");
        System.out.println("11. Recommendations");
        System.out.println("12. Load Real Songs");
        System.out.println(" 0. Exit");
    }
    private static void addFourSongsToPlaylist(){
        Artist tlc = new Artist("TLC","Don't go chasing waterfalls.");
        Song song1 = new Song("i1", "Ain't 2 Proud 2 Beg", 337 ,"R&B", "09/23/2026", 1991, tlc, "Ooooooohhh... On the TLC Tip");
        Song song2 = new Song("i2","Waterfalls",228,"R&B","09/23/2026",1995, tlc, "CrazySexyCool");
        Song song3 = new Song("i3","No Scrubs",219,"R&B","09/23/2026",1999, tlc, "FanMail");
        Song song4 = new Song("i4","Creep",268,"R&B","09/23/2026",1999, tlc, "CrazySexyCool");
        myPlaylist.addSong(song1);
        myPlaylist.addSong(song2);
        myPlaylist.addSong(song3);
        myPlaylist.addSong(song4);
    }
    private static void playbackMenu(Scanner input){
        System.out.println("\n------Playback-------");
        Playable current = player.getNowPlaying();
        boolean back = false;
        while(!back){
            if(current == null){
                System.out.println("Now Playing: (nothing)");
            } else {
                System.out.printf("Now Playing: (%s)%n",current.getTitle());
            }
            System.out.println("1. Play Next");
            System.out.println("2. Go Back");
            System.out.println("3. Show Up Next");
            System.out.println("4. Show Recently Played");
            System.out.println("5. Add playlist to Queue");
            System.out.println("0. Back");
            System.out.print("Enter a choice: ");
            String choice = input.nextLine().trim();
            switch(choice){
                case "1" -> player.playNext();
                case "2" -> player.goBack();
                case "3" -> player.showUpNext();
                case "4"-> player.showRecentlyPlayed();
                case "5" -> player.addPlaylistToQueue(myPlaylist);
                case "0" -> back = true;
                default -> System.out.println("Invalid Choice");
            }
        }
    }
    private static void playlistMenu(Scanner input) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- " + myPlaylist.getName() + " ---");
            System.out.println("1. View playlist");
            System.out.println("2. Remove by title");
            System.out.println("3. Reorder");
            System.out.println("0. Back");
            System.out.print("Enter a choice: ");
            String choice = input.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.println(myPlaylist);
                    break;
                case "2":
                    System.out.print("Title: ");
                    boolean removed = myPlaylist.removeSong(input.nextLine().trim());
                    System.out.println(removed ? "Removed." : "Not in playlist.");
                    break;
                case "3":
                    System.out.println(myPlaylist);
                    int from;
                    int to;
                    try{
                        System.out.print("Move position #: ");
                        from = Integer.parseInt(input.nextLine()) - 1;   // menu shows 1-based
                        System.out.print("To position #: ");
                        to = Integer.parseInt(input.nextLine()) - 1;
                    } catch(NumberFormatException e){
                        System.out.println("Positions must be numbers. Restarting submenu...");
                        break;
                    }
                    myPlaylist.reorder(from, to);
                    System.out.println(myPlaylist);
                    break;
                case "0":
                    back = true;
                    break;
                default: 
                    System.out.println("Invalid choice.");
            }
        }
    }
    private static void loadSampleData(Scanner input) {
        System.out.print("How many items would you like to generate? ");
        int count;
        try {
            count = Integer.parseInt(input.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Not a valid number -- generating 500 by default.");
            count = 500;
        }
        sampleData = SampleDataGenerator.generate(count);
        System.out.println("Generated " + sampleData.size() + " sample records.");
        System.out.println("Example: " + sampleData.get(0));
        for(RawMediaRecord r : sampleData){
            if(r.type.equals("SONG")){
                Artist artist = new Artist(r.creator, "This artist plays "+r.genre+" and has at least one song in the year "+r.releaseYear+".");
                String album = "Sgt. Pepper's Lonely Hearts Club Band";
                Song song = new Song(r.id, r.title, r.durationSeconds, r.genre, r.dateAdded, r.releaseYear, artist, album);
                library.add(song);
            } 
            //add podcasts
            //add audiobook
        }
        System.out.println("List amount: "+library.size());
        System.out.println("HashMap Amount: "+library.hashSize());
    }
    private static void loadRealSongs(Scanner input){
        sampleData = SampleDataGenerator.generate(1);
        sampleData.clear();
        sampleData = SampleDataGenerator.getActualSongs();
        System.out.println("Real Songs Loaded.");
        System.out.println("Example: " + sampleData.get(0));
        System.out.println("This will now let you check if your play() features are working.");
    }
    private static void comingSoon(String project, String hint) {
        System.out.println("\nComing in " + project + " " + hint);
    }
}
