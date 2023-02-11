import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The CS1003P2 class contains three private attributes, arguments, url, and encodedURL. The hashmap arguments
 * contains all three of the command line functions. The url contains a string which has the default link to the api.
 * This is modified based on a search input. The encodedURL is blank by default and is set to the proper encoded url
 * one the url variable has been updated.
 */
public class CS1003P2 {
    private HashMap<String, String> arguments = new HashMap<>();
    private String url = "https://dblp.org/search/~/api?format=xml&c=0&h=40&q=";
    private String encodedURL = "";

    /**
     * The main method acts as a starting point for this program. There are three checks to make sure that
     * the user inputted all the required command line arguments. Should one of the arguments not be present,
     * a correctly worded message is returned. Should all the checks pass, an instance of CS1003P2 is created and
     * findArguments/search are both called.
     * @param args - the command line arguments presented to the program by the user
     */
    public static void main(String[] args) {
        if (!Arrays.toString(args).contains("--search")) {
            System.out.println("Missing value for --search");
            System.out.println("Malformed command line arguments.");
            System.exit(1);
        } else if (!Arrays.toString(args).contains("--cache")) {
            System.out.println("Missing value for --cache");
            System.out.println("Malformed command line arguments.");
            System.exit(1);
        } else if (!Arrays.toString(args).contains("--query")) {
            System.out.println("Missing value for --query");
            System.out.println("Malformed command line arguments.");
            System.exit(1);
        }
        CS1003P2 check = new CS1003P2();
        check.findArguments(args);
        check.search();
    }

    /**
     * Find arguments takes the user's command line arguments as input. A for loop cycles through the array,
     * and there are three checks. Each of the checks see if a specific value equals "--search", "--query",
     * or "--cache". For search and cache
     * @param args
     */
    public void findArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String input = args[i];
            if (input.equals("--search")) {
                if (args.length == i + 1) {
                    System.out.println("Missing value for --search");
                    System.out.println("Malformed command line arguments.");
                    System.exit(1);
                }
                this.arguments.put("search", args[i + 1]);
            } else if (input.equals("--query")) {
                if (args.length == i + 1) {
                    System.out.println("Missing value for --query");
                    System.out.println("Malformed command line arguments.");
                    System.exit(1);
                }
                if (args[i + 1].split(" ").length > 1) {
                    this.arguments.put("query", args[i + 1].replace(" ", "+"));
                } else {
                    this.arguments.put("query", args[i + 1]);
                }
            } else if (input.equals("--cache")) {
                if (args.length == i + 1) {
                    System.out.println("Missing value for --cache");
                    System.out.println("Malformed command line arguments.");
                    System.exit(1);
                }
                this.arguments.put("cache", args[i + 1]);
            }
        }
    }

    public void search() {
        String query = this.arguments.get("query");
        InputStream stream = null;
        if (this.arguments.get("search").equals("author")) {
            this.url = this.url.replace("~", "author") + query;
            this.encodedURL = URLEncoder.encode(this.url, StandardCharsets.UTF_8);
            callToAuthorAPI();
        } else if (this.arguments.get("search").equals("publication")) {
            this.url = this.url.replace("~", "publ") + query;
            this.encodedURL = URLEncoder.encode(this.url, StandardCharsets.UTF_8);
            callToPublAPI();
        } else if (this.arguments.get("search").equals("venue")) {
            this.url = this.url.replace("~", "venue") + query;
            this.encodedURL = URLEncoder.encode(this.url, StandardCharsets.UTF_8);
            callToVenueAPI();
        } else {
            System.out.println("Invalid value for --search: " + this.arguments.get("search"));
            System.out.println("Malformed command line arguments.");
            System.exit(1);
        }
        if (!checkDirectory()) {
            System.out.println("Cache directory doesn't exist: " + this.arguments.get("cache"));
            System.exit(1);
        }
    }

    public boolean checkDirectory() {
        String path = this.arguments.get("cache");
        File directory = new File(path);
        return directory.isDirectory();
    }

    public boolean checkCache() {
        String path = this.arguments.get("cache") + "/" + this.encodedURL;
        File file = new File(path);
        return file.exists();
    }

    public void callToAuthorAPI() {
        try {
            String author = "";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = null;
            if (checkCache()) {
                File file = new File(this.arguments.get("cache") + "/" + this.encodedURL);
                document = builder.parse(file);
            } else {
                FileOutputStream outputStream = new FileOutputStream(this.arguments.get("cache") + "/" + this.encodedURL);
                document = builder.parse(new URL(this.url).openStream());
                writeXMLtoCache(document, outputStream);
            }
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("hit");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node authorName = nodeList.item(i);
                if (authorName.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) authorName;
                    author = element.getElementsByTagName("author").item(0).getTextContent();
                    System.out.print(author);
                    URL newURL = new URL(element.getElementsByTagName("url").item(0).getTextContent() + ".xml");
                    callToCoAuthors(newURL);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void callToCoAuthors(URL url) {
        try {
            int publications = 0;
            int coAuthors = 0;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("r");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node venueName = nodeList.item(i);
                if (venueName.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) venueName;
                    publications += element.getElementsByTagName("title").getLength();
                }
            }
            coAuthors = document.getElementsByTagName("co").getLength();
            System.out.println(" - " + publications + " publications with " + coAuthors + " co-authors.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void callToPublAPI() {
        try {
            String title = "";
            int authors = 0;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = null;
            if (checkCache()) {
                File file = new File(this.arguments.get("cache") + "/" + this.encodedURL);
                document = builder.parse(file);
            } else {
                FileOutputStream outputStream = new FileOutputStream(this.arguments.get("cache") + "/" + this.encodedURL);
                document = builder.parse(new URL(this.url).openStream());
                writeXMLtoCache(document, outputStream);
            }
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("hit");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node publName = nodeList.item(i);
                if (publName.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) publName;
                    authors = element.getElementsByTagName("author").getLength();
                    title = element.getElementsByTagName("title").item(0).getTextContent();
                    System.out.println(title + " (number of authors: " + authors + ")");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void callToVenueAPI() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = null;
            if (checkCache()) {
                File file = new File(this.arguments.get("cache") + "/" + this.encodedURL);
                document = builder.parse(file);
            } else {
                FileOutputStream outputStream = new FileOutputStream(this.arguments.get("cache") + "/" + this.encodedURL);
                document = builder.parse(new URL(this.url).openStream());
                writeXMLtoCache(document, outputStream);
            }
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("hit");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node venueName = nodeList.item(i);
                if (venueName.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) venueName;
                    System.out.println(element.getElementsByTagName("venue").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void writeXMLtoCache(Document document, OutputStream output) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.getMessage();
        } catch (TransformerException e) {
            e.getMessage();
        }
    }
}
