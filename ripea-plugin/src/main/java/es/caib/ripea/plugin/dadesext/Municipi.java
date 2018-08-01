/**
 * 
 */
package es.caib.ripea.plugin.dadesext;

import java.io.Serializable;

/**
 * Informació d'una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Municipi implements Serializable, Comparable<Municipi> {

	private String codi;
	private String codiEntitatGeografica;
	private String codiProvincia;
	private String nom;

	public Municipi(
			Long codi,
			String codiEntitatGeografica,
			Long codiProvincia,
			String nom) {
		this.setCodi(codi);
		this.codiEntitatGeografica = codiEntitatGeografica;
		this.setCodiProvincia(codiProvincia);
		this.nom = nom;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public void setCodi(Long lCodi) {
		this.codi = lCodi.toString();
	}

	public String getCodiEntitatGeografica() {
		return codiEntitatGeografica;
	}
	public void setCodiEntitatGeografica(String codiEntitatGeografica) {
		this.codiEntitatGeografica = codiEntitatGeografica;
	}

	public String getCodiProvincia() {
		return codiProvincia;
	}
	public void setCodiProvincia(String codiProvincia) {
		this.codiProvincia = codiProvincia;
	}
	public void setCodiProvincia(Long lCodiProvincia) {
		String sCodiProvincia = lCodiProvincia.toString();
		this.codiProvincia = ("00" + sCodiProvincia).substring(sCodiProvincia.length());
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	@Override
	public int compareTo(Municipi o) {
		return nom.compareToIgnoreCase(o.getNom());
	}
	
	private static final long serialVersionUID = -5602898182576627524L;

}
