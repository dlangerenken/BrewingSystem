package gson;

import general.BrewingState;
import general.BrewingState.Position;
import general.BrewingState.State;
import general.BrewingState.Type;
import general.HopAddition;
import general.MaltAddition;
import general.Unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import utilities.CollectionUtil;
import utilities.DummyBuilder;
import categories.IntegrationTest;
import categories.UnitTest;

import com.google.gson.JsonElement;

/**
 * Tests the correct serialization and deserialization of the BrewingState-Object
 * 
 * @author Daniel Langerenken
 *
 */
public class BrewingStateAdapterTest extends SerializerTest {

  /**
   * Tests whether or not a brewing state with hop-cooking can contain ingredients
   */
  @Category(UnitTest.class)
  @Test
  public void testIssue194HopCookingWithIngredients() {
    BrewingState state = BrewingState.fromValue(452);
    List<HopAddition> hopAdditions = new ArrayList<HopAddition>();
    HopAddition firstHopAddition = new HopAddition(1337, Unit.g, "First", 0);
    HopAddition secondHopAddition = new HopAddition(42, Unit.g, "Second", 100);
    hopAdditions.add(firstHopAddition);
    hopAdditions.add(secondHopAddition);
    state.setData(hopAdditions);
    genericTest(state, BrewingState.class);
    String gsonString = Serializer.getInstance().toJson(state);
    Assert.assertTrue(gsonString.contains("First"));
    Assert.assertTrue(gsonString.contains("Second"));
    Assert.assertTrue(gsonString.contains("42"));
    Assert.assertTrue(gsonString.contains("1337"));
    Assert.assertTrue(gsonString.contains("100"));
  }

  /**
   * Test to serialize and deserialize the plain brewing state.
   */
  @Category(UnitTest.class)
  @Test
  public void testPlainBrewingState() {
    genericTest(DummyBuilder.getBrewingState(), BrewingState.class);
  }

  /**
   * Test to serialize and deserialize the brewing state with a iodine-test as object.
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingStateWithIodineTest() {
    BrewingState m = DummyBuilder.getBrewingState();
    m.setData(DummyBuilder.getIodineTest(true));
    genericTest(m, BrewingState.class);
  }

  /**
   * Test to serialize and deserialize the brewing state with a malt addition.
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingStateWithMaltAddition() {
    BrewingState m = DummyBuilder.getBrewingState();
    m.setData(DummyBuilder.getMaltAddition());
    genericTest(m, BrewingState.class);
  }

  /**
   * Test to serialize and deserialize the brewing state with a hop addition
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingStateWithHopAddition() {
    BrewingState m = DummyBuilder.getBrewingState();
    m.setData(DummyBuilder.getHopAddition());
    genericTest(m, BrewingState.class);
  }

  /**
   * Test to serialize and deserialize the brewing state with multiple hop-additions
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingStateWithHopAdditionList() {
    BrewingState m = DummyBuilder.getBrewingState();
    m.setState(State.HOP_COOKING);
    m.setData(DummyBuilder.getHopAdditions());
    genericTest(m, BrewingState.class);
  }

  /**
   * Test to serialize and deserialize the brewing state with multiple malt-additions
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingStateWithMaltAdditionList() {
    BrewingState m = DummyBuilder.getBrewingState();
    m.setState(State.MASHING);
    m.setData(DummyBuilder.getMaltAdditions());
    genericTest(m, BrewingState.class);
  }

  /**
   * Test according to the issue 156 posted by Tobias: {"state":"212","data":null,"type":null}
   * throws exception
   */
  @Category(UnitTest.class)
  @Test
  public void testIssue156() {
    BrewingState m = BrewingState.fromValue(212);
    m.setData(null);
    /*
     * state should not be in quotes, 212 instead of "212"
     */
    String json = "{\"state\":212,\"data\":null,\"type\":null}";
    BrewingState state = gson.fromJson(json, BrewingState.class);
    Assert.assertNotNull(state);
    Assert.assertEquals(m, state);

    /*
     * also if data and type is null, this should be enough:
     */
    String jsonMinified = "{\"state\":212}";
    BrewingState state2 = gson.fromJson(jsonMinified, BrewingState.class);
    Assert.assertEquals(state, state2);
  }

  /**
   * Tests parsing of list<?> #153
   */
  @Category(UnitTest.class)
  @Test
  public void testListParsing() {
    BrewingState m = BrewingState.fromValue(212);
    m.setData(DummyBuilder.getMaltAdditions());
    genericTest(m, BrewingState.class);
  }

  /**
   * Verifies that the BrewingState - Json serialization works correctly. A BrewingState for the
   * mashing process in the state of malt addition request is created, with a hash map of
   * MaltAddition objects to be added. This HashMap is turned into a sorted ArrayList using
   * CollectionUtil.getArrayListOfBrewingStatesSortedByTime. Then the BrewingState is serialized
   * into a Json Object, which is deserialized into a BrewingState again. For this state the data
   * object is converted into a Collection<MaltAddition> using
   * CollectionUtil.getTypedCollectionFromObject. For this collection it will be verified, that
   * every element is also in the original hash set.
   */
  @Category(IntegrationTest.class)
  @Test
  public void testSerialization() {
    BrewingState maltAdditionRequest =
        new BrewingState(Type.REQUEST, State.MASHING, Position.ADDING);
    HashSet<MaltAddition> maltAdditions = new HashSet<MaltAddition>();
    maltAdditions.add(new MaltAddition(1.0f, Unit.kg, "Malt 1", 0));
    maltAdditions.add(new MaltAddition(0.5f, Unit.kg, "Malt 2", 0));
    maltAdditions.add(new MaltAddition(50.0f, Unit.kg, "Malt 3", 2 * 1000));

    maltAdditionRequest.setData(CollectionUtil
        .getArrayListOfBrewingStatesSortedByTime(maltAdditions));

    BrewingStateAdapter bsa = new BrewingStateAdapter();
    JsonElement requestJson = bsa.serialize(maltAdditionRequest, null, null);

    BrewingState maltAdditionResponse = bsa.deserialize(requestJson, null, null);
    Object data = maltAdditionResponse.getData();

    Collection<MaltAddition> responseMaltAdditions =
        CollectionUtil.getTypedCollectionFromObject(data, MaltAddition.class);

    for (MaltAddition malt : responseMaltAdditions) {
      Assert.assertTrue(maltAdditions.contains(malt));
    }
  }
}
