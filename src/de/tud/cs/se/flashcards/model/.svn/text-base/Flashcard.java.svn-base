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
package de.tud.cs.se.flashcards.model;

import java.awt.Dimension;
import java.io.Serializable;

/**
 * A very simple flashcard.
 * 
 * @author Michael Eichberg
 */
public class Flashcard implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The width of a flashcard.
	 */
	public static int WIDTH = 600;

	/**
	 * The height of a flashcard.
	 */
	public static int HEIGHT = 400;

	/**
	 * The dimension of flashcards.
	 * 
	 * @see #WIDTH
	 * @see #HEIGHT
	 */
	public static final Dimension FLASHCARD_DIMENSION = new Dimension(WIDTH,
			HEIGHT);

	private String question;

	private String answer;

	public Flashcard(String question, String answer) {

		this.question = question;
		this.answer = answer;
	}

	// convenience constructor
	public Flashcard() {

		this("", "");
	}

	public String getAnswer() {

		return answer;
	}

	public String getQuestion() {

		return question;
	}

	public void setAnswer(String answer) {

		this.answer = answer;
	}

	public void setQuestion(String question) {

		this.question = question;
	}

}