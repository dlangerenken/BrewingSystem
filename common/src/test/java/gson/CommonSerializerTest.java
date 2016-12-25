package gson;

import junit.framework.Assert;
import general.BrewingLog;
import general.BrewingProcess;
import general.HopAddition;
import general.MaltAddition;
import general.Protocol;
import general.Recipe;
import messages.BrewingAbortedMessage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import utilities.DummyBuilder;
import categories.UnitTest;

/**
 * This class tests a few objects from the common project which might not have an own
 * serialization-adapter but still need to be send via network
 * 
 * @author Daniel Langerenken
 *
 */
public class CommonSerializerTest extends SerializerTest {

  /**
   * Exception which should occur
   */
  @Rule
  public ExpectedException exception = ExpectedException.none();

  /**
   * Test to serialize and deserialize the brewing process.
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingProcess() {
    genericTest(DummyBuilder.getBrewingProcess(), BrewingProcess.class);
  }

  /**
   * Test to parse a recipe and deserialize it afterwards.
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipeIssue() {
    Recipe recipe = DummyBuilder.getRealisticRecipe();
    genericTest(recipe, Recipe.class);
  }

  /**
   * Exception occured while testing - somehow the BrewingAbortedMessage was not taken into account
   * when serializing
   */
  @Category(UnitTest.class)
  @Test
  public void testNullPointerExceptionBrewingProcess() {
    BrewingProcess process = DummyBuilder.getBrewingProcess();
    process.getBrewingLog().getMessages().add(new BrewingAbortedMessage("Test reason"));
    String nullPointerThrough = Serializer.getInstance().toJson(process);
    BrewingProcess newProcess =
        Serializer.getInstance().fromJson(nullPointerThrough, BrewingProcess.class);
    Assert.assertNotNull(newProcess);
  }

  /**
   * Test to serialize and deserialize a brewing log.
   */
  @Category(UnitTest.class)
  @Test
  public void testLog() {
    genericTest(DummyBuilder.getBrewingLog(), BrewingLog.class);
  }

  /**
   * Test to serialize and deserialize a hop addition.
   */
  @Category(UnitTest.class)
  @Test
  public void testHopAddition() {
    genericTest(DummyBuilder.getHopAddition(), HopAddition.class);
  }

  /**
   * Test to serialize and deserialize a malt addition.
   */
  @Category(UnitTest.class)
  @Test
  public void testMaltAddition() {
    genericTest(DummyBuilder.getMaltAddition(), MaltAddition.class);
  }

  /**
   * Test to serialize and deserialize a brewing protocol.
   */
  @Category(UnitTest.class)
  @Test
  public void testProtocol() {
    genericTest(DummyBuilder.getProtocol(), Protocol.class);
  }

  /**
   * Test to serialize and deserialize a random recipe.
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipe() {
    genericTest(DummyBuilder.getRecipe(), Recipe.class);
  }
}
