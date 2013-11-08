package it.dcs.iscrivo.mailclientnew.dao.blogic.interfaces;


public interface IMultiSelectionEvent<T extends IGenericDAOEntity> {
	void multiSelectionEvent(T selectedEntity);
}
