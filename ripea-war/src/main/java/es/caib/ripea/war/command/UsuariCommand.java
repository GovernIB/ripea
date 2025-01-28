/**
 * 
 */
package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.ContingutVistaEnumDto;
import es.caib.ripea.core.api.dto.MoureDestiVistaEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class UsuariCommand implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String emailAlternatiu;
	private String idioma;
	private String[] rols;
	private boolean rebreEmailsAgrupats;
	private boolean rebreAvisosNovesAnotacions;
	private boolean rebreEmailsCanviEstatRevisio;
	private Long numElementsPagina;
	private boolean expedientListDataDarrerEnviament;
	private boolean expedientListAgafatPer;
	private boolean expedientListInteressats;
	private boolean expedientListComentaris;
	private boolean expedientListGrup;
	private Long procedimentId;
	private Long entitatPerDefecteId;
	private ContingutVistaEnumDto vistaActual;
	private boolean expedientExpandit;
	private MoureDestiVistaEnumDto vistaMoureActual;
	
	
	public void setCodi(String codi) {
		this.codi = Utils.trim(codi);
	}
	public void setNom(String nom) {
		this.nom = Utils.trim(nom);
	}
	public void setNif(String nif) {
		this.nif = Utils.trim(nif);
	}
	public void setEmail(String email) {
		this.email = Utils.trim(email);
	}
	public void setIdioma(String idioma) {
		this.idioma = Utils.trim(idioma);
	}
	public void setEmailAlternatiu(String emailAlternatiu) {
		this.emailAlternatiu = Utils.trim(emailAlternatiu);
	}

    public static UsuariCommand asCommand(UsuariDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				UsuariCommand.class);
	}
	public static UsuariDto asDto(UsuariCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				UsuariDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private static final long serialVersionUID = -139254994389509932L;

}
