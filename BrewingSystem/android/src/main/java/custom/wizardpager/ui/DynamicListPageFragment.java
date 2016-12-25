/*
 * 
 */
package custom.wizardpager.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import custom.wizardpager.model.GenericFactory;
import custom.wizardpager.page.Page;


/**
 * The Class DynamicListPageFragment.
 *
 * @param <A> the generic type
 */
public class DynamicListPageFragment<A> extends PageFragment {

    /** The Constant ARG_KEY. */
    private static final String ARG_KEY = "key";
    
    /** The Constant ARG_FACTORY. */
    private static final String ARG_FACTORY = "factory";
    
    /** The m choices. */
    private ArrayList<A> mChoices;
    
    /** The generic factory. */
    private GenericFactory<A> genericFactory;
    
    /** The m page. */
    private Page mPage;


    /**
     * Instantiates a new dynamic list page fragment.
     */
    public DynamicListPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param <A> the generic type
     * @param key KEY.
     * @param dynamicFactory the dynamic factory
     * @return A new instance of fragment MultipleUnfixedChoiceFragment.
     */
    public static <A> DynamicListPageFragment create(String key, GenericFactory<A> dynamicFactory) {
        DynamicListPageFragment fragment = new DynamicListPageFragment<A>();
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        args.putSerializable(ARG_FACTORY, dynamicFactory);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        String mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);
        genericFactory = (GenericFactory<A>) args.getSerializable(ARG_FACTORY);
        mChoices = new ArrayList<>();
    }

    /**
     * On create view.
     *
     * @param inflater the inflater
     * @param container the container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final BaseAdapter adapter = genericFactory.getAdapter(getActivity(), mChoices);
        View rootView = genericFactory.inflateLayout(adapter, inflater, container, mPage, mChoices, this);
        // Pre-select currently selected items.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<A> selectedItems = (ArrayList<A>) mPage.getData().getSerializable(
                        Page.COMPLEX_DATA_KEY);
                mChoices.clear();
                if (selectedItems != null) {
                    mChoices.addAll(selectedItems);
                }
                adapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }
}
