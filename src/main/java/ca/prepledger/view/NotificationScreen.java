package view;

import static constants.Constants.DARK_ACTIVE_NOTIF_COL;
import static constants.Constants.DARK_BG_COL;
import static constants.Constants.DARK_FG_COL;
import static constants.Constants.DARK_GRADIENT_BOTTOM;
import static constants.Constants.DARK_GRADIENT_TOP;
import static constants.Constants.DARK_INACTIVE_NOTIF_COL;
import static constants.Constants.LIGHT_ACTIVE_NOTIF_COL;
import static constants.Constants.LIGHT_BG_COL;
import static constants.Constants.LIGHT_FG_COL;
import static constants.Constants.LIGHT_GRADIENT_BOTTOM;
import static constants.Constants.LIGHT_GRADIENT_TOP;
import static constants.Constants.LIGHT_INACTIVE_NOTIF_COL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import constants.Constants;
import model.Notification;
import model.Theme;
import service.NotificationListener;
import service.NotificationService;
import util.Utility;

/**
 * Author: Cailean Bernard
 * Contents: The screen which contains all elements related to displaying notifications
 * to the user.
 */
@SuppressWarnings("serial")
public class NotificationScreen extends JPanel implements ApplicationScreen, Listenable, NotificationListener {

	// Swing
	private JPanel headerPanel;
	private JPanel footerPanel;
	private JPanel notificationListPanel;
	private JPanel expandedNotificationInfo;
	private JScrollPane notificationsListScrollPane;
	private JCheckBox selectAll;
	private JButton timeBtn;	// These buttons are for sorting notifications
	private JButton typeBtn;	// These buttons are for sorting notifications
	private JButton rcpBtn;		// These buttons are for sorting notifications
	private JButton senderBtn;	// These buttons are for sorting notifications
	private JButton expandBtn;
	private JButton confirmBtn;
	private JButton rejectBtn;
	private JButton backBtn;

	// Constants and Flags
	private final int NOTIFICATION_ROW_HEIGHT = 45;
	private boolean timeStampSortingOrder = Constants.ASCENDING; // Newest to oldest by default
	private boolean nameSortingOrder = Constants.ASCENDING; // Newest to oldest by default

	// Other
	private ResourceBundle bundle;
	private ActionListener listener;
	private NotificationService service;
	private Color topGradient;
	private Color botGradient;
	private Color activeCol;
	private Color inactiveCol;
	private Color headerPanelCol;
	private Color footerPanelCol;
	private Color panelBgCol;
	private static final Logger logger = LoggerFactory.getLogger(NotificationScreen.class);

	public NotificationScreen(ResourceBundle bundle, NotificationService service) {
		this.bundle = bundle;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setOpaque(false);
		
		// Register for events from the NotificationService
		service.addListener(this);
		this.service = service;

		// Default theme
		topGradient = LIGHT_GRADIENT_TOP;
		botGradient = LIGHT_GRADIENT_BOTTOM;
		activeCol = LIGHT_ACTIVE_NOTIF_COL;
		inactiveCol = LIGHT_INACTIVE_NOTIF_COL;
		panelBgCol = LIGHT_BG_COL;
		headerPanelCol = LIGHT_FG_COL;
		footerPanelCol = LIGHT_FG_COL;

		// ---------------------------------------------------------------------
		// C O M P O N E N T S
		// ---------------------------------------------------------------------

		// ----- Panel init -----
		headerPanel = new JPanel();
		notificationListPanel = new JPanel();
		notificationsListScrollPane = new JScrollPane(notificationListPanel);
		footerPanel = new JPanel();

		// ----- Clickables -----
		selectAll = new JCheckBox();
		timeBtn = new JButton(bundle.getString("timeBtn"));
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
		headerPanel.add(timeBtn);
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
		BoxLayout notifListLayout = new BoxLayout(notificationListPanel, BoxLayout.Y_AXIS);
		notificationListPanel.setLayout(notifListLayout);
		notificationListPanel.setBackground(panelBgCol);

		// ----- Scrollable Area for Notifications Panel -----
		notificationsListScrollPane.getVerticalScrollBar().setUnitIncrement(
				Constants.SCROLL_SPEED);
		notificationsListScrollPane.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		notificationsListScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//notificationsListScrollPane.setBorder(Constants.SOFT_LOWERED_BORDER);

		// ----- Header -----
		headerPanel.setBackground(headerPanelCol);
		//headerPanel.setBorder(Constants.SOFT_RAISED_BORDER);

		// ----- Footer -----
		footerPanel.setBackground(footerPanelCol);
		//footerPanel.setBorder(Constants.SOFT_RAISED_BORDER);

		// ---------------------------------------------------------------------
		// B U T T O N  A C T I O N S
		// ---------------------------------------------------------------------

		selectAll.addActionListener(ignored -> toggleNotificationSelectionStatus());
		
		timeBtn.addActionListener(ignored -> {
			// XXX replace this with call to NotificationService request
			//sort(Constants.SORT_TIME, timeStampSortingOrder);
			timeStampSortingOrder = !timeStampSortingOrder;
		});
		
		senderBtn.addActionListener(ignored -> {
			// XXX replace this with call to NotificationService request
			//sort(Constants.SORT_SENDER, nameSortingOrder);
			nameSortingOrder = !nameSortingOrder;			
		});
		
		confirmBtn.addActionListener(ignored -> {
			confirmSelected();
		});
		
		rejectBtn.addActionListener(ignored -> {
			rejectSelected();
		});

		// ---------------------------------------------------------------------
		// S C R E E N  A S S E M B L Y
		// ---------------------------------------------------------------------

		add(headerPanel, BorderLayout.NORTH);
		add(notificationsListScrollPane, BorderLayout.CENTER);
		add(footerPanel, BorderLayout.SOUTH);
		
		// At the end of it all, refresh
		refreshNotifications();
	}

	private void initButtons() {
		backBtn.addActionListener(ignored -> {
			listener.actionPerformed(new ActionEvent(backBtn, 
					ActionEvent.ACTION_PERFORMED, 
					"showRcpScreen"));
		});
	}
	
	/**
	 * Fetch the list of active notifications from NotificationService.
	 * 
	 * @return List<Notification> a list of all active notifications.
	 */
	private List<Notification> fetchNotifications() {
		return service.getNotifications();
	}
	
	/**
	 * Refresh the list of notifications displayed in the notification screen
	 * by fetching and validating the list from the Notification Service, and
	 * then displaying them. 
	 */
	private void refreshNotifications() {
		List<Notification> notificationList = fetchNotifications();
		
		if (notificationList == null) {
			logger.error("RefreshNotifications(): Notification list null.");
			return;
		} else if (notificationList.isEmpty()) {
			logger.info("RefreshNotifications(): No notifications to display.");
		}
		
		rebuildUI(notificationList);
	}
	
	/**
	 * Display the notifications passed to this method as visual representations
	 * in the Notification List Panel.
	 * 
	 * @param notifications The list of notifications to display.
	 */
	private void rebuildUI(List<Notification> notifications) {
		List<NotificationPanel> notificationItems = new ArrayList<>();

		// Make graphical representations for each notification that exists
		for (Notification n : notifications) {
			// add actions and such here if needed
			NotificationPanel newNotif = new NotificationPanel(n);
			newNotif.setMaximumSize(new Dimension(Integer.MAX_VALUE, NOTIFICATION_ROW_HEIGHT));
			notificationItems.add(newNotif);
		}
		
		// Remove whatever was being displayed
		notificationListPanel.removeAll();

		for (NotificationPanel nBtn : notificationItems) {
			notificationListPanel.add(nBtn);
			nBtn.setBackground(nBtn.getNotification().isActive() ? activeCol : inactiveCol);
			notificationListPanel.add(Box.createVerticalStrut(1));
		}

		Utility.revalidateAndRepaint(notificationListPanel);
	}

	private void toggleNotificationSelectionStatus() {
		setAllNotificationsSelected(selectAll.isSelected());
	}

	// There's probably a more efficient way to do this than removeAll()
	private void setAllNotificationsSelected(boolean isSelected) {
		if (notificationListPanel == null) {
			logger.warn("Notification list Panel was not initialized in setAllNotificationsSelected().");
			return;
		}

		logger.info("Setting all notifications selected: " + isSelected);		
		service.setAllNotificationsSelected(isSelected);
		List<Notification> notifications = service.getNotifications();
		
		rebuildUI(notifications);
	}
	
	/**
	 * Confirm the changes proposed by all notifications selected (via tickbox).
	 */
	private void confirmSelected() {
		List<Notification> list = service.getNotifications();
		
		if (list == null) {
			logger.warn("ConfirmSelected(): Notification List is null.");
			return;
		}
		
		selectAll.setSelected(false);
		
		for (Notification n : list) {
			if (n.isSelected()) {
				n.setSelected(false);
				
				if (n.isActive()) {
					applyProposedChanges(); // Method stub
					n.setInactive();
				}
			}
		}
		
		rebuildUI(list);
	}
	
	private void rejectSelected() {
		List<Notification> list = service.getNotifications();
		
		if (list == null) {
			logger.warn("RejectSelected(): Notification List is null.");
			return;
		}
		
		selectAll.setSelected(false);
		
		for (Notification n : list) {
			if (n.isSelected()) {
				n.setSelected(false);
				
				if (n.isActive()) {
					rejectProposedChanges(); // Method stub
					n.setInactive();
				}
			}
		}
		
		rebuildUI(list);
	}
	
	private void applyProposedChanges() {
		// TODO way down the line
		logger.info("Applying proposed changes");
	}
	
	private void rejectProposedChanges() {
		// TODO way down the line
		logger.info("Rejecting proposed changes");
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
	public void updateBundle(Locale locale) {
		bundle = ResourceBundle.getBundle(Constants.BUNDLE_LOC, locale);
	}

	@Override
	public void refreshTranslatable() {
		timeBtn.setText(bundle.getString("timeBtn"));
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
			activeCol = LIGHT_ACTIVE_NOTIF_COL;
			inactiveCol = LIGHT_INACTIVE_NOTIF_COL;
			break;
		case DARK:
			topGradient = DARK_GRADIENT_TOP;
			botGradient = DARK_GRADIENT_BOTTOM;
			panelBgCol = DARK_BG_COL;
			headerPanelCol = DARK_FG_COL;
			footerPanelCol = DARK_FG_COL;
			activeCol = DARK_ACTIVE_NOTIF_COL;
			inactiveCol = DARK_INACTIVE_NOTIF_COL;
			break;
		default:
			logger.warn("Unrecognized theme: {}", theme.toString());
		}

		notificationListPanel.setBackground(panelBgCol);
		headerPanel.setBackground(headerPanelCol);
		footerPanel.setBackground(footerPanelCol);
		
		refreshNotifications();
	}

	@Override
	public void notificationsChanged() {
		refreshNotifications();
	}

	@Override
	public void registerController(ActionListener listener) {
		this.listener = listener;		
	}

	@Override
	public void notificationAdded() {
		refreshNotifications();
	}

	@Override
	public void notificationRemoved() {
		refreshNotifications();
	}

	@Override
	public void notificationMarkedAsSelected() {
		refreshNotifications();
	}

	@Override
	public void notificationMarkedAsUnselected() {
		refreshNotifications();
	}

	@Override
	public void notificationMarkedAsInactive() {
		// TODO Auto-generated method stub
		
	}

}
