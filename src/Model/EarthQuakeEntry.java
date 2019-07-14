package Model;/*
  Author: Hisham Maged
  Date : 7/11/2019
  Project Name : An Entry representation of RSS Feed (Pojo)
*/

import de.fhpotsdam.unfolding.geo.Location;
import java.math.BigDecimal;
import java.math.MathContext;

public class EarthQuakeEntry implements Comparable<EarthQuakeEntry>{

  private final Location loc;// immutable location field
  private final double magnitude;// immutable magnitude
  private final double depth;// immutable depth
  private final String locationTitle; // immutable text location
  private final String age; // immutable age
  private final String title; // immutable whole rss title
  private final BigDecimal exactMagnitude; // immutable bigDecimal object representing exact magnitude
  private final BigDecimal exactDepth; // immutable bigDecimal object representing exact Depth

  /*
  * Main and only Constructor for the POJO object to initialize all Final field (Immutable)
  * takes info string which contains the magnitude and location in text and splits them and use the
  * respected split values into their correct fields with the required operations
  * takes the elevation which is the depth
  * age
  * and Locations, split into latitude, longitude, Location object is made with them
  * */
  public EarthQuakeEntry(String id, String info,String locationPoints, String elevation,String age)
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
    this.exactMagnitude = new BigDecimal(tempMagnitude);
    this.magnitude = Double.parseDouble(tempMagnitude);

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

  public String getLocationTitle()
  {
    return this.locationTitle;
  }

  public String getTitle()
  {
    return this.title;
  }

  public double getMagnitude()
  {
    return this.magnitude;
  }
  // getter of longitude and latitude because Location isn't immutable and POJO is Immutable
  public float getLongitude()
  {
    return this.loc.getLon();
  }

  public String getAge()
  {
    return this.age;
  }


  public BigDecimal getExactMagnitude()
  {
    return this.exactMagnitude;
  }
  public BigDecimal getExactDepth()
  {
    return this.exactDepth;
  }

  public double getDepth()
  {
    return this.depth;
  }

  public float getLatitude()
  {
    return this.loc.getLat();
  }

  /*
  * equals methods used to separate Objects using the text Location and exact magnitude
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

  /*
  * HashCode so if put in hashed structure, using the text Location and the exact magnitude
  * */
  @Override
  public int hashCode()
  {
    return ( this.locationTitle.hashCode() + this.exactMagnitude.hashCode() ) * 31;
  }

  /*
  * Natural Ordering using the exact magnitude to sort ascendingly
  * */
  @Override
  public int compareTo(EarthQuakeEntry anotherQuake)
  {
    return this.exactMagnitude.compareTo(anotherQuake.getExactMagnitude());
  }

  /*
  *  String Representation of the POJO object for debuging and logging
  */
  @Override
  public String toString()
  {
    return "Title : "+ this.locationTitle + " , lat, lon : " +this.loc.getLat()+", "+this.loc.getLon()+" , Magnitude: "+this.magnitude +", Depth: "+this.depth+" , Age: "+this.age;
  }


}
