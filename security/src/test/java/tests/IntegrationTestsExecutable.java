package tests;

import org.junit.experimental.categories.Categories.IncludeCategory;

import tests.AllTestsExecutable;
import categories.IntegrationTest;

/**
 * Runs all integration tests of the common-project
 */
@IncludeCategory(IntegrationTest.class)
public class IntegrationTestsExecutable extends AllTestsExecutable {
  /*
   * has to be empty
   */
}
