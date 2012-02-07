package de.tud.cs.se.flashcards.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class FlashCategory extends DefaultMutableTreeNode implements IFlashcardSeriesComponent, Serializable { //VON_UNS
	private static final long serialVersionUID = 1L;
	private String name;
	
	public String getName() {
		return name;
	}
	
	public FlashCategory(String string) {
		name = string;
	}

	@Override
	public int numberOfCards() {
		int sum = 0;
		for (int i = 0; i<getChildCount(); i++) {
			IFlashcardSeriesComponent x = (IFlashcardSeriesComponent) getChildAt(i);
			sum += x.numberOfCards();
		}
		return sum;
	}

	@Override
	public float learnProgress() {
		if (getChildCount() == 0) return Float.NaN;
		
		float sum = 0;
		for (int i = 0; i<getChildCount(); i++) {
			IFlashcardSeriesComponent x = (IFlashcardSeriesComponent) getChildAt(i);
			float f = x.learnProgress();
			sum += f;
		}
		return Float.valueOf(sum) / getChildCount();
	}



	@Override
	public void initializeListeners() {
		// TODO initializeListeners
		
	}
	
	@Override
	public String toString() {
		return getName() + " (" + ((int) (learnProgress() * 100.0f)) + "%)";
	}

	@Override
	/*
	 * returns yes when the element should be removed
	 */
	public boolean removeAllExcluding(Set<Flashcard> flashcards) {
		for (int i = 0; i<getChildCount(); i++) {
			IFlashcardSeriesComponent x = (IFlashcardSeriesComponent) getChildAt(i);
			if (x.removeAllExcluding(flashcards))
				super.remove((MutableTreeNode) getChildAt(i));
		}
		
		return false;
	}

	@Override
	public void print(int level) {
		char[] arr = new char[level];
		Arrays.fill(arr, ' ');
		System.err.print(new String(arr));
		
		System.err.println(this);
		for (int i = 0; i<getChildCount(); i++) {
			IFlashcardSeriesComponent x = (IFlashcardSeriesComponent) getChildAt(i);
			x.print(level+1);
		}
	}

	@Override
	public boolean containsFlashcard(Flashcard arg0) {
		for (int i = 0; i<getChildCount(); i++) {
			IFlashcardSeriesComponent x = (IFlashcardSeriesComponent) getChildAt(i);
			if (x.containsFlashcard(arg0)) return true;
		}
		return false;
	}
	
	@Override
	public boolean contains(IFlashcardSeriesComponent component) {
		if (component == this) return true;
		for (int i = 0; i<getChildCount(); i++) {
			IFlashcardSeriesComponent x = (IFlashcardSeriesComponent) getChildAt(i);
			if (x.contains(component)) return true;
		}
		return false;
	}

	@Override
	public FlashCategory getParent() {
		return (FlashCategory) super.getParent();
	}

	public void remove(IFlashcardSeriesComponent cat) {
		super.remove((DefaultMutableTreeNode) cat);
	}

	@Override
	public List<FlashLeaf> getLeaves() {
		List<FlashLeaf> l = new ArrayList<FlashLeaf>();
		for (int i = 0; i<getChildCount(); i++) {
			IFlashcardSeriesComponent x = (IFlashcardSeriesComponent) getChildAt(i);
			l.addAll(x.getLeaves());
		}
		return l;
 	}

	@Override
	public void removeRecursiveLeafContent(Flashcard i2) {
		for (int i = 0; i<getChildCount(); i++) {
			IFlashcardSeriesComponent x = (IFlashcardSeriesComponent) getChildAt(i);
			if (x.getFlashcard() == i2) {
				remove(i);
				//super.remove((MutableTreeNode) getChildAt(i));
				continue;
			}
			x.removeRecursiveLeafContent(i2);
		}
	}
/*
	@Override
	public void add(IFlashcardSeriesComponent l) {
		super.add((MutableTreeNode) l);
	}
*/

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
	public Flashcard getFlashcard() {
		return null;
	}
}
