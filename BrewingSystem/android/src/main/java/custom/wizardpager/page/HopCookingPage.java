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
import custom.wizardpager.model.HopAdditionAdapter;
import custom.wizardpager.model.ModelCallbacks;
import se.brewingsystem.android.R;
import se.brewingsystem.android.utilities.MessageHelper;
import general.HopAddition;
import general.Unit;


/**
 * Created by Daniel on 09.12.2014.
 */
public class HopCookingPage extends DynamicListPage<HopAddition> {

    /**
     * Instantiates a new hop cooking page.
     *
     * @param callbacks the callbacks
     * @param title the title
     * @param factoryA the factory a
     */
    private HopCookingPage(ModelCallbacks callbacks, String title, GenericFactory<HopAddition> factoryA) {
        super(callbacks, title, factoryA);
    }

    /**
     * Creates the.
     *
     * @param callbacks the callbacks
     * @param context the context
     * @return the hop cooking page
     */
    public static HopCookingPage create(ModelCallbacks callbacks, Context context) {
        return new HopCookingPage(callbacks, context.getString(R.string.hop_addition_title), new HopAdditionGenericFactory());
    }

    /**
     * A factory for creating HopAdditionGeneric objects.
     */
    private static class HopAdditionGenericFactory implements GenericFactory<HopAddition> {
        
        /**
         * Creates the.
         *
         * @param val the val
         * @return the hop addition
         */
        @Override
        public HopAddition create(Object... val) {
            HopAddition hopAddition = new HopAddition();
            if (val.length == 3) {
                if (val[0] != null) {
                    hopAddition.setName(val[0].toString());
                }
                if (val[1] != null && val[1] instanceof JavaTimeInstant) {
                    JavaTimeInstant timeInstant = (JavaTimeInstant) val[1];
                    /*
                     * Converts time into seconds
                     */
                    int time = timeInstant.getSecondOfMinute() + (timeInstant.getMinuteOfHour() + timeInstant.getHourOfDay() * 60) * 60;
                    hopAddition.setInputTime(time * 1000);
                }
                if (val[2] != null && val[2] instanceof GenericInstant) {
                    GenericInstant amountInstant = (GenericInstant) val[2];
                    hopAddition.setAmount((float) amountInstant.getAmount());
                    hopAddition.setUnit(Unit.g);
                }
            }
            return hopAddition;
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
        public View inflateLayout(final BaseAdapter adapter, final LayoutInflater inflater, final ViewGroup container, final Page mPage, final ArrayList<HopAddition> mChoices, final Fragment fragment) {
            View rootView = inflater.inflate(R.layout.fragment_hop_cooking_page, container, false);
            ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

            final ListView listView = (ListView) rootView.findViewById(R.id.listView_items);
            listView.setAdapter(adapter);

            final FloatingLabelEditText editTextHopSort = (FloatingLabelEditText) rootView.findViewById(R.id.editText_sort);
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
                    HmsPickerBuilder builder = new HmsPickerBuilder();
                    Context mContext = fragment.getActivity();
                    if (mContext == null){
                        return;
                    }
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
                    if (editTextHopSort != null && editTextHopSort.getInputWidgetText() != null && durationPicker.getSelectedInstant() != null && amountPicker.getSelectedInstant() != null) {
                        mChoices.add(create(editTextHopSort.getInputWidgetText().toString(), durationPicker.getSelectedInstant(), amountPicker.getSelectedInstant()));
                        mPage.getData().putSerializable(Page.COMPLEX_DATA_KEY, mChoices);
                        mPage.notifyDataChanged();
                        amountPicker.setSelectedInstant(null);
                        durationPicker.setSelectedInstant(null);
                        editTextHopSort.setInputWidgetText("");
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
        public String getReviewItem(ArrayList<HopAddition> list) {
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
        public BaseAdapter getAdapter(Context context, ArrayList<HopAddition> mChoices) {
            return new HopAdditionAdapter(context, mChoices);
        }
    }
}
