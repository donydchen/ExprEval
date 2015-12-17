package lexer;

public class Dollar extends Token {
	
	protected String lexeme;
	protected String label;
	
	public Dollar() {
		type = "Dollar";
		lexeme = "$";
		label = "$";
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public String getLabel() {
		return label;
	}
	
}
