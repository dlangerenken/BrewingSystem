/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package custom.picker;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import se.brewingsystem.android.R;


/**
 * Displays text with no padding at the top.
 */
public class ZeroTopPaddingTextView extends TextView {

    /** The Constant NORMAL_FONT_PADDING_RATIO. */
    private static final float NORMAL_FONT_PADDING_RATIO = 0.328f;
    // the bold fontface has less empty space on the top
    /** The Constant BOLD_FONT_PADDING_RATIO. */
    private static final float BOLD_FONT_PADDING_RATIO = 0.208f;

    /** The Constant NORMAL_FONT_BOTTOM_PADDING_RATIO. */
    private static final float NORMAL_FONT_BOTTOM_PADDING_RATIO = 0.25f;
    // the bold fontface has less empty space on the top
    /** The Constant BOLD_FONT_BOTTOM_PADDING_RATIO. */
    private static final float BOLD_FONT_BOTTOM_PADDING_RATIO = 0.208f;

    // pre-ICS (Droid Sans) has weird empty space on the bottom
    /** The Constant PRE_ICS_BOTTOM_PADDING_RATIO. */
    private static final float PRE_ICS_BOTTOM_PADDING_RATIO = 0.233f;

    /** The Constant SAN_SERIF_BOLD. */
    private static final Typeface SAN_SERIF_BOLD = Typeface.create("san-serif", Typeface.BOLD);
    
    /** The Constant SAN_SERIF_CONDENSED_BOLD. */
    private static final Typeface SAN_SERIF_CONDENSED_BOLD = Typeface.create("sans-serif-condensed", Typeface.BOLD);

    /** The m padding right. */
    private int mPaddingRight = 0;

    /** The decimal seperator. */
    private String decimalSeperator = "";
    
    /** The time seperator. */
    private String timeSeperator = "";

    /**
     * Instantiates a new zero top padding text view.
     *
     * @param context the context
     */
    public ZeroTopPaddingTextView(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new zero top padding text view.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public ZeroTopPaddingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new zero top padding text view.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public ZeroTopPaddingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        setIncludeFontPadding(false);
        updatePadding();
    }

    /**
     * Inits the.
     */
    private void init() {
        decimalSeperator = getResources().getString(R.string.number_picker_seperator);
        timeSeperator = getResources().getString(R.string.time_picker_time_seperator);
    }

    /**
     * Update padding.
     */
    void updatePadding() {
        float paddingRatio = NORMAL_FONT_PADDING_RATIO;
        float bottomPaddingRatio = NORMAL_FONT_BOTTOM_PADDING_RATIO;
        if (getPaint().getTypeface() != null && getPaint().getTypeface().equals(Typeface.DEFAULT_BOLD)) {
            paddingRatio = BOLD_FONT_PADDING_RATIO;
            bottomPaddingRatio = BOLD_FONT_BOTTOM_PADDING_RATIO;
        }
        if (getTypeface() != null && getTypeface().equals(SAN_SERIF_BOLD)) {
            paddingRatio = BOLD_FONT_PADDING_RATIO;
            bottomPaddingRatio = BOLD_FONT_BOTTOM_PADDING_RATIO;
        }
        if (getTypeface() != null && getTypeface().equals(SAN_SERIF_CONDENSED_BOLD)) {
            paddingRatio = BOLD_FONT_PADDING_RATIO;
            bottomPaddingRatio = BOLD_FONT_BOTTOM_PADDING_RATIO;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                getText() != null &&
                (getText().toString().equals(decimalSeperator) ||
                        getText().toString().equals(timeSeperator))) {
            bottomPaddingRatio = PRE_ICS_BOTTOM_PADDING_RATIO;
        }
        // no need to scale by display density because getTextSize() already returns the font
        // height in px
        setPadding(0, (int) (-paddingRatio * getTextSize()), mPaddingRight,
                (int) (-bottomPaddingRatio * getTextSize()));
    }

    /**
     * Update padding for bold date.
     */
    public void updatePaddingForBoldDate() {
        float paddingRatio = BOLD_FONT_PADDING_RATIO;
        float bottomPaddingRatio = BOLD_FONT_BOTTOM_PADDING_RATIO;
        // no need to scale by display density because getTextSize() already returns the font
        // height in px
        setPadding(0, (int) (-paddingRatio * getTextSize()), mPaddingRight,
                (int) (-bottomPaddingRatio * getTextSize()));
    }

    /**
     * Sets the padding right.
     *
     * @param padding the new padding right
     */
    public void setPaddingRight(int padding) {
        mPaddingRight = padding;
        updatePadding();
    }
}