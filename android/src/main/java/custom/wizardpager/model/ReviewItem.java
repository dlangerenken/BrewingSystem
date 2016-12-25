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

package custom.wizardpager.model;


/**
 * Represents a single line item on the final review page.
 */
public class ReviewItem {
    
    /** The Constant DEFAULT_WEIGHT. */
    private static final int DEFAULT_WEIGHT = 0;

    /** The m weight. */
    private int mWeight;
    
    /** The m title. */
    private String mTitle;
    
    /** The m display value. */
    private String mDisplayValue;
    
    /** The m page key. */
    private String mPageKey;

    /**
     * Instantiates a new review item.
     *
     * @param title the title
     * @param displayValue the display value
     * @param pageKey the page key
     */
    public ReviewItem(String title, String displayValue, String pageKey) {
        this(title, displayValue, pageKey, DEFAULT_WEIGHT);
    }

    /**
     * Instantiates a new review item.
     *
     * @param title the title
     * @param displayValue the display value
     * @param pageKey the page key
     * @param weight the weight
     */
    public ReviewItem(String title, String displayValue, String pageKey, int weight) {
        mTitle = title;
        mDisplayValue = displayValue;
        mPageKey = pageKey;
        mWeight = weight;
    }

    /**
     * Gets the display value.
     *
     * @return the display value
     */
    public String getDisplayValue() {
        return mDisplayValue;
    }

    /**
     * Sets the display value.
     *
     * @param displayValue the new display value
     */
    public void setDisplayValue(String displayValue) {
        mDisplayValue = displayValue;
    }

    /**
     * Gets the page key.
     *
     * @return the page key
     */
    public String getPageKey() {
        return mPageKey;
    }

    /**
     * Sets the page key.
     *
     * @param pageKey the new page key
     */
    public void setPageKey(String pageKey) {
        mPageKey = pageKey;
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
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Gets the weight.
     *
     * @return the weight
     */
    public int getWeight() {
        return mWeight;
    }

    /**
     * Sets the weight.
     *
     * @param weight the new weight
     */
    public void setWeight(int weight) {
        mWeight = weight;
    }
}
