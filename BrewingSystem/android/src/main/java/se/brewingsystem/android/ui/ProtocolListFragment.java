/*
 * 
 */
package se.brewingsystem.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Style;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.brewingsystem.android.R;
import se.brewingsystem.android.network.BrewingCallback;
import se.brewingsystem.android.utilities.CroutonHelper;
import se.brewingsystem.android.utilities.ProtocolListAdapter;
import general.LogSummary;


/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class ProtocolListFragment extends BaseFragment implements AbsListView.OnItemClickListener {
    
    /** The Constant ARG_RECIPE_ID. */
    private static final String ARG_RECIPE_ID = "recipeId";
    
    /** The m listener. */
    private OnProtocolListFragmentInteractionListener mListener;
    
    /** The m recipe id. */
    private String mRecipeId;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private BaseAdapter mAdapter;
    
    /** The protocols. */
    private List<LogSummary> protocols;

    /** The swipe refresh layout. */
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProtocolListFragment() {
    }

    /**
     * New instance.
     *
     * @param recipeId the recipe id
     * @return the protocol list fragment
     */
    public static ProtocolListFragment newInstance(String recipeId) {
        ProtocolListFragment fragment = new ProtocolListFragment();
        Bundle args = new Bundle();
        if (recipeId != null) {
            args.putString(ARG_RECIPE_ID, recipeId);
        }
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

        if (getArguments() != null) {
            mRecipeId = getArguments().getString(ARG_RECIPE_ID);
        }

        protocols = new ArrayList<>();
        mAdapter = new ProtocolListAdapter(getActivity(), protocols);
    }

    /**
     * Load protocols.
     */
    private void loadProtocols() {
        /*
         * Workaround
         * http://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                mNetworkCommunication.getProtocols(new BrewingCallback<List<LogSummary>>() {
                    @Override
                    public void onEnd() {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onSuccess(List<LogSummary> newProtocols, Response response) {
                        if (newProtocols != null) {
                            protocols.clear();
                            for (Iterator<LogSummary> iterator = newProtocols.iterator(); iterator.hasNext();){
                                LogSummary summary = iterator.next();
                                /*
                                 * remove logs which don't belong to the recipe id if not null
                                 */
                                if (mRecipeId != null && !summary.getRecipeId().equals(mRecipeId)){
                                    iterator.remove();
                                }
                            }
                            protocols.addAll(newProtocols);
                            Collections.sort(protocols, new Comparator<LogSummary>() {
                                @Override
                                public int compare(LogSummary lhs, LogSummary rhs) {
                                    return Long.compare(rhs.getDate(), lhs.getDate());
                                }
                            });
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(RetrofitError error) {
                        Activity context = getActivity();
                        if (context != null) {
                            CroutonHelper.showText(context, R.string.protocol_loading_exception_toast, Style.ALERT, R.id.crouton_handle);
                        }
                    }
                });
            }
        });
    }

    /**
     * On create view.
     *
     * @param inflater the inflater
     * @param container the container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_protocol_list, container, false);
        AbsListView mListView = (AbsListView) rootView.findViewById(R.id.list);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeView);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProtocols();
            }
        });

        AnimationAdapter fadeInAnimation = new SwingBottomInAnimationAdapter(mAdapter);
        fadeInAnimation.setAbsListView(mListView);
        mListView.setAdapter(mAdapter);
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        loadProtocols();
        return rootView;
    }

    /**
     * On attach.
     *
     * @param activity the activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnProtocolListFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * On detach.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * On item click.
     *
     * @param parent the parent
     * @param view the view
     * @param position the position
     * @param id the id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            LogSummary LogSummary = protocols.get(position);
            mListener.onProtocolClicked(LogSummary, (int) view.getX(), (int) view.getY(),
                    view.getWidth(), view.getHeight(), mRecipeId != null);
        }
    }

    /**
     * On create animation.
     *
     * @param transit the transit
     * @param enter the enter
     * @param nextAnim the next anim
     * @return the animation
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return AnimationUtils.loadAnimation(getActivity(),
                enter ? android.R.anim.fade_in : android.R.anim.fade_out);
    }

    /**
     * The listener interface for receiving onProtocolListFragmentInteraction events.
     * The class that is interested in processing a onProtocolListFragmentInteraction
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addOnProtocolListFragmentInteractionListener<code> method. When
     * the onProtocolListFragmentInteraction event occurs, that object's appropriate
     * method is invoked.
     *
     */
    public interface OnProtocolListFragmentInteractionListener {
        
        /**
         * On protocol clicked.
         *
         * @param LogSummary the protocol summary
         * @param x the x
         * @param y the y
         * @param width the width
         * @param height the height
         * @param fromRecipe the from recipe
         */
        public void onProtocolClicked(LogSummary LogSummary, int x, int y, int width, int height, boolean fromRecipe);
    }
}