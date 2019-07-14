package View;
/*
  Author: Hisham Maged
  Date : 7/11/2019
  Project Name : A Map with visualized EarthQuakes data
*/

import Controller.EarthQuakesFeedParser;
import Controller.EarthQuakesFeedParser.ExactMagnitudeFilter;
import Controller.EarthQuakesFeedParser.MagnitudeRangeFilter;
import Controller.EarthQuakesMarkerHandler;
import Controller.EarthQuakesMarkerHandler.EarthQuakeMarker;
import Controller.LifeExpectancyMarkerHandler;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import java.net.URL;
import java.util.List;
import processing.core.PApplet;

public class LiveDataMap extends PApplet {

  /* private ENUM made to choose a map*/
  private enum Map{
    MAGNITUDE_EARTHQUAKES,DEPTH_EARTHQUAKES,LIFEEXPECTANCY;
  }
  // Used Map reference
  private UnfoldingMap map;
  // Magnitude Earth quakes map
  private UnfoldingMap magnitudeEarthQuakesMap;
  // Depth Earth quakes map
  private UnfoldingMap depthEarthQuakesMap;
  // used magnitudeEarthquakeMarkers reference
  private List<SimplePointMarker> magnitudeEarthquakeMarkers;
  // used depthEarthquakeMarkers reference
  private List<SimplePointMarker> depthEarthquakeMarkers;
  // used lifeExpectancyMarkers reference
  private List<Marker> lifeExpectancyMarkers;
  //LifeExpectancy Map reference
  private UnfoldingMap lifeExpectancyMap;

  // private List<ScreenPosition> holding the markers of earthquake magnitudes to be customized in draw
  private List<ScreenPosition> magnitudeScreenPositions;

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
  * Light earthQuake value : 4.0
  * moderate earthQuake Value : 4.0 - 5.0
  * intense earthquake value : 10.0
  * */
  private static final double LIGHT_EARTHQUAKE = 4.0;
  private static final double MODERATE_EARTHQUAKE = 5.0;
  private static final double INTENSE_EARTHQUAKE = 10.0;

  /*
  * Light Depth value : 0-50
  * Moderate Depth Value: 50-200
  * Intense : 200+
  * */
  private static final double LIGHT_DEPTH = 0.0;
  private static final double MODERATE_DEPTH = 50.0;
  private static final double INTENSE_DEPTH = 300.0;

  /*
  * Colors for makres of light,moderate,intense magnitude earthquake markers
  * not static because color method can't be invoked in static context
  * */
  private final int LIGHT_EARTHQUAKE_COLOR = color(6,175,194,100);
  private final int MODERATE_EARTHQUAKE_COLOR = color(251,255,0,130);
  private final int INTENSE_EARTHQUAKE_COLOR = color(191,34,40,150);

  /*
   * Colors for makres of light,moderate,intense depth earthquake markers
   * not static because color method can't be invoked in static context
   * */
  private final int LIGHT_DEPTH_COLOR = color(242,255,56,130);
  private final int MODERATE_DEPTH_COLOR = color(214,71,24,100);
  private final int INTENSE_DEPTH_COLOR = color(148,28,32,200);


  /*
  * Year used to get data of Life Expectancy of all countries
  * */
  private final static int LIFE_EXPECTANCY_YEAR = 2017;

  /*
  * key choosers for showing earthquake magnitude data depth data or lifeExpectancy map
  * */

  private static final char MAGNITUDE_EARTHQUAKES_KEY = '1';
  private static final char DEPTH_EARTHQUAKES_KEY = '2';
  private static final char LIFE_EXPECTANCY_KEY = '3';

  private Map usedMap = Map.MAGNITUDE_EARTHQUAKES;

  // main setup method, the looks of the window
  public void setup()
  {
    size(1280,720,OPENGL);
    initMap();
  }
  // the draw loop, invoked due to any change or loop (determined in backend of Processing library)
  public void draw()
  {
    background(30);
    this.map.draw();
    addLegend();
    makeScreenPositions();
    writeLocation();
  }

  /*
  * a method that initializes both the earthquake map and life expectancy map to be of Microsoft Road Provider Map
  * and adds interactivity to the map
  * and uses the default XML File and gets the magnitudeEarthquakeMarkers of it using the EarthQuakesMarkerHandler Class
  * and uses the default CSV file for lifeExpectancy using the LifeExpectancyMarkerHandler class
  * and adds those magnitudeEarthquakeMarkers and life expectancy to the map
  * setting the default map reference to earthquake data maps
  *
  * */
  private void initMap()
  {

    // =========================================== Magnitude EarthQuakes Section ====================================


    this.magnitudeEarthQuakesMap = new UnfoldingMap(this,200,50,1020,620,new Microsoft.RoadProvider());
    MapUtils.createDefaultEventDispatcher(this,this.magnitudeEarthQuakesMap);
    /*
    * if you want to filter the earthquake data, use respected EarthQuakesFilter objects for more info about them
    * check the EarthQuakeFeedParser static classes
    * change the 1st parameter in makeEarthQuakeMarkers
    * no 1st parameter >> FileChooser to choose ATOM file
    * String >> filePath
    * File >> Atom file object
    * URL >> live data url
    * */
    this.magnitudeEarthquakeMarkers = EarthQuakesMarkerHandler
        .makeEarthQuakeMarkers("./data/2.5_week.atom");

    this.magnitudeEarthQuakesMap.setZoomRange(1,15);

    // =========================================== Depth EarthQuakes Section ====================================


    this.depthEarthQuakesMap = new UnfoldingMap(this,200,50,1020,620,new Microsoft.RoadProvider());
    MapUtils.createDefaultEventDispatcher(this,this.depthEarthQuakesMap);
    /*
     * if you want to filter the earthquake data, use respected EarthQuakesFilter objects for more info about them
     * check the EarthQuakeFeedParser static classes
     * change the 1st parameter in makeEarthQuakeMarkers
     * no 1st parameter >> FileChooser to choose ATOM file
     * String >> filePath
     * File >> Atom file object
     * URL >> live data url
     * */
    // TODO: FIND OUT WHY DEPTH FILTER IS BUGGY USING THE MAIN METHOD OF EARTHQUAKESFEEDPARSER to FIND out easier
    this.depthEarthquakeMarkers = EarthQuakesMarkerHandler
        .makeEarthQuakeMarkers("./data/2.5_week.atom");

    this.depthEarthQuakesMap.setZoomRange(1,15);

    // ===========================================  Life Expectancy Section ====================================

    this.lifeExpectancyMap = new UnfoldingMap(this,200,50,1020,620, new Microsoft.RoadProvider());
    MapUtils.createDefaultEventDispatcher(this,this.lifeExpectancyMap);
    /*
     * change first parameter in makeLifeExpectancyMarkers
     * no 1st parameter >> FileChooser to choose CSV file
     * String >> filePath
     * File >> CSV file object
     * URL >> live data url
     * */
    this.lifeExpectancyMarkers = LifeExpectancyMarkerHandler.makeLifeExpectancyMarkers(this,"./data/API_SP.DYN.LE00.IN_DS2_en_csv_v2_40967.csv",LIFE_EXPECTANCY_YEAR);
    this.lifeExpectancyMap.addMarkers(this.lifeExpectancyMarkers);

    this.lifeExpectancyMap.setZoomRange(1,15);

    this.map = this.magnitudeEarthQuakesMap;
  }

  /*
   * Private helper method that adds customized screen positions instead of markers for earthquake magnitude or depth data
   * using the map enum and static method of EarthQuakesMarkerHandler showMarkers() using Marker enum
   *  */
  private void makeScreenPositions()
  {
    switch(this.usedMap)
    {
      case MAGNITUDE_EARTHQUAKES:
        EarthQuakesMarkerHandler.showMarkers(this,this.magnitudeEarthQuakesMap,this.magnitudeEarthquakeMarkers,
            EarthQuakeMarker.MAGNITUDE,LIGHT_EARTHQUAKE,MODERATE_EARTHQUAKE,INTENSE_EARTHQUAKE,LIGHT_EARTHQUAKE_COLOR,MODERATE_EARTHQUAKE_COLOR,INTENSE_EARTHQUAKE_COLOR);
        break;
      case DEPTH_EARTHQUAKES:

        EarthQuakesMarkerHandler.showMarkers(this,this.depthEarthQuakesMap,this.depthEarthquakeMarkers,
            EarthQuakeMarker.DEPTH,LIGHT_DEPTH,MODERATE_DEPTH,INTENSE_DEPTH,LIGHT_DEPTH_COLOR,MODERATE_DEPTH_COLOR,INTENSE_DEPTH_COLOR);
        break;
    }
  }

  /*
  * depending on Map Enum of selected Map "usedMap" private field
  * adds Legend on right hand side with Magnitude and
  * their respected circle color and size for case of EarthQuakes
  * adds legend for life Expectancy
  * with red shade meaning low life expectancy
  * blue shade meaning high life epxectancy
  * in between is the range from 40 to 90
  * */
  private void addLegend()
  {
    fill(color(230));
    noStroke();
    rect(30,50,140,250);
    textSize(13);
    fill(color(0));
    switch(this.usedMap)
    {
      case MAGNITUDE_EARTHQUAKES:
        text("Legend",65,75);
        text(MODERATE_EARTHQUAKE+" + Magnitude",60,120);
        text(LIGHT_EARTHQUAKE+" + Magnitude",60,170);
        text("Below "+LIGHT_EARTHQUAKE,60,220);
        fill(INTENSE_EARTHQUAKE_COLOR);
        ellipse(45,115,18,18);
        fill(MODERATE_EARTHQUAKE_COLOR);
        ellipse(45,165,9,9);
        fill(LIGHT_EARTHQUAKE_COLOR);
        ellipse(45,215,4,4);
        break;
      case DEPTH_EARTHQUAKES:
        text("Legend",65,75);
        text(INTENSE_DEPTH+" + Depth",60,120);
        text(MODERATE_DEPTH+" + Depth",60,170);
        text("Below "+MODERATE_DEPTH,60,220);
        fill(INTENSE_DEPTH_COLOR);
        ellipse(45,115,18,18);
        fill(MODERATE_DEPTH_COLOR);
        ellipse(45,165,9,9);
        fill(LIGHT_DEPTH_COLOR);
        ellipse(45,215,4,4);
                      break;
      case LIFEEXPECTANCY:
        text("Life Expectancy",50,75);
        text("Low shade",60,210);
        text("High shade",60,120);
        fill(color(255,0,0));
        ellipse(45,205,18,18);
        fill(color(0,0,255));
        ellipse(45,115,18,18);
        break;
    }

  }
  /*
  * Key pressed functionality that changes between the earthquake map and life expectancy map
  * using final char MAGNITUDE_EARTHQUAKES_KEY for magnitude earthquake data
  * using final char DEPTH_EARTHQUAKES_KEY for depth earthquake data
  * using final char LIFE_EXPECTANCY_KEY for life expectancy data
  * */
  public void keyPressed()
  {
    if(key == MAGNITUDE_EARTHQUAKES_KEY) {
      this.usedMap = Map.MAGNITUDE_EARTHQUAKES;
      this.map = this.magnitudeEarthQuakesMap;
    }
    else if( key == DEPTH_EARTHQUAKES_KEY)
    {
      this.usedMap = Map.DEPTH_EARTHQUAKES;
      this.map = this.depthEarthQuakesMap;
    }
    else if (key == LIFE_EXPECTANCY_KEY)
    {
      this.usedMap = Map.LIFEEXPECTANCY;
      this.map = this.lifeExpectancyMap;
    }
  }


  /*
  * made to write the latitude, longitude of place of mouse
  * */
  public void writeLocation()
  {
    Location mouseLoc = this.map.getLocation(mouseX,mouseY);
    fill(0);
    text("Latitude: "+mouseLoc.getLat()+", Longitude: "+mouseLoc.getLon(),mouseX,mouseY);
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
