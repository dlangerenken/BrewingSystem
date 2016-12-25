/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package custom.wizardpager.page;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import custom.wizardpager.model.ModelCallbacks;
import custom.wizardpager.model.PageTreeNode;
import custom.wizardpager.model.ReviewItem;


/**
 * Represents a single page in the wizard.
 */
public abstract class Page implements PageTreeNode {
    /**
     * The key into {@link #getData()} used for wizards with simple (single) values.
     */
    public static final String SIMPLE_DATA_KEY = "_";
    
    /** The Constant COMPLEX_DATA_KEY. */
    public static final String COMPLEX_DATA_KEY = "#";

    /** The m callbacks. */
    final ModelCallbacks mCallbacks;

    /**
     * Current wizard values/selections.
     */
    Bundle mData = new Bundle();
    
    /** The m title. */
    private final String mTitle;
    
    /** The m required. */
    private boolean mRequired = false;
    
    /** The m parent key. */
    private String mParentKey;

    /**
     * Instantiates a new page.
     *
     * @param callbacks the callbacks
     * @param title the title
     */
    Page(ModelCallbacks callbacks, String title) {
        mCallbacks = callbacks;
        mTitle = title;
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public Bundle getData() {
        return mData;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Checks if is required.
     *
     * @return true, if is required
     */
    public boolean isRequired() {
        return mRequired;
    }

    /**
     * Sets the required.
     *
     * @param required the required
     * @return the page
     */
    public Page setRequired(boolean required) {
        mRequired = required;
        return this;
    }

    /**
     * Sets the parent key.
     *
     * @param parentKey the new parent key
     */
    void setParentKey(String parentKey) {
        mParentKey = parentKey;
    }

    /**
     * Find by key.
     *
     * @param key the key
     * @return the page
     */
    @Override
    public Page findByKey(String key) {
        return getKey().equals(key) ? this : null;
    }

    /**
     * Flatten current page sequence.
     *
     * @param dest the dest
     */
    @Override
    public void flattenCurrentPageSequence(ArrayList<Page> dest) {
        dest.add(this);
    }

    /**
     * Creates the fragment.
     *
     * @return the fragment
     */
    public abstract Fragment createFragment();

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
        return (mParentKey != null) ? mParentKey + ":" + mTitle : mTitle;
    }

    /**
     * Gets the review items.
     *
     * @param dest the dest
     * @return the review items
     */
    public abstract void getReviewItems(ArrayList<ReviewItem> dest);

    /**
     * Checks if is completed.
     *
     * @return true, if is completed
     */
    public boolean isCompleted() {
        return true;
    }

    /**
     * Reset data.
     *
     * @param data the data
     */
    public void resetData(Bundle data) {
        mData = data;
        notifyDataChanged();
    }

    /**
     * Notify data changed.
     */
    public void notifyDataChanged() {
        mCallbacks.onPageDataChanged(this);
    }
}
