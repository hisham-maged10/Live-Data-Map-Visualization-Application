package model.pojo;

import java.util.Map;

/*
* @Author: Hisham Maged
* @Date: 21-7-2019
* Class Description: an interface that points out that certain classes are POJO classes
*  of World Data Bank giving global data, have a map for each pojo object
* */

/**
 * An interface that points out that certain classes are <b>POJO</b> classes
 * of <b>World Data Bank</b> giving global data, have a map for each pojo object
 * @author Hisham Maged
 * @version 1.1
 * @since 21/7/2019
 * @see LifeExpectancyEntry
 */
public interface WorldData {
  /**
   * Gets the mapping map as a unmodefiable Map so can't be changed by reference sharing and it holds year and life expectancy values for
   * current record, could be used for something ( unused for now ).
   * @return  UnmodefiableMap containing the Mapping of Years to values for the Pojo Object from 1960 to 2017
   */
  public Map<Integer,Float> getMap();

  /**
   * Gets the float value for a given year in entry.
   * @param year The int representation of a certain year in the entry
   * @throws IllegalArgumentException  if year input is not between 1960 and 2017.
   * @return The value for the given year input
   * */
  public String getCountryCode();

  /**
   * Gets the Country Code for the entry represented by the POJO
   * @return The country code of the Country
   */
  public Float getCertainYearValue(int year);
}
