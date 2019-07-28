package controller;/*
  Author: Hisham Maged
  Date : 7/23/2019
  Class Desc : A class Made to control the flow of data to the map from all the other classes in the project
*/

import controller.EarthQuakeUtils.EarthQuakeFilter;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.stage.Stage;
import model.parser.DataParser;
import model.pojo.EarthQuakeEntry;
import model.pojo.LifeExpectancyEntry;

/**
 * <h1>DataUtils</h1>
 * <p>
 *   Class That control the flow of data to the Visualization Map from other types in project.
 * </p>
 * Makes
 * <ul>
 *   <li>
 *     <h3>Earthquakes markers from</h3>
 *     <ul>
 *       <li>Local File using Swing File Chooser</li>
 *       <li>Local File using JavaFX file chooser</li>
 *       <li>String containing File Path</li>
 *       <li>File object pointing to File on disk</li>
 *       <li>
 *         <strong>Live data from USGS website</strong>
 *          <ul>
 *            <li>Past Hour</li>
 *            <li>Past Day</li>
 *            <li>Past 7 Days</li>
 *            <li>Past Month</li>
 *          </ul>
 *       </li>
 *     </ul>
 *   </li>
 *   <li>
 *      <h3>Life Expectancy Markers from</h3>
 *      <ul>
 *         <li>Local File using Swing File Chooser</li>
 *         <li>Local File using JavaFX file chooser</li>
 *         <li>String containing File Path</li>
 *         <li>File object pointing to File on disk</li>
 *         <li> Live Data from World Bank of data </li>
 *       </ul>
 *   </li>
 * </ul>
 * @author Hisham Maged
 * @version 1.1
 * @since 23/7/2019
 * @see DataParser
 * @see EarthQuakeUtils
 * @see WorldDataUtils
 * @see MarkerUtils
 */
public class DataUtils {

  private DataUtils(){}


  /**
    * Holds unmodefiable list of latest PointFeature made.
   */
  public static List<Marker> lastMadeEarthQuakesMarkers = Collections.EMPTY_LIST; //unmodefiable


  /*
   * String URLS for live earth quake data
   * past 30 days
   * past 7 days
   * past day
   * past hour
   * */
  private static final String LIVE_EARTHQUAKE_DATA_PAST_30_DAYS_STRING = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_month.atom";
  private static final String LIVE_EARTHQUAKE_DATA_PAST_7_DAYS_STRING = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
  private static final String LIVE_EARTHQUAKE_DATA_PAST_DAY_STRING = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.atom";
  private static final String LIVE_EARTHQUAKE_DATA_PAST_HOUR_STRING = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.atom";
  /*
   * URLS for live earth quake data
   * past 30 days
   * past 7 days
   * past day
   * past hour
   * */
  private final static URL LIVE_EARTHQUAKE_DATA_PAST_30_DAYS = makeURL(LIVE_EARTHQUAKE_DATA_PAST_30_DAYS_STRING);
  private final static URL LIVE_EARTHQUAKE_DATA_PAST_7_DAYS = makeURL(LIVE_EARTHQUAKE_DATA_PAST_7_DAYS_STRING);
  private final static URL LIVE_EARTHQUAKE_DATA_PAST_DAY = makeURL(LIVE_EARTHQUAKE_DATA_PAST_DAY_STRING);
  private final static URL LIVE_EARTHQUAKE_DATA_PAST_HOUR = makeURL(LIVE_EARTHQUAKE_DATA_PAST_HOUR_STRING);

  /**
  * Makes the earth quakes markers List<Marker> using a Swing FileChooser to get local data
  * @param filters EarthQuakeFilter var args that contains filter instances that will be applied on data
  * @return List Containing Earthquakes Markers
   * */
  public static List<Marker> makeLocalEarthQuakesMarkers(EarthQuakeFilter... filters)
  {
    DataParser<EarthQuakeEntry> parser = DataParserBuilder.buildXMLParser().swingFileChooser().earthquakes().parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /**
   * Makes the earth quakes markers List<Marker> using a FX FileChooser to get local data
   * @param stage Stage  that is the current stage of the fx application
   * @param filters EarthQuakeFilter var args that contains filter instances that will be applied on data
   * @return List Containing Earthquakes Markers
   * */
  public static List<Marker> makeLocalEarthQuakesMarkers(Stage stage, EarthQuakeFilter... filters)
  {
    DataParser<EarthQuakeEntry> parser = DataParserBuilder.buildXMLParser().fxFileChooser(stage).earthquakes().parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /**
   * Makes the earth quakes markers List<Marker> using a String filePath to get local data
   * @param filePath String filePath that holds the path of the file on disk
   * @param filters EarthQuakeFilter var args that contains filter instances that will be applied on data
   * @return List Containing Earthquakes Markers
   * */
  public static List<Marker> makeLocalEarthQuakesMarkers(String filePath, EarthQuakeFilter... filters)
  {
    DataParser<EarthQuakeEntry> parser = DataParserBuilder.buildXMLParser().filePath(filePath).earthquakes().parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /**
   * Makes the earth quakes markers List<Marker> using a File object pointing local data
   * @param file File file that holds the object pointing to the file on disk
   * @param filters EarthQuakeFilter var args that contains filter instances that will be applied on data
   * @return List Containing Earthquakes Markers
   * */
  public static List<Marker> makeLocalEarthQuakesMarkers(File file, EarthQuakeFilter... filters)
  {
    DataParser<EarthQuakeEntry> parser = DataParserBuilder.buildXMLParser().file(file).earthquakes().parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /**
   * Makes the earth quakes markers List<Marker> using live earthquakes data from USGS website
   * uses the private field containing the lasthour data url, to get the last hour live data
   * @param filters EarthQuakeFilter var args that contains filter instances that will be applied on data
   * @return List Containing Earthquakes Markers
   * */
  public static List<Marker> makeLastHourEarthQuakesMarkers(EarthQuakeFilter... filters)
  {
    DataParser<EarthQuakeEntry> parser = DataParserBuilder.buildXMLParser().url(LIVE_EARTHQUAKE_DATA_PAST_HOUR).earthquakes().parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /**
   * Makes the earth quakes markers List<Marker> using live earthquakes data from USGS website
   * uses the private field containing the lastDay data url, to get the last Day live data
   * @param filters EarthQuakeFilter var args that contains filter instances that will be applied on data
   * @return List Containing Earthquakes Markers
   * */
  public static List<Marker> makeLastDayEarthQuakesMarkers(EarthQuakeFilter... filters)
  {
    DataParser<EarthQuakeEntry> parser = DataParserBuilder.buildXMLParser().url(LIVE_EARTHQUAKE_DATA_PAST_DAY).earthquakes().parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /**
   * Makes the earth quakes markers List<Marker> using live earthquakes data from USGS website
   * uses the private field containing the lastWeek data url, to get the last Week live data
   * @param filters EarthQuakeFilter var args that contains filter instances that will be applied on data
   * @return List Containing Earthquakes Markers
   * */
  public static List<Marker> makeLastWeekEarthQuakesMarkers(EarthQuakeFilter... filters)
  {
    DataParser<EarthQuakeEntry> parser = DataParserBuilder.buildXMLParser().url(LIVE_EARTHQUAKE_DATA_PAST_7_DAYS).earthquakes().parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /**
   * Makes the earth quakes markers List<Marker> using live earthquakes data from USGS website
   * uses the private field containing the lastMonth data url, to get the last Month live data
   * @param filters EarthQuakeFilter var args that contains filter instances that will be applied on data
   * @return List Containing Earthquakes Markers
   * */
  public static List<Marker> makeLastMonthEarthQuakesMarkers(EarthQuakeFilter... filters)
  {
    DataParser<EarthQuakeEntry> parser = DataParserBuilder.buildXMLParser().url(LIVE_EARTHQUAKE_DATA_PAST_30_DAYS).earthquakes().parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /*
  * a private helper method that does the common code for earthquakes markers for all making earthquake markers methods
  * @Param:Iterable<EarthQuakeEntry> iterable containing the data of EarthQuakes filled in POJOs
  * @Param: EarthQuakeFilter var args that contains filter instances that will be applied on data
  */
  private static List<Marker> getEarthQuakeMarkerFromDataAndFilters(Iterable<EarthQuakeEntry> entries,EarthQuakeFilter... filters)
  {
    entries = EarthQuakeUtils.filter(entries,filters); // filters entries
    List<PointFeature> features = EarthQuakeUtils.toPointFeatures(entries); // makes features due to filtered entries
    return lastMadeEarthQuakesMarkers = Collections.unmodifiableList(MarkerUtils.makeEarthQuakesMarkers(features)); // equates last made markers to same reference but as unmoedfiable and returns it
  }

  /**
   * Makes the life expectancy markers List<Marker> using a Swing FileChooser to get local data
   * @param year int that contains the year that you want the global values of
   * @return List Containing LifeExpectancy Markers
   * */
  public static List<Marker> makeLocalLifeExpectancyMarkers(int year)
  {
    DataParser<LifeExpectancyEntry> parser = DataParserBuilder.buildCSVParser().swingFileChooser().lifeExpectancy().parse();
    Iterable<LifeExpectancyEntry> entries = parser.getParsedData();
    Map<String,Float> mappedValues =WorldDataUtils.toCountryCodeMap(entries,year);
    return getLifeExpectancyFromDataAndFilters(mappedValues);
  }

  /**
   * Makes the life expectancy markers List<Marker> using a String filePath to get local data
   * @param filePath filePath that holds the path of the file on disk
   * @param year int that contains the year that you want the global values of
   * @return List Containing LifeExpectancy Markers
   * */
  public static List<Marker> makeLocalLifeExpectancyMarkers(String filePath, int year)
  {
    DataParser<LifeExpectancyEntry> parser = DataParserBuilder.buildCSVParser().filePath(filePath).lifeExpectancy().parse();
    Iterable<LifeExpectancyEntry> entries = parser.getParsedData();
    Map<String,Float> mappedValues =WorldDataUtils.toCountryCodeMap(entries,year);
    return getLifeExpectancyFromDataAndFilters(mappedValues);

  }

  /**
   * Makes the life expectancy markers List<Marker> using a File object pointing local data
   * @param file File that holds the object pointing to the file on disk
   * @param year int that contains the year that you want the global values of
   * @return List Containing LifeExpectancy Markers
   * */
  public static List<Marker> makeLocalLifeExpectancyMarkers(File file, int year)
  {
    DataParser<LifeExpectancyEntry> parser = DataParserBuilder.buildCSVParser().file(file).lifeExpectancy().parse();
    Iterable<LifeExpectancyEntry> entries = parser.getParsedData();
    Map<String,Float> mappedValues =WorldDataUtils.toCountryCodeMap(entries,year);
    return getLifeExpectancyFromDataAndFilters(mappedValues);

  }

  /**
   * the main method of making the life expectancy markers List<Marker> using a FX FileChooser to get local data
   * @param stage Stage that is the current stage of the fx application
   * @param year int that contains the year that you want the global values of
   * @return List Containing LifeExpectancy Markers
   * */
  public static List<Marker> makeLocalLifeExpectancyMarkers(Stage stage, int year)
  {
    DataParser<LifeExpectancyEntry> parser = DataParserBuilder.buildCSVParser().fxFileChooser(stage).lifeExpectancy().parse();
    Iterable<LifeExpectancyEntry> entries = parser.getParsedData();
    Map<String,Float> mappedValues =WorldDataUtils.toCountryCodeMap(entries,year);
    return getLifeExpectancyFromDataAndFilters(mappedValues);

  }

  /**
   * Makes the life expectancy markers List<Marker> using live lifeexpectancy data from worlddatabank website
   * @param url URL holding the file's url  on the internet
   * @param year int that contains the year that you want the global values of
   * @return List Containing LifeExpectancy Markers
   * */
  public static List<Marker> makeLiveLifeExpectancyMarkers(URL url, int year)
  {
    DataParser<LifeExpectancyEntry> parser = DataParserBuilder.buildCSVParser().url(url).lifeExpectancy().parse();
    Iterable<LifeExpectancyEntry> entries = parser.getParsedData();
    Map<String,Float> mappedValues =WorldDataUtils.toCountryCodeMap(entries,year);
    return getLifeExpectancyFromDataAndFilters(mappedValues);

  }

  /*
   * a private helper method that does the common code for life expectancy markers for all making life expectancy markers methods
   * @Param:Map<String,Float> containing all the mapped values of country code and the value needed to given year at initial method invocation
   */
  private static List<Marker> getLifeExpectancyFromDataAndFilters(Map<String,Float> mappedValues)
  {
    List<Marker> markers = WorldDataUtils.makeWorldDataMarkers(mappedValues,45,90); // uses the mappedValues to make the List<Marker>
    return markers;
  }

  /**
  * Makes the CityMarkers using the city-file by making features using the file and then making the Markers using those features
  * @return List containing City Markers
  * */
  public static List<Marker> makeCityMarkers()
  {
    List<Feature> cityFeatures = WorldDataUtils.getCityFeatures(); // gets the CityFeatures using the geoJSOnReader
    return MarkerUtils.makeLocationMarkers(cityFeatures); // returns the List<Marker> of the cities using the features made on the previous step
  }




  /*
   * used to make final URL objects as with static initializers it's complicated and it always throws
   * a MalformedURLException that can't be propagated to a private static method solves this by making
   * a URL object with given string or null if exception so solved.
   * */
  private static URL makeURL(String urlString)
  {
    try{
      return new URL(urlString);
    }catch(Exception ex)
    {
      return null;
    }
  }

//  public static void main(String[] args)
//  {
//    DataParser<EarthQuakeEntry> parserTester = DataParserBuilder.buildXMLParser().url(LIVE_EARTHQUAKE_DATA_PAST_30_DAYS).earthquakes();
//    parserTester.parse();
//    Iterable<EarthQuakeEntry> iterableTester = parserTester.getParsedData();
//    iterableTester = EarthQuakeUtils.filter(iterableTester,new MagnitudeMoreThanFilter(5.0,true));
//    iterableTester.forEach((entry) -> System.out.println(entry));
//  }

}
