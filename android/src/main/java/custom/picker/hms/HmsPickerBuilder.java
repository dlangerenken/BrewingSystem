/*
 * 
 */
package custom.picker.hms;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.Vector;


/**
 * User: derek Date: 5/2/13 Time: 7:55 PM.
 */
public class HmsPickerBuilder {

    /** The manager. */
    private FragmentManager manager; // Required
    
    /** The target fragment. */
    private Fragment targetFragment;
    
    /** The m reference. */
    private int mReference;
    
    /** The m title. */
    private String mTitle;
    
    /** The m hms picker dialog handlers. */
    private final Vector<HmsPickerDialogFragment.HmsPickerDialogHandler> mHmsPickerDialogHandlers = new Vector<>();

    /**
     * Attach a FragmentManager. This is required for creation of the Fragment.
     *
     * @param manager the FragmentManager that handles the transaction
     * @return the current Builder object
     */
    public HmsPickerBuilder setFragmentManager(FragmentManager manager) {
        this.manager = manager;
        return this;
    }

    /**
     * Attach a target Fragment. This is optional and useful if creating a Picker within a Fragment.
     *
     * @param targetFragment the Fragment to attach to
     * @return the current Builder object
     */
    public HmsPickerBuilder setTargetFragment(Fragment targetFragment) {
        this.targetFragment = targetFragment;
        return this;
    }

    /**
     * Attach a reference to this Picker instance. This is used to track multiple pickers, if the user wishes.
     *
     * @param reference a user-defined int intended for Picker tracking
     * @return the current Builder object
     */
    public HmsPickerBuilder setReference(int reference) {
        this.mReference = reference;
        return this;
    }

    /**
     * Attach a reference to this Picker instance. This is used to track multiple pickers, if the user wishes.
     *
     * @param title the title
     * @return the current Builder object
     */
    public HmsPickerBuilder setTitle(String title) {
        this.mTitle = title;
        return this;
    }
    /**
     * Attach universal objects as additional handlers for notification when the Picker is set. For most use cases, this
     * method is not necessary as attachment to an Activity or Fragment is done automatically.  If, however, you would
     * like additional objects to subscribe to this Picker being set, attach Handlers here.
     *
     * @param handler an Object implementing the appropriate Picker Handler
     * @return the current Builder object
     */
    public HmsPickerBuilder addHmsPickerDialogHandler(HmsPickerDialogFragment.HmsPickerDialogHandler handler) {
        this.mHmsPickerDialogHandlers.add(handler);
        return this;
    }

    /**
     * Remove objects previously added as handlers.
     *
     * @param handler the Object to remove
     * @return the current Builder object
     */
    public HmsPickerBuilder removeHmsPickerDialogHandler(HmsPickerDialogFragment.HmsPickerDialogHandler handler) {
        this.mHmsPickerDialogHandlers.remove(handler);
        return this;
    }

    /**
     * Instantiate and show the Picker.
     */
    public void show() {
        if (manager == null) {
            Log.e("HmsPickerBuilder", "setFragmentManager() must be called.");
            return;
        }
        final FragmentTransaction ft = manager.beginTransaction();
        final Fragment prev = manager.findFragmentByTag("hms_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        final HmsPickerDialogFragment fragment = HmsPickerDialogFragment.newInstance(mReference, mTitle);
        if (targetFragment != null) {
            fragment.setTargetFragment(targetFragment, 0);
        }
        fragment.setHmsPickerDialogHandlers(mHmsPickerDialogHandlers);
        fragment.show(ft, "hms_dialog");
    }
}
