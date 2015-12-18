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
	protected final String opers = "+-*/^?:><=&|!()";
	
	public Lexer(String expression) {
		inputString = expression;
		fixString = inputString.toLowerCase().replace(" ", "");
		index = 0;
	}
	
	public Token getNextToken() throws LexicalException {
		/*
		 * 记录修改过后的字符串的长度。
		 */
		int strLen = fixString.length();	
		/**
		 * 判断全是空字符串
		 */
		if (strLen == 0) {
			throw new exceptions.LexicalException();//全是空字符
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
			int endInt = strLen;
			for (int j = startInt + 1; j < strLen; j++) {
				char peek = fixString.charAt(j);
				if (Character.isDigit(peek)) {
					index++; //更新index
					continue;
				}
				else if (peek == '.') {
					if (eFlag) 
						throw new exceptions.LexicalException(); //小数点出现在指数部分
					else if (dotFlag)
						throw new exceptions.LexicalException(); //小数点出现两次
					else if (j + 1 < strLen) {
						if (!Character.isDigit(fixString.charAt(j+1)))
							throw new exceptions.LexicalException(); //小数点后接的不是数字
					}
					else if (j + 1 == strLen) 
						throw new exceptions.LexicalException(); //小数点不能是最后一位
					else {
						dotFlag = true;
					}					
				}
				else if (peek == 'e') {
					if (eFlag)
						throw new exceptions.LexicalException(); //e出现多次
					else if (j + 1 < strLen) { 
						if (!(Character.isDigit(fixString.charAt(j+1)) || 
								fixString.charAt(j+1) == '+' || fixString.charAt(j+1) == '-' ))
							throw new exceptions.LexicalException(); //e后面接的不是数字或‘+’‘-’
					}
					else if (j+1 == strLen) 
						throw new exceptions.LexicalException(); //e不能是最后一位
					else {
						eFlag = true;
					}
				}
				else if (peek == '-' || peek == '+') {
					if ( !(fixString.charAt(j-1) == 'e') ) {
						index = j - 1;
						endInt = j;
						break;
					}
					if (j+1 == strLen)
						throw new exceptions.LexicalException(); //e后面的'+'‘-’后面必须要有数字
				}
				else {
					index = j - 1; //为了配合for循环外的index++，此处必须回退一位。
					endInt = j;
					break;
				}
			}
			index++; //当最后一个数字是个位数，将不会进入上面的for循环，但此时的index仍然必须加1
			return new CalDecimal(fixString.substring(startInt, endInt));
		}
		
		//判断布尔式子
		else if (curChar == 't' || curChar == 'f') {
			String boolStrLow = "";
			if (curChar == 't') {
				boolStrLow = fixString.substring(index, index+4);
				if (boolStrLow == "true") {
					index += 4;
					return new CalBoolean(boolStrLow);
				}
				else {
					throw new exceptions.LexicalException(); //开头是t,但不是true
				}
			}
			else {
				boolStrLow = fixString.substring(index, index+5);
				if (boolStrLow == "false") {
					index += 5;
					return new CalBoolean(boolStrLow);
				}
				else
					throw new exceptions.LexicalException(); //开头是f，但不是false
			}
		}
		
		//判断函数
		else if (curChar == 's' || curChar == 'c' || curChar == 'm') {
			String funcLow = fixString.substring(index, index+3);
			if (funcLow == "sin" || funcLow == "cos" || funcLow == "max" || funcLow == "min") {
				index += 3;
				return new CalFunction(funcLow);
			}
			else {
				throw new exceptions.LexicalException();//函数名错误
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
			
			index += 1;
			return new CalOperator(curChar.toString());
		}
		
		//如果出现其他类型，则抛出异常
		else {
			throw new exceptions.LexicalException(); //出现其他非法字符。
		}
		
	}
	
	public String getFixString() {
		return fixString;
	}
	
}
