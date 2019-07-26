package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : a subclass that extends the Abstract EarthquakeMarker that represents ocean earthquake
*/

import static processing.core.PConstants.CENTER;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;
/**
 * <h1>OceanEarthQuakeMarker</h1>
 * <p>
 *   Defines the behavior of the EarthQuake Marker in Ocean
 * </p>
 * @author Hisham Maged
 * @version 1.1
 * @since 22/7/2019
 * @see AbstractEarthQuakeMarker
 */
public class OceanEarthQuakeMarker extends AbstractEarthQuakeMarker {

  /**
   * PointFeature constructor that initializes hierarchy of AbstractEarthQuakeMarker. uses super constructor to initialize implementation
   * sets the inherited onLand boolean to false
   * @param feature PointFeature that locates the earthquake
   * */
  public OceanEarthQuakeMarker(PointFeature feature) {
    super(feature);
    this.setOnLand(false);
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
    pg.rectMode(CENTER);
    pg.rect(x,y,this.radius*2,this.radius*2,10);
  }


}
