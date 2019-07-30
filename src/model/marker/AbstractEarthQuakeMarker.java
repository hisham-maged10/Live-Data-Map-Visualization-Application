package model.marker;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Project Name : An Abstract Class implementing the common code or behavior of CustomizedMarker interface
*/

import static processing.core.PConstants.CORNER;
import static processing.core.PConstants.LEFT;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import processing.core.PGraphics;

/**
 * <h1>AbstractEarthQuakeMarker</h1>
 * <p>
 *   Defines the common behaviour of Earth quake markers and Natural ordering of Earthquake Markers
 * </p>
 * @author Hisham Maged
 * @since 22/7/2019
 * @version 1.1
 * @see LandEarthQuakeMarker
 * @see OceanEarthQuakeMarker
 * @see CustomizedMarker
 */
public abstract class AbstractEarthQuakeMarker extends SimplePointMarker implements CustomizedMarker, Comparable<AbstractEarthQuakeMarker>{

  // shows whether the earthquake marker is on land or on ocean
  private boolean onLand;
  private float tempRadius; // used if age is past day to make animation
  private boolean pastDay = false; // to check if past day or not (including pastHour)
  private List<ScreenPosition> citiesInThreatCircle; // holds cities in close promixity of threat circle
  {
    this.citiesInThreatCircle = new ArrayList<>(); // initializes it to be an empty array list
  }
  private boolean clicked;

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

  /**
  * PointFeature constructor that takes a point feature to initialize the SimplePointMarker
  * implementation using the super constructor with location of Feature
  * also setting the properties of the earthquake marker (properties of simplepointmarker ) using
  * the Map of properties of each feature, and getting the magnitude from properties
  * and putting radius property times a value of magnitude to determine size of marker based on magnitude
  * and setting the inherited radius property of simple point marker to a value based on magnitude
  * @param feature PointFeature representing the earthquake data at a certain location
  * */
  public AbstractEarthQuakeMarker(PointFeature feature)
  {
    super(feature.getLocation());
    // HashMap as it returns a hashMap and setting properties require a Hashmap not a map
    HashMap<String,Object> props = feature.getProperties();
    // gets magnitude that is stored as object in map and converts it to string using the actual type toString
    // parsing it into a float
    float magnitude = Float.parseFloat(props.get("magnitude").toString());
    props.put("radius",2*Math.abs(magnitude)); // putting radius property to the properties of the feature
    //setting the properties of the marker with the added radius properties
    this.setProperties(props);
    // setting value of radius of this marker based on magnitude
    this.radius = 3F*Math.abs(magnitude);
    this.tempRadius = radius; // used in animating of marker if it's from the past day (including past hour earthquakes)
    String tempAge = null;
    // gets the age of the earthquake and if it's in the past day or past hour then makes pastDay to be true
    if((tempAge=getStringProperty("age")).equalsIgnoreCase("past day") || tempAge.equalsIgnoreCase("past hour"))
      this.pastDay = true;

  }
  /**
  * Draws a special marker due to the specified code using PGraphics and x,y positions
  * since this is an abstract class, this is a normal implementation that is inherited and changed
  * due to the actual type changing when calling the drawMarker method of CustomizedMarker interface
  * @param pg PGgraphics to render graphics of the marker
  * @param x float for x-coordinate
  * @param y float for y-coordinate
  * */
  @Override
  public void draw(PGraphics pg, float x, float y)
  {
    if(!hidden) // draws only if marker is not hidden from interactivity
    {
      pg.pushStyle(); //saves previous style
      determineColor(pg); //determines the color based on magnitude
      if (this.pastDay) {
        changeRadius();
      } //makes the animation if earthquake is of past day
      this.drawMarker(pg, x, y); // draws the specified marker look
      // shows the title of the earthquake marker if it's selected meaning that it's under the cursor position now
      // with help of the mouseMoved EventHandler on map
      if (isSelected()) {
        showTitle(pg, x, y);
      }
      // shows the radius of the threat circle if clicked
      if(clicked)
      {
        showThreatCircleLines(pg,x,y);
      }
      pg.popStyle(); // reset to previous styling of whole current applet
    }
  }

  /**
  * shows the magnitude, depth, title of the earthquake
  * @param pg PGraphics that is used for rendering
  * @param x float x-coordinate
  * @param y float y-coordinate
  * */
  @Override
  public void showTitle(PGraphics pg, float x, float y)
  {
    pg.fill(245,240,208);
    pg.noStroke();
    pg.rectMode(CORNER);
    pg.textSize(12);
    String desc = getMagnitude()+" Richter , "+getDepth()+" km, "+getTitle();
    pg.rect(x,y-20,pg.textWidth(desc)+10,20);
    pg.textAlign(LEFT);
    pg.fill(0);
    pg.text(desc,x+5,y-5);
  }

  /**
   * Represents this Earthquake Marker using Magnitude, depth, title.
   * @return String Representation of Marker
   */
  @Override
  public String toString()
  {
    return getMagnitude()+" Richter , "+getDepth()+" km, "+getTitle()+(this.onLand ? " , Happened on Land": " , Happened in Ocean");
  }

  /**
   * Gets the Magnitude of the Earthquake
   * @return Magnitude of the Earthquake
   */
  public float getMagnitude()
  {
    return Float.parseFloat(getProperty("magnitude").toString());
  }

  /**
   * Gets the Depth of the Earthquake
   * @return Depth of the Earthquake
   */
  public float getDepth()
  {
    return Float.parseFloat(getProperty("depth").toString());
  }
  /**
   * Gets Title of earthquake from RSS feed
   * @return String title of earthquake
   */
  public String getTitle()
  {
    return getStringProperty("title");
  }

  /**
   * Gets Radius of Earthquake marker
   * @return float radius of the Earthquake Marker
   */
  public float getRadius()
  {
    return Float.parseFloat(getProperty("radius").toString());
  }

  /**
   * Gets Age of the Earthquake
   * @return String representation of Earthquake's age
   */
  public String getAge(){ return getStringProperty("age");}

  /**
   * Checks whether the Earthquake Marker is on land or not
   * @return True if on land, false otherwise.
   */
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
    }else if(depth >= LIGHT_DEPTH && depth < MODERATE_DEPTH)
    {
      pg.strokeWeight(7);
      pg.stroke(LIGHT_EARTHQUAKE_COLOR);
    }else{
      pg.strokeWeight(11);
      pg.stroke(INTENSE_EARTHQUAKE_COLOR);
    }
  }

  /**
   * Sets the value if earthquake marker is on land or not
   * @param onLand value checking whether earthquake marker is on land or not
   */
  public void setOnLand(boolean onLand)
  {
    this.onLand = onLand;
  }
  /*
   * private helper method
   * made to change radius ot produce an animation for earthquakes
   */
  private void changeRadius()
  {
    if((int)tempRadius == (int)this.radius)
      this.radius = 0;
    else
      this.radius+=0.4;
  }

  /**
  * Gets the threat circle's radius of the earthquake represented by that marker
  * DISCLAIMER: this formula is for illustration only, not intended
  * to be used for safety-critical or predictive applications.
  * @return The raidus of the Threat Circle in KM
   * */
  public double getThreatCircle()
  {
    // an equation taken from the internet to measure the threat circle's radius
    // from an earthquake marker
    double miles = 20.0 * Math.pow(1.8, 2*getMagnitude()-5); // gets radius on miles
    return (miles * 1.6); // return the radius in km
  }

  /**
   * Checks whether earthquake marker is clicked or not
   * @return True if clicked, false otherwise.
   */
  public boolean isClicked() {
    return clicked;
  }

  /**
   * Sets the marker to be of clicked state or not
   * @param clicked boolean Value to indicate that the marker is clicked or not.
   */
  public void setClicked(boolean clicked) {
    this.clicked = clicked;
  }

  /**
  * Shows stroked lines from the earth quake to the city markers that is proximity of the
  * threat circle
  * @param pg PGraphics used for rendering
  * @param x float for x-coordinate
  * @param y foat for y-coordinate
  * */
  private void showThreatCircleLines(PGraphics pg, float x, float y)
  {
    pg.strokeWeight(3);
    pg.stroke(191, 34, 40, 150);
    this.citiesInThreatCircle.forEach( m ->{
      pg.line(x,y,m.x,m.y); // makes lines from earthquake to cities in threat
    });

  }

  /**
  * Adds cities that are in threat circle to Earthquake.
   * used by the map in mouse Released to add the cities in
  * threat circle
  * throw IllegalArgumentException if something else rather than a city is sent
  * @param city ScreenPositon holding the city marker location
  * */
  public void addCityInThreat(ScreenPosition city)
  {
      this.citiesInThreatCircle.add(city);
  }

  /**
   * Clears all cities from the threat circle of the earthquake
   */
  public void clearCities()
  {
    this.citiesInThreatCircle.clear();
  }

  /**
   * Defines the Natural ordering of Earthquake Markers to be in descending order due to Magnitude.
   * Does Referential check to check whether it's the same object in memory, if so returns 0 directly for equality
   * Doesn't accept null throwing NullPointerException
   * uses Float.compare with reversed arguments to reverse the order of magnitudes
   * @param anotherEarthquakeMarker
   * @return
   */
  @Override
  public int compareTo(AbstractEarthQuakeMarker anotherEarthquakeMarker)
  {
    if( this == anotherEarthquakeMarker)
      return 0;
    if(anotherEarthquakeMarker == null)
      throw new NullPointerException("Comparable doesn't accept null");
    return Float.compare(anotherEarthquakeMarker.getMagnitude(),this.getMagnitude());
  }


}
