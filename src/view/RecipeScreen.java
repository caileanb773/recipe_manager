package view;

import static definitions.Constants.DARK_BG_COL;
import static definitions.Constants.DARK_GRADIENT_BOTTOM;
import static definitions.Constants.DARK_GRADIENT_TOP;
import static definitions.Constants.DARK_RECIPE_BTN_COL;
import static definitions.Constants.LIGHT_BG_COL;
import static definitions.Constants.LIGHT_GRADIENT_BOTTOM;
import static definitions.Constants.LIGHT_GRADIENT_TOP;
import static definitions.Constants.LIGHT_RECIPE_BTN_COL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import definitions.Constants;
import definitions.Ingredient;
import definitions.Recipe;
import definitions.Theme;
import util.Utility;

/*
 * Author: Cailean Bernard
 * Contents: Contains the default screen that the user sees upon running the
 * program. The left side of the screen has a list of all recipes loaded as
 * represented by buttons. The right side of the screen contains the recipe
 * ingredients and directions that are displayed when a recipe is clicked.
 */

@SuppressWarnings("serial")
public class RecipeScreen extends JPanel implements ApplicationScreen {

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

	// Selected (active) Recipe information (UI right side)
	private JPanel selectedRcpDescPanel;
	private JTextArea selectedRcpTxt;
	private JScrollPane selectedRcpTxtScrollPane;
	private JLabel scaleRcpLabel;
	private JSpinner scaleRcpSpinner;
	private JPanel scaleRcpPanel;
	private JButton detachRecipeBtn;
	private JButton notificationsBtn;
	
	// Other
	private ResourceBundle bundle;
	private Recipe activeRecipe;
	private ActionListener listener;
	private BigDecimal scaleVal;
	private Color topGradient;
	private Color botGradient;
	private Color rcpBtnColor;
	private Color rcpBtnFontCol;
	private Color panelBgCol;
	private List<JDialog> detachedRcps;

	// Constant
	private final int UNSCALED = 0;
	private final int SCALED = 1;
	private final int SELECTED_RCP_TXT_TEXT_AREA_WIDTH = 500;
	private final int SELECTED_RCP_TXT_TEXT_AREA_HEIGHT = 500;
	private final int DETACHED_RECIPE_HEIGHT_OFFSET = 18;
	private final int DETACHED_RECIPE_X_OFFSET = 285;
	private final int DETACHED_RECIPE_Y_OFFSET = 45;


	public RecipeScreen(ResourceBundle bundle) {	
		this.bundle = bundle;
		setLayout(new BorderLayout());
		rcpSelectList = new ArrayList<RecipeSelectButton>();
		scaleVal = BigDecimal.ONE;
		detachedRcps = new ArrayList<>();

		topGradient = LIGHT_GRADIENT_TOP;
		botGradient = LIGHT_GRADIENT_BOTTOM;
		rcpBtnColor = LIGHT_RECIPE_BTN_COL;
		rcpBtnFontCol = Color.black;
		panelBgCol = LIGHT_BG_COL;

		// ---------------------------------------------------------------------
		// T A G S
		// ---------------------------------------------------------------------
		filterLabelCombo = new JPanel(new BorderLayout());
		filterInputPanel = new JPanel(new BorderLayout());
		filterInputPanel.setOpaque(false);
		filterInput = new JTextField(10);
		filterInput.setBorder(BorderFactory.createLineBorder(Color.gray));
		filterApply = new JButton(bundle.getString("filterApply"));
		filterLabel = new JLabel(bundle.getString("filterLabel"));
		filterLabel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
		filterClear = new JButton(bundle.getString("filterClear"));
		JPanel filterBtns = new JPanel();
		filterBtns.setOpaque(false);
		filterBtns.add(filterApply);
		filterBtns.add(filterClear);
		filterInputPanel.add(filterLabel, BorderLayout.WEST);
		filterInputPanel.add(filterInput, BorderLayout.CENTER);
		filterInputPanel.add(filterBtns, BorderLayout.SOUTH);
		filterLabelCombo.add(filterInputPanel, BorderLayout.SOUTH);
		filterLabelCombo.setBorder(Constants.softRaisedBorder);
		filterLabelCombo.setBackground(Constants.LIGHT_FG_COL);

		// ---------------------------------------------------------------------
		// R E C I P E  S E L E C T I O N  S U B S E C T I O N
		// ---------------------------------------------------------------------
		rcpSelectPanel = new JPanel(new BorderLayout());
		rcpSelectPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,5));
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
				height += 5;

				return new Dimension(width, height);
			}
		};
		
		// ---------------------------------------------------------------------
		// R E C I P E  S E L E C T I O N  L I S T
		// ---------------------------------------------------------------------
		rcpSelectListPanel.setBackground(panelBgCol);
		rcpSelectScrollPane = new JScrollPane(rcpSelectListPanel);
		rcpSelectScrollPane.getVerticalScrollBar().setUnitIncrement(Constants.SCROLL_SPEED);
		rcpSelectScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		rcpEditPanel = new JPanel();
		rcpSelectLabel = new JLabel(bundle.getString("rcpSelectLabel"), JLabel.CENTER);
		rcpSelectLabel.setFont(Constants.titleFont);
		BoxLayout rcpSelectListLayout = new BoxLayout(rcpSelectListPanel, BoxLayout.Y_AXIS);
		rcpSelectListPanel.setLayout(rcpSelectListLayout);
		rcpSelectListPanel.setBorder(Constants.softLoweredBorder);
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
		selectedRcpDescPanel = new JPanel(new BorderLayout());
		selectedRcpTxt = new JTextArea();
		selectedRcpTxt.setBackground(panelBgCol);
		selectedRcpTxt.setEditable(false);
		selectedRcpTxt.setCaretColor(new Color(0,0,0,0));
		selectedRcpTxt.setCaretPosition(0);
		selectedRcpTxt.setWrapStyleWord(true);
		selectedRcpTxt.setLineWrap(true);
		selectedRcpTxt.setFont(Constants.textFont);

		// ----- Scale Recipe Spinner -----
		scaleRcpPanel = new JPanel(new FlowLayout());
		scaleRcpPanel.setOpaque(false);
		scaleRcpLabel = new JLabel(bundle.getString("scaleRcp"));
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, scaleVal);
		scaleRcpSpinner = new JSpinner(spinnerModel);
		scaleRcpSpinner.addChangeListener(ignored -> {
			scaleVal = BigDecimal.valueOf((int)scaleRcpSpinner.getValue());
			displayActiveRecipe(SCALED);
		});
		scaleRcpPanel.add(scaleRcpLabel);
		scaleRcpPanel.add(scaleRcpSpinner);

		// ----- Send Recipe to New Screen Btn -----
		detachRecipeBtn = new JButton(bundle.getString("detachRcp"));
		detachRecipeBtn.addActionListener(ignored -> {
			handleDetachRecipe();
		});
		scaleRcpPanel.add(detachRecipeBtn);
		detachRecipeBtn.setToolTipText(bundle.getString("detachRcpToolTip"));
		
		// ----- Go to Notification Center -----
		notificationsBtn = new JButton(bundle.getString("notifications"));
		notificationsBtn.addActionListener(ignored -> {
			gotoNotifications();
		});
		
		scaleRcpPanel.add(notificationsBtn);
		
		// ----- Selected Recipe Scrollpane -----
		selectedRcpTxtScrollPane = new JScrollPane(selectedRcpTxt);
		selectedRcpTxtScrollPane.setPreferredSize(new Dimension(
				SELECTED_RCP_TXT_TEXT_AREA_WIDTH,
				SELECTED_RCP_TXT_TEXT_AREA_HEIGHT));
		selectedRcpTxtScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		selectedRcpTxtScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		selectedRcpDescPanel.add(selectedRcpTxtScrollPane, BorderLayout.CENTER);
		selectedRcpTxtScrollPane.setBorder(Constants.softLoweredBorder);
		selectedRcpDescPanel.setBorder(BorderFactory.createEmptyBorder(10,5,5,10));
		selectedRcpDescPanel.add(scaleRcpPanel, BorderLayout.SOUTH);

		// ----- Build Panel ----- 
		add(selectedRcpDescPanel, BorderLayout.CENTER);
		add(rcpSelectPanel, BorderLayout.WEST);

		// ----- Panel Graphical Settings -----
		selectedRcpDescPanel.setOpaque(false);
		rcpSelectPanel.setOpaque(false);
		rcpSelectPanel.setOpaque(false);
		rcpEditPanel.setOpaque(false);
		initKeyBindings();
	}
	
	private void gotoNotifications() {
		listener.actionPerformed(new ActionEvent(notificationsBtn, 
				ActionEvent.ACTION_PERFORMED,
				"notifications"));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g.create();
		int w = getWidth();
		int h = getHeight();

		g2d.setPaint(new GradientPaint(0, 0, topGradient, 0, h, botGradient));
		g2d.fillRect(0, 0, w, h);
		g2d.dispose();
	}

	public void initKeyBindings() {
		InputMap inMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actMap = getActionMap();

		inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deletePressed");

		actMap.put("deletePressed", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeRecipe();
			}
		});
	}

	public void handleDetachRecipe() {
		if (activeRecipe == null) {
			JOptionPane.showMessageDialog(this,
					bundle.getString("detachRcpError"),
					bundle.getString("error.title"),
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		JDialog detachedRecipe = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
				bundle.getString("detachedRcp") + ": " + activeRecipe.getTitle(),
				false);

		JTextArea detachedRcpTxt = new JTextArea();
		detachedRecipe.setBackground(panelBgCol);
		detachedRcpTxt = new JTextArea();
		detachedRcpTxt.setOpaque(true);
		detachedRcpTxt.setEditable(false);
		detachedRcpTxt.setCaretColor(new Color(0,0,0,0));
		detachedRcpTxt.setWrapStyleWord(true);
		detachedRcpTxt.setLineWrap(true);
		detachedRcpTxt.setFont(Constants.textFont);
		JScrollPane scroll = new JScrollPane(detachedRcpTxt);
		scroll.setPreferredSize(new Dimension(
				SELECTED_RCP_TXT_TEXT_AREA_WIDTH,
				SELECTED_RCP_TXT_TEXT_AREA_HEIGHT - DETACHED_RECIPE_HEIGHT_OFFSET));
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		detachedRcpTxt.setText(activeRecipe.formatRecipeForTextDisplay());
		detachedRecipe.add(scroll);
		detachedRecipe.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Point pnt = rcpSelectListPanel.getLocationOnScreen();
		detachedRecipe.pack();
		detachedRecipe.setLocation(pnt.x + DETACHED_RECIPE_X_OFFSET,
				pnt.y - DETACHED_RECIPE_Y_OFFSET);
		detachedRecipe.setVisible(true);
		detachedRcpTxt.setCaretPosition(0);

		// Add to the list
		detachedRcps.add(detachedRecipe);
	}
	
	public List<Ingredient> scaleRecipe(BigDecimal amt) {
		if (amt.compareTo(BigDecimal.ZERO) == -1) {
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

	public void focusFirstRecipe() {
		if (rcpSelectList.size() == 0) {
			return;
		}

		RecipeSelectButton  b = rcpSelectList.get(0);
		b.requestFocus();
		setActiveRecipe(b.getBtnRecipe());
	}

	@Override
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
			newRcpButton.addActionListener(ignored -> {
				setActiveRecipe(rcp);
				scaleRcpSpinner.setValue(1);
			});

			if (rcp.getTitle().length() >= 20) {
				newRcpButton.setText(rcp.getTitle().substring(0, 20) + "...");
			}
		}
	}

	public void displayRecipeButtons() {
		if (rcpSelectList == null) {
			System.err.println("Recipe Select List was not initialized before display.");
			return;
		}

		rcpSelectListPanel.removeAll();

		for (RecipeSelectButton r : rcpSelectList) {
			r.setBackground(rcpBtnColor);
			r.setForeground(rcpBtnFontCol);
			rcpSelectListPanel.add(r);
			rcpSelectListPanel.add(Box.createVerticalStrut(2));
		}

		Utility.revalidateAndRepaint(rcpSelectListPanel);
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

		Utility.revalidateAndRepaint(rcpSelectListPanel);
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
		rcpListRemove.addActionListener(ignored -> {
			removeRecipe();
		});
	}

	public void removeRecipe() {
		if (activeRecipe == null) {
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
	}

	public void initEditButton() {
		rcpListEdit.setActionCommand("edit");
		rcpListEdit.addActionListener(ignored -> {
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

	@Override
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
	
	@Override
	public void refreshTranslatable() {
		rcpSelectLabel.setText(bundle.getString("rcpSelectLabel"));
		rcpListAdd.setText(bundle.getString("rcpListAdd"));
		rcpListRemove.setText(bundle.getString("rcpListRemove"));
		rcpListEdit.setText(bundle.getString("rcpListEdit"));
		filterApply.setText(bundle.getString("filterApply"));
		filterLabel.setText(bundle.getString("filterLabel"));
		filterClear.setText(bundle.getString("filterClear"));
		detachRecipeBtn.setText(bundle.getString("detachRcp"));
		notificationsBtn.setText(bundle.getString("notifications"));
	}

	@Override
	public void changeTheme(Theme theme) {

		switch (theme) {
		case LIGHT:
			topGradient = LIGHT_GRADIENT_TOP;
			botGradient = LIGHT_GRADIENT_BOTTOM;
			panelBgCol = LIGHT_BG_COL;
			rcpBtnColor = LIGHT_RECIPE_BTN_COL;
			rcpBtnFontCol = Color.black;
			filterLabelCombo.setBackground(Constants.LIGHT_FG_COL);
			break;
		case DARK:
			topGradient = DARK_GRADIENT_TOP;
			botGradient = DARK_GRADIENT_BOTTOM;
			panelBgCol = DARK_BG_COL;
			rcpBtnColor = DARK_RECIPE_BTN_COL;
			rcpBtnFontCol = Color.white;
			filterLabelCombo.setBackground(Constants.DARK_FG_COL);
			break;
		default:
			System.err.println("Unrecognized theme: " + theme);
		}
		
		selectedRcpTxt.setBackground(panelBgCol);
		rcpSelectListPanel.setBackground(panelBgCol);

		for (JDialog d : detachedRcps) {
			if (d != null) {
				d.setBackground(panelBgCol);
			} else {
				System.out.println("Error changing dialog BGCol, null dialog.");
			}
		}

		displayRecipeButtons();
	}

	public void setActiveRecipe(Recipe recipe) {
		activeRecipe = recipe;
		displayActiveRecipe(UNSCALED);
	}

	private void displayActiveRecipe(int mode) {
		if (activeRecipe == null) {
			return;
		}

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
