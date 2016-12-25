/*
 *
 */
package interfaces;

import java.io.File;
import java.util.List;

import exceptions.LogNotFoundException;
import exceptions.LogParseException;
import exceptions.LogSavingException;
import general.BrewingLog;


/**
 * The Interface ILogStorage.
 */
public interface ILogStorage {

  /**
   * Saves a  log, updates log id if it is already taken.
   *
   * @param log the log
   * @return the file
   * @throws LogSavingException the log saving exception
   */
  File saveLog(BrewingLog log) throws LogSavingException;

  /**
   * Returns the given brewing log by id
   *
   * @param id id of the brewing log
   * @return BrewingLog-Class
   * @throws LogNotFoundException log does not exist
   */
  BrewingLog getLogById(int id) throws LogNotFoundException, LogParseException;

  /**
   * Returns a list of logs
   *
   * @return list of logs
   */
  List<BrewingLog> getLogs();

  /** the last time the log files have been modified */
  long getLastLogUpdateTime();
  
  /**returns all ids that are currently used*/
  List<Integer> getUsedIds();

}
