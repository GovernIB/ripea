package es.caib.ripea.service.intf.dto;

import es.caib.ripea.service.intf.utils.Utils;
import lombok.Setter;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
public class PermisosPerExpedientsDto  {

	private static final int DEFAULT_PARTITIONS_SIZE = 4;
	private static final int MAX_PARTITION_ELEMENTS = 999;

	List<Long> idsMetaExpedientsPermesos;
	List<Long> idsOrgansPermesos;
	List<Long> idsOrganActualAndDescendents;
	List<Long> idsMetaExpedientOrganPairsPermesos;
	List<Long> idsOrgansAmbProcedimentsComunsPermesos;
	List<Long> idsProcedimentsComuns;
	List<Long> idsGrupsPermesos;

	private List<List<Long>> idsMetaExpedientsPermesosSplit;
	private List<List<Long>> idsOrgansPermesosSplit;
	private List<List<Long>> idsOrganActualAndDescendentsSplit;
	private List<List<Long>> idsMetaExpedientOrganPairsPermesosSplit;
	private List<List<Long>> idsOrgansAmbProcedimentsComunsPermesosSplit;
	private List<List<Long>> idsProcedimentsComunsSplit;

	public List<Long> getIdsMetaExpedientsPermesos() {
		return Utils.getNullIfEmpty(idsMetaExpedientsPermesos) ;
	}
	public List<Long> getIdsOrgansPermesos() {
		return Utils.getNullIfEmpty(idsOrgansPermesos);
	}
	public List<Long> getIdsOrganActualAndDescendents() {
		return Utils.getNullIfEmpty(idsOrganActualAndDescendents);
	}
	public List<Long> getIdsMetaExpedientOrganPairsPermesos() {
		return Utils.getNullIfEmpty(idsMetaExpedientOrganPairsPermesos);
	}
	public List<Long> getIdsOrgansAmbProcedimentsComunsPermesos() {
		return Utils.getNullIfEmpty(idsOrgansAmbProcedimentsComunsPermesos);
	}
	public List<Long> getIdsProcedimentsComuns() {
		return Utils.getNullIfEmpty(idsProcedimentsComuns);
	}
	public List<Long> getIdsGrupsPermesos() {
		return Utils.getNullIfEmpty(idsGrupsPermesos);
	}

	public void setIdsMetaExpedientsPermesos(List<Long> idsMetaExpedientsPermesos) {
		this.idsMetaExpedientsPermesos = idsMetaExpedientsPermesos;
		this.idsMetaExpedientsPermesosSplit = listSplit(idsMetaExpedientsPermesos);
	}
	public List<Long> getIdsMetaExpedientsPermesos(int index) {
		return getList(this.idsMetaExpedientsPermesosSplit, index);
	}

	public void setIdsOrgansPermesos(List<Long> idsOrgansPermesos) {
		this.idsOrgansPermesos = idsOrgansPermesos;
		this.idsOrgansPermesosSplit = listSplit(idsOrgansPermesos);
	}
	public List<Long> getIdsOrgansPermesos(int index) {
		return getList(this.idsOrgansPermesosSplit, index);
	}

	public void setIdsOrganActualAndDescendents(List<Long> idsOrganActualAndDescendents) {
		this.idsOrganActualAndDescendents = idsOrganActualAndDescendents;
		this.idsOrganActualAndDescendentsSplit = listSplit(idsOrganActualAndDescendents);
	}
	
	public void setIdsMetaExpedientOrganPairsPermesos(List<Long> idsMetaExpedientOrganPairsPermesos) {
		this.idsMetaExpedientOrganPairsPermesos = idsMetaExpedientOrganPairsPermesos;
		this.idsMetaExpedientOrganPairsPermesosSplit = listSplit(idsMetaExpedientOrganPairsPermesos);
	}
	public List<Long> getIdsMetaExpedientOrganPairsPermesos(int index) {
		return getList(this.idsMetaExpedientOrganPairsPermesosSplit, index);
	}

	public void setIdsOrgansAmbProcedimentsComunsPermesos(List<Long> idsOrgansAmbProcedimentsComunsPermesos) {
		this.idsOrgansAmbProcedimentsComunsPermesos = idsOrgansAmbProcedimentsComunsPermesos;
		this.idsOrgansAmbProcedimentsComunsPermesosSplit = listSplit(idsOrgansAmbProcedimentsComunsPermesos);
	}
	public List<Long> getIdsOrgansAmbProcedimentsComunsPermesos(int index) {
		return getList(this.idsOrgansAmbProcedimentsComunsPermesosSplit, index);
	}

	public void setIdsProcedimentsComuns(List<Long> idsProcedimentsComuns) {
		this.idsProcedimentsComuns = idsProcedimentsComuns;
		this.idsProcedimentsComunsSplit = listSplit(idsProcedimentsComuns);
	}
	public List<Long> getIdsProcedimentsComuns(int index) {
		return getList(this.idsProcedimentsComunsSplit, index);
	}

    public List<String> getIdsProcedimentsGruposMil() {
    	return getIdsProcedimentsGruposMil(this.idsMetaExpedientsPermesos);
    }
    
    public List<String> getIdsGrupsGruposMil() {
    	return getIdsProcedimentsGruposMil(this.idsGrupsPermesos);
    }
    
    public List<String> getIdsMetaExpedientOrganPairsGruposMil() {
    	return getIdsProcedimentsGruposMil(this.idsMetaExpedientOrganPairsPermesos);
    }
    
    public List<String> getIdsOrgansAmbProcedimentsComunsGruposMil() {
    	return getIdsProcedimentsGruposMil(this.idsOrgansAmbProcedimentsComunsPermesos);
    }
    
    public List<String> getIdsOrgansGruposMil() {
    	return getIdsProcedimentsGruposMil(this.idsOrgansPermesos);
    }
    
    public List<String> getIdsProcedimentsComunsGruposMil() {
    	return getIdsProcedimentsGruposMil(this.idsProcedimentsComuns);
    }
    
    private List<String> getIdsProcedimentsGruposMil(List<Long> ids) {
    	int maxSize = 1000;
    	if (ids!=null && ids.size()>0) {
    		List<String> result = new ArrayList<>();
    		for (int i = 0; i < ids.size(); i += maxSize) {
                List<Long> subList = ids.subList(i, Math.min(i + maxSize, ids.size()));
                String concatenated = subList.stream().map(String::valueOf).collect(Collectors.joining(","));
                result.add(concatenated);
            }
    		return result;
    	}
    	return null;
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

	private static <T> List<T> getList(List<List<T>> list, int index) {
		if (list == null) {
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
