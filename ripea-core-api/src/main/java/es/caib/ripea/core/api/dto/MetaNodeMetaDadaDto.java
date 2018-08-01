/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una meta-dada d'un meta-node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaNodeMetaDadaDto implements Serializable {

	private Long id;
	private MetaDadaDto metaDada;
	private MultiplicitatEnumDto multiplicitat;
	boolean readOnly;
	private int ordre;
	private boolean global;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public MetaDadaDto getMetaDada() {
		return metaDada;
	}
	public void setMetaDada(MetaDadaDto metaDada) {
		this.metaDada = metaDada;
	}
	public MultiplicitatEnumDto getMultiplicitat() {
		return multiplicitat;
	}
	public void setMultiplicitat(MultiplicitatEnumDto multiplicitat) {
		this.multiplicitat = multiplicitat;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}
	public boolean isGlobal() {
		return global;
	}
	public void setGlobal(boolean global) {
		this.global = global;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
