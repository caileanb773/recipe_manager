package view;

import static definitions.Constants.GRADIENT_BOTTOM;
import static definitions.Constants.GRADIENT_TOP;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.mindrot.jbcrypt.BCrypt;
import com.sun.tools.javac.Main;
import definitions.Constants;
import util.Utility;

/*
 * Author: Cailean Bernard
 * Contents: Allows user to register with an email and password.
 */
@SuppressWarnings("serial")
public class RegisterScreen extends JPanel {

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
	private JLabel passwordRequirements;
	private JPanel contentPanel;
	private JPanel pwRqmntPanel;
	private JPanel buttonPanel;
	private JPanel wrapperPanel;
	private ResourceBundle bundle;
	private Image[] pwStrengthIndicators;
	private ImageIcon[] pwRevealIcons;
	private ActionListener listener;

	// Local Constants
	private final int GRAY_CHECK = 0;
	private final int GREEN_CHECK = 1;
	private final int EYE_OPEN = 0;
	private final int EYE_CLOSED = 1;
	private final int EYE_OPEN_ROLL = 2;
	private final int EYE_CLOSED_ROLL = 3;
	private final int ICON_SCALE = 3;

	// Other
	private boolean isPasswordHidden;


	public RegisterScreen(ResourceBundle bundle) {
		this.bundle = bundle;
		setLayout(new BorderLayout());
		pwStrengthIndicators = new Image[2];
		pwRevealIcons = new ImageIcon[4];
		isPasswordHidden = true;

		// ---------------
		// Panels
		// ---------------
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		buttonPanel.setOpaque(false);

		contentPanel = new JPanel();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3,3,3,3);
		contentPanel.setLayout(new GridBagLayout());
		contentPanel.setOpaque(false);

		pwRqmntPanel = new JPanel();
		pwRqmntPanel.setOpaque(false);

		// ----- Wrapper for stacking panels vertically -----
		wrapperPanel = new JPanel();
		wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
		wrapperPanel.setOpaque(false);

		// ---------------
		// Components
		// ---------------
		emailInput = new JTextField(20);
		passwordInput = new JPasswordField(20);
		confirmBtn = new JButton(bundle.getString("btnConfirm"));
		cancelBtn = new JButton(bundle.getString("btnCancel"));
		registerLbl = new JLabel(bundle.getString("register"));
		emailInputLbl = new JLabel(bundle.getString("enterEmail"));
		passwordInputLbl = new JLabel(bundle.getString("enterPass"));
		registerLbl.putClientProperty( "FlatLaf.styleClass", "h2" );

		// ----- Indicators for weak/strong password & revealing ----- 
		initPwStrengthIndicator();
		initPwRevealBtn();
		initPwFieldChecking();

		// ---------------
		// Registration Form
		// ---------------
		gbc.gridx = 1;
		gbc.gridy = 0;
		contentPanel.add(registerLbl, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		contentPanel.add(emailInputLbl, gbc);
		gbc.gridx = 1;
		contentPanel.add(emailInput, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		contentPanel.add(passwordInputLbl, gbc);
		gbc.gridx = 1;
		contentPanel.add(passwordInput, gbc);
		gbc.gridx = 2;
		contentPanel.add(pwStrengthIndicator, gbc);
		gbc.gridx = 3;
		contentPanel.add(pwRevealBtn, gbc);

		// ---------------
		// Button Panel
		// ---------------
		buttonPanel.add(confirmBtn);
		buttonPanel.add(cancelBtn);

		// ---------------
		// Adding Components to Vertical Stacking Panel
		// ---------------
		wrapperPanel.add(contentPanel);
		wrapperPanel.add(Box.createVerticalStrut(10));
		wrapperPanel.add(buttonPanel);
		wrapperPanel.add(Box.createVerticalStrut(10));
		wrapperPanel.add(pwRqmntPanel);

		// ---------------
		// Constrain Size of Panels Vertically
		// ---------------
		Dimension pref = contentPanel.getPreferredSize();
		contentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
		pref = buttonPanel.getPreferredSize();
		buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
		pref = pwRqmntPanel.getPreferredSize();
		pwRqmntPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));

		// ---------------
		// Center Wrapper Panel Vertically in Outer Panel
		// ---------------
		JPanel outer = new JPanel(new GridBagLayout());
		outer.setOpaque(false);
		outer.add(wrapperPanel);

		// Add Centered Panel to Parent
		add(outer, BorderLayout.CENTER);
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
			System.err.println("Could not resolve path(s) to password strength indicators.");
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
			System.err.println("Could not resolve path(s) to password reveal button icon.");
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

		g2d.setPaint(new GradientPaint(0, 0, GRADIENT_TOP, 0, h, GRADIENT_BOTTOM));
		g2d.fillRect(0, 0, w, h);
		g2d.dispose();
	}

	public void initializeButtons(ActionListener listener) {
		this.listener = listener;
		pwRevealBtn.addActionListener(e -> togglePwReveal());
		confirmBtn.addActionListener(e -> validateFields());
		cancelBtn.addActionListener(e -> {
			ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
					"cancelRegister");
			listener.actionPerformed(event);
		});
	}

	public void togglePwReveal() {
		System.out.println("Toggling pw reveal");
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
		System.out.println("Storing new credentials");
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
			System.out.println("IO exception encountered: " + e.getMessage());
			clearFields();
			return;
		} finally {
			java.util.Arrays.fill(pw, '\0');
		}

		finishRegistration(email);
	}

	private boolean emailIsRegistered(String newEmail) {
		try (BufferedReader r = new BufferedReader(new FileReader("resources/credentials.txt"))) {
			String line;

			while ((line = r.readLine()) != null) {
				String[] lineData = line.split("=");
				String existingEmail = lineData[Constants.EMAIL_IDX];

				if (newEmail.equals(existingEmail)) {
					return true;
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("Could not find credentials file: " + e.getMessage());
			return true;
		} catch (IOException e) {
			System.out.println("IO exception while checking if email is registered: "
					+ e.getMessage());
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

	public void updateBundle(Locale locale) {
		bundle = ResourceBundle.getBundle("MessagesBundle", locale);
	}

	public void refreshTranslatable() {
		confirmBtn.setText(bundle.getString("btnConfirm"));
		cancelBtn.setText(bundle.getString("btnCancel"));
		registerLbl.setText(bundle.getString("register"));
		emailInputLbl.setText(bundle.getString("enterEmail"));
		passwordInputLbl.setText(bundle.getString("enterPass"));
	}

	public void initFocus() {
		emailInput.requestFocusInWindow();
	}

}
