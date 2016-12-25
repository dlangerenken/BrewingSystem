/*
 * 
 */
package custom.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;


/**
 * User: derek Date: 5/2/13 Time: 9:19 PM.
 */
public class AutoScrollHorizontalScrollView extends HorizontalScrollView {

    /**
     * Instantiates a new auto scroll horizontal scroll view.
     *
     * @param context the context
     */
    public AutoScrollHorizontalScrollView(Context context) {
        super(context);
    }

    /**
     * Instantiates a new auto scroll horizontal scroll view.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public AutoScrollHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new auto scroll horizontal scroll view.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public AutoScrollHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * On layout.
     *
     * @param changed the changed
     * @param l the l
     * @param t the t
     * @param r the r
     * @param b the b
     */
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.fullScroll(FOCUS_RIGHT);
    }
}
