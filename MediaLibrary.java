import java.util.ArrayList;
import java.util.List;
import java.lang.IllegalArgumentException;
import java.util.HashMap;
import java.util.Map;

public class MediaLibrary {
    private List<Playable> items = new ArrayList<>();
    private Map<String, Playable> titleIndex = new HashMap<>();

    public MediaLibrary(){
        this.items = new ArrayList<>();
        this.titleIndex = new HashMap<>();
    }
    public void add(Playable item){
        if (item == null){
            throw new IllegalArgumentException("Don't give me nulls!");
        }
        this.items.add(item);
        this.titleIndex.put(key(item.getTitle()), item);
    }
    private String key(String title){
        return title.trim().toLowerCase();
    }
    public void remove(Playable item){
        if(items.contains(item)){
            items.remove(item);
        } else {
            System.out.println("Item not found. Nothing removed. Try again");
        }
        titleIndex.remove(key(item.getTitle()), item);
    }
    public List<Playable> getAll(){
        return new ArrayList<>(items);
    }
    public int size(){
        return items.size();
    }
    public int hashSize(){
        return titleIndex.size();
    }
    public void hashSearch(String searchTitle){
        Playable found = titleIndex.get(key(searchTitle));
        if(found==null){
            System.out.println("Song not found :'(");
        } else{
            System.out.println("Found Title!");
            System.out.println("More info about your song");
            System.out.println("Title: "+found.getTitle());
            System.out.println("Artist: "+found.getCreator());
            System.out.println("Year Released: "+found.getReleaseYear());
        }
    }
    public void linearSearch(String searchTitle){
        if(items.isEmpty()){
            System.out.println("Library empty. Please add sick beats!");
            return;
        }
        for(Playable p : items){
            if(p.getTitle().equalsIgnoreCase(searchTitle)){
                System.out.println("Found Title!");
                System.out.println("More info about your song");
                System.out.println("Title: "+p.getTitle());
                System.out.println("Artist: "+p.getCreator());
                System.out.println("Year Released: "+p.getReleaseYear());
                //p.play();
                return;
            }
        }
        System.out.println("Song never found. :( ");
    }
}