/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.ServeiTipusEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per a gestionar les notificacions de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentNotificacioCommand {

	private Long id;
	@NotNull(groups = {Create.class})
	private Long documentId;
	@NotNull
	private DocumentNotificacioTipusEnumDto tipus;
	private DocumentEnviamentEstatEnumDto estat;
	@NotEmpty(groups = {Create.class, Update.class})
	@Size(max = 64, groups = {Create.class, Update.class})
	private String assumpte;
	@Size(groups = {Create.class, Update.class}, max = 256)
	private String observacions;
	private Date dataProgramada;
	private Integer retard;
	private Date dataCaducitat;
	@NotEmpty(groups = {Create.class, Update.class})
	private List<Long> interessatsIds;
	
	@NotNull
	private ServeiTipusEnumDto serveiTipusEnum;
	
	private Boolean entregaPostal;
	


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
	public DocumentNotificacioTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(DocumentNotificacioTipusEnumDto tipus) {
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
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public Date getDataProgramada() {
		return dataProgramada;
	}
	public void setDataProgramada(Date dataProgramada) {
		this.dataProgramada = dataProgramada;
	}
	public Integer getRetard() {
		return retard;
	}
	public void setRetard(Integer retard) {
		this.retard = retard;
	}
	public Date getDataCaducitat() {
		return dataCaducitat;
	}
	public void setDataCaducitat(Date dataCaducitat) {
		this.dataCaducitat = dataCaducitat;
	}

	public List<Long> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<Long> annexos) {
		this.annexos = annexos;
	}

	public static DocumentNotificacioCommand asCommand(DocumentNotificacioDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				DocumentNotificacioCommand.class);
	}
	public static DocumentNotificacioDto asDto(DocumentNotificacioCommand command) {
		DocumentNotificacioDto dto = ConversioTipusHelper.convertir(
				command,
				DocumentNotificacioDto.class);
		if (command.getAnnexos() != null) {
			List<DocumentDto> annexos = new ArrayList<DocumentDto>();
			for (Long annexId: command.getAnnexos()) {
				DocumentDto annex = new DocumentDto();
				annex.setId(annexId);
				annexos.add(annex);
			}
			dto.setAnnexos(annexos);
		}
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public ServeiTipusEnumDto getServeiTipusEnum() {
		return serveiTipusEnum;
	}
	public void setServeiTipusEnum(ServeiTipusEnumDto serveiTipusEnum) {
		this.serveiTipusEnum = serveiTipusEnum;
	}

	public List<Long> getInteressatsIds() {
		return interessatsIds;
	}
	public void setInteressatsIds(List<Long> interessatsIds) {
		this.interessatsIds = interessatsIds;
	}


	public Boolean getEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(Boolean entregaPostal) {
		this.entregaPostal = entregaPostal;
	}


	public interface Electronica {}
	public interface Create {}
	public interface Update {}

}
