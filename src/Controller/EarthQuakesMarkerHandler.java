package Controller;/*
  Author: Hisham Maged
  Date : 7/11/2019
  Project Name : A Class Made to handle Markers and returns List<Marker> for immediate usage
*/

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthQuakesMarkerHandler {

  /*
  *  a static method to make EarthQuake Markers given the light value and moderate and high values'
  *  giving a color for each level using the next 3 parameters for colors
  *  also, given an XmLFile, the EarthQuakesFeedParser class will parse it, if Valid then used to make the List<PointFeature>
  *  that will be used to make a marker per each using the Location of it and its properties
  *  where the magnitude property will be used to set the color accordingly
  *  also the markers, the highest magnitude will have larger size using stroke, moderate will have medium size using stroke
  *  and low will have no stroke so normal size for it
  * File Version
  * */
  public static List<Marker> makeEarthQuakeMarkers(File xmlFile,double light, double moderate, double high,int lightColor, int moderateColor, int highColor)
  {
    List<Marker> markers = new ArrayList<>();
    List<PointFeature> features = new EarthQuakesFeedParser(xmlFile).getParsedFeatures();
    SimplePointMarker tempMarker = null; // to hold reference to the newly added marker
    Object tempPropertyHolder = null ; // to store the property from the map of each point feature as the value of its hashMap is Object
    double tempMag = 0.0; // the Object Property is converted into a double for checking to put color according to magnitude

    for(PointFeature pf : features) {
      markers.add((tempMarker = new SimplePointMarker()));
      tempMarker.setLocation(pf.getLocation());
      tempMarker.setProperties(pf.getProperties());
      tempPropertyHolder = pf.getProperty("magnitude");
      tempMag = Double.valueOf(tempPropertyHolder.toString());
      if (tempMag >= light && tempMag < moderate)
      {
        tempMarker.setColor(moderateColor);
        tempMarker.setStrokeColor(moderateColor);
        tempMarker.setStrokeWeight(3);
      }
      else if (tempMag < light)
        tempMarker.setColor(lightColor);
      else {
        tempMarker.setColor(highColor);
        tempMarker.setStrokeColor(highColor);
        tempMarker.setStrokeWeight(12);
      }
    }

    return markers;
  }

  // overloaded String version
  public static List<Marker> makeEarthQuakeMarkers(String xmlFilePath,double light, double moderate, double high,int lightColor, int moderateColor, int highColor)
  {
    List<Marker> markers = new ArrayList<>();
    List<PointFeature> features = new EarthQuakesFeedParser(xmlFilePath).getParsedFeatures();
    SimplePointMarker tempMarker = null; // to hold reference to the newly added marker
    Object tempPropertyHolder = null ; // to store the property from the map of each point feature as the value of its hashMap is Object
    double tempMag = 0.0; // the Object Property is converted into a double for checking to put color according to magnitude


    for(PointFeature pf : features) {
      markers.add((tempMarker = new SimplePointMarker()));
      tempMarker.setLocation(pf.getLocation());
      tempMarker.setProperties(pf.getProperties());
      tempPropertyHolder = pf.getProperty("magnitude");
      tempMag = Double.valueOf(tempPropertyHolder.toString());
      if (tempMag >= light && tempMag < moderate)
      {
        tempMarker.setColor(moderateColor);
        tempMarker.setStrokeColor(moderateColor);
        tempMarker.setStrokeWeight(3);
      }
      else if (tempMag < light)
        tempMarker.setColor(lightColor);
      else {
        tempMarker.setColor(highColor);
        tempMarker.setStrokeColor(highColor);
        tempMarker.setStrokeWeight(12);
      }
    }

    return markers;
  }

  // overloaded URL version
  public static List<Marker> makeEarthQuakeMarkers(URL xmlFileURL,double light, double moderate, double high,int lightColor, int moderateColor, int highColor)
  {
    List<Marker> markers = new ArrayList<>();
    List<PointFeature> features = new EarthQuakesFeedParser(xmlFileURL).getParsedFeatures();
    SimplePointMarker tempMarker = null; // to hold reference to the newly added marker
    Object tempPropertyHolder = null ; // to store the property from the map of each point feature as the value of its hashMap is Object
    double tempMag = 0.0; // the Object Property is converted into a double for checking to put color according to magnitude


    for(PointFeature pf : features) {
      markers.add((tempMarker = new SimplePointMarker()));
      tempMarker.setLocation(pf.getLocation());
      tempMarker.setProperties(pf.getProperties());
      tempPropertyHolder = pf.getProperty("magnitude");
      tempMag = Double.valueOf(tempPropertyHolder.toString());
      if (tempMag >= light && tempMag < moderate)
      {
        tempMarker.setColor(moderateColor);
        tempMarker.setStrokeColor(moderateColor);
        tempMarker.setStrokeWeight(3);
      }
      else if (tempMag < light)
        tempMarker.setColor(lightColor);
      else {
        tempMarker.setColor(highColor);
        tempMarker.setStrokeColor(highColor);
        tempMarker.setStrokeWeight(12);
      }
    }

    return markers;
  }

  // overloaded FileChooser version
  public static List<Marker> makeEarthQuakeMarkers(double light, double moderate, double high,int lightColor, int moderateColor, int highColor)
  {
    List<Marker> markers = new ArrayList<>();
    List<PointFeature> features = new EarthQuakesFeedParser().getParsedFeatures();
    SimplePointMarker tempMarker = null; // to hold reference to the newly added marker
    Object tempPropertyHolder = null ; // to store the property from the map of each point feature as the value of its hashMap is Object
    double tempMag = 0.0; // the Object Property is converted into a double for checking to put color according to magnitude


    for(PointFeature pf : features) {
      markers.add((tempMarker = new SimplePointMarker()));
      tempMarker.setLocation(pf.getLocation());
      tempMarker.setProperties(pf.getProperties());
      tempPropertyHolder = pf.getProperty("magnitude");
      tempMag = Double.valueOf(tempPropertyHolder.toString());
      if (tempMag >= light && tempMag < moderate)
      {
        tempMarker.setColor(moderateColor);
        tempMarker.setStrokeColor(moderateColor);
        tempMarker.setStrokeWeight(3);
      }
      else if (tempMag < light)
        tempMarker.setColor(lightColor);
      else {
        tempMarker.setColor(highColor);
        tempMarker.setStrokeColor(highColor);
        tempMarker.setStrokeWeight(12);
      }
    }

    return markers;
  }

}
