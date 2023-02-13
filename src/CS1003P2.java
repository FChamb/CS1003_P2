import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
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
    String url = "https://dblp.org/search/~/api?format=xml&c=0&h=40&q=";
    String encodedURL = "";

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
        } else if (args.length > 6) {
            System.out.println("Too many arguments!");
            System.out.println("Malformed command line arguments.");
            System.exit(1);
        }
        CS1003P2 check = new CS1003P2();
        check.findArguments(args);
        check.search();
    }

    /**
     * Find arguments takes the user's command line arguments as input. A for loop cycles through the array,
     * and with three checks. Each check looks for a specific value equaling "--search", "--query",
     * or "--cache". For search and cache if the next index is equal to the array length, and error prints out
     * that there is no value for its flag. If the next index is not equal to the array length, the private hashmap
     * is set with the corresponding values. For query, the same index check exists, but it also checks if the
     * corresponding query input is more than one word. If it is, then a "+" is added in every instance of " ". This
     * is done to match the specifications of the API.
     * @param args - the command line arguments passed to findArguments in the main method
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

    /**
     * Search is the method that sets the appropriate url, checks the cache directory, creates a document
     * builder, decides if the cache contains a search inquiry, and if not calls to the appropriate API
     * search method. If the cache directory does not exist an error message is printed and the program terminates.
     * A try-catch loop creates a Document Build Factory and Builder for reading the xml file. A check then sees
     * if the cache directory contains the search inquiry. If it does, the document is parsed the cached file. If not,
     * the document is parsed an url link to the api and a call to writeXMLtoCache creates an instance of the data in
     * cache.
     */
    public void search() {
        changeURL();
        if (!checkDirectory()) {
            System.out.println("Cache directory doesn't exist: " + this.arguments.get("cache"));
            System.exit(1);
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;
            if (checkCache()) {
                File file = new File(this.arguments.get("cache") + "/" + this.encodedURL);
                document = builder.parse(file);
            } else {
                FileOutputStream outputStream = new FileOutputStream(this.arguments.get("cache") + "/" + this.encodedURL);
                document = builder.parse(new URL(this.url).openStream());
                writeXMLtoCache(document, outputStream);
            }
            if (this.url.contains("/author/")) {
                callToAuthorAPI(document);
            } else if (this.url.contains("/publ/")) {
                callToPublAPI(document);
            } else if (this.url.contains("/venue/")) {
                callToVenueAPI(document);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Change URL grabs the user's query from the hashmap and then looks at three conditional
     * checks. Each method looks to see if the "--search" input equals "author", "publication",
     * or "venue". Then url is properly fit with the correct format, and encodedURL is initiated.
     * If "--search" input does not equal any of these, then the method prints out an appropriate
     * message and exits.
     */
    public void changeURL() {
        String query = this.arguments.get("query");
        if (this.arguments.get("search").equals("author")) {
            this.url = this.url.replace("~", "author") + query;
            this.encodedURL = URLEncoder.encode(this.url, StandardCharsets.UTF_8);
        } else if (this.arguments.get("search").equals("publication")) {
            this.url = this.url.replace("~", "publ") + query;
            this.encodedURL = URLEncoder.encode(this.url, StandardCharsets.UTF_8);
        } else if (this.arguments.get("search").equals("venue")) {
            this.url = this.url.replace("~", "venue") + query;
            this.encodedURL = URLEncoder.encode(this.url, StandardCharsets.UTF_8);
        } else {
            System.out.println("Invalid value for --search: " + this.arguments.get("search"));
            System.out.println("Malformed command line arguments.");
            System.exit(1);
        }
    }

    /**
     * This method checks that the provided cache directory exists and is a directory.
     * @return - returns a boolean value, true if the directory exists, false otherwise
     */
    public boolean checkDirectory() {
        String path = this.arguments.get("cache");
        File directory = new File(path);
        return directory.isDirectory();
    }

    /**
     * This method checks if the current search query has already been called. In other terms,
     * this method ensures that the cache directory contains a file titled the encoded url.
     * @return - returns a boolean value, true if the file exists in the cache, false otherwise
     */
    public boolean checkCache() {
        String path = this.arguments.get("cache") + "/" + this.encodedURL;
        File file = new File(path);
        return file.exists();
    }

    /**
     * One of the instances of looking at a xml file. This particular case retries the data for an author search.
     * A try-catch loop exists as a new URL is created later on. The document is normalized and a list of nodes
     * pertaining to "hit" is found. For every hit, the item is grabbed and checked to see if it is an element
     * node. Then it is cast to an element and the author is retrieved and printed. A newURL is created with the
     * element url under the author and sent to callToCoAuthors().
     * @param document - the xml document given by search for data retrieval
     */
    public void callToAuthorAPI(Document document) {
        try {
            String author = "";
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

    /**
     * This method acts as a subbranch of callToAuthorAPI. It takes an url and encodes the value in a string.
     * Then a new Document Builder Factory and Builder are created for reading a xml file. A check sees if the
     * cache directory contains the coauthor information. If it does, the document is parsed the cached file. If not,
     * the document is parsed an url link to the api and a call to writeXMLtoCache creates an instance of the
     * data in cache. The document is then normalized and a list of nodes pertaining to "r" is found. For every
     * r, the item is grabbed and checked to see if it is an element node. Then it is cast to an element and
     * the number of titles is added to the total publications. At the ends, the number of coauthors is found
     * by looking at how many nodes pertain to "co". This information is printed out and the method ends.
     * @param url - the input url under each individual author for number of publications and co-authors
     */
    public void callToCoAuthors(URL url) {
        try {
            int publications = 0;
            String newEncodedURL = URLEncoder.encode(String.valueOf(url), StandardCharsets.UTF_8);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;
            if (checkCache()) {
                File file = new File(this.arguments.get("cache") + "/" + newEncodedURL);
                document = builder.parse(file);
            } else {
                FileOutputStream outputStream = new FileOutputStream(this.arguments.get("cache") + "/" + newEncodedURL);
                document = builder.parse(url.openStream());
                writeXMLtoCache(document, outputStream);
            }
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("r");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node venueName = nodeList.item(i);
                if (venueName.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) venueName;
                    publications += element.getElementsByTagName("title").getLength();
                }
            }
            int coAuthors = document.getElementsByTagName("co").getLength();
            System.out.println(" - " + publications + " publications with " + coAuthors + " co-authors.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Another instance of looking at a xml file. This particular method retrieves publication data. The document
     * is normalized, and then a list of nodes is created from the tag name "hit". Using a for loop, every hit
     * checks if the node is an element. Then the node is cast to an element and the number of authors/the title
     * of the publication is retrieved. This is printed out to the user.
     * @param document - the xml document given by search for data retrieval
     */
    public void callToPublAPI(Document document) {
        String title = "";
        int authors = 0;
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
    }

    /**
     * The final instance of looking at a xml file. For finding the venue information, the document is first
     * normalized. Then a list of nodes is created from the tag name "hit". Using a for loop, every node
     * checks to see if it is an element. It is then cast to an element and the venue is found and printed out.
     * @param document - the xml document given by search for data retrieval
     */
    public void callToVenueAPI(Document document) {
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("hit");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node venueName = nodeList.item(i);
            if (venueName.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) venueName;
                System.out.println(element.getElementsByTagName("venue").item(0).getTextContent());
            }
        }
    }

    /**
     * Write XML to cache has a try-catch loop to check transformer issues. A new TransformerFactory,
     * Transformer, DOMSource, and StreamResult are created with the source xml and future cache location.
     * The transformer puts the xml data into the new cache file for future reference.
     * @param document - the xml document given by search for reading
     * @param output - the output stream which contains the location of where to put the new cached file
     */
    public void writeXMLtoCache(Document document, OutputStream output) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
        }
    }
}