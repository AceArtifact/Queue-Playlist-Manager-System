
package queue.music.playlist.system;


public class Songs {

    private String name;
    private String filePath;

    public Songs(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    public String getName() {

        return name;

    }

    public String getFilePath() {

        return filePath;

    }
    
    @Override
    public String toString() {
        return name;
    }

}
