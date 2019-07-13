package Controller;/*
  Author: Hisham Maged
  Date : 7/12/2019
  Project Name : A parser made for the LifeExpectancy feed data from the data world bank
*/
import Model.LifeExpectancyRecord;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class LifeExpectancyFeedParser {

  // a private field containing the LifeExpectancyRecords parsed from the given file
  private List<LifeExpectancyRecord> lifeExpectancyRecords;

  // Default Constructor that takes no parameters and uses a File Chooser to get the CSV file for lifeExpectancy data
  public LifeExpectancyFeedParser()
  {
    init(getFile());
  }
  // String Constructor that takes file path and is validated to be of csv format and that it is not null before making it a file
  public LifeExpectancyFeedParser(String filePath)
  {
    if(!isValidInput(filePath))
        throw new IllegalArgumentException("The data should be in CSV Format only and year should be from 1960 to 2017 inclusive only");
    init(new File(filePath));
  }

  // File Constructor that takes the File object and is validated that it exists and that it is not null and of CSV format before usage for parsing
  public LifeExpectancyFeedParser(File csvFile)
  {
    if(!isValidInput(csvFile))
      throw new IllegalArgumentException("The given file object must refer to a CSV format file only and year should be from 1960 to 2017 inclusive only");
    init(csvFile);
  }

  // URL Constructor that takes the URL object and no validations happens on it ( open for change ) and is used for parsing
  public LifeExpectancyFeedParser(URL csvFileURL)
  {
    init(csvFileURL);
  }
  /*
  * validates the filePath string to be path of a file that is of CSV format
  * @PARAMS path of CSV File to be parsed
  * */
  private boolean isValidInput(String filePath)
  {
    return filePath != null && filePath.toLowerCase().matches("^.*\\.csv$");
  }
  /*
  * Overlaoded File version of validation that validates the file to be not null and to actually exist and is of CSV format
  * @PARAMS file object that points to CSV file on Disk
  * */
  private boolean isValidInput(File file)
  {
    return file != null && file.exists() && file.getName().toLowerCase().matches("^.*\\.csv$");
  }

  /*
  * overloaded int version that validates that year is in between 1960 to 2017 inclusive as life expectancy data that we have
  * is from 1960 to 2017 only
  * @PARAM year needed to get life expectancy map containing all countries and their life expectancy value for that year
  * */
  private boolean isValidInput(int year)
  {
    return year >= 1960 && year <=2017;
  }

  /*
  * Uses a JFileChooser with extension Filter to be CSV only and if not selected uses the default data that is in data folder in project
  * */
  private File getFile()
  {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));
    chooser.setMultiSelectionEnabled(false);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setFileFilter(new FileNameExtensionFilter("CSV Files","CSV","csv","Csv"));
    return chooser.showOpenDialog(null) != -1 ? chooser.getSelectedFile() : new File("API_SP.DYN.LE00.IN_DS2_en_csv_v2_40967.csv");
  }

  /*
  * file version of method, takes the validated file and uses it for parsing
  * initializes the lifeExpectancy record field to have list containing POJOs of each record holding data of each record using given file
  * @PARAM validated CSV file that will be used for parsing
  * */
  private void init(File csvFile)
  {
    this.lifeExpectancyRecords = parseRecords(csvFile);
  }

  /*
  * URL version of method, takes the unvalidated URL ( open for change ) and uses it for parsing
  * initializes the life expectancy record field to have list containing POJOs of each record holding data of each record using given url
  * @PARAM unvalidated URL of CSV Live data that will be used for parsing
  * */
  private void init(URL csvFileURL)
  {
    this.lifeExpectancyRecords = parseRecords(csvFileURL);
  }

  /*
  * method can't be used if given list is null or empty throwing UnsupportedOperationException
  * takes the List of life Expectancy Records and uses it to output a Map<String,Float>  where key is Country Code
  * and Value is Life Expectancy for given year parameter that will be used by the Map
  * with Geographical locations to make a Life Expectancy map out of it with LifeExpectancy values for the given year
  * @PARAM List<LifeExpectancyRecord> holding data of each record in the parsed CSV file
  * @PARAM year needed for life expectancy values, must be valid year between 1960 to 2017 inclusive
  * @NotNullable, @NOTEMPTY list
  * */
  private Map<String,Float> parseRecordsIntoMap(List<LifeExpectancyRecord> lifeExpectancyRecords, int year)
  {

    if(lifeExpectancyRecords == null || lifeExpectancyRecords.isEmpty())
      throw new UnsupportedOperationException("Can't use this method unless records are loaded");
    if(!isValidInput(year))
      throw new IllegalArgumentException("Year input must be between 1960 to 2017 inclusive, given year: "+year);
    Map<String,Float> lifeExpectancyMap = new TreeMap<>();
    for(LifeExpectancyRecord record : lifeExpectancyRecords)
    {
      lifeExpectancyMap.put(record.getCountryCode(),record.getCertainYearExpectancy(year));
    }

    return lifeExpectancyMap;
  }

  /*
  * getter method that takes a year and returns the Map<String,Float> where key is country code and value is its life expectancy
  * for given year
  * @Param year ranging between 1960 to 2017 inclusive
  * */
  public Map<String,Float> getLifeExpectancyMap(int year) {

    return parseRecordsIntoMap(this.lifeExpectancyRecords,year);

  }
  /*
  * a getter method that returns the private field in its unmodefiable version so not be modified but outer source of class
  * and it holds POJO of each record in the Parsed CSV file
  * can't add or set in the returning list
  * */
  public List<LifeExpectancyRecord> getLifeExpectancyRecords()
  {
    return Collections.unmodifiableList(this.lifeExpectancyRecords);
  }

  /*
  * File version of method
  * Main method used in parsing of CSVFile using the Apache csv parser and Apache CSV format classes
  * uses the first record in the file as header and makes internally a map containing each record due to its index
  * and its String column name using the default charset and default format of CSV
  * @Param Validated CSV File used for parsing
  * */
  private List<LifeExpectancyRecord> parseRecords(File csvFile)
  {
    List<LifeExpectancyRecord> records = new ArrayList<>();
    try {
      CSVParser parser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.DEFAULT.withFirstRecordAsHeader());
      for(CSVRecord record : parser.getRecords())
      {
        records.add(new LifeExpectancyRecord(
            record.get(0), // using the name doesn't work // TODO: figure out why
            record.get("Country Code"),
            fillMap(new HashMap<>(57),record) // makes a Map of initialy capacity 57 holding 57 year data, Integer as key for year and float for lifeexpectancy value
        ));
      }

    }catch(IOException ex)
    {
      System.err.println(ex.getMessage());
    }catch(Exception ex)
    {
      System.err.println(ex.getMessage());

    }

    return records;
  }
  /*
  * Overloaded URL Version
  * @Param unvalidated url that should contaqin the CSV LIVE expectancy data // todo: try and validate the url
  * */
  private List<LifeExpectancyRecord> parseRecords(URL csvFileURL)
  {
    List<LifeExpectancyRecord> records = new ArrayList<>();
    try {
      CSVParser parser = CSVParser.parse(csvFileURL, Charset.defaultCharset(), CSVFormat.DEFAULT.withFirstRecordAsHeader());
      for(CSVRecord record : parser.getRecords())
      {
        records.add(new LifeExpectancyRecord(
            record.get(0), // using the name doesn't work // TODO: figure out why
            record.get("Country Code"),
            fillMap(new HashMap<>(57),record) // makes a Map of initialy capacity 57 holding 57 year data, Integer as key for year and float for lifeexpectancy value
        ));
      }

    }catch(IOException ex)
    {
      System.err.println(ex.getMessage());
    }catch(Exception ex)
    {
      System.err.println(ex.getMessage());

    }

    return records;
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

  /*
  * Made for testing only
  * */
//  public static void main(String[] args)
//  {
//    LifeExpectancyFeedParser parser = new LifeExpectancyFeedParser("./data/API_SP.DYN.LE00.IN_DS2_en_csv_v2_40967.csv");
//    System.out.println("for year: "+1960);
//    for(Map.Entry<String,Float> e : parser.getLifeExpectancyMap(1960).entrySet())
//      System.out.println("Country Code: "+e.getKey()+
//          "\nLife Expectancy: "+e.getValue());
//
//  }


}
