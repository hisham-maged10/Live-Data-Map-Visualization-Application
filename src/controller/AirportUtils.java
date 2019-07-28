package controller;/*
  Author: Hisham Maged
  Date : 7/28/2019
  Project Name : 
*/
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import java.util.ArrayList;
import java.util.List;
import model.pojo.AirportEntry;
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
   * properties of PointFeature
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
      tempEntry.putProperty("name",entry.getName());
      tempEntry.putProperty("country",entry.getCountry());
      tempEntry.putProperty("code",entry.getIATA());
      tempEntry.putProperty("timezone",entry.getTimeZone());
      tempEntry.putProperty("altitude",entry.getAltitude());
    }
    return features;
  }
}
