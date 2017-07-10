package game;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * @author D067928
 * Die Kartenklasse welche die Rectangleform aus JavaFX erweitert. Dies ist sowohl die Anzeige als auch die Backendrepr�sentation
 * der Karten, welche im Spiel umgedreht werden k�nnen
 */
public class Card extends Rectangle {
	private boolean turned;
	private boolean matched;
	private boolean animationlock = false;
	private int card_id;

	/**
	 * Erstellt eine Karte zusammen mit einer weite und h�he sowie einem Offset
	 * @param x offset x-Koordinate
	 * @param y offset y-Koordinate
	 * @param width 
	 * @param height
	 */
	public Card(double x, double y, double width, double height) {
		super(x, y, width, height);
	}

	
	/**
	 * F�llt die Karte mit einem Bild welches zu einem ImagePattern umkonvertiert wird.
	 * 
	 * @param img - Das Image das im Rectangle angezeigt werden soll.
	 * 
	 * WARNUNG: Set fill ist eine recht imperformante Funktion und sollte wenn m�glich nicht in einer Gameloop aufgerufen werden.
	 * Gameloop bezeichnet hier einen aufruf der Methode pro angezeigtem Frame (im JavaFX Umfeld)
	 */
	public void fillCard(Image img) {
		this.setFill(new ImagePattern(img));
	}

	/**
	 * @return card_id - Die Karten ID - wichtig f�r das angezeigte Bild
	 */
	public int getCard_Id() {
		return card_id;
	}

	
	/**
	 * @return matched - Ist die Karte bereits mit einer anderen gematched worden - Dann ist sie nicht klickbar / irrelevant f�rs 
	 * Spiel
	 */
	public boolean isMatched() {
		return matched;
	}

	/**
	 * @return turned - Ist die Karte umgedreht? Dann muss die Karte im Spiel anders behandelt werden. N�heres im Gameeventhandler
	 */
	public boolean isTurned() {
		return turned;
	}

	/**
	 * @param card_id - Setzt die Karten ID - wichtig f�r das angezeigte Bild
	 */
	public void setCard_Id(int card_id) {
		this.card_id = card_id;
	}

	/**
	 * @param matched - Setzt die Karte auf matched oder nicht matched - auf True zu setzen, falls die Karte mit seinem �quivalent
	 * gepaart wurde
	 */
	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	/**
	 * @param turned - Setzt die Karte auf turned oder nicht turned - auf True zu setzen, falls die Karte als umgedreht (sichtbar)
	 * angezeigt werden soll
	 */
	public void setTurned(boolean turned) {
		this.turned = turned;
	}

	/**
	 * @return animationlock - Zeigt an ob die Karte momentan in einer Animation "gelocked" / "gefangen" ist
	 */
	public boolean inAnimation() {
		return animationlock;
	}

	
	/**
	 *  Locked die Karte. Wird aufgerufen wenn die Karte in einer Animation ist.
	 */
	public void lock() {
		this.animationlock = true;
	}

	/**
	 * Unlocked die Karte. Wird aufgerufen sobald die Karte nicht mehr in einer Animation ist.
	 */
	public void unlock() {
		this.animationlock = false;
	}
}
