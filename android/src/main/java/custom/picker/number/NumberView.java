/*
 * 
 */
package custom.picker.number;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import se.brewingsystem.android.R;


/**
 * The Class NumberView.
 */
public class NumberView extends LinearLayout {

    /** The m decimal. */
    private TextView mNumber, mDecimal;
    
    /** The m decimal seperator. */
    private TextView mDecimalSeperator;
    
    /** The m minus label. */
    private TextView mMinusLabel;

    /**
     * Instantiate a NumberView.
     *
     * @param context the Context in which to inflate the View
     */
    public NumberView(Context context) {
        this(context, null);
    }

    /**
     * Instantiate a NumberView.
     *
     * @param context the Context in which to inflate the View
     * @param attrs   attributes that define the title color
     */
    public NumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * On finish inflate.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mNumber = (TextView) findViewById(R.id.number);
        mDecimal = (TextView) findViewById(R.id.decimal);
        mDecimalSeperator = (TextView) findViewById(R.id.decimal_separator);
        mMinusLabel = (TextView) findViewById(R.id.minus_label);
    }

    /**
     * Set the number shown.
     *
     * @param numbersDigit the non-decimal digits
     * @param decimalDigit the decimal digits
     * @param showDecimal  whether it's a decimal or not
     * @param isNegative   whether it's positive or negative
     */
    public void setNumber(String numbersDigit, String decimalDigit, boolean showDecimal,
                          boolean isNegative) {
        mMinusLabel.setVisibility(isNegative ? View.VISIBLE : View.GONE);
        if (mNumber != null) {
            if (numbersDigit.equals("")) {
                // Set to -
                mNumber.setText("-");
                mNumber.setEnabled(false);
                mNumber.setVisibility(View.VISIBLE);
            } else if (showDecimal) {
                // Set to bold
                mNumber.setText(numbersDigit);
                mNumber.setEnabled(true);
                mNumber.setVisibility(View.VISIBLE);
            } else {
                // Set to thin
                mNumber.setText(numbersDigit);
                mNumber.setEnabled(true);
                mNumber.setVisibility(View.VISIBLE);
            }
        }
        if (mDecimal != null) {
            // Hide digit
            if (decimalDigit.equals("")) {
                mDecimal.setVisibility(View.GONE);
            } else {
                mDecimal.setText(decimalDigit);
                mDecimal.setEnabled(true);
                mDecimal.setVisibility(View.VISIBLE);
            }
        }
        if (mDecimalSeperator != null) {
            // Hide separator
            mDecimalSeperator.setVisibility(showDecimal ? View.VISIBLE : View.GONE);
        }
    }
}