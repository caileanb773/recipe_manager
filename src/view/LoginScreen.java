package view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.sun.tools.javac.Main;

import definitions.Constants;
import util.Config;
import util.Utility;

/*
 * Author: Cailean Bernard
 * Contents: Login manager for the application.
 */

@SuppressWarnings("serial")
public class LoginScreen extends JPanel {

	private JLabel emailLabel;
	private JLabel pwLabel;
	private JTextField emailInput;
	private JPasswordField pwInput;
	private JButton login;
	private JButton clear;
	private JButton register;
	private JPanel buttonPanel;
	private JPanel emailPanel;
	private JPanel pwPanel;
	private JCheckBox pwReveal;
	private static JCheckBox rmbrMe;
	private ActionListener listener;
	private ResourceBundle bundle;


	public LoginScreen(ResourceBundle bundle) {
		this.bundle = bundle;
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		setBackground(Constants.rcpBtnGray);

		URL bannerUrl = Main.class.getClassLoader().getResource("banner_red.png");

		if (bannerUrl != null) {
			ImageIcon icon = new ImageIcon(bannerUrl);
			Image scaledImage = icon.getImage().getScaledInstance(
					icon.getIconWidth() / 2, icon.getIconHeight() / 2,
					Image.SCALE_SMOOTH);
			JLabel logoBanner = new JLabel(new ImageIcon(scaledImage));
			logoBanner.setAlignmentX(Component.CENTER_ALIGNMENT);
			add(logoBanner);
		} else {
			System.err.println("Could not resolve path to banner.png.");
		}

		emailLabel = new JLabel(bundle.getString("email"));
		pwLabel = new JLabel(bundle.getString("password"));
		pwReveal = new JCheckBox(bundle.getString("revealPassword"), false);
		rmbrMe = new JCheckBox(bundle.getString("remember"), false);
		emailInput = new JTextField(15);
		pwInput = new JPasswordField(15);
		buttonPanel = new JPanel(new FlowLayout());
		emailPanel = new JPanel(new FlowLayout());
		pwPanel = new JPanel(new FlowLayout());

		buttonPanel.setBackground(Constants.rcpBtnGray);
		emailPanel.setBackground(Constants.rcpBtnGray);
		pwPanel.setBackground(Constants.rcpBtnGray);

		login = new JButton(bundle.getString("login"));
		clear = new JButton(bundle.getString("clear"));
		register = new JButton(bundle.getString("register"));

		buttonPanel.add(login);
		buttonPanel.add(clear);
		buttonPanel.add(register);
		emailPanel.add(emailLabel);
		emailPanel.add(emailInput);
		emailPanel.add(rmbrMe);
		pwPanel.add(pwLabel);
		pwPanel.add(pwInput);
		pwPanel.add(pwReveal);

		//emailPanel.setAlignmentX(CENTER_ALIGNMENT);


		add(emailPanel);
		add(pwPanel);
		add(buttonPanel);

		// Check if there is a last email
		String lastEmail = Config.getLastEmail();
		if (lastEmail == null || lastEmail.equals("null") || lastEmail.isEmpty()) {
			rmbrMe.setSelected(false);
		} else {
			rmbrMe.setSelected(true);
			emailInput.setText(lastEmail);
			emailInput.setSelectionStart(0);
			emailInput.setSelectionEnd(0);
		}

	}

	public void initializeButtons(ActionListener listener) {
		this.listener = listener;
		login.addActionListener(e -> validateFields());
		clear.addActionListener(e -> reset());
		register.addActionListener(e -> register());
		pwReveal.addActionListener(e -> togglePwReveal());

		Action confirmAction = new AbstractAction(bundle.getString("login")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateFields();
			}
		};
		KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		emailInput.getInputMap(JComponent.WHEN_FOCUSED).put(enterKey, "confirm");
		emailInput.getActionMap().put("confirm", confirmAction);
		pwInput.getInputMap(JComponent.WHEN_FOCUSED).put(enterKey, "confirm");
		pwInput.getActionMap().put("confirm", confirmAction);
	}

	public void reset() {
		emailInput.setText("");
		pwInput.setText("");
	}

	public String getEmail() {
		return emailInput.getText();
	}

	public void validateFields() {
		String email = emailInput.getText();
		// XXX this should be replaced by something more secure asap
		char[] passArr = pwInput.getPassword();
		String password = new String(passArr); // don't store pw as string

		if (email.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(null, bundle.getString("validate.missingField"), 
					bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (Utility.isEmailValid(email)) {
			// TODO finish this, for now this is temp
			if (email.equalsIgnoreCase("admin@admin.com") && password.equals("admin")) {
				pwInput.setText(""); // replace this with "clearing" the char[]
				ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login");
				listener.actionPerformed(event);
			} else {
				JOptionPane.showMessageDialog(null, bundle.getString("validate.incorrectPass"),
						bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else {
			JOptionPane.showMessageDialog(null, bundle.getString("validate.invalidEmail"),
					bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public void togglePwReveal() {
		char echoChar = '•';

		if (pwReveal.isSelected()) {
			pwInput.setEchoChar((char) 0);
		} else {
			pwInput.setEchoChar(echoChar);
		}
	}

	public void register() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
				"register");
		listener.actionPerformed(event);
	}

	public void updateBundle(Locale locale) {
		bundle = ResourceBundle.getBundle("MessagesBundle", locale);
	}

	public void refreshTranslatable() {
		rmbrMe.setText(bundle.getString("remember"));
		emailLabel.setText(bundle.getString("login"));
		pwLabel.setText(bundle.getString("password"));
		pwReveal.setText(bundle.getString("revealPassword"));
		login.setText(bundle.getString("login"));
		clear.setText(bundle.getString("clear"));
		register.setText(bundle.getString("register"));
	}

	public static boolean isRemembering() {
		return rmbrMe.isSelected();
	}

	public void initFocus(String field) {
		switch (field) {
		case "PASSWORD_FIELD":
			SwingUtilities.invokeLater(() -> pwInput.requestFocusInWindow());
			break;
		default:
		case "EMAIL_FIELD":
			SwingUtilities.invokeLater(() -> emailInput.requestFocusInWindow());
			break;
		}

	}

}
