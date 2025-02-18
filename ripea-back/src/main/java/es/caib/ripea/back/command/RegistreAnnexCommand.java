/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Informació d'un registre annex.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RegistreAnnexCommand {

	private Long id;

	private String titol;
	private String uuid;
	
	private Long metaDocumentId;
	private String nom;
	private String titolINom;

	private String tipusMime;
	
	
	private Date ntiFechaCaptura;
	private String ntiOrigen;
	private String ntiTipoDocumental;
	private String sicresTipoDocumento;
	private String ntiEstadoElaboracion;
	private String observacions;
	private ArxiuEstatEnumDto annexArxiuEstat;


	public String getTitolINom() {
		return StringUtils.isNotEmpty(titolINom) ? titolINom : (titol + " (" + nom + ")");
	}
	public void setTitolINom(String titolINom) {
		this.titolINom = titolINom;
	}
	

}
