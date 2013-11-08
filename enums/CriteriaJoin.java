package it.dcs.iscrivo.mailclientnew.dao.enums;

public enum CriteriaJoin {
	LEFT_JOIN("LEFT JOIN"), RIGHT_JOIN("RIGHT JOIN"), INNER_JOIN("INNER JOIN");

	private CriteriaJoin() {
	}

	private CriteriaJoin(String description) {
		this.description = description;
	}

	private String	description;

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
