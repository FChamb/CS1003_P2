import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Testing {
    public void main(String[] args) {
        CS1003P2 test = new CS1003P2();
        ArrayList<String[]> arguments = new ArrayList<>();
        arguments.add(new String[]{"--search", "--query", "--cache"});
        File file1 = new File("../cache");
        file1.mkdirs();
        arguments.add(new String[]{"--search", "--query", "--cache", "cache"});
        file1.delete();
        File file2 = new File("cache");
        try {
            file2.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        arguments.add(new String[]{"--search", "author", "--cache", "cache", "--query", "Finnegan Chamberlain"});
        File file3 = new File("cache");
        file3.mkdirs();
        arguments.add(new String[]{"--search", "venue", "--cache", "cache", "--query", "philosophy"});
        arguments.add(new String[]{"--search", "venue", "--cache", "cache", "--query", "philosophy"});
        arguments.add(new String[]{"--search", "author", "--cache", "cache"});
        for (int i = 0; i < 6; i++) {
            test.main(args);
        }
    }
}
