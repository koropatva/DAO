package it.dcs.iscrivo.mailclientnew.dao.model;

import it.dcs.iscrivo.mailclientnew.dao.enums.CriteriaJoin;

public class JoinCondition {
	private boolean			distinct;

	private String			join;

	private String			as;

	private CriteriaJoin	criteriaJoin;

	public JoinCondition(String join, String as) {
		this(join, as, false, CriteriaJoin.INNER_JOIN);
	}

	public JoinCondition(String join, String as, boolean distinct) {
		this(join, as, distinct, CriteriaJoin.INNER_JOIN);
	}

	public JoinCondition(String join, String as, boolean distinct, CriteriaJoin criteriaJoin) {
		this.join = join;
		this.as = as;
		this.distinct = distinct;
		this.criteriaJoin = criteriaJoin;
	}

	public String getJoin() {
		return join;
	}

	public String getAs() {
		return as;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setCriteriaJoin(CriteriaJoin criteriaJoin) {
		this.criteriaJoin = criteriaJoin;
	}

	public CriteriaJoin getCriteriaJoin() {
		return criteriaJoin;
	}
}
