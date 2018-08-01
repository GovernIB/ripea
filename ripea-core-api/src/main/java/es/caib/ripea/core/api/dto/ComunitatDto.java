package es.caib.ripea.core.api.dto;

public class ComunitatDto {
	private String codi;
	private String nom;
	
	public ComunitatDto() {

	}
	
	public ComunitatDto(
			String codi,
			String nom) {
		this.codi = codi;
		this.nom = nom;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
}
