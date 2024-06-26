/**
 * 
 */
package es.caib.ripea.core.api.dto;

import es.caib.ripea.core.api.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Informació d'un expedient peticio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ExpedientPeticioDto {

	private Long id;
	private String identificador;
	private String clauAcces;
	private Date dataAlta;
	private ExpedientPeticioEstatEnumDto estat;
	private RegistreDto registre;
	private Long metaExpedientId;
	private String metaExpedientNom;
	private ExpedientPeticioAccioEnumDto accio;
	private String notificaDistError;
	private Long expedientId;
	private Date dataActualitzacio;
	private String usuariActualitzacio;
	private String observacions;
	private Long grupId;

	
	public String getDataActualitzacioStr() {
		return Utils.convertDateToString(dataActualitzacio, "dd/MM/yyyy HH:mm:ss");
	}

	public String getDataAltaStr() {
		return Utils.convertDateToString(dataAlta, "dd/MM/yyyy HH:mm:ss");
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
