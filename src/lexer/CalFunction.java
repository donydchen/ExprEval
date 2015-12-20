package lexer;
/**
 * 函数的词法单元，继承了Token类。
 * @author Donald
 * @see Token
 */
public class CalFunction extends Token{
	/**
	 * 存储词法单元的函数名
	 */
	protected String lexeme;
	/**
	 * 存储词法单元在算符优先关系表中的label
	 */
	protected String label;
	/**
	 * 初始化函数词法单元。包括设置其类型为Function，设置其函数名以及设置其label为func
	 * @param func  保存被认为是函数名的字符串
	 */
	public CalFunction(String func) {
		type = "Function";
		lexeme = func.toLowerCase();
		label = "func";
	}
	/**
	 * 获取函数名
	 * @return 函数名
	 */
	public String getLexeme() {
		return lexeme;
	}
	/**
	 * 获取词法单元在算符优先级表中的标记
	 * @return 词法单元在算符优先级表中的标记
	 */
	public String getLabel() {
		return label;
	}
}
