/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public abstract class NodeDto extends ContingutDto {

	private MetaNodeDto metaNode;
	private List<DadaDto> dades;
	private boolean valid;

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
