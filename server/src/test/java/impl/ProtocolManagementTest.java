/*
 *
 */
package impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import exceptions.LogNotFoundException;
import exceptions.LogParseException;
import exceptions.LogSavingException;
import exceptions.ProtocolNotFoundException;
import exceptions.ProtocolParsingException;
import general.BrewingLog;
import general.LogSummary;
import general.Protocol;
import interfaces.ILogStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import persistence.PersistenceHandler;
import utilities.DummyBuilder;
import categories.IntegrationTest;
import categories.UnitTest;


/**
 * This class tests protocol management class and its interaction with the log storage
 *
 * @author matthias
 */
@RunWith(MockitoJUnitRunner.class)
public class ProtocolManagementTest {

  /** the log storage */
  private ILogStorage logStorage;

  /** The protocol service. */
  private ProtocolManagement protocolManagement;


  /**
   * Tests whether cache gets refreshed if there has been an update since last refresh and doesn't
   * otherwise.
   */
  @Category(UnitTest.class)
  @Test
  public void testCacheUpdate() {
    logStorage = mock(ILogStorage.class);

    // cache is updated if updateTime > refreshTime
    List<BrewingLog> logs = new ArrayList<BrewingLog>();
    logs.add(DummyBuilder.getBrewingLog());
    when(logStorage.getLastLogUpdateTime()).thenReturn(Long.MAX_VALUE);
    when(logStorage.getLogs()).thenReturn(logs);
    protocolManagement = new ProtocolManagement(logStorage);
    long oldRefresh = protocolManagement.getLastRefresh();
    Assert.assertTrue(protocolManagement.getLastRefresh() < logStorage.getLastLogUpdateTime());
    protocolManagement.getProtocolIndex();
    Assert.assertTrue(protocolManagement.getLastRefresh() >= oldRefresh);

    // cache isn't updated if updatetime <= refreshtime
    when(logStorage.getLastLogUpdateTime()).thenReturn(Long.MIN_VALUE);
    protocolManagement = new ProtocolManagement(logStorage);
    oldRefresh = protocolManagement.getLastRefresh();
    Assert.assertTrue(protocolManagement.getLastRefresh() <= logStorage.getLastLogUpdateTime());
    protocolManagement.getProtocolIndex();
    Assert.assertTrue(protocolManagement.getLastRefresh() == oldRefresh);

  }

  /**
   * tests that getProtocolContent(id) does return a non-null protocol
   */
  @Category(UnitTest.class)
  @Test
  public void testProtocolContent() throws LogNotFoundException, LogParseException,
      ProtocolNotFoundException, ProtocolParsingException {
    logStorage = mock(ILogStorage.class);
    when(logStorage.getLogById(Mockito.anyInt())).thenReturn(DummyBuilder.getBrewingLog());
    protocolManagement = new ProtocolManagement(logStorage);
    Protocol protocol = protocolManagement.getProtocolContent(1);
    Assert.assertNotNull(protocol);
  }

  /**
   * tests that getProtocolIndex() returns as many non-null summaries as there are logs
   */
  @Category(UnitTest.class)
  @Test
  public void testProtocolIndex() {
    logStorage = mock(ILogStorage.class);
    List<BrewingLog> logs = new ArrayList<BrewingLog>();
    for (int i = 0; i < 10; i++) {
      logs.add(DummyBuilder.getBrewingLog());
    }
    when(logStorage.getLogs()).thenReturn(logs);
    when(logStorage.getLastLogUpdateTime()).thenReturn(Long.MAX_VALUE);
    protocolManagement = new ProtocolManagement(logStorage);
    List<LogSummary> index = protocolManagement.getProtocolIndex();
    System.out.println(index.size());
    Assert.assertTrue(index.size() == 10);
    for (LogSummary summary : index) {
      Assert.assertNotNull(summary);
    }
  }

  /**
   * Tests that cache is refreshed on first run, and again after the PersistenceHandler saved a new log.
 * @throws LogSavingException 
   */
  @Category(IntegrationTest.class)
  @Test
  public void testInitialCacheRefresh() throws LogSavingException {
    PersistenceHandler ph = new PersistenceHandler();
    ProtocolManagement pm = new ProtocolManagement(ph);
    Assert.assertTrue(ph.getLastLogUpdateTime() > pm.getLastRefresh());
    long refresh = pm.getLastRefresh();
    int protocols = pm.getProtocolIndex().size();
    Assert.assertTrue(pm.getLastRefresh() > refresh);
   
    File file = ph.saveLog(DummyBuilder.getBrewingLog());
    Assert.assertTrue(ph.getLastLogUpdateTime() >= pm.getLastRefresh());
    Assert.assertTrue(protocols + 1 == pm.getProtocolIndex().size());
    Assert.assertTrue(ph.getLastLogUpdateTime() < pm.getLastRefresh());
    Assert.assertTrue(file.delete());
    
   
  }

}
