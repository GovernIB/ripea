/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentPublicacioDto;
import es.caib.ripea.core.api.dto.DocumentPublicacioTipusEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per a gestionar les publicacions de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentPublicacioCommand {

	private Long id;
	@NotNull(groups = {Create.class})
	private Long documentId;
	@NotNull(groups = {Create.class, Update.class})
	private DocumentPublicacioTipusEnumDto tipus;
	@NotNull(groups = {Create.class, Update.class})
	private DocumentEnviamentEstatEnumDto estat;
	@NotEmpty(groups = {Create.class, Update.class})
	@Size(max = 64, groups = {Create.class, Update.class})
	private String assumpte;
	private Date dataPublicacio;
	@Size(groups = {Create.class, Update.class}, max = 256)
	private String observacions;
	@NotNull(groups = {Create.class})
	private Date enviatData;



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
	public DocumentPublicacioTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(DocumentPublicacioTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public DocumentEnviamentEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(DocumentEnviamentEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getAssumpte() {
		return assumpte;
	}
	public void setAssumpte(String assumpte) {
		this.assumpte = assumpte;
	}
	public Date getDataPublicacio() {
		return dataPublicacio;
	}
	public void setDataPublicacio(Date dataPublicacio) {
		this.dataPublicacio = dataPublicacio;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}

	public static DocumentPublicacioCommand asCommand(DocumentPublicacioDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				DocumentPublicacioCommand.class);
	}
	public static DocumentPublicacioDto asDto(DocumentPublicacioCommand command) {
		DocumentPublicacioDto dto = ConversioTipusHelper.convertir(
				command,
				DocumentPublicacioDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	public Date getEnviatData() {
		return enviatData;
	}
	public void setEnviatData(Date enviatData) {
		this.enviatData = enviatData;
	}


	public interface Create {}
	public interface Update {}

}
