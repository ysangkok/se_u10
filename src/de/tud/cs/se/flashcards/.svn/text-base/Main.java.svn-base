/** License (BSD Style License):
 *  Copyright (c) 2010
 *  Michael Eichberg (Software Engineering)
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
package de.tud.cs.se.flashcards;

import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.UIManager;

import de.tud.cs.se.flashcards.model.FlashcardSeries;
import de.tud.cs.se.flashcards.ui.FlashcardsWindow;

/**
 * Starts the flashcard application. Basically, each given file is associated
 * with its own Frame.
 * 
 * @author Michael Eichberg
 */
public class Main {

	/**
	 * Starts the application.
	 * 
	 * @param args
	 *            a list of filenames, a new editor frame is created for each
	 *            file.
	 */
	public static void main(String[] args) {

		if (System.getProperty("os.name").startsWith("Mac OS X")) {
			// we have to avoid tight coupling to make the project usable on
			// different platforms
			try {
				Class.forName("de.tud.cs.se.flashcards.MacOSXAdapter");
			} catch (ClassNotFoundException cnfe) {
				System.err
						.println("Setting up the Mac OS X integration failed. Error:");
				cnfe.printStackTrace(System.err);
			}
		} else {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				System.err
						.println("The native system look and feel is not available ("
								+ e.getLocalizedMessage() + ").");
			}
		}

		// show all user interface related properties
		Iterator<Entry<Object, Object>> properties = javax.swing.UIManager
				.getDefaults().entrySet().iterator();
		while (properties.hasNext()) {
			Entry<Object, Object> property = properties.next();
			System.out.println(property.getKey() + " = " + property.getValue());
		}

		// let's try to open one of the documents specified on the command line
		boolean documentOpened = false;
		if (args.length > 0) {
			for (String arg : args) {
				if (FlashcardsWindow.createFlashcardsEditor(new File(arg)))
					documentOpened = true;
			}
		}
		// either the user didn't specify a document on the command line or all
		// specified documents
		// were unreadable
		if (!documentOpened) {
			new FlashcardsWindow(FlashcardSeries.createInitialFlashcardSeries());
		}
	}
}
