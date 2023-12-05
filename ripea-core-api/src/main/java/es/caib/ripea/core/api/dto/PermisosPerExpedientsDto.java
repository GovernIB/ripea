package es.caib.ripea.core.api.dto;

import java.util.List;

import es.caib.ripea.core.api.utils.Utils;
import lombok.Setter;
@Setter
public class PermisosPerExpedientsDto  {
	List<Long> idsMetaExpedientsPermesos;
	List<Long> idsOrgansPermesos;
	List<Long> idsMetaExpedientOrganPairsPermesos;
	List<Long> idsOrgansAmbProcedimentsComunsPermesos;
	List<Long> idsProcedimentsComuns;
	List<Long> idsGrupsPermesos;
	
	public List<Long> getIdsMetaExpedientsPermesos() {
		return Utils.getNullIfEmpty(idsMetaExpedientsPermesos) ;
	}
	public List<Long> getIdsOrgansPermesos() {
		return Utils.getNullIfEmpty(idsOrgansPermesos);
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
	

	
}
