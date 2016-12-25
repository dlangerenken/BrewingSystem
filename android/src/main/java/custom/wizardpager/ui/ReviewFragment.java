/*
 * 
 */
package custom.wizardpager.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import custom.wizardpager.model.AbstractWizardModel;
import custom.wizardpager.model.ModelCallbacks;
import custom.wizardpager.model.ReviewItem;
import custom.wizardpager.page.Page;
import se.brewingsystem.android.R;
import se.brewingsystem.android.utilities.ReviewAdapter;


/**
 * The Class ReviewFragment.
 */
public class ReviewFragment extends Fragment implements ModelCallbacks {
    
    /** The m callbacks. */
    private Callbacks mCallbacks;
    
    /** The m wizard model. */
    private AbstractWizardModel mWizardModel;
    
    /** The m current review items. */
    private List<ReviewItem> mCurrentReviewItems;
    
    /** The m review adapter. */
    private ReviewAdapter mReviewAdapter;

    /**
     * Instantiates a new review fragment.
     */
    public ReviewFragment() {
    }

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCurrentReviewItems == null) {
            mCurrentReviewItems = new ArrayList<>();
        }
        mReviewAdapter = new ReviewAdapter(getActivity(), mCurrentReviewItems);
    }

    /**
     * On create view.
     *
     * @param inflater the inflater
     * @param container the container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);

        TextView titleView = (TextView) rootView.findViewById(android.R.id.title);
        titleView.setText(R.string.review);
        titleView.setTextColor(getResources().getColor(R.color.review_color));

        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        listView.setAdapter(mReviewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallbacks.onEditScreenAfterReview(mCurrentReviewItems.get(position).getPageKey());
            }
        });
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        return rootView;
    }

    /**
     * On attach.
     *
     * @param activity the activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Fragment parentFragment = getParentFragment();
        if (parentFragment == null || !(parentFragment instanceof Callbacks)) {
            if (!(activity instanceof Callbacks)) {
                throw new ClassCastException("Activity/ParentFragment must implement Callbacks");
            }
            mCallbacks = (Callbacks) activity;
        } else {
            mCallbacks = (Callbacks) parentFragment;
        }

        mWizardModel = mCallbacks.onGetModel();
        mWizardModel.registerListener(this);
        onPageTreeChanged();
    }

    /**
     * On page tree changed.
     */
    @Override
    public void onPageTreeChanged() {
        onPageDataChanged(null);
    }

    /**
     * On detach.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;

        mWizardModel.unregisterListener(this);
    }

    /**
     * On page data changed.
     *
     * @param changedPage the changed page
     */
    @Override
    public void onPageDataChanged(Page changedPage) {
        ArrayList<ReviewItem> reviewItems = new ArrayList<>();
        for (Page page : mWizardModel.getCurrentPageSequence()) {
            page.getReviewItems(reviewItems);
        }
        Collections.sort(reviewItems, new Comparator<ReviewItem>() {
            @Override
            public int compare(ReviewItem a, ReviewItem b) {
                return a.getWeight() > b.getWeight() ? +1 : a.getWeight() < b.getWeight() ? -1 : 0;
            }
        });


        if (mCurrentReviewItems == null) {
            mCurrentReviewItems = new ArrayList<>();
        } else {
            mCurrentReviewItems.clear();
        }

        mCurrentReviewItems.addAll(reviewItems);
        if (mReviewAdapter != null) {
            mReviewAdapter.notifyDataSetInvalidated();
        }
    }

    /**
     * The Interface Callbacks.
     */
    public interface Callbacks {
        
        /**
         * On get model.
         *
         * @return the abstract wizard model
         */
        AbstractWizardModel onGetModel();

        /**
         * On edit screen after review.
         *
         * @param pageKey the page key
         */
        void onEditScreenAfterReview(String pageKey);
    }

}
