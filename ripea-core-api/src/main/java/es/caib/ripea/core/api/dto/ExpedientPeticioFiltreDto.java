/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació del filtre d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ExpedientPeticioFiltreDto implements Serializable {

	private String procediment;
	private String numero;
	private String extracte;
	private String destinacioCodi;
	private ExpedientPeticioAccioEnumDto accioEnum;
	private Date dataInicial;
	private Date dataFinal;
	private ExpedientPeticioEstatViewEnumDto estat;
	private Long metaExpedientId;
	private String procedimentCodi;
	private String interessat;
	private ExpedientPeticioEstatEnumDto estatAll;
	private ExpedientPeticioEstatPendentDistribucioEnumDto estatPendentEnviarDistribucio;
	private boolean nomesPendentEnviarDistribucio;
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	private static final long serialVersionUID = -139254994389509932L;

}
