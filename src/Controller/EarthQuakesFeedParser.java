package Controller;/*
  Author: Hisham Maged
  Date : 7/11/2019
  Project Name : A Parser of RSS Feed Of Earthquakes
*/

import Model.EarthQuakeEntry;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EarthQuakesFeedParser {

  // List holding the PointFeatures that will be used in the Map for EarthQuake Markers
  private List<PointFeature> features;
  // List holding entries of earthQuakes as EarthQuakeEntry Pojo
  private List<EarthQuakeEntry> quakeEntries;

  // initialization of both lists
  {
    this.features = new ArrayList<>();
    this.quakeEntries = new ArrayList<>();
  }

  // default constructor that will prompt the user to choose the ATOM file of choice
  public EarthQuakesFeedParser()
  {
    init(getFile());
  }

  // String constructor that will use the filePath
  public EarthQuakesFeedParser(String filePath)
  {
    // checks if valid ATOM file
    if(!isValid(filePath))
      throw new IllegalArgumentException("Atom files are only used");
    init(new File(filePath));
  }

  // File Constructor that will use the given File
  public EarthQuakesFeedParser(File file)
  {
    init(file);
  }
  // URI constructor to get the file from the internet
  public EarthQuakesFeedParser(URL url)
  {
    init(url);
  }

  // checks if fileName String is a valid atom or xml file
  private boolean isValid(String fileName)
  {
    return fileName != null && fileName.toLowerCase().matches("^.*\\.atom$");
  }

  // init Method that get the parsed values of the XML File
  private void init(File file)
  {
    this.quakeEntries = parseXML(file);
    this.features = parseEntries(this.quakeEntries);
  }

  // URL version
  private void init(URL file)
  {
    this.quakeEntries = parseXML(file);
    this.features = parseEntries(this.quakeEntries);
  }

  // getter of Features for Markers
  public List<PointFeature> getParsedFeatures()
  {
    return this.features;
  }

  // getter of QuakeEntries Pojos
  public List<EarthQuakeEntry> getParsedQuakeEntries()
  {
    return this.quakeEntries;
  }

  /*
  * Parses the Entries from the given RSS Feed file into Features for markers on map
  * making a List of PointFeatures and making a reference to each PointFeature made
  * to put magnitude,depth,title,age properties in the HashMap of the pointFeature as its properties
  * using the EarthQuake Pojo properties
  * Used after the QuakeProperties are loaded inside quakeEntries list field is made
  * */
  private List<PointFeature> parseEntries(List<EarthQuakeEntry> entries)
  {
    List<PointFeature> parsedFeatures = new ArrayList<>(entries.size());
    PointFeature tempPoint = null;
    for(EarthQuakeEntry e : entries)
    {
      parsedFeatures.add((tempPoint = new PointFeature(new Location(e.getLatitude(),e.getLongitude()))));
      tempPoint.putProperty("magnitude",e.getMagnitude());
      int interVal = (int) (e.getDepth()/100);
      tempPoint.putProperty("depth",Math.abs(interVal/10.0));
      tempPoint.putProperty("title",e.getTitle());
      tempPoint.putProperty("age",e.getAge());
    }
    return parsedFeatures;
  }

  /*
  * gets The File using a FileChooser and filters the files into atom,xml extension
  * and if no file was selected it loads the default file as ./data/2.5_week.atom
  * */
  private File getFile()
  {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));
    chooser.setFileFilter(new FileNameExtensionFilter("Atom or XML Files","atom","Atom","ATOM","XML","xml","Xml"));
    chooser.setMultiSelectionEnabled(false);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    return chooser.showOpenDialog(null) == -1 ? new File("./data/2.5_week.atom") : chooser.getSelectedFile();
  }

  /*
  * Main method to parse the RSS fEED and should be the first method to be used
  * in the class, takes the xml file and parses it using Xerces XML Parser
  * Makes a Node element of entry (which is the main Node that holds all the info of each line or input in file)
  * and using that Entry node, gets the id,title,location,depth,Age using their respected tag names in the RSS feed file
  * and makes a EarthQuakeEntry Pojo Object using these data
  * putting each one in a List of QuakeEntries that will be used later in making the features for the markers on the map
  * */
  private List<EarthQuakeEntry> parseXML(File inputXmlFile)
  {
    try {
      System.out.println("Using File: "+inputXmlFile.getName());
      Document xmlFile = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder().parse(inputXmlFile);
      xmlFile.getDocumentElement().normalize();
      NodeList entries = xmlFile.getElementsByTagName("entry");
      Node tempEntry = null;
      Element tempElement = null;
      List<EarthQuakeEntry> quakesInfo = new ArrayList<>();
      for (int i = 0, n = entries.getLength(); i < n; ++i) {
        tempEntry = entries.item(i);
        if (tempEntry.getNodeType() == Node.ELEMENT_NODE) {
          tempElement = (Element) tempEntry;
          quakesInfo.add(new EarthQuakeEntry(
              tempElement.getElementsByTagName("id").item(0).getTextContent(),
              tempElement.getElementsByTagName("title").item(0).getTextContent(),
              tempElement.getElementsByTagName("georss:point").item(0).getTextContent(),
              tempElement.getElementsByTagName("georss:elev").item(0).getTextContent(),
              ((Element) tempElement.getElementsByTagName("category").item(0)).getAttribute("term")
          ));
        }
      }
      return quakesInfo;
    }catch(Exception ex)
    {
      System.err.println(ex.getMessage());
    }
    return new ArrayList<>();
  }
  //URL version
  private List<EarthQuakeEntry> parseXML(URL inputXmlFile)
  {
    try {
      Document xmlFile = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder().parse(inputXmlFile.openStream());
      xmlFile.getDocumentElement().normalize();
      NodeList entries = xmlFile.getElementsByTagName("entry");
      Node tempEntry = null;
      Element tempElement = null;
      List<EarthQuakeEntry> quakesInfo = new ArrayList<>();
      for (int i = 0, n = entries.getLength(); i < n; ++i) {
        tempEntry = entries.item(i);
        if (tempEntry.getNodeType() == Node.ELEMENT_NODE) {
          tempElement = (Element) tempEntry;
          quakesInfo.add(new EarthQuakeEntry(
              tempElement.getElementsByTagName("id").item(0).getTextContent(),
              tempElement.getElementsByTagName("title").item(0).getTextContent(),
              tempElement.getElementsByTagName("georss:point").item(0).getTextContent(),
              tempElement.getElementsByTagName("georss:elev").item(0).getTextContent(),
              ((Element) tempElement.getElementsByTagName("category").item(0)).getAttribute("term")
          ));
        }
      }
      return quakesInfo;
    }catch(Exception ex)
    {
      System.err.println(ex.getMessage());
    }
    return new ArrayList<>();
  }
  /*
  * Made for testing
  * */
  public static void main(String[] args){
    EarthQuakesFeedParser parser = new EarthQuakesFeedParser();
//    parser.parseXML("./data/2.5_week.atom");
//    for (EarthQuakeEntry e : parser.parseXML("./data/2.5_week.atom")) {
//      System.out.println(e);
//    }
  }

}
