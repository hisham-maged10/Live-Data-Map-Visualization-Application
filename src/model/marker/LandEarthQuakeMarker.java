package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : a subclass extending the AbstractEarthQuakeMarker common behaviour to draw a custom shape marker for land markers
*/


import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/**
 * <h1>LandEarthQuakeMarker</h1>
 * <p>
 *   Defines the behavior of the EarthQuake Marker on Land
 * </p>
 * @author Hisham Maged
 * @version 1.1
 * @since 22/7/2019
 * @see AbstractEarthQuakeMarker
 */
public class LandEarthQuakeMarker extends AbstractEarthQuakeMarker{

  /**
  * PointFeature constructor that initializes hierarchy of AbstractEarthQuakeMarker. uses super constructor to initialize implementation
  * sets the inherited onLand boolean to true
  * @param feature PointFeature that locates the earthquake
  * */
  public LandEarthQuakeMarker(PointFeature feature) {
    super(feature);
    this.setOnLand(true);
  }

  /**
  * Draws the customized shape of the land earthquake marker
  * @param pg PGraphics that draws the earthquake
  * @param x float x-coordinate
  * @param y float y-coordinate
  * */
  @Override
  public void drawMarker(PGraphics pg, float x, float y)
  {
    pg.noFill();
    pg.ellipse(x,y,this.radius*2,this.radius*2);

  }

  /**
   * Gets the country that the earthquake is in
   * @return The Country name that the earthquake occurred in
   */
  public String getCountry()
  {
    return getStringProperty("country");
  }
}
