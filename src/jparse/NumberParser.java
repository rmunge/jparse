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
package jparse;

import jparse.sun.misc.FloatingDecimal;

/**
 * Class with static methods for parsing positive decimal integers, doubles and floats without throwing
 * any {@link NumberFormatException} or {@link IllegalArgumentException}.
 *
 * @author rmunge
 */
public final class NumberParser {

	private static final char LATIN_0 = '\u0030';
	private static final char LATIN_9 = '\u0039';
	private static final int NO_POSITIVE_INT = -1;

	/**
	 * Parses a given string as a signed decimal integer with radix 10.
	 * <p>
	 * The characters in the string must all be decimal digits, The first character may be an ASCII minus sign {@code '-'} (
	 * {@code '\u005Cu002D'}) to indicate a negative value or an ASCII plus sign {@code '+'} ({@code '\u005Cu002B'}) to indicate a
	 * positive value.
	 * </p>
	 *
	 * @param string a {@code String} containing the signed decimal integer representation to be parsed
	 * @return the positive integer value represented by the argument in decimal or <code>-1</code> if the string does not contain a
	 *         valid representation of a positive decimal integer (with an optional leading '+' sign, leading and trailing spaces
	 *         an spaces as separators e.g. "1 000 000 000")
	 */
	public static int parsePositiveInt(String string) {
		return parsePositiveInt(string, 10);
	}


	/**
	 * Parses a given string as a signed decimal integer.
	 * <p>
	 * The characters in the string must all be decimal digits, The first character may be an ASCII minus sign {@code '-'} (
	 * {@code '\u005Cu002D'}) to indicate a negative value or an ASCII plus sign {@code '+'} ({@code '\u005Cu002B'}) to indicate a
	 * positive value.
	 * </p>
	 *
	 * @param string a {@code String} containing the signed decimal integer representation to be parsed
	 * @param radix the radix (e.g. 16 for Hex)
	 * @return the positive integer value represented by the argument in decimal or <code>-1</code> if the string does not contain a
	 *         valid representation of a positive decimal integer (with an optional leading '+' sign, leading and trailing spaces
	 *         an spaces as separators e.g. "1 000 000 000")
	 */
	public static int parsePositiveInt(String string, int radix) {

		if (string == null) {
			return NO_POSITIVE_INT;
		}

		final int length = string.length();
		int i = 0;

		if (length == 0) {
			return NO_POSITIVE_INT;
		}

		// skip leading spaces
		while (string.charAt(i) == ' ') {
			i++;
			if (i >= length) {
				return NO_POSITIVE_INT;
			}
		}

		char potentialSign = string.charAt(i);

		if (potentialSign < '0') {

			if (potentialSign == '-') {
				return NO_POSITIVE_INT;

			} else if (potentialSign != '+')
				return NO_POSITIVE_INT;

			if (i == length - 1) {
				// just a '+' or '-' is not a valid number
				return NO_POSITIVE_INT;
			}
			i++;
		}

		final int multiplicationLimit = Integer.MAX_VALUE / radix;
		boolean checkForLatinDigitsFirst = (radix == 10);
		int digit;
		int result = 0;

		while (i < length) {

			char c = string.charAt(i);

			// ignore spaces
			if (c == ' ') {
				i++;
				continue;
			}

			/*
			 * Optimized path for ISO-LATIN-1 digits ('0' through '9').
			 *
			 * Guarantees best performance for strings which contain only latin digits.
			 * To limit potential negative performance impact on strings which contain only non-latin digits,
			 * we skip the explicit check for latin digits after we've found at least one non-latin digit.
			 */
			if (checkForLatinDigitsFirst && c >= LATIN_0 && c <= LATIN_9) {
				digit = c - LATIN_0;

			} else {
				digit = Character.digit(c, radix);

				if (digit < 0) {
					return NO_POSITIVE_INT;

				} else {
					checkForLatinDigitsFirst = false;
				}
			}

			// we always have to check the limits, BEFORE we do any calculations,
			// when we hit a limit, it's already too late ;-)
			if (result > multiplicationLimit) {
				return NO_POSITIVE_INT;
			}
			result *= radix;

			if (result > Integer.MAX_VALUE - digit) {
				return NO_POSITIVE_INT;
			}

			result += digit;
			i++;
		}
		return result;


	}

	/**
	 * Returns a new {@code Double} initialized to the value
	 * represented by the specified {@code String}.
	 *
	 * <p>
	 * In contrast to {@link Double#parseDouble(String)} both, ',' and '.' are supported as decimal separator independent from the
	 * current locale.
	 * </p>
	 *
	 * @param string the string to be parsed.
	 * @return the <tt>double</tt> value represented by the string
	 *         argument or {@link Double#NaN} if the string does not contain
	 *         a valid representation of a floating decimal or is <code>null</code>
	 */
	public static double parseDouble(String string) {
		return FloatingDecimal.parseDouble(string);
	}

	/**
	 * Returns a new {@code Float} initialized to the value
	 * represented by the specified {@code String}.
	 *
	 * <p>
	 * In contrast to {@link Float#parseFloat(String)} both, ',' and '.' are supported as decimal separator independent from the
	 * current locale.
	 * </p>
	 *
	 * @param string the string to be parsed.
	 * @return the <tt>float</tt> value represented by the string
	 *         argument or {@link Float#NaN} if the string does not contain
	 *         a valid representation of a floating decimal or is <code>null</code>
	 */
	public static float parseFloat(String string) {
		return FloatingDecimal.parseFloat(string);
	}

}
