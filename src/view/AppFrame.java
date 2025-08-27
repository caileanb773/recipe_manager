package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

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
	private JMenuItem menuBtnExport;
	private JMenuItem menuBtnImport;
	private JMenuItem menuBtnEn;
	private JMenuItem menuBtnFr;
	private JMenuItem menuBtnDe;
	private JMenuItem menuBtnReadMe;
	
	
	public AppFrame() {
		frame = new JFrame();
		frame.setTitle("MacroMEP Recipe Manager");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		config = new Config();
		bundle = config.getResourceBundle();
		userInterface = new UserInterface(bundle);
		
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
				System.out.println("Saving settings...");
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
		menuBtnExport.setText(bundle.getString("menuBtnExport"));
		menuBtnImport.setText(bundle.getString("menuBtnImport"));
		menuBtnEn.setText(bundle.getString("menuBtnEn"));
		menuBtnFr.setText(bundle.getString("menuBtnFr"));
	}
	
	public void packFrame() {
		frame.pack();
	}
	
}
