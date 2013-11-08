package it.dcs.iscrivo.mailclientnew.dao.blogic.interfaces;

import it.dcs.iscrivo.documentnew.exceptions.IscrivoException;
import it.dcs.iscrivo.mailclientnew.dao.enums.CriteriaOperator;
import it.dcs.iscrivo.mailclientnew.dao.model.CriteriaDAO;
import it.dcs.iscrivo.mailclientnew.dao.model.Field;
import it.dcs.iscrivo.mailclientnew.dao.model.JoinCondition;
import it.dcs.iscrivo.mailclientnew.dao.model.PaginatedData;

import java.util.List;

public interface IGenericDAOService<T extends IGenericDAOEntity> {
	public T save(T t) throws IscrivoException;

	public T simpleSave(T model) throws IscrivoException;

	public void remove(T model) throws IscrivoException;

	public void remove(int id) throws IscrivoException;

	void removeAllWhere(String name, Object value) throws IscrivoException;

	public T get(int id) throws IscrivoException;

	public List<T> getAll() throws IscrivoException;

	public List<T> getAll(List<String> orderBy) throws IscrivoException;

	public boolean isPresent() throws IscrivoException;

	public Long getCountObjects(List<CriteriaDAO> criteriaDAOs) throws IscrivoException;

	public Long getCountObjects(String name, List<CriteriaDAO> criteriaDAOs) throws IscrivoException;

	public Long getCountObjects(CriteriaDAO... criteriaDAOs) throws IscrivoException;

	public Long getCountObjects(String name, CriteriaDAO... criteriaDAOs) throws IscrivoException;

	public Long getCountObjects(String name, JoinCondition joinCondition, CriteriaDAO... criteriaDAOs)
			throws IscrivoException;

	Long getCountObjects(String name, List<JoinCondition> joinConditions, CriteriaDAO... criteriaDAOs)
			throws IscrivoException;

	Long getCountObjects(String name, List<JoinCondition> joinConditions, List<String> groupBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException;

	Long getCountObjects(String name, JoinCondition joinCondition, List<String> groupBy, CriteriaDAO... criteriaDAOs)
			throws IscrivoException;

	public PaginatedData<T> getLazyLoadObjects(int maxResult, int firstResult, List<String> orderBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException;

	public PaginatedData<T> getLazyLoadObjects(int maxResult, int firstResult, JoinCondition joinCondition,
			List<String> orderBy, CriteriaDAO... criteriaDAOs) throws IscrivoException;

	PaginatedData<T> getLazyLoadObjects(int maxResult, int firstResult, JoinCondition joinCondition,
			List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs) throws IscrivoException;

	PaginatedData<T> getLazyLoadObjects(int maxResult, int firstResult, List<JoinCondition> joinConditions,
			List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs) throws IscrivoException;

	PaginatedData<T> getLazyLoadObjects(String countGroupName, int maxResult, int firstResult,
			List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs)
			throws IscrivoException;

	PaginatedData<T> getLazyLoadObjects(String countGroupName, List<Field> fields, int maxResult,
			int firstResult, List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException ;
	
	public T getFirstObjectByField(String name, Object value) throws IscrivoException;

	public T getFirstObjectByField(String name, Object value, CriteriaOperator criteriaOperator)
			throws IscrivoException;

	public T getFirstObjectByField(CriteriaDAO... criteriaDAOs) throws IscrivoException;

	@SuppressWarnings("rawtypes")
	Object getFirstObjectByField(List<Field> fields, Class returnClass, List<JoinCondition> joinConditions,
			List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs) throws IscrivoException;

	@SuppressWarnings("rawtypes")
	List<Object> getAllObjectsByField(int maxResults, int firstResult, List<Field> fields, Class returnClass,
			List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs)
			throws IscrivoException;

	public List<T> getAllObjectsByField(CriteriaDAO... criteriaDAOs) throws IscrivoException;

	public List<T> getAllObjectsByField(String name, Object value) throws IscrivoException;

	public List<T> getAllObjectsByField(String name, Object value, CriteriaOperator criteriaOperator)
			throws IscrivoException;

	public List<T> getAllObjectsByField(String name, Object value, List<String> orderBy) throws IscrivoException;

	public List<T> getAllObjectsByField(String name, Object value, CriteriaOperator criteriaOperator,
			List<String> orderBy) throws IscrivoException;

	public List<T> getAllObjectsByField(String name, Object value, CriteriaOperator criteriaOperator, int maxResults)
			throws IscrivoException;

	public List<T> getAllObjectsByField(String name, Object value, CriteriaOperator criteriaOperator, int maxResults,
			List<String> orderBy) throws IscrivoException;

	List<T> getAllObjectsByField(int maxResults, List<String> orderBy, CriteriaDAO... criteriaDAOs)
			throws IscrivoException;

	List<T> getAllObjectsByField(JoinCondition joinCondition, CriteriaDAO... criteriaDAOs) throws IscrivoException;

	List<T> getAllObjectsByField(List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException;

	List<T> getAllObjectsByField(int maxResults, int firstResult, JoinCondition joinCondition, List<String> orderBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException;

	List<T> getAllObjectsByField(JoinCondition joinCondition, List<String> orderBy, List<String> groupBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException;

	List<T> getAllObjectsByField(int maxResults, int firstResult, JoinCondition joinCondition, List<String> orderBy,
			List<String> groupBy, CriteriaDAO... criteriaDAOs) throws IscrivoException;

	List<T> getAllObjectsByField(int maxResults, int firstResult, List<JoinCondition> joinConditions,
			List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs) throws IscrivoException;

	List getAllObjectsByField(List<Field> fields, JoinCondition joinCondition, CriteriaDAO... criteriaDAOs)
			throws IscrivoException;

	@SuppressWarnings("rawtypes")
	List getAllObjectsByField(int maxResults, int firstResult, List<Field> fields, List<JoinCondition> joinConditions,
			List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs) throws IscrivoException;

}