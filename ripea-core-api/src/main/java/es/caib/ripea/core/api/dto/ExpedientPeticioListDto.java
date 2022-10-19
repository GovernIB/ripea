/**
 * 
 */
package es.caib.ripea.core.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Informació d'un expedient peticio per llistat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ExpedientPeticioListDto {

	private Long id;
	private String identificador;
	private String clauAcces;
	private Date dataAlta;
	private ExpedientPeticioEstatEnumDto estat;
	private RegistreDto registre;
	private Long metaExpedientId;
	private String metaExpedientNom;
	private String procedimentCodi;
	private ExpedientPeticioAccioEnumDto accio;
	private String notificaDistError;
	private Long expedientId;
	private boolean pendentEnviarDistribucio;
	private ExpedientPeticioEstatPendentDistribucioEnumDto estatPendentEnviarDistribucio;
	private String interessatsResum;

    public String getProcedimentCodiSiaINom() {
    	return procedimentCodi + " - " + metaExpedientNom;
    }
	
	@SuppressWarnings("incomplete-switch")
	public ExpedientPeticioEstatViewEnumDto getEstatView() {
		ExpedientPeticioEstatViewEnumDto estatView = null;
		if (estat != null) {
			switch (estat) {
			case PENDENT:
				estatView = ExpedientPeticioEstatViewEnumDto.PENDENT;
				break;
			case PROCESSAT_PENDENT:
			case PROCESSAT_NOTIFICAT:
				estatView = ExpedientPeticioEstatViewEnumDto.ACCEPTAT;
				break;
			case REBUTJAT:
				estatView = ExpedientPeticioEstatViewEnumDto.REBUTJAT;
				break;
			}
		}
		return estatView;
	}
}
