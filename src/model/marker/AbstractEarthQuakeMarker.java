package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : An Abstract Class implementing the common code or behavior of CustomizedMarker interface
*/

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import java.util.HashMap;
import java.util.Map;
import processing.core.PGraphics;

public abstract class AbstractEarthQuakeMarker extends SimplePointMarker implements CustomizedMarker{

  // shows whether the earthquake marker is on land or on ocean
  private boolean onLand;
  private float tempRadius; // used if age is past day to make animation
  private boolean pastDay = false;
  /*
   * Light earthQuake value : 4.0
   * moderate earthQuake Value : 4.0 - 5.0
   * intense earthquake value : 10.0
   * made for usage if u want
   * */
  private static final double LIGHT_EARTHQUAKE = 4.0;
  private static final double MODERATE_EARTHQUAKE = 5.0;
  private static final double INTENSE_EARTHQUAKE = 10.0;

  /*
   * Light Depth value : 0-50
   * Moderate Depth Value: 50-200
   * Intense : 200+
   * */
  private static final double LIGHT_DEPTH = 0.0;
  private static final double MODERATE_DEPTH = 70.0;
  private static final double INTENSE_DEPTH = 300.0;

  /*
   * Colors for makres of light,moderate,intense magnitude earthquake markers
   * sets the earthquake marker color based on depth
   * */
  private final static int LIGHT_EARTHQUAKE_COLOR;
  private final static int MODERATE_EARTHQUAKE_COLOR;
  private final static int INTENSE_EARTHQUAKE_COLOR;

  /*
   * Colors for makres of light,moderate,intense depth earthquake markers
   * made for usage if u want
   * */
  private final static int LIGHT_DEPTH_COLOR;
  private final static int MODERATE_DEPTH_COLOR;
  private final static int INTENSE_DEPTH_COLOR;

  /*
  * initializing the color constants using a PGraphcis object
  * using a Static initializer
  * */
  static{
    PGraphics pg = new PGraphics();
    LIGHT_EARTHQUAKE_COLOR = 1678159810; // made using the color method of PApplet
    MODERATE_EARTHQUAKE_COLOR = -1090781440; // made using the color method of PApplet
    INTENSE_EARTHQUAKE_COLOR = -1765858776; // made using the color method of PApplet
//    System.out.println(color(6,175,194,100)); got them using these
//    System.out.println(color(251,255,0,190));
//    System.out.println(color(191,34,40,150));

    LIGHT_DEPTH_COLOR = pg.color(242,255,56,130);
    MODERATE_DEPTH_COLOR = pg.color(214,71,24,100);
    INTENSE_DEPTH_COLOR = pg.color(148,28,32,200);
  }

  /*
  * PointFeature constructor that takes a point feature to initialize the SimplePointMarker
  * implementation using the super constructor with location of Feature
  * also setting the properties of the earthquake marker (properties of simplepointmarker ) using
  * the Map of properties of each feature, and getting the magnitude from properties
  * and putting radius property times a value of magnitude to determine size of marker based on magnitude
  * and setting the inherited radius property of simple point marker to a value based on magnitude
  * @Param: PointFeature representing the earthquake data at a certain location
  * */
  public AbstractEarthQuakeMarker(PointFeature feature)
  {
    super(feature.getLocation());
    // HashMap as it returns a hashMap and setting properties require a Hashmap not a map
    HashMap<String,Object> props = feature.getProperties();
    // gets magnitude that is stored as object in map and converts it to string using the actual type toString
    // parsing it into a float
    float magnitude = Float.parseFloat(props.get("magnitude").toString());
    props.put("radius",2*magnitude); // putting radius property to the properties of the feature
    //setting the properties of the marker with the added radius properties
    this.setProperties(props);
    // setting value of radius of this marker based on magnitude
    this.radius = 3F*magnitude;
    this.tempRadius = radius;
    if(props.get("age").toString().equalsIgnoreCase("past day"))
      this.pastDay = true;

  }
  /*
  * the overriden method of the SimplePointMarker class
  * that draws a special marker due to the specified code using PGraphics and x,y positions
  * since this is an abstract class, this is a normal implementation that is inherited and changed
  * due to the actual type changing when calling the drawMarker method of CustomizedMarker interface
  * @Param:PGgraphics to change graphics of the marker
  * @Param : float x for x-coordinate
  * @Param : float y for y-coordinate
  * */
  @Override
  public void draw(PGraphics pg, float x, float y)
  {
    pg.pushStyle(); //saves previous style
    determineColor(pg); //determines the color based on magnitude
    if(this.pastDay){changeRadius();} //makes the animation if earthquake is of past day
    this.drawMarker(pg,x,y); // draws the specified marker look
    pg.popStyle(); // reset to previous styling of whole current applet
  }
  // getter for magnitude as float for this earthquake marker
  public float getMagnitude()
  {
    return Float.parseFloat(getProperty("magnitude").toString());
  }
  // getter for depth as float for this earthquake marker
  public float getDepth()
  {
    return Float.parseFloat(getProperty("depth").toString());
  }
  // getter for title as String for this earthquake marker
  public String getTitle()
  {
    return getStringProperty("title");
  }
  // getter for radius as float for this earthquake marker
  public float getRadius()
  {
    return Float.parseFloat(getProperty("radius").toString());
  }
  // getter for age as String for this earthquake Marker
  public String getAge(){ return getStringProperty("age");}
  // returns whether the marker is on land or ocean
  public boolean isOnLand()
  {
    return this.onLand;
  }
  /*
  *
  * */
  private void determineColor(PGraphics pg)
  {
    float depth = this.getDepth();
    if(depth >= MODERATE_DEPTH && depth < INTENSE_DEPTH)
    {
      pg.strokeWeight(9);
      pg.stroke(MODERATE_EARTHQUAKE_COLOR);
    }else if(depth > LIGHT_DEPTH && depth < MODERATE_DEPTH)
    {
      pg.strokeWeight(7);
      pg.stroke(LIGHT_EARTHQUAKE_COLOR);
    }else{
      pg.strokeWeight(11);
      pg.stroke(INTENSE_EARTHQUAKE_COLOR);
    }
  }

  public void setOnLand(boolean onLand)
  {
    this.onLand = onLand;
  }

  private void changeRadius()
  {
    if((int)tempRadius == (int)this.radius)
      this.radius = 0;
    else
      this.radius+=0.4;
  }
}
