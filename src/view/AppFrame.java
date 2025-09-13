package view;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import controller.Main;
import definitions.Recipe;
import util.Config;

/*
 * Author: Cailean Bernard
 * Contents: The frame of the app which contains the user interface, the part of
 * the screen that the user interacts with.
 */

public class AppFrame {

	// Displayed screens & layout
	private JFrame frame;
	private UserInterfaceScreen userInterface;
	private LoginScreen loginScreen;
	private RegisterScreen registerScreen;

	private Config config;
	private ResourceBundle bundle;
	private CardLayout cardLayout;
	private Container container;
	//private User activeUser;

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


	public AppFrame() {
		frame = new JFrame();
		cardLayout = new CardLayout();
		frame.getContentPane().setLayout(cardLayout);
		frame.setTitle("Macromise Recipe Manager");		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		config = new Config();
		bundle = config.getResourceBundle();
		userInterface = new UserInterfaceScreen(bundle);
		loginScreen = new LoginScreen(bundle);
		registerScreen = new RegisterScreen(bundle);
		container = frame.getContentPane();

		container.add(loginScreen, "LOGIN");
		container.add(userInterface, "USER_INTERFACE");
		container.add(registerScreen, "REGISTER_SCREEN");

		try {
			URL iconUrl = Main.class.getClassLoader().getResource("icon.png");		
			ImageIcon icon = new ImageIcon(iconUrl);
			frame.setIconImage(icon.getImage());
		} catch (NullPointerException e) {
			System.err.println("Could not find icon.png");
		}

		// Menu bar
		menuBar = new JMenuBar();
		menuFile = new JMenu(bundle.getString("menuFile"));
		menuBtnExport = new JMenuItem(bundle.getString("menuBtnExport"));
		menuBtnImport = new JMenuItem(bundle.getString("menuBtnImport"));
		menuFile.add(menuBtnExport);
		menuFile.add(menuBtnImport);
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
		updateLanguageButtons();	
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		setEnabledButtons("LOGIN");

		if (LoginScreen.isRemembering()) {
			loginScreen.initFocus("PASSWORD_FIELD");
		} else {
			loginScreen.initFocus("LOGIN_FIELD");
		}
	}

	public void initializeMainScreen(List<Recipe> recipeList) {
		userInterface.populateRecipeSelectList(recipeList);
		userInterface.displayRecipeButtons(); 
	}

	public void updateLanguageButtons() {
		boolean isEnglish = config.getLocale().equals(Locale.ENGLISH);
		menuBtnEn.setEnabled(!isEnglish);
		menuBtnFr.setEnabled(isEnglish);
	}

	public UserInterfaceScreen getUserInterface() {
		return userInterface;
	}

	public void addButtonListeners(ActionListener listener) {
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

	// TODO needs to be modified for additional languages
	public void displayReadMe() {
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

	public void initializeUIButtons(ActionListener listener) {
		userInterface.initializeRemoveButton(listener);
		userInterface.initializeAddButton(listener);
		userInterface.initializeEditButton(listener);
		userInterface.initializeFilter(listener);
	}

	public void initializeLoginButtons(ActionListener listener) {
		loginScreen.initializeButtons(listener);
	}
	
	public void initializeRegisterButtons(ActionListener listener) {
		registerScreen.initializeButtons(listener);
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

	public void initCloseBtn(ActionListener listener) {
		// save settings in config on close
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				config.saveConfig();
				listener.actionPerformed(new ActionEvent(this,
						ActionEvent.ACTION_PERFORMED,
						"closeWindow"));
				frame.dispose();
			}
		});
	}

	public Config getConfig() {
		return config;
	}

	public void updateBundle() {
		bundle = config.getResourceBundle();
	}

	public ResourceBundle getBundle() {
		return bundle;
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

	public CardLayout getCardLayout() {
		return cardLayout;
	}

	public void switchScreen(String screenName) {
		if (!screenName.isEmpty()) {
			switch (screenName) {
			case "LOGIN":
				System.out.println("Switching to Login screen");
				cardLayout.show(container, "LOGIN");
				setEnabledButtons(screenName);
				loginScreen.initFocus("EMAIL_FIELD");
				break;
			case "USER_INTERFACE":
				System.out.println("Switching to UI");
				cardLayout.show(container, "USER_INTERFACE");
				setEnabledButtons(screenName);
				userInterface.initFocus();
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

	public LoginScreen getLoginScreen() {
		return loginScreen;
	}
	
	public RegisterScreen getRegisterScreen() {
		return registerScreen;
	}

	public void packFrame() {
		frame.pack();
	}

	public void setEnabledButtons(String visibleScreen) {
		switch (visibleScreen) {
		case "LOGIN":
		case "REGISTER_SCREEN":
			menuBtnExport.setEnabled(false);
			menuBtnImport.setEnabled(false);
			menuBtnLogout.setEnabled(false);
			break;
		case "USER_INTERFACE":
			menuBtnExport.setEnabled(true);
			menuBtnImport.setEnabled(true);
			menuBtnLogout.setEnabled(true);
			break;
		default:
			break;
		}
	}

}
