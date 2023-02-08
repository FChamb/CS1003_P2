import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class CS1003P2 {
    private HashMap<String, String> arguments = new HashMap<>();

    public static void main(String[] args) {
        if (!Arrays.asList(args).contains("--search")) {
            throw new IllegalArgumentException("Not a valid command line argument!");
        }
        CS1003P2 check = new CS1003P2();
        check.findArguments(args);
        check.search();
    }

    public void findArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String input = args[i];
            if (input.equals("--search")) {
                this.arguments.put("search", args[i + 1]);
            } else if (input.equals("--query")) {
                this.arguments.put("query", args[i + 1]);
            } else if (input.equals("--cache")) {
                this.arguments.put("cache", args[i + 1]);
            }
        }
    }

    public void search() {
        if (arguments.get("search").toLowerCase().equals("author")) {
            URL url = null;
            try {
                url = new URL("https://dblp.org/search/author/api");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            InputStream stream = null;
            try {
                stream = url.openStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Scanner scan = new Scanner(new InputStreamReader(stream));
            while (scan.hasNext()) {
                System.out.println(scan.nextLine());
            }
        } else if (arguments.get("search").toLowerCase().equals("publication")) {
            URL url = null;
            try {
                url = new URL("https://dblp.org/search/publ/api");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            InputStream stream = null;
            try {
                stream = url.openStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Scanner scan = new Scanner(new InputStreamReader(stream));
            while (scan.hasNext()) {
                System.out.println(scan.nextLine());
            }
        } else if (arguments.get("search").toLowerCase().equals("venue")) {
            URL url = null;
            try {
                url = new URL("https://dblp.org/search/venue/api");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            InputStream stream = null;
            try {
                stream = url.openStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Scanner scan = new Scanner(new InputStreamReader(stream));
            while (scan.hasNext()) {
                System.out.println(scan.nextLine());
            }
        }
    }
}
