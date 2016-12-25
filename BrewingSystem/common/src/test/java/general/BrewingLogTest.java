package general;

import junit.framework.Assert;
import messages.Message;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import utilities.DummyBuilder;
import categories.UnitTest;
/**
 * Tests that brewing logs work as expected
 * @author matthias
 *
 */
public class BrewingLogTest {

	/**performs a test for brewing logs*/
	@Category(UnitTest.class)
	@Test
	public void testBrewingLog() {
		Recipe recipe = DummyBuilder.getRealisticRecipe();
		Message message = DummyBuilder.getMessage();
		BrewingLog log1 = new BrewingLog(recipe, 1);
		BrewingLog log2 = new BrewingLog(recipe, 1);
		Assert.assertTrue(log1.getId() == log2.getId());
		Assert.assertEquals(log1.getRecipe(), log2.getRecipe());
		Assert.assertTrue(log1.log(message));
		Assert.assertFalse(log1.equals(log2));
		Assert.assertTrue(log1.getLatestTime() > 0 && log2.getLatestTime() == null);
		Assert.assertTrue(log1.getMessages().contains(message));
		
		log1.setMessages(null);
		Assert.assertNotNull(log1.getSummary());
		
		Assert.assertTrue(log1.log(message));
		Assert.assertNotNull(log1.getMessages());
		Assert.assertTrue(log1.getMessages().contains(message));
		Assert.assertEquals(log1.getMessages().size(), 1);
		Assert.assertNotNull(log1.getProtocol());
		Assert.assertNotNull(log1.getSummary());
	}

}
