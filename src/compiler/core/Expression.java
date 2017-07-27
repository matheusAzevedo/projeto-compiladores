package compiler.core;

public class Expression {
	private Type type;
	private String value;
	private String context;

	public Expression(Type t) {
		this.type = t;
	}

	public Expression(String name) {
		type = new Type("UNKNOWN");
	}

	public Expression(Type t, String value) {
		this.type = t;
		this.value = value;
		this.context = "";
	}

	public Type getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public String getContext() {
		return this.context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public boolean isNumeric() {
		return getType().getTypeName().equals("int") || getType().getTypeName().equals("float")
				|| getType().getTypeName().equals("long") || getType().getTypeName().equals("double");
	}

	public String toString() {
		return "Expression of type; " + getType();
	}
}
