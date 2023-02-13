import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Testing {
    /**
     * This class acts as a testing class which can run the main class, CS1003P2. There are 6 tests in this
     * class which are all explained and used in my practical report.
     * @param args - not used in this method
     */
    public static void main(String[] args) {
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
        CS1003P2.main(arguments.get(5));
    }
}
