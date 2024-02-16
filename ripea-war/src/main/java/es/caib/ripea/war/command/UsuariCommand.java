/**
 * 
 */
package es.caib.ripea.war.command;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.utils.Utils;
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
	private Long numElementsPagina;
	private boolean expedientListDataDarrerEnviament;
	private boolean expedientListAgafatPer;
	private boolean expedientListInteressats;
	private boolean expedientListComentaris;
	private boolean expedientListGrup;
	private Long procedimentId;
	
	
	public void setRebreAvisosNovesAnotacions(boolean rebreAvisosNovesAnotacions) {
		this.rebreAvisosNovesAnotacions = rebreAvisosNovesAnotacions;
	}
	public void setRebreEmailsAgrupats(boolean rebreEmailsAgrupats) {
		this.rebreEmailsAgrupats = rebreEmailsAgrupats;
	}
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
	public void setRols(String[] rols) {
		this.rols = rols;
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

	public void setNumElementsPagina(Long numElementsPagina) {
		this.numElementsPagina = numElementsPagina;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public void setExpedientListDataDarrerEnviament(
			boolean expedientListDataDarrerEnviament) {
		this.expedientListDataDarrerEnviament = expedientListDataDarrerEnviament;
	}
	public void setExpedientListAgafatPer(
			boolean expedientListAgafatPer) {
		this.expedientListAgafatPer = expedientListAgafatPer;
	}
	public void setExpedientListInteressats(
			boolean expedientListInteressats) {
		this.expedientListInteressats = expedientListInteressats;
	}
	public void setExpedientListComentaris(
			boolean expedientListComentaris) {
		this.expedientListComentaris = expedientListComentaris;
	}
	public void setExpedientListGrup(
			boolean expedientListGrup) {
		this.expedientListGrup = expedientListGrup;
	}

	public void setProcedimentId(
			Long procedimentId) {
		this.procedimentId = procedimentId;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
