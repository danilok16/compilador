package controller;

import list.LexNode;
import list.List;
import model.entities.Lex;



public class SymbolsTable {
	private static List symbols = new List() ;
	private static int scope;// escopo atual
	
	

	public List getsymbols() {
		return symbols;
	}

	public void setsymbols(List symbols) {
		this.symbols = symbols;
	}

	public static int getScope() {
		return scope;
	}

	public static void setScope(int scope) {
		SymbolsTable.scope = scope;
	}

	public boolean insert(Lex lex, Integer type) { //inserir a variavel na tabela de simbolos
		
		if(findInThisScope(lex) == null) {//procurar lexema no scopo(bloco) atual
			symbols.insert(lex.getLexema(), type ,this.scope);
			return true;
		}
		else {
			return false;
		}
	}
	
	public void remove() { //remover da tabela de simbolos sempre que sair de um escopo '}'
		//System.out.println("-------------------");
		//symbols.teste();
		symbols.delete(this.scope); //recebe o escopo atual e remove toa
	}
	
	public LexNode findInThisScope(Lex lex) {//procurar no escopo atual
		 
		return symbols.optimizedFinder(lex, scope) ;
	}
	
	public LexNode findInAllScope(Lex lex) {//busca geral
		return symbols.finder(lex);
	}
	
	
	public void teste() {
		symbols.teste();
	}
}
