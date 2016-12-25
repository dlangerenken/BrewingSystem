/*
 * 
 */
package custom.wizardpager.model;

import android.content.Context;

import java.util.ArrayList;

import general.MaltAddition;


/**
 * Created by Daniel on 09.12.2014.
 */
public class MaltAdditionAdapter extends BaseRecipeAdapter<MaltAddition> {

    /**
     * Instantiates a new malt addition adapter.
     *
     * @param context the context
     * @param ingredients the ingredients
     */
    public MaltAdditionAdapter(Context context, ArrayList<MaltAddition> ingredients) {
        super(context, ingredients);
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.model.BaseRecipeAdapter#getTextFromIngredient(java.lang.Object)
     */
    @Override
    protected String getTextFromIngredient(MaltAddition item) {
        return " Maischzutat: " + item.getName() + "  Menge:" + item.getAmount() + "g";
    }
}