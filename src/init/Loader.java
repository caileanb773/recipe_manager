package init;

import java.awt.BorderLayout;
import java.awt.Image;
import java.net.URL;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
	private Image bannerImage;
	private ImageIcon icon;


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
		URL loadingBannerURL = Main.class.getClassLoader().getResource(
				"img/loadingBanner.png");
		
		if (loadingBannerURL != null) {
			icon = new ImageIcon(loadingBannerURL);
			bannerImage = icon.getImage().getScaledInstance(icon.getIconWidth() / 2,
					icon.getIconHeight() / 2,
					Image.SCALE_SMOOTH);
			icon = new ImageIcon(bannerImage);
		} else {
			System.out.println("Error locating loading screen banner.");
		}
		
		JDialog progressDialog = new JDialog((JFrame) null, "Loading", true);
		JProgressBar progressBar = new JProgressBar(0, 100);
		JLabel loadingLabel = new JLabel();
		loadingLabel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		ProgressListener progressListener = new ProgressListener(progressBar,
				loadingLabel);

		// Register ProgressListener in MVC
		model.setProgressListener(progressListener);
		view.setProgressListener(progressListener);
		controller.setProgressListener(progressListener);

		JPanel container = new JPanel(new BorderLayout());
		container.add(progressBar, BorderLayout.CENTER);
		container.add(loadingLabel, BorderLayout.NORTH);
		JLabel imgHolder = new JLabel();
		imgHolder.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		imgHolder.setIcon(icon);
		progressBar.setStringPainted(true);
		progressDialog.add(container, BorderLayout.CENTER);
		progressDialog.add(imgHolder, BorderLayout.NORTH);
		loadingLabel.setHorizontalAlignment(JLabel.CENTER);
		progressDialog.pack();
		progressDialog.setSize(progressDialog.getWidth(),
				progressDialog.getHeight() + 40);
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
