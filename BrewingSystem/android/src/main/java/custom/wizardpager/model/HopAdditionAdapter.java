/*
 * 
 */
package custom.wizardpager.model;

import android.content.Context;

import java.util.ArrayList;

import general.HopAddition;


/**
 * Created by Daniel on 09.12.2014.
 */
public class HopAdditionAdapter extends BaseRecipeAdapter<HopAddition> {

    /**
     * Instantiates a new hop addition adapter.
     *
     * @param context the context
     * @param pairs the pairs
     */
    public HopAdditionAdapter(final Context context, ArrayList<HopAddition> pairs) {
        super(context, pairs);
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.model.BaseRecipeAdapter#getTextFromIngredient(java.lang.Object)
     */
    @Override
    protected String getTextFromIngredient(HopAddition item) {
        String inputString = (item.getInputTime() / 60) + " min";
        return "Hopfen: " + item.getName() + "  Eingabezeit: " + inputString + " Menge:" + item.getAmount() + "g";
    }


}