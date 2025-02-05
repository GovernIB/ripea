/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;

/**
 * Command per al manteniment d'organs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class OrganGestorCommand {

    private Long id;
    private String codi;
    private String nom;
    private Long pareId;
    private String cif;
    private boolean utilitzarCifPinbal;
    private boolean permetreEnviamentPostal;
    private boolean permetreEnviamentPostalDescendents;


	public static OrganGestorCommand asCommand(OrganGestorDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				OrganGestorCommand.class);
	}
	public static OrganGestorDto asDto(OrganGestorCommand command){
		OrganGestorDto entitat = ConversioTipusHelper.convertir(
				command,
				OrganGestorDto.class);
		return entitat;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setCodi(String codi) {
		this.codi = StringUtils.trim(codi);
	}
	public void setNom(String nom) {
		this.nom = StringUtils.trim(nom);
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public void setCif(String cif) {
		this.cif = StringUtils.trim(cif);
	}
	public void setUtilitzarCifPinbal(boolean utilitzarCifPinbal) {
		this.utilitzarCifPinbal = utilitzarCifPinbal;
	}

}
