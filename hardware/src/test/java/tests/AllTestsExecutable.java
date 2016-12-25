package tests;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import components.GPIOTest;
import components.ThermometerReaderTest;

/**
 * Runs all tests of the common-project
 */
@RunWith(Categories.class)
@SuiteClasses({GPIOTest.class, ThermometerReaderTest.class})
public class AllTestsExecutable {
  /*
   * has to be empty
   */
}
