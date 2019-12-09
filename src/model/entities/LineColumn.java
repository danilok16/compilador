package model.entities;


public class LineColumn {
	private static Integer line;
	private static Integer column;
	
	
	public static Integer getLine() {
		return line;
	}
	public static void setLine(Integer line) {
		LineColumn.line = line;
	}
	public static Integer getColumn() {
		return column;
	}
	public static void setColumn(Integer column) {
		LineColumn.column = column;
	}
	
	public static void lineColumnCounter(char ch) {
		if (Character.isSpaceChar(ch)) {
			LineColumn.setColumn(LineColumn.getColumn() + 1);

		} else if (ch == '\n') {
			LineColumn.setLine(LineColumn.getLine() + 1);
			LineColumn.setColumn(1);

		} else if (ch == '\t') {
			LineColumn.setColumn(LineColumn.getColumn() + 4);
		}
	}

}
    	