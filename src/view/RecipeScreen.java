package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import definitions.Constants;
import definitions.Ingredient;
import definitions.Recipe;

/*
 * Author: Cailean Bernard
 * Contents: Contains the default screen that the user sees upon running the
 * program. The left side of the screen has a list of all recipes loaded as
 * represented by buttons. The right side of the screen contains the recipe
 * ingredients and directions that are displayed when a recipe is clicked.
 */

@SuppressWarnings("serial")
public class RecipeScreen extends JPanel {

	// Recipe selection list (UI left side)
	private JPanel rcpSelectPanel;
	private JPanel rcpSelectListPanel;
	private JLabel rcpSelectLabel;
	private JPanel rcpEditPanel;
	private JPanel filterLabelCombo;
	private JPanel filterInputPanel;
	private JLabel filterLabel;
	private JTextField filterInput;
	private JButton filterApply;
	private JButton filterClear;
	private List<RecipeSelectButton> rcpSelectList;
	private JButton rcpListAdd;
	private JButton rcpListRemove;
	private JButton rcpListEdit;
	private JScrollPane rcpSelectScrollPane;
	private JLabel scaleRcpLabel;
	private JSpinner scaleRcpSpinner;
	private JPanel scaleRcpPanel;

	// Selected (active) Recipe information (UI right side)
	private JPanel selectedRcpDescPanel;
	private JPanel selectedRcpInfo;
	private JLabel selectedDescLabel;
	private JTextArea selectedRcpTxt;
	private JScrollPane selectedRcpTxtScrollPane;

	// Other
	private ResourceBundle bundle;
	private Recipe activeRecipe;
	private ActionListener listener;
	private int scaleVal;
	
	// Constant
	private final int UNSCALED = 0;
	private final int SCALED = 1;


	public RecipeScreen(ResourceBundle bundle) {	
		this.bundle = bundle;
		setLayout(new BorderLayout());
		rcpSelectList = new ArrayList<RecipeSelectButton>();
		scaleVal = 1;

		// ---------------------------------------------------------------------
		// T A G S
		// ---------------------------------------------------------------------
		filterLabelCombo = new JPanel(new BorderLayout());
		filterInputPanel = new JPanel(new BorderLayout());
		filterInput = new JTextField(10);
		filterApply = new JButton(bundle.getString("filterApply"));
		filterLabel = new JLabel(bundle.getString("filterLabel"));
		filterClear = new JButton(bundle.getString("filterClear"));
		filterInputPanel.add(filterLabel, BorderLayout.WEST);
		filterInputPanel.add(filterInput, BorderLayout.CENTER);
		filterInputPanel.add(filterApply, BorderLayout.EAST);
		filterInputPanel.add(filterClear, BorderLayout.SOUTH);
		filterLabelCombo.add(filterInputPanel, BorderLayout.SOUTH);

		// ---------------------------------------------------------------------
		// R E C I P E  S E L E C T  S U B S E C T I O N
		// ---------------------------------------------------------------------
		rcpSelectPanel = new JPanel(new BorderLayout());
		rcpSelectPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		rcpSelectListPanel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				int width = 0;
				int height = 0;

				for (Component comp : getComponents()) {
					Dimension d = comp.getPreferredSize();
					width = Math.max(width, d.width);
					height += d.height;
				}

				// padding
				width += 10;

				return new Dimension(width, height);
			}
		};
		rcpSelectListPanel.setLayout(new BoxLayout(rcpSelectListPanel, BoxLayout.Y_AXIS));

		// Ensure buttons expand to fill the available width
		for (RecipeSelectButton btn : rcpSelectList) {
			btn.setAlignmentX(Component.LEFT_ALIGNMENT);
			btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
		}

		// ---------------------------------------------------------------------
		// R E C I P E  S E L E C T  L I S T
		// ---------------------------------------------------------------------
		rcpSelectListPanel.setBackground(Color.lightGray);
		rcpSelectScrollPane = new JScrollPane(rcpSelectListPanel);
		rcpSelectScrollPane.getVerticalScrollBar().setUnitIncrement(Constants.SCROLL_SPEED);
		rcpEditPanel = new JPanel();
		rcpSelectLabel = new JLabel(bundle.getString("rcpSelectLabel"), JLabel.CENTER);
		rcpSelectLabel.setFont(Constants.titleFont);
		BoxLayout rcpSelectListLayout = new BoxLayout(rcpSelectListPanel, BoxLayout.Y_AXIS);
		rcpSelectListPanel.setLayout(rcpSelectListLayout);
		rcpListAdd = new JButton(bundle.getString("rcpListAdd"));
		rcpListRemove = new JButton(bundle.getString("rcpListRemove"));
		rcpListEdit = new JButton(bundle.getString("rcpListEdit"));
		rcpEditPanel.add(rcpListAdd);
		rcpEditPanel.add(rcpListRemove);
		rcpEditPanel.add(rcpListEdit);
		rcpSelectPanel.add(filterLabelCombo, BorderLayout.NORTH);
		rcpSelectPanel.add(rcpSelectScrollPane, BorderLayout.CENTER);
		rcpSelectPanel.add(rcpEditPanel, BorderLayout.SOUTH);

		// ---------------------------------------------------------------------
		// S E L E C T E D  R E C I P E  D I S P L A Y
		// ---------------------------------------------------------------------
		selectedRcpDescPanel = new JPanel();	
		BoxLayout recipeDescLayout = new BoxLayout(selectedRcpDescPanel, BoxLayout.Y_AXIS);
		selectedRcpDescPanel.setLayout(recipeDescLayout);
		selectedRcpInfo = new JPanel();
		selectedRcpTxt = new JTextArea();
		selectedRcpTxt.setBackground(Color.white);
		selectedRcpTxt.setEditable(false);
		selectedRcpTxt.setCaretColor(new Color(0,0,0,0));
		selectedRcpTxt.setCaretPosition(0);
		selectedRcpTxt.setWrapStyleWord(true);
		selectedRcpTxt.setLineWrap(true);
		selectedRcpTxt.setFont(Constants.textFont);
		selectedRcpTxt.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		selectedDescLabel = new JLabel(bundle.getString("selectedDescLabel"));
		selectedDescLabel.setAlignmentX(CENTER_ALIGNMENT);

		// ----- Scale Recipe Spinner -----
		scaleRcpPanel = new JPanel(new FlowLayout());
		scaleRcpPanel.setOpaque(false);
		scaleRcpLabel = new JLabel("Scale Recipe ");
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
		scaleRcpSpinner = new JSpinner(spinnerModel);
		scaleRcpSpinner.addChangeListener(e -> {
			scaleVal = (int)scaleRcpSpinner.getValue();
			displayActiveRecipe(SCALED);
		});
		scaleRcpPanel.add(scaleRcpLabel);
		scaleRcpPanel.add(scaleRcpSpinner);
		selectedRcpDescPanel.add(scaleRcpPanel);

		// ----- Selected Recipe Scrollpane -----
		selectedRcpTxtScrollPane = new JScrollPane(selectedRcpTxt);
		selectedRcpTxtScrollPane.setPreferredSize(new Dimension(500,500));
		selectedRcpTxtScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		selectedRcpTxtScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		selectedRcpInfo.add(selectedRcpTxtScrollPane);
		selectedRcpDescPanel.add(selectedRcpInfo);

		// ----- Build Panel ----- 
		add(selectedRcpDescPanel, BorderLayout.CENTER);
		add(rcpSelectPanel, BorderLayout.WEST);

		// ----- Panel Graphical Settings -----
		setBackground(Constants.bgGray);
		rcpSelectListPanel.setBackground(Color.white);
		selectedRcpDescPanel.setOpaque(false);
		selectedRcpInfo.setOpaque(false);
		rcpSelectPanel.setOpaque(false);
		rcpSelectPanel.setOpaque(false);
		rcpEditPanel.setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g.create();
		int w = getWidth();
		int h = getHeight();

		Color topColor = new Color(184,184,184);
		Color bottomColor = new Color(217,217,217);

		g2d.setPaint(new GradientPaint(0, 0, topColor, 0, h, bottomColor));
		g2d.fillRect(0, 0, w, h);
		g2d.dispose();
	}

	public List<Ingredient> scaleRecipe(int amt) {
		if (amt <= 0) {
			System.err.println("Negative scale val. passed to scaleRecipe().");
			return null;
		} else if (activeRecipe == null) {
			System.err.println("No recipe to scale.");
			scaleRcpSpinner.setValue(1);
			return null;
		}

		List<Ingredient> scaledIngredients = new ArrayList<>();
		for (Ingredient baseIngredient : activeRecipe.getIngredients()) {
			scaledIngredients.add(new Ingredient(
					baseIngredient.getAmount().multiply(amt),
					baseIngredient.getUnit(),
					baseIngredient.getName()));
		}
		
		return scaledIngredients;
	}

	public void registerController(ActionListener listener) {
		this.listener = listener;
	}

	public void populateRecipeSelectList(List<Recipe> recipes) {
		if (recipes == null || rcpSelectList == null) {
			System.err.println("Recipe list in model or view was not properly initialized: populateRecipeList().");
			return;
		}

		rcpSelectList.clear();

		for (Recipe rcp : recipes) {
			RecipeSelectButton newRcpButton = new RecipeSelectButton(rcp, Constants.recipeTxtFont);
			rcpSelectList.add(newRcpButton);
			newRcpButton.setAlignmentX(CENTER_ALIGNMENT);
			newRcpButton.addActionListener(e -> {
				setActiveRecipe(rcp);
				scaleRcpSpinner.setValue(1);
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

		rcpSelectListPanel.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
				Constants.BUTTON_HEIGHT * rcpSelectList.size()));
		rcpSelectListPanel.revalidate();
		rcpSelectListPanel.repaint();
	}

	public void displayRecipeButtons(List<String> filters) {
		if (rcpSelectList == null) {
			System.err.println("Recipe Select List was not initialized before display.");
			return;
		}

		rcpSelectListPanel.removeAll();

		for (RecipeSelectButton r : rcpSelectList) {
			String name = r.getText().toLowerCase();
			boolean added = false;

			for (String filter : filters) {
				String f = filter.toLowerCase();

				if (name.contains(f)) {
					rcpSelectListPanel.add(r);
					added = true;
					break;
				} else {
					for (String tag : r.getTags()) {
						if (tag.toLowerCase().contains(f)) {
							rcpSelectListPanel.add(r);
							added = true;
							break;
						}
					}
				}

				if (added) {
					break;
				}
			}
		}

		rcpSelectListPanel.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
				Constants.BUTTON_HEIGHT * rcpSelectList.size()));

		rcpSelectScrollPane.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
				Constants.BUTTON_HEIGHT * rcpSelectList.size()));

		rcpSelectListPanel.revalidate();
		rcpSelectListPanel.repaint();
	}

	public void clearFilters() {
		if (filterInput == null) {
			System.err.println("Filter input uninitialized.");
			return;
		}

		filterInput.setText("");
	}

	public void initAddButton() {
		rcpListAdd.setActionCommand("add");
		rcpListAdd.addActionListener(listener);
	}

	public void initRemoveButton() {
		rcpListRemove.setActionCommand("remove");
		rcpListRemove.addActionListener(e -> {
			if (activeRecipe == null) {
				System.out.println("Aborting remove recipe dialog: no active recipe.");
				JOptionPane.showMessageDialog(null,
						bundle.getString("noRcpSelRemove"),
						bundle.getString("noRcpSelRemoveTitle"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			int choice = JOptionPane.showConfirmDialog(null,
					bundle.getString("removeRcpConfirm") +
					activeRecipe.getTitle() + "?",
					bundle.getString("removeRcpConfirmTitle"),
					JOptionPane.OK_CANCEL_OPTION);


			if (choice == JOptionPane.OK_OPTION) {
				listener.actionPerformed(new ActionEvent(
						rcpListRemove, ActionEvent.ACTION_PERFORMED,
						"remove"));
			}
		});
	}

	public void initEditButton() {
		rcpListEdit.setActionCommand("edit");
		rcpListEdit.addActionListener(e -> {
			if (activeRecipe == null) {
				System.out.println("Aborting edit recipe dialog: no active recipe.");
				JOptionPane.showMessageDialog(null,
						bundle.getString("noRcpSelEdit"),
						bundle.getString("noRcpSelEditTitle"),
						JOptionPane.ERROR_MESSAGE);
			} else {
				listener.actionPerformed(new ActionEvent(
						rcpListEdit, ActionEvent.ACTION_PERFORMED,
						"edit"));
			}
		});
	}

	public void clearSelectedRecipeText() {
		selectedRcpTxt.setText("");
	}

	public void updateBundle(Locale locale) {
		bundle = ResourceBundle.getBundle("MessagesBundle", locale);
	}

	public void initFilter() {
		filterApply.addActionListener(listener);
		filterApply.setActionCommand("applyFilter");
		filterClear.addActionListener(listener);
		filterClear.setActionCommand("clearFilter");
		// Filter input shortcut
		filterInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.actionPerformed(new ActionEvent(filterInput,
						ActionEvent.ACTION_PERFORMED,
						"applyFilter"));
			}
		});
	}

	public void refreshTranslatable() {
		rcpSelectLabel.setText(bundle.getString("rcpSelectLabel"));
		selectedDescLabel.setText(bundle.getString("selectedDescLabel"));
		rcpListAdd.setText(bundle.getString("rcpListAdd"));
		rcpListRemove.setText(bundle.getString("rcpListRemove"));
		rcpListEdit.setText(bundle.getString("rcpListEdit"));
		filterApply.setText(bundle.getString("filterApply"));
		filterLabel.setText(bundle.getString("filterLabel"));
		filterClear.setText(bundle.getString("filterClear"));
	}

	public void setActiveRecipe(Recipe recipe) {
		activeRecipe = recipe;
		displayActiveRecipe(UNSCALED);
	}

	private void displayActiveRecipe(int mode) {
		if (mode == UNSCALED) {
			selectedRcpTxt.setText(activeRecipe.formatRecipeForTextDisplay());
		} else if (mode == SCALED) {
			selectedRcpTxt.setText(activeRecipe.formatScaledRecipeForTextDisplay(
							scaleRecipe(scaleVal)));
		}

		selectedRcpTxt.setCaretPosition(0);
	}

	public void clearActiveRecipe() {
		activeRecipe = null;
	}

	public void initFocus() {
		filterInput.requestFocusInWindow();
	}

	public List<String> getFilters() {
		if (filterInput == null) {
			System.err.println("Filter input uninitialized.");
			return null;
		} else if (filterInput.getText().isEmpty()) {
			System.out.println("Nothing to filter by.");
			return null;
		}

		return Arrays.asList(filterInput.getText().trim().split(","));
	}

	public Recipe getActiveRecipe() {
		return activeRecipe;
	}

}
