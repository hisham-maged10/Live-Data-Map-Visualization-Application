package model.pojo;/*
  Author: Hisham Maged
  Date : 7/12/2019
  Project Name : A Pojo object for holding each of the Life Expectancy Records in the being parsed CSV file
*/

import java.util.Collections;
import java.util.Map;
/**
 * LifeExpectancyEntry is a POJO class that consumes all the data about a single Life Expectancy Survey for a country
 * which is used by the rest of the program.
 * A <code>LifeExpectancyEntry</code> Object encapsulates the state information needed
 * to make markers of, this state information includes:
 * <ul>
 *   <li> The country name that the entry is about</li>
 *   <li> The country code that the entry is about</li>
 *   <li> A Mapping between the years from 1960 to 2017 and their life expectancy value for the country</li>
 * </ul>
 * <p>
 *   Overrides the <code>equals()</code> , <code>hashCode()</code>, <code>toString()</code> methods
 * </p>
 * @author  Hisham Maged
 * @version 1.1
 * @since   12/7/2019
 */
public class LifeExpectancyEntry implements DataEntry, WorldData, Comparable<LifeExpectancyEntry>{

  private final String countryName; // private immutable (final for added security and readablity) country Name in record
  private final String countryCode; // private immutable (final for added security and readablity) country code in record
  private final Map<Integer,Float> yearLifeExpectancyMap; // map immutable and unmodefiable that holds the year and its respected life expectancy value for current record (used in feedparser)_

  /**
   * Sole Constructor for the POJO class to initialize all final fields (Immutable).
   * takes country name string which the POJO represents, takes country code of the country being represented
   * takes the Mapping of years to life expectancy values
   * @param countryName  The string representation of the Country Name from the data feed
   * @param countryCode  The String representation of Country
   * @param yearLifeExpectancyMap A Mapping of Years to Life expectancy value for the country of that entry from 1960 to 2017
   * */
  public LifeExpectancyEntry(String countryName,String countryCode,Map<Integer,Float> yearLifeExpectancyMap)
  {
    this.countryName = countryName;
    this.countryCode = countryCode;
    this.yearLifeExpectancyMap = yearLifeExpectancyMap;
  }

  /**
   * Gets the Country Code for the entry represented by the POJO
   * @return The country code of the Country
   */
  public String getCountryCode()
  {
    return this.countryCode;
  }

  /**
   * Gets the Country Name for the entry represented by the POJO
   * @return The Country Name of the entry
   */
  public String getCountryName()
  {
    return this.countryName;
  }

  /**
   * Gets the mapping map as a unmodefiable Map so can't be changed by reference sharing and it holds year and life expectancy values for
   * current record, could be used for something ( unused for now ).
   * @return  UnmodefiableMap containing the Mapping of Years to Life expectancy values for the Pojo Object from 1960 to 2017
   */
  public Map<Integer,Float> getMap()
  {
    return Collections.unmodifiableMap(this.yearLifeExpectancyMap);
  }
  /**
   * Gets the float value (life expectancy value) for a given year in entry.
   * @param year The int representation of a certain year in the entry
   * @throws IllegalArgumentException  if year input is not between 1960 and 2017.
   * @return The Life Expectancy value for the given year input
   * */
  public Float getCertainYearValue(int year)
  {
    if(year < 1960 || year >2017)
      throw new IllegalArgumentException("year must range from 1960 to 2017 inclusive");
    return this.yearLifeExpectancyMap.get(year);
  }

  /**
   * Uses no class hierarchy and is reflexive, symmetric, transitive, consistent and returns false on null input.
   * uses the immutable value of countryCode as equality value
   * @param o Object holding another <code>LifeExpectancyEntry</code> that will be used for equality comparison
   * @return True if equal, false otherwise.
   * */
  @Override
  public boolean equals(Object o)
  {
    if( this == o )
        return true;
    if(o == null || o.getClass() != LifeExpectancyEntry.class)
      return false;
    LifeExpectancyEntry anotherRecord = (LifeExpectancyEntry) o;
    return this.countryCode.equalsIgnoreCase(anotherRecord.getCountryCode());
  }
  /**
  * Computes the consistent and distinct hashCode value used with hashed structures to work using the POJO.
  * using the country code string's hash code value * 31 as hashing function for hashing structures
  * <p>
  * also matches the equals method for Hashed Structures to work efficiently
  * using immutable values for hashed structures not to break (same value as equals)
  * </p>
  * @return int representation of hashcode of POJO object
  * */
  @Override
  public int hashCode()
  {
    return this.countryCode.hashCode() * 31;
  }
  /**
  * Defines natural ordering of sorting in ascending order using country name.
  * matches <code>equals</code> for sorted hashed structures to work
  * as if two countries have the same name then country code is compared instead
  * matching the <code>equals</code> method for sorted hashed structures to work efficiently
  * uses Immutable values same as hashcode and equals
  * @throws NullPointerException if a null input is given.
  * @param anotherRecord  Another LifeExpectancyEntry to be compared against
  * @return int representation of the comparison < 0 if less than, == 0 if equals to, > 0 if bigger than
   * */
  @Override
  public int compareTo(LifeExpectancyEntry anotherRecord)
  {
    if(this == anotherRecord)
      return 0;
    if(anotherRecord == null)
      throw new NullPointerException("Cannot compare with null");
    int comparison;
    return (comparison = this.countryName.compareTo(anotherRecord.getCountryName())) == 0 ?
            this.countryCode.compareTo(anotherRecord.getCountryCode()) :
            comparison;
  }

  /**
   * Represents String containing information about the POJO object.
   * is called implicitly on System.out.println and represents
   * the country name, country code and mapping of years and their life expectancy values
   * @return String representation of the POJO object
   * */
  @Override
  public String toString()
  {
    return "Country Name: " + this.countryName + " , Country Code : " + this.countryCode + "\nMap: " + this.yearLifeExpectancyMap ;
  }
}
