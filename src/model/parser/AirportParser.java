package model.parser;/*
  Author: Hisham Maged
  Date : 7/28/2019
  Project Name : 
*/
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import javafx.stage.Stage;
import model.pojo.AirportEntry;
import controller.DataParserBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * <h1>AirportEntry</h1>
 * parses the CSV format files of openflights.org airport data
 * That has information about Airports and encapsulates it AirportEntry POJO
 * <b> Should be instansiated using DataParserBuilder class only </b>
 * @author Hisham Maged
 * @version 1.1
 * @since 28/7/2019
 * @see DataParserBuilder
 * @see AbstractXML
 * @see AbstractDataParser
 * @see DataParser
 * @see AirportEntry
 */
public class AirportParser extends AbstractCSV<AirportEntry>{

  private static final Pattern URL_VALIDATOR= null; //TODO: find Live Data if u want
  /**
   * Swing Filechooser constructor that opens up a Swing JFilechooser.
   * <p>
   * uses the super constructor to initialize the inputStream using the file from the JfFilechooser
   * that is validated to be of correct CSV extensions and the filechooser will have suitable header
   * for csv files
   * </p>
   * @throws IllegalArgumentException if no file was chosen.
   */
  public AirportParser() {
  }

  /**
   * Javafx Filechooser constructor that opens up a JavaFX Filechooser.
   * <p>
   * uses the super constructor to initialize the inputStream using the file from the Filechooser that
   * is validated to be of correct CSV extensions and the filechooser will have suitable header for
   * csv files
   * </p>
   * @param stage Current Stage of FX Application
   * @throws IllegalArgumentException if no file was chosen.
   */
  public AirportParser(Stage stage) {
    super(stage);
  }

  /**
   * File Path Constructor that holds the path of the file that will be used to make path of. and is
   * given the validator extensions as Swing Extensions as EndsWith is used with it
   *
   * @param filePath String filePath containing the filepath of the file
   * @throws NullPointerException if input string is null and can't check for it as super must be
   * first line
   * @throws IllegalArgumentException if filePath was invalid due to extensions
   */
  public AirportParser(String filePath) throws NullPointerException {
    super(filePath);
  }

  /**
   * File constructor that holds the file object pointing to file on disk.
   * <p>will be used to be converted into an InputStream that is validated using the Pattern that
   * holds the
   * valid extensions</p>
   *
   * @param file File that holds the input of Pojo entries
   * @throws IllegalArgumentException if file is null or file doesn't exist or not valid due to CSV
   * Pattern
   */
  public AirportParser(File file) {
    super(file);
  }

  /**
   * URL constructor and URL validator pattern constructor that makes an inputStream using that URL if
   * it's valid using that URL validator pattern that validates that it's from a specific site
   *
   * @param url URL that holds the input of Pojo entries
   * @throws IllegalArgumentException if invalid url or null
   */
  public AirportParser(URL url) {
    super(url, URL_VALIDATOR);
  }

  /**
   * Parses CSVFile and should be first method to get called.
   * using the Apache csv parser and Apache CSV format classes
   * using the default charset and default format of CSV
   * uses the InputStream made by the AbstractDataParser and is accessed using
   * the <code>.getSource()</code> method as it's private and uses the <code>.addPojo(T pojo)</code> to add
   * the pojo object made from each record
   * and it uses the entries List made by AbstractDataParser and it's of type AirportEntry
   * as you extended the AbstractCSV with AirportEntry type parameter
   * also that's why addPojo accepts AirPortEntry
   * @throws IllegalArgumentException if a null element was parsed
   * @return Same AirportParser object used for API Flexibility to use Aggregate Operations, null if an un-handled exception happens
   * */
  @Override
  public DataParser<AirportEntry> parse() {
    try {
      System.out.println("getting Airport data");
      CSVParser parser = CSVParser.parse(this.getSource(), Charset.defaultCharset(),
          CSVFormat.DEFAULT);
      for (CSVRecord record : parser.getRecords())
      {
        System.out.print(".");
        this.addPojo(new AirportEntry(
            record.get(0), // id
            record.get(1), // name
            record.get(3), // country name
            record.get(4), // 3 letter code
            record.get(5), // ICAO code
            record.get(6), // latitude
            record.get(7), // longitude
            record.get(8), // altitude
            record.get(9) //timezone
        ));

    }
      System.out.println();
    return this;
    }catch(IOException ex)
    {
      System.err.println(ex.getMessage());
    }
    catch(Exception ex)
    {
      System.err.println(ex.getMessage());
    }
    return null;
  }
}
