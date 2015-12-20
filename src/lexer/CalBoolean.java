package lexer;

/**
 * 布尔类型的词法单元，继承了Token类。
 * @author Donald
 * @see Token
 */
public class CalBoolean extends Token {
	/**
	 * 存储布尔对象对应的布尔值。
	 */
	protected Boolean value;
	/**
	 * 初始化布尔对象。
	 * 将其类型设置为Boolean，并设置其value。
	 * @param temp  保存被认为是布尔值的字符串。
	 */
	public CalBoolean(String temp) {
		type = "Boolean";
		temp = temp.toLowerCase();
		if (temp.equals("true") )
			value = true;
		else {
			value = false;
		}
	}
	/**
	 * 返回布尔对象对应的布尔值。
	 * @return 该对象的布尔值。
	 */
	public Boolean getValue() {
		return value;
	}
	/**
	 * 设置对象的布尔值。
	 * @param newValue  用来设置value的布尔值。
	 */
	public void setValue(Boolean newValue) {
		value = newValue;
	}
}
