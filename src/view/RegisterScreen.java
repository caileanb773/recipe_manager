package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.mindrot.jbcrypt.BCrypt;
import definitions.Constants;
import util.Utility;

/*
 * Author: Cailean Bernard
 * Contents: Allows user to register with an email and password.
 */

@SuppressWarnings("serial")
public class RegisterScreen extends JPanel {

	private JButton confirm;
	private JButton cancel;
	private JLabel register;
	private JLabel enterEmail;
	private JLabel confirmEmail;
	private JLabel enterPass;
	private JLabel confirmPass;
	private JTextField enterEmailField;
	private JTextField confirmEmailField;
	private JPasswordField enterPasswordField;
	private JPasswordField confirmPasswordField;
	private ResourceBundle bundle;
	private JPanel contentPanel;
	private final int FIELD_GAP = 10;


	public RegisterScreen(ResourceBundle bundle) {
		this.bundle = bundle;
		JPanel emailEnterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));		
		JPanel emailConfirmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		JPanel passwordEnterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		JPanel passwordConfirmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		contentPanel = new JPanel();
		
		enterEmailField = new JTextField(15);
		confirmEmailField = new JTextField(15);
		enterPasswordField = new JPasswordField(15);
		confirmPasswordField = new JPasswordField(15);

		confirm = new JButton(bundle.getString("btnConfirm"));
		cancel = new JButton(bundle.getString("btnCancel"));
		register = new JLabel(bundle.getString("register"));
		enterEmail = new JLabel(bundle.getString("enterEmail"));
		confirmEmail = new JLabel(bundle.getString("confirmEmail"));
		enterPass = new JLabel(bundle.getString("enterPass"));
		confirmPass = new JLabel(bundle.getString("confirmPass"));
		
		BoxLayout layout = new BoxLayout(contentPanel, BoxLayout.Y_AXIS);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		contentPanel.setLayout(layout);
		
		contentPanel.add(register);
		contentPanel.add(Box.createVerticalStrut(FIELD_GAP));
		contentPanel.add(emailEnterPanel);
		contentPanel.add(Box.createVerticalStrut(FIELD_GAP));
		contentPanel.add(emailConfirmPanel);
		contentPanel.add(Box.createVerticalStrut(FIELD_GAP));
		contentPanel.add(passwordEnterPanel);
		contentPanel.add(Box.createVerticalStrut(FIELD_GAP));
		contentPanel.add(passwordConfirmPanel);
		contentPanel.add(Box.createVerticalStrut(FIELD_GAP));
		contentPanel.add(buttonPanel);

		add(contentPanel, BorderLayout.CENTER);
		
		emailEnterPanel.add(enterEmail);
		emailEnterPanel.add(enterEmailField);
		emailConfirmPanel.add(confirmEmail);
		emailConfirmPanel.add(confirmEmailField);
		passwordEnterPanel.add(enterPass);
		passwordEnterPanel.add(enterPasswordField);
		passwordConfirmPanel.add(confirmPass);
		passwordConfirmPanel.add(confirmPasswordField);
		buttonPanel.add(confirm);
		buttonPanel.add(cancel);
		
		emailEnterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, emailEnterPanel.getPreferredSize().height));
		emailConfirmPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, emailConfirmPanel.getPreferredSize().height));
		passwordEnterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, passwordEnterPanel.getPreferredSize().height));
		passwordConfirmPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, passwordConfirmPanel.getPreferredSize().height));
		buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonPanel.getPreferredSize().height));
		
		JPanel wrapper = new JPanel(new GridBagLayout());
		wrapper.add(contentPanel);
		add(wrapper, BorderLayout.CENTER);
	}

	public void initializeButtons(ActionListener listener) {
		confirm.addActionListener(e -> validateFields());
		cancel.addActionListener(e -> {
			ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
					"cancelRegister");
			listener.actionPerformed(event);
		});
	}

	private void validateFields() {	
		if (formHasEmptyFields()) {
			JOptionPane.showMessageDialog(null, bundle.getString("register.blankFields"),
					bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String eEmail = enterEmailField.getText().toLowerCase().trim();
		String cEmail = confirmEmailField.getText().toLowerCase().trim();
		
		// XXX too many nested ifs. clean this up (use && conditional, separate into methods)
		if (Utility.isEmailValid(eEmail) && Utility.isEmailValid(cEmail)) {
			if (eEmail.equals(cEmail)) {
				
				// emails match and are in the right format. match passwords
				if (Arrays.equals(enterPasswordField.getPassword(),
						confirmPasswordField.getPassword())) {
					
					// passwords match, check strength
					if (isPasswordStrong(enterPasswordField.getPassword())) {
						
						// Emails are in the correct format, match, and so do PWs. store credentials
						if (emailIsUnique(eEmail)) {
							storeCredentials(eEmail);
							
						} else {
							JOptionPane.showMessageDialog(null, bundle.getString("register.emailNotUnique"),
									bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
							return;
						}
						
					} else {
						JOptionPane.showMessageDialog(null, bundle.getString("register.weakPassword"),
								bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
						return;
					}
					
				} else {
					JOptionPane.showMessageDialog(null, bundle.getString("register.mismatchPassword"),
							bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else {
				JOptionPane.showMessageDialog(null, bundle.getString("register.mismatchEmail"),
						bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else {
			JOptionPane.showMessageDialog(null, bundle.getString("register.invalidEmail"),
					bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		JOptionPane.showMessageDialog(null, bundle.getString("register.success"),
				bundle.getString("export.title"), JOptionPane.INFORMATION_MESSAGE);
	}
	
	private boolean isPasswordStrong(char[] pw) {
		boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
		
		if (pw.length < Constants.MIN_PW_LEN) {
			return false;
		}
		
		for (char c : pw) {
			if (Character.isUpperCase(c)) hasUpper = true;
			else if (Character.isLowerCase(c)) hasLower = true;
			else if (Character.isDigit(c)) hasDigit = true;
			else if (Constants.ASCII_SPECIAL_CHARS.indexOf(c) >= 0) hasSpecial = true;
 		}
		
		return (hasUpper && hasLower && hasDigit && hasSpecial);
	}
	
	// XXX for now, this stores credentials locally. this will need to be changed to a db in the future
	public void storeCredentials(String newUserEmail) {
		System.out.println("Storing new credentials");
		char[] pw = enterPasswordField.getPassword();
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/credentials.txt", true))) {
			
			String email = enterEmailField.getText().toLowerCase().trim();
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
		
		clearFields();
	}
	
	private boolean emailIsUnique(String newEmail) {
		try (BufferedReader r = new BufferedReader(new FileReader("resources/credentials.txt"))) {
			String line;
			
			while ((line = r.readLine()) != null) {
				String[] lineData = line.split("=");
				String existingEmail = lineData[Constants.EMAIL_IDX];
				
				if (newEmail.equals(existingEmail)) {
					return false;
				}
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("Could not find credentials file: " + e.getMessage());
			return false;
		} catch (IOException e) {
			System.out.println("IO exception while checking if email is registered: "
					+ e.getMessage());
			return false;
		}
		
		return true;
	}
	
	private void clearFields() {
		enterEmailField.setText("");
		confirmEmailField.setText("");
		enterPasswordField.setText("");
		confirmPasswordField.setText("");
	}
	
	private boolean formHasEmptyFields() {
		if (enterEmailField.getText().isEmpty()) {
			enterEmailField.requestFocusInWindow();
			return true;
		} else if (confirmEmailField.getText().isEmpty()) {
			confirmEmailField.requestFocusInWindow();
			return true;
		} else if (enterPasswordField.getPassword().length == 0) {
			enterPasswordField.requestFocusInWindow();
			return true;
		} else if (confirmPasswordField.getPassword().length == 0) {
			confirmPasswordField.requestFocusInWindow();
			return true;
		}
		
		return false;
	}

	public void updateBundle(Locale locale) {
		bundle = ResourceBundle.getBundle("MessagesBundle", locale);
	}

	public void refreshTranslatable() {
		confirm.setText(bundle.getString("btnConfirm"));
		cancel.setText(bundle.getString("btnCancel"));
		register.setText(bundle.getString("register"));
		enterEmail.setText(bundle.getString("enterEmail"));
		confirmEmail.setText(bundle.getString("confirmEmail"));
		enterPass.setText(bundle.getString("enterPass"));
		confirmPass.setText(bundle.getString("confirmPass"));
	}
	
	public void initFocus() {
		enterEmailField.requestFocusInWindow();
	}

}
