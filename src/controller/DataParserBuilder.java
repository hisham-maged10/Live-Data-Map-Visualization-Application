package controller;/*
  Author: Hisham Maged
  Date : 7/23/2019
  Project Name : A utility class made for building data parsers without the overhead of the user in other parts of the project
*/

import java.io.File;
import java.net.URL;
import javafx.stage.Stage;
import model.parser.EarthQuakesParser;
import model.parser.LifeExpectancyParser;

public class DataParserBuilder {

  /*
  * default constructor made private as the class should be only used in a static way
  * */
  private DataParserBuilder(){}

  /*
  * a private enum, static by nature that defines all the available ways of loading data
  * and will be used in the TypeBuilders
  * */
  private enum LoadType{
    SWING,FX,FILE_PATH,URL,FILE;
  }

  /*
  * A private interface that defines the interface of the TypeBuilder hierarchy
  * */
  private interface Buildable{
    // returns AbstractTypeBuilder as both XMLTypeBuilder and CSVTypeBuilder are subclasses of AbstractTypeBuilder
    public AbstractTypeBuilder swingFileChooser();
    public AbstractTypeBuilder fxFileChooser(Stage stage);
    public AbstractTypeBuilder url(URL url);
    public AbstractTypeBuilder filePath(String filePath);
    public AbstractTypeBuilder file(File file);
  }

  /*
   * a private abstract class that defines common CTor behaviour for TypeBuilders for not repeating code
   * */
  private static abstract class AbstractTypeBuilder{

    private LoadType usedLoad; // not hiding the same variable as they have the same name only but not same instance
    private String filePath; // holds the filePath if String CTOR is used
    private Stage stage; // holds the stage if Stage CTOR is used
    private URL url; // holds the URL if URL CTOR is used
    private File file; // holds the File if File CTOR is used
    // loadType CTOR for swingFile chooser
    public AbstractTypeBuilder()
    {
      this(LoadType.SWING,null,null,null,null);
    }
    // String CTOR for filePath as input source for parser
    public AbstractTypeBuilder(String filePath)
    {
      this(LoadType.FILE_PATH,filePath,null,null,null);
    }

    // Stage CTOR for FX file chooser
    public AbstractTypeBuilder(Stage stage)
    {
      this(LoadType.FX,null,stage,null,null);
    }

    public AbstractTypeBuilder(File file)
    {
      this(LoadType.FILE,null,null,null,file);
    }
    // URL CTOR for url as input source for parser
    public AbstractTypeBuilder(URL url)
    {
      this(LoadType.URL,null,null,url,null);
    }

    // Complete CTOR for rerouting and not repeating code
    public AbstractTypeBuilder(LoadType usedLoad, String filePath, Stage stage, URL url,File file)
    {
      this.usedLoad = usedLoad;
      this.filePath = filePath;
      this.stage = stage;
      this.url = url;
      this.file = file;
    }
    // inherited getter methods
    //TODO: make them somehow private and inherited so encapsulate behaviour for class user to have only earthquakes() or lifeExpectancy() methods
    public LoadType getUsedLoad() {
      return this.usedLoad;
    }

    public String getFilePath() {
      return this.filePath;
    }

    public Stage getStage() {
      return this.stage;
    }

    public URL getUrl() {
      return this.url;
    }

    public File getFile(){
      return this.file;
    }

  }

  /*
  * A utility class made as static class for package streamlining and it is responsible for the
  * creating of the required XMLParser type using it's methods choosing a way of loading of data
  * then using its static class XMLTypeBuilder to build the required type
  * */
  public static class XMLBuilder implements Buildable{

    /*
    * private default constructor because it should be instansiated using
    * DataParserBuilder static methods only
    * */
    private XMLBuilder(){}

    /*
    * returns the swing file chooser version
    * */
    @Override
    public XMLTypeBuilder swingFileChooser()
    {
      return new XMLTypeBuilder();
    }
    /*
    * returns the fx file chooser version
    * @Param: Stage of current fx application
    * */
    @Override
    public XMLTypeBuilder fxFileChooser(Stage stage)
    {
      return new XMLTypeBuilder(stage);
    }
    /*
    * returns the url version
    * @Param: url holding the data on internet
    * */
    @Override
    public XMLTypeBuilder url(URL url) {
      return new XMLTypeBuilder(url);
    }
    /*
    * returns the filepath version
    * @Param: file path of file holding the data
    * */
    @Override
    public XMLTypeBuilder filePath(String filePath) {
      return new XMLTypeBuilder(filePath);
    }
    /*
    * returns file version
    * @Param: File holding the data on disk
    * */
    @Override
    public XMLTypeBuilder file(File file) {
      return new XMLTypeBuilder(file);
    }

    /*
    * a utility static class that eventually is the one that creates the required
    * actual type parser, using the loadType field to load the data in the specified way
    * */
    public static class XMLTypeBuilder extends AbstractTypeBuilder
    {
      /*
      * private constructor as it should be only instansiated using the XMLBuilder class
      * not on its own, takes a LoadType enum value and assigns it to the private field usedLoad that
      * will be used in making the final instansiation of required actual type of Parser
      * puts the loadType as Swing
      * Swing fileChooser CTOR
      * @NOParam
      * */
      private XMLTypeBuilder()
      {
        super();
      }
      /*
      * private String CTOR that puts the
      * loadtype as the file path ctor of type's parser
      * @Param: filePath string to the file
      * */
      private XMLTypeBuilder(String filePath) {
        super(filePath);
      }

      /*
       * private Stage CTOR that puts the
       * loadtype as the FX filechooser ctor of type's parser
       * @Param: Stage that holds the current FX APplication stage
       * */
      private XMLTypeBuilder(Stage stage) {
        super(stage);
      }
      /*
       * private URL CTOR that puts the
       * loadtype as the URL Ctor of type's parser
       * @Param: url object pointing to the file on internet
       * */
      private XMLTypeBuilder(URL url) {
        super(url);
      }

      /*
       * private File CTOR that puts the
       * loadtype as the File ctor of type's parser
       * @Param: File object pointing to the file on disk
       * */
      private XMLTypeBuilder(File file)
      {
        super(file);
      }

      /*
      * A complete CTOR made for not repeating code and using constructor re-route
      * */
      private XMLTypeBuilder(LoadType usedLoad, String filePath, Stage stage, URL url, File file)
      {
        super(usedLoad,filePath,stage,url,file);
      }

      public EarthQuakesParser earthquakes()
      {
        switch(this.getUsedLoad())
        {
          case SWING: return new EarthQuakesParser();
          case FX:    return new EarthQuakesParser(this.getStage());
          case URL:   return new EarthQuakesParser(this.getUrl());
          case FILE:  return new EarthQuakesParser(this.getFile());
          case FILE_PATH: return new EarthQuakesParser(this.getFilePath());
          default: throw new IllegalArgumentException("This should not happen, happened in XMLTypeBuilder");
        }
      }

    }

  }

  /*
   * A utility class made as static class for package streamlining and it is responsible for the
   * creating of the required CSVParser type using it's methods choosing a way of loading of data
   * then using its static class CSVTypeBuilder to build the required type
   * */
  public static class CSVBuilder implements Buildable{

    /*
     * private default constructor because it should be instansiated using
     * DataParserBuilder static methods only
     * */
    private CSVBuilder(){}

    /*
     * returns the swing file chooser version
     * */
    @Override
    public CSVTypeBuilder swingFileChooser() {
      return new CSVTypeBuilder();
    }
    /*
     * returns the fx file chooser version
     * @Param: Stage of current fx application
     * */
    @Override
    public CSVTypeBuilder fxFileChooser(Stage stage) {
      return new CSVTypeBuilder(stage);
    }
    /*
     * returns the url version
     * @Param: url holding the data on internet
     * */
    @Override
    public CSVTypeBuilder url(URL url) {
      return new CSVTypeBuilder(url);
    }
    /*
     * returns the filepath version
     * @Param: file path of file holding the data
     * */
    @Override
    public CSVTypeBuilder filePath(String filePath) {
      return new CSVTypeBuilder(filePath);
    }

    /*
     * returns file version
     * @Param: File holding the data on disk
     * */
    @Override
    public CSVTypeBuilder file(File file) {
      return new CSVTypeBuilder(file);
    }

    /*
     * a utility static class that eventually is the one that creates the required
     * actual type parser, using the loadType field to load the data in the specified way
     * */
    public static class CSVTypeBuilder extends AbstractTypeBuilder
    {
      /*
       * private constructor as it should be only instansiated using the XMLBuilder class
       * not on its own, takes a LoadType enum value and assigns it to the private field usedLoad that
       * will be used in making the final instansiation of required actual type of Parser
       * puts the loadType as Swing
       * Swing fileChooser CTOR
       * @NOParam
       * */
      private CSVTypeBuilder()
      {
        super();
      }
      /*
       * private String CTOR that puts the
       * loadtype as the file path ctor of type's parser
       * @Param: filePath string to the file
       * */
      private CSVTypeBuilder(String filePath) {
        super(filePath);
      }

      /*
       * private Stage CTOR that puts the
       * loadtype as the FX filechooser ctor of type's parser
       * @Param: Stage that holds the current FX APplication stage
       * */
      private CSVTypeBuilder(Stage stage) {
        super(stage);
      }
      /*
       * private URL CTOR that puts the
       * loadtype as the URL Ctor of type's parser
       * @Param: url object pointing to the file on internet
       * */
      private CSVTypeBuilder(URL url) {
        super(url);
      }

      /*
       * private File CTOR that puts the
       * loadtype as the File ctor of type's parser
       * @Param: File object pointing to the file on disk
       * */
      private CSVTypeBuilder(File file)
      {
        super(file);
      }

      /*
       * A complete CTOR made for not repeating code and using constructor re-route
       * */
      private CSVTypeBuilder(LoadType usedLoad, String filePath, Stage stage, URL url, File file)
      {
        super(usedLoad,filePath,stage,url,file);
      }


      public LifeExpectancyParser lifeExpectancy()
      {
        switch(this.getUsedLoad())
        {
          case SWING: return new LifeExpectancyParser();
          case FX:    return new LifeExpectancyParser(this.getStage());
          case URL:   return new LifeExpectancyParser(this.getUrl());
          case FILE:  return new LifeExpectancyParser(this.getFile());
          case FILE_PATH: return new LifeExpectancyParser(this.getFilePath());
          default: throw new IllegalArgumentException("This should not happen, happened in CSVTypeBuilder");
        }
      }
    }

  }


  /*
  * A utility method made to return the static class XMLBuilder that returns the user specifiying XMLParser type
  * in XMLParser hierarchy and the ways of loading of data
  * @NOPARAM
  * */
  public static XMLBuilder buildXMLParser(){
      return new XMLBuilder();

  }

  /*
   * A utility method made to return the static class CSVBuilder that returns the user specifiying CSVParser type
   * in CSVParser hierarchy and the ways of loading of data
   * @NOPARAM
   *  */
  public static CSVBuilder buildCSVParser()
  {
    return new CSVBuilder();
  }
}
