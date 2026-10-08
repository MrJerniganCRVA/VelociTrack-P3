import java.util.HashSet;
import java.util.Set;

public class User{
    private String username;
    private Set<Playable> likedSongs = new HashSet<>();

    public User(String username){
        this.username = username;
    }

    public boolean like(Playable p){
        return likedSongs.add(p); 
    }
    public int getLikedCount(){
        return likedSongs.size();
    }

    //TODO return a list of liked songs?
}