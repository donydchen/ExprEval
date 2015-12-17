package lexer;

public class CalBoolean extends Token {
	
	protected Boolean value;
	
	public CalBoolean(String temp) {
		type = "Boolean";
		if (temp == "true")
			value = true;
		else {
			value = false;
		}
	}
	
	/**
	 * 返回布尔对象对应的布尔值
	 * @return
	 */
	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean newValue) {
		value = newValue;
	}
}
