package start_MEMORY;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import XML_DEPRECATED.XMLhandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sound.MP3handler;

/**
 * Changes are reported to Slack as well - This is still under Testing
 * 
 * @author Gruppe 6 - Memory DHBW Mannheim
 */
public class Start extends Application {
	public static void main(String[] args) {
		XMLhandler.createdoc();
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/MainMenu/Menu.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.show();
		// initialize MP3Handler
		MP3handler.play(0);
	}
}
