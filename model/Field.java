package it.dcs.iscrivo.mailclientnew.dao.model;

public class Field {
	public Field(String name) {
		this(name, name, false, null);
	}

	public Field(String name, String propertyName) {
		this(name, propertyName, true, null);
	}

	public Field(String name, Object propertyType){
		this(name, name, false, propertyType);
	}
	
	public Field(String name, String propertyName, boolean join, Object propertyType) {
		this.name = name;
		this.propertyName = propertyName;
		this.join = join;
		this.propertyType = propertyType;
	}

	private String	name;

	private String	propertyName;

	private Object	propertyType;

	private boolean	join;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isJoin() {
		return join;
	}

	public void setJoin(boolean join) {
		this.join = join;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Object getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(Object propertyType) {
		this.propertyType = propertyType;
	}

}
