package game;

import javafx.animation.Interpolator;
import javafx.animation.Transition;

/**
 * @author D067928
 *	Der TransitionRun ist eine Runnable-Implementierung welche es erm�glicht einen Interpolator zur Animation hinzuzuf�gen.
 *	Der Interpolator erlaubt fl�ssige Animation durch "Abrunden" der Transitionsverh�ltnisse der Koordinaten. F�r n�heres siehe
 *	Dokumentation der Methode setInterpolator();
 */
public class TransitionRun implements Runnable {
	private Transition anim;

	public Transition getAnim() {
		return anim;
	}

	@Override
	public void run() {
		//Dies setzt den Interpolator um Anfang und Ende der Animation abzurunden, sodass kein pl�tzlicher Start/End entsteht.
		anim.setInterpolator(Interpolator.EASE_BOTH);
		anim.play();
	}

	/**
	 * @param anim - die Transition / Animation auf die die Interpolation angewendet werden soll.
	 */
	public void setAnim(Transition anim) {
		this.anim = anim;
	}

}
