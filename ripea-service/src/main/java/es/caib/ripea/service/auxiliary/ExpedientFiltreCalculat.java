package es.caib.ripea.service.auxiliary;

import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
public class ExpedientFiltreCalculat implements Serializable {

	private static final long serialVersionUID = 926780773567896387L;
	
	private MetaExpedientEntity metaExpedientFiltre;
	private OrganGestorEntity organGestorFiltre;
	private UsuariEntity agafatPer;
	private UsuariEntity agafatPerActual;
	private UsuariEntity seguitPer;
	private ExpedientEstatEnumDto chosenEstatEnum;
	private ExpedientEstatEntity chosenEstat;
	private List<ExpedientEntity> expedientsToBeExluded;
	private List<Long> idsMetaExpedientsDomini;
	private GrupEntity grup;
	
	public OrganGestorEntity getOrganGestorFiltre() {
		return organGestorFiltre;
	}

	public UsuariEntity getAgafatPer() {
		return agafatPer;
	}

	public UsuariEntity getAgafatPerActual() {
		return agafatPerActual;
	}

	// Cert quan no s'ha d'aplicar cap filtre per usuari que ha agafat l'expedient
	// (ni l'usuari seleccionat al desplegable "Agafat per" ni el pulsador "Agafats per mi").
	public boolean isAgafatPerEmpty() {
		return agafatPer == null && agafatPerActual == null;
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
		return Utils.getNullIfEmpty(idsMetaExpedientsDomini);
	}

	public GrupEntity getGrup() {
		return grup;
	}

	public UsuariEntity getSeguitPer() {
		return seguitPer;
	}
}