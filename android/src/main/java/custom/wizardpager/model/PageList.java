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

import java.util.ArrayList;

import custom.wizardpager.page.Page;


/**
 * Represents a list of wizard pages.
 */
public class PageList extends ArrayList<Page> implements PageTreeNode {

    /**
     * Instantiates a new page list.
     */
    public PageList() {

    }

    /**
     * Instantiates a new page list.
     *
     * @param pages the pages
     */
    public PageList(Page... pages) {
        for (Page page : pages) {
            add(page);
        }
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.model.PageTreeNode#findByKey(String)
     */
    @Override
    public Page findByKey(String key) {
        for (Page childPage : this) {
            Page found = childPage.findByKey(key);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    /* (non-Javadoc)
     * @see java.custom.wizardpager.model.PageTreeNode#flattenCurrentPageSequence(ArrayList)
     */
    @Override
    public void flattenCurrentPageSequence(ArrayList<Page> dest) {
        for (Page childPage : this) {
            childPage.flattenCurrentPageSequence(dest);
        }
    }
}
