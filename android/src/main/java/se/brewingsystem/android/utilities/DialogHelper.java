/*
 * 
 */
package se.brewingsystem.android.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.inject.Inject;
import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;

import de.keyboardsurfer.android.widget.crouton.Style;
import se.brewingsystem.android.R;
import se.brewingsystem.android.gcm.PushUtilities;



/**
 * Created by Daniel on 30.01.14.
 */
public class DialogHelper {

    /** The m pref utilities. */
    private final PrefUtilities mPrefUtilities;
    
    /** The m push utilities. */
    private final PushUtilities mPushUtilities;

    /** The m context. */
    private final Activity mContext;
    /**
     * Instantiates a new dialog helper.
     *
     * @param activity the activity
     * @param prefUtilities the pref utilities
     * @param pushUtilities the push utilities
     */
    @Inject
    public DialogHelper(Activity activity, PrefUtilities prefUtilities, PushUtilities pushUtilities) {
        mContext = activity;
        mPrefUtilities = prefUtilities;
        mPushUtilities = pushUtilities;
    }

    /**
     * Sets the server url dialog.
     */
    public void setServerUrlDialog() {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.check_valid_url_dialog, null, false);
        final ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.input_layout);
        final View progress = view.findViewById(R.id.m_progress);
        final FloatingLabelEditText input = (FloatingLabelEditText) view.findViewById(R.id.input);
        input.setInputWidgetText(mPrefUtilities.getServerURL());

        final MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .customView(view)
                .title(R.string.set_server_url_header)
                .positiveText(mContext.getString(R.string.server_url_dialog_positive_text))
                .negativeText(mContext.getString(R.string.server_url_dialog_negative_text))
                .callback(new MaterialDialog.Callback() {
                              @Override
                              public void onNegative(MaterialDialog materialDialog) {
                                  materialDialog.cancel();
                              }

                              @Override
                              public void onPositive(MaterialDialog materialDialog) {
                                  String url = input.getInputWidgetText().toString();
                                  if (CommonUtilities.isValidUrl(url)) {
                                      crossFade(progress, input, mContext.getResources().getInteger(
                                              android.R.integer.config_shortAnimTime));
                                      mPrefUtilities.setServerUrl(url);
                                      mPushUtilities.relog();
                                      materialDialog.dismiss();
                                  } else {
                                      invalidUrl(false, view, viewGroup, input, progress);
                                  }
                              }
                          }

                )
                .build();
        dialog.show();
    }

    /**
     * Invalid url.
     *
     * @param hideProgressBar the hide progress bar
     * @param view the view
     * @param viewGroup the view group
     * @param input the input
     * @param progress the progress
     */
    private void invalidUrl(final boolean hideProgressBar, final View view, final ViewGroup viewGroup, final View input, final View progress) {
        Animation shake = AnimationUtils.loadAnimation(mContext,
                R.anim.shake);
        view.startAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (hideProgressBar) {
                    crossFade(input, progress, mContext.getResources().getInteger(
                            android.R.integer.config_shortAnimTime));
                }
                CroutonHelper.showText(mContext, mContext.getString(R.string.not_valid_format), Style.ALERT, viewGroup);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * Cross fade.
     *
     * @param firstView the first view
     * @param secondView the second view
     * @param duration the duration
     */
    public static void crossFade(final View firstView, final View secondView, int duration) {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        ViewPropertyAnimator.animate(firstView).alpha(0f);
        firstView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        ViewPropertyAnimator.animate(firstView).alpha(1f).setDuration(duration).setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        ViewPropertyAnimator.animate(secondView).alpha(0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                secondView.setVisibility(View.GONE);
            }
        });

    }
}
