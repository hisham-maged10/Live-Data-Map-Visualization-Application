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

/**
 * <h1>City Marker</h1>
 * <p>
 *   Defines the behaviour of the City Marker of the map
 * </p>
 * @author Hisham Maged
 * @since 22/7/2019
 * @version 1.1
 * @see AbstractLocationMarker
 * @see CustomizedMarker
 */
public class CityMarker extends AbstractLocationMarker {

  private PImage img; //holds the image of the marker that will represent the location

  /**
   * Location Constructor that takes a location of the city to make a marker of.
   * @param location location of the city
   * @param img image that the marker is gonna be rendered into
   */
  public CityMarker(Location location,PImage img) {
    super(location);
    this.img = img;
  }

  /**
   * Feature constructtor that takes a feature containing the location of the city to make a marker of.
   * @param place Feature holding location of the city
   * @param img image that the marker is gonna be rendered into
   */
  public CityMarker(Feature place,PImage img) {
    super(place);
    this.img = img;
  }

  /**
  * draws the city marker based on implementation of AbstractLocationMarker
   * @param pg PGraphics to render the marker
   * @param x float x-coordinate
   * @param y float y-coordinate
  * */
  @Override
  public void drawMarker(PGraphics pg, float x, float y)
  {
    pg.imageMode(PConstants.CORNER);
    pg.image(img,x-15,y-37);
  }
  /**
   * Shows the city, country, population of a city marker
   * @param pg PGraphics that is used for rendering
   * @param x float x-coordinate
   * @param y float y-coordinate
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

  /**
   * Gets population of this city in millions
    * @return Population of this city in millions as a float
   */
  public float getPopulation()
  {
    return Float.parseFloat(getStringProperty("population"));
  }
}
