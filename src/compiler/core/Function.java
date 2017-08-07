package compiler.core;

import java.util.ArrayList;
import java.util.List;

import compiler.exceptions.InvalidFunctionException;

public class Function extends ScopedEntity {
	Type declaredReturnType;
	Type returnType;
	List<Parameter> params;
	
	public Function(String name, ArrayList<Parameter>params){
        super(name);
        
        if (params != null) {
			this.params = params;
		} else {
			this.params = new ArrayList<Parameter>();
		}
	}

	public Type getDeclaredReturnType() {
		return declaredReturnType;
	}

	public void setDeclaredReturnType(Type declaredReturnType) {
		this.declaredReturnType = declaredReturnType;
	}

	public Type getReturnType() {
		return returnType;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	public List<Parameter> getParams() {
		return params;
	}

	public void setParams(List<Parameter> params) {
		this.params = params;
	}
	
	public void validateReturnType() throws InvalidFunctionException {
		if (!returnType.equals(declaredReturnType)) {
			throw new InvalidFunctionException("A função " + getName() + " supostamente deveria retornar um " + declaredReturnType);
		}
	}
	
	public void addParam(Parameter a){
		params.add(a);
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Function)) { 
			return false; 
		}
		
		Function function = (Function) obj;
		
		if(!function.getName().equals(getName())) {
			return false;
		}
		
		if(function.getParams().size() != getParams().size()) {
			return false;
		}
		
		for(int i=0; i<getParams().size(); i++){
			if(!function.getParams().get(i).getType().equals(getParams().get(i).getType())) {
				return false;
			}
		}
		
		return true;
	}
}
