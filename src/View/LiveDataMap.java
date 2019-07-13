package View;
/*
  Author: Hisham Maged
  Date : 7/11/2019
  Project Name : A Map with visualized EarthQuakes data
*/

import Controller.EarthQuakesMarkerHandler;
import Controller.LifeExpectancyMarkerHandler;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import java.net.URL;
import java.util.List;
import processing.core.PApplet;

public class LiveDataMap extends PApplet {

  /* private ENUM made to choose a map*/
  private enum Map{
    EARTHQUAKES,LIFEEXPECTANCY;
  }
  // Used Map reference
  private UnfoldingMap map;
  // Earth quakes map
  private UnfoldingMap earthquakesMap;
  // used earthquakeMarkers reference
  private List<Marker> earthquakeMarkers;
  // used lifeExpectancyMarkers reference
  private List<Marker> lifeExpectancyMarkers;
  //LifeExpectancy Map reference
  private UnfoldingMap lifeExpectancyMap;

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
  * Colors for makres of light,moderat,intense earthquake markers
  * light: blue
  * moderate: yellow
  * intense : red
  * not static because color method can't be invoked in static context
  * */
  private final int LIGHT_EARTHQUAKE_COLOR = color(0,0,255);
  private final int MODERATE_EARTHQUAKE_COLOR = color(255,255,0);
  private final int INTENSE_EARTHQUAKE_COLOR = color(255,0,0);

  /*
  * Year used to get data of Life Expectancy of all countries
  * */
  private final static int LIFE_EXPECTANCY_YEAR = 2017;

  /*
  * key choosers for showing earthquake data or lifeExpectancy
  * */

  private static final char LIFE_EXPECTANCY_KEY = '2';
  private static final char EARTHQUAKES_KEY = '1';

  private Map usedMap = Map.EARTHQUAKES;

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
  }

  /*
  * a method that initializes both the earthquake map and life expectancy map to be of Microsoft Road Provider Map
  * and adds interactivity to the map
  * and uses the default XML File and gets the earthquakeMarkers of it using the EarthQuakesMarkerHandler Class
  * and uses the default CSV file for lifeExpectancy using the LifeExpectancyMarkerHandler class
  * and adds those earthquakeMarkers and life expectancy to the map
  * setting the default map reference to earthquake data maps
  *
  * */
  private void initMap()
  {

    // ===========================================  EarthQuakes Section ====================================


    this.earthquakesMap= new UnfoldingMap(this,200,50,1020,620,new Microsoft.RoadProvider());
    MapUtils.createDefaultEventDispatcher(this,this.earthquakesMap);
    /*
    * change first parameter in makeEarthQuakeMarkers
    * no 1st parameter >> FileChooser to choose ATOM file
    * String >> filePath
    * File >> Atom file object
    * URL >> live data url
    * */
    this.earthquakeMarkers = EarthQuakesMarkerHandler
        .makeEarthQuakeMarkers("./data/2.5_week.atom",LIGHT_EARTHQUAKE,MODERATE_EARTHQUAKE,INTENSE_EARTHQUAKE,LIGHT_EARTHQUAKE_COLOR,MODERATE_EARTHQUAKE_COLOR,INTENSE_EARTHQUAKE_COLOR);
    this.earthquakesMap.addMarkers(this.earthquakeMarkers);

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

    this.map = this.earthquakesMap;
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
    rect(30,50,140,250);
    textSize(13);
    fill(color(0));
    switch(this.usedMap)
    {
      case EARTHQUAKES:
        text("Legend",65,75);
        text(MODERATE_EARTHQUAKE+" + Magnitude",60,120);
        text(LIGHT_EARTHQUAKE+" + Magnitude",60,170);
        text("Below "+LIGHT_EARTHQUAKE,60,220);
        fill(color(255,0,0));
        ellipse(45,115,18,18);
        fill(color(255,255,0));
        ellipse(45,165,9,9);
        fill(color(255,0,0));
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

    }

  }
  /*
  * Key pressed functionality that changes between the earthquake map and life expectancy map
  * using final char EARTHQUAKES_KEY for earthquake data
  * using final char LIFE_EXPECTANCY_KEY for life expectancy data
  * */
  public void keyPressed()
  {
    if(key == EARTHQUAKES_KEY) {
      this.usedMap = Map.EARTHQUAKES;
      this.map = this.earthquakesMap;
    }
      else if (key == LIFE_EXPECTANCY_KEY)
    {
      this.usedMap = Map.LIFEEXPECTANCY;
      this.map = this.lifeExpectancyMap;
    }
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
