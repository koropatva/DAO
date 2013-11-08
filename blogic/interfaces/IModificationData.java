package it.dcs.iscrivo.mailclientnew.dao.blogic.interfaces;

import it.dcs.iscrivo.central.model.User;

import java.util.Date;

public interface IModificationData {
	public void setCreationTime(Date creationTime);

	public void setCreationUser(User creationUser);

	public void setUpdateTime(Date updateTime);

	public void setUpdateUser(User updateUser);
}
