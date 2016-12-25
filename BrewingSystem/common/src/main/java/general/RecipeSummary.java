/*
 * 
 */
package general;

import java.io.Serializable;


/**
 * The Class RecipeSummary.
 */
public class RecipeSummary implements Serializable {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The m title. */
  private String mTitle;

  /** The m description. */
  private String mDescription;

  /** The m date. */
  private long mDate;
  
  /** The m id. */
  private String mId;
  
  /**
   * Instantiates a new recipe summary.
   */
  public RecipeSummary() {}
  
  /**
   * Instantiates a new recipe summary.
   *
   * @param title the title
   * @param description the description
   * @param date the date
   * @param id the id
   */
  public RecipeSummary(final String title, final String description, final long date,
      final String id) {
    mTitle = title;
    mDescription = description;
    mDate = date;
    mId = id;
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
    RecipeSummary other = (RecipeSummary) obj;
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
    if (mId == null) {
      if (other.mId != null) {
        return false;
      }
    } else if (!mId.equals(other.mId)) {
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
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return mId;
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
    result = prime * result + ((mId == null) ? 0 : mId.hashCode());
    result = prime * result + ((mTitle == null) ? 0 : mTitle.hashCode());
    return result;
  }

  /**
   * Sets the date.
   *
   * @param date the new date
   */
  public void setDate(final long date) {
    mDate = date;
  }

  /**
   * Sets the description.
   *
   * @param desc the new description
   */
  public void setDescription(final String desc) {
    mDescription = desc;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(final String id) {
    mId = id;
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle(final String title) {
    mTitle = title;
  }
}
