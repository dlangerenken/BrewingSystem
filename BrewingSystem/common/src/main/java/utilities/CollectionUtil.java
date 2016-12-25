package utilities;

import general.MaltAddition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Provides methods related to Collection and its generalizations
 * 
 * @author Patrick
 *
 */
public class CollectionUtil {

  /**
   * Checks if data is of the type Collection<T> and returns that collection. Returns null
   * otherwise.
   * 
   * @param data the data object that one assumes is a Collection<T>
   * @param type the type of the collection
   * @return a Collection<T> if o is such a collection, null otherwise.
   */
  @SuppressWarnings("unchecked")
  /* this is not unchecked but java doesn't know */
  public static <T> Collection<T> getTypedCollectionFromObject(final Object data, final Class<T> type) {
    if (data == null || !(data instanceof Collection<?>)) {
      return null;
    }
    Collection<?> collection = (Collection<?>) data;
    for (Object o : collection) {
      if (o != null && !type.isAssignableFrom(o.getClass())) {
        return null;
      }
    }
    return (Collection<T>) collection;
  }

  /**
   * Takes a Collection of MaltAddition Objects and sorts it by the input time, returning an
   * ArrayList
   * 
   * @param input unsorted Collection<MaltAddition>
   * @return sorted ArrayList<MaltAddition>
   */
  public static ArrayList<MaltAddition> getArrayListOfBrewingStatesSortedByTime(
      final Collection<MaltAddition> input) {
    ArrayList<MaltAddition> output = new ArrayList<MaltAddition>(input);
    Collections.sort(output, new Comparator<MaltAddition>() {

      @Override
      public int compare(final MaltAddition o1, final MaltAddition o2) {
        if (o1 == o2 || o1 == null && o2 == null) {
          return 0;
        }
        if (o1 == null) {
          return -1;
        }
        if (o2 == null) {
          return 1;
        }
        if (o1.getInputTime() == o2.getInputTime()) {
          if (o1.getName() == null) {
            return -1;
          }
          if (o2.getName() == null) {
            return 1;
          }
          return o1.getName().compareTo(o2.getName());
        }
        return Long.compare(o1.getInputTime(), o2.getInputTime());
      }
    });
    return output;
  }
}
