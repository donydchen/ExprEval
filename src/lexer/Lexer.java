package lexer;

import exceptions.*;

public class Lexer {

	/*
	 * 标记当前指针位置
	 */
	protected int index;
	/*
	 * 存储读取进来的原始字符串。
	 */
	protected String inputString;
	/*
	 * 存取修改过后的输入字符串。修改指的是变为小写，去除空格
	 */
	protected String fixString;
	/*
	 * 存储符号种类。
	 */
	protected final String opers = "+-*/^?:><=&|!(),";
	
	public Lexer(String expression) {
		inputString = expression;
		fixString = inputString.toLowerCase().replace(" ", "");
		index = 0;
	}
	
	public Token getNextToken() throws LexicalException, ExpressionException {
		/*
		 * 记录修改过后的字符串的长度。
		 */
		int strLen = fixString.length();	
		/**
		 * 判断全是空字符串
		 */
		if (strLen == 0) {
			throw new EmptyExpressionException();//全是空字符
		}
		//已经读完了全部字符
		if (index >= strLen) {
			return new Dollar();
		}
		
		/**
		 * 记录当前位置的字符。
		 */
		Character curChar = fixString.charAt(index);
				
		//判断Decimal
		if (Character.isDigit(curChar)) {
			Boolean dotFlag = false;
			Boolean eFlag = false;
			int startInt = index;
			//此处使用j来预读下一位，如果判断发现是合法字符，那么就将其加入Decimal中，也就是将index自增1。
			for (int j = startInt + 1; j < strLen; j++) {
				char peek = fixString.charAt(j);
				if (Character.isDigit(peek)) {
					index++; //合法字符，更新index
					continue;
				}
				else if (peek == '.') {
					if (eFlag || dotFlag) 
						throw new IllegalDecimalException(); //小数点出现在指数部分,或者小数点出现两次
					else if (j + 1 >= strLen) 
						throw new IllegalDecimalException(); //小数点是最后一位
					else if (!Character.isDigit(fixString.charAt(j+1)))
						throw new IllegalDecimalException(); //小数点后接的不是数字
					else {
						index++; //合法字符，更新index
						dotFlag = true;
					}					
				}
				else if (peek == 'e') {
					if (eFlag)
						throw new IllegalDecimalException(); //e出现多次
					else if (j+1 >= strLen) 
						throw new IllegalDecimalException(); //e是最后一位
					else if (!(Character.isDigit(fixString.charAt(j+1)) || 
							fixString.charAt(j+1) == '+' || fixString.charAt(j+1) == '-' ))
						throw new IllegalDecimalException(); //e后面接的不是数字或‘+’‘-’
					else {
						index++; //合法字符，更新index
						eFlag = true;
					}
				}
				else if (peek == '-' || peek == '+') {
					if ( !(fixString.charAt(j-1) == 'e') ) { //+，—不是在e后面，那么便认为是符号，Decimal读入结束
						break;
					}
					else if (j+1 == strLen)
						throw new IllegalDecimalException(); //e后面的'+'‘-’后面必须要有数字
					else 
						index++; //合法字符，更新index
				}
				//不是合法的字符，结束decimal。
				else {
					break;
				}
			}
			//从for循环跳出来，要么就是遇到不合法字符，要么就是字符串读完了。但index都是指向最后一个合法的输入，所以将index自增1使得其指向下一个字符，以便读取下个Token
			index++; 
			return new CalDecimal(fixString.substring(startInt, index));
		}
		
		//判断布尔式子
		else if (curChar == 't' || curChar == 'f') {
			String boolStrLow = "";
			if (curChar == 't') {
				boolStrLow = fixString.substring(index, index+4);
				if (boolStrLow.equals("true")) {
					index += 4;
					return new CalBoolean(boolStrLow);
				}
				else {
					throw new IllegalIdentifierException(); //开头是t,但不是true
				}
			}
			else {
				boolStrLow = fixString.substring(index, index+5);
				if (boolStrLow.equals("false")) {
					index += 5;
					return new CalBoolean(boolStrLow);
				}
				else
					throw new IllegalIdentifierException(); //开头是f，但不是false
			}
		}
		
		//判断函数
		else if (curChar == 's' || curChar == 'c' || curChar == 'm') {
			String funcLow = fixString.substring(index, index+3);
			if (funcLow.equals("sin") || funcLow.equals("cos") || funcLow.equals("max") || funcLow.equals("min")) {
				index += 3;
				return new CalFunction(funcLow);
			}
			else {
				throw new IllegalIdentifierException();//函数名错误
			}
		}
		
		//判断符号
		else if (opers.indexOf(curChar) != -1) {
			if (curChar == '>') {
				if (index < strLen - 1) {
					if (fixString.charAt(index+1) == '=') {
						index += 2;
						return new CalOperator(">=");
					}
				}
			}
			if (curChar == '<') {
				if (index < strLen -1) {
					if (fixString.charAt(index+1) == '=') {
						index += 2;
						return new CalOperator("<=");
					}
					if (fixString.charAt(index+1) == '>') {
						index += 2;
						return new CalOperator("<>");
					}
				}
			}
			if (curChar == '-') {
				if (index - 1 >= 0) {
					//只有当-前为）或者数字时，它才代表减号，否则就是负号
					if (fixString.charAt(index - 1 ) == ')' || 
							Character.isDigit(fixString.charAt(index -1))) {
						index += 1;
						return new CalOperator("-");
					}
				}
				index += 1;
				return new CalOperator("minus");
			}
			
			//埋坑，由于操作符和操作数不在同一个堆栈中，有些语法错误只能在词法分析中抛出。。。
			if (curChar == ')') {
				if (index - 1 >= 0) {
					if (fixString.charAt(index - 1 ) == '(')
						throw new MissingOperandException();
				}
			}
			if (curChar == ':') {
				if (index - 1 >= 0) {
					if (fixString.charAt(index - 1 ) == '?')
						throw new MissingOperandException();
				}
			}
 			
			index += 1;
			return new CalOperator(curChar.toString());
		}
		
		//如果出现其他类型，则抛出异常
		else {
			if (curChar == '.') {
				if (index + 1 < fixString.length()) {
					if (Character.isDigit(fixString.charAt(index+1)))
						throw new IllegalDecimalException();
					else 
						throw new LexicalException();
				}
				else 
					throw new LexicalException();
			}
			else if (Character.isAlphabetic(curChar)) {
				throw new IllegalIdentifierException(); //字母开头但不是函数
			}
			else 
				throw new IllegalSymbolException(); //出现其他非法字符。
		}
		
	}
	
	public String getFixString() {
		return fixString;
	}
	
}
