package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import definitions.Constants;
import definitions.Recipe;

/*
 * Author: Cailean Bernard
 * Contents: Contains the default screen that the user sees upon running the
 * program. The left side of the screen has a list of all recipes loaded as
 * represented by buttons. The right side of the screen contains the recipe
 * ingredients and directions that are displayed when a recipe is clicked.
 */

@SuppressWarnings("serial")
public class UserInterfaceScreen extends JPanel {

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
	private JPanel selectedRcpInfo;
	private JLabel selectedDescLabel;
	private JTextArea selectedRcpTxt;
	private JScrollPane selectedRcpTxtScrollPane;
	
	// Other
	private ResourceBundle bundle;
	private Recipe activeRecipe;


	public UserInterfaceScreen(ResourceBundle bundle) {		
		rcpSelectList = new ArrayList<RecipeSelectButton>();
		setLayout(new BorderLayout());
		this.bundle = bundle;

		// TAG STUFF
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

		// Recipe Selection List Panel (WEST)
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

		// Selected Recipe Information Panel (CENTER)
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
		//selectedDescLabel.setFont(Constants.titleFont);
		
		//selectedDescLabel.putClientProperty( "FlatLaf.styleClass", "h2" );

		
		selectedRcpTxtScrollPane = new JScrollPane(selectedRcpTxt);
		selectedRcpTxtScrollPane.setPreferredSize(new Dimension(500,500));
		selectedRcpTxtScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		selectedRcpTxtScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		selectedRcpInfo.add(selectedRcpTxtScrollPane);
		//selectedRcpDescPanel.add(selectedDescLabel);
		selectedRcpDescPanel.add(selectedRcpInfo);
		add(selectedRcpDescPanel, BorderLayout.CENTER);
		add(rcpSelectPanel, BorderLayout.WEST);
		
		// testing colours
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

	            if (added) break;
	        }
	    }

	    rcpSelectListPanel.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
                Constants.BUTTON_HEIGHT * rcpSelectList.size()));

	    rcpSelectScrollPane.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
                Constants.BUTTON_HEIGHT * rcpSelectList.size()));

	    
	    rcpSelectListPanel.revalidate();
	    rcpSelectListPanel.repaint();
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

	public void clearFilters() {
		if (filterInput == null) {
			System.err.println("Filter input uninitialized.");
			return;
		}

		filterInput.setText("");
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

	public void initializeEditButton(ActionListener listener) {
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

	public void initializeFilter(ActionListener listener) {
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

	public Recipe getActiveRecipe() {
		return activeRecipe;
	}

	public void setActiveRecipe(Recipe recipe) {
		activeRecipe = recipe;
		selectedRcpTxt.setText(activeRecipe.formatRecipeForTextDisplay());
		selectedRcpTxt.setCaretPosition(0);
	}
	
	public void clearActiveRecipe() {
		activeRecipe = null;
	}
	
	public void initFocus() {
		filterInput.requestFocusInWindow();
	}

}
