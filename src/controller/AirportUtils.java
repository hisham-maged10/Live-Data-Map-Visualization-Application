package controller;/*
  Author: Hisham Maged
  Date : 7/28/2019
  Project Name : 
*/
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.Feature.FeatureType;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.pojo.AirportEntry;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * <h1>AirportUtils</h1>
 * A Utility class made for Airport Entries, doesn't have a lot of features at the moment
 * @author Hisham Maged
 * @version 1.1
 * @since 28/7/2019
 * @see AirportEntry
 */
public class AirportUtils {

  private AirportUtils(){}

  /**
   * Parses the Entries from the given data Feed file into Features for markers on map.
   * Makes PointFeature of POJOs to be drawn on map, putting the properties of Airports as
   * setting a unique id of each feature which is the id of airport
   * properties of PointFeature with type property specifiying it's an airport, used later in project
   * @param data parsed Airport entries into POJOs
   * @return List containing PointFeatures out of POJOs to be drawn on map
   */
  public static List<PointFeature> toPointFeatures(Iterable<AirportEntry> data)
  {
    List<PointFeature> features = new ArrayList<>();
    PointFeature tempEntry = null; // made to share reference with each entry to put properteis for PointFeatures
    for(AirportEntry entry : data)
    {
      features.add(tempEntry = new PointFeature(new Location(entry.getLatitude(),entry.getLongitude())));
      tempEntry.setId(Integer.toString(entry.getID()));
      tempEntry.putProperty("name",entry.getName());
      tempEntry.putProperty("country",entry.getCountry());
      tempEntry.putProperty("code",entry.getIATA());
      tempEntry.putProperty("timezone",entry.getTimeZone());
      tempEntry.putProperty("altitude",entry.getAltitude());
      tempEntry.putProperty("type","airport");
    }
    return features;
  }

  /**
   * Makes and Returns a Map that holds the airport Feature ID as key and the Location as value.
   * @param airportsFeature List holding the PointFeatures of Airports
   * @return Map holding airport ID as key and location as value.
   */
  public static Map<Integer, Location> getAirportsMap(List<PointFeature> airportsFeature)
  {
    Map<Integer, Location> airports = new HashMap<>();
    for(PointFeature airport : airportsFeature)
    {

      airports.put(Integer.parseInt(airport.getId()),airport.getLocation());
    }
    return airports;
  }

  /**
   * Makes and gets the Routes of Airports as a List of ShapeFeatures.
   * Makes a List of Shape Feature each specifying a route with property source and destination
   * if not null
   * @param filePath String path of File that holds route data
   * @return List containing Shape Features that has routes from source to destination
   */
  public static List<ShapeFeature> getAirportRoutes(String filePath)
  {
    File routesFile = null;
    // validates the String given
    if(filePath == null || !filePath.toLowerCase().endsWith(".csv") || !(routesFile = new File(filePath)).exists())
      throw new IllegalArgumentException("Given filePath in airport routes isn't correct");

    List<ShapeFeature> routes = new ArrayList<>();

    try {
      // makes the CSV Parser to be used
      CSVParser parser = CSVParser.parse(routesFile, Charset.defaultCharset(), CSVFormat.DEFAULT);
        // loops on each record in file
        for(CSVRecord record : parser.getRecords())
        {
          // \N specifies Null in the routes data file as specified by openFlights site
          // checked if it has a destination and source before making a route object and adds it
          if(!record.get(3).equals("\\N") && !record.get(5).equals("\\N"))
          {
            ShapeFeature route = new ShapeFeature(Feature.FeatureType.LINES);
            route.putProperty("source",record.get(3));
            route.putProperty("destination",record.get(5));
            routes.add(route);
          }
        }
      return routes;
    }catch(IOException ex)
      {
        System.err.println(ex.getMessage());

      }
    return routes;
  }
}
