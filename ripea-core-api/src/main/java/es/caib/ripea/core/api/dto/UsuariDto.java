/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariDto implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String emailAlternatiu;
	private String idioma;
	private String[] rols;
	private boolean rebreEmailsAgrupats;
	private String rolActual;
	
	public boolean isRebreEmailsAgrupats() {
		return rebreEmailsAgrupats;
	}
	public void setRebreEmailsAgrupats(
			boolean rebreEmailsAgrupats) {
		this.rebreEmailsAgrupats = rebreEmailsAgrupats;
	}
	public String getCodiAndNom() {
		return nom + " (" + codi + ")";
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
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public String[] getRols() {
		return rols;
	}
	public void setRols(String[] rols) {
		this.rols = rols;
	}
	public String getRolActual() {
		return rolActual;
	}
	public void setRolActual(String rolActual) {
		this.rolActual = rolActual;
	}
	public String getEmailAlternatiu() {
		return emailAlternatiu;
	}
	public void setEmailAlternatiu(String emailAlternatiu) {
		this.emailAlternatiu = emailAlternatiu;
	}


	private static final long serialVersionUID = -139254994389509932L;

}
