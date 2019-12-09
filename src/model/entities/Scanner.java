package model.entities;

public class Scanner {
	private static char lookahead;
	
	public static char getLookahead() {
		return lookahead;
	}

	public static void setLookahead(char lookahead) {
		Scanner.lookahead = lookahead;
	}

	
}