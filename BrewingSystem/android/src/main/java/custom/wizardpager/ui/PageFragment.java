/*
 * 
 */
package custom.wizardpager.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;


/**
 * Created by Daniel on 09.12.2014.
 */
public class PageFragment extends Fragment {

    /** The callbacks. */
    PageFragmentCallbacks mCallbacks;

    /**
     * On attach.
     *
     * @param activity the activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Fragment parentFragment = getParentFragment();
        if (parentFragment == null || !(parentFragment instanceof PageFragmentCallbacks)) {
            if (!(activity instanceof PageFragmentCallbacks)) {
                throw new ClassCastException("Activity/ParentFragment must implement PageFragmentCallbacks");
            }
            mCallbacks = (PageFragmentCallbacks) activity;
        } else {
            mCallbacks = (PageFragmentCallbacks) parentFragment;
        }
    }

    /**
     * On detach.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
