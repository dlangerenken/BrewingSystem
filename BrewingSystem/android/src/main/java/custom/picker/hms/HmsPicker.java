/*
 * 
 */
package custom.picker.hms;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import se.brewingsystem.android.R;



/**
 * The Class HmsPicker.
 */
public class HmsPicker extends LinearLayout implements Button.OnClickListener, Button.OnLongClickListener {

    /** The m numbers. */
    private final Button[] mNumbers = new Button[10];
    
    /** The m input size. */
    private final int mInputSize = 5;
    
    /** The m input. */
    private int[] mInput = new int[mInputSize];
    
    /** The m input pointer. */
    private int mInputPointer = -1;
    
    /** The m delete. */
    private ImageButton mDelete;
    
    /** The m left. */
    private Button mLeft;
    
    /** The m right. */
    private Button mRight;
    
    /** The m entered hms. */
    private HmsView mEnteredHms;
    
    /** The m divider. */
    private View mDivider;
    
    /** The m seconds label. */
    private TextView mHoursLabel, mMinutesLabel, mSecondsLabel;
    
    /** The m set button. */
    private Button mSetButton;
    
    /** The m text color. */
    private final ColorStateList mTextColor;
    
    /** The m key background res id. */
    private final int mKeyBackgroundResId;
    
    /** The m button background res id. */
    private final int mButtonBackgroundResId;
    
    /** The m divider color. */
    private final int mDividerColor;
    
    /** The m delete drawable src res id. */
    private final int mDeleteDrawableSrcResId;

    /**
     * Instantiates an HmsPicker object.
     *
     * @param context the Context required for creation
     */
    public HmsPicker(Context context) {
        this(context, null);
    }

    /**
     * Instantiates an HmsPicker object.
     *
     * @param context the Context required for creation
     * @param attrs   additional attributes that define custom colors, selectors, and backgrounds.
     */
    public HmsPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        Context mContext = context;
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.hms_picker_view, this);

        // Init defaults
        mTextColor = getResources().getColorStateList(R.color.dialog_text_color_holo_light);
        mKeyBackgroundResId = R.drawable.key_background_light;
        mButtonBackgroundResId = R.drawable.button_background_light;
        mDividerColor = getResources().getColor(R.color.default_divider_color_light);
        mDeleteDrawableSrcResId = R.drawable.ic_backspace_light;
    }

    /**
     * Restyle views.
     */
    private void restyleViews() {
        for (Button number : mNumbers) {
            if (number != null) {
                number.setTextColor(mTextColor);
                number.setBackgroundResource(mKeyBackgroundResId);
            }
        }
        if (mDivider != null) {
            mDivider.setBackgroundColor(mDividerColor);
        }
        if (mHoursLabel != null) {
            mHoursLabel.setTextColor(mTextColor);
            mHoursLabel.setBackgroundResource(mKeyBackgroundResId);
        }
        if (mMinutesLabel != null) {
            mMinutesLabel.setTextColor(mTextColor);
            mMinutesLabel.setBackgroundResource(mKeyBackgroundResId);
        }
        if (mSecondsLabel != null) {
            mSecondsLabel.setTextColor(mTextColor);
            mSecondsLabel.setBackgroundResource(mKeyBackgroundResId);
        }
        if (mDelete != null) {
            mDelete.setBackgroundResource(mButtonBackgroundResId);
            mDelete.setImageDrawable(getResources().getDrawable(mDeleteDrawableSrcResId));
        }
    }

    /**
     * On finish inflate.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View v1 = findViewById(R.id.first);
        View v2 = findViewById(R.id.second);
        View v3 = findViewById(R.id.third);
        View v4 = findViewById(R.id.fourth);
        mEnteredHms = (HmsView) findViewById(R.id.hms_text);
        mDelete = (ImageButton) findViewById(R.id.delete);
        mDelete.setOnClickListener(this);
        mDelete.setOnLongClickListener(this);

        mNumbers[1] = (Button) v1.findViewById(R.id.key_left);
        mNumbers[2] = (Button) v1.findViewById(R.id.key_middle);
        mNumbers[3] = (Button) v1.findViewById(R.id.key_right);

        mNumbers[4] = (Button) v2.findViewById(R.id.key_left);
        mNumbers[5] = (Button) v2.findViewById(R.id.key_middle);
        mNumbers[6] = (Button) v2.findViewById(R.id.key_right);

        mNumbers[7] = (Button) v3.findViewById(R.id.key_left);
        mNumbers[8] = (Button) v3.findViewById(R.id.key_middle);
        mNumbers[9] = (Button) v3.findViewById(R.id.key_right);

        mLeft = (Button) v4.findViewById(R.id.key_left);
        mNumbers[0] = (Button) v4.findViewById(R.id.key_middle);
        mRight = (Button) v4.findViewById(R.id.key_right);
        setLeftRightEnabled(false);

        for (int i = 0; i < 10; i++) {
            mNumbers[i].setOnClickListener(this);
            mNumbers[i].setText(String.format("%d", i));
            mNumbers[i].setTag(R.id.numbers_key, i);
        }
        updateHms();

        mHoursLabel = (TextView) findViewById(R.id.hours_label);
        mMinutesLabel = (TextView) findViewById(R.id.minutes_label);
        mSecondsLabel = (TextView) findViewById(R.id.seconds_label);
        mDivider = findViewById(R.id.divider);

        restyleViews();
        updateKeypad();
    }

    /**
     * Update the delete button to determine whether it is able to be clicked.
     */
    void updateDeleteButton() {
        boolean enabled = mInputPointer != -1;
        if (mDelete != null) {
            mDelete.setEnabled(enabled);
        }
    }

    /**
     * On click.
     *
     * @param v the v
     */
    @Override
    public void onClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        doOnClick(v);
        updateDeleteButton();
    }

    /**
     * Do on click.
     *
     * @param v the v
     */
    void doOnClick(View v) {
        Integer val = (Integer) v.getTag(R.id.numbers_key);
        // A number was pressed
        if (val != null) {
            addClickedNumber(val);
        } else if (v == mDelete) {
            if (mInputPointer >= 0) {
                System.arraycopy(mInput, 1, mInput, 0, mInputPointer);
                mInput[mInputPointer] = 0;
                mInputPointer--;
            }
        }
        updateKeypad();
    }

    /**
     * On long click.
     *
     * @param v the v
     * @return true, if successful
     */
    @Override
    public boolean onLongClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        if (v == mDelete) {
            mDelete.setPressed(false);

            reset();
            updateKeypad();
            return true;
        }
        return false;
    }

    /**
     * Reset all inputs and the hours:minutes:seconds.
     */
    void reset() {
        for (int i = 0; i < mInputSize; i++) {
            mInput[i] = 0;
        }
        mInputPointer = -1;
        updateHms();
    }

    /**
     * Update keypad.
     */
    private void updateKeypad() {
        // Update the h:m:s
        updateHms();
        // enable/disable the "set" key
        enableSetButton();
        // Update the backspace button
        updateDeleteButton();
    }

    /**
     * Update the time displayed in the picker:
     * <p/>
     * Put "-" in digits that was not entered by passing -1
     * <p/>
     * Hide digit by passing -2 (for highest hours digit only);.
     */
    void updateHms() {
        mEnteredHms.setTime(mInput[4], mInput[3], mInput[2], mInput[1], mInput[0]);
    }

    /**
     * Adds the clicked number.
     *
     * @param val the val
     */
    private void addClickedNumber(int val) {
        if (mInputPointer < mInputSize - 1) {
            System.arraycopy(mInput, 0, mInput, 1, mInputPointer + 1);
            mInputPointer++;
            mInput[0] = val;
        }
    }

    /**
     * Enable/disable the "Set" button.
     */
    private void enableSetButton() {
        if (mSetButton == null) {
            return;
        }

        // Nothing entered - disable
        if (mInputPointer == -1) {
            mSetButton.setEnabled(false);
            return;
        }

        mSetButton.setEnabled(mInputPointer >= 0);
    }

    /**
     * Expose the set button to allow communication with the parent Fragment.
     *
     * @param b the parent Fragment's "Set" button
     */
    public void setSetButton(Button b) {
        mSetButton = b;
        enableSetButton();
    }

    /**
     * Returns the hours as currently inputted by the user.
     *
     * @return the inputted hours
     */
    public int getHours() {
        return mInput[4];
    }

    /**
     * Returns the minutes as currently inputted by the user.
     *
     * @return the inputted minutes
     */
    public int getMinutes() {
        return mInput[3] * 10 + mInput[2];
    }

    /**
     * Return the seconds as currently inputted by the user.
     *
     * @return the inputted seconds
     */
    public int getSeconds() {
        return mInput[1] * 10 + mInput[0];
    }

    /**
     * On save instance state.
     *
     * @return the parcelable
     */
    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable parcel = super.onSaveInstanceState();
        final SavedState state = new SavedState(parcel);
        state.mInput = mInput;
        state.mInputPointer = mInputPointer;
        return state;
    }

    /**
     * On restore instance state.
     *
     * @param state the state
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        mInputPointer = savedState.mInputPointer;
        mInput = savedState.mInput;
        if (mInput == null) {
            mInput = new int[mInputSize];
            mInputPointer = -1;
        }
        updateKeypad();
    }

    /**
     * Returns the time in seconds.
     *
     * @return an int representing the time in seconds
     */
    public int getTime() {
        return mInput[4] * 3600 + mInput[3] * 600 + mInput[2] * 60 + mInput[1] * 10 + mInput[0];
    }

    /**
     * Save entry state.
     *
     * @param outState the out state
     * @param key the key
     */
    public void saveEntryState(Bundle outState, String key) {
        outState.putIntArray(key, mInput);
    }

    /**
     * Restore entry state.
     *
     * @param inState the in state
     * @param key the key
     */
    public void restoreEntryState(Bundle inState, String key) {
        int[] input = inState.getIntArray(key);
        if (input != null && mInputSize == input.length) {
            for (int i = 0; i < mInputSize; i++) {
                mInput[i] = input[i];
                if (mInput[i] != 0) {
                    mInputPointer = i;
                }
            }
            updateHms();
        }
    }

    /**
     * Sets the left right enabled.
     *
     * @param enabled the new left right enabled
     */
    void setLeftRightEnabled(boolean enabled) {
        mLeft.setEnabled(enabled);
        mRight.setEnabled(enabled);
        if (!enabled) {
            mLeft.setContentDescription(null);
            mRight.setContentDescription(null);
        }
    }

    /**
     * The Class SavedState.
     */
    private static class SavedState extends BaseSavedState {

        /** The Constant CREATOR. */
        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        
        /** The m input pointer. */
        int mInputPointer;
        
        /** The m input. */
        int[] mInput;
        
        /** The m am pm state. */
        int mAmPmState;

        /**
         * Instantiates a new saved state.
         *
         * @param superState the super state
         */
        public SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Instantiates a new saved state.
         *
         * @param in the in
         */
        private SavedState(Parcel in) {
            super(in);
            mInputPointer = in.readInt();
            in.readIntArray(mInput);
            mAmPmState = in.readInt();
        }

        /**
         * Write to parcel.
         *
         * @param dest the dest
         * @param flags the flags
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mInputPointer);
            dest.writeIntArray(mInput);
            dest.writeInt(mAmPmState);
        }
    }
}
