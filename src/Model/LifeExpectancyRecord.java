package Model;/*
  Author: Hisham Maged
  Date : 7/12/2019
  Project Name : A Pojo object for holding each of the Life Expectancy Records in the being parsed CSV file
*/

import java.util.Collections;
import java.util.Map;

public class LifeExpectancyRecord implements Comparable<LifeExpectancyRecord>{

  private final String countryName; // private immutable (final for added security and readablity) country Name in record
  private final String countryCode; // private immutable (final for added security and readablity) country code in record
  private final Map<Integer,Float> yearLifeExpectancyMap; // map immutable and unmodefiable that holds the year and its respected life expectancy value for current record (used in feedparser)_

  /*
  * only constructor version and yearLifeExpectancy is immutable as given in feedParser as annonymous object
  * so can't get hold of it and change it using reference sharing
  * */
  public LifeExpectancyRecord(String countryName,String countryCode,Map<Integer,Float> yearLifeExpectancyMap)
  {
    this.countryName = countryName;
    this.countryCode = countryCode;
    this.yearLifeExpectancyMap = yearLifeExpectancyMap;
  }

  // getter for country code
  public String getCountryCode()
  {
    return this.countryCode;
  }

  // getter for country name
  public String getCountryName()
  {
    return this.countryName;
  }

  // getter for map as unmodefiable so can't be changed by reference sharing and it holds year and life expectancy values for
  // current record, could be used for something ( unused for now )
  public Map<Integer,Float> getYearExpectancyMap()
  {
    return Collections.unmodifiableMap(this.yearLifeExpectancyMap);
  }
  /*
  * used to get float value (life expectancy value) for given year in record held by calling pojo
  * @Param int year that is a year to return respected life expectancy value for it
  * if year not between 1960 and 2017, Illegal ArgumentException will be thrown
  * */
  public Float getCertainYearExpectancy(int year)
  {
    if(year < 1960 || year >2017)
      throw new IllegalArgumentException("year must range from 1960 to 2017 inclusive");
    return this.yearLifeExpectancyMap.get(year);
  }

  /*
  * Override of equals method that uses no class hierarchy and is reflexive, symmetric, transitive, consistent and not null
  * uses the immutable value of countryCode as equality value
  * */
  @Override
  public boolean equals(Object o)
  {
    if( this == o )
        return true;
    if(o == null || o.getClass() != LifeExpectancyRecord.class)
      return false;
    LifeExpectancyRecord anotherRecord = (LifeExpectancyRecord) o;
    return this.countryCode.equalsIgnoreCase(anotherRecord.getCountryCode());
  }
  /*
  * overriden hashCode method for hashed structures to work using the POJO
  * using the countryCode string's hash code * 31 as hashing function for hashing structures
  * consistent and distinct for non equal and same for equal
  * matches equals method
  * uses immutable values for hashed structures not to break (same value as equals)
  * */
  @Override
  public int hashCode()
  {
    return this.countryCode.hashCode() * 31;
  }
  /*
  * compare to given natural ordering of sorting ascendingly using country name not country code
  * and matches equals for sorted hashed structures to work
  * as if two countries have the same name then country code is compared insted and if equal then they are equal
  * matches equals method for sorted hashed structures
  * doesn't accept null
  * @NotNullable
  * @Param another LifeExpectancyRecord to be compared
  * uses Immutable values same as hashcode and equals
  * */
  @Override
  public int compareTo(LifeExpectancyRecord anotherRecord)
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

  /*
  * String representation of POJO object and is called implicitly on System.out.println
  * */
  @Override
  public String toString()
  {
    return "Country Name: " + this.countryName + " , Country Code : " + this.countryCode + "\nMap: " + this.yearLifeExpectancyMap ;
  }
}
