package model.parser;/*
  Author: Hisham Maged
  Date : 7/21/2019
  Class Description : An Abstract Class defining the common behaviours of AbstractDataParser on XML Files
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
public abstract class AbstractXML<T extends DataEntry & Comparable<? super T>> extends AbstractDataParser<T>{

  // A private Static final String array that holds the extensions of XML files needed for JFileChooser of Swing
  private static final String[] EXTENSIONS_SWING = {"atom","Atom","ATOM","XML","xml","Xml"};
  // A private Static final String array that holds the extensions of XML files needed for FX file Chooser
  private static final String[] EXTENSIONS_FX = {"*.atom","*.Atom","*.ATOM","*.xml","*.Xml","*.XML"};
  // A private Static final String that holds the Header of files that will be used in filechoosers
  private static final String HEADER = "Atom/XML/RSSFeed files";
  // a private static final pattern that holds the regex validator of file
  private static final Pattern FILE_PATTERN = Pattern.compile(".*\\.(atom|xml)"); // captures .atom or .xml for lower case filename

  /*
  * the default constructor that opens up a Swing JFilechooser
  * and uses the super constructor to initialize the inputStream using the file from the JfFilechooser that is
  * validated by given extensions and the filechooser will have the given header
  * */
  public AbstractXML()
  {
    super(HEADER,EXTENSIONS_SWING);
  }

  /*
   * the Stage constructor that opens up a JavafX Filechooser
   * and uses the super constructor to initialize the inputStream using the file from the JfFilechooser that is
   * validated by given extensions and the filechooser will have the given header
   * @Param: stage of FX Application
   * */
  public AbstractXML(Stage stage)
  {
    super(stage,HEADER,EXTENSIONS_FX);
  }

  /*
  * A string Constructor that holds the path of the file
  * that will be used to make path of, and is given the validator extensions as Swing Extensions as EndsWith is used with it
  * throws NullPointerException as String may be null and can't check for it as super must be first line
  * @Param: String filePath containing the filepath of the file
  * */
  public AbstractXML(String filePath) throws NullPointerException
  {
    super(Paths.get(filePath),EXTENSIONS_SWING);
  }

  /*
  * File constructor that holds the file object pointing to file on disk
  * that will be used to be converted into an InputStream if valid using the going to be used Pattern that holds the
  * valid extensions
  * @Param: File file that holds the input of Pojo entries
  * */
  public AbstractXML(File file)
  {
    super(file,FILE_PATTERN);
  }
  /*
  * the URL and URL validator pattern constructor that makes an inputStream
  * using that URL if it's valid using that URL validator pattern that validates
  * that it's from a specific site
  * @Param: URL url that holds the input of Pojo entries
  * @Param: Pattern urlValidator that validates the url to be of a specific site
  * */
  public AbstractXML(URL url, Pattern urlValidator)
  {
    super(url,urlValidator);
  }


}
