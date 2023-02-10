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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Scanner;

public class CS1003P2 {
    private HashMap<String, String> arguments = new HashMap<>();

    public static void main(String[] args) {
        if (args.length > 6) {
            System.out.println("Too many command line arguments!");
            System.exit(1);
        } else if (args.length < 6) {
            System.out.println("Not enough command line arguments!");
            System.exit(1);
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
        File file = new File(this.arguments.get("cache"));
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void search() {
        String query = arguments.get("query");
        URL url = null;
        InputStream stream = null;
        Scanner scan = null;
        if (arguments.get("search").toLowerCase().equals("author")) {
            try {
                Boolean bool = true;
                url = new URL("https://dblp.org/search/author/api?format=xml&c=0&h=40&q=" + query);
                if (!checkCache(url)) {
                    String cachePath = arguments.get("cache");
                    File file = new File(cachePath + "/" + URLEncoder.encode(String.valueOf(url)));
                    bool = false;
                }
                callToAuthorAPI(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else if (arguments.get("search").toLowerCase().equals("publication")) {
            try {
                Boolean bool = true;
                url = new URL("https://dblp.org/search/publ/api?format=xml&c=0&h=40&q=" + query);
                if (!checkCache(url)) {
                    String cachePath = arguments.get("cache");
                    File file = new File(cachePath + "/" + URLEncoder.encode(String.valueOf(url)));
                    bool = false;
                }
                callToPublAPI(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else if (arguments.get("search").toLowerCase().equals("venue")) {
            try {
                Boolean bool = true;
                url = new URL("https://dblp.org/search/venue/api?format=xml&c=0&h=40&q=" + query);
                if (!checkCache(url)) {
                    String cachePath = arguments.get("cache");
                    File file = new File(cachePath + "/" + URLEncoder.encode(String.valueOf(url)));
                    bool = false;
                }
                callToVenueAPI(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean checkCache(URL url) {
        File file = new File(URLEncoder.encode(String.valueOf(url)));
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public void callToAuthorAPI(URL url) {
        try {
            String author = "";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("hit");
            System.out.println("Names of Authors for " + url.toString().substring(url.toString().indexOf("q=") + 2) + ":");
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

    public void callToPublAPI(URL url) {
        try {
            String title = "";
            int authors = 0;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("hit");
            System.out.println("Names of Publications for " + url.toString().substring(url.toString().indexOf("q=") + 2) + ":");
            for (int i = 0; i < nodeList.getLength(); i++) {
                authors = 0;
                Node publName = nodeList.item(i);
                if (publName.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) publName;
                    authors = element.getElementsByTagName("author").getLength();
                    title = element.getElementsByTagName("title").item(0).getTextContent();
                    System.out.println(title + " - " + authors + " authors.");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void callToVenueAPI(URL url) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("hit");
            System.out.println("Names of Venues for " + url.toString().substring(url.toString().indexOf("q=") + 2) + ":");
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
