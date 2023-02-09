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
                callToAuthorAPI(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else if (arguments.get("search").toLowerCase().equals("publication")) {
            try {
                url = new URL("https://dblp.org/search/publ/api?format=xml&c=0&h=40&q=" + query);
                callToPublAPI(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else if (arguments.get("search").toLowerCase().equals("venue")) {
            try {
                url = new URL("https://dblp.org/search/venue/api?format=xml&c=0&h=40&q=" + query);
                callToVenueAPI(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void callToAuthorAPI(URL url) {
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

    public void callToPublAPI(URL url) {
        InputStream stream = null;
        try {
            stream = url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scanner scan = new Scanner(new InputStreamReader(stream));
        String title = "";
        int authors = 0;
        while (scan.hasNext()) {
            String line = scan.nextLine();

            System.out.println(scan.nextLine());
        }
    }

    public void callToVenueAPI(URL url) {
        InputStream stream = null;
        try {
            stream = url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scanner scan = new Scanner(new InputStreamReader(stream));
        System.out.println("Names of Venues for " + url.toString().substring(url.toString().indexOf("q=") + 2) + ":\n");
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (line.contains("<venue>")) {
                String takenFromLine = line.substring(line.indexOf("e>") + 2, line.indexOf("</"));
                System.out.println(takenFromLine);
            }
        }
    }
}
