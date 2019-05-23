/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.ArrayList;
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

/**
 * Command per al manteniment de meta-dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	private String valor;
	@Size(max=1024)
	private String descripcio;
	private Long entitatId;
	private Long metaNodeId;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public MetaDadaTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(MetaDadaTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public MultiplicitatEnumDto getMultiplicitat() {
		return multiplicitat;
	}
	public void setMultiplicitat(MultiplicitatEnumDto multiplicitat) {
		this.multiplicitat = multiplicitat;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public Long getMetaNodeId() {
		return metaNodeId;
	}
	public void setMetaNodeId(Long metaNodeId) {
		this.metaNodeId = metaNodeId;
	}

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
