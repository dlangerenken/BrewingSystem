/*
 * 
 */
package components;

import interfaces.IThermometerReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

import utilities.PropertyUtil;
import exceptions.TemperatureNotReadableException;


/**
 * Thermometer-Reader which loads the relevant config from properties and parses temperatures out of
 * the written file based on the example-file we've got.
 * 
 * @author Daniel Langerenken
 *
 */
public class ThermometerReader implements IThermometerReader {

  /**
   * File name of W1 DS18b20 sensor file. Has to be adapted to concrete sensor.
   */
  private final String tempSensorBaseFolderName;

  @SuppressWarnings("unused")
  /** Temperature-sensor-file name which is not used as the name is calculated. */
  private final String tempSensorFileName;

  /**
   * Instantiates the ThermometerReader and sets the folder for the temperature-savings as well as
   * the file-name from properties
   */
  public ThermometerReader() {
    tempSensorBaseFolderName =
        PropertyUtil.getProperty(PropertyUtil.TEMPERATURE_SENSOR_BASE_FOLDER_PROPERTY);
    tempSensorFileName = PropertyUtil.getProperty(PropertyUtil.TEMPERATURE_SENSOR_FILE_PROPERTY);
  }

  /**
   * Gets the file name for the temperature-sensor.
   *
   * @return name of the file to read from
   */
  protected File getDS18B20TemperatureFile() throws FileNotFoundException {
    File tempSensorBaseFolderFile = new File(tempSensorBaseFolderName);
    if (!tempSensorBaseFolderFile.exists()) {
      throw new FileNotFoundException();
    }
    File[] files = tempSensorBaseFolderFile.listFiles();
    if (files == null || files.length == 0) {
      throw new FileNotFoundException("No files found");
    }
    for (File subFile : files) {
      if (subFile.getName().startsWith("28-")) {
        return new File(subFile.getAbsolutePath() + "/w1_slave");
      }
    }
    return null;
  }

  /**
   * Reads contents of text file.
   *
   * @param filename Name of file to read.
   * @return text content of file.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected String getFileContent(final File file) throws IOException {
    BufferedReader br = null;
    String everything = null;


    try {
      br = new BufferedReader(new FileReader(file));

      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append(System.lineSeparator());
        line = br.readLine();
      }
      everything = sb.toString();

    } finally {
      try {
        br.close();
      } catch (IOException e) {
        // nothing to do here
      }
    }

    return everything;
  }

  /**
   * Reads temperature from DS18b20 sensor.
   * 
   * @return temperature
   * @throws TemperatureNotReadableException thrown if reading failed
   */
  @Override
  public float getTemperature() throws TemperatureNotReadableException {
    try {
      /*
       * parse file content and round the temperature
       */
      String filecontent = getFileContent(getDS18B20TemperatureFile());
      float temp = parseSensorFileContent(filecontent);
      temp = roundTemperature(temp);
      return temp;

    } catch (Throwable t) {
      throw new TemperatureNotReadableException(t);
    }
  }

  /**
   * Parses text content from DS18b20 sensor file to float.
   * 
   * @param filecontent Content of sensor file to parse.
   * @return float value of temperature sensor.
   */
  private float parseSensorFileContent(final String filecontent) {
    String temperatureBeginningConstant = "t=";
    int requiredIndex =
        filecontent.toLowerCase().lastIndexOf(temperatureBeginningConstant)
            + temperatureBeginningConstant.length();
    /*
     * get relevant part of file content
     */
    String tempString = filecontent.substring(requiredIndex);
    /*
     * convert to float
     */
    float temp = new Float(tempString) / 1000;
    return temp;
  }

  /**
   * Rounds temperature value from sensor.
   *
   * @param temp sensor value to round.
   * @return the float
   */
  private float roundTemperature(final float temp) {
    /*
     * round to one decimal place
     */
    float tempRounded = new BigDecimal(temp).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    return tempRounded;
  }
}
