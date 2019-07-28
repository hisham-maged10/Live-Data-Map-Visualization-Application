package model.pojo;/*
  Author: Hisham Maged
  Date : 7/28/2019
  Project Name : 
*/

import model.parser.AbstractDataParser;
import model.parser.AirportParser;
/**
 * <h1>AirportEntry</h1>
 * A <b>POJO</b> Class That represents an entry of Airport Data from https://openflights.org/data.html
 * Encapsulates information making them Immutable such as
 * <ul>
 *   <li>Airport Name</li>
 *   <li>Airport's Country</li>
 *   <li>IATA Code of Airport</li>
 *   <li>ICAO Code of Airport</li>
 *   <li>Latitude of Airport's Location</li>
 *   <li>Longitude of Airport's Location</li>
 *   <li>Altitude in feet</li>
 *   <li>Time Zone</li>
 * </ul>
 * Overrides <code>equals()</code>,<code>compareTo()</code>,<code> hashCode()</code>,<code>toString()</code>
 * @author Hisham Maged
 * @version 1.1
 * @since 28/7/2019
 * @see AirportParser
 * @see DataEntry
 * @see AbstractDataParser
 *
 */
public class AirportEntry implements DataEntry, Comparable<AirportEntry> {

  // encapsulated data (immutable)
  private final int id; // id of Airport Entry
  private final String name; // name of Airport
  private final String country; // country that the airport is in
  private final String IATA; // 3-letter code of Airport, can be null
  private final String ICAO; // ICAO code, can be null
  private final double latitude; // latitude of Airport's location
  private final double longitude; //longitude of Airport's locaiton
  private final double altitude; // in feet
  private final String timeZone;

  /**
   * Sole Constructor that Takes all the needed data from each entry to encapsulate in POJO.
   * @param id String containing ID of Airport
   * @param name String containing Name of Airport
   * @param country String containing Country that the Airport is in
   * @param IATA String Containing 3-Letter code of Airport, can be null if not assigned
   * @param ICAO String containing the ICAO Code of Airport, can be null if not assigned
   * @param latitude String containing Latitude position of Airport
   * @param longitude String containing longitude position of Airport
   * @param timeZone String containing Time Zone that the Airport is in
   * @throws IllegalArgumentException if given wrong values that is going to be changed to doubles such as latitude,longitude,altitude
   */
  public AirportEntry(String id, String name, String country, String IATA, String ICAO, String latitude, String longitude, String altitude, String timeZone)
  {
    this.id = Integer.parseInt(id);
    this.name = name;
    this.country = country;
    this.IATA = IATA;
    this.ICAO = ICAO;
    this.timeZone = timeZone;
    try {
      this.latitude = Double.parseDouble(latitude);
      this.longitude = Double.parseDouble(longitude);
      this.altitude = Double.parseDouble(altitude);
    }catch(NumberFormatException ex)
    {
      throw new IllegalArgumentException("latitude, longitude, altitude should be numbers");
    }
  }

  /**
   * Gets the Unique ID of the Airport
   * @return Unique ID of Airport
   */
  public int getID()
  {
    return id;
  }
  /**
   * Gets the Name of the Airport
   * @return Name Of Airport
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the Name of the Country that's the Airport is in
   * @return Country Name
   */
  public String getCountry() {
    return country;
  }

  /**
   * Gets 3-Letter Code of Airport
   * @return IATA code
   */
  public String getIATA() {
    return IATA;
  }

  /**
   * Gets ICAO Code of Airport
   * @return ICAO code
   */
  public String getICAO() {
    return ICAO;
  }

  /**
   * Gets Latitude of Airport's Location
   * @return Latitude of Airport
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * Gets Longitude of Airport's Location
   * @return Longitude of Airport
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * Gets Altitude of Airport
   * @return Altitude of Airport
   */
  public double getAltitude() {
    return altitude;
  }

  /**
   * Gets Time-Zone that the Airport is in
   * @return Time-Zone that the Airport is in
   */
  public String getTimeZone() {
    return timeZone;
  }

  /**
   * checks if two POJO objects of type Airport Entry are the Same.
   * using Referential check then check of class then uses
   * the unique ID of Airport Entry as a check
   * making the method reflexive, transitive, symmetric, doesn't accept null giving false, consistent
   * @param o Object containing a POJO object of Airport Entry
   * @return True if Two POJOs are the same, False otherwise.
   */
  @Override
  public boolean equals(Object o)
  {
    if(this == o)
      return true;
    if(o == null || o.getClass() != AirportEntry.class)
      return false;
    AirportEntry anotherEntry = (AirportEntry)o;
    return anotherEntry.getID() == this.id;
  }

  /**
   * Computes the HashCode value for the POJO to work with Hashed Structures.
   * and it computes the hashCode to be consistent and equal for two Same POJOs
   * and distinct for two not equal POJOs, matches the equals method for the hashed structures to work correctly
   * and distinct for the hashing function to work efficiently
   * uses the unique ID of Airport like the equals method
   * @return Hashcode value of this POJO
   */
  @Override
  public int hashCode()
  {
    return new Integer(this.id).hashCode() * 31;
  }

  /**
   * Represents the POJO in the form of String.
   * made for logging and debugging
   * prints id, name of Airport, country, IATA, latitude, longitude, altitude, timezone
   * @return String Representation of this POJO.
   */
  @Override
  public String toString()
  {
    return this.id+" , "+this.name+" , "+this.country+" , "+this.IATA+" , "+this.latitude+" , "+this.longitude+" , "+this.altitude+" , "+this.timeZone+" , ";
  }

  /**
   * Defines the Natural ordering of Elements, Comparing Two POJO objects to check if bigger, less than , or equal to the other POJO object.
   * sorts The Airports in Ascending order due to their name
   * doesn't Accept Null, matches the equals as it doesn't say that two objects are equal unless
   * the ID is checked, matching the equals and hashcode methods to work correctly in Sorted Hashed Structures.
   * also does a referential check to see if same objects in memory, if so returns 0 without any other operations
   * @param anotherEntry AirportEntry holding another POJO to be compared against
   * @return 0 if equal, > 0 if bigger than, < 0 if less than
   * @throws NullPointerException if null is given as argument
   */
  @Override
  public int compareTo(AirportEntry anotherEntry)
  {
    if(this == anotherEntry)
      return 0;
    if(anotherEntry == null)
      throw new NullPointerException("Comparable doesn't accept null");
    int comparison;
    return (comparison = this.name.compareTo(anotherEntry.getCountry())) == 0 ?
            Integer.compare(this.id,anotherEntry.getID()) :
            comparison;
  }
}

