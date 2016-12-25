package interfaces;

/**
 * Used during a brewing process to write temperature changes reported by the ITemperatureController
 * to the brewing log.
 * 
 * @author max
 *
 */
public interface ITemperatureLogger {

  /**
   * Subscribes to the supplied TemperatureController and writes TemperatureMessages to the supplied
   * BrewingLogService.
   * 
   * @param tc
   */
  void subscribeToTemperatureController(ITemperatureService tc);

  /**
   * Unsubscribes from the TemperatureController that was supplied to
   * subscribeToTemperatureController.
   */
  void unsubscribe();
}
