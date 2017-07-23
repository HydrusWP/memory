package game;

import java.util.ArrayList;
import java.util.Comparator;

import image.IMGhandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import start_MEMORY.Start;

/**
 * @author D067928
 *	Das WinStack Objekt ist daf�r verantwortlich, die Siegerdarstellung zu �bernehmen, falls das Spiel zu Ende ist. Es nimmt
 *	eine Sortierung der Spieler nach 1. Highscore, 2. Versuchen, 3. gebrauchter Zeit vor und bestimmt so den Sieger. Die Kartenpaare
 *	werden �ber farbig addierte Balken dargestellt. Extended eine HBox (Horizontal Box), weil es die Spieler horizontal anordnen soll.
 */
public class WinStack extends HBox {
	/**
	 * Initialisiert den WinStack. Startparameter sowie Comparator sollen
	 * gesetzt werden
	 * 
	 * @param arrayList
	 *            - Die Spielerliste die f�r den WinScreen verarbeitet werden
	 *            soll.
	 */
	public void initialize(ArrayList<Player> arrayList) {
		// Setzt Startwerte f�r eine FullHD Aufl�sung
		this.setPrefWidth(600);
		this.setPrefHeight(500);
		this.setSpacing(20);
		this.setLayoutX(650);
		this.setLayoutY(325);
		this.setAlignment(Pos.TOP_CENTER);

		// Hier wird eine Anonyme Klasse verbaut, da der Comparator sonst nirgends gebraucht wird.
		// Zuerst wird nach Highscore, dann nach Versuchen und dann nach Mintime Sortiert.
		// Der h�here Highscore zuerst, weniger Versuche zuerst, weniger Zeit zuerst.
		arrayList.sort(new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				if (o1.getHighscore() > o2.getHighscore()) {
					return -1;
				} else if (o1.getHighscore() < o2.getHighscore()) {
					return 1;
				} else {
					if (o1.getAttempts() < o2.getAttempts()) {
						return -1;
					} else if (o1.getAttempts() > o2.getAttempts()) {
						return 1;
					} else {
						if (o1.getMintime() > o2.getMintime()) {
							return -1;
						} else if (o1.getMintime() < o2.getMintime()) {
							return 1;
						}
						return 0;
					}
				}
			}
		});
		
		//erstellt einen Index um die Spieler abzuarbeiten
		int idx = 0;
		for (Player player : arrayList) {
			// f�r jeden Spieler wird der Index inkrementiert, alternativ w�re hier auch eine For-schleife verwednbar.
			idx++;
			// Eine neue VBox (Vertical Box) wird erstellt um die Informationen pro Spieler untereinander darzustellen
			VBox highscore = new VBox();
			highscore.setSpacing(10);
			this.getChildren().add(highscore);
			//Zun�chst wird die Winnumber (1., 2., 3. oder 4. Platz) hinzugef�gt
			highscore.getChildren().add(new ImageView(IMGhandler.getWinNumber(idx)));
			highscore.setAlignment(Pos.TOP_CENTER);
			// Nun wird der Spielername hinzugef�gt
			Label l = new Label(player.getName());
			l.setTextFill(Color.WHITE);
			l.setFont(new Font("Arial", 30));
			highscore.getChildren().add(l);
			
			//In dieser Schleife wird f�r jedes erzielte Paar ein Balken hinzugef�gt
			//Die Balken sind je nach Schwierigkeitsstufe unterschieldich gro�.
			for (int i = 0; i < player.getHighscore(); i++) {
				Rectangle scorerec = new Rectangle(this.getPrefWidth() / Start.getGamemode(),
						25 * 1 / Start.getJhdl().getModel().getInfo().getDifficulty());
				scorerec.setFill(new ImagePattern(IMGhandler.getStack()));
				highscore.getChildren().add(scorerec);
				scorerec.toFront();
			}
			//Nun werden noch die gebrauchten Versuche sowie die ben�tigte Zeit hinzugeschrieben.
			Label l2 = new Label(Double.toString(Math.floor(player.getMintime() * 100) / 100) + " Sekunden");
			l2.setTextFill(Color.WHITE);
			l2.setFont(new Font("Arial", 30));
			highscore.getChildren()
					.add(l2);
			Label l3 = new Label(Integer.toString(player.getAttempts()) + " Versuche");
			l3.setTextFill(Color.WHITE);
			l3.setFont(new Font("Arial", 30));
			highscore.getChildren().add(l2);
		}
	}
}
