/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.command.ContenidorCommand.Create;
import es.caib.ripea.back.command.ContenidorCommand.Update;
import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.back.validation.ExpedientGrup;
import es.caib.ripea.back.validation.ExpedientNomUnique;
import es.caib.ripea.back.validation.ExpedientODocumentNom;
import es.caib.ripea.service.intf.dto.ExpedientDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;

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
@Getter @Setter
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
	@NotNull(groups = {Create.class, Update.class})
	private PrioritatEnumDto prioritat;
	private String prioritatMotiu;

	public void setTancatMotiu(String tancatMotiu) {
		this.tancatMotiu = Utils.trim(tancatMotiu);
	}
	public void setNom(String nom) {
		this.nom = Utils.trim(nom);
	}

	public static ExpedientCommand asCommand(ExpedientDto dto) {
		ExpedientCommand command = ConversioTipusHelper.convertir(dto, ExpedientCommand.class);
		if (dto.getPare() != null) command.setPareId(dto.getPare().getId());
		if (dto.getMetaNode() != null) command.setMetaNodeId(dto.getMetaNode().getId());
		if (dto.getMetaExpedientDomini() != null) command.setMetaNodeDominiId(dto.getMetaExpedientDomini().getId());
		if (dto.getPrioritat() == null) command.setPrioritat(PrioritatEnumDto.B_NORMAL);
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
