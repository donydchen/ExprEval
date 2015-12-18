package lexer;

/**
 * 所有词法单元的父类。
 * @author Donald
 *
 */
public class Token {
	protected String type;

	
	public Token() {
		// TODO Auto-generated constructor stub
		type = "";
	}
	
	public String getType() {
		return type;
	}
	
}
