/*
 * 
 */
package se.brewingsystem.android.utilities;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.ocpsoft.pretty.time.PrettyTime;

import java.util.Date;
import java.util.List;

import se.brewingsystem.android.R;
import messages.Message;



/**
 * Created by Daniel on 25.10.2014.
 */
public class LogEntryAdapter extends ArrayAdapter<Message> {
    
    /** The m context. */
    private final Context mContext;

    /**
     * Message-Helper to display a name for the type of the message
     */
    private final IMessageHelper mMessageHelper;

    /**
     * Instantiates a new log entry adapter.
     *
     * @param context the context
     * @param messages the messages
     */
    public LogEntryAdapter(final Context context, List<Message> messages, IMessageHelper helper) {
        super(messages);
        mContext = context;
        mMessageHelper = helper;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.log_list_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.desc = (TextView) view.findViewById(R.id.description);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        Pair<String, String> messageText = mMessageHelper.getTextFromMessage(getItem(position));
        holder.title.setText(messageText.first);
        holder.desc.setText(messageText.second);
        holder.date.setText(new PrettyTime().format(new Date(getItem(position).getTime())));
        return view;
    }

    private class ViewHolder{
        public TextView title;
        public TextView desc;
        public TextView date;
    }

}
