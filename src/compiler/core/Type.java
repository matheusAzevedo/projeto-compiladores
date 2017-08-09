package compiler.core;

public class Type {
	private String typeName;

	public Type(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Type)) {
			return false;
		}
		return this.getTypeName().toLowerCase().equals(((Type) obj).getTypeName().toLowerCase());
	}

	@Override
	public String toString() {
		return getTypeName();
	}
	
	
	
}
