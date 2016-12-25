/*
 * 
 */
package impl;

import general.BrewingState;
import general.BrewingState.Position;
import general.BrewingState.State;
import general.BrewingState.Type;
import general.HopAddition;
import general.Recipe;
import interfaces.IBrewingController;
import interfaces.ITemperatureService;
import interfaces.IUserFacadeService;

import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import mocks.MockTemperatureController;
import mocks.MockUserFacadeWithAutoConfirm;
import modules.BrewingControllerTestModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import categories.UnitTest;

import utilities.UserFacadeProvider;
import utilities.DummyBuilder;

import com.google.inject.Guice;

import exceptions.BrewingProcessException;

/**
 * Tests the HopCooker with some fakes and so, you know... ;)
 * 
 * @author Max
 */
@RunWith(MockitoJUnitRunner.class)
public class HopCookerTest {

  UserFacadeProvider userFacadeProvider;

  @Before
  public void beforeClass() throws Exception {
    Application.setInjector(Guice.createInjector(new BrewingControllerTestModule()), true);
    userFacadeProvider = Application.get(UserFacadeProvider.class);
    userFacadeProvider.setUserFacade(Mockito.mock(IUserFacadeService.class));
  }

  /**
   * Skip all requests that do not belong to the hop cooking process and return the
   * first state associated with hop cooking.
   * @param iterator
   */
  BrewingState skipNonHopCookingRequests(final Iterator<BrewingState> iterator) {
  	BrewingState state = null;
    // Alle Stati vor Beginn des Hopfenkochens sind in diesem Test egal.
    while (iterator.hasNext()) {
      state = iterator.next();
      if (state.getState() == State.HOP_COOKING && state.getPosition() == Position.START) {
        break;
      }
    }
    return state;
  }

  /**
   * Performs the Test with a demo recipe.
   * 
   * @throws BrewingProcessException
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipe() throws BrewingProcessException {
    ITemperatureService temperatureService = Application.get(ITemperatureService.class);
    ((MockTemperatureController) temperatureService).setInstantHeatUp(true);
    IBrewingController brewingController =
        BrewingControllerTest.createBrewingController(temperatureService);

    MockUserFacadeWithAutoConfirm userFacadeService =
        new MockUserFacadeWithAutoConfirm(brewingController, new BrewingState(Type.REQUEST,
            State.WHIRLPOOL, Position.END), 0);

    userFacadeProvider.setUserFacade(userFacadeService);

    Recipe usedRecipe = DummyBuilder.createValidTestRecipe();
    brewingController.startBrewing(usedRecipe);
    try {
      Thread.sleep(20 * 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(
          "Test failed: Could not sleep to wait for internal Threads to finish.");
    }
    BrewingState currentBrewingState = brewingController.getCurrentBrewingProcess().getState();
    System.out.println("Current state: " + currentBrewingState.toString());
    Assert.assertTrue(currentBrewingState.equals(Type.REQUEST, State.WHIRLPOOL, Position.END));
    validateRequests(usedRecipe, userFacadeService.getRequestedBrewingStates());
  }
  
  /**
   * Checks that the requests that were sent by the Hop Cooker correspond to the HopAdditions
   * in the recipe.
   * @param recipe The recipe to be checked against.
   * @param requests The requests that were sent by the Hop cooker.
   */
  @SuppressWarnings("unchecked")
  private void validateRequests(final Recipe recipe, final List<BrewingState> requests) {
    Iterator<BrewingState> iterator = requests.iterator();
    BrewingState state = null;

    state = skipNonHopCookingRequests(iterator);

    // Prüfen, ob Beginn des Hopfenkochens kommt.
    Assert.assertTrue(new BrewingState(Type.REQUEST, State.HOP_COOKING, Position.START)
        .equals(state));

    Iterator<HopAddition> haIterator = recipe.getHopCookingPlan().getHopAdditions().iterator();
    while (iterator.hasNext()) {
      state = iterator.next();

      if (state.getState() == State.HOP_COOKING && state.getPosition() == Position.ADDING) {

        List<HopAddition> confirmedHAs = null;
        try {
          confirmedHAs = (List<HopAddition>) state.getData();
        } catch (Throwable t) {
          confirmedHAs = null;
        }

        if (confirmedHAs == null) {
          Assert.fail("Confirmation state does not contain a List<HopAddition>!");
        }

        for (HopAddition confirmedHA : confirmedHAs) {
          HopAddition recipeHA = null;

          if (haIterator.hasNext()) {
            recipeHA = haIterator.next();
          } else {
            break;
          }

          // Die bestätigte Hopfengabe muss der des Rezepts entsprechen.
          Assert.assertEquals(true, confirmedHA.equals(recipeHA));
          System.out.println("HopAddition: " + confirmedHA.getName());
        }
        if (!haIterator.hasNext()) {
          System.out.println("--------");
          // Keine Hopfengaben mehr, dann sofort RAUS!
          // --> Nächster Status, weil hier haben wir ihn ja schon konsumiert, im Gegensatz zu
          // unten.
          state = iterator.next();
          break;
        }
      } else {
        break;
      }
    }

    // Keine Hopfengabe vergessen.
    Assert.assertFalse(haIterator.hasNext());

    // Status Whirlpool.
    Assert.assertTrue(new BrewingState(Type.REQUEST, State.WHIRLPOOL, Position.END).equals(state));
  }
}