package parser;

import java.util.Arrays;
import java.util.Stack;
import exceptions.*;
import lexer.*;

/**
 * 语法分析及语义处理程序。
 * 此类为本实验的核心，它通过调用词法分析器lexer来获取词法单元，然后依据算符优先关系移入归约表以及BNF来
 * 对读入的词法单元执行相应的语义动作。<br>
 * 由于算符优先分析法只比较符号之间的优先关系，所以此parser中将符号以及函数存在一个堆栈operators中，
 * 而将十进制数以及布尔值存放在于另外一个堆栈operands中。<br>
 * 如果表达式格式正确，最后运行的结果将会是operands堆栈的栈顶元素。
 * @author Donald
 *
 */
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
	 * 初始化两个堆栈。并将Dollar符号压入操作符堆栈中。
	 */
	public Parser() {
		operators.clear();
		operands.clear();
		Dollar dollar = new Dollar();
		operators.push(dollar);				
	}
	
	/**
	 * 获取相应词法单元的label
	 * @param temp  词法单元
	 * @return 该词法单元在OPP符号表中所对应的标记
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
	 * @param label  符号表中的词法单元标记
	 * @return 词法单元在符号表中对应的下标
	 */
	private int getIndex(String label) {
		return Arrays.asList(LABEL_ARRAY).indexOf(label);
	}
	
	/**
	 * 执行函数操作。
	 * 执行包括sin,cos,max和min这四个函数的操作。它从operands堆栈中读取操作数，
	 * 最后将得到的结果压入operands堆栈中
	 * @param cnt  逗号的数量，用来确定max和min函数所用的操作数个数。
	 * @param func  函数的名字
	 * @throws SyntacticException
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
			if (func.equals("sin") || func.equals("cos"))
				throw new FunctionCallException();			
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
		//do sin and cos
		else {
			//Double radians = Math.toRadians(((CalDecimal)tempOperand).getValue()); //将度数转换为弧度
			Double radians = ((CalDecimal)tempOperand).getValue();
			if(func.equals("sin")) {
				ansValue = Math.sin( radians );
			}
			else if (func.equals("cos")) {
				ansValue = Math.cos( radians );
			}
			//如果是min和max但只有一个操作数
			else {
				throw new MissingOperandException();
			}
		}
		operands.push(new CalDecimal(Double.toString(ansValue)));
	}
	
	/**
	 * 将符号词法单元压入operators堆栈中。
	 * @param oper  符号词法单元
	 */
	private void shift(Token oper) {
		operators.push(oper);
	}
	
	/**
	 * 单目运算的归约。
	 * 执行取反(!)和负数(-)这两个操作。从operands中读取一个操作数，
	 * 运算得到的结果压入operands堆栈中。
	 * @throws SyntacticException
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
	 * 执行加，减，乘，除，取幂，关系运算，取与，取或这些二元算符归约。从operands堆栈中读取两个
	 * 操作数，运算得到的结果压入operands堆栈中。
	 * @throws TypeMismatchedException 
	 * @throws DividedByZeroException 
	 * @throws SyntacticException
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
	 * 执行选择运算(?:)三元运算的归约。从operands堆栈中读取三个操作数，运算得到的结果压入operands堆栈中。
	 * @throws TrinaryOperationException
	 * @throws TypeMismatchedException
	 * @throws SyntacticException
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
			if (operandA.getType().equals("Boolean") && operandB.getType().equals("Decimal") 
					&& operandC.getType().equals("Decimal")) {
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
	 * 当读到右括号时，执行此归约。最后判断左括号的左边是否有函数符号，有则通过调用doFunction函数来继续执行函数运算归约。
	 * @throws TypeMismatchedException
	 * @throws SyntacticException
	 * @throws DividedByZeroException 
	 * @see Parser#doFunction(int, String)
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
	 * @param expression  表达式字符串
	 * @return 表达式运算结果
	 * @throws ExpressionException
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
					throw new TrinaryOperationException();
				default:
					break;
				}
			}
		}
		
		if (completed) {
			if (operands.size() == 1) {
				if (operands.peek().getType().equals("Decimal"))
					return ((CalDecimal)operands.peek()).getValue();
				//结果只有一个操作数但是类型是布尔值的，抛出错误类型错误。
				else
					throw new TypeMismatchedException();
			}
			//最后操作数堆栈中有多于一个操作数，抛出缺少操作符错误。
			else {
				throw new MissingOperatorException();
			}
		}
		else {
			throw new SyntacticException();
		}		
	} //end of parsing
		
}
