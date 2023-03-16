package es.caib.ripea.core.helper;

import java.util.List;

import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import lombok.Setter;
@Setter
public class PermisosPerAnotacions {
	
	boolean adminOrganHasPermisAdminComu;
	List<String> adminOrganCodisOrganAmbDescendents;
	List<MetaExpedientEntity> procedimentsPermesos;
	
	public boolean isAdminOrganHasPermisAdminComu() {
		return adminOrganHasPermisAdminComu;
	}
	public List<String> getAdminOrganCodisOrganAmbDescendents() {
		return Utils.getNullIfEmpty(adminOrganCodisOrganAmbDescendents);
	}
	public List<MetaExpedientEntity> getProcedimentsPermesos() {
		return Utils.getNullIfEmpty(procedimentsPermesos);
	}

}
