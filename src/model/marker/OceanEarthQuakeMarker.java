package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : a subclass that extends the Abstract EarthquakeMarker that represents ocean earthquake
*/

import static processing.core.PConstants.CENTER;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

public class OceanEarthQuakeMarker extends AbstractEarthQuakeMarker {

  /*
   * PointFeature constructor that initializes hierarchy of AbstractEarthQuakeMarker using super constructor
   * sets the inherited onLand boolean to true
   * @Param:PointFeature feature that locates the earthquake
   * */
  public OceanEarthQuakeMarker(PointFeature feature) {
    super(feature);
    this.setOnLand(false);
  }

  /*
   * Main method that draws the customized shape of the ocean earthquake marker
   * @Param: PGraphics that draws the earthquake
   * @Param: float x-coordinate
   * @Param: float y-coordinate
   * */
  @Override
  public void drawMarker(PGraphics pg, float x, float y)
  {
    pg.noFill();
    pg.rectMode(CENTER);
    pg.rect(x,y,this.radius*2,this.radius*2,10);
  }


}
