package controller;

import list.LexNode;
import model.entities.Lex;
import model.entities.LineColumn;
import model.entities.enums.Token;


public class ParserController {
	ScannerController scanner = new ScannerController();
	Lex parserLookahead;
	SymbolsTable symblosTable = new SymbolsTable();
	LexNode aux1Cast, aux2Cast;
	boolean updateAux1, updateAux2;
	int countTemp, countLabel; //************** IGC numero da variavel temporaria, numero do label

	
	public void main(FileController fileController) {
		countTemp = 0; countLabel = 0;//************** IGC
		symblosTable.setScope(0);/// ********************
		parserLookahead = scanner.scanFunction(fileController);
		if (parserLookahead.getToken() != Token.INT_TYPE.value) {
			ParserController.parserErro("Token nao encontrado: TIPO INTEIRO", parserLookahead);
		}
		parserLookahead = scanner.scanFunction(fileController);
		if (parserLookahead.getToken() != Token.MAIN.value) {
			ParserController.parserErro("Token nao encontrado: MAIN", parserLookahead);
		}
		parserLookahead = scanner.scanFunction(fileController);
		if (parserLookahead.getToken() != Token.LEFT_PARENTHESIS.value) {
			ParserController.parserErro("Token nao encontrado: ABRE PARENTESES", parserLookahead);
		}
		parserLookahead = scanner.scanFunction(fileController);
		if (parserLookahead.getToken() != Token.RIGHT_PARENTHESIS.value) {
			ParserController.parserErro("Token nao encontrado: FECHA PARENTESES", parserLookahead);
		}
		parserLookahead = scanner.scanFunction(fileController);
		// TESTAR BLOCO
		this.block(fileController, parserLookahead);
		if (parserLookahead.getToken() != Token.END_OF_FILE.value) {
			this.parserErro("Existe token fora do main", parserLookahead);
		}

	}

	public void block(FileController fileController, Lex parserLookahead) {
		// ABRE CHAVES
		if (parserLookahead.getToken() != Token.LEFT_BRACE.value) {
			this.parserErro("Token nao encontrado: ABRE CHAVE", parserLookahead);
		}
		symblosTable.setScope(symblosTable.getScope() + 1); // incrementar escopo
		parserLookahead = scanner.scanFunction(fileController);
		// DECLARACAO DE VARIAVEL
		while (parserLookahead.getToken() == Token.INT_TYPE.value
				|| parserLookahead.getToken() == Token.FLOAT_TYPE.value
				|| parserLookahead.getToken() == Token.CHAR_TYPE.value) {
			this.variableDeclarations(fileController, parserLookahead);
		}
		while (parserLookahead.getToken() == Token.IDENTIFIER.value
				|| parserLookahead.getToken() == Token.LEFT_BRACE.value
				|| parserLookahead.getToken() == Token.WHILE.value || parserLookahead.getToken() == Token.DO.value
				|| parserLookahead.getToken() == Token.IF.value) {
			this.command(fileController, parserLookahead);
		}

		// FECHA CHAVES
		if (parserLookahead.getToken() != Token.RIGHT_BRACE.value) {
			this.parserErro("Token nao encontrado: FECHA CHAVE", parserLookahead);

		}
		symblosTable.setScope(symblosTable.getScope() - 1);/// ********************
		symblosTable.remove();/// ********************
		parserLookahead = scanner.scanFunction(fileController);
	}

	public void variableDeclarations(FileController fileController, Lex parserLookahead) {
		Integer variableType = parserLookahead.getToken();
		do {
			// IDENTIFICADOR
			parserLookahead = scanner.scanFunction(fileController);
			if (parserLookahead.getToken() != Token.IDENTIFIER.value) {
				this.parserErro("Token nao encontrado: IDENTIFICADOR", parserLookahead);
			}
			if(symblosTable.insert(parserLookahead, variableType) == false) {
				this.semanticError("Variavel ja declarada neste escopo: ", parserLookahead, false);
			}
			parserLookahead = scanner.scanFunction(fileController);
		} while (parserLookahead.getToken() == Token.COMMA.value);
		// PONTO E VIRGULA
		if (parserLookahead.getToken() != Token.SEMICOLON.value) {
			this.parserErro("Token nao encontrado: PONTO E VIRGULA", parserLookahead);
		}
		parserLookahead = scanner.scanFunction(fileController);
	}

	public void command(FileController fileController, Lex parserLookahead) {
		LexNode aux1; //************** IGC
		int label = this.countLabel; //************** IGC
		
		if (parserLookahead.getToken() == Token.IDENTIFIER.value
				|| parserLookahead.getToken() == Token.LEFT_BRACE.value) {
			this.basicCommand(fileController, parserLookahead);
		} else if (parserLookahead.getToken() == Token.DO.value || parserLookahead.getToken() == Token.WHILE.value) {
			this.interaction(fileController, parserLookahead);
		} else if (parserLookahead.getToken() == Token.IF.value) {
			parserLookahead = scanner.scanFunction(fileController);
			if (parserLookahead.getToken() != Token.LEFT_PARENTHESIS.value) {
				this.parserErro("Token nao encontrado: ABRE PARENTESES", parserLookahead);
			}
			parserLookahead = scanner.scanFunction(fileController);
			aux1 = this.relationalExpression(fileController, parserLookahead); //************** IGC
			if (parserLookahead.getToken() != Token.RIGHT_PARENTHESIS.value) {
				this.parserErro("Token nao encontrado: FECHA PARENTESES", parserLookahead);
			}
			parserLookahead = scanner.scanFunction(fileController);
			
			////////************** IGC para o IF **********************/
			System.out.print("if "+ aux1.getLexema());
			if (aux1.getTId() >= 0) {
				System.out.print(aux1.getTId());
			}
			System.out.println(" == 0 goto label "+label);
			countLabel++;
			////////************** IGC para o IF **********************/
			
			this.command(fileController, parserLookahead);
			if (parserLookahead.getToken() == Token.ELSE.value) {
		////////************** IGC para o ELSE **********************/
				System.out.println("goto label "+ countLabel);
				System.out.println("label "+label);
				label = countLabel;
				parserLookahead = scanner.scanFunction(fileController);
				this.command(fileController, parserLookahead);
				System.out.println("label "+label);
			}
			else {
				System.out.println("label "+label);	////////************** IGC para o ELSE **********************/
			}
		}

		else if (parserLookahead.getToken() == Token.INT_TYPE.value
				|| parserLookahead.getToken() == Token.FLOAT_TYPE.value
				|| parserLookahead.getToken() == Token.CHAR_TYPE.value) {
			this.parserErro("Nao se pode declarar variavel fora de bloco", parserLookahead);
		} else {
			this.parserErro("Um comando e esperado", parserLookahead);
		}

	}

	public void basicCommand(FileController fileController, Lex parserLookahead) {
		LexNode aux1, aux2;
		if (parserLookahead.getToken() == Token.IDENTIFIER.value) {
			aux1 = symblosTable.findInAllScope(parserLookahead);
			if (aux1 == null) {/// ********************
				this.semanticError("variavel nao declarada: " ,parserLookahead, false);
			}
			parserLookahead = scanner.scanFunction(fileController);
			aux2 = this.assignment(fileController, parserLookahead);// ********* semantico
			this.typeChecking(aux1, aux2, true);// ********* semantico
			this.typeCasting(aux1, aux2, false, true);// ********* semantico
		
		}
		if (parserLookahead.getToken() == Token.LEFT_BRACE.value) {
			this.block(fileController, parserLookahead);
		}
	}

	public void interaction(FileController fileController, Lex parserLookahead) {
		LexNode aux1;
		int label = countLabel;//************** IGC
		
		System.out.println("label "+label);//************** IGC
		// while
		if (parserLookahead.getToken() == Token.WHILE.value) {
			countLabel = countLabel+2;//************** IGC
			parserLookahead = scanner.scanFunction(fileController);
			// "("
			if (parserLookahead.getToken() != Token.LEFT_PARENTHESIS.value) {
				this.parserErro("Token nao encontrado: ABRE PARENTESES", parserLookahead);
			}
			parserLookahead = scanner.scanFunction(fileController);
			// <expr_relacional>
			aux1 = this.relationalExpression(fileController, parserLookahead);//************** IGC
			// ")"
			if (parserLookahead.getToken() != Token.RIGHT_PARENTHESIS.value) {
				this.parserErro("Token nao encontrado: FECHA PARENTESES", parserLookahead);
			}
			parserLookahead = scanner.scanFunction(fileController);
			//************** IGC para o while**************************************/
			System.out.print("if "+ aux1.getLexema());
			if(aux1.getTId() >= 0) {
				System.out.print(aux1.getTId());
			}
			System.out.println(" == 0 goto label "+label+1);
			
			// <comando>
			this.command(fileController, parserLookahead);
			
			System.out.println("goto label "+label);
			System.out.println("label "+label+1);
			//************** IGC para o while**************************************/
		} // do
		else if (parserLookahead.getToken() == Token.DO.value) {
			countLabel++;
			parserLookahead = scanner.scanFunction(fileController);
			// <comando>
			this.command(fileController, parserLookahead);
			// while
			if (parserLookahead.getToken() != Token.WHILE.value) {
				this.parserErro("Token nao encontrado: WHILE DO COMANDO DO", parserLookahead);
			}
			parserLookahead = scanner.scanFunction(fileController);
			// "("
			if (parserLookahead.getToken() != Token.LEFT_PARENTHESIS.value) {
				this.parserErro("Token nao encontrado: ABRE PARENTESES", parserLookahead);
			}
			parserLookahead = scanner.scanFunction(fileController);
			// <expr_relacional>
			aux1 = this.relationalExpression(fileController, parserLookahead);//************** IGC
			// ")"
			if (parserLookahead.getToken() != Token.RIGHT_PARENTHESIS.value) {
				this.parserErro("Token nao encontrado: FECHA PARENTESES", parserLookahead);
			}
			parserLookahead = scanner.scanFunction(fileController);
			// ";"
			if (parserLookahead.getToken() != Token.SEMICOLON.value) {
				this.parserErro("Token nao encontrado: PONTO E VIRGULA", parserLookahead);
			}
			parserLookahead = scanner.scanFunction(fileController);
			//************** IGC para o DO WHILE**************************************/
			System.out.print("if "+ aux1.getLexema());
			if(aux1.getTId() >= 0)
				System.out.print(aux1.getTId());
			System.out.println(" != 0 goto label "+label);
		}
	}


	public LexNode assignment(FileController fileController, Lex parserLookahead) {
		LexNode aux1;// *********** semantico
		if (parserLookahead.getToken() != Token.ASSIGNMENT.value) {
			this.parserErro("Token nao encontrado: ATRIBUICAO", parserLookahead);
		}
		parserLookahead = scanner.scanFunction(fileController);
		aux1 = this.arithmeticExpression(fileController, parserLookahead); // ********* semantico
		if (parserLookahead.getToken() != Token.SEMICOLON.value) {
			this.parserErro("Token nao encontrado: PONTO E VIRGULA", parserLookahead);
		}
		parserLookahead = scanner.scanFunction(fileController);
		return aux1; // ********* semantico
	}

	public LexNode relationalExpression(FileController fileController, Lex parserLookahead) {
		LexNode aux1, aux2;// ********* semantico
		String operation;//************** IGC
		
		aux1 = this.arithmeticExpression(fileController, parserLookahead);// ********* semantico
		operation = parserLookahead.getLexema(); //************** IGC
		this.relationalOperator(fileController, parserLookahead);
		aux2 = this.arithmeticExpression(fileController, parserLookahead);// ********* semantico
		this.typeChecking(aux1, aux2, false);// ********* semantico
		this.typeCasting(aux1, aux2, false, false);
		if (updateAux1 == true)
			aux1 = this.aux1Cast;
		if (updateAux2 == true)
			aux2 = this.	aux2Cast;
		
		aux1 = this.codeGenerator(operation, aux1, aux2);//************** IGC
		return aux1;//************** IGC
	}

	public void relationalOperator(FileController fileController, Lex parserLookahead) {
		if (parserLookahead.getToken() != Token.GREATER_THAN.value
				&& parserLookahead.getToken() != Token.LESS_THAN.value
				&& parserLookahead.getToken() != Token.GREATER_THAN_OR_EQUAL.value
				&& parserLookahead.getToken() != Token.LESS_THAN_OR_EQUAL.value
				&& parserLookahead.getToken() != Token.EQUAL.value
				&& parserLookahead.getToken() != Token.NOT_EQUAL.value) {
			this.parserErro("Token de operador relacional nao encontrado", parserLookahead);
		}
		parserLookahead = scanner.scanFunction(fileController);

	}

	public LexNode arithmeticExpression(FileController fileController, Lex parserLookahead) {
		LexNode aux1 = new LexNode(), aux2 = new LexNode(); // ********** semantico
		String operation;//************** IGC
		
		aux1.setType(Token.NIL.value);
		aux2.setType(Token.NIL.value);
		aux1 = this.term(fileController, parserLookahead);// ********** semantico
		operation = parserLookahead.getLexema();//************** IGC
		if (parserLookahead.getToken() == Token.PLUS.value || parserLookahead.getToken() == Token.MINUS.value) {
			parserLookahead = scanner.scanFunction(fileController); // ********** semantico
			aux2 = this.arithmeticExpression(fileController, parserLookahead); // ********** semantico
			if (aux2.getType() == Token.INT_TYPE.value || aux2.getType() == Token.FLOAT_TYPE.value
					|| aux2.getType() == Token.CHAR_TYPE.value) {// ********** semantico
				this.typeChecking(aux1, aux2, false);// ********** semantico
				this.typeCasting(aux1, aux2, false, false);// ********** semantico
				if (updateAux1 == true)
					aux1 = this.aux1Cast;
				if (updateAux2 == true)
					aux2 = this.aux2Cast;
				
				aux1 = this.codeGenerator(operation, aux1, aux2);//************** IGC
			}
		}

		return aux1;
	}

	public LexNode term(FileController fileController, Lex parserLookahead) {
		LexNode aux1 = new LexNode(), aux2 = new LexNode(); // ********** semantico
		boolean divisionOperator = false;
		String operation;//************** IGC
		
		aux1.setType(Token.NIL.value);
		aux2.setType(Token.NIL.value);
		aux1 = this.factor(fileController, parserLookahead);
		while (parserLookahead.getToken() == Token.SLASH.value || parserLookahead.getToken() == Token.ASTERISK.value) {
			operation = parserLookahead.getLexema();//************** IG
			if (parserLookahead.getToken() == Token.SLASH.value) {
				divisionOperator = true;
			}
			parserLookahead = scanner.scanFunction(fileController);
			aux2 = this.factor(fileController, parserLookahead);
			this.typeChecking(aux1, aux2, false);
			this.typeCasting(aux1, aux2, divisionOperator, false);
			if (updateAux1 == true)
				aux1 = this.aux1Cast;
			if (updateAux2 == true)
				aux2 = this.aux2Cast;
			
			divisionOperator = false;
			aux1 = this.codeGenerator(operation, aux1, aux2);//************** IGC
		}
		return aux1;
	}

	public LexNode factor(FileController fileController, Lex parserLookahead) {
		LexNode aux = new LexNode();
		
		aux.setTId(-1);//************** IGC
		if (parserLookahead.getToken() == Token.LEFT_PARENTHESIS.value) {
			parserLookahead = scanner.scanFunction(fileController);
			aux = this.arithmeticExpression(fileController, parserLookahead);// *******************semantico
			if (parserLookahead.getToken() != Token.RIGHT_PARENTHESIS.value) {
				this.parserErro("Token nao encontrado: FECHA PARENTESES", parserLookahead);
			}
			parserLookahead = scanner.scanFunction(fileController);
			return aux;// *******************semantico
		} else if (parserLookahead.getToken() == Token.IDENTIFIER.value
				|| parserLookahead.getToken() == Token.FLOAT_VALUE.value
				|| parserLookahead.getToken() == Token.INTEGER_VALUE.value
				|| parserLookahead.getToken() == Token.CHARACTER_VALUE.value) {
			if (parserLookahead.getToken() == Token.IDENTIFIER.value) {
				aux = symblosTable.findInAllScope(parserLookahead);
				if (aux == null) {// ********************
					this.semanticError("variavel nao declarada: ", parserLookahead, false);
				}
			} else { 
				aux.setLexema(parserLookahead.getLexema());
				if (parserLookahead.getToken() == Token.FLOAT_VALUE.value) {
					aux.setLexema(parserLookahead.getLexema());
					aux.setType(Token.FLOAT_TYPE.value);
				}
				if (parserLookahead.getToken() == Token.INTEGER_VALUE.value) {
					aux.setLexema(parserLookahead.getLexema());
					aux.setType(Token.INT_TYPE.value);
				}
				if (parserLookahead.getToken() == Token.CHARACTER_VALUE.value) {
					aux.setLexema(parserLookahead.getLexema());
					aux.setType(Token.CHAR_TYPE.value);
				}
			}
			parserLookahead = scanner.scanFunction(fileController);
			return aux;
		} else {
			this.parserErro("Fator vazio", parserLookahead);
			return null;
		}
	}

	// *******************semantico chacagem de tipos
	public void typeChecking(LexNode aux1, LexNode aux2, boolean assignment) {
		if (assignment) { // *******************semantico for uma atribuicao
			if (aux1.getType() == Token.INT_TYPE.value && aux2.getType() != Token.INT_TYPE.value) {
				this.semanticError("tipo int so pode receber tipo int", parserLookahead, true);
			}
			if (aux1.getType() == Token.CHAR_TYPE.value && aux2.getType() != Token.CHAR_TYPE.value) {
				this.semanticError("tipo char so pode receber tipo char", parserLookahead, true);
			}
			if (aux1.getType() == Token.FLOAT_TYPE.value && aux2.getType() == Token.CHAR_TYPE.value) {
				this.semanticError("tipo float pode receber tipo float", parserLookahead, true);
			}
		} else {
			if (aux1.getType() == Token.INT_TYPE.value && aux2.getType() == Token.CHAR_TYPE.value) {
				this.semanticError("tipo int nao ecompativel com tipo char", parserLookahead, true);
			}
			if (aux1.getType() == Token.FLOAT_TYPE.value && aux2.getType() == Token.CHAR_TYPE.value) {
				this.semanticError("tipo float nao e compativel tipo char", parserLookahead, true);
			}
			////////////////// verificar pq talvez possa valor char
			if (aux1.getType() == Token.CHAR_TYPE.value && aux2.getType() != Token.CHAR_TYPE.value) {
				this.semanticError("tipo char so e compativel tipo char", parserLookahead, true);

			}
		}
	}

	// *******************semantico cast de tipo
	public void typeCasting(LexNode aux1, LexNode aux2, boolean divisionOperation, boolean assignment) {
		this.aux1Cast = aux1;
		this.aux2Cast = aux2;

		this.updateAux1 = false;
		this.updateAux2 = false;

		if (assignment == true) {
			if (aux1.getType() == Token.FLOAT_TYPE.value && aux2.getType() == Token.INT_TYPE.value) {
				aux2Cast.setType(Token.FLOAT_TYPE.value);
				this.updateAux2 = true;
				//************** IGC************************
				System.out.print("T"+countTemp+" = (float) "+aux2.getLexema());
				if(aux2.getLexema().equals("T") && aux2.getTId() >= 0)
					System.out.print(aux2.getTId());
				System.out.println();
				aux2.setLexema("T");
				aux2.setTId(countTemp);
				countTemp++;
				//************** IGC*************************
				
			}
			// imprimir a atribuicao final
			System.out.print(aux1.getLexema() +" = "+ aux2.getLexema());
			if(aux2.getLexema().equals("T")&&aux2.getTId() >= 0)
				System.out.print(aux2.getTId());
			System.out.println();
		} else {
			if (aux1.getType() == Token.INT_TYPE.value && aux2.getType() == Token.FLOAT_TYPE.value) {
				aux1Cast.setType(Token.FLOAT_TYPE.value);
				this.updateAux1 = true;
				System.out.print("T"+countTemp+" = (float) "+aux1.getLexema());
				if(aux1.getLexema().equals("T")&&aux1.getTId() >= 0)
					System.out.print(aux1.getTId());
				System.out.println();
				aux1.setLexema("T");
				aux1.setTId(countTemp);
				countTemp++;
			}
			if (aux1.getType() == Token.FLOAT_TYPE.value && aux2.getType() == Token.INT_TYPE.value) {
				aux2Cast.setType(Token.FLOAT_TYPE.value);
				this.updateAux2 = true;
				//************** IGC************************
				System.out.print("T"+countTemp+" = (float) "+aux2.getLexema());
				if(aux2.getLexema().equals("T")&&aux2.getTId() >= 0)
					System.out.print(aux2.getTId());
				System.out.println();
				aux2.setLexema("T");
				aux2.setTId(countTemp);
				countTemp++;
				//************** IGC*************************
			}
			if (aux1.getType() == Token.INT_TYPE.value && aux2.getType() == Token.INT_TYPE.value
					&& divisionOperation == true) {
				aux1Cast.setType(Token.FLOAT_TYPE.value);
				this.updateAux1 = true;
				//************** IGC************************
				System.out.print("T"+countTemp+" = (float) "+aux1.getLexema());
				if(aux1.getLexema().equals("T")&&aux1.getTId() >= 0)
					System.out.print(aux1.getTId());
				System.out.println();
				aux1.setLexema("T");
				aux1.setTId(countTemp);
				countTemp++;
				System.out.print("T"+countTemp+" = (float) "+aux2.getLexema());
				if(aux1.getLexema().equals("T")&&aux2.getTId() >= 0)
					System.out.print(aux2.getTId());
				System.out.println();
				aux2.setLexema("T");
				aux2.setTId(countTemp);
				countTemp++;
				//************** IGC*************************
				
			}
			
		
		}
	}

	LexNode codeGenerator(String operation, LexNode aux1, LexNode aux2) {
		System.out.print("T"+ countTemp +" = "+aux1.getLexema());
		if(aux1.getLexema().equals("T") && aux1.getTId() >= 0)
			System.out.print(aux1.getTId());
		System.out.print(operation);
		System.out.print(aux2.getLexema());
		if(aux2.getLexema().equals("T") && aux2.getTId() >= 0)
			System.out.print(aux2.getTId());
		System.out.println();
		aux1.setLexema("T");
		aux1.setTId(countTemp);
		countTemp++;
		return aux1;	
	}

	public static void parserErro(String msg, Lex parserLookahead) {
		System.out.println("Erro sintatico:" + "\'" + msg + "'");
		System.out.print("Ultimo token lido: " + "'" + lastTokenRead(parserLookahead));
		System.out.println("'");
		System.out.println("Linha: " + LineColumn.getLine() + " Coluna: " + LineColumn.getColumn());
		System.exit(0);
	}
	
	public void semanticError(String msg, Lex parserLookahead, boolean typeError) {
		if(typeError) {
			System.out.println("Erro semantico: "+ msg );
		}
		else {
		System.out.println("Erro semantico: "+ msg + "\'" +parserLookahead.getLexema() +"'");
		}
		System.out.print("Ultimo token lido: " + "'" +lastTokenRead(parserLookahead));
		System.out.println("'");
		System.out.println("Linha: " + LineColumn.getLine() + " Coluna: " + LineColumn.getColumn());
		System.exit(0);
	}
	
	public static String lastTokenRead(Lex parserLookahead) {
		if (parserLookahead.getToken() == Token.END_OF_FILE.value) {
			return "FIM DE ARQUIVO: ";
		} else if (parserLookahead.getToken() == Token.IDENTIFIER.value) {
			return "IDENTIFICADOR: ";
		} else if (parserLookahead.getToken() == Token.INTEGER_VALUE.value) {
			return "VALOR_INTEIRO: ";
		} else if (parserLookahead.getToken() == Token.FLOAT_VALUE.value) {
			return "VALOR_FLOAT: ";
		} else if (parserLookahead.getToken() == Token.CHARACTER_VALUE.value) {//
			return "VALOR_CARACTER";
		} else if (parserLookahead.getToken() == Token.LEFT_PARENTHESIS.value) {//
			return "(";
		} else if (parserLookahead.getToken() == Token.RIGHT_PARENTHESIS.value) {
			return ")";
		} else if (parserLookahead.getToken() == Token.LEFT_BRACE.value) {
			return"{";
		} else if (parserLookahead.getToken() == Token.RIGHT_BRACE.value) {
			return "}";
		} else if (parserLookahead.getToken() == Token.COMMA.value) {
			return ",";
		} else if (parserLookahead.getToken() == Token.SEMICOLON.value) {
			return ";";
		} else if (parserLookahead.getToken() == Token.ASSIGNMENT.value) {
			return "=";
		} else if (parserLookahead.getToken() == Token.PLUS.value) {
			return "+";
		} else if (parserLookahead.getToken() == Token.MINUS.value) {
			return "-";
		} else if (parserLookahead.getToken() == Token.ASTERISK.value) {
			return "*";
		} else if (parserLookahead.getToken() == Token.SLASH.value) {
			return "/";
		} else if (parserLookahead.getToken() == Token.INT_TYPE.value) {
			return "TIPO_INTEIRO";
		} else if (parserLookahead.getToken() == Token.FLOAT_TYPE.value) {
			return "TIPO_FLOAT";
		} else if (parserLookahead.getToken() == Token.CHAR_TYPE.value) {
			return "TIPO_CHAR";
		} else if (parserLookahead.getToken() == Token.MAIN.value) {
			return "MAIN";
		} else if (parserLookahead.getToken() == Token.IF.value) {
			return "IF";
		} else if (parserLookahead.getToken() == Token.ELSE.value) {
			return "ELSE";
		} else if (parserLookahead.getToken() == Token.WHILE.value) {
			return "WHILE";
		} else if (parserLookahead.getToken() == Token.DO.value) {
			return "DO";
		} else if (parserLookahead.getToken() == Token.FOR.value) {
			return "FOR";
		} else if (parserLookahead.getToken() == Token.LESS_THAN.value) {
			return "<";
		} else if (parserLookahead.getToken() == Token.GREATER_THAN.value) {
			return ">";
		} else if (parserLookahead.getToken() == Token.LESS_THAN_OR_EQUAL.value) {
			return "<=";
		} else if (parserLookahead.getToken() == Token.GREATER_THAN_OR_EQUAL.value) {
			return ">=";
		} else if (parserLookahead.getToken() == Token.EQUAL.value) {
			return "==";
		} else if (parserLookahead.getToken() == Token.NOT_EQUAL.value) {
			return "!=";
		}
		return " ";
	}

}
