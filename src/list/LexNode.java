package list;

public class LexNode {
	private String lexema;
	private Integer type; // tipo da variavel
	private LexNode next; //referencia para o proximo
	private int scope; //escopo da variavel
	int TId;
	
	public int getScope() {
		return scope;
	}
	public void setScope(int scope) {
		this.scope = scope;
	}
	public String getLexema() {
		return lexema;
	}
	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public LexNode getNext() {
		return next;
	}
	public void setNext(LexNode next) {
		this.next = next;
	}
	public int getTId() {
		return TId;
	}
	public void setTId(int tId) {
		TId = tId;
	}
	
	
	
	
}
