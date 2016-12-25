package utilities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class gives the possibility to send get and post requests which can be used for testing
 * 
 * @author Daniel Langerenken
 *
 */
public class NetworkRequestHelper {

  /**
   * Address which is going to be called
   */
  public static final String ADDRESS = "0.0.0.0";

  /**
   * Careful, we NEED TO start the server on a DIFFERENT port as they "main"-server could run and
   * therefore result in a conflict
   */
  public static final int PORT = 1338;

  /**
   * Full address of the server
   */
  public static final String SERVER_ADDRESS = String.format("http://%s:%d/", ADDRESS, PORT);

  /**
   * Pretend to be a mozilla/5.0 - client
   */
  private static final String USER_AGENT = "Mozilla/5.0";

  /**
   * Sends a get request based on
   * http://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
   * 
   * @throws Exception HttpRequest-Exception which can occur
   */
  public static NetworkResult sendGet(final String url) throws Exception {
    try {
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      // optional default is GET
      con.setRequestMethod("GET");
      con.setRequestProperty("Accept-Charset", "UTF-8");

      // add request header
      con.setRequestProperty("User-Agent", USER_AGENT);
      int responseCode = con.getResponseCode();
      InputStream stream = getStreamByConnection(con);

      BufferedReader in = new BufferedReader(new InputStreamReader(stream));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      return new NetworkResult(response.toString(), responseCode);
    } catch (IOException e) {
      /*
       * Ignore this exception as it occurs even if the request was succesfull
       */
    }
    return null;
  }

  /**
   * Returns the inputstream or errorstream via the http-connection
   * 
   * @param con connection which is analyzed
   * @return inputstream or errorstream
   * @throws IOException exception which can occur in the network-task
   */
  private static InputStream getStreamByConnection(final HttpURLConnection con) throws IOException {
    InputStream stream = null;
    int responseCode = con.getResponseCode();
    if (responseCode > 399) {
      stream = con.getErrorStream();
    } else {
      stream = con.getInputStream();
    }
    return stream;
  }

  /**
   * Sends a post request based on
   * http://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
   * 
   * @throws Exception HttpRequest-Exception which can occur
   */
  public static NetworkResult sendPost(final String url, final String params) throws Exception {
    try {
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      // add reuqest header
      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", USER_AGENT);

      // Send post request
      con.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.writeBytes(params);
      wr.flush();
      wr.close();
      int responseCode = con.getResponseCode();
      InputStream stream = getStreamByConnection(con);
      BufferedReader in = new BufferedReader(new InputStreamReader(stream));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      return new NetworkResult(response.toString(), responseCode);
    } catch (IOException e) {
      /*
       * Ignore this exception as it occurs even if the request was succesfull
       */
    }
    return null;
  }

}
