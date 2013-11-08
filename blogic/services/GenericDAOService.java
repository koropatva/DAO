package it.dcs.iscrivo.mailclientnew.dao.blogic.services;

import it.dcs.iscrivo.central.web.UserData;
import it.dcs.iscrivo.documentnew.exceptions.IscrivoException;
import it.dcs.iscrivo.mailclientnew.dao.blogic.interfaces.IGenericDAOEntity;
import it.dcs.iscrivo.mailclientnew.dao.blogic.interfaces.IGenericDAOService;
import it.dcs.iscrivo.mailclientnew.dao.blogic.interfaces.IModificationData;
import it.dcs.iscrivo.mailclientnew.dao.enums.CriteriaCondition;
import it.dcs.iscrivo.mailclientnew.dao.enums.CriteriaOperator;
import it.dcs.iscrivo.mailclientnew.dao.model.CriteriaDAO;
import it.dcs.iscrivo.mailclientnew.dao.model.Field;
import it.dcs.iscrivo.mailclientnew.dao.model.JoinCondition;
import it.dcs.iscrivo.mailclientnew.dao.model.PaginatedData;
import it.dcs.iscrivo.mailclientnew.dao.model.QueryCriteria;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class GenericDAOService<T extends IGenericDAOEntity> implements IGenericDAOService<T> {

	protected EntityManager		entityManager;

	protected Logger			logger				= Logger.getLogger(GenericDAOService.class);

	public static final String	QUERY_MODEL_NAME	= "model";

	private Class<T>			type;

	protected String			className;

	protected String			sql;

	protected Query				query;

	private int					counter;

	public abstract void setEntityManager(EntityManager entityManager);

	@Resource
	protected ApplicationContext	ctx;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GenericDAOService() {
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		type = (Class) pt.getActualTypeArguments()[0];
		className = type.getName().substring(type.getName().lastIndexOf(".") + 1);
	}

	@Transactional
	public synchronized T save(T model) throws IscrivoException {
		logger.info("save CALLED");

		if (model.getId() == null || model.getId().equals(0)) {
			if (model instanceof IModificationData) {
				((IModificationData) model).setCreationTime(new Date());
				((IModificationData) model).setUpdateTime(new Date());
				try {
					UserData userData = null;
					try {
						userData = (UserData) ctx.getBean("userData");
					} catch (Exception e) {
					}
					if (userData != null) {
						((IModificationData) model).setCreationUser(userData.getCurrentLoggedUser());
						((IModificationData) model).setUpdateUser(userData.getCurrentLoggedUser());
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			entityManager.persist(model);
		} else {
			if (model instanceof IModificationData) {
				((IModificationData) model).setUpdateTime(new Date());
				try {
					UserData userData = null;
					try {
						userData = (UserData) ctx.getBean("userData");
					} catch (Exception e) {
					}
					if (userData != null) {
						((IModificationData) model).setUpdateUser(userData.getCurrentLoggedUser());
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			entityManager.merge(model);
		}
		return model;
	}

	@Transactional
	public synchronized T simpleSave(T model) throws IscrivoException {
		logger.info("simpleSave CALLED");
		if (model.getId() == null || model.getId().equals(0)) {
			entityManager.persist(model);
		} else {
			entityManager.merge(model);
		}
		return model;
	}

	public synchronized void remove(T model) throws IscrivoException {
		if (model != null && model.getId() != null && !model.getId().equals(0)) {
			remove(model.getId());
		}
	}

	public synchronized void removeAllWhere(String name, Object value) throws IscrivoException {
		logger.info("removeAll CALLED");
		List<T> list = getAllObjectsByField(name, value);
		for (T model : list) {
			remove(model);
		}
	}

	@Override
	@Transactional
	public synchronized void remove(int id) throws IscrivoException {
		logger.info("remove CALLED");
		entityManager.remove(get(id));
	}

	@Override
	public synchronized T get(int id) throws IscrivoException {
		logger.info("get CALLED");
		return (T) entityManager.find(type, id);
	}

	public synchronized List<T> getAll() throws IscrivoException {
		return getAllObjectsByField(0, null);
	}

	public synchronized List<T> getAll(List<String> orderBy) throws IscrivoException {
		return getAllObjectsByField(0, orderBy);
	}

	public synchronized boolean isPresent() throws IscrivoException {
		T model = getFirstObjectByField("id", null, CriteriaOperator.IS_NOT_NULL);
		if (model != null) {
			return true;
		} else {
			return false;
		}
	}

	public synchronized Long getCountObjects(List<CriteriaDAO> criteriaDAOs) throws IscrivoException {
		return getCountObjects(null, criteriaDAOs);
	}

	public synchronized Long getCountObjects(String name, List<CriteriaDAO> criteriaDAOs) throws IscrivoException {
		CriteriaDAO[] daos = new CriteriaDAO[criteriaDAOs.size()];
		criteriaDAOs.toArray(daos);
		return getCountObjects(name, daos);
	}

	public synchronized Long getCountObjects(CriteriaDAO... criteriaDAOs) throws IscrivoException {
		return getCountObjects(null, criteriaDAOs);
	}

	public synchronized Long getCountObjects(String name, CriteriaDAO... criteriaDAOs) throws IscrivoException {
		return getCountObjects(name, new ArrayList<JoinCondition>(), new ArrayList<String>(), criteriaDAOs);
	}

	public synchronized Long getCountObjects(String name, JoinCondition joinCondition, CriteriaDAO... criteriaDAOs)
			throws IscrivoException {
		return getCountObjects(name, joinCondition, null, criteriaDAOs);
	}

	public Long getCountObjects(String name, JoinCondition joinCondition, List<String> groupBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException {
		List<JoinCondition> joinConditions = new ArrayList<JoinCondition>();
		if (joinCondition != null) {
			joinConditions.add(joinCondition);
		}
		return getCountObjects(name, joinConditions, criteriaDAOs);
	}

	public synchronized Long getCountObjects(String name, List<JoinCondition> joinConditions,
			CriteriaDAO... criteriaDAOs) throws IscrivoException {
		return getCountObjects(name, joinConditions, null, criteriaDAOs);
	}

	public synchronized Long getCountObjects(String name, List<JoinCondition> joinConditions, List<String> groupBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException {
		logger.info("getCountObjects CALLED");
		String countRegex = "";
		if (name == null || name.equals("")) {
			countRegex = String.format("%s.id", QUERY_MODEL_NAME);
		} else {
			countRegex = name;
		}

		if (joinConditions == null) {
			sql = String.format("SELECT COUNT(DISTINCT %s) FROM %s AS %s WHERE ", countRegex, className,
					QUERY_MODEL_NAME);
		} else {
			sql = String.format("SELECT COUNT(DISTINCT %s) FROM %s AS %s ", countRegex, className, QUERY_MODEL_NAME);
			for (JoinCondition joinCondition : joinConditions) {
				sql += String.format(" %s %s AS %s ", joinCondition.getCriteriaJoin().getDescription(),
						joinCondition.getJoin(), joinCondition.getAs());
			}
			sql += " WHERE ";
		}

		List<QueryCriteria> queryCriterias = null;
		if (criteriaDAOs != null && criteriaDAOs.length > 0) {
			counter = 0;
			queryCriterias = fillAllCriteria(Arrays.asList(criteriaDAOs));
		}
		clearSql();

		if (groupBy != null) {
			List<String> newGroupBy = new ArrayList<String>();
			for (String group : groupBy) {
				if (!group.equals(countRegex)) {
					newGroupBy.add(group);
				}
			}

			sql += addGroupBy(newGroupBy);
		}
		query = entityManager.createQuery(sql);

		if (queryCriterias != null) {
			fillAllCriteriaParams(queryCriterias);
		}

		return (Long) query.getSingleResult();

	}

	public synchronized PaginatedData<T> getLazyLoadObjects(int maxResult, int firstResult, List<String> orderBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException {
		return getLazyLoadObjects(maxResult, firstResult, null, orderBy, criteriaDAOs);
	}

	public synchronized PaginatedData<T> getLazyLoadObjects(int maxResult, int firstResult,
			JoinCondition joinCondition, List<String> orderBy, CriteriaDAO... criteriaDAOs) throws IscrivoException {
		return getLazyLoadObjects(maxResult, firstResult, joinCondition, orderBy, null, criteriaDAOs);
	}

	public synchronized PaginatedData<T> getLazyLoadObjects(int maxResult, int firstResult,
			JoinCondition joinCondition, List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs)
			throws IscrivoException {
		List<JoinCondition> joinConditions = new ArrayList<JoinCondition>();
		if (joinCondition != null) {
			joinConditions.add(joinCondition);
		}
		return getLazyLoadObjects(maxResult, firstResult, joinConditions, orderBy, groupBy, criteriaDAOs);
	}

	public synchronized PaginatedData<T> getLazyLoadObjects(int maxResult, int firstResult,
			List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs)
			throws IscrivoException {
		return getLazyLoadObjects(null, maxResult, firstResult, joinConditions, orderBy, groupBy, criteriaDAOs);
	}

	public synchronized PaginatedData<T> getLazyLoadObjects(String countGroupName, int maxResult, int firstResult,
			List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs)
			throws IscrivoException {
		return getLazyLoadObjects(countGroupName, null, maxResult, firstResult, joinConditions, orderBy, groupBy,
				criteriaDAOs);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized PaginatedData<T> getLazyLoadObjects(String countGroupName, List<Field> fields, int maxResult,
			int firstResult, List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException {

		logger.info(String.format("getLazyLoadObjects CALLED(%d, %d)", maxResult, firstResult));
		List<T> list;
		if (fields == null) {
			list = getAllObjectsByField(maxResult, firstResult, joinConditions, orderBy, groupBy, criteriaDAOs);
		} else {
			list = new ArrayList<T>();
			List objectList = getAllObjectsByField(maxResult, firstResult, fields, joinConditions, orderBy, groupBy,
					criteriaDAOs);
			List<Object> listObjects = marshallFields(objectList, fields, type);
			for (Object object : listObjects) {
				if (object instanceof IGenericDAOEntity) {
					list.add((T) object);
				}
			}
		}

		return new PaginatedData<T>(list, getCountObjects(countGroupName, joinConditions, null, criteriaDAOs));
	}

	public synchronized T getFirstObjectByField(String name, Object value) throws IscrivoException {
		return getFirstObjectByField(name, value, CriteriaOperator.EQUALS);
	}

	public synchronized T getFirstObjectByField(String name, Object value, CriteriaOperator criteriaOperator)
			throws IscrivoException {
		return getFirstObjectByField(new CriteriaDAO(name, value, criteriaOperator));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized T getFirstObjectByField(CriteriaDAO... criteriaDAOs) throws IscrivoException {
		List list = getAllObjectsByField(0, null, criteriaDAOs);

		if (list != null && list.size() > 0) {
			return (T) list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public synchronized Object getFirstObjectByField(List<Field> fields, Class returnClass,
			List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs)
			throws IscrivoException {
		List list = getAllObjectsByField(0, 0, fields, returnClass, joinConditions, orderBy, groupBy, criteriaDAOs);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public synchronized List<T> getAllObjectsByField(List<CriteriaDAO> criteriaDAOs) throws IscrivoException {
		return getAllObjectsByField(0, null, criteriaDAOs.toArray(new CriteriaDAO[criteriaDAOs.size()]));
	}

	public synchronized List<T> getAllObjectsByField(CriteriaDAO... criteriaDAOs) throws IscrivoException {
		return getAllObjectsByField(0, null, criteriaDAOs);
	}

	public synchronized List<T> getAllObjectsByField(String name, Object value) throws IscrivoException {
		return getAllObjectsByField(name, value, CriteriaOperator.EQUALS, 0);
	}

	public synchronized List<T> getAllObjectsByField(String name, Object value, CriteriaOperator criteriaOperator)
			throws IscrivoException {
		return getAllObjectsByField(name, value, criteriaOperator, 0);
	}

	public synchronized List<T> getAllObjectsByField(String name, Object value, List<String> orderBy)
			throws IscrivoException {
		return getAllObjectsByField(name, value, CriteriaOperator.EQUALS, 0, orderBy);
	}

	public synchronized List<T> getAllObjectsByField(String name, Object value, CriteriaOperator criteriaOperator,
			List<String> orderBy) throws IscrivoException {
		return getAllObjectsByField(name, value, criteriaOperator, 0, orderBy);
	}

	public synchronized List<T> getAllObjectsByField(String name, Object value, CriteriaOperator criteriaOperator,
			int maxResults) throws IscrivoException {
		return getAllObjectsByField(name, value, criteriaOperator, 0, null);
	}

	public synchronized List<T> getAllObjectsByField(String name, Object value, CriteriaOperator criteriaOperator,
			int maxResults, List<String> orderBy) throws IscrivoException {
		if (criteriaOperator == null) {
			throw new IscrivoException("criteriaOperator IS NULL");
		}

		if (value == null
				&& (criteriaOperator.equals(CriteriaOperator.EQUALS) || criteriaOperator.equals(CriteriaOperator.LIKE) || criteriaOperator
						.equals(CriteriaOperator.NOT_EQUALS))) {
			throw new IscrivoException("value IS NULL");
		}
		logger.info(String
				.format("getAllObjectsByField CALLEd with(name = '%s', value = '%s', criteriaOperator = '%s', maxResults = '%d')",
						name, value, criteriaOperator.toString(), maxResults));

		return getAllObjectsByField(maxResults, orderBy, new CriteriaDAO(name, value, criteriaOperator));
	}

	public synchronized List<T> getAllObjectsByField(JoinCondition joinCondition, CriteriaDAO... criteriaDAOs)
			throws IscrivoException {
		return getAllObjectsByField(0, 0, joinCondition, null, null, criteriaDAOs);
	}

	public synchronized List<T> getAllObjectsByField(JoinCondition joinCondition, List<String> orderBy,
			List<String> groupBy, CriteriaDAO... criteriaDAOs) throws IscrivoException {
		return getAllObjectsByField(0, 0, joinCondition, orderBy, groupBy, criteriaDAOs);
	}

	public synchronized List<T> getAllObjectsByField(List<JoinCondition> joinConditions, List<String> orderBy,
			List<String> groupBy, CriteriaDAO... criteriaDAOs) throws IscrivoException {
		return getAllObjectsByField(0, 0, joinConditions, orderBy, groupBy, criteriaDAOs);
	}

	public synchronized List<T> getAllObjectsByField(int maxResults, List<String> orderBy, CriteriaDAO... criteriaDAOs)
			throws IscrivoException {
		return getAllObjectsByField(maxResults, 0, orderBy, criteriaDAOs);
	}

	public synchronized List<T> getAllObjectsByField(int maxResults, int firstResult, List<String> orderBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException {
		return getAllObjectsByField(maxResults, firstResult, null, orderBy, criteriaDAOs);
	}

	private void createSql(List<JoinCondition> joinConditions) {
		createSql(null, joinConditions);
	}

	private void createSql(List<Field> fields, List<JoinCondition> joinConditions) {
		sql = "SELECT ";
		if (fields == null || fields.isEmpty()) {
			sql += String.format("%s ", QUERY_MODEL_NAME);
		} else {
			for (Field field : fields) {
				if (field.isJoin()) {
					sql += String.format("%s, ", field.getName());
				} else {
					sql += String.format("%s.%s, ", QUERY_MODEL_NAME, field.getName());
				}
			}
			sql = StringUtils.removeEnd(sql, ", ");
		}
		sql += String.format(" FROM %s AS %s ", className, QUERY_MODEL_NAME);

		if (joinConditions != null && !joinConditions.isEmpty()) {
			for (JoinCondition joinCondition : joinConditions) {
				if (joinCondition.getCriteriaJoin() != null && joinCondition.getJoin() != null
						&& joinCondition.getAs() != null) {
					sql += String.format("%s %s AS %s ", joinCondition.getCriteriaJoin().getDescription(),
							joinCondition.getJoin(), joinCondition.getAs());
				}
			}
		}
		sql += " WHERE ";
	}

	public synchronized List<T> getAllObjectsByField(int maxResults, int firstResult, JoinCondition joinCondition,
			CriteriaDAO... criteriaDAOs) throws IscrivoException {
		return getAllObjectsByField(maxResults, firstResult, joinCondition, null, criteriaDAOs);
	}

	public synchronized List<T> getAllObjectsByField(int maxResults, int firstResult, JoinCondition joinCondition,
			List<String> orderBy, CriteriaDAO... criteriaDAOs) throws IscrivoException {
		return getAllObjectsByField(maxResults, firstResult, joinCondition, orderBy, null, criteriaDAOs);
	}

	public synchronized List<T> getAllObjectsByField(int maxResults, int firstResult, JoinCondition joinCondition,
			List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs) throws IscrivoException {
		List<JoinCondition> joinConditions = null;
		if (joinCondition != null) {
			joinConditions = new ArrayList<JoinCondition>();
			joinConditions.add(joinCondition);
		}
		return getAllObjectsByField(maxResults, firstResult, joinConditions, orderBy, groupBy, criteriaDAOs);
	}

	@SuppressWarnings("unchecked")
	public synchronized List<T> getAllObjectsByField(int maxResults, int firstResult,
			List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs)
			throws IscrivoException {

		createSql(joinConditions);

		List<QueryCriteria> queryCriterias = null;
		if (criteriaDAOs != null && criteriaDAOs.length > 0) {
			counter = 0;
			queryCriterias = fillAllCriteria(Arrays.asList(criteriaDAOs));
		}
		clearSql();

		if (groupBy != null) {
			sql += addGroupBy(groupBy);
		}

		if (orderBy != null) {
			sql += addOrderBy(orderBy);
		}

		query = entityManager.createQuery(sql);

		if (queryCriterias != null) {
			fillAllCriteriaParams(queryCriterias);
		}

		if (maxResults != 0) {
			query.setMaxResults(maxResults);
		}

		if (firstResult != 0) {
			query.setFirstResult(firstResult);
		}

		// Find first result
		List<T> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("rawtypes")
	public synchronized List getAllObjectsByField(List<Field> fields, JoinCondition joinCondition,
			CriteriaDAO... criteriaDAOs) throws IscrivoException {
		List<JoinCondition> joinConditions = null;
		if (joinCondition != null) {
			joinConditions = new ArrayList<JoinCondition>();
			joinConditions.add(joinCondition);
		}
		return getAllObjectsByField(0, 0, fields, joinConditions, null, null, criteriaDAOs);
	}

	@SuppressWarnings("rawtypes")
	public synchronized List getAllObjectsByField(int maxResults, int firstResult, List<Field> fields,
			List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs)
			throws IscrivoException {

		createSql(fields, joinConditions);

		List<QueryCriteria> queryCriterias = null;
		if (criteriaDAOs != null && criteriaDAOs.length > 0) {
			counter = 0;
			queryCriterias = fillAllCriteria(Arrays.asList(criteriaDAOs));
		}
		clearSql();

		if (groupBy != null) {
			sql += addGroupBy(groupBy);
		}

		if (orderBy != null) {
			sql += addOrderBy(orderBy);
		}

		query = entityManager.createQuery(sql);

		if (queryCriterias != null) {
			fillAllCriteriaParams(queryCriterias);
		}

		if (maxResults != 0) {
			query.setMaxResults(maxResults);
		}

		if (firstResult != 0) {
			query.setFirstResult(firstResult);
		}
		return query.getResultList();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized List<Object> getAllObjectsByField(int maxResults, int firstResult, List<Field> fields,
			Class returnClass, List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy,
			CriteriaDAO... criteriaDAOs) throws IscrivoException {
		createSql(fields, joinConditions);

		List<QueryCriteria> queryCriterias = null;
		if (criteriaDAOs != null && criteriaDAOs.length > 0) {
			counter = 0;
			queryCriterias = fillAllCriteria(Arrays.asList(criteriaDAOs));
		}
		clearSql();

		if (groupBy != null) {
			sql += addGroupBy(groupBy);
		}

		if (orderBy != null) {
			sql += addOrderBy(orderBy);
		}

		query = entityManager.createQuery(sql);

		if (queryCriterias != null) {
			fillAllCriteriaParams(queryCriterias);
		}

		if (maxResults != 0) {
			query.setMaxResults(maxResults);
		}

		if (firstResult != 0) {
			query.setFirstResult(firstResult);
		}

		return marshallFields(query.getResultList(), fields, returnClass);
	}

	protected String addGroupBy(List<String> groupBy) {
		String sqlGroupBy = "";
		if (groupBy != null && !groupBy.isEmpty()) {
			sqlGroupBy = " GROUP BY ";
			for (int i = 0; i < groupBy.size(); i++) {
				sqlGroupBy += String.format(" %s, ", groupBy.get(i));
			}
			if (sqlGroupBy.endsWith(", ")) {
				sqlGroupBy = sqlGroupBy.substring(0, sqlGroupBy.length() - 2);
			}
		}
		return sqlGroupBy;
	}

	protected String addOrderBy(List<String> orderBy) {
		String sqlOrderBy = "";
		if (orderBy != null && !orderBy.isEmpty()) {
			sqlOrderBy = " ORDER BY ";
			for (int i = 0; i < orderBy.size(); i++) {
				sqlOrderBy += String.format(" %s, ", orderBy.get(i));
			}
			if (sqlOrderBy.endsWith(", ")) {
				sqlOrderBy = sqlOrderBy.substring(0, sqlOrderBy.length() - 2);
			}
		}
		return sqlOrderBy;
	}

	protected synchronized List<QueryCriteria> fillAllCriteria(List<CriteriaDAO> criteriaDAOs) {
		List<QueryCriteria> queryCriterias = new ArrayList<QueryCriteria>();
		for (CriteriaDAO criteriaDAO : criteriaDAOs) {
			// Check if criteria have group
			if (criteriaDAO.getCriteriaDAOs() != null) {
				if (!Arrays.asList(criteriaDAO.getCriteriaDAOs()).isEmpty()) {
					groupCriteriaGenerator(queryCriterias, Arrays.asList(criteriaDAO.getCriteriaDAOs()));
				}
				continue;
			}

			if (!throwAwayEmptyCriteria(criteriaDAO.getCriteriaOperator(), criteriaDAO.getParamValue())) {
				continue;
			}
			counter++;
			QueryCriteria queryCriteria = new QueryCriteria("paramValue" + counter, criteriaDAO.getParamValue(),
					criteriaDAO.getCriteriaOperator());

			if (criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_EQUALS
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_MORE
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_MORE_EQUAL
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_LESS
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_LESS_EQUAL
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_IN
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_IS_EMPTY
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_IS_NOT_EMPTY
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_IS_NOT_NULL
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_IS_NULL
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_LIKE
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_START_WITH
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_ENDS_WITH
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_NOT_EQUALS
					|| criteriaDAO.getCriteriaOperator() == CriteriaOperator.JOIN_NOT_IN) {
				sql += fillCriteria(criteriaDAO.getCriteriaOperator(), criteriaDAO.getCriteriaCondition(),
						criteriaDAO.getParamName(), queryCriteria.getParamName());
			} else {
				sql += fillCriteria(criteriaDAO.getCriteriaOperator(), criteriaDAO.getCriteriaCondition(),
						String.format("%s.%s", QUERY_MODEL_NAME, criteriaDAO.getParamName()),
						queryCriteria.getParamName());
			}
			queryCriterias.add(queryCriteria);
		}
		return queryCriterias;
	}

	private void groupCriteriaGenerator(List<QueryCriteria> queryCriterias, List<CriteriaDAO> criteriaDAOs) {
		// Open Group
		sql += "( ";

		// Fill all criteria
		queryCriterias.addAll(fillAllCriteria(criteriaDAOs));

		// Close Group
		if (sql.endsWith("AND ")) {
			sql = sql.substring(0, sql.length() - 4) + ") AND ";
		}
		if (sql.endsWith("OR ")) {
			sql = sql.substring(0, sql.length() - 4) + ") OR ";
		}
		if (sql.endsWith("( ")) {
			sql = sql.substring(0, sql.length() - 2);
		}
	}

	@SuppressWarnings("rawtypes")
	protected synchronized boolean throwAwayEmptyCriteria(CriteriaOperator criteriaOperator, Object paramValue) {
		if (criteriaOperator.equals(CriteriaOperator.IS_EMPTY)
				|| criteriaOperator.equals(CriteriaOperator.IS_NOT_EMPTY)
				|| criteriaOperator.equals(CriteriaOperator.IS_NULL)
				|| criteriaOperator.equals(CriteriaOperator.IS_NOT_NULL)
				|| criteriaOperator.equals(CriteriaOperator.JOIN_IS_EMPTY)
				|| criteriaOperator.equals(CriteriaOperator.JOIN_IS_NOT_EMPTY)
				|| criteriaOperator.equals(CriteriaOperator.JOIN_IS_NULL)
				|| criteriaOperator.equals(CriteriaOperator.JOIN_IS_NOT_NULL)) {
			return true;
		}

		if (paramValue == null) {
			return false;
		}

		if (paramValue instanceof String && paramValue.equals("")) {
			return false;
		}
		if (paramValue instanceof List && ((List) paramValue).isEmpty()) {
			return false;
		}
		if (paramValue instanceof Map && ((Map) paramValue).isEmpty()) {
			return false;
		}
		if (paramValue instanceof Set && ((Set) paramValue).isEmpty()) {
			return false;
		}
		if (paramValue instanceof ArrayList && ((ArrayList) paramValue).isEmpty()) {
			return false;
		}
		return true;

	}

	protected synchronized String fillCriteria(CriteriaOperator criteriaOperator, CriteriaCondition criteriaCondition,
			String fieldName, String paramName) {
		switch (criteriaOperator) {
			case JOIN_IS_NOT_NULL:
			case IS_NOT_NULL:
				return String.format(" %s IS NOT NULL %s ", fieldName, criteriaCondition.name());
			case JOIN_IS_NULL:
			case IS_NULL:
				return String.format(" %s IS NULL %s ", fieldName, criteriaCondition.name());
			case JOIN_IS_EMPTY:
			case IS_EMPTY:
				return String.format(" (%s IS NULL OR %s LIKE '') %s ", fieldName, fieldName, criteriaCondition.name());
			case JOIN_IS_NOT_EMPTY:
			case IS_NOT_EMPTY:
				return String.format(" (%s IS NOT NULL AND %s NOT LIKE '') %s ", fieldName, fieldName,
						criteriaCondition.name());
			case JOIN_LIKE:
			case LIKE:
				return " " + fieldName + " LIKE '%' || :" + paramName + " || '%' " + criteriaCondition.name() + " ";
			case JOIN_START_WITH:
			case STARTS_WITH:
				return " " + fieldName + " LIKE :" + paramName + " || '%' " + criteriaCondition.name() + " ";
			case JOIN_ENDS_WITH:
			case ENDS_WITH:
				return " " + fieldName + " LIKE '%' || :" + paramName + criteriaCondition.name() + " ";
			case JOIN_NOT_EQUALS:
			case NOT_EQUALS:
				return String.format(" %s != :%s %s ", fieldName, paramName, criteriaCondition.name());
			case JOIN_IN:
			case IN:
				if (fieldName.endsWith(".")) {
					fieldName = fieldName.substring(0, fieldName.length() - 1);
				}
				return String.format(" %s IN (:%s) %s ", fieldName, paramName, criteriaCondition.name());
			case JOIN_NOT_IN:
			case NOT_IN:
				if (fieldName.endsWith(".")) {
					fieldName = fieldName.substring(0, fieldName.length() - 1);
				}

				return String.format(" %s NOT IN (:%s) %s ", fieldName, paramName, criteriaCondition.name());
			case JOIN_MORE:
			case MORE:
				return String.format(" %s > :%s %s ", fieldName, paramName, criteriaCondition.name());
			case JOIN_MORE_EQUAL:
			case MORE_EQUAL:
				return String.format(" %s >= :%s %s ", fieldName, paramName, criteriaCondition.name());
			case JOIN_LESS:
			case LESS:
				return String.format(" %s < :%s %s ", fieldName, paramName, criteriaCondition.name());
			case JOIN_LESS_EQUAL:
			case LESS_EQUAL:
				return String.format(" %s <= :%s %s ", fieldName, paramName, criteriaCondition.name());
			case JOIN_EQUALS:
			case EQUALS:
			default:
				return String.format(" %s = :%s %s ", fieldName, paramName, criteriaCondition.name());
		}
	}

	protected synchronized void fillAllCriteriaParams(List<QueryCriteria> queryCriterias) {
		for (QueryCriteria queryCriteria : queryCriterias) {
			fillCriteriaParam(queryCriteria);
		}

	}

	protected synchronized void fillCriteriaParam(QueryCriteria queryCriteria) {
		switch (queryCriteria.getCriteriaOperator()) {
			case JOIN_EQUALS:
			case EQUALS:
			case JOIN_MORE:
			case MORE:
			case JOIN_MORE_EQUAL:
			case MORE_EQUAL:
			case JOIN_LESS:
			case LESS:
			case JOIN_LESS_EQUAL:
			case LESS_EQUAL:
			case JOIN_NOT_EQUALS:
			case NOT_EQUALS:
			case JOIN_LIKE:
			case LIKE:
			case JOIN_START_WITH:
			case STARTS_WITH:
			case JOIN_ENDS_WITH:
			case ENDS_WITH:
			case JOIN_IN:
			case IN:
			case JOIN_NOT_IN:
			case NOT_IN:
				query.setParameter(queryCriteria.getParamName(), queryCriteria.getParamValue());
				break;
			case JOIN_IS_EMPTY:
			case IS_EMPTY:
			case JOIN_IS_NOT_EMPTY:
			case IS_NOT_EMPTY:
			case JOIN_IS_NOT_NULL:
			case IS_NOT_NULL:
			case JOIN_IS_NULL:
			case IS_NULL:
			default:
				break;
		}
	}

	protected synchronized void clearSql() {
		for (CriteriaCondition criteriaCondition : CriteriaCondition.values()) {
			String condition = criteriaCondition.name() + " ";
			if (sql != null && sql.endsWith(condition)) {
				sql = sql.substring(0, sql.length() - condition.length());
			}
		}

		if (sql != null && sql.endsWith("WHERE ")) {
			sql = sql.substring(0, sql.length() - 6);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Object> marshallFields(List<Object> objects, List<Field> fields, Class returnClass)
			throws IscrivoException {
		try {
			List list = new ArrayList();
			for (Object object : objects) {
				Object[] objects2 = (Object[]) object;
				Object newObject = returnClass.newInstance();

				for (int i = 0; i < fields.size(); i++) {

					if (fields.get(i).getPropertyType() != null && fields.get(i).getPropertyType().equals(Date.class)
							&& objects2[i] == null) {
						logger.info("Date is null need to skip. BeanUtils have a bug for it converting");
					} else {
						BeanUtils.setProperty(newObject, fields.get(i).getPropertyName(), objects2[i]);
					}
				}
				list.add(newObject);

			}
			return list;
		} catch (Exception e) {
			throw new IscrivoException(e.getMessage(), e);
		}
	}
}
