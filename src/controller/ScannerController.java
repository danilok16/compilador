package controller;

import java.io.IOException;
import java.util.ArrayList;

import model.entities.Ch;
import model.entities.Lex;
import model.entities.LineColumn;
import model.entities.Scanner;
import model.entities.enums.Token;


public class ScannerController {

	static ArrayList<Character> lexeme = new ArrayList(); // ajustar
	static Lex lex = new Lex();
	static Ch ch = new Ch();

	public Lex scanFunction(FileController fileController) {
		lexeme.clear();

		// GetNoBlank
		GetNoBlank(fileController);

		// FIM DE ARQUIVO
		if (ch.getChInt() == Token.END_OF_FILE.value) {
			lex.setToken(Token.END_OF_FILE.value);
			return lex;
		}

		// IDENTIFICADOR OU PALAVRA RESERVADA
		else if (Character.isLetter(Scanner.getLookahead()) || Scanner.getLookahead() == '_') {
			while (Character.isLetter(Scanner.getLookahead()) || Character.isDigit(Scanner.getLookahead())
					|| Scanner.getLookahead() == '_') {
				lexeme.add(Scanner.getLookahead());
				ch = read(fileController);
				Scanner.setLookahead(ch.getChChar());
				LineColumn.setColumn(LineColumn.getColumn() + 1);
			}
			lex.setToken(Token.IDENTIFIER.value);
			setLexeme(lex);
			restrictedWord(lex); //
			return lex; // IDENTIFICADOR
		}

		// VALOR INTEIRO OU FLOAT
		else if (Character.isDigit(Scanner.getLookahead()) || Scanner.getLookahead() == '.') {
			while (Character.isDigit(Scanner.getLookahead())) {
				lexeme.add(Scanner.getLookahead());
				ch = read(fileController);
				Scanner.setLookahead(ch.getChChar());
				LineColumn.setColumn(LineColumn.getColumn() + 1);
			}
			if (Scanner.getLookahead() == '.') {// VALOR FLOAT
				lexeme.add(Scanner.getLookahead());
				ch = read(fileController);
				Scanner.setLookahead(ch.getChChar());
				if (!Character.isDigit(Scanner.getLookahead())) {
					this.scannerErro("Valor float mal formado", fileController);
				}
				while (Character.isDigit(Scanner.getLookahead())) {
					lexeme.add(Scanner.getLookahead());
					ch = read(fileController);
					Scanner.setLookahead(ch.getChChar());
					LineColumn.setColumn(LineColumn.getColumn() + 1);
				}
				if (lexeme.get(0) == '.') {
					ArrayList<Character> aux = new ArrayList();
					aux.add('0');
					for (int i = 0; i < lexeme.size(); i++) {
						aux.add(lexeme.get(i));
					}
					lexeme = aux;
				}
				lex.setToken(Token.FLOAT_VALUE.value);
				setLexeme(lex);
				return lex;
			}

			lex.setToken(Token.INTEGER_VALUE.value);
			setLexeme(lex);
			return lex;
		}
		// VALOR CHAR
		else if (Scanner.getLookahead() == '\'') {
			lexeme.add(Scanner.getLookahead());
			ch = read(fileController);
			Scanner.setLookahead(ch.getChChar());
			LineColumn.setColumn(LineColumn.getColumn() + 1);
			if (Character.isJavaLetterOrDigit(Scanner.getLookahead())) {
				lexeme.add(Scanner.getLookahead());
				ch = read(fileController);
				Scanner.setLookahead(ch.getChChar());
				LineColumn.setColumn(LineColumn.getColumn() + 1);
				if (Scanner.getLookahead() == '\'') {
					lexeme.add(Scanner.getLookahead());
					ch = read(fileController);
					Scanner.setLookahead(ch.getChChar());
					LineColumn.setColumn(LineColumn.getColumn() + 1);
					lex.setToken(Token.CHARACTER_VALUE.value);
					setLexeme(lex);
					return lex;
				} else {
					this.scannerErro("Char mal formado", fileController);
				}
			} else {
				this.scannerErro("Char mal formado", fileController);
			}
		}

		// ESPECIAIS, OPERADORES E COMENTARIOS
		else if (isSpecial(Scanner.getLookahead())) {
			char aux = Scanner.getLookahead();
			boolean isComment = false;
			boolean lineComments = false;
			boolean blockComments = false;

			switch (aux) {
			case '(':
				lex.setToken(Token.LEFT_PARENTHESIS.value);
				break;
			case ')':
				lex.setToken(Token.RIGHT_PARENTHESIS.value);
				break;
			case '{':
				lex.setToken(Token.LEFT_BRACE.value);
				break;
			case '}':
				lex.setToken(Token.RIGHT_BRACE.value);
				break;
			case ',':
				lex.setToken(Token.COMMA.value);
				break;
			case ';':
				lex.setToken(Token.SEMICOLON.value);
				break;
			case '+':
				lex.setToken(Token.PLUS.value);
				break;
			case '-':
				lex.setToken(Token.MINUS.value);
				break;
			case '*':
				lex.setToken(Token.ASTERISK.value);
				break;
			case '/':
				do {
					ch = read(fileController);
					Scanner.setLookahead(ch.getChChar());
					if (Scanner.getLookahead() == '/' && !blockComments) { // COMENTARIO DE LINHA
						lineComments = true;
						isComment = true;
					} else if (Scanner.getLookahead() == '\n' && !blockComments) {
						lineComments = false;
					} else if (Scanner.getLookahead() == '*' && !lineComments) { // COMENTARIO DE BLOCO
						blockComments = true;
						isComment = true;
						do {
							ch = read(fileController);
							Scanner.setLookahead(ch.getChChar());
							LineColumn.lineColumnCounter(Scanner.getLookahead());
							while (Scanner.getLookahead() == '*') {
								ch = read(fileController);
								Scanner.setLookahead(ch.getChChar());
								LineColumn.lineColumnCounter(Scanner.getLookahead());
								if (Scanner.getLookahead() == '/') {
									blockComments = false;
									ch = read(fileController);
									Scanner.setLookahead(ch.getChChar());
								}
							}
							if (ch.getChInt() == Token.END_OF_FILE.value) {
								this.scannerErro("Fim de arquivo sem fechar o comentario", fileController);
							}
						} while (blockComments);
					}
				} while (lineComments || blockComments);
				if (isComment) {
					return lex = this.scanFunction(fileController);
				} else {
					lexeme.add('/');
					lex.setToken(Token.SLASH.value);
					setLexeme(lex);
					return lex;
				}

			}

			lexeme.add(Scanner.getLookahead());
			ch = read(fileController);
			Scanner.setLookahead(ch.getChChar());
			LineColumn.setColumn(LineColumn.getColumn() + 1);
			setLexeme(lex);
			return lex;

		}

		// OPERADORES RELACIONAIS
		else if (Scanner.getLookahead() == '=' || Scanner.getLookahead() == '!' || Scanner.getLookahead() == '<'
				|| Scanner.getLookahead() == '>') {
			char aux = Scanner.getLookahead();
			lexeme.add(Scanner.getLookahead());
			ch = read(fileController);
			Scanner.setLookahead(ch.getChChar());
			LineColumn.setColumn(LineColumn.getColumn() + 1);
			
			if (Scanner.getLookahead() == '=') {
				lexeme.add(Scanner.getLookahead());
				switch (aux) {
				case '=':
					lex.setToken(Token.EQUAL.value);
					break;
				case '!':
					
					lex.setToken(Token.NOT_EQUAL.value);
					break;
				case '<':
					lex.setToken(Token.LESS_THAN_OR_EQUAL.value);
					break;
				case '>':
					lex.setToken(Token.GREATER_THAN_OR_EQUAL.value);
					break;
				}
				
				ch = read(fileController);
				Scanner.setLookahead(ch.getChChar());
				LineColumn.setColumn(LineColumn.getColumn() + 1);
				
			} else if (Scanner.getLookahead() == ' ' || Scanner.getLookahead() == '\n'
					|| Character.isLetter(Scanner.getLookahead()) || Character.isDigit(Scanner.getLookahead())
					|| isSpecial(Scanner.getLookahead()) || Scanner.getLookahead() == '>' || Scanner.getLookahead() == '<' 
					||Scanner.getLookahead() =='\'' || Scanner.getLookahead() == '\t') {
				switch (aux) {
				case '=':
					lex.setToken(Token.ASSIGNMENT.value);
					break;
				case '<':
					lex.setToken(Token.LESS_THAN.value);
					break;
				case '>':
					lex.setToken(Token.GREATER_THAN.value);
					break;
				default: this.scannerErro("Operador relacional mal formatado", fileController);
				}

			} 
			else {
				this.scannerErro("Operador relacional mal formatado", fileController);
			}
			setLexeme(lex);
			return lex;

		}

		// ERRO
		else {
			this.scannerErro("Caracter invalido", fileController);
		}
		return lex;

	}//

	public static void GetNoBlank(FileController fileController) {
		while (Character.isSpaceChar(Scanner.getLookahead()) || Scanner.getLookahead() == '\n'
				|| Scanner.getLookahead() == '\t') {
			LineColumn.lineColumnCounter(Scanner.getLookahead());
			ch = read(fileController);
			Scanner.setLookahead(ch.getChChar());
		}
	}

	// 
	public static Ch read(FileController fileController) {
		Ch ch = new Ch();// ajustar

		try {
			ch.setChInt(fileController.getBufferedReader().read());
			if (ch.getChInt() != -1) {	
				ch.setChChar((char) ch.getChInt());
				return ch;
			}
		} catch (IOException e) {
			e.printStackTrace();
			fileController.closeFile();
		}
		return ch;
	}

	public static boolean isSpecial(char ch) {
		if (ch == '(' || ch == ')' || ch == '{' || ch == '}'  || ch == ',' || ch == ';'
				||  ch == '+' || ch == '-' || ch == '*' || ch == '/') {
			return true;
		}
		return false;

	}

	// METODO PARA PREENCHER O LEXEMA
	public static void setLexeme(Lex lex) {
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < lexeme.size(); i++) {
			strBuilder.append(lexeme.get(i));
		}
		lex.setLexema(strBuilder.toString());
	}

	// VERIFICAR PALAVRAS RESERVADAS
	public static void restrictedWord(Lex lex) {
		switch (lex.getLexema()) {
		case "int":
			lex.setToken(Token.INT_TYPE.value);
			break;
		case "float":
			lex.setToken(Token.FLOAT_TYPE.value);
			break;
		case "char":
			lex.setToken(Token.CHAR_TYPE.value);
			break;
		case "main":
			lex.setToken(Token.MAIN.value);
			break;
		case "if":
			lex.setToken(Token.IF.value);
			break;
		case "else":
			lex.setToken(Token.ELSE.value);
			break;
		case "while":
			lex.setToken(Token.WHILE.value);
			break;
		case "do":
			lex.setToken(Token.DO.value);
			break;
		case "for":
			lex.setToken(Token.FOR.value);
			break;
		}

	}

	// MENSAGENS DE ERRO
	public void scannerErro(String msg, FileController fileController) {
		
		System.out.println("Erro: "+msg+"\nLinha: " + LineColumn.getLine() + " Coluna: " + LineColumn.getColumn()+"\nUltimo caracter lido: "+ Scanner.getLookahead());
		fileController.closeFile();
		System.exit(0);
	}
}