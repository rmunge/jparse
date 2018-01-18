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


/**
 *
 * @author rmunge
 */
public interface Features {

	/**
	 * Check whether a given <tt>feature</tt> is enabled or not.
	 *
	 * @param feature a regular expression feature to check
	 * @return <code>true</code> if the given feature is enabled
	 */
	boolean isFeatureEnabled(RegexFeature feature);

	/**
	 * Gets the maximum number of allowed levels of nested groups.
	 * <p>
	 * The returned value is only enforced if {@link RegexFeature#Complexity_UnrestrictedNestedGroups} is disabled.
	 *
	 * @return a value >= 0
	 */
	int getMaxNestedGroups();

	/**
	 * Gets the maximum length of regular expressions.
	 * <p>
	 * The returned value is only enforced if {@link RegexFeature#Complexity_UnrestrictedLength} is disabled.
	 *
	 * @return a value >= 0
	 */
	int getMaxRegexLength();

	/**
	 * Gets the number of overlapping quantified tokens which are ignored per regular expression .
	 *
	 * <p>
	 * The returned value is only relevant if {@link RegexFeature#Complexity_OverlappingQuantifiedTokens} is disabled.
	 * Default value is 1.
	 * </p>
	 *
	 * @return a value >= 0
	 */
	int getIgnoredOverlappingQuantifiers();

}
