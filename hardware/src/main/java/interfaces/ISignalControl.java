/*
 * 
 */
package interfaces;

/**
 * The Interface ISignalController.
 */
public interface ISignalControl extends IRelaisController {
  
	/**
	 * Sends two short beep signals.
	 */
  void sendDoubleBeep();
  
  /**
   * Sends a single short beep signal.
   */
  void sendSingleBeep();
	
}
