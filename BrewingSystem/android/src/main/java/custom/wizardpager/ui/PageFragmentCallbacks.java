/*
 * 
 */
package custom.wizardpager.ui;


import custom.wizardpager.page.Page;


/**
 * The Interface PageFragmentCallbacks.
 */
public interface PageFragmentCallbacks {
    
    /**
     * On get page.
     *
     * @param key the key
     * @return the page
     */
    Page onGetPage(String key);
}
