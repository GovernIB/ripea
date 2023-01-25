/**
 * 
 */
package es.caib.ripea.core.auxiliary;

import java.io.Serializable;
import java.util.List;

import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import lombok.Setter;



@Setter
public class ExpedientFiltreCalculat implements Serializable {


	private MetaExpedientEntity metaExpedientFiltre;
	private OrganGestorEntity organGestorFiltre;
	private UsuariEntity agafatPer;
	private ExpedientEstatEnumDto chosenEstatEnum;
	private ExpedientEstatEntity chosenEstat;
	private List<ExpedientEntity> expedientsToBeExluded;
	
	private List<Long> idsMetaExpedientsDomini;
	
	
	public OrganGestorEntity getOrganGestorFiltre() {
		return organGestorFiltre;
	}

	public UsuariEntity getAgafatPer() {
		return agafatPer;
	}

	public ExpedientEstatEnumDto getChosenEstatEnum() {
		return chosenEstatEnum;
	}

	public ExpedientEstatEntity getChosenEstat() {
		return chosenEstat;
	}

	public List<ExpedientEntity> getExpedientsToBeExluded() {
		return expedientsToBeExluded == null || expedientsToBeExluded.isEmpty() ? null : expedientsToBeExluded;
	}


	public MetaExpedientEntity getMetaExpedientFiltre() {
		return metaExpedientFiltre;
	}

	public List<Long> getIdsMetaExpedientsDomini() {
		return Utils.geValueOrNull(idsMetaExpedientsDomini);
	}


	private static final long serialVersionUID = 1L;
}
