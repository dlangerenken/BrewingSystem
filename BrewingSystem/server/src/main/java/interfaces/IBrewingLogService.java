/*
 * 
 */
package interfaces;

import java.util.Collection;

import messages.Message;
import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import general.BrewingProcess;
import general.MessagePriority;


/**
 * This interfaces gives the possibilities to log and return messages.
 *
 * @author Daniel Langerenken
 */
public interface IBrewingLogService {
  
  /**
   * Finishs the logging and saves the file on the file system.
   *
   * @throws BrewingProcessNotFoundException Thrown if no log-file exists for the current brewing
   *         process
   */
  void finishLog() throws BrewingProcessNotFoundException;

  /**
   * Returns a list of messages in correct order.
   *
   * @return list of messages
   * @throws BrewingProcessNotFoundException Thrown if no log-file exists for the current brewing
   *         process
   */
  Collection<Message> getMessages() throws BrewingProcessNotFoundException;

  /**
   * Returns a list of messages in correct order.
   *
   * @param priority e.g. high, low, medium
   * @return list of messages with given priority (or higher)
   * @throws BrewingProcessNotFoundException Thrown if no log-file exists for the current brewing
   *         process
   */
  Collection<Message> getMessagesByPriority(MessagePriority priority)
      throws BrewingProcessNotFoundException;

  /**
   * Logs a specific message.
   *
   * @param m Message which should be logged
   * @throws BrewingProcessNotFoundException Thrown if no log-file exists for the current brewing
   *         process
   */
  void log(Message m) throws BrewingProcessNotFoundException;

  /**
   * Starts the logging, creates a file and keeps the new messages in a list.
   *
   * @param brewingProcess current brewingprocess which should be logged
   * @throws BrewingProcessNotFoundException Thrown if brewingProcess is null
   * @throws BrewingProcessException Thrown if another brewing process is currently logging
   */
  void startLog(BrewingProcess brewingProcess) throws BrewingProcessNotFoundException,
      BrewingProcessException;
}
