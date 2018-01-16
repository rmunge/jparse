package xpressive;

public class CompileResult {

	public long regexId;
	public long micros;

	@Override
	public String toString() {
		return CompileResult.class.getSimpleName() + " { regexId=" + regexId + ", micros=" + micros + " }";
	}

}
