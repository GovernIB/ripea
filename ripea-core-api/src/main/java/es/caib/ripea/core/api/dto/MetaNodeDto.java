/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un MetaNode.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaNodeDto implements Serializable {

	protected Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private boolean actiu;

	private List<PermisDto> permisos;
	private boolean usuariActualCreate;
	private boolean usuariActualRead;
	private boolean usuariActualWrite;
	private boolean usuariActualDelete;



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
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}
	public List<PermisDto> getPermisos() {
		return permisos;
	}
	public void setPermisos(List<PermisDto> permisos) {
		this.permisos = permisos;
	}
	public boolean isUsuariActualCreate() {
		return usuariActualCreate;
	}
	public void setUsuariActualCreate(boolean usuariActualCreate) {
		this.usuariActualCreate = usuariActualCreate;
	}
	public boolean isUsuariActualRead() {
		return usuariActualRead;
	}
	public void setUsuariActualRead(boolean usuariActualRead) {
		this.usuariActualRead = usuariActualRead;
	}
	public boolean isUsuariActualWrite() {
		return usuariActualWrite;
	}
	public void setUsuariActualWrite(boolean usuariActualWrite) {
		this.usuariActualWrite = usuariActualWrite;
	}
	public boolean isUsuariActualDelete() {
		return usuariActualDelete;
	}
	public void setUsuariActualDelete(boolean usuariActualDelete) {
		this.usuariActualDelete = usuariActualDelete;
	}

	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}
	
	public String getIdentificador() {
		return nom + " (" + codi + ")"; 
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
