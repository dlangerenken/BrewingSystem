package tests;

import impl.ApplicationTest;
import impl.BrewingControllerTest;
import impl.BrewingLoggerTest;
import impl.HopCookerTest;
import impl.MasherTest;
import impl.ProtocolManagementTest;
import impl.RecipeManagementTest;
import impl.StirrerServiceTest;
import impl.TemperatureControllerTest;
import impl.TemperatureLoggerTest;
import impl.UserFacadeTest;
import network.ActuatorResourceTest;
import network.BrewingResourceTest;
import network.InfoResourceTest;
import network.NetworkControllerTest;
import network.ProtocolResourceTest;
import network.PushResourceTest;
import network.PushServiceTest;
import network.RecipeResourceTest;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import persistence.PersistenceHandlerTest;

/**
 * Runs all tests of the project
 */
@RunWith(Categories.class)
@SuiteClasses({ApplicationTest.class, BrewingControllerTest.class, BrewingLoggerTest.class,
     HopCookerTest.class, MasherTest.class, ProtocolManagementTest.class,
    RecipeManagementTest.class, StirrerServiceTest.class, TemperatureControllerTest.class,
    TemperatureLoggerTest.class, UserFacadeTest.class, ActuatorResourceTest.class,
    BrewingResourceTest.class, InfoResourceTest.class, NetworkControllerTest.class,
    ProtocolResourceTest.class, PushResourceTest.class, PushServiceTest.class,
    RecipeResourceTest.class, PersistenceHandlerTest.class})//, BrewingProcessTest.class})
public class AllTestsExecutable {
  /* has to be empty */
}
