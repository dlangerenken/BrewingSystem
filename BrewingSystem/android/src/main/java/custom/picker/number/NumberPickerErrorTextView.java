/*
 * 
 */
package custom.picker.number;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import se.brewingsystem.android.R;


/**
 * User: derek Date: 6/21/13 Time: 10:37 AM.
 */
public class NumberPickerErrorTextView extends TextView {

    /** The Constant LENGTH_SHORT. */
    private static final long LENGTH_SHORT = 3000;
    
    /** The hide runnable. */
    private final Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    
    /** The fade in end handler. */
    private final Handler fadeInEndHandler = new Handler();

    /**
     * Instantiates a new number picker error text view.
     *
     * @param context the context
     */
    public NumberPickerErrorTextView(Context context) {
        super(context);
    }

    /**
     * Instantiates a new number picker error text view.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public NumberPickerErrorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new number picker error text view.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public NumberPickerErrorTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Show.
     */
    public void show() {
        fadeInEndHandler.removeCallbacks(hideRunnable);
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fadeInEndHandler.postDelayed(hideRunnable, LENGTH_SHORT);
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startAnimation(fadeIn);
    }

    /**
     * Hide.
     */
    void hide() {
        fadeInEndHandler.removeCallbacks(hideRunnable);
        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startAnimation(fadeOut);
    }

    /**
     * Hide immediately.
     */
    public void hideImmediately() {
        fadeInEndHandler.removeCallbacks(hideRunnable);
        setVisibility(View.INVISIBLE);
    }
}
