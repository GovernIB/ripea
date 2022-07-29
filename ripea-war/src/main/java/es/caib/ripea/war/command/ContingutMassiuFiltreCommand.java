/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ContingutMassiuFiltreCommand {

	private ContingutTipusEnumDto tipusElement;
	private Long metaExpedientId;
	private Long expedientId;
	private String expedientNom;
	private Long metaDocumentId;
	private String metaDada;
	private String nom;
	private Date dataInici;
	private Date dataFi;
	private String identificador;
	
	private boolean bloquejarTipusElement;
	private boolean bloquejarMetaDada;
	private boolean bloquejarMetaExpedient;
	private boolean bloquejarMetaDocument;
	
	public static ContingutMassiuFiltreCommand asCommand(ContingutMassiuFiltreDto dto) {
		ContingutMassiuFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				ContingutMassiuFiltreCommand.class);
		return command;
	}
	public static ContingutMassiuFiltreDto asDto(ContingutMassiuFiltreCommand command) {
		ContingutMassiuFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				ContingutMassiuFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public ContingutTipusEnumDto getTipusElement() {
		return tipusElement;
	}
	public void setTipusElement(ContingutTipusEnumDto tipusElement) {
		this.tipusElement = tipusElement;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public Long getMetaExpedientId() {
		return metaExpedientId;
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	public Long getMetaDocumentId() {
		return metaDocumentId;
	}
	public void setMetaDocumentId(Long metaDocumentId) {
		this.metaDocumentId = metaDocumentId;
	}
	public String getMetaDada() {
		return metaDada;
	}
	public void setMetaDada(String metaDada) {
		this.metaDada = metaDada != null ? metaDada.trim() : null;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public boolean isBloquejarTipusElement() {
		return bloquejarTipusElement;
	}
	public void setBloquejarTipusElement(boolean bloquejarTipusElement) {
		this.bloquejarTipusElement = bloquejarTipusElement;
	}
	public boolean isBloquejarMetaDada() {
		return bloquejarMetaDada;
	}
	public void setBloquejarMetaDada(boolean bloquejarMetaDada) {
		this.bloquejarMetaDada = bloquejarMetaDada;
	}
	public boolean isBloquejarMetaExpedient() {
		return bloquejarMetaExpedient;
	}
	public void setBloquejarMetaExpedient(boolean bloquejarMetaExpedient) {
		this.bloquejarMetaExpedient = bloquejarMetaExpedient;
	}
	public boolean isBloquejarMetaDocument() {
		return bloquejarMetaDocument;
	}
	public void setBloquejarMetaDocument(boolean bloquejarMetaDocument) {
		this.bloquejarMetaDocument = bloquejarMetaDocument;
	}
	public void setExpedientNom(String expedientNom) {
		this.expedientNom = expedientNom != null ? expedientNom.trim() : null;
	}
	public String getExpedientNom() {
		return expedientNom;
	}
}
