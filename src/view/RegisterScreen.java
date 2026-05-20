package view;

import static definitions.Constants.DARK_GRADIENT_BOTTOM;
import static definitions.Constants.DARK_GRADIENT_TOP;
import static definitions.Constants.LIGHT_GRADIENT_BOTTOM;
import static definitions.Constants.LIGHT_GRADIENT_TOP;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.mindrot.jbcrypt.BCrypt;
import com.sun.tools.javac.Main;
import definitions.Constants;
import definitions.Theme;
import util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Author: Cailean Bernard
 * Contents: Allows user to register with an email and password.
 */
@SuppressWarnings("serial")
public class RegisterScreen extends JPanel implements ApplicationScreen {

	// Swing
	private JPasswordField passwordInput;
	private JTextField emailInput;
	private JButton confirmBtn;
	private JButton cancelBtn;
	private JButton pwRevealBtn;
	private JLabel pwStrengthIndicator;
	private JLabel registerLbl;
	private JLabel emailInputLbl;
	private JLabel passwordInputLbl;
	private JPanel buttonPanel;
	private Image[] pwStrengthIndicators;
	private ImageIcon[] pwRevealIcons;

	// Constants
	private final int GRAY_CHECK = 0;
	private final int GREEN_CHECK = 1;
	private final int EYE_OPEN = 0;
	private final int EYE_CLOSED = 1;
	private final int EYE_OPEN_ROLL = 2;
	private final int EYE_CLOSED_ROLL = 3;
	private final int ICON_SCALE = 3;

	// Other
	private boolean isPasswordHidden;
	private ActionListener listener;
	private ResourceBundle bundle;
	private Color topGradient;
	private Color botGradient;
	private static final Logger logger = LoggerFactory.getLogger(RegisterScreen.class);


	public RegisterScreen(ResourceBundle bundle) {
		this.bundle = bundle;
		pwStrengthIndicators = new Image[2];
		pwRevealIcons = new ImageIcon[4];
		isPasswordHidden = true;
		topGradient = LIGHT_GRADIENT_TOP;
		botGradient = LIGHT_GRADIENT_BOTTOM;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

		// ---------------------------------------------------------------------
		// C O M P O N E N T S
		// ---------------------------------------------------------------------
		registerLbl = new JLabel(bundle.getString("register"));
		emailInputLbl = new JLabel(bundle.getString("enterEmail"));
		passwordInputLbl = new JLabel(bundle.getString("enterPass"));
		emailInput = new JTextField(20);
		passwordInput = new JPasswordField(20);
		confirmBtn = new JButton(bundle.getString("btnConfirm"));
		cancelBtn = new JButton(bundle.getString("btnCancel"));

		registerLbl.putClientProperty("FlatLaf.styleClass", "h2");

		initPwStrengthIndicator();
		initPwRevealBtn();
		initPwFieldChecking();

		// Button panel
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		buttonPanel.setOpaque(false);
		buttonPanel.add(confirmBtn);
		buttonPanel.add(cancelBtn);

		// ---------------------------------------------------------------------
		// I N P U T  P A N E L S
		// ---------------------------------------------------------------------
		JPanel emailPanel = createInputPanel(emailInputLbl, emailInput);
		JPanel passwordPanel = createPasswordPanel();

		// Form container
		JPanel formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setOpaque(false);
		formPanel.add(emailPanel);
		formPanel.add(Box.createVerticalStrut(10));
		formPanel.add(passwordPanel);

		// ---------------------------------------------------------------------
		// C E N T E R I N G
		// ---------------------------------------------------------------------
		registerLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		registerLbl.setHorizontalAlignment(SwingConstants.CENTER);

		emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// ---------------------------------------------------------------------
		// A S S E M B L Y
		// ---------------------------------------------------------------------
		add(Box.createVerticalGlue());
		add(registerLbl);
		add(Box.createVerticalStrut(20));
		add(formPanel);
		add(Box.createVerticalStrut(20));
		add(buttonPanel);
		add(Box.createVerticalGlue());
	}

	private JPanel createInputPanel(JLabel label, JTextField input) {
		JPanel panel = new JPanel(new SpringLayout());
		panel.setOpaque(false);
		panel.add(label);
		panel.add(input);

		SpringLayout layout = (SpringLayout) panel.getLayout();
		layout.putConstraint(SpringLayout.NORTH, label, 0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.NORTH, input, 0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, input, 0, SpringLayout.HORIZONTAL_CENTER, panel);
		layout.putConstraint(SpringLayout.EAST, label, -6, SpringLayout.WEST, input);
		layout.putConstraint(SpringLayout.SOUTH, panel, 0, SpringLayout.SOUTH, input);

		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, input.getPreferredSize().height + 6));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		return panel;
	}

	private JPanel createPasswordPanel() {
		JPanel panel = new JPanel(new SpringLayout());
		panel.setOpaque(false);
		panel.add(passwordInputLbl);
		panel.add(passwordInput);
		panel.add(pwStrengthIndicator);
		panel.add(pwRevealBtn);

		SpringLayout layout = (SpringLayout) panel.getLayout();
		layout.putConstraint(SpringLayout.NORTH, passwordInputLbl, 0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.NORTH, passwordInput,    0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.NORTH, pwStrengthIndicator, 0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.NORTH, pwRevealBtn,      0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, passwordInput, 0, SpringLayout.HORIZONTAL_CENTER, panel);
		layout.putConstraint(SpringLayout.EAST, passwordInputLbl, -6, SpringLayout.WEST, passwordInput);
		layout.putConstraint(SpringLayout.WEST, pwStrengthIndicator, 6, SpringLayout.EAST, passwordInput);
		layout.putConstraint(SpringLayout.WEST, pwRevealBtn,         4, SpringLayout.EAST, pwStrengthIndicator);
		layout.putConstraint(SpringLayout.SOUTH, panel, 0, SpringLayout.SOUTH, passwordInput);

		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, passwordInput.getPreferredSize().height + 6));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		return panel;
	}

	private void initPwFieldChecking() {
		passwordInput.getDocument().addDocumentListener(new DocumentListener() {
			private void updateStrength() {
				char[] pw = passwordInput.getPassword();
				boolean weak = isPasswordWeak(pw);

				Arrays.fill(pw, '\0');

				pwStrengthIndicator.setIcon(weak ? 
						new ImageIcon(pwStrengthIndicators[GRAY_CHECK]) : 
							new ImageIcon(pwStrengthIndicators[GREEN_CHECK]));
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateStrength();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateStrength();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateStrength();
			}
		});
	}

	@Override
	public void registerController(ActionListener listener) {
		this.listener = listener;
	}

	private void initPwStrengthIndicator() {
		URL grayCheckUrl = Main.class.getClassLoader().getResource("img/gray_check.png");
		URL greenCheckUrl = Main.class.getClassLoader().getResource("img/green_check.png");

		// TODO handle indexoutofbounds
		if (grayCheckUrl != null && greenCheckUrl != null) {
			ImageIcon grayChk = new ImageIcon(grayCheckUrl);
			Image scaledGrayChk = grayChk.getImage().getScaledInstance(
					grayChk.getIconWidth() / 3,
					grayChk.getIconHeight() / 3,
					Image.SCALE_SMOOTH); // try this with scale_fast as well

			ImageIcon greenChk = new ImageIcon(greenCheckUrl);
			Image scaledGreenChk = greenChk.getImage().getScaledInstance(
					greenChk.getIconWidth() / 3,
					greenChk.getIconHeight() / 3,
					Image.SCALE_SMOOTH);
			pwStrengthIndicators[GRAY_CHECK] = scaledGrayChk;
			pwStrengthIndicators[GREEN_CHECK] = scaledGreenChk;
		} else {
			logger.warn("Could not resolve path(s) to password strength indicator icon(s).");
		}

		pwStrengthIndicator = new JLabel(new ImageIcon(pwStrengthIndicators[GRAY_CHECK]));
	}

	// TODO add the checkbox back as a fallback if images fail to load
	private void initPwRevealBtn() {
		pwRevealBtn = new JButton();
		pwRevealBtn.setContentAreaFilled(false);
		URL eyeClosedUrl = Main.class.getClassLoader().getResource("img/eye_closed.png");
		URL eyeClosedRollUrl = Main.class.getClassLoader().getResource("img/eye_closed_rollover.png");
		URL eyeOpenUrl = Main.class.getClassLoader().getResource("img/eye_open.png");
		URL eyeOpenRollUrl = Main.class.getClassLoader().getResource("img/eye_open_rollover.png");

		if (eyeOpenUrl != null && eyeOpenRollUrl != null 
				&& eyeClosedUrl != null && eyeClosedRollUrl != null) {
			pwRevealIcons[EYE_OPEN] = loadScaledIcon(eyeOpenUrl, ICON_SCALE);
			pwRevealIcons[EYE_CLOSED] = loadScaledIcon(eyeClosedUrl, ICON_SCALE);
			pwRevealIcons[EYE_OPEN_ROLL] = loadScaledIcon(eyeOpenRollUrl, ICON_SCALE);
			pwRevealIcons[EYE_CLOSED_ROLL] = loadScaledIcon(eyeClosedRollUrl, ICON_SCALE);
		} else {
			logger.warn("Could not resolve path(s) to password reveal button icon(s).");
		}

		pwRevealBtn.setIcon(pwRevealIcons[EYE_CLOSED]);
		pwRevealBtn.setRolloverIcon(pwRevealIcons[EYE_CLOSED_ROLL]);

	}

	private ImageIcon loadScaledIcon(URL url, int scale) {
		ImageIcon icon = new ImageIcon(url);
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		return new ImageIcon(icon.getImage()
				.getScaledInstance(w / scale, h / scale, Image.SCALE_SMOOTH));
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

	public void initializeButtons() {
		pwRevealBtn.addActionListener(ignored -> togglePwReveal());
		confirmBtn.addActionListener(ignored -> validateFields());
		cancelBtn.addActionListener(ignored -> {
			ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
					"cancelRegister");
			listener.actionPerformed(event);
		});
	}

	public void togglePwReveal() {
		logger.info("Toggling password shown: {}", isPasswordHidden);
		char echoChar = '•';

		if (isPasswordHidden) {
			passwordInput.setEchoChar((char) 0);
			isPasswordHidden = false;
			pwRevealBtn.setIcon(pwRevealIcons[EYE_OPEN]);
			pwRevealBtn.setRolloverIcon(pwRevealIcons[EYE_OPEN_ROLL]);
		} else {
			passwordInput.setEchoChar(echoChar);
			isPasswordHidden = true;
			pwRevealBtn.setIcon(pwRevealIcons[EYE_CLOSED]);
			pwRevealBtn.setRolloverIcon(pwRevealIcons[EYE_CLOSED_ROLL]);
		}
	}

	private void validateFields() {
		String email = emailInput.getText().toLowerCase().trim();

		if (formHasEmptyFields()) {
			JOptionPane.showMessageDialog(null, bundle.getString("register.blankFields"),
					bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!Utility.isEmailValid(email)) {
			JOptionPane.showMessageDialog(null, bundle.getString("register.invalidEmail"),
					bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (emailIsRegistered(email)) {
			JOptionPane.showMessageDialog(null, bundle.getString("register.emailNotUnique"),
					bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (isPasswordWeak(passwordInput.getPassword())) {
			JOptionPane.showMessageDialog(null, bundle.getString("register.weakPassword"),
					bundle.getString("register.weakPasswordTitle"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		storeCredentials(email);
		JOptionPane.showMessageDialog(null, bundle.getString("register.success"),
				bundle.getString("export.title"), JOptionPane.INFORMATION_MESSAGE);
	}

	private boolean isPasswordWeak(char[] pw) {
		boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;

		if (pw.length < Constants.MIN_PW_LEN) {
			return true;
		}

		for (char c : pw) {
			if (Character.isUpperCase(c)) hasUpper = true;
			else if (Character.isLowerCase(c)) hasLower = true;
			else if (Character.isDigit(c)) hasDigit = true;
			else if (Constants.ASCII_SPECIAL_CHARS.indexOf(c) >= 0) hasSpecial = true;
		}

		return (hasUpper && hasLower && hasDigit && hasSpecial) ? false : true;
	}

	// XXX for now, this stores credentials locally. this will need to be changed to a db in the future
	public void storeCredentials(String newUserEmail) {
		logger.info("Storing new User credentials.");
		String email;
		char[] pw = passwordInput.getPassword();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/credentials.txt", true))) {

			email = emailInput.getText().toLowerCase().trim();
			writer.write(email);
			writer.write("=");
			String s = new String(pw);
			String hashed = BCrypt.hashpw(s, BCrypt.gensalt());
			writer.write(hashed);
			writer.write('\n');

		} catch (IOException e) {
			logger.error("IO exception encountered in storeCredentials(): {}", e.getMessage());
			clearFields();
			return;
		} finally {
			java.util.Arrays.fill(pw, '\0');
		}

		finishRegistration(email);
	}

	private boolean emailIsRegistered(String newEmail) {
		File credentials = new File("resources/credentials.txt");

		if (!credentials.exists()) {
			logger.info("Credentials file not found. Creating...");

			try {
				credentials.createNewFile();
			} catch (IOException e) {
				logger.error(
						"IOException while creating default credentials file in emailIsRegistered(): {}",
						e.getMessage());
			}
		}

		try (BufferedReader r = new BufferedReader(new FileReader(credentials))) {
			String line;

			while ((line = r.readLine()) != null) {
				String[] lineData = line.split("=");
				String existingEmail = lineData[Constants.EMAIL_IDX];

				if (newEmail.equals(existingEmail)) {
					return true;
				}
			}

		} catch (FileNotFoundException e) {
			logger.error(
					"FileNotFoundException: Could not find credentials file in emailIsRegistered: {}",
					e.getMessage());
			return true;
		} catch (IOException e) {
			logger.error(
					"IO exception while checking emailIsRegistered(): {}",
					e.getMessage());
			return true;
		}

		return false;
	}

	public void clearFields() {
		emailInput.setText("");
		passwordInput.setText("");
	}

	public void finishRegistration(String newEmail) {
		emailInput.setText("");
		passwordInput.setText("");
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
				"returnToLoginAfterRegister&" + newEmail);
		listener.actionPerformed(event);
	}

	private boolean formHasEmptyFields() {
		if (emailInput.getText().isEmpty()) {
			emailInput.requestFocusInWindow();
			return true;
		} else if (passwordInput.getPassword().length == 0) {
			passwordInput.requestFocusInWindow();
			return true;
		}

		return false;
	}

	@Override
	public void updateBundle(Locale locale) {
		bundle = ResourceBundle.getBundle("MessagesBundle", locale);
	}

	@Override
	public void refreshTranslatable() {
		confirmBtn.setText(bundle.getString("btnConfirm"));
		cancelBtn.setText(bundle.getString("btnCancel"));
		registerLbl.setText(bundle.getString("register"));
		emailInputLbl.setText(bundle.getString("enterEmail"));
		passwordInputLbl.setText(bundle.getString("enterPass"));
	}

	@Override
	public void changeTheme(Theme theme) {

		switch (theme) {
		case LIGHT:
			topGradient = LIGHT_GRADIENT_TOP;
			botGradient = LIGHT_GRADIENT_BOTTOM;
			break;
		case DARK:
			topGradient = DARK_GRADIENT_TOP;
			botGradient = DARK_GRADIENT_BOTTOM;
			break;
		default:
			logger.warn("Unrecognized theme: {}", theme);
		}
	}

	public void initFocus() {
		emailInput.requestFocusInWindow();
	}

}
