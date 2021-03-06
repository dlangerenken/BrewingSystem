/*
 * 
 */
package custom.wizardpager.page;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.marvinlabs.widget.floatinglabel.instantpicker.FloatingLabelInstantPicker;
import com.marvinlabs.widget.floatinglabel.instantpicker.JavaTimeInstant;

import java.util.ArrayList;

import custom.picker.FloatingLabelHMSPicker;
import custom.picker.FloatingLabelTemperaturePicker;
import custom.picker.GenericInstant;
import custom.picker.hms.HmsPickerBuilder;
import custom.picker.hms.HmsPickerDialogFragment;
import custom.picker.number.NumberPickerBuilder;
import custom.picker.number.NumberPickerDialogFragment;
import custom.wizardpager.model.GenericFactory;
import custom.wizardpager.model.ModelCallbacks;
import custom.wizardpager.model.TemperatureLevelAdapter;
import se.brewingsystem.android.R;
import general.TemperatureLevel;
import se.brewingsystem.android.utilities.MessageHelper;


/**
 * Created by Daniel on 09.12.2014.
 */
public class TemperatureLevelPage extends DynamicListPage<TemperatureLevel> {

    /**
     * Instantiates a new temperature level page.
     *
     * @param callbacks the callbacks
     * @param title the title
     * @param factoryA the factory a
     */
    private TemperatureLevelPage(ModelCallbacks callbacks, String title, GenericFactory<TemperatureLevel> factoryA) {
        super(callbacks, title, factoryA);
    }

    /**
     * Creates the.
     *
     * @param callbacks the callbacks
     * @param context the context
     * @return the temperature level page
     */
    public static TemperatureLevelPage create(ModelCallbacks callbacks, Context context) {
        return new TemperatureLevelPage(callbacks, context.getString(R.string.temperature_level_page_title), new TemperatureLevelGenericFactory());
    }

    /**
     * A factory for creating TemperatureLevelGeneric objects.
     */
    private static class TemperatureLevelGenericFactory implements GenericFactory<TemperatureLevel> {
        
        /**
         * Creates the.
         *
         * @param val the val
         * @return the temperature level
         */
        @Override
        public TemperatureLevel create(Object... val) {
            TemperatureLevel temperatureLevel = new TemperatureLevel();
            if (val.length == 2) {
                if (val[0] != null && val[0] instanceof GenericInstant) {
                    GenericInstant temperatureInstant = (GenericInstant) val[0];
                    temperatureLevel.setTemperature((Float)temperatureInstant.getAmount());
                }
                if (val[1] != null && val[1] instanceof JavaTimeInstant) {
                    JavaTimeInstant timeInstant = (JavaTimeInstant) val[1];
                    /*
                     * Converts time into seconds
                     */
                    int time = timeInstant.getSecondOfMinute() + (timeInstant.getMinuteOfHour() + timeInstant.getHourOfDay() * 60) * 60;
                    temperatureLevel.setDuration(time);
                }
            }
            return temperatureLevel;
        }

        /**
         * Inflate layout.
         *
         * @param adapter the adapter
         * @param inflater the inflater
         * @param container the container
         * @param mPage the m page
         * @param mChoices the m choices
         * @param fragment the fragment
         * @return the view
         */
        @Override
        public View inflateLayout(final BaseAdapter adapter, final LayoutInflater inflater, final ViewGroup container, final Page mPage,
                                  final ArrayList<TemperatureLevel> mChoices, final Fragment fragment) {
            View rootView = inflater.inflate(R.layout.fragment_temperature_level_page, container, false);
            ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

            final ListView listView = (ListView) rootView.findViewById(R.id.listView_items);
            listView.setAdapter(adapter);

            final FloatingLabelTemperaturePicker editTextTemperature = (FloatingLabelTemperaturePicker) rootView.findViewById(R.id.editText_temperature);
            editTextTemperature.setWidgetListener(new FloatingLabelInstantPicker.OnWidgetEventListener<GenericInstant<Float>>() {
                @Override
                public void onShowInstantPickerDialog(FloatingLabelInstantPicker<GenericInstant<Float>> temperatureInstantFloatingLabelInstantPicker) {
                    NumberPickerBuilder builder = new NumberPickerBuilder();
                    builder.setFragmentManager(fragment.getFragmentManager()).setTargetFragment(fragment);
                    builder.setMinNumber(0).setMaxNumber(100).setDecimalVisibility(1).setPlusMinusVisibility(0);
                    builder.addNumberPickerDialogHandler(new NumberPickerDialogFragment.NumberPickerDialogHandler() {
                        @Override
                        public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
                            editTextTemperature.setSelectedInstant(new GenericInstant<>(number + 0.0f));
                        }
                    });
                    builder.show();
                }
            });

            final FloatingLabelHMSPicker timePicker = (FloatingLabelHMSPicker) rootView.findViewById(R.id.timepicker_duration);
            timePicker.setWidgetListener(new FloatingLabelInstantPicker.OnWidgetEventListener<JavaTimeInstant>() {
                @Override
                public void onShowInstantPickerDialog(FloatingLabelInstantPicker<JavaTimeInstant> javaTimeInstantFloatingLabelInstantPicker) {
                    Context mContext = fragment.getActivity();
                    if (mContext == null){
                        return;
                    }
                    HmsPickerBuilder builder = new HmsPickerBuilder();
                    builder.setTitle(mContext.getString(R.string.time_picker_text));
                    builder.setFragmentManager(fragment.getFragmentManager()).setTargetFragment(fragment);
                    builder.addHmsPickerDialogHandler(new HmsPickerDialogFragment.HmsPickerDialogHandler() {
                        @Override
                        public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
                            timePicker.setSelectedInstant(new JavaTimeInstant(hours, minutes, seconds));
                        }
                    });
                    builder.show();
                }
            });

            Button button = (Button) rootView.findViewById(R.id.button_add);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextTemperature.getInputWidget() != null
                            && timePicker.getInputWidget() != null) {
                        mChoices.add(create(editTextTemperature.getSelectedInstant(), timePicker.getSelectedInstant()));
                        mPage.getData().putSerializable(Page.COMPLEX_DATA_KEY, mChoices);
                        mPage.notifyDataChanged();
                        editTextTemperature.setSelectedInstant(null);
                        timePicker.setSelectedInstant(null);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            return rootView;
        }

        /**
         * Gets the review item.
         *
         * @param list the list
         * @return the review item
         */
        @Override
        public String getReviewItem(ArrayList<TemperatureLevel> list) {
            return MessageHelper.getTemperatureLevelReviewItem(list);
        }

        /**
         * Gets the adapter.
         *
         * @param context the context
         * @param mChoices the m choices
         * @return the adapter
         */
        @Override
        public BaseAdapter getAdapter(Context context, ArrayList<TemperatureLevel> mChoices) {
            return new TemperatureLevelAdapter(context, mChoices);
        }
    }
}
