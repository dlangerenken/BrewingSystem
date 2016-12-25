/*
 * 
 */
package custom.wizardpager.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marvinlabs.widget.floatinglabel.instantpicker.FloatingLabelInstantPicker;
import com.marvinlabs.widget.floatinglabel.instantpicker.JavaTimeInstant;

import custom.picker.FloatingLabelHMSPicker;
import custom.picker.hms.HmsPickerBuilder;
import custom.picker.hms.HmsPickerDialogFragment;
import custom.wizardpager.page.HopCookingDurationPage;
import custom.wizardpager.page.Page;
import se.brewingsystem.android.R;


/**
 * Created by Daniel on 09.12.2014.
 */
public class DurationFragment extends PageFragment {

    /** The Constant ARG_KEY. */
    private static final String ARG_KEY = "key";

    /** The hopCooking duration page page. */
    private HopCookingDurationPage mPage;

    /**
     * Instantiates a new duration fragment.
     */
    public DurationFragment() {
    }

    /**
     * Creates the.
     *
     * @param key the key
     * @return the single text fragment
     */
    public static DurationFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        DurationFragment fragment = new DurationFragment();
        fragment.setArguments(args);
        return fragment;
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
        String mKey = args.getString(ARG_KEY);
        mPage = (HopCookingDurationPage) mCallbacks.onGetPage(mKey);
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
        View rootView = inflater.inflate(R.layout.fragment_page_duration, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());
        final FloatingLabelHMSPicker durationPicker = (FloatingLabelHMSPicker) rootView.findViewById(R.id.editText_input);
        durationPicker.setWidgetListener(new FloatingLabelInstantPicker.OnWidgetEventListener<JavaTimeInstant>() {
            @Override
            public void onShowInstantPickerDialog(FloatingLabelInstantPicker<JavaTimeInstant> javaTimeInstantFloatingLabelInstantPicker) {
                HmsPickerBuilder builder = new HmsPickerBuilder();
                Context mContext = getActivity();
                if (mContext == null) {
                    return;
                }
                builder.setTitle(mContext.getString(R.string.page_duration_title));
                builder.setFragmentManager(getFragmentManager()).setTargetFragment(DurationFragment.this);
                builder.addHmsPickerDialogHandler(new HmsPickerDialogFragment.HmsPickerDialogHandler() {
                    @Override
                    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
                        JavaTimeInstant timeInstant = new JavaTimeInstant(hours, minutes, seconds);
                        durationPicker.setSelectedInstant(timeInstant);
                        int time = timeInstant.getSecondOfMinute() + (timeInstant.getMinuteOfHour() + timeInstant.getHourOfDay() * 60) * 60;
                        mPage.getData().putLong(Page.SIMPLE_DATA_KEY, time * 1000);
                        mPage.notifyDataChanged();
                    }
                });
                builder.show();
            }
        });

        return rootView;
    }

}