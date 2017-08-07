/**
 * 
 */
package compiler.core;

import java.util.HashMap;

/**
 * @author mendelssohn
 *
 */
public class ScopedEntity extends NamedEntity {
	private HashMap<String, Variable> vars;
	private HashMap<String, Type> types;

	public ScopedEntity(String name) {
		super(name);
		vars = new HashMap<String, Variable>();
	}

	public HashMap<String, Variable> getVars() {
		return vars;
	}

	public void setVars(HashMap<String, Variable> vars) {
		this.vars = vars;
	}

	public HashMap<String, Type> getTypes() {
		return types;
	}

	public void setTypes(HashMap<String, Type> types) {
		this.types = types;
	}

	public void addVariable(Variable v) {
		this.vars.put(v.getIdentifier(), v);
	}

	public void addType(Type t) {
		this.types.put(t.getTypeName(), t);
	}

}
