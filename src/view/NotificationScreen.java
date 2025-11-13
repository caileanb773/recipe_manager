package view;

import static definitions.Constants.DARK_GRADIENT_BOTTOM;
import static definitions.Constants.DARK_GRADIENT_TOP;
import static definitions.Constants.DARK_BG_COL;
import static definitions.Constants.DARK_FG_COL;
import static definitions.Constants.LIGHT_GRADIENT_BOTTOM;
import static definitions.Constants.LIGHT_GRADIENT_TOP;
import static definitions.Constants.LIGHT_BG_COL;
import static definitions.Constants.LIGHT_RECIPE_BTN_COL;
import static definitions.Constants.LIGHT_FG_COL;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import definitions.Constants;
import definitions.Notification;
import definitions.Theme;

public class NotificationScreen extends JPanel implements ApplicationScreen {
	
	// Swing
	private JPanel headerPanel;
	private JPanel footerPanel;
	private JPanel notificationsListPanel;
	private JPanel expandedNotificationInfo;
	private JScrollPane notificationsListScrollPane;
	private JCheckBox selectAll;
	private JButton typeBtn;	// These buttons are for sorting notifications
	private JButton rcpBtn;		// These buttons are for sorting notifications
	private JButton senderBtn;	// These buttons are for sorting notifications
	private JButton expandBtn;
	private JButton confirmBtn;
	private JButton rejectBtn;
	private JButton backBtn;
	
	// Constants
	
	// Other
	private ArrayList<Notification> notifications;
	private ResourceBundle bundle;
	private ActionListener listener;
	private Color topGradient;
	private Color botGradient;
	private Color rcpBtnColor;
	private Color rcpBtnFontCol;
	private Color headerPanelCol;
	private Color footerPanelCol;
	private Color panelBgCol;
	
	public NotificationScreen(ResourceBundle bundle) {
		this.bundle = bundle;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setOpaque(false);
		
		// Default theme
		topGradient = LIGHT_GRADIENT_TOP;
		botGradient = LIGHT_GRADIENT_BOTTOM;
		rcpBtnColor = LIGHT_RECIPE_BTN_COL;
		rcpBtnFontCol = Color.black;
		panelBgCol = LIGHT_BG_COL;
		headerPanelCol = LIGHT_FG_COL;
		footerPanelCol = LIGHT_FG_COL;
		
		// ---------------------------------------------------------------------
		// C O M P O N E N T S
		// ---------------------------------------------------------------------
		
		// ----- Panel init -----
		headerPanel = new JPanel();
		notificationsListPanel = new JPanel();
		notificationsListScrollPane = new JScrollPane(notificationsListPanel);
		footerPanel = new JPanel();
				
		// ----- Clickables -----
		selectAll = new JCheckBox();
		typeBtn = new JButton(bundle.getString("typeBtn"));
		rcpBtn = new JButton(bundle.getString("rcpBtn"));
		senderBtn = new JButton(bundle.getString("senderBtn"));
		expandBtn = new JButton(bundle.getString("expandBtn"));
		confirmBtn = new JButton(bundle.getString("confirmBtn"));
		rejectBtn = new JButton(bundle.getString("rejectBtn"));
		backBtn = new JButton(bundle.getString("backBtn"));
		initButtons();
		
		// ---------------------------------------------------------------------
		// P A N E L  L A Y O U T
		// ---------------------------------------------------------------------
		
		// ----- Header Panel -----
		headerPanel.add(selectAll);
		headerPanel.add(typeBtn);
		headerPanel.add(rcpBtn);
		headerPanel.add(senderBtn);
		headerPanel.add(expandBtn);
		
		// ----- Footer Panel -----
		footerPanel.add(confirmBtn);
		footerPanel.add(rejectBtn);
		footerPanel.add(backBtn);
		
		// ---------------------------------------------------------------------
		// P A N E L  S E T T I N G S
		// ---------------------------------------------------------------------
		
		// ----- Inner Notifications Panel -----
		BoxLayout notifListLayout = new BoxLayout(notificationsListPanel, BoxLayout.Y_AXIS);
		notificationsListPanel.setLayout(notifListLayout);
		notificationsListPanel.setBackground(panelBgCol);
		
		// ----- Scrollable Area for Notifications Panel -----
		notificationsListScrollPane.getVerticalScrollBar().setUnitIncrement(
				Constants.SCROLL_SPEED);
		notificationsListScrollPane.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		notificationsListScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		notificationsListScrollPane.setBorder(BorderFactory.createSoftBevelBorder(
				BevelBorder.LOWERED));
		
		// ----- Header -----
		headerPanel.setBackground(headerPanelCol);
		headerPanel.setBorder(BorderFactory.createSoftBevelBorder(
				BevelBorder.RAISED));
		
		// ----- Footer -----
		footerPanel.setBackground(footerPanelCol);
		footerPanel.setBorder(BorderFactory.createSoftBevelBorder(
				BevelBorder.RAISED));
			
		// ---------------------------------------------------------------------
		// S C R E E N  A S S E M B L Y
		// ---------------------------------------------------------------------
		
		add(headerPanel, BorderLayout.NORTH);
		add(notificationsListScrollPane, BorderLayout.CENTER);
		add(footerPanel, BorderLayout.SOUTH);
	}
	
	private void initButtons() {
		backBtn.addActionListener(ignored -> {
			listener.actionPerformed(new ActionEvent(backBtn, 
					ActionEvent.ACTION_PERFORMED, 
					"showRcpScreen"));
		});
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

	@Override
	public void registerController(ActionListener controller) {
		this.listener = controller;
	}

	@Override
	public void updateBundle(Locale locale) {
		bundle = ResourceBundle.getBundle("MessagesBundle", locale);
	}

	@Override
	public void refreshTranslatable() {
		typeBtn.setText(bundle.getString("typeBtn"));
		rcpBtn.setText(bundle.getString("rcpBtn"));
		senderBtn.setText(bundle.getString("senderBtn"));
		expandBtn.setText(bundle.getString("expandBtn"));
		confirmBtn.setText(bundle.getString("confirmBtn"));
		rejectBtn.setText(bundle.getString("rejectBtn"));
		backBtn.setText(bundle.getString("backBtn"));
	}

	@Override
	public void changeTheme(Theme theme) {
		
		switch (theme) {
		case LIGHT:
			topGradient = LIGHT_GRADIENT_TOP;
			botGradient = LIGHT_GRADIENT_BOTTOM;
			panelBgCol = LIGHT_BG_COL;
			headerPanelCol = LIGHT_FG_COL;
			footerPanelCol = LIGHT_FG_COL;
			break;
		case DARK:
			topGradient = DARK_GRADIENT_TOP;
			botGradient = DARK_GRADIENT_BOTTOM;
			panelBgCol = DARK_BG_COL;
			headerPanelCol = DARK_FG_COL;
			footerPanelCol = DARK_FG_COL;
			break;
		default:
			System.err.println("Unrecognized theme: " + theme);
		}
		
		notificationsListPanel.setBackground(panelBgCol);
		headerPanel.setBackground(headerPanelCol);
		footerPanel.setBackground(footerPanelCol);
	}
	
}
