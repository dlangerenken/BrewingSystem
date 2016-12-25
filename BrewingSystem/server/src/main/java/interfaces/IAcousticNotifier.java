package interfaces;

/**
 * Sends acoustic notifications using the Piezzo that is connected with the
 * Raspberry Pi.
 * 
 * 
 * @author max
 *
 */
public interface IAcousticNotifier {

	/**
	 * Sends two long beeps which indicate a pre-notification.
	 */
	void sendPreNotificationBeep();
	
	/**
	 * Sends a single short beep which indicates a confirmation request.
	 */
	void sendConfirmationRequestBeep();
	
	/**
	 * Ensures that the signaller is switched off.
	 */
	void switchOff();
	
}
