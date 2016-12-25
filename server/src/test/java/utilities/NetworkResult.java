package utilities;

/**
 * Network Result for debugging purposes
 * 
 * @author Daniel Langerenken
 *
 */
public class NetworkResult {

  /**
   * Result of the network request
   */
  private final String result;

  /**
   * Status code of the network request
   */
  private final int statusCode;

  /**
   * Instantiates a network result
   * 
   * @param result message which was received
   * @param statusCode statuscode of the http request
   */
  public NetworkResult(final String result, final int statusCode) {
    this.result = result;
    this.statusCode = statusCode;
  }

  /**
   * Returns the given result message
   * 
   * @return http response
   */
  public String getResult() {
    return result;
  }

  /**
   * Returns the given status code
   * 
   * @return status code from the http request
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Whether or not the http request was successful
   * 
   * @return true if sucessful, false otherwise
   */
  public boolean isNegativeResult() {
    return !isPositiveResult();
  }

  /**
   * Whether or not the http request was successful
   * 
   * @return true if sucessful, false otherwise
   */
  public boolean isPositiveResult() {
    return statusCode >= 200 && statusCode <= 300;
  }
}
