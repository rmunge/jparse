/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: RegexFeature.java
 * @date: 12.12.2017
 * @author: roland.mungenast
 */
package jparse.regex;


/**
 * Selected Regular Expression syntax features.
 *
 * @author rmunge
 */
public enum RegexFeature implements java.io.Serializable {

	/**
	 * Possessive quantifiers (*+, ++, ?+) and independent, non-capturing groups(?>X).
	 */
	PossessiveQuantifiers("Possessive quantifiers (*+, ++, ?+) and independent, non-capturing groups (?>X)", false),

	/**
	 * Unicode scripts, blocks, categories and binary properties with \\p and \\P".
	 */
	UnicodeExpressions("Unicode scripts, blocks, categories and binary properties with \\p and \\P", false),

	/**
	 * Back references to capturing groups (e.g. \\\\1, \\\\2)
	 */
	Backreferences("Back references (e.g. \\\\1, \\\\2)", false),

	/**
	 * Escaped characters with octal values (\0n, \0nn, \0mnn).
	 */
	OctalEscapeSequences("Octal escape sequences (e.g. \\\\01)", false),

	/**
	 * \A, the beginning of the input; an alternative to ^.
	 */
	AlternativeBegin("\\\\A, the beginning of the input; an alternative to ^", false),

	/**
	 * \z and \\Z, the end of the input and the end of the input but for the final terminator, if any.
	 * Both are alternatives to $.
	 */
	AlternativeEnd("\\\\z and \\\\Z, the end of the input and the end of the input but for the final terminator, if any", false),

	/**
	 * \G, end of previous match; a special kind of back reference.
	 */
	EndOfPreviousMatch("\\\\G, end of previous match", false),

	/**
	 * \h and \\H, horizontal whitespace and non horizontal white space.
	 */
	HorizontalWhitespace("\\\\h and \\\\H, horizontal white space", false),

	/**
	 * \R, any Unicode linebreak sequence.
	 */
	AnyUnicodeLinebreakSequence("\\\\R, any Unicode linebreak sequence", false),

	/**
	 * \V, a non vertical whitespace
	 */
	NonVerticalWhitespace("\\\\V, a non vertical white space", false),

	/**
	 * Named capturing groups (?&lt;name>) and references (\k&lt;name>).
	 */
	NamedCapturingGroupsAndReferences("Named capturing groups (?&lt;name>) and references (\\k&lt;name>)", false),

	/**
	 * (?<=X), zero-width positive lookbehind.
	 */
	PositiveLookbehind("(?<=X), zero-width positive lookbehind", false),

	/**
	 * (?<!X), zero-width negative lookbehind.
	 */
	NegativeLookbehind("(?<!X), zero-width negative lookbehind", false),

	/**
	 * (?xxx:), inlined match flags (e.g. (?i))
	 */
	InlinedMatchFlags("(?xxx:), inlined match flags", false),

	/**
	 * Intersection within character classes (e.g. [a-z&&[aeiou]]).
	 */
	CharacterClassIntersection("Character Class Intersection (e.g. [a-z&&[aeiou]])", false),

	/**
	 * Union within character classes (e.g. [a-d[m-p]]).
	 */
	CharacterClassUnion("Character Class Unition (e.g. [a-d[m-p]])", false),

	/**
	 * Quotation Sequence (e.g. \Q[name]\E).
	 */
	QuotationSequence("Quotation Sequence (e.g. \\\\Q[name]\\\\E)", false),

	/**
	 * Exact quantifiers with zeros only (e.g. x{0,0} or x{0}).
	 * Although allowed in ECMA script, this does not work with BOOST Xpressive.
	 */
	ExactZeroQuantifierWithZero("Exact quantifiers with zeros only (e.g. {0,0} x{0})", false),

	/**
	 * A quantifier compounds a quantifier which can lead to an explosion of matching time, especially with long strings that do
	 * not match.
	 */
	Complexity_CompoundQuantifiers("A quantifier compounds a quantifier", true),

	/**
	 * Contiguous not mutually exclusive quantified tokens. Can lead to an explosion of matching time, especially with long
	 * strings that do not match. By default not more than two occurrences are allowed within one regular expression.
	 * The maximal number of occurrences is configurable through {@link RegexFeatureSet#setMaxNestedGroups(int).
	 */
	Complexity_OverlappingQuantifiedTokens("Contiguous not mutually exclusive quantified tokens", true),

	/**
	 * If this feature is disabled a maximum number of level of nested groups is enforced.
	 * The allowed number is configurable through {@link RegexFeatureSet#setMaxNestedGroups(int). Default is 2 levels.
	 */
	Complexity_UnrestrictedNestedGroups("Unrestricted levels of nested groups", true),

	/**
	 * If this feature is disabled a maximum number of level of nested groups is enforced.
	 * The allowed maximum length is configurable through {@link RegexFeatureSet#setMaxNestedGroups(int). Default is 10 000
	 * characters.
	 */
	Complexity_UnrestrictedLength("Unrestricted length of regular expression", true);


	private final String description;
	private final boolean complexityFeature;

	private RegexFeature(String description, boolean complexityFeature) {
		this.description = description;
		this.complexityFeature = complexityFeature;
	}

	/**
	 * Gets a textual description of the feature.
	 *
	 * @return a non-empty string in English
	 */
	public String getDescription() {
		return description;
	}

	public boolean isComplixityFeature() {
		return complexityFeature;
	}

}
