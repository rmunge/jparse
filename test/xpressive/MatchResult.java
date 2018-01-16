package xpressive;

public class MatchResult {

	public boolean matches;
	public long micros;

	@Override
	public String toString() {
		return MatchResult.class.getSimpleName() + " { matches=" + matches + ", micros=" + micros + " }";
	}

}
