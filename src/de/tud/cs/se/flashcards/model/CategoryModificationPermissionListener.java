package de.tud.cs.se.flashcards.model;

public interface CategoryModificationPermissionListener {
	public void setAllowCreation(boolean allow);
	public void setAllowDeletion(boolean allow);
}
