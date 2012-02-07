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

import javax.swing.tree.DefaultTreeModel;

import de.tud.cs.se.flashcards.model.FlashCategory;
import de.tud.cs.se.flashcards.model.FlashLeaf;
import de.tud.cs.se.flashcards.model.IFlashcardSeriesComponent;

public class DragAndDropHandling {
    /**
     * This method is used for dragging. It determines if draggedElement can be
     * dropped on targetComponent.
     * 
     * @param draggedComponent
     *            the dragged component
     * @param targetComponent
     *            the component on which to check if the drop is allowed
     * @return true if drop is allowed
     */
    public static boolean canDropOnObject(final IFlashcardSeriesComponent draggedComponent,
            final IFlashcardSeriesComponent targetComponent) {

    	try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	System.out.println(draggedComponent);
    	System.out.println(targetComponent);
    	
    	FlashCategory from;
    	FlashCategory to;
    	
        if (draggedComponent instanceof FlashCategory && targetComponent instanceof FlashCategory) {
        	from = (FlashCategory) draggedComponent;
        	to = (FlashCategory) targetComponent;
        	
        	if (from.contains(to)) return false;
        	
            return true;
        } else if (draggedComponent instanceof FlashLeaf && targetComponent instanceof FlashCategory) {
        	return true;
        } else {
        	return false;
        }
        
        
    }

    /**
     * This method is called if a move action should be performed.
     * 
     * @param parentOfDraggedComponent
     *            the parent of the dragged component
     * @param draggedComponent
     *            the dragged component
     * @param targetComponent
     *            the component on which the drop happened
     */
    public static void move(final IFlashcardSeriesComponent parentOfDraggedComponent,
            final IFlashcardSeriesComponent draggedComponent, final IFlashcardSeriesComponent targetComponent, DefaultTreeModel r) {
    	System.err.println("DROP");
    	
    	if (draggedComponent instanceof FlashCategory && targetComponent instanceof FlashCategory) {

    	} else if (draggedComponent instanceof FlashLeaf && targetComponent instanceof FlashCategory) {
    		
    	} else {
    		throw new RuntimeException("IMPOSSIBLE");
    	}
    	
		parentOfDraggedComponent.removeI(draggedComponent);
		targetComponent.addI(draggedComponent);

    	
    	r.reload();
    	
    }
}
