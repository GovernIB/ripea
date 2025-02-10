package es.caib.ripea.core.helper;

import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.persistence.MetaExpedientEntity;
import lombok.Setter;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;
@Setter
public class PermisosPerAnotacions {

	private static final int DEFAULT_PARTITIONS_SIZE = 4;
	private static final int MAX_PARTITION_ELEMENTS = 999;

	boolean adminOrganHasPermisAdminComu;

	List<String> adminOrganCodisOrganAmbDescendents;
	List<MetaExpedientEntity> procedimentsPermesos;
	List<Long> idsGrupsPermesos;

	private List<List<String>> adminOrganCodisOrganAmbDescendentsSplit;
	private List<List<MetaExpedientEntity>> procedimentsPermesosSplit;
	
	public boolean isAdminOrganHasPermisAdminComu() {
		return adminOrganHasPermisAdminComu;
	}
	public List<String> getAdminOrganCodisOrganAmbDescendents() {
		return Utils.getNullIfEmpty(adminOrganCodisOrganAmbDescendents);
	}
	public List<MetaExpedientEntity> getProcedimentsPermesos() {
		return Utils.getNullIfEmpty(procedimentsPermesos);
	}
	public List<Long> getIdsGrupsPermesos() {
		return Utils.getNullIfEmpty(idsGrupsPermesos);
	}


	public void setAdminOrganCodisOrganAmbDescendents(List<String> adminOrganCodisOrganAmbDescendents) {
		this.adminOrganCodisOrganAmbDescendents = adminOrganCodisOrganAmbDescendents;
		this.adminOrganCodisOrganAmbDescendentsSplit = listSplit(adminOrganCodisOrganAmbDescendents);
	}

	public List<String> getAdminOrganCodisOrganAmbDescendents(int index) {
		return getList(this.adminOrganCodisOrganAmbDescendentsSplit, index);
	}

	public void setProcedimentsPermesos(List<MetaExpedientEntity> procedimentsPermesos) {
		this.procedimentsPermesos = procedimentsPermesos;
		this.procedimentsPermesosSplit = listSplit(procedimentsPermesos);
	}

	public List<MetaExpedientEntity> getProcedimentsPermesos(int index) {
		return getList(this.procedimentsPermesosSplit, index);
	}

	private static <T> List<List<T>> listSplit(List<T> list) {

		if (list == null || list.isEmpty()) {
			return initializeSplits();
		} else {
			List<List<T>> sublists = new ArrayList<>(ListUtils.partition(list, MAX_PARTITION_ELEMENTS));

			while (sublists.size() < DEFAULT_PARTITIONS_SIZE) {
				sublists.add(new ArrayList<T>());
			}
			return sublists;
		}
	}
	private static <T> List<List<T>> initializeSplits() {
		List<List<T>> newSplits = new ArrayList<>(DEFAULT_PARTITIONS_SIZE);
		for (int i = 0; i < DEFAULT_PARTITIONS_SIZE; i++) {
			newSplits.add(new ArrayList<T>());
		}
		return newSplits;
	}

	private static <T> List<Boolean> esNullSplit(List<List<T>> list) {
		List<Boolean> sonNull = new ArrayList<>();
		for (List<T> sublist: list) {
			sonNull.add(sublist == null || sublist.isEmpty());
		}
		return sonNull;
	}

	private static <T> List<T> getList(List<List<T>> list, int index) {
		if (list == null) {
//			throw new NullPointerException("La llista és nul·la.");
			return null;
		}
		if (index < 0) {
			throw new IndexOutOfBoundsException("Index " + index + ". L'índex no pot ser negatiu.");
		}
		if (index > list.size()) {
			throw new IndexOutOfBoundsException("Index " + index + ". La llista només té " + list.size() + " elements.");
		}
		return Utils.getNullIfEmpty(list.get(index));
	}
}
