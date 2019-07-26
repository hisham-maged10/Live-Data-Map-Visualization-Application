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

/**
 * <h1>AbstractLocationMarker</h1>
 * <p>
 *   An Abstract Class that define the common behaviour for city markers or Locaiton Markers
 * </p>
 * @author Hisham Maged
 * @since 22/7/2019
 * @version 1.1
 * @see CityMarker
 * @see CustomizedMarker
 */
public abstract class AbstractLocationMarker extends SimplePointMarker implements CustomizedMarker{


  /**
  * Location constructor that makes initializes the simplePointMarker implementation using
  * super constructor
  * @param location Location of place to make marker for
  * */
  public AbstractLocationMarker(Location location)
  {
    super(location);
  }

  /**
  * A Point Feature constructor that initializes the simplePointMarker implementation using super
  * constructor that accepts location and properties
  * @param place Feature that holds place of place
  * feature because the city data from GeoReader returns as List<Feature> and casted to do our implementaiton
  * */
  public AbstractLocationMarker(Feature place){
    super(((PointFeature)place).getLocation(),place.getProperties());
  }

  /**
  * draws the place markers or location markers using a customized
  * draw method due to subclasses using drawMarker() method from customizedMarker interface
  * @param pg PGraphics to draw marker
  * @param x float x-coordinate
  * @param y float y-coordinate
  * */
  @Override
  public void draw(PGraphics pg, float x, float y)
  {
    if(!hidden) { // draws only if marker is not hidden from interactivity
      pg.pushStyle();
      this.drawMarker(pg, x, y); // draws the customized marker
      if (isSelected()) {
        this.showTitle(pg, x, y);
      }
      pg.popStyle();
    }
  }
}
