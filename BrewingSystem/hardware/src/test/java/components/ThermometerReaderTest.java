/*
 * 
 */
package components;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import categories.UnitTest;
import exceptions.TemperatureNotReadableException;


/**
 * Class for testing the temperature-sensor.
 */
@RunWith(MockitoJUnitRunner.class)
public class ThermometerReaderTest {


  /** The reader which should be used. */
  private static ThermometerReader reader;

  /**
   * Exception which should be thrown in a few tests
   */
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  /**
   * Parameterless constructor needed as test will start multiple times.
   */
  public ThermometerReaderTest() {}

  /**
   * Inits the Thermometer only once at the beginning of all tests as it does not change
   */
  @BeforeClass
  public static void init() {
    reader = Mockito.spy(new ThermometerReader());
  }

  /**
   * Tests whether or not the parsing works as expected - should return 20.4 degrees as the file
   * does not change
   * 
   * @throws TemperatureNotReadableException thrown if something was invalid
   * @throws IOException
   * @throws URISyntaxException
   */
  @Category(UnitTest.class)
  @Test
  public void testValidFileParsing() throws TemperatureNotReadableException, IOException,
      URISyntaxException {
    ClassLoader classLoader = getClass().getClassLoader();
    String path = classLoader.getResource("temperature_file_20_degree.txt").toURI().getPath();
    File file = new File(path);
    Mockito.doReturn(file).when(reader).getDS18B20TemperatureFile();
    float temperature = reader.getTemperature();
    float desiredTemp = 20.4f;
    Assert.assertEquals(desiredTemp, temperature, 0.5f);
  }

  /**
   * Tests whether or not the parsing works as expected - should fail as the file is invalid
   * 
   * @throws TemperatureNotReadableException thrown if something was invalid
   * @throws IOException
   * @throws URISyntaxException
   */
  @Category(UnitTest.class)
  @Test
  public void testInvalidFileParsing() throws TemperatureNotReadableException, IOException,
      URISyntaxException {
    ClassLoader classLoader = getClass().getClassLoader();
    String path = classLoader.getResource("temperature_file_20_degree_invalid.txt").toURI().getPath();
    File file = new File(path);
    Mockito.doReturn(file).when(reader).getDS18B20TemperatureFile();
    expectedException.expect(TemperatureNotReadableException.class);
    reader.getTemperature();
  }

  /**
   * Tests whether or not the parsing works as expected
   * 
   * @throws TemperatureNotReadableException thrown if something was invalid
   * @throws IOException
   */
  @Category(UnitTest.class)
  @Test
  public void testNoFileFound() throws TemperatureNotReadableException, IOException {
    File file = null;
    Mockito.doReturn(file).when(reader).getDS18B20TemperatureFile();
    expectedException.expect(TemperatureNotReadableException.class);
    reader.getTemperature();
  }
}
