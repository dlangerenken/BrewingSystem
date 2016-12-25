/*
 * 
 */
package custom.picker.hms;


import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Vector;

import se.brewingsystem.android.R;


/**
 * Dialog to set alarm time.
 */
public class HmsPickerDialogFragment extends DialogFragment {

    /** The Constant REFERENCE_KEY. */
    private static final String REFERENCE_KEY = "HmsPickerDialogFragment_ReferenceKey";
    
    /** The Constant TITLE_KEY. */
    private static final String TITLE_KEY = "title";
    
    /** The m picker. */
    private HmsPicker mPicker;

    /** The m reference. */
    private int mReference = -1;
    
    /** The m title. */
    private String mTitle = null;
    
    /** The m divider color. */
    private int mDividerColor;
    
    /** The m text color. */
    private ColorStateList mTextColor;
    
    /** The m button background res id. */
    private int mButtonBackgroundResId;
    
    /** The m dialog background res id. */
    private int mDialogBackgroundResId;
    
    /** The m hms picker dialog handlers. */
    private Vector<HmsPickerDialogHandler> mHmsPickerDialogHandlers = new Vector<>();

    /**
     * Create an instance of the Picker (used internally).
     *
     * @param reference an (optional) user-defined reference, helpful when tracking multiple Pickers
     * @param title the title
     * @return a Picker!
     */
    public static HmsPickerDialogFragment newInstance(int reference, String title) {
        final HmsPickerDialogFragment frag = new HmsPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(REFERENCE_KEY, reference);
        args.putString(TITLE_KEY, title);
        frag.setArguments(args);
        return frag;
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
            mTitle = args.getString(TITLE_KEY);
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);


        // Init defaults
        mTextColor = getResources().getColorStateList(R.color.app_color_text_light);
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

        View v = inflater.inflate(R.layout.hms_picker_dialog, container, false);
        ((TextView) v.findViewById(R.id.titleTextView)).setText(mTitle);
        Button mSet = (Button) v.findViewById(R.id.set_button);
        Button mCancel = (Button) v.findViewById(R.id.cancel_button);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mPicker = (HmsPicker) v.findViewById(R.id.hms_picker);
        mPicker.setSetButton(mSet);
        mSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (HmsPickerDialogHandler handler : mHmsPickerDialogHandlers) {
                    handler.onDialogHmsSet(mReference, mPicker.getHours(), mPicker.getMinutes(), mPicker.getSeconds());
                }
                final Activity activity = getActivity();
                final Fragment fragment = getTargetFragment();
                if (activity instanceof HmsPickerDialogHandler) {
                    final HmsPickerDialogHandler act =
                            (HmsPickerDialogHandler) activity;
                    act.onDialogHmsSet(mReference, mPicker.getHours(), mPicker.getMinutes(), mPicker.getSeconds());
                } else if (fragment instanceof HmsPickerDialogHandler) {
                    final HmsPickerDialogHandler frag =
                            (HmsPickerDialogHandler) fragment;
                    frag.onDialogHmsSet(mReference, mPicker.getHours(), mPicker.getMinutes(), mPicker.getSeconds());
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

        return v;
    }

    /**
     * Attach a Vector of handlers to be notified in addition to the Fragment's Activity and target Fragment.
     *
     * @param handlers a Vector of handlers
     */
    public void setHmsPickerDialogHandlers(Vector<HmsPickerDialogHandler> handlers) {
        mHmsPickerDialogHandlers = handlers;
    }

    /**
     * This interface allows objects to register for the Picker's set action.
     */
    public interface HmsPickerDialogHandler {

        /**
         * On dialog hms set.
         *
         * @param reference the reference
         * @param hours the hours
         * @param minutes the minutes
         * @param seconds the seconds
         */
        void onDialogHmsSet(int reference, int hours, int minutes, int seconds);
    }
}