package general;

import general.BrewingState.Position;
import general.BrewingState.State;
import general.BrewingState.Type;

import java.util.ArrayList;

import junit.framework.Assert;
import messages.BrewingAbortedMessage;
import messages.BrewingCompleteMessage;
import messages.BrewingStartMessage;
import messages.ConfirmationMessage;
import messages.ConfirmationRequestMessage;
import messages.Message;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import utilities.DummyBuilder;
import categories.UnitTest;

/**
 * Tests that protocols work as expected.
 * 
 * @author matthias
 *
 */
public class ProtocolTest {

  /** tests the protocol class */
  @Category(UnitTest.class)
  @Test
  public void testProtocol() {
    ArrayList<Message> messages = new ArrayList<Message>();
    messages.add(DummyBuilder.getIodineTestMessage());
    messages.add(DummyBuilder.getTemperatureMessage());
    messages.add(DummyBuilder.getConfirmationRequestMessage());
    BrewingLog log = DummyBuilder.getBrewingLog();
    log.setMessages(messages);
    Protocol protocol = new Protocol(log);
    Assert.assertEquals(protocol.getLogId(), log.getId());


  }

  /**
   * Verifies that the confirmation messages are stored correctly
   */
  @Category(UnitTest.class)
  @Test
  public void testConfirmations() {
    BrewingLog log = new BrewingLog(DummyBuilder.getRealisticRecipe(), 1);
    log.log(new ConfirmationRequestMessage(new BrewingState(Type.REQUEST, State.MASHING,
        Position.ADDING)));
    log.log(new ConfirmationMessage(
        new BrewingState(Type.NORMAL, State.LAUTERING, Position.ONGOING), new BrewingState(
            Type.REQUEST, State.MASHING, Position.ADDING)));
    Protocol protocol = new Protocol(log);
    Assert.assertTrue(protocol.getRequests().size() == 1);
    Assert.assertFalse(protocol.getRequests().get(0).getSecond() == null);
  }

  /**
   * Verifies that the protocol times are stored correctly
   */
  @Category(UnitTest.class)
  @Test
  public void testProtocolTimes() {
    BrewingLog log = new BrewingLog(DummyBuilder.getRealisticRecipe(), 1);
    log.log(new BrewingStartMessage("beer"));
    log.log(new BrewingAbortedMessage("no beer"));
    log.log(new BrewingCompleteMessage("beer"));
    Protocol protocol = new Protocol(log);
    Assert.assertFalse(protocol.getStartTime() == -1);
    Assert.assertFalse(protocol.getAbortionTime() == -1);
    Assert.assertFalse(protocol.getCompletionTime() == -1);

  }
}
