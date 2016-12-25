/*
 * 
 */
package custom.wizardpager.page;


import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import custom.wizardpager.model.GenericFactory;
import custom.wizardpager.model.ModelCallbacks;
import custom.wizardpager.model.ReviewItem;
import custom.wizardpager.ui.DynamicListPageFragment;


/**
 * Created by Daniel on 02.12.2014.
 *
 * @param <A> the generic type
 */
public class DynamicListPage<A> extends Page {

    /** The factory a. */
    private final GenericFactory<A> factoryA;

    /**
     * Instantiates a new dynamic list page.
     *
     * @param callbacks the callbacks
     * @param title the title
     * @param factoryA the factory a
     */
    DynamicListPage(ModelCallbacks callbacks, String title, GenericFactory<A> factoryA) {
        super(callbacks, title);
        this.factoryA = factoryA;
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#createFragment()
     */
    @Override
    public Fragment createFragment() {
        return DynamicListPageFragment.create(getKey(), factoryA);
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#getReviewItems(ArrayList)
     */
    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        ArrayList<A> serializableList = (ArrayList<A>) mData.getSerializable(COMPLEX_DATA_KEY);
        String reviewItem = factoryA.getReviewItem(serializableList);
        dest.add(new ReviewItem(getTitle(), reviewItem, getKey()));
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#isCompleted()
     */
    @Override
    public boolean isCompleted() {
        ArrayList<A> serializableList = (ArrayList<A>) mData.getSerializable(COMPLEX_DATA_KEY);
        return serializableList != null && serializableList.size() > 0;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public List<A> getContent() {
        return (List<A>) mData.getSerializable(COMPLEX_DATA_KEY);
    }
}
