package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : A sub class implementing customizedMarker interface and extending AbstractLocationMarker common behaviour to make city markers
*/

import static processing.core.PConstants.CORNER;
import static processing.core.PConstants.LEFT;

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
    pg.image(img,x-15,y-37);
  }
  /*
   * shows the city, country, population of a city marker
   * @Param: PGraphics pg that is used for rendering
   * @Param: float x-coordinate
   * @Param: float y-coordinate
   * */
  @Override
  public void showTitle(PGraphics pg, float x, float y)
  {
    pg.fill(245,240,208);
    pg.noStroke();
    pg.rectMode(CORNER);
    pg.textSize(12);
    String desc = getCityName()+" , "+getCountry()+" , Population: "+getPopulation()+" m";
    pg.rect(x,y-40,pg.textWidth(desc)+10,20);
    pg.textAlign(LEFT);
    pg.fill(0);
    pg.text(desc,x+5,y-25);
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
