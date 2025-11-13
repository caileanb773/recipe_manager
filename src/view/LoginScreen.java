package view;

import static definitions.Constants.DARK_GRADIENT_BOTTOM;
import static definitions.Constants.DARK_GRADIENT_TOP;
import static definitions.Constants.EMAIL_IDX;
import static definitions.Constants.INCORRECT_PASSWORD;
import static definitions.Constants.LIGHT_GRADIENT_BOTTOM;
import static definitions.Constants.LIGHT_GRADIENT_TOP;
import static definitions.Constants.NONEXISTENT_EMAIL;
import static definitions.Constants.PW_IDX;
import static definitions.Constants.VALID;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
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
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.mindrot.jbcrypt.BCrypt;

import com.sun.tools.javac.Main;

import definitions.Constants;
import definitions.Theme;
import util.Utility;

/*
 * Author: Cailean Bernard
 * Contents: Login manager for the application.
 */

@SuppressWarnings("serial")
public class LoginScreen extends JPanel implements ApplicationScreen {

	// Swing
	private static JCheckBox rmbrMe;
	private JLabel emailLabel;
	private JLabel pwLabel;
	private JTextField emailInput;
	private JPasswordField pwInput;
	private JButton login;
	private JButton clear;
	private JButton register;
	private JPanel buttonPanel;
	private JCheckBox pwReveal;
	private ResourceBundle bundle;
	private JLabel logoBanner;
	private List<ImageIcon> banners;
	private Color topGradient;
	private Color botGradient;

	// Other
	private ActionListener listener;

	// Constants
	private final int LIGHT_THEME_BANNER = 0;
	private final int DARK_THEME_BANNER = 1;


	public LoginScreen(ResourceBundle bundle) {
		this.bundle = bundle;
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		setBackground(Constants.LIGHT_BG_COL);
		banners = new ArrayList<>();

		// ---------------------------------------------------------------------
		// P A N E L S
		// ---------------------------------------------------------------------
		buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);

		// ---------------------------------------------------------------------
		// C O M P O N E N T S
		// ---------------------------------------------------------------------
		emailLabel = new JLabel(bundle.getString("email"));
		pwLabel = new JLabel(bundle.getString("password"));
		pwReveal = new JCheckBox(bundle.getString("revealPassword"), false);
		rmbrMe = new JCheckBox(bundle.getString("remember"), false);
		login = new JButton(bundle.getString("login"));
		clear = new JButton(bundle.getString("clear"));
		register = new JButton(bundle.getString("register"));
		emailInput = new JTextField(19);
		pwInput = new JPasswordField(19);
		logoBanner = new JLabel();

		// ----- Banner -----
		loadBanner("img/banner_bluegray.png");
		loadBanner("img/banner_blackred.png");

		// ---------------------------------------------------------------------
		// I N P U T  F I E L D S
		// ---------------------------------------------------------------------

		// ----- Email Input -----
		JPanel emailPanel = new JPanel(new SpringLayout());
		emailPanel.setOpaque(false);
		emailPanel.add(emailLabel);
		emailPanel.add(emailInput);
		emailPanel.add(rmbrMe);
		SpringLayout emailLayout = (SpringLayout) emailPanel.getLayout();
		emailLayout.putConstraint(SpringLayout.NORTH, emailLabel, 0, SpringLayout.NORTH, emailPanel);
		emailLayout.putConstraint(SpringLayout.NORTH, emailInput, 0, SpringLayout.NORTH, emailPanel);
		emailLayout.putConstraint(SpringLayout.NORTH, rmbrMe,   0, SpringLayout.NORTH, emailPanel);
		emailLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, emailInput, 0, SpringLayout.HORIZONTAL_CENTER, emailPanel);
		emailLayout.putConstraint(SpringLayout.EAST, emailLabel, -5, SpringLayout.WEST, emailInput);
		emailLayout.putConstraint(SpringLayout.WEST, rmbrMe, 5, SpringLayout.EAST, emailInput);
		emailLayout.putConstraint(SpringLayout.SOUTH, emailPanel, 2, SpringLayout.SOUTH, emailInput);
		emailLayout.putConstraint(SpringLayout.SOUTH, emailPanel, 2, SpringLayout.SOUTH, rmbrMe);

		// ----- Password Input -----
		JPanel passwordPanel = new JPanel(new SpringLayout());
		passwordPanel.setOpaque(false);
		passwordPanel.add(pwLabel);
		passwordPanel.add(pwInput);
		passwordPanel.add(pwReveal);
		SpringLayout passwordLayout = (SpringLayout) passwordPanel.getLayout();
		passwordLayout.putConstraint(SpringLayout.NORTH, pwLabel,   0, SpringLayout.NORTH, passwordPanel);
		passwordLayout.putConstraint(SpringLayout.NORTH, pwInput,   0, SpringLayout.NORTH, passwordPanel);
		passwordLayout.putConstraint(SpringLayout.NORTH, pwReveal, 0, SpringLayout.NORTH, passwordPanel);
		passwordLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pwInput, 0, SpringLayout.HORIZONTAL_CENTER, passwordPanel);
		passwordLayout.putConstraint(SpringLayout.EAST, pwLabel, -5, SpringLayout.WEST, pwInput);
		passwordLayout.putConstraint(SpringLayout.WEST, pwReveal, 5, SpringLayout.EAST, pwInput);
		passwordLayout.putConstraint(SpringLayout.SOUTH, passwordPanel, 2, SpringLayout.SOUTH, pwInput);
		passwordLayout.putConstraint(SpringLayout.SOUTH, passwordPanel, 2, SpringLayout.SOUTH, pwReveal);

		// ----- Parent (BoxLayout) -----
		JPanel formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setOpaque(false);
		formPanel.add(emailPanel);
		formPanel.add(Box.createVerticalStrut(8));
		formPanel.add(passwordPanel);
		formPanel.add(Box.createVerticalStrut(8));

		// ---------------------------------------------------------------------
		// B U T T O N S
		// ---------------------------------------------------------------------
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		buttonPanel.setOpaque(false);
		buttonPanel.add(login);
		buttonPanel.add(clear);
		buttonPanel.add(register);

		// ----- Centering -----
		emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		logoBanner.setAlignmentX(Component.CENTER_ALIGNMENT);
		logoBanner.setHorizontalAlignment(SwingConstants.CENTER);
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// ----- Assembling Screen -----
		add(logoBanner);
		add(formPanel);
		add(buttonPanel);
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

	private void loadBanner(String resourcePath) {
		URL bannerLightUrl = Main.class.getClassLoader().getResource(
				resourcePath);
		if (bannerLightUrl != null) {
			ImageIcon icon = new ImageIcon(bannerLightUrl);
			Image scaledImage = icon.getImage().getScaledInstance(
					icon.getIconWidth() / 2, icon.getIconHeight() / 2,
					Image.SCALE_SMOOTH);
			banners.add(new ImageIcon(scaledImage));
		} else {
			System.err.println("Could not resolve path to banner.");
		}
	}

	@Override
	public void registerController(ActionListener listener) {
		this.listener = listener;
	}

	public void initializeButtons() {
		login.addActionListener(ignored -> validateFields());
		clear.addActionListener(ignored -> reset());
		register.addActionListener(ignored -> register());
		pwReveal.addActionListener(ignored -> togglePwReveal());

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

	public void validateFields() {
		String email = emailInput.getText();
		char[] passArr = pwInput.getPassword();

		if (email.isEmpty() || passArr.length == 0) {
			JOptionPane.showMessageDialog(null, bundle.getString("validate.missingField"), 
					bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (Utility.isEmailValid(email)) {
			int credentialCheck = areCredentialsValid(email, passArr);

			if (credentialCheck == VALID) {
				login();
				pwInput.setText("");
			} else if (credentialCheck == NONEXISTENT_EMAIL) {
				JOptionPane.showMessageDialog(null, bundle.getString("validate.emailUnregistered"),
						bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
				return;
			} else if (credentialCheck == INCORRECT_PASSWORD) {
				JOptionPane.showMessageDialog(null, bundle.getString("validate.incorrectPass"),
						bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
				pwInput.setText("");
				requestPwFocus();
				return;
			}

		} else {
			JOptionPane.showMessageDialog(null, bundle.getString("validate.invalidEmail"),
					bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	private int areCredentialsValid(String email, char[] pw) {
		if (!emailIsRegistered(email)) {
			return NONEXISTENT_EMAIL;
		}

		if (validatePassword(email, pw) == VALID) {
			return VALID;
		} else {
			return INCORRECT_PASSWORD;
		}
	}

	private int validatePassword(String email, char[] pw) {
		try (BufferedReader reader = new BufferedReader(new FileReader("resources/credentials.txt"))) {
			String line;

			while ((line = reader.readLine()) != null) {
				String[] lineData = line.split("=");

				if (email.equalsIgnoreCase(lineData[EMAIL_IDX])) {
					if (BCrypt.checkpw(new String(pw), lineData[PW_IDX])) {
						return VALID;
					}
				} else {
					continue;
				}
			}

		} catch (FileNotFoundException e) {
			System.err.println("Could not find credentials file.");
			return ERROR;
		} catch (IOException e) {
			System.err.println("IO Exception while checking for existing email." + e.getMessage());
			return ERROR;
		} finally {
			java.util.Arrays.fill(pw, '\0');
		}

		return INCORRECT_PASSWORD;
	}

	private boolean emailIsRegistered(String newEmail) {
		try (BufferedReader reader = new BufferedReader(new FileReader("resources/credentials.txt"))) {
			String line;

			while ((line = reader.readLine()) != null) {
				String[] lineData = line.split("=");
				String existingEmail = lineData[EMAIL_IDX];

				if (newEmail.equalsIgnoreCase(existingEmail)) {
					return true;
				}

			}
		} catch (FileNotFoundException e) {
			System.err.println("Could not find credentials file.");
			return false;
		} catch (IOException e) {
			System.err.println("IO Exception while checking for existing email." + e.getMessage());
			return false;
		}

		return false;
	}

	private void login() {
		System.out.println("Credentials validated, logging in");
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "login");
		listener.actionPerformed(event);
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

	@Override
	public void updateBundle(Locale locale) {
		bundle = ResourceBundle.getBundle("MessagesBundle", locale);
	}

	@Override
	public void refreshTranslatable() {
		rmbrMe.setText(bundle.getString("remember"));
		emailLabel.setText(bundle.getString("email"));
		pwLabel.setText(bundle.getString("password"));
		pwReveal.setText(bundle.getString("revealPassword"));
		login.setText(bundle.getString("login"));
		clear.setText(bundle.getString("clear"));
		register.setText(bundle.getString("register"));
	}

	@Override
	public void changeTheme(Theme theme) {

		switch (theme) {
		case LIGHT:
			logoBanner.setIcon(banners.get(LIGHT_THEME_BANNER));
			topGradient = LIGHT_GRADIENT_TOP;
			botGradient = LIGHT_GRADIENT_BOTTOM;
			break;
		case DARK:
			logoBanner.setIcon(banners.get(DARK_THEME_BANNER));
			topGradient = DARK_GRADIENT_TOP;
			botGradient = DARK_GRADIENT_BOTTOM;
			break;
		default:
			System.err.println("Unrecognized theme: " + theme);
		}
	}

	public static boolean isRemembering() {
		return rmbrMe.isSelected();
	}

	public void setRemembering(boolean remembering) {
		rmbrMe.setSelected(remembering);
	}

	public void grabFocus(String field) {
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

	public void clearPwField() {
		pwInput.setText("");
	}

	public String getEmail() {
		return emailInput.getText();
	}

	public void setEmail(String email) {
		emailInput.setText(email);
	}

	public void requestPwFocus() {
		pwInput.requestFocusInWindow();
	}

}
