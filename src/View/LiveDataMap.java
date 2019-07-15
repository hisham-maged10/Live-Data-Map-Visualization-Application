package View;
/*
  Author: Hisham Maged
  Date : 7/11/2019
  Project Name : A Map with visualized live data
*/

import Controller.EarthQuakesFeedParser;
import Controller.EarthQuakesFeedParser.EarthQuakeFilter;
import Controller.EarthQuakesMarkerHandler;
import Controller.EarthQuakesMarkerHandler.EarthQuakeMarker;
import Controller.LifeExpectancyMarkerHandler;
import Model.EarthQuakeEntry;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import processing.core.PApplet;
import processing.core.PSurface;
import processing.javafx.PSurfaceFX;

public class LiveDataMap extends PApplet {

  /* private ENUM made to choose a map (in case of earthquakes, to choose a coloring sequence)*/
  private enum Map{
    MAGNITUDE_EARTHQUAKES,DEPTH_EARTHQUAKES,LIFEEXPECTANCY;
  }
  // Used Map reference
  private UnfoldingMap map;
  // Earth quakes map (same for any earthquake property, as same data but different coloring)
  private UnfoldingMap earthQuakesMap;
  // used earthQuakesMarkers reference (same for any earthquake property, as same data but different coloring)
  private List<SimplePointMarker> earthQuakesMarkers;
  // used lifeExpectancyMarkers reference
  private List<Marker> lifeExpectancyMarkers;
  //LifeExpectancy Map reference
  private UnfoldingMap lifeExpectancyMap;

  // private List<ScreenPosition> holding the markers of earthquake magnitudes to be customized in draw
  private List<ScreenPosition> magnitudeScreenPositions;

  /*
  * String URLS for live earth quake data
  * past 30 days
  * past 7 days
  * past day
  * past hour
  * */
  private static final String LIVE_EARTHQUAKE_DATA_PAST_30_DAYS_STRING = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_month.atom";
  private static final String LIVE_EARTHQUAKE_DATA_PAST_7_DAYS_STRING = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
  private static final String LIVE_EARTHQUAKE_DATA_PAST_DAY_STRING = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.atom";
  private static final String LIVE_EARTHQUAKE_DATA_PAST_HOUR_STRING = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.atom";
  /*
   * URLS for live earth quake data
   * past 30 days
   * past 7 days
   * past day
   * past hour
   * */
  private final static URL LIVE_EARTHQUAKE_DATA_PAST_30_DAYS = makeURL(LIVE_EARTHQUAKE_DATA_PAST_30_DAYS_STRING);
  private final static URL LIVE_EARTHQUAKE_DATA_PAST_7_DAYS = makeURL(LIVE_EARTHQUAKE_DATA_PAST_7_DAYS_STRING);
  private final static URL LIVE_EARTHQUAKE_DATA_PAST_DAY = makeURL(LIVE_EARTHQUAKE_DATA_PAST_DAY_STRING);
  private final static URL LIVE_EARTHQUAKE_DATA_PAST_HOUR = makeURL(LIVE_EARTHQUAKE_DATA_PAST_HOUR_STRING);

  /*
  * Light earthQuake value : 4.0
  * moderate earthQuake Value : 4.0 - 5.0
  * intense earthquake value : 10.0
  * */
  private static final double LIGHT_EARTHQUAKE = 4.0;
  private static final double MODERATE_EARTHQUAKE = 5.0;
  private static final double INTENSE_EARTHQUAKE = 10.0;

  /*
  * Light Depth value : 0-50
  * Moderate Depth Value: 50-200
  * Intense : 200+
  * */
  private static final double LIGHT_DEPTH = 0.0;
  private static final double MODERATE_DEPTH = 50.0;
  private static final double INTENSE_DEPTH = 300.0;

  /*
  * Colors for makres of light,moderate,intense magnitude earthquake markers
  * not static because color method can't be invoked in static context
  * */
  private final int LIGHT_EARTHQUAKE_COLOR = color(6,175,194,100);
  private final int MODERATE_EARTHQUAKE_COLOR = color(251,255,0,130);
  private final int INTENSE_EARTHQUAKE_COLOR = color(191,34,40,150);

  /*
   * Colors for makres of light,moderate,intense depth earthquake markers
   * not static because color method can't be invoked in static context
   * */
  private final int LIGHT_DEPTH_COLOR = color(242,255,56,130);
  private final int MODERATE_DEPTH_COLOR = color(214,71,24,100);
  private final int INTENSE_DEPTH_COLOR = color(148,28,32,200);


  /*
  * Year used to get data of Life Expectancy of all countries
  * not final static as can be changed
  * */
  private int LIFE_EXPECTANCY_YEAR = 2017;

  /*
  * key choosers for showing earthquake magnitude data depth data or lifeExpectancy map
  * */

  private static final char MAGNITUDE_EARTHQUAKES_KEY = '1';
  private static final char DEPTH_EARTHQUAKES_KEY = '2';
  private static final char LIFE_EXPECTANCY_KEY = '3';

  /*
  * Key choosers for zooming in or out of map
  * */
  private static final char ZOOM_IN_KEY = '=';
  private static final char ZOOM_OUT_KEY = '-';

  // first opened map is set to Map.Magnitude_EarthQuakes by default
  private Map usedMap = Map.MAGNITUDE_EARTHQUAKES;

  // default map selection in case of changing data set and another Map rather than earthquakes is chosen, used in GUI
  private static final Map DEFAULT_EARTHQUAKE_MAP_CHOICE = Map.MAGNITUDE_EARTHQUAKES;


  // default provider of map
  private static final AbstractMapProvider DEFAULT_PROVIDER = new Microsoft.RoadProvider();

  // road provider of map
  private static final AbstractMapProvider ROAD_PROVIDER = new Microsoft.RoadProvider();
  // Hybrid between Aerial and road provider of map
  private static final AbstractMapProvider HYBRID_PROVIDER = new Microsoft.HybridProvider();
  // Aerial provider of map
  private static final AbstractMapProvider AERIAL_PROVIDER = new Microsoft.AerialProvider();




  // main setup method, the looks of the window
  @Override
  public void settings()
  {
    size(displayWidth,displayHeight-100,FX2D); // -100 so not to fill the whole screen
    smooth(8); // anti-allising x8 (remove if program is slow)
    initMap();
  }

  // method that will embedd the PApplet processing into JavaFX Application
  @Override
  public PSurface initSurface()
  {
    return makeFXSurface();
  }
  // private helper method that is used to do the process of embedding the Papplet into FX App
  /*
  * Using what the Processing Core does when rending with FX2D to make our own FXApp
  * embedding the PApplet inside it making a VBox holding the PApplet as lowerChild
  * and Upper Child as a Menu bar holding Operations menu and Map Selection Menu
  * for more info:
  * https://stackoverflow.com/questions/28266274/how-to-embed-a-papplet-in-javafx
  * */
  private PSurface makeFXSurface()
  {
    PSurface surface = super.initSurface(); // makes the surface that processing uses to sketch on
    PSurfaceFX fxSurface = (PSurfaceFX) surface; // since we're rendering using FX2D, then it uses a Canvas to sketch on wrapped on what's called a native
    Canvas canvas = (Canvas) fxSurface.getNative(); // getting the native which is the canvas since FX2d is used
    Stage stage = (Stage) canvas.getScene().getWindow(); // getting the scene of the canvas then getting the stage from it
    stage.setTitle("Live Data Visualization Map"); // setting title of the stage

    // ========================= Filtering Options Menu =======================
    Menu filterMenu = new Menu("Filtering Options"); // main item of operations Menu
    MenuItem bothFilters = new MenuItem("Filter by Magnitude, Depth");

    bothFilters.setOnAction(e -> filterOperation());

    filterMenu.getItems().addAll(bothFilters);



    // ======================== Operations Menu ==================
    Menu operationsMenu = new Menu("Operations");  // Operations menu object
    operationsMenu.getItems().add(filterMenu); // adding the FilterMenu to the Operations Menu



    // ======================= Map Selection Menu ====================
    Menu mapSelectionMenu = new Menu("Maps"); // making the Map Selection Menu
    Menu earthQuakeMap = new Menu("Earthquakes"); // earthquake coloring logic selection
    MenuItem magnitudeEarthQuakes = new MenuItem("Magnitude"); // magnitude map
    MenuItem depthEarthQuakes = new MenuItem("Depth"); // depth map
    earthQuakeMap.getItems().addAll(magnitudeEarthQuakes,depthEarthQuakes);
    MenuItem lifeExpectancyMap = new MenuItem("Life Expectancy"); // lifeExpectancyMap

    magnitudeEarthQuakes.setOnAction(e -> swapMap(Map.MAGNITUDE_EARTHQUAKES));
    depthEarthQuakes.setOnAction(e -> swapMap(Map.DEPTH_EARTHQUAKES));
    lifeExpectancyMap.setOnAction(e -> swapMap(Map.LIFEEXPECTANCY));


    mapSelectionMenu.getItems().addAll(earthQuakeMap,lifeExpectancyMap); // adding map items to map menu


    // ===================== EarthQuake Data Section ====================
    Menu earthQuakeData = new Menu("Earthquakes");
    MenuItem lastHour = new MenuItem("Last Hour (Live)");
    MenuItem lastDay = new MenuItem("Last Day (Live)");
    MenuItem lastWeek = new MenuItem("Last Week (Live)");
    MenuItem lastMonth = new MenuItem("Last Month (Live)");
    MenuItem local = new MenuItem("Local Data (Offline)");

    // checks in second parameter if the used map is life expectancy map, if so puts to default earthQuakeMap which is Magnitude due to Final variable DEFAULT_EARTHQUAKE_MAP_CHOICE
    // else puts the used map as it could be any earthquake type map so doesn't change the marker coloring
    // confirms if the user want to filter the dataset using confirmFiltering, if so a filter dialog opens to get filters input
    lastHour.setOnAction( e-> {if(confirmFiltering())
      changeData(LIVE_EARTHQUAKE_DATA_PAST_HOUR,this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE: this.usedMap,getFilterDialog()); // for added filters functionality
      else
      changeData(LIVE_EARTHQUAKE_DATA_PAST_HOUR,this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE: this.usedMap);});

    lastDay.setOnAction( e-> {if(confirmFiltering())
      changeData(LIVE_EARTHQUAKE_DATA_PAST_DAY,this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE: this.usedMap,getFilterDialog()); // for added filters functionality
    else
      changeData(LIVE_EARTHQUAKE_DATA_PAST_DAY,this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE: this.usedMap);});

    lastWeek.setOnAction( e-> {if(confirmFiltering())
      changeData(LIVE_EARTHQUAKE_DATA_PAST_7_DAYS,this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE: this.usedMap,getFilterDialog()); // for added filters functionality
    else
      changeData(LIVE_EARTHQUAKE_DATA_PAST_7_DAYS,this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE: this.usedMap);});

    lastMonth.setOnAction( e-> {if(confirmFiltering())
      changeData(LIVE_EARTHQUAKE_DATA_PAST_30_DAYS,this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE: this.usedMap,getFilterDialog()); // for added filters functionality
    else
      changeData(LIVE_EARTHQUAKE_DATA_PAST_30_DAYS,this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE: this.usedMap);});


    // given a Stage because FileChooser of fx needs a stage to be opened from.
    local.setOnAction( e-> {if(confirmFiltering())
      changeData(stage,this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE: this.usedMap,getFilterDialog()); // for added filters functionality
    else
      changeData(stage,this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE: this.usedMap);});

    earthQuakeData.getItems().addAll(lastHour,lastDay,lastWeek,lastMonth,local);

    // ===================== LifeExpectancy Data Section ====================
    // the user is prompted to input the year that the value of life expectancy is set on, default is LIFE_EXPECTANCY_YEAR
    Menu lifeExpectancyData = new Menu("Life Expectancy");
    MenuItem localLife = new MenuItem("Change Year (default: 2017)");

    localLife.setOnAction( e->{ if(chooseLifeExpectancyYear())changeData(Map.LIFEEXPECTANCY);} );

    lifeExpectancyData.getItems().add(localLife);


    // ====================== Data Menu Section ========================
    Menu dataSelectionMenu = new Menu("Data Selection");
    dataSelectionMenu.getItems().addAll(earthQuakeData,lifeExpectancyData);


    //======================= Provider Menu Section ====================
    Menu providerMenu = new Menu("Provider Selection");
    MenuItem roadProvider = new MenuItem("Road Map");
    MenuItem aerialProvider = new MenuItem("Aerial Map");
    MenuItem hybridProvider = new MenuItem("Hybrid Map");

    roadProvider.setOnAction( e -> this.map.mapDisplay.setProvider(ROAD_PROVIDER));
    aerialProvider.setOnAction( e -> this.map.mapDisplay.setProvider(AERIAL_PROVIDER));
    hybridProvider.setOnAction( e -> this.map.mapDisplay.setProvider(HYBRID_PROVIDER));


    providerMenu.getItems().addAll(roadProvider,aerialProvider,hybridProvider);
    // ====================== Menu bar section ========================
    MenuBar menuBar = new MenuBar(); // makes a menu bar
    menuBar.getMenus().addAll(operationsMenu,mapSelectionMenu,dataSelectionMenu,providerMenu); // adds Operation Menu and MapSelection menu


    // ====================== VBox Container ==========================
    VBox container = new VBox(); // main container of FX Application
    container.getChildren().addAll(menuBar,canvas);


    // ====================== Scene Section ===========================
    Scene fxScene = new Scene(container); // new scene containing the menu bar and canvas in vbox container

    // ====================== Stage Section ===========================
    Platform.runLater( () -> stage.setScene(fxScene)); // replaces the scene of the stage with our own made one
    // lambda expression as the parameter is Runnable so annonymous method using annonymous object of annonymous subclass
    // as it's a SAM Interface (Funcional) and overrides the method run, which takes no parameters hence the ()

    return surface;
  }






  // the draw loop, invoked due to any change or loop (determined in backend of Processing library)
  @Override
  public void draw()
  {
    background(30);
    this.map.draw();
    makeScreenPositions();
    writeLocation();
    addLegend();
    setRestriction();
  }

  /*
  * a method that initializes both the earthquake map and life expectancy map to be of Microsoft Road Provider Map
  * and adds interactivity to the map
  * and uses the default XML File and gets the earthQuakesMarkers of it using the EarthQuakesMarkerHandler Class
  * and uses the default CSV file for lifeExpectancy using the LifeExpectancyMarkerHandler class
  * and adds those earthQuakesMarkers and life expectancy to the map
  * setting the default map reference to earthquake data maps
  *
  * */
  private void initMap()
  {

    // =========================================== EarthQuakes Section ====================================


    this.earthQuakesMap = new UnfoldingMap(this,0,0,1920,1080,DEFAULT_PROVIDER);
    MapUtils.createDefaultEventDispatcher(this,this.earthQuakesMap);
    /*
    * if you want to filter the earthquake data, use respected EarthQuakesFilter objects for more info about them
    * check the EarthQuakeFeedParser static classes
    * change the 1st parameter in makeEarthQuakeMarkers
    * no 1st parameter >> FileChooser to choose ATOM file
    * String >> filePath
    * File >> Atom file object
    * URL >> live data url
    * */
    this.earthQuakesMarkers = EarthQuakesMarkerHandler
        .makeEarthQuakeMarkers("./data/2.5_week.atom");

    this.earthQuakesMap.zoomToLevel(3);
    this.earthQuakesMap.setZoomRange(3,20);
    this.earthQuakesMap.setPanningRestriction(this.earthQuakesMap.getCenter(),width/2f); //makes map centered and doesn't go out of range


    // ===========================================  Life Expectancy Section ====================================

    this.lifeExpectancyMap = new UnfoldingMap(this,0,0,1920,1080, DEFAULT_PROVIDER);
    MapUtils.createDefaultEventDispatcher(this,this.lifeExpectancyMap);
    /*
     * change first parameter in makeLifeExpectancyMarkers
     * no 1st parameter >> FileChooser to choose CSV file
     * String >> filePath
     * File >> CSV file object
     * URL >> live data url
     * */
    this.lifeExpectancyMarkers = LifeExpectancyMarkerHandler.makeLifeExpectancyMarkers(this,"./data/API_SP.DYN.LE00.IN_DS2_en_csv_v2_40967.csv",LIFE_EXPECTANCY_YEAR);
    this.lifeExpectancyMap.addMarkers(this.lifeExpectancyMarkers);
    this.lifeExpectancyMap.zoomToLevel(3);
    this.lifeExpectancyMap.setZoomRange(3,20);
    this.lifeExpectancyMap.setPanningRestriction(this.lifeExpectancyMap.getCenter(),width/2f); //makes map centered and doesn't go out of range

    this.map = this.earthQuakesMap;
  }

  /*
   * Private helper method that adds customized screen positions instead of markers for earthquake magnitude or depth data
   * using the map enum and static method of EarthQuakesMarkerHandler showMarkers() using Marker enum
   * note, what changes is the coloring only as the data of depth and magnitude is the same earthquake data
   *  */
  private void makeScreenPositions()
  {
    switch(this.usedMap)
    {
      case MAGNITUDE_EARTHQUAKES:
        EarthQuakesMarkerHandler.showMarkers(this,this.earthQuakesMap,this.earthQuakesMarkers,
            EarthQuakeMarker.MAGNITUDE,LIGHT_EARTHQUAKE,MODERATE_EARTHQUAKE,INTENSE_EARTHQUAKE,LIGHT_EARTHQUAKE_COLOR,MODERATE_EARTHQUAKE_COLOR,INTENSE_EARTHQUAKE_COLOR);
        break;
      case DEPTH_EARTHQUAKES:

        EarthQuakesMarkerHandler.showMarkers(this,this.earthQuakesMap,this.earthQuakesMarkers,
            EarthQuakeMarker.DEPTH,LIGHT_DEPTH,MODERATE_DEPTH,INTENSE_DEPTH,LIGHT_DEPTH_COLOR,MODERATE_DEPTH_COLOR,INTENSE_DEPTH_COLOR);
        break;
    }
  }

  /*
  * depending on Map Enum of selected Map "usedMap" private field
  * adds Legend on right hand side with Magnitude and
  * their respected circle color and size for case of EarthQuakes
  * adds legend for life Expectancy
  * with red shade meaning low life expectancy
  * blue shade meaning high life epxectancy
  * in between is the range from 40 to 90
  * */
  private void addLegend()
  {
    fill(color(230));
    noStroke();
    rect(30,height-300,140,250);
    textSize(13);
    fill(color(0));
    switch(this.usedMap)
    {
      case MAGNITUDE_EARTHQUAKES:
        text("Legend",65,height-350+75);
        text(MODERATE_EARTHQUAKE+" + Magnitude",60,height-350+120);
        text(LIGHT_EARTHQUAKE+" + Magnitude",60,height-350+170);
        text("Below "+LIGHT_EARTHQUAKE,60,height-350+220);
        fill(INTENSE_EARTHQUAKE_COLOR);
        ellipse(45,height-350+115,18,18);
        fill(MODERATE_EARTHQUAKE_COLOR);
        ellipse(45,height-350+165,15,15);
        fill(LIGHT_EARTHQUAKE_COLOR);
        ellipse(45,height-350+215,12,12);
        break;
      case DEPTH_EARTHQUAKES:
        text("Legend",65,height-350+75);
        text(INTENSE_DEPTH+" + Depth",60,height-350+120);
        text(MODERATE_DEPTH+" + Depth",60,height-350+170);
        text("Below "+MODERATE_DEPTH,60,height-350+220);
        fill(INTENSE_DEPTH_COLOR);
        ellipse(45,height-350+115,18,18);
        fill(MODERATE_DEPTH_COLOR);
        ellipse(45,height-350+165,15,15);
        fill(LIGHT_DEPTH_COLOR);
        ellipse(45,height-350+215,12,12);
                      break;
      case LIFEEXPECTANCY:
        text("Life Expectancy",50,height-350+75);
        text("Low shade",60,height-350+210);
        text("High shade",60,height-350+120);
        fill(color(255,0,0));
        ellipse(45,height-350+205,18,18);
        fill(color(0,0,255));
        ellipse(45,height-350+115,18,18);
        break;
    }

  }

  /*
  * private helper method that changes the map based on selection from menu
  * in case of earthquakes, it changes the coloring logic of the markers only
  * @Param Map enum instance that selects which map is needed
  * */
  private void swapMap(Map map)
  {
    switch(map)
    {
      case MAGNITUDE_EARTHQUAKES:
        this.usedMap = Map.MAGNITUDE_EARTHQUAKES;
        this.map = this.earthQuakesMap;
        break;
      case DEPTH_EARTHQUAKES:
        this.usedMap = Map.DEPTH_EARTHQUAKES;
        this.map = this.earthQuakesMap;
        break;
      case LIFEEXPECTANCY:
        this.usedMap = Map.LIFEEXPECTANCY;
        this.map = this.lifeExpectancyMap;
        break;
    }
  }

  /*
  * private helper method that prompts the user on filtering data before changing the data set
  * by making a custom-made confirmation dialog box,
  * uses the custom made Filter dialog if yes, continues to change dataset if no
  * returns true for GUI API Flexiblity
  */
  private boolean confirmFiltering()
  {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Data Filtering Prompt");
    alert.setHeaderText("Do you want to filter the dataset?");
    ButtonType yesType = new ButtonType("Yes");
    ButtonType noType = new ButtonType("No",ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(yesType,noType);
    Optional<ButtonType> res = alert.showAndWait();
    return res.isPresent() && res.get() == yesType; // true if yes, false if no
  }


  /*
  * URL Version (live), basically for earthquakes but life expectancy is added for extendability
  * private helper method that changes the dataset of earthquakes used by changing the markers
  * of the earthquakes
  * and changes the map selection automatically based on given map enum
  * @Param: url containing live data of RSS Feed for earthquakes
  * @Param: map enum to choose Map gonna be changed to
  * @Param: filter vararg to use in case filters are chosen
  * */
  private void changeData(URL url, Map map, EarthQuakeFilter... filters)
  {
    try {
      switch (map) {
        // same markers, same case
        case MAGNITUDE_EARTHQUAKES:
        case DEPTH_EARTHQUAKES:
          this.earthQuakesMarkers = EarthQuakesMarkerHandler.makeEarthQuakeMarkers(url,filters);
          break;
        case LIFEEXPECTANCY:
          this.lifeExpectancyMarkers = LifeExpectancyMarkerHandler
              .makeLifeExpectancyMarkers(this, url, LIFE_EXPECTANCY_YEAR);
          break;
      }
      swapMap(map);
    }catch(IllegalArgumentException ex)
    {
      showAlert(AlertType.ERROR,"Wrong Input",ex.getMessage());
    }catch(Exception ex)
    {
      showAlert(AlertType.ERROR,"Unexpected error","please contact Hisham Maged\n"+ex.getMessage(),ex);
    }
  }

  /*
   * offline version
   * private helper method that changes the dataset of earthquakes used by changing the markers
   * of the earthquakes
   * @Param: Stage that will be used to open the File Chooser from
   * @Param: map enum to choose Map gonna be changed to
   * @Param: filter vararg to use in case filters are chosen
   * * */
  private void changeData(Map map, EarthQuakeFilter... filters)
  {
    try{
      switch(map)
      {
        // same markers, same case
        case MAGNITUDE_EARTHQUAKES:
        case DEPTH_EARTHQUAKES:
          this.earthQuakesMarkers = EarthQuakesMarkerHandler.makeEarthQuakeMarkers(filters);
          break;
        case LIFEEXPECTANCY:
          this.lifeExpectancyMarkers = LifeExpectancyMarkerHandler.makeLifeExpectancyMarkers(this,"./data/API_SP.DYN.LE00.IN_DS2_en_csv_v2_40967.csv",LIFE_EXPECTANCY_YEAR);
          break;
      }
      swapMap(map);
    }catch(IllegalArgumentException ex)
    {
      showAlert(AlertType.ERROR,"Wrong Input",ex.getMessage());
    }catch(Exception ex)
    {
      showAlert(AlertType.ERROR,"Unexpected Error","please contact Hisham Maged\n"+ex.getMessage(),ex);
    }
  }
  /*
   * offline version filechooser version >> fx
   * private helper method that changes the dataset of earthquakes used by changing the markers
   * of the earthquakes
   * @Param: Stage that will be used to open the File Chooser from
   * @Param: map enum to choose Map gonna be changed to
   * @Param: filter vararg to use in case filters are chosen
   * * */
  private void changeData(Stage stage,Map map, EarthQuakeFilter... filters)
  {
   try{
    switch(map)
    {
      // same markers, same case
      case MAGNITUDE_EARTHQUAKES:
      case DEPTH_EARTHQUAKES:
        this.earthQuakesMarkers = EarthQuakesMarkerHandler.makeEarthQuakeMarkers(stage,filters);
        break;
      case LIFEEXPECTANCY:
        this.lifeExpectancyMarkers = LifeExpectancyMarkerHandler.makeLifeExpectancyMarkers(this,"./data/API_SP.DYN.LE00.IN_DS2_en_csv_v2_40967.csv",LIFE_EXPECTANCY_YEAR);
        break;
    }
    swapMap(map);
  }catch(IllegalArgumentException ex)
  {
    showAlert(AlertType.ERROR,"Wrong Input",ex.getMessage());
  }catch(Exception ex)
  {
    showAlert(AlertType.ERROR,"Unexpected Error","please contact Hisham Maged\n"+ex.getMessage(),ex);
  }
  }

  /*
   * same data version for filtering
   * private helper method that changes the dataset of earthquakes used by changing the markers
   * of the earthquakes
   * @Param: map enum to choose Map gonna be changed to
   * @Param: filter vararg to use in case filters are chosen
   * * */
  private void changeData(List<EarthQuakeEntry> entries,Map map, EarthQuakeFilter... filters)
  {
    try{
      switch(map)
      {
        // same markers, same case
        case MAGNITUDE_EARTHQUAKES:
        case DEPTH_EARTHQUAKES:
          this.earthQuakesMarkers = EarthQuakesMarkerHandler.makeEarthQuakeMarkers(entries,filters);
          break;
        case LIFEEXPECTANCY:
          this.lifeExpectancyMarkers = LifeExpectancyMarkerHandler.makeLifeExpectancyMarkers(this,"./data/API_SP.DYN.LE00.IN_DS2_en_csv_v2_40967.csv",LIFE_EXPECTANCY_YEAR);
          break;
      }
      swapMap(map);
    }catch(IllegalArgumentException ex)
    {
      showAlert(AlertType.ERROR,"Wrong Input",ex.getMessage());
    }catch(Exception ex)
    {
      showAlert(AlertType.ERROR,"Unexpected Error","please contact Hisham Maged\n"+ex.getMessage(),ex);
    }
  }
  /*
  * A private helper method that is used in GUI where the user is prompted a text input to input the year
  * that is going to be used to get the life expectancy value.
  * return boolean whether the year was changed successfully or not, used in GUI
  * */
  private boolean chooseLifeExpectancyYear()
  {
    // 1960 - 2017 as dataset used ranges from 1960 to 2017 only
    TextInputDialog input = new TextInputDialog("Input Life Expectancy year (1960-2017)");
    input.setHeaderText("Enter Life Expectancy Year");
    input.showAndWait();
    String tempStr = "";
    tempStr = input.getEditor().getText();
    int tempVal = 0;
    try {
      if (tempStr == null || tempStr.isEmpty() || (tempVal = Integer.parseInt(tempStr)) > 2017 || tempVal < 1960)
      {
        showAlert(AlertType.ERROR,"Wrong Input","Input must be a year between 1960 - 2017 inclusive!, no change happened");
        return false;
      }
        LIFE_EXPECTANCY_YEAR = tempVal;
      return true;
    }catch(NumberFormatException ex)
    {
      showAlert(AlertType.ERROR,"Wrong Input","Input must be a year between 1960 - 2017 inclusive!, no change happened");
      return false;
    }catch(Exception ex)
    {
      showAlert(AlertType.ERROR,"Unexpected Error","Input must be a year between 1960 - 2017 inclusive!, no change happened",ex);
      return false;
    }
  }
  /*
  * A private helper method made to show alert with a given type and message
  * @Param: Enum for Type of Alert
  * @Param: header of alert
  * @Param: message to be show on alert
  * */
  private void showAlert(AlertType type, String header,String message)
  {
    if(type == null || header == null || message == null)
        return;
    Alert alert = new Alert(type);
    alert.setHeaderText(header);
    alert.setContentText(message);
    alert.show();
  }

  /*
   * Unexpected exception version
   * A private helper method made to show alert with a given type and message
   * @Param: Enum for Type of Alert
   * @Param: Header of alert
   * @Param: message to be show on alert
   * @Param: Exception whose stack trace will be printed
   * */
  private void showAlert(AlertType type, String header,String message,Exception ex)
  {
    if(type == null || header == null || message == null)
      return;
    Alert alert = new Alert(type);
    alert.setHeaderText(header);
    alert.setContentText(message);
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    ex.printStackTrace(pw);
    Label label = new Label("The exception stack-trace was: ");
    TextArea textArea = new TextArea(sw.toString());
    textArea.setEditable(false);
    textArea.setWrapText(true);
    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    GridPane.setVgrow(textArea, Priority.ALWAYS);
    GridPane.setHgrow(textArea, Priority.ALWAYS);
    GridPane expContent = new GridPane();
    expContent.setMaxWidth(Double.MAX_VALUE);
    expContent.add(label,0,0);
    expContent.add(textArea,0,1);
    alert.getDialogPane().setExpandableContent(expContent);
    alert.show();
  }

  /*
  * private helper method that creates Custom-Made Dialog to choose filters and input their values
  * having the ability to only choose one for magnitude and only one for depth
  * returning a Filter array to use in changing data or just filtering (which is the same >> code wise)
  * */
  private EarthQuakeFilter[] getFilterDialog()
  {
    Dialog<Pair<EarthQuakeFilter,EarthQuakeFilter>> dialog = new Dialog<>(); // returning filters
    dialog.setTitle("Data Filter dialog");
    dialog.setHeaderText("Input Filtering values");

    ButtonType filterBtnType = new ButtonType("Filter", ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(filterBtnType,ButtonType.CANCEL);
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20,150,10,10));
    // ====================================== magnitude filters ================================
    TextField exactMagnitude = new TextField(); // input of exact magnitude filter
    exactMagnitude.setPromptText(" Exact value");
    TextField lessMagnitude = new TextField(); // input of lessMagnitude Filter
    lessMagnitude.setPromptText("Upper limit value");
    TextField moreMagnitude = new TextField(); // input of moreMagnitude filter
    moreMagnitude.setPromptText("lower limit value");
    TextField rangeMagnitude = new TextField(); // input of range magnitude filter
    rangeMagnitude.setPromptText("Input range (x,y)");

    // disables all magnitude filters but chosen one
    // by making a radio button for each filter and adding them in a toggle group where only one
    // can be selected and then binding the disable property with the selected property
    // making only the selected radio button filter enabled
    // making the default filter the exact one
    ToggleGroup magToggleGp = new ToggleGroup();
    RadioButton exactMagnitudeR = new RadioButton();
    RadioButton lessMagnitudeR = new RadioButton();
    RadioButton moreMagnitudeR = new RadioButton();
    RadioButton rangeMagnitudeR = new RadioButton();
    exactMagnitudeR.setSelected(true);

    exactMagnitudeR.setToggleGroup(magToggleGp);
    lessMagnitudeR.setToggleGroup(magToggleGp);
    moreMagnitudeR.setToggleGroup(magToggleGp);
    rangeMagnitudeR.setToggleGroup(magToggleGp);

    lessMagnitude.disableProperty().bind(lessMagnitudeR.selectedProperty().not());
    moreMagnitude.disableProperty().bind(moreMagnitudeR.selectedProperty().not());
    exactMagnitude.disableProperty().bind(exactMagnitudeR.selectedProperty().not());
    rangeMagnitude.disableProperty().bind(rangeMagnitudeR.selectedProperty().not());

    // adding radiobutton,label,textfield to grid
    grid.add(exactMagnitudeR,0,0);
    grid.add(new Label("Exact Magnitude Filter:"),1,0);
    grid.add(exactMagnitude,2,0);

    grid.add(lessMagnitudeR,0,1);
    grid.add(new Label("Upperlimit Magnitude Filter:"),1,1);
    grid.add(lessMagnitude,2,1);

    grid.add(moreMagnitudeR,0,2);
    grid.add(new Label("Lowerlimit Magnitude Filter:"),1,2);
    grid.add(moreMagnitude,2,2);

    grid.add(rangeMagnitudeR,0,3);
    grid.add(new Label("Range Magnitude Filter:"),1,3);
    grid.add(rangeMagnitude,2,3);

    // ====================================== depth filters =====================================
    TextField exactDepth = new TextField(); // input of exact depth filter
    exactDepth.setPromptText("Exact value");
    TextField lessDepth = new TextField(); // input of lessDepth Filter
    lessDepth.setPromptText("Upper limit value");
    TextField moreDepth = new TextField(); // input of moreDepth filter
    moreDepth.setPromptText("lower limit value");
    TextField rangeDepth = new TextField(); // input of range Depth filter
    rangeDepth.setPromptText("range values (x,y)");

    // disables all deptth filters but chosen one
    // by making a radio button for each filter and adding them in a toggle group where only one
    // can be selected and then binding the disable property with the selected property
    // making only the selected radio button filter enabled
    // making the default filter the exact one
    ToggleGroup depthToggleGp = new ToggleGroup();
    RadioButton exactDepthR = new RadioButton();
    RadioButton lessDepthR = new RadioButton();
    RadioButton moreDepthR = new RadioButton();
    RadioButton rangeDepthR = new RadioButton();
    exactDepthR.setSelected(true);

    exactDepthR.setToggleGroup(depthToggleGp);
    lessDepthR.setToggleGroup(depthToggleGp);
    moreDepthR.setToggleGroup(depthToggleGp);
    rangeDepthR.setToggleGroup(depthToggleGp);

    lessDepth.disableProperty().bind(lessDepthR.selectedProperty().not());
    moreDepth.disableProperty().bind(moreDepthR.selectedProperty().not());
    exactDepth.disableProperty().bind(exactDepthR.selectedProperty().not());
    rangeDepth.disableProperty().bind(rangeDepthR.selectedProperty().not());


    // adding radiobutton,label,textfield to grid
    grid.add(exactDepthR,3,0);
    grid.add(new Label("Exact Depth Filter:"),4,0);
    grid.add(exactDepth,5,0);

    grid.add(lessDepthR,3,1);
    grid.add(new Label("Upperlimit Depth Filter:"),4,1);
    grid.add(lessDepth,5,1);

    grid.add(moreDepthR,3,2);
    grid.add(new Label("Lowerlimit Depth Filter:"),4,2);
    grid.add(moreDepth,5,2);

    grid.add(rangeDepthR,3,2);
    grid.add(new Label("Range Depth Filter:"),4,3);
    grid.add(rangeDepth,5,3);



    Node filterBtn = dialog.getDialogPane().lookupButton(filterBtnType);

    // disable button if all text fields are empty
    filterBtn.disableProperty().bind(Bindings.isEmpty(lessMagnitude.textProperty())
        .and(Bindings.isEmpty(lessDepth.textProperty()))
        .and(Bindings.isEmpty(moreMagnitude.textProperty()))
        .and(Bindings.isEmpty(moreDepth.textProperty()))
        .and(Bindings.isEmpty(rangeDepth.textProperty()))
        .and(Bindings.isEmpty(rangeMagnitude.textProperty()))
        .and(Bindings.isEmpty(exactMagnitude.textProperty()))
        .and(Bindings.isEmpty(exactDepth.textProperty())));

    dialog.getDialogPane().setContent(grid);

    // converts the inputs into Filter objects
    // only one filter of magnitude can be chosen at a time so only one if condition of magnitude
    // will execute, same for depth, returning a Pair of magnitude, depth filters, mag as key and depth as value
    dialog.setResultConverter(dialogButton ->{
     try {
       if (dialogButton == filterBtnType) {
         EarthQuakeFilter magnitudeFilter = null, depthFilter = null;
         if (exactDepthR.isSelected() && !exactDepth.getText().isEmpty())
           depthFilter = new EarthQuakesFeedParser.ExactDepthFilter(exactDepth.getText().trim());

         if (lessDepthR.isSelected()  && !lessDepth.getText().isEmpty())
           depthFilter = new EarthQuakesFeedParser.DepthLessThanFilter(
               Double.parseDouble(lessDepth.getText().trim()), true);

         if (moreDepthR.isSelected() && !moreDepth.getText().isEmpty())
           depthFilter = new EarthQuakesFeedParser.DepthMoreThanFilter(
               Double.parseDouble(moreDepth.getText().trim()), true);

         if (rangeDepthR.isSelected() && !rangeDepth.getText().isEmpty()) {
           String[] values = rangeDepth.getText().trim().split(",");
           depthFilter = new EarthQuakesFeedParser.DepthRangeFilter(Double.parseDouble(values[0]),
               true, Double.parseDouble(values[1]), true);
         }

         if (exactMagnitudeR.isSelected() && !exactMagnitude.getText().isEmpty())
           magnitudeFilter = new EarthQuakesFeedParser.ExactMagnitudeFilter(
               exactMagnitude.getText().trim());

         if (lessMagnitudeR.isSelected() && !lessMagnitude.getText().isEmpty())
           magnitudeFilter = new EarthQuakesFeedParser.MagnitudeLessThanFilter(
               Double.parseDouble(lessMagnitude.getText().trim()), true);

         if (moreMagnitudeR.isSelected() && !moreMagnitude.getText().isEmpty())
           magnitudeFilter = new EarthQuakesFeedParser.MagnitudeMoreThanFilter(
               Double.parseDouble(moreMagnitude.getText().trim()), true);

         if (rangeMagnitudeR.isSelected() && !rangeMagnitude.getText().isEmpty()) {
           String[] values = rangeMagnitude.getText().trim().split(",");
           magnitudeFilter = new EarthQuakesFeedParser.MagnitudeRangeFilter(
               Double.parseDouble(values[0]), true, Double.parseDouble(values[1]), true);
         }

         return new Pair<>(magnitudeFilter,
             depthFilter); // used Filters and done this way because only one magnitude filter can be chosen and only one depth filter can be chosen due to toggle group
       }

     }catch(NumberFormatException ex)
     {
       showAlert(AlertType.ERROR,"Invalid Input, nothing done!",ex.getMessage());
     }catch(IllegalArgumentException ex)
     {
       showAlert(AlertType.ERROR,"Invalid input, Numeric input only",ex.getMessage());
     }
     catch(ArrayIndexOutOfBoundsException ex)
     {
       showAlert(AlertType.ERROR,"Invalid input ","Invalid input format (numeric input only, and if range > ex: 2.5,5.0)");
     }
     catch(Exception ex)
     {
       showAlert(AlertType.ERROR,"Unexpected Error","Please Contact Hisham Maged",ex);
     }
    return null;
    });
    // return the result in form of optional
    Optional<Pair<EarthQuakeFilter,EarthQuakeFilter>> result = dialog.showAndWait();

    // if optional is present then return a new array of two elements, first of magnitude filter, second of depth filter
    return result.isPresent() ?
        new EarthQuakeFilter[]{result.get().getKey(),result.get().getValue()} :
        null;
  }

  /*
  * A private helper method to do filterOption in operations Map
  * */
  private void filterOperation()
  {
    if(!isEarthQuakeSelected())
    {
      showAlert(AlertType.ERROR,"Un supported operation","You can't filter life expectancy data with magnitude, depth, choose EarthQuake Map first");
      return;
    }
    // no conditional expression on 2nd parameter as made sure from if condition that it's earthquake map
    changeData(EarthQuakesMarkerHandler.lastMadeEntries,this.usedMap,getFilterDialog());

  }

  /*
  * A private boolean helper method that returns true if an earthquake map is selected, used for GUI Interactions
  * */
  private boolean isEarthQuakeSelected()
  {
    return this.usedMap == Map.DEPTH_EARTHQUAKES || this.usedMap == Map.MAGNITUDE_EARTHQUAKES;
  }

  /*
  * Key pressed functionality that changes between the earthquake map and life expectancy map
  * using final char MAGNITUDE_EARTHQUAKES_KEY for magnitude earthquake data
  * using final char DEPTH_EARTHQUAKES_KEY for depth earthquake data
  * using final char LIFE_EXPECTANCY_KEY for life expectancy data
  * zooms in map and out of it using ZOOM_IN_KEY and ZOOM_OUT_KEY at the mouse location
  * */
  public void keyPressed()
  {
    switch(key)
    {
      case MAGNITUDE_EARTHQUAKES_KEY:
        this.map = this.earthQuakesMap;
        this.usedMap = Map.MAGNITUDE_EARTHQUAKES;
        break;

      case DEPTH_EARTHQUAKES_KEY:
        this.usedMap = Map.DEPTH_EARTHQUAKES;
        this.map = this.earthQuakesMap;
        break;

      case LIFE_EXPECTANCY_KEY:
        this.usedMap = Map.LIFEEXPECTANCY;
        this.map = this.lifeExpectancyMap;
        break;

      case ZOOM_IN_KEY:
        this.map.zoomAndPanTo(this.map.getZoomLevel()+1,map.getLocation(mouseX,mouseY));
        break;
      case ZOOM_OUT_KEY:
        this.map.zoomAndPanTo(this.map.getZoomLevel()-1,map.getLocation(mouseX,mouseY));
        break;
    }
  }


  /*
  * made to write the latitude, longitude of place of mouse
  * */
  private void writeLocation()
  {
    Location mouseLoc = this.map.getLocation(mouseX,mouseY);
    fill(0);
    text("Latitude: "+mouseLoc.getLat()+", Longitude: "+mouseLoc.getLon(),mouseX,mouseY);
  }
  /*
   * used to make final URL objects as with static initializers it's complicated and it always throws
   * a MalformedURLException that can't be propagated to a private static method solves this by making
   * a URL object with given string or null if exception so solved.
   * */
  private static URL makeURL(String urlString)
  {
    try{
      return new URL(urlString);
    }catch(Exception ex)
    {
      return null;
    }
  }

  /*
  * Applies the panning restriction pased on zoom level if zoom increased than level 3
  * */
  private void setRestriction()
  {
    if(this.map.getZoomLevel()>3)
    this.map.setPanningRestriction(this.map.getCenter(),(2*width)/2F);
  }






}
