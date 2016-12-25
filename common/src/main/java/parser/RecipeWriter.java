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
import general.Ingredient;
import general.MaltAddition;
import general.MashingPlan;
import general.Recipe;
import general.TemperatureLevel;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * This class parses a recipe to xml.
 *
 * @author Daniel Langerenken
 */
public class RecipeWriter {

  /** Global logger to log the interaction with the controller. */
  public static final Logger LOGGER = LogManager.getLogger();

  /** current recipe to parse. */
  private Recipe recipe;

  /** create XMLEventWriter. */
  private XMLEventWriter eventWriter;

  /** create an EventFactory. */
  private XMLEventFactory eventFactory;

  /** New-Line-Element. */
  private XMLEvent end;

  /** New-Tab-Element. */
  private XMLEvent tab;

  /** create an XMLOutputFactory. */
  private XMLOutputFactory outputFactory;

  /** OutputStream as byte[]. */
  private ByteArrayOutputStream outputStream;

  /** How many tabs the text should be written in (not relevant for parsing). */
  private int tabPosition = 0;

  /**
   * Adds a new line to the current position in the document.
   *
   * @throws XMLStreamException possible exception which can be thrown
   */
  private void addEnd() throws XMLStreamException {
    eventWriter.add(end);
  }

  /**
   * adds an end-element with given name to the document and creates a new line.
   *
   * @param element name which should be inside of the end-tag
   * @throws XMLStreamException possible exception which can be thrown
   */
  private void addEndElement(final String element) throws XMLStreamException {
    addTab();
    eventWriter.add(eventFactory.createEndElement("", "", element));
    addEnd();
  }

  /**
   * Adds an ingredient to the document.
   *
   * @param addition Ingredient which should be added to the document
   * @throws XMLStreamException possible exception which can be thrown
   */
  private void addIngredient(final Ingredient addition) throws XMLStreamException {
    if (addition != null) {
      createNode(AMOUNT_NODE, addition.getAmount() + "");
      createNode(UNIT_NODE, addition.getUnit() != null ? addition.getUnit() + "" : "");
      createNode(NAME_NODE, addition.getName());
    }
  }

  /**
   * Validate null.
   *
   * @param object the object
   * @return the string
   */
  private String validateNull(final Object object) {
    if (object == null) {
      return NULL_NODE;
    }
    return object + "";
  }

  /**
   * adds an start-element with given name to the document and creates a new line.
   *
   * @param element name which should be inside of the start-tag
   * @throws XMLStreamException possible exception which can be thrown
   */
  private void addStartElement(final String element) throws XMLStreamException {
    addTab();
    eventWriter.add(eventFactory.createStartElement("", "", element));
    addEnd();
  }

  /**
   * Adds a tab to the current line of the document.
   *
   * @throws XMLStreamException possible exception which can be thrown
   */
  private void addTab() throws XMLStreamException {
    for (int i = 0; i < tabPosition; i++) {
      eventWriter.add(tab);
    }
  }

  /**
   * based on http://www.vogella.com/tutorials/JavaXML/article.html
   *
   * @param recipe Recipe which should be parsed to XML
   * @return String as XML
   * @throws RecipeParseException if something went wrong during the process
   */
  public String convertRecipeToXml(final Recipe recipe) throws RecipeParseException {
    try {
      LOGGER.info(String.format("Convert recipe: %s", recipe.getId()));
      init(recipe);

      eventWriter.add(eventFactory.createStartDocument());
      addEnd();
      addStartElement(RECIPE_NODE);

      createRecipeDetails();
      createMashPlan();
      createHopCookingPlan();

      addEndElement(RECIPE_NODE);
      eventWriter.add(eventFactory.createEndDocument());
      eventWriter.close();
      return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    } catch (XMLStreamException e) {
      LOGGER.error(e);
      throw new RecipeParseException(e);
    } catch (Exception e) {
      LOGGER.error(e);
      throw new RecipeParseException(e);
    }
  }

  /**
   * adds the hop-cooking plan to the document.
   *
   * @throws XMLStreamException possible exception which can be thrown
   */
  private void createHopCookingPlan() throws XMLStreamException {
    HopCookingPlan hopCookingPlan = recipe.getHopCookingPlan();
    if (hopCookingPlan != null && hopCookingPlan.getHopAdditions() != null) {
      addStartElement(HOP_ADDITION_NODE);
      createNode(DURATION_NODE, hopCookingPlan.getDuration() + "");
      for (HopAddition addition : hopCookingPlan.getHopAdditions()) {
        tabPosition++;
        addStartElement(HOP_NODE);
        tabPosition++;
        addIngredient(addition);
        createNode(INPUT_TIME_NODE, addition.getInputTime() + "");
        tabPosition--;
        addEndElement(HOP_NODE);
        tabPosition--;
      }
      addEndElement(HOP_ADDITION_NODE);
    }
  }

  /**
   * Creates the malt addition.
   *
   * @param plan the plan
   * @throws XMLStreamException the XML stream exception
   */
  private void createMaltAddition(final MashingPlan plan) throws XMLStreamException {
    tabPosition++;
    if (plan != null && plan.getMaltAdditions() != null) {
      addStartElement(MALT_ADDITION_NODE);
      for (MaltAddition addition : plan.getMaltAdditions()) {
        tabPosition++;
        addStartElement(MALT_NODE);
        tabPosition++;
        addIngredient(addition);
        createNode(INPUT_TIME_NODE, addition.getInputTime() + "");
        tabPosition--;
        addEndElement(MALT_NODE);
        tabPosition--;
      }
      addEndElement(MALT_ADDITION_NODE);
    }
    tabPosition--;
  }

  /**
   * Creates the mash plan.
   *
   * @throws XMLStreamException possible exception which can be thrown
   */
  private void createMashPlan() throws XMLStreamException {
    MashingPlan plan = recipe.getMashingPlan();
    if (plan != null) {
      addStartElement(MASHPLAN_NODE);
      createTempLevel(plan);
      createMaltAddition(plan);
      addEndElement(MASHPLAN_NODE);
    }
  }

  /**
   * http://www.vogella.com/tutorials/JavaXML/article.html Creates a node with name and value
   * 
   * @param name name of the node which should be added to the document
   * @param value value of the node which should be added to the document
   * 
   * @throws XMLStreamException possible exception which can be thrown
   */
  private void createNode(final String name, final String value) throws XMLStreamException {
    addTab();
    eventWriter.add(eventFactory.createStartElement("", "", name));
    // create Content
    Characters characters = eventFactory.createCharacters(validateNull(value));
    eventWriter.add(characters);
    // create End node
    eventWriter.add(eventFactory.createEndElement("", "", name));
    addEnd();
  }

  /**
   * Creates the recipe-details (name ...)
   * 
   * @throws XMLStreamException possible exception which can be thrown
   */
  private void createRecipeDetails() throws XMLStreamException {
    addStartElement(RECIPE_SUMMARY);
    tabPosition++;
    createNode(NAME_NODE, recipe.getName());
    createNode(DESCRIPTION_NODE, recipe.getDescription());
    createNode(DATE_NODE, recipe.getDate() + "");
    tabPosition--;
    addEndElement(RECIPE_SUMMARY);
  }

  /**
   * Creates the temp level.
   *
   * @param plan the plan
   * @throws XMLStreamException the XML stream exception
   */
  private void createTempLevel(final MashingPlan plan) throws XMLStreamException {
    tabPosition++;
    if (plan != null && plan.getTemperatureLevels() != null) {
      addStartElement(TEMP_LEVEL_NODE);
      for (TemperatureLevel level : plan.getTemperatureLevels()) {
        tabPosition++;
        addStartElement(LEVEL_NODE);
        tabPosition++;
        createNode(START_TIME_NODE, level.getStartTime() + "");
        createNode(DURATION_NODE, level.getDuration() + "");
        createNode(TEMP_NODE, level.getTemperature() + "");
        tabPosition--;
        addEndElement(LEVEL_NODE);
        tabPosition--;
      }
      addEndElement(TEMP_LEVEL_NODE);
    }
    tabPosition--;
  }

  /**
   * Inits the Parser.
   *
   * @param recipe recipe which is going to be read from
   * @throws XMLStreamException possible exception during parsing
   */
  private void init(final Recipe recipe) throws XMLStreamException {
    if (recipe == null) {
      throw new IllegalArgumentException("Recipe should not be null");
    }
    outputFactory = XMLOutputFactory.newInstance();
    outputStream = new ByteArrayOutputStream();
    eventWriter = outputFactory.createXMLEventWriter(outputStream);
    eventFactory = XMLEventFactory.newInstance();
    end = eventFactory.createDTD("\n");
    tab = eventFactory.createDTD("\t");
    this.recipe = recipe;
  }
}
