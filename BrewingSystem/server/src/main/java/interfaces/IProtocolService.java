/*
 *
 */
package interfaces;

import java.util.List;

import exceptions.LogNotFoundException;
import exceptions.ProtocolNotFoundException;
import exceptions.ProtocolParsingException;
import general.LogSummary;
import general.Protocol;


/**
 * The Interface IProtocolService.
 */
public interface IProtocolService {

  /**
   * get protocol data with specified id.
   *
   * @param id ID of the protocol to get
   * @return the complete protocol
   * @throws ProtocolNotFoundException the protocol not found exception
   * @throws LogNotFoundException
   * @throws ProtocolParsingException
   */
  Protocol getProtocolContent(final int id) throws ProtocolNotFoundException,
  ProtocolParsingException;

  /**
   * Get Index of Protocols for user selection.
   *
   * @return A Summary of every stored protocol
   */
  List<LogSummary> getProtocolIndex();
}
