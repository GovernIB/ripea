/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.command.DocumentGenericCommand.ConcatenarDigital;
import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.back.validation.NomDocumentNoRepetit;
import es.caib.ripea.service.intf.dto.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.Date;

/**
 * Command per al manteniment de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@NomDocumentNoRepetit(groups = {ConcatenarDigital.class})
public class DocumentGenericCommand extends ContenidorCommand {

	@NotNull(groups = {ConcatenarDigital.class})
	private DocumentTipusEnumDto documentTipus = DocumentTipusEnumDto.DIGITAL;
	@NotNull(groups = {ConcatenarDigital.class})
	private Long metaNodeId;
	@NotNull(groups = {ConcatenarDigital.class})
	private Date data;
	private String fitxerContentType;
	private String fitxerNom;
	private byte[] fitxerContingut;
	private String escanejatTempId;
	@NotNull(groups = {ConcatenarDigital.class})
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	@Size(groups = {ConcatenarDigital.class}, max=48)
	private String ntiIdDocumentoOrigen;
	private NtiOrigenEnumDto ntiOrigen;


	public NtiOrigenEnumDto getNtiOrigen() {
		return ntiOrigen;
	}
	public void setNtiOrigen(
			NtiOrigenEnumDto ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	public DocumentTipusEnumDto getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(DocumentTipusEnumDto documentTipus) {
		this.documentTipus = documentTipus;
	}
	public Long getMetaNodeId() {
		return metaNodeId;
	}
	public void setMetaNodeId(Long metaNodeId) {
		this.metaNodeId = metaNodeId;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getFitxerContentType() {
		return fitxerContentType;
	}
	public void setFitxerContentType(String fitxerContentType) {
		this.fitxerContentType = fitxerContentType != null ? fitxerContentType.trim() : null;
	}
	public String getFitxerNom() {
		return fitxerNom;
	}
	public void setFitxerNom(String fitxerNom) {
		this.fitxerNom = fitxerNom != null ? fitxerNom.trim() : null;
	}
	public byte[] getFitxerContingut() {
		return fitxerContingut;
	}
	public void setFitxerContingut(byte[] fitxerContingut) {
		this.fitxerContingut = fitxerContingut;
	}
	public String getEscanejatTempId() {
		return escanejatTempId;
	}
	public void setEscanejatTempId(String escanejatTempId) {
		this.escanejatTempId = escanejatTempId != null ? escanejatTempId.trim() : null;
	}
	public DocumentNtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public void setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}
	public String getNtiIdDocumentoOrigen() {
		return ntiIdDocumentoOrigen;
	}
	public void setNtiIdDocumentoOrigen(String ntiIdDocumentoOrigen) {
		this.ntiIdDocumentoOrigen = ntiIdDocumentoOrigen != null ? ntiIdDocumentoOrigen.trim() : null;
	}
	public static DocumentGenericCommand asCommand(DocumentDto dto) {
		DocumentGenericCommand command = ConversioTipusHelper.convertir(
				dto,
				DocumentGenericCommand.class);
		if (dto.getPare() != null)
			command.setPareId(dto.getPare().getId());
		if (dto.getMetaNode() != null)
			command.setMetaNodeId(dto.getMetaNode().getId());
		return command;
	}
	public static DocumentDto asDto(DocumentGenericCommand command) throws IOException{
		DocumentDto dto = ConversioTipusHelper.convertir(
				command,
				DocumentDto.class);

		if (command.getFitxerContingut() != null) {
			dto.setFitxerContingut(command.getFitxerContingut());
			dto.setFitxerNom(command.getFitxerNom());
			dto.setFitxerContentType(command.getFitxerContentType());	
			dto.setFitxerTamany(new Long(command.getFitxerContingut().length));
		}

		if (command.getMetaNodeId() != null) {
			MetaDocumentDto metaDocument = new MetaDocumentDto();
			metaDocument.setId(command.getMetaNodeId());
			dto.setMetaNode(metaDocument);
		}
		return dto;
	}

	public interface ConcatenarDigital {}
}
