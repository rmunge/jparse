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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Represents a set of enabled regular expression features.
 *
 * <p>A {@code RegexFeatureSet} is safe for multi-threaded use.</p>
 *
 * @author rmunge
 */
public final class RegexFeatureSet implements Features, java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private final ConcurrentHashMap<RegexFeature, Boolean> features = new ConcurrentHashMap<RegexFeature, Boolean>(RegexFeature.values().length, 0.75f, 1);
	private final AtomicInteger maxNestedGroups = new AtomicInteger(2);
	private final AtomicInteger maxRegexLength = new AtomicInteger(10_000);
	private final AtomicInteger ignoredOverlappingQuantifiers = new AtomicInteger(1);

	/**
	 * All optional features are disabled.
	 */
	public static final Features ALL_DISABLED;

	/**
	 * All features which are by default supported in Java.
	 */
	public static final Features JAVA_DEFAULT;

	/**
	 * Only features which are also supported in regular expressions within ECMA script / JavaScript.
	 */
	public static final Features ECMA_SCRIPT_REGEXP;

	/**
	 * Only features which are also supported in regular expressions, interpreted by the BOOST Xpressive library.
	 */
	public static final Features BOOST_XPRESSIVE;

	static {

		RegexFeatureSet featureSet = new RegexFeatureSet();
		featureSet.setFeatureEnabled(RegexFeature.PossessiveQuantifiers, false);
		featureSet.setFeatureEnabled(RegexFeature.UnicodeExpressions, false);
		featureSet.setFeatureEnabled(RegexFeature.Backreferences, false);
		featureSet.setFeatureEnabled(RegexFeature.OctalEscapeSequences, false);
		featureSet.setFeatureEnabled(RegexFeature.AlternativeBegin, false);
		featureSet.setFeatureEnabled(RegexFeature.AlternativeEnd, false);
		featureSet.setFeatureEnabled(RegexFeature.EndOfPreviousMatch, false);
		featureSet.setFeatureEnabled(RegexFeature.HorizontalWhitespace, false);
		featureSet.setFeatureEnabled(RegexFeature.AnyUnicodeLinebreakSequence, false);
		featureSet.setFeatureEnabled(RegexFeature.NonVerticalWhitespace, false);
		featureSet.setFeatureEnabled(RegexFeature.NamedCapturingGroupsAndReferences, false);
		featureSet.setFeatureEnabled(RegexFeature.InlinedMatchFlags, false);
		featureSet.setFeatureEnabled(RegexFeature.CharacterClassIntersection, false);
		featureSet.setFeatureEnabled(RegexFeature.CharacterClassUnion, false);
		featureSet.setFeatureEnabled(RegexFeature.QuotationSequence, false);

		// According to documentation, the following two features are not be supported by BOOST expressive,
		// but they seem to work. We follow the official documentation here and disable them.
		featureSet.setFeatureEnabled(RegexFeature.PositiveLookbehind, false);
		featureSet.setFeatureEnabled(RegexFeature.NegativeLookbehind, false);

		ECMA_SCRIPT_REGEXP = featureSet;

		featureSet = new RegexFeatureSet(ECMA_SCRIPT_REGEXP);
		featureSet.setFeatureEnabled(RegexFeature.ExactZeroQuantifierWithZero, false);
		BOOST_XPRESSIVE = featureSet;

		JAVA_DEFAULT = new RegexFeatureSet();

		featureSet = new RegexFeatureSet();
		for (RegexFeature feature : RegexFeature.values()) {
			featureSet.setFeatureEnabled(feature, false);
		}
		ALL_DISABLED = featureSet;

	}

	/**
	 * Copy constructor.
	 *
	 * @param featureSet
	 */
	public RegexFeatureSet(Features featureSet) {
		for (RegexFeature feature : RegexFeature.values()) {
			setFeatureEnabled(feature, featureSet.isFeatureEnabled(feature));
		}
	}

	/**
	 * Constructs a {@link RegexFeatureSet} with all known features enabled.
	 */
	public RegexFeatureSet() {
		for (RegexFeature feature : RegexFeature.values()) {
			features.put(feature, Boolean.TRUE);
		}
	}

	@Override
	public boolean isFeatureEnabled(RegexFeature feature) {
		return features.get(feature).booleanValue();
	}

	/**
	 * Enables/Disables a given <tt>feature</tt>.
	 *
	 * @param feature a regular expression feature to check
	 * @param enabled <code>true</code> to enable the given feature, otherwise <code>false</code>
	 */
	public void setFeatureEnabled(RegexFeature feature, boolean enabled) {
		features.put(feature, Boolean.valueOf(enabled));
	}

	/**
	 * Sets the maximum number of allowed levels of nested groups.
	 *
	 * <p>
	 * The value will only be enforced if {@link RegexFeature#Complexity_UnrestrictedNestedGroups} is disabled. The
	 * default value is 2.
	 * </p>
	 *
	 * @param maxNestedGroups a value >= 0
	 */
	public void setMaxNestedGroups(int maxNestedGroups) {

		if (maxNestedGroups < 0) {
			throw new IllegalArgumentException("invalid value for maxNestedGroups: " + maxNestedGroups);
		}

		this.maxNestedGroups.set(maxNestedGroups);
	}

	/**
	 * Sets the maximum length of regular expressions.
	 * <p>
	 * The value will only be enforced if {@link RegexFeature#Complexity_UnrestrictedLength} is disabled.
	 * The default is 10 000.
	 * </p>
	 *
	 * @return a value >= 0
	 */
	public void setMaxRegexLength(int maxRegexLength) {

		if (maxRegexLength < 0) {
			throw new IllegalArgumentException("invalid value for maxRegexLength: " + maxRegexLength);
		}

		this.maxRegexLength.set(maxRegexLength);
	}

	/**
	 * Sets the number of overlapping quantified tokens which are ignored per regular expression .
	 *
	 * <p>
	 * The value is only relevant if {@link RegexFeature#Complexity_OverlappingQuantifiedTokens} is disabled.
	 * Default value is 1.
	 * </p>
	 *
	 * @return a value >= 0
	 */
	public void setIgnoredOverlappingQuantifiers(int ignored) {
		if (ignored < 0) {
			throw new IllegalArgumentException("invalid value for ignored: " + maxRegexLength);
		}

		this.ignoredOverlappingQuantifiers.set(ignored);
	}

	@Override
	public int getMaxNestedGroups() {
		return maxNestedGroups.get();
	}

	@Override
	public int getMaxRegexLength() {
		return maxRegexLength.get();
	}

	@Override
	public int getIgnoredOverlappingQuantifiers() {
		return ignoredOverlappingQuantifiers.get();
	}

}
