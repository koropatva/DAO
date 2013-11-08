package it.dcs.iscrivo.mailclientnew.dao.model;

import java.io.Serializable;

import it.dcs.iscrivo.mailclientnew.dao.enums.CriteriaCondition;
import it.dcs.iscrivo.mailclientnew.dao.enums.CriteriaOperator;

public class CriteriaDAO implements Serializable {

	private static final long	serialVersionUID	= 1L;

	public CriteriaDAO(CriteriaDAO... criteriaDAOs) {
		this.criteriaDAOs = criteriaDAOs;
	}

	public CriteriaDAO(String paramName, Object paramValue) {
		this(paramName, paramValue, CriteriaOperator.EQUALS, CriteriaCondition.AND);
	}

	public CriteriaDAO(String paramName, Object paramValue, CriteriaCondition criteriaCondition) {
		this(paramName, paramValue, CriteriaOperator.EQUALS, criteriaCondition);
	}

	public CriteriaDAO(String paramName, Object paramValue, CriteriaOperator criteriaOperator) {
		this(paramName, paramValue, criteriaOperator, CriteriaCondition.AND);
	}

	public CriteriaDAO(String paramName, Object paramValue, CriteriaOperator criteriaOperator,
			CriteriaCondition criteriaCondition) {
		this.paramName = paramName;
		this.paramValue = paramValue;
		this.criteriaCondition = criteriaCondition;
		this.criteriaOperator = criteriaOperator;
	}

	private CriteriaDAO[]		criteriaDAOs;

	private String				paramName;

	private Object				paramValue;

	private CriteriaOperator	criteriaOperator;

	private CriteriaCondition	criteriaCondition;

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

	public void setCriteriaCondition(CriteriaCondition criteriaCondition) {
		this.criteriaCondition = criteriaCondition;
	}

	public CriteriaCondition getCriteriaCondition() {
		return criteriaCondition;
	}

	public void setCriteriaDAOs(CriteriaDAO[] criteriaDAOs) {
		this.criteriaDAOs = criteriaDAOs;
	}

	public CriteriaDAO[] getCriteriaDAOs() {
		return criteriaDAOs;
	}

}
