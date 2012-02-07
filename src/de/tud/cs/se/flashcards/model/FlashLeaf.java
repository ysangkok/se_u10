package de.tud.cs.se.flashcards.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class FlashLeaf extends  DefaultMutableTreeNode implements IFlashcardSeriesComponent { //VON_UNS
	private static final long serialVersionUID = 1L;
	
	private Flashcard f;
	private FlashCategory parent;
	
	public Flashcard getFlashcard() { return f; }
	
	public String getName() {
		return f.getQuestion();
	}
	
	FlashLeaf(Flashcard f) {
		this.f = f;
		setAllowsChildren(false);
	}
	
	public void setParent(FlashCategory parent) {
		this.parent = parent;
	}
	
	@Override
	public int numberOfCards() {
		return 1;
	}

	@Override
	public float learnProgress() {
		return f.getProgress();
	}

	@Override
	public boolean contains(IFlashcardSeriesComponent component) {
		return this == component; 
	}

	@Override
	public void initializeListeners() {
		// TODO initializeListeners
	}
/*
	@Override
	public void add(IFlashcardSeriesComponent l) {
		throw new UnsupportedOperationException("Can't add to leaf!");
	}
*/
	
	@Override
	public String toString() {
		return getName() + " (" + ((int) (learnProgress() * 100.0f)) + "%)";
	}

	@Override
	// returns yes when the element should be removed
	public boolean removeAllExcluding(Set<Flashcard> flashcards) {
		return !flashcards.contains(f);
	}

	@Override
	public void print(int level) {
		char[] arr = new char[level];
		Arrays.fill(arr, ' ');
		System.err.print(new String(arr));
		
		System.err.println(this);
	}
	
	@Override
	public boolean containsFlashcard(Flashcard arg0) {
		return arg0 == f;
	}

	@Override
	public FlashCategory getParent() {
		return parent;
	}
/*
	@Override
	public void remove(IFlashcardSeriesComponent cat) {
		throw new UnsupportedOperationException("Can't remove from leaf");
	}
*/
	@Override
	public Collection<FlashLeaf> getLeaves() {
		return Arrays.asList(new FlashLeaf[] {this});
	}

	@Override
	public void removeRecursiveLeafContent(Flashcard i) {
		return;
	}

	@Override
	public void addI(IFlashcardSeriesComponent draggedComponent) {
		super.add((MutableTreeNode) draggedComponent);
	}

	@Override
	public void removeI(IFlashcardSeriesComponent draggedComponent) {
		super.remove((MutableTreeNode) draggedComponent);
	}
/*
	@Override
	public int compareTo(Object o) {
		return o.toString().compareTo(getName());
		//return ((Integer) getParent().getIndex(this)).compareTo(((DefaultMutableTreeNode) o).getParent().getIndex((TreeNode) o));
	}
	*/
}
