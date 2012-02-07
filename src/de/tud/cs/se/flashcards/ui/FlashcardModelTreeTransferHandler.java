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

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.tud.cs.se.flashcards.model.IFlashcardSeriesComponent;
import de.tud.cs.se.flashcards.ui.FlashcardsTransferable.TransferTuple;

public class FlashcardModelTreeTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 4892450268190868767L;
	
	DefaultTreeModel treeModel;
	
	public FlashcardModelTreeTransferHandler(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}
	
	@Override
	public boolean canImport(final TransferHandler.TransferSupport info) {
		System.err.println(Thread.currentThread().getName());
		if (!info.isDataFlavorSupported(FlashcardsTransferable.flashcardFlavor)) {
			System.err.println("q1");
			return false;
		}

		final JTree.DropLocation dl = (JTree.DropLocation) info
				.getDropLocation();
		if (dl.getPath() == null) {
			System.err.println("q2");
			return false;
		}

		try {
			final Transferable transferable = info.getTransferable();
			final TransferTuple[] draggedElements = (TransferTuple[]) transferable
					.getTransferData(FlashcardsTransferable.flashcardFlavor);
			if (draggedElements.length == 0) {
				System.err.println("q3");
				return false;
			}

			for (final TransferTuple draggedElement : draggedElements) {
				if (!DragAndDropHandling.canDropOnObject(
						
						draggedElement.getChild(),
						
						((IFlashcardSeriesComponent) ((DefaultMutableTreeNode) dl.getPath().getLastPathComponent())))) {
					
					System.err.println("q7");
					return false;
				}
			}
			return true;
		} catch (final UnsupportedFlavorException e) {
			System.err.println("q4");
			return false;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getSourceActions(final JComponent c) {
		return MOVE;
	}

	@Override
	protected Transferable createTransferable(final JComponent c) {
		final JTree tree = (JTree) c;
		final TreePath[] selectionPaths = tree.getSelectionModel()
				.getSelectionPaths();
		Set<TreePath> draggedPaths = getAllPathsWithoutDescendants(selectionPaths);
		draggedPaths = filterRoot(draggedPaths);

		final TransferTuple[] transferTuples = new TransferTuple[draggedPaths
				.size()];
		int i = 0;
		for (final TreePath path : draggedPaths) {
			transferTuples[i++] = new TransferTuple(
					(IFlashcardSeriesComponent) ((DefaultMutableTreeNode) path.getParentPath()
							.getLastPathComponent()),
					(IFlashcardSeriesComponent) ((DefaultMutableTreeNode) path.getLastPathComponent()));
		}

		return new FlashcardsTransferable(transferTuples);
	}

	private Set<TreePath> filterRoot(final Set<TreePath> draggedPaths) {
		final Set<TreePath> result = new HashSet<TreePath>();
		for (final TreePath treePath : draggedPaths) {
			if (treePath.getParentPath() != null) {
				result.add(treePath);
			}
		}
		return result;
	}

	private Set<TreePath> getAllPathsWithoutDescendants(final TreePath[] paths) {
		final Set<TreePath> draggedPaths = new HashSet<TreePath>();
		if (paths == null) {
			return draggedPaths;
		}

		Collections.addAll(draggedPaths, paths);

		for (final TreePath checkPath : paths) {
			for (final TreePath treePath : paths) {
				if (treePath != checkPath && treePath.isDescendant(checkPath)) {
					draggedPaths.remove(checkPath);
				}
			}
		}
		return draggedPaths;
	}

	@Override
	public boolean importData(final TransferHandler.TransferSupport info) {
		if (!info.isDrop()) {
			System.err.println("q5");
			return false;
		}

		try {
			final JTree.DropLocation dropLocation = (JTree.DropLocation) info
					.getDropLocation();

			final Transferable transferable = info.getTransferable();
			final TransferTuple[] draggedElements = (TransferTuple[]) transferable
					.getTransferData(FlashcardsTransferable.flashcardFlavor);
			for (final TransferTuple draggedElement : draggedElements) {
				DragAndDropHandling.move(draggedElement.getParent(),
						draggedElement.getChild(),
						(IFlashcardSeriesComponent) ((DefaultMutableTreeNode) dropLocation.getPath()
								.getLastPathComponent()), treeModel);
			}
			return true;
		} catch (final UnsupportedFlavorException e) {
			System.err.println("q6");
			return false;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
