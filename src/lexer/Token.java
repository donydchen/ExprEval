package lexer;

/**
 * 所有词法单元的父类。
 * @author Donald
 */
public class Token {
	/**
	 * 存储词法单元的类型。在本实验中具体包括Boolean,Decimal,Function,Operator以及Dollar这五个。
	 */
	protected String type;
	/**
	 * 初始化词法单元
	 */
	public Token() {
		type = "";
	}
	/**
	 * 获取词法单元的类型
	 * @return 词法单元的类型
	 */
	public String getType() {
		return type;
	}	
}
