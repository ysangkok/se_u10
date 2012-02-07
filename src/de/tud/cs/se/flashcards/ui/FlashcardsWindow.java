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
package de.tud.cs.se.flashcards.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.tud.cs.se.flashcards.model.CategoryModificationPermissionListener;
import de.tud.cs.se.flashcards.model.FlashCategory;
import de.tud.cs.se.flashcards.model.Flashcard;
import de.tud.cs.se.flashcards.model.FlashcardTree;
import de.tud.cs.se.flashcards.model.IFlashcardSeriesComponent;
import de.tud.cs.se.flashcards.model.SelectRootActionExecutor;
import de.tud.cs.se.flashcards.model.SelectionListener;
import de.tud.cs.se.flashcards.persistence.Store;

/**
 * A Frame is always associated with exactly one document and it is the parent
 * of all related dialogs etc.
 * 
 * @author Michael Eichberg
 * @author Ralf Mitschke
 */
public class FlashcardsWindow implements SelectRootActionExecutor {

	// The UI components:

	private JFrame frame;

	private JMenuBar menuBar;

	private JMenu fileMenu;

	private JMenuItem newFileMenuItem;

	private JMenuItem openFileMenuItem;

	private  JMenuItem saveFileMenuItem;

	private  JMenuItem saveAsFileMenuItem;

	private  JMenuItem closeFileMenuItem;

	private  JToolBar toolbar;

	private  JButton addButton;

	private  JButton removeButton;

	private  JButton editButton;

	private  JButton playButton;

	private  JScrollPane listScrollPane;

	private  JList list;

	private  FlashcardEditor flashcardEditor;

	private  LearnDialog learnDialog;

	private  FileDialog fileDialog;

	// State of the editor:

	private FlashcardTree series;

	private File file;

	private JTree tree;

	private Collection<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();

	private JButton newCatBut;

	private JButton deleteCatBut;

	protected boolean singleDirectorySelected;

	//private Map<Flashcard, JLabel> flashcardTreeLabelMap;

	void attachSelectionListener(SelectionListener arg0) {
		selectionListeners.add(arg0);
	}
	
	protected FlashcardsWindow(File file) throws IOException {
		this(Store.openSeries(file));

		this.file = file;
		Utilities.setFrameTitle(frame, file);
	}

	public static boolean createFlashcardsEditor(File file) {
		try {
			new FlashcardsWindow(file);
			return true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(
					null,
					"The document \"" + file.getName()
							+ "\" could not be read." + "\n"
							+ e.getLocalizedMessage(), "",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}

	public FlashcardsWindow(final FlashcardTree series) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {

				System.err.println(Thread.currentThread().getName());
				
				/*
				 * General Design Decision(s):
				 * 
				 * ActionListener do not contain domain logic; they always delegate to
				 * corresponding methods.
				 * 
				 * All errors are handled as early as possible.
				 * 
				 * A Frame is associated with exactly one FlashcardSeries.
				 */

				FlashcardsWindow.this.series = series;


				// setup of this frame; we need to do it here since the rootpane's
				// client property has to set before the other components are created
				frame = new JFrame();
				frame.getRootPane().putClientProperty("apple.awt.brushMetalLook",
						java.lang.Boolean.TRUE);

				Utilities.setFrameTitle(frame, file);

				// dialogs and other components that are related to this frame
				flashcardEditor = new FlashcardEditor(FlashcardsWindow.this);

				learnDialog = new LearnDialog(FlashcardsWindow.this);

				fileDialog = new java.awt.FileDialog(frame);
				fileDialog.setFilenameFilter(new FilenameFilter() {

					public boolean accept(File directory, String name) {
						return name.endsWith(Store.FILE_ENDING);
					}
				});

				// setup the menu and its listeners
				newFileMenuItem = new JMenuItem("New");
				newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				// TODO Implement the functionality to create an empty flashcard series in a new FlashcardsEditor Window

				openFileMenuItem = new JMenuItem("Open File...");
				openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				openFileMenuItem.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {
						openFlashcardSeries();
					}
				});

				saveFileMenuItem = new JMenuItem("Save");
				saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				saveFileMenuItem.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {
						saveFlashcardSeries();
					}
				});

				saveAsFileMenuItem = new JMenuItem("Save As...");
				saveAsFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
						(java.awt.event.InputEvent.SHIFT_MASK | Toolkit
								.getDefaultToolkit().getMenuShortcutKeyMask())));
				saveAsFileMenuItem.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {
						saveAsFlashcardSeries();
					}
				});

				closeFileMenuItem = new JMenuItem("Close Window");
				closeFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				closeFileMenuItem.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {
						closeFlashcardEditor();
					}
				});

				fileMenu = new JMenu("File");
				fileMenu.add(newFileMenuItem);
				fileMenu.addSeparator();
				fileMenu.add(openFileMenuItem);
				fileMenu.addSeparator();
				fileMenu.add(saveFileMenuItem);
				fileMenu.add(saveAsFileMenuItem);
				fileMenu.addSeparator();
				fileMenu.add(closeFileMenuItem);

				menuBar = new JMenuBar();
				menuBar.add(fileMenu);

				newCatBut = Utilities.createToolBarButton("Create category", javax.swing.plaf.metal.MetalIconFactory.getFileChooserNewFolderIcon());
				newCatBut.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						createCategoryButtonPushed();
					}
				});

				deleteCatBut = Utilities.createToolBarButton("Delete category", javax.swing.plaf.metal.MetalIconFactory.getInternalFrameCloseIcon(32));
				deleteCatBut.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						deleteCategoryButtonPushed();
					}
				});

				series.addCategoryModificationPermissionListener(new CategoryModificationPermissionListener() {

					@Override
					public void setAllowDeletion(boolean allow) {
						deleteCatBut.setEnabled(allow);
					}

					@Override
					public void setAllowCreation(boolean allow) {
						newCatBut.setEnabled(allow);
					}
				});

				addButton = Utilities.createToolBarButton(" Create ", "list-add.png",
						"create new flashcard");
				addButton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {

						createFlashcard();
					}
				});

				removeButton = Utilities.createToolBarButton(" Delete ",
						"list-remove.png", "remove selected flashcards");
				removeButton.setEnabled(false);
				removeButton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {

						removeFlashcards();
					}
				});

				editButton = Utilities.createToolBarButton(" Edit ",
						"accessories-text-editor.png", "edit selected flashcard");
				editButton.setEnabled(false);
				editButton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {

						editFlashcard();
					}
				});

				list = new JList(series);
				list.addListSelectionListener(new ListSelectionListener() {

					public void valueChanged(ListSelectionEvent event) {
						// Only GUI related functionality:
						if (list.getSelectedIndex() != -1 && singleDirectorySelected) {
							removeButton.setEnabled(true);
							editButton.setEnabled(true);
						} else {
							removeButton.setEnabled(false);
							editButton.setEnabled(false);
						}
					}
				});

				listScrollPane = new JScrollPane(list);
				listScrollPane.setBorder(BorderFactory.createEmptyBorder());
				listScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

				playButton = Utilities.createToolBarButton(" Learn ",
						"media-playback-start.png", "learn flashcards");
				playButton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {

						learn();
					}
				});
				// TODO Disable the playButton if the series contains no flashcards.

				toolbar = new JToolBar();
				toolbar.putClientProperty("JToolBar.isRollover", Boolean.FALSE);
				toolbar.add(addButton);
				toolbar.add(removeButton);
				toolbar.addSeparator();
				toolbar.add(newCatBut);
				toolbar.add(deleteCatBut);
				toolbar.addSeparator();
				toolbar.add(editButton);
				toolbar.add(Box.createHorizontalGlue());
				toolbar.add(playButton);
				toolbar.setFloatable(false);


				//////////////////// BEGIN ex10

				tree = new JTree();
				series.createTreeModel();
				tree.setModel( series.getTreeModel()  );
				tree.getSelectionModel().setSelectionMode(
						TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
				tree.setEditable(false);
				tree.addTreeSelectionListener(getTreeSelectionListener());
				FlashcardTreeCellRenderer ftcr = new FlashcardTreeCellRenderer();
				//rebuildFlashcardLabelMap(ftcr.getMap());
				tree.setCellRenderer( ftcr  );
				tree.setDropMode(DropMode.ON);
				tree.setTransferHandler(new FlashcardModelTreeTransferHandler(series.getTreeModel()));
				tree.setDragEnabled(true);
				JScrollPane treeScrollPane = new JScrollPane(tree);
				treeScrollPane.setMinimumSize(new Dimension(200, 0));
				treeScrollPane.setPreferredSize(new Dimension(200, 0));
				treeScrollPane.setBorder(BorderFactory.createEmptyBorder());

				//////////////////// END ex10

				frame.setJMenuBar(menuBar);
				frame.getContentPane().add(listScrollPane);
				frame.getContentPane().add(toolbar, BorderLayout.NORTH);
				frame.getContentPane().add(treeScrollPane, BorderLayout.WEST); //ex10
				frame.setSize(640, 480);
				frame.setLocationByPlatform(true);
				frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
				frame.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosed(WindowEvent event) {

						SwingUtilities.invokeLater(new Runnable() {

							public void run() {
								// we have to make sure that the JFrame object will be
								// collected...
								// (the VM terminates if all frames are disposed and
								// finally collected)
								System.gc();
							}
						});
					}

				});

				FlashcardsWindow.this.attachSelectionListener(new SelectionListener() {
					@Override
					public void selectionsChanged(List<IFlashcardSeriesComponent> l) {
						singleDirectorySelected = l.size() == 1 && l.iterator().next() instanceof FlashCategory;

						addButton.setEnabled(singleDirectorySelected);
					}
				});

				FlashcardsWindow.this.attachSelectionListener(getSeries().getSelectionListener());

				selectRoot();

				series.setSelectRootAction(FlashcardsWindow.this);

				// Everything is setup; show the window:
				frame.setVisible(true);
			}
		}
				);

	}
	/*
	private void rebuildFlashcardLabelMap(Map<Flashcard, JLabel> fl) {
		//flashcardTreeLabelMap = new HashMap<Flashcard, JLabel>();
		for (final Entry<Flashcard, JLabel> f : fl.entrySet()) {
			f.getKey().addSubscriber(new Runnable() {

				@Override
				public void run() {
					f.getValue().setText(f.getKey().toString());
				}
				
			});
		}
	}
*/
	public void selectRoot() {
		tree.setSelectionPath(new TreePath(tree.getModel().getRoot()));
	}

	protected void deleteCategoryButtonPushed() {
		series.deleteSelectedCategories();
	}

	protected void createCategoryButtonPushed() {
		String name = JOptionPane.showInputDialog(frame, "Please enter a name for the new category.");
		series.createCategory(name);
	}

	private TreeSelectionListener getTreeSelectionListener() {
		TreeSelectionListener listener = new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e) {
		    	TreePath[] paths = tree.getSelectionPaths();
		    	
		    	List<IFlashcardSeriesComponent> l = new ArrayList<IFlashcardSeriesComponent>();
		    	//List<IFlashcardSeriesComponent> removed = new ArrayList<IFlashcardSeriesComponent>();
		    	
		    	list.clearSelection();
		    	
		    	if (paths == null) return; 

		    	for (TreePath p : paths) {
		    		DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
		    		
		    		IFlashcardSeriesComponent nodeInfo = (IFlashcardSeriesComponent) node;
		    		l.add(nodeInfo);
		    		System.err.println("window: " + nodeInfo);
		    	}
		    	
		    	//l.add((IFlashcardSeriesComponent) ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject());
		    	
		    	triggerSelected(l);
		    	//tree.setSelectionPaths(paths);
		    	
		    	//tree.selectionRedirector
		    }
		};
		//listener.
		return listener;
	}

	protected void triggerSelected(List<IFlashcardSeriesComponent> l) {
		for (SelectionListener listener : selectionListeners) {
			listener.selectionsChanged(l);
		}
	}

	// Implementation of the "logic":

	public FlashcardTree getSeries() {
		return series;
	}

	public JFrame getFrame() {
		return frame;
	}

	protected void openFlashcardSeries() {
		fileDialog.setMode(FileDialog.LOAD);
		fileDialog.setVisible(true);
		String filename = fileDialog.getFile();
		if (filename != null) {
			if (!filename.endsWith(Store.FILE_ENDING))
				filename += Store.FILE_ENDING;
			File file = new File(fileDialog.getDirectory(), filename);
			createFlashcardsEditor(file);
		}
	}

	protected void saveFlashcardSeries() {
		if (file == null)
			saveAsFlashcardSeries();
		else
			doSave(file);
	}

	protected void saveAsFlashcardSeries() {
		fileDialog.setMode(FileDialog.SAVE);
		fileDialog.setVisible(true);
		String filename = fileDialog.getFile();
		if (filename != null) {
			if (!filename.endsWith(Store.FILE_ENDING))
				filename += Store.FILE_ENDING;

			File newFile = new File(fileDialog.getDirectory(), filename);
			if (newFile.exists()) {
				if (JOptionPane
						.showConfirmDialog(
								frame,
								"The file with the name:\n"
										+ filename
										+ "\nalready exists.\nDo you want to overwrite the file?",
								"Warning", JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION)
					return;
			}
			doSave(newFile);
		}

	}

	protected void doSave(File file) {
		try {

			Store.saveSeries(series, file);

			// Saving the file was successful:
			this.file = file;
			Utilities.setFrameTitle(frame, file);

		}catch (NotSerializableException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame, "Could not save flashcards",
					"Saving the flashcards to :\n" + file.getName()
							+ "\nfailed.", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void learn() {
		learnDialog.show();
	}

	protected void closeFlashcardEditor() {
		frame.setVisible(false);
		frame.dispose(); // required to give up all resources
	}

	protected void createFlashcard() {
		Flashcard card = new Flashcard();
		if (flashcardEditor.edit(card))
			series.addCard(card);
	}

	protected void removeFlashcards() {
		FlashcardsWindow.this.series.removeCards(list.getSelectedIndices());
		list.clearSelection();
	}

	protected void editFlashcard() {
		int index = list.getSelectedIndex();
		flashcardEditor.edit(series.getElementAt(index));
		//series.fireIntervalChanged(this, index, index);
		//series.updateFlashcardTreeEntry(series.getElementAt(index));
	}

}

class FlashcardTreeCellRenderer extends DefaultTreeCellRenderer {  
	private static final long serialVersionUID = 1L;

	//private Map<Flashcard,JLabel> map = new HashMap<Flashcard,JLabel>();
	
	FlashcardTreeCellRenderer() {
	}
	
	//public Map<Flashcard,JLabel> getMap() { return map; }
	
	public Component getTreeCellRendererComponent(JTree tree,  
            Object value, boolean sel, boolean expanded, boolean leaf,  
            int row, boolean hasFocus)  
    {  
        JLabel renderer = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);  
        if (((DefaultMutableTreeNode) value).getAllowsChildren())  
        {
            if (expanded)  
            {  
                renderer.setIcon(openIcon);
            }  
            else  
            {  
                renderer.setIcon(closedIcon);  
            }  
        }
        else  
        {  
            renderer.setIcon(leafIcon);  
        }
        /*
        Object usrObj = ((DefaultMutableTreeNode) value).getUserObject();
        
        if (usrObj instanceof FlashLeaf) {
        	map.put(((FlashLeaf) usrObj).f,renderer);
        }
        */
        return renderer;  
    }  
}  