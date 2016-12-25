/*
 * 
 */
package custom.wizardpager.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

import custom.wizardpager.page.Page;
import custom.wizardpager.page.SingleTextPage;
import se.brewingsystem.android.R;



/**
 * Created by Daniel on 09.12.2014.
 */
public class SingleTextFragment extends PageFragment {
    
    /** The Constant ARG_KEY. */
    private static final String ARG_KEY = "key";
    
    /** The Constant ARG_BOX. */
    private static final String ARG_BOX = "box";

    /** The m should use text box. */
    private boolean mShouldUseTextBox;
    
    /** The m page. */
    private SingleTextPage mPage;
    
    /** The m field view. */
    private FloatingLabelEditText mFieldView;

    /**
     * Instantiates a new single text fragment.
     */
    public SingleTextFragment() {
    }

    /**
     * Creates the.
     *
     * @param key the key
     * @param shouldUseTextBox the should use text box
     * @return the single text fragment
     */
    public static SingleTextFragment create(String key, boolean shouldUseTextBox) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        args.putBoolean(ARG_BOX, shouldUseTextBox);

        SingleTextFragment fragment = new SingleTextFragment();
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
        mShouldUseTextBox = args.getBoolean(ARG_BOX);
        mPage = (SingleTextPage) mCallbacks.onGetPage(mKey);
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
        View rootView = inflater.inflate(R.layout.fragment_page_single_text, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());
        ((TextView) rootView.findViewById(R.id.wizard_text_field_desc)).setText(mPage.getDesc());

        mFieldView = ((FloatingLabelEditText) rootView.findViewById(R.id.wizard_text_field));
        mFieldView.setLabelText(mPage.getTitle());
        mFieldView.setInputWidgetText(mPage.getData().getString(Page.SIMPLE_DATA_KEY));
        mFieldView.getInputWidget().setSingleLine(!mShouldUseTextBox);
        return rootView;
    }


    /**
     * On view created.
     *
     * @param view the view
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFieldView.getInputWidget().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(Page.SIMPLE_DATA_KEY,
                        (editable != null) ? editable.toString() : null);
                mPage.notifyDataChanged();
            }
        });
    }

    /**
     * Sets the menu visibility.
     *
     * @param menuVisible the new menu visibility
     */
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mFieldView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }
}