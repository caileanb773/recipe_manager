package ca.prepledger.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ImportExportController {
	
	@FXML
	private Button exportBtn;
	
	
	@FXML
	public void onExportBtnClicked() {
		System.out.println("click!");
	}

}
