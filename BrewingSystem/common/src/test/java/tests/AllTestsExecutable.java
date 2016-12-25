package tests;

import general.HopCookingPlanTest;
import general.MashingPlanTest;
import general.RecipeTest;
import gson.BrewingStateAdapterTest;
import gson.CommonSerializerTest;
import gson.MessageAdapterTest;
import gson.SerializerTest;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import parser.RecipeWriterReaderTest;
import utilities.CollectionUtilTest;

/**
 * Runs all tests of the common-project
 */
@RunWith(Categories.class)
@SuiteClasses({BrewingStateAdapterTest.class, CommonSerializerTest.class, MessageAdapterTest.class,
    CollectionUtilTest.class, RecipeWriterReaderTest.class, SerializerTest.class,
    HopCookingPlanTest.class, MashingPlanTest.class, RecipeTest.class})
public class AllTestsExecutable {
  /*
   * has to be empty
   */
}
