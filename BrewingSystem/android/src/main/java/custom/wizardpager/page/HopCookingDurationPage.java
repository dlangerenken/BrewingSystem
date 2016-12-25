/*
 * 
 */
package custom.wizardpager.page;


import android.support.v4.app.Fragment;

import java.util.ArrayList;

import custom.wizardpager.model.ModelCallbacks;
import custom.wizardpager.model.ReviewItem;
import custom.wizardpager.ui.DurationFragment;
import se.brewingsystem.android.utilities.CommonUtilities;


/**
 * A page asking for the duration
 */
public class HopCookingDurationPage extends Page {

    /** The duration. */
    private long duration = 0;

    /**
     * Instantiates a new single text page.
     *
     * @param callbacks the callbacks
     * @param title the title
     */
    public HopCookingDurationPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#createFragment()
     */
    @Override
    public Fragment createFragment() {
        return DurationFragment.create(getKey());
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#getReviewItems(ArrayList)
     */
    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        long longValue = mData.getLong(SIMPLE_DATA_KEY);
        String durationText = CommonUtilities.convertSecondsToHMmSs(longValue);
        dest.add(new ReviewItem(getTitle(), durationText, getKey(), -1));
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#isCompleted()
     */
    @Override
    public boolean isCompleted() {
        return mData.getLong(SIMPLE_DATA_KEY) > 0;
    }

    /**
     * Sets the duration.
     *
     * @param duration the duration
     * @return the HopCookingDurationPage
     */
    public HopCookingDurationPage setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public String getContent() {
        return mData.getString(SIMPLE_DATA_KEY);
    }

    /**
     * Gets the duration.
     *
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }
}