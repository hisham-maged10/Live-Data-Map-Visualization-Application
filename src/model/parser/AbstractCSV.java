package model.parser;/*
  Author: Hisham Maged
  Date : 7/21/2019
  Class Description : An Abstract Class defining the common behaviours of AbstractDataParser on CSV Files
*/

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import javafx.stage.Stage;
import model.pojo.DataEntry;

/*
 * Accepts only elements of POJO dataEntry interface and that are comparable, extends the AbstractDataParser abstract class
 * */

/**
 * An Abstract Class defining the common behaviours of AbstractDataParser on CSV Files.
 * @author Hisham Maged
 * @version 1.1
 * @since 21/7/2019
 * @see AbstractDataParser
 * @see DataParser
 * @see EarthQuakesParser
 * @param <T> Accepts only Types that implements dataEntry interface and that are comparable
 */
public abstract class AbstractCSV<T extends DataEntry & Comparable<? super T>> extends AbstractDataParser<T> {

  // A private Static final String array that holds the extensions of XML files needed for JFileChooser of Swing
  private static final String[] EXTENSIONS_SWING = {"CSV","csv","Csv"};
  // A private Static final String array that holds the extensions of XML files needed for FX file Chooser
  private static final String[] EXTENSIONS_FX = {"*.CSV","*.csv","*.Csv"};
  // A private Static final String that holds the Header of files that will be used in filechoosers
  private static final String HEADER = "CSV Files";
  // a private static final pattern that holds the regex validator of file
  private static final Pattern FILE_PATTERN = Pattern.compile(".*\\.csv"); // captures .atom or .xml for lower case filename

  /**
   * Swing Filechooser constructor that opens up a Swing JFilechooser.
   * <p>
   * uses the super constructor to initialize the inputStream using the file from the JfFilechooser that is
   * validated to be of correct CSV extensions and the filechooser will have suitable header for csv files
   * </p>
   * @throws IllegalArgumentException if no file was chosen.
   * */
  public AbstractCSV() {
    super(HEADER, EXTENSIONS_SWING);
  }

  /**
   * Javafx Filechooser constructor that opens up a JavaFX Filechooser.
   * <p>
   * uses the super constructor to initialize the inputStream using the file from the Filechooser that is
   * validated to be of correct CSV extensions and the filechooser will have suitable header for csv files
   * </p>
   * @param stage Current Stage of FX Application
   * @throws IllegalArgumentException if no file was chosen.
   * */
  public AbstractCSV(Stage stage) {
    super(stage, HEADER, EXTENSIONS_FX);
  }

  /**
   * File Path Constructor that holds the path of the file that will be used to make path of. and is given the validator extensions as Swing Extensions as EndsWith is used with it
   * @param filePath String filePath containing the filepath of the file
   * @throws NullPointerException if input string is null and can't check for it as super must be first line
   * @throws IllegalArgumentException if filePath was invalid due to extensions
   * */
  public AbstractCSV(String filePath) throws NullPointerException {
    super(Paths.get(filePath), EXTENSIONS_SWING);
  }

  /**
   * File constructor that holds the file object pointing to file on disk.
   * <p>will be used to be converted into an InputStream that is validated using the Pattern that holds the
   * valid extensions</p>
   * @param file File that holds the input of Pojo entries
   * @throws IllegalArgumentException if file is null or file doesn't exist or not valid due to CSV Pattern
   * */
  public AbstractCSV(File file)  {
    super(file, FILE_PATTERN);
  }
  /**
   * URL constructor and URL validator pattern constructor that makes an inputStream
   * using that URL if it's valid using that URL validator pattern that validates
   * that it's from a specific site
   * @param url URL that holds the input of Pojo entries
   * @param urlValidator Pattern that validates the url to be of a specific site
   * @throws IllegalArgumentException if invalid url or null
   * */
  public AbstractCSV(URL url, Pattern urlValidator) {
    super(url, urlValidator);
  }
}
