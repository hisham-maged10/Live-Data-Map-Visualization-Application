package model.pojo;/*
  Author: Hisham Maged
  Date : 7/11/2019
  Project Name : An Entry representation of RSS Feed of earthquakes from UCSG (Pojo)
*/

import de.fhpotsdam.unfolding.geo.Location;
import java.math.BigDecimal;

/**
 * EarthQuakeEntry is a POJO class that consumes all the data about a single Earthquake event
 * which is used by the rest of the program.
 * An EarthQuakeEntry Object encapsulates the state information needed
 * to make markers of, this state information includes:
 * <ul>
 *   <li> The location of the earthquake in latitude and longitude</li>
 *   <li> The magnitude of the earthquake</li>
 *   <li> The depth of the earthquake</li>
 *   <li> The descriptive location of the earthquake along with its magnitude in text</li>
 *   <li> The age the earthquake in text</li>
 *   <li> The exact title of the earthquake from the RSS feed</li>
 *   <li> The exact magnitude of the earthquake for exact matching of floating point values</li>
 *   <li> The exact depth of the earthquake for exact matching of floating point values</li>
 * </ul>
 * <p>
 *   Overrides the <code>equals</code> , <code>hashCode</code>, <code>toString</code> methods
 * </p>
 *
 * @author  Hisham Maged
 * @version 1.1
 * @since   11/7/2019
 */
public class EarthQuakeEntry implements DataEntry,Comparable<EarthQuakeEntry>{

  private final Location loc;// immutable location field
  private final double magnitude;// immutable magnitude
  private final double depth;// immutable depth
  private final String locationTitle; // immutable text location
  private final String age; // immutable age
  private final String title; // immutable whole rss title
  private final BigDecimal exactMagnitude; // immutable bigDecimal object representing exact magnitude
  private final BigDecimal exactDepth; // immutable bigDecimal object representing exact Depth

  /**
  * Sole Constructor for the POJO class to initialize all final fields (Immutable).
  * takes info string which contains the magnitude and location in text and splits them and use the
  * respected split values into their correct fields with the required operations
  * takes the elevation and computes it into depth, age
  * and Locations, split into latitude, longitude, Location object is made with them
  *
   * @param info  The string representation of the earthquake from the RSS feed
   * @param locationPoints  The String representation of latitude, longitude
   * @param elevation The string representation of elevation that will be used to compute depth
   * @param age The string representation of the age of the earthquake
   * */
  public EarthQuakeEntry(String info,String locationPoints, String elevation,String age)
  {
    this.title = info;
    String[] titleItems = info.split("\\s-\\s");
    /*
    * this is for when this happens (two minuses)
    * M -0.3 - 50km ESE of Beatty, Nevada
    * M 1.2 - 17km S of Trona, CA
    * more than one case, so in regex, made sure that minus surrounded with whitespaces both sides
    * */
    // extracts the String magnitude, made into a local variable because used twice, for big decimal and for magnitude
    String tempMagnitude = titleItems[0]
        .split("\\s+")[1];
    if(!tempMagnitude.equalsIgnoreCase("?")) {
      this.exactMagnitude = new BigDecimal(tempMagnitude);
      this.magnitude = Double.parseDouble(tempMagnitude);
    }else
    {
      this.exactMagnitude = new BigDecimal("0.0");
      this.magnitude = Double.parseDouble("0.0");
    }

      this.locationTitle = titleItems[1].trim();
    String[] points = locationPoints.split("\\s+");
    this.loc = new Location(Float.parseFloat(points[0]),Float.parseFloat(points[1]));
    this.age = age;
    // next series of steps is to get the depth value from elevation
    // the most accurate division that can be accomplished to get the exact depth
    BigDecimal tempDepth = new BigDecimal(Math.abs(Double.parseDouble(elevation)));
    tempDepth = tempDepth.divide(new BigDecimal("100.0"));
    this.exactDepth = tempDepth.divide(new BigDecimal("10.0"));
    // double representation of depth
    this.depth = this.exactDepth.doubleValue();

  }
  /**
   * Gets the descriptive location of the earthquake.
   * @return  String representation of location of earthquake along with its magnitude for eg. "M 4.6 - 36 km north of Alexandria"
   */
  public String getLocationTitle()
  {
    return this.locationTitle;
  }

  /**
   * Gets the descriptive location of the earthquake along with its magnitude.
   * @return String representation of location of earthquake for eg. "36 km north of Alexandria"
   */
  public String getTitle()
  {
    return this.title;
  }

  /**
   * Gets the magnitude of the earthquake.
   * @return magnitude of the earthquake in double form
   */
  public double getMagnitude()
  {
    return this.magnitude;
  }

  /**
   * Gets the longitude of the earthquake.
   * @return the longitude part of the location of the earthquake
   */
  public float getLongitude()
  {
    return this.loc.getLon();
  }

  /**
   * Gets the String representation of the age of earthquake
   * @return The String representation of the age of earthquake
   */
  public String getAge()
  {
    return this.age;
  }

  /**
   * Gets the exact magnitude of the earthquake for exact double comparisons.
   * @return The BigDecimal object holding the exact magnitude of the earthquake
   */
  public BigDecimal getExactMagnitude()
  {
    return this.exactMagnitude;
  }

  /**
   * Gets the exact depth of the earthquake for exact double comparisons.
   * @return The BigDecimal object holding the exact depth of the earthquake
   */
  public BigDecimal getExactDepth()
  {
    return this.exactDepth;
  }

  /**
   * Gets the depth of the earthquake.
   * @return The double representation of depth of the earthquake
   */
  public double getDepth()
  {
    return this.depth;
  }

  /**
   * Gets the latitude of the earthquake.
   * @return The latitude portion of the location of the earthquake
   */
  public float getLatitude()
  {
    return this.loc.getLat();
  }

  /**
   * This method used to separate Objects using the text Location and exact magnitude
   * @param o   Object holding the other <code>EarthQuakeEntry</code> to be compared for equality with
   * @return  true if objects are equal, false otherwise
   * */
  @Override
  public boolean equals(Object o)
  {
    if( this == o )
      return true;
    if(o == null || o.getClass() != EarthQuakeEntry.class)
      return false;
    EarthQuakeEntry anotherQuake = (EarthQuakeEntry) o;
    return this.locationTitle.equals(anotherQuake.getLocationTitle())
          &&
          this.exactMagnitude.compareTo(anotherQuake.getExactMagnitude()) == 0;
  }

  /**
   * This method computes the HashCode so if put in hashed structure, using the text Location and the exact magnitude, matching the equals method
   * @return  hashcode representation of the object
   * */
  @Override
  public int hashCode()
  {
    return ( this.locationTitle.hashCode() + this.exactMagnitude.hashCode() ) * 31;
  }

  /**
   * This method defines the Natural Ordering using the exact magnitude to sort in descending order
   * @param  anotherQuake  An object that holds another <code>EarthQuakeEntry</code> to be compared against
   * @return int representation of the comparison < 0 if less than, == 0 if equals to, > 0 if bigger than
   * */
  @Override
  public int compareTo(EarthQuakeEntry anotherQuake)
  {
    return anotherQuake.getExactMagnitude().compareTo(this.exactMagnitude); // Descending order by reverse parameters
  }

  /**
    * This method gets the String Representation of the POJO object for debugging and logging.
   *  <p>
   *    for eg.
   *    Title : 18km E of Little Lake, CA , lat, lon : 35.926334, -117.71233 , Magnitude: 0.7, Depth: 8.96 , Age: Past Week
   *  </p>
    * @return The String representation that conveys information about the current POJO object
    */
  @Override
  public String toString()
  {
    return "Title : "+ this.locationTitle + " , lat, lon : " +this.loc.getLat()+", "+this.loc.getLon()+" , Magnitude: "+this.magnitude +", Depth: "+this.depth+" , Age: "+this.age;
  }


}
