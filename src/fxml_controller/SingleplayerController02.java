package fxml_controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import game.ExceptionHandler;
import game.GameMaster;
import image.IMGhandler;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sound.MP3handler;
import start_MEMORY.Start;

/**
 * @author D067928
 *	Diese Szene fragt nach dem Namen im Einzelspieler und startet dann das Spiel
 */
public class SingleplayerController02 implements Initializable {

	@FXML
	VBox textFieldArea;
	@FXML
	Text errorTxt;
	static int players = Start.getGamemode();

	TextField newField = new TextField();

	Stage popupload;
	FXMLLoader loader = new FXMLLoader();
	@FXML
	private Button singleplayer;
	@FXML
	private Button gameStart;
	@FXML
	private AnchorPane AnchorPane;

	/**
	 * F�gt das Textfeld f�r den Namen hinzu
	 */
	private void AddTextFields() {
		textFieldArea.getChildren().add(newField);
	}

	/**
	 * Pr�ft ob der eingegebene Name leer ist
	 */
	@FXML
	private void checkNames(ActionEvent event) {
		// Exception only String?
		boolean canWeStart = false;
		if (!newField.getText().replaceAll(" ","").isEmpty()) {
			gameStart.setDisable(true);
			GameMaster.setNames(1, newField.getText());
			canWeStart = true;
		}
		
		if (canWeStart == true) {
			init_game();
		} else
			errorTxt.setVisible(true);
	}

	/**
	 * Siehe MultiplayerController02 - init_game()	
	 */
	private void init_game() {
		ProgressBar progressBar = new ProgressBar(0);
		progressBar.setPrefSize(400, 40);
		Service<Void> sv = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return IMGhandler.initialize(Start.getJhdl().getModel().getInfo().getCardcount());
			}
		};
		AnimationTimer anitim = new AnimationTimer() {
			@Override
			public void handle(long now) {
				progressBar.setProgress(sv.getProgress());
				if (progressBar.getProgress() == 1) {
					this.stop();
					loader.setLocation(getClass().getResource("/fxml/UIGame/UIGame.fxml"));
					try {
						Parent root = loader.load();
						AnchorPane.getScene().setRoot(root);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		if (popupload == null) {
			popupload = new Stage();
			popupload.setWidth(400);
			popupload.setHeight(100);
			popupload.centerOnScreen();
			popupload.initStyle(StageStyle.UTILITY);
			popupload.initModality(Modality.NONE);
			popupload.initOwner(AnchorPane.getScene().getWindow());
			VBox dialogVbox = new VBox(20);
			dialogVbox.getChildren().add(new Text("Loading your game..."));
			dialogVbox.getChildren().add(progressBar);
			dialogVbox.setAlignment(Pos.CENTER);
			Scene dialogScene = new Scene(dialogVbox, 300, 200);
			popupload.setScene(dialogScene);
			anitim.start();
			sv.start();
			sv.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					popupload.hide();
					anitim.stop();
					sv.reset();
				}
			});
			popupload.showAndWait();
		} else {
			anitim.start();
			sv.start();
			sv.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					popupload.hide();
					anitim.stop();
					sv.reset();
				}
			});
			popupload.showAndWait();
		}
	}
	
	@FXML
	private void back (ActionEvent event) {
		loader.setLocation(getClass().getResource("/fxml/MainMenu/Menu.fxml"));
		try {
			MP3handler.stopbackground();
			Parent root = loader.load();
			AnchorPane.getScene().setRoot(root);
		} catch (IOException e) {
			ExceptionHandler exc = new ExceptionHandler(e, "Error", "Load Error",
					"Something went wrong loading the next screen", "Oops");
			exc.showdialog();
		}
	}

	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		AddTextFields();
	}
}