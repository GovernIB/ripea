/**
 * 
 */
package es.caib.ripea.plugin.usuari;

import java.io.Serializable;

/**
 * Dades d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuari implements Serializable {

	private String codi;
	private String nomSencer;
	private String nom;
	private String llinatges;
	private String nif;
	private String email;

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNomSencer() {
		if (nomSencer != null) {
			return nomSencer;
		} else if (nom != null) {
			if (llinatges != null) {
				return nom + " " + llinatges;
			} else {
				return nom;
			}
		} else {
			return null;
		}
	}
	public void setNomSencer(String nomSencer) {
		this.nomSencer = nomSencer;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatges() {
		return llinatges;
	}
	public void setLlinatges(String llinatges) {
		this.llinatges = llinatges;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DadesUsuari other = (DadesUsuari) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
