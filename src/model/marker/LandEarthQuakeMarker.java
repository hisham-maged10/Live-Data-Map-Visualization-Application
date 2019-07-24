package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : a subclass extending the AbstractEarthQuakeMarker common behaviour to draw a custom shape marker for land markers
*/

import static processing.core.PConstants.CENTER;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;
import processing.core.PImage;

public class LandEarthQuakeMarker extends AbstractEarthQuakeMarker{

  /*
  * PointFeature constructor that initializes hierarchy of AbstractEarthQuakeMarker using super constructor
  * sets the inherited onLand boolean to true
  * @Param:PointFeature feature that locates the earthquake
  * */
  public LandEarthQuakeMarker(PointFeature feature) {
    super(feature);
    this.setOnLand(true);
  }

  /*
  * Main method that draws the customized shape of the land earthquake marker
  * @Param: PGraphics that draws the earthquake
  * @Param: float x-coordinate
  * @Param: float y-coordinate
  * */
  @Override
  public void drawMarker(PGraphics pg, float x, float y)
  {
    pg.noFill();
    pg.ellipse(x,y,this.radius*2,this.radius*2);

  }

  // get the country that the earthquake is in
  public String getCountry()
  {
    return getStringProperty("country");
  }
}
