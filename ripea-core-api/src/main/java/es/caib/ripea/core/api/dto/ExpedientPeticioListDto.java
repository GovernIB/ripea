/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;

import es.caib.ripea.core.api.utils.Utils;
import lombok.Getter;
import lombok.Setter;


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
	
	private Long anotacioId;
	private boolean consultaWsError; 
	private String consultaWsErrorDesc;
	private Date consultaWsErrorDate;
	private boolean pendentCanviEstatDistribucio;
	private int reintentsCanviEstatDistribucio;
	private Date dataActualitzacio;
	
	private String grupNom;
	
    public String getConsultaWsErrorDescShort() {
		return Utils.abbreviate(consultaWsErrorDesc, 400);
    	
    }

    public String getProcedimentCodiSiaINom() {
		if (procedimentCodi != null) {
			return procedimentCodi + " - " + metaExpedientNom;
		} else {
			return "";
		}
    	
    }
    
	public String getDataActualitzacioStr() {
		return Utils.surroundWithParenthesis(Utils.convertDateToString(dataActualitzacio, "dd-MM-yyyy HH:mm:ss"));
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
