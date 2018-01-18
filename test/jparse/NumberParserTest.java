package jparse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * JUnit tests for {@link NumberParser}.
 *
 * @author rmunge
 */
public class NumberParserTest {

	private static char ARABIC_ZERO = '\u0660';
	private static char ARABIC_NINE = '\u0669';
	private static char BENGALI_SIX = '\u09EC';

	@Test
	public void testParsePositiveInt_NumbersWithinRange() {
		assertEquals(0, NumberParser.parsePositiveInt("0", 10));
		assertEquals(473, NumberParser.parsePositiveInt("473", 10));
		assertEquals(42, NumberParser.parsePositiveInt("+42", 10));
		assertEquals(-1, NumberParser.parsePositiveInt("-0", 10));
		assertEquals(2147483647, NumberParser.parsePositiveInt("2147483647", 10)); // Interger.MAX_VALUE
		assertEquals(-1, NumberParser.parsePositiveInt("-2147483648", 10)); // Interger.MIN_VALUE
	}

	@Test
	public void testParsePositiveInt_NonLatinDigits() {
		assertEquals(90, NumberParser.parsePositiveInt("" + ARABIC_NINE + ARABIC_ZERO, 10));
	}

	@Test
	public void testParsePositiveInt_MixedDigits() {
		assertEquals(56906, NumberParser.parsePositiveInt("56" + ARABIC_NINE + ARABIC_ZERO + BENGALI_SIX, 10));
		assertEquals(5690600, NumberParser.parsePositiveInt("56" + ARABIC_NINE + ARABIC_ZERO + BENGALI_SIX + "00", 10));
		assertEquals(-1, NumberParser.parsePositiveInt("56" + ARABIC_NINE + ARABIC_ZERO + BENGALI_SIX + "/", 10));
	}

	@Test
	public void testParsePositiveInt_LeadingSpaces() {
		assertEquals(5, NumberParser.parsePositiveInt(" 5"));
	}

	@Test
	public void testParsePositiveInt_TrailingSpaces() {
		assertEquals(5, NumberParser.parsePositiveInt("5  "));
	}

	@Test
	public void testParsePositiveInt_InvalidNumbers() {
		assertEquals(-1, NumberParser.parsePositiveInt(null));
		assertEquals(-1, NumberParser.parsePositiveInt(""));
		assertEquals(-1, NumberParser.parsePositiveInt(" "));
		assertEquals(-1, NumberParser.parsePositiveInt("-"));
		assertEquals(-1, NumberParser.parsePositiveInt("ABC"));
		assertEquals(-1, NumberParser.parsePositiveInt("/:"));
	}

	@Test
	public void textParseInt_Limits() {
		assertEquals(-1, NumberParser.parsePositiveInt("2147483648", 10)); // Interger.MAX_VALUE + 1
		assertEquals(-1, NumberParser.parsePositiveInt("-2147483649", 10)); // Interger.MIN_VALUE - 1
		assertEquals(-1, NumberParser.parsePositiveInt("214748364700", 10)); // Interger.MAX_VALUE + 100 (more than the radix
// value)
	}

	@Test
	public void testParsePositiveInt_WrongRadix() {
		assertEquals(-1, NumberParser.parsePositiveInt("99", 8));
		assertEquals(-1, NumberParser.parsePositiveInt("Kona", 10));
	}

	@Test
	public void testParsePositiveInt_SpecialRadix() {
		assertEquals(411787, NumberParser.parsePositiveInt("Kona", 27));
		assertEquals(-1, NumberParser.parsePositiveInt("Kona", 10));
		assertEquals(-1, NumberParser.parsePositiveInt("-FF", 16));
		assertEquals(102, NumberParser.parsePositiveInt("1100110", 2));
	}

	@Test
	public void testParsePositiveInt_WithSpaces() {

		/*
		 * In contrast to Integer.parseInt() we support also spaces within numbers.
		 *
		 * Rationale:
		 *
		 * "... The 22nd General Conference on Weights and Measures declared in 2003 that
		 * "the symbol for the decimal marker shall be either the point on the line or the comma on the line". It further
		 * reaffirmed that
		 * "numbers may be divided in groups of three in order to facilitate reading; neither dots nor commas are ever inserted in the spaces between groups"
		 * [14] e.g. 1 000 000 000. This usage has therefore been recommended by technical organizations, such as the United
		 * States' National Institute of Standards and Technology ..."
		 *
		 * Source: https://en.wikipedia.org/wiki/Decimal_mark ... nowadays even default decimal marker in Microsoft office
		 */

		assertEquals(100_000, NumberParser.parsePositiveInt("100 000"));
		assertEquals(1_000_000_000, NumberParser.parsePositiveInt("1 000 000 000"));
		assertEquals(1, NumberParser.parsePositiveInt("  1   "));
	}

	@Test
	public void testParseDoubleNull() {
		assertTrue(Double.isNaN(NumberParser.parseDouble(null)));
	}

	@Test
	public void testParseDouble() {

		// potential StringIndexOutOfBoundsException
		assertTrue(Double.isNaN(NumberParser.parseDouble("+"))); // SIOOB1
		assertEquals(0d, NumberParser.parseDouble("0"), 0.0d); // SIOOB2
		assertTrue(Double.isNaN(NumberParser.parseDouble("15.485E"))); // SIOOB3

		assertTrue(Double.isNaN(NumberParser.parseDouble("")));
		assertTrue(Double.isNaN(NumberParser.parseDouble(" ")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("-")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("-.")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("1. 5")));
		assertTrue(Double.isNaN(NumberParser.parseDouble(".")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("..")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("0..")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("N")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("N23")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("NaNo")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("I")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("InfinitX")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("Infinitys")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("10.0.0.1")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("15.485E")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("15.485Ex")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("15.485E0xFF")));
		assertTrue(Double.isNaN(NumberParser.parseDouble("10x")));

		assertEquals(Double.POSITIVE_INFINITY, NumberParser.parseDouble("15.485E214748364700"), 0.0d);  // exp > Interger.MAX_VALUE
// => positive infinity
		assertEquals(0d, NumberParser.parseDouble("15.485E-214748364700"), 0.0d);  // exp < Interger.MIN_VALUE => 0

		assertEquals(Double.NaN, NumberParser.parseDouble("NaN"), 0.0d);
		assertEquals(Double.NEGATIVE_INFINITY, NumberParser.parseDouble("-Infinity"), 0.0d);
		assertEquals(Double.POSITIVE_INFINITY, NumberParser.parseDouble("Infinity"), 0.0d);


		assertEquals(0d, NumberParser.parseDouble("-.0"), 0d);
		assertEquals(0.1d, NumberParser.parseDouble("+.1"), 0.1d);
		assertEquals(-0.1d, NumberParser.parseDouble("-.1"), 0.1d);
		assertEquals(0.1d, NumberParser.parseDouble("0.1d"), 0.1d);
		assertEquals(0.1d, NumberParser.parseDouble("0.1D"), 0.1d);
		assertEquals(0.1d, NumberParser.parseDouble("0.1f"), 0.1d);
		assertEquals(0.1d, NumberParser.parseDouble("0.1F"), 0.1d);
		assertEquals(0.0d, NumberParser.parseDouble("0.0"), 0.01d);
		assertEquals(0.0d, NumberParser.parseDouble("-0"), 0.01d);
		assertEquals(0.0d, NumberParser.parseDouble("0."), 0.0d);
		assertEquals(-0.0d, NumberParser.parseDouble("-0."), 0.0d);

		// leading zeros
		assertEquals(0.0d, NumberParser.parseDouble("0000.0"), 0.01d);

		assertEquals(0.15485d, NumberParser.parseDouble("15.485E-2"), 0.000001d);
		assertEquals(0.15485d, NumberParser.parseDouble("15.485e-2"), 0.000001d);
		assertEquals(0.15485d, NumberParser.parseDouble("15.485E-0002"), 0.000001d);

		// source for hexadecimal floating points: http://www.exploringbinary.com/hexadecimal-floating-point-constants/
		assertEquals(0.1d, NumberParser.parseDouble("0x1.999999999999ap-4"), 0.1d);
		assertEquals(0.1d, NumberParser.parseDouble("0x1.99999ap-4"), 0.1d);
		assertEquals(0.1d, NumberParser.parseDouble("0x1.99999ap-00004"), 0.1d);
		assertEquals(-2.703125d, NumberParser.parseDouble("-0x1.5ap+1"), 0.00001d);
		assertEquals(Double.POSITIVE_INFINITY, NumberParser.parseDouble("0X1.99999ap+214748364700"), 0.0d);
		assertEquals(0d, NumberParser.parseDouble("0X1.99999ap-214748364700"), 0.0d);

		// invalid hexadecimal floating point number
		assertTrue(Double.isNaN(NumberParser.parseDouble("0x1.99999")));
	}

	@Test
	public void testFastRun() {
		NumberParser.parsePositiveInt("500");
	}

	@Test
	public void testParseDouble_DecimalSeparators() {

		// in contrast to Double.parseDouble() we support also comma as decimal separator

		// (.) ... English-speaking, some Latin American / Asian countries
		assertEquals(1.5d, NumberParser.parseDouble("1.5"), 0.1d);

		// (,) Europe
		assertEquals(1.5d, NumberParser.parseDouble("1,5"), 0.1d);

		// Doesn't change anything about thousands separators (Double.parseDouble() would throw an exception here)
		assertTrue(Double.isNaN(NumberParser.parseDouble("1,500.20")));
	}

	@Test
	public void testParseFloatNull() {
		assertTrue(Float.isNaN(NumberParser.parseFloat("")));
	}

	@Test
	public void testParseFloatInvalid() {
		assertTrue(Float.isNaN(NumberParser.parseFloat("")));
	}

}
