package de.tud.cs.se.flashcards.model;

import java.util.List;


public interface SelectionListener {
	void selectionsChanged(List<IFlashcardSeriesComponent> l);
}
