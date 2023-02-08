import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
                if (args[i + 1].split(" ").length > 1) {
                    this.arguments.put("query", args[i + 1].replace(" ", "+"));
                } else {
                    this.arguments.put("query", args[i + 1]);
                }
            } else if (input.equals("--cache")) {
                this.arguments.put("cache", args[i + 1]);
            }
        }
    }

    public void search() {
        String query = arguments.get("query");
        URL url = null;
        InputStream stream = null;
        Scanner scan = null;
        if (arguments.get("search").toLowerCase().equals("author")) {
            try {
                url = new URL("https://dblp.org/search/author/api?format=xml&c=0&h=40&q=" + query);
                System.out.println(url);
                stream = url.openStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scan = new Scanner(new InputStreamReader(stream));
            while (scan.hasNext()) {
                System.out.println(scan.nextLine());
            }
        } else if (arguments.get("search").toLowerCase().equals("publication")) {
            try {
                url = new URL("https://dblp.org/search/publ/api?format=xml&c=0&h=40&q=" + query);
                System.out.println(url);
                stream = url.openStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scan = new Scanner(new InputStreamReader(stream));
            while (scan.hasNext()) {
                System.out.println(scan.nextLine());
            }
        } else if (arguments.get("search").toLowerCase().equals("venue")) {
            try {
                url = new URL("https://dblp.org/search/author/api?format=xml&c=0&h=40&q=" + query);
                System.out.println(url);
                stream = url.openStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scan = new Scanner(new InputStreamReader(stream));
            while (scan.hasNext()) {
                System.out.println(scan.nextLine());
            }
        }
    }
}
