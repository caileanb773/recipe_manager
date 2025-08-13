package view;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import definitions.Recipe;
import util.Config;

/*
 * Author: Cailean Bernard
 * Contents: The frame of the app which contains the user interface, the part of
 * the screen that the user interacts with.
 */

public class AppFrame {
	
	private JFrame frame;
	private UserInterface userInterface;
	private Config config;
	private ResourceBundle bundle;
	
	// Menu bar
	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenu menuOpt;
	private JMenu menuLang;
	private JMenuItem menuBtnSave;
	private JMenuItem menuBtnLoad;
	private JMenuItem menuBtnEn;
	private JMenuItem menuBtnFr;
	private JMenuItem menuBtnDe;
	
	
	public AppFrame() {
		frame = new JFrame();
		frame.setTitle("Misene Recipe Manager");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		config = new Config();
		bundle = config.getResourceBundle();
		userInterface = new UserInterface(bundle);
		
		// Menu bar
		menuBar = new JMenuBar();
		menuFile = new JMenu(bundle.getString("menuFile"));
		menuBtnSave = new JMenuItem(bundle.getString("menuBtnSave"));
		menuBtnLoad = new JMenuItem(bundle.getString("menuBtnLoad"));
		menuFile.add(menuBtnSave);
		menuFile.add(menuBtnLoad);
		menuOpt = new JMenu(bundle.getString("menuOpt"));
		menuLang = new JMenu(bundle.getString("menuLang"));
		menuOpt.add(menuLang);
		menuBtnEn = new JMenuItem(bundle.getString("menuBtnEn"));
		menuBtnFr = new JMenuItem(bundle.getString("menuBtnFr"));
		menuBtnDe = new JMenuItem(bundle.getString("menuBtnDe"));
		
		menuLang.add(menuBtnEn);
		menuLang.add(menuBtnFr);
		menuLang.add(menuBtnDe);
		menuBar.add(menuFile);
		menuBar.add(menuOpt);
		frame.setJMenuBar(menuBar);
		
		updateLanguageButtons();
		
		// save settings in config on close
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Saving settings...");
				config.saveConfig();
				frame.dispose();
			}
		});
		
		frame.add(userInterface);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
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

	public UserInterface getUserInterface() {
		return userInterface;
	}
	
	public void addButtonListeners(ActionListener listener) {
		menuBtnSave.addActionListener(listener);
		menuBtnSave.setActionCommand("save");
		menuBtnEn.addActionListener(listener);
		menuBtnEn.setActionCommand("english");
		menuBtnFr.addActionListener(listener);
		menuBtnFr.setActionCommand("french");
		menuBtnDe.addActionListener(listener);
		menuBtnDe.setActionCommand("german");
	}
	
	public void initializeUIButtons(ActionListener listener) {
		userInterface.initializeRemoveButton(listener);
		userInterface.initializeAddButton(listener);
		userInterface.initializeEditButton(listener);
		userInterface.initializeFilter(listener);
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
		menuBtnSave.setText(bundle.getString("menuBtnSave"));
		menuBtnLoad.setText(bundle.getString("menuBtnLoad"));
		menuBtnEn.setText(bundle.getString("menuBtnEn"));
		menuBtnFr.setText(bundle.getString("menuBtnFr"));
	}
	
	public void packFrame() {
		frame.pack();
	}
	
}
