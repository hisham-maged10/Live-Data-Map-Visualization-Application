package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : A customizable marker interface that holds a hierarchy for customized marker of Wolrd Bank and EarthQuakes
*/

import processing.core.PGraphics;

/**
 * <h1>CustomizedMarker</h1>
 * <p>
 * An interface that promises a certain interface of methods
 * for custom markers
 * </p>
 * @author Hisham Maged
 * @version 1.1
 * @since 22/7/2019
 * @see AbstractEarthQuakeMarker
 * @see AbstractLocationMarker
 */
public interface CustomizedMarker {

  /**
   * Draws the Custom shape of the marker
   * @param pg PGraphics for rendering the marker shape
   * @param x float x-coordinate
   * @param y float y-coordinate
   */
  public void drawMarker(PGraphics pg, float x, float y);

  /**
   * Draws the Title of the Marker, showing information about it
   * @param pg PGraphics for rendering the Title
   * @param x float x-coordinate
   * @param y float y-coordinate
   */
  public void showTitle(PGraphics pg, float x, float y);
}
