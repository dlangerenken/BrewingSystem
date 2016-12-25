/*
 * 
 */
package parser;

import static parser.RecipeConstants.AMOUNT_NODE;
import static parser.RecipeConstants.DATE_NODE;
import static parser.RecipeConstants.DESCRIPTION_NODE;
import static parser.RecipeConstants.DURATION_NODE;
import static parser.RecipeConstants.HOP_ADDITION_NODE;
import static parser.RecipeConstants.HOP_NODE;
import static parser.RecipeConstants.INPUT_TIME_NODE;
import static parser.RecipeConstants.LEVEL_NODE;
import static parser.RecipeConstants.MALT_ADDITION_NODE;
import static parser.RecipeConstants.MALT_NODE;
import static parser.RecipeConstants.MANUAL_NODE;
import static parser.RecipeConstants.MANUAL_STEPS_NODE;
import static parser.RecipeConstants.MASHPLAN_NODE;
import static parser.RecipeConstants.NAME_NODE;
import static parser.RecipeConstants.NULL_NODE;
import static parser.RecipeConstants.RECIPE_NODE;
import static parser.RecipeConstants.RECIPE_SUMMARY;
import static parser.RecipeConstants.START_TIME_NODE;
import static parser.RecipeConstants.TEMP_LEVEL_NODE;
import static parser.RecipeConstants.TEMP_NODE;
import static parser.RecipeConstants.UNIT_NODE;
import exceptions.RecipeParseException;
import general.HopAddition;
import general.HopCookingPlan;
import general.MaltAddition;
import general.ManualStep;
import general.MashingPlan;
import general.Recipe;
import general.RecipeSummary;
import general.TemperatureLevel;
import general.Unit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.InvalidAttributesException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Reads the recipe from a given xml file.
 *
 * @author Daniel Langerenken
 */
public class RecipeReader {

  /**
   * Handles the amount-node.
   *
   * @author Daniel Langerenken
   */
  private final class AmountNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() throws XMLStreamException {
      if (state == ParsingState.MALT_ADDITION && maltAddition != null) {
        try {
          maltAddition.setAmount(Float.parseFloat(getCharacterData()));
        } catch (NumberFormatException e) {
          /*
           * ignore this here and do not set a date at all
           */
        }
      }
      if (state == ParsingState.HOP_ADDITION && hopAddition != null) {
        try {
          hopAddition.setAmount(Float.parseFloat(getCharacterData()));
        } catch (NumberFormatException e) {
          /*
           * ignore this here and do not set a date at all
           */
        }
      }
    }
  }

  /**
   * Handles the date-node.
   *
   * @author Daniel Langerenken
   */
  private final class DateNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() throws XMLStreamException {
      if (state == ParsingState.INFO) {
        try {
          summary.setDate(Long.parseLong(getCharacterData()));
        } catch (NumberFormatException e) {
          /*
           * ignore this here and do not set a date at all
           */
        }
      }
    }
  }

  /**
   * Handles the description-node.
   *
   * @author Daniel Langerenken
   */
  private final class DescNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() throws XMLStreamException {
      if (state == ParsingState.INFO) {
        summary.setDescription(getCharacterData());
      }
      if (state == ParsingState.MANUAL && manualStep != null) {
        manualStep.setDescription(getCharacterData());
      }
    }
  }

  /**
   * Handles the duration-node.
   *
   * @author Daniel Langerenken
   */
  private final class DurationNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() throws XMLStreamException {
      try {
        int duration = Integer.parseInt(getCharacterData());
        if (state == ParsingState.TEMP_LEVEL && level != null) {
          level.setDuration(duration);
        }
        if (state == ParsingState.MANUAL && manualStep != null) {
          manualStep.setDuration(duration);
          manualSteps.add(manualStep);
          manualStep = null;
        }
        if (state == ParsingState.HOP_ADDITION && hopCookingPlan != null) {
          hopCookingPlan.setDuration(duration);
        }
      } catch (NumberFormatException e) {
        /*
         * ignore this here and do not set a duration at all
         */
      }
    }
  }

  /**
   * Handles the hop-cooking plan node.
   *
   * @author Daniel Langerenken
   */
  private final class HopAdditionNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() {
      hopCookingPlan = new HopCookingPlan();
      hopAdditions = new ArrayList<>();
      hopCookingPlan.setHopAdditions(hopAdditions);
      state = ParsingState.HOP_ADDITION;
      recipe.setHopCookingPlan(hopCookingPlan);
    }
  }

  /**
   * Handles the hop-node (single).
   *
   * @author Daniel Langerenken
   */
  private final class HopNodeImpl implements ParseNodeInterface {


    @SuppressWarnings("deprecation")
    @Override
    public void parse() {
      hopAddition = new HopAddition();
    }
  }

  /**
   * Handles the id-node.
   *
   * @author Daniel Langerenken
   * @deprecated Might result in inconsistencies
   */
  @SuppressWarnings("unused")
  @Deprecated
  private final class IdNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() throws XMLStreamException {
      if (state == ParsingState.INFO) {
        summary.setId(getCharacterData());
      }
    }
  }

  /**
   * Handles the input-time-node.
   *
   * @author Daniel Langerenken
   */
  private final class InputTimeNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() throws XMLStreamException {
      long time = 0;
      try {
        time = Long.parseLong(getCharacterData());
      } catch (NumberFormatException e) {
        /*
         * ignore this here and do not set a duration at all
         */
      }
      if (state == ParsingState.MALT_ADDITION && maltAddition != null) {
        maltAddition.setInputTime(time);
        maltAdditions.add(maltAddition);
        maltAddition = null;
      }
      if (state == ParsingState.HOP_ADDITION && hopAddition != null) {
        hopAddition.setInputTime(time);
        hopAdditions.add(hopAddition);
        hopAddition = null;
      }
    }
  }

  /**
   * Handles the temperature-level-node (single).
   *
   * @author Daniel Langerenken
   */
  private final class LevelNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() {
      state = ParsingState.TEMP_LEVEL;
      level = new TemperatureLevel();
    }
  }

  /**
   * Handles the malt-addition-node (multiple).
   *
   * @author Daniel Langerenken
   */
  private final class MaltAdditionNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() {
      maltAdditions = new ArrayList<>();
      state = ParsingState.MALT_ADDITION;
      mashingPlan.setMaltAdditions(maltAdditions);
    }
  }

  /**
   * Handles the malt-node (single).
   *
   * @author Daniel Langerenken
   */
  private final class MaltNodeImpl implements ParseNodeInterface {


    @SuppressWarnings("deprecation")
    @Override
    public void parse() {
      maltAddition = new MaltAddition();
    }
  }

  /**
   * Handles the manual-step-node (single).
   *
   * @author Daniel Langerenken
   */
  private final class ManualNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() {
      manualStep = new ManualStep();
    }
  }

  /**
   * Handles the manual-steps-node (multiple).
   *
   * @author Daniel Langerenken
   */
  private final class ManualStepsNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() {
      state = ParsingState.MANUAL;
      manualSteps = new ArrayList<ManualStep>();
    }
  }

  /**
   * Handles the mashing-plan-node (multiple).
   *
   * @author Daniel Langerenken
   */
  private final class MashPlanNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() {
      mashingPlan = new MashingPlan();
      recipe.setMashingPlan(mashingPlan);
    }
  }

  /**
   * Handles the name-node (e.g. recipe)
   * 
   * @author Daniel Langerenken
   *
   */
  private final class NameNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() throws XMLStreamException {
      if (state == ParsingState.INFO) {
        summary.setTitle(getCharacterData());
      } else if (state == ParsingState.MALT_ADDITION && maltAddition != null) {
        maltAddition.setName(getCharacterData());
      } else if (state == ParsingState.HOP_ADDITION && hopAddition != null) {
        hopAddition.setName(getCharacterData());
      }
    }
  }

  /**
   * Interface for saving nodes and implementations within a map.
   *
   * @author Daniel Langerenken
   */
  public interface ParseNodeInterface {

    /**
     * Parses the current node and adds necessary information to the elements.
     *
     * @throws XMLStreamException Thrown if parsing failed
     */
    void parse() throws XMLStreamException;
  }

  /**
   * States for the parsing process to differentiate between elements with same names.
   */
  private enum ParsingState {

    /** The info. */
    INFO,
    /** The mash. */
    MASH,
    /** The hop addition. */
    HOP_ADDITION,
    /** The manual. */
    MANUAL,
    /** The temp level. */
    TEMP_LEVEL,
    /** The malt addition. */
    MALT_ADDITION,
    /** The null. */
    NULL
  }

  /**
   * Handles the recipe-nodes.
   *
   * @author Daniel Langerenken
   */
  private final class RecipeNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() {
      summary = new RecipeSummary();
    }
  }

  /**
   * Handles the summary-node.
   *
   * @author Daniel Langerenken
   */
  private final class RecipeSummaryImpl implements ParseNodeInterface {


    @Override
    public void parse() throws XMLStreamException {
      state = ParsingState.INFO;
    }
  }

  /**
   * Handles the start-time-node.
   *
   * @author Daniel Langerenken
   */
  private final class StartTimeNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() throws XMLStreamException {
      long time = 0;
      try {
        time = Long.parseLong(getCharacterData());
      } catch (NumberFormatException e) {
        /*
         * ignore this here and do not set a duration at all
         */
      }
      if (state == ParsingState.TEMP_LEVEL && level != null) {
        level.setStartTime(time);
      }
      if (state == ParsingState.MANUAL && manualStep != null) {
        manualStep.setStartTime(time);
      }
    }
  }

  /**
   * Handles the temperature-level-nodes (multiple).
   *
   * @author Daniel Langerenken
   */
  private final class TempLevelNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() {
      tempLevel = new ArrayList<>();
      mashingPlan.setTemperatureLevels(tempLevel);
    }
  }

  /**
   * Handles the temp-node.
   *
   * @author Daniel Langerenken
   */
  private final class TempNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() throws XMLStreamException {

      if (state == ParsingState.TEMP_LEVEL && level != null) {
        try {
          float temperature = Float.parseFloat(getCharacterData());
          level.setTemperature(temperature);
        } catch (NumberFormatException e) {
          /*
           * ignore this here and do not set a temperature at all
           */
        }
      }
      tempLevel.add(level);
      level = null;


    }
  }

  /**
   * Handles the unit-node.
   *
   * @author Daniel Langerenken
   */
  private final class UnitNodeImpl implements ParseNodeInterface {


    @Override
    public void parse() throws XMLStreamException {
      String data = getCharacterData();
      if (data == null || "null".equals(data) || "".equals(data)) {
        return;
      }
      if (state == ParsingState.MALT_ADDITION && maltAddition != null) {
        maltAddition.setUnit(Unit.valueOf(data));
      }
      if (state == ParsingState.HOP_ADDITION && hopAddition != null) {
        hopAddition.setUnit(Unit.valueOf(data));
      }
    }
  }

  /** Temporary object for recipe parsing. */
  private Recipe recipe = null;

  /** Temporary object for recipe-summary parsing. */
  private RecipeSummary summary = null;

  /** Temporary object for mashing-plan parsing. */
  private MashingPlan mashingPlan = null;

  /** Temporary object for hop-cooking-plan parsing. */
  private HopCookingPlan hopCookingPlan = null;

  /** Temporary list of temperature levels. */
  private List<TemperatureLevel> tempLevel = null;

  /** Temporary object for temperature-level. */
  private TemperatureLevel level = null;

  /** Temporary list for malt-additions. */
  private List<MaltAddition> maltAdditions = null;

  /** Temporary object for malt addition parsing. */
  private MaltAddition maltAddition = null;

  /** Temporary list for hop-additions. */
  private List<HopAddition> hopAdditions = null;

  /** Temporary object for hop addition parsing. */
  private HopAddition hopAddition = null;

  /** Temporary list for manual-steps. */
  private List<ManualStep> manualSteps = null;

  /** Temporary object for manual step parsing. */
  private ManualStep manualStep = null;

  /** Map of node-names and parse-implementations. */
  private Map<String, ParseNodeInterface> stateMap;

  /** Global logger to log the interaction with the controller. */
  public static final Logger LOGGER = LogManager.getLogger();

  /** InputFactory for a recipe. */
  private XMLInputFactory inputFactory;

  /** Xml-Event reader which will loop through the xml file. */
  private XMLEventReader xmlEventReader;

  /** InputStream for the InputFactory (in our case this is a byte[] (string)). */
  private InputStream inputStream;

  /** Current State of the parsing process for dealing with same names. */
  private ParsingState state;

  /**
   * Instantiates the RecipeReader and stateMap.
   */
  public RecipeReader() {
    initStateMap();
  }

  /**
   * based on http://www.vogella.com/tutorials/RSSFeed/article.html#read_stax
   * 
   * @return value inside of the xmlEvent
   * @throws XMLStreamException Thrown if something in the reading-process has failed
   */
  private String getCharacterData() throws XMLStreamException {
    String result = "";
    XMLEvent event = xmlEventReader.nextEvent();
    if (event instanceof Characters) {
      result = event.asCharacters().getData();
    }
    return NULL_NODE.equals(result) ? null : result;
  }

  /**
   * returns the recipe by the given xml-string.
   *
   * @param content xml-document of the recipe
   * @return Recipe which was inside of the given string
   * @throws RecipeParseException if something went wrong during the process
   */
  public Recipe getRecipeByString(final String content) throws RecipeParseException {
    try {
      init(content);
      return parseRecipe();
    } catch (XMLStreamException e) {
      LOGGER.error(e);
      throw new RecipeParseException(e);
    } catch (Exception e) {
      LOGGER.error(e);
      throw new RecipeParseException(e);
    }
  }

  /**
   * Gets only the recipe-summary (name, desc, date, id).
   *
   * @param content content to parse from
   * @return RecipeSummary-Object
   * @throws RecipeParseException if something went wrong during the process
   */
  public RecipeSummary getRecipeSummaryByString(final String content) throws RecipeParseException {
    try {
      init(content);
      return parseRecipeSummary();
    } catch (XMLStreamException e) {
      LOGGER.error(e);
      throw new RecipeParseException(e);
    } catch (Exception e) {
      LOGGER.error(e);
      throw new RecipeParseException(e);
    }
  }

  /**
   * Initializes the Reader with a new content-file and resets all states.
   *
   * @param content content-file which should be parsed from
   * @throws RecipeParseException if something goes wrong we need catch the exception and throw a
   *         general one
   */
  private void init(final String content) throws RecipeParseException {
    if (content == null) {
      throw new RecipeParseException(new InvalidAttributesException("Content should not be null"));
    }
    LOGGER.info("Trying to parse recipe");
    inputFactory = XMLInputFactory.newInstance();
    inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    try {
      xmlEventReader = inputFactory.createXMLEventReader(inputStream);
    } catch (XMLStreamException e) {
      LOGGER.error(e);
      throw new RecipeParseException(e);
    }
    resetValues();
  }

  /**
   * Reset all values
   */
  private void resetValues() {
    recipe = null;
    summary = null;
    mashingPlan = null;
    hopCookingPlan = null;
    tempLevel = null;
    level = null;
    maltAdditions = null;
    maltAddition = null;
    hopAdditions = null;
    hopAddition = null;
    manualSteps = null;
    manualStep = null;
  }

  /**
   * Adds possible nodes to the hashmap and the implementation which deals with the node.
   */
  private void initStateMap() {
    stateMap = new HashMap<>();
    stateMap.put(RECIPE_NODE, new RecipeNodeImpl());
    stateMap.put(RECIPE_SUMMARY, new RecipeSummaryImpl());
    stateMap.put(NAME_NODE, new NameNodeImpl());
    stateMap.put(DESCRIPTION_NODE, new DescNodeImpl());
    stateMap.put(MASHPLAN_NODE, new MashPlanNodeImpl());
    stateMap.put(TEMP_LEVEL_NODE, new TempLevelNodeImpl());
    stateMap.put(LEVEL_NODE, new LevelNodeImpl());
    stateMap.put(START_TIME_NODE, new StartTimeNodeImpl());
    stateMap.put(DURATION_NODE, new DurationNodeImpl());
    stateMap.put(TEMP_NODE, new TempNodeImpl());
    stateMap.put(MALT_ADDITION_NODE, new MaltAdditionNodeImpl());
    stateMap.put(MALT_NODE, new MaltNodeImpl());
    stateMap.put(AMOUNT_NODE, new AmountNodeImpl());
    stateMap.put(UNIT_NODE, new UnitNodeImpl());
    stateMap.put(INPUT_TIME_NODE, new InputTimeNodeImpl());
    stateMap.put(HOP_ADDITION_NODE, new HopAdditionNodeImpl());
    stateMap.put(HOP_NODE, new HopNodeImpl());
    stateMap.put(MANUAL_STEPS_NODE, new ManualStepsNodeImpl());
    stateMap.put(MANUAL_NODE, new ManualNodeImpl());
    stateMap.put(DATE_NODE, new DateNodeImpl());
  }

  /**
   * Parses the.
   *
   * @param event the event
   * @throws XMLStreamException the XML stream exception
   */
  private void parse(final XMLEvent event) throws XMLStreamException {
    if (event.isStartElement()) {
      String localPart = event.asStartElement().getName().getLocalPart();
      ParseNodeInterface parseNodeInterface = stateMap.get(localPart);
      if (parseNodeInterface != null) {
        parseNodeInterface.parse();
      }
    }
  }

  /**
   * Parses the recipe from the given intialization And yes, this method has to be this size -
   * otherwise there would be a lot of duplicated code.
   *
   * @return the read recipe
   * @throws XMLStreamException Thrown if something in the reading-process has failed
   */
  private Recipe parseRecipe() throws XMLStreamException {
    RecipeSummary mySummary = parseRecipeSummary();
    if (mySummary != null) {
      recipe = new Recipe();
      recipe.setName(mySummary.getTitle());
      recipe.setDate(mySummary.getDate());
      recipe.setDescription(mySummary.getDescription());
      recipe.setId(mySummary.getId());
    }

    while (xmlEventReader.hasNext()) {
      parse(xmlEventReader.nextEvent());
    }

    return recipe;
  }

  /**
   * Parses a recipe-summary and ignores other tags.
   *
   * @return recipe-summary
   * @throws XMLStreamException Thrown if something in the reading-process has failed
   */
  private RecipeSummary parseRecipeSummary() throws XMLStreamException {
    while (xmlEventReader.hasNext()) {
      XMLEvent peek = xmlEventReader.peek();
      if (peek.isEndElement()) {
        /*
         * if the end-element is </summary> we're done here with the parsing of the recipe-summary
         */
        if (peek.asEndElement().getName().getLocalPart().equals(RECIPE_SUMMARY)) {
          /*
           * done here
           */
          return summary;
        }
      }
      parse(xmlEventReader.nextEvent());
    }
    return summary;
  }
}
