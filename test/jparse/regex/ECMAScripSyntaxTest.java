package jparse.regex;

import static org.junit.Assert.assertEquals;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.junit.Assert;
import org.junit.Test;


/**
 * Documents differences in semantic of common syntax between regular expression in Java (with default settings) and ECMA script / JavaScript
 * and ensures that features which are not supported in ECMA script / JavaScript are not allowed by
 * {@link RegexFeatureSet#ECMA_SCRIPT_REGEXP}.
 *
 * <p>
 * See https://www.ecma-international.org/ecma-262/8.0/index.html#sec-regular-expressions for more details about regular
 * expression syntax of the ECMA script standard
 * </p>
 *
 * @author rmunge
 */
public class ECMAScripSyntaxTest extends AbstractRegexSyntaxTest<jparse.regex.java.util.regex.Pattern> {

	// --------------------------------------------------------------------------------------------------------
	// --- Common syntax but (slightly) different semantics:

	@Test
	@Override
	public void testWhitespaceCharactersAdditionalUnicodes() {

		/*
		 * INCOMPATIBILITY to regular expressions in Java
		 * ECMA script has Unicode support for white spaces by default, while java doesn't
		 */
		assertMatches(false, "\\s", "\\s");
		assertMatchesOtherUnicodeSpaces(true, "\\s"); // DIFFERENCE
	}

	@Test
	@Override
	public void testNonWhitespaceCharactersAdditionalUnicodes() {

		/*
		 * INCOMPATIBILITY to regular expressions in Java
		 * ECMA script has Unicode support for white spaces by default, while java doesn't
		 */
		assertMatches(false, "\\S", "\\S");
		assertMatchesOtherUnicodeSpaces(false, "\\S"); // DIFFERENCE
	}

	@Test
	@Override
	public void testDotNewLineCharacter() {

		/*
		 * INCOMPATIBILITY to regular expressions in Java
		 * ECMA script counts next-line character (NEL) as new line character
		 * source: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp
		 */
		assertMatches(true, "\u0085", "."); // DIFFERENCE
	}

	@Test
	@Override
	public void testAdditionalVerticalWhitespaces() {

		/*
		 * INCOMPATIBILITY to regular expressions in Java
		 * Java recognizes additional vertical whitespace characters
		 */
		assertMatches(true, "\u000B", "\\v"); // vertical tab character,
		assertMatches(false, "\n", "\\v"); // DIFFERENCE
		assertMatches(false, "\r", "\\v"); // DIFFERENCE
		assertMatches(false, "\u0085", "\\v"); // DIFFERENCE
		assertMatches(false, "\u2028", "\\v"); // DIFFERENCE
		assertMatches(false, "\u2029", "\\v"); // DIFFERENCE

	}


	// --- Features which are supported in Java but not in ECMA script:

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
	public void testUnicodeSriptsBlocksCategoriesWithinCharacterClass() {
		super.testUnicodeSriptsBlocksCategoriesWithinCharacterClass();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testJavaCharacterClasses() {
		super.testJavaCharacterClasses();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testUnicodeScriptsBlocksCategories() {
		super.testUnicodeScriptsBlocksCategories();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testNamedCapturingGroupsAndReferences() {
		super.testNamedCapturingGroupsAndReferences();
	}

	@Test(expected = PatternSyntaxFeatureException.class)
	@Override
	public void testBackReferences() {
		super.testBackReferences();
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

	@Override
	protected void assertMatches(boolean expected, String string, String pattern) {

		compile(pattern);

		assertEquals("Expected result must not differ from ECMA script implementation (regex: " + pattern + " text: " + string +
				")",
				expected, matchECMAScriptRegex(pattern, string));

	}

	@Override
	protected jparse.regex.java.util.regex.Pattern compile(String regex) {
		return jparse.regex.java.util.regex.Pattern.compile(regex, RegexFeatureSet.ECMA_SCRIPT_REGEXP);
	}

	private boolean matchECMAScriptRegex(String regex, String text) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		try {

			final ScriptContext ctx = new SimpleScriptContext();
			ctx.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);


			String matchExact = "function matchExact(regex, str) {\n" +
					"   var match = str.match('^' + regex + '$');\n" +
					"   return match !== null && str == match[0];\n" +
					"}\n\n";

			String escapedRegex = escapeJavaScriptString(regex);
			String escapedText = escapeJavaScriptString(text);
			String javaScript = matchExact + "var matches = false;\nif (matchExact(\"" + escapedRegex + "\", \"" + escapedText +
					"\")) {\n  matches = true;\n}";

			engine.eval(javaScript, ctx);
			return Boolean.valueOf(ctx.getAttribute("matches").toString());

		} catch (ScriptException e) {

			e.printStackTrace();
			Assert.fail("No valid regex in ECMA script: " + e.getMessage());
			return false;
		}
	}

	private String escapeJavaScriptString(String string) {

		String escapedString = string.replace("\\", "\\\\").replace("\n", "\\n").replace("\t", "\\t").replace("\r", "\\r").replace(
				"\f", "\\f");

		for (String literal : UNICODE_LITERALS) {
			escapedString = escapedString.replace(literal, toUnicode(literal.charAt(0)));
		}

		return escapedString;

	}

	private String[] UNICODE_LITERALS = { "\u2028", "\u2029", "\u0085", "\u000B", "\u00a0", "\u00a0", "\u1680", "\u2000",
			"\u200a", "\u2028", "\u2029", "\u202f", "\u205f", "\u3000", "\ufeff" };


	private static String toUnicode(char ch) {
		return String.format("\\u%04x", (int) ch);
	}



}
