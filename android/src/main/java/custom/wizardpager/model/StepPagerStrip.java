/*
 * 
 */
package custom.wizardpager.model;

/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import se.brewingsystem.android.R;


/**
 * The Class StepPagerStrip.
 */
public class StepPagerStrip extends View {
    
    /** The Constant ATTRS. */
    private static final int[] ATTRS = new int[]{
            android.R.attr.gravity
    };
    
    /** The m page count. */
    private int mPageCount;
    
    /** The m current page. */
    private int mCurrentPage;

    /** The m gravity. */
    private int mGravity = Gravity.START | Gravity.TOP;
    
    /** The m tab width. */
    private final float mTabWidth;
    
    /** The m tab height. */
    private final float mTabHeight;
    
    /** The m tab spacing. */
    private final float mTabSpacing;

    /** The m prev tab paint. */
    private final Paint mPrevTabPaint;
    
    /** The m selected tab paint. */
    private final Paint mSelectedTabPaint;
    
    /** The m selected last tab paint. */
    private final Paint mSelectedLastTabPaint;
    
    /** The m next tab paint. */
    private final Paint mNextTabPaint;

    /** The m temp rect f. */
    private final RectF mTempRectF = new RectF();

    //private Scroller mScroller;

    /** The m on page selected listener. */
    private OnPageSelectedListener mOnPageSelectedListener;

    /**
     * Instantiates a new step pager strip.
     *
     * @param context the context
     */
    public StepPagerStrip(Context context) {
        this(context, null, 0);
    }

    /**
     * Instantiates a new step pager strip.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public StepPagerStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new step pager strip.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public StepPagerStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mGravity = a.getInteger(0, mGravity);
        a.recycle();

        final Resources res = getResources();
        mTabWidth = res.getDimensionPixelSize(R.dimen.step_pager_tab_width);
        mTabHeight = res.getDimensionPixelSize(R.dimen.step_pager_tab_height);
        mTabSpacing = res.getDimensionPixelSize(R.dimen.step_pager_tab_spacing);

        mPrevTabPaint = new Paint();
        mPrevTabPaint.setColor(res.getColor(R.color.step_pager_previous_tab_color));

        mSelectedTabPaint = new Paint();
        mSelectedTabPaint.setColor(res.getColor(R.color.step_pager_selected_tab_color));

        mSelectedLastTabPaint = new Paint();
        mSelectedLastTabPaint.setColor(res.getColor(R.color.step_pager_selected_last_tab_color));

        mNextTabPaint = new Paint();
        mNextTabPaint.setColor(res.getColor(R.color.step_pager_next_tab_color));
    }

    /**
     * Sets the on page selected listener.
     *
     * @param onPageSelectedListener the new on page selected listener
     */
    public void setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener) {
        mOnPageSelectedListener = onPageSelectedListener;
    }

    /**
     * On draw.
     *
     * @param canvas the canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPageCount == 0) {
            return;
        }

        float totalWidth = mPageCount * (mTabWidth + mTabSpacing) - mTabSpacing;
        float totalLeft;
        boolean fillHorizontal = false;

        switch (mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                totalLeft = (getWidth() - totalWidth) / 2;
                break;
            case Gravity.END:
                totalLeft = getWidth() - getPaddingRight() - totalWidth;
                break;
            case Gravity.FILL_HORIZONTAL:
                totalLeft = getPaddingLeft();
                fillHorizontal = true;
                break;
            default:
                totalLeft = getPaddingLeft();
        }

        switch (mGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.CENTER_VERTICAL:
                mTempRectF.top = (int) (getHeight() - mTabHeight) / 2;
                break;
            case Gravity.BOTTOM:
                mTempRectF.top = getHeight() - getPaddingBottom() - mTabHeight;
                break;
            default:
                mTempRectF.top = getPaddingTop();
        }

        mTempRectF.bottom = mTempRectF.top + mTabHeight;

        float tabWidth = mTabWidth;
        if (fillHorizontal) {
            tabWidth = (getWidth() - getPaddingRight() - getPaddingLeft()
                    - (mPageCount - 1) * mTabSpacing) / mPageCount;
        }

        for (int i = 0; i < mPageCount; i++) {
            mTempRectF.left = totalLeft + (i * (tabWidth + mTabSpacing));
            mTempRectF.right = mTempRectF.left + tabWidth;
            canvas.drawRect(mTempRectF, i < mCurrentPage
                    ? mPrevTabPaint
                    : (i > mCurrentPage
                    ? mNextTabPaint
                    : (i == mPageCount - 1
                    ? mSelectedLastTabPaint
                    : mSelectedTabPaint)));
        }
    }

    /**
     * On measure.
     *
     * @param widthMeasureSpec the width measure spec
     * @param heightMeasureSpec the height measure spec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                View.resolveSize(
                        (int) (mPageCount * (mTabWidth + mTabSpacing) - mTabSpacing)
                                + getPaddingLeft() + getPaddingRight(),
                        widthMeasureSpec),
                View.resolveSize(
                        (int) mTabHeight
                                + getPaddingTop() + getPaddingBottom(),
                        heightMeasureSpec));
    }


    /**
     * On touch event.
     *
     * @param event the event
     * @return true, if successful
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnPageSelectedListener != null) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    int position = hitTest(event.getX());
                    if (position >= 0) {
                        mOnPageSelectedListener.onPageStripSelected(position);
                    }
                    return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * Hit test.
     *
     * @param x the x
     * @return the int
     */
    private int hitTest(float x) {
        if (mPageCount == 0) {
            return -1;
        }

        float totalWidth = mPageCount * (mTabWidth + mTabSpacing) - mTabSpacing;
        float totalLeft;
        boolean fillHorizontal = false;

        switch (mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                totalLeft = (getWidth() - totalWidth) / 2;
                break;
            case Gravity.END:
                totalLeft = getWidth() - getPaddingRight() - totalWidth;
                break;
            case Gravity.FILL_HORIZONTAL:
                totalLeft = getPaddingLeft();
                fillHorizontal = true;
                break;
            default:
                totalLeft = getPaddingLeft();
        }

        float tabWidth = mTabWidth;
        if (fillHorizontal) {
            tabWidth = (getWidth() - getPaddingRight() - getPaddingLeft()
                    - (mPageCount - 1) * mTabSpacing) / mPageCount;
        }

        float totalRight = totalLeft + (mPageCount * (tabWidth + mTabSpacing));
        if (x >= totalLeft && x <= totalRight && totalRight > totalLeft) {
            return (int) (((x - totalLeft) / (totalRight - totalLeft)) * mPageCount);
        } else {
            return -1;
        }
    }

    /**
     * Sets the current page.
     *
     * @param currentPage the new current page
     */
    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
        invalidate();
    }


    /**
     * Sets the page count.
     *
     * @param count the new page count
     */
    public void setPageCount(int count) {
        mPageCount = count;
        invalidate();
    }

    /**
     * The listener interface for receiving onPageSelected events.
     * The class that is interested in processing a onPageSelected
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addOnPageSelectedListener<code> method. When
     * the onPageSelected event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnPageSelectedEvent
     */
    public static interface OnPageSelectedListener {
        
        /**
         * On page strip selected.
         *
         * @param position the position
         */
        void onPageStripSelected(int position);
    }

}
