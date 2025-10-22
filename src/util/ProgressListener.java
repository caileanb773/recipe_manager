package util;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

/*
 * Author: Cailean Bernard
 * Contents: 
 */

public class ProgressListener {
	
	private final JProgressBar progressBar;
	private final JLabel messageLabel;
	
	
	public ProgressListener (JProgressBar progressBar, JLabel messageLabel) {
		this.progressBar = progressBar;
		this.messageLabel = messageLabel;
	}
	
	public void onProgress(int percentLoaded, String msg) {
		progressBar.setValue(percentLoaded);
		messageLabel.setText(msg);
	}

}
