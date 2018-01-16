/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: Features.java
 * @date: 13.12.2017
 * @author: roland.mungenast
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
