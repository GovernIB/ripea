/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per a gestionar les publicacions de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentPortafirmesCommand {

	private Long id;
	@NotNull
	private Long documentId;
	@NotEmpty
	@Size(max = 64)
	private String assumpte;
	@NotNull
	private PortafirmesPrioritatEnumDto prioritat;
	@NotNull
	private Date dataCaducitat;
	@Size(max = 256)
	private String observacions;
	private List<Long> annexos;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
	public String getAssumpte() {
		return assumpte;
	}
	public void setAssumpte(String assumpte) {
		this.assumpte = assumpte.trim();
	}
	public PortafirmesPrioritatEnumDto getPrioritat() {
		return prioritat;
	}
	public void setPrioritat(PortafirmesPrioritatEnumDto prioritat) {
		this.prioritat = prioritat;
	}
	public Date getDataCaducitat() {
		return dataCaducitat;
	}
	public void setDataCaducitat(Date dataCaducitat) {
		this.dataCaducitat = dataCaducitat;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions.trim();
	}
	public List<Long> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<Long> annexos) {
		this.annexos = annexos;
	}

	public static DocumentPortafirmesCommand asCommand(DocumentPortafirmesDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				DocumentPortafirmesCommand.class);
	}
	public static DocumentPortafirmesDto asDto(DocumentPortafirmesCommand command) {
		DocumentPortafirmesDto dto = ConversioTipusHelper.convertir(
				command,
				DocumentPortafirmesDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
