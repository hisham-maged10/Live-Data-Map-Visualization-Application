package model.parser;/*
  Author: Hisham Maged
  Date : 7/21/2019
  Class Name : Provides the common behaviour and constructors of the DataParser interface
*/
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Optional;
import model.pojo.DataEntry;

/*
* Accepts only Pojo types that implements DataEntry and are comparables, later giving the accepted type to DataParser interface to keep hierarchy
* */

/**
 * AbstractDataParser defines common behaviour for the derived classes
 * @author Hisham Maged
 * @version 1.1
 * @since 21/7/2019
 * @see AbstractXML
 * @see AbstractCSV
 * @param <T> Accepts only Type parameters that implements DataEntry and are comparable
 */
public abstract class AbstractDataParser<T extends DataEntry & Comparable<? super T>> implements
    DataParser<T> {

  private List<T> entries; // the List that will hold the Pojo objects that implements DataEntry and Comparable
  private InputStream inputSource; // the input source of the data

  {
    this.entries = new ArrayList<>();
  }

  /**
  * Swing Filechooser Constructor that opens up the Swing File chooser that accepts files of given header and given extensions only.
  * made for API Flexibility for the lower hierarchy of DataParser
  * @param header A header string that represents the Type of Files that can be chosen by file chooser
  * @param extensions String vararg that represents the extensions of the files that can be chosen
  * given as "TXT" || "txt" (no dot), as the Swing version makes it implicitly
  * @throws IllegalArgumentException if no file was chosen or given invalid inputs
  * */
  public AbstractDataParser(String header, String... extensions)
  {
    if(!isValid(header,extensions))
      throw new IllegalArgumentException("Invalid inputs for header or extensions or both, given header: "+header+" given extensions: "+getExtensionsAsString(extensions));
    init(getFile(header, extensions));
  }
  /**
   * JavaFX FileChooser constructor that opens up the JavaFx File chooser that accepts files of given header and given extensions only.
   * made for API Flexibility for the lower hierarchy of DataParser
   * @param stage Stage of used FX Application
   * @param header A header string that represents the Type of Files that can be chosen by file chooser
   * @param extensions String vararg that represents the extensions of the files that can be chosen
   *         given as "*.TXT" || "*.txt" (asteric and dot before extension), as the Swing version makes it implicitly
   * @throws IllegalArgumentException if no file was chosen or given invalid inputs
   * */
  public AbstractDataParser(Stage stage, String header, String... extensions)
  {
    if(stage == null || !isValid(header,extensions))
      throw new IllegalArgumentException("Invalid inputs for stage or header or extensions or all, given stage: "+stage+" , given header: "+header+" given extensions: "+getExtensionsAsString(extensions));
    init(getFile(stage,header,extensions));
  }

  /**
  * FilePath constructor that uses the given FilePath to construct a File from. after
  * validating to be a valid filepath and the file it holds is of valid extensions using the
  * varargs String extensions and it can be ".txt" or "txt" as endsWith method was used
  * @param filePath The Path containing the file path of the file needed
  * @param extensions A String varargs that has the extensions to validate on
  * @throws IllegalArgumentException if invalid inputs, if filepath is incorrect or extensions are not applied
  * */
  public AbstractDataParser(Path filePath, String... extensions)
  {
    if(!isValid(filePath,extensions))
      throw new IllegalArgumentException("Invalid inputs for filePath or extensions or both, given filePath: "+filePath.toString()+" , given extensions: "+getExtensionsAsString(extensions));
    init(filePath.toFile());
  }

  /**
  * File constructor that takes a File that to be converted into an input stream.
  * and takes a compiled regex pattern to match the file name against it for extension validation
  * A pattern is given for API Flexibility for lower level abstract classes
  * @param file File to be converted to input stream ( it's name is converted to lower case)
  * @param fileValidator pattern to be matched against the file extensions (case insensitive as name of file is converted to lower case)
  * @throws IllegalArgumentException if pattern is of incorrect regex or file is null or pattern do not apply on it
   * */
  public AbstractDataParser(File file, Pattern fileValidator)
  {
    if(!isValid(file,fileValidator))
      throw new IllegalArgumentException("invalid inputs for file or fileValidator, given file: "+file.toString()+" , fileValidator: "+fileValidator.toString());
    init(file);
  }

  /**
   * URL constructor that takes a URL that to be converted into an input stream.
   * and takes a compiled regex pattern to match the url against it for extension validation
   * A pattern is given for API Flexibility for lower level abstract classes
   * @param url URL to be converted to input stream ( it's name is converted to lower case)
   * @param urlValidator pattern to be matched against the file extensions (case insensitive as url is converted to lower case)
   * @throws IllegalArgumentException if pattern is of incorrect regex or url is null or pattern do not apply on it
   * */
  public AbstractDataParser(URL url, Pattern urlValidator)
  {
    if(!isValid(url,urlValidator))
      throw new IllegalArgumentException("invalid inputs for url or urlValidator, given file: "+url.toString()+" , fileValidator: "+urlValidator.toString());
    init(url);
  }
  /**
  * Gets the Parsed Data from the input source,
  * @throws UnsupportedOperationException if <code>parse()</code> was not yet used
  * @return The parsed Data in an unmodefiable container so can't be added to
  * */
  @Override
  public List<T> getParsedData()
  {
    if(this.entries == null || this.entries.isEmpty())
      throw new UnsupportedOperationException("Can't use getParsedData() before using parse method");
    return Collections.unmodifiableList(this.entries);
  }

  /**
  * Gets the input source of the Data
  * @return The input source used after converted from file to inputstream or from url to inputStream
  * */
  @Override
  public InputStream getSource()
  {
    return this.inputSource;
  }

  /**
  * Adds a POJO object of same type as the hierarchy.
   * <p>
   *   A method made for the lower hierarchy levels to have the ability to add elements to the private
   *   inherited List of entries that
   * </p>
  * and adds it to the list, returning true if accepted, false if not
  * @param  pojo Pojo object to be added to the list
  * @throws IllegalArgumentException if null is given as input
  * */
  public boolean addPojo(T pojo)
  {
    if(pojo == null)
      throw new IllegalArgumentException("null is not accepted in the List of pojos, given pojo: "+pojo);
    return this.entries.add(pojo);
  }

  /*
  * A private helper method that initializes the input Stream making the validation of the file existing
  * using validateOptional
  * @Param Optional<File> returned from any version getFile Method
  * */
  private void init(Optional<File> fileOptional)
  {
    try {
      this.inputSource = new FileInputStream(validateOptional(fileOptional)); // gets an inputStream from File as FileInputStream

    }catch(IOException ex) // Should not happen, if so print the stack trace and terminate the program
    {
      System.err.println("Unexpected Error");
      ex.printStackTrace();
      System.exit(1);
    }
  }

  /*
   * A private helper method that initializes the input Stream making the validation of the file existing
   * @Param validated File to be converted to an inputStream
   * */
  private void init(File file)
  {
    try {
      this.inputSource = new FileInputStream(file); // gets an inputStream from File as FileInputStream

    }catch(IOException ex) // Should not happen, if so print the stack trace and terminate the program
    {
      System.err.println("Unexpected Error");
      ex.printStackTrace();
      System.exit(1);
    }
  }
  /*
  * A private helper method that initializes the input Stream making the validation of the url
  * @Param validated URL to be converted to an inputStream
  * */
  private void init(URL url)
  {
    try {
      this.inputSource = url.openStream(); // gets an inputStream from File as FileInputStream

    }catch(IOException ex) // Should not happen, if so print the stack trace and terminate the program
    {
      System.err.println("Unexpected Error");
      ex.printStackTrace();
      System.exit(1);
    }
  }

  /*
  * A private helper method that validates the header and extensions not to be null or empty
  * @Param: header that represents the accepted type of files to be validated
  * @Param: extensions of the type of file to be validated
  * */
  private boolean isValid(String header,String... extensions)
  {
    return header != null && !header.isEmpty() && extensions != null && extensions.length > 0;
  }

  /*
   * Validation method used to validate whether the given File object exists and is valid or not
   * @param: File file which holds the object pointing to file on disk containing the input to be parsed into POJO objects
   * @Param Pattern fileValidtor that have the compiled validation regex to check against filename
   * */
  private boolean isValid(File file,Pattern fileValidator)
  {
    return file != null && fileValidator != null && file.exists() && fileValidator.matcher(file.getName().toLowerCase()).matches();
  }

  /*
   * Validation method used to validate whether the given URL object exists and is valid or not
   * @param: URL url pointing to file on Internet containing the input to be parsed into POJO objects
   * @Param Pattern urlValidtor that have the compiled validation regex to check against url
   * */
  private boolean isValid(URL urlFile,Pattern urlValidator)
  {
    return urlFile != null && urlValidator != null && urlValidator.matcher(urlFile.toString().toLowerCase()).matches();
  }


  /*
   * Validation method used to validate whether the filePath is valid or not
   * and of valid extensions or not
   * false if not found or filePath is null or extensions is null or length of extensions array is zero
   * returns true if any extension is found at the end of the filePath in string version
   * @param: Path filePath which holds the filePath of the file containing the RSS feed
   * @Param: valid extensions for the parser made for API Flexibility for lower level abstract classes in the hierrachy
   * */
  private boolean isValid(Path filePath, String... extensions)
  {
    if(filePath == null || extensions == null || extensions.length == 0)
      return false;
    String fileName = filePath.toString();

    for(String extension : extensions)
      if(fileName.endsWith(extension))
        return true;

    return false;
  }

  /*
  * A private helper method that returns String representation of the extensions varargs
  * used in Exception handling
  * @Param: the extension varargs of the constructor
  * */
  private String getExtensionsAsString(String... extensions)
  {
    if(extensions == null || extensions.length == 0)
      return null;

    String output = "";
    for(String e : extensions)
      output+=e+" ";
    return output;
  }

  /*
   * Swing version >> JFileChooser
   * gets The File using a FileChooser and filters the files into given extensions
   * returning a NullableOptional to be checked for existance
   * @Param: validated header that represents the Type of files that can be chosen
   * @Param: validated extensions that represents the extensions of files that can be chosen in the form of "txt" for example
   * returns Nullable Optional<File> that is present if not null, otherwise it's not present
   * for flexibility in checking and getting rid of possible null pointer exception
   * */
  private Optional<File> getFile(String header, String... extensions)
  {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));
    chooser.setFileFilter(new FileNameExtensionFilter(header,extensions));
    chooser.setMultiSelectionEnabled(false);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.showOpenDialog(null);
    return Optional.ofNullable(chooser.getSelectedFile());
  }

  /*
   * Javafx version >> FileChooser
   * gets The File using a FileChooser and filters the files into given extensions
   * returning a NullableOptional to be checked for existance
   * @Param: validate stage that reperesnts the Stage of the program
   * @Param: validated header that represents the Type of files that can be chosen
   * @Param: validated extensions that represents the extensions of files that can be chosen in the form of "*.txt" for example
   * returns Nullable Optional<File> that is present if not null, otherwise it's not present
   * for flexibility in checking and getting rid of possible null pointer exception
   * */

  private Optional<File> getFile(Stage stage, String header, String... extensions)
  {
    FileChooser chooser = new FileChooser();
    chooser.setInitialDirectory(new File("."));
    chooser.setTitle("Choose the RSS Feed local file");
    chooser.getExtensionFilters().addAll(
        new ExtensionFilter(header,extensions)
    );
    return Optional.ofNullable(chooser.showOpenDialog(stage));
  }


  /*
  * a Private helper method that checks if the optional is present, if not then no file was chosen
  * as it's a NullableOptional from getFile method
  * throws an IllegalArgumentException with given message that no file was chosen
  * @Param: NullableOptional from getFile method to check if a file was chosen
  * used in Filechooser constructors before making an input stream with the file
  * */
  private File validateOptional(Optional<File> fileOptional)
  {
    if(fileOptional.isPresent())
      return fileOptional.get();
    throw new IllegalArgumentException("You must choose a File to continue");
  }
}
