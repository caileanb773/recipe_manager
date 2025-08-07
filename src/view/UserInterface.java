package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import definitions.Constants;
import definitions.Recipe;
import util.Config;

/*
 * Author: Cailean Bernard
 * Contents: Contains the default screen that the user sees upon running the
 * program. The left side of the screen has a list of all recipes loaded as
 * represented by buttons. The right side of the screen contains the recipe
 * ingredients and directions that are displayed when a recipe is clicked.
 */

@SuppressWarnings("serial")
public class UserInterface extends JPanel {

	// Recipe selection list (UI left side)
	private JPanel rcpSelectPanel;
	private JPanel rcpSelectListPanel;
	private JLabel rcpSelectLabel;
	private JPanel rcpEditPanel;
	private List<RecipeSelectButton> rcpSelectList;
	private JButton rcpListAdd;
	private JButton rcpListRemove;
	private JButton rcpListEdit;
	private JScrollPane rcpSelectScrollPane;
	private Recipe activeRecipe;

	// Selected (active) Recipe information (UI right side)
	private JPanel selectedRcpDescPanel;
	private JPanel selectedRcpInfo;
	private JLabel selectedDescLabel;
	private JTextArea selectedRcpTxt;
	private JScrollPane selectedRcpTxtScrollPane;


	public UserInterface() {
		rcpSelectList = new ArrayList<RecipeSelectButton>();
		setLayout(new BorderLayout());

		// Recipe Selection List Panel (WEST)
		rcpSelectPanel = new JPanel(new BorderLayout());
		rcpSelectPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		rcpSelectListPanel = new JPanel();
		rcpSelectListPanel.setBackground(Color.lightGray);
		rcpSelectListPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.darkGray, 1),
				BorderFactory.createLineBorder(Color.black, 1)));
		rcpSelectScrollPane = new JScrollPane(rcpSelectListPanel);
		rcpSelectScrollPane.setPreferredSize(new Dimension(
				Constants.BUTTON_WIDTH,
				Constants.BUTTON_HEIGHT * Constants.NUM_SHOWN_BUTTONS));
		rcpEditPanel = new JPanel();
		rcpSelectLabel = new JLabel("Recipes", JLabel.CENTER);
		rcpSelectLabel.setFont(Constants.titleFont);
		BoxLayout rcpSelectListLayout = new BoxLayout(rcpSelectListPanel, BoxLayout.Y_AXIS);
		rcpSelectListPanel.setLayout(rcpSelectListLayout);
		rcpListAdd = new JButton("Add");
		rcpListRemove = new JButton("Remove");
		rcpListEdit = new JButton("Edit");
		rcpEditPanel.add(rcpListAdd);
		rcpEditPanel.add(rcpListRemove);
		rcpEditPanel.add(rcpListEdit);
		rcpSelectPanel.add(rcpSelectLabel, BorderLayout.NORTH);
		rcpSelectPanel.add(rcpSelectScrollPane, BorderLayout.CENTER);
		rcpSelectPanel.add(rcpEditPanel, BorderLayout.SOUTH);

		// Selected Recipe Information Panel (CENTER)
		selectedRcpDescPanel = new JPanel();
		BoxLayout recipeDescLayout = new BoxLayout(selectedRcpDescPanel, BoxLayout.Y_AXIS);
		selectedRcpDescPanel.setLayout(recipeDescLayout);
		selectedRcpInfo = new JPanel();
		selectedRcpTxt = new JTextArea();
		selectedRcpTxt.setEditable(false);
		selectedRcpTxt.setCaretColor(new Color(0,0,0,0));
		selectedRcpTxt.setCaretPosition(0);
		selectedRcpTxt.setWrapStyleWord(true);
		selectedRcpTxt.setLineWrap(true);
		selectedRcpTxt.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		selectedDescLabel = new JLabel("Recipe Information");
		selectedDescLabel.setAlignmentX(CENTER_ALIGNMENT);
		selectedDescLabel.setFont(Constants.titleFont);
		selectedRcpTxtScrollPane = new JScrollPane(selectedRcpTxt);
		selectedRcpTxtScrollPane.setPreferredSize(new Dimension(300,300));
		selectedRcpTxtScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		selectedRcpTxtScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		selectedRcpInfo.add(selectedRcpTxtScrollPane);
		selectedRcpDescPanel.add(selectedDescLabel);
		selectedRcpDescPanel.add(selectedRcpInfo);

		// Separation of this code from the code that is previous
		add(selectedRcpDescPanel, BorderLayout.CENTER);
		add(rcpSelectPanel, BorderLayout.WEST);
	}

	public void populateRecipeSelectList(List<Recipe> recipes) {
		if (recipes == null || rcpSelectList == null) {
			System.err.println("Recipe list in model or view was not properly initialized: populateRecipeList().");
			return;
		}
		
		rcpSelectList.clear();

		for (Recipe rcp : recipes) {
			RecipeSelectButton newRcpButton = new RecipeSelectButton(rcp.getTitle());
			rcpSelectList.add(newRcpButton);
			newRcpButton.setAlignmentX(CENTER_ALIGNMENT);
			newRcpButton.addActionListener(e -> {
				setActiveRecipe(rcp);
			});
		}

	}

	public void displayRecipeButtons() {
		if (rcpSelectList == null) {
			System.err.println("Recipe Select List was not initialized before display.");
			return;
		}
		
		rcpSelectListPanel.removeAll();
		
		for (RecipeSelectButton r : rcpSelectList) {
			rcpSelectListPanel.add(r);
		}

		rcpSelectListPanel.revalidate();
		rcpSelectListPanel.repaint();
	}

	public void switchLanguage(String langCode) {

	}
	
	public void initializeAddButton(ActionListener listener) {
		rcpListAdd.setActionCommand("add");
		rcpListAdd.addActionListener(listener);
	}
	
	public void initializeRemoveButton(ActionListener listener) {
		rcpListRemove.setActionCommand("remove");
		rcpListRemove.addActionListener(e -> {
			if (activeRecipe == null) {
				System.out.println("Aborting remove recipe dialog: no active recipe.");
				JOptionPane.showMessageDialog(null,
						"Select a recipe you wish to remove, then click \"remove\".",
						"No Recipe Selected",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			int choice = JOptionPane.showConfirmDialog(null,
					"Are you sure you want to delete " + activeRecipe.getTitle() + "?",
					"Confirm Choice", JOptionPane.OK_CANCEL_OPTION);
			
			if (choice == JOptionPane.OK_OPTION) {
				listener.actionPerformed(new ActionEvent(
						rcpListRemove, ActionEvent.ACTION_PERFORMED,
						"remove"));
			}
		});
	}
	
	public void initializeEditButton(ActionListener listener) {
		rcpListEdit.setActionCommand("edit");
		rcpListEdit.addActionListener(listener);
	}
	
	public void clearSelectedRecipeText() {
		selectedRcpTxt.setText("");
	}

	public Recipe getActiveRecipe() {
		return activeRecipe;
	}
	
	public void setActiveRecipe(Recipe recipe) {
		activeRecipe = recipe;
		selectedRcpTxt.setText(activeRecipe.formatRecipeForTextDisplay());
	}

}
