package de.tud.cs.se.flashcards.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;




public class FlashcardTree extends FlashcardSeries implements FlashcardSubscriber {
/*
	private List<RedrawnNotificationSubscriber> redrawnSubs = new ArrayList<RedrawnNotificationSubscriber>();
	
	public void addRedrawnSub(RedrawnNotificationSubscriber sub) {
		redrawnSubs.add(sub);
	}
	
	private void notifyRedrawn(List<Flashcard> f) {
		for (RedrawnNotificationSubscriber sub : redrawnSubs) {
			sub.wasRedrawn(f);
		}
	}
*/	
	private void remakeTree() {
		//treeModel.setRoot(getRoot().getSwingTreeNode());
		treeModel.reload();
		/*
		List<Flashcard> f = new ArrayList<Flashcard>();
		Enumeration<javax.swing.tree.DefaultMutableTreeNode> t = ((javax.swing.tree.DefaultMutableTreeNode) treeModel.getRoot()).depthFirstEnumeration();
		while (t.hasMoreElements()) {
			f.add(((FlashLeaf) t.nextElement().getUserObject()).f);
		}
		notifyRedrawn(f);
		*/
	}
	
	DefaultTreeModel treeModel;
	
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}
	
	public void createTreeModel() {

		

		
		
				treeModel = new DefaultTreeModel(getRoot());
				
				addListDataListener(new ListDataListener() {
					
					@Override
					public void intervalRemoved(ListDataEvent e) {
						remakeTree();
					}
					
					@Override
					public void intervalAdded(ListDataEvent e) {
						remakeTree();
					}
					
					@Override
					public void contentsChanged(ListDataEvent e) {
						//System.err.println("changed");
						if (e.getSource().getClass().equals(SelectionListenerImpl.class)) return;
						remakeTree();
					}
				});
	}
	
	private FlashCategory root;
	private List<IFlashcardSeriesComponent> currentlySelectedTreeItems;
	
	public FlashcardTree() {
		super();
		
		takeSelectionSnapshot();
	}
	
	void takeSelectionSnapshot() {
		currentlySelectedTreeItems = selectionListener.selectedComponents; //selected components is changed to a whole new object on each selection change, therefore we can copy the object reference like this
	}

	public FlashcardTree(List<Flashcard> fl) {
		this();
		flashcards.addAll(fl);
		takeSelectionSnapshot();
	}

	public FlashcardTree(IFlashcardSeriesComponent root2) {
		this();
		setRoot((FlashCategory) root2);
		takeSelectionSnapshot();
	}

	private void setRoot(FlashCategory cat) {
		root = cat;
		takeSelectionSnapshot();
	}

	public static FlashcardTree createInitialFlashcardTree() {
		FlashcardSeries n = FlashcardTree.createInitialFlashcardSeries();
		FlashcardTree m = new FlashcardTree(n.flashcards);
		
		FlashCategory cat = new FlashCategory("Root category");
		m.setRoot(cat);	
		FlashCategory cat1 = new FlashCategory("Cat 1");
		FlashCategory cat2 = new FlashCategory("Cat 2");
		cat.addI(cat1);
		cat.addI(cat2);

		int i = 0;
		cat1.add(new FlashLeaf(m.flashcards.get(i++)));
		cat1.add(new FlashLeaf(m.flashcards.get(i++)));
		cat2.add(new FlashLeaf(m.flashcards.get(i++)));
		cat2.add(new FlashLeaf(m.flashcards.get(i++)));
		for (; i<m.flashcards.size(); i++)
			cat.add(new FlashLeaf(m.flashcards.get(i)));
		
		m.takeSelectionSnapshot();
		
		for (IFlashcardSeriesComponent x : m.getRoot().getLeaves()) {
			final FlashLeaf l = (FlashLeaf) x;
			l.getFlashcard().addSubscriber(m);
		}
		
		return m;
	}
	/*
	private void categorizeInRoot() {
		FlashCategory cat = new FlashCategory("Root category");
		setRoot(cat);
		for (int i = 0; i<getSize(); i++) {
			Flashcard c = getElementAt(i);
			
			cat.add(new FlashLeaf(c));
		}
	}
	*/

	public FlashCategory getRoot() {
		return root;
	}
	


	public void deleteSelectedCategories() {
		// updateCategoryModificationPermissionListeners ensures only categories
		for (IFlashcardSeriesComponent x : selectionListener.selectedComponents) {
			FlashCategory cat = (FlashCategory) x;
			/*
			if (cat.getParent() == null) {
				root = new FlashCategory(root.getName());
				return;
			}
			*/
			List<Flashcard> copy = new ArrayList<Flashcard>(flashcards);
			for (Flashcard f : copy) {
				if (cat.containsFlashcard(f)) {
					flashcards.remove(f);
				}
			}
			cat.getParent().removeI(cat);
			
			if (selectRootAction != null) selectRootAction.selectRoot();
			
		}
		fireIntervalChanged(this,-1,-1);
	}

	public void createCategory(String name) {
		// gets called only when one category is selected
		
		FlashCategory cat = (
											selectionListener.selectedComponents.size() == 0
										?	root
										:	(FlashCategory) selectionListener.selectedComponents.iterator().next()
		);
		
		cat.add(new FlashCategory(name));
		
		fireIntervalChanged(this,-1,-1);
	}
	
	@Override
	public Flashcard getElementAt(int i) {
		return combine(currentlySelectedTreeItems).get(i).getFlashcard();
	};
	
	@Override
	public int getSize() {
		try {
			return combine(currentlySelectedTreeItems).size();
		} catch (NullPointerException e) {
			//if (currentDirectory.containsAll(Collections.list(root.children()))) throw new RuntimeException(e);
			currentlySelectedTreeItems = new ArrayList<IFlashcardSeriesComponent>();
			currentlySelectedTreeItems.addAll(Collections.list(root.children()));
			return getSize();
		}
	}

	@Override
	public void addCard(Flashcard flashcard) {
		flashcards.add(flashcard);
		
		selectionListener.selectedComponents.iterator().next().addI(new FlashLeaf(flashcard));
		//System.err.println("added"); System.err.println(selectionListener.selectedComponents.get(0));
		
		flashcard.addSubscriber(this);
		
		takeSelectionSnapshot();
		
		fireIntervalChanged(this, 0, combine(currentlySelectedTreeItems).size()-1);
		//fireIntervalAdded(this, getPositionOfFlashcardInCurrentView(flashcard), getPositionOfFlashcardInCurrentView(flashcard));
	}

	private static List<FlashLeaf> combine(
			List<IFlashcardSeriesComponent> currentDirectory2) {
		
		
		List<FlashLeaf> f = new ArrayList<FlashLeaf>();
		
		for (IFlashcardSeriesComponent x: currentDirectory2) {
			f.addAll(x.getLeaves());
		}
		
		return f;
	}

	@Override
	public void removeCards(int[] indices) {
		List<Flashcard> f = new ArrayList<Flashcard>();
		for (int i : indices) {
			f.add(combine(currentlySelectedTreeItems).get(i).getFlashcard());
		}
		for (Flashcard c : f) {
			root.removeRecursiveLeafContent(c);
		}
		for (int i: indices) fireIntervalRemoved(this, i, i);
	}
	
	
// CATEGORY MODIFICATION PERMISSION INTERFACE

	private List<CategoryModificationPermissionListener> catmodperlisteners = new ArrayList<CategoryModificationPermissionListener>();
	
	public void addCategoryModificationPermissionListener(CategoryModificationPermissionListener l) {
		catmodperlisteners.add(l);
	}

	void updateCategoryModificationPermissionListeners() {
		boolean allowCreate;
		boolean allowDelete;
		
		allowCreate = selectionListener.selectedComponents.size() == 1 && selectionListener.selectedComponents.iterator().next() instanceof FlashCategory;
		allowDelete = true;
		for (IFlashcardSeriesComponent x: selectionListener.selectedComponents) {
			if (!x.getAllowsChildren() || x == root) {
				allowDelete = false;
				break;
			}
		}
		
		for (CategoryModificationPermissionListener x : catmodperlisteners) {
			x.setAllowCreation(allowCreate);
			x.setAllowDeletion(allowDelete);
		}
	}

// SELECTION
	
	private SelectRootActionExecutor selectRootAction;
	
	private SelectionListenerImpl selectionListener = new SelectionListenerImpl(this);

	protected  List<Flashcard> getFlashcards() {
		return flashcards;
	}
	
	public SelectionListener getSelectionListener() {
		return selectionListener;
	}

	public void setSelectRootAction(SelectRootActionExecutor runnable) {
		selectRootAction = runnable;
	}

	public void updateFlashcardTreeEntry(Flashcard elementAt) {
//		remakeTree(); return;
	
		Enumeration<DefaultMutableTreeNode> e = ((DefaultMutableTreeNode) treeModel.getRoot()).depthFirstEnumeration();
	    while (e.hasMoreElements()) {
	    	DefaultMutableTreeNode node = e.nextElement();
	    	IFlashcardSeriesComponent l = (IFlashcardSeriesComponent) (node);
	    	if (l.getFlashcard() == elementAt) {
	    		//MutableTreeNode newnode = l.getSwingTreeNode();
	    		//node.invalidate();
	    		//treeModel.nodeChanged(node);
	    		treeModel.reload(node);
	    		return;
	    	}
	    }
	    throw new RuntimeException("couldn't find node to update in tree");
	}
	
}

class SelectionListenerImpl implements SelectionListener {
	List<IFlashcardSeriesComponent> selectedComponents;
	List<Flashcard> selectedCards;
	private FlashcardTree tree;

	public SelectionListenerImpl(FlashcardTree tree) {
		this.tree = tree;
	}
	
	@Override
	public void selectionsChanged(List<IFlashcardSeriesComponent> l) {
		selectedComponents = l;
		selectedCards = new ArrayList<Flashcard>();

		for (Flashcard c : tree.getFlashcards()) {
			for (IFlashcardSeriesComponent j : selectedComponents) {
				if (j == null) continue;
				if (j.containsFlashcard(c)) {
					selectedCards.add(c);
					break;
				}
			}
		}

		tree.updateCategoryModificationPermissionListeners();
		
		tree.takeSelectionSnapshot();

		tree.fireIntervalChanged(this, -1, -1);
	}
}


