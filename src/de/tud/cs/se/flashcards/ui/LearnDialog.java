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

import static de.tud.cs.se.flashcards.ui.Utilities.createImageIcon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import de.tud.cs.se.flashcards.model.Flashcard;
import de.tud.cs.se.flashcards.model.FlashcardSeries;

/**
 * This dialog first renders a flashcard's question and then the answer.
 * Additionally, the logic to step through a series of flashcards is provided.
 * 
 * @author Michael Eichberg
 */
public class LearnDialog {

	private final FlashcardsWindow owner;

	private final JDialog dialog;

	protected final Box headerBox;

	protected final JLabel titleLabel;

	protected final JButton cancelButton;

	protected final JLabel contentLabel;

	protected final Box navigationBox;

	protected final JButton nextButton;

	public LearnDialog(FlashcardsWindow owner) {

		this.owner = owner;

		dialog = new JDialog(owner.getFrame(), true);
		dialog.getRootPane().putClientProperty(
				"apple.awt.draggableWindowBackground", Boolean.TRUE);
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		// Create the header components:
		titleLabel = new JLabel(); // need to be initialized...
		titleLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 10, 10));
		titleLabel.setVerticalAlignment(SwingConstants.CENTER);
		titleLabel
				.setPreferredSize(new JLabel("XXXXXXXXXX").getPreferredSize());

		cancelButton = new JButton(createImageIcon("process-stop.png",
				"stop learning"));
		cancelButton.setBorderPainted(false);
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});

		headerBox = Box.createHorizontalBox();
		headerBox.add(titleLabel);
		headerBox.add(Box.createHorizontalGlue());
		headerBox.add(cancelButton);

		contentLabel = new JLabel(); // need to be initialized...
		contentLabel.setSize(Flashcard.FLASHCARD_DIMENSION);
		contentLabel.setPreferredSize(Flashcard.FLASHCARD_DIMENSION);
		contentLabel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		contentLabel.setFont(UIManager.getFont("FormattedTextField.font"));
		contentLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// setup the footer
		nextButton = new JButton(createImageIcon("go-next.png", "next"));

		navigationBox = Box.createHorizontalBox();
		navigationBox.add(Box.createHorizontalGlue());
		navigationBox.add(nextButton);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				next();
			}
		});

		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(headerBox, BorderLayout.NORTH);
		dialog.getContentPane().add(contentLabel, BorderLayout.CENTER);
		dialog.getContentPane().add(navigationBox, BorderLayout.SOUTH);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// dialog.setLocationRelativeTo(owner.getFrame());
		dialog.setLocation(screenSize.width / 2 - (320),
				screenSize.height / 2 - 240);
		dialog.setUndecorated(true);
		((JComponent) dialog.getContentPane()).setBorder(BorderFactory
				.createBevelBorder(BevelBorder.RAISED));
		dialog.pack();
	}

	// The "core" logic:

	enum State {
		QUESTION, ANSWER
	}

	private State state = null; // initialized by "show"

	private int index = 0; // initialized by "show"

	public void show() {
		state = State.QUESTION;
		index = -1;
		showNextQuestion();
		dialog.setVisible(true);
	}

	private void next() {
		switch (state) {

		case ANSWER:
			showNextQuestion();
			state = State.QUESTION;
			break;

		case QUESTION:
			showAnswer();
			state = State.ANSWER;
			break;
		}
	}

	private void showNextQuestion() {
		
		FlashcardSeries fs = owner.getSeries();
		
		/* START VON_UNS */
		if (index >= 0) { // there was a question before this one 
			int n = JOptionPane.showConfirmDialog(
				    dialog,
				    "Did you answer correctly?",
				    "Correct?",
				    JOptionPane.YES_NO_OPTION);
			
			Flashcard f = fs.getElementAt(index);
			if (n == JOptionPane.YES_OPTION) {
				f.increaseFach();
			} else if (n == JOptionPane.NO_OPTION) {
				f.resetFach();
			} else {
				throw new RuntimeException();
			}
			
			//fs.fireIntervalChanged(this, -1, -1);
		}
		/* END VON_UNS */
		
		index++;
		if (fs.getSize() > index) {
			titleLabel.setText("Question");
			contentLabel.setText(fs.getElementAt(index).getQuestion());
		} else {
			dialog.setVisible(false);
		}
	}

	private void showAnswer() {
		FlashcardSeries fs = owner.getSeries();
		titleLabel.setText("Answer");
		contentLabel.setText(fs.getElementAt(index).getAnswer());
	}

}
