/*
 * 
 */
package gson;

import junit.framework.Assert;

import org.junit.Before;

import com.google.gson.Gson;


/**
 * The Class SerializerTest offers a generic test functionality for gson which consists of parsing
 * an object to a string and back to an object afterw it then calls the equals-method of this object
 * to check if it did not lose any information. it also checks the string-equality which could show
 * up any errors (for better debug purposes)
 */
public abstract class SerializerTest {

  /** The gson - serializer */
  protected Gson gson;

  /**
   * Inits the gson-serializer
   */
  @Before
  public void init() {
    gson = Serializer.getInstance();
  }

  /**
   * Generic test of object parsing and deparsing
   * 
   * @param obj Object which should be tested
   * @param clazz type of the object
   */
  protected <T> void genericTest(final T obj, final Class<T> clazz) {
    String json = gson.toJson(obj, clazz);
    T from = gson.fromJson(json, clazz);
    String json2 = gson.toJson(from, clazz);
    Assert.assertEquals(obj, from);
    Assert.assertEquals(json, json2);
  }

}
