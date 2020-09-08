/**
 * 
 */
package es.caib.ripea.war.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.CodiMetaDadaNoRepetit;
import es.caib.ripea.war.validation.CodiMetaDadaNomValid;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment de meta-dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@CodiMetaDadaNomValid
@CodiMetaDadaNoRepetit(
		campId = "id",
		campCodi = "codi",
		campEntitatId = "entitatId",
		campMetaNodeId = "metaNodeId")
public class MetaDadaCommand {

	private Long id;

	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	@NotNull
	private MetaDadaTipusEnumDto tipus;
	@NotNull
	private MultiplicitatEnumDto multiplicitat;
	
	@Size(max=1024)
	private String descripcio;
	private Long entitatId;
	private Long metaNodeId;
	private String domini;
		
	private Long valorSencer;
	private Double valorFlotant;
	private BigDecimal valorImport;
	private Date valorData; 
	private Boolean valorBoolea;
	private String valorString;
	
	public static List<MetaDadaCommand> toMetaDadaCommands(
			List<MetaDadaDto> dtos) {
		List<MetaDadaCommand> commands = new ArrayList<MetaDadaCommand>();
		for (MetaDadaDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							MetaDadaCommand.class));
		}
		return commands;
	}

	public static MetaDadaCommand asCommand(MetaDadaDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				MetaDadaCommand.class);
	}
	public static MetaDadaDto asDto(MetaDadaCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				MetaDadaDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
