/*
 * 
 */
package impl;

import general.HardwareStatus;
import interfaces.IStirrerControl;
import interfaces.IStirrerService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * This class handles the interaction with the stirrer-controller control.
 *
 * @author Daniel Langerenken
 */
@Singleton
public class StirrerService implements IStirrerService {

  /** Static logger which is used for logging interactions with the stirrer. */
  public static final Logger LOGGER = LogManager.getLogger();

  /** Interface to the hardware-component. */
  private final IStirrerControl stirrerControl;

  /**
   * Initiates the StirrerController.
   *
   * @param stirrerControl hardware-component to interact with
   */
  @Inject
  public StirrerService(final IStirrerControl stirrerControl) {
    this.stirrerControl = stirrerControl;
    LOGGER.info("StirrerController constructed");
  }

  @Override
  public HardwareStatus getStatus() {
    return stirrerControl.isSwitchedOn() ? HardwareStatus.ENABLED : HardwareStatus.DISABLED;
  }

  @Override
  public boolean startStirring() {
    try {
      LOGGER.info("start stirring");
      stirrerControl.switchOn();
    } catch (Exception e) {
      /*
       * As the stirrer does not know, if any exception can be thrown we should catch possible
       * exceptions here (even if catching the generic "exception" is not good style
       */
      LOGGER.error(e);
      return false;
    }
    return stirrerControl.isSwitchedOn();
  }

  @Override
  public boolean stopStirring() {
    try {
      LOGGER.info("stop stirring");
      stirrerControl.switchOff();
    } catch (Exception e) {
      /*
       * As the stirrer does not know, if any exception can be thrown we should catch possible
       * exceptions here (even if catching the generic "exception" is not good style
       */
      LOGGER.error(e);
      return false;
    }
    return !stirrerControl.isSwitchedOn();
  }

}
