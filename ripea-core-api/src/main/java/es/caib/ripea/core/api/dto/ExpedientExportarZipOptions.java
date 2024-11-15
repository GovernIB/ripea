package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.Set;

public class ExpedientExportarZipOptions implements Serializable {

	private static final long serialVersionUID = 3501366497822926268L;
	
	private Long entitatId;
	private String usuariCodi;
	private Set<Long> expedientsIds;
	private boolean carpetes = true;
	private boolean versioImprimible = false;
	private PrincipalTipusEnumDto nomFitxer = PrincipalTipusEnumDto.USUARI;
	
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(
			Long entitatId) {
		this.entitatId = entitatId;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(
			String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public Set<Long> getExpedientsIds() {
		return expedientsIds;
	}
	public void setExpedientsIds(Set<Long> expedientsIds) {
		this.expedientsIds = expedientsIds;
	}
	public boolean isCarpetes() {
		return carpetes;
	}
	public void setCarpetes(
			boolean carpetes) {
		this.carpetes = carpetes;
	}
	public boolean isVersioImprimible() {
		return versioImprimible;
	}
	public void setVersioImprimible(
			boolean versioImprimible) {
		this.versioImprimible = versioImprimible;
	}
	public PrincipalTipusEnumDto getNomFitxer() {
		return nomFitxer;
	}
	public void setNomFitxer(
			PrincipalTipusEnumDto nomFitxer) {
		this.nomFitxer = nomFitxer;
	}	
}