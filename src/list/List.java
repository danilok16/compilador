package list;

import model.entities.Lex;



public class List {
	private LexNode first;

	public void insert(String lexeme, Integer type,int scope) {

		LexNode LexNode = new LexNode();
		LexNode.setScope(scope);
		LexNode.setLexema(lexeme);
		LexNode.setType(type);
		if (isEmpty()) {
			first = LexNode;
		} else {
			LexNode.setNext(first);
			first = LexNode;
		}
		/*
		 * LexNode LexNode = new LexNode(); LexNode.setLex(lex); LexNode.setNext(first); first = LexNode;
		 */
	}

	public void delete(int flagScope) {
		if (!isEmpty()) {
			LexNode aux = first;
			while (aux != null) {
				if (aux.getScope() > flagScope) {
					//System.out.println("Removeu: "+aux.getScope() + " : " + aux.getToken() + " : " + aux.getLexema());
					first = first.getNext();
					aux = null;
					aux = first;
				} else {
					break;
				}
			}
		}
	}

	public LexNode finder(Lex lex) {
		if (!isEmpty()) {
			LexNode aux = first;
			while (aux != null) {
				if (aux.getLexema().equals(lex.getLexema())) {
					LexNode aux2 = new LexNode();
					aux2.setLexema(aux.getLexema());
					aux2.setType(aux.getType());
					return aux2;
				}
				aux = aux.getNext();
			}
		}
		return null;
	}
	
	public LexNode optimizedFinder(Lex lex, int flagScope) {
		if (!isEmpty()) {
			LexNode aux = first;
			while (aux != null) {
				if (aux.getScope() < flagScope) {
					return null;
				}
				if (aux.getLexema().equals(lex.getLexema())) {
					return aux;
				}
				aux = aux.getNext();
			}
		}
		return null;
	}

	public boolean isEmpty() {
		if (first == null) {
			return true;
		} else {
			return false;
		}
	}

	public void teste() {
		/*
		 * while(!isEmpty()) { System.out.println(pop().getLex().getToken()); }
		 */
		LexNode aux = first;
		while (aux != null) {
			System.out.println(aux.getScope() + " : " + aux.getType() + " : " + aux.getLexema());
			aux = aux.getNext();
		}
	}
}
