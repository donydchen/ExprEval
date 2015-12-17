/**
 * @Copyright(C) 2008 Software Engineering Laboratory (SELAB), Department of Computer 
 * Science, SUN YAT-SEN UNIVERSITY. All rights reserved.
**/

package parser;

import exceptions.*;
import lexer.CalDecimal;
import lexer.Lexer;
import lexer.Token;

/**
 * Main program of the expression based calculator ExprEval
 * 
 * @author Donald
 * @version 1.00 (Last update: [PENDING the last update])
**/
public class Calculator
{
	/**
	 * The main program of the parser. You should substitute the body of this method 
	 * with your experiment result. 
	 * 
	 * @param expression  user input to the calculator from GUI. 
	 * @return  if the expression is well-formed, return the evaluation result of it. 
	 * @throws ExpressionException  if the expression has error, a corresponding 
	 *                              exception will be raised. 
	**/
	public double calculate(String expression) throws ExpressionException
	{
		// You should substitute this method body ...
		//double result = ((int) (Math.random() * 1000000000)) / 100.0;
		Lexer ans = new Lexer(expression);
		Token temp = ans.getNextToken();
		double result = ((CalDecimal)temp).getValue();
		return result;
	}
}
