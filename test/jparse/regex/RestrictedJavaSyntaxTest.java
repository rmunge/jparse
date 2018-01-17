/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: RegexParser.java
 * @date: 11.12.2017
 * @author: roland.mungenast
 */
package jparse.regex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jparse.regex.java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Ensures that disabled features lead to an appropriate {@link PatternSyntaxFeatureException} whenever a regular expression is
 * compiled that contains the disabled feature.
 *
 * @author rmunge
 */
public class RestrictedJavaSyntaxTest extends AbstractRegexSyntaxTest<Pattern> {

	private static Set<RegexFeature> featuresToTest = Collections.synchronizedSet(new HashSet<>());

	private volatile RegexFeature expectedSyntaxExceptionFor;

	@Test
	@Override
	public void testNegativeLookbehind() {

		expectSyntaxExceptionFor(RegexFeature.NegativeLookbehind);
		super.testNegativeLookbehind();
	}

	@Test
	@Override
	public void testPositiveLookbehind() {

		expectSyntaxExceptionFor(RegexFeature.PositiveLookbehind);
		super.testPositiveLookbehind();
	}

	@Test
	@Override
	public void testQuantifierRangeZero() {

		expectSyntaxExceptionFor(RegexFeature.ExactZeroQuantifierWithZero);
		super.testQuantifierRangeZero();
	}

	@Test
	@Override
	public void testQuantifierExactZero() {

		expectSyntaxExceptionFor(RegexFeature.ExactZeroQuantifierWithZero);
		super.testQuantifierExactZero();
	}

	@Test
	@Override
	public void testCharacterClassIntersection() {

		expectSyntaxExceptionFor(RegexFeature.CharacterClassIntersection);
		super.testCharacterClassIntersection();
	}

	@Test
	@Override
	public void testCharacterClassIntersectionSimplifiedSyntax() {

		expectSyntaxExceptionFor(RegexFeature.CharacterClassIntersection);
		super.testCharacterClassIntersectionSimplifiedSyntax();
	}

	@Test
	@Override
	public void testCharacterClassIntersectionWithNegatedRange() {

		expectSyntaxExceptionFor(RegexFeature.CharacterClassIntersection);
		super.testCharacterClassIntersectionWithNegatedRange();
	}

	@Test
	@Override
	public void testCharacterClassIntersectionWithNegation() {

		expectSyntaxExceptionFor(RegexFeature.CharacterClassIntersection);
		super.testCharacterClassIntersectionWithNegation();
	}

	@Test
	@Override
	public void testJavaCharacterClasses() {

		expectSyntaxExceptionFor(RegexFeature.UnicodeExpressions);
		super.testJavaCharacterClasses();
	}

	@Test
	@Override
	public void testUnicodeSriptsBlocksCategoriesWithinCharacterClass() {

		expectSyntaxExceptionFor(RegexFeature.UnicodeExpressions);
		super.testUnicodeSriptsBlocksCategoriesWithinCharacterClass();
	}

	@Test
	@Override
	public void testPosixCharacterClasses() {

		expectSyntaxExceptionFor(RegexFeature.UnicodeExpressions);
		super.testPosixCharacterClasses();
	}

	@Test
	@Override
	public void testUnicodeScriptsBlocksCategories() {

		expectSyntaxExceptionFor(RegexFeature.UnicodeExpressions);
		super.testUnicodeScriptsBlocksCategories();
	}

	@Test
	@Override
	public void testBackReferences() {

		expectSyntaxExceptionFor(RegexFeature.Backreferences);
		super.testBackReferences();
	}

	@Test
	@Override
	public void testNamedCapturingGroupsAndReferences() {

		expectSyntaxExceptionFor(RegexFeature.NamedCapturingGroupsAndReferences);
		super.testNamedCapturingGroupsAndReferences();
	}

	@Test
	@Override
	public void testNonCapturingAtomicGroups() {

		expectSyntaxExceptionFor(RegexFeature.PossessiveQuantifiers);
		super.testNonCapturingAtomicGroups();
	}

	@Test
	@Override
	public void testOctalExcapeSequences() {

		expectSyntaxExceptionFor(RegexFeature.OctalEscapeSequences);
		super.testOctalExcapeSequences();
	}

	@Test
	@Override
	public void testPossessiveQuantifiers() {

		expectSyntaxExceptionFor(RegexFeature.PossessiveQuantifiers);
		super.testPossessiveQuantifiers();
	}

	@Test
	@Override
	public void testPossessiveQuantifiersOnGroup() {

		expectSyntaxExceptionFor(RegexFeature.PossessiveQuantifiers);
		super.testPossessiveQuantifiersOnGroup();
	}

	@Test
	@Override
	public void testPossessiveQuantifiersOnCharacterClass() {

		expectSyntaxExceptionFor(RegexFeature.PossessiveQuantifiers);
		super.testPossessiveQuantifiersOnCharacterClass();
	}

	@Test
	@Override
	public void testAlternativeBegin() {

		expectSyntaxExceptionFor(RegexFeature.AlternativeBegin);
		super.testAlternativeBegin();
	}

	@Test
	@Override
	public void testAlternativeEnd() {

		expectSyntaxExceptionFor(RegexFeature.AlternativeEnd);
		super.testAlternativeEnd();
	}

	@Test
	@Override
	public void testAnyUnicodeLinebreakSequence() {

		expectSyntaxExceptionFor(RegexFeature.AnyUnicodeLinebreakSequence);
		super.testAnyUnicodeLinebreakSequence();
	}

	@Test
	@Override
	public void testEndOfPreviousMatch() {

		expectSyntaxExceptionFor(RegexFeature.EndOfPreviousMatch);
		super.testEndOfPreviousMatch();
	}

	@Test
	@Override
	public void testHorizontalWhitespace() {

		expectSyntaxExceptionFor(RegexFeature.HorizontalWhitespace);
		super.testHorizontalWhitespace();
	}

	@Test
	@Override
	public void testNonVerticalWhitespace() {

		expectSyntaxExceptionFor(RegexFeature.NonVerticalWhitespace);
		super.testNonVerticalWhitespace();
	}

	@Test
	@Override
	public void testCharacterClassUnion() {

		expectSyntaxExceptionFor(RegexFeature.CharacterClassUnion);
		super.testCharacterClassUnion();
	}

	@Test
	@Override
	public void testInlinedMatchFlags() {

		expectSyntaxExceptionFor(RegexFeature.InlinedMatchFlags);
		super.testInlinedMatchFlags();
	}

	@Test
	@Override
	public void testQuotationSequence() {

		expectSyntaxExceptionFor(RegexFeature.QuotationSequence);
		super.testQuotationSequence();
	}


	private void expectSyntaxExceptionFor(RegexFeature feature) {
		expectedSyntaxExceptionFor = feature;
	}

	@After
	public void clearExpectedSyntaxException() {
		featuresToTest.remove(expectedSyntaxExceptionFor);
		expectedSyntaxExceptionFor = null;
	}

	@BeforeClass
	public static void setUp() {

		for (RegexFeature feature : RegexFeature.values()) {
			if (!feature.isComplixityFeature()) {
				featuresToTest.add(feature);
			}
		}
	}

	@AfterClass
	public static void tearDown() {
		assertEquals("There must be no untested features", Collections.emptySet(), featuresToTest);
	}

	protected void assertMatches(boolean expected, String string, String pattern) {

		try {
			assertEquals("string: '" + string + "', pattern: '" + pattern + "'", expected,
					compile(pattern).matcher(string).matches());

			if (expectedSyntaxExceptionFor != null) {
				fail();
			}

		} catch (PatternSyntaxFeatureException e) {

			if (expectedSyntaxExceptionFor == null) {
				throw e;
			}

			assertEquals(expectedSyntaxExceptionFor, e.getFeature());

		}

	}

	protected Pattern compile(String regex) {
		return Pattern.compile(regex, RegexFeatureSet.ALL_DISABLED);

	}


}
