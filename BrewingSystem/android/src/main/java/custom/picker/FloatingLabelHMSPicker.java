/*
 * 
 */
package custom.picker;


import android.content.Context;
import android.util.AttributeSet;

import com.marvinlabs.widget.floatinglabel.instantpicker.FloatingLabelInstantPicker;
import com.marvinlabs.widget.floatinglabel.instantpicker.InstantPrinter;
import com.marvinlabs.widget.floatinglabel.instantpicker.JavaTimeInstant;

import se.brewingsystem.android.R;


/**
 * Created by Daniel on 11.12.2014.
 */
public class FloatingLabelHMSPicker extends FloatingLabelInstantPicker<JavaTimeInstant> {
    
    /**
     * Instantiates a new floating label hms picker.
     *
     * @param context the context
     */
    public FloatingLabelHMSPicker(Context context) {
        super(context);
    }

    /**
     * Instantiates a new floating label hms picker.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public FloatingLabelHMSPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new floating label hms picker.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public FloatingLabelHMSPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Gets the default instant printer.
     *
     * @return the default instant printer
     */
    @Override
    protected InstantPrinter<JavaTimeInstant> getDefaultInstantPrinter() {
        return new InstantPrinter<JavaTimeInstant>() {
            @Override
            public String print(JavaTimeInstant timeInstant) {
                String hourString = timeInstant.getHourOfDay() > 0 ? timeInstant.getHourOfDay() + "h" : "";
                String minuteString = timeInstant.getMinuteOfHour() > 0 ? timeInstant.getMinuteOfHour() + "m" : "";
                String secondString = timeInstant.getSecondOfMinute() > 0 ? timeInstant.getSecondOfMinute() + "s" : "";
                return hourString + " " + minuteString + " " + secondString;
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
        return R.drawable.ic_timepicker;
    }
}
