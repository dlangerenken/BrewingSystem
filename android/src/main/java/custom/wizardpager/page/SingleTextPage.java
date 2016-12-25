/*
 * 
 */
package custom.wizardpager.page;


import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;

import custom.wizardpager.model.ModelCallbacks;
import custom.wizardpager.model.ReviewItem;
import custom.wizardpager.ui.SingleTextFragment;


/**
 * A page asking for a text field.
 */
public class SingleTextPage extends Page {
    
    /** The m desc. */
    private String mDesc = "";
    
    /** The m should use text box. */
    private final boolean mShouldUseTextBox;

    /**
     * Instantiates a new single text page.
     *
     * @param callbacks the callbacks
     * @param title the title
     * @param shouldUseTextBox the should use text box
     */
    public SingleTextPage(ModelCallbacks callbacks, String title, boolean shouldUseTextBox) {
        super(callbacks, title);
        mShouldUseTextBox = shouldUseTextBox;
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#createFragment()
     */
    @Override
    public Fragment createFragment() {
        return SingleTextFragment.create(getKey(), mShouldUseTextBox);
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#getReviewItems(ArrayList)
     */
    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY), getKey(), -1));
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#isCompleted()
     */
    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    /**
     * Sets the description.
     *
     * @param desc the desc
     * @return the single text page
     */
    public SingleTextPage setDescription(String desc) {
        mDesc = desc;
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
     * Gets the desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return mDesc;
    }
}