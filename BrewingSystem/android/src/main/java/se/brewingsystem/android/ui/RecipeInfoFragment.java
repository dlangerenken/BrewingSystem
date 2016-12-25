/*
 * 
 */
package se.brewingsystem.android.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import custom.wizardpager.model.ReviewItem;
import retrofit.client.Response;
import se.brewingsystem.android.R;
import se.brewingsystem.android.network.BrewingCallback;
import se.brewingsystem.android.utilities.CommonUtilities;
import se.brewingsystem.android.utilities.MessageHelper;
import se.brewingsystem.android.utilities.ReviewAdapter;
import general.Recipe;


/**
 * The Class RecipeInfoFragment.
 */
public class RecipeInfoFragment extends BaseFragment {

    /** The Constant ARG_RECIPE_ID. */
    private static final String ARG_RECIPE_ID = "recipe";
    
    /** The m recipe id. */
    private String mRecipeId;
    
    /** The m recipe. */
    private Recipe mRecipe;
    
    /** The m adapter. */
    private BaseAdapter mAdapter;
    
    /** The review items. */
    private List<ReviewItem> reviewItems;
    
    /** The swipe refresh layout. */
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Instantiates a new recipe info fragment.
     */
    public RecipeInfoFragment() {
        // Required empty public constructor
    }

    /**
     * New instance.
     *
     * @param recipeId the recipe id
     * @return the recipe info fragment
     */
    public static RecipeInfoFragment newInstance(String recipeId) {
        RecipeInfoFragment fragment = new RecipeInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPE_ID, recipeId);
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
        reviewItems = new ArrayList<>();
        mAdapter = new ReviewAdapter(getActivity(), reviewItems);
    }

    /**
     * Gets the recipe.
     *
     * @return the recipe
     */
    private void getRecipe() {
        /*
         * Workaround
         * http://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                mNetworkCommunication.getRecipe(mRecipeId, new BrewingCallback<Recipe>() {
                    @Override
                    public void onSuccess(Recipe recipe, Response response) {
                        mRecipe = recipe;
                        updateContent();
                    }

                    @Override
                    public void onEnd() {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                });
            }
        });
    }

    /**
     * Update content.
     */
    private void updateContent() {
        List<ReviewItem> items = new ArrayList<>();
        Context mContext = getActivity();
        if (mRecipe != null && mContext != null) {
            if (mRecipe.getHopCookingPlan() != null) {

                items.add(new ReviewItem("Hopfenkochen-Dauer", CommonUtilities.convertSecondsToHMmSs(mRecipe.getHopCookingPlan().getDuration() / 1000), null));
                items.add(new ReviewItem(mContext.getString(R.string.hop_review_item_title), MessageHelper.getListOfIngrediends(mRecipe.getHopCookingPlan().getHopAdditions()), null));

            }
            if (mRecipe.getMashingPlan() != null) {
                items.add(new ReviewItem(mContext.getString(R.string.mash_addition_review_item_title), MessageHelper.getListOfIngrediends(mRecipe.getMashingPlan().getMaltAdditions()), null));
                items.add(new ReviewItem(mContext.getString(R.string.temp_level_review_item_title), MessageHelper.getTemperatureLevelReviewItem(mRecipe.getMashingPlan().getTemperatureLevels()), null));
            }
        }

        reviewItems.clear();
        reviewItems.addAll(items);
        mAdapter.notifyDataSetChanged();
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
        View rootView = inflater.inflate(R.layout.fragment_recipe_info, container, false);
        ListView mListView = (ListView) rootView.findViewById(R.id.information_list);
        mListView.setAdapter(mAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeView);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRecipe();
            }
        });
        swipeRefreshLayout.setEnabled(false);
        getRecipe();
        return rootView;
    }


}
