package se.brewingsystem.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

/**
 * Animate-In Fragment for removing duplicate code in Procotol/Recipe Fragments
 */
public abstract class AnimateInFragment extends BaseFragment implements Animation.AnimationListener
{
    /** The Constant ARG_X. */
    private static final String ARG_X = "x";

    /** The Constant ARG_Y. */
    private static final String ARG_Y = "y";

    /** The Constant ARG_WIDTH. */
    private static final String ARG_WIDTH = "width";

    /** The Constant ARG_HEIGHT. */
    private static final String ARG_HEIGHT = "height";

    /**
     * This method puts args to the bundle which are used in multiple fragments
     * @param bundle bundle to add the args to
     * @param x x position of the view
     * @param y y position of the view
     * @param width width of the view
     * @param height height of the view
     */
    protected static void putArgs(Bundle bundle, int x, int y, int width, int height){
        bundle.putInt(ARG_X, x);
        bundle.putInt(ARG_Y, y);
        bundle.putInt(ARG_WIDTH, width);
        bundle.putInt(ARG_HEIGHT, height);
    }

    /**
     * This method returns LayoutParams from a bundle
     * @param bundle bundle to receive the params from (or null)
     * @return layout with margin and width/height
     */
    protected FrameLayout.LayoutParams getParams(Bundle bundle){
        FrameLayout.LayoutParams params = null;
        if (bundle != null) {
            params = new FrameLayout.LayoutParams(
                    bundle.getInt(ARG_WIDTH), bundle.getInt(ARG_HEIGHT));
            params.topMargin = bundle.getInt(ARG_Y);
            params.leftMargin = bundle.getInt(ARG_X);
        }
        return params;
    }


    protected void inflateAndAdd(View view, int layoutId, Bundle args){
        FrameLayout root = (FrameLayout) view;
        Context context = view.getContext();
        assert context != null;
        // This is how the fragment looks at first. Since the transition is one-way, we don't need to make
        // this a Scene.
        View item = LayoutInflater.from(context).inflate(layoutId, root, false);
        assert item != null;
        bind(item);
        // We adjust the position of the initial image with LayoutParams using the values supplied
        // as the fragment arguments.

        root.addView(item, getParams(args));
    }

    /**
     * Handles the transition between the views
     * @param layoutId id whichs hould be animated
     * @return rootView which is used for transition
     */
    protected View onAnimEnd(int layoutId){
        // This method is called at the end of the animation for the fragment transaction,
        // which is perfect time to start our Transition.
        ViewGroup rootView = (ViewGroup) getView();
        if (rootView == null){
            return null;
        }
        Log.i(TAG, "Fragment animation ended. Starting a Transition.");
        final Scene scene = Scene.getSceneForLayout(rootView,
                layoutId, getActivity());
        TransitionManager.go(scene);
        // Note that we need to bind views with data after we call TransitionManager.go().
        bind(scene.getSceneRoot());
        return rootView;
    }

    /**
     * Bind the views inside of parent with the fragment arguments.
     *
     * @param parent The parent of views to bind.
     */
    protected abstract void bind(View parent);

    /* (non-Javadoc)
     * @see java.se.brewingsystem.android.ui.BaseFragment#onCreateAnimation(int, boolean, int)
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(),
                enter ? android.R.anim.fade_in : android.R.anim.fade_out);
        // We bind a listener for the fragment transaction. We only bind it when
        // this fragment is entering.
        if (animation != null && enter) {
            animation.setAnimationListener(this);
        }
        return animation;
    }

    /**
     * On animation repeat.
     *
     * @param animation the animation
     */
    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    /**
     * On animation start.
     *
     * @param animation the animation
     */
    @Override
    public void onAnimationStart(Animation animation) {
    }

}
