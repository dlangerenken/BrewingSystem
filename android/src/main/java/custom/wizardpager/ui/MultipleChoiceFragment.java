/*
 * 
 */
package custom.wizardpager.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import custom.wizardpager.page.MultipleFixedChoicePage;
import custom.wizardpager.page.Page;
import se.brewingsystem.android.R;


/**
 * The Class MultipleChoiceFragment.
 */
public class MultipleChoiceFragment extends PageFragment {
    
    /** The Constant ARG_KEY. */
    private static final String ARG_KEY = "key";

    /** The m choices. */
    private List<String> mChoices;
    
    /** The m page. */
    private Page mPage;

    /**
     * Instantiates a new multiple choice fragment.
     */
    public MultipleChoiceFragment() {
    }

    /**
     * Creates the.
     *
     * @param key the key
     * @return the multiple choice fragment
     */
    public static MultipleChoiceFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        MultipleChoiceFragment fragment = new MultipleChoiceFragment();
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

        MultipleFixedChoicePage fixedChoicePage = (MultipleFixedChoicePage) mPage;
        mChoices = new ArrayList<>();
        for (int i = 0; i < fixedChoicePage.getOptionCount(); i++) {
            mChoices.add(fixedChoicePage.getOptionAt(i));
        }
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
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        final ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                android.R.id.text1,
                mChoices);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray checkedPositions = listView.getCheckedItemPositions();
                ArrayList<String> selections = new ArrayList<>();
                for (int i = 0; i < checkedPositions.size(); i++) {
                    if (checkedPositions.valueAt(i)) {
                        selections.add(adapter.getItem(checkedPositions.keyAt(i)));
                    }
                }

                mPage.getData().putStringArrayList(Page.SIMPLE_DATA_KEY, selections);
                mPage.notifyDataChanged();
            }
        });
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Pre-select currently selected items.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> selectedItems = mPage.getData().getStringArrayList(
                        Page.SIMPLE_DATA_KEY);
                if (selectedItems == null || selectedItems.size() == 0) {
                    return;
                }

                Set<String> selectedSet = new HashSet<>(selectedItems);

                for (int i = 0; i < mChoices.size(); i++) {
                    if (selectedSet.contains(mChoices.get(i))) {
                        listView.setItemChecked(i, true);
                    }
                }
            }
        });

        return rootView;
    }
}
