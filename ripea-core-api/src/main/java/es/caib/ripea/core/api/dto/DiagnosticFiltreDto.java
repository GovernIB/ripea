package es.caib.ripea.core.api.dto;

import java.io.Serializable;

public class DiagnosticFiltreDto implements Serializable {
	
	private static final long serialVersionUID = 2602895818496714054L;
	
	private String entitatCodi;
	private String organCodi;
	
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(
			String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getOrganCodi() {
		return organCodi;
	}
	public void setOrganCodi(
			String organCodi) {
		this.organCodi = organCodi;
	}
	
	
}