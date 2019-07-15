package Controller;/*
  Author: Hisham Maged
  Date : 7/12/2019
  Project Name : a class that makes markers for life expectancy values given to it and JSON file for Geographic content on map
*/

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import processing.core.PApplet;

public class LifeExpectancyMarkerHandler {

  /*
  * File Version
  * Make markers Method that takes the current PApplet, year
  * uses the LifeExpectancyFeedParser to get the Map<String,Float> key: Country ID
  * and float as lifeExpectancyValue for the country id for the given year parameter
  * making a List<Feature> holding points for each place on the map using the JSON object data
  * that holds all locations of Map and ID element for each location matching the one in the Map
  * of LifeExpectancyFeedParser to color the markers with shades of blue and red using the matching
  * ID value with year expectancy
  * then shading the colors of the markers made
  * @Param Currently used PApplet
  * @Param year for life expectancy values (between 1960 and 2017 inclusive )
  * */
  public static List<Marker> makeLifeExpectancyMarkers(PApplet applet,File file, int year)
  {
    // list of featrures representing all countries from JSON file
    List<Feature> countries = GeoJSONReader.loadDataFromJSON(applet,"./data/countries.geo.json");
    //list of markers made using the list of features using MapUtils method that makes markers
    // for given list<Feature> returning List<Marker>
    List<Marker> markers = MapUtils.createSimpleMarkers(countries);
    //private helper method that shades the markers
    LifeExpectancyMarkerHandler.shadeMarkers(applet,markers,new LifeExpectancyFeedParser(file)
                                                     .getLifeExpectancyMap(year)
    );
    return markers;
  }
  /*
  * Overloaded String Version
  * */
  public static List<Marker> makeLifeExpectancyMarkers(PApplet applet,String filePath, int year)
  {
    // list of featrures representing all countries from JSON file
    List<Feature> countries = GeoJSONReader.loadData(applet,"./data/countries.geo.json");
    //list of markers made using the list of features using MapUtils method that makes markers
    // for given list<Feature> returning List<Marker>
    List<Marker> markers = MapUtils.createSimpleMarkers(countries);
    //private helper method that shades the markers
    LifeExpectancyMarkerHandler.shadeMarkers(applet,markers,new LifeExpectancyFeedParser(filePath)
        .getLifeExpectancyMap(year)
    );
    return markers;
  }
  /*
   * Overloaded URL Version
   * */
  public static List<Marker> makeLifeExpectancyMarkers(PApplet applet, URL fileURL, int year)
  {
    // list of featrures representing all countries from JSON file
    List<Feature> countries = GeoJSONReader.loadDataFromJSON(applet,"./data/countries.geo.json");
    //list of markers made using the list of features using MapUtils method that makes markers
    // for given list<Feature> returning List<Marker>
    List<Marker> markers = MapUtils.createSimpleMarkers(countries);
    //private helper method that shades the markers
    LifeExpectancyMarkerHandler.shadeMarkers(applet,markers,new LifeExpectancyFeedParser(fileURL)
        .getLifeExpectancyMap(year)
    );
    return markers;
  }

  /*
   * Overloaded File Chooser Version
   * */
  public static List<Marker> makeLifeExpectancyMarkers(PApplet applet, int year)
  {
    // list of featrures representing all countries from JSON file
    List<Feature> countries = GeoJSONReader.loadDataFromJSON(applet,"./data/countries.geo.json");
    //list of markers made using the list of features using MapUtils method that makes markers
    // for given list<Feature> returning List<Marker>
    List<Marker> markers = MapUtils.createSimpleMarkers(countries);
    //private helper method that shades the markers
    LifeExpectancyMarkerHandler.shadeMarkers(applet,markers,new LifeExpectancyFeedParser()
        .getLifeExpectancyMap(year)
    );
    return markers;
  }


  /*
  * a private helper method that sets the colors of the markers made by geo locations of countries
  * according to id of country from jsob object that matches id of lifeExpectency map
  * getting the life expectency value mapping it to a value between 0 to 255
  * as the life expectancy average range is from 40 to 90 so mapping that value to 0 to 255
  * to get the color representing it and using that color as blue shade and that color as red's difference value
  * making the high level expectancy be of blue shade
  * and low level expectancy be of red shade
  * @Param applet currently being used to be able to color the marker
  * lifeExpectancy map containing IDS of countries from CSV matching JSON object of geo locations of countries as keys
  * and value as life expectancy of that location in given year
  * */
  private static void shadeMarkers(PApplet applet,List<Marker> markers, Map<String,Float> lifeExpectancyMap)
  {
    String tempID = null;
    for(Marker m : markers)
    {
      tempID = m.getId();
      if(lifeExpectancyMap.containsKey(tempID))
      {
        // maps the life expectancy which ranges from 40 to 90 to a color shade
        // if tends to 90 then color shade will be tending to 255 making red 0 and blue 255
        // so high life expectancy becomes blue shade and low life expectancy becomes red shade
        // as if value tends to (after being mapped) to 10 then red will be 245 and blue will be 10
        // giving red and shades between red and blue is ranging , red meaning low life expectency
        // and blue meaning high level expectency
        int colorShade = LifeExpectancyMarkerHandler.mapColorShade(lifeExpectancyMap.get(tempID),
                                                                   40.0F,
                                                                    90.0F,
                                                                    10,
                                                                       255);
        m.setColor(applet.color(255-colorShade,100,colorShade));

      }else
        m.setColor(applet.color(150));
    }
  }

  /*
  * this private static helper method takes an input value in a certain range to map it to other value in
  * another range
  * @param input: value to be mapped
  * @param inputStart : start of range of input value
  * @Param inputEnd : end of range of input value
  * @Param outoutStart : start of range to be mapped to
  * @Param outputEnd : end of range to be mapped to
  * to understand this more check:
  * https://stackoverflow.com/questions/5731863/mapping-a-numeric-range-onto-another
  * */
  private static int mapColorShade(float input, float inputStart, float inputEnd, float outputStart, float outputEnd)
  {

    float slope = (outputEnd - outputStart) / (inputEnd - inputStart);
    float output = outputStart + Math.round(slope * (input - inputStart));
    return (int)output;
  }
}
