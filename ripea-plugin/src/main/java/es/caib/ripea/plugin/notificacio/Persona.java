package es.caib.ripea.plugin.notificacio;

import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una persona per a un enviament a NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class Persona {
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String raoSocial;
	private String nif;
	private String codiDir3;
	private String telefon;
	private String email;
	private InteressatTipusEnumDto interessatTipus;
	private InteressatDocumentTipusEnumDto documentTipus;
	private boolean incapacitat;
}