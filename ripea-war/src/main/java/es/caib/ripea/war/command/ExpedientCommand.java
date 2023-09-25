/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.command.ContenidorCommand.Create;
import es.caib.ripea.war.command.ContenidorCommand.Update;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.ExpedientGrup;
import es.caib.ripea.war.validation.ExpedientNomUnique;
import es.caib.ripea.war.validation.ExpedientODocumentNom;
import lombok.Getter;

/**
 * Command per al manteniment d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientGrup(
		groups = {Create.class, Update.class})
@ExpedientNomUnique(
		groups = {Create.class, Update.class},
		campId = "id",
		campMetaExpedientId = "metaNodeId",
		campNom = "nom",
		campEntitatId = "entitatId",
		campPareId = "pareId",
		campOrganGestorId = "organGestorId")
@ExpedientODocumentNom(groups = {Create.class, Update.class})
@Getter
public class ExpedientCommand extends ContenidorCommand {

	@NotNull(groups = {Create.class})
	protected Long metaNodeId;
	private String tancatMotiu;
	private int any;
	private Long sequencia;
	private Long expedientEstatId;
	protected Long metaNodeDominiId;
	@NotNull(groups = {Create.class})
	protected Long organGestorId;
	private Long grupId;

	private boolean gestioAmbGrupsActiva;

	public void setMetaNodeId(Long metaNodeId) {
		this.metaNodeId = metaNodeId;
	}
	public void setTancatMotiu(String tancatMotiu) {
		this.tancatMotiu = Utils.trim(tancatMotiu);
	}
	public void setAny(int any) {
		this.any = any;
	}
	public void setSequencia(Long sequencia) {
		this.sequencia = sequencia;
	}
	public Long getExpedientEstatId() {
		return expedientEstatId;
	}
	public void setExpedientEstatId(Long expedientEstatId) {
		this.expedientEstatId = expedientEstatId;
	}
	public void setMetaNodeDominiId(Long metaNodeDominiId) {
		this.metaNodeDominiId = metaNodeDominiId;
	}
	public void setOrganGestorId(Long organGestorId) {
		this.organGestorId = organGestorId;
	}
	public void setGrupId(Long grupId) {
		this.grupId = grupId;
	}
	public void setGestioAmbGrupsActiva(boolean gestioAmbGrupsActiva) {
		this.gestioAmbGrupsActiva = gestioAmbGrupsActiva;
	}
	public void setNom(String nom) {
		this.nom = Utils.trim(nom);
	}

	public static ExpedientCommand asCommand(ExpedientDto dto) {
		ExpedientCommand command = ConversioTipusHelper.convertir(
				dto,
				ExpedientCommand.class);
		if (dto.getPare() != null)
			command.setPareId(dto.getPare().getId());
		if (dto.getMetaNode() != null)
			command.setMetaNodeId(dto.getMetaNode().getId());
		if (dto.getMetaExpedientDomini() != null)
			command.setMetaNodeDominiId(dto.getMetaExpedientDomini().getId());
		return command;
	}
	public static ExpedientDto asDto(ExpedientCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ExpedientDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}



}
