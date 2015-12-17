package lexer;

public class CalFunction extends Token{
	
	protected String lexeme;
	protected String label;
	
	public CalFunction(String func) {
		type = "Function";
		lexeme = func;
		label = "func";
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public String getLabel() {
		return label;
	}
}
