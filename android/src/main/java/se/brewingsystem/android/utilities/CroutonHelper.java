package se.brewingsystem.android.utilities;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * CroutonHelper for cancelling all toasts if a new toast is shown
 */
public class CroutonHelper {
    /**
     * Cancels all crouton toasts if a new one is shown
     */
    private static void cancel() {
        Crouton.cancelAllCroutons();
    }

    /**
     * See Crouton Crouton.showText(...)
     * @param activity
     * @param textResourceId
     * @param style
     */
    public static void showText(android.app.Activity activity, int textResourceId, de.keyboardsurfer.android.widget.crouton.Style style) {
        cancel();
        Crouton.showText(activity, textResourceId, style);
    }

    /**
     * See Crouton Crouton.showText(...)
     * @param activity
     * @param textResourceId
     * @param style
     * @param viewGroup
     */
    public static void showText(android.app.Activity activity, int textResourceId, de.keyboardsurfer.android.widget.crouton.Style style, android.view.ViewGroup viewGroup) {
        cancel();
        Crouton.showText(activity, textResourceId, style, viewGroup);
    }

    /**
     * See Crouton Crouton.showText(...)
     * @param activity
     * @param textResourceId
     * @param style
     * @param viewGroupResId
     */
    public static void showText(android.app.Activity activity, int textResourceId, de.keyboardsurfer.android.widget.crouton.Style style, int viewGroupResId) {
        cancel();
        Crouton.showText(activity, textResourceId, style, viewGroupResId);
    }

    /**
     * See Crouton Crouton.showText(...)
     * @param activity
     * @param text
     * @param style
     */
    public static void showText(android.app.Activity activity, java.lang.CharSequence text, de.keyboardsurfer.android.widget.crouton.Style style) {
        cancel();
        Crouton.showText(activity, text, style); }

    /**
     * See Crouton Crouton.showText(...)
     * @param activity
     * @param text
     * @param style
     * @param viewGroup
     */
    public static void showText(android.app.Activity activity, java.lang.CharSequence text, de.keyboardsurfer.android.widget.crouton.Style style, android.view.ViewGroup viewGroup) {
        cancel();
        Crouton.showText(activity, text, style, viewGroup);}

    /**
     * See Crouton Crouton.showText(...)
     * @param activity
     * @param text
     * @param style
     * @param viewGroupResId
     */
    public static void showText(android.app.Activity activity, java.lang.CharSequence text, de.keyboardsurfer.android.widget.crouton.Style style, int viewGroupResId) {
        cancel();
        Crouton.showText(activity, text, style, viewGroupResId);}

    /**
     * See Crouton Crouton.showText(...)
     * @param activity
     * @param text
     * @param style
     * @param viewGroupResId
     * @param configuration
     */
    public static void showText(android.app.Activity activity, java.lang.CharSequence text, de.keyboardsurfer.android.widget.crouton.Style style, int viewGroupResId, de.keyboardsurfer.android.widget.crouton.Configuration configuration) {
        cancel();
        Crouton.showText(activity, text, style, viewGroupResId, configuration); }

}
