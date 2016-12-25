/*
 *
 */
package utilities;

import general.ActuatorDetails;
import general.BrewingLog;
import general.BrewingProcess;
import general.BrewingState;
import general.BrewingState.Position;
import general.BrewingState.State;
import general.BrewingState.Type;
import general.HardwareStatus;
import general.HopAddition;
import general.HopCookingPlan;
import general.IodineTest;
import general.LogSummary;
import general.MaltAddition;
import general.ManualStep;
import general.MashingPlan;
import general.Protocol;
import general.Recipe;
import general.RecipeSummary;
import general.TemperatureLevel;
import general.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import messages.ConfirmationRequestMessage;
import messages.HopAdditionMessage;
import messages.IodineTestMessage;
import messages.MaltAdditionMessage;
import messages.ManualStepMessage;
import messages.MashingMessage;
import messages.Message;
import messages.TemperatureLevelMessage;
import messages.TemperatureMessage;


/**
 * This class provides dummy objects to test the gui without having real data.
 */
public class DummyBuilder {

  /** log id counter */
  private static int logIdCounter = 0;

  /**
   * Helper class to provide dates for testing purposes.
   *
   * @param days days which should be added on today
   * @param hours hours which should be added on today
   * @param minutes minutes which should be added on today
   * @param seconds seconds which should be added on today
   * @param milliseconds milliseconds which should be added on today
   * @return new date with according additions of days,hours,minutes,seconds and milliseconds
   */
  public static long addTimeToNow(final int days, final int hours, final int minutes,
      final int seconds, final int milliseconds) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DATE, days);
    cal.add(Calendar.HOUR, hours);
    cal.add(Calendar.MINUTE, minutes);
    cal.add(Calendar.SECOND, seconds);
    cal.add(Calendar.MILLISECOND, milliseconds);
    return cal.getTimeInMillis();
  }

  /**
   * Returns Actuator-Details for the brewing.
   *
   * @return random attributes of temperature/heater/stirrer hardware
   */
  public static ActuatorDetails getActuatorDetails() {
    ActuatorDetails details = new ActuatorDetails();
    details
        .setHeaterStatus(HardwareStatus.values()[randInt(0, HardwareStatus.values().length - 1)]);
    details
        .setStirrerStatus(HardwareStatus.values()[randInt(0, HardwareStatus.values().length - 1)]);
    details.setTemperatureSensorStatus(HardwareStatus.values()[randInt(0,
        HardwareStatus.values().length - 1)]);
    details.setTemperature(randInt(0, 100) + 0f);
    return details;
  }

  /**
   * Creates a brewing log.
   *
   * @return random list of brewing logs
   */
  public static BrewingLog getBrewingLog() {
    BrewingLog brewingLog = new BrewingLog(getRealisticRecipe(), logIdCounter++);
    brewingLog.setMessages(getMessages());
    return brewingLog;
  }

  /**
   * Creates a brewing process.
   *
   * @return random brewing process
   */
  public static BrewingProcess getBrewingProcess() {
    BrewingProcess process = new BrewingProcess(getRealisticRecipe(), logIdCounter++);
    process.start();
    process.setState(getBrewingState());
    BrewingLog log = getBrewingLog();
    process.getBrewingLog().setMessages(log.getMessages());
    process.finish();
    List<IodineTest> iodines = getIodineTests();
    process.getIodineTests().addAll(iodines);
    return process;
  }

  /**
   * Gets the brewing state.
   *
   * @return the brewing state
   */
  public static BrewingState getBrewingState() {
    return new BrewingState(Type.REQUEST, State.MASHING, Position.START);
  }

  /**
   * Gets the confirmation request message.
   *
   * @return the confirmation request message
   */
  public static ConfirmationRequestMessage getConfirmationRequestMessage() {
    BrewingState state = null;
    int counter = 0;
    while (state == null || state.getType() != Type.REQUEST && counter < 50) {
      state = getBrewingState();
      counter++;
    }
    ConfirmationRequestMessage message = new ConfirmationRequestMessage(state);
    setBasicMessageDetails(message);
    return message;
  }

  /**
   * Creates a hop-addition.
   *
   * @return random hop addition
   */
  public static HopAddition getHopAddition() {
    HopAddition addition =
        new HopAddition(randInt(10, 100), Unit.g, "MyHop(e)", new Date().getTime());
    return addition;
  }

  /**
   * Creates a list of hop additions.
   *
   * @return random list of hop additions
   */
  public static List<HopAddition> getHopAdditions() {
    List<HopAddition> additions = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      additions.add(getHopAddition());
    }
    return additions;
  }

  /**
   * Creates a random hop-cooking-message.
   *
   * @return hop-cooking-message with a random hop-addition
   */
  public static Message getHopCookingMessage() {
    HopAdditionMessage message = new HopAdditionMessage(null);
    message.setHopAddition(getHopAdditions());
    setBasicMessageDetails(message);
    return message;
  }

  /**
   * Creates a hop cooking plan.
   *
   * @return random hop cooking plan
   */
  public static HopCookingPlan getHopCookingPlan() {
    HopCookingPlan plan = new HopCookingPlan();
    plan.setHopAdditions(getHopAdditions());
    return plan;
  }

  /**
   * Creates a random ingredient-addition-message.
   *
   * @return Ingredient-addition message with a random malt-addition
   */
  public static Message getIngredientAdditionMessage() {
    MaltAdditionMessage message = new MaltAdditionMessage(Arrays.asList(getMaltAddition()));
    setBasicMessageDetails(message);
    return message;
  }

  /**
   * Creates a iodine-test.
   *
   * @param result if the iodine test should be negative or positive
   * @return random iodine-test
   */
  public static IodineTest getIodineTest(final boolean result) {
    IodineTest test = new IodineTest(result ? 0 : 60 * randInt(5, 15));
    return test;
  }

  /**
   * Creates a random iodine-test-message.
   *
   * @return Iodine-test-message with a positive iodine-test result
   */
  public static Message getIodineTestMessage() {
    IodineTestMessage message = new IodineTestMessage(getIodineTest(true));
    setBasicMessageDetails(message);
    return message;
  }

  /**
   * List of iodine tests.
   *
   * @return random list of iodine tests (last iodine test is positive)
   */
  public static List<IodineTest> getIodineTests() {
    List<IodineTest> tests = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      tests.add(getIodineTest(i < 9));
    }
    return tests;
  }

  /**
   * Creates a malt-addition.
   *
   * @return random malt-addition
   */
  public static MaltAddition getMaltAddition() {
    MaltAddition addition =
        new MaltAddition(randInt(10, 100), Unit.g, "MyMalt", new Date().getTime());
    return addition;
  }

  /**
   * creates a list of malt additions.
   *
   * @return random list of malt additions
   */
  public static List<MaltAddition> getMaltAdditions() {
    List<MaltAddition> additions = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      additions.add(getMaltAddition());
    }
    return additions;
  }

  /**
   * Creates a manual step with the current timestamp
   *
   * @return
   */
  public static ManualStep getManualStep() {
    ManualStep step = new ManualStep();
    step.setDescription("This is the manual Step");
    step.setStartTime(new Date().getTime());
    step.setDuration(2000);
    return step;
  }

  /**
   * Creates a random manual-step-message.
   *
   * @return Manual-step message with specific manual step
   */
  public static Message getManualStepMessage() {
    ManualStepMessage message = new ManualStepMessage(null);
    message.setManualStep(getManualStep());
    setBasicMessageDetails(message);
    return message;
  }

  /**
   * Creates a random mashing-message.
   *
   * @return Mashing-message
   */
  private static Message getMashingMessage() {
    MashingMessage message = new MashingMessage();
    setBasicMessageDetails(message);
    return message;
  }

  /**
   * Creates a mashing plan.
   *
   * @return random mashing plan
   */
  public static MashingPlan getMashingPlan() {
    MashingPlan plan = new MashingPlan();
    plan.setTemperatureLevels(getTemperatureLevels());
    plan.setMaltAdditions(getMaltAdditions());
    return plan;
  }

  /**
   * Returns a random message.
   *
   * @return random message
   */
  public static Message getMessage() {
    return getMessage(randInt(0, 7));
  }

  /**
   * Creates a message with given message-type.
   *
   * @param messageType e.g. 0 (temperature-level), ...
   * @return created message with data
   */
  public static Message getMessage(final int messageType) {
    switch (messageType) {
      case 0:
        return getTemperatureLevelMessage();
      case 1:
        return getIodineTestMessage();
      case 2:
        return getIngredientAdditionMessage();
      case 3:
        return getMashingMessage();
      case 4:
        return getHopCookingMessage();
      case 5:
        return getManualStepMessage();
      case 6:
        return getTemperatureMessage();
      case 7:
        return getConfirmationRequestMessage();
      default:
        Message message = new Message();
        setBasicMessageDetails(message);
        return message;
    }
  }

  /**
   * Creates a list of messages.
   *
   * @return random message types + messages
   */
  public static List<Message> getMessages() {
    List<Message> messages = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      messages.add(getMessage(randInt(0, 8)));
    }
    /*
     * At least 5 temperature-messages
     */
    for (int i = 0; i < 5; i++) {
      messages.add(getMessage(6));
    }
    /*
     * At a confirmation-expected message to the end
     */
    Message confirmationExpected = getConfirmationRequestMessage();
    confirmationExpected.setTime(new Date().getTime());
    messages.add(confirmationExpected);

    return messages;
  }

  /**
   * Creates a random protocol.
   *
   * @return Random generated protocol
   */
  public static Protocol getProtocol() {
    Protocol protocol = new Protocol(getBrewingLog());
    return protocol;
  }

  /**
   * Creates a list of protocol-summaries.
   *
   * @return random generated list of protocol-summaries
   */
  public static List<LogSummary> getProtocols() {
    List<LogSummary> protocols = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      LogSummary protocol =
          new LogSummary("Title " + i, "Description " + i, addTimeToNow(-i, 0, 0, 0, 0), i, "42");
      protocols.add(protocol);
    }
    return protocols;
  }

  /**
   * Creates a random recipe.
   *
   * @return Random generated Recipe
   */
  public static Recipe getRecipe() {
    Recipe recipe = new Recipe();
    recipe.setDescription("This is the recipe");
    recipe.setName("My Recipe");
    recipe.setId("id");
    recipe.setHopCookingPlan(getHopCookingPlan());
    recipe.setMashingPlan(getMashingPlan());
    return recipe;
  }

  /** Returns a HopCookingPlan for Weizenbock */
  public static HopCookingPlan getRealisticHopCookingPlan() {
    HopCookingPlan hop = new HopCookingPlan();
    hop.setDuration(4800000); // 80min
    List<HopAddition> hopAdditions = new ArrayList<HopAddition>();
    HopAddition perle = new HopAddition(15, Unit.g, "Perle", 300000);
    hopAdditions.add(perle);

    HopAddition hallertau = new HopAddition(12, Unit.g, "Hallertauer Mittelfrüh", 4500000);
    hopAdditions.add(hallertau);

    hop.setHopAdditions(hopAdditions);
    return hop;
  }

  /** Returns a MashingPlan for Weizenbock */
  public static MashingPlan getRealisticMashingPlan() {
    MashingPlan mashingPlan = new MashingPlan();
    List<MaltAddition> maltAdditions = new ArrayList<MaltAddition>();

    MaltAddition weizen = new MaltAddition(3750, Unit.g, "Weizen Hell", 0);
    maltAdditions.add(weizen);

    MaltAddition pils = new MaltAddition(2000, Unit.g, "Pilsener Malz", 0);
    maltAdditions.add(pils);

    MaltAddition hell = new MaltAddition(1500, Unit.g, "CaraHell", 0);
    maltAdditions.add(hell);

    MaltAddition red = new MaltAddition(250, Unit.g, "CaraRed", 0);
    maltAdditions.add(red);

    mashingPlan.setMaltAdditions(maltAdditions);
    List<TemperatureLevel> temperatureLevels = new ArrayList<TemperatureLevel>();
    TemperatureLevel eiweiss = new TemperatureLevel(600000, 0, 55);
    temperatureLevels.add(eiweiss);

    TemperatureLevel maltose = new TemperatureLevel(1800000, 600000, 63);
    temperatureLevels.add(maltose);

    TemperatureLevel zucker = new TemperatureLevel(1500000, 600000 + 1800000, 72);
    temperatureLevels.add(zucker);
    mashingPlan.setTemperatureLevels(temperatureLevels);
    return mashingPlan;
  }

  /**
   * creates an actual recipe for wheat bock beer.
   *
   * @return the created recipe object
   */
  public static Recipe getRealisticRecipe() {
    Recipe recipe =
        new Recipe(
            "weizenbock",
            "Heller Weissbier-Bock / Hefeweizen-Bock",
            "Ein charaktervoller Weizenbock der Extraklasse. "
                + "Die Farbe erinnert an leuchtend helles Bernstein. "
                + "Fein schimmernde Hefetrübung und sehr stabiler, feinporiger und kräftiger Schaum. "
                + "Interessantes Aromenspiel, vordergründig nach Honigmelone, "
                + "begleitet von Banane, Mango und Ananas. "
                + "Sehr spritzig, ausgeprägter und runder Körper, sehr sämig im Trunk. "
                + "Deutlicher, honigartiger Süsseeindruck, "
                + "in Kombination mit einer weichen, sich im Hintergrund haltende Bittere. "
                + "Anhaltender, vollfruchtiger Abgang.", Calendar.getInstance().getTimeInMillis(),
            getRealisticMashingPlan(), getRealisticHopCookingPlan());
    return recipe;
  }

  /**
   * Creates a list of recipe-summaries.
   *
   * @return random generated list of recipe-summaries
   */
  public static List<RecipeSummary> getRecipes() {
    List<RecipeSummary> recipes = new ArrayList<>();
    recipes.add(getRealisticRecipe().getSummary());

    for (int i = 0; i < 20; i++) {
      RecipeSummary recipe =
          new RecipeSummary("Title " + i, "Description " + i, addTimeToNow(-i, 0, 0, 0, 0), i + "");
      recipes.add(recipe);
    }
    return recipes;
  }

  /**
   * Creates a temperature-level.
   *
   * @return random temperature level
   */
  public static TemperatureLevel getTemperatureLevel() {
    TemperatureLevel level = new TemperatureLevel();
    level.setTemperature(randInt(40, 100));
    level.setStartTime(new Date().getTime() - (1000 * 60 * randInt(0, 300)));
    level.setDuration(60 * (randInt(10, 20)));
    return level;
  }

  /**
   * Creates a random temperature-level-message.
   *
   * @return Temperature-level message
   */
  public static TemperatureLevelMessage getTemperatureLevelMessage() {
    TemperatureLevelMessage message = new TemperatureLevelMessage(getTemperatureLevel());
    setBasicMessageDetails(message);
    return message;
  }

  /**
   * Creates a list of temperature levels.
   *
   * @return random list of temperature levels
   */
  public static List<TemperatureLevel> getTemperatureLevels() {
    List<TemperatureLevel> levels = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      levels.add(getTemperatureLevel());
    }
    return levels;
  }

  /**
   * Creates a random temperature-message.
   *
   * @return Temperature message with temperature
   */
  public static Message getTemperatureMessage() {
    TemperatureMessage message = new TemperatureMessage(0.0f);
    message.setTemperature(randInt(0, 100));
    setBasicMessageDetails(message);
    return message;
  }

  /**
   * Helper function to generate random numbers.
   *
   * @param min - Min Value
   * @param max - Max Value
   * @return Value between Min - Max (inclusive)
   */
  public static int randInt(final int min, final int max) {
    return RANDOM.nextInt((max - min) + 1) + min;
  }

  /**
   * Sets basic values to message objects.
   *
   * @param message message which should be modified
   */
  private static void setBasicMessageDetails(final Message message) {
    message.setMessage("This is a message");
    message.setTime(new Date().getTime() - (1000 * 60 * randInt(0, 300)));
  }

  /** Random generator. */
  public final static Random RANDOM = new Random();


  /**
   * Creates a valid recipe for testing purpose with shortened steps so the test does not take
   * forever
   *
   * @return the recipe
   */
  public static Recipe createValidTestRecipe() {

    Recipe recipe = new Recipe();

    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    List<MaltAddition> maltAdditions = new Vector<MaltAddition>();
    mashingPlan.setTemperatureLevels(temperatureLevels);
    mashingPlan.setMaltAdditions(maltAdditions);

    HopCookingPlan hopCookingPlan = new HopCookingPlan();
    List<HopAddition> hopAdditions = new Vector<HopAddition>();
    hopCookingPlan.setHopAdditions(hopAdditions);

    recipe.setMashingPlan(mashingPlan);
    recipe.setHopCookingPlan(hopCookingPlan);

    addTemperatureLevels(temperatureLevels);
    addMaltAdditions(maltAdditions);
    addHopAdditions(hopAdditions);

    hopCookingPlan.setDuration(7000);

    return recipe;
  }

  /**
   * Adds some temperature levels for testing.
   *
   * @param temperatureLevels
   */
  private static void addTemperatureLevels(final Collection<TemperatureLevel> temperatureLevels) {
    TemperatureLevel tempLevel;
    tempLevel = new TemperatureLevel(2 * 1000, 0, 50.0f);
    temperatureLevels.add(tempLevel);
    tempLevel = new TemperatureLevel(2 * 1000, 2 * 1000, 60.0f);
    temperatureLevels.add(tempLevel);
    tempLevel = new TemperatureLevel(2 * 1000, 4 * 1000, 75.0f);
    temperatureLevels.add(tempLevel);
  }

  /**
   * Adds some malt for testing.
   *
   * @param maltAdditions
   */
  private static void addMaltAdditions(final Collection<MaltAddition> maltAdditions) {
    MaltAddition maltAddition;
    maltAddition = new MaltAddition(2.0f, Unit.kg, "Malz 1", 0);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(1.0f, Unit.kg, "Malz 2", 1 * 1000);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(3.5f, Unit.kg, "Malz 3", 3 * 1000);
    maltAdditions.add(maltAddition);
  }

  /**
   * Adds some hop for testing.
   *
   * @param hopAdditions
   */
  private static void addHopAdditions(final Collection<HopAddition> hopAdditions) {
    HopAddition ha = new HopAddition(1.0f, Unit.kg, "Hopfen 1", 2 * 1000);
    hopAdditions.add(ha);
    ha = new HopAddition(2.0f, Unit.kg, "Hopfen Zwo", 3 * 1000);
    hopAdditions.add(ha);
    ha = new HopAddition(1.5f, Unit.kg, "Hopfen 3", 6 * 1000);
    hopAdditions.add(ha);
  }
}
