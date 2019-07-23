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
import model.parser.EarthQuakesParser;
import model.parser.LifeExpectancyParser;
import model.pojo.EarthQuakeEntry;
import model.pojo.LifeExpectancyEntry;

public class DataUtils {

  private DataUtils(){}


  // holds unmodefiable list of latest PointFeature Made
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

  /*
  *
  * */
  public static List<Marker> makeLocalEarthQuakesMarkers(EarthQuakeFilter... filters)
  {
    EarthQuakesParser parser = DataParserBuilder.buildXMLParser().swingFileChooser().earthquakes();
    parser.parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /*
   *
   * */
  public static List<Marker> makeLocalEarthQuakesMarkers(Stage stage, EarthQuakeFilter... filters)
  {
    EarthQuakesParser parser = DataParserBuilder.buildXMLParser().fxFileChooser(stage).earthquakes();
    parser.parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /*
   *
   * */
  public static List<Marker> makeLocalEarthQuakesMarkers(String filePath, EarthQuakeFilter... filters)
  {
    EarthQuakesParser parser = DataParserBuilder.buildXMLParser().filePath(filePath).earthquakes();
    parser.parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /*
   *
   * */
  public static List<Marker> makeLocalEarthQuakesMarkers(File file, EarthQuakeFilter... filters)
  {
    EarthQuakesParser parser = DataParserBuilder.buildXMLParser().file(file).earthquakes();
    parser.parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /*
  *
  * */
  public static List<Marker> makeLastHourEarthQuakesMarkers(EarthQuakeFilter... filters)
  {
    EarthQuakesParser parser = DataParserBuilder.buildXMLParser().url(LIVE_EARTHQUAKE_DATA_PAST_HOUR).earthquakes();
    parser.parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /*
   *
   * */
  public static List<Marker> makeLastDayEarthQuakesMarkers(EarthQuakeFilter... filters)
  {
    EarthQuakesParser parser = DataParserBuilder.buildXMLParser().url(LIVE_EARTHQUAKE_DATA_PAST_DAY).earthquakes();
    parser.parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /*
   *
   * */
  public static List<Marker> makeLastWeekEarthQuakesMarkers(EarthQuakeFilter... filters)
  {
    EarthQuakesParser parser = DataParserBuilder.buildXMLParser().url(LIVE_EARTHQUAKE_DATA_PAST_7_DAYS).earthquakes();
    parser.parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /*
   *
   * */
  public static List<Marker> makeLastMonthEarthQuakesMarkers(EarthQuakeFilter... filters)
  {
    EarthQuakesParser parser = DataParserBuilder.buildXMLParser().url(LIVE_EARTHQUAKE_DATA_PAST_30_DAYS).earthquakes();
    parser.parse();
    Iterable<EarthQuakeEntry> entries = parser.getParsedData();
    return DataUtils.getEarthQuakeMarkerFromDataAndFilters(entries,filters);
  }

  /*
  *
  */
  private static List<Marker> getEarthQuakeMarkerFromDataAndFilters(Iterable<EarthQuakeEntry> entries,EarthQuakeFilter... filters)
  {
    entries = EarthQuakeUtils.filter(entries,filters);
    List<PointFeature> features = EarthQuakeUtils.toPointFeatures(entries);
    return lastMadeEarthQuakesMarkers = Collections.unmodifiableList(MarkerUtils.makeEarthQuakesMarkers(features));
  }

  /*
  *
  * */
  public static List<Marker> makeLocalLifeExpectancyMarkers(int year)
  {
    LifeExpectancyParser parser = DataParserBuilder.buildCSVParser().swingFileChooser().lifeExpectancy();
    parser.parse();
    Iterable<LifeExpectancyEntry> entries = parser.getParsedData();
    Map<String,Float> mappedValues =WorldDataUtils.toCountryCodeMap(entries,year);
    return getLifeExpectancyFromDataAndFilters(mappedValues);
  }

  /*
   *
   * */
  public static List<Marker> makeLocalLifeExpectancyMarkers(String filePath, int year)
  {
    LifeExpectancyParser parser = DataParserBuilder.buildCSVParser().filePath(filePath).lifeExpectancy();
    parser.parse();
    Iterable<LifeExpectancyEntry> entries = parser.getParsedData();
    Map<String,Float> mappedValues =WorldDataUtils.toCountryCodeMap(entries,year);
    return getLifeExpectancyFromDataAndFilters(mappedValues);

  }

  /*
   *
   * */
  public static List<Marker> makeLocalLifeExpectancyMarkers(File file, int year)
  {
    LifeExpectancyParser parser = DataParserBuilder.buildCSVParser().file(file).lifeExpectancy();
    parser.parse();
    Iterable<LifeExpectancyEntry> entries = parser.getParsedData();
    Map<String,Float> mappedValues =WorldDataUtils.toCountryCodeMap(entries,year);
    return getLifeExpectancyFromDataAndFilters(mappedValues);

  }

  /*
   *
   * */
  public static List<Marker> makeLocalLifeExpectancyMarkers(Stage stage, int year)
  {
    LifeExpectancyParser parser = DataParserBuilder.buildCSVParser().fxFileChooser(stage).lifeExpectancy();
    parser.parse();
    Iterable<LifeExpectancyEntry> entries = parser.getParsedData();
    Map<String,Float> mappedValues =WorldDataUtils.toCountryCodeMap(entries,year);
    return getLifeExpectancyFromDataAndFilters(mappedValues);

  }

  /*
   *
   * */
  public static List<Marker> makeLiveLifeExpectancyMarkers(URL url, int year)
  {
    LifeExpectancyParser parser = DataParserBuilder.buildCSVParser().url(url).lifeExpectancy();
    parser.parse();
    Iterable<LifeExpectancyEntry> entries = parser.getParsedData();
    Map<String,Float> mappedValues =WorldDataUtils.toCountryCodeMap(entries,year);
    return getLifeExpectancyFromDataAndFilters(mappedValues);

  }

  private static List<Marker> getLifeExpectancyFromDataAndFilters(Map<String,Float> mappedValues)
  {
    List<Marker> markers = WorldDataUtils.makeWorldDataMarkers(mappedValues,45,90);
    return markers;
  }
  /*
  *
  * */
  public static List<Marker> makeCityMarkers()
  {
    List<Feature> cityFeatures = WorldDataUtils.getCityFeatures();
    return MarkerUtils.makeLocationMarkers(cityFeatures);
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

}
