package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.StringJoiner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import definitions.Constants;
import definitions.Ingredient;
import definitions.Recipe;
import definitions.Unit;

/*
 * Author: Cailean Bernard
 * Contents: The dialog that facilitates creation of a new recipe. The field
 * "createdRecipe" is null until a valid recipe is created using the dialog and
 * the "confirm" button is clicked. If the recipe is in the correct format, then
 * the recipe can be fetched using its getter from outside of the dialog before
 * the dialog is disposed.
 */

@SuppressWarnings("serial")
public class AddRecipeDialog extends JDialog {

	// Swing components
	private JLabel recipeTitle;
	private JLabel recipeIngredients;
	private JLabel recipeDirections;
	private JButton btnConfirm;
	private JButton btnCancel;
	private JButton btnHelp;
	private JTextField inputTitle;
	private JTextArea inputIngredients;
	private JTextArea inputDirections;
	private JPanel dialogPanel;
	private JPanel btnPanel;
	private JLabel recipeTags;
	private JTextField inputTags;
	private JScrollPane ingredientsScrollPane;
	private JScrollPane directionsScrollPane;

	// Other / Constants
	private static final int TXT_ROWS = 10;
	private static final int TXT_COLS = 40;
	private Recipe createdRecipe;
	private ResourceBundle bundle;
	private int tempRecipeId;


	public AddRecipeDialog(ActionListener listener,
			int mode,
			Recipe recipe,
			ResourceBundle bundle) {
		super(null, "Add a New Recipe", JDialog.DEFAULT_MODALITY_TYPE);
		createdRecipe = recipe;
		this.bundle = bundle;
		recipeTitle = new JLabel(bundle.getString("recipeTitle"));
		recipeIngredients = new JLabel(bundle.getString("recipeIngredients"));
		recipeDirections = new JLabel(bundle.getString("recipeDirections"));
		recipeTags = new JLabel(bundle.getString("recipeTags"));
		btnConfirm = new JButton(bundle.getString("btnConfirm"));
		btnCancel = new JButton(bundle.getString("btnCancel"));
		btnHelp = new JButton(bundle.getString("btnHelp"));
		inputTitle = new JTextField(); 
		inputIngredients = new JTextArea(TXT_ROWS,TXT_COLS);
		inputDirections = new JTextArea(TXT_ROWS,TXT_COLS);
		inputTags = new JTextField();
		btnPanel = new JPanel();
		dialogPanel = new JPanel();
		recipeTitle.setAlignmentX(CENTER_ALIGNMENT);
		recipeIngredients.setAlignmentX(CENTER_ALIGNMENT);
		recipeDirections.setAlignmentX(CENTER_ALIGNMENT);
		recipeTags.setAlignmentX(CENTER_ALIGNMENT);
		
		recipeTitle.putClientProperty("FlatLaf.styleClass", "h3");
		recipeIngredients.putClientProperty("FlatLaf.styleClass", "h3");
		recipeDirections.putClientProperty("FlatLaf.styleClass", "h3");
		recipeTags.putClientProperty("FlatLaf.styleClass", "h3");
		
		inputIngredients.setLineWrap(true);
		inputIngredients.setWrapStyleWord(true);
		inputDirections.setLineWrap(true);
		inputDirections.setWrapStyleWord(true);
		BoxLayout layout = new BoxLayout(dialogPanel, BoxLayout.Y_AXIS);		
		ingredientsScrollPane = new JScrollPane(inputIngredients);
		ingredientsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		ingredientsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		directionsScrollPane = new JScrollPane(inputDirections);
		directionsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		directionsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);		
		dialogPanel.setLayout(layout);
		dialogPanel.add(recipeTitle);
		dialogPanel.add(inputTitle);
		dialogPanel.add(recipeIngredients);
		dialogPanel.add(ingredientsScrollPane);
		dialogPanel.add(recipeDirections);
		dialogPanel.add(directionsScrollPane);
		dialogPanel.add(recipeTags);
		dialogPanel.add(inputTags);
		Border blackLineBorder = BorderFactory.createLineBorder(Color.black, 1);
		dialogPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		btnPanel.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
		inputTitle.setBorder(blackLineBorder);
		inputIngredients.setBorder(blackLineBorder);
		inputDirections.setBorder(blackLineBorder);
		inputTags.setBorder(blackLineBorder);
		btnPanel.add(btnConfirm);
		btnPanel.add(btnCancel);
		btnPanel.add(btnHelp);
		dialogPanel.add(btnPanel);
		add(dialogPanel);

		addConfirmListener(listener, mode);
		btnCancel.addActionListener(e -> cancelRecipe());
		btnHelp.addActionListener(e -> displayHelp());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancelRecipe();
			}
		});

		if (mode == Constants.EDIT_MODE) {
			initDialogForEdit();
		}

		pack();
		setLocationRelativeTo(null);
		btnConfirm.requestFocusInWindow();
	}

	public void confirmRecipe() {
		Recipe newRecipe = getRecipeFromFields();

		if (newRecipe != null) {
			createdRecipe = newRecipe;
		}
		this.dispose();
	}

	public void addConfirmListener(ActionListener listener, int mode) {
		if (mode == Constants.ADD_MODE) {
			btnConfirm.setActionCommand("confirmAdd");
		} else if (mode == Constants.EDIT_MODE) {
			btnConfirm.setActionCommand("confirmEdit");
		}

		btnConfirm.addActionListener(e -> {
			System.out.println("Checking validity of recipe fields...");
			Recipe newRecipe = getRecipeFromFields();
			if (newRecipe != null) {
				createdRecipe = newRecipe;
			} else {
				System.out.println("getRecipeFromFields() failed.");
				createdRecipe = null;
			}

			if (mode == Constants.ADD_MODE) {
				listener.actionPerformed(new ActionEvent(
						btnConfirm, ActionEvent.ACTION_PERFORMED,
						"confirmAdd"));
			} else if (mode == Constants.EDIT_MODE) {
				listener.actionPerformed(new ActionEvent(
						btnConfirm, ActionEvent.ACTION_PERFORMED,
						"confirmEdit"));
			}

		});
	}

	public void cancelRecipe() {
		createdRecipe = null;
		this.dispose();
	}

	public void displayHelp() {
		String helpString = bundle.getString("displayHelp");
		JOptionPane.showMessageDialog(this,
				helpString,
				bundle.getString("displayHelpTitle"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	public Recipe getRecipeFromFields() {
		String title = inputTitle.getText().trim();
		String ingredientsStr = inputIngredients.getText().trim();
		List<Ingredient> ingredientsList = new ArrayList<>();
		String directions = inputDirections.getText().trim();
		String tagsStr = inputTags.getText().trim();
		String[] tags = tagsStr.split(", ");

		try (Scanner scan = new Scanner(ingredientsStr)) {
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] lineParts = line.split("\\s+");
				int linePartsLen = lineParts.length;

				String amount = lineParts[Constants.AMT_IDX].trim();
				Unit unit = Unit.valueOf(lineParts[Constants.UNIT_IDX].toUpperCase().trim());
				String name = null;

				// If the ingredient name is longer than one word, concatenate it
				if (linePartsLen == Constants.DEFAULT_LENGTH) {
					name = lineParts[Constants.NAME_IDX].trim();
				} else {
					StringJoiner sj = new StringJoiner(" ");
					for (int i = Constants.NAME_IDX; i < linePartsLen; i++) {
						sj.add(lineParts[i]);
					}
					name = sj.toString();
				}

				ingredientsList.add(new Ingredient(amount, unit, name));
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					bundle.getString("errIncorrectIngAmt"),
					bundle.getString("errIncorrectIngAmtTitle")
					, JOptionPane.ERROR_MESSAGE);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this,
					bundle.getString("errUnknownUnit"),
					bundle.getString("errUnknownUnitTitle"),
					JOptionPane.ERROR_MESSAGE);
		}

		// recipes can have no tags if the user doesn't care to add any, so no check
		if (title.isEmpty()
				|| directions.isEmpty()
				|| ingredientsList.size() < 1) {
			JOptionPane.showMessageDialog(this,
					bundle.getString("errMissingFields"),
					bundle.getString("errMissingFieldsTitle"),
					JOptionPane.WARNING_MESSAGE);
			return null;
		}

		Recipe newRecipe = null;

		if (tagsStr.isEmpty()) {
			newRecipe = new Recipe(title, ingredientsList, directions);
		} else {
			newRecipe = new Recipe(title, ingredientsList, directions, tags);
		}

		return newRecipe;
	}

	public void initDialogForEdit() {
		tempRecipeId = createdRecipe.getId();
		inputTitle.setText(createdRecipe.getTitle());
		inputIngredients.setText(createdRecipe.stringifyIngredients());
		inputDirections.setText(createdRecipe.getDirections());
		inputTags.setText(createdRecipe.stringifyTags());
	}

	public Recipe getCreatedRecipe() {
		if (createdRecipe == null) {
			System.err.println("getCreatedRecipe() returned null.");
			return null;
		} else {
			createdRecipe.setId(tempRecipeId);
			tempRecipeId = -1;
			return createdRecipe;
		}
	}

	public void setCreatedRecipeToNull() {
		createdRecipe = null;
	}

	public void setCreatedRecipe(Recipe recipe) {
		createdRecipe = recipe;
	}

}
