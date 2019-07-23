package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : A sub class implementing customizedMarker interface and extending AbstractLocationMarker common behaviour to make city markers
*/

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class CityMarker extends AbstractLocationMarker {

  private PImage img; //holds the image of the marker that will represent the location


  public CityMarker(Location location,PImage img) {
    super(location);
    this.img = img;
  }

  public CityMarker(Feature place,PImage img) {
    super(place);
    this.img = img;
  }

  /*
  * The actual method responsible for drawing the city marker based on implementation of AbstractLocationMarker
  * */
  @Override
  public void drawMarker(PGraphics pg, float x, float y)
  {
    pg.imageMode(PConstants.CORNER);
    pg.image(img,x-11,y-37);
  }

  // get city name of city Marker
  public String getCityName()
  {
    return getStringProperty("name");
  }

  // get country name
  public String getCountry()
  {
    return getStringProperty("country");
  }
  // get population of city
  public float getPopulation()
  {
    return Float.parseFloat(getStringProperty("population"));
  }
}
