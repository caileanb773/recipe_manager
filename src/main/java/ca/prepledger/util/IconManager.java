package ca.prepledger.util;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.prepledger.config.Theme;
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

}
