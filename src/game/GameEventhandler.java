package game;

import image.IMGhandler;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.util.Duration;
import sound.MP3handler;
import start_MEMORY.Start;

/**
 * @author D067928
 *	Der GameEventhandler ist daf�r verantwortlich, ein "Event" im Spiel entsprechend zu behandeln. Dazu geh�ren in erster Linie
 *	die Kartenanimationen, das hei�t wenn Karten vom User/Spieler umgedreht bzw. gepaart werden. Entsprechend dazu ist er
 *	verkn�pft mit der Backendlogik im GameMaster.
 */
public class GameEventhandler {

	private static Point3D axis = new Point3D(5, 5, 0);
	private static Card c1;
	private static Card c2;
	
	/**
	 * Die Methode cardturn, aufgerufen vom BoardPane im OnClickHandler f�r jede Karte, ist f�r die Pr�fung der Situation der Karte
	 * zust�ndig. Dabei gibt es 2 Varianten f�r Zustands�nderungen:
	 *  1. Es ist die erste ausgew�hlte Karte - Dann wird der Buffer des GameEventhandlers beschrieben und c umgedreht.
	 *  2. Die Karte ist die 2. umgedrehte Karte
	 *  2.1 Die Karte ist identisch mit der gepufferten -> Match
	 *  2.2 Die Karte ist nicht identisch -> Zur�cksetzen
	 * @param c - Die mitgegebene Karteninstanz, welche aus dem Handleraufruf mitgegeben wird.
	 */
	public static void cardturn(Card c) {
		Transition animation = null;
		/* Die Karte c wird entweder in puffer c1 oder in puffer c2 geschrieben. Hierbei wird die Karteninstanz gelocked (siehe
		 * Animationlock).
		 * Im Spieler p (der Spieler an der Reihe) wird nun der interne Timer gestartet. Die Runde beginnt.  */
		if (c1 == null) {
			c1 = c;
			c1.lock();
			animation = flipCard(c1, 0);
			Player p = GameMaster.getPlayerInTurn();
			p.setSavedtime(p.getSavedtime() + p.getTimer().getCurrent());
			p.start();
			c.setTurned(true);
		} else if (c2 == null) {
			c2 = c;
			c2.lock();
		}
		// Check ob beide Pufferkarten gef�llt sind => Logik wird nur ausgef�hrt wenn die 2. Karte ausgew�hlt wurde
		if (c1 != null && c2 != null) {
			// Der Versuchcounter wird inkrementiert (Spieler an der Reihe)
			GameMaster.getPlayerInTurn().setAttempts(GameMaster.getPlayerInTurn().getAttempts() + 1);
			/* Falls die Karten-ID's �bereinstimmen wird die Runde durchlaufen (GameMaster.doTurn(true)), wobei ein Score erzielt 
			 * wurde (Parameter auf true gesetzt). Es wird die Animation flipGrey(c1, c2) ausgef�hrt,
			 * um die beiden Karten auszugrauen. Au�erdem werden beide Karten gematched und der Buffer wird geleert.
			 * 
			 * Falls die Karten-ID's nicht �bereinstimmen, wird die Runde ohne Score durchlaufen (GameMaster.doTurn(false)), 
			 * und die Karten werden zur�ckgedreht (flipBack(c1, c2)). Gleichzeitig werden sie als nicht umgedreht markiert.
			 * Der Buffer wird geleert*/
			if (c1.getCard_Id() == c2.getCard_Id()) {
				GameMaster.doTurn(true);
				animation = flipGrey(c1, c2);
				match(c1, c2);

				c1 = null;
				c2 = null;
			} else {

				GameMaster.doTurn(false);
				if (Start.getGamemode() > 1) {
				}
				animation = flipBack(c1, c2);
				
				c1.setTurned(false);
				c2.setTurned(false);

				c1 = null;
				c2 = null;
			}
		}
		
		// Hier wird die ausgew�hlte Animation zusammen mit einem Klicksound abgespielt.
		animation.play();
		MP3handler.play(1);
	}

	// Ab hier beginnen die Teillogiken des Animationshandlings.
	
	
	/**
	 * Erstellt und gibt eine Fadein Animation zur�ck (Node n: unsichtbar->sichtbar).
	 * 
	 * @param n - Eine zu animierende Node (Knoten: im JavaFX Kontext jedes Objekt, welches auf dem User Interface gehandhabt
	 * werden kann.)
	 * @return - Eine Transition (Bewegungsanimation) mit Fadein als Resultat.
	 */
	public static Transition fadein(Node n) {
		TransitionRun TR = new TransitionRun();
		FadeTransition ft = new FadeTransition(Duration.millis(4000), n);
		ft.setFromValue(0); //Ausgangssichtbarkeitswert (Opacity) - 0 ist nicht sichtbar
		ft.setToValue(1); //Endsichtbarkeitswert (Opacity) - 1.0 ist 100% sichtbar
		TR.setAnim(ft); //Assoziiert den Transitionrun mit der Animation/Transition - so werden die Eigenschaften des TR �bertragen.
		TR.run(); //startet die Animation
		return ft;
	}

	/**
	 * Erstellt und gibt eine Fadeout Animation zur�ck (Node n: sichtbar->unsichtbar).
	 * 
	 * @param n - Eine zu animierende Node (Knoten: im JavaFX Kontext jedes Objekt, welches auf dem User Interface gehandhabt
	 * werden kann.)
	 * @return - Eine Transition (Bewegungsanimation) mit Fadein als Resultat.
	 */
	public static Transition fadeout(Node n) {
		TransitionRun TR = new TransitionRun();
		FadeTransition ft = new FadeTransition(Duration.millis(2000), n);
		ft.setFromValue(1); //Ausgangssichtbarkeitswert (Opacity) - 1.0 ist 100% sichtbar
		ft.setToValue(0.4); //Endsichtbarkeitswert - 0.4 ist 40% sichtbar, erscheint leicht grau transparent.
		TR.setAnim(ft); //Assoziiert den Transitionrun mit der Animation/Transition - so werden die Eigenschaften des TR �bertragen.
		TR.run(); //startet die Animation
		return ft;
	}

	/**
	 * Dreht die beiden Karten c1 und c2 zur�ck, falls kein Paar erzielt wurde.
	 * @param c1 - Erste Karte
	 * @param c2 - Zweite Karte
	 * @return flipBack - Animation als Objekt
	 */
	static Transition flipBack(Card c1, Card c2) {
		//locked die Karten
		c1.lock();
		c2.lock();
		
		//Dreht die karte c1 um den Winkel von 90 Grad um die Achse 5,5,0 (xyz-codiert) mit einer Dauer von 0.2 Sekunden.
		RotateTransition c1turnAnimation = new RotateTransition(Duration.seconds(0.2), c1);
		c1turnAnimation.setToAngle(90);
		c1turnAnimation.setAxis(axis);

		//Dreht die karte c1 auf den Winkel 0 Grad um die Achse 5,5,0 (xyz-codiert) zur�ck.Dauer von 0.2 Sekunden.
		RotateTransition c1turnBackAnimation = new RotateTransition(Duration.seconds(0.2), c1);
		c1turnBackAnimation.setToAngle(0);
		c1turnBackAnimation.setAxis(axis);

		/*Falls die Drehanimation im Winkel 90 Grad angelangt ist, wird das Bild ge�ndert (IMGhandler) und es wird die
		Animation zum zur�ckdrehen der Karte abgespielt.*/ 
		c1turnAnimation.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				c1.setFill(IMGhandler.getImage_card(0));
				c1turnBackAnimation.play();
			}
		});

		/* Falls die Animation zum Zur�ckdrehen abgeschlossen ist wird die Karte wieder unlocked, also zug�nglich f�r
		 * Nutzerinteraktion. */
		c1turnBackAnimation.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				c1.unlock();
			}
		});


		//Dreht die karte c2 um den Winkel von 90 Grad um die Achse 5,5,0 (xyz-codiert) mit einer Dauer von 0.2 Sekunden.
		RotateTransition c2turnAnimation = new RotateTransition(Duration.seconds(0.2), c2);
		c2turnAnimation.setToAngle(90);
		c2turnAnimation.setAxis(axis);

		//Dreht die karte c2 auf den Winkel 0 Grad um die Achse 5,5,0 (xyz-codiert) zur�ck.Dauer von 0.2 Sekunden.
		RotateTransition c2turnBackAnimation = new RotateTransition(Duration.seconds(0.2), c2);
		c2turnBackAnimation.setToAngle(0);
		c2turnBackAnimation.setAxis(axis);

		/*Falls die Drehanimation im Winkel 90 Grad angelangt ist, wird das Bild ge�ndert (IMGhandler) und es wird die
		Animation zum zur�ckdrehen der Karte abgespielt.*/ 
		c2turnAnimation.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				c2.setFill(IMGhandler.getImage_card(0));
				c2turnBackAnimation.play();
			}
		});
		
		/* Falls die Animation zum Zur�ckdrehen abgeschlossen ist wird die Karte wieder unlocked, also zug�nglich f�r
		 * Nutzerinteraktion. */
		c2turnBackAnimation.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				c2.unlock();
			}
		});

		/* Die zwei Animationen werden nun in einer Parallelanimation zusammengefasst. Diese wurden zuvor separat erstellt, weil
		 * die Animationen auf 2 verschiedene Objekte (c1, c2) zugreifen m�ssen und dies von JavaFX nicht nativ unterst�tzt wird.*/
		ParallelTransition parallel = new ParallelTransition(c1turnAnimation, c2turnAnimation);
		
		/* Die Animation zum Zur�ckdrehen von 2 Karten setzt voraus, dass beide Karten im umgedrehten Zustand sind. Ist dies
		 * nicht der Fall w�rde die nach oben zeigende Karte nach unten gedreht und umgekehrt. Deshalb wird mit einer Sequenzanimation vorher
		 * die 2. Karte zun�chst umgedreht bevor sie zur�ckgedreht wird. Die Reihenfolge der eingegeben Karten ist deswegen wichtig. C1 ist
		 * dabei immer die zuerst umgedrehte Karte, C2 die zweite. So wird flipCard auf C2 zuerst aufgerufen und dann flipBack
		 * auf C1 und C2.  */
		SequentialTransition Seq;
		
		// Mit dieser Abfrage wird erreicht, dass sich Karten in gezoomtem Status korrekt �bereinander legen.
		if (c1.getParent().getChildrenUnmodifiable().indexOf(c1) == c2.getParent().getChildrenUnmodifiable().indexOf(c2)
				- 1) {
			Seq = new SequentialTransition(flipCard(c2, 1), parallel);
		} else if (c1.getParent().getChildrenUnmodifiable().indexOf(c1) - 1 == c2.getParent().getChildrenUnmodifiable()
				.indexOf(c2)) {
			Seq = new SequentialTransition(flipCard(c2, -1), parallel);
		} else {
			Seq = new SequentialTransition(flipCard(c2, 0), parallel);
		}
		
		//die fertig behandelte Animation wird zur�ckgegeben.
		return Seq;
	}

	/**
	 * Dreht eine Karte mit einem jeweiligen Positionsoffset um. 
	 * @param c - Karte c die umgedreht werden soll.
	 * @param pos - Positionsoffset f�r Clipping (Clipping beschreibt hier �berschneiden von Nodes)
	 * @return die Umdrehanimation als Objekt
	 */
	static Transition flipCard(Card c, int pos) {
		//Definiert eine Aufskalierung auf 160%
		ScaleTransition ScaleUp = new ScaleTransition(Duration.seconds(0.2), c);
		ScaleUp.setByX(0.6);
		ScaleUp.setByY(0.6);

		//Definiert eine Runterskalierung auf 100%
		ScaleTransition ScaleDown = new ScaleTransition(Duration.seconds(0.2), c);
		ScaleDown.setByX(-0.6);
		ScaleDown.setByY(-0.6);

		//Definiert eine animationslose Zeit (Pause)
		PauseTransition pause = new PauseTransition(Duration.seconds(0.75));

		//Definiert eine Bewegungsanimation (Vor/Zur�ck) die einen Pseudo 3D Effekt erwirkt, in dem Tiefenwirkung im Auge entsteht.
		TranslateTransition moveAnimation = new TranslateTransition(Duration.seconds(0.2), c);
		moveAnimation.setToY(-100);
		TranslateTransition moveBackAnimation = new TranslateTransition(Duration.seconds(0.2), c);
		moveBackAnimation.setToY(0);
		
		/* Hier wird die Karte mit unterschiedlichem pos aufgerufen um Clippingfehler zu vermeiden. So kann gew�hrleistet werden,
		 * dass trotz FlowPane Layout (Node's die zuerst eingef�gt werden befinden sich logisch auch immer vor sp�teren Nodes)
		 * eine Ver�nderung der Z-Position eines Nodes sichtbar gemacht wird. (So kann eine sp�ter eingef�gte Node auch �ber
		 * einer fr�heren Node angezeigt werden) */
		if (pos < 0) {
			moveAnimation.setToZ(0);
			moveBackAnimation.setToZ(0);
		} else if (pos > 0) {
			moveAnimation.setToZ(-100);
			moveBackAnimation.setToZ(0);
		}

		//Setzt eine neue Sequenz auf, die die definierten Animationen hintereinander abspielen l�sst.
		SequentialTransition zoomSeq = new SequentialTransition(ScaleUp, pause, ScaleDown, moveBackAnimation);
		
		//setzt einen linearen Interpolator um Performanceprobleme zu beheben, die bei vielen Animationen auf einmal entstehen.
		zoomSeq.setInterpolator(Interpolator.LINEAR);

		//Dreht die karte c um den Winkel von 90 Grad um die Achse 5,5,0 (xyz-codiert) mit einer Dauer von 0.2 Sekunden.
		RotateTransition turnAnimation = new RotateTransition(Duration.seconds(0.2), c);
		turnAnimation.setToAngle(90);
		turnAnimation.setAxis(axis);

		//Dreht die karte c auf den Winkel 0 Grad um die Achse 5,5,0 (xyz-codiert) zur�ck.Dauer von 0.2 Sekunden.
		RotateTransition turnBackAnimation = new RotateTransition(Duration.seconds(0.2), c);
		turnBackAnimation.setToAngle(0);
		turnBackAnimation.setAxis(axis);

		/*Falls die Drehanimation im Winkel 90 Grad angelangt ist, wird das Bild ge�ndert (IMGhandler) und es wird die
		Animation zum zur�ckdrehen der Karte abgespielt.*/ 
		turnAnimation.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				c.setFill(IMGhandler.getImage_card(c.getCard_Id()));
				turnBackAnimation.play();
			}
		});

		//setzt einen linearen Interpolator um Performanceprobleme zu beheben, die bei vielen Animationen auf einmal entstehen.
		turnAnimation.setInterpolator(Interpolator.LINEAR);
		// Setzt eine Parallelanimation um sowohl das Drehen, das Zoomen, als auch die Pseudo3D Bewegung in einem abzuspielen
		ParallelTransition turn = new ParallelTransition(turnAnimation, zoomSeq, moveAnimation);
		return turn;
	}

	/**
	 * Graut die Karten aus
	 * @param c1 - Erste Karte
	 * @param c2 - Zweite Karte
	 * @return - Ausgrauanimation als Objekt
	 */
	static Transition flipGrey(Card c1, Card c2) {
		//lockt die Karten
		c1.lock();
		c2.lock();
		
		//setzt einen neuen TransitionRun auf um das Ausgrauen fl�ssiger zu gestalten.
		TransitionRun TR = new TransitionRun();

		//Dreht die karte c1 um den Winkel von 90 Grad um die Achse 5,5,0 (xyz-codiert) mit einer Dauer von 0.2 Sekunden.
		RotateTransition c1turnAnimation = new RotateTransition(Duration.seconds(0.2), c1);
		c1turnAnimation.setToAngle(90);
		c1turnAnimation.setAxis(axis);


		//Dreht die karte c1 auf den Winkel 0 Grad um die Achse 5,5,0 (xyz-codiert) zur�ck.Dauer von 0.2 Sekunden.
		RotateTransition c1turnBackAnimation = new RotateTransition(Duration.seconds(0.2), c1);
		c1turnBackAnimation.setToAngle(0);
		c1turnBackAnimation.setAxis(axis);


		/*Falls die Drehanimation im Winkel 90 Grad angelangt ist, wird die Karte c2 ausgegraut.*/ 
		c1turnAnimation.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				fadeout(c1);
				TR.setAnim(c1turnBackAnimation);
				TR.run();
			}
		});

		//Dreht die karte c2 um den Winkel von 90 Grad um die Achse 5,5,0 (xyz-codiert) mit einer Dauer von 0.2 Sekunden.
		RotateTransition c2turnAnimation = new RotateTransition(Duration.seconds(0.2), c2);
		c2turnAnimation.setToAngle(90);
		c2turnAnimation.setAxis(axis);

		//Dreht die karte c2 auf den Winkel 0 Grad um die Achse 5,5,0 (xyz-codiert) zur�ck.Dauer von 0.2 Sekunden.
		RotateTransition c2turnBackAnimation = new RotateTransition(Duration.seconds(0.2), c2);
		c2turnBackAnimation.setToAngle(0);
		c2turnBackAnimation.setAxis(axis);


		/*Falls die Drehanimation im Winkel 90 Grad angelangt ist, wird die Karte c2 ausgegraut.*/ 
		c2turnAnimation.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				fadeout(c2);
				TR.setAnim(c2turnBackAnimation);
				TR.run();
			}
		});
		
		// Hier wird eine Flipcard Animation in eine Sequenzanimation "verpackt" um 2 Finishhandler gleichzeitig ausf�hren zu k�nnen.
		SequentialTransition Seq = new SequentialTransition(flipCard(c2, 1));

		Seq.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				fadeout(c2).play();
				fadeout(c1).play();
			}
		});
		
		return Seq;
	}

	/**
	 * Match beide Karten.
	 * @param c1 - Erste Karte
	 * @param c2 - Zweite Karte
	 */
	static void match(Card c1, Card c2) {
		c1.setMatched(true);
		c2.setMatched(true);
	}

}
