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

/**
 * Command per al manteniment de meta-dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
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
	
	private boolean noAplica;
	
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
	public void setId(Long id) {
		this.id = id;
	}
	public void setCodi(String codi) {
		this.codi = codi != null ? codi.trim() : null;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public void setTipus(MetaDadaTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public void setMultiplicitat(MultiplicitatEnumDto multiplicitat) {
		this.multiplicitat = multiplicitat;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio != null ? descripcio.trim() : null;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public void setMetaNodeId(Long metaNodeId) {
		this.metaNodeId = metaNodeId;
	}
	public void setDomini(String domini) {
		this.domini = domini != null ? domini.trim() : null;
	}
	public void setValorSencer(Long valorSencer) {
		this.valorSencer = valorSencer;
	}
	public void setValorFlotant(Double valorFlotant) {
		this.valorFlotant = valorFlotant;
	}
	public void setValorImport(BigDecimal valorImport) {
		this.valorImport = valorImport;
	}
	public void setValorData(Date valorData) {
		this.valorData = valorData;
	}
	public void setValorBoolea(Boolean valorBoolea) {
		this.valorBoolea = valorBoolea;
	}
	public void setValorString(String valorString) {
		this.valorString = valorString != null ? valorString.trim() : null;
	}
	public void setNoAplica(boolean noAplica) {
		this.noAplica = noAplica;
	}
}
