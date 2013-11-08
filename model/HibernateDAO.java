package it.dcs.iscrivo.mailclientnew.dao.model;

public class HibernateDAO {

	public HibernateDAO(String paramName, Object paraValue) {
		this.paramName = paramName;
		this.paramValue = paraValue;
	}

	private String	paramName;

	private Object	paramValue;

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamValue(Object paramValue) {
		this.paramValue = paramValue;
	}

	public Object getParamValue() {
		return paramValue;
	}

}
