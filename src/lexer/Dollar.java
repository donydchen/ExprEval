package lexer;

/**
 * 终结符词法单元，继承了Token类。
 * @author Donald
 * @see Token
 */
public class Dollar extends Token {
	/**
	 * 存储终结符
	 */
	protected String lexeme;
	/**
	 * 存储终结符在算符优先关系表OPP中的标记。
	 */
	protected String label;
	/**
	 * 初始化终结符。包括设置其类型为Dollar，设置其表示为$,以及设置其在OPP表中的标记为$。
	 */
	public Dollar() {
		type = "Dollar";
		lexeme = "$";
		label = "$";
	}
	/**
	 * 获取终结符表示
	 * @return 终结符表示
	 */
	public String getLexeme() {
		return lexeme;
	}
	/**
	 * 获取终结符在OPP表中的标记。
	 * @return 终结符在OPP表中的标记
	 */
	public String getLabel() {
		return label;
	}
	
}
