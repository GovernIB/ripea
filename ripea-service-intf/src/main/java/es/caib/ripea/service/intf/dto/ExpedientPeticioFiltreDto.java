/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

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
	private boolean nomesAmbErrorsConsulta;
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	private static final long serialVersionUID = -139254994389509932L;

}
