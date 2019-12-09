package model.entities;

public class Lex {
	private static String lexema;
	private static Integer token;
	
	
	public static String getLexema() {
		return lexema;
	}
	public static void setLexema(String lexema) {
		Lex.lexema = lexema;
	}
	public static Integer getToken() {
		return token;
	}
	public static void setToken(Integer token) {
		Lex.token = token;
	}
	

}