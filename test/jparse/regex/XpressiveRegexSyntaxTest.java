/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: XPressiveRegexSyntaxTest.java
 * @date: 12.12.2017
 * @author: roland.mungenast
 */
package jparse.regex;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xpressive.CompileResult;
import xpressive.Xpressive;


/**
 * Documents differences in semantic of common syntax between regular expression in Java (with default settings) and BOOSt Xpressive libary
 * and ensures that features which are not supported by Xpressive are not allowed by
 * {@link RegexFeatureSet#BOOST_XPRESSIVE}.
 *
 * <p>
 * See https://www.ecma-international.org/ecma-262/8.0/index.html#sec-regular-expressions for more details about regular
 * expression syntax of the ECMA script standard
 * </p>
 *
 * @author rmunge
 */
public class XpressiveRegexSyntaxTest extends AbstractRegexSyntaxTest<CompileResult> {

	// --------------------------------------------------------------------------------------------------------
	// --- Common syntax but (slightly) different semantics:

	@Test
	@Override
	public void testNonWhitespaceCharactersAdditionalUnicodes() {

		/*
		 * INCOMPATIBILITY to regular expressions in Java (see overridden method)
		 * \\S does not match additional Unicode whitespace characters like \u1680
		 */
		assertMatches(false, "\\S", "\\S");
		assertMatchesOtherUnicodeSpaces(false, "\\S"); // DIFFERENCE
	}

	@Test
	@Override
	public void testDotLineTerminators() {

		/*
		 * INCOMPATIBILITY to regular expressions in Java (see overridden method)
		 * . matches also '\n', '\r' line terminators
		 */

		assertMatches(true, "\n", "."); // DIFFERENCE 1
		assertMatches(true, "\r", "."); // DIFFERENCE 2
		assertMatches(false, "\u2028", ".");
		assertMatches(false, "\u2029", ".");
		assertMatches(true, "\r\n", ".."); // DIFFERENCE 1+2
	}

	@Test
	@Override
	public void testAdditionalVerticalWhitespaces() {

		/*
		 * INCOMPATIBILITY to regular expressions in Java (see overridden method)
		 * \\v matches ONLY the vertical tab character
		 */

		assertMatches(true, "\u000B", "\\v"); // vertical tab character
		assertMatches(false, "\n", "\\v"); // DIFFERENCE 1
		assertMatches(false, "\r", "\\v"); // DIFFERENCE 2
		assertMatches(false, "\u0085", "\\v"); // DIFFERENCE 3
		assertMatches(false, "\u2028", "\\v"); // DIFFERENCE 4
		assertMatches(false, "\u2029", "\\v"); // DIFFERENCE 5
	}


	// --------------------------------------------------------------------------------------------------------
	// --- Features which are supported in Java but not in BOOST Xpressive:

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testQuantifierRangeZero() {
		super.testQuantifierRangeZero();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testQuantifierExactZero() {
		super.testQuantifierExactZero();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testNegativeLookbehind() {
		super.testNegativeLookbehind();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testPositiveLookbehind() {
		super.testPositiveLookbehind();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testCharacterClassIntersection() {
		super.testCharacterClassIntersection();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testCharacterClassIntersectionSimplifiedSyntax() {
		super.testCharacterClassIntersectionSimplifiedSyntax();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testCharacterClassIntersectionWithNegatedRange() {
		super.testCharacterClassIntersectionWithNegatedRange();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testCharacterClassIntersectionWithNegation() {
		super.testCharacterClassIntersectionWithNegation();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testPosixCharacterClasses() {
		super.testPosixCharacterClasses();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testJavaCharacterClasses() {
		super.testJavaCharacterClasses();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testUnicodeSriptsBlocksCategoriesWithinCharacterClass() {
		super.testUnicodeSriptsBlocksCategoriesWithinCharacterClass();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testUnicodeScriptsBlocksCategories() {
		super.testUnicodeScriptsBlocksCategories();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testBackReferences() {
		super.testBackReferences();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testNamedCapturingGroupsAndReferences() {
		super.testNamedCapturingGroupsAndReferences();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testPossessiveQuantifiers() {
		super.testPossessiveQuantifiers();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testPossessiveQuantifiersOnGroup() {
		super.testPossessiveQuantifiersOnGroup();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testPossessiveQuantifiersOnCharacterClass() {
		super.testPossessiveQuantifiersOnCharacterClass();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testOctalExcapeSequences() {
		super.testOctalExcapeSequences();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testNonCapturingAtomicGroups() {
		super.testNonCapturingAtomicGroups();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testAlternativeBegin() {
		super.testAlternativeBegin();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testAlternativeEnd() {
		super.testAlternativeEnd();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testAnyUnicodeLinebreakSequence() {
		super.testAnyUnicodeLinebreakSequence();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testEndOfPreviousMatch() {
		super.testEndOfPreviousMatch();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testHorizontalWhitespace() {
		super.testHorizontalWhitespace();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testNonVerticalWhitespace() {
		super.testNonVerticalWhitespace();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testCharacterClassUnion() {
		super.testCharacterClassUnion();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testInlinedMatchFlags() {
		super.testInlinedMatchFlags();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testQuotationSequence() {
		super.testQuotationSequence();
	}

	// --------------------------------------------------------------------------------------------------------
	// --- Helper methods:


	@Override
	protected void assertMatches(boolean expected, String string, String pattern) {
		CompileResult result = compile(pattern);
		assertEquals("Expected result must not differ from Boost Xpressive implementation",
				expected, Xpressive.match(result.regexId, string).matches);
	}


	@Override
	protected CompileResult compile(String regex) {
		jparse.regex.java.util.regex.Pattern.compile(regex, RegexFeatureSet.BOOST_XPRESSIVE);
		return Xpressive.compile(regex);
	}

}
