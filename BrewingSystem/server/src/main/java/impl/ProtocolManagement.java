package impl;

import exceptions.LogNotFoundException;
import exceptions.LogParseException;
import exceptions.ProtocolNotFoundException;
import exceptions.ProtocolParsingException;
import general.BrewingLog;
import general.LogSummary;
import general.Protocol;
import interfaces.ILogStorage;
import interfaces.IProtocolService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Management of Protocols
 *
 * @author matthias
 *
 */
@Singleton
public class ProtocolManagement implements IProtocolService {

  /** The last time the cache has been refreshed */
  private long lastRefresh = 0;

  /** Cache of summaries of logs */
  private final List<LogSummary> summaryCache = new ArrayList<LogSummary>();

  /** The Constant LOGGER. */
  public static final Logger LOGGER = LogManager.getLogger();

  /**
   * Log-Storage which should be used for receiving finished brewing processes
   */
  private final ILogStorage logStorage;

  /**
   * Instantiates a new protocol management object
   */
  @Inject
  public ProtocolManagement(final ILogStorage logStorage) {
    LOGGER.info("ProtocolManagement constructed");
    this.logStorage = logStorage;
  }

  /**
   * gets the protocol for the brewing log with the given id
   *
   * @throws ProtocolParsingException
   */
  @Override
  public Protocol getProtocolContent(final int id) throws ProtocolNotFoundException,
  ProtocolParsingException {
    try {
      BrewingLog log = logStorage.getLogById(id);
      return log.getProtocol();
    } catch (LogNotFoundException e) {
      LOGGER.error(e);
      throw new ProtocolNotFoundException(e);
    } catch (LogParseException e) {
      LOGGER.error(e);
      throw new ProtocolParsingException(e);
    }
  }

  /** updates the cache of log summaries */
  private void updateCache() {
    List<BrewingLog> logs = logStorage.getLogs();
    summaryCache.clear();
    for (BrewingLog log : logs) {
      LogSummary summary = log.getSummary();
      summaryCache.add(summary);
    }
    lastRefresh = (new Date()).getTime();
  }

  /**
   * Gets a list of all logSummaries
   */
  @Override
  public List<LogSummary> getProtocolIndex() {
    if (lastRefresh < logStorage.getLastLogUpdateTime()) {
      updateCache();
    }

    return summaryCache;
  }

  /** time of last cache refresh */
  public long getLastRefresh() {
    return lastRefresh;
  }

}
