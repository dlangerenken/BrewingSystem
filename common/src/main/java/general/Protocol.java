/*
 *
 */
package general;

import general.BrewingState.State;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import messages.BrewingAbortedMessage;
import messages.BrewingCompleteMessage;
import messages.BrewingStartMessage;
import messages.ConfirmationMessage;
import messages.ConfirmationRequestMessage;
import messages.IodineTestMessage;
import messages.Message;
import messages.StartMessage;
import messages.TemperatureLevelMessage;
import messages.TemperatureMessage;

/**
 * The protocol class used to transmit relevant messages for the creation of a graphical protocol to
 * the client
 */
public class Protocol implements Serializable {

  /** The id of the log this protocol was created from. */
  private transient final int logId;

  /** The Constant serialVersionUID. */
  private transient static final long serialVersionUID = -5829154040962718249L;

  /** when process was aborted, or -1 if it wasn't */
  private long abortionTime = -1;

  /**
   * returns the start time
   * @return start time
   */
  public long getStartTime() {
    return startTime;
  }
  
  /**
   * returns all requests
   * @return list of pairs of confirmation-requests and confirmations
   */
  public List<Pair<ConfirmationRequestMessage, ConfirmationMessage>> getRequests() {
    return requests;
  }


  /** time of completion, or -1 if process wasn't completed */
  private long completionTime = -1;

  /** time when process was started */
  private long startTime = -1;

  /** The recipe that was brewed */
  private Recipe recipe;

  /** each iodine test with its time */
  private List<Pair<IodineTest, Long>> iodineTests;

  /** all temperature messages that occurred during mashing */
  private List<TemperatureMessage> mashingTemperatureMessages;

  /** all temperature messages that occurred during hop cooking */
  private List<TemperatureMessage> hopCookingTemperatureMessages;

  /** all temperature level messages */
  private List<TemperatureLevelMessage> temperatureLevelMessages;

  /** maps each request to its confirmation or null if it has not been confirmed */
  private List<Pair<ConfirmationRequestMessage, ConfirmationMessage>> requests;

  /**
   * All messages which don't belong into the described messages above
   */
  private List<Message> unusedMessages;

  /** parses all relevant information from the BrewingLog stores them in protocol object */
  public Protocol(final BrewingLog brewingLog) {
    temperatureLevelMessages = new ArrayList<>();
    mashingTemperatureMessages = new ArrayList<>();
    hopCookingTemperatureMessages = new ArrayList<>();
    unusedMessages = new ArrayList<>();
    iodineTests = new ArrayList<>();
    requests = new ArrayList<>();
    Map<State, Long> stepTimes = new HashMap<>();

    recipe = brewingLog.getRecipe();
    List<Message> messages = brewingLog.getMessages();
    logId = brewingLog.getId();
    if (messages != null) {
      Collections.sort(messages, new MessageTimeComparator());
      // analyzes all messages that can be processed without information
      // from rest of log
      for (Message message : messages) {
        if (message instanceof BrewingAbortedMessage) {
          abortionTime = message.getTime();
        } else if (message instanceof StartMessage) {
          stepTimes.put(((StartMessage) message).getPosition(), message.getTime());
        } else if (message instanceof BrewingCompleteMessage) {
          completionTime = message.getTime();
        } else if (message instanceof BrewingStartMessage) {
          startTime = message.getTime();
        } else if (message instanceof IodineTestMessage) {
          iodineTests.add(new Pair<>(((IodineTestMessage) message).getIodineTest(), message
              .getTime()));
        } else if (message instanceof TemperatureLevelMessage) {
          temperatureLevelMessages.add((TemperatureLevelMessage) message);
        } else if (message instanceof ConfirmationRequestMessage) {
          requests.add(new Pair<ConfirmationRequestMessage, ConfirmationMessage>(
              (ConfirmationRequestMessage) message, null));
        } else {
          /*
           * The android client only shows messages which therefore need to be saved here
           */
          unusedMessages.add(message);
        }
      }

      // analyzes messages that depend on information that might occur
      // later in log
      for (Message message : messages) {
        if (message instanceof TemperatureMessage) {
          if (stepTimes.containsKey(State.HOP_COOKING)
              && message.getTime() >= stepTimes.get(State.HOP_COOKING)) {
            hopCookingTemperatureMessages.add((TemperatureMessage) message);
          } else if (stepTimes.containsKey(State.MASHING)
              && message.getTime() >= stepTimes.get(State.MASHING)) {
            mashingTemperatureMessages.add((TemperatureMessage) message);
          }

        } else if (message instanceof ConfirmationMessage) {
          for (Pair<ConfirmationRequestMessage, ConfirmationMessage> pair : requests) {
            if (pair.getFirst().getBrewingStep()
                .equals(((ConfirmationMessage) message).getConfirmedState())
                && pair.getSecond() == null) {
              pair.setSecond((ConfirmationMessage) message);
            }
          }
        }
      }

      Collections.sort(mashingTemperatureMessages, new MessageTimeComparator());
      Collections.sort(hopCookingTemperatureMessages, new MessageTimeComparator());
      Collections.sort(temperatureLevelMessages, new MessageTimeComparator());
      Collections.sort(unusedMessages, new MessageTimeComparator());
    }
  }

  /**
   * Returns a list of all messages
   * 
   * @return list of messages
   */
  public List<Message> getAllMessages() {
    List<Message> messages = new ArrayList<>();
    if (mashingTemperatureMessages != null) {
      messages.addAll(mashingTemperatureMessages);
    }
    if (hopCookingTemperatureMessages != null) {
      messages.addAll(hopCookingTemperatureMessages);
    }
    if (temperatureLevelMessages != null) {
      messages.addAll(temperatureLevelMessages);
    }
    if (unusedMessages != null) {
      messages.addAll(unusedMessages);
    }
    if (requests != null) {
      for (Pair<ConfirmationRequestMessage, ConfirmationMessage> pair : requests) {
        if (pair.getFirst() != null) {
          messages.add(pair.getFirst());
        }
        if (pair.getSecond() != null) {
          messages.add(pair.getSecond());
        }
      }
    }
    return new ArrayList<>(new LinkedHashSet<>(messages));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (abortionTime ^ (abortionTime >>> 32));
    result = prime * result + (int) (completionTime ^ (completionTime >>> 32));
    result =
        prime
            * result
            + ((hopCookingTemperatureMessages == null) ? 0 : hopCookingTemperatureMessages
                .hashCode());
    result = prime * result + ((iodineTests == null) ? 0 : iodineTests.hashCode());
    result =
        prime * result
            + ((mashingTemperatureMessages == null) ? 0 : mashingTemperatureMessages.hashCode());
    result = prime * result + ((recipe == null) ? 0 : recipe.hashCode());
    result = prime * result + ((requests == null) ? 0 : requests.hashCode());
    result = prime * result + (int) (startTime ^ (startTime >>> 32));
    result =
        prime * result
            + ((temperatureLevelMessages == null) ? 0 : temperatureLevelMessages.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Protocol other = (Protocol) obj;
    if (abortionTime != other.abortionTime) {
      return false;
    }
    if (completionTime != other.completionTime) {
      return false;
    }
    if (hopCookingTemperatureMessages == null) {
      if (other.hopCookingTemperatureMessages != null) {
        return false;
      }
    } else if (!hopCookingTemperatureMessages.equals(other.hopCookingTemperatureMessages)) {
      return false;
    }
    if (iodineTests == null) {
      if (other.iodineTests != null) {
        return false;
      }
    } else if (!iodineTests.equals(other.iodineTests)) {
      return false;
    }
    if (mashingTemperatureMessages == null) {
      if (other.mashingTemperatureMessages != null) {
        return false;
      }
    } else if (!mashingTemperatureMessages.equals(other.mashingTemperatureMessages)) {
      return false;
    }
    if (recipe == null) {
      if (other.recipe != null) {
        return false;
      }
    } else if (!recipe.equals(other.recipe)) {
      return false;
    }
    if (requests == null) {
      if (other.requests != null) {
        return false;
      }
    } else if (!requests.equals(other.requests)) {
      return false;
    }
    if (startTime != other.startTime) {
      return false;
    }
    if (temperatureLevelMessages == null) {
      if (other.temperatureLevelMessages != null) {
        return false;
      }
    } else if (!temperatureLevelMessages.equals(other.temperatureLevelMessages)) {
      return false;
    }
    return true;
  }

  /**
   * Returns the abortion time.
   * 
   * @return
   */
  public long getAbortionTime() {
    return abortionTime;
  }

  /**
   * Returns the completion time.
   * 
   * @return
   */
  public long getCompletionTime() {
    return completionTime;
  }

  /** gets the id of the brewingLog this protocol was created from */
  public int getLogId() {
    return logId;
  }

  /** used to sort messages by time (ascending) */
  private class MessageTimeComparator implements Comparator<Message> {

    @Override
    public int compare(final Message o1, final Message o2) {
      return Long.valueOf(o1.getTime()).compareTo(o2.getTime());
    }

  }
}
