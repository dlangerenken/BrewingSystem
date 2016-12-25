/*
 * 
 */
package custom.picker;


import android.content.Context;
import android.util.AttributeSet;

import com.marvinlabs.widget.floatinglabel.instantpicker.FloatingLabelInstantPicker;
import com.marvinlabs.widget.floatinglabel.instantpicker.InstantPrinter;

import se.brewingsystem.android.R;


/**
 * Created by Daniel on 11.12.2014.
 */
public class FloatingLabelAmountPicker extends FloatingLabelInstantPicker<GenericInstant<Float>> {
    
    /**
     * Instantiates a new floating label amount picker.
     *
     * @param context the context
     */
    public FloatingLabelAmountPicker(Context context) {
        super(context);
    }

    /**
     * Instantiates a new floating label amount picker.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public FloatingLabelAmountPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new floating label amount picker.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public FloatingLabelAmountPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Gets the default instant printer.
     *
     * @return the default instant printer
     */
    @Override
    protected InstantPrinter<GenericInstant<Float>> getDefaultInstantPrinter() {
        return new InstantPrinter<GenericInstant<Float>>() {
            @Override
            public String print(GenericInstant<Float> amountInstant) {
                return amountInstant.getAmount() + " g";
            }
        };
    }

    /**
     * Gets the default drawable right res id.
     *
     * @return the default drawable right res id
     */
    @Override
    protected int getDefaultDrawableRightResId() {
        return R.drawable.ic_picker;
    }
}
