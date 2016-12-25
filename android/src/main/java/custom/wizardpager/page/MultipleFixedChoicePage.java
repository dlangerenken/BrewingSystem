/*
 * 
 */
package custom.wizardpager.page;


import android.support.v4.app.Fragment;

import java.util.ArrayList;

import custom.wizardpager.model.ModelCallbacks;
import custom.wizardpager.model.ReviewItem;
import custom.wizardpager.ui.MultipleChoiceFragment;


/**
 * A page offering the user a number of non-mutually exclusive choices.
 */
public class MultipleFixedChoicePage extends SingleFixedChoicePage {
    
    /**
     * Instantiates a new multiple fixed choice page.
     *
     * @param callbacks the callbacks
     * @param title the title
     */
    public MultipleFixedChoicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.SingleFixedChoicePage#createFragment()
     */
    @Override
    public Fragment createFragment() {
        return MultipleChoiceFragment.create(getKey());
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.SingleFixedChoicePage#getReviewItems(ArrayList)
     */
    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        StringBuilder sb = new StringBuilder();

        ArrayList<String> selections = mData.getStringArrayList(SIMPLE_DATA_KEY);
        if (selections != null && selections.size() > 0) {
            for (String selection : selections) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(selection);
            }
        }

        dest.add(new ReviewItem(getTitle(), sb.toString(), getKey()));
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.SingleFixedChoicePage#isCompleted()
     */
    @Override
    public boolean isCompleted() {
        ArrayList<String> selections = mData.getStringArrayList(SIMPLE_DATA_KEY);
        return selections != null && selections.size() > 0;
    }
}
