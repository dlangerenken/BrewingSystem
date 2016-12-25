/*
 * 
 */
package custom.wizardpager.model;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.Serializable;
import java.util.ArrayList;

import custom.wizardpager.page.Page;


/**
 * Created by Daniel on 09.12.2014.
 *
 * @param <A> the generic type
 */
public interface GenericFactory<A> extends Serializable {
    
    /**
     * Creates the.
     *
     * @param val the val
     * @return the a
     */
    A create(Object... val);

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
    View inflateLayout(BaseAdapter adapter, LayoutInflater inflater, ViewGroup container, Page mPage, ArrayList<A> mChoices, Fragment fragment);

    /**
     * Gets the review item.
     *
     * @param serializableList the serializable list
     * @return the review item
     */
    String getReviewItem(ArrayList<A> serializableList);

    /**
     * Gets the adapter.
     *
     * @param context the context
     * @param mChoices the m choices
     * @return the adapter
     */
    BaseAdapter getAdapter(Context context, ArrayList<A> mChoices);
}
