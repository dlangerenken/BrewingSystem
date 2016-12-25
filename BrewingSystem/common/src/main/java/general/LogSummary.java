/*
 *
 */
package general;

import java.io.Serializable;


/**
 * the summary of a log, for indexation and user choice of logs
 */
public class LogSummary implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;


  /** The title. */
  private final String mTitle;

  /** The description. */
  private final String mDescription;

  /** The date of creation of the log. */
  private final long mDate;

  /** The id of the log. */
  private final int mId;

  /** The recipe id that was used in the log. */
  private final String mRecipeId;

  /**
   * Instantiates a new log summary.
   *
   * @param title the title of the summary
   * @param description short description of the log
   * @param date the date of log creation
   * @param id the id of the log
   * @param recipeId the recipe id of the log
   */
  public LogSummary(final String title, final String description, final long date,
      final int id, final String recipeId) {
    mTitle = title;
    mDescription = description;
    mDate = date;
    mId = id;
    mRecipeId = recipeId;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    LogSummary other = (LogSummary) obj;
    if (mDate != other.mDate) {
      return false;
    }
    if (mDescription == null) {
      if (other.mDescription != null) {
        return false;
      }
    } else if (!mDescription.equals(other.mDescription)) {
      return false;
    }
    if (mId != other.mId) {
      return false;
    }
    if (mTitle == null) {
      if (other.mTitle != null) {
        return false;
      }
    } else if (!mTitle.equals(other.mTitle)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the date.
   *
   * @return the date
   */
  public long getDate() {
    return mDate;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return mDescription;
  }

  /**
   * Gets the id of the log this summary describes.
   *
   * @return the id
   */
  public int getId() {
    return mId;
  }

  /**
   * Gets the recipe id that was used in the logged brewing process.
   *
   * @return the recipe id
   */
  public String getRecipeId() {
    return mRecipeId;
  }

  /**
   * Gets the title.
   *
   * @return the title
   */
  public String getTitle() {
    return mTitle;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (mDate ^ (mDate >>> 32));
    result = prime * result + ((mDescription == null) ? 0 : mDescription.hashCode());
    result = prime * result + mId;
    result = prime * result + ((mTitle == null) ? 0 : mTitle.hashCode());
    return result;
  }
}
