package es.caib.ripea.core.api.dto;

import java.io.Serializable;

public class ExpedientExportarZipOptions implements Serializable {

	private static final long serialVersionUID = 3501366497822926268L;
	
	private Long entitatId;
	private String usuariCodi;
	private Integer numExps;
	private boolean carpetes = true;
	private boolean versioImprimible = false;
	private FileNameOption nomFitxer = FileNameOption.ORIGINAL;
	
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public Integer getNumExps() {
		return numExps;
	}
	public void setNumExps(Integer numExps) {
		this.numExps = numExps;
	}
	public boolean isCarpetes() {
		return carpetes;
	}
	public void setCarpetes(boolean carpetes) {
		this.carpetes = carpetes;
	}
	public boolean isVersioImprimible() {
		return versioImprimible;
	}
	public void setVersioImprimible(boolean versioImprimible) {
		this.versioImprimible = versioImprimible;
	}
	public FileNameOption getNomFitxer() {
		return nomFitxer;
	}
	public void setNomFitxer(FileNameOption nomFitxer) {
		this.nomFitxer = nomFitxer;
	}	
}