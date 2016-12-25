/*
 * 
 */
package custom.wizardpager.model;

import android.content.Context;

import java.util.ArrayList;

import general.TemperatureLevel;


/**
 * Created by Daniel on 09.12.2014.
 */
public class TemperatureLevelAdapter extends BaseRecipeAdapter<TemperatureLevel> {


    /**
     * Instantiates a new temperature level adapter.
     *
     * @param context the context
     * @param pairs the pairs
     */
    public TemperatureLevelAdapter(final Context context, ArrayList<TemperatureLevel> pairs) {
        super(context, pairs);
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.model.BaseRecipeAdapter#getTextFromIngredient(java.lang.Object)
     */
    @Override
    protected String getTextFromIngredient(TemperatureLevel item) {
        String durationString = (item.getDuration() / 60) + " min";
        return "Temperatur: " + item.getTemperature() + "°" + "  Dauer: " + durationString;
    }
}