package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : A customizable marker interface that holds a hierarchy for customized marker of Wolrd Bank and EarthQuakes
*/

import processing.core.PGraphics;

public interface CustomizedMarker {

  public void drawMarker(PGraphics pg, float x, float y);
  public void showTitle(PGraphics pg, float x, float y);
}
