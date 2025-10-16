package view;

import java.awt.CardLayout;
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

import controller.Main;
import definitions.Recipe;
import definitions.Theme;
import util.Config;

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
	private JMenuItem menuBtnDe;
	private JMenuItem menuBtnReadMe;
	private JMenuItem menuBtnLogout;
	private static JCheckBox autoBackup;
	private JRadioButtonMenuItem themeLight;
	private JRadioButtonMenuItem themeDark;

	// Other
	private ActionListener listener;


	public AppFrame() {
		frame = new JFrame();
		cardLayout = new CardLayout();
		frame.getContentPane().setLayout(cardLayout);
		frame.setTitle("Macromise Recipe Manager");		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// ---------------------------------------------------------------------
		// C O M P O N E N T S
		// ---------------------------------------------------------------------
		config = new Config();
		bundle = config.getResourceBundle();
		recipeScreen = new RecipeScreen(bundle);
		loginScreen = new LoginScreen(bundle);
		registerScreen = new RegisterScreen(bundle);
		container = frame.getContentPane();

		// Icon
		try {
			URL iconUrl = Main.class.getClassLoader().getResource("img/icon.png");		
			ImageIcon icon = new ImageIcon(iconUrl);
			frame.setIconImage(icon.getImage());
		} catch (NullPointerException e) {
			System.err.println("Could not find icon.png");
		}

		// ----- Adding Screens to Frame -----
		container.add(loginScreen, "LOGIN");
		container.add(recipeScreen, "RECIPE_SCREEN");
		container.add(registerScreen, "REGISTER_SCREEN");

		// ---------------------------------------------------------------------
		// M E N U  B A R
		// ---------------------------------------------------------------------
		menuBar = new JMenuBar();
		menuFile = new JMenu(bundle.getString("menuFile"));
		menuBtnExport = new JMenuItem(bundle.getString("menuBtnExport"));
		menuBtnImport = new JMenuItem(bundle.getString("menuBtnImport"));
		menuFile.add(menuBtnImport);
		menuFile.add(menuBtnExport);
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
		menuBtnDe = new JMenuItem(bundle.getString("menuBtnDe"));
		menuLang.add(menuBtnEn);
		menuLang.add(menuBtnFr);
		menuLang.add(menuBtnDe);
		menuBar.add(menuFile);
		menuBar.add(menuOpt);
		menuBar.add(menuAccount);
		frame.setJMenuBar(menuBar);
		autoBackup = new JCheckBox(bundle.getString("autoBackup"));
		autoBackup.setSelected(config.isAutoBackup());
		menuOpt.add(autoBackup);
		JMenu menuTheme = new JMenu("Themes");
		ButtonGroup themeGroup = new ButtonGroup();
		themeLight = new JRadioButtonMenuItem("Light", true);
		themeDark = new JRadioButtonMenuItem("Dark");
		themeGroup.add(themeLight);
		themeGroup.add(themeDark);
		menuOpt.add(menuTheme);
		menuTheme.add(themeLight);
		menuTheme.add(themeDark);

		// Theme Selection Setup
		themeLight.addActionListener(e -> fireThemeChangeEvent(Theme.LIGHT));
		themeDark.addActionListener(e -> fireThemeChangeEvent(Theme.DARK));

		// Final JFrame Settings
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Set Appropriate Buttons to be Selected
		updateLanguageButtons();	
		setEnabledButtons("LOGIN");

		// Set Focus if Login Screen remembers an email
		if (LoginScreen.isRemembering()) {
			loginScreen.grabFocus("PASSWORD_FIELD");
		} else {
			loginScreen.grabFocus("LOGIN_FIELD");
		}
	}
	
	public void fireThemeChangeEvent(Theme theme) {
		if (theme.toString().isEmpty()) {
			System.err.println("No theme passed to fireThemeChangeEvent().");
			return;
		}
		
		if (!(theme == Theme.LIGHT || theme == Theme.DARK)) {
			System.err.println("Unrecognized theme passed to fireThemeChangeEvent().");
			return;
		}
		
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
	}

	public void applyTheme(Theme theme) {
		try {
			switch (theme) {
			case LIGHT:
				UIManager.setLookAndFeel(new FlatLightLaf());
				break;
			case DARK:
				UIManager.setLookAndFeel(new FlatDarkLaf());
				break;
			default:
				return;
			}
			
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println("Exception caught while switching theme: "
					+ e.getMessage());
		} catch (Exception e) {
			System.err.println("Exception caught while switching theme: "
					+ e.getMessage());
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
		menuBtnDe.addActionListener(listener);
		menuBtnDe.setActionCommand("german");
		menuBtnReadMe.addActionListener(e -> {
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
			System.err.println("IOException while opening browser: "
					+ e.getMessage());
		} catch (URISyntaxException e) {
			System.err.println("URISyntaxException while opening browser: "
					+ e.getMessage());
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
			System.err.println("Could not find README.md");
		} catch (IOException e) {
			System.err.println("IO Exception");
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
			menuBtnDe.setEnabled(true);
		} else if (lang.equals(Locale.FRENCH)) {
			menuBtnEn.setEnabled(true);
			menuBtnFr.setEnabled(false);
			menuBtnDe.setEnabled(true);
		} else if (lang.equals(Locale.GERMAN)) {
			menuBtnEn.setEnabled(true);
			menuBtnFr.setEnabled(true);
			menuBtnDe.setEnabled(false);
		}
	}

	public void registerController(ActionListener listener) {
		this.listener = listener;
	}

	public void registerControllerInSubscreens(ActionListener listener) {
		loginScreen.registerController(listener);
		registerScreen.registerController(listener);
		recipeScreen.registerController(listener);
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
	}

	public void switchScreen(String screenName) {
		if (!screenName.isEmpty()) {
			switch (screenName) {
			case "LOGIN":
				System.out.println("Switching to Login screen");
				cardLayout.show(container, "LOGIN");
				setEnabledButtons(screenName);
				loginScreen.grabFocus("EMAIL_FIELD");
				break;
			case "RECIPE_SCREEN":
				System.out.println("Switching to UI");
				cardLayout.show(container, "RECIPE_SCREEN");
				setEnabledButtons(screenName);
				recipeScreen.initFocus();
				break;
			case "REGISTER_SCREEN":
				System.out.println("Switching to register screen");
				cardLayout.show(container, "REGISTER_SCREEN");
				setEnabledButtons(screenName);
				registerScreen.initFocus();
				break;
			default:
				System.err.println("Unknown screen type passed to switchScreen().");
				break;
			}
			container.revalidate();
			container.repaint();
		} else {
			System.err.println("Empty screen ID string passed to switchScreen().");
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
		return bundle;
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

}
