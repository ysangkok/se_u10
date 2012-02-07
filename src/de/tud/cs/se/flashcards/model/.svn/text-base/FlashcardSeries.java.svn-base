/** License (BSD Style License):
 *  Copyright (c) 2010
 *  Software Engineering
 *  Department of Computer Science
 *  Technische Universität Darmstadt
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  - Neither the name of the Software Engineering Group or Technische 
 *    Universität Darmstadt nor the names of its contributors may be used to 
 *    endorse or promote products derived from this software without specific 
 *    prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package de.tud.cs.se.flashcards.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * A series represents a set of flashcards and basically provides a set of
 * management functions.
 * 
 * @author Michael Eichberg
 */
public class FlashcardSeries implements ListModel {
	// We did deliberately not extend "AbstractListModel" to avoid that Java
	// Serialization of this
	// (then Serializable) class would store references to listeners that do not
	// belong to the model.

	/**
	 * Convenience method to create an initial flashcard series.
	 */
	public static FlashcardSeries createInitialFlashcardSeries() {

		FlashcardSeries flashcards = new FlashcardSeries();
		flashcards.addCard(new Flashcard("lose Kopplung", "loose coupling"));
		flashcards.addCard(new Flashcard("hoher Zusammenhalt", "high cohesion"));
		flashcards.addCard(new Flashcard("Stellvertreter", "Proxy"));
		flashcards.addCard(new Flashcard("Entwurfsmuster", "Design Pattern"));
		flashcards.addCard(new Flashcard("Beispiel", "Example"));
		flashcards.addCard(new Flashcard("Haus", "House"));
		flashcards.addCard(new Flashcard("Hund", "Dog"));

		return flashcards;
	}

	public static final ListDataListener[] NO_LIST_DATA_LISTENERS = new ListDataListener[0];

	// This array is treated as immutable (i.e., its content never changes!)
	private ListDataListener[] listDataListeners = NO_LIST_DATA_LISTENERS;

	private final List<Flashcard> flashcards = new ArrayList<Flashcard>();

	public synchronized void addListDataListener(ListDataListener l) {

		// It is an error to register the same listener twice!

		ListDataListener[] newListDataListeners = new ListDataListener[this.listDataListeners.length + 1];
		System.arraycopy(this.listDataListeners, 0, newListDataListeners, 0,
				this.listDataListeners.length);
		newListDataListeners[this.listDataListeners.length] = l;

		this.listDataListeners = newListDataListeners;
	}

	public synchronized void removeListDataListener(ListDataListener l) {

		// It is an error to try to remove a listener that is not (no longer)
		// registered.

		ListDataListener[] newListDataListeners = new ListDataListener[this.listDataListeners.length - 1];
		int index = 0;
		for (; index < listDataListeners.length; index++) {
			if (listDataListeners[index] == l)
				break;
		}
		System.arraycopy(listDataListeners, 0, newListDataListeners, 0, index);
		if (index < (listDataListeners.length - 1)) {
			System.arraycopy(listDataListeners, index + 1,
					newListDataListeners, index, listDataListeners.length
							- (index + 1));
		}

		this.listDataListeners = newListDataListeners;
	}

	protected ListDataListener[] getListDataListeners() {

		return listDataListeners;
	}

	protected void fireIntervalAdded(Object source, int index0, int index1) {

		if (listDataListeners != NO_LIST_DATA_LISTENERS) {
			ListDataListener[] listeners = listDataListeners;
			ListDataEvent e = new ListDataEvent(source,
					ListDataEvent.INTERVAL_ADDED, index0, index1);

			for (int i = listeners.length - 1; i >= 0; i -= 1) {
				listeners[i].intervalAdded(e);
			}
		}
	}

	protected void fireIntervalRemoved(Object source, int index0, int index1) {

		if (listDataListeners != NO_LIST_DATA_LISTENERS) {
			ListDataListener[] listeners = listDataListeners;
			ListDataEvent e = new ListDataEvent(source,
					ListDataEvent.INTERVAL_REMOVED, index0, index1);

			for (int i = listeners.length - 1; i >= 0; i -= 1) {
				listeners[i].intervalRemoved(e);
			}
		}
	}

	public void addCard(Flashcard flashcard) {

		flashcards.add(0, flashcard);
		fireIntervalAdded(this, 0, 0);
	}

	public void removeCards(int[] indices) {

		int i = indices.length - 1;
		while (i >= 0) {
			int index = indices[i];
			flashcards.remove(index);
			fireIntervalRemoved(this, index, index);
			i -= 1;
		}
	}

	public Flashcard getElementAt(int i) {

		return flashcards.get(i);
	}

	public int getSize() {

		return flashcards.size();
	}

}
