package model.marker;/*
  Author: Hisham Maged
  Date : 7/28/2019
  Project Name : 
*/

import static processing.core.PConstants.CORNER;
import static processing.core.PConstants.LEFT;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PGraphics;
//TODO: IMPLEMEN METHOD BODIES
/**
 * <h1>Airport Marker</h1>
 * <p>
 *   Defines the behaviour of the Airportt Marker of the map
 * </p>
 * @author Hisham Maged
 * @since 22/7/2019
 * @version 1.1
 * @see AbstractLocationMarker
 * @see CustomizedMarker
 */
public class AirportMarker extends AbstractLocationMarker {


  /**
   * Location constructor that makes initializes the simplePointMarker implementation using super
   * constructor
   *
   * @param location Location of place to make marker for
   */
  public AirportMarker(Location location) {
    super(location);
  }

  /**
   * A Point Feature constructor that initializes the simplePointMarker implementation using super
   * constructor that accepts location and properties
   *
   * @param place Feature that holds place of place feature because the Airport data
   * returns as List<Feature> and casted to do our implementaiton
   */
  public AirportMarker(Feature place) {
    super(place);
    this.setId(place.getId());
  }

  /**
   * Draws the Custom shape of the marker
   *
   * @param pg PGraphics for rendering the marker shape
   * @param x float x-coordinate
   * @param y float y-coordinate
   */
  @Override
  public void drawMarker(PGraphics pg, float x, float y) {
    pg.fill(255,0,0);
    pg.noStroke();
    pg.circle(x,y,5);
  }

  /**
   * Draws the Title of the Marker, showing information about it
   *
   * @param pg PGraphics for rendering the Title
   * @param x float x-coordinate
   * @param y float y-coordinate
   */
  @Override
  public void showTitle(PGraphics pg, float x, float y) {
    pg.fill(245,240,208);
    pg.noStroke();
    pg.rectMode(CORNER);
    pg.textSize(12);
    String desc = getCityName()+" , "+getCountry()+" , Code: "+getCode()+" , TMZ: "+getTimeZone()+" UTC";
    pg.rect(x,y-20,pg.textWidth(desc)+10,20);
    pg.textAlign(LEFT);
    pg.fill(0);
    pg.text(desc,x+5,y-7);
  }

  /**
   * Gets the 3-letter IATA Code for the Airport
   * @return 3-Letter IATA code for the Airport.
   */
  public String getCode()
  {
    return this.getStringProperty("code");
  }

  /**
   * Gets the Timezone of Airport
   * @return Timezone of Airport
   */
  public String getTimeZone()
  {
    return this.getStringProperty("timezone");
  }


}
