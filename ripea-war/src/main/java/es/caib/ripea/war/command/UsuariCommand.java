/**
 * 
 */
package es.caib.ripea.war.command;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.StringUtils;

import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariCommand implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String emailAlternatiu;
	private String idioma;
	private String[] rols;
	private boolean rebreEmailsAgrupats;
	
	public boolean isRebreEmailsAgrupats() {
		return rebreEmailsAgrupats;
	}
	public void setRebreEmailsAgrupats(
			boolean rebreEmailsAgrupats) {
		this.rebreEmailsAgrupats = rebreEmailsAgrupats;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi != null ? codi.trim() : null;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif != null ? nif.trim() : null;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email != null ? email.trim() : null;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma != null ? idioma.trim() : null;
	}
	public String[] getRols() {
		return rols;
	}
	public void setRols(String[] rols) {
		this.rols = rols;
	}
	public String getEmailAlternatiu() {
		return emailAlternatiu;
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
