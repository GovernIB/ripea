/**
 * 
 */
package es.caib.ripea.plugin.notificacio;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació retornada per la consulta de l'estat d'un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RespostaConsultaEstatEnviament {

	private EnviamentEstat estat;
	private Date estatData;
	private String estatDescripcio;
	private String estatOrigen;
	private String receptorNif;
	private String receptorNom;
	private Date certificacioData;
	private String certificacioOrigen;
	private byte[] certificacioContingut;
	private String certificacioHash;
	private String certificacioMetadades;
	private String certificacioCsv;
	private String certificacioTipusMime;
	private boolean error;
	private String errorDescripcio;
	
	public boolean isFinalitzat() {
		if (estat != null && 
				(estat == EnviamentEstat.ABSENT || 
				estat == EnviamentEstat.ADRESA_INCORRECTA || 
				estat == EnviamentEstat.ERROR_ENTREGA || 
				estat == EnviamentEstat.EXPIRADA || 
				estat == EnviamentEstat.EXTRAVIADA || 
				estat == EnviamentEstat.MORT || 
				estat == EnviamentEstat.LLEGIDA || 
				estat == EnviamentEstat.NOTIFICADA || 
				estat == EnviamentEstat.REBUTJADA)) {
			return true;
		} else {
			return false;
		}
	}
}
