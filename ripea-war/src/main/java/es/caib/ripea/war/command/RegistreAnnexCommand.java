/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;

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

	private String observacions;
	private Date ntiFechaCaptura;
	private String ntiOrigen;
	private String tipusMime;


	public String getTitolINom() {
		return StringUtils.isNotEmpty(titolINom) ? titolINom : (titol + " (" + nom + ")");
	}
	public void setTitolINom(String titolINom) {
		this.titolINom = titolINom;
	}
	

}
