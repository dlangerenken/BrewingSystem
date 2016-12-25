package utilities;

import general.MaltAddition;
import general.Unit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import categories.UnitTest;

/**
 * Tests the functionality of the collection-util class
 */
public class CollectionUtilTest {

  /**
   * Verifies that the sort method in CollectionUtil for MaltAddition convertion from HashSet to
   * ArrayList works correctly.
   */
  @Category(UnitTest.class)
  @Test
  public void sortMaltAdditionTest() {
    final int loops = 10;
    for (int loop = 0; loop < loops; loop++) {
      final int maltAdditionCount = 50;
      final Random random = new Random(System.currentTimeMillis());
      final HashSet<MaltAddition> input = new HashSet<MaltAddition>();
      for (int i = 0; i < maltAdditionCount; i++) {
        input.add(new MaltAddition(1.0f, Unit.kg, "" + i, random.nextLong()));
      }
      ArrayList<MaltAddition> output =
          CollectionUtil.getArrayListOfBrewingStatesSortedByTime(input);
      MaltAddition lastMalt = null;
      for (MaltAddition malt : output) {
        if (lastMalt != null) {
          Assert.assertEquals(true, malt.getInputTime() >= lastMalt.getInputTime());
        }
        lastMalt = malt;
      }
    }
  }
}
