/*
 * 
 */
package custom.wizardpager.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;

import java.util.ArrayList;

import se.brewingsystem.android.R;


/**
 * Created by Daniel on 09.12.2014.
 *
 * @param <T> the generic type
 */
abstract class BaseRecipeAdapter<T> extends ArrayAdapter<T> {
    
    /** The m context. */
    private final Context mContext;

    /**
     * Instantiates a new base recipe adapter.
     *
     * @param context the context
     * @param ingredients the ingredients
     */
    BaseRecipeAdapter(final Context context, ArrayList<T> ingredients) {
        super(ingredients);
        mContext = context;
    }

    /**
     * Gets the item id.
     *
     * @param position the position
     * @return the item id
     */
    @Override
    public long getItemId(final int position) {
        return getItem(position).hashCode();
    }

    /**
     * Checks for stable ids.
     *
     * @return true, if successful
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }


    /**
     * Gets the view.
     *
     * @param position the position
     * @param convertView the convert view
     * @param parent the parent
     * @return the view
     */
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.recipe_creation_list_item, parent, false);
        }
        TextView text = (TextView) view.findViewById(R.id.textView);

        text.setText("[" + (position + 1) + "] " + getTextFromIngredient(getItem(position)));
        return view;
    }

    /**
     * Gets the text from ingredient.
     *
     * @param item the item
     * @return the text from ingredient
     */
    protected abstract String getTextFromIngredient(T item);
}
