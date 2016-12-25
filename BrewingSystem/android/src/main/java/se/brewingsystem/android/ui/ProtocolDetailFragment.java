/*
 * 
 */
package se.brewingsystem.android.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.ocpsoft.pretty.time.PrettyTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Style;
import general.MessagePriority;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.brewingsystem.android.R;
import se.brewingsystem.android.utilities.CommonUtilities;
import se.brewingsystem.android.utilities.CroutonHelper;
import se.brewingsystem.android.utilities.IMessageHelper;
import se.brewingsystem.android.utilities.GraphUtilities;
import se.brewingsystem.android.utilities.LogEntryAdapter;
import se.brewingsystem.android.utilities.TemperatureGraphModel;
import general.Protocol;
import general.LogSummary;
import messages.Message;


/**
 * Protocol-Detail View which shows a single protocol with the whole information about it
 */
public class ProtocolDetailFragment extends AnimateInFragment {
    
    /** The Constant ARG_PROTOCOL. */
    private static final String ARG_PROTOCOL = "protocol";

    /** The protocol summary. */
    private LogSummary mLogSummary;

    /** The protocol. */
    private Protocol mProtocol;
    
    /** The protocol messages. */
    private List<Message> protocolMessages;
    
    /** The adapter. */
    private BaseAdapter mAdapter;

    /** The temperature series. */
    private GraphViewSeries temperatureSeries;
    
    /** The graph view. */
    private GraphView graphView;
    
    /** The model. */
    private TemperatureGraphModel[] model;

    @Inject
    IMessageHelper mMessageHelper;

    /**
     * Instantiates a new protocol detail fragment.
     */
    public ProtocolDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param summary Recipe.
     * @param x the x
     * @param y the y
     * @param width the width
     * @param height the height
     * @return A new instance of fragment BrewingLogSummaryFragment.
     */
    public static ProtocolDetailFragment newInstance(LogSummary summary, int x, int y, int width, int height) {
        ProtocolDetailFragment fragment = new ProtocolDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROTOCOL, summary);
        putArgs(args, x, y, width, height);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_protocol_detail, container, false);
    }

    /**
     * On view created.
     *
     * @param view the view
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mLogSummary = (LogSummary) getArguments().getSerializable(ARG_PROTOCOL);
            protocolMessages = new ArrayList<>();
            mNetworkCommunication.getProtocol(mLogSummary.getId(), new Callback<Protocol>() {
                @Override
                public void success(Protocol protocol, Response response) {
                    mProtocol = protocol;
                    protocolMessages.clear();
                    List<Message> messages = protocol.getAllMessages();
                    if (messages == null){
                        messages = new ArrayList<>();
                    }
                    model = GraphUtilities.getGraphModel(messages);
                            /*
                            * after receiving the temperature graph model we can remove all temperature messages
                             */
                    for (Iterator<Message> iterator = messages.iterator(); iterator.hasNext();) {
                        Message message = iterator.next();
                        if (message.getPriority().getValue() > MessagePriority.MEDIUM.getValue()) {
                            iterator.remove();
                        }
                    }
                    protocolMessages.addAll(messages);
                    Collections.sort(protocolMessages, new Comparator<Message>() {
                        @Override
                        public int compare(Message lhs, Message rhs) {
                            return Long.compare(rhs.getTime(), lhs.getTime());
                        }
                    });
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    if (temperatureSeries != null) {
                        temperatureSeries.resetData(model);
                        graphView.redrawAll();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Activity mContext = getActivity();
                    if (mContext != null) {
                        CroutonHelper.showText(mContext, mContext.getString(R.string.protocol_not_loaded_crouton_text), Style.ALERT, R.id.crouton_handle);
                    }
                }
            });
        }

        inflateAndAdd(view, R.layout.protocol_list_item, args);
    }

    @Override
    protected void bind(View parent) {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        ImageView image = (ImageView) parent.findViewById(R.id.image);
        image.setImageResource(R.drawable.beer);
        TextView title = (TextView) parent.findViewById(R.id.title);
        TextView description = (TextView) parent.findViewById(R.id.description);
        TextView date = (TextView) parent.findViewById(R.id.date);


        title.setText(mLogSummary.getTitle());
        description.setText(mLogSummary.getDescription());
        date.setText(new PrettyTime().format(new Date(mLogSummary.getDate())));
    }

    /**
     * On animation end.
     *
     * @param animation the animation
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        View rootView = onAnimEnd(R.layout.fragment_protocol_detail_content);
        Activity context = getActivity();
        if (rootView == null || context == null){
            return;
        }

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        mAdapter = new LogEntryAdapter(context, protocolMessages, mMessageHelper);
        listView.setAdapter(mAdapter);
        createGraph(rootView, context);
    }

    /**
     * Creates the graph.
     *
     * @param rootView the root view
     * @param context the context
     */
    private void createGraph(View rootView, final Context context) {
        if (model == null) {
            model = new TemperatureGraphModel[]{};
        }
        temperatureSeries = new GraphViewSeries(model);
        graphView = CommonUtilities.getTemperatureGraph(context, temperatureSeries);
        LinearLayout graphLayout = (LinearLayout) rootView.findViewById(R.id.graph);
        graphLayout.addView(graphView);
    }
}