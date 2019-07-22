package controller;/*
  Author: Hisham Maged
  Date : 7/21/2019
  Class Desc: A Utitlity Class Made for The Data Entries Pojo Objects from the World Data bank site (Wolrd-wide markers)
*/

import java.util.Collections;
import model.pojo.WorldData;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import processing.core.PApplet;

public class WorldDataUtils {

  // holds the countryFeature data got from the geoReader
  private static List<Feature> countryFeatures;
  // holds the CityFeature data from the geoReader
  private static List<Feature> cityFeatures;

  /*
   * method can't be used if given Iterable is null throwing UnsupportedOperationException
   * takes the Iterable of WorldData Records and uses it to output a Map<String,Float>  where key is Country Code
   * and Value is Life Expectancy for given year parameter that will be used by the Map
   * with Geographical locations to make a map out of it with needed value is values for the given year
   * @PARAM Iterable<? extends WorldBank> holding data of each record in the parsed CSV file
   * @PARAM year needed for the values, must be valid year between 1960 to 2017 inclusive
   * @NotNullable, @NOTEMPTY list
   * */
  public static Map<String,Float> toCountryCodeMap(Iterable<? extends WorldData> data, int year)
  {
    if(data == null )
      throw new UnsupportedOperationException("Can't use this method unless records are loaded");
    if(!isValidInput(year))
      throw new IllegalArgumentException("Year input must be between 1960 to 2017 inclusive, given year: "+year);
    Map<String,Float> lifeExpectancyMap = new HashMap<>();
    for(WorldData record : data)
    {
      lifeExpectancyMap.put(record.getCountryCode(),record.getCertainYearValue(year));
    }

    return lifeExpectancyMap;
  }


  /*
   * Make markers Method that takes the current PApplet, map that will be used to make markers from using the country and it's value
   * key: Country ID
   * and float value as Value assigned to that country id
   * initializes the countryFeatures List of the WorldDataUtils once as it's the same for all data
   * holding points for each place on the map using the JSON object data
   * that holds all locations of Map and ID element for each location matching the one in the Map
   * sent , to color the markers with shades of blue and red using the matching
   * ID value with year expectancy
   * then shading the colors of the markers made
   * @Param Currently used PApplet
   * @Param Map<String, Float> that holds the country code and value associated to it
   * @Param: float that represents the starting value of the input range that the value of map is in to be mapped to a valie between 0 to 255
   * @Param: float that represents the ending value of the input range that the value of map is in to be mapped to a valie between 0 to 255
   * */
  public static List<Marker> makeCountryMarkers(PApplet applet,Map<String,Float> map,float inputRangeStart,float inputRangeEnd)
  {

    // list of featrures representing all countries from JSON file
    if(WorldDataUtils.countryFeatures == null)
      WorldDataUtils.getCountryFeatures(applet);
    //list of markers made using the list of features using MapUtils method that makes markers
    // for given list<Feature> returning List<Marker>
    List<Marker> markers = MapUtils.createSimpleMarkers(WorldDataUtils.countryFeatures);
    //private helper method that shades the markers
    WorldDataUtils.shadeCountryMarkers(applet,markers,map,inputRangeStart,inputRangeEnd);
    return markers;
  }


  /*
   * a private helper method that sets the colors of the markers made by geo locations of countries
   * according to id of country from json object that matches id of  map
   * getting the value and mapping it to a value between 0 to 255
   * for eg. the life expectancy average range is from 40 to 90 so mapping that value to 0 to 255
   * to get the color representing it and using that color as blue shade and that color as red's difference value
   * making the high level expectancy be of blue shade
   * and low level expectancy be of red shade
   * @Param applet currently being used to be able to color the marker
   * @Param: List<Marker> that will be shaded
   * @Param:map containing IDS of countries from CSV matching JSON object of geo locations of countries as keys
   * and value as life expectancy of that location in given year
   * @Param: float inputRangeStart
   * @Param: float inputRangeEnd
   *
   * */
  private static void shadeCountryMarkers(PApplet applet, List<Marker> markers, Map<String,Float> map,float inputRangeStart,float inputRangeEnd)
  {
    String tempID = null;
    for(Marker m : markers)
    {
      tempID = m.getId();
      if(map.containsKey(tempID))
      {
        // maps the life expectancy which ranges from 40 to 90 to a color shade
        // if tends to 90 then color shade will be tending to 255 making red 0 and blue 255
        // so high life expectancy becomes blue shade and low life expectancy becomes red shade
        // as if value tends to (after being mapped) to 10 then red will be 245 and blue will be 10
        // giving red and shades between red and blue is ranging , red meaning low life expectency
        // and blue meaning high level expectency
        int colorShade = WorldDataUtils.mapColorShade(map.get(tempID),
            inputRangeStart,
            inputRangeEnd,
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

  /*
   * validates that year is in between 1960 to 2017 inclusive as world bank data that we have
   * is from 1960 to 2017 only
   * @PARAM year needed to get life expectancy map containing all countries and their life expectancy value for that year
   * */
  private static boolean isValidInput(int year)
  {
    return year >= 1960 && year <=2017;
  }

  /*
  * Loads the data of country json into countryFeatures private field only once, then returns the same list each time
  * returns an unmodefiable list of features so it becomes secure in case of reference sharing
  * @Param: PApplet current pApplet used
  * */
  public static List<Feature> getCountryFeatures(PApplet applet)
  {
    if(WorldDataUtils.countryFeatures == null)
      return WorldDataUtils.countryFeatures = Collections.unmodifiableList(GeoJSONReader.loadData(applet,"./data/countries.geo.json"));
    else
      return Collections.unmodifiableList(WorldDataUtils.countryFeatures);
  }
  /*
   * Loads the data of city json into countryFeatures private field only once, then returns the same list each time
   * returns an unmodefiable list of features so it becomes secure in case of reference sharing
   * @Param: PApplet current pApplet used
   * */
  public static List<Feature> getCityFeatures(PApplet applet)
  {
    if(WorldDataUtils.cityFeatures == null)
      return WorldDataUtils.cityFeatures = Collections.unmodifiableList(GeoJSONReader.loadData(applet,"./data/city-data.json"));
    else
      return Collections.unmodifiableList(WorldDataUtils.cityFeatures);
  }

}
