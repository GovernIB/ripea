/**
 * 
 */
package es.caib.ripea.core.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Informació d'un node.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString(callSuper = true)
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

}
