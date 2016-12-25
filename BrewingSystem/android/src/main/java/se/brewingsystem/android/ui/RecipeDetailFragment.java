/*
 * 
 */
package se.brewingsystem.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.ocpsoft.pretty.time.PrettyTime;

import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Style;
import general.BrewingState;
import me.grantland.widget.AutofitHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.brewingsystem.android.R;
import se.brewingsystem.android.utilities.CommonUtilities;
import general.RecipeSummary;
import se.brewingsystem.android.utilities.CroutonHelper;

/**
 * Recipe-Detail View which shows a single recipe with the whole information about it
 */
public class RecipeDetailFragment extends AnimateInFragment {
    
    /** The Constant ARG_RECIPE. */
    private static final String ARG_RECIPE = "recipe";

    /** The m recipe. */
    private RecipeSummary mRecipe;

    /**
     * Interface which is called when the brewing process is started
     */
    public interface IBrewingProcessStarted{
        void brewingProcessStarted();
    }
    /**
     * Instantiates a new recipe detail fragment.
     */
    public RecipeDetailFragment() {
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
    public static RecipeDetailFragment newInstance(RecipeSummary summary, int x, int y, int width, int height) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPE, summary);
        putArgs(args, x, y, width, height);
        fragment.setArguments(args);
        return fragment;
    }

    private IBrewingProcessStarted listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IBrewingProcessStarted){
            listener = (IBrewingProcessStarted) activity;
        }else{
            throw new ClassCastException("IBrewingProcessStarted not implemented");
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
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false);
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
            mRecipe = (RecipeSummary) getArguments().getSerializable(ARG_RECIPE);
        }
        inflateAndAdd(view, R.layout.recipe_list_item, args);
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

        AutofitHelper.create(title);
        AutofitHelper.create(description);

        title.setText(mRecipe.getTitle());
        description.setText(mRecipe.getDescription());
        date.setText(new PrettyTime().format(new Date(mRecipe.getDate())));
    }

    /**
     * On animation end.
     *
     * @param animation the animation
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        View rootView = onAnimEnd(R.layout.fragment_recipe_detail_content);
        if (rootView == null){
            return;
        }

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        viewPager.setAdapter(new RecipeDetailTabAdapter());
        FloatingActionButton startBrewingButton = (FloatingActionButton) rootView.findViewById(R.id.button_floating_action);
        startBrewingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dg = new DialogFragment() {
                    @NonNull
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new AlertDialog.Builder(getActivity())
                                .setMessage("Wollen Sie einen neuen Brauvorgang mit diesem Rezept starten?")
                                .setPositiveButton(
                                        R.string.submit_confirm_button,
                                        new StartBrewingListener())
                                .setNegativeButton(android.R.string.cancel,
                                        null).create();
                    }
                };
                dg.show(getChildFragmentManager(), "new_brewing_process");
            }
        });
    }

    /**
     * The Class RecipeDetailTabAdapter which handles the different options within the recipe-detail view.
     */
    public class RecipeDetailTabAdapter extends FragmentStatePagerAdapter {
        
        /** The titles. */
        private final String[] titles;

        /**
         * Instantiates a new my adapter.
         */
        public RecipeDetailTabAdapter() {
            super(getChildFragmentManager());
            Context mContext = getActivity();
            if (mContext != null){
                titles = new String[]{getActivity().getString(R.string.recipe_detail_header_information),
                        getActivity().getString(R.string.recipe_detail_header_protocol)};
            }else{
                titles = new String[]{"",""};
            }
        }

        /**
         * Gets the page title.
         *
         * @param position the position
         * @return the page title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        /**
         * Gets the count.
         *
         * @return the count
         */
        @Override
        public int getCount() {
            return titles.length;
        }

        /**
         * Gets the item.
         *
         * @param position the position
         * @return the item
         */
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return RecipeInfoFragment.newInstance(mRecipe.getId());
                case 1:
                    return ProtocolListFragment.newInstance(mRecipe.getId());
            }
            return RecipeInfoFragment.newInstance(mRecipe.getId());
        }
    }

    /**
     * The listener interface for receiving startBrewing events.
     * The class that is interested in processing a startBrewing
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addStartBrewingListener<code> method. When
     * the startBrewing event occurs, that object's appropriate
     * method is invoked.
     *
     */
    private class StartBrewingListener implements DialogInterface.OnClickListener {

        /**
         * On click.
         *
         * @param dialog the dialog
         * @param which the which
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mNetworkCommunication.startBrewing(mRecipe.getId(), new Callback<BrewingState>() {
                @Override
                public void success(BrewingState brewingState, Response response) {
                    Activity mContext = getActivity();
                    if (mContext != null) {
                        CroutonHelper.showText(mContext, mContext.getString(R.string.brewing_process_started_text), CommonUtilities.getSuccessStyle(getActivity()), R.id.crouton_handle);
                    }
                    listener.brewingProcessStarted();
                }
                @Override
                public void failure(RetrofitError error) {
                        Activity mContext = getActivity();
                        if (mContext != null) {
                            CroutonHelper.showText(mContext, mContext.getString(R.string.brewing_process_not_found_exception_text), Style.ALERT, R.id.crouton_handle);
                        }
                }
            });
        }
    }
}
