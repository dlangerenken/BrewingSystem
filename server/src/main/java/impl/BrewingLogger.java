/*
 * 
 */
package impl;

import general.BrewingLog;
import general.BrewingProcess;
import general.MessagePriority;
import interfaces.IBrewingLogService;
import interfaces.ILogStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import messages.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.LogSavingException;


/**
 * This class logs every brewing-relevant information such as hop cooking steps, temperature levels,
 * ...
 *
 * @author Daniel Langerenken
 *
 */
@Singleton
public class BrewingLogger implements IBrewingLogService {
  /**
   * Logger which is used for normal logging (info, debug values, ...)
   */
  public static final Logger LOGGER = LogManager.getLogger();

  /** Storage where the log files will be redirected to. */
  private final ILogStorage logStorage;

  /** CurrentBrewingProcess for which the log should be used for. */
  private BrewingProcess currentBrewingProcess;

  /** BrewingLog of the current brewing process. */
  private BrewingLog brewingLog;

  /**
   * Creates a BrewingLogger which waits until startLog() is called to initialize its content.
   *
   * @param logStorage the log storage
   */
  @Inject
  public BrewingLogger(final ILogStorage logStorage) {
    this.logStorage = logStorage;
    LOGGER.info("RecipeManagement constructed");
  }


  @Override
  public void finishLog() throws BrewingProcessNotFoundException {
    if (currentBrewingProcess == null || brewingLog == null) {
      throw new BrewingProcessNotFoundException("brewingProcess should not be null");
    }
    saveLog();
    LOGGER.info(String.format("finish log for brewing process %s called",
        currentBrewingProcess.toString()));
    currentBrewingProcess = null;
    brewingLog = null;
  }

  /**
   * Tries to save the log (if it fails -> attempt again)
   */
  private void saveLog() {
    int attempts = 0;
    int maxAttempts = 5;

    boolean successfull = false;
    while (attempts < maxAttempts && !successfull) {
      try {
        logStorage.saveLog(brewingLog);
        successfull = true;
      } catch (LogSavingException e) {
        LOGGER.error("Attempt: " + attempts, e);
        attempts++;
      }
    }
    if (!successfull) {
      LOGGER.error("Saving failed after " + attempts + " attempts");
    }
  }



  @Override
  public Collection<Message> getMessages() throws BrewingProcessNotFoundException {
    if (currentBrewingProcess == null || brewingLog == null) {
      throw new BrewingProcessNotFoundException("brewingProcess should not be null");
    }
    return brewingLog.getMessages();
  }


  @Override
  public Collection<Message> getMessagesByPriority(final MessagePriority priority)
      throws BrewingProcessNotFoundException {
    if (currentBrewingProcess == null || brewingLog == null) {
      throw new BrewingProcessNotFoundException("brewingProcess should not be null");
    }
    List<Message> prioritizedMessages = new ArrayList<>();
    for (Message m : brewingLog.getMessages()) {
      if (m.getPriority().getValue() <= priority.getValue()) {
        prioritizedMessages.add(m);
      }
    }
    return prioritizedMessages;
  }


  @Override
  public void log(final Message m) throws BrewingProcessNotFoundException {
    if (currentBrewingProcess == null || brewingLog == null) {
      throw new BrewingProcessNotFoundException("startLog needs to be called first");
    }
    brewingLog.log(m);
  }


  @Override
  public void startLog(final BrewingProcess brewingProcess) throws BrewingProcessNotFoundException,
      BrewingProcessException {
    if (brewingProcess == null) {
      throw new BrewingProcessNotFoundException("brewingProcess should not be null");
    } else if (currentBrewingProcess != null) {
      throw new BrewingProcessException(
          "Cannot start log while another brewing process is currently logging. Stop logging first before starting a new log");
    }
    LOGGER.info(String.format("startLog for brewing process %s called", brewingProcess.toString()));
    currentBrewingProcess = brewingProcess;
    brewingLog = currentBrewingProcess.getBrewingLog();
  }
}
