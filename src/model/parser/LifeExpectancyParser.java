package model.parser;/*
  Author: Hisham Maged
  Date : 7/21/2019
  Project Desc : a Sub-class of AbstractCSV that defines the behaviour of parsing of RSS feed of CSV format hence extending AbstractCSV from local file or Website(TODO)
*/

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javafx.stage.Stage;
import model.pojo.LifeExpectancyEntry;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/*
 * extends AbstractCSV and it's a generic hierhachy so given LifeExpectancyEntry which is the POJO that will hold the data of LifeExpectancy records
 * */
public class LifeExpectancyParser extends AbstractCSV<LifeExpectancyEntry> {

  // private static final that is used to validate the URL to be of the WolrdBank site and it's lower case
  // because in hierarchy, it converts the url to lower case
  //TODO: Find the suitable URL for the LifeExpectancy Values
  private static final Pattern URL_VALIDATOR = Pattern.compile("");

  /*
   * default constructor that will use the AbstractCSV constructor to get the
   * Correct File format of CSV files using a SWing File Chooser
   * */
  public LifeExpectancyParser() {
    super();
  }

  /*
   * Stage constructor that will use the AbstractCSV constructor to get the
   * Correct File format of CSV files using an FX File Chooser
   * @Param: Stage of the FX Application
   * */
  public LifeExpectancyParser(Stage stage) {
    super(stage);
  }
  /*
   * String constructor that will use the AbstractCSV constructor to validate
   * the file and make the inputStream using it
   * @Param: String path of the file
   * throws NullPointerException as null check can't be handled here so Exception propagation
   * */
  public LifeExpectancyParser(String filePath) throws NullPointerException {
    super(filePath);
  }
  /*
   * File Consturctor that will use the AbstractCSV constructor to validate the file
   * to be of correct extension and such and then makes the InputStream object using it
   * @Param: File pointing to file object in memory
   * */
  public LifeExpectancyParser(File file) {
    super(file);
  }

  /*
   * URL Constructor that will use the AbstractCSV constructor that will use the AbstractDataParser
   * Constructor to validate the URL of given url link using the private Static final Pattern that validates
   * the url to be of specified location
   * @Param: Url holding the data needed
   * */
  public LifeExpectancyParser(URL url) {
    super(url, URL_VALIDATOR);
  }

  /*
   * Main method used in parsing of CSVFile using the Apache csv parser and Apache CSV format classes
   * uses the first record in the file as header and makes internally a map containing each record due to its index
   * and its String column name using the default charset and default format of CSV
   * uses the InputStream made by the AbstractDataParser and is accessed using
   * the .getSource method as it's private and uses the .addPojo to add
   * the pojo object made from each record
   * if null object then will throw IllegalARgumentException
   * and it uses the entries List made by AbstractDataParser and it's of type LifeExpectancyEntry
   * as you extended the AbstractCSV with LifeExpectancyEntry type parameter
   * also that's why addPojo accepts LifeExpectancyEntry
   * */
  @Override
  public boolean parse()
  {
    try {
      CSVParser parser = CSVParser.parse(this.getSource(), Charset.defaultCharset(), CSVFormat.DEFAULT.withFirstRecordAsHeader());
      for(CSVRecord record : parser.getRecords())
      {
        this.addPojo(new LifeExpectancyEntry(
            record.get(0), // using the name for country name doesn't work // TODO: figure out why
            record.get("Country Code"),
            fillMap(new HashMap<>(57),record) // makes a Map of initialy capacity 57 holding 57 year data, Integer as key for year and float for lifeexpectancy value
        ));
      }
    return true; // returns true since the loop finished without exceptions
    }catch(IOException ex)
    {
      System.err.println(ex.getMessage());
    }catch(Exception ex)
    {
      System.err.println(ex.getMessage());

    }

    return false; // if reached here in code then an exception happened
  }

  /*
   * Private helper method that fills the map with years as keys and their respected life expectancy value for each record
   * in csv file being parsed
   * @PARAM Map<Integer,Float> that will be given to POJO object
   * @PARAM current record being parsed in CSV file
   * */
  private Map<Integer,Float> fillMap(Map<Integer,Float> yearMap,CSVRecord currentRecord)
  {
    for(int i = 1960 ; i <= 2017 ; ++i)
    {
      if(!currentRecord.get(i-1956).isEmpty())
        yearMap.put(i,Float.valueOf(currentRecord.get(i-1956))); // because years columns start from 4 so 1960-1956 is 4 and so on till 57
      else
        yearMap.put(i,0.0F); // 0 as value if empty string (no life expectancy value recorded for the year at hand)
    }
    return yearMap;
  }
}
