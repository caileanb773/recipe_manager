package ca.prepledger.service;

/**
 * Listener for the in-memory list of recipes. The listener is notified whenever
 * the list changes, so that interested parties can update themselves. Mostly this
 * is uses for the recipe list view to maintain an accurate depiction of all
 * current recipes.
 * 
 * @author Cailean Bernard
 * @since v0.1.1
 */
public interface RecipeChangeListener {
	
	void onRecipesChanged();

}
