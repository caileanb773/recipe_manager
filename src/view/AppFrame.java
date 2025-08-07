package view;

import java.awt.event.ActionListener;
import java.util.List;

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
	
	// Menu bar
	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenu menuOpt;
	private JMenu menuLang;
	private JMenuItem menuBtnSave;
	private JMenuItem menuBtnLoad;
	private JMenuItem menuBtnEn;
	private JMenuItem menuBtnFr;
	
	
	public AppFrame() {
		frame = new JFrame();
		userInterface = new UserInterface();
		frame.setTitle("Recipe Book");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		config = new Config();
		
		// Menu bar
		menuBar = new JMenuBar();
		menuFile = new JMenu("File");
		menuBtnSave = new JMenuItem("Save Recipe List");
		menuBtnLoad = new JMenuItem("Load Recipe List");
		menuFile.add(menuBtnSave);
		menuFile.add(menuBtnLoad);
		menuOpt = new JMenu("Options");
		menuLang = new JMenu("Language");
		menuOpt.add(menuLang);
		menuBtnEn = new JMenuItem("English");
		menuBtnFr = new JMenuItem("French");
		menuLang.add(menuBtnEn);
		menuLang.add(menuBtnFr);
		menuBar.add(menuFile);
		menuBar.add(menuOpt);
		frame.setJMenuBar(menuBar);
		
		frame.add(userInterface);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void initializeMainScreen(List<Recipe> recipeList) {
		userInterface.populateRecipeSelectList(recipeList);
		userInterface.displayRecipeButtons(); 
	}

	public UserInterface getUserInterface() {
		return userInterface;
	}
	
	public void addButtonListeners(ActionListener listener) {
		menuBtnSave.addActionListener(listener);
		menuBtnSave.setActionCommand("save");
	}
	
	public void initializeUIButtons(ActionListener listener) {
		userInterface.initializeRemoveButton(listener);
		userInterface.initializeAddButton(listener);
		userInterface.initializeEditButton(listener);
	}
	
	public void toggleLangButton(String lang) {
		if (lang.equals("en")) {
			menuBtnEn.setEnabled(true);
			menuBtnFr.setEnabled(false);
		} else if (lang.equals("fr")) {
			menuBtnEn.setEnabled(false);
			menuBtnFr.setEnabled(true);
		}
	}
}
