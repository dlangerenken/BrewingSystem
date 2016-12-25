/*
 *
 */
package interfaces;


/**
 * An Interface that represents objects that can be in either valid or invalid state, e.g. a
 * MashingPlan that is invalid if two rests overlap
 */
public interface IObjectWithValidationStatus {

  /**
   * Checks the validation status of this element.
   *
   * @return A string containing the error message if the element is not valid, null else wise
   */
  public String getErrorMessage();
}
