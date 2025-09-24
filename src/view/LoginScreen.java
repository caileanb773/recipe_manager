package view;

import static definitions.Constants.EMAIL_IDX;
import static definitions.Constants.INCORRECT_PASSWORD;
import static definitions.Constants.NONEXISTENT_EMAIL;
import static definitions.Constants.PW_IDX;
import static definitions.Constants.VALID;

import java.awt.Color;
import java.awt.Component;
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
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import org.mindrot.jbcrypt.BCrypt;
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
	private JPanel inputsPanel;
	private JCheckBox pwReveal;
	private static JCheckBox rmbrMe;
	private ActionListener listener;
	private ResourceBundle bundle;


	public LoginScreen(ResourceBundle bundle) {
		this.bundle = bundle;
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		setBackground(Constants.rcpBtnGray);
		URL bannerUrl = Main.class.getClassLoader().getResource("banner_transparentbg.png");

		if (bannerUrl != null) {
			ImageIcon icon = new ImageIcon(bannerUrl);
			Image scaledImage = icon.getImage().getScaledInstance(
					icon.getIconWidth() / 2, icon.getIconHeight() / 2,
					Image.SCALE_SMOOTH);
			JLabel logoBanner = new JLabel(new ImageIcon(scaledImage));
			logoBanner.setAlignmentX(Component.CENTER_ALIGNMENT);
			add(logoBanner);
		} else {
			System.err.println("Could not resolve path to Login Screen banner.");
		}

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3,3,3,3);
		inputsPanel = new JPanel(new GridBagLayout());
		buttonPanel = new JPanel(new FlowLayout());
		emailLabel = new JLabel(bundle.getString("email"));
		pwLabel = new JLabel(bundle.getString("password"));
		pwReveal = new JCheckBox(bundle.getString("revealPassword"), false);
		rmbrMe = new JCheckBox(bundle.getString("remember"), false);
		login = new JButton(bundle.getString("login"));
		clear = new JButton(bundle.getString("clear"));
		register = new JButton(bundle.getString("register"));
		emailInput = new JTextField(20);
		pwInput = new JPasswordField(20);
		
		buttonPanel.setOpaque(false);
		inputsPanel.setOpaque(false);

		buttonPanel.add(login);
		buttonPanel.add(clear);
		buttonPanel.add(register);

		gbc.gridx = 0;
		gbc.gridy = 0;
		inputsPanel.add(emailLabel, gbc);
		gbc.gridx++;
		inputsPanel.add(emailInput, gbc);
		gbc.gridx++;
		inputsPanel.add(rmbrMe, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		inputsPanel.add(pwLabel, gbc);
		gbc.gridx++;
		inputsPanel.add(pwInput, gbc);
		gbc.gridx++;
		inputsPanel.add(pwReveal, gbc);
		gbc.gridx = 1;
		gbc.gridy++;

		add(inputsPanel);
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
	
	public void setEmail(String email) {
		emailInput.setText(email);
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
			// XXX this can now return other constants, like ERROR
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
