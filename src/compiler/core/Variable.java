package compiler.core;

public class Variable implements Parameter {
	private Type type;
	private String identifier;
	private Expression value;

	public Variable(String identifier, Type type) {
		this.type = type;
		this.identifier = identifier;
	}

	public Variable(String identifier, Type type, Expression value) {
		this.type = type;
		this.identifier = identifier;
		this.value = value;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Expression getValue() {
		return value;
	}

	public void setValue(Expression value) {
		this.value = value;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.identifier + " of type: " + getType().getTypeName();
	}

}
