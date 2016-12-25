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

import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;
import com.marvinlabs.widget.floatinglabel.instantpicker.FloatingLabelInstantPicker;
import com.marvinlabs.widget.floatinglabel.instantpicker.JavaTimeInstant;

import java.util.ArrayList;

import custom.picker.FloatingLabelAmountPicker;
import custom.picker.FloatingLabelHMSPicker;
import custom.picker.GenericInstant;
import custom.picker.hms.HmsPickerBuilder;
import custom.picker.hms.HmsPickerDialogFragment;
import custom.picker.number.NumberPickerBuilder;
import custom.picker.number.NumberPickerDialogFragment;
import custom.wizardpager.model.GenericFactory;
import custom.wizardpager.model.MaltAdditionAdapter;
import custom.wizardpager.model.ModelCallbacks;
import se.brewingsystem.android.R;
import se.brewingsystem.android.utilities.MessageHelper;
import general.MaltAddition;
import general.Unit;


/**
 * Created by Daniel on 09.12.2014.
 */
public class MashingPage extends DynamicListPage<MaltAddition> {

    /**
     * Instantiates a new mashing page.
     *
     * @param callbacks the callbacks
     * @param title the title
     * @param factoryA the factory a
     */
    private MashingPage(ModelCallbacks callbacks, String title, GenericFactory<MaltAddition> factoryA) {
        super(callbacks, title, factoryA);
    }

    /**
     * Creates the.
     *
     * @param callbacks the callbacks
     * @param context the context
     * @return the mashing page
     */
    public static MashingPage create(ModelCallbacks callbacks, Context context) {
        return new MashingPage(callbacks, context.getString(R.string.mash_addition_page_title), new MaltAdditionGenericFactory());
    }

    /**
     * A factory for creating MaltAdditionGeneric objects.
     */
    private static class MaltAdditionGenericFactory implements GenericFactory<MaltAddition> {
        
        /**
         * Creates the.
         *
         * @param val the val
         * @return the malt addition
         */
        @Override
        public MaltAddition create(Object... val) {
            MaltAddition maltAddition = new MaltAddition();
            if (val.length == 3) {
                if (val[0] != null) {
                    maltAddition.setName(val[0].toString());
                }
                if (val[1] != null && val[1] instanceof JavaTimeInstant) {
                    JavaTimeInstant timeInstant = (JavaTimeInstant) val[1];
                    /*
                     * Converts time into seconds
                     */
                    int time = timeInstant.getSecondOfMinute() + (timeInstant.getMinuteOfHour() + timeInstant.getHourOfDay() * 60) * 60;
                    maltAddition.setInputTime(time * 1000);
                }
                if (val[2] != null && val[2] instanceof GenericInstant) {
                    GenericInstant amountInstant = (GenericInstant) val[2];
                    maltAddition.setAmount((float)amountInstant.getAmount());
                    maltAddition.setUnit(Unit.g);
                }
            }
            return maltAddition;
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
        public View inflateLayout(final BaseAdapter adapter, final LayoutInflater inflater, final ViewGroup container, final Page mPage, final ArrayList<MaltAddition> mChoices, final Fragment fragment) {
            View rootView = inflater.inflate(R.layout.fragment_mashing_page, container, false);
            ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

            final ListView listView = (ListView) rootView.findViewById(R.id.listView_items);
            listView.setAdapter(adapter);

            final FloatingLabelEditText editTextSort = (FloatingLabelEditText) rootView.findViewById(R.id.editText_sort);
            final FloatingLabelAmountPicker amountPicker = (FloatingLabelAmountPicker) rootView.findViewById(R.id.editText_amount);
            amountPicker.setWidgetListener(new FloatingLabelInstantPicker.OnWidgetEventListener<GenericInstant<Float>>() {
                @Override
                public void onShowInstantPickerDialog(FloatingLabelInstantPicker<GenericInstant<Float>> amountInstantFloatingLabelInstantPicker) {
                    NumberPickerBuilder builder = new NumberPickerBuilder();
                    builder.setFragmentManager(fragment.getFragmentManager()).setTargetFragment(fragment);
                    builder.setMinNumber(0).setMaxNumber(10000).setDecimalVisibility(1).setPlusMinusVisibility(0);
                    builder.addNumberPickerDialogHandler(new NumberPickerDialogFragment.NumberPickerDialogHandler() {
                        @Override
                        public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
                            amountPicker.setSelectedInstant(new GenericInstant<>(number + 0.0f));
                        }
                    });
                    builder.show();
                }
            });

            final FloatingLabelHMSPicker durationPicker = (FloatingLabelHMSPicker) rootView.findViewById(R.id.editText_input);
            durationPicker.setWidgetListener(new FloatingLabelInstantPicker.OnWidgetEventListener<JavaTimeInstant>() {
                @Override
                public void onShowInstantPickerDialog(FloatingLabelInstantPicker<JavaTimeInstant> javaTimeInstantFloatingLabelInstantPicker) {
                    Context mContext = fragment.getActivity();
                    if (mContext == null){
                        return;
                    }
                    HmsPickerBuilder builder = new HmsPickerBuilder();
                    builder.setTitle(mContext.getString(R.string.page_duration_title));
                    builder.setFragmentManager(fragment.getFragmentManager()).setTargetFragment(fragment);
                    builder.addHmsPickerDialogHandler(new HmsPickerDialogFragment.HmsPickerDialogHandler() {
                        @Override
                        public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
                            durationPicker.setSelectedInstant(new JavaTimeInstant(hours, minutes, seconds));
                        }
                    });
                    builder.show();
                }
            });

            Button button = (Button) rootView.findViewById(R.id.button_add);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextSort != null && editTextSort.getInputWidgetText() != null
                            && durationPicker.getSelectedInstant() != null
                            && amountPicker.getSelectedInstant() != null) {
                        mChoices.add(create(editTextSort.getInputWidgetText().toString(), durationPicker.getSelectedInstant(), amountPicker.getSelectedInstant()));
                        mPage.getData().putSerializable(Page.COMPLEX_DATA_KEY, mChoices);
                        mPage.notifyDataChanged();
                        amountPicker.setSelectedInstant(null);
                        durationPicker.setSelectedInstant(null);
                        editTextSort.setInputWidgetText("");
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
        public String getReviewItem(ArrayList<MaltAddition> list) {
            return MessageHelper.getListOfIngrediends(list);
        }

        /**
         * Gets the adapter.
         *
         * @param context the context
         * @param mChoices the m choices
         * @return the adapter
         */
        @Override
        public BaseAdapter getAdapter(Context context, ArrayList<MaltAddition> mChoices) {
            return new MaltAdditionAdapter(context, mChoices);
        }
    }
}
