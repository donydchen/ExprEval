package lexer;

/**
 * 操作符的词法单元，继承了Token类。
 * @author Donald
 * @see Token
 */
public class CalOperator extends Token {
	/**
	 * 存储操作符名
	 */
	protected String lexeme;
	/**
	 * 存储操作符在算符优先关系表OPP中的标记
	 */
	protected String label;
	/**
	 * 初始化操作符词法单元。包括设置其类型为Operator,设置操作符名以及设置操作符在OPP中的标记。
	 * @param Oper  保存被认为是操作符的字符串。
	 */
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
	 * @return 操作符
	 */
	public String getLexeme() {
		return lexeme;
	}	
	/**
	 * 获取操作符在符号表中的标记
	 * @return 操作符在符号表中的标记
	 */
	public String getLable() {
		return label;
	}
	/**
	 * 获取操作符是几目运算符
	 * @return 获取操作符是几目运算符
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
