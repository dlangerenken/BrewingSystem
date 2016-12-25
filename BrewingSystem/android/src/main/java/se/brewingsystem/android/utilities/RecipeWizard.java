/*
 * 
 */
package se.brewingsystem.android.utilities;

import android.content.Context;

import custom.wizardpager.model.AbstractWizardModel;
import custom.wizardpager.model.PageList;
import custom.wizardpager.page.BranchPage;
import custom.wizardpager.page.HopCookingDurationPage;
import custom.wizardpager.page.HopCookingPage;
import custom.wizardpager.page.MashingPage;
import custom.wizardpager.page.SingleTextPage;
import custom.wizardpager.page.TemperatureLevelPage;
import se.brewingsystem.android.R;

/**
 * Created by Daniel on 30.11.2014.
 */
public class RecipeWizard extends AbstractWizardModel {
    
    /**
     * Instantiates a new recipe wizard.
     *
     * @param context the context
     */
    public RecipeWizard(Context context) {
        super(context);
        if (context == null){
            throw new IllegalArgumentException("Context must not be null");
        }
    }

    /**
     * On new root page list.
     *
     * @return the page list
     */
    @Override
    protected PageList onNewRootPageList() {
        BranchPage mashBranch = new BranchPage(this, mContext.getString(R.string.mashing_question_recipe_wizard)).
                addBranch(mContext.getString(R.string.recipe_wizard_confirm_text),
                        MashingPage.create(this, mContext).setRequired(true),
                        (TemperatureLevelPage.create(this, mContext).setRequired(true))).
                addBranch(mContext.getString(R.string.recipe_wizard_decline)).setValue(mContext.getString(R.string.recipe_wizard_no_mashing));

        BranchPage hopCookingPage = new BranchPage(this, mContext.getString(R.string.recipe_wizard_hop_cooking)).
                addBranch(mContext.getString(R.string.recipe_wizard_confirm_text),
                        new HopCookingDurationPage(this, "Dauer des Hopfenkochens").setRequired(true),
                        HopCookingPage.create(this, mContext).setRequired(true)).
                addBranch(mContext.getString(R.string.recipe_wizard_decline)).setValue(mContext.getString(R.string.recipe_wizard_no_hop_cooking));

        return new PageList(new SingleTextPage(this, mContext.getString(R.string.recipe_wizard_recipe_name), false).setRequired(true),
                new SingleTextPage(this, mContext.getString(R.string.recipe_wizard_recipe_id), false).setRequired(false),
                new SingleTextPage(this, mContext.getString(R.string.recipe_wizard_recipe_desc), true).setRequired(true),
                mashBranch, hopCookingPage);
    }
}
