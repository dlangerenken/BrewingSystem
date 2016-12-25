/*
 * 
 */
package network;

import general.BrewingProcess;
import general.BrewingProcessSummary;
import general.BrewingState;
import gson.Serializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import messages.Message;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import dispatcher.Context;
import dispatcher.HttpError;
import dispatcher.Result;
import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;


/**
 * This Resource deals with everything about the brewing process (e.g. show current brewing status,
 * show current step, show temperature)
 * 
 * @author Daniel Langerenken
 *
 */
public class BrewingResource extends BaseResource {

  /**
   * Initiates the brewing resource.
   *
   * @param context Context of the Servlet
   */
  public BrewingResource(final Context context) {
    super(context);
  }

  /**
   * This method is used for confirming a single brewing step which was executed manually or which
   * was required to get necessary information.
   *
   * @param brewingString the brewing state which is confirmed (can include data)
   * @throws HttpError thrown if the brewing process is not available
   */
  public Result confirmStep(final String brewingString) throws HttpError {
    if (brewingString == null) {
      returnBadRequestException(null);
    }
    try {
      BrewingState brewingState =
          Serializer.getInstance().fromJson(brewingString, BrewingState.class);
      getUserFacade().confirmStep(brewingState);
      brewingState = getUserFacade().getCurrentBrewingState();
      synchronized (brewingState) {
        return new JsonResult<BrewingState>(brewingState, BrewingState.class);
      }
    } catch (BrewingProcessNotFoundException e) {
      returnBadRequestException(e);
    } catch (InvalidBrewingStepException ibe) {
      returnBadRequestException(ibe);
    } catch (JsonSyntaxException jse) {
      returnBadRequestException(jse);
    }
    return null;
  }

  /**
   * Show current summary of the brewing process. If no current brewing process is available, a bad
   * request exception is thrown.
   * 
   * @return current brewing state
   * @throws HttpError the http error
   */
  public Result summary() throws HttpError {
    try {
      BrewingProcessSummary summary = getUserFacade().getCurrentBrewingProcessSummary();
      synchronized (summary) {
        return new JsonResult<BrewingProcessSummary>(summary, BrewingProcessSummary.class);
      }
    } catch (BrewingProcessNotFoundException e) {
      returnBadRequestException(e);
    }
    return null;
  }

  /**
   * Show current state information about the brewing process. If no current brewing process is
   * available, a bad request exception is thrown.
   * 
   * @return current brewing state
   * @throws HttpError the http error
   */
  public Result state() throws HttpError {
    try {
      BrewingState state = getUserFacade().getCurrentBrewingState();
      synchronized (state) {
        return new JsonResult<>(state, BrewingState.class);
      }
    } catch (BrewingProcessNotFoundException e) {
      returnBadRequestException(e);
    }
    return null;
  }

  /**
   * Cancels a current running brewing process
   * 
   * @return current state (should be aborted)
   * @throws HttpError throws error, if for some reason the brewing process could not be interrupted
   */
  public Result cancel() throws HttpError {
    try {
      getUserFacade().cancelCurrentBrewingProcess();
      BrewingState state = getUserFacade().getCurrentBrewingState();
      synchronized (state) {
        return new JsonResult<>(state, BrewingState.class);
      }
    } catch (BrewingProcessNotFoundException e) {
      returnBadRequestException(e);
    }
    return null;
  }

  /**
   * Shows basic information about the brewing process.
   *
   * @return basic information about the brewing
   * @throws HttpError the http error
   */
  public Result info() throws HttpError {
    BrewingProcess bp = getUserFacade().getCurrentBrewingProcess();
    if (bp == null) {

      /*
       * throws an exception if now brewing process is ongoing
       */
      returnServerException(new BrewingProcessNotFoundException("No BrewingProcess ongoing"));
    } else {
      synchronized (bp) {
        return new JsonResult<BrewingProcess>(bp, BrewingProcess.class);
      }
    }
    return null;
  }

  /**
   * User gives the result for the iodineTest.
   *
   * @param duration time to wait untilo the next iodine test should be executed
   * @throws HttpError thrown if no parameter was given
   */
  public Result iodineTest(Integer duration) throws HttpError {
    if (duration == null) {
      duration = 0;
    }
    try {
      getUserFacade().confirmDoIodineTest(duration);
      BrewingState state = getUserFacade().getCurrentBrewingState();
      synchronized (state) {
        return new JsonResult<>(state, BrewingState.class);
      }
    } catch (BrewingProcessNotFoundException e) {
      returnBadRequestException(e);
    } catch (BrewingProcessException e) {
      returnBadRequestException(e);
    }
    return null;
  }

  /**
   * /** This method is used for starting a brewing process.
   *
   * @param recipeId id of the recipe
   * @throws HttpError thrown if the brewing process is not available
   * @throws RecipeNotFoundException if recipe not valid
   * @throws BrewingProcessException if brewing process is already started
   */
  public Result startBrewing(final String recipeId) throws HttpError {
    if (recipeId == null) {
      returnBadRequestException(new BrewingProcessException());
    }
    try {
      getUserFacade().startBrewing(recipeId);
      BrewingState state = getUserFacade().getCurrentBrewingState();
      synchronized (state) {
        return new JsonResult<>(state, BrewingState.class);
      }
    } catch (BrewingProcessNotFoundException e) {
      returnBadRequestException(e);
    } catch (RecipeParseException e) {
      returnServerException(e);
    } catch (RecipeNotFoundException e) {
      returnBadRequestException(e);
    } catch (BrewingProcessException e) {
      returnServerException(e);
    }
    return null;
  }

  /**
   * Returns a list of messages which are newer than a given date
   * 
   * @param since timestamp which is used for receiving only newer messages
   * @return List of Messages which are newer than the given time stamp
   */
  public Result messageHistory(final Long since) throws HttpError {
    List<Message> messagesSince;
    try {
      messagesSince = getUserFacade().getCurrentMessagesSince(since);
      synchronized (messagesSince) {
        Type listType = new TypeToken<List<Message>>() {
          /* Needs to be an empty block */
        }.getType();
        JsonResult<List<Message>> result = new JsonResult<List<Message>>(messagesSince, listType);
        return result;
      }

    } catch (BrewingProcessNotFoundException e) {
      returnBadRequestException(e);
    }
    return null;
  }

  /**
   * Returns a map of temperatures which are newer than a given date
   * 
   * @param since timestamp which is used for receiving only newer temperatures
   * @return Map of temperatures which are newer than the given time stamp
   */
  public Result temperatureHistory(final Long since) throws HttpError {
    Map<Long, Float> temperaturesSince;
    try {
      temperaturesSince = getUserFacade().getTemperaturesSince(since);
      synchronized (temperaturesSince) {
        Type mapType = new TypeToken<Map<Long, Float>>() {
          /* Needs to be an empty block */
        }.getType();
        JsonResult<Map<Long, Float>> result =
            new JsonResult<Map<Long, Float>>(temperaturesSince, mapType);
        return result;
      }
    } catch (BrewingProcessNotFoundException e) {
      returnBadRequestException(e);
    }
    return null;
  }

  /**
   * Returns the latest push messages of the current brewing process
   * 
   * @return list of push messages of brewing process
   * @throws HttpError
   */
  public Result pushMessages() throws HttpError {
    try {
      List<Message> pushMessages = getUserFacade().getPushMessages();
      Type type = new TypeToken<List<Message>>() {
        /* Needs to be an empty block */
      }.getType();
      synchronized (pushMessages) {
        return new JsonResult<List<Message>>(pushMessages, type);
      }

    } catch (BrewingProcessNotFoundException e) {
      returnBadRequestException(e);
    }
    return null;
  }

}
