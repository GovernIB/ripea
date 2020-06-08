/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

/**
 * Informació d'un Domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DominiDto implements Serializable {
	
	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private Long entitatId;
	private String consulta;
	private String cadena;
	private String contrasenya;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public Long getEntitatId() {
		return entitatId;
	}

	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}

	public String getConsulta() {
		return consulta;
	}

	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}

	public String getCadena() {
		return cadena;
	}

	public void setCadena(String cadena) {
		this.cadena = cadena;
	}

	public String getContrasenya() {
		return contrasenya;
	}

	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}

	private static final long serialVersionUID = 4550970703735934708L;

}
