package lexer;

/**
 * 十进制数的词法单元，继承了Token类。
 * @author Donald
 * @see Token
 */
public class CalDecimal extends Token {
	/**
	 * 十进制数的数值
	 */
	protected Double value;
	/**
	 * 初始化十进制数词法单元。包括设置其类型为Decimal，并将科学计数法转化为double类型。
	 * @param num  保存被认为是十进制数的字符串。
	 */
	public CalDecimal(String num) {
		type = "Decimal";
		String lowerNum = num.toLowerCase();
		int hasE = lowerNum.indexOf('e');
		if (hasE != -1) {
			double fraction = Double.parseDouble(lowerNum.substring(0, hasE));
			double exponent;
			switch (lowerNum.charAt(hasE+1)) {
			case '-':
				exponent = Double.parseDouble(lowerNum.substring(hasE+2, lowerNum.length()));
				value = fraction / Math.pow(10.0, exponent);
				break;
			case '+':
				exponent = Double.parseDouble(lowerNum.substring(hasE+2, lowerNum.length()));
				value = fraction * Math.pow(10.0, exponent);
				break;
			default:
				exponent = Double.parseDouble(lowerNum.substring(hasE+1, lowerNum.length()));
				value = fraction * Math.pow(10.0, exponent);
				break;
			}
		}
		else {
			value = Double.parseDouble(lowerNum);
		}
		
	}	
	/**
	 * 获取十进制的数值
	 * @return 该词法单元的额十进制数值
	 */
	public Double getValue() {
		return value;
	}
	/**
	 * 设置词法单元的十进制数数值
	 * @param newValue double类型数值。
	 */
	public void setValue(Double newValue) {
		value = newValue;
	}
}


