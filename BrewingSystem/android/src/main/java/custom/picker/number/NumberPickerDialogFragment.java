/*
 * 
 */
package custom.picker.number;


import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Vector;

import se.brewingsystem.android.R;


/**
 * Dialog to set alarm time.
 */
public class NumberPickerDialogFragment extends DialogFragment {

    /** The Constant REFERENCE_KEY. */
    private static final String REFERENCE_KEY = "NumberPickerDialogFragment_ReferenceKey";
    
    /** The Constant MIN_NUMBER_KEY. */
    private static final String MIN_NUMBER_KEY = "NumberPickerDialogFragment_MinNumberKey";
    
    /** The Constant MAX_NUMBER_KEY. */
    private static final String MAX_NUMBER_KEY = "NumberPickerDialogFragment_MaxNumberKey";
    
    /** The Constant PLUS_MINUS_VISIBILITY_KEY. */
    private static final String PLUS_MINUS_VISIBILITY_KEY = "NumberPickerDialogFragment_PlusMinusVisibilityKey";
    
    /** The Constant DECIMAL_VISIBILITY_KEY. */
    private static final String DECIMAL_VISIBILITY_KEY = "NumberPickerDialogFragment_DecimalVisibilityKey";
    
    /** The Constant LABEL_TEXT_KEY. */
    private static final String LABEL_TEXT_KEY = "NumberPickerDialogFragment_LabelTextKey";

    /** The m picker. */
    private NumberPicker mPicker;

    /** The m reference. */
    private int mReference = -1;
    
    /** The m divider color. */
    private int mDividerColor;
    
    /** The m text color. */
    private ColorStateList mTextColor;
    
    /** The m label text. */
    private String mLabelText = "";
    
    /** The m button background res id. */
    private int mButtonBackgroundResId;
    
    /** The m dialog background res id. */
    private int mDialogBackgroundResId;

    /** The m min number. */
    private Integer mMinNumber = null;
    
    /** The m max number. */
    private Integer mMaxNumber = null;
    
    /** The m plus minus visibility. */
    private int mPlusMinusVisibility = View.VISIBLE;
    
    /** The m decimal visibility. */
    private int mDecimalVisibility = View.VISIBLE;
    
    /** The m number picker dialog handlers. */
    private Vector<NumberPickerDialogHandler> mNumberPickerDialogHandlers = new Vector<>();

    /**
     * Create an instance of the Picker (used internally).
     *
     * @param reference           an (optional) user-defined reference, helpful when tracking multiple Pickers
     * @param minNumber           (optional) the minimum possible number
     * @param maxNumber           (optional) the maximum possible number
     * @param plusMinusVisibility (optional) View.VISIBLE, View.INVISIBLE, or View.GONE
     * @param decimalVisibility   (optional) View.VISIBLE, View.INVISIBLE, or View.GONE
     * @param labelText           (optional) text to add as a label
     * @return a Picker!
     */
    public static NumberPickerDialogFragment newInstance(int reference, Integer minNumber,
                                                         Integer maxNumber, Integer plusMinusVisibility, Integer decimalVisibility, String labelText) {
        final NumberPickerDialogFragment frag = new NumberPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(REFERENCE_KEY, reference);
        if (minNumber != null) {
            args.putInt(MIN_NUMBER_KEY, minNumber);
        }
        if (maxNumber != null) {
            args.putInt(MAX_NUMBER_KEY, maxNumber);
        }
        if (plusMinusVisibility != null) {
            args.putInt(PLUS_MINUS_VISIBILITY_KEY, plusMinusVisibility);
        }
        if (decimalVisibility != null) {
            args.putInt(DECIMAL_VISIBILITY_KEY, decimalVisibility);
        }
        if (labelText != null) {
            args.putString(LABEL_TEXT_KEY, labelText);
        }
        frag.setArguments(args);
        return frag;
    }

    /**
     * On save instance state.
     *
     * @param outState the out state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(REFERENCE_KEY)) {
            mReference = args.getInt(REFERENCE_KEY);
        }
        if (args != null && args.containsKey(PLUS_MINUS_VISIBILITY_KEY)) {
            mPlusMinusVisibility = args.getInt(PLUS_MINUS_VISIBILITY_KEY);
        }
        if (args != null && args.containsKey(DECIMAL_VISIBILITY_KEY)) {
            mDecimalVisibility = args.getInt(DECIMAL_VISIBILITY_KEY);
        }
        if (args != null && args.containsKey(MIN_NUMBER_KEY)) {
            mMinNumber = args.getInt(MIN_NUMBER_KEY);
        }
        if (args != null && args.containsKey(MAX_NUMBER_KEY)) {
            mMaxNumber = args.getInt(MAX_NUMBER_KEY);
        }
        if (args != null && args.containsKey(LABEL_TEXT_KEY)) {
            mLabelText = args.getString(LABEL_TEXT_KEY);
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        // Init defaults
        mTextColor = getResources().getColorStateList(R.color.dialog_text_color_holo_light);
        mButtonBackgroundResId = R.drawable.button_background_light;
        mDividerColor = getResources().getColor(R.color.default_divider_color_light);
        mDialogBackgroundResId = R.drawable.dialog_full_holo_light;
    }

    /**
     * On create view.
     *
     * @param inflater the inflater
     * @param container the container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.number_picker_dialog, container, false);
        Button mSet = (Button) v.findViewById(R.id.set_button);
        Button mCancel = (Button) v.findViewById(R.id.cancel_button);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mPicker = (NumberPicker) v.findViewById(R.id.number_picker);
        mPicker.setSetButton(mSet);
        mSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double number = mPicker.getEnteredNumber();
                if (mMinNumber != null && mMaxNumber != null && (number < mMinNumber || number > mMaxNumber)) {
                    String errorText = String.format(getString(R.string.min_max_error), mMinNumber, mMaxNumber);
                    mPicker.getErrorView().setText(errorText);
                    mPicker.getErrorView().show();
                    return;
                } else if (mMinNumber != null && number < mMinNumber) {
                    String errorText = String.format(getString(R.string.min_error), mMinNumber);
                    mPicker.getErrorView().setText(errorText);
                    mPicker.getErrorView().show();
                    return;
                } else if (mMaxNumber != null && number > mMaxNumber) {
                    String errorText = String.format(getString(R.string.max_error), mMaxNumber);
                    mPicker.getErrorView().setText(errorText);
                    mPicker.getErrorView().show();
                    return;
                }
                for (NumberPickerDialogHandler handler : mNumberPickerDialogHandlers) {
                    handler.onDialogNumberSet(mReference, mPicker.getNumber(), mPicker.getDecimal(),
                            mPicker.getIsNegative(), number);
                }
                final Activity activity = getActivity();
                final Fragment fragment = getTargetFragment();
                if (activity instanceof NumberPickerDialogHandler) {
                    final NumberPickerDialogHandler act =
                            (NumberPickerDialogHandler) activity;
                    act.onDialogNumberSet(mReference, mPicker.getNumber(), mPicker.getDecimal(),
                            mPicker.getIsNegative(), number);
                } else if (fragment instanceof NumberPickerDialogHandler) {
                    final NumberPickerDialogHandler frag = (NumberPickerDialogHandler) fragment;
                    frag.onDialogNumberSet(mReference, mPicker.getNumber(), mPicker.getDecimal(),
                            mPicker.getIsNegative(), number);
                }
                dismiss();
            }
        });

        View mDividerOne = v.findViewById(R.id.divider_1);
        View mDividerTwo = v.findViewById(R.id.divider_2);
        mDividerOne.setBackgroundColor(mDividerColor);
        mDividerTwo.setBackgroundColor(mDividerColor);
        mSet.setTextColor(mTextColor);
        mSet.setBackgroundResource(mButtonBackgroundResId);
        mCancel.setTextColor(mTextColor);
        mCancel.setBackgroundResource(mButtonBackgroundResId);
        getDialog().getWindow().setBackgroundDrawableResource(mDialogBackgroundResId);

        mPicker.setDecimalVisibility(mDecimalVisibility);
        mPicker.setPlusMinusVisibility(mPlusMinusVisibility);
        mPicker.setLabelText(mLabelText);
        return v;
    }

    /**
     * Attach a Vector of handlers to be notified in addition to the Fragment's Activity and target Fragment.
     *
     * @param handlers a Vector of handlers
     */
    public void setNumberPickerDialogHandlers(Vector<NumberPickerDialogHandler> handlers) {
        mNumberPickerDialogHandlers = handlers;
    }

    /**
     * This interface allows objects to register for the Picker's set action.
     */
    public interface NumberPickerDialogHandler {

        /**
         * On dialog number set.
         *
         * @param reference the reference
         * @param number the number
         * @param decimal the decimal
         * @param isNegative the is negative
         * @param fullNumber the full number
         */
        void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber);
    }
}