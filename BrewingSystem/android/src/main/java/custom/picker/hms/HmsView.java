/*
 * 
 */
package custom.picker.hms;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import se.brewingsystem.android.R;


/**
 * The Class HmsView.
 */
public class HmsView extends LinearLayout {

    /** The m hours ones. */
    private TextView mHoursOnes;
    
    /** The m minutes ones. */
    private TextView mMinutesOnes;
    
    /** The m minutes tens. */
    private TextView mMinutesTens;
    
    /** The m seconds ones. */
    private TextView mSecondsOnes;
    
    /** The m seconds tens. */
    private TextView mSecondsTens;

    /**
     * Instantiate an HmsView.
     *
     * @param context the Context in which to inflate the View
     */
    public HmsView(Context context) {
        this(context, null);
    }

    /**
     * Instantiate an HmsView.
     *
     * @param context the Context in which to inflate the View
     * @param attrs   attributes that define the title color
     */
    public HmsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * On finish inflate.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHoursOnes = (TextView) findViewById(R.id.hours_ones);
        mMinutesTens = (TextView) findViewById(R.id.minutes_tens);
        mMinutesOnes = (TextView) findViewById(R.id.minutes_ones);
        mSecondsTens = (TextView) findViewById(R.id.seconds_tens);
        mSecondsOnes = (TextView) findViewById(R.id.seconds_ones);
    }

    /**
     * Set the time shown.
     *
     * @param hoursOnesDigit   the ones digit of the hours TextView
     * @param minutesTensDigit the tens digit of the minutes TextView
     * @param minutesOnesDigit the ones digit of the minutes TextView
     * @param secondsTensDigit the tens digit of the seconds TextView
     * @param secondsOnesDigit the ones digit of the seconds TextView
     */
    public void setTime(int hoursOnesDigit, int minutesTensDigit, int minutesOnesDigit, int secondsTensDigit,
                        int secondsOnesDigit) {
        if (mHoursOnes != null) {
            mHoursOnes.setText(String.format("%d", hoursOnesDigit));
        }
        if (mMinutesTens != null) {
            mMinutesTens.setText(String.format("%d", minutesTensDigit));
        }
        if (mMinutesOnes != null) {
            mMinutesOnes.setText(String.format("%d", minutesOnesDigit));
        }
        if (mSecondsTens != null) {
            mSecondsTens.setText(String.format("%d", secondsTensDigit));
        }
        if (mSecondsOnes != null) {
            mSecondsOnes.setText(String.format("%d", secondsOnesDigit));
        }
    }
}