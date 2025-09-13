package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

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
	//private ActionListener listener;
	private ResourceBundle bundle;


	public RegisterScreen(ResourceBundle bundle) {
		this.bundle = bundle;
		JPanel emailEnterPanel = new JPanel();
		JPanel emailConfirmPanel = new JPanel();
		JPanel passwordEnterPanel = new JPanel();
		JPanel passwordConfirmPanel = new JPanel();
		JPanel buttonPanel = new JPanel();

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

		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		add(register);
		add(emailEnterPanel);
		add(emailConfirmPanel);
		add(passwordEnterPanel);
		add(passwordConfirmPanel);
		add(buttonPanel);

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
	}

	public void initializeButtons(ActionListener listener) {
		//this.listener = listener;
		confirm.addActionListener(e -> validateFields());
		cancel.addActionListener(e -> {
			ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
					"cancelRegister");
			listener.actionPerformed(event);
		});
	}

	// XXX this method deals with passwords, update to be more secure asap
	// also probably clean this up. pretty ugly
	private void validateFields() {	
		// Check for empty fields
		if (formHasEmptyFields()) {
			System.out.println("Form had empty fields.");
			// error dialog, one or more fields is empty and the focus is now in that field
			return;
		}
		
		String eEmail = enterEmailField.getText().trim();
		String cEmail = confirmEmailField.getText().trim();
		
		if (Utility.isEmailValid(eEmail) && Utility.isEmailValid(cEmail)) {
			if (eEmail.equals(cEmail)) {
				// emails match and are in the right format. check passwords
				
				if (Arrays.equals(enterPasswordField.getPassword(),
						confirmPasswordField.getPassword())) {
					
					// Emails are in the correct format, match, and so do PWs. store credentials
					storeCredentials(eEmail);
					
				} else {
					// JDialog here
					System.out.println("Passwords do not match.");
				}
			} else {
				// JDialog here
				System.out.println("Entered emails did not match.");
				return;
			}
		} else {
			// JDialog here
			System.out.println("One of the emails wasn't in the right format.");
			return;
		}
		// ensure the passwords match
		// if they do, hash them, then store the credentials
	}
	
	public void storeCredentials(String newUserEmail) {
		System.out.println("Storing credentials for " + newUserEmail);
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
	
	public void registerNewUser(String email, String password) {
		
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
