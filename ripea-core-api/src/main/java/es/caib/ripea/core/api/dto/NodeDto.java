/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class NodeDto extends ContingutDto {

	private MetaNodeDto metaNode;
	private List<DadaDto> dades;
	private boolean valid;



	public MetaNodeDto getMetaNode() {
		return metaNode;
	}
	public void setMetaNode(MetaNodeDto metaNode) {
		this.metaNode = metaNode;
	}
	public List<DadaDto> getDades() {
		return dades;
	}
	public void setDades(List<DadaDto> dades) {
		this.dades = dades;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public int getDadesCount() {
		if (dades == null)
			return 0;
		else
			return dades.size(); 
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
