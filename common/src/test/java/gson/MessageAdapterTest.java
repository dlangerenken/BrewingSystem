package gson;

import general.BrewingState;
import general.BrewingState.State;
import general.HopAddition;
import general.Unit;

import java.util.ArrayList;
import java.util.List;

import messages.ConfirmationRequestMessage;
import messages.Message;
import messages.PreNotificationMessage;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import utilities.DummyBuilder;
import categories.UnitTest;

/**
 * Tests the correct serialization and deserialization of the message-object
 * 
 * @author Daniel Langerenken
 *
 */
public class MessageAdapterTest extends SerializerTest {

  /**
   * Test to serialize and deserialize all message types
   */
  @Category(UnitTest.class)
  @Test
  public void testMessage() {
    for (int i = 0; i < 7; i++) {
      Message m = DummyBuilder.getMessage(i);
      genericTest(m, Message.class);
    }
  }

  /**
   * Tests whether or not the pre notification can contain a message which is correctly parsed
   */
  @Category(UnitTest.class)
  @Test
  public void testPreNotificationMessage() {
    PreNotificationMessage message =
        new PreNotificationMessage(BrewingState.fromValue(333),
            DummyBuilder.getHopCookingMessage(), 0);
    genericTest(message, Message.class);
  }

  /**
   * Test to serialize and deserialize a random message.
   */
  @Category(UnitTest.class)
  @Test
  public void testMessageWithGenericData() {
    ConfirmationRequestMessage m = DummyBuilder.getConfirmationRequestMessage();
    m.getBrewingStep().setState(State.MASHING);
    genericTest(m, Message.class);
  }
  
  /**
   * Tests to serialize a confirmation request message with ingredient addition
   */
  @Category(UnitTest.class)
  @Test
  public void testConfirmationRequestMessage(){
    ConfirmationRequestMessage m = DummyBuilder.getConfirmationRequestMessage();
    BrewingState step = BrewingState.fromValue(452);
    List<HopAddition> hopAdditions = new ArrayList<HopAddition>();
    hopAdditions.add(new HopAddition(20, Unit.g, "Test", 0));
    step.setData(hopAdditions);
    m.setBrewingStep(step);
    genericTest(m, Message.class);
    
  }
}
