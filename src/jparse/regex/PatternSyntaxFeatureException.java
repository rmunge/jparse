/*
 * Copyright (c) 2003, rmunge and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  The author designates this
 * particular file as subject to the "Classpath" exception as provided
 * in the LICENSE file that accompanied this code.
 *
 * This work contains modified source code from the OpenJDK project (package: jparse.sun.misc).
 * The original source code is available here: http://hg.openjdk.java.net/jdk8.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package jparse.regex;

import java.text.MessageFormat;
import java.util.regex.PatternSyntaxException;


/**
 * Specialized {@link PatternSyntaxException} which is fired when a feature is found within a regular expression that is disabled.
 *
 * @author rmunge
 */
public final class PatternSyntaxFeatureException extends PatternSyntaxException {

	private static final String NL = System.getProperty("line.separator");
	public static final String MESSAGE_TEMPLATE = "Unsupported feature, \"{0}\" near index {1}";

	private static final long serialVersionUID = 6970880952693947915L;

	private final RegexFeature feature;
	private final String regex;
	private final int index;


	/**
	 * Constructs a new instance of this class.
	 *
	 * @param cause
	 *        the found, but disabled feature
	 *
	 * @param regex
	 *        The erroneous pattern
	 *
	 * @param index
	 *        The approximate index in the pattern of the error,
	 *        or <tt>-1</tt> if the index is not known
	 */
	public PatternSyntaxFeatureException(RegexFeature cause, String regex, int index) {
		super(cause.getDescription(), regex, index);
		this.feature = cause;
		this.regex = regex;
		this.index = index;
	}

	/**
	 * @return the found but disabled feature
	 */
	public RegexFeature getFeature() {
		return feature;
	}


	  /**
     * Returns a multi-line string containing the description of the syntax
     * error and its index, the erroneous regular-expression pattern, and a
     * visual indication of the error index within the pattern.
     *
     * @return  The full detail message
     */
    public String getMessage() {
    	return MessageFormat.format(MESSAGE_TEMPLATE, feature.getDescription(), index) + NL + generateDetailMessage();
    }


    private String generateDetailMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append(regex);
        if (index >= 0) {
            sb.append(NL);
            for (int i = 0; i < index; i++) {
            	sb.append(' ');
            }
            sb.append('^');
        }
        return sb.toString();
    }

}
