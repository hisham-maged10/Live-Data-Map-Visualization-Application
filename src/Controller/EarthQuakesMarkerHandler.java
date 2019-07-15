package Controller;/*
  Author: Hisham Maged
  Date : 7/11/2019
  Project Name : A Class Made to handle Markers and returns List<Marker> for immediate usage
*/

import Model.EarthQuakeEntry;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.stage.Stage;
import processing.core.PApplet;

public class EarthQuakesMarkerHandler {


  /*
  * public static enum that is used in the LiveDataMap class to specify whether to make Depth Marker or Magnitude Marker
  * */
  public enum EarthQuakeMarker{
    MAGNITUDE,DEPTH;
  }

  // holds unmodefiable list of latest entries Made
  public static List<EarthQuakeEntry> lastMadeEntries = Collections.EMPTY_LIST; //unmodefiable


  /*
  * File Version
  *  a static method to make EarthQuake Markers given an XmLFile, the EarthQuakesFeedParser class will parse it, if Valid then used to make the List<PointFeature>
  *  that will be used to make a marker per each using the Location of it and its properties
  *  using the PointFeature version of the earthquake using the EarthQuakesFeedParser setting the properties of
  *  the marker using them
  *  and the Filter vararg is used to filter the to specified needed data by filter objects
  * @Param: XMLFile to parse the RSS Feed from
  * @Param: VARARG Filter to apply on parsed data
  * */
  public static List<SimplePointMarker> makeEarthQuakeMarkers(File xmlFile,EarthQuakesFeedParser.EarthQuakeFilter... filters)
  {
    List<SimplePointMarker> markers = new ArrayList<>();
    List<EarthQuakeEntry> entries = new EarthQuakesFeedParser(xmlFile).getParsedQuakeEntries(); // gets the List containing Pojo objects of earth quake records
    List<PointFeature> features = EarthQuakesFeedParser.filterIntoPointFeatures(entries,filters); //gets the filtered PointFeatures
    SimplePointMarker tempMarker = null; // to hold reference to the newly added marker
    for(PointFeature pf : features) {
      markers.add((tempMarker = new SimplePointMarker()));
      tempMarker.setLocation(pf.getLocation());
      tempMarker.setProperties(pf.getProperties());
    }

    lastMadeEntries = Collections.unmodifiableList(EarthQuakesFeedParser.filterEarthQuakeEntries(entries,filters)); // very bad (doing same operation twice) filtered again for GUI API USAGE //TODO: REFEACTOR LATER

    return markers;
  }

  /*
   * String Version
   *  a static method to make EarthQuake Markers given an XmLFile, the EarthQuakesFeedParser class will parse it, if Valid then used to make the List<PointFeature>
   *  that will be used to make a marker per each using the Location of it and its properties
   *  using the PointFeature version of the earthquake using the EarthQuakesFeedParser setting the properties of
   *  the marker using them
   *  and the Filter vararg is used to filter the to specified needed data by filter objects
   * @Param: String containing the filepath of the file that is going to be used to parse the RSS Feed from
   * @Param: VARARG Filter to apply on parsed data
   * */
  public static List<SimplePointMarker> makeEarthQuakeMarkers(String xmlFilePath,EarthQuakesFeedParser.EarthQuakeFilter... filters)
  {
    List<SimplePointMarker> markers = new ArrayList<>();
    List<EarthQuakeEntry> entries = new EarthQuakesFeedParser(xmlFilePath).getParsedQuakeEntries(); // gets the List containing Pojo objects of earth quake records
    List<PointFeature> features = EarthQuakesFeedParser.filterIntoPointFeatures(entries,filters); //gets the filtered PointFeatures
    SimplePointMarker tempMarker = null; // to hold reference to the newly added marker
    for(PointFeature pf : features) {
      markers.add((tempMarker = new SimplePointMarker()));
      tempMarker.setLocation(pf.getLocation());
      tempMarker.setProperties(pf.getProperties());
    }

    lastMadeEntries = Collections.unmodifiableList(EarthQuakesFeedParser.filterEarthQuakeEntries(entries,filters)); // very bad (doing same operation twice) filtered again for GUI API USAGE //TODO: REFEACTOR LATER

    return markers;
  }

  /*
   * URL Version (Live)
   *  a static method to make EarthQuake Markers given an XmLFile, the EarthQuakesFeedParser class will parse it, if Valid then used to make the List<PointFeature>
   *  that will be used to make a marker per each using the Location of it and its properties
   *  using the PointFeature version of the earthquake using the EarthQuakesFeedParser setting the properties of
   *  the marker using them
   *  and the Filter vararg is used to filter the to specified needed data by filter objects
   * @Param: URL that is going to be used to get data from (live version) to parse the RSS Feed from
   * @Param: VARARG Filter to apply on parsed data
   * */
  public static List<SimplePointMarker> makeEarthQuakeMarkers(URL xmlFileURL,EarthQuakesFeedParser.EarthQuakeFilter... filters)
  {
    List<SimplePointMarker> markers = new ArrayList<>();
    List<EarthQuakeEntry> entries = new EarthQuakesFeedParser(xmlFileURL).getParsedQuakeEntries(); // gets the List containing Pojo objects of earth quake records
    List<PointFeature> features = EarthQuakesFeedParser.filterIntoPointFeatures(entries,filters); //gets the filtered PointFeatures
    SimplePointMarker tempMarker = null; // to hold reference to the newly added marker
    for(PointFeature pf : features) {
      markers.add((tempMarker = new SimplePointMarker()));
      tempMarker.setLocation(pf.getLocation());
      tempMarker.setProperties(pf.getProperties());
    }

    lastMadeEntries = Collections.unmodifiableList(EarthQuakesFeedParser.filterEarthQuakeEntries(entries,filters)); // very bad (doing same operation twice) filtered again for GUI API USAGE //TODO: REFEACTOR LATER

    return markers;
  }

  /*
   * File chooser swing Version
   *  a static method to make EarthQuake Markers given an XmLFile, the EarthQuakesFeedParser class will parse it, if Valid then used to make the List<PointFeature>
   *  that will be used to make a marker per each using the Location of it and its properties
   *  using the PointFeature version of the earthquake using the EarthQuakesFeedParser setting the properties of
   *  the marker using them
   *  and the Filter vararg is used to filter the to specified needed data by filter objects
   * @Param: the stage that the file chooser will be opened from
   * @Param: VARARG Filter to apply on parsed data
   * */
  public static List<SimplePointMarker> makeEarthQuakeMarkers(EarthQuakesFeedParser.EarthQuakeFilter... filters)
  {
    List<SimplePointMarker> markers = new ArrayList<>();
    List<EarthQuakeEntry> entries = new EarthQuakesFeedParser().getParsedQuakeEntries(); // gets the List containing Pojo objects of earth quake records
    List<PointFeature> features = EarthQuakesFeedParser.filterIntoPointFeatures(entries,filters); //gets the filtered PointFeatures
    SimplePointMarker tempMarker = null; // to hold reference to the newly added marker
    for(PointFeature pf : features) {
      markers.add((tempMarker = new SimplePointMarker()));
      tempMarker.setLocation(pf.getLocation());
      tempMarker.setProperties(pf.getProperties());
    }
    lastMadeEntries = Collections.unmodifiableList(EarthQuakesFeedParser.filterEarthQuakeEntries(entries,filters)); // very bad (doing same operation twice) //TODO: REFEACTOR LATER
    return markers;
  }

  /*
   * File chooser Version
   *  a static method to make EarthQuake Markers given an XmLFile, the EarthQuakesFeedParser class will parse it, if Valid then used to make the List<PointFeature>
   *  that will be used to make a marker per each using the Location of it and its properties
   *  using the PointFeature version of the earthquake using the EarthQuakesFeedParser setting the properties of
   *  the marker using them
   *  and the Filter vararg is used to filter the to specified needed data by filter objects
   * @Param: the stage that the file chooser will be opened from
   * @Param: VARARG Filter to apply on parsed data
   * */
  public static List<SimplePointMarker> makeEarthQuakeMarkers(Stage stage,EarthQuakesFeedParser.EarthQuakeFilter... filters)
  {
    List<SimplePointMarker> markers = new ArrayList<>();
    List<EarthQuakeEntry> entries = new EarthQuakesFeedParser(stage).getParsedQuakeEntries(); // gets the List containing Pojo objects of earth quake records
    List<PointFeature> features = EarthQuakesFeedParser.filterIntoPointFeatures(entries,filters); //gets the filtered PointFeatures
    SimplePointMarker tempMarker = null; // to hold reference to the newly added marker
    for(PointFeature pf : features) {
      markers.add((tempMarker = new SimplePointMarker()));
      tempMarker.setLocation(pf.getLocation());
      tempMarker.setProperties(pf.getProperties());
    }
    lastMadeEntries = Collections.unmodifiableList(EarthQuakesFeedParser.filterEarthQuakeEntries(entries,filters)); // very bad (doing same operation twice) filtered again for GUI API USAGE//TODO: REFEACTOR LATER

    return markers;
  }

  /*
   * same data Version, made for filtering API for same data and for GUI API flexibility
   *  a static method to make EarthQuake Markers given an XmLFile, the EarthQuakesFeedParser class will parse it, if Valid then used to make the List<PointFeature>
   *  that will be used to make a marker per each using the Location of it and its properties
   *  using the PointFeature version of the earthquake using the EarthQuakesFeedParser setting the properties of
   *  the marker using them
   *  and the Filter vararg is used to filter the to specified needed data by filter objects
   * @Param: VARARG Filter to apply on parsed data
   * */
  public static List<SimplePointMarker> makeEarthQuakeMarkers(List<EarthQuakeEntry> entries,EarthQuakesFeedParser.EarthQuakeFilter... filters)
  {
    List<SimplePointMarker> markers = new ArrayList<>();
    List<PointFeature> features = EarthQuakesFeedParser.filterIntoPointFeatures(entries,filters); //gets the filtered PointFeatures
    SimplePointMarker tempMarker = null; // to hold reference to the newly added marker
    for(PointFeature pf : features) {
      markers.add((tempMarker = new SimplePointMarker()));
      tempMarker.setLocation(pf.getLocation());
      tempMarker.setProperties(pf.getProperties());
    }
    lastMadeEntries = Collections.unmodifiableList(EarthQuakesFeedParser.filterEarthQuakeEntries(entries,filters)); // very bad (doing same operation twice) filtered again for GUI API USAGE//TODO: REFEACTOR LATER

    return markers;
  }

  /*
  * public static method that colors the EarthQuakeMarkers and customizes its shape based on its magnitude
  * giving each of light, moderate, intense magnitude earthquake a separate color using ScreenPosition method
  * and it's called in the draw method of the map
  * @Param: Currently used PApplet
  * @Param: currently used Map object
  * @Param: List<SimplePointMarker> containing the point markers made with earthquake entries using their PointFeature
  * @Param: Type of Marker using the public static enum of EarthQuakesMarkerHandler class
  * @Param: magnitude of light earthquake
  * @Param: magnitude of Intense earthquake
  * @Param: magnitude of moderate earthquake
  * @Param: color of light earthquake
  * @Param: color of moderate earthquake
  * @Param: color of intense earthquake
  * */
  public static void showMarkers(PApplet current,UnfoldingMap map, List<SimplePointMarker> markers,EarthQuakeMarker type,double LIGHT_VALUE,double MODERATE_VALUE,double INTENSE_VALUE,int LIGHT_COLOR,int MODERATE_COLOR,int INTENSE_COLOR)
  {
    switch(type)
    {
      case MAGNITUDE:
        ScreenPosition testMarker = null;
        String magnitude = null;
        double magnitudeV = 0.0;
        for(SimplePointMarker m : markers) {
          testMarker = m.getScreenPosition(map);
          magnitude = m.getProperty("magnitude").toString();
          magnitudeV = Double.parseDouble(magnitude);
          current.noFill();
          if (magnitudeV >= LIGHT_VALUE && magnitudeV < MODERATE_VALUE)
          {
            current.strokeWeight(5);
            current.stroke(MODERATE_COLOR);
            current.ellipse(testMarker.x,testMarker.y,19,19);
          }
          else if (magnitudeV < LIGHT_VALUE) {
            current.strokeWeight(4);
            current.stroke(LIGHT_COLOR);
            current.ellipse(testMarker.x,testMarker.y,14,14);
          }
          else {
            current.strokeWeight(10);
            current.stroke(INTENSE_COLOR);
            current.ellipse(testMarker.x,testMarker.y,30,30);
          }
        }
        break;
      case DEPTH:
        ScreenPosition testMarkerDepth = null;
        String depth = null;
        double depthV = 0.0;
        for(SimplePointMarker m : markers) {
          testMarkerDepth = m.getScreenPosition(map);
          depth = m.getProperty("depth").toString();
          depthV = Double.parseDouble(depth);
          current.noFill();
          if (depthV >= MODERATE_VALUE && depthV < INTENSE_VALUE)
          {
            current.strokeWeight(8);
            current.stroke(MODERATE_COLOR);
            current.ellipse(testMarkerDepth.x,testMarkerDepth.y,25,25);
          }
          else if (depthV > LIGHT_VALUE && depthV < MODERATE_VALUE) {
            current.strokeWeight(4);
            current.stroke(LIGHT_COLOR);
            current.ellipse(testMarkerDepth.x,testMarkerDepth.y,14,14);
          }
          else {
            current.strokeWeight(10);
            current.stroke(INTENSE_COLOR);
            current.ellipse(testMarkerDepth.x,testMarkerDepth.y,36,36);
          }
        }
        break;
    }
  }

}
