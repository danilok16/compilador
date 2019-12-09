package model.entities.enums;


public enum Token {
	
	END_OF_FILE(-1), 
	IDENTIFIER(0),
	INTEGER_VALUE(1),	 
	FLOAT_VALUE(2), 
	CHARACTER_VALUE(3), 
	
	// ESPECIAL
	LEFT_PARENTHESIS(4),// '('
	RIGHT_PARENTHESIS(5),// ')'
	LEFT_BRACE(6),// '{'
	RIGHT_BRACE(7),// '}'
	COMMA(8),//,
	SEMICOLON(9),// ';'
	ASSIGNMENT(10), // '='
	PLUS(11),// '+'
	MINUS(12),// '-'
	ASTERISK(13),// '*'
	SLASH(14),// '/'

	
	// PALAVRAS RESERVADAS
	INT_TYPE(15),
	FLOAT_TYPE(16),
	CHAR_TYPE(17),
	MAIN(18),
	IF(19),
	ELSE(20),
	WHILE(21),
	DO(22),
	FOR(23),
	
	//OPERADORES RELACIONAIS
	LESS_THAN(24),//'<'
	GREATER_THAN(25),// '>'
	LESS_THAN_OR_EQUAL(26), // '<='
	GREATER_THAN_OR_EQUAL(27),// '>='
	EQUAL(28),// '=='
	NOT_EQUAL(29), // '!='
	NIL(30);
	
   public int value;
   Token(int opValue) {
        value = opValue;
    }
   public int getValue() {
	   return value;
   }
   

}
