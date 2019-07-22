package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : A sub class implementing customizedMarker interface and extending AbstractLocationMarker common behaviour to make city markers
*/

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PGraphics;

public class CityMarker extends AbstractLocationMarker {

  public CityMarker(Location location) {
    super(location);
  }

  public CityMarker(Feature place) {
    super(place);
  }

  /*
  * The actual method responsible for drawing the city marker based on implementation of AbstractLocationMarker
  * */
  @Override
  public void drawMarker(PGraphics pg, float x, float y)
  {
    // TODO: implement drawing technique
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
