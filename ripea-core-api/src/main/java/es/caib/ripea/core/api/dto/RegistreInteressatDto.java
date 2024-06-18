/**
 * 
 */
package es.caib.ripea.core.api.dto;



/**
 * Informació d'un registre interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreInteressatDto {

	private String adresa;
	private String canal;
	private String cp;
	private String documentNumero;
	private String documentTipus;
	private String email;
	private String llinatge1;
	private String llinatge2;
	private String municipiCodi;
	private String nom;
	private String observacions;
	private String paisCodi;
	private String provinciaCodi;
	private String pais;
	private String provincia;
	private String municipi;
	private String raoSocial;
	private String telefon;
	private String tipus;
	private RegistreInteressatDto representant;
	
	
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getMunicipi() {
		return municipi;
	}
	public void setMunicipi(String municipi) {
		this.municipi = municipi;
	}
	public String getAdresa() {
		return adresa;
	}
	public void setAdresa(
			String adresa) {
		this.adresa = adresa;
	}
	public String getCanal() {
		return canal;
	}
	public void setCanal(
			String canal) {
		this.canal = canal;
	}
	public String getCp() {
		return cp;
	}
	public void setCp(
			String cp) {
		this.cp = cp;
	}
	public String getDocumentNumero() {
		return documentNumero;
	}
	public void setDocumentNumero(
			String documentNumero) {
		this.documentNumero = documentNumero;
	}
	public String getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(
			String documentTipus) {
		this.documentTipus = documentTipus;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(
			String email) {
		this.email = email;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(
			String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(
			String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getMunicipiCodi() {
		return municipiCodi;
	}
	public void setMunicipiCodi(
			String municipiCodi) {
		this.municipiCodi = municipiCodi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(
			String nom) {
		this.nom = nom;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(
			String observacions) {
		this.observacions = observacions;
	}
	public String getPaisCodi() {
		return paisCodi;
	}
	public void setPaisCodi(
			String paisCodi) {
		this.paisCodi = paisCodi;
	}
	public String getProvinciaCodi() {
		return provinciaCodi;
	}
	public void setProvinciaCodi(
			String provinciaCodi) {
		this.provinciaCodi = provinciaCodi;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(
			String raoSocial) {
		this.raoSocial = raoSocial;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(
			String telefon) {
		this.telefon = telefon;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(
			String tipus) {
		this.tipus = tipus;
	}
	public RegistreInteressatDto getRepresentant() {
		return representant;
	}
	public void setRepresentant(
			RegistreInteressatDto representant) {
		this.representant = representant;
	}

	public String getDocumentNom() {
		return documentNumero + " - " + getNomSencer();
	}

	public String getNomSencer() {

		if (nom == null) {
			return raoSocial;
		}
		return llinatge1 != null ? llinatge2 != null ? nom + " " + llinatge1 + " " + llinatge2 : nom + " " + llinatge1 : nom;
	}
	

}
