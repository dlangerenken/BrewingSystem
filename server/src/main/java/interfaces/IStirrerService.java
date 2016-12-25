/*
 * 
 */
package interfaces;

import general.HardwareStatus;


/**
 * Wrapper for IStirrerController to start/stop stirring.
 *
 * @author Daniel Langerenken
 */
public interface IStirrerService {
  
  /**
   * whether the stirrer is enabled/disabled or not connected.
   *
   * @return enabled/disabled/not connected
   */
  HardwareStatus getStatus();

  /**
   * Starts stirring.
   *
   * @return true, if stirring is started, false if any exception was thrown or stirrer not enabled
   */
  boolean startStirring();

  /**
   * stops stirring.
   *
   * @return true, if stirring is stopped, false if any exception was thrown or stirrer still
   *         enabled
   */
  boolean stopStirring();
}
