package model.parser;/*
  Author: Hisham Maged
  Date : 7/21/2019
  Class Desc : a Sub-class of AbstractXML that defines the behaviour of parsing of RSS feed of XML format hence extending AbstractXML  from local file or UCSG website
*/
import java.io.File;
import java.net.URL;
import java.util.regex.Pattern;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilderFactory;
import model.pojo.EarthQuakeEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
* extends AbstractXML and it's a generic hierhachy so given EarthQuakeEntry which is the POJO that will hold the data of earthQuakes
* */
public class EarthQuakesParser extends AbstractXML<EarthQuakeEntry>{

  // private static final that is used to validate the URL to be of the USGS site and it's lower case
  // because in hierarchy, it converts the url to lower case
  private static final Pattern URL_VALIDATOR = Pattern.compile(".*earthquake.usgs.gov/earthquakes/feed/v1.0/summary.*\\.atom");

  /*
  * default constructor that will use the AbstractXML constructor to get the
  * Correct File format of XML or ATOM files using a SWing File Chooser
  * */
  public EarthQuakesParser() {
    super();
  }

  /*
  * Stage constructor that will use the AbstractXML constructor to get the
  * Correct File format of XML or ATOM files using an FX File Chooser
  * @Param: Stage of the FX Application
  * */
  public EarthQuakesParser(Stage stage) {
    super(stage);
  }
  /*
  * String constructor that will use the AbstractXML constructor to validate
  * the file and make the inputStream using it
  * @Param: String path of the file
  * throws NullPointerException as null check can't be handled here so Exception propagation
  * */
  public EarthQuakesParser(String filePath) throws NullPointerException {
    super(filePath);
  }
  /*
  * File Consturctor that will use the AbstractXML constructor to validate the file
  * to be of correct extension and such and then makes the InputStream object using it
  * @Param: File pointing to file object in memory
  * */
  public EarthQuakesParser(File file) {
    super(file);
  }

  /*
  * URL Constructor that will use the AbstractXML constructor that will use the AbstractDataParser
  * Constructor to validate the URL of given url link using the private Static final Pattern that validates
  * the url to be of specified location
  * @Param: Url holding the data needed
  * */
  public EarthQuakesParser(URL url) {
    super(url, URL_VALIDATOR);
  }

  /*
   * Main method to parse the RSS fEED and should be the first method to be used
   * in the class, uses the inputsource made in initialization and parses it
   * using the public inherited method .getSource(), using Xerces XML Parser
   * Makes a Node element of entry (which is the main Node that holds all the info of each line or input in file)
   * and using that Entry node, gets the title,location,depth,Age using their respected tag names in the RSS feed file
   * and makes a EarthQuakeEntry Pojo Object using these data
   * putting each one in a List of entries that is inhertied from hierarchy using the inherited
   * using the method .addPojo(T pojo), T in that case is EarthQuakeEntry because we extend the AbstractXML<EarthQuakeEntry>
   * if null is sent to it, IllegalArgumentException will be thrown.
   * */
  @Override
  public boolean parse()
  {
    try {
      System.out.println("Using InputStream: "+this.getSource().toString());
      Document xmlFile = DocumentBuilderFactory.newInstance() // making a document object for XML file and parsing it
          .newDocumentBuilder().parse(this.getSource());
      xmlFile.getDocumentElement().normalize(); // removing white spaces and such
      NodeList entries = xmlFile.getElementsByTagName("entry"); // gets Node List of Entry tags where each entry should be made into a POJO object as it holds the earthquake data
      Node tempEntry = null;
      Element tempElement = null;
      // main loop for filling pojo objects
      for (int i = 0, n = entries.getLength(); i < n; ++i) {
        tempEntry = entries.item(i);
        if (tempEntry.getNodeType() == Node.ELEMENT_NODE) {
          tempElement = (Element) tempEntry;
          this.addPojo(new EarthQuakeEntry(
              tempElement.getElementsByTagName("title").item(0).getTextContent(), // gets Title of earthQuake as it's in title tag
              tempElement.getElementsByTagName("georss:point").item(0).getTextContent(), // gets location of earthQuake as it's in georss:point tag
              tempElement.getElementsByTagName("georss:elev").item(0).getTextContent(), //// gets Depth of earthQuake as it's in georess:elev tag
              ((Element) tempElement.getElementsByTagName("category").item(0)).getAttribute("term") // gets value of term attribute which holds the age of earthquake in Category Tag
          ));
        }
      }
      return true; // returns true if loop is finished meaning no exceptions happened
    }catch(Exception ex)
    {
      System.err.println(ex.getMessage());
    }
    return false; // returns false if an exception occured
  }
}
