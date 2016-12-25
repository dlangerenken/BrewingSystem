/*
 * 
 */
package se.brewingsystem.android.ui;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.inject.Inject;

import java.lang.reflect.Field;

import roboguice.fragment.RoboFragment;
import se.brewingsystem.android.network.INetworkCommunication;



/**
 * Created by Daniel on 14.12.2014.
 * Based on http://stackoverflow.com/questions/14900738/nested-fragments-disappear-during-transition-animation
 */
public class BaseFragment extends RoboFragment {
    
    /** The Constant TAG. */
    static final String TAG = BaseFragment.class.getName();

    // Arbitrary value; set it to some reasonable default
    /** The Constant DEFAULT_CHILD_ANIMATION_DURATION. */
    private static final int DEFAULT_CHILD_ANIMATION_DURATION = 1000;
    
    /** The m network communication. */
    @Inject
    INetworkCommunication mNetworkCommunication;

    /**
     * Gets the next animation duration.
     *
     * @param fragment the fragment
     * @param defValue the def value
     * @return the next animation duration
     */
    private static long getNextAnimationDuration(Fragment fragment, long defValue) {
        try {
            // Attempt to get the resource ID of the next animation that
            // will be applied to the given fragment.
            Field nextAnimField = Fragment.class.getDeclaredField("mNextAnim");
            nextAnimField.setAccessible(true);
            int nextAnimResource = nextAnimField.getInt(fragment);
            Animation nextAnim = AnimationUtils.loadAnimation(fragment.getActivity(), nextAnimResource);

            // ...and if it can be loaded, return that animation's duration
            return (nextAnim == null) ? defValue : nextAnim.getDuration();
        } catch (NoSuchFieldException | IllegalAccessException | Resources.NotFoundException ex) {
            return defValue;
        }
    }

    /**
     * On create animation.
     *
     * @param transit the transit
     * @param enter the enter
     * @param nextAnim the next anim
     * @return the animation
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        final Fragment parent = getParentFragment();

        // Apply the workaround only if this is a child fragment, and the parent
        // is being removed.
        if (!enter && parent != null && parent.isRemoving()) {
            // This is a workaround for the bug where child fragments disappear when
            // the parent is removed (as all children are first removed from the parent)
            // See https://code.google.com/p/android/issues/detail?id=55228
            Animation doNothingAnim = new AlphaAnimation(1, 1);
            doNothingAnim.setDuration(getNextAnimationDuration(parent, DEFAULT_CHILD_ANIMATION_DURATION));
            return doNothingAnim;
        } else {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

}
