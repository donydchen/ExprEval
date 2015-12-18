package parser;

import java.util.Arrays;
import java.util.Stack;

import exceptions.DividedByZeroException;
import exceptions.ExpressionException;
import exceptions.FunctionCallException;
import exceptions.LexicalException;
import exceptions.MissingLeftParenthesisException;
import exceptions.MissingOperandException;
import exceptions.MissingOperatorException;
import exceptions.MissingRightParenthesisException;
import exceptions.SyntacticException;
import exceptions.TrinaryOperationException;
import exceptions.TypeMismatchedException;
import lexer.CalBoolean;
import lexer.CalDecimal;
import lexer.CalFunction;
import lexer.CalOperator;
import lexer.Dollar;
import lexer.Lexer;
import lexer.Token;

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
	/**
	 * 存储操作符堆栈栈顶元素
	 */
	protected Token topToken = new Token();
	/**
	 * 定义两个Double比较的精度
	 */
	private static final Double EPSILON = 0.00000001;
	
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
		if (tempType.equals("Function")) {
			return ((CalFunction)temp).getLabel();
		}
		if (tempType.equals("Operator")) {
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
	 * @throws MissingOperandException 
	 */
	private void doFunction(int cnt, String func) throws SyntacticException {
		if (operands.size() == 0) {
			throw new MissingOperandException();
		}
		Token tempOperand = operands.pop();
		if (tempOperand.getType().intern() != "Decimal") {
			throw new FunctionCallException();
		}
		Double ansValue = ((CalDecimal)tempOperand).getValue();
		//do max and min
		if (cnt > 0) {
			if (operands.size() < cnt) 
				throw new MissingOperandException();
			
			Double topValue = 0.0;
			for (int i = 0; i < cnt; i++) {
				tempOperand = operands.pop();
				topValue = ((CalDecimal)tempOperand).getValue();
				if (func.equals("max") && topValue > ansValue) {
					ansValue = topValue; 
				}
				if (func.equals("min") && topValue < ansValue) {
					ansValue = topValue;
				}
			}
		}
		//do sin and cos 以及只有一个数值的max和min
		else {
			Double radians = Math.toRadians(((CalDecimal)tempOperand).getValue()); //将度数转换为弧度
			if(func.equals("sin")) {
				ansValue = Math.sin( radians );
			}
			if (func.equals("cos")) {
				ansValue = Math.cos( radians );
			}
		}
		operands.push(new CalDecimal(Double.toString(ansValue)));
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
	private void unaryReduce() throws SyntacticException{
		if (operands.empty())
			throw new MissingOperandException();
		CalOperator tempOperator = (CalOperator)operators.pop();  //读取符号堆栈栈顶元素并移除
		Token tempOperand = operands.pop();
		if (tempOperator.getLable().equals("-")) {
			Double tempValue = ((CalDecimal)tempOperand).getValue();
			tempValue = 0 - tempValue;
			((CalDecimal)tempOperand).setValue(tempValue);
		}
		if (tempOperator.getLable().equals("!")) {
			Boolean tempValue = ((CalBoolean)tempOperand).getValue();
			tempValue = !tempValue;
			((CalBoolean)tempOperand).setValue(tempValue);			
		}
		operands.push(tempOperand);
	}
	
	/**
	 * 双目运算归约。
	 * @throws TypeMismatchedException 
	 * @throws DividedByZeroException 
	 */
	private void binaryReduce() throws TypeMismatchedException, SyntacticException, DividedByZeroException {
		if (operands.size() < 2)
			throw new MissingOperandException();
		CalOperator tempOperator = (CalOperator)operators.pop(); //获取栈顶符号并移除
		Token operandB = operands.pop();
		Token operandA = operands.pop();
		// 执行 + - * / ^
		if (tempOperator.getLable().equals("pm") || tempOperator.getLable().equals("md") || tempOperator.getLable().equals("^")) {
			if (operandA.getType().equals("Decimal") && operandB.getType().equals("Decimal")) {
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
					if (Math.abs(valueB - 0.0) < EPSILON)
						throw new DividedByZeroException();
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
		if (tempOperator.getLable().equals("&") || tempOperator.getLable().equals("|")) {
			if (operandA.getType().equals("Boolean") && operandB.getType().equals("Boolean")) {
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
		if (tempOperator.getLable().equals("cmp")) {
			if (operandA.getType().equals("Decimal") && operandB.getType().equals("Decimal")) {
				Double valueA = ((CalDecimal)operandA).getValue();
				Double valueB = ((CalDecimal)operandB).getValue();
				Boolean boolC = false;
				String operLexeme = tempOperator.getLexeme();
				//由于浮点数的精度问题，不能直接比较两个Double之间的相等性，所以此处改为判断两个double之间的大小相差小于epsilon来确定相等
				if ( (operLexeme.equals(">") && valueA > valueB) || 
					 (operLexeme.equals("<") && valueA < valueB) || 
					 (operLexeme.equals("=") && (Math.abs(valueA - valueB) < EPSILON) ) || 
					 (operLexeme.equals(">=")&& ( (valueA > valueB) || (Math.abs(valueA - valueB) < EPSILON)) ) ||
					 (operLexeme.equals("<=")&& ( (valueA < valueB) || (Math.abs(valueA - valueB) < EPSILON)) ) || 
					 (operLexeme.equals("<>")&& (Math.abs(valueA - valueB) >= EPSILON)) ) {
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
	private void trinaryReduce() throws TrinaryOperationException, TypeMismatchedException, SyntacticException {
		if (operands.size() < 3 || operators.size() < 3) {
			throw new MissingOperandException();
		}
		CalOperator operatorB = (CalOperator)operators.pop();
		CalOperator operatorA = (CalOperator)operators.pop();
		Token operandC = operands.pop();
		Token operandB = operands.pop();
		Token operandA = operands.pop();
		if (operatorA.getLexeme().equals("?") && operatorB.getLexeme().equals(":")) {
			if (operandA.getType().equals("Boolean") && operandB.getType().equals("Decimal") && operandC.getType().equals("Decimal")) {
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
	
	/**
	 * 括号运算以及函数运算归约。
	 * @throws TypeMismatchedException
	 * @throws SyntacticException
	 * @throws DividedByZeroException 
	 * @see doFunction
	 */
	private void matchReduce() throws TypeMismatchedException, SyntacticException, DividedByZeroException {	
		Token tempOperator = operators.peek();
		int cntComma = 0;
		Boolean matchCompleted = false;
		while (!matchCompleted) {
			if (tempOperator.getType().equals("Dollar")) {
				throw new MissingLeftParenthesisException();
			}
			else {
				if (((CalOperator)tempOperator).getLexeme().equals("(")) {
					operators.pop(); //移除（
					matchCompleted = true;
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
				else if (((CalOperator)tempOperator).getLexeme().equals(",")) { //该操作符是,
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
		if (tempOperator.getType().equals("Function")) {
			doFunction(cntComma, ((CalFunction)tempOperator).getLexeme());
			operators.pop(); // 函数预算执行结束，将函数pop掉
		}		
		
	}
	
	/**
	 * 执行语法分析和语义动作。
	 * @param expression
	 * @return
	 * @throws LexicalException
	 * @throws TypeMismatchedException
	 * @throws SyntacticException
	 * @throws DividedByZeroException 
	 */
	public Double parsing(String expression) throws ExpressionException {
		
		Lexer lexer = new Lexer(expression);
		curToken = lexer.getNextToken();
		Boolean completed = false;
		int action = 0;
		int lableStackIndex;
		int lableReadIndex;
		
		
		while(!completed) {
			topToken = operators.peek();
			if (curToken.getType().equals("Boolean") || curToken.getType().equals("Decimal")) {
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
					curToken = lexer.getNextToken(); //做完括号运算后，右括号就没用了，应该再读入一个新的Token
					break;
				case OPP.ERRLEFTPAR:
					throw new MissingLeftParenthesisException();
				case OPP.ERRSYN:
					throw new SyntacticException();
				case OPP.ERROPERAND:
					throw new MissingOperandException();
				case OPP.ERRTYPE:
					throw new TypeMismatchedException();
				case OPP.ERRFUNCSYN:
					throw new FunctionCallException();
				case OPP.ERRRIGHTPAR:
					throw new MissingRightParenthesisException();
				case OPP.ERRTRINA:
					throw new TypeMismatchedException();
				default:
					break;
				}
			}
		}
		
		if (completed) {
			//return the ans;
			if (operands.size() == 1 && operands.peek().getType().equals("Decimal")) {
				return ((CalDecimal)operands.peek()).getValue();
			}
			else {
				throw new MissingOperatorException();
			}
		}
		else {
			throw new SyntacticException();
		}
		
	}
	
	
}
