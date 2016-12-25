package network;

import static utilities.NetworkRequestHelper.sendGet;
import gson.Serializer;

import java.util.Date;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import utilities.NetworkResult;
import utilities.PropertyUtil;
import categories.UnitTest;

/**
 * This class provides tests for the info resource and checks that the network-request is reassigned
 * correctly
 * 
 * @author Daniel Langerenken
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class InfoResourceTest extends ResourceControllerTestHelper {

  /**
   * Tests the information resource which returns the projectFolder and recipe-path
   *
   * @throws Exception exception which can be thrown by the network communication
   */
  @Category(UnitTest.class)
  @Test
  public void testInfoResource() throws Exception {
    Properties properties = PropertyUtil.getProperties();
    properties.setProperty("ProjectFolder", PropertyUtil.PROJECT_FOLDER_PATH);
    properties.setProperty("RecipePath", PropertyUtil.RECIPE_PATH);

    NetworkResult result = sendGet(serverAddress);
    Assert.assertTrue(result.isPositiveResult());
    Properties from = Serializer.getInstance().fromJson(result.getResult(), Properties.class);
    Assert.assertEquals(from, properties);
  }

  /**
   * Tests the information resource which returns the projectFolder and recipe-path
   *
   * @throws Exception exception which can be thrown by the network communication
   */
  @Category(UnitTest.class)
  @Test
  public void testIsAlive() throws Exception {
    NetworkResult result = sendGet(serverAddress + "/alive");
    Assert.assertTrue(result.isPositiveResult());
    Assert.assertEquals(new Boolean(true), Boolean.valueOf(result.getResult()));
  }

  /**
   * Tests the time resource which returns the server time
   * 
   * @throws Exception exception which can be thrown by the network communication
   */
  @Category(UnitTest.class)
  @Test
  public void time() throws Exception {
    long time = new Date().getTime();
    NetworkResult result = sendGet(serverAddress + "/time");
    Assert.assertTrue(result.isPositiveResult());
    long givenTime = Long.parseLong(result.getResult());
    Assert.assertEquals(time, givenTime, 1000);
  }

  /**
   * Security Surveillance causes exception when asking for alive-status
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testIssue240() throws Exception {
    for (int i = 0; i < 20; i++) {
      Thread.sleep(200);
      NetworkResult result = sendGet(serverAddress + "/alive");
      Assert.assertEquals(Boolean.valueOf(true), Boolean.valueOf(result.getResult()));
    }
  }
}
