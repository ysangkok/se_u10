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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import de.tud.cs.se.flashcards.model.IFlashcardSeriesComponent;

public class FlashcardsTransferable implements Transferable {

    protected static final DataFlavor flashcardFlavor = createDataFlavor();

    protected static DataFlavor createDataFlavor() {
        try {
            return new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + TransferTuple[].class.getName()
                    + "\"");
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private final TransferTuple[] transferTuples;

    public FlashcardsTransferable(final TransferTuple[] transferTuples) {
        this.transferTuples = transferTuples;
    }

    @Override
    public Object getTransferData(final DataFlavor arg0) throws UnsupportedFlavorException, IOException {
        return transferTuples;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { flashcardFlavor };
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return flavor.equals(flashcardFlavor);
    }

    public TransferTuple[] getTransferTuples() {
        return transferTuples;
    }

    public static class TransferTuple {

        private final IFlashcardSeriesComponent parent;
        private final IFlashcardSeriesComponent child;

        public TransferTuple(final IFlashcardSeriesComponent parent, final IFlashcardSeriesComponent child) {
            this.parent = parent;
            this.child = child;
        }

        public IFlashcardSeriesComponent getParent() {
            return parent;
        }

        public IFlashcardSeriesComponent getChild() {
            return child;
        }
    }
}