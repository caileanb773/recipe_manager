package view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import definitions.Recipe;
import definitions.Theme;
import init.Main;
import util.Config;
import util.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Author: Cailean Bernard
 * Contents: The frame of the app which contains the user interface, the part of
 * the screen that the user interacts with.
 */

public class AppFrame {

	// Displayed screens & layout
	private JFrame frame;
	private RecipeScreen recipeScreen;
	private LoginScreen loginScreen;
	private RegisterScreen registerScreen;
	private NotificationScreen notificationScreen;
	private Config config;
	private ResourceBundle bundle;
	private CardLayout cardLayout;
	private Container container;

	// Menu bar
	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenu menuOpt;
	private JMenu menuLang;
	private JMenu menuAccount;
	private JMenuItem menuBtnExport;
	private JMenuItem menuBtnImport;
	private JMenuItem menuBtnEn;
	private JMenuItem menuBtnFr                                                                                                                                                            ;
	private JMenuItem menuBtnReadMe;
	private JMenuItem menuBtnLogout;
	private static JCheckBox autoBackup;
	private JMenu menuTheme;
	private JRadioButtonMenuItem themeLight;
	private JRadioButtonMenuItem themeDark;
	private Theme currentTheme;

	// DEBUGGING OPTIONS -- XXX -- TO BE DELETED //
	private JMenuItem debugLogin;
	private JMenuItem debugAddNotif;

	// Other
	private ActionListener listener;
	private ProgressListener progressListener;
	private static final Logger logger = LoggerFactory.getLogger(AppFrame.class);


	public AppFrame() {
		frame = new JFrame();
		cardLayout = new CardLayout();
		frame.getContentPane().setLayout(cardLayout);
		frame.setTitle("Macromise Recipe Manager");		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void initialize() {
		reportProgress(7, "Loading config...");

		// ---------------------------------------------------------------------
		// C O M P O N E N T S
		// ---------------------------------------------------------------------
		config = new Config();
		reportProgress(10, "Loading language...");

		bundle = config.getResourceBundle();
		recipeScreen = new RecipeScreen(bundle);
		loginScreen = new LoginScreen(bundle);
		registerScreen = new RegisterScreen(bundle);
		notificationScreen = new NotificationScreen(bundle);
		container = frame.getContentPane();
		currentTheme = config.getTheme();
		reportProgress(12, "Loading images...");

		// Icon
		try {
			URL iconUrl = Main.class.getClassLoader().getResource("img/icon.png");		
			ImageIcon icon = new ImageIcon(iconUrl);
			frame.setIconImage(icon.getImage());
		} catch (NullPointerException e) {
			logger.error("Could not find icon.png.");
		}

		reportProgress(15, "Loading screens...");

		// ----- Adding Screens to Frame -----
		container.add(loginScreen, "LOGIN");
		container.add(recipeScreen, "RECIPE_SCREEN");
		container.add(registerScreen, "REGISTER_SCREEN");
		container.add(notificationScreen, "NOTIFICATION_SCREEN");
		reportProgress(20, "Initializing screens...");

		// ---------------------------------------------------------------------
		// M E N U  B A R
		// ---------------------------------------------------------------------
		menuBar = new JMenuBar();
		menuFile = new JMenu(bundle.getString("menuFile"));
		menuBtnExport = new JMenuItem(bundle.getString("menuBtnExport"));
		menuBtnImport = new JMenuItem(bundle.getString("menuBtnImport"));
		menuFile.add(menuBtnImport);
		menuFile.add(menuBtnExport);

		// XXX Debugging
		debugLogin = new JMenuItem("DEBUG: Force Login");
		menuFile.add(debugLogin);
		debugLogin.addActionListener(ignored -> listener.actionPerformed(
				new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login")));
		
		debugAddNotif = new JMenuItem("DEBUG: Add Notification");
		menuFile.add(debugAddNotif);
		debugAddNotif.addActionListener(ignored -> listener.actionPerformed(
				new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "dbgAddNotif")));

		menuOpt = new JMenu(bundle.getString("menuOpt"));
		menuLang = new JMenu(bundle.getString("menuLang"));
		menuBtnReadMe = new JMenuItem(bundle.getString("menuBtnReadMe"));
		menuOpt.add(menuLang);
		menuOpt.add(menuBtnReadMe);
		menuAccount = new JMenu(bundle.getString("menuAccount"));
		menuBtnLogout = new JMenuItem(bundle.getString("menuBtnLogout"));
		menuAccount.add(menuBtnLogout);
		menuBtnEn = new JMenuItem(bundle.getString("menuBtnEn"));
		menuBtnFr = new JMenuItem(bundle.getString("menuBtnFr"));
		menuLang.add(menuBtnEn);
		menuLang.add(menuBtnFr);
		menuBar.add(menuFile);
		menuBar.add(menuOpt);
		menuBar.add(menuAccount);
		frame.setJMenuBar(menuBar);
		autoBackup = new JCheckBox(" " + bundle.getString("autoBackup")); // space for spacing
		autoBackup.setSelected(config.isAutoBackup());
		menuOpt.add(autoBackup);
		menuTheme = new JMenu(bundle.getString("theme"));
		ButtonGroup themeGroup = new ButtonGroup();
		reportProgress(25, "Loading theme...");

		// Theme section
		themeLight = new JRadioButtonMenuItem(bundle.getString("theme.Light"));
		themeDark = new JRadioButtonMenuItem(bundle.getString("theme.Dark"));
		themeGroup.add(themeLight);
		themeGroup.add(themeDark);
		menuOpt.add(menuTheme);
		menuTheme.add(themeLight);
		menuTheme.add(themeDark);
		themeLight.addActionListener(ignored -> fireThemeChangeEvent(Theme.LIGHT));
		themeDark.addActionListener(ignored -> fireThemeChangeEvent(Theme.DARK));
		reportProgress(30, "Final UI initialization...");

		if (currentTheme == Theme.LIGHT) {
			themeLight.setSelected(true);
		} else {
			themeDark.setSelected(true);
		}

		// Final JFrame Settings
		frame.pack();
		frame.setLocationRelativeTo(null);

		// Set Appropriate Buttons to be Selected
		updateLanguageButtons();	
		setEnabledButtons("LOGIN");

		// Set Focus if Login Screen remembers an email
		if (LoginScreen.isRemembering()) {
			loginScreen.grabFocus("PASSWORD_FIELD");
		} else {
			loginScreen.grabFocus("LOGIN_FIELD");
		}

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		reportProgress(35, "UI Initialized.");
	}

	public void setProgressListener(ProgressListener progressListener) {
		if (progressListener != null) {
			this.progressListener = progressListener;
		}
	}

	public void reportProgress(int percent, String msg) {
		if (progressListener != null) {
			progressListener.onProgress(percent, msg);	
		}
	}

	public void setViewVisible(boolean visible) {
		frame.setVisible(visible);
	}

	public void fireThemeChangeEvent(Theme theme) {
		if (theme.toString().isEmpty()) {
			logger.error("No theme passed to fireThemeChangeEvent().");
			return;
		}

		if (!(theme == Theme.LIGHT || theme == Theme.DARK)) {
			logger.error("Unrecognized theme passed to fireThemeChangeEvent().");
			return;
		}

		// Notify config of the theme change
		config.setTheme(theme);

		// Switch the FlatLaf library
		applyTheme(theme);

		// Fire the theme change event to the controller
		ActionEvent themeChange = new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED,
				"switchTheme&" + theme.toString());
		listener.actionPerformed(themeChange);
	}

	public void changeThemeInChildren(Theme theme) {
		// change theme in registerscreen, loginscreen, recipescreen
		// tell the model what the current theme is so the config can save it
		loginScreen.changeTheme(theme);
		registerScreen.changeTheme(theme);
		recipeScreen.changeTheme(theme);
		notificationScreen.changeTheme(theme);
	}

	public void applyTheme(Theme theme) {
		try {
			switch (theme) {
			case LIGHT:
				UIManager.setLookAndFeel(new FlatLightLaf());
		        UIManager.put("Component.focusColor", new Color(152, 195, 235));
		        UIManager.put("Button.hoverBorderColor", new Color(137, 176, 212));
		        UIManager.put("Button.focusedBorderColor", new Color(137, 176, 212));		  
				break;
			case DARK:
				UIManager.setLookAndFeel(new FlatDarkLaf());				
		        UIManager.put("Component.focusColor", new Color(224, 116, 129));
		        UIManager.put("Button.hoverBorderColor", new Color(224, 57, 77));
		        UIManager.put("Button.focusedBorderColor", new Color(122, 31, 42));		        
				break;
			default:
				return;
			}

		} catch (UnsupportedLookAndFeelException e) {
			logger.error("Exception caught while switching theme: {}", e.getMessage());
		} catch (Exception e) {
			logger.error("Exception caught while switching theme: {}", e.getMessage());
		}

		for (Window w : Window.getWindows()) {
			SwingUtilities.updateComponentTreeUI(w);
		}
	}

	public void populateRecipeList(List<Recipe> recipeList) {
		recipeScreen.populateRecipeSelectList(recipeList);
		recipeScreen.displayRecipeButtons(); 
	}

	public void updateLanguageButtons() {
		boolean isEnglish = config.getLocale().equals(Locale.ENGLISH);
		menuBtnEn.setEnabled(!isEnglish);
		menuBtnFr.setEnabled(isEnglish);
	}

	public void addButtonListeners() {
		menuBtnExport.addActionListener(listener);
		menuBtnExport.setActionCommand("export");
		menuBtnImport.addActionListener(listener);
		menuBtnImport.setActionCommand("import");
		menuBtnEn.addActionListener(listener);
		menuBtnEn.setActionCommand("english");
		menuBtnFr.addActionListener(listener);
		menuBtnFr.setActionCommand("french");
		menuBtnReadMe.addActionListener(ignored -> {
			displayReadMe();
		});
		menuBtnLogout.setActionCommand("logout");
		menuBtnLogout.addActionListener(listener);
	}

	public void displayReadMe() {
		try {
			Desktop.getDesktop().browse(new URI(
					"https://github.com/caileanb773/recipe_manager/blob/main/README.md"));
		} catch (IOException e) {
			logger.error("IOException while opening browser: {}", e.getMessage());
		} catch (URISyntaxException e) {
			logger.error("URISyntaxException while opening browser: {}", e.getMessage());
		}

	}

	@Deprecated
	public void displayReadMeOLD() {
		String readMe = null;

		try (BufferedReader reader = new BufferedReader(new FileReader("README.md"))) {
			String line = null;
			StringBuilder sb = new StringBuilder();

			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}

			readMe = sb.toString();

		} catch (FileNotFoundException e) {
			logger.error("Could not find README.md");
		} catch (IOException e) {
			logger.error("IO Exception");
		}

		JOptionPane.showMessageDialog(frame, readMe);
	}

	public void initiRecipeScreenButtons() {
		recipeScreen.initRemoveButton();
		recipeScreen.initAddButton();
		recipeScreen.initEditButton();
		recipeScreen.initFilter();
	}

	public void initLoginScreenButtons() {
		loginScreen.initializeButtons();
	}

	public void initRegisterScreenButtons() {
		registerScreen.initializeButtons();
	}

	public void toggleLangButton(Locale lang) {
		if (lang.equals(Locale.ENGLISH)) {
			menuBtnEn.setEnabled(false);
			menuBtnFr.setEnabled(true);
		} else if (lang.equals(Locale.FRENCH)) {
			menuBtnEn.setEnabled(true);
			menuBtnFr.setEnabled(false);
		}
	}

	public void registerController(ActionListener listener) {
		this.listener = listener;
	}

	public void registerControllerInSubscreens(ActionListener listener) {
		loginScreen.registerController(listener);
		registerScreen.registerController(listener);
		recipeScreen.registerController(listener);
		notificationScreen.registerController(listener);
	}

	public void initCloseBtn() {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				config.saveConfig();

				if (autoBackup.isSelected()) {
					listener.actionPerformed(new ActionEvent(this,
							ActionEvent.ACTION_PERFORMED,
							"closeWithBackup"));
				}

				frame.dispose();
			}
		});
	}

	public void updateBundle() {
		bundle = config.getResourceBundle();
	}

	public void refreshTranslatableText() {
		menuFile.setText(bundle.getString("menuFile"));
		menuOpt.setText(bundle.getString("menuOpt"));
		menuLang.setText(bundle.getString("menuLang"));
		menuAccount.setText(bundle.getString("menuAccount"));
		menuBtnExport.setText(bundle.getString("menuBtnExport"));
		menuBtnImport.setText(bundle.getString("menuBtnImport"));
		menuBtnEn.setText(bundle.getString("menuBtnEn"));
		menuBtnFr.setText(bundle.getString("menuBtnFr"));
		menuBtnLogout.setText(bundle.getString("menuBtnLogout"));
		autoBackup.setText(" " + bundle.getString("autoBackup")); // space for spacing
		menuTheme.setText(bundle.getString("theme"));
		themeLight.setText(bundle.getString("theme.Light"));
		themeDark.setText(bundle.getString("theme.Dark"));
	}

	public void switchScreen(String screenName) {
		if (!screenName.isEmpty()) {
			switch (screenName) {
			case "LOGIN":
				logger.info("Switching to Login screen");
				cardLayout.show(container, "LOGIN");
				setEnabledButtons(screenName);
				loginScreen.grabFocus("EMAIL_FIELD");
				break;
			case "RECIPE_SCREEN":
				logger.info("Switching to UI");
				cardLayout.show(container, "RECIPE_SCREEN");
				setEnabledButtons(screenName);
				recipeScreen.initFocus();
				break;
			case "REGISTER_SCREEN":
				logger.info("Switching to register screen");
				cardLayout.show(container, "REGISTER_SCREEN");
				setEnabledButtons(screenName);
				registerScreen.initFocus();
				loginScreen.clearPwField();
				break;
			case "NOTIFICATIONS":
				logger.info("Switching to noficiations screen");
				cardLayout.show(container, "NOTIFICATION_SCREEN");
				setEnabledButtons(screenName);
				break;
			default:
				logger.error("Unknown screen type passed to switchScreen().");
				break;
			}
			container.revalidate();
			container.repaint();
		} else {
			logger.warn("Empty screen ID string passed to switchScreen().");
		}
	}

	public void setEnabledButtons(String visibleScreen) {
		switch (visibleScreen) {
		case "LOGIN":
		case "REGISTER_SCREEN":
			menuBtnExport.setEnabled(false);
			menuBtnImport.setEnabled(false);
			menuBtnLogout.setEnabled(false);
			break;
		case "RECIPE_SCREEN":
			menuBtnExport.setEnabled(true);
			menuBtnImport.setEnabled(true);
			menuBtnLogout.setEnabled(true);
			break;
		case "NOTIFICATION_SCREEN":
			menuBtnExport.setEnabled(false);
			menuBtnImport.setEnabled(false);
			menuBtnLogout.setEnabled(true);
			break;
		default:
			break;
		}
	}

	public static boolean isBackingUp() {
		return autoBackup.isSelected();
	}

	public void packFrame() {
		frame.pack();
	}

	public CardLayout getCardLayout() {
		return cardLayout;
	}

	public ResourceBundle getBundle() {
		//return bundle;
		return config.getResourceBundle();
	}

	public Config getConfig() {
		return config;
	}

	public LoginScreen getLoginScreen() {
		return loginScreen;
	}

	public RegisterScreen getRegisterScreen() {
		return registerScreen;
	}

	public RecipeScreen getRecipeScreen() {
		return recipeScreen;
	}
	
	public NotificationScreen getNotificationScreen() {
		return notificationScreen;
	}

}
