/*
 * 
 */
package custom.wizardpager.page;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

import java.util.ArrayList;

import custom.wizardpager.model.GenericFactory;
import custom.wizardpager.model.ModelCallbacks;
import se.brewingsystem.android.R;


/**
 * Created by Daniel on 02.12.2014.
 */
public class StringListPage extends DynamicListPage<String> {

    /**
     * Instantiates a new string list page.
     *
     * @param callbacks the callbacks
     * @param title the title
     * @param mFactory the m factory
     */
    private StringListPage(ModelCallbacks callbacks, String title, GenericFactory<String> mFactory) {
        super(callbacks, title, mFactory);
    }

    /**
     * Creates the.
     *
     * @param callbacks the callbacks
     * @param title the title
     * @return the string list page
     */
    public static StringListPage create(ModelCallbacks callbacks, String title) {
        return new StringListPage(callbacks, title, new GenericFactory<String>() {
            @Override
            public String create(Object... val) {
                if (val.length >= 1) {
                    return val[0].toString();
                }
                return null;
            }

            @Override
            public View inflateLayout(final BaseAdapter adapter, final LayoutInflater inflater, final ViewGroup container, final Page mPage, final ArrayList<String> mChoices, Fragment fragment) {
                View rootView = inflater.inflate(R.layout.fragment_temperature_level_page, container, false);
                ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

                final ListView listView = (ListView) rootView.findViewById(R.id.listView_items);
                listView.setAdapter(adapter);

                final FloatingLabelEditText editText = (FloatingLabelEditText) rootView.findViewById(R.id.editText_input);
                Button button = (Button) rootView.findViewById(R.id.button_add);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText != null && editText.getInputWidgetText() != null) {
                            mChoices.add(editText.getInputWidgetText().toString());
                            mPage.getData().putStringArrayList(Page.SIMPLE_DATA_KEY, mChoices);
                            mPage.notifyDataChanged();
                            adapter.notifyDataSetChanged();
                            editText.setInputWidgetText("");
                        }
                    }
                });
                return rootView;
            }

            @Override
            public String getReviewItem(ArrayList<String> list) {
                StringBuilder builder = new StringBuilder();
                if (list != null && list.size() > 0) {
                    for (String string : list) {
                        if (builder.length() > 0) {
                            builder.append(", ");
                        }
                        builder.append(string);
                    }
                }
                return builder.toString();
            }

            @Override
            public BaseAdapter getAdapter(Context context, ArrayList<String> mChoices) {
                return new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        mChoices);
            }
        });
    }
}
