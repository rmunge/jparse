package jparse.regex;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Baseline for {@link AbstractRegexSyntaxTest}. Executes all tests in <tt>AbstractRegexSyntaxTest</tt> with an instance of
 * <tt>java.util.regex.Pattern</tt>, constructed with
 * default settings.
 *
 * @author rmunge
 */
public class DefaultJavaSyntaxTest extends AbstractRegexSyntaxTest<Pattern> {

	@Override
	protected void assertMatches(boolean expected, String string, String pattern) {
		Matcher matcher = compile(pattern).matcher(string);
		boolean matches = matcher.matches();

		assertEquals("Expected result must not differ from JRE's default regex implementation with default settings (string=" +
				string + ", pattern=" + pattern + ")",
				expected, matches);
	}

	@Override
	protected Pattern compile(String regex) {
		return Pattern.compile(regex);
	}

}
