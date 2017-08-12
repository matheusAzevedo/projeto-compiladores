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

	public void setType(Type type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
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
	
	public boolean isString() {
        return getType().getTypeName().equalsIgnoreCase("String");
    }

	public String toString() {
		return "Expression of type; " + getType();
	}

	public String getAssemblyValue() {
		if(this.value != null){
			if(this.value.equals("true")){
				return "1";
			}else if(this.value.equals("false")){
				return "0";
			}
		}
		return this.value;
	}
}
