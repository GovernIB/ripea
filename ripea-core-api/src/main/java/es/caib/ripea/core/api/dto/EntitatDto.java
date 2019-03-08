/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatDto extends AuditoriaDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private String cif;
	private String unitatArrel;
	private boolean activa;

	private List<PermisDto> permisos;
	private boolean usuariActualRead;
	private boolean usuariActualAdministration;
	
	private byte[] logoImgBytes;
	private String capsaleraColorFons;
	private String capsaleraColorLletra;
	
	
	public String getCapsaleraColorLletra() {
		return capsaleraColorLletra;
	}
	public void setCapsaleraColorLletra(String capsaleraColorLletra) {
		this.capsaleraColorLletra = capsaleraColorLletra;
	}
	public String getCapsaleraColorFons() {
		return capsaleraColorFons;
	}
	public void setCapsaleraColorFons(String capsaleraColorFons) {
		this.capsaleraColorFons = capsaleraColorFons;
	}
	public byte[] getLogoImgBytes() {
		return logoImgBytes;
	}
	public void setLogoImgBytes(byte[] logoImgBytes) {
		this.logoImgBytes = logoImgBytes;
	}
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
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}
	public String getUnitatArrel() {
		return unitatArrel;
	}
	public void setUnitatArrel(String unitatArrel) {
		this.unitatArrel = unitatArrel;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	public List<PermisDto> getPermisos() {
		return permisos;
	}
	public void setPermisos(List<PermisDto> permisos) {
		this.permisos = permisos;
	}
	public boolean isUsuariActualRead() {
		return usuariActualRead;
	}
	public void setUsuariActualRead(boolean usuariActualRead) {
		this.usuariActualRead = usuariActualRead;
	}
	public boolean isUsuariActualAdministration() {
		return usuariActualAdministration;
	}
	public void setUsuariActualAdministration(boolean usuariActualAdministration) {
		this.usuariActualAdministration = usuariActualAdministration;
	}

	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
