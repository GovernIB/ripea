/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Informació de log d'una accio realitzada damunt un node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class ContingutLogDto extends AuditoriaDto {

	private Long id;
	private LogTipusEnumDto tipus;
	private String objecteId;
	private LogObjecteTipusEnumDto objecteTipus;
	private LogTipusEnumDto objecteLogTipus;
	private String param1;
	private String param2;

	public boolean isSecundari() {
		return tipus.equals(LogTipusEnumDto.MODIFICACIO) && objecteId != null;
	}

}
