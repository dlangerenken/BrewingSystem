/*
 * 
 */
package custom.wizardpager.page;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

import custom.wizardpager.model.ModelCallbacks;
import custom.wizardpager.model.ReviewItem;
import custom.wizardpager.ui.SingleChoiceFragment;


/**
 * A page offering the user a number of mutually exclusive choices.
 */
public class SingleFixedChoicePage extends Page {
    
    /** The m choices. */
    private final ArrayList<String> mChoices = new ArrayList<>();

    /**
     * Instantiates a new single fixed choice page.
     *
     * @param callbacks the callbacks
     * @param title the title
     */
    SingleFixedChoicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#createFragment()
     */
    @Override
    public Fragment createFragment() {
        return SingleChoiceFragment.create(getKey());
    }

    /**
     * Gets the option at.
     *
     * @param position the position
     * @return the option at
     */
    public String getOptionAt(int position) {
        return mChoices.get(position);
    }

    /**
     * Gets the option count.
     *
     * @return the option count
     */
    public int getOptionCount() {
        return mChoices.size();
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#getReviewItems(ArrayList)
     */
    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY), getKey()));
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#isCompleted()
     */
    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    /**
     * Sets the choices.
     *
     * @param choices the choices
     * @return the single fixed choice page
     */
    public SingleFixedChoicePage setChoices(String... choices) {
        mChoices.addAll(Arrays.asList(choices));
        return this;
    }

    /**
     * Sets the value.
     *
     * @param value the value
     * @return the single fixed choice page
     */
    public SingleFixedChoicePage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }
}
