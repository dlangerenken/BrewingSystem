/*
 * 
 */
package interfaces;

import exceptions.ServerAlreadyRunningException;


/**
 * The Interface INetworkService.
 */
public interface INetworkService {
  /**
   * Starts the server and creates the Servlets/Resources.
   * 
   * @throws ServerAlreadyRunningException Cannot start the server when another instance is already
   *         in use of the same port and address
   */
  public void startServer() throws ServerAlreadyRunningException;

  /**
   * Starts the server and creates the Servlets/Resources.
   * 
   * @param url - the address the server should be started on
   * @param port - the port the server should use * @throws ServerAlreadyRunningException Cannot
   *        start the server when another instance is already in use of the same port and address
   */
  public void startServer(String url, int port) throws ServerAlreadyRunningException;

  /**
   * Shutdown the server immediately.
   */
  public void shutdownServer();
}
