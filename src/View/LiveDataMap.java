package view;
/*
  Author: Hisham Maged
  Date : 7/11/2019
  Project Name : A Map with visualized live data
*/

import controller.DataUtils;
import controller.EarthQuakeUtils;
import controller.EarthQuakeUtils.EarthQuakeFilter;
import controller.WorldDataUtils;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import model.marker.AbstractEarthQuakeMarker;
import model.marker.CityMarker;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PSurface;
import processing.javafx.PSurfaceFX;

public class LiveDataMap extends PApplet {

  /* private ENUM made to choose a map (in case of earthquakes, to choose a coloring sequence)*/
  private enum Map {
    EARTHQUAKES, LIFEEXPECTANCY;
  }

  private enum LiveDatachoice {
    LAST_DAY, LAST_HOUR, LAST_WEEK, LAST_MONTH;
  }
  //TODO: SOLVE the problem of filtering doesn't change markers as markers are added to city and the previous ones aren't removed

  // Used Map reference
  private UnfoldingMap map;
  // Earth quakes map (same for any earthquake property, as same data but different coloring)
  private UnfoldingMap earthQuakesMap;
  // used earthQuakesMarkers reference (same for any earthquake property, as same data but different coloring)
  private List<Marker> earthQuakesMarkers;
  // used lifeExpectancyMarkers reference
  private List<Marker> lifeExpectancyMarkers;
  // used City markers reference
  private List<Marker> cityMarkers;
  //LifeExpectancy Map reference
  private UnfoldingMap lifeExpectancyMap;

  // private List<ScreenPosition> holding the markers of earthquake magnitudes to be customized in draw
  private List<ScreenPosition> magnitudeScreenPositions;


  private PImage cityMarkerImg;

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
  private Map usedMap = Map.EARTHQUAKES;

  // default map selection in case of changing data set and another Map rather than earthquakes is chosen, used in GUI
  private static final Map DEFAULT_EARTHQUAKE_MAP_CHOICE = Map.EARTHQUAKES;


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
  public void settings() {
    size(displayWidth, displayHeight - 100, FX2D); // -100 so not to fill the whole screen
    smooth(8); // anti-allising x8 (remove if program is slow)
    WorldDataUtils.useApplet(
        this); // essential for the whole WorldDataUtils to work, if not done will throw unsupportedOperation exception on all operations
    this.cityMarkerImg = loadImage("data/icon.png");
    this.cityMarkerImg.resize(14, 0);
    initMap();
  }

  // method that will embedd the PApplet processing into JavaFX Application
  @Override
  public PSurface initSurface() {
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
  private PSurface makeFXSurface() {
    PSurface surface = super.initSurface(); // makes the surface that processing uses to sketch on
    PSurfaceFX fxSurface = (PSurfaceFX) surface; // since we're rendering using FX2D, then it uses a Canvas to sketch on wrapped on what's called a native
    Canvas canvas = (Canvas) fxSurface
        .getNative(); // getting the native which is the canvas since FX2d is used
    Stage stage = (Stage) canvas.getScene()
        .getWindow(); // getting the scene of the canvas then getting the stage from it
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
    earthQuakeMap.getItems().addAll(magnitudeEarthQuakes);
    MenuItem lifeExpectancyMap = new MenuItem("Life Expectancy"); // lifeExpectancyMap

    magnitudeEarthQuakes.setOnAction(e -> swapMap(Map.EARTHQUAKES));
    lifeExpectancyMap.setOnAction(e -> swapMap(Map.LIFEEXPECTANCY));

    mapSelectionMenu.getItems()
        .addAll(earthQuakeMap, lifeExpectancyMap); // adding map items to map menu

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
    lastHour.setOnAction(e -> {
      if (confirmFiltering())
        changeData(LiveDatachoice.LAST_HOUR,
            this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE : this.usedMap,
            getFilterDialog()); // for added filters functionality
      else
        changeData(LiveDatachoice.LAST_HOUR,
            this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE : this.usedMap);
    });

    lastDay.setOnAction(e -> {
      if (confirmFiltering())
        changeData(LiveDatachoice.LAST_DAY,
            this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE : this.usedMap,
            getFilterDialog()); // for added filters functionality
      else
        changeData(LiveDatachoice.LAST_DAY,
            this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE : this.usedMap);
    });

    lastWeek.setOnAction(e -> {
      if (confirmFiltering())
        changeData(LiveDatachoice.LAST_WEEK,
            this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE : this.usedMap,
            getFilterDialog()); // for added filters functionality
      else
        changeData(LiveDatachoice.LAST_WEEK,
            this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE : this.usedMap);
    });

    lastMonth.setOnAction(e -> {
      if (confirmFiltering())
        changeData(LiveDatachoice.LAST_MONTH,
            this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE : this.usedMap,
            getFilterDialog()); // for added filters functionality
      else
        changeData(LiveDatachoice.LAST_MONTH,
            this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE : this.usedMap);
    });

    // given a Stage because FileChooser of fx needs a stage to be opened from.
    local.setOnAction(e -> {
      if (confirmFiltering())
        changeData(stage,
            this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE : this.usedMap,
            getFilterDialog()); // for added filters functionality
      else
        changeData(stage,
            this.usedMap == Map.LIFEEXPECTANCY ? DEFAULT_EARTHQUAKE_MAP_CHOICE : this.usedMap);
    });

    earthQuakeData.getItems().addAll(lastHour, lastDay, lastWeek, lastMonth, local);

    // ===================== LifeExpectancy Data Section ====================
    // the user is prompted to input the year that the value of life expectancy is set on, default is LIFE_EXPECTANCY_YEAR
    Menu lifeExpectancyData = new Menu("Life Expectancy");
    MenuItem localLife = new MenuItem("Change Year (default: 2017)");

    localLife.setOnAction(e -> {
      if (chooseLifeExpectancyYear())
        changeData(Map.LIFEEXPECTANCY);
    });

    lifeExpectancyData.getItems().add(localLife);

    // ====================== Data Menu Section ========================
    Menu dataSelectionMenu = new Menu("Data Selection");
    dataSelectionMenu.getItems().addAll(earthQuakeData, lifeExpectancyData);

    //======================= Provider Menu Section ====================
    Menu providerMenu = new Menu("Provider Selection");
    MenuItem roadProvider = new MenuItem("Road Map");
    MenuItem aerialProvider = new MenuItem("Aerial Map");
    MenuItem hybridProvider = new MenuItem("Hybrid Map");

    roadProvider.setOnAction(e -> this.map.mapDisplay.setProvider(ROAD_PROVIDER));
    aerialProvider.setOnAction(e -> this.map.mapDisplay.setProvider(AERIAL_PROVIDER));
    hybridProvider.setOnAction(e -> this.map.mapDisplay.setProvider(HYBRID_PROVIDER));

    providerMenu.getItems().addAll(roadProvider, aerialProvider, hybridProvider);
    // ====================== Menu bar section ========================
    MenuBar menuBar = new MenuBar(); // makes a menu bar
    menuBar.getMenus().addAll(operationsMenu, mapSelectionMenu, dataSelectionMenu,
        providerMenu); // adds Operation Menu and MapSelection menu

    // ====================== VBox Container ==========================
    VBox container = new VBox(); // main container of FX Application
    container.getChildren().addAll(menuBar, canvas);

    // ====================== Scene Section ===========================
    Scene fxScene = new Scene(
        container); // new scene containing the menu bar and canvas in vbox container

    // ====================== Stage Section ===========================
    Platform.runLater(
        () -> stage.setScene(fxScene)); // replaces the scene of the stage with our own made one
    // lambda expression as the parameter is Runnable so annonymous method using annonymous object of annonymous subclass
    // as it's a SAM Interface (Funcional) and overrides the method run, which takes no parameters hence the ()

    return surface;
  }


  // the draw loop, invoked due to any change or loop (determined in backend of Processing library)
  @Override
  public void draw() {
    background(30);
    this.map.draw();
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
  private void initMap() {

    // =========================================== EarthQuakes Section ====================================
    this.earthQuakesMap = new UnfoldingMap(this, 0, 0, 1920, 1080, DEFAULT_PROVIDER);
    MapUtils.createDefaultEventDispatcher(this, this.earthQuakesMap);
    /*
     * if you want to filter the earthquake data, use respected EarthQuakesFilter objects for more info about them
     * check the EarthQuakeFeedParser static classes
     * change the 1st parameter in makeEarthQuakeMarkers
     * no 1st parameter >> FileChooser to choose ATOM file
     * String >> filePath
     * File >> Atom file object
     * URL >> live data url
     * */
    this.earthQuakesMarkers = DataUtils.makeLocalEarthQuakesMarkers("./data/2.5_week.atom");
    this.cityMarkers = DataUtils.makeCityMarkers();

    this.earthQuakesMap.addMarkers(this.earthQuakesMarkers);
    this.earthQuakesMap.addMarkers(this.cityMarkers);
    this.earthQuakesMap.zoomToLevel(3);
    this.earthQuakesMap.setZoomRange(3, 20);
    this.earthQuakesMap.setPanningRestriction(this.earthQuakesMap.getCenter(),
        width / 2f); //makes map centered and doesn't go out of range

    // ===========================================  Life Expectancy Section ====================================

    this.lifeExpectancyMap = new UnfoldingMap(this, 0, 0, 1920, 1080, DEFAULT_PROVIDER);
    MapUtils.createDefaultEventDispatcher(this, this.lifeExpectancyMap);
    /*
     * change first parameter in makeLifeExpectancyMarkers
     * no 1st parameter >> FileChooser to choose CSV file
     * String >> filePath
     * File >> CSV file object
     * URL >> live data url
     * */
    this.lifeExpectancyMarkers = DataUtils
        .makeLocalLifeExpectancyMarkers("./data/API_SP.DYN.LE00.IN_DS2_en_csv_v2_40967.csv",
            LIFE_EXPECTANCY_YEAR);
    this.lifeExpectancyMap.addMarkers(this.lifeExpectancyMarkers);
    this.lifeExpectancyMap.zoomToLevel(3);
    this.lifeExpectancyMap.setZoomRange(3, 20);
    this.lifeExpectancyMap.setPanningRestriction(this.lifeExpectancyMap.getCenter(),
        width / 2f); //makes map centered and doesn't go out of range

    this.map = this.earthQuakesMap;

    /* made for testing purposes only, don't uncomment
    System.out.println("Testing earthquake Marker data");
    this.earthQuakesMarkers.forEach( m -> System.out.println("location: "+m.getLocation().getLat()+", "+m.getLocation().getLon()+"\n properties: "+m.getProperties()));

    System.out.println();
    System.out.println();

    System.out.println("Testing Life Expectancy Marker data");
    this.lifeExpectancyMarkers.forEach( m -> System.out.println(" properties: "+m.getProperties()));

    System.out.println();
    System.out.println();

    System.out.println("Testing City Marker data");
    this.cityMarkers.forEach( m -> System.out.println("location: "+m.getLocation().getLat()+", "+m.getLocation().getLon()+"\n properties: "+m.getProperties()));

    System.out.println("terminating");
    System.exit(0);
    */

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
  private void addLegend() {
    fill(color(230));
    strokeWeight(3);
    stroke(0);
    rect(30, height - 350, 150, 300);
    textSize(13);
    fill(color(0));
    textAlign(LEFT, CENTER);
    switch (this.usedMap) {
      case EARTHQUAKES:
        text("Earthquakes", 65, height - 400 + 65);

        image(this.cityMarkerImg, 40, height - 400 + 98);
        text(" City Marker", 60, height - 400 + 100);
        text("Land-Quake", 60, height - 400 + 125);
        text("Ocean-Quake", 60, height - 400 + 150);
        strokeWeight(1);
        fill(255, 255, 255);
        stroke(0);
        ellipse(45, height - 400 + 130, 14, 14);
        fill(255, 255, 255);
        rect(38, height - 400 + 145, 14, 14);
        fill(0);
        text("Size ~ Magnitude", 50, height - 400 + 175);
        text(" Shallow", 60, height - 400 + 205);
        text("Intermediate", 60, height - 400 + 230);
        text("Deep", 60, height - 400 + 255);
        text("Past Day ~ Animated", 45, height - 400 + 290);
        strokeWeight(1);
        fill(6, 175, 194, 100);
        stroke(0);
        ellipse(45, height - 400 + 210, 13, 13);
        strokeWeight(1);
        fill(251, 255, 0, 190);
        stroke(0);
        ellipse(45, height - 400 + 235, 13, 13);
        strokeWeight(1);
        fill(191, 34, 40, 150);
        stroke(0);
        ellipse(45, height - 400 + 260, 13, 13);

        break;
      case LIFEEXPECTANCY:
        text("Life Expectancy", 50, height - 350 + 75);
        text("Low shade", 60, height - 350 + 210);
        text("High shade", 60, height - 350 + 120);
        fill(color(255, 0, 0));
        ellipse(45, height - 350 + 205, 18, 18);
        fill(color(0, 0, 255));
        ellipse(45, height - 350 + 115, 18, 18);
        break;
    }

  }

  /*
   * private helper method that changes the map based on selection from menu
   * in case of earthquakes, it changes the coloring logic of the markers only
   * @Param Map enum instance that selects which map is needed
   * */
  private void swapMap(Map map) {
    switch (map) {
      case EARTHQUAKES:
        this.usedMap = Map.EARTHQUAKES;
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
  private boolean confirmFiltering() {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Data Filtering Prompt");
    alert.setHeaderText("Do you want to filter the dataset?");
    ButtonType yesType = new ButtonType("Yes");
    ButtonType noType = new ButtonType("No", ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(yesType, noType);
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
  private void changeData(LiveDatachoice choice, Map map, EarthQuakeFilter... filters) {
    try {
      switch (map) {
        case EARTHQUAKES:
          switch (choice) {
            case LAST_DAY:
              this.earthQuakesMarkers = DataUtils.makeLastDayEarthQuakesMarkers(filters);
              break;
            case LAST_HOUR:
              this.earthQuakesMarkers = DataUtils.makeLastHourEarthQuakesMarkers(filters);
              break;
            case LAST_MONTH:
              this.earthQuakesMarkers = DataUtils.makeLastMonthEarthQuakesMarkers(filters);
              break;
            case LAST_WEEK:
              this.earthQuakesMarkers = DataUtils.makeLastWeekEarthQuakesMarkers(filters);
              break;
          }
          ;
          break;
        case LIFEEXPECTANCY:
//          this.lifeExpectancyMarkers = DataUtils //TODO: figure it out
//              .makeLiveLifeExpectancyMarkers(url, LIFE_EXPECTANCY_YEAR);
          break;
      }
      swapMap(map);
    } catch (IllegalArgumentException ex) {
      showAlert(AlertType.ERROR, "Wrong Input", ex.getMessage());
    } catch (Exception ex) {
      showAlert(AlertType.ERROR, "Unexpected error",
          "please contact Hisham Maged\n" + ex.getMessage(), ex);
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
  private void changeData(Map map, EarthQuakeFilter... filters) {
    try {
      switch (map) {
        // same markers, same case
        case EARTHQUAKES:
          this.earthQuakesMarkers = DataUtils.makeLocalEarthQuakesMarkers(filters);
          break;
        case LIFEEXPECTANCY:
          this.lifeExpectancyMarkers = DataUtils
              .makeLocalLifeExpectancyMarkers("./data/API_SP.DYN.LE00.IN_DS2_en_csv_v2_40967.csv",
                  LIFE_EXPECTANCY_YEAR);
          break;
      }
      swapMap(map);
    } catch (IllegalArgumentException ex) {
      showAlert(AlertType.ERROR, "Wrong Input", ex.getMessage());
    } catch (Exception ex) {
      showAlert(AlertType.ERROR, "Unexpected Error",
          "please contact Hisham Maged\n" + ex.getMessage(), ex);
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
  private void changeData(Stage stage, Map map, EarthQuakeFilter... filters) {
    try {
      switch (map) {
        // same markers, same case
        case EARTHQUAKES:
          this.earthQuakesMarkers = DataUtils.makeLocalEarthQuakesMarkers(stage, filters);
          break;
        case LIFEEXPECTANCY:
          this.lifeExpectancyMarkers = DataUtils
              .makeLocalLifeExpectancyMarkers("./data/API_SP.DYN.LE00.IN_DS2_en_csv_v2_40967.csv",
                  LIFE_EXPECTANCY_YEAR);
          break;
      }
      swapMap(map);
    } catch (IllegalArgumentException ex) {
      showAlert(AlertType.ERROR, "Wrong Input", ex.getMessage());
    } catch (Exception ex) {
      showAlert(AlertType.ERROR, "Unexpected Error",
          "please contact Hisham Maged\n" + ex.getMessage(), ex);
    }
  }

  /*
   * same data version for filtering
   * private helper method that changes the dataset of earthquakes used by changing the markers
   * of the earthquakes
   * @Param: map enum to choose Map gonna be changed to
   * @Param: filter vararg to use in case filters are chosen
   * * */
  private void changeData(List<Marker> markers, Map map, EarthQuakeFilter... filters) {
    try {
      switch (map) {
        // same markers, same case
        case EARTHQUAKES:
          this.earthQuakesMarkers = markers;
          break;
        case LIFEEXPECTANCY:
          this.lifeExpectancyMarkers = DataUtils
              .makeLocalLifeExpectancyMarkers("./data/API_SP.DYN.LE00.IN_DS2_en_csv_v2_40967.csv",
                  LIFE_EXPECTANCY_YEAR);
          break;
      }
      swapMap(map);
    } catch (IllegalArgumentException ex) {
      showAlert(AlertType.ERROR, "Wrong Input", ex.getMessage());
    } catch (Exception ex) {
      showAlert(AlertType.ERROR, "Unexpected Error",
          "please contact Hisham Maged\n" + ex.getMessage(), ex);
    }
  }

  /*
   * A private helper method that is used in GUI where the user is prompted a text input to input the year
   * that is going to be used to get the life expectancy value.
   * return boolean whether the year was changed successfully or not, used in GUI
   * */
  private boolean chooseLifeExpectancyYear() {
    // 1960 - 2017 as dataset used ranges from 1960 to 2017 only
    TextInputDialog input = new TextInputDialog("Input Life Expectancy year (1960-2017)");
    input.setHeaderText("Enter Life Expectancy Year");
    input.showAndWait();
    String tempStr = "";
    tempStr = input.getEditor().getText();
    int tempVal = 0;
    try {
      if (tempStr == null || tempStr.isEmpty() || (tempVal = Integer.parseInt(tempStr)) > 2017
          || tempVal < 1960) {
        showAlert(AlertType.ERROR, "Wrong Input",
            "Input must be a year between 1960 - 2017 inclusive!, no change happened");
        return false;
      }
      LIFE_EXPECTANCY_YEAR = tempVal;
      return true;
    } catch (NumberFormatException ex) {
      showAlert(AlertType.ERROR, "Wrong Input",
          "Input must be a year between 1960 - 2017 inclusive!, no change happened");
      return false;
    } catch (Exception ex) {
      showAlert(AlertType.ERROR, "Unexpected Error",
          "Input must be a year between 1960 - 2017 inclusive!, no change happened", ex);
      return false;
    }
  }

  /*
   * A private helper method made to show alert with a given type and message
   * @Param: Enum for Type of Alert
   * @Param: header of alert
   * @Param: message to be show on alert
   * */
  private void showAlert(AlertType type, String header, String message) {
    if (type == null || header == null || message == null)
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
  private void showAlert(AlertType type, String header, String message, Exception ex) {
    if (type == null || header == null || message == null)
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
    expContent.add(label, 0, 0);
    expContent.add(textArea, 0, 1);
    alert.getDialogPane().setExpandableContent(expContent);
    alert.show();
  }

  /*
   * private helper method that creates Custom-Made Dialog to choose filters and input their values
   * having the ability to only choose one for magnitude and only one for depth
   * returning a Filter array to use in changing data or just filtering (which is the same >> code wise)
   * */
  private EarthQuakeFilter[] getFilterDialog() {
    Dialog<Pair<EarthQuakeFilter, EarthQuakeFilter>> dialog = new Dialog<>(); // returning filters
    dialog.setTitle("Data Filter dialog");
    dialog.setHeaderText("Input Filtering values");

    ButtonType filterBtnType = new ButtonType("Filter", ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(filterBtnType, ButtonType.CANCEL);
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));
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
    grid.add(exactMagnitudeR, 0, 0);
    grid.add(new Label("Exact Magnitude Filter:"), 1, 0);
    grid.add(exactMagnitude, 2, 0);

    grid.add(lessMagnitudeR, 0, 1);
    grid.add(new Label("Upperlimit Magnitude Filter:"), 1, 1);
    grid.add(lessMagnitude, 2, 1);

    grid.add(moreMagnitudeR, 0, 2);
    grid.add(new Label("Lowerlimit Magnitude Filter:"), 1, 2);
    grid.add(moreMagnitude, 2, 2);

    grid.add(rangeMagnitudeR, 0, 3);
    grid.add(new Label("Range Magnitude Filter:"), 1, 3);
    grid.add(rangeMagnitude, 2, 3);

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
    grid.add(exactDepthR, 3, 0);
    grid.add(new Label("Exact Depth Filter:"), 4, 0);
    grid.add(exactDepth, 5, 0);

    grid.add(lessDepthR, 3, 1);
    grid.add(new Label("Upperlimit Depth Filter:"), 4, 1);
    grid.add(lessDepth, 5, 1);

    grid.add(moreDepthR, 3, 2);
    grid.add(new Label("Lowerlimit Depth Filter:"), 4, 2);
    grid.add(moreDepth, 5, 2);

    grid.add(rangeDepthR, 3, 2);
    grid.add(new Label("Range Depth Filter:"), 4, 3);
    grid.add(rangeDepth, 5, 3);

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
    dialog.setResultConverter(dialogButton -> {
      try {
        if (dialogButton == filterBtnType) {
          EarthQuakeFilter magnitudeFilter = null, depthFilter = null;
          if (exactDepthR.isSelected() && !exactDepth.getText().isEmpty())
            depthFilter = new EarthQuakeUtils.ExactDepthFilter(exactDepth.getText().trim());

          if (lessDepthR.isSelected() && !lessDepth.getText().isEmpty())
            depthFilter = new EarthQuakeUtils.DepthLessThanFilter(
                Double.parseDouble(lessDepth.getText().trim()), true);

          if (moreDepthR.isSelected() && !moreDepth.getText().isEmpty())
            depthFilter = new EarthQuakeUtils.DepthMoreThanFilter(
                Double.parseDouble(moreDepth.getText().trim()), true);

          if (rangeDepthR.isSelected() && !rangeDepth.getText().isEmpty()) {
            String[] values = rangeDepth.getText().trim().split(",");
            depthFilter = new EarthQuakeUtils.DepthRangeFilter(Double.parseDouble(values[0]),
                true, Double.parseDouble(values[1]), true);
          }

          if (exactMagnitudeR.isSelected() && !exactMagnitude.getText().isEmpty())
            magnitudeFilter = new EarthQuakeUtils.ExactMagnitudeFilter(
                exactMagnitude.getText().trim());

          if (lessMagnitudeR.isSelected() && !lessMagnitude.getText().isEmpty())
            magnitudeFilter = new EarthQuakeUtils.MagnitudeLessThanFilter(
                Double.parseDouble(lessMagnitude.getText().trim()), true);

          if (moreMagnitudeR.isSelected() && !moreMagnitude.getText().isEmpty())
            magnitudeFilter = new EarthQuakeUtils.MagnitudeMoreThanFilter(
                Double.parseDouble(moreMagnitude.getText().trim()), true);

          if (rangeMagnitudeR.isSelected() && !rangeMagnitude.getText().isEmpty()) {
            String[] values = rangeMagnitude.getText().trim().split(",");
            magnitudeFilter = new EarthQuakeUtils.MagnitudeRangeFilter(
                Double.parseDouble(values[0]), true, Double.parseDouble(values[1]), true);
          }

          return new Pair<>(magnitudeFilter,
              depthFilter); // used Filters and done this way because only one magnitude filter can be chosen and only one depth filter can be chosen due to toggle group
        }

      } catch (NumberFormatException ex) {
        showAlert(AlertType.ERROR, "Invalid Input, nothing done!", ex.getMessage());
      } catch (IllegalArgumentException ex) {
        showAlert(AlertType.ERROR, "Invalid input, Numeric input only", ex.getMessage());
      } catch (ArrayIndexOutOfBoundsException ex) {
        showAlert(AlertType.ERROR, "Invalid input ",
            "Invalid input format (numeric input only, and if range > ex: 2.5,5.0)");
      } catch (Exception ex) {
        showAlert(AlertType.ERROR, "Unexpected Error", "Please Contact Hisham Maged", ex);
      }
      return null;
    });
    // return the result in form of optional
    Optional<Pair<EarthQuakeFilter, EarthQuakeFilter>> result = dialog.showAndWait();

    // if optional is present then return a new array of two elements, first of magnitude filter, second of depth filter
    return result.isPresent() ?
        new EarthQuakeFilter[]{result.get().getKey(), result.get().getValue()} :
        null;
  }

  /*
   * A private helper method to do filterOption in operations Map
   * */
  private void filterOperation() {
    if (!isEarthQuakeSelected()) {
      showAlert(AlertType.ERROR, "Un supported operation",
          "You can't filter life expectancy data with magnitude, depth, choose EarthQuake Map first");
      return;
    }
    // no conditional expression on 2nd parameter as made sure from if condition that it's earthquake map
    changeData(DataUtils.lastMadeEarthQuakesMarkers, this.usedMap, getFilterDialog());

  }

  /*
   * A private boolean helper method that returns true if an earthquake map is selected, used for GUI Interactions
   * */
  private boolean isEarthQuakeSelected() {
    return this.usedMap == Map.EARTHQUAKES;
  }

  /*
   * Key pressed functionality that changes between the earthquake map and life expectancy map
   * using final char MAGNITUDE_EARTHQUAKES_KEY for magnitude earthquake data
   * using final char DEPTH_EARTHQUAKES_KEY for depth earthquake data
   * using final char LIFE_EXPECTANCY_KEY for life expectancy data
   * zooms in map and out of it using ZOOM_IN_KEY and ZOOM_OUT_KEY at the mouse location
   * */
  public void keyPressed() {
    switch (key) {
      case MAGNITUDE_EARTHQUAKES_KEY:
        this.map = this.earthQuakesMap;
        this.usedMap = Map.EARTHQUAKES;
        break;

      case LIFE_EXPECTANCY_KEY:
        this.usedMap = Map.LIFEEXPECTANCY;
        this.map = this.lifeExpectancyMap;
        break;

      case ZOOM_IN_KEY:
        this.map.zoomAndPanTo(this.map.getZoomLevel() + 1, map.getLocation(mouseX, mouseY));
        break;
      case ZOOM_OUT_KEY:
        this.map.zoomAndPanTo(this.map.getZoomLevel() - 1, map.getLocation(mouseX, mouseY));
        break;
    }
  }

  /*
   * a method that is called automatically on mouse movement because
   * PApplet implements the mouseListener so it listens for any mouse events
   * and call the currosponding mouseEvent operation when it happens automatically
   * set the selection of the marker the cursur is on to be true
   * and that state is used in the drawing of the marker where it outputs text on top of it
   * if selected
   * then if mouse moved again after wards it keeps reference of the last selected marker
   * and sets its selection false then to null for the if condition to work only
   * when selectMarkerHover sets the hoveredOnMarker private field to be not null
   * after it selects it
   * two invokation of selectMarkerHover because it could be an earthquake or a city marker
   * but only one of them will be true
   * */
  private Marker hoveredOnMarker = null;

  @Override
  public void mouseMoved() {
    if (this.usedMap == Map.EARTHQUAKES) {
      if (hoveredOnMarker != null) {
        hoveredOnMarker.setSelected(false);
        hoveredOnMarker = null;
      }

      selectMarkerHover(this.earthQuakesMarkers);
      selectMarkerHover(this.cityMarkers);
    }
  }


  /*
   * is called automatically when mouse is released after a click on some location in map
   * hides all the markers except the one clicked on or the ones in the epicenter of an earthquake
   * if an earthquake marker is clicked on, then all markers will be hidden except the cities
   * in the threat circle of the earthquake
   * if a city is clicked on. all markers will disappear except the earthquakes that the city is
   * in the threat circle of
   * uses the hoveredOnMarker field to check if it's null, it unhides all markers using
   * a private helper method makeMarkersVisible, and if it's not null then it hides all
   * except the cities in threat circle
   * */
  @Override
  public void mouseReleased() {
    if (this.usedMap == Map.EARTHQUAKES) {
      if (hoveredOnMarker == null) { // if null then cursor is not inside marker
        this.makeMarkersVisible();
      } else { // if it's not null then cursor is inside the marker
        if (hoveredOnMarker instanceof AbstractEarthQuakeMarker) {
          // a private helper that shows only the selected earthquake marker and cities that
          // are in the threat circle of the earthquake ( if there's one )
          ((AbstractEarthQuakeMarker) hoveredOnMarker).setClicked(true);
          doMouseReleaseEarthQuakeMarker();
        } else if (hoveredOnMarker.getClass() == CityMarker.class) {
          doMouseReleaseCityMarker();
        }
      }
    }
  }


  /*
   * made to write the latitude, longitude of place of mouse
   * */
  private void writeLocation() {
    Location mouseLoc = this.map.getLocation(mouseX, mouseY);
    fill(0);
    text("Latitude: " + mouseLoc.getLat() + ", Longitude: " + mouseLoc.getLon(), mouseX, mouseY);
  }

  /*
   * Applies the panning restriction pased on zoom level if zoom increased than level 3
   * */
  private void setRestriction() {
    if (this.map.getZoomLevel() > 3)
      this.map.setPanningRestriction(this.map.getCenter(), (2 * width) / 2F);
  }


  /*
   * private helper method that is used in mouse Moved and it's essential
   * for selecting only one marker on hovering when there's multiple markers on screen
   * it selects the first marker found to show text of
   * by looping on all markers, finding whether the mouseX, mouseY position is inside the location
   * of the marker
   * after it being marked that it's selected, the loop breaks and method returns
   * @Param:List<Marker> markers to check if any marker is under the mouse cursor
   * */
  private void selectMarkerHover(List<Marker> markers) {
    for (Marker m : markers) {
      if (m.isInside(this.map, mouseX, mouseY) && hoveredOnMarker == null) // making sure nothing is selected as it will be null if nothing is selected
      {
        hoveredOnMarker = m;
        hoveredOnMarker.setSelected(true);
        return;
      }
    }
  }

  /*
   * A private helper method that set all markers to be visible in case of earthquakes map
   * shows all city markers and all earthquakes markers
   * */
  private void makeMarkersVisible() {
    if (this.usedMap == Map.EARTHQUAKES) {
      this.earthQuakesMarkers.forEach(m ->{ m.setHidden(false);
        if(m instanceof AbstractEarthQuakeMarker) {
          ((AbstractEarthQuakeMarker) m)
              .clearCities(); // clears all cities Lists when pressed anywhere but marker
          ((AbstractEarthQuakeMarker) m).setClicked(false); // set all selected earthquakes markers to be not selected
        }
      });
      this.cityMarkers.forEach(m -> m.setHidden(false));

    }
  }

  /*
   * A private helper method that shows only the selected earthquake marker from mouseReleased
   * method along with the city markers that exist in the threat circle of the earthquake if there's one
   * uses the private field hoveredOnMarker as it holds the marker that the cursor is on and clicked on
   * happens only during use of earthquakes map, a check is made in mouseReleased
   * */
  private void doMouseReleaseEarthQuakeMarker() {
    // just a check so no null pointer exception can happen un expectantly
    if (hoveredOnMarker != null) {
      // hides all earthquakes markers except the one clicked on by using a referential check with
      // the hoveredOnMarker cuz it holds the marker hovered on
      this.earthQuakesMarkers.forEach(m -> {
        if (m != hoveredOnMarker)
          m.setHidden(true);
      });
      // hides all cities that are not in the threat circle of the earthquake
      // by getting the distance from the hoveredOnMarker and the city's location
      // and checking if it's less than the threat circle's radius or equal
      // check is made by Double.compare for exact match (slightly not correct)
      // if needed more exactness, use BigDecimals
      // since it's made sure in mouseReleased that it's an earthquake marker, then it's a safe cast
      this.cityMarkers.forEach(
          m -> {
            if (Double.compare(hoveredOnMarker.getDistanceTo(m.getLocation()),
                ((AbstractEarthQuakeMarker) hoveredOnMarker).getThreatCircle()) > 0) {
              m.setHidden(true);
            }else{
              ((AbstractEarthQuakeMarker)hoveredOnMarker).addCityInThreat(((AbstractMarker)m).getScreenPosition(this.map)); // adds cities in threat
            }
          });
    }

  }

  /*
   * A private helper method that shows only the selected city marker from mouseReleased
   * method along with the earthquakes that the city exists in the threat circle of ,if there's one
   * uses the private field hoveredOnMarker as it holds the marker that the cursor is on and clicked on
   * happens only during use of earthquakes map, a check is made in mouseReleased
   * */
  private void doMouseReleaseCityMarker() {
    // just a check so no null pointer exception can happen un expectantly
    if (hoveredOnMarker != null) {
      // hides all city markers except the one clicked on by using a referential check with
      // the hoveredOnMarker cuz it holds the marker hovered on
      this.cityMarkers.forEach(m -> {
        if (m != hoveredOnMarker)
          m.setHidden(true);
      });
      // hides all earthquakes that the city is not in the threat circle of
      // by getting the distance from the hoveredOnMarker and the earthQuakes's location
      // and checking if it's less than the threat circle's radius or equal
      // check is made by Double.compare for exact match (slightly not correct)
      // if needed more exactness, use BigDecimals
      // since it's made sure in mouseReleased that it's an earthquake marker, then it's a safe cast
      this.earthQuakesMarkers.forEach(
          m -> {
            if (Double.compare(hoveredOnMarker.getDistanceTo(m.getLocation()),
                ((AbstractEarthQuakeMarker) m).getThreatCircle()) > 0) {
              m.setHidden(true);
            }
          });
    }

  }
}
