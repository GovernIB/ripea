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
	
	public List<Long> getIdsMetaExpedientsPermesos() {
		return Utils.geValueOrNull(idsMetaExpedientsPermesos) ;
	}
	public List<Long> getIdsOrgansPermesos() {
		return Utils.geValueOrNull(idsOrgansPermesos);
	}
	public List<Long> getIdsMetaExpedientOrganPairsPermesos() {
		return Utils.geValueOrNull(idsMetaExpedientOrganPairsPermesos);
	}
	public List<Long> getIdsOrgansAmbProcedimentsComunsPermesos() {
		return Utils.geValueOrNull(idsOrgansAmbProcedimentsComunsPermesos);
	}
	public List<Long> getIdsProcedimentsComuns() {
		return Utils.geValueOrNull(idsProcedimentsComuns);
	}
	
	

	
}
