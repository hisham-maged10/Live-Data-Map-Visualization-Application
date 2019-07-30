package controller;/*
  Author: Hisham Maged
  Date : 7/21/2019
  Class desc : A utitility class for EarthQuake data facility methods
*/

import de.fhpotsdam.unfolding.marker.Marker;
import model.pojo.EarthQuakeEntry;
import model.pojo.DataEntry;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * <h1>EarthQuakeUtils</h1>
 * <p>
 *   A utility class that can Filter Earthquake data based on
 *   <ul>
 *     <li><b>Magnitude</b>
   *     <ul>
     *     <li>Earthquakes having a certain magnitude</li>
     *     <li>Earthquakes having magnitude less than a given value</li>
     *     <li>Earthquakes having magnitude more than a given value</li>
     *     <li>Earthquakes whose magnitude fall in a range of magnitude values</li>
   *     </ul>
 *     </li>
 *     <li><b>Depth</b>
   *     <ul>
 *       <li>Earthquakes having a certain magnitude</li>
   *     <li>Earthquakes having magnitude less than a given value</li>
   *     <li>Earthquakes having magnitude more than a given value</li>
   *     <li>Earthquakes whose magnitude fall in a range of magnitude values</li>
 *      </ul>
 *      </li>
 *   </ul>
 * </p>
 * <p>
 *   Also can Convert Earthquake Data into PointFeatures to be used by UnfoldingMap API
 * </p>
 * @author Hisham Maged
 * @since 21/7/2019
 * @version 1.1
 * @see EarthQuakeEntry
 * @see DataEntry
 */
public class EarthQuakeUtils {
  //TODO: CHECK WHY DEPTH FILTERS ARE BUGGY
  /**
   * <h1>
   *   EarthQuakeFilter
   * </h1>
   * A Markup interface made for API Flexibility in calling of <code>EarthQuakeUtils</code> filter method
   * @see EarthQuakeUtils
   * @see ExactMagnitudeFilter
   * @see MagnitudeLessThanFilter
   * @see MagnitudeMoreThanFilter
   * @see MagnitudeRangeFilter
   * @see ExactDepthFilter
   * @see DepthLessThanFilter
   * @see DepthMoreThanFilter
   * @see DepthRangeFilter
   * @see #filter
   * */
  public interface EarthQuakeFilter extends Predicate<EarthQuakeEntry> {}

  /* ===============================================  Magnitude Filters Section  =========================================*/
  /**
   * <h1>ExactMagnitudeFilter</h1>
   * <p>a public static class to Filter magnitude based on an exact value, using BigDecimals comparisons.
   * to get exact values of entry and given magnitude and compares them equally using BigDecimal comparison
   * for exact value</p>
   * implements EarthQuakeFilter which is a Markup interface for API Flexibility that implements Predicate<EarthQuakeEntry>
   * and Override the test method to return true if and only if the values exactly match
   * @author Hisham Maged
   * @version 1.1
   * @since 21/7/2019
   * */
  public static class ExactMagnitudeFilter implements EarthQuakeFilter
  {
    private String magnitude;

    /**
     * Sole Constructor that takes magnitude as a String and extracts its value in a BigDecimal
     * to keep exactness
     * @throws IllegalArgumentException if null is given or value is less than 0
     * @param magnitude exact magnitude value that you want magnitude to be filtered based on
     */
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

    /**
     * Tests whether the given magnitude of instance is exactly equal to given entry's exact magnitude
     * @param entry EarthQuakeEntry that holds the pojo object to be compared against given magnitude
     * @return True if the earthquake entry has magnitude exactly equal to given one, false otherwise
     */
    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return entry.getExactMagnitude().compareTo(new BigDecimal(this.magnitude)) == 0 ;
    }
  }

  /**
   * <h1>MagnitudeLessThanFilter</h1>
   * <p>a public static class made for API Flexibility of filtering of Earth Quakes
   * MagnitudeLessThanFilter is used to Filter the earth quakes to be of magnitude less than
   * given value and it's slightly exact, not exact as the ExactMagnitudeFilter as not needed as much
   * </p>
   * @author Hisham Maged
   * @version 1.1
   * @since 21/7/2019
   * */
  public static class MagnitudeLessThanFilter implements EarthQuakeFilter{

    private double upperLimitMagnitude;
    private boolean inclusive;

    /**
     * Sole Constructor that takes the upper limit magnitude as a double and checks
     * against it if it's applicable
     * @param upperLimitMagnitude double value holding the Upper limit magnitude
     * @param inclusive boolean value to specify whether that upper magnitude is inclusive or not
     */
    public MagnitudeLessThanFilter(double upperLimitMagnitude,boolean inclusive)
    {
      if(Double.compare(upperLimitMagnitude, 0.0) < 0)
        throw new IllegalArgumentException("Magnitude value must be Positive, given value: "+upperLimitMagnitude);
      this.upperLimitMagnitude = upperLimitMagnitude;
      this.inclusive = inclusive;
    }

    /**
     * Tests whether the given magnitude of instance is less than or (equal based on inclusive boolean) to given entry's magnitude
     * @param entry EarthQuakeEntry that holds the pojo object to be compared against given magnitude
     * @return True if the earthquake entry has magnitude less than given one, false otherwise
     */
    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getMagnitude(),upperLimitMagnitude) <= 0 :
          Double.compare(entry.getMagnitude(),upperLimitMagnitude) < 0;
    }

  }

  /** <h1>MagnitudeMoreThanFilter</h1>
   * <p>
   * a public static class made for API Flexibility of filtering of Earth Quakes
   * MagnitudeMoreThanFilter is used to Filter the earth quakes to be of magnitude More than
   * given value and it's slightly exact, not exact as the ExactMagnitudeFilter as not needed as much
   * </p>
   * @author Hisham Maged
   * @version 1.1
   * @since 21/7/2019
   * */
  public static class MagnitudeMoreThanFilter implements EarthQuakeFilter{

    private double lowerLimitMagnitude;
    private boolean inclusive;

    /**
     * Sole Constructor that takes the lower limit magnitude as a double and checks
     * against it if it's applicable
     * @param lowerLimitMagnitude double value holding the lower limit magnitude
     * @param inclusive boolean value to specify whether that lower magnitude is inclusive or not
     */
    public MagnitudeMoreThanFilter(double lowerLimitMagnitude,boolean inclusive)
    {
      if(Double.compare(lowerLimitMagnitude, 0.0) < 0)
        throw new IllegalArgumentException("Magnitude value must be Positive, given value: "+lowerLimitMagnitude);
      this.lowerLimitMagnitude = lowerLimitMagnitude;
      this.inclusive = inclusive;
    }

    /**
     * Tests whether the given magnitude of instance is more than or (equal based on inclusive boolean) to given entry's magnitude
     * @param entry EarthQuakeEntry that holds the pojo object to be compared against given magnitude
     * @return True if the earthquake entry has magnitude more than given one, false otherwise
     */
    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getMagnitude(),lowerLimitMagnitude) >= 0 :
          Double.compare(entry.getMagnitude(),lowerLimitMagnitude) > 0;
    }

  }

  /**
   * <h1>MagnitudeRangeFilter</h1>
   * <p>a public static class made for API Flexibility of Filtering Of EarthQuakes using the Filter method
   * MagnitudeRangeFilter is used to Filter the earth quakes to be of magnitude less than a given value
   * and more than another given value making a range amd it uses slightly less exact double comparisons not like the ExactMagmitudeFilter
   * which is extremely exact and has two boolean values for whether to include the upper limit and lower limit or not
   * using a negative value or upperLimit equal to or less than lower limit will result in Illegal Argument Exception
   * </p>
   * @author Hisham Maged
   * @version 1.1
   * @since 21/7/2019
   * */
  public static class MagnitudeRangeFilter implements EarthQuakeFilter
  {
    private double lowerLimit, upperLimit;
    private boolean lowerInclusive, upperInclusive;

    /**
     * Sole constructor that takes lower limit magnitude and upper limit magnitude and whether they're inclusive
     * or not to test if it's applicable against entries
     * @param lowerLimit double lower limit, a lower limit value where magnitudes can't be less than it
     * @param lowerInclusive boolean lowerInclusive, a boolean specifing wheter the lowerLimit should be inclusive or not
     * @param upperLimit double upperLimit, an upper bound limit where magnitudes can't be more than it
     * @param upperInclusive boolean upperInclusve, a boolean specifying whether upperlimit should be inclusive or not
     */
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
    /**
     * Tests whether the given magnitude of instance is more than or (equal based on inclusive boolean) to given entry's magnitude
     * and less than or equal to upper limit magnitude of given entry's magnitude
     * @param entry EarthQuakeEntry that holds the pojo object to be compared against given magnitude
     * @return True if the earthquake entry has magnitude less than upper limit and more than lower limit given, false otherwise
     */
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
  /**
   *  <h1>ExactDepthFilter</h1>
   * <p>a public static class to Filter depth based on an exact value, using BigDecimals comparisons
   * to get exact values of entry and given depth and compares them equally using BigDecimal comparison
   * for exact value</p>
   * implements EarthQuakeFilter which is a Markup interface for API Flexibility that implements Predicate<EarthQuakeEntry>
   * and Override the test method to return true if and only if the values exactly match
   * @author Hisham Maged
   * @version 1.1
   * @since 21/7/2019
   * */
  public static class ExactDepthFilter implements EarthQuakeFilter
  {
    private String depth;
    /**
     * Sole Constructor that takes depth as a String and extracts its value in a BigDecimal
     * to keep exactness
     * @throws IllegalArgumentException if null is given or value is less than 0
     * @param depth exact magnitude value that you want depth to be filtered based on
     */
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

    /**
     * Tests whether the given depth of instance is exactly equal to given entry's exact depth
     * @param entry EarthQuakeEntry that holds the pojo object to be compared against given depth
     * @return True if the earthquake entry has depth equal to given one, false otherwise
     */
    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return entry.getExactDepth().compareTo(new BigDecimal(this.depth)) == 0 ;
    }
  }

  /**
   * <h1>DepthLessThanFilter</h1>
   * <p>
   * a public static class made for API Flexibility of filtering of Earth Quakes
   * DepthLessThanFilter is used to Filter the earth quakes to be of depth less than
   * given value and it's slightly exact, not exact as the ExactDepthFilter as not needed as much
   * </p>
   * @author Hisham Maged
   * @version 1.1
   * @since 21/7/2019
   * */
  public static class DepthLessThanFilter implements EarthQuakeFilter{

    private double upperLimitDepth;
    private boolean inclusive;
    /**
     * Sole Constructor that takes the upper limit depth as a double and checks
     * against it if it's applicable
     * @param upperLimitDepth double value holding the Upper limit depth
     * @param inclusive boolean value to specify whether that upper depth is inclusive or not
     */
    public DepthLessThanFilter(double upperLimitDepth,boolean inclusive)
    {
      if(Double.compare(upperLimitDepth, 0.0) < 0)
        throw new IllegalArgumentException("Depth value must be Positive, given value: "+upperLimitDepth);
      this.upperLimitDepth = upperLimitDepth;
      this.inclusive = inclusive;
    }
    /**
     * Tests whether the given depth of instance is less than or (equal based on inclusive boolean) to given entry's depth
     * @param entry EarthQuakeEntry that holds the pojo object to be compared against given depth
     * @return True if the earthquake entry has depth less than given one, false otherwise
     */
    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getDepth(),upperLimitDepth) <= 0 :
          Double.compare(entry.getDepth(),upperLimitDepth) < 0;
    }

  }

  /** <h1>DepthMoreThanFilter</h1>
   * <p>
   * a public static class made for API Flexibility of filtering of Earth Quakes
   * DepthMoreThanFilter is used to Filter the earth quakes to be of depth More than
   * given value and it's slightly exact, not exact as the ExactDepthFilter as not needed as much
   * </p>
   * @author Hisham Maged
   * @version 1.1
   * @since 21/7/2019
   * */
  public static class DepthMoreThanFilter implements EarthQuakeFilter{

    private double lowerLimitDepth;
    private boolean inclusive;
    /**
     * Sole Constructor that takes the lower limit depth as a double and checks
     * against it if it's applicable
     * @param lowerLimitDepth double value holding the lower limit depth
     * @param inclusive boolean value to specify whether that lower depth is inclusive or not
     */
    public DepthMoreThanFilter(double lowerLimitDepth,boolean inclusive)
    {
      if(Double.compare(lowerLimitDepth, 0.0) < 0)
        throw new IllegalArgumentException("Depth value must be Positive, given value: "+lowerLimitDepth);
      this.lowerLimitDepth = lowerLimitDepth;
      this.inclusive = inclusive;
    }

    /**
     * Tests whether the given depth of instance is more than or (equal based on inclusive boolean) to given entry's depth
     * @param entry EarthQuakeEntry that holds the pojo object to be compared against given depth
     * @return True if the earthquake entry has depth more than given one, false otherwise
     */
    @Override
    public boolean test(EarthQuakeEntry entry)
    {
      return inclusive ?
          Double.compare(entry.getDepth(),lowerLimitDepth) >= 0 :
          Double.compare(entry.getDepth(),lowerLimitDepth) > 0;
    }

  }

  /**
   * <h1>DepthRangeFilter</h1>
   * <p>
   * a public static class made for API Flexibility of Filtering Of EarthQuakes using the Filter method
   * DepthRangeFilter is used to Filter the earth quakes to be of depth less than a given value
   * and more than another given value making a range amd it uses slightly less exact double comparisons not like the ExactMagmitudeFilter
   * which is extremely exact and has two boolean values for whether to include the upper limit and lower limit or not
   * using a negative value or upperLimit equal to or less than lower limit will result in Illegal Argument Exception
   * </p>
   * @author Hisham Maged
   * @version 1.1
   * @since 21/7/2019
   * */
  public static class DepthRangeFilter implements EarthQuakeFilter
  {
    private double lowerLimit, upperLimit;
    private boolean lowerInclusive, upperInclusive;
    /**
     * Sole constructor that takes lower limit depth and upper limit depth and whether they're inclusive
     * or not to test if it's applicable against entries
     * @param lowerLimit double lower limit, a lower limit value where depth can't be less than it
     * @param lowerInclusive boolean lowerInclusive, a boolean specifing wheter the lowerLimit should be inclusive or not
     * @param upperLimit double upperLimit, an upper bound limit where depth can't be more than it
     * @param upperInclusive boolean upperInclusve, a boolean specifying whether upperlimit should be inclusive or not
     */
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

    /**
     * Tests whether the given depth of instance is more than or (equal based on inclusive boolean) to given entry's depth
     * and less than or equal to upper limit magnitude of given entry's depth
     * @param entry EarthQuakeEntry that holds the pojo object to be compared against given magnitude
     * @return True if the earthquake entry has depth less than upper limit and more than lower limit given, false otherwise
     */
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




  /**
   * Filters EarthQuakes according to EarthQuakeFilters var args.
   * General algorithm that is suitable for all filter usage and the filters used are
   * static classes of the EarthQuakeUtils
   * the method is static as it doesn't belong to an instance, it belongs to the class itself in concept
   * @param data Iterable<EarthQuakeEntry> that holds the earthquake data in POJOs made by EarthQuakesParser
   * @param filters EarthQuakeFilter var arg that holds any number of filters and all of them will be applied
   * @return Iterable containing EarthQuakeEntries that are applicable to given filters
   * * */
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




  /**
   * Parses the Entries from the given RSS Feed file into Features for markers on map.
   * making a List of PointFeatures and making a reference to each PointFeature made
   * to put magnitude,depth,title,age properties in the HashMap of the pointFeature as its properties
   * using the EarthQuake Pojo properties
   * @param data Iterable<EarthQuakeEntry> made using the AbstractDataParser subclass EarthQuakesParser using .getParsedData()
   * @return returns List<PointFeature> corresponding to given EarthQuakeEntries to be used with UnfoldingMap API
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




}
