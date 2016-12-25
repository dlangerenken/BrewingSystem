/*
 * 
 */
package custom.wizardpager.page;


import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import custom.wizardpager.model.ModelCallbacks;
import custom.wizardpager.model.PageList;
import custom.wizardpager.model.ReviewItem;
import custom.wizardpager.ui.SingleChoiceFragment;


/**
 * A page representing a branching point in the wizard. Depending on which choice is selected, the
 * next set of steps in the wizard may change.
 */
public class BranchPage extends SingleFixedChoicePage {
    
    /** The m branches. */
    private final List<Branch> mBranches = new ArrayList<>();

    /**
     * Instantiates a new branch page.
     *
     * @param callbacks the callbacks
     * @param title the title
     */
    public BranchPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#findByKey(java.lang.String)
     */
    @Override
    public Page findByKey(String key) {
        if (getKey().equals(key)) {
            return this;
        }

        for (Branch branch : mBranches) {
            Page found = branch.childPageList.findByKey(key);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#flattenCurrentPageSequence(java.util.ArrayList)
     */
    @Override
    public void flattenCurrentPageSequence(ArrayList<Page> destination) {
        super.flattenCurrentPageSequence(destination);
        for (Branch branch : mBranches) {
            if (branch.choice.equals(mData.getString(SIMPLE_DATA_KEY))) {
                branch.childPageList.flattenCurrentPageSequence(destination);
                break;
            }
        }
    }

    /**
     * Adds the branch.
     *
     * @param choice the choice
     * @param childPages the child pages
     * @return the branch page
     */
    public BranchPage addBranch(String choice, Page... childPages) {
        PageList childPageList = new PageList(childPages);
        for (Page page : childPageList) {
            page.setParentKey(choice);
        }
        mBranches.add(new Branch(choice, childPageList));
        return this;
    }

    /**
     * Adds the branch.
     *
     * @param choice the choice
     * @return the branch page
     */
    public BranchPage addBranch(String choice) {
        mBranches.add(new Branch(choice, new PageList()));
        return this;
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.SingleFixedChoicePage#createFragment()
     */
    @Override
    public Fragment createFragment() {
        return SingleChoiceFragment.create(getKey());
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.SingleFixedChoicePage#getOptionAt(int)
     */
    public String getOptionAt(int position) {
        return mBranches.get(position).choice;
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.SingleFixedChoicePage#getOptionCount()
     */
    public int getOptionCount() {
        return mBranches.size();
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.SingleFixedChoicePage#getReviewItems(ArrayList)
     */
    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY), getKey()));
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.SingleFixedChoicePage#isCompleted()
     */
    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.Page#notifyDataChanged()
     */
    @Override
    public void notifyDataChanged() {
        mCallbacks.onPageTreeChanged();
        super.notifyDataChanged();
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.page.SingleFixedChoicePage#setValue(java.lang.String)
     */
    public BranchPage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }

    /**
     * The Class Branch.
     */
    private static class Branch {
        
        /** The choice. */
        public final String choice;
        
        /** The child page list. */
        public final PageList childPageList;

        /**
         * Instantiates a new branch.
         *
         * @param choice the choice
         * @param childPageList the child page list
         */
        private Branch(String choice, PageList childPageList) {
            this.choice = choice;
            this.childPageList = childPageList;
        }
    }
}
