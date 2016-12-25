/*
 * 
 */
package se.brewingsystem.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.inject.Inject;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import de.greenrobot.event.EventBus;
import roboguice.activity.RoboActionBarActivity;
import se.brewingsystem.android.events.AlarmEvent;
import se.brewingsystem.android.gcm.PushUtilities;
import se.brewingsystem.android.ui.BrewingProcessFragment;
import se.brewingsystem.android.ui.ProtocolDetailFragment;
import se.brewingsystem.android.ui.ProtocolListFragment;
import se.brewingsystem.android.ui.RecipeCreationFragment;
import se.brewingsystem.android.ui.RecipeDetailFragment;
import se.brewingsystem.android.ui.RecipeListFragment;
import se.brewingsystem.android.ui.StartFragment;
import se.brewingsystem.android.utilities.DialogHelper;
import general.LogSummary;
import general.RecipeSummary;


/**
 * The Class MainActivity.
 */
public class MainActivity extends RoboActionBarActivity implements RecipeListFragment.OnBrewingProcessFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener, RecipeCreationFragment.OnRecipeCreatedListener,
        ProtocolListFragment.OnProtocolListFragmentInteractionListener, RecipeDetailFragment.IBrewingProcessStarted {

    /** The dialog helper. */
    @Inject
    private
    DialogHelper mDialogHelper;
    
    /** The push utilities. */
    @Inject
    private
    PushUtilities mPushUtilities;

    /** The start fragment. */
    private StartFragment startFragment;
    
    /** The sliding panel. */
    private SlidingUpPanelLayout slidingPanel;
    
    /** The recipe creation fragment. */
    private RecipeCreationFragment recipeCreationFragment;
    
    /** The brewing process fragment. */
    private BrewingProcessFragment brewingProcessFragment;

    /** The Constant ACTION_IODINE_TEST_REQUIRED. */
    public static final String ACTION_IODINE_TEST_REQUIRED = "se.brewingsystem.android.Iodine";

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        if (savedInstanceState == null) {
            brewingProcessFragment = BrewingProcessFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.brewing_steps, brewingProcessFragment, "brewing")
                    .commit();
            startFragment = StartFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, startFragment, "start")
                    .commit();
        } else {
            recipeCreationFragment = (RecipeCreationFragment) getSupportFragmentManager().findFragmentByTag("recipeCreation");
            startFragment = (StartFragment) getSupportFragmentManager().findFragmentByTag("start");
            brewingProcessFragment = (BrewingProcessFragment) getSupportFragmentManager().findFragmentByTag("brewing");
        }

        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingPanel.setAnchorPoint(0.3f);
        brewingProcessFragment.setSlidingPanel(slidingPanel);

        //Listen for changes in the back stack
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();
        slidingPanel.hidePanel();
        // Sync data on load
        if (savedInstanceState == null) {
            mPushUtilities.relog();
            if (ACTION_IODINE_TEST_REQUIRED.equals(getIntent().getAction())){
                // From notification
                brewingProcessFragment.showIodineTestDialog();
            }
        }
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
    public void onEventMainThread(AlarmEvent event){
        showAlarmScreen();
    }

    private boolean alarmScreenShown = false;
    // http://stackoverflow.com/questions/2306503/how-to-make-an-alert-dialog-fill-90-of-screen-size
    private void showAlarmScreen() {
        if (alarmScreenShown){
            return;
        }
        alarmScreenShown = true;
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                alarmScreenShown = false;
            }
        });
        adb.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                alarmScreenShown = false;
            }
        });
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.alarm_screen_layout, null);
        Dialog d = adb.setView(dialoglayout).create();
        // (That new View is just there to have something inside the dialog that can grow big enough to cover the whole screen.)

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        d.show();
        d.getWindow().setAttributes(lp);
    }

    /**
     * On destroy.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPushUtilities.unregisterDevice();
    }

    /**
     * On create options menu.
     *
     * @param menu the menu
     * @return true, if successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * On options item selected.
     *
     * @param item the item
     * @return true, if successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.relog:
                mDialogHelper.setServerUrlDialog();
                return true;
            case R.id.refresh:
                if (brewingProcessFragment != null){
                    brewingProcessFragment.onRefresh();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * On recipe clicked.
     *
     * @param summary the summary
     * @param x the x
     * @param y the y
     * @param width the width
     * @param height the height
     */
    @Override
    public void onRecipeClicked(RecipeSummary summary, int x, int y, int width, int height) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out)
                .replace(R.id.container, RecipeDetailFragment.newInstance(summary, x, y,
                                width, height)
                )
                        // We push the fragment transaction to back stack. User can go back to the
                        // previous fragment by pressing back button.
                .addToBackStack("recipeCreation")
                .commit();
    }

    /**
     * On add recipe click.
     */
    @Override
    public void onAddRecipeClick() {
        recipeCreationFragment = new RecipeCreationFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out)
                .replace(R.id.container, recipeCreationFragment, "recipeCreation")
                .addToBackStack("wizard")
                .commit();
    }

    /**
     * On back pressed.
     */
    @Override
    public void onBackPressed() {
        if (slidingPanel != null && (slidingPanel.isPanelExpanded() || slidingPanel.isPanelAnchored())) {
            slidingPanel.collapsePanel();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * On new intent.
     *
     * @param intent the intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        switch (intent.getAction()) {
            case ACTION_IODINE_TEST_REQUIRED:
                brewingProcessFragment.showIodineTestDialog();
                break;
        }
    }

    /**
     * On back stack changed.
     */
    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    /**
     * Should display home up.
     */
    void shouldDisplayHomeUp() {
        //Enable Up button only  if there are entries in the back stack
        boolean canBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
    }

    /**
     * On support navigate up.
     *
     * @return true, if successful
     */
    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }

    /**
     * On recipe created.
     */
    @Override
    public void onRecipeCreated() {
        reset();
    }

    private void reset(){
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        startFragment = StartFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out)
                .replace(R.id.container, startFragment, "startFragment")
                .commit();

        brewingProcessFragment.onRefresh();
    }

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
    @Override
    public void onProtocolClicked(LogSummary LogSummary, int x, int y, int width, int height, boolean fromRecipe) {
        /* Workaround */
        if (fromRecipe){
            getSupportFragmentManager().popBackStackImmediate();
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out)
                .replace(R.id.container, ProtocolDetailFragment.newInstance(LogSummary, x, y,
                                width, height)).addToBackStack(null)
                .commit();
    }

    @Override
    public void brewingProcessStarted() {
        reset();
    }
}
