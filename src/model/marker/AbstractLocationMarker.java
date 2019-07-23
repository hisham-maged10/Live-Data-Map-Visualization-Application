package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : An Abstract class that has the common implementation of the City Markers or Country Markers (world data)
*/

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

public abstract class AbstractLocationMarker extends SimplePointMarker implements CustomizedMarker{


  /*
  * Location constructor that makes initializes the simplePointMarker implementation using
  * super constructor
  * @Param Location of marker
  * */
  public AbstractLocationMarker(Location location)
  {
    super(location);
  }

  /*
  * A Point Feature constructor that initializes the simplePointMarker implementation using super
  * constructor that accepts location and properties
  * @Param : Feature that holds place of place
  * feature because the city data from GeoReader returns as List<Feature> and casted to do our implementaiton
  * */
  public AbstractLocationMarker(Feature place){
    super(((PointFeature)place).getLocation(),place.getProperties());
  }

  /*
  * Overriden method that draws the place markers or locattion markers using a customized
  * draw method due to subclasses using drawMarker() method from customizedMarker interface
  * @Param: PGraphics to draw marker
  * @Param: float x- coordinate
  * @Param: float y- coordinate
  * */
  @Override
  public void draw(PGraphics pg, float x, float y)
  {
    pg.pushStyle();
    this.drawMarker(pg,x,y); // draws the customized marker
    pg.popStyle();
  }
}
