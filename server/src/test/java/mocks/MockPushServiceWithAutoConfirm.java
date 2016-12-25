/*
 * 
 */
package mocks;

import static utilities.NetworkRequestHelper.sendPost;
import push.PushMessage;
import push.PushType;
import messages.ConfirmationRequestMessage;
import messages.Message;
import general.BrewingState;
import general.BrewingState.Position;
import gson.Serializer;
import interfaces.IMessageService;
import utilities.NetworkRequestHelper;


/**
 * This mock confirms requests directly so that a test can run automatically through a whole brewing
 * process
 *
 * @author Daniel Langerenken
 */
public class MockPushServiceWithAutoConfirm implements IMessageService {

  @Override
  public void alarm(final String text) {
    // should do nothing as we dont need to take care of alarms in tests
  }

  /**
   * Catches the messages and answers if necessary
   */
  @Override
  public void notify(final Message m) {
    if (!(m instanceof ConfirmationRequestMessage)) {
      return;
    }
    ConfirmationRequestMessage crm = (ConfirmationRequestMessage) m;
    BrewingState state = crm.getBrewingStep();
    if (!state.requestNeeded()) {
      return;
    }

    try {
      if (state.getPosition() == Position.IODINE) {
        sendPost(NetworkRequestHelper.SERVER_ADDRESS + "brewing/iodine", "duration=0");
      } else {
        sendPost(NetworkRequestHelper.SERVER_ADDRESS + "brewing/confirm", "state="
            + Serializer.getInstance().toJson(state));
      }
    } catch (Exception e) {
      // should do nothing
      return;
    }
  }

  @Override
  public void notify(final PushMessage m) {
    // should do nothing as we dont need to take care of subscribers in tests
    return;
  }

  @Override
  public void notify(final String data, final PushType type) {
    // should do nothing as we dont need to take care of subscribers in tests
    return;
  }

  @Override
  public void subscribe(final String id) {
    // should do nothing as we dont need to take care of subscribers in tests
    return;
  }

  @Override
  public void unsubscribe(final String id) {
    // should do nothing as we dont need to take care of subscribers in tests
    return;
  }

}
