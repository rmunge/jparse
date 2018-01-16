/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: PatternSyntaxRestrictionException.java
 * @date: 28.09.2017
 * @author: roland.mungenast
 */
package jparse.regex;

import java.text.MessageFormat;
import java.util.regex.PatternSyntaxException;


/**
 * Specialized {@link PatternSyntaxException} which is fired when a feature is found within a regular expression that is disabled.
 *
 * @author rmunge
 */
public final class PatternSyntaxFeatureException extends PatternSyntaxException {

	private static final String NL = System.getProperty("line.separator");
	public static final String MESSAGE_TEMPLATE = "Unsupported feature, \"{0}\" near index {1}";

	private static final long serialVersionUID = 6970880952693947915L;

	private final RegexFeature feature;
	private final String regex;
	private final int index;


	/**
	 * Constructs a new instance of this class.
	 *
	 * @param cause
	 *        the found, but disabled feature
	 *
	 * @param regex
	 *        The erroneous pattern
	 *
	 * @param index
	 *        The approximate index in the pattern of the error,
	 *        or <tt>-1</tt> if the index is not known
	 */
	public PatternSyntaxFeatureException(RegexFeature cause, String regex, int index) {
		super(cause.getDescription(), regex, index);
		this.feature = cause;
		this.regex = regex;
		this.index = index;
	}

	/**
	 * @return the found but disabled feature
	 */
	public RegexFeature getFeature() {
		return feature;
	}


	  /**
     * Returns a multi-line string containing the description of the syntax
     * error and its index, the erroneous regular-expression pattern, and a
     * visual indication of the error index within the pattern.
     *
     * @return  The full detail message
     */
    public String getMessage() {
    	return MessageFormat.format(MESSAGE_TEMPLATE, feature.getDescription(), index) + NL + generateDetailMessage();
    }


    private String generateDetailMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append(regex);
        if (index >= 0) {
            sb.append(NL);
            for (int i = 0; i < index; i++) {
            	sb.append(' ');
            }
            sb.append('^');
        }
        return sb.toString();
    }

}
