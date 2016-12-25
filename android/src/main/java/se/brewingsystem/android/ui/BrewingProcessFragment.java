/*
 * 
 */
package se.brewingsystem.android.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import custom.picker.hms.HmsPickerBuilder;
import custom.picker.hms.HmsPickerDialogFragment;
import de.greenrobot.event.EventBus;
import general.BrewingProcess;
import general.BrewingState;
import general.MessagePriority;
import gson.Serializer;
import me.grantland.widget.AutofitHelper;
import messages.Message;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.brewingsystem.android.NotifyIntentService;
import se.brewingsystem.android.R;
import se.brewingsystem.android.events.BrewingStateChangedEvent;
import se.brewingsystem.android.events.IodineTestPositiveEvent;
import se.brewingsystem.android.events.UpdateMessageEvent;
import se.brewingsystem.android.network.BrewingCallback;
import se.brewingsystem.android.utilities.CroutonHelper;
import se.brewingsystem.android.utilities.IMessageHelper;
import se.brewingsystem.android.gcm.command.MessageCommand;
import se.brewingsystem.android.utilities.CommonUtilities;
import se.brewingsystem.android.utilities.DialogHelper;
import se.brewingsystem.android.utilities.GraphUtilities;
import se.brewingsystem.android.utilities.LogEntryAdapter;
import se.brewingsystem.android.utilities.TemperatureGraphModel;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link BrewingProcessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrewingProcessFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    /** The drawable state map. */
    private final Map<BrewingState.State, Integer> drawableStateMap;
    {
        drawableStateMap = new HashMap<>();
        drawableStateMap.put(BrewingState.State.FINISHED, R.drawable.garen_white);
        drawableStateMap.put(BrewingState.State.HOP_COOKING, R.drawable.kochen_white);
        drawableStateMap.put(BrewingState.State.LAUTERING, R.drawable.lautern_white);
        drawableStateMap.put(BrewingState.State.NOT_STARTED, R.drawable.initial_white);
        drawableStateMap.put(BrewingState.State.MASHING, R.drawable.maischen_white);
        drawableStateMap.put(BrewingState.State.WHIRLPOOL, R.drawable.whirlpool_white);
    }

    /** The current state image. */
    private ImageView currentStateImage;
    
    /** The current brewing step title. */
    private TextView currentBrewingStepTitle;
    
    /** The temperature view. */
    private TextView temperatureView;
    
    /** The time view. */
    private TextView timeView;
    
    /** The sliding panel. */
    private SlidingUpPanelLayout slidingPanel;
    
    /** The m progress bar. */
    private ProgressBar mProgressBar;
    
    /** The m progress view. */
    private ImageView mProgressView;
    
    /** The temperature series. */
    private GraphViewSeries temperatureSeries;
    
    /** The graph view. */
    private GraphView graphView;

    /** The messages. */
    private List<Message> messages;
    
    /** The default header view. */
    private ViewGroup defaultHeaderView;
    
    /** The confirmation header view. */
    private ViewGroup confirmationHeaderView;
    
    /** The confirmation button. */
    private Button confirmationButton;

    /** The swipe refresh layout. */
    private SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    IMessageHelper mMessageHelper;

    /** The model. */
    private TemperatureGraphModel[] model;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private BaseAdapter mAdapter;

    /**
     * Instantiates a new brewing process fragment.
     */
    public BrewingProcessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BrewingStepsFragment.
     */
    public static BrewingProcessFragment newInstance() {
        return new BrewingProcessFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_brewing_process, container, false);
        currentStateImage = (ImageView) rootView.findViewById(R.id.currentStateImage);
        currentBrewingStepTitle = (TextView) rootView.findViewById(R.id.currentBrewingStepTitle);
        AutofitHelper.create(currentBrewingStepTitle);
        temperatureView = (TextView) rootView.findViewById(R.id.temperatureView);

        defaultHeaderView = (ViewGroup) rootView.findViewById(R.id.default_header_view);
        confirmationHeaderView = (ViewGroup) rootView.findViewById(R.id.confirmation_header_view);

        confirmationButton = (Button) rootView.findViewById(R.id.confirmation_button);
        timeView = (TextView) rootView.findViewById(R.id.timeView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeView);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        swipeRefreshLayout.setOnRefreshListener(this);
        slidingPanel.setDragView(rootView.findViewById(R.id.alwaysVisible));
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.refreshProgressBar);
        mProgressView = (ImageView) rootView.findViewById(R.id.refreshImage);
        View progressButtonBar = rootView.findViewById(R.id.loadingPanel);
        progressButtonBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });

        messages = new ArrayList<>();
        mAdapter = new LogEntryAdapter(getActivity(), messages, mMessageHelper);

        AbsListView mLogListView = (ListView) rootView.findViewById(R.id.logView);
        mLogListView.setAdapter(mAdapter);

        createGraph(rootView, getActivity());
        onRefresh();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
     public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Refresh every time a BrewingStateChangedEvent is received
     * @param event
     */
    public void onEventMainThread(BrewingStateChangedEvent event){
        onRefresh();
    }

    /**
     * Refresh every time an IodineTestPositiveEvent is received
     * @param event
     */
    public void onEventMainThread(IodineTestPositiveEvent event){
        onRefresh();
    }

    /**
     * Refresh every time an UpdateMessageEvent is received
     * @param event
     */
    public void onEventMainThread(UpdateMessageEvent event){
        onRefresh();
    }

    /**
     * Creates the graph.
     *
     * @param rootView the root view
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

    /**
     * Sets the header view visibility.
     *
     * @param confirmation the new header view visibility
     */
    private void setHeaderViewVisibility(boolean confirmation) {
        final View fadeInView = confirmation ? confirmationHeaderView : defaultHeaderView;
        final View fadeOutView = confirmation ? defaultHeaderView : confirmationHeaderView;

        Context mContext = getActivity();
        if (mContext != null){
            DialogHelper.crossFade(fadeInView, fadeOutView, mContext.getResources().getInteger(
                    android.R.integer.config_shortAnimTime));
        }
    }

    /**
     * Sets the refreshing.
     *
     * @param refreshing the new refreshing
     */
    private void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
        mProgressView.setVisibility(refreshing ? View.INVISIBLE : View.VISIBLE);
        mProgressBar.setVisibility(refreshing ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Update process.
     *
     * @param brewingProcess the brewing process
     */
    private void updateProcess(BrewingProcess brewingProcess) {
        if (brewingProcess == null || brewingProcess.getBrewingLog() == null || brewingProcess.getBrewingLog().getMessages() == null) {
            if (slidingPanel != null) {
                slidingPanel.hidePanel();
            }
            return;
        }
        if (drawableStateMap.containsKey(brewingProcess.getState().getState())) {
            currentStateImage.setImageResource(drawableStateMap.get(brewingProcess.getState().getState()));
        }
        List<Message> mMessages = brewingProcess.getBrewingLog().getMessages();
        TemperatureGraphModel[] model = GraphUtilities.getGraphModel(mMessages);
        /*
         * after receiving the temperature graph model we can remove all temperature messages
         */
        for (Iterator<Message> iterator = mMessages.iterator(); iterator.hasNext();) {
            Message message = iterator.next();
            if (message.getPriority().getValue() > MessagePriority.MEDIUM.getValue()) {
                iterator.remove();
            }
        }
        Collections.sort(mMessages, Collections.reverseOrder()); //newest at the beginning
        messages.clear();
        messages.addAll(mMessages);

        temperatureSeries.resetData(model);
        graphView.setManualYAxisBounds(100, 0);
        graphView.redrawAll();
        if (model.length > 0) {
            float temperature = model[model.length - 1].getMessage().getTemperature();
            temperatureView.setText(String.format(getActivity().getString(R.string.temp_format_string),  temperature));
        }

        Activity mContext = getActivity();
        if (mContext != null) {
            CroutonHelper.showText(mContext, mContext.getString(R.string.brewing_process_updated_text), CommonUtilities.getSuccessStyle(mContext), R.id.crouton_handle);
        }
        if (slidingPanel != null) {
            slidingPanel.showPanel();
        }
        mAdapter.notifyDataSetChanged();
        setHeaderView(brewingProcess);
    }

    /**
     * Sets the header view.
     *
     * @param brewingProcess the new header view
     */
    private void setHeaderView(BrewingProcess brewingProcess) {
        boolean requestNeeded = brewingProcess.getState().requestNeeded();
        boolean isCancelled = brewingProcess.getState().isCancelled();
        if (requestNeeded || isCancelled) {
            createConfirmationRequestHeader(brewingProcess);
        }
        createDefaultHeader(brewingProcess);
        setHeaderViewVisibility(requestNeeded);
    }

    /**
     * Creates the confirmation request header.
     *
     * @param brewingProcess the current brewing process
     */
    private void createConfirmationRequestHeader(final BrewingProcess brewingProcess) {
        final BrewingState brewingStep = brewingProcess.getState();
        confirmationButton.setText(mMessageHelper.getTextFromBrewingState(brewingStep));
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (brewingStep.getState() == BrewingState.State.MASHING && brewingStep.getPosition() == BrewingState.Position.IODINE &&
                        brewingStep.getType() != BrewingState.Type.CANCEL) {
                    showIodineTestDialog();
                } else {
                    confirmStep(brewingStep);
                }
            }
        });
    }

    /**
     * Confirm step.
     *
     * @param step the brewing state to confirm
     */
    private void confirmStep(final BrewingState step) {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("state", Serializer.getInstance().toJson(step));
        mNetworkCommunication.confirmStep(parameter, new BrewingCallback<BrewingState>() {
            @Override
            public void onSuccess(BrewingState brewingState, Response response) {
                NotifyIntentService.cancelNotifications(getActivity());
            }

            @Override
            public void onEnd() {
                onRefresh();
            }
        });
    }

    /**
     * Creates the default header.
     *
     * @param brewingProcess the brewing process
     */
    private void createDefaultHeader(BrewingProcess brewingProcess) {
        if (brewingProcess.getRecipe() != null) {
            String brewingProcessTitle = brewingProcess.getRecipe().getName();
            currentBrewingStepTitle.setText(brewingProcessTitle);
        }
        timeView.setText(getDurationText(brewingProcess));
    }

    /**
     * Gets the duration text.
     *
     * @param brewingProcess the brewing process
     * @return the duration text
     */
    private String getDurationText(BrewingProcess brewingProcess) {
        long startTime = brewingProcess.getStartTime();
        long currentTime = new Date().getTime();

        long diff = currentTime - startTime;
        int seconds = (int) (diff / 1000) % 60 ;
        int minutes = (int) ((diff / (1000*60)) % 60);
        int hours   = (int) ((diff / (1000*60*60)) % 24);

        return String.format("Zeit: %d:%02d:%02d", hours, minutes, seconds);
    }


    /**
     * Sets the sliding panel.
     *
     * @param slidingPanel the new sliding panel
     */
    public void setSlidingPanel(SlidingUpPanelLayout slidingPanel) {
        this.slidingPanel = slidingPanel;
    }



    /**
     * On refresh.
     */
    @Override
    public void onRefresh() {
        /*
         * Workaround
         * http://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
            }
        });

        mNetworkCommunication.getCurrentBrewingStatus(new BrewingCallback<BrewingProcess>() {

            @Override
            public void onSuccess(BrewingProcess brewingProcess, Response response) {
                updateProcess(brewingProcess);
            }

            @Override
            public void onEnd() {
                setRefreshing(false);
            }

            @Override
            public void onFailure(RetrofitError error) {
                updateProcess(null);
                NotifyIntentService.cancelNotifications(getActivity());
                Log.e(TAG, "getCurrentBrewingStatus returned an exception (might not have any value");
            }

        });
    }

    /**
     * Show iodine test dialog.
     */
    public void showIodineTestDialog() {
        Context context = getActivity();
        if (context == null){
            return;
        }
        HmsPickerBuilder builder = new HmsPickerBuilder();
        builder.setFragmentManager(getFragmentManager()).setTargetFragment(this);
        builder.setTitle(context.getString(R.string.iodin_test_dialog_title));
        builder.addHmsPickerDialogHandler(new HmsPickerDialogFragment.HmsPickerDialogHandler() {
            @Override
            public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
                int duration = (hours * 60 + minutes) * 60 + seconds;
                mNetworkCommunication.confirmIodineTest(duration, new BrewingCallback<BrewingState>() {
                    @Override
                    public void onEnd() {
                        closeNotification();
                        onRefresh();
                    }
                });
            }
        });
        builder.show();
    }

    /**
     * Close notification.
     */
    private void closeNotification() {
        Context mContext = getActivity();
        if (mContext != null) {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(MessageCommand.CONFIRM_ID);
        }
    }
}
