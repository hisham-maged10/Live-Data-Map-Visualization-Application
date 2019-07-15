package Controller;/*
  Author: Hisham Maged
  Date : 7/11/2019
  Project Name : A Parser of RSS Feed Of Earthquakes
*/

import Model.EarthQuakeEntry;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.function.Predicate;

public class EarthQuakesFeedParser {

  /*
  * A Markup interface made for API Flexibility in calling of EarthQuakesFeedParser filter method
  * */
  public interface EarthQuakeFilter extends Predicate<EarthQuakeEntry> {}

  /* ===============================================  Magnitude Filters Section  =========================================*/
  /*
  * a public static class to Filter magnitude based on an exact value, using BigDecimals comparisons
  * to get exact values of entry and given magnitude and compares them equally using BigDecimal comparison
  * for exact value
  * implements EarthQuakeFilter which is a Markup interface for API Flexibility that implements Predicate<EarthQuakeEntry>
  * and Override the test method to return true if and only if the values exactly match
  * @Constructor_Param: exact magnitude value that you want magnitude to be filtered based on
  * */
  public static class ExactMagnitudeFilter implements EarthQuakeFilter
  {
    private String magnitude;
    public ExactMagnitudeFilter(String magnitude)
    {
      if(magnitude == null)
        throw new IllegalArgumentException("Magnitude can't be null, given value: "+magnitude);
      try {
        if (new BigDecimal(magnitude).compareTo(new BigDecimal("0.0")) < 0)
          throw new IllegalArgumentException("Doesn't accept negative Magnitude value, given value: "+magnitude);

        this.magnitude = magnitude;

      }catch(NumberFormatException ex)
      {
        throw new IllegalArgumentException("Must be numeric input for magnitude, given value: "+magnitude);
      }
      }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return entry.getExactMagnitude().compareTo(new BigDecimal(this.magnitude)) == 0 ;
    }
  }

  /*
   * a public static class made for API Flexibility of filtering of Earth Quakes
   * MagnitudeLessThanFilter is used to Filter the earth quakes to be of magnitude less than
   * given value and it's slightly exact, not exact as the ExactMagnitudeFilter as not needed as much
   * @Ctor Param: double value which is the upper Limit magnitude, if less than 0 gives Illegal ArgumentException
   * @Ctor_Param: boolean value which specifies whether to include upper limit or not
   * */
  public static class MagnitudeLessThanFilter implements EarthQuakeFilter{

    private double upperLimitMagnitude;
    private boolean inclusive;
    public MagnitudeLessThanFilter(double upperLimitMagnitude,boolean inclusive)
    {
      if(Double.compare(upperLimitMagnitude, 0.0) < 0)
        throw new IllegalArgumentException("Magnitude value must be Positive, given value: "+upperLimitMagnitude);
      this.upperLimitMagnitude = upperLimitMagnitude;
      this.inclusive = inclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getMagnitude(),upperLimitMagnitude) <= 0 :
          Double.compare(entry.getMagnitude(),upperLimitMagnitude) < 0;
    }

  }

  /*
   * a public static class made for API Flexibility of filtering of Earth Quakes
   * MagnitudeMoreThanFilter is used to Filter the earth quakes to be of magnitude More than
   * given value and it's slightly exact, not exact as the ExactMagnitudeFilter as not needed as much
   * @Ctor Param: double value which is the lower Limit magnitude, if less than 0 gives Illegal ArgumentException
   * @Ctor_Param: boolean value which specifies whether to include lower limit or not
   * */
  public static class MagnitudeMoreThanFilter implements EarthQuakeFilter{

    private double lowerLimitMagnitude;
    private boolean inclusive;
    public MagnitudeMoreThanFilter(double lowerLimitMagnitude,boolean inclusive)
    {
      if(Double.compare(lowerLimitMagnitude, 0.0) < 0)
        throw new IllegalArgumentException("Magnitude value must be Positive, given value: "+lowerLimitMagnitude);
      this.lowerLimitMagnitude = lowerLimitMagnitude;
      this.inclusive = inclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getMagnitude(),lowerLimitMagnitude) >= 0 :
          Double.compare(entry.getMagnitude(),lowerLimitMagnitude) > 0;
    }

  }

  /*
  * a public static class made for API Flexibility of Filtering Of EarthQuakes using the Filter method
  * MagnitudeRangeFilter is used to Filter the earth quakes to be of magnitude less than a given value
  * and more than another given value making a range amd it uses slightly less exact double comparisons not like the ExactMagmitudeFilter
  * which is extremely exact and has two boolean values for whether to include the upper limit and lower limit or not
  * using a negative value or upperLimit equal to or less than lower limit will result in Illegal Argument Exception
  * @CTOR_PARAM: double lower limit, a lower limit value where magnitudes can't be less than it
  * @CTOR_PARAM: boolean lowerInclusive, a boolean specifing wheter the lowerLimit should be inclusive or not
  * @CTOR_PARAM: double upperLimit, an upper bound limit where magnitudes can't be more than it
  * @CTOR_PARAM : boolean upperInclusve, a boolean specifying whether upperlimit should be inclusive or not
  * */
  public static class MagnitudeRangeFilter implements EarthQuakeFilter
  {
    private double lowerLimit, upperLimit;
    private boolean lowerInclusive, upperInclusive;
    public MagnitudeRangeFilter(double lowerLimit,boolean lowerInclusive, double upperLimit, boolean upperInclusive)
    {
      // no need to test if upperLimit is less than 0 as the lower limit, upperlimit test satisfies it as lower limit must be positive to reach it
      if(Double.compare(lowerLimit,0.0) < 0 || Double.compare(lowerLimit,upperLimit) >= 0)
        throw new IllegalArgumentException("upper and lower limit must be positive and lower limit can't be bigger than or equal to upper limit"+
            "\n if you want to exactly match a Magnitude then use ExactMagnitudeFilter, given Values lowerLimit: "+lowerLimit+", upperLimit: "+upperLimit);

      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
      this.lowerInclusive = lowerInclusive;
      this.upperInclusive = upperInclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      if(lowerInclusive && upperInclusive)
        return Double.compare(entry.getMagnitude(),lowerLimit) >= 0 && Double.compare(entry.getMagnitude(),upperLimit) <= 0;
      else if(lowerInclusive && !upperInclusive)
        return Double.compare(entry.getMagnitude(),lowerLimit) >= 0 && Double.compare(entry.getMagnitude(),upperLimit) < 0;
      else if(!lowerInclusive && upperInclusive)
        return Double.compare(entry.getMagnitude(),lowerLimit) > 0 && Double.compare(entry.getMagnitude(),upperLimit) <= 0;
      else
        return Double.compare(entry.getMagnitude(),lowerLimit) > 0 && Double.compare(entry.getMagnitude(),upperLimit) < 0;
    }
  }


  /* ===============================================  Depth Filters Section  =========================================*/
  /*
   * a public static class to Filter depth based on an exact value, using BigDecimals comparisons
   * to get exact values of entry and given depth and compares them equally using BigDecimal comparison
   * for exact value
   * implements EarthQuakeFilter which is a Markup interface for API Flexibility that implements Predicate<EarthQuakeEntry>
   * and Override the test method to return true if and only if the values exactly match
   * @Constructor_Param: exact depth value that you want depth to be filtered based on, given as a String to exactly match the values
   * */
  public static class ExactDepthFilter implements EarthQuakeFilter
  {
    private String depth;
    public ExactDepthFilter(String depth)
    {
      if(depth == null)
        throw new IllegalArgumentException("depth can't be null, given value: "+depth);
      try {
        if (new BigDecimal(depth).compareTo(new BigDecimal("0.0")) < 0)
          throw new IllegalArgumentException("Doesn't accept negative depth value, given value: "+depth);

        this.depth = depth;

      }catch(NumberFormatException ex)
      {
        throw new IllegalArgumentException("Must be numeric input for depth, given value: "+depth);
      }
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return entry.getExactDepth().compareTo(new BigDecimal(this.depth)) == 0 ;
    }
  }

  /*
   * a public static class made for API Flexibility of filtering of Earth Quakes
   * DepthLessThanFilter is used to Filter the earth quakes to be of depth less than
   * given value and it's slightly exact, not exact as the ExactDepthFilter as not needed as much
   * @Ctor Param: double value which is the upper Limit depth, if less than 0 gives Illegal ArgumentException
   * @Ctor_Param: boolean value which specifies whether to include upper limit or not
   * */
  public static class DepthLessThanFilter implements EarthQuakeFilter{

    private double upperLimitDepth;
    private boolean inclusive;
    public DepthLessThanFilter(double upperLimitDepth,boolean inclusive)
    {
      if(Double.compare(upperLimitDepth, 0.0) < 0)
        throw new IllegalArgumentException("Depth value must be Positive, given value: "+upperLimitDepth);
      this.upperLimitDepth = upperLimitDepth;
      this.inclusive = inclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getDepth(),upperLimitDepth) <= 0 :
          Double.compare(entry.getDepth(),upperLimitDepth) < 0;
    }

  }

  /*
   * a public static class made for API Flexibility of filtering of Earth Quakes
   * DepthMoreThanFilter is used to Filter the earth quakes to be of depth More than
   * given value and it's slightly exact, not exact as the ExactDepthFilter as not needed as much
   * @Ctor Param: double value which is the lower Limit depth, if less than 0 gives Illegal ArgumentException
   * @Ctor_Param: boolean value which specifies whether to include lower limit or not
   * */
  public static class DepthMoreThanFilter implements EarthQuakeFilter{

    private double lowerLimitDepth;
    private boolean inclusive;
    public DepthMoreThanFilter(double lowerLimitDepth,boolean inclusive)
    {
      if(Double.compare(lowerLimitDepth, 0.0) < 0)
        throw new IllegalArgumentException("Depth value must be Positive, given value: "+lowerLimitDepth);
      this.lowerLimitDepth = lowerLimitDepth;
      this.inclusive = inclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getDepth(),lowerLimitDepth) >= 0 :
          Double.compare(entry.getDepth(),lowerLimitDepth) > 0;
    }

  }

  /*
   * a public static class made for API Flexibility of Filtering Of EarthQuakes using the Filter method
   * DepthRangeFilter is used to Filter the earth quakes to be of depth less than a given value
   * and more than another given value making a range amd it uses slightly less exact double comparisons not like the ExactMagmitudeFilter
   * which is extremely exact and has two boolean values for whether to include the upper limit and lower limit or not
   * using a negative value or upperLimit equal to or less than lower limit will result in Illegal Argument Exception
   * @CTOR_PARAM: double lower limit, a lower limit value where depth can't be less than it
   * @CTOR_PARAM: boolean lowerInclusive, a boolean specifing wheter the lowerLimit should be inclusive or not
   * @CTOR_PARAM: double upperLimit, an upper bound limit where depth can't be more than it
   * @CTOR_PARAM : boolean upperInclusve, a boolean specifying whether upperlimit should be inclusive or not
   * */
  public static class DepthRangeFilter implements EarthQuakeFilter
  {
    private double lowerLimit, upperLimit;
    private boolean lowerInclusive, upperInclusive;
    public DepthRangeFilter(double lowerLimit,boolean lowerInclusive, double upperLimit, boolean upperInclusive)
    {
      // no need to test if upperLimit is less than 0 as the lower limit, upperlimit test satisfies it as lower limit must be positive to reach it
      if(Double.compare(lowerLimit,0.0) < 0 || Double.compare(lowerLimit,upperLimit) >= 0)
        throw new IllegalArgumentException("upper and lower limit must be positive and lower limit can't be bigger than or equal to upper limit"+
            "\n if you want to exactly match a Depth then use ExactDepthFilter, given Values lowerLimit: "+lowerLimit+", upperLimit: "+upperLimit);

      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
      this.lowerInclusive = lowerInclusive;
      this.upperInclusive = upperInclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      if(lowerInclusive && upperInclusive)
        return Double.compare(entry.getDepth(),lowerLimit) >= 0 && Double.compare(entry.getDepth(),upperLimit) <= 0;
      else if(lowerInclusive && !upperInclusive)
        return Double.compare(entry.getDepth(),lowerLimit) >= 0 && Double.compare(entry.getDepth(),upperLimit) < 0;
      else if(!lowerInclusive && upperInclusive)
        return Double.compare(entry.getDepth(),lowerLimit) > 0 && Double.compare(entry.getDepth(),upperLimit) <= 0;
      else
        return Double.compare(entry.getDepth(),lowerLimit) > 0 && Double.compare(entry.getDepth(),upperLimit) < 0;
    }
  }




  // List holding entries of earthQuakes as EarthQuakeEntry Pojo
  private List<EarthQuakeEntry> quakeEntries;

  // initialization of both lists so no Null Pointer Exception happens
  {
    this.quakeEntries = new ArrayList<>();
  }

  /*
   * default constructor that will prompt the user to choose the ATOM file of choice
   * using a File chooser
   * */
  public EarthQuakesFeedParser()
  {
    init(getFile());
  }

  /* String constructor version that will use the filePath string to locate the file and make it if valid
  * @ParamL filepath of the XML file as a string
  */
  public EarthQuakesFeedParser(String filePath)
  {
    // checks if valid ATOM file
    if(!isValid(filePath))
      throw new IllegalArgumentException("Atom files are only used");
    init(new File(filePath));
  }

  /*
  * File Constructor that will use the given File to parse RSS feed
  * @Param: File object containing the RSS Feed
  */
  public EarthQuakesFeedParser(File file)
  {
    if(!isValid(file))
      throw new IllegalArgumentException("Atom files are only used");
    init(file);
  }
  /*
  * URL Constructor that will use given URL to get Live RSS Feed data from and is validated by trying to
  * find if it's from the UCGS website or not
  * */
  public EarthQuakesFeedParser(URL url)
  {
    if(!isValid(url))
      throw new IllegalArgumentException("URLS of UCGS can be used only");
    init(url);
  }


  /*
  * A private constructor made for use only in the static method to filter EarthQuakeEntries into point Features
  * filterIntoFeatures method
  * @Param: filtered and validated List<earthQuakeEntry> to make List<PointFeature> of
  *  */
  private EarthQuakesFeedParser(List<EarthQuakeEntry> quakeEntries)
  {
    this.quakeEntries = quakeEntries;
  }

  /*
  * Validation method used to validate whether the filePath is valid or not
  * @param: String fileName which holds the filePath of the file containing the RSS feed
  * */
  private boolean isValid(String fileName)
  {
    return fileName != null && fileName.toLowerCase().matches("^.*\\.atom$");
  }

  /*
   * Validation method used to validate whether the given File object exists and is valid or not
   * @param: File file which holds the object pointing to file on disk containing the RSS feed
   * */
  private boolean isValid(File file)
  {
    return file != null && file.exists() && file.getName().toLowerCase().matches("^.*\\.atom$");
  }

  /*
   * Validation method used to validate whether the given url object is of the UCGS website or not
   * and is valid or not
   * @param: URL object which points to URL containing the RSS feed
   * */
  private boolean isValid(URL url)
  {
    return url != null && isValidURL(url.toString().toLowerCase());
  }
  /*
  * Private helper method made to check if url is of USGS and is of EarthQuake RSS FEED or not
  * made into a seperate method for API Readability
  * */
  private boolean isValidURL(String urlString) {

    return (
        urlString.startsWith("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary")
        ||urlString.startsWith("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary")
        || urlString.startsWith("www.earthquake.usgs.gov/earthquakes/feed/v1.0/summary")
        || urlString.startsWith("https://www.earthquake.usgs.gov/earthquakes/feed/v1.0/summary")
        || urlString.startsWith("http://www.earthquake.usgs.gov/earthquakes/feed/v1.0/summary")
        )
        && urlString.endsWith(".atom");
  }

  /*
  * File version
  * Initialization method that is used to get the POJO records into the private List field using the given Validated File object
  * */
  private void init(File file)
  {
    this.quakeEntries = parseXML(file);
  }

  /*
   * URL version
   * Initialization method that is used to get the POJO records into the private List field using the given Validated URL object
   * */
  private void init(URL file)
  {
    this.quakeEntries = parseXML(file);
  }

  /*
  * used to make and get List<PointFeature> having the locations and mappings of all the loaded earth quake data
  * from the List<Entries> that holds the Pojo objects of all entries of the specified RSS Feed file
  * UnSupportedOperationException is thrown if there is no records loaded into Pojo Objects
  * */
  public List<PointFeature> getParsedFeatures()
  {
    if(this.quakeEntries == null)
      throw new UnsupportedOperationException("Operation can't be done unless there is records loaded from XML file");
    return parseEntries();
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
  * @Param : No params as it's only used for the parsed Entries and this functionality needs to be no arg
  * for the private constructor to process efficiently without need of extra storage and for better design.
  * should be called only using getParsedFeatures() public method both internally and out of the class for better security
  * */
  private List<PointFeature> parseEntries()
  {
    List<PointFeature> parsedFeatures = new ArrayList<>(quakeEntries.size());
    PointFeature tempPoint = null;
    for(EarthQuakeEntry e : quakeEntries)
    {
      parsedFeatures.add((tempPoint = new PointFeature(new Location(e.getLatitude(),e.getLongitude()))));
      tempPoint.putProperty("magnitude",e.getExactMagnitude().doubleValue());
      tempPoint.putProperty("depth",e.getExactDepth().doubleValue());
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
  * @Param: file the contains the RSS feed to be parsed
  * */
  private List<EarthQuakeEntry> parseXML(File inputXmlFile)
  {
    try {
      System.out.println("Using File: "+inputXmlFile.getName());
      Document xmlFile = DocumentBuilderFactory.newInstance() // making a document object for XML file and parsing it
          .newDocumentBuilder().parse(inputXmlFile);
      xmlFile.getDocumentElement().normalize(); // removing white spaces and such
      NodeList entries = xmlFile.getElementsByTagName("entry"); // gets Node List of Entry tags where each entry should be made into a POJO object as it holds the earthquake data
      Node tempEntry = null;
      Element tempElement = null;
      List<EarthQuakeEntry> quakesInfo = new ArrayList<>();
      // main loop for filling pojo objects
      for (int i = 0, n = entries.getLength(); i < n; ++i) {
        tempEntry = entries.item(i);
        if (tempEntry.getNodeType() == Node.ELEMENT_NODE) {
          tempElement = (Element) tempEntry;
          quakesInfo.add(new EarthQuakeEntry(
              tempElement.getElementsByTagName("id").item(0).getTextContent(), // gets EarthQuake ID as it's in ID tag
              tempElement.getElementsByTagName("title").item(0).getTextContent(), // gets Title of earthQuake as it's in title tag
              tempElement.getElementsByTagName("georss:point").item(0).getTextContent(), // gets location of earthQuake as it's in georss:point tag
              tempElement.getElementsByTagName("georss:elev").item(0).getTextContent(), //// gets Depth of earthQuake as it's in georess:elev tag
              ((Element) tempElement.getElementsByTagName("category").item(0)).getAttribute("term") // gets value of term attribute which holds the age of earthquake in Category Tag
          ));
        }
      }
      return quakesInfo;
    }catch(Exception ex)
    {
      System.err.println(ex.getMessage());
    }
    return new ArrayList<>(); // returns an Empty array list instead of null for api flexibility
  }
  /*
   * URL Version
   * Main method to parse the RSS fEED and should be the first method to be used
   * in the class, takes the xml file and parses it using Xerces XML Parser
   * Makes a Node element of entry (which is the main Node that holds all the info of each line or input in file)
   * and using that Entry node, gets the id,title,location,depth,Age using their respected tag names in the RSS feed file
   * and makes a EarthQuakeEntry Pojo Object using these data
   * putting each one in a List of QuakeEntries that will be used later in making the features for the markers on the map
   * @Param: file the contains the RSS feed to be parsed
   * */
  private List<EarthQuakeEntry> parseXML(URL inputXmlFile)
  {
    try {
      Document xmlFile = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder().parse(inputXmlFile.openStream()); // making a document object for XML file and parsing it
      xmlFile.getDocumentElement().normalize(); // removing white spaces and such
      NodeList entries = xmlFile.getElementsByTagName("entry"); // gets Node List of Entry tags where each entry should be made into a POJO object as it holds the earthquake data
      Node tempEntry = null;
      Element tempElement = null;
      List<EarthQuakeEntry> quakesInfo = new ArrayList<>();
      // main loop for filling pojo objects
      for (int i = 0, n = entries.getLength(); i < n; ++i) {
        tempEntry = entries.item(i);
        if (tempEntry.getNodeType() == Node.ELEMENT_NODE) {
          tempElement = (Element) tempEntry;
          quakesInfo.add(new EarthQuakeEntry(
              tempElement.getElementsByTagName("id").item(0).getTextContent(), // gets EarthQuake ID as it's in ID tag
              tempElement.getElementsByTagName("title").item(0).getTextContent(), // gets Title of earthQuake as it's in title tag
              tempElement.getElementsByTagName("georss:point").item(0).getTextContent(), // gets location of earthQuake as it's in georss:point tag
              tempElement.getElementsByTagName("georss:elev").item(0).getTextContent(), //// gets Depth of earthQuake as it's in georess:elev tag
              ((Element) tempElement.getElementsByTagName("category").item(0)).getAttribute("term") // gets value of term attribute which holds the age of earthquake in Category Tag
          ));
        }
      }
      return quakesInfo;
    }catch(Exception ex)
    {
      System.err.println(ex.getMessage());
    }
    return new ArrayList<>(); // returns an Empty array list instead of null for api flexibility
  }

  /*
  * List<EarthQuakeEntry> version
  * A public static method that filters EarthQuakes according to EarthQuakeFilters var args
  * a kinda generic algorithm that is suitable for all filter usage and the filters used are
  * static classes of the EarthQuakesFeedParser
  * the method is static as it doesn't belong to an instance, it belongs to the class itself in concept
  * returns List<EarthQuakeEntry>
  * @Param List<EarthQuakeEntry> that holds the earthquake data in POJOs
  * @Param EarthQuakeFilter var arg that holds any number of filters and all of them will be applied
  * */
  public static List<EarthQuakeEntry> filterEarthQuakeEntries(List<EarthQuakeEntry> earthquakes,EarthQuakeFilter... filters)
  {
    if(earthquakes == null || earthquakes.isEmpty())
      throw new IllegalArgumentException("earthquake entries and filters can't be either null or empty");
    if(filters == null || filters.length == 0) // if no filters, return the same List given without any operation
      return earthquakes;

    List<EarthQuakeEntry> filteredEntries = new ArrayList<>();

    // the sorting approach isn't suitable as different filters can be applied and also because
    // sorting nlogn while each element is n and for each filter n and m filters will never reach n elements
    // so O(n) and the sorting approach is nlogn + n so O(nlogn) which is worse
    for(EarthQuakeEntry entry : earthquakes)
    {
      if(EarthQuakesFeedParser.isApplicable(entry,filters))
        filteredEntries.add(entry);
    }
    return filteredEntries;
  }

  /*
   * List<PointFeature> version, uses the List<EarthQuakeEntry> version and uses parseEntries method
   * to make point features using the filtered array not the whole array returning it
   * A public static method that filters EarthQuakes according to EarthQuakeFilters var args
   * a kinda generic algorithm that is suitable for all filter usage and the filters used are
   * static classes of the EarthQuakesFeedParser
   * the method is static as it doesn't belong to an instance, it belongs to the class itself in concept
   * uses the private List<EarthQuakeEntry> constructor to keep the static context with no extra storage
   * @Param List<EarthQuakeEntry> that holds the earthquake data in POJOs
   * @Param EarthQuakeFilter var arg that holds any number of filters and all of them will be applied
   * */
  public static List<PointFeature> filterIntoPointFeatures(List<EarthQuakeEntry> earthquakes,EarthQuakeFilter... filters)
  {
    if(earthquakes == null  || earthquakes.isEmpty())
      throw new IllegalArgumentException("earthquake entries and filters can't be either null or empty");
    if(filters == null || filters.length == 0) // if no filters, return the PointFeature List using the given EarthQuakeEntry list without any operation
      return new EarthQuakesFeedParser(earthquakes).getParsedFeatures();
    List<EarthQuakeEntry> filteredEntries = EarthQuakesFeedParser.filterEarthQuakeEntries(earthquakes,filters);

    return new EarthQuakesFeedParser(filteredEntries).getParsedFeatures();
  }


  /*
  * A private helper method is made for returning boolean if all filters are accepted on entries
  * as if the filters loop is made inside the entry loop, and continue keyword is used, it will continue itself
  * not continue the main loop so a private helper method is needed
  *  static as it doesn't belong to the instance but to the class itself in concept
  * if any filter return false then false is returned, if the loop finished and no false is returned
  * then all filters are applicable and returns true
  * @Param : EarthQuakeEntry that the filters are tested against
  * @Param : filters vararg to be applied on each entry
  */

  private static boolean isApplicable(EarthQuakeEntry entry,EarthQuakeFilter... filters)
  {
    for(EarthQuakeFilter f : filters)
    {
      if(f == null) continue; // made for API reason in GUI
      if(!f.test(entry))
        return false;
    }
    return true;
  }

  /*
  * Made for testing
  * works nicely
  * */
//  public static void main(String[] args){
//    EarthQuakesFeedParser parser = new EarthQuakesFeedParser("./data/2.5_week.atom");
//    List<EarthQuakeEntry> testerList = parser.getParsedQuakeEntries();
//    Collections.sort(testerList, new Comparator<EarthQuakeEntry>(){
//      public int compare(EarthQuakeEntry e1,EarthQuakeEntry e2)
//      {
//        return e1.getExactDepth().compareTo(e2.getExactDepth());
//      }
//    });
//    System.out.println("no. of earthquakes before filtering: "+testerList.size());
//    List<EarthQuakeEntry> filteredTesterList = EarthQuakesFeedParser.filterEarthQuakeEntries(testerList, new EarthQuakesFeedParser.MagnitudeLessThanFilter(2.50,true));
//    System.out.println("no. of earthquakes after filtering: "+filteredTesterList.size());
//    for (EarthQuakeEntry e : testerList) {
//      System.out.println(e);
//    }
//  }

}
