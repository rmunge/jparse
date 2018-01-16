package xpressive;

public class Xpressive {

	public static native CompileResult compile(String regex);
	public static native MatchResult match(long regexId, String input, long n);
	public static native void reset();

	public static MatchResult match(long regexId, String input) {
		return match(regexId, input, 1);
	}

	static {
		System.loadLibrary("xpressive");
	}

	public static void main(String [] args) {
		CompileResult compileResult = compile(".*.*");
		System.out.println(compileResult);
		System.out.println(match(compileResult.regexId, "Hello World!", 1000));

		reset();

		compileResult = compile(".*.*");
		System.out.println(compileResult);
		System.out.println(match(compileResult.regexId, "Hello World!", 1000));
	}

}
