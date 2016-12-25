/*
 * 
 */
package se.brewingsystem.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dd.CircularProgressButton;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import custom.wizardpager.model.AbstractWizardModel;
import custom.wizardpager.model.ModelCallbacks;
import custom.wizardpager.model.StepPagerStrip;
import custom.wizardpager.page.HopCookingDurationPage;
import custom.wizardpager.page.HopCookingPage;
import custom.wizardpager.page.MashingPage;
import custom.wizardpager.page.Page;
import custom.wizardpager.page.SingleTextPage;
import custom.wizardpager.page.TemperatureLevelPage;
import custom.wizardpager.ui.PageFragmentCallbacks;
import custom.wizardpager.ui.ReviewFragment;
import de.keyboardsurfer.android.widget.crouton.Style;
import general.HopAddition;
import general.TemperatureLevel;
import gson.Serializer;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.brewingsystem.android.R;
import se.brewingsystem.android.utilities.CommonUtilities;
import se.brewingsystem.android.utilities.CroutonHelper;
import se.brewingsystem.android.utilities.RecipeWizard;
import general.HopCookingPlan;
import general.MashingPlan;
import general.Recipe;



/**
 * Fragment which handles the recipe-creation steps
 */
public class RecipeCreationFragment extends BaseFragment implements
        PageFragmentCallbacks, ReviewFragment.Callbacks, ModelCallbacks {
    
    /** The m pager. */
    private ViewPager mPager;
    
    /** The m pager adapter. */
    private MyPagerAdapter mPagerAdapter;
    
    /** The m editing after review. */
    private boolean mEditingAfterReview;

    /** The m wizard model. */
    private AbstractWizardModel mWizardModel;
    
    /** Thenext button. */
    private Button mNextButton;

    /** The complete next button. */
    private CircularProgressButton mCompleteButton;
    
    /** The m prev button. */
    private Button mPrevButton;

    /** The m current page sequence. */
    private List<Page> mCurrentPageSequence;
    
    /** The m step pager strip. */
    private StepPagerStrip mStepPagerStrip;
    
    /** The m consume page selected event. */
    private boolean mConsumePageSelectedEvent;
    
    /** The m listener. */
    private OnRecipeCreatedListener mListener;

    /**
     * On attach.
     *
     * @param activity the activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mWizardModel = new RecipeWizard(getActivity());
        try {
            mListener = (OnRecipeCreatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_creation, container, false);

        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);

        mPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) rootView.findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1, position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        mNextButton = (Button) rootView.findViewById(R.id.next_button);
        mPrevButton = (Button) rootView.findViewById(R.id.prev_button);
        mCompleteButton = (CircularProgressButton) rootView.findViewById(R.id.complete_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dg = new DialogFragment() {
                    @NonNull
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.submit_confirm_message)
                                .setPositiveButton(
                                        R.string.submit_confirm_button,
                                        new CreateRecipeListener())
                                .setNegativeButton(android.R.string.cancel,
                                        null).create();
                    }
                };
                dg.show(getChildFragmentManager(), "create_recipe_dialog");
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditingAfterReview) {
                    mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                } else {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });
        onPageTreeChanged();
        updateBottomBar();

        return rootView;
    }

    /**
     * On page tree changed.
     */
    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 = review step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    /**
     * Update bottom bar.
     */
    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        mCompleteButton.setVisibility(position == mCurrentPageSequence.size() ? View.VISIBLE : View.INVISIBLE);
        mNextButton.setVisibility(position != mCurrentPageSequence.size() ? View.VISIBLE : View.INVISIBLE);
        mPrevButton
                .setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
        if (position != mCurrentPageSequence.size()) {
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }

    }

    /**
     * On destroy.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    /**
     * On save instance state.
     *
     * @param outState the out state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    /**
     * On get model.
     *
     * @return the abstract wizard model
     */
    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    /**
     * On edit screen after review.
     *
     * @param key the key
     */
    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    /**
     * On page data changed.
     *
     * @param page the page
     */
    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    /**
     * On get page.
     *
     * @param key the key
     * @return the page
     */
    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    /**
     * Recalculate cut off page.
     *
     * @return true, if successful
     */
    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    /**
     * Creates the recipe.
     *
     * @param recipe the recipe
     */
    private void createRecipe(Recipe recipe) {
        Map<String, String> query = new HashMap<String, String>();
        query.put("recipe", Serializer.getInstance().toJson(recipe));
        mCompleteButton.setProgress(50);
        mCompleteButton.setIndeterminateProgressMode(true);
        mNetworkCommunication.createRecipe(query, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Activity context = getActivity();
                if (context != null) {
                    CroutonHelper.showText(context, context.getString(R.string.recipe_createad_crouton_text), CommonUtilities.getSuccessStyle(context), R.id.crouton_handle);
                }
                mListener.onRecipeCreated();
                mCompleteButton.setProgress(100);
            }

            @Override
            public void failure(RetrofitError error) {
                Activity context = getActivity();
                if (context != null) {
                    CroutonHelper.showText(context, context.getString(R.string.recipe_not_created_crouton_text), Style.ALERT, R.id.crouton_handle);
                }
                mCompleteButton.setProgress(-1);
                mCompleteButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCompleteButton != null) {
                            mCompleteButton.setProgress(0);
                        }
                    }
                }, 2000);
            }
        });
    }

    /**
     * The listener interface for receiving onRecipeCreated events.
     * The class that is interested in processing a onRecipeCreated
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addOnRecipeCreatedListener<code> method. When
     * the onRecipeCreated event occurs, that object's appropriate
     * method is invoked.
     *
     */
    public interface OnRecipeCreatedListener {
        
        /**
         * Invoked when on recipe is created.
         */
        public void onRecipeCreated();
    }

    /**
     * The Class MyPagerAdapter.
     */
    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        
        /** The m cut off page. */
        private int mCutOffPage;
        
        /** The m primary item. */
        private Fragment mPrimaryItem;

        /**
         * Instantiates a new my pager adapter.
         *
         * @param fm the fm
         */
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Gets the item.
         *
         * @param i the i
         * @return the item
         */
        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        /**
         * Gets the item position.
         *
         * @param object the object
         * @return the item position
         */
        @Override
        public int getItemPosition(Object object) {
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }

        /**
         * Sets the primary item.
         *
         * @param container the container
         * @param position the position
         * @param object the object
         */
        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        /**
         * Gets the count.
         *
         * @return the count
         */
        @Override
        public int getCount() {
            return Math.min(mCutOffPage + 1, mCurrentPageSequence == null ? 1
                    : mCurrentPageSequence.size() + 1);
        }

        /**
         * Gets the cut off page.
         *
         * @return the cut off page
         */
        public int getCutOffPage() {
            return mCutOffPage;
        }

        /**
         * Sets the cut off page.
         *
         * @param cutOffPage the new cut off page
         */
        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }
    }

    /**
     * The listener interface for receiving createRecipe events.
     * The class that is interested in processing a createRecipe
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addCreateRecipeListener<code> method. When
     * the createRecipe event occurs, that object's appropriate
     * method is invoked.
     *
     */
    private class CreateRecipeListener implements DialogInterface.OnClickListener {

        /** The recipe. */
        private final Recipe recipe = new Recipe();

        /**
         * On click.
         *
         * @param dialog the dialog
         * @param which the which
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            for (int pageNumber = 0; pageNumber < mCurrentPageSequence.size(); pageNumber++) {
                Page currentPage = mCurrentPageSequence.get(pageNumber);
                if (currentPage instanceof SingleTextPage) {
                    addSingleText((SingleTextPage) currentPage, pageNumber);
                } else if (currentPage instanceof MashingPage) {
                    addMashing((MashingPage) currentPage);
                } else if (currentPage instanceof HopCookingPage) {
                    addHopCooking((HopCookingPage) currentPage);
                } else if (currentPage instanceof TemperatureLevelPage) {
                    addTempLevel((TemperatureLevelPage) currentPage);
                } else if (currentPage instanceof HopCookingDurationPage){
                    addDurationHopCooking((HopCookingDurationPage) currentPage);
                }
            }
            recipe.setDate(new Date().getTime());
            createRecipe(recipe);
        }

        /**
         * Adds the temp level.
         *
         * @param currentPage the current page
         */
        private void addTempLevel(TemperatureLevelPage currentPage) {
            MashingPlan plan = recipe.getMashingPlan();
            if (plan == null) {
                plan = new MashingPlan();
                recipe.setMashingPlan(plan);
            }
            List<TemperatureLevel> temperatureLevels = currentPage.getContent();
            if (temperatureLevels != null && temperatureLevels.size() > 1){
                for (int i = 0; i < temperatureLevels.size(); i +=2){
                    TemperatureLevel level1 = temperatureLevels.get(i);
                    TemperatureLevel level2 = temperatureLevels.get(i + 1);
                    level2.setStartTime(level1.getStartTime() + level1.getDuration());
                }
            }
            plan.setTemperatureLevels(temperatureLevels);
        }

        /**
         * Adds the hop cooking.
         *
         * @param currentPage the current page
         */
        private void addDurationHopCooking(HopCookingDurationPage currentPage) {
            HopCookingPlan plan = recipe.getHopCookingPlan();
            if (plan == null) {
                plan = new HopCookingPlan();
                recipe.setHopCookingPlan(plan);
            }
            plan.setDuration(currentPage.getDuration());
        }

        /**
         * Adds the hop cooking.
         *
         * @param currentPage the current page
         */
        private void addHopCooking(HopCookingPage currentPage) {
            HopCookingPlan plan = recipe.getHopCookingPlan();
            if (plan == null) {
                plan = new HopCookingPlan();
                recipe.setHopCookingPlan(plan);
            }
            List<HopAddition> hopAdditions = currentPage.getContent();
            long duration = plan.getDuration();
            for(HopAddition hop : hopAdditions){
               duration = hop.getInputTime() > duration ? hop.getInputTime() + 5000 : duration;
            }
            plan.setDuration(duration);
            plan.setHopAdditions(currentPage.getContent());
        }

        /**
         * Adds the mashing.
         *
         * @param currentPage the current page
         */
        private void addMashing(MashingPage currentPage) {
            MashingPlan plan = recipe.getMashingPlan();
            if (plan == null) {
                plan = new MashingPlan();
                recipe.setMashingPlan(plan);
            }
            plan.setMaltAdditions(currentPage.getContent());
        }

        /**
         * Adds the single text.
         *
         * @param singleTextPage the single text page
         * @param pageNumber the page number
         */
        private void addSingleText(SingleTextPage singleTextPage, int pageNumber) {
            String content = singleTextPage.getContent();
            switch (pageNumber) {
                case 0:
                    recipe.setName(content);
                    break;
                case 1:
                    recipe.setId(content);
                    break;
                case 2:
                    recipe.setDescription(content);
                    break;
                default:
                    break;
            }
        }
    }
}