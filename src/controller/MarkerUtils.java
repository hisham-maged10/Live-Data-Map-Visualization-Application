package controller;/*
  Author: Hisham Maged
  Date : 7/22/2019
  Class Desc : A utility class made for marker facilities for the map
*/
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import model.marker.AbstractEarthQuakeMarker;
import model.marker.AbstractLocationMarker;
import model.marker.AirportMarker;
import model.marker.CityMarker;
import model.marker.LandEarthQuakeMarker;
import model.marker.OceanEarthQuakeMarker;
import model.pojo.EarthQuakeEntry;
import model.pojo.DataEntry;

/**
 * <h1>MarkerUtils</h1>
 * <p>
 *   A Utility class made for creating markers for UnFoldingMap API
 *   <ul>
 *     <li>Earthquake Markers</li>
 *     <li> City Markers</li>
 *   </ul>
 * </p>
 * @author Hisham Maged
 * @version 1.1
 * @since 22/7/2019
 * @see EarthQuakeUtils
 * @see MarkerUtils
 * @see WorldDataUtils
 * @see EarthQuakeEntry
 * @see DataEntry
 * @see AbstractEarthQuakeMarker
 * @see AbstractLocationMarker
 * @see CityMarker
 * @see LandEarthQuakeMarker
 * @see OceanEarthQuakeMarker
 */
public class MarkerUtils {

  // private static final predicate that represents the condition of type differentiation of earthquakes at the moment, change behaviour by changing implementation
  // implemented by lambda expression to get annonymous object of annonymous class using annonymous method, used in implementation of
  // EarthQuakesMarkersFunction
  private static final Predicate<PointFeature> earthQuakeMarkersPredicate = pf -> WorldDataUtils.isLand(pf);

  // private static final predicate that represents the condition of type differentiation of Locations at the moment, change behaviour by changing implementation
  // implemented by lambda expression to get annonymous object of annonymous class using annonymous method, used in implementation of
  // LocationMarkersFunction
  private static final Predicate<Feature> locationMarkersPredicate = pf -> WorldDataUtils.isAirport(pf);


  //private static final Function whose implementation is used in making of earthquakesMarkers for each point feature
  // returning a type if it satisfies the predicate implementation and returns another one if not
  private static final Function<PointFeature, AbstractEarthQuakeMarker> earthQuakesMarkersFunction = pf -> earthQuakeMarkersPredicate.test(pf)?
                                                                                                         new LandEarthQuakeMarker(pf) :
                                                                                                         new OceanEarthQuakeMarker(pf);
  //private static final Function whose implementation is used in making of Location Markers for each point feature
  // returning a type if it satisfies the predicate implementation and returns another one if not
  // makes city | airportt markers atm
  private static final Function<Feature, AbstractLocationMarker> locationMarkersFunction = pf -> locationMarkersPredicate.test(pf)?
                                                                                                                               new AirportMarker(pf) :
                                                                                                                               new CityMarker(pf,WorldDataUtils.getCityMarkerImage());


  /**
  * Makes the List<Marker> to be added to the map and List<Marker> will contain customized markers.
  * that implements the CustomizedMarker interface ,so it will hold different types of Customized Markers in same list
  * to return a single data structure to be added to map directly, uses the earthQuakeMarkersFunction
  * loops on each PointFeature checking to see of which earthquake type it is from the AbstractEarthQuakeMarker hierarchy
  * constructing the representing type using the earthQuakesMarkersFunction and adding it to the list of markers
  * @param locations List of PointFeature containing the locations of the earthquakes
   * @return A List of Markers due to given Point Features
  * */
  public static List<Marker> makeEarthQuakesMarkers(List<PointFeature> locations)
  {
    List<Marker> earthQuakeMarkers = new ArrayList<>();
    AbstractEarthQuakeMarker tempMarker = null;
    for(PointFeature pf : locations)
    {
      earthQuakeMarkers.add((tempMarker =MarkerUtils.earthQuakesMarkersFunction.apply(pf)));
      tempMarker.setLocation(pf.getLocation());
      tempMarker.setProperties(pf.getProperties());
    }
    return earthQuakeMarkers;
  }

  /**
   * Makes the List<Marker> to be added to the map and List<Marker> will contain customized markers.
   * that implements the CustomizedMarker interface ,so it will hold different types of Customized Markers in same list
   * to return a single data structure to be added to map directly, uses the locationMarkersFunction
   * loops on each PointFeature checking to see of which location type it is from the AbstractLocationMarker hierarchy
   * constructing the representing type using the locationMarkersFunction and adding it to the list of markers
   * @param locations list of Feature containing the locations of the locations
   * @return List containing Markers of corresponding to Locations PointFeature
   * */
  public static List<Marker> makeLocationMarkers(List<? extends Feature> locations)
  {
    List<Marker> locationMarkers = new ArrayList<>();
    for(Feature f : locations)
    {
      locationMarkers.add(MarkerUtils.locationMarkersFunction.apply(f));
    }
    return locationMarkers;
  }

  /**
   * Sorts the Markers due to their natural ordering and prints them to the limit given.
   * uses bounded wild card to increase API Flexability to accept any list of Applicable type Marker that implements Comparable interface.
   * @param markers List of Markers to be sorted and printed
   * @param limit int for given limit of earthquakes markers to be printed
   * */
  public static <T extends Marker & Comparable<? super T>> void printAndSortMarkers(List<? extends T> markers, int limit){
    int counter = 0;
    Collections.sort(markers);
    for(Marker m : markers)
    {
      if(counter < limit) {
        ++counter;
        System.out.println(m);
      }
    }

  }

  /**
   * Makes routes Markers depending on the routeFeature list and airport Maps made from AirportUtils.
   * @param routeFeatures List containing shape features of routes
   * @param airportsMap Map containing Airport ID as key and Location as value
   * @return List of route Markers made by simple Line Markers.
   */
  public static List<Marker> makeRoutesMarkers(List<ShapeFeature> routeFeatures, Map<Integer, Location> airportsMap)
  {
    List<Marker> routesMarkers = new ArrayList<>();
    for(ShapeFeature routeFeature : routeFeatures)
    {
      int source = Integer.parseInt((String) routeFeature.getProperty("source"));
      int dest = Integer.parseInt((String) routeFeature.getProperty("destination"));

      if(airportsMap.containsKey(source) && airportsMap.containsKey(dest))
      {
        routeFeature.addLocation(airportsMap.get(source));
        routeFeature.addLocation(airportsMap.get(dest));
      }
      SimpleLinesMarker line = new SimpleLinesMarker(routeFeature.getLocations(),routeFeature.getProperties());

      routesMarkers.add(line);
    }
    return routesMarkers;
  }

}
