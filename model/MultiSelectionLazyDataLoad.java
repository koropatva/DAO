package it.dcs.iscrivo.mailclientnew.dao.model;

import it.dcs.iscrivo.documentnew.exceptions.IscrivoException;
import it.dcs.iscrivo.mailclientnew.dao.blogic.interfaces.IGenericDAOEntity;
import it.dcs.iscrivo.mailclientnew.dao.blogic.interfaces.IGenericDAOService;
import it.dcs.iscrivo.mailclientnew.dao.blogic.interfaces.IMultiSelectionEvent;
import it.dcs.iscrivo.mailclientnew.dao.enums.CriteriaOperator;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;

public class MultiSelectionLazyDataLoad<T extends IGenericDAOEntity> extends LazyDataLoad<T> {

	private static final long	serialVersionUID	= 1L;

	private Logger				logger				= Logger.getLogger(MultiSelectionLazyDataLoad.class);

	private boolean				normal;

	private List<T>				ignored				= new ArrayList<T>();

	public MultiSelectionLazyDataLoad(boolean normal, List<Field> fields, IGenericDAOService<T> iGenericDAOService,
			String countGroupName, List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy,
			CriteriaDAO... criteriaDAOs) {
		super(iGenericDAOService, fields, countGroupName, joinConditions, orderBy, groupBy, criteriaDAOs);
		this.normal = normal;
	}

	public MultiSelectionLazyDataLoad(boolean normal, IGenericDAOService<T> iGenericDAOService, String countGroupName,
			List<JoinCondition> joinConditions, List<String> orderBy, List<String> groupBy, CriteriaDAO... criteriaDAOs) {
		super(iGenericDAOService, countGroupName, joinConditions, orderBy, groupBy, criteriaDAOs);
		this.normal = normal;
	}

	public void toggleAll() {
		logger.info("toggleAll CALLED");

		normal = !normal;
		ignored.clear();
	}

	public void listener() {
		logger.info("listener CALLED");
	}

	public boolean isNotInIgnoredList() {
		for (T ignoredModel : ignored) {
			if (ignoredModel.getId().equals(getRowData().getId())) {
				return !normal;
			}
		}
		return normal;
	}

	public void setNotInIgnoredList(boolean notInIgnoredList) {

	}

	public void toggleInIgnoredList(ValueChangeEvent event) {
		logger.info("toggleInIgnoredList CALLED");
		T selectedDataModel = getRowData();
		if (normal) {
			if ((Boolean) event.getNewValue()) {
				for (T ignoredModel : ignored) {
					if (ignoredModel.getId().equals(selectedDataModel.getId())) {
						ignored.remove(ignoredModel);
						break;
					}
				}
			} else {
				ignored.add(selectedDataModel);
			}
		} else {
			if ((Boolean) event.getNewValue()) {
				ignored.add(selectedDataModel);
			} else {
				for (T ignoredModel : ignored) {
					if (ignoredModel.getId().equals(selectedDataModel.getId())) {
						ignored.remove(ignoredModel);
						break;
					}
				}
			}
		}
	}

	public boolean isSelected() {
		if (normal && getRowCount() != ignored.size() || !normal && ignored.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	public LazyDataModel<T> getSelectedList() {
		logger.info("getSelectedList CALLED");
		if (normal) {
			return this;
		} else {
			return new LazyDataLoad<T>(ignored);
		}
	}

	public List<T> getSelectedData() throws IscrivoException {
		checkData();
		logger.info("getSelectedData CALLED");
		if (normal) {
			return iGenericDAOService.getAllObjectsByField(joinConditions, orderBy, groupBy, getSelectedCriteria());
		} else {
			return ignored;
		}
	}

	public int getSelectedRowCount() {
		if (normal) {
			return getRowCount() - ignored.size();
		} else {
			return ignored.size();
		}

	}

	private void checkData() throws IscrivoException {
		if (iGenericDAOService == null) {
			throw new IscrivoException(
					"iGenericDAOService IS null. You need to initialize MultiSelection use constructors with IGenericDAOService params");
		}
	}

	public CriteriaDAO[] getSelectedCriteria() {
		List<Integer> ids = new ArrayList<Integer>();
		for (T model : ignored) {
			ids.add(model.getId());
		}

		CriteriaDAO[] daos = (CriteriaDAO[]) ArrayUtils.addAll(criteriaDAOs, new CriteriaDAO[] { new CriteriaDAO("id",
				ids, CriteriaOperator.NOT_IN) });
		return daos;
	}

	public void multiSelectionEvent(IMultiSelectionEvent<T> iMultiSelectionEvent) throws IscrivoException {
		multiSelectionEvent(iMultiSelectionEvent, 1);
	}

	public void multiSelectionEvent(IMultiSelectionEvent<T> iMultiSelectionEvent, int maxResult)
			throws IscrivoException {
		checkData();
		logger.info("multiSelectionEvent CALLED");
		if (normal) {
			int firstResult = 0;
			while (true) {
				List<T> list = iGenericDAOService.getAllObjectsByField(maxResult, firstResult, joinConditions, orderBy,
						groupBy, getSelectedCriteria());
				if (list.isEmpty()) {
					break;
				}
				for (T model : list) {
					iMultiSelectionEvent.multiSelectionEvent(model);
					firstResult++;
				}
			}

		} else {
			for (T model : ignored) {
				iMultiSelectionEvent.multiSelectionEvent(model);
			}
		}
	}

	public void setIgnored(List<T> ignored) {
		this.ignored = ignored;
	}

	public List<T> getIgnored() {
		return ignored;
	}

	public void setNormal(boolean normal) {
		this.normal = normal;
	}

	public boolean isNormal() {
		return normal;
	}

	public void setCriteriaDAOs(CriteriaDAO[] criteriaDAOs) {
		this.criteriaDAOs = criteriaDAOs;
	}

	public CriteriaDAO[] getCriteriaDAOs() {
		return criteriaDAOs;
	}

	public List<JoinCondition> getJoinConditions() {
		return joinConditions;
	}

	public void setJoinConditions(List<JoinCondition> joinConditions) {
		this.joinConditions = joinConditions;
	}

	public List<String> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(List<String> orderBy) {
		this.orderBy = orderBy;
	}

	public List<String> getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(List<String> groupBy) {
		this.groupBy = groupBy;
	}

}
