/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: AbstractRegexSyntaxTest.java
 * @date: 11.12.2017
 * @author: roland.mungenast
 */
package jparse.regex;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * Base class for unit tests which cover all features of default Java regular expressions that exist also in
 * regular expressions of ECMA script and Boost Xpressive.
 *
 * @author roland.mungenast
 */
public abstract class AbstractRegexSyntaxTest<CR> {

	@Test
	public void testUnicode() {
		assertMatches(true, "♜♞♝♛♚♝♞♜", "♜♞♝♛♚♝♞♜");
		assertMatches(true, "人不知而不慍，不亦君子乎？」", "人不知而不慍.*"); // Chinese
		assertMatches(true, "بِسْمِ ٱللّٰهِ ٱلرَّحْمـَبنِ ٱلرَّحِيمِ", "بِسْمِ ٱللّٰهِ ٱلرَّحْمـَبنِ ٱلرَّحِيمِ"); // Arabic
	}

	@Test
	public void testUnicodeSupplementaryCharacters() {
		// source: http://www.i18nguy.com/unicode/supplementary-test.html
		assertMatches(true, "𠜎x", "𠜎.");
	}

	@Test
	public void testWhitespaceCharactersAdditionalUnicodes() {

		/*
		 * Note:
		 * With default parameters Java's implementation knows only ASCII white spaces
		 * With optional flag, Pattern.UNICODE_CHARACTER_CLASS, JRE's java.util.regex.Pattern
		 * implementation interprets all additional Unicode white spaces except \ufeff
		 */
		assertMatches(false, "\\s", "\\s"); // not interpreted as literal
		assertMatchesOtherUnicodeSpaces(false, "\\s"); // but ignores non-ASCII whitespace characters

	}

	@Test
	public void testNonWhitespaceCharactersAdditionalUnicodes() {

		assertMatches(false, "\\S", "\\S"); // not interpreted as literal
		assertMatchesOtherUnicodeSpaces(true, "\\S"); // but matches additional Unicode whitespace characters like

	}

	@Test
	public void testAdditionalVerticalWhitespaces() {

		assertMatches(true, "\u000B", "\\v"); // vertical tab character,
		assertMatches(true, "\n", "\\v");
		assertMatches(true, "\r", "\\v");
		assertMatches(true, "\u0085", "\\v");
		assertMatches(true, "\u2028", "\\v");
		assertMatches(true, "\u2029", "\\v");

	}

	@Test
	public void testNonCapturingGroups() {

		assertMatches(true, "", "(?:(?:))");
		assertMatches(true, "XY", "(?:X)Y");
		assertMatches(true, "XY", "(?:(?:X)(?:Y))");
		assertMatches(false, "(?:text)", "(?:text)");
	}

	@Test
	public void testUnicodeCharacter() {

		assertMatches(true, "\u000B\r", "\\u000B\\u000D");
		assertMatches(false, "\u000B", "\\u000D");

		assertMatches(true, "\u000B\r", "\\x0B\\x0D");
		assertMatches(false, "\u000B", "\\x0D");

	}

	@Test
	public void testVerticalWhitespace() {
		assertMatches(true, "\u000B", "\\v");
	}

	@Test
	public void testControlCharacters() {

		// control M, carriage return (U+000D)
		assertMatches(true, "\r", "\\cM");
	}

	@Test
	public void testNull() {
		assertMatches(true, "\0", "\0");
	}

	@Test
	public void testDotNewLineCharacter() {
		assertMatches(false, "\u0085", ".");
	}


	/**
	 * (The dot, the decimal point) matches any single character except line terminators: \n, \r, \u2028 or \u2029.
	 */
	@Test
	public void testDotLineTerminators() {

		assertMatches(false, "\n", ".");
		assertMatches(false, "\r", ".");
		assertMatches(false, "\u2028", ".");
		assertMatches(false, "\u2029", ".");
		assertMatches(false, "\r\n", "..");

	}

	/**
	 * Inside a character classes, the dot loses its special meaning and matches a literal dot.
	 */
	@Test
	public void testDotInsideCharacterSet() {
		assertMatches(true, ".", "[.]");
	}

	@Test
	public void testCapturingGroup() {
		assertMatches(true, "", "()");
		assertMatches(true, "\0", "(\0)");
		assertMatches(true, "x", "(x)");
		assertMatches(true, "xy", "((x)y)");
		assertMatches(true, "information", "((infor)mation)");
	}

	@Test
	public void testBegin() {
		assertMatches(true, "", "^");
		assertMatches(true, "text", "^text");
		assertMatches(false, "text", "text^");
		assertMatches(true, "", "^$");
	}

	@Test
	public void testEnd() {
		assertMatches(true, "", "$");
		assertMatches(false, "text", "$text");
		assertMatches(true, "text", "text$");
		assertMatches(true, "", "^$");
	}


	@Test
	public void testAlternation() {
		assertMatches(true, "info", "info|debug|error");
		assertMatches(true, "debug", "info|debug|error");
		assertMatches(false, "ERROR", "info|debug|error");
		assertMatches(false, "information", "info|debug|error");
	}


	@Test
	public void testAlternativeExotics() {
		assertMatches(true, "a", "a|");
		assertMatches(true, "a", "a|b|");
		assertMatches(true, "b", "a|b|$");
		assertMatches(false, "x", "a|b|$");

		assertMatches(true, "b", "$|a|b");
		assertMatches(false, "x", "$|a|b");
		assertMatches(true, "a", "a||b");
		assertMatches(false, "x", "a||b");
		assertMatches(true, "", "|");
		assertMatches(false, " ", "|");

		assertMatches(false, "|a", "a");
		assertMatches(false, "|a|b", "a");
		assertMatches(false, "a||b", "b");
	}


	@Test
	public void testNoneDanglingMetaCharacters() {

		// ] and { have special meaning but in contrast to + ? * they are valid literals without escapes
		assertMatches(true, "]", "]");
		assertMatches(true, "}", "}");

	}


	@Test
	public void testQuantifierExact() {
		assertMatches(true, "x", "x{1}");
		assertMatches(false, "", "x{1}");
	}

	@Test
	public void testQuantifierExactZero() {
		assertMatches(true, "", "x{0}");
		assertMatches(false, "x", "x{0}");
		assertMatches(false, "xx", "x{0}");
	}

	@Test
	public void testQuantifierMin() {
		assertMatches(false, "x", "x{2,}");
		assertMatches(true, "xx", "x{2,}");
		assertMatches(true, "xxx", "x{2,}");
	}


	@Test
	public void testQuantifierRange() {
		assertMatches(true, "xx", "x{2,3}");
		assertMatches(true, "xxx", "x{2,3}");
		assertMatches(false, "xxxx", "x{2,3}");
	}

	@Test
	public void testQuantifierRangeZero() {
		assertMatches(true, "", "x{0,0}");
		assertMatches(false, "x", "x{0,0}");
	}

	/**
	 * \d ... Matches any digit (Arabic numeral).
	 */
	@Test
	public void testDigit() {

		for (int i = 0; i < 10; i++) {
			assertMatches(true, String.valueOf(i), "\\d");
		}

		assertMatches(false, "a", "\\d");

	}

	/**
	 * \d ... with default settings in Java non-western-arabic digits do not match
	 */
	@Test
	public void testDigitNonWesternArabic() {

		// Arabic-Indic digits
		for (int i = '\u0660'; i <= '\u0669'; i++) {
			assertMatches(false, "" + (char) i, "\\d");
		}

		// Extended Arabic-Indic digits
		for (int i = '\u06F0'; i <= '\u06F9'; i++) {
			assertMatches(false, "" + (char) i, "\\d");
		}

		// Devanagari digits
		for (int i = '\u0966'; i <= '\u096F'; i++) {
			assertMatches(false, "" + (char) i, "\\d");
		}

		// Fullwidth digits
		for (int i = '\uFF10'; i <= '\uFF19'; i++) {
			assertMatches(false, "" + (char) i, "\\d");
		}
	}

	/**
	 * \D ... Matches any character that is not a digit (Arabic numeral). Equivalent to [^0-9].
	 */
	@Test
	public void testNoDigit() {

		for (int i = 0; i < 10; i++) {
			assertMatches(false, String.valueOf(i), "\\D");
		}

		assertMatches(true, "a", "\\D");
	}

	/**
	 * \w ... Matches any alphanumeric character from the basic Latin alphabet, including the underscore. Equivalent to
	 * [A-Za-z0-9_].
	 */
	@Test
	public void testWordCharacters() {

		assertMatchesBasicLatinAlphabet(true, "\\w");
		assertMatches(false, "$", "\\w");
	}

	/**
	 * \W ... Matches any character that is not a word character from the basic Latin alphabet. Equivalent to [^A-Za-z0-9_].
	 */
	@Test
	public void testNonWordCharacters() {

		assertMatchesBasicLatinAlphabet(false, "\\W");
		assertMatches(true, "$", "\\W");
	}

	/**
	 * \s ... Matches a single white space character, including space, tab, form feed, line feed and other Unicode spaces.
	 * Equivalent to
	 * [ \f\n\r\t\v\u00a0\u1680\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff].
	 */
	@Test
	public void testWhitespaceCharacters() {

		assertMatchesSpaces(true, "\\s");
	}


	@Test
	public void testNonWhitespaceCharacters() {
		assertMatchesSpaces(false, "\\S");
	}


	/**
	 * [xyz] ...
	 * x, y, or z (simple class)
	 */
	@Test
	public void testCharacterClass() {

		assertMatches(true, "x", "[xyz]");
		assertMatches(true, "y", "[xyz]");
		assertMatches(true, "z", "[xyz]");

		assertMatches(false, "w", "[xyz]");
		assertMatches(false, "0", "[xyz]");
	}

	/**
	 * [^xyz] ...
	 * any character except x, y, or z (negation)
	 */
	@Test
	public void testCharacterClassNegation() {

		assertMatches(false, "x", "[^xyz]");
		assertMatches(false, "y", "[^xyz]");
		assertMatches(false, "z", "[^xyz]");

		assertMatches(true, "w", "[^xyz]");
		assertMatches(true, "0", "[^xyz]");
	}

	/**
	 * [a-c] ...
	 * a through c or A through C, inclusive (range)
	 */
	@Test
	public void testCharacterClassRange() {

		assertMatches(true, "a", "[a-c]");
		assertMatches(true, "c", "[a-c]");
		assertMatches(true, "c", "[a-c]");

		assertMatches(false, "d", "[xyz]");
		assertMatches(false, "0", "[xyz]");
	}

	/**
	 * [^a-c] ...
	 * any character except a through c or A through C, inclusive (range)
	 */
	@Test
	public void testCharacterClassRangeNegation() {

		assertMatches(false, "a", "[^a-c]");
		assertMatches(false, "c", "[^a-c]");
		assertMatches(false, "c", "[^a-c]");

		assertMatches(true, "d", "[^xyz]");
		assertMatches(true, "0", "[^xyz]");
	}

	@Test
	public void testCharacterClassUnion() {

		assertMatchesRange(true, 'a', 'd', "[a-d[m-p]]");
		assertMatchesRange(false, 'e', 'l', "[a-d[m-p]]");
		assertMatchesRange(true, 'm', 'p', "[a-d[m-p]]");
	}

	/**
	 * [a-g&&[def]] ...
	 * d, e, or f (intersection)
	 */
	@Test
	public void testCharacterClassIntersection() {

		assertMatches(true, "d", "[a-g&&[def]]");
		assertMatches(true, "e", "[a-g&&[def]]");
		assertMatches(true, "f", "[a-g&&[def]]");
		assertMatches(false, "g", "[a-g&&[def]]");
	}

	/**
	 * [a-z&&[^bc]] ...
	 * a through z, except for b and c: [ad-z] (subtraction)
	 */
	@Test
	public void testCharacterClassIntersectionWithNegation() {

		String pattern = "[a-z&&[^bc]]";

		for (char ch = 'a'; ch <= 'z'; ch++) {

			String str = "" + ch;
			boolean matches = true;

			if (ch == 'b' || ch == 'c') {
				matches = false;
			}

			assertMatches(matches, str, pattern);
		}
	}

	/**
	 * [a-z&&[^m-p]] ...
	 * a through z, and not m through p: [a-lq-z](subtraction)
	 */
	@Test
	public void testCharacterClassIntersectionWithNegatedRange() {

		String pattern = "[a-z&&[^m-p]]";

		for (char ch = 'a'; ch <= 'z'; ch++) {

			String str = "" + ch;

			boolean matches = false;

			if (ch >= 'a' && ch <= 'l')  {
				matches = true;

			} else if (ch >= 'q' && ch <= 'z')  {
				matches = true;
			}

			assertMatches(matches, str, pattern);
		}

	}

	/**
	 * If the intersected class does not need a negating caret, then Java and Ruby allow you to omit the nested square brackets: [class&&intersect].
	 * Source: https://www.regular-expressions.info/charclassintersect.html
	 */
	@Test
	public void testCharacterClassIntersectionSimplifiedSyntax() {

		String pattern = "[a-g&&def]";

		assertMatches(true, "d", pattern);
		assertMatches(true, "e", pattern);
		assertMatches(true, "f", pattern);
		assertMatches(false, "g", pattern);
	}



	@Test
	public void testPosixCharacterClasses() {

		assertMatchesRange(true, 'a', 'z', "\\p{Lower}");

		assertMatchesRange(true, 'A', 'Z', "\\p{Upper}");

		assertMatchesRange(true, '\u0000', '\u007F', "\\p{ASCII}");

		// \p{Alpha}
		assertMatchesRange(true, 'a', 'z', "\\p{Alpha}");
		assertMatchesRange(true, 'A', 'Z', "\\p{Alpha}");

		// \p{Digit}
		assertMatchesRange(true, '0', '9', "\\p{Digit}");

		// \p{Punct}
		assertMatchesEverySingleCharacter(true, "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", "\\p{Punct}");

		// \p{Graph}
		assertMatchesRange(true, 'a', 'z', "\\p{Graph}");
		assertMatchesRange(true, 'A', 'Z', "\\p{Graph}");
		assertMatchesEverySingleCharacter(true, "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", "\\p{Graph}");

		// \p{Print}
		assertMatchesRange(true, 'a', 'z', "\\p{Print}");
		assertMatchesRange(true, 'A', 'Z', "\\p{Print}");
		assertMatchesEverySingleCharacter(true, "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", "\\p{Print}");
		assertMatches(true, "\u0020", "\\p{Print}");

		// \p{Blank}
		assertMatchesEverySingleCharacter(true, " \t", "\\p{Blank}");

		// \p{Cntrl}
		assertMatchesRange(true, '\u0000', '\u001F', "\\p{Cntrl}");
		assertMatches(true, "\u007F", "\\p{Cntrl}");

		// \p{XDigit}
		assertMatchesRange(true, '0', '9', "\\p{XDigit}");
		assertMatchesRange(true, 'a', 'f', "\\p{XDigit}");
		assertMatchesRange(true, 'A', 'F', "\\p{XDigit}");

		// \p{Space}
		assertMatchesEverySingleCharacter(true, " \t\n\u000B\f\r", "\\p{Space}");

	}

	@Test
	public void testJavaCharacterClasses() {

		for (char ch = '\u0000'; ch < '\u007F'; ch++) { // test with all ASCII characters
			assertMatches(java.lang.Character.isLowerCase(ch), "" + ch, "\\p{javaLowerCase}");
		}

		for (char ch = '\u0000'; ch < '\u007F'; ch++) { // test with all ASCII characters
			assertMatches(java.lang.Character.isUpperCase(ch), "" + ch, "\\p{javaUpperCase}");
		}

		for (char ch = '\u0000'; ch < '\u007F'; ch++) { // test with all ASCII characters
			assertMatches(java.lang.Character.isWhitespace(ch), "" + ch, "\\p{javaWhitespace}");
		}

		for (char ch = '\u0000'; ch < '\u007F'; ch++) { // test with all ASCII characters
			assertMatches(java.lang.Character.isMirrored(ch), "" + ch, "\\p{javaMirrored}");
		}

	}


	@Test
	public void testUnicodeScriptsBlocksCategories() {

		// Note: we test all syntax variants but not all available scripts/blocks/categories.
		// We expect that if one script/block/category is supported, all categories are supported.

		// scripts
		assertMatches(true, "あ", "\\p{IsHiragana}"); // source: https://en.wikipedia.org/wiki/Hiragana
		assertMatches(true, "あ", "\\p{script=Hiragana}");
		assertMatches(true, "あ", "\\p{sc=Hiragana}");

		// blocks
		assertMatches(true, "᠀", "\\p{InMongolian}"); // source: https://en.wikipedia.org/wiki/Mongolian_(Unicode_block)
		assertMatches(true, "᠀", "\\p{block=Mongolian}");
		assertMatches(true, "᠀", "\\p{blk=Mongolian}");

		// categories
		assertMatchesRange(true, 'A', 'Z', "\\p{general_category=Lu}"); // Lu ... upper case letter
		assertMatchesRange(true, 'A', 'Z', "\\p{gc=Lu}");

	}

	/**
	 * "... scripts, blocks, categories and binary properties can be used both inside and outside of a character class."
	 * source: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#Unicode support
	 */
	@Test
	public void testUnicodeSriptsBlocksCategoriesWithinCharacterClass() {

		String regex = "[\\p{IsHiragana}\\p{blk=Mongolian}\\p{gc=Lu}]";

		assertMatches(true, "あ", regex);
		assertMatches(true, "᠀", regex);
		assertMatches(true, "A", regex);
		assertMatches(false, "0", regex);
	}


	@Test
	public void testBoundaryBegin() {
		assertMatches(true, "", "^");
		assertMatches(true, "red", "^red");
		assertMatches(false, "red", "red^");

		assertMatches(true, "a^b", "a\\^b");
	}

	@Test
	public void testBoundaryEnd() {
		assertMatches(true, "", "$");
		assertMatches(false, "red", "$red");
		assertMatches(true, "red", "red$");

		assertMatches(true, "a$b", "a\\$b");
	}

	@Test
	public void testWordBoundary() {
		assertMatches(true, "two words", "\\btwo words");
		assertMatches(true, "two words", "two \\bwords");

		assertMatches(true, "key=value", "key=(.*)\\b");
		assertMatches(true, "key=value;anotherkey=anotherValue", "key=(.*)\\b.*");
	}

	@Test
	public void testNoWordBoundary() {
		assertMatches(true, "two words", "tw\\Bo words");
		assertMatches(true, "two words", "two wo\\Brds");

		assertMatches(false, "two words", "\\Btwo words");
	}


	@Test
	public void testGroupNonCapturing() {

		assertMatches(true, "x", "(?:x)");
		assertMatches(true, "xy", "(?:(?:x)y)");
	}

	@Test
	public void testQuantifierZeroOrMore() {

		assertMatches(true, "", "x*");
		assertMatches(true, "x", "x*");
		assertMatches(true, "xx", "x*");
	}

	@Test
	public void testQuantifierZeroOrMore_NonGreedy() {

		assertMatches(true, "", "x*?");
		assertMatches(true, "x", "x*?");
		assertMatches(true, "xx", "x*?");
	}

	@Test
	public void testQuantifierOneOrMore() {

		assertMatches(false, "", "x+");
		assertMatches(true, "x", "x+");
		assertMatches(true, "xx", "x+");
	}

	@Test
	public void testQuantifierOneOrMore_NonGreedy() {

		assertMatches(false, "", "x+?");
		assertMatches(true, "x", "x+?");
		assertMatches(true, "xx", "x+?");
	}


	@Test
	public void testQuantifierOptional() {

		assertMatches(true, "", "x?");
		assertMatches(true, "x", "x?");
		assertMatches(false, "xx", "x?");

		assertMatches(true, "", "x{0,1}");
		assertMatches(true, "x", "x{0,1}");
		assertMatches(false, "xx", "x{0,1}");

	}


	@Test
	public void testQuantifierOptional_NonGreedy() {

		assertMatches(true, "", "x??");
		assertMatches(true, "x", "x??");
		assertMatches(false, "xx", "x??");
	}

	@Test
	public void testPositiveLookahead() {

		assertMatches(true, "JackSprat", "Jack(?=Sprat)Sprat");
		assertMatches(false, "Jack", "Jack(?=Sprat)");

	}

	@Test
	public void testNegativeLookahead() {

		assertMatches(true, "3", "\\d+(?!\\.)");
		assertMatches(false, "3.141", "\\d+(?!\\.)");
	}

	@Test
	public void testNegativeLookbehind() {

		assertMatches(true, "b", "(?<!a)b");
		assertMatches(false, "ab", "(?<!a)b");
	}

	@Test
	public void testPositiveLookbehind() {

		assertMatches(false, "b", "(?<=a)b");
		// there is not positive example; a complete string will never match
	}

	@Test
	public void testBackReferences() {

		assertMatches(true, "0101", "(\\d\\d)\\1");
		assertMatches(false, "0102", "(\\d\\d)\\1");
	}

	@Test
	public void testNamedCapturingGroupsAndReferences() {

		// named-capturing group + back reference
		assertMatches(true, "texttext", "(?<name>.*)\\k<name>");

		assertMatches(false, "text", "(?<name>.+)\\\\k<name>");
	}


	@Test
	public void testPossessiveQuantifiers() {

		assertMatches(false, "'abc'x", "'.*+'x");  // non-possessive variant, "'.*'x" would match
		assertMatches(false, "''x", "'.?+'x"); // non-possessive variant, "'.?'x" would match
		assertMatches(false, "'abc'x", "'.++'x"); // non-possessive variant, "'.+'x" would match


	}

	@Test
	public void testPossessiveQuantifiersOnGroup() {

		assertMatches(false, "'abc'x", "'(.)*+'x");
		assertMatches(false, "''x", "'(.)?+'x");
		assertMatches(false, "'abc'x", "'(.)++'x");
	}


	@Test
	public void testPossessiveQuantifiersOnCharacterClass() {

		assertMatches(false, "'abc'x", "'[abc']*+'x");
		assertMatches(false, "''x", "'[.']?+'x");
		assertMatches(false, "'abc'x", "'[a-c']++'x");
	}


	@Test
	public void testNonCapturingAtomicGroups() {

		/*
		 * Note: (?>X) X, ... an independent, non-capturing atomic group
		 * is semantically equivalent to possessive quantifiers, but it has its own syntax
		 */
		assertMatches(false, "'abc'x", "(?>'.*+'x)");
		assertMatches(false, "''x", "(?>'.?+'x)");
		assertMatches(false, "'abc'x", "(?>'.++'x)");

	}

	@Test
	public void testOctalExcapeSequences() {

		// \0n ... The character with octal value 0n (0 <= n <= 7)
		assertMatches(true, "\t", "\\011");
		assertMatches(false, "\n", "\\011");

		// \0nn ... The character with octal value 0nn (0 <= n <= 7)
		assertMatches(true, "\u000B", "\\013");  // 000B in hex is 13 in octal
		assertMatches(false, "\0", "\\013");


		// \0mnn ... The character with octal value 0mnn (0 <= m <= 3, 0 <= n <= 7)
		assertMatches(true, "\u00BF", "\\0277"); // 377 in octal is 00FF in hex
		assertMatches(false, "\0", "\\0277");
	}


	@Test
	public void testAlternativeEnd() {

		// \Z ... The end of the input but for the final terminator, if any

		assertMatches(true, "", "\\Z");
		assertMatches(false, "text", "\\Ztext");
		assertMatches(true, "text", "text\\Z");
		assertMatches(true, "", "^\\Z");
		assertMatches(false, "joe\n", "[a-z]+\\Z");


		// \z ... The end of the input
		// Note: difference between \Z and \z is not relevant for Matcher#find()
		assertMatches(true, "", "\\z");
		assertMatches(false, "text", "\\ztext");
		assertMatches(true, "text", "text\\z");
		assertMatches(true, "", "^\\z");
		assertMatches(false, "joe\n", "[a-z]+\\z");

	}

	@Test
	public void testNonVerticalWhitespace() {

		// \V ... A non-vertical whitespace character: [^\v]

		assertMatches(false, "\u000B", "\\V"); // vertical tab character,
		assertMatches(false, "\n", "\\V");
		assertMatches(false, "\r", "\\V");
		assertMatches(false, "\u0085", "\\V");
		assertMatches(false, "\u2028", "\\V");
		assertMatches(false, "\u2029", "\\V");

		assertMatchesRange(true, '\u0000', '\u0009', "\\V");
		assertMatchesRange(true, '\u000E', '\u007F', "\\V");

	}

	@Test
	public void testAlternativeBegin() {

		// \A is equivalent to ^ ... The beginning of the input

		assertMatches(true, "", "\\A");
		assertMatches(true, "text", "\\Atext");
		assertMatches(false, "text", "text\\A");
		assertMatches(true, "", "\\A$");
	}

	@Test
	public void testEndOfPreviousMatch() {

		// \G ... The end of the previous match
		// Note: we can only test that the construct is not interpreted as a simple literal.
		// This construct makes only sense when e.g. using Pattern.find()

		assertMatches(true, "", "\\G");
		assertMatches(false, "\\G", "\\G");

	}

	@Test
	public void testHorizontalWhitespace() {

		assertMatches(true, "\t", "\\h");
		assertMatches(false, "\t", "\\H");
	}

	@Test
	public void testAnyUnicodeLinebreakSequence() {

		// \R ... Any Unicode linebreak sequence,
		// Note: introduced in Java 8
		assertMatchesEverySingleCharacter(true, "" + '\n' + '\u000B' + '\u000C' + '\r' + '\u0085' + '\u2028' + '\u2029', "\\R");
	}

	@Test
	public void testQuotationSequence() {

		assertMatches(true, "[name]", "\\Q[name]\\E");
	}

	@Test
	public void testInlinedMatchFlags() {

		// (?i) case insensitive
		assertMatches(true, "aBc", "(?i)abc");
		assertMatches(false, "aBc", "(?-i)abc");
		assertMatches(false, "ς", "(?i)σ");

		// (?u) Unicode-aware case folding
		assertMatches(true, "ς", "(?i)(?u)σ");

		// (?d) Unix lines mode
		assertMatches(false, "\n", "(?d).");
		assertMatches(true, "\r", "(?d).");
		assertMatches(true, "\u2028", "(?d).");
		assertMatches(true, "\u2029", "(?d).");

		// (?s) dotall mode
		assertMatches(true, "\n", "(?s).");
		assertMatches(true, "\r", "(?s).");
		assertMatches(true, "\u2028", "(?s).");
		assertMatches(true, "\u2029", "(?s).");
		assertMatches(true, "\r\n", "(?s)..");

		// (?m) multiline mode
		assertMatches(true, "line\nline\n", "(?m)(^line$\\n){2}");
		assertMatches(false, "line\nline\n", "(?-m)(^line$\\n){2}");

		// (?x) comments
		assertMatches(true, "text", "(?x).*#example for a regex with a comment");
		assertMatches(false, "text", "(?-x).*#example for a regex with a comment");

		// (?U)
		assertMatches(true, "ς", "(?U)\\p{Lower}");
	}

	private void assertMatchesSpaces(boolean matches, String regex) {

		assertMatches(matches, " ", regex);
		assertMatches(matches, "\f", regex);
		assertMatches(matches, "\n", regex);
		assertMatches(matches, "\r", regex);
		assertMatches(matches, "\t", regex);
		assertMatches(matches, "\u000B", regex); // \v in ECMA script
	}

	protected final void assertMatchesBasicLatinAlphabet(boolean matches, String regex) {

		for (char i = 'a'; i <= 'z'; i++) {
			assertMatches(matches, "" + i, regex);
		}

		for (char i = 'A'; i <= 'Z'; i++) {
			assertMatches(matches, "" + i, regex);
		}

		for (int i = 0; i < 10; i++) {
			assertMatches(matches, String.valueOf(i), regex);
		}

		assertMatches(matches, "_", regex);

	}

	protected final void assertMatchesEverySingleCharacter(boolean matches, String characters, String regex) {

		for (int i = 0; i < characters.length(); i++) {
			assertMatches(matches, "" + characters.charAt(i), regex);
		}
	}

	protected final void assertMatchesRange(boolean matches, char fromInclusive, char toInclusive, String regex) {

		for (char ch = fromInclusive; ch <= toInclusive; ch++) {
			assertMatches(matches, "" + ch, regex);
		}
	}


	/**
	 * Asserts that a given regular expression matches additional Unicode whitespace characters
	 * (which are not part of ASCII)
	 *
	 * @param matches true if it is expected that regex matches additional Unicode spaces
	 * @param regex regular expression
	 */
	protected void assertMatchesOtherUnicodeSpaces(boolean matches, String regex) {

		/*
		 * Source:
		 *
		 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp
		 * https://www.ecma-international.org/publications/files/ECMA-ST/Ecma-262.pdf
		 */

		assertMatches(matches, "\u1680", regex);
		assertMatches(matches, "\u2000", regex);
		assertMatches(matches, "\u200a", regex);
		assertMatches(matches, "\u2028", regex);
		assertMatches(matches, "\u2029", regex);
		assertMatches(matches, "\u202f", regex);
		assertMatches(matches, "\u205f", regex);
		assertMatches(matches, "\u3000", regex);
		assertMatches(matches, "\ufeff", regex);
	}

	/**
	 * @param expected <code>true</code> if it is expected that <tt>pattern</tt> matches <tt>string</tt>
	 * @param string string to match
	 * @param pattern regular expression
	 */
	protected abstract void assertMatches(boolean expected, String string, String pattern);

	protected final void assertMatches(boolean expected, char ch, String pattern) {
		assertMatches(expected, Character.toString(ch), pattern);
	}

	protected void verifyNoSyntaxError(String regex) {
		RuntimeException e = compileAndCatch(regex);

		if (e != null) {
			throw e;
		}
	}

	protected void verifySyntaxError(RegexFeature expectedRestrictionType, String regex) {

		Exception exception = compileAndCatch(regex);

		assertEquals("Unexpected exception: " + exception, PatternSyntaxFeatureException.class, exception == null ? null
				: exception.getClass());
		assertEquals(expectedRestrictionType, ((PatternSyntaxFeatureException) exception).getFeature());
	}

	protected final RuntimeException compileAndCatch(String regex) {
		try {

			compile(regex);
			return null;

		} catch (RuntimeException e) {
			return e;
		}
	}

	protected abstract CR compile(String regex);

}
