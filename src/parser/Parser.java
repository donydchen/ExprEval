package parser;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;

import exceptions.FunctionCallException;
import exceptions.LexicalException;
import exceptions.MissingLeftParenthesisException;
import exceptions.SyntacticException;
import exceptions.TrinaryOperationException;
import exceptions.TypeMismatchedException;
import lexer.*;

public class Parser {

	/**
	 * 用于确定label所对应的下标，以此来匹配符号表。
	 */
	protected final String[] LABEL_ARRAY = 
		{"(", ")", "func", "-", "^", "md", "pm", "cmp", "!", "&", "|", "?", ":", ",", "$"};
	/**
	 * 用于存储符号的堆栈。
	 */
	protected Stack<Token> operators = new Stack<Token>();	
	/**
	 * 用于存储操作数的堆栈，包括十进制数和布尔。
	 */
	protected Stack<Token> operands = new Stack<Token>();
	/**
	 * 存储当前读入的词法单元。
	 */
	protected Token curToken = new Token();
	/*
	 * 存储操作符堆栈栈顶元素
	 */
	protected Token topToken = new Token();
	
	/**
	 * 初始化两个堆栈.
	 */
	public Parser() {
		operators.clear();
		operands.clear();
		Dollar dollar = new Dollar();
		operators.push(dollar);				
	}
	
	/**
	 * 获取相应词法单元的label
	 * @param temp
	 * @return
	 */
	private String getLabel(Token temp) {
		String tempType = temp.getType();
		if (tempType == "Function") {
			return ((CalFunction)temp).getLabel();
		}
		if (tempType == "Operator") {
			return ((CalOperator)temp).getLable();
		}
		return ((Dollar)temp).getLabel();
	}
	
	/**
	 * 获取词法单元label在LABEL_ARRAY中对应的下标
	 * @param label
	 * @return
	 */
	private int getIndex(String label) {
		return Arrays.asList(LABEL_ARRAY).indexOf(label);
	}
	
	/**
	 * 执行函数操作。
	 * @param cnt
	 * @param func
	 * @throws FunctionCallException 
	 */
	private void doFunction(int cnt, String func) throws FunctionCallException {
		Token tempOperand = operands.pop();
		if (tempOperand.getType() != "Decimal") {
			throw new FunctionCallException();
		}
		Token ansOperand = new Token();
		
		if (cnt > 0) {
			//do max and min
		}
		else {
			//do sin and cos
		}
	}
	
	/**
	 * 将符号词法单元压入operators堆栈中。
	 * @param oper
	 */
	private void shift(Token oper) {
		operators.push(oper);
	}
	
	/**
	 * 单目运算的归约。
	 */
	private void unaryReduce() {
		CalOperator tempOperator = (CalOperator)operators.pop();  //读取符号堆栈栈顶元素并移除
		Token tempOperand = operands.pop();
		if (tempOperator.getLable() == "-") {
			Double tempValue = ((CalDecimal)tempOperand).getValue();
			tempValue = 0 - tempValue;
			((CalDecimal)tempOperand).setValue(tempValue);
		}
		if (tempOperator.getLable() == "!") {
			Boolean tempValue = ((CalBoolean)tempOperand).getValue();
			tempValue = !tempValue;
			((CalBoolean)tempOperand).setValue(tempValue);			
		}
		operands.push(tempOperand);
	}
	
	/**
	 * 双目运算归约。
	 * @throws TypeMismatchedException 
	 */
	private void binaryReduce() throws TypeMismatchedException {
		CalOperator tempOperator = (CalOperator)operators.pop(); //获取栈顶符号并移除
		Token operandB = operands.pop();
		Token operandA = operands.pop();
		// 执行 + - * / ^
		if (tempOperator.getLable() == "pm" || tempOperator.getLable() == "md" || tempOperator.getLable() == "^") {
			if (operandA.getType() == "Decimal" && operandB.getType() == "Decimal") {
				Double valueA = ((CalDecimal)operandA).getValue();
				Double valueB = ((CalDecimal)operandB).getValue();
				Double valueC = 0.0;
				switch (tempOperator.getLexeme().charAt(0)) {
				case '+':
					valueC = valueA + valueB; break;
				case '-':
					valueC = valueA - valueB; break;
				case '*':
					valueC = valueA * valueB; break;			
				case '/':
					valueC = valueA / valueB; break;
				case '^':
					valueC = Math.pow(valueA, valueB); break;
				default:
					break;
				}
				operands.push(new CalDecimal(Double.toString(valueC)));
			}
			else {
				throw new TypeMismatchedException();
			}
		}
		//执行 & |
		if (tempOperator.getLable() == "&" || tempOperator.getLable() == "|") {
			if (operandA.getType() == "Boolean" && operandB.getType() == "Boolean") {
				Boolean boolA = ((CalBoolean)operandA).getValue();
				Boolean boolB = ((CalBoolean)operandB).getValue();
				Boolean boolC = false;
				switch (tempOperator.getLexeme().charAt(0)) {
				case '&':
					boolC = boolA & boolB; break;
				case '|':	
					boolC = boolA | boolB; break;
				default:
					break;
				}
				operands.push(new CalBoolean(Boolean.toString(boolC)));
			}
			else {
				throw new TypeMismatchedException();
			}
		}
		//执行关系运算，> < = >= <= <>
		if (tempOperator.getLable() == "cmp") {
			if (operandA.getType() == "Decimal" && operandB.getType() == "Decimal") {
				Double valueA = ((CalDecimal)operandA).getValue();
				Double valueB = ((CalDecimal)operandB).getValue();
				Boolean boolC = false;
				String operLexeme = tempOperator.getLexeme();
				if ( (operLexeme == ">" && valueA > valueB) || (operLexeme == "<" && valueA < valueB) || 
					 (operLexeme == "=" && valueA ==valueB) || (operLexeme == ">="&& valueA >= valueB) ||
					 (operLexeme == "<="&& valueA <=valueB) || (operLexeme == "<>"&& valueA != valueB)) {
					boolC = true;
				}
				operands.push(new CalBoolean(Boolean.toString(boolC)));
			}
			else {
				throw new TypeMismatchedException();
			}
		}		
	}
	
	/**
	 * 三目运算归约。
	 * @throws TrinaryOperationException
	 * @throws TypeMismatchedException
	 */
	private void trinaryReduce() throws TrinaryOperationException, TypeMismatchedException {
		CalOperator operatorB = (CalOperator)operators.pop();
		CalOperator operatorA = (CalOperator)operators.pop();
		Token operandC = operands.pop();
		Token operandB = operands.pop();
		Token operandA = operands.pop();
		if (operatorA.getLexeme() == "?" && operatorB.getLexeme() == ":") {
			if (operandA.getType() == "Boolean" && operandB.getType() == "Decimal" && operandC.getType() == "Decimal") {
				Double valueD = 0.0;
				if ( ((CalBoolean)operandA).getValue() ) {
					valueD = ((CalDecimal)operandB).getValue(); 
				}
				else {
					valueD = ((CalDecimal)operandC).getValue();
				}
				operands.push(new CalDecimal(Double.toString(valueD)));
			}
			else {
				throw new TypeMismatchedException();
			}
		}
		else {
			throw new TrinaryOperationException();
		}
	}
	
	private void matchReduce() throws TypeMismatchedException, SyntacticException {	
		Token tempOperator = operators.peek();
		int cntComma = 0;
		while (true) {
			if (tempOperator.getType() == "Dollar") {
				throw new MissingLeftParenthesisException();
			}
			else {
				if (((CalOperator)tempOperator).getLexeme() == "(") {
					operators.pop(); //移除（
					break;
				}
				//保存操作符需要的操作数数量
				int tempNum = ((CalOperator)tempOperator).getNum();
				
				if (cntComma == 0 && tempNum == 1) {
					unaryReduce();
				}
				else if (cntComma == 0 && tempNum == 2) {
					binaryReduce();
				}
				else if (cntComma == 0 && tempNum == 3) {
					trinaryReduce();
				}
				else if (((CalOperator)tempOperator).getLexeme() == ",") { //该操作符是,
					operators.pop();
					cntComma++;
				}
				else {
					throw new SyntacticException();
				}			
			}
			tempOperator = operators.peek();
		} //end of while loop
		
		tempOperator = operators.peek();
		//如果是function的话，接着执行function操作，否则结束运算。
		if (tempOperator.getType() == "Function") {
			doFunction(cntComma, ((CalFunction)tempOperator).getLexeme());
		}		
		
	}
	
	
	public Double parsing(String expression) throws LexicalException, TypeMismatchedException, 
	SyntacticException {
		
		Lexer lexer = new Lexer(expression);
		curToken = lexer.getNextToken();
		Boolean completed = false;
		int action = 0;
		int lableStackIndex;
		int lableReadIndex;
		
		
		while(!completed) {
			topToken = operators.peek();
			if (curToken.getType() == "Boolean" || curToken.getType() == "Decimal") {
				operands.push(curToken);
				curToken = lexer.getNextToken();
				continue;
			}
			else {
				lableReadIndex = getIndex(getLabel(curToken));
				lableStackIndex = getIndex(getLabel(topToken));
				action = OPP.table[lableStackIndex][lableReadIndex];
				switch (action) {
				case OPP.ACCEPT:
					completed = true;
					break;
				case OPP.SHIFT:
					shift(curToken);
					curToken = lexer.getNextToken();
					break;
				case OPP.RDUNAOPER:
					unaryReduce();
					break;
				case OPP.RDBINAOPER:
					binaryReduce();
					break;
				case OPP.RDTRINAOPER:
					trinaryReduce();
					break;
				case OPP.RDMATCH:
					matchReduce();
					break;
					
					

				default:
					break;
				}
			}
		}
		
		if (completed) {
			//return the ans;
		}
	}
	
	
}
