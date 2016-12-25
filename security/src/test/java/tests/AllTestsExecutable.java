package tests;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import security.SecuritySurveillanceTest;

/**
 * Runs all tests of the common-project
 */
@RunWith(Categories.class)
@SuiteClasses({SecuritySurveillanceTest.class})
public class AllTestsExecutable {
  /*
   * has to be empty
   */
}
