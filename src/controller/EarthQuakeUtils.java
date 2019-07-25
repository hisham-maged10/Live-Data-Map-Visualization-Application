package controller;/*
  Author: Hisham Maged
  Date : 7/21/2019
  Class desc : A utitility class for EarthQuake data facility methods
*/

import de.fhpotsdam.unfolding.marker.Marker;
import model.pojo.EarthQuakeEntry;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class EarthQuakeUtils {




  //TODO: CHECK WHY DEPTH FILTERS ARE BUGGY
  /*
   * A Markup interface made for API Flexibility in calling of EarthQuakeUtils filter method
   * */
  public interface EarthQuakeFilter extends Predicate<EarthQuakeEntry> {}

  /* ===============================================  Magnitude Filters Section  =========================================*/
  /*
   * a public static class to Filter magnitude based on an exact value, using BigDecimals comparisons
   * to get exact values of entry and given magnitude and compares them equally using BigDecimal comparison
   * for exact value
   * implements EarthQuakeFilter which is a Markup interface for API Flexibility that implements Predicate<EarthQuakeEntry>
   * and Override the test method to return true if and only if the values exactly match
   * @Constructor_Param: exact magnitude value that you want magnitude to be filtered based on
   * */
  public static class ExactMagnitudeFilter implements EarthQuakeFilter
  {
    private String magnitude;
    public ExactMagnitudeFilter(String magnitude)
    {
      if(magnitude == null)
        throw new IllegalArgumentException("Magnitude can't be null, given value: "+magnitude);
      try {
        if (new BigDecimal(magnitude).compareTo(new BigDecimal("0.0")) < 0)
          throw new IllegalArgumentException("Doesn't accept negative Magnitude value, given value: "+magnitude);

        this.magnitude = magnitude;

      }catch(NumberFormatException ex)
      {
        throw new IllegalArgumentException("Must be numeric input for magnitude, given value: "+magnitude);
      }
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return entry.getExactMagnitude().compareTo(new BigDecimal(this.magnitude)) == 0 ;
    }
  }

  /*
   * a public static class made for API Flexibility of filtering of Earth Quakes
   * MagnitudeLessThanFilter is used to Filter the earth quakes to be of magnitude less than
   * given value and it's slightly exact, not exact as the ExactMagnitudeFilter as not needed as much
   * @Ctor Param: double value which is the upper Limit magnitude, if less than 0 gives Illegal ArgumentException
   * @Ctor_Param: boolean value which specifies whether to include upper limit or not
   * */
  public static class MagnitudeLessThanFilter implements EarthQuakeFilter{

    private double upperLimitMagnitude;
    private boolean inclusive;
    public MagnitudeLessThanFilter(double upperLimitMagnitude,boolean inclusive)
    {
      if(Double.compare(upperLimitMagnitude, 0.0) < 0)
        throw new IllegalArgumentException("Magnitude value must be Positive, given value: "+upperLimitMagnitude);
      this.upperLimitMagnitude = upperLimitMagnitude;
      this.inclusive = inclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getMagnitude(),upperLimitMagnitude) <= 0 :
          Double.compare(entry.getMagnitude(),upperLimitMagnitude) < 0;
    }

  }

  /*
   * a public static class made for API Flexibility of filtering of Earth Quakes
   * MagnitudeMoreThanFilter is used to Filter the earth quakes to be of magnitude More than
   * given value and it's slightly exact, not exact as the ExactMagnitudeFilter as not needed as much
   * @Ctor Param: double value which is the lower Limit magnitude, if less than 0 gives Illegal ArgumentException
   * @Ctor_Param: boolean value which specifies whether to include lower limit or not
   * */
  public static class MagnitudeMoreThanFilter implements EarthQuakeFilter{

    private double lowerLimitMagnitude;
    private boolean inclusive;
    public MagnitudeMoreThanFilter(double lowerLimitMagnitude,boolean inclusive)
    {
      if(Double.compare(lowerLimitMagnitude, 0.0) < 0)
        throw new IllegalArgumentException("Magnitude value must be Positive, given value: "+lowerLimitMagnitude);
      this.lowerLimitMagnitude = lowerLimitMagnitude;
      this.inclusive = inclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getMagnitude(),lowerLimitMagnitude) >= 0 :
          Double.compare(entry.getMagnitude(),lowerLimitMagnitude) > 0;
    }

  }

  /*
   * a public static class made for API Flexibility of Filtering Of EarthQuakes using the Filter method
   * MagnitudeRangeFilter is used to Filter the earth quakes to be of magnitude less than a given value
   * and more than another given value making a range amd it uses slightly less exact double comparisons not like the ExactMagmitudeFilter
   * which is extremely exact and has two boolean values for whether to include the upper limit and lower limit or not
   * using a negative value or upperLimit equal to or less than lower limit will result in Illegal Argument Exception
   * @CTOR_PARAM: double lower limit, a lower limit value where magnitudes can't be less than it
   * @CTOR_PARAM: boolean lowerInclusive, a boolean specifing wheter the lowerLimit should be inclusive or not
   * @CTOR_PARAM: double upperLimit, an upper bound limit where magnitudes can't be more than it
   * @CTOR_PARAM : boolean upperInclusve, a boolean specifying whether upperlimit should be inclusive or not
   * */
  public static class MagnitudeRangeFilter implements EarthQuakeFilter
  {
    private double lowerLimit, upperLimit;
    private boolean lowerInclusive, upperInclusive;
    public MagnitudeRangeFilter(double lowerLimit,boolean lowerInclusive, double upperLimit, boolean upperInclusive)
    {
      // no need to test if upperLimit is less than 0 as the lower limit, upperlimit test satisfies it as lower limit must be positive to reach it
      if(Double.compare(lowerLimit,0.0) < 0 || Double.compare(lowerLimit,upperLimit) >= 0)
        throw new IllegalArgumentException("upper and lower limit must be positive and lower limit can't be bigger than or equal to upper limit"+
            "\n if you want to exactly match a Magnitude then use ExactMagnitudeFilter, given Values lowerLimit: "+lowerLimit+", upperLimit: "+upperLimit);

      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
      this.lowerInclusive = lowerInclusive;
      this.upperInclusive = upperInclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      if(lowerInclusive && upperInclusive)
        return Double.compare(entry.getMagnitude(),lowerLimit) >= 0 && Double.compare(entry.getMagnitude(),upperLimit) <= 0;
      else if(lowerInclusive && !upperInclusive)
        return Double.compare(entry.getMagnitude(),lowerLimit) >= 0 && Double.compare(entry.getMagnitude(),upperLimit) < 0;
      else if(!lowerInclusive && upperInclusive)
        return Double.compare(entry.getMagnitude(),lowerLimit) > 0 && Double.compare(entry.getMagnitude(),upperLimit) <= 0;
      else
        return Double.compare(entry.getMagnitude(),lowerLimit) > 0 && Double.compare(entry.getMagnitude(),upperLimit) < 0;
    }
  }


  /* ===============================================  Depth Filters Section  =========================================*/
  /*
   * a public static class to Filter depth based on an exact value, using BigDecimals comparisons
   * to get exact values of entry and given depth and compares them equally using BigDecimal comparison
   * for exact value
   * implements EarthQuakeFilter which is a Markup interface for API Flexibility that implements Predicate<EarthQuakeEntry>
   * and Override the test method to return true if and only if the values exactly match
   * @Constructor_Param: exact depth value that you want depth to be filtered based on, given as a String to exactly match the values
   * */
  public static class ExactDepthFilter implements EarthQuakeFilter
  {
    private String depth;
    public ExactDepthFilter(String depth)
    {
      if(depth == null)
        throw new IllegalArgumentException("depth can't be null, given value: "+depth);
      try {
        if (new BigDecimal(depth).compareTo(new BigDecimal("0.0")) < 0)
          throw new IllegalArgumentException("Doesn't accept negative depth value, given value: "+depth);

        this.depth = depth;

      }catch(NumberFormatException ex)
      {
        throw new IllegalArgumentException("Must be numeric input for depth, given value: "+depth);
      }
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return entry.getExactDepth().compareTo(new BigDecimal(this.depth)) == 0 ;
    }
  }

  /*
   * a public static class made for API Flexibility of filtering of Earth Quakes
   * DepthLessThanFilter is used to Filter the earth quakes to be of depth less than
   * given value and it's slightly exact, not exact as the ExactDepthFilter as not needed as much
   * @Ctor Param: double value which is the upper Limit depth, if less than 0 gives Illegal ArgumentException
   * @Ctor_Param: boolean value which specifies whether to include upper limit or not
   * */
  public static class DepthLessThanFilter implements EarthQuakeFilter{

    private double upperLimitDepth;
    private boolean inclusive;
    public DepthLessThanFilter(double upperLimitDepth,boolean inclusive)
    {
      if(Double.compare(upperLimitDepth, 0.0) < 0)
        throw new IllegalArgumentException("Depth value must be Positive, given value: "+upperLimitDepth);
      this.upperLimitDepth = upperLimitDepth;
      this.inclusive = inclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getDepth(),upperLimitDepth) <= 0 :
          Double.compare(entry.getDepth(),upperLimitDepth) < 0;
    }

  }

  /*
   * a public static class made for API Flexibility of filtering of Earth Quakes
   * DepthMoreThanFilter is used to Filter the earth quakes to be of depth More than
   * given value and it's slightly exact, not exact as the ExactDepthFilter as not needed as much
   * @Ctor Param: double value which is the lower Limit depth, if less than 0 gives Illegal ArgumentException
   * @Ctor_Param: boolean value which specifies whether to include lower limit or not
   * */
  public static class DepthMoreThanFilter implements EarthQuakeFilter{

    private double lowerLimitDepth;
    private boolean inclusive;
    public DepthMoreThanFilter(double lowerLimitDepth,boolean inclusive)
    {
      if(Double.compare(lowerLimitDepth, 0.0) < 0)
        throw new IllegalArgumentException("Depth value must be Positive, given value: "+lowerLimitDepth);
      this.lowerLimitDepth = lowerLimitDepth;
      this.inclusive = inclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getDepth(),lowerLimitDepth) >= 0 :
          Double.compare(entry.getDepth(),lowerLimitDepth) > 0;
    }

  }

  /*
   * a public static class made for API Flexibility of Filtering Of EarthQuakes using the Filter method
   * DepthRangeFilter is used to Filter the earth quakes to be of depth less than a given value
   * and more than another given value making a range amd it uses slightly less exact double comparisons not like the ExactMagmitudeFilter
   * which is extremely exact and has two boolean values for whether to include the upper limit and lower limit or not
   * using a negative value or upperLimit equal to or less than lower limit will result in Illegal Argument Exception
   * @CTOR_PARAM: double lower limit, a lower limit value where depth can't be less than it
   * @CTOR_PARAM: boolean lowerInclusive, a boolean specifing wheter the lowerLimit should be inclusive or not
   * @CTOR_PARAM: double upperLimit, an upper bound limit where depth can't be more than it
   * @CTOR_PARAM : boolean upperInclusve, a boolean specifying whether upperlimit should be inclusive or not
   * */
  public static class DepthRangeFilter implements EarthQuakeFilter
  {
    private double lowerLimit, upperLimit;
    private boolean lowerInclusive, upperInclusive;
    public DepthRangeFilter(double lowerLimit,boolean lowerInclusive, double upperLimit, boolean upperInclusive)
    {
      // no need to test if upperLimit is less than 0 as the lower limit, upperlimit test satisfies it as lower limit must be positive to reach it
      if(Double.compare(lowerLimit,0.0) < 0 || Double.compare(lowerLimit,upperLimit) >= 0)
        throw new IllegalArgumentException("upper and lower limit must be positive and lower limit can't be bigger than or equal to upper limit"+
            "\n if you want to exactly match a Depth then use ExactDepthFilter, given Values lowerLimit: "+lowerLimit+", upperLimit: "+upperLimit);

      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
      this.lowerInclusive = lowerInclusive;
      this.upperInclusive = upperInclusive;
    }

    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      if(lowerInclusive && upperInclusive)
        return Double.compare(entry.getDepth(),lowerLimit) >= 0 && Double.compare(entry.getDepth(),upperLimit) <= 0;
      else if(lowerInclusive && !upperInclusive)
        return Double.compare(entry.getDepth(),lowerLimit) >= 0 && Double.compare(entry.getDepth(),upperLimit) < 0;
      else if(!lowerInclusive && upperInclusive)
        return Double.compare(entry.getDepth(),lowerLimit) > 0 && Double.compare(entry.getDepth(),upperLimit) <= 0;
      else
        return Double.compare(entry.getDepth(),lowerLimit) > 0 && Double.compare(entry.getDepth(),upperLimit) < 0;
    }
  }




  /*
   * A public static method that filters EarthQuakes according to EarthQuakeFilters var args
   * a kinda generic algorithm that is suitable for all filter usage and the filters used are
   * static classes of the EarthQuakeUtils
   * the method is static as it doesn't belong to an instance, it belongs to the class itself in concept
   * returns Iterable<EarthQuakeEntry>
   * @Param Iterable<EarthQuakeEntry> that holds the earthquake data in POJOs made by EarthQuakesParser
   * @Param EarthQuakeFilter var arg that holds any number of filters and all of them will be applied
   * */
  public static Iterable<EarthQuakeEntry> filter(Iterable<EarthQuakeEntry> data,EarthQuakeFilter... filters)
  {
    if(data == null)
      throw new IllegalArgumentException("please check your input, and if Live data >> check internet connection");
    if(filters == null || filters.length == 0) // if no filters, return the same List given without any operation
      return data;

    List<EarthQuakeEntry> filteredEntries = new ArrayList<>();

    // the sorting approach isn't suitable as different filters can be applied and also because
    // sorting nlogn while each element is n and for each filter n and m filters will never reach n elements
    // so O(n) and the sorting approach is nlogn + n so O(nlogn) which is worse
    for(EarthQuakeEntry entry : data)
    {
      if(EarthQuakeUtils.isApplicable(entry,filters))
        filteredEntries.add(entry);
    }
    return filteredEntries;
  }

  /*
   * A private helper method is made for returning boolean if all filters are accepted on entries
   * as if the filters loop is made inside the entry loop, and continue keyword is used, it will continue itself
   * not continue the main loop so a private helper method is needed
   *  static as it doesn't belong to the instance but to the class itself in concept
   * if any filter return false then false is returned, if the loop finished and no false is returned
   * then all filters are applicable and returns true
   * @Param : EarthQuakeEntry that the filters are tested against
   * @Param : filters vararg to be applied on each entry
   */

  private static boolean isApplicable(EarthQuakeEntry entry, EarthQuakeFilter... filters)
  {
    for(EarthQuakeFilter f : filters)
    {
      if(f == null) continue; // made for API reason in GUI
      if(!f.test(entry))
        return false;
    }
    return true;
  }




  /*
   * Parses the Entries from the given RSS Feed file into Features for markers on map
   * making a List of PointFeatures and making a reference to each PointFeature made
   * to put magnitude,depth,title,age properties in the HashMap of the pointFeature as its properties
   * using the EarthQuake Pojo properties
   * returns List<PointFeature>
   * @Param : Iterable<EarthQuakeEntry> made using the AbstractDataParser subclass EarthQuakesParser using .getParsedData()
   * */
  public static List<PointFeature> toPointFeatures(Iterable<EarthQuakeEntry> data)
  {
    List<PointFeature> parsedFeatures = new ArrayList<>();
    PointFeature tempPoint = null;
    for(EarthQuakeEntry e : data)
    {
      parsedFeatures.add((tempPoint = new PointFeature(new Location(e.getLatitude(),e.getLongitude()))));
      tempPoint.putProperty("magnitude",e.getExactMagnitude().doubleValue());
      tempPoint.putProperty("depth",e.getExactDepth().doubleValue());
      tempPoint.putProperty("title",e.getLocationTitle());
      tempPoint.putProperty("age",e.getAge());
    }

    return parsedFeatures;
  }

//  /*
//   *  a static method to make EarthQuake Markers given a List<PointFeature>,
//   *  that will be used to make a marker per each using the Location of it and its properties
//   *  @Param: List<PointFeature> a list containing the PointFeatures of each pojo object of earthquake entry can be made by static method
//   *  toPointFeature of this class
//   * */
//
//  public List<Marker> makeEarthQuakeMarkers(List<PointFeature> features)
//  {
//    List<Marker> markers = new ArrayList<>();
//    SimplePointMarker tempMarker = null; // to hold reference to the newly added marker
//    for(PointFeature pf : features) {
//      markers.add((tempMarker = new SimplePointMarker()));
//      tempMarker.setLocation(pf.getLocation());
//      tempMarker.setProperties(pf.getProperties());
//    }
//
//    lastMadeEarthQuakesMarkers = Collections.unmodifiableList(features);
//
//    return markers;
//  }

  /*
  * //TODO: right desc
  * */
  public static void printQuakes()
  {
    //TODO: implement it
  }

}
