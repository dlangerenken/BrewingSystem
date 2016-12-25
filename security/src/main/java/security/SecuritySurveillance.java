/*
 * 
 */
package security;

import interfaces.IHeaterControl;
import interfaces.ISignalControl;
import interfaces.IStirrerControl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;


/**
 * The SecuritySurveillance tries to receive a ping from the address of the brewing server. If no
 * ping is received after a certain time, the system will turn off the heater and stirrer to avoid
 * any damage
 * 
 * @author Daniel Langerenken
 *
 */
public class SecuritySurveillance {

  /** Global instance of the Logger for logging purposes. */
  private static final Logger LOGGER = LogManager.getLogger();

  /** Time the security surveillance should wait until the next ping-request. */
  public static final int TIME_TO_WAIT = 5 * 1000;

  /** Time to answer the ping-request. */
  public static final int TIME_TO_AWAIT_RESPONSE = 1 * 1000;

  /** How often the server is allowed to not answer until the system is shut down. */
  public static final int TIMES_UNTIL_ALERT = 2;

  /** If the security surveillance is interruped. */
  private boolean cancelled = false;

  /** Heater control which should be turned off in case of an emergency. */
  private final IHeaterControl heaterControl;

  /** Stirrer control which should be turned off in case of an emergency. */
  private final IStirrerControl stirrerControl;

  /** Signal control which should be turned on in case of an emergency. */
  private final ISignalControl signalControl;

  /**
   * Instantiates the security surveillance.
   *
   * @param heaterControl injected IHeaterControl-Interface
   * @param stirrerControl injected IStirrerControl-Interface
   */
  @Inject
  public SecuritySurveillance(final IHeaterControl heaterControl,
      final IStirrerControl stirrerControl, final ISignalControl signalControl) {
    this.heaterControl = heaterControl;
    this.stirrerControl = stirrerControl;
    this.signalControl = signalControl;
  }

  /**
   * Gives an answer if the Security Surveillance is still running.
   *
   * @return true, if system is still running, false otherwise
   */
  public boolean isCancelled() {
    return cancelled;
  }

  /**
   * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the
   * response code is in the 200-399 range. source: http
   * ://stackoverflow.com/questions/3584210/preferred-java-way-to-ping-a- http -url-for-availability
   * 
   * @param url The HTTP URL to be pinged.
   * @param timeout The timeout in millis for both the connection timeout and the response read
   *        timeout. Note that the total timeout is effectively two times the given timeout.
   * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD
   *         request within the given timeout, otherwise <code>false</code>.
   */
  public boolean ping(String url, final int timeout) {
    /*
     * Otherwise an exception may be thrown on invalid SSL certificates.
     */
    url = url.replaceFirst("https", "http");

    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setConnectTimeout(timeout);
      connection.setReadTimeout(timeout);
      connection.setRequestMethod("HEAD");
      int responseCode = connection.getResponseCode();
      return (200 <= responseCode && responseCode <= 399);
    } catch (IOException exception) {
      LOGGER.error(exception);
      return false;
    }
  }

  /**
   * Method which checks if the given address is replying. Turns down heater and stirrer in case of
   * no response.
   * 
   * @param address Address which should be "pinged" every time interval
   */
  void runPingLoop(final String address) {
    LOGGER.info("SecuritySurveillance started and checking address: " + address);
    int timesOfNoResponse = 0;
    while (!cancelled) {
      boolean pingResult = ping(address, TIME_TO_AWAIT_RESPONSE);
      if (!pingResult) {
        timesOfNoResponse++;
        LOGGER.error("No ping result within given timespan. Counter: " + timesOfNoResponse);
      } else {
        if (timesOfNoResponse > 0) {
          LOGGER.info("Positive ping result after failures. Counter set to 0.", timesOfNoResponse
              + "");
          timesOfNoResponse = 0;
        } else {
          LOGGER.info("Ping-test successful");
        }
      }
      if (timesOfNoResponse >= TIMES_UNTIL_ALERT) {
        shutdownSystem();
      } else {
        sleep();
      }
    }
  }

  /**
   * This method shuts down heater and stirrer to avoid any damage. It also turns on the
   * signal-controler to emit a signal which can be heard by the user
   */
  void shutdownSystem() {
    LOGGER.error("Shutting down system");
    heaterControl.switchOff();
    stirrerControl.switchOff();
    signalControl.switchOn();
    cancelled = true;
  }

  /**
   * Set the security system to sleep for a certain time. If interrupted, it will turn off the
   * system to avoid any damage
   */
  private void sleep() {
    try {
      Thread.sleep(TIME_TO_WAIT);
    } catch (InterruptedException e) {
      /*
       * Even though this case should not appear, we need to shutdown the system as the security
       * surveillance will terminate here and therefore no ping-test is applied afterwards
       */
      shutdownSystem();
      LOGGER.error(e);
    }
  }

  /**
   * The ping-test runs in a seperate thread as the security surveillance needs to be available by
   * other components (e.g. to ask if the surveillance is still running)
   * 
   * @param address The address of the server to run the ping-test on
   */
  void start(final String address) {
    new Thread() {

      @Override
      public void run() {
        runPingLoop(address);
      }
    }.start();
  }
}
