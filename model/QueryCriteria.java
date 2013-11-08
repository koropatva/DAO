package it.dcs.iscrivo.mailclientnew.dao.model;

import it.dcs.iscrivo.mailclientnew.dao.enums.CriteriaOperator;

public class QueryCriteria {

	public QueryCriteria(String paramName, Object paramValue, CriteriaOperator criteriaOperator) {
		this.paramName = paramName;
		this.paramValue = paramValue;
		this.criteriaOperator = criteriaOperator;
	}

	private String				paramName;

	private Object				paramValue;

	private CriteriaOperator	criteriaOperator;

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

	public void setCriteriaOperator(CriteriaOperator criteriaOperator) {
		this.criteriaOperator = criteriaOperator;
	}

	public CriteriaOperator getCriteriaOperator() {
		return criteriaOperator;
	}
}