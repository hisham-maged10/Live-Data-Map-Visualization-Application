package model.pojo;

import java.util.Map;

/*
* @Author: Hisham Maged
* @Date: 21-7-2019
* Class Description: an interface that points out that certain classes are POJO classes
*  of World Data Bank giving global data, have a map for each pojo object
* */
public interface WorldData {
  public Map<Integer,Float> getMap(); //returns the value of year and the value of that year of certain data
  public String getCountryCode(); // returns the CountryCode of pojo object
  public Float getCertainYearValue(int year); // returns value associated with given eyar from the map
}
