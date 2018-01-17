/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: ComplexRegexSyntaxTest.java
 * @date: 13.12.2017
 * @author: roland.mungenast
 */
package jparse.regex;

import static org.junit.Assert.assertEquals;
import jparse.regex.java.util.regex.Pattern;

import org.junit.Test;


/**
 * Ensures correct detection of complexity features for all <pre>RegexFeature.Complexity_*</pre>
 *
 * @author rmunge
 */
public class ComplexityTest extends AbstractRegexSyntaxTest<Pattern> {

	private static final RegexFeatureSet FEATURES = new RegexFeatureSet(RegexFeatureSet.JAVA_DEFAULT);

	static {
		FEATURES.setFeatureEnabled(RegexFeature.Complexity_CompoundQuantifiers, false);
		FEATURES.setFeatureEnabled(RegexFeature.Complexity_OverlappingQuantifiedTokens, false);
		FEATURES.setFeatureEnabled(RegexFeature.Complexity_UnrestrictedNestedGroups, false);
		FEATURES.setFeatureEnabled(RegexFeature.Complexity_UnrestrictedLength, false);
		FEATURES.setIgnoredOverlappingQuantifiers(0);
	}

	@Test
	public void testCompoundQuantifiers() {

		// Inspired by:
		// http://www.rexegg.com/regex-explosive-quantifiers.html and
		// https://www.regular-expressions.info/catastrophic.html

		/*
		 * A Quantifier Compounds a Quantifier
		 *
		 * *,+ and {m,n} quantifiers within a group are not allowed when the group also has a *,+ or {m,n} quantifier.
		 * This combination can easily lead to an explosion of the matching time, especially when a long string does not match.
		 *
		 * Possessive variants of the quantifiers are ignored.
		 * Groups within an independent group are ignored.
		 */

		verifySyntaxError(RegexFeature.Complexity_CompoundQuantifiers, "(A+)*");
		verifyNoSyntaxError("(A+)*+");
		verifyNoSyntaxError("(?>(A+)*)");

		verifySyntaxError(RegexFeature.Complexity_CompoundQuantifiers, "^(A+)*B");
		verifyNoSyntaxError("^(A+)*+B");
		verifyNoSyntaxError("^(?>(A+)*)B");

		verifySyntaxError(RegexFeature.Complexity_CompoundQuantifiers, "(?:\\D+|0(?!1))*");
		verifyNoSyntaxError("(?:\\D+|0(?!1))*+");

		verifySyntaxError(RegexFeature.Complexity_CompoundQuantifiers, "(?:\\D+|0(?!1))*");
		verifyNoSyntaxError("(?:\\D+|0(?!1))*+");

		verifySyntaxError(RegexFeature.Complexity_CompoundQuantifiers, "^(.*?,){11}P");

		try {
			FEATURES.setFeatureEnabled(RegexFeature.Complexity_UnrestrictedNestedGroups, true);
			verifyNoSyntaxError("(?>(?:\\D+|0(?!1))*)");

		} finally {
			FEATURES.setFeatureEnabled(RegexFeature.Complexity_UnrestrictedNestedGroups, false);
		}

	}


	@Test
	public void testOverlappingQuantifierTokens_IgnoreFirst() {

		int defaultValue = FEATURES.getIgnoredOverlappingQuantifiers();

		try {

			FEATURES.setIgnoredOverlappingQuantifiers(1);
			verifyNoSyntaxError(".*.*");
			verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "a+.*.*");
			verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "\\d+\\d*\\d+");


		} finally {
			FEATURES.setIgnoredOverlappingQuantifiers(defaultValue);
		}
	}

	@Test
	public void testOverlappingQuantifierTokens() {

		// Inspired by:
		// http://www.rexegg.com/regex-explosive-quantifiers.html and
		// https://www.regular-expressions.info/catastrophic.html

		/*
		 * Contiguous quantified tokens which are not mutually exclusive.
		 *
		 * It is impossible to detect overlaps between different types of tokens in a generic way.
		 * Therefore, overlaps are only detected for selected tokens:
		 *
		 * - \d and \w
		 * - POSIX character classes (US-ASCII only)
		 * - . and single characters
		 *
		 */

		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "^\\d+\\w*@");
		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "^\\d+\\w{1,}@");
		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "^\\d+\\w{1}@");
		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "^\\d*\\w+@");

		// only problematic when we have a combination of + * and {cmin,cmax} where cmax > 1
		verifyNoSyntaxError("\\d\\w");
		verifyNoSyntaxError("\\d\\w*");
		verifyNoSyntaxError("\\d*\\w");
		verifyNoSyntaxError("\\d\\w+");
		verifyNoSyntaxError("\\d?\\w");
		verifyNoSyntaxError("\\d?\\w{1}");

		// POSIX character classes
		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "^\\p{Alpha}+\\p{Lower}*@");
		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "^\\p{Upper}+\\p{Alpha}*@");
		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "^\\p{Alnum}+\\d*@");
		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "^\\p{Graph}+\\p{Punct}*@");

		// DOT
		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "^.*A+.*AB");
		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "^.*\\d+");
		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, ".*.+");

		// single characters
		verifySyntaxError(RegexFeature.Complexity_OverlappingQuantifiedTokens, "a*a+");
		verifyNoSyntaxError("a*b+");
		verifyNoSyntaxError("aaa+");


	}


	@Test
	public void testExamplesFromWikipedia() {

		// https://en.wikipedia.org/wiki/ReDoS

		verifySyntaxError(RegexFeature.Complexity_CompoundQuantifiers, "(a+)+");
		verifySyntaxError(RegexFeature.Complexity_CompoundQuantifiers, "([a-zA-Z]+)*");
		verifySyntaxError(RegexFeature.Complexity_CompoundQuantifiers, "(.*a){11}");

		verifySyntaxError(RegexFeature.Complexity_CompoundQuantifiers,
				"^([a-zA-Z0-9])(([\\-.]|[_]+)?([a-zA-Z0-9]+))*(@){1}[a-z0-9]+[.]{1}(([a-z]{2,3})|([a-z]{2,3}[.]{1}[a-z]{2,3}))$");
		verifySyntaxError(RegexFeature.Complexity_CompoundQuantifiers, "^(([a-z])+.)+[A-Z]([a-z])+$");
	}


	@Test
	public void testNotAllowedNestedGroups() {

		/*
		 *  source: https://stackoverflow.com/questions/800813/what-is-the-most-difficult-challenging-regular-expression-you-have-ever-written
		 */
		verifySyntaxError(
				RegexFeature.Complexity_UnrestrictedNestedGroups,
				"^(?:(?:(?:0?[13578]|1[02])(\\/|-|\\.)31)\\1|(?:(?:0?[13-9]|1[0-2])(\\/|-|\\.)(?:29|30)\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:0?2(\\/|-|\\.)29\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:(?:0?[1-9])|(?:1[0-2]))(\\/|-|\\.)(?:0?[1-9]|1\\d|2[0-8])\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$");

		verifySyntaxError(RegexFeature.Complexity_UnrestrictedNestedGroups, "a(b(c(d(e)))");
	}

	@Test
	public void testAllowedNestedGroups() {
		verifyNoSyntaxError("");
		verifyNoSyntaxError(".*");
		verifyNoSyntaxError("(a(b(c)))");
		verifyNoSyntaxError("a(b(c(d)))");
		verifyNoSyntaxError("(a(b(c)))");
		verifyNoSyntaxError("the ((red|white) (king|queen))");
	}


	@Test
	public void testUnrestrictedLength() {

		int maxLength = FEATURES.getMaxRegexLength();
		StringBuilder regex = new StringBuilder(maxLength);
		for (int i = 0; i < FEATURES.getMaxRegexLength(); i++) {
			regex.append("x");
		}

		String regexWithMaxAllowedLength = regex.toString();

		verifySyntaxError(RegexFeature.Complexity_UnrestrictedLength, regexWithMaxAllowedLength + 'x');
		verifyNoSyntaxError(regexWithMaxAllowedLength);
	}

	@Test
	public void testCustomMaxLength() {

		int defaultLength = FEATURES.getMaxRegexLength();

		try {
			FEATURES.setMaxRegexLength(1);

			verifySyntaxError(RegexFeature.Complexity_UnrestrictedLength, "ab");
			verifyNoSyntaxError("a");

		} finally {
			FEATURES.setMaxRegexLength(defaultLength);
		}
	}




	@Test
	public void testExtremeCases() {

		/*
		 * source: http://www.ex-parrot.com/~pdw/Mail-RFC822-Address.html
		 */

		verifySyntaxError(
				RegexFeature.Complexity_CompoundQuantifiers, "(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t]\r\n" +
		")+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\r\n" +
		"\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(\r\n" +
		"?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \r\n" +
		"\\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\0\r\n" +
		"31]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\\r\n" +
		"](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+\r\n" +
		"(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:\r\n" +
		"(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z\r\n" +
		"|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)\r\n" +
		"?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\\r\n" +
		"r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[\r\n" +
		" \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)\r\n" +
		"?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t]\r\n" +
		")*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[\r\n" +
		" \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*\r\n" +
		")(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t]\r\n" +
		")+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)\r\n" +
		"*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+\r\n" +
		"|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\r\n" +
		"\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\r\n" +
		"\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t\r\n" +
		"]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031\r\n" +
		"]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](\r\n" +
		"?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?\r\n" +
		":(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?\r\n" +
		":\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?\r\n" +
		":(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?\r\n" +
		"[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \r\n" +
		"\\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\r\n" +
		"\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>\r\n" +
		"@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"\r\n" +
		"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t]\r\n" +
		")*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\r\n" +
		"\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?\r\n" +
		":[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\r\n" +
		"\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\r\n" +
		"\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(\r\n" +
		"?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;\r\n" +
		":\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([\r\n" +
		"^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\"\r\n" +
		".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\\r\n" +
		"]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\\r\n" +
		"[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\\r\n" +
		"r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \r\n" +
		"\\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]\r\n" +
		"|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\0\r\n" +
		"00-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\\r\n" +
		".|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,\r\n" +
		";:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?\r\n" +
		":[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*\r\n" +
		"(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\r\n" +
		"\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[\r\n" +
		"^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]\r\n" +
		"]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(\r\n" +
		"?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\r\n" +
		"\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(\r\n" +
		"?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\r\n" +
		"\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t\r\n" +
		"])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t\r\n" +
		"])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?\r\n" +
		":\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\r\n" +
		"\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:\r\n" +
		"[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\\r\n" +
		"]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)\r\n" +
		"?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"\r\n" +
		"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)\r\n" +
		"?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>\r\n" +
		"@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[\r\n" +
		" \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,\r\n" +
		";:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t]\r\n" +
		")*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\r\n" +
		"\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?\r\n" +
		"(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\r\n" +
		"\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\r\n" +
		"\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\r\n" +
		"\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])\r\n" +
		"*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])\r\n" +
		"+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\\r\n" +
		".(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z\r\n" +
		"|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(\r\n" +
		"?:\\r\\n)?[ \\t])*))*)?;\\s*)");
	}

	protected void assertMatches(boolean expected, String string, String pattern) {

		assertEquals("string: '" + string + "', pattern: '" + pattern + "'", expected,
				Pattern.compile(pattern, FEATURES).matcher(string).matches());

	}


	@Override
	protected Pattern compile(String regex) {
		return Pattern.compile(regex, FEATURES);
	}



}
