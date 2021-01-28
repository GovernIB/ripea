/**
 * 
 */
package es.caib.ripea.war.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.core.api.dto.ArbreJsonDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.CodiMetaExpedientNoRepetit;
import es.caib.ripea.war.validation.OrganGestorMetaExpedientNotNull;
import lombok.Data;

/**
 * Command per al manteniment de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@CodiMetaExpedientNoRepetit(campId = "id", campCodi = "codi", campEntitatId = "entitatId")
@OrganGestorMetaExpedientNotNull
public class MetaExpedientCommand {

	private Long id;

	@NotEmpty
	@Size(max = 64)
	private String codi;
	@NotEmpty
	@Size(max = 256)
	private String nom;
	@Size(max = 1024)
	private String descripcio;
	@NotEmpty
	@Size(max = 30)
	private String classificacioSia;
	@NotEmpty
	@Size(max = 30)
	private String serieDocumental;
	@Size(max = 100)
	private String expressioNumero;

	private Long organGestorId;

	private boolean notificacioActiva;

	private boolean permetMetadocsGenerals;

	private Long pareId;
	private Long entitatId;

	private boolean isRolAdminOrgan;
	
    private boolean gestioAmbGrupsActiva;

    private String estructuraCarpetesJson;
    
	public MetaExpedientCommand(boolean isRolOrgan) {
		this.isRolAdminOrgan = isRolOrgan;
	}

	public MetaExpedientCommand() {
		this.isRolAdminOrgan = true;
	}

	public static List<MetaExpedientCommand> toEntitatCommands(List<MetaExpedientDto> dtos) {
		List<MetaExpedientCommand> commands = new ArrayList<MetaExpedientCommand>();
		for (MetaExpedientDto dto : dtos) {
			commands.add(ConversioTipusHelper.convertir(dto, MetaExpedientCommand.class));
		}
		return commands;
	}

	public static MetaExpedientCommand asCommand(MetaExpedientDto dto) {
		MetaExpedientCommand command = ConversioTipusHelper.convertir(dto, MetaExpedientCommand.class);
		command.setOrganGestorId(dto.getOrganGestor() != null ? dto.getOrganGestor().getId() : null);
		return command;
	}

	public MetaExpedientDto asDto() throws JsonMappingException {
		MetaExpedientDto dto = ConversioTipusHelper.convertir(this, MetaExpedientDto.class);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			List<ArbreJsonDto> listCarpetes = objectMapper.readValue(this.getEstructuraCarpetesJson(), new TypeReference<List<ArbreJsonDto>>(){});
			dto.setEstructuraCarpetes(listCarpetes);
		} catch (IOException ex) {
			throw new JsonMappingException("Hi ha hagut un error en la conversió del json de jstree a List<ArbreJsonDto>", ex);
		}
		
		if (this.getOrganGestorId() != null) {
			OrganGestorDto organ = new OrganGestorDto();
			organ.setId(this.getOrganGestorId());
			dto.setOrganGestor(organ);
		}
		return dto;
	}

}
