package lexer;

public class CalOperator extends Token {
	protected String lexeme;
	protected String label;
	
	public CalOperator(String Oper) {
		type = "Operator";
		lexeme = Oper;
		/*
		 * The string.equal function checks the actual contents of the string, 
		 * the == operator checks whether the references to the objects are equal
		 */
		if(Oper.equals("+") || Oper.equals("-") ) {
			label = "pm";
		}
		else if(Oper.equals("*") || Oper.equals("/")) {
			label = "md";
		}
		else if(Oper.equals("minus")) {
			label = "-";
		}
		else if (Oper.equals("=") || Oper.equals("<") || Oper.equals(">") ||
				Oper.equals("<=") || Oper.equals(">=") || Oper.equals("<>")) {
			label = "cmp";
		}
		//包括 ^ , & | ? : ! ( )
		else {
			label = Oper;
		}
	}
	
	/**
	 * 获取操作符
	 * @return
	 */
	public String getLexeme() {
		return lexeme;
	}
	
	/**
	 * 获取操作符在符号表中的标记
	 * @return
	 */
	public String getLable() {
		return label;
	}
	
	/**
	 * 获取操作符是几目运算符
	 * @return
	 */
	public int getNum() {
		int num = 0;
				
		if (label.equals("-") || label.equals("!")) {
			num = 1;
		}
		if (label.equals("pm") || label.equals("md") || label.equals("^") ||
				label.equals("&") || label.equals("|") || label.equals("cmp")) {
			num = 2;
		}
		if (label.equals("?") || label.equals(":")) {
			num = 3;
		}
		
		return num;			
	}
}
