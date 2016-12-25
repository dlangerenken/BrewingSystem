/*
 * 
 */
package custom.picker.number;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
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

import java.math.BigDecimal;

import se.brewingsystem.android.R;


/**
 * The Class NumberPicker.
 */
public class NumberPicker extends LinearLayout implements Button.OnClickListener,
        Button.OnLongClickListener {

    /** The Constant CLICKED_DECIMAL. */
    private static final int CLICKED_DECIMAL = 10;
    
    /** The Constant SIGN_POSITIVE. */
    private static final int SIGN_POSITIVE = 0;
    
    /** The Constant SIGN_NEGATIVE. */
    private static final int SIGN_NEGATIVE = 1;
    
    /** The m numbers. */
    private final Button[] mNumbers = new Button[10];
    
    /** The m context. */
    private final Context mContext;
    
    /** The m input size. */
    private final int mInputSize = 20;
    
    /** The m input. */
    private int[] mInput = new int[mInputSize];
    
    /** The m input pointer. */
    private int mInputPointer = -1;
    
    /** The m left. */
    private Button mLeft;
    
    /** The m right. */
    private Button mRight;
    
    /** The m delete. */
    private ImageButton mDelete;
    
    /** The m entered number. */
    private NumberView mEnteredNumber;
    
    /** The m divider. */
    private View mDivider;
    
    /** The m label. */
    private TextView mLabel;
    
    /** The m error. */
    private NumberPickerErrorTextView mError;
    
    /** The m sign. */
    private int mSign;
    
    /** The m label text. */
    private String mLabelText = "";
    
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
     * Instantiates a NumberPicker object.
     *
     * @param context the Context required for creation
     */
    public NumberPicker(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a NumberPicker object.
     *
     * @param context the Context required for creation
     * @param attrs   additional attributes that define custom colors, selectors, and backgrounds.
     */
    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.number_picker_view, this);

        // Init defaults
        mTextColor = getResources().getColorStateList(R.color.dialog_text_color_holo_light);
        mKeyBackgroundResId = R.drawable.key_background_light;
        mButtonBackgroundResId = R.drawable.button_background_light;
        mDeleteDrawableSrcResId = R.drawable.ic_backspace_light;
        mDividerColor = getResources().getColor(R.color.default_divider_color_light);
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
        if (mLeft != null) {
            mLeft.setTextColor(mTextColor);
            mLeft.setBackgroundResource(mKeyBackgroundResId);
        }
        if (mRight != null) {
            mRight.setTextColor(mTextColor);
            mRight.setBackgroundResource(mKeyBackgroundResId);
        }
        if (mDelete != null) {
            mDelete.setBackgroundResource(mButtonBackgroundResId);
            mDelete.setImageDrawable(getResources().getDrawable(mDeleteDrawableSrcResId));
        }
        if (mLabel != null) {
            mLabel.setTextColor(mTextColor);
        }
    }

    /**
     * On finish inflate.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDivider = findViewById(R.id.divider);
        mError = (NumberPickerErrorTextView) findViewById(R.id.error);

        for (int i = 0; i < mInput.length; i++) {
            mInput[i] = -1;
        }

        View v1 = findViewById(R.id.first);
        View v2 = findViewById(R.id.second);
        View v3 = findViewById(R.id.third);
        View v4 = findViewById(R.id.fourth);
        mEnteredNumber = (NumberView) findViewById(R.id.number_text);
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
        setLeftRightEnabled();

        for (int i = 0; i < 10; i++) {
            mNumbers[i].setOnClickListener(this);
            mNumbers[i].setText(String.format("%d", i));
            mNumbers[i].setTag(R.id.numbers_key, i);
        }
        updateNumber();

        Resources res = mContext.getResources();
        mLeft.setText(res.getString(R.string.number_picker_plus_minus));
        mRight.setText(res.getString(R.string.number_picker_seperator));
        mLeft.setOnClickListener(this);
        mRight.setOnClickListener(this);
        mLabel = (TextView) findViewById(R.id.label);
        mSign = SIGN_POSITIVE;

        // Set the correct label state
        showLabel();

        restyleViews();
        updateKeypad();
    }

    /**
     * Using View.GONE, View.VISIBILE, or View.INVISIBLE, set the visibility of the plus/minus indicator
     *
     * @param visiblity an int using Android's View.* convention
     */
    public void setPlusMinusVisibility(int visiblity) {
        if (mLeft != null) {
            mLeft.setVisibility(visiblity);
        }
    }

    /**
     * Using View.GONE, View.VISIBILE, or View.INVISIBLE, set the visibility of the decimal indicator
     *
     * @param visiblity an int using Android's View.* convention
     */
    public void setDecimalVisibility(int visiblity) {
        if (mRight != null) {
            mRight.setVisibility(visiblity);
        }
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
     * Expose the NumberView in order to set errors.
     *
     * @return the NumberView
     */
    public NumberPickerErrorTextView getErrorView() {
        return mError;
    }

    /**
     * On click.
     *
     * @param v the v
     */
    @Override
    public void onClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        mError.hideImmediately();
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
        if (val != null) {
            // A number was pressed
            addClickedNumber(val);
        } else if (v == mDelete) {
            if (mInputPointer >= 0) {
                System.arraycopy(mInput, 1, mInput, 0, mInputPointer);
                mInput[mInputPointer] = -1;
                mInputPointer--;
            }
        } else if (v == mLeft) {
            onLeftClicked();
        } else if (v == mRight) {
            onRightClicked();
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
        mError.hideImmediately();
        if (v == mDelete) {
            mDelete.setPressed(false);
            reset();
            updateKeypad();
            return true;
        }
        return false;
    }

    /**
     * Update keypad.
     */
    private void updateKeypad() {
        // Update state of keypad
        // Update the number
        updateLeftRightButtons();
        updateNumber();
        // enable/disable the "set" key
        enableSetButton();
        // Update the backspace button
        updateDeleteButton();
    }

    /**
     * Set the text displayed in the small label.
     *
     * @param labelText the String to set as the label
     */
    public void setLabelText(String labelText) {
        mLabelText = labelText;
        showLabel();
    }

    /**
     * Show label.
     */
    private void showLabel() {
        if (mLabel != null) {
            mLabel.setText(mLabelText);
        }
    }

    /**
     * Reset all inputs.
     */
    void reset() {
        for (int i = 0; i < mInputSize; i++) {
            mInput[i] = -1;
        }
        mInputPointer = -1;
        updateNumber();
    }

    // Update the number displayed in the picker:
    /**
     * Update number.
     */
    void updateNumber() {
        String numberString = getEnteredNumberString();
        numberString = numberString.replaceAll("\\-", "");
        String[] split = numberString.split("\\.");
        if (split.length >= 2) {
            if (split[0].equals("")) {
                mEnteredNumber.setNumber("0", split[1], containsDecimal(),
                        mSign == SIGN_NEGATIVE);
            } else {
                mEnteredNumber.setNumber(split[0], split[1], containsDecimal(),
                        mSign == SIGN_NEGATIVE);
            }
        } else if (split.length == 1) {
            mEnteredNumber.setNumber(split[0], "", containsDecimal(),
                    mSign == SIGN_NEGATIVE);
        } else if (numberString.equals(".")) {
            mEnteredNumber.setNumber("0", "", true, mSign == SIGN_NEGATIVE);
        }
    }

    /**
     * Sets the left right enabled.
     */
    void setLeftRightEnabled() {
        mLeft.setEnabled(true);
        mRight.setEnabled(canAddDecimal());
        if (!canAddDecimal()) {
            mRight.setContentDescription(null);
        }
    }

    /**
     * Adds the clicked number.
     *
     * @param val the val
     */
    private void addClickedNumber(int val) {
        if (mInputPointer < mInputSize - 1) {
            // For 0 we need to check if we have a value of zero or not
            if (mInput[0] == 0 && mInput[1] == -1 && !containsDecimal() && val != CLICKED_DECIMAL) {
                mInput[0] = val;
            } else {
                System.arraycopy(mInput, 0, mInput, 1, mInputPointer + 1);
                mInputPointer++;
                mInput[0] = val;
            }
        }
    }

    /**
     * Clicking on the bottom left button will toggle the sign.
     */
    private void onLeftClicked() {
        if (mSign == SIGN_POSITIVE) {
            mSign = SIGN_NEGATIVE;
        } else {
            mSign = SIGN_POSITIVE;
        }
    }

    /**
     * Clicking on the bottom right button will add a decimal point.
     */
    private void onRightClicked() {
        if (canAddDecimal()) {
            addClickedNumber(CLICKED_DECIMAL);
        }
    }

    /**
     * Contains decimal.
     *
     * @return true, if successful
     */
    private boolean containsDecimal() {
        boolean containsDecimal = false;
        for (int i : mInput) {
            if (i == 10) {
                containsDecimal = true;
            }
        }
        return containsDecimal;
    }

    /**
     * Checks if the user allowed to click on the right button.
     *
     * @return true or false if the user is able to add a decimal or not
     */
    private boolean canAddDecimal() {
        return !containsDecimal();
    }

    /**
     * Gets the entered number string.
     *
     * @return the entered number string
     */
    private String getEnteredNumberString() {
        String value = "";
        for (int i = mInputPointer; i >= 0; i--) {
            if (mInput[i] == CLICKED_DECIMAL) {
                value += ".";
            } else if (mInput[i] != -1){
                value += mInput[i];
            }
        }
        return value;
    }

    /**
     * Returns the number inputted by the user.
     *
     * @return a double representing the entered number
     */
    public double getEnteredNumber() {
        String value = "0";
        for (int i = mInputPointer; i >= 0; i--) {
            if (mInput[i] == -1) {
                break;
            } else if (mInput[i] == CLICKED_DECIMAL) {
                value += ".";
            } else {
                value += mInput[i];
            }
        }
        if (mSign == SIGN_NEGATIVE) {
            value = "-" + value;
        }
        return Double.parseDouble(value);
    }

    /**
     * Update left right buttons.
     */
    private void updateLeftRightButtons() {
        mRight.setEnabled(canAddDecimal());
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

        // If the user entered 1 digits or more
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
     * Returns the number as currently inputted by the user.
     *
     * @return an int representation of the number with no decimal
     */
    public int getNumber() {
        String numberString = Double.toString(getEnteredNumber());
        String[] split = numberString.split("\\.");
        return Integer.parseInt(split[0]);
    }

    /**
     * Returns the decimal following the number.
     *
     * @return a double representation of the decimal value
     */
    public double getDecimal() {
        return BigDecimal.valueOf(getEnteredNumber()).divideAndRemainder(BigDecimal.ONE)[1].doubleValue();
    }

    /**
     * Returns whether the number is positive or negative.
     *
     * @return true or false whether the number is positive or negative
     */
    public boolean getIsNegative() {
        return mSign == SIGN_NEGATIVE;
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
        state.mSign = mSign;
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
        mSign = savedState.mSign;
        updateKeypad();
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
        
        /** The m sign. */
        int mSign;

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
            mSign = in.readInt();
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
            dest.writeInt(mSign);
        }
    }
}
