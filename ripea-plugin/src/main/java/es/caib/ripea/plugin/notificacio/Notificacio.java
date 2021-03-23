/**
 * 
 */
package es.caib.ripea.plugin.notificacio;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ServeiTipusEnumDto;
import lombok.Data;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Data
public class Notificacio {

	private String emisorDir3Codi;
	private EnviamentTipus enviamentTipus;
	private String concepte;
	private String descripcio;
	private Date enviamentDataProgramada;
	private Integer retard;
	private Date caducitat;
	private String documentArxiuNom;
	private byte[] documentArxiuContingut;
	private String documentArxiuUuid;
	private String procedimentCodi;
	private List<Enviament> enviaments;
	private String usuariCodi;
	private ServeiTipusEnumDto serveiTipusEnum;
	private String numExpedient;
	private String organGestor;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
