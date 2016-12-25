/*
 *
 */
package utilities;

import general.MessagePriority;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Reads the information of a properties file and saves changes accordingly used for basic
 * server-information such as address,port, loglevel, directory.
 *
 * @author Daniel Langerenken
 */
public class PropertyUtil {

  /** Log4j-Logger. */
  public static final Logger LOGGER = LogManager.getLogger();

  /** Name of the folder, property-file and other elements should be stored in. */
  public static final String PROJECT_FOLDERNAME = ".brewing";

  /** Key for the server-address-property. */
  public static final String SERVER_ADDRESS_PROPERTY = "ServerAddres";

  /** Key for the heater-pin-property. */
  public static final String HEATER_PIN_PROPERTY = "HeaterPin";

  /** Key for the heater-pin-property. */
  public static final String SIGNAL_PIN_PROPERTY = "SignalPin";

  /** Key for the Stirrer-pin-property. */
  public static final String STIRRER_PIN_PROPERTY = "StirrerPin";

  /** Default value for the heater-pin-property. */
  public static final String HEATER_PIN_DEFAULT = "03";

  /** Default value for the Stirrer-pin-property. */
  public static final String STIRRER_PIN_DEFAULT = "04";

  /** Default value for the Stirrer-pin-property. */
  public static final String SIGNAL_PIN_DEFAULT = "05";

  /** Key for the resource-property. */
  public static final String RESOURCE_PROPERTY = "Resource";

  /** Key for the web-frontend-property. */
  public static final String WEB_FRONTEND_PROPERTY = "WebFrontend";

  /** Key for the log-level-property. */
  public static final String LOG_LEVEL_PROPERTY = "LogLevel";

  /** Key for the http-port-property. */
  public static final String HTTP_PORT_PROPERTY = "HttpPort";

  /** Key for the gcm-api-property. */
  public static final String GCM_API_KEY_PROPERTY = "GcmApi";

  /** Key for the prenotification time. */
  public static final String MILLIS_TO_NOTIFICATION_PROPERY = "PreNotifyTime";

  /** Key for timeout for unconfirmed requests. */
  public static final String MILLIS_TO_REQUEST_CONFIRMATION_TIMEOUT_PROPERTY = "ReqResTimeout";

  /** Default value for the web-frontend-address. */
  public static final String WEB_FRONTEND_DEFAULT = "http://www.google.de/";

  /** Default value for the log level. */
  public static final String LOG_LEVEL_DEFAULT = "ALL";

  /** Default value for the http-port. */
  public static final int HTTP_PORT_DEFAULT = 1337;

  /** Default value for the server-url-address. */
  public static final String SERVER_ADDRESS_DEFAULT = "http://127.0.0.1";

  /** Default value for the resource. */
  public static final String RESOURCE_DEFAULT = "/";

  /** Default value for the gcm-api-key. */
  public static final String GCM_API_KEY_DEFAULT = "INSERT_GCM_API_KEY_HERE";

  /** Default value for the prenotification time. */
  public static final long MILLIS_TO_NOTIFICATION_DEFAULT = 5 * 60 * 1000;

  /** Default timeout for unconfirmed requests. */
  public static final long MILLIS_TO_REQUEST_CONFIRMATION_TIMEOUT_DEFAULT = 15 * 60 * 1000;

  /** Location of the project folder (which contains properties file, recipes, logs. */
  public static final String PROJECT_FOLDER_PATH = System.getProperty("user.home") + File.separator
      + PROJECT_FOLDERNAME;


  /** Propertie that specifies tolerance in heatup delta **/
  public static final String TEMPC_HEATUP_DELTA_PROPERTY = "TempcHeatupDelta";
  /** Default value for heatup delta **/
  private static final float TEMPC_HEATUP_DELTA_DEFAULT = 2.0f;

  /** Property that specifies the temperature delta when to start hop cooking **/
  public static final String HOPCK_PRENOTIF_TEMPERATURE_PROPERTY = "HopcPrenotificationTemperature";
  /** Default value for temperature delta when to start hop cooking **/
  private static final float HOPCK_PRENOTIF_TEMPERATURE_DEFAULT = 5.0f;

  /**
   * Property that specifies the time to notify the user that he has to add hop before the hop needs
   * to be added
   **/
  public static final String HOPCK_PRENOTIF_TIME_PROPERTY = "HopcPrenotificationTimeMillis";
  /** Default value for hopcooking prenotification time **/
  private static final long HOPCK_PRENOTIF_TIME_DEFAULT = 3 * 60 * 1000; // 3 minutes.

  /** The temperature that is used for hop cooking **/
  public static final String HOPCK_COOKING_TEMPERATURE_PROPERTY = "HopcCookingTemperature";
  /** Default temperature to be used for hop cooking **/
  private static final float HOPCK_COOKING_TEMPERATURE_DEFAULT = 100.0f;

  /** The room temperature. Mashing temperature levels may not be below the room temperature */
  public static final String ROOM_TEMPERATURE_PROPERTY = "RoomTemperature";
  /** The default room temperature */
  private static final float ROOM_TEMPERATURE_DEFAULT = 20.0f;

  /**
   * The amount the temperature has to change in order for the new temperature to be written to the
   * log
   */
  public static final String TEMPERATURE_LOGGING_TEMP_DELTA_PROPERTY = "TempLoggingTempDelta";
  /** Default value is 3°C. */
  private static final int TEMPERATURE_LOGGING_TEMP_DELTA_DEFAULT = 3;

  /** The amount of time to pass until the next temperature is logged. */
  public static final String TEMPERATURE_LOGGING_TIME_DELTA_PROPERTY = "TempLoggingTimeDelta";
  /** Default value is 5 seconds. */
  private static final long TEMPERATURE_LOGGING_TIME_DELTA_DEFAULT = 5 * 1000;

  /** Whether a Pre-Notification should be indicated with a beep using the Raspberrys Piezzo */
  public static final String SEND_PRENOTIFICATION_BEEP_PROPERTY = "SendPreNotificationBeep";
  /** Initially send beeps */
  public static final boolean SEND_PRENOTIFICATION_BEEP_DEFAULT = true;
  
  /** Whether a ConfirmationRequest Message should be indicated with a beep using the Raspberrys Piezzo */
  public static final String SEND_CONFIRMATION_REQUEST_BEEP_PROPERTY = "SendConfirmationRequestBeep";
  /** By default send beeps */
  public static final boolean SEND_CONFIRMATION_REQUEST_BEEP_DEFAULT = true;
  
  /** Location of log files. */
  public static final String LOG_PATH = PROJECT_FOLDER_PATH + File.separator + "logs";

  /** Location of protocol files. */
  public static final String PROTOCOL_PATH = PROJECT_FOLDER_PATH + File.separator + "protocols";

  /** Location of recipe files. */
  public static final String RECIPE_PATH = PROJECT_FOLDER_PATH + File.separator + "recipes";

  /** File extension for recipe files. */
  public static final String RECIPE_FILE_EXT = ".beer";

  /** File extension for protocol files. */
  public static final String PROTOCOL_FILE_EXT = ".protocol";

  /** File extension for log files. */
  public static final String LOG_FILE_EXT = ".log";

  /** Minimum message priority in protocol */
  public static final MessagePriority PROTOCOL_PRIORITY = MessagePriority.HIGH;

  /** Creates the propertiesFile in the according user-folder. */
  private static final String PROPERTIES_FILE_PATH = PROJECT_FOLDER_PATH + File.separator
      + "brewing.properties";

  /** Temperature-sensor-file name which is not used as the name is calculated. */
  public static final String TEMPERATURE_SENSOR_FILE_PROPERTY = "TempSensorfile";

  public static final String TEMPERATURE_SENSOR_FILE_DEFAULT =
      "/sys/bus/w1/devices/28-0000056291e6/w1_slave";

  /**
   * File name of W1 DS18b20 sensor file. Has to be adapted to concrete sensor.
   */
  public static final String TEMPERATURE_SENSOR_BASE_FOLDER_PROPERTY = "TempsensorBaseFolder";

  public static final String TEMPERATURE_SENSOR_BASE_FOLDER_DEFAULT = "/sys/bus/w1/devices/";


  /**
   * Cached map of all properties
   */
  static Map<String, String> propertyMap;

  /**
   * Instantiates the property map with all values available from default-properties
   */
  static {
    List<String> properties = new ArrayList<String>();
    for (Object element : createDefaultProperty().keySet()) {
      properties.add(element.toString());
    }
    propertyMap = getProperties(properties);
  }

  /**
   * Generate a property with default data.
   *
   * @return the default property
   */
  private static Properties createDefaultProperty() {
    Properties properties = new Properties();

    properties.setProperty(HTTP_PORT_PROPERTY, HTTP_PORT_DEFAULT + "");
    properties.setProperty(LOG_LEVEL_PROPERTY, LOG_LEVEL_DEFAULT);
    properties.setProperty(WEB_FRONTEND_PROPERTY, WEB_FRONTEND_DEFAULT);
    properties.setProperty(RESOURCE_PROPERTY, RESOURCE_DEFAULT);
    properties.setProperty(SERVER_ADDRESS_PROPERTY, SERVER_ADDRESS_DEFAULT);
    properties.setProperty(GCM_API_KEY_PROPERTY, GCM_API_KEY_DEFAULT);
    properties.setProperty(HEATER_PIN_PROPERTY, HEATER_PIN_DEFAULT);
    properties.setProperty(STIRRER_PIN_PROPERTY, STIRRER_PIN_DEFAULT);
    properties.setProperty(SIGNAL_PIN_PROPERTY, SIGNAL_PIN_DEFAULT);
    properties.setProperty(MILLIS_TO_NOTIFICATION_PROPERY, MILLIS_TO_NOTIFICATION_DEFAULT + "");
    properties.setProperty(MILLIS_TO_REQUEST_CONFIRMATION_TIMEOUT_PROPERTY,
        MILLIS_TO_REQUEST_CONFIRMATION_TIMEOUT_DEFAULT + "");

    properties.setProperty(TEMPC_HEATUP_DELTA_PROPERTY, String.valueOf(TEMPC_HEATUP_DELTA_DEFAULT));
    properties.setProperty(HOPCK_PRENOTIF_TEMPERATURE_PROPERTY,
        String.valueOf(HOPCK_PRENOTIF_TEMPERATURE_DEFAULT));
    properties.setProperty(HOPCK_PRENOTIF_TIME_PROPERTY,
        String.valueOf(HOPCK_PRENOTIF_TIME_DEFAULT));
    properties.setProperty(HOPCK_COOKING_TEMPERATURE_PROPERTY,
        String.valueOf(HOPCK_COOKING_TEMPERATURE_DEFAULT));
    properties.setProperty(TEMPERATURE_LOGGING_TEMP_DELTA_PROPERTY,
        String.valueOf(TEMPERATURE_LOGGING_TEMP_DELTA_DEFAULT));
    properties.setProperty(TEMPERATURE_LOGGING_TIME_DELTA_PROPERTY,
        String.valueOf(TEMPERATURE_LOGGING_TIME_DELTA_DEFAULT));
    properties.setProperty(ROOM_TEMPERATURE_PROPERTY, String.valueOf(ROOM_TEMPERATURE_DEFAULT));
    properties.setProperty(TEMPERATURE_SENSOR_FILE_PROPERTY, TEMPERATURE_SENSOR_FILE_DEFAULT);
    properties.setProperty(TEMPERATURE_SENSOR_BASE_FOLDER_PROPERTY,
        TEMPERATURE_SENSOR_BASE_FOLDER_DEFAULT);
    properties.setProperty(SEND_PRENOTIFICATION_BEEP_PROPERTY, 
    		String.valueOf(SEND_PRENOTIFICATION_BEEP_DEFAULT));
    properties.setProperty(SEND_CONFIRMATION_REQUEST_BEEP_PROPERTY, 
    		String.valueOf(SEND_CONFIRMATION_REQUEST_BEEP_DEFAULT));

    return properties;
  }

  /**
   * To create a properties file with default values.
   *
   * @throws IOException if the file cannot created
   */
  public static void createPropertyFile() throws IOException {
    LOGGER.info("Create new property-File in " + PROPERTIES_FILE_PATH);

    /*
     * Checks if parent-directory for file exists, if not, creates this directory
     */
    File properties = new File(PROPERTIES_FILE_PATH);
    File parent = properties.getParentFile();
    if (!parent.exists() && !parent.mkdirs()) {
      LOGGER.error("Couldn't create dir: " + parent);
      throw new IllegalStateException("Couldn't create dir: " + parent);
    }

    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(properties));
    createDefaultProperty().store(bos,
        "# Configuration file for the brewing-server (default generated)");
    bos.close();
    LOGGER.info("A new property-File with default values was generated in " + PROPERTIES_FILE_PATH);
  }

  /**
   * Returns the http port in the given file.
   *
   * @return http-port as integer
   */
  public static int getHttpPort() {
    try {
      return Integer.parseInt(getProperty(HTTP_PORT_PROPERTY));
    } catch (NumberFormatException e) {
      LOGGER.error("HTTP Port is not a number.", e);
    }
    return HTTP_PORT_DEFAULT;
  }

  /**
   * Returns the TemperatureController heatup delta to be used.
   * 
   * @return Heatup Delta as float.
   */
  public static float getTempcHeatupDelta() {
    try {
      return Float.parseFloat(getProperty(PropertyUtil.TEMPC_HEATUP_DELTA_PROPERTY));
    } catch (Throwable t) {
      LOGGER.error("Cannot read TEMPC_HEATUP_DELTA_PROPERTY from settings file!", t);
      return PropertyUtil.TEMPC_HEATUP_DELTA_DEFAULT;
    }
  }

  /**
   * Returns the hop cooking prenotification temperature.
   * 
   * @return The hop cooking prenotification temperature as float value.
   */
  public static float getHopCookingPrenotificationTemperature() {
    try {
      return Float.parseFloat(getProperty(PropertyUtil.HOPCK_PRENOTIF_TEMPERATURE_PROPERTY));
    } catch (Throwable t) {
      LOGGER.error("Cannot read HOPCK_PRENOTIF_TEMPERATURE_PROPERTY from settings file!", t);
      return PropertyUtil.HOPCK_PRENOTIF_TEMPERATURE_DEFAULT;
    }
  }

  /**
   * Returns the hop cooking prenotification time in milliseconds.
   * 
   * @return The hop cooking prenotification time as long in milliseconds.
   */
  public static long getHopCookingPrenotificationTimeMillis() {
    try {
      return Long.parseLong(getProperty(PropertyUtil.HOPCK_PRENOTIF_TIME_PROPERTY));
    } catch (Throwable t) {
      LOGGER.error("Cannot read HOPCK_PRENOTIF_TIME_PROPERTY from settings file!", t);
      return PropertyUtil.HOPCK_PRENOTIF_TIME_DEFAULT;
    }
  }

  /**
   * Returns the hop cooking temperature.
   * 
   * @return
   */
  public static float getHopCookingTemperature() {
    try {
      return Float.parseFloat(getProperty(PropertyUtil.HOPCK_COOKING_TEMPERATURE_PROPERTY));
    } catch (Throwable t) {
      LOGGER.error("Cannot read HOPCK_COOKING_TEMPERATURE_PROPERTY from settings file!", t);
      return PropertyUtil.HOPCK_COOKING_TEMPERATURE_DEFAULT;
    }
  }

  /**
   * The amount the temperature has to change in order for the new temperature to be written to the
   * log.
   * 
   * @return
   */
  public static int getTemperatureLoggingTemperatureDelta() {
    try {
      return Integer.parseInt(getProperty(PropertyUtil.TEMPERATURE_LOGGING_TEMP_DELTA_PROPERTY));
    } catch (Throwable t) {
      LOGGER.error("Cannot read HOPCK_COOKING_TEMPERATURE_PROPERTY from settings file!", t);
      return PropertyUtil.TEMPERATURE_LOGGING_TEMP_DELTA_DEFAULT;
    }
  }

  /**
   * The room temperature. Temperature levels in the mashing plan are not allowed to be below the
   * room temperature as we are not able to cool down.
   * 
   * @return
   */
  public static float getRoomTemperature() {
    try {
      return Float.parseFloat(getProperty(PropertyUtil.ROOM_TEMPERATURE_PROPERTY));
    } catch (Throwable t) {
      LOGGER.error("Cannot read ROOM_TEMPERATURE from settings file!", t);
      return PropertyUtil.ROOM_TEMPERATURE_DEFAULT;
    }
  }
  
  public static boolean getSendPreNotificationBeep() {
  	try {
  		return Boolean.parseBoolean(getProperty(PropertyUtil.SEND_PRENOTIFICATION_BEEP_PROPERTY));
  	} catch (Throwable t) {
  		LOGGER.error("Cannot read PRENOTIFICATION_BEEP Property", t);
  		return PropertyUtil.SEND_PRENOTIFICATION_BEEP_DEFAULT;
  	}
  }
  
  public static boolean getSendConfirmationRequestBeep() {
  	try {
  		return Boolean.parseBoolean(getProperty(PropertyUtil.SEND_CONFIRMATION_REQUEST_BEEP_PROPERTY));
  	} catch (Throwable t) {
  		LOGGER.error("Cannot read CONFIRMATION_REQUEST_BEEP Property", t);
  		return PropertyUtil.SEND_CONFIRMATION_REQUEST_BEEP_DEFAULT;
  	}
  }

  /**
   * To get the properties object.
   *
   * @return properties object
   */
  public static Properties getProperties() {
    LOGGER.info("getProperties from " + PROPERTIES_FILE_PATH);
    return PropertyUtil.loadFile();
  }

  /**
   * Now caches the properties to have a better performance
   * 
   * @param properties List of properties which should be collected
   * @return Map of Key,Value Properties
   */
  public static Map<String, String> getProperties(final Collection<String> properties) {
    LOGGER.info("getProperties");
    Map<String, String> propertiesMap = new HashMap<String, String>();
    Properties propertyFile = PropertyUtil.loadFile();
    for (String singleProperty : properties) {
      String value = getProperty(singleProperty, propertyFile);
      propertiesMap.put(singleProperty, value);
    }
    return propertiesMap;
  }

  /**
   * Returns the property which is stored in the cache if this returns null, we check the properties
   * file again
   * 
   * @param param property which should received
   * @return value of the key-property
   */
  public static String getProperty(final String param) {
    String value = propertyMap.get(param);
    if (value == null) {
      value = getProperty(param, createDefaultProperty());
    }
    return value;
  }

  /**
   * To get one value of the given parameter.
   *
   * @param param the parameter of the property
   * @return the value, if it exists in the property file a default value, if it don't exists in the
   *         property file, but it exists a default value else null
   */
  public static String getProperty(final String param, final Properties properties) {
    LOGGER.info("getProperty " + param);

    if (properties.getProperty(param) == null) {

      String defaultPropertyValue = createDefaultProperty().getProperty(param);

      /*
       * A default value for the param exists, but is still not in the file -> added to the file
       */
      if (defaultPropertyValue != null) {
        LOGGER.info("Add following default property to the property-file: " + param + ": "
            + defaultPropertyValue);
        properties.setProperty(param, defaultPropertyValue);
        try {
          FileOutputStream outputStream = new FileOutputStream(PROPERTIES_FILE_PATH);
          properties.store(outputStream,
              "# Configuration file for the Mobile4D-Server (default value for " + param
                  + " added)");
          outputStream.close();
        } catch (IOException e) {
          LOGGER.error("Could not add the not existing default property to the property-file: "
              + param + ": " + defaultPropertyValue, e);
          return defaultPropertyValue;
        }
      }
      return defaultPropertyValue;
    }
    return properties.getProperty(param);
  }

  /**
   * Returns a long property value from the given parameter
   * 
   * @param param parameter which should be checked in the properties
   * @return long-value of the value from the property
   */
  public static long getPropertyLong(final String param) {
    return new Long(getProperty(param)).longValue();
  }

  /**
   * Returns a int property value from the given parameter
   * 
   * @param param parameter which should be checked in the properties
   * @return int-value of the value from the property
   */
  public static long getPropertyInt(final String param) {
    return new Integer(getProperty(param)).intValue();
  }

  /**
   * Returns the serveraddress.
   *
   * @return serveraddress in format http://{address}:]port}/{resource}
   */
  public static String getServerAddress() {
    return String.format("%s:%s%s", getProperty(SERVER_ADDRESS_PROPERTY),
        getProperty(HTTP_PORT_PROPERTY), getProperty(RESOURCE_PROPERTY));
  }

  /**
   * to load a properties file.
   *
   * @return the properties loaded from the file
   */
  private static Properties loadFile() {
    LOGGER.info("load PropertyFile " + PROPERTIES_FILE_PATH);
    try {
      Properties property = new Properties();
      BufferedInputStream stream =
          new BufferedInputStream(new FileInputStream(PROPERTIES_FILE_PATH));
      property.load(stream);
      stream.close();
      return property;

    } catch (FileNotFoundException e) {
      /*
       * Create a properties file with default values
       */
      try {
        createPropertyFile();
        LOGGER.info("Properties File with default values generated");

        Properties property = new Properties();
        BufferedInputStream stream =
            new BufferedInputStream(new FileInputStream(PROPERTIES_FILE_PATH));
        property.load(stream);
        stream.close();

        return property;
      } catch (IOException ioe) {
        LOGGER.error("Properties File cannot be created", ioe);
        /*
         * Default properties file cannot created or read
         */
        return createDefaultProperty();
      }

    } catch (IOException e) {
      /*
       * Return default properties values
       */
      return createDefaultProperty();
    }
  }
}
