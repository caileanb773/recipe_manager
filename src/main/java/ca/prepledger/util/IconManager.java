package ca.prepledger.util;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.config.Theme;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconManager {

	private static final String ICON_ROOT = "/img/icon/scaled/";
	private static final Logger logger = LoggerFactory.getLogger(IconManager.class);


	private IconManager() {}

	public static void applyTheme(ImageView imageView, Theme theme) {
		if (imageView == null || imageView.getImage() == null) {
			logger.warn("applyTheme(): Passed ImageView or its Image == null.");
			return;
		}

		String currentUrl = imageView.getImage().getUrl();

		if (currentUrl == null) {
			logger.warn("applyTheme(): currentUrl == null.");
			return;
		}

		String fileName = currentUrl.substring(currentUrl.lastIndexOf('/') + 1);

		String folder = switch (theme) {
			case LIGHT -> "dark";
			case DARK -> "light";
		};
		
		URL resource = IconManager.class.getResource(
				ICON_ROOT + folder + "/" + fileName);
		
		if (resource != null) {
			imageView.setImage(new Image(resource.toExternalForm()));
		} else {
			logger.warn("applyTheme(): resource == null.");
		}
	}
	
	/**
	 * Recursive method that finds the Parent nodes in a graph, gets the children
	 * of said parent, and calls "applyTheme()" on those children if the Node in
	 * question is an ImageView.
	 * 
	 * @since v0.1.2
	 */
	public static void applyThemeToTree(
	        Node root, Theme theme, Node excludedNode) {

	    if (root == excludedNode) {
	        return;
	    }
	    
	    if (root instanceof Labeled labeled) {
	        boolean fixedIcon =
	            labeled.getStyleClass().contains("primary-button") ||
	            labeled.getStyleClass().contains("danger-button");

	        if (fixedIcon) {
	            return;
	        }

	        if (labeled.getGraphic() != null) {
	            applyThemeToTree(labeled.getGraphic(), theme, excludedNode);
	        }
	    }

	    if (root instanceof ImageView imageView) {
	        applyTheme(imageView, theme);
	    }
	    
	    if (root instanceof Parent parent) {
	        for (Node child : parent.getChildrenUnmodifiable()) {
	            applyThemeToTree(child, theme, excludedNode);
	        }
	    }
	}

}
