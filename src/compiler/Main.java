package compiler;

import controller.FileController;

import controller.ParserController;
import controller.ScannerController;
import list.List;
import model.entities.Lex;
import model.entities.LineColumn;
import model.entities.Scanner;
import model.entities.enums.Token;


public class Main {
	
	public static void main(String[] args) {
		LineColumn.setLine(1);
		LineColumn.setColumn(0);
		Lex lex;
		Scanner.setLookahead(' ');
		//Stack stack = new Stack();
		FileController fileController = new FileController();
		fileController.setBufferedReader(fileController.openFile(args[0]));
		ScannerController scanner = new ScannerController();
		ParserController parserController = new ParserController();
		if (fileController.getBufferedReader() != null) {
		parserController.main(fileController);
			/*int i = 0;
			do {
				lex = scanner.scanFunction(fileController);
				//printToken(lex);
				
				stack.push(lex, i);
				i++;
			} while (lex.getToken() != Token.END_OF_FILE.value);*/
		}
		/*lex = new Lex();
		for (int i = 0; i<10; i++)
			stack.push(lex, i);*/
	//	stack.teste();
		
		fileController.closeFile();

	}
	

	public static void printToken(Lex lex) {
		if (lex.getToken() == Token.IDENTIFIER.value) {
			System.out.print("IDENTIFICADOR: " + lex.getLexema());
		} else if (lex.getToken() == Token.INTEGER_VALUE.value) {
			System.out.print("VALOR_INTEIRO: " + lex.getLexema());
		} else if (lex.getToken() == Token.FLOAT_VALUE.value) {
			System.out.print("VALOR_FLOAT: " + lex.getLexema());
		} else if (lex.getToken() == Token.CHARACTER_VALUE.value) {//
			System.out.print("VALOR_CARACTER");
		} else if (lex.getToken() == Token.LEFT_PARENTHESIS.value) {//
			System.out.print("(");
		} else if (lex.getToken() == Token.RIGHT_PARENTHESIS.value) {
			System.out.print(")");
		} else if (lex.getToken() == Token.LEFT_BRACE.value) {
			System.out.print("{");
		} else if (lex.getToken() == Token.RIGHT_BRACE.value) {
			System.out.print("}");
		} else if (lex.getToken() == Token.COMMA.value) {
			System.out.print(",");
		} else if (lex.getToken() == Token.SEMICOLON.value) {
			System.out.print(";");
		} else if (lex.getToken() == Token.ASSIGNMENT.value) {
			System.out.print("=");
		} else if (lex.getToken() == Token.PLUS.value) {
			System.out.print("+");
		} else if (lex.getToken() == Token.MINUS.value) {
			System.out.print(")");
		} else if (lex.getToken() == Token.ASTERISK.value) {
			System.out.print("*");
		} else if (lex.getToken() == Token.SLASH.value) {
			System.out.print("/");
		} else if (lex.getToken() == Token.INT_TYPE.value) {
			System.out.print("TIPO_INTEIRO");
		} else if (lex.getToken() == Token.FLOAT_TYPE.value) {
			System.out.print("TIPO_FLOAT");
		} else if (lex.getToken() == Token.CHAR_TYPE.value) {
			System.out.print("TIPO_CHAR");
		} else if (lex.getToken() == Token.MAIN.value) {
			System.out.print("MAIN");
		} else if (lex.getToken() == Token.IF.value) {
			System.out.print("IF");
		} else if (lex.getToken() == Token.ELSE.value) {
			System.out.print("ELSE");
		} else if (lex.getToken() == Token.WHILE.value) {
			System.out.print("WHILE");
		} else if (lex.getToken() == Token.DO.value) {
			System.out.print("DO");
		} else if (lex.getToken() == Token.FOR.value) {
			System.out.print("FOR");
		} else if (lex.getToken() == Token.LESS_THAN.value) {
			System.out.print("<");
		} else if (lex.getToken() == Token.GREATER_THAN.value) {
			System.out.print(">");
		} else if (lex.getToken() == Token.LESS_THAN_OR_EQUAL.value) {
			System.out.print("<=");
		} else if (lex.getToken() == Token.GREATER_THAN_OR_EQUAL.value) {
			System.out.print(">=");
		} else if (lex.getToken() == Token.EQUAL.value) {
			System.out.print("==");
		} else if (lex.getToken() == Token.NOT_EQUAL.value) {
			System.out.print("!=");
		}
		System.out.println();
	}
	
	public static void format() {
		for (int i = 1; i < LineColumn.getLine(); i++)
			System.out.println();
		for (int i = 1; i < LineColumn.getColumn(); i++)
			System.out.print(" ");

	}

}
