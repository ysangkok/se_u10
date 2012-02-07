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
package de.tud.cs.se.flashcards.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.IOUtils;

import de.tud.cs.se.flashcards.model.FlashcardTree;
import de.tud.cs.se.flashcards.model.IFlashcardSeriesComponent;

/**
 * Some helper methods related to persisting a flashcard series.
 * <p>
 * <b><font color="red"> Persistence is currently not handled properly! Do never
 * use Java Serialization for long term storage! </font></b>
 * </p>
 * 
 * @author Michael Eichberg
 * @author Ralf Mitschke
 * @author Johannes Lerch
 */
public class Store {

    public static final String FILE_ENDING = ".flashcards";

    public static FlashcardTree openSeries(final File file) throws IOException {

        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(new FileInputStream(file));
            final IFlashcardSeriesComponent root = (IFlashcardSeriesComponent) oin.readObject();
            root.initializeListeners();
            final FlashcardTree series = new FlashcardTree(root);
            return series;
        } catch (final ClassNotFoundException e) {
            // the file did contain something unexpected...
            throw new IOException(e);
        } finally {
            if (oin != null) {
                IOUtils.closeQuietly(oin);
            }
        }
    }

    public static void saveSeries(final FlashcardTree series, final File file) throws IOException {

        ObjectOutputStream oout = null;
        try {
            oout = new ObjectOutputStream(new FileOutputStream(file));
            oout.writeObject(series.getRoot());
        } finally {
            if (oout != null) {
                oout.close();
            }
        }

    }

}
