/*
 * 
 */
package se.brewingsystem.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Style;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.brewingsystem.android.R;
import se.brewingsystem.android.network.BrewingCallback;
import se.brewingsystem.android.utilities.CroutonHelper;
import se.brewingsystem.android.utilities.RecipeListAdapter;
import general.RecipeSummary;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link RecipeListFragment.OnBrewingProcessFragmentInteractionListener}
 * interface.
 */
public class RecipeListFragment extends BaseFragment implements AbsListView.OnItemClickListener {

    /** The m listener. */
    private OnBrewingProcessFragmentInteractionListener mListener;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private BaseAdapter mAdapter;
    
    /** The recipes. */
    private List<RecipeSummary> recipes;

    /** The swipe refresh layout. */
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeListFragment() {
    }

    /**
     * New instance.
     *
     * @return the recipe list fragment
     */
    public static RecipeListFragment newInstance() {
        return new RecipeListFragment();
    }

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipes = new ArrayList<>();
        mAdapter = new RecipeListAdapter(getActivity(), recipes);
    }

    /**
     * Load recipes.
     */
    private void loadRecipes() {
        /*
         * Workaround
         * http://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                mNetworkCommunication.getRecipes(new BrewingCallback<List<RecipeSummary>>() {
                    @Override
                    public void onSuccess(List<RecipeSummary> recipeSummaries, Response response) {
                        if (recipes != null) {
                            recipes.clear();
                            recipes.addAll(recipeSummaries);
                            Collections.sort(recipes, new Comparator<RecipeSummary>() {
                                @Override
                                public int compare(RecipeSummary lhs, RecipeSummary rhs) {
                                    return Long.compare(rhs.getDate(), lhs.getDate());
                                }
                            });
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onEnd() {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(RetrofitError error) {
                        Activity context = getActivity();
                        if (context != null) {
                            CroutonHelper.showText(context, R.string.recipe_load_failed_message, Style.ALERT, R.id.crouton_handle);
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        AbsListView mListView = (AbsListView) rootView.findViewById(android.R.id.list);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeView);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRecipes();
            }
        });

        AnimationAdapter fadeInAnimation = new SwingBottomInAnimationAdapter(mAdapter);
        fadeInAnimation.setAbsListView(mListView);
        mListView.setAdapter(fadeInAnimation);

        // floating action button
        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.button_floating_action);
        floatingActionButton.attachToListView(mListView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onAddRecipeClick();
                }
            }
        });

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        loadRecipes();
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
            mListener = (OnBrewingProcessFragmentInteractionListener) activity;
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
            RecipeSummary recipe = recipes.get(position);
            mListener.onRecipeClicked(recipe, (int) view.getX(), (int) view.getY(),
                    view.getWidth(), view.getHeight());
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *
     * @see OnBrewingProcessFragmentInteractionListener
     */
    public interface OnBrewingProcessFragmentInteractionListener {
        
        /**
         * On recipe clicked.
         *
         * @param recipe the recipe
         * @param x the x
         * @param y the y
         * @param width the width
         * @param height the height
         */
        public void onRecipeClicked(RecipeSummary recipe, int x, int y, int width, int height);

        /**
         * On add recipe click.
         */
        public void onAddRecipeClick();
    }
}
