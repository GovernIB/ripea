/**
 * 
 */
package es.caib.ripea.war.command;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.StringUtils;

import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter 
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
	
	
	public void setRebreAvisosNovesAnotacions(boolean rebreAvisosNovesAnotacions) {
		this.rebreAvisosNovesAnotacions = rebreAvisosNovesAnotacions;
	}
	public void setRebreEmailsAgrupats(boolean rebreEmailsAgrupats) {
		this.rebreEmailsAgrupats = rebreEmailsAgrupats;
	}
	public void setCodi(String codi) {
		this.codi = StringUtils.trim(codi);
	}
	public void setNom(String nom) {
		this.nom = StringUtils.trim(nom);
	}
	public void setNif(String nif) {
		this.nif = StringUtils.trim(nif);
	}
	public void setEmail(String email) {
		this.email = StringUtils.trim(email);
	}
	public void setIdioma(String idioma) {
		this.idioma = StringUtils.trim(idioma);
	}
	public void setRols(String[] rols) {
		this.rols = rols;
	}
	public void setEmailAlternatiu(String emailAlternatiu) {
		this.emailAlternatiu = StringUtils.trim(emailAlternatiu);
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

	private static final long serialVersionUID = -139254994389509932L;

}
