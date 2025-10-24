package init;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

import controller.AppController;
import model.RecipeMgrModel;
import util.ProgressListener;
import view.AppFrame;

/*
 * Author: Cailean Bernard
 * Contents: 
 */

public class Loader {

	private AppController controller;


	public Loader(AppController controller) {
		this.controller = controller;
	}

	public void run() {
		// Default theme is light
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (Exception e) {
			System.err.println("Error while initializing FlatLAF: " + e.getMessage());
		}
		
		RecipeMgrModel model = controller.getModel();
		AppFrame view = controller.getView();

		// Create ProgressListener
		JDialog progressDialog = new JDialog((JFrame) null, "Loading", true);
		JProgressBar progressBar = new JProgressBar(0, 100);
		JLabel loadingLabel = new JLabel();
		ProgressListener progressListener = new ProgressListener(progressBar,
				loadingLabel);

		// Register ProgressListener in MVC
		model.setProgressListener(progressListener);
		view.setProgressListener(progressListener);
		controller.setProgressListener(progressListener);

		JPanel container = new JPanel(new BorderLayout());
		container.add(progressBar, BorderLayout.CENTER);
		container.add(loadingLabel, BorderLayout.NORTH);
		progressBar.setStringPainted(true);
		progressDialog.add(container, BorderLayout.CENTER);
		progressDialog.setSize(300, 75);
		progressDialog.setLocationRelativeTo(null);
		progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		SwingWorker<Void, Integer> worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
				model.initialize(true); // magic number, false is offline
				view.initialize();
				controller.initialize(1); // magic number, and diff from model. 0 is offline

				return null;
			}

			@Override
			protected void process(List<Integer> chunks) {
				Integer percent = chunks.get(chunks.size() -1);
				progressBar.setValue(percent);
			}

			@Override
			protected void done() {
				progressDialog.dispose();
				view.setViewVisible(true);
			}
		};

		worker.execute();
		progressDialog.setVisible(true);

	}

}
