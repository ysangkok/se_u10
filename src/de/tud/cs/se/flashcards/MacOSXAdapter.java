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

import java.awt.Image;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.JOptionPane;

import de.tud.cs.se.flashcards.ui.Utilities;

/**
 * This class provides the major part of the Mac OS X integration of the
 * flashcards app.
 * <p>
 * <i> This class does not introduce any coupling on Mac OS X specific classes
 * or technologies. </i>
 * </p>
 * 
 * @author Michael Eichberg
 */
public class MacOSXAdapter {

	static {

		// properties to make the application look more similar to a native Mac
		// OS X application
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.growbox.intrudes",
				"false");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				"Flashcards");

		try {
			Class<?> applicationClass = Class
					.forName("com.apple.eawt.Application");
			Object application = applicationClass.getMethod("getApplication")
					.invoke(null);
			applicationClass.getMethod("setEnabledPreferencesMenu",
					boolean.class).invoke(application, Boolean.FALSE);

			Image appImage = java.awt.Toolkit.getDefaultToolkit().getImage(
					Utilities.class.getResource("Papers-icon.png"));
			applicationClass.getMethod("setDockIconImage", Image.class).invoke(
					application, appImage);

			Class<?> applicationAdapterClass = Class
					.forName("com.apple.eawt.ApplicationListener");
			Object applicationAdapter = Proxy.newProxyInstance(
					System.class.getClassLoader(),
					new Class<?>[] { applicationAdapterClass },
					new InvocationHandler() {

						public Object invoke(Object proxy, Method method,
								Object[] args) throws Throwable {
							if (method.getName().equals("handleAbout")) {
								JOptionPane
										.showMessageDialog(
												null,
												"(c) 2010 Michael Eichberg,\nDepartment of Computer Science,\nTechnische Universität Darmstadt",
												"Flashcards",
												JOptionPane.INFORMATION_MESSAGE,
												Utilities.createImageIcon(
														"Papers-icon.png",
														"The Flashcards Icon"));
								args[0].getClass()
										.getMethod("setHandled", boolean.class)
										.invoke(args[0], Boolean.TRUE);
							} else if (method.getName().equals("handleQuit")) {
								// Check to see if the user has unsaved
								// changes.
								// If the user does not have unhandled changes
								// call
								// setHandled(true) otherwise call
								// setHandled(false).
								args[0].getClass()
										.getMethod("setHandled", boolean.class)
										.invoke(args[0], Boolean.TRUE);
							}
							return null;
						}
					});

			applicationClass.getMethod("addApplicationListener",
					Class.forName("com.apple.eawt.ApplicationListener"))
					.invoke(application, applicationAdapter);
		} catch (Exception e) {
			System.err.println("Mac OS X integration failed: "
					+ e.getLocalizedMessage() + ".");
		}
	}
}
