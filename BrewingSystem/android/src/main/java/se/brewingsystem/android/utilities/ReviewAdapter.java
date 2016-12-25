/*
 * 
 */
package se.brewingsystem.android.utilities;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;
import java.util.List;

import custom.wizardpager.model.ReviewItem;
import se.brewingsystem.android.R;



/**
 * Created by Daniel on 25.10.2014.
 */
public class ReviewAdapter extends ArrayAdapter<ReviewItem> {
    
    /** The m context. */
    private final Context mContext;

    /**
     * Instantiates a new review adapter.
     *
     * @param context the context
     * @param items the items
     */
    public ReviewAdapter(final Context context, List<ReviewItem> items) {
        super(items);
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
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.review_list_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(android.R.id.text1);
            holder.desc = (TextView) view.findViewById(android.R.id.text2);
            view.setTag(holder);
        }
        ReviewItem reviewItem = getItem(position);
        String value = reviewItem.getDisplayValue();
        if (TextUtils.isEmpty(value)) {
            value = mContext.getString(R.string.no_items_review_adapter_text);
        }
        holder = (ViewHolder) view.getTag();
        holder.title.setText(reviewItem.getTitle());
        holder.desc.setText(value);
        return view;
    }

    private class ViewHolder{
        public TextView title;
        public TextView desc;
    }
}
