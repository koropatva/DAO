package it.dcs.iscrivo.mailclientnew.dao.model;

import it.dcs.iscrivo.mailclientnew.dao.blogic.interfaces.IGenericDAOEntity;
import it.dcs.iscrivo.mailclientnew.dao.blogic.interfaces.IGenericDAOService;
import it.dcs.iscrivo.mailclientnew.dao.blogic.services.GenericDAOService;
import it.dcs.iscrivo.mailclientnew.dao.enums.CriteriaCondition;
import it.dcs.iscrivo.mailclientnew.dao.enums.CriteriaOperator;
import it.dcs.newsletter.engine.enums.MailStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortOrder;

public class LazyDataLoad<T extends IGenericDAOEntity> extends LazyDataModel<T> implements SelectableDataModel<T> {

	private static final long		serialVersionUID	= 1L;

	private Logger					logger				= Logger.getLogger(LazyDataLoad.class);

	protected List<T>				datasource;

	protected CriteriaDAO[]			criteriaDAOs;

	protected IGenericDAOService<T>	iGenericDAOService;

	protected List<T>				models;

	protected List<JoinCondition>	joinConditions;

	protected List<String>			orderBy;

	protected List<String>			groupBy;

	protected String				countGroupName;

	protected List<Field>			fields;

	public LazyDataLoad(List<T> models) {
		this.models = models;
	}

	public LazyDataLoad(IGenericDAOService<T> iGenericDAOService, CriteriaDAO... criteriaDAOs) {
		this(iGenericDAOService, new ArrayList<JoinCondition>(), null, null, criteriaDAOs);
	}

	public LazyDataLoad(IGenericDAOService<T> iGenericDAOService, List<String> orderBy, CriteriaDAO... criteriaDAOs) {
		this(iGenericDAOService, new ArrayList<JoinCondition>(), orderBy, criteriaDAOs);
	}

	public LazyDataLoad(IGenericDAOService<T> iGenericDAOService, JoinCondition joinCondition,
			CriteriaDAO... criteriaDAOs) {
		this(iGenericDAOService, joinCondition, null, criteriaDAOs);
	}

	public LazyDataLoad(IGenericDAOService<T> iGenericDAOService, JoinCondition joinCondition, List<String> orderBy,
			CriteriaDAO... criteriaDAOs) {
		this(iGenericDAOService, joinCondition, orderBy, null, criteriaDAOs);
	}

	public LazyDataLoad(IGenericDAOService<T> iGenericDAOService, List<JoinCondition> joinConditions,
			List<String> orderBy, CriteriaDAO... criteriaDAOs) {
		this(iGenericDAOService, joinConditions, orderBy, null, criteriaDAOs);
	}

	public LazyDataLoad(IGenericDAOService<T> iGenericDAOService, JoinCondition joinCondition, List<String> orderBy,
			List<String> groupBy, CriteriaDAO... criteriaDAOs) {
		this(iGenericDAOService, null, joinCondition, orderBy, groupBy, criteriaDAOs);
	}

	public LazyDataLoad(IGenericDAOService<T> iGenericDAOService, String countGroupName, JoinCondition joinCondition,
			List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs) {
		List<JoinCondition> joinConditions = new ArrayList<JoinCondition>();
		if (joinCondition != null) {
			joinConditions.add(joinCondition);
		}
		this.countGroupName = countGroupName;
		this.iGenericDAOService = iGenericDAOService;
		this.criteriaDAOs = criteriaDAOs;
		this.models = null;
		this.orderBy = orderBy;
		this.groupBy = groupBy;
		this.joinConditions = joinConditions;
	}

	public LazyDataLoad(IGenericDAOService<T> iGenericDAOService, List<JoinCondition> joinConditions,
			List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs) {
		this(iGenericDAOService, null, joinConditions, orderBy, groupBy, criteriaDAOs);
	}

	public LazyDataLoad(IGenericDAOService<T> iGenericDAOService, String countGroupName,
			List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs) {
		this(iGenericDAOService, null, countGroupName, joinConditions, orderBy, groupBy, criteriaDAOs);
	}

	public LazyDataLoad(IGenericDAOService<T> iGenericDAOService, List<Field> fields, String countGroupName,
			List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs) {
		this.iGenericDAOService = iGenericDAOService;
		this.fields = fields;
		this.countGroupName = countGroupName;
		this.criteriaDAOs = criteriaDAOs;
		this.models = null;
		this.orderBy = orderBy;
		this.groupBy = groupBy;
		this.joinConditions = joinConditions;
	}

	@Override
	/** Will fix error 
			java.lang.ArithmeticException: / by zero
	 		at org.primefaces.model.LazyDataModel.setRowIndex(LazyDataModel.java:62)... **/
	public void setRowIndex(int rowIndex) {
		if (rowIndex == -1 || getPageSize() == 0) {
			super.setRowIndex(-1);
		} else
			super.setRowIndex(rowIndex % getPageSize());
	}

	@Override
	public T getRowData(String rowKey) {
		for (T model : datasource)
			if (model instanceof IGenericDAOEntity
					&& ((IGenericDAOEntity) model).getId().equals(Integer.parseInt(rowKey))) {
				return model;
			}
		return null;
	}

	@Override
	public Object getRowKey(T model) {
		if (model instanceof IGenericDAOEntity) {
			return ((IGenericDAOEntity) model).getId();
		}
		return null;
	}

	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		logger.info("load CALLED (" + first + ", " + pageSize + ", " + sortField + ")");
		try {
			datasource = new ArrayList<T>();
			if (models == null) {
				if (sortField != null) {
					orderBy = new ArrayList<String>();
					String order;

					switch (sortOrder) {
						case DESCENDING:
							order = "DESC";
							break;

						case ASCENDING:
						case UNSORTED:
						default:
							order = "ASC";
							break;
					}
					orderBy.add(String.format("%s.%s %s", GenericDAOService.QUERY_MODEL_NAME, sortField, order));
				}

				PaginatedData<T> data = iGenericDAOService.getLazyLoadObjects(countGroupName, fields, pageSize, first,
						joinConditions, orderBy, groupBy, generateCriteriaWithFilters(filters));
				this.setRowCount((int) data.getTotalRecords());
				datasource.addAll(data.getCollection());
			} else {

				this.setRowCount(models.size());

				if (sortField != null) {
					String methodName = "get" + sortField.substring(0, 1).toUpperCase() + sortField.substring(1);
					Collections.sort(models, new Comparator1<T>(methodName, sortOrder));
				}
				ListIterator<T> iterator = models.listIterator(first);
				while (iterator.hasNext() && pageSize > 0) {
					datasource.add(iterator.next());
					pageSize--;
				}
			}
			return datasource;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	protected CriteriaDAO[] generateCriteriaWithFilters(Map<String, String> filters) {
		if (filters != null && !filters.isEmpty()) {
			List<CriteriaDAO> list = new ArrayList<CriteriaDAO>();
			for (String key : filters.keySet()) {
				if (!key.endsWith("description")) {
					list.add(new CriteriaDAO(key, filters.get(key), CriteriaOperator.LIKE));
				} else {
					String paramName = key.replace(".description", "");
					if (paramName.equals("mailStatus")) {
						List<CriteriaDAO> criteriaDAOs = new ArrayList<CriteriaDAO>();
						for (MailStatus mailStatus : MailStatus.values()) {
							if (mailStatus.getDescription().toUpperCase().startsWith(filters.get(key).toUpperCase())) {
								criteriaDAOs.add(new CriteriaDAO(paramName, mailStatus, CriteriaCondition.OR));
							}
						}
						if (!criteriaDAOs.isEmpty()) {
							criteriaDAOs.get(criteriaDAOs.size() - 1).setCriteriaCondition(CriteriaCondition.AND);
						} else {
							criteriaDAOs.add(new CriteriaDAO(paramName, null, CriteriaOperator.IS_EMPTY));
						}
						list.add(new CriteriaDAO(criteriaDAOs.toArray(new CriteriaDAO[criteriaDAOs.size()])));
					}
				}
			}

			return (CriteriaDAO[]) ArrayUtils.addAll(criteriaDAOs, list.toArray());
		}
		return criteriaDAOs;
	}

	public List<T> getDatasource() {
		return datasource;
	}
}

class Comparator1<T> implements Comparator<T> {

	private String	methodName;

	private int		sortOrder;

	public Comparator1(String methodName, SortOrder sortOrder) {
		this.methodName = methodName;
		switch (sortOrder) {
			case DESCENDING:
				this.sortOrder = -1;
				break;
			case ASCENDING:
			default:
				this.sortOrder = 1;
				break;
		}
	}

	@Override
	public int compare(T o1, T o2) {
		try {
			Method method1 = o1.getClass().getMethod(methodName);
			Method method2 = o2.getClass().getMethod(methodName);
			if (method1 != null && method2 != null) {
				Object object1 = method1.invoke(o1);
				Object object2 = method2.invoke(o2);
				if (object1 != null && object2 != null) {
					return object1.toString().compareTo(object2.toString()) * sortOrder;
				}
			}

		} catch (SecurityException e) {
			return 0;
		} catch (NoSuchMethodException e) {
			return 0;
		} catch (IllegalArgumentException e) {
			return 0;
		} catch (IllegalAccessException e) {
			return 0;
		} catch (InvocationTargetException e) {
			return 0;
		}
		return 0;
	}

}