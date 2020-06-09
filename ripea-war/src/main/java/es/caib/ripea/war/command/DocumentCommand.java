/**
 * 
 */
package es.caib.ripea.war.command;

import java.io.IOException;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusFirmaEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.war.command.DocumentCommand.CreateDigital;
import es.caib.ripea.war.command.DocumentCommand.CreateFirmaSeparada;
import es.caib.ripea.war.command.DocumentCommand.CreateFisic;
import es.caib.ripea.war.command.DocumentCommand.UpdateDigital;
import es.caib.ripea.war.command.DocumentCommand.UpdateFirmaSeparada;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.ArxiuNoBuit;
import es.caib.ripea.war.validation.DocumentDigitalExistent;
import es.caib.ripea.war.validation.NomDocumentNoRepetit;
import es.caib.ripea.war.validation.ValidIfSeparada;

/**
 * Command per al manteniment de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@ArxiuNoBuit(groups = {CreateDigital.class, CreateFirmaSeparada.class})
@NomDocumentNoRepetit(groups = {CreateDigital.class, CreateFisic.class})
@DocumentDigitalExistent(groups = {CreateDigital.class, UpdateDigital.class})
@ValidIfSeparada(groups = {CreateFirmaSeparada.class, UpdateFirmaSeparada.class})
public class DocumentCommand extends ContenidorCommand {

	@NotNull(groups = {CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class})
	private DocumentTipusEnumDto documentTipus = DocumentTipusEnumDto.DIGITAL;
	@NotEmpty(groups = {CreateFisic.class, UpdateFisic.class})
	@Size(groups = {CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class}, max=255)
	private String ubicacio;
	@NotNull(groups = {CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class})
	private Long metaNodeId;
	@NotNull(groups = {CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class})
	private Date data;
	private MultipartFile arxiu;
	private DocumentFisicOrigenEnum origen;
	private boolean ambFirma;
	private MultipartFile firma;
	@NotNull(groups = {CreateFirmaSeparada.class, UpdateFirmaSeparada.class})
	private DocumentTipusFirmaEnumDto tipusFirma = DocumentTipusFirmaEnumDto.ADJUNT;
	private String escanejatTempId;
	/*@NotNull(groups = {CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class})
	private Date dataCaptura;
	@NotEmpty(groups = {CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class})
	@Size(groups = {CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class}, max=9)
	private String ntiOrgano;
	@NotNull(groups = {CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class})
	private NtiOrigenEnumDto ntiOrigen;*/
	@NotNull(groups = {CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class})
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	/*@NotNull(groups = {CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class})
	private DocumentNtiTipoDocumentalEnumDto ntiTipoDocumental;*/
	@Size(groups = {CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class}, max=48)
	private String ntiIdDocumentoOrigen;
	private String fitxerContentType;
	private String fitxerNom;
	private byte[] fitxerContingut;
	private String descripcio;

	public DocumentTipusEnumDto getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(DocumentTipusEnumDto documentTipus) {
		this.documentTipus = documentTipus;
	}
	public String getUbicacio() {
		return ubicacio;
	}
	public void setUbicacio(String ubicacio) {
		this.ubicacio = ubicacio;
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
	public MultipartFile getArxiu() {
		return arxiu;
	}
	public void setArxiu(MultipartFile arxiu) {
		this.arxiu = arxiu;
	}
	public DocumentFisicOrigenEnum getOrigen() {
		return origen;
	}
	public void setOrigen(DocumentFisicOrigenEnum origen) {
		this.origen = origen;
	}
	public boolean isAmbFirma() {
		return ambFirma;
	}
	public void setAmbFirma(boolean ambFirma) {
		this.ambFirma = ambFirma;
	}
	public MultipartFile getFirma() {
		return firma;
	}
	public void setFirma(MultipartFile firma) {
		this.firma = firma;
	}
	public String getEscanejatTempId() {
		return escanejatTempId;
	}
	public void setEscanejatTempId(String escanejatTempId) {
		this.escanejatTempId = escanejatTempId;
	}
	public String getFitxerContentType() {
		return fitxerContentType;
	}
	public void setFitxerContentType(String fitxerContentType) {
		this.fitxerContentType = fitxerContentType;
	}
	public String getFitxerNom() {
		return fitxerNom;
	}
	public void setFitxerNom(String fitxerNom) {
		this.fitxerNom = fitxerNom;
	}
	public byte[] getFitxerContingut() {
		return fitxerContingut;
	}
	public void setFitxerContingut(byte[] fitxerContingut) {
		this.fitxerContingut = fitxerContingut;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	/*public Date getDataCaptura() {
		return dataCaptura;
	}
	public void setDataCaptura(Date dataCaptura) {
		this.dataCaptura = dataCaptura;
	}
	public String getNtiOrgano() {
		return ntiOrgano;
	}
	public void setNtiOrgano(String ntiOrgano) {
		this.ntiOrgano = ntiOrgano;
	}
	public NtiOrigenEnumDto getNtiOrigen() {
		return ntiOrigen;
	}
	public void setNtiOrigen(NtiOrigenEnumDto ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}*/
	public DocumentNtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public void setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}
	/*public DocumentNtiTipoDocumentalEnumDto getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public void setNtiTipoDocumental(DocumentNtiTipoDocumentalEnumDto ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}*/
	public String getNtiIdDocumentoOrigen() {
		return ntiIdDocumentoOrigen;
	}
	public void setNtiIdDocumentoOrigen(String ntiIdDocumentoOrigen) {
		this.ntiIdDocumentoOrigen = ntiIdDocumentoOrigen;
	}

	public DocumentTipusFirmaEnumDto getTipusFirma() {
		return tipusFirma;
	}
	public void setTipusFirma(DocumentTipusFirmaEnumDto tipusFirma) {
		this.tipusFirma = tipusFirma;
	}
	public static DocumentCommand asCommand(DocumentDto dto) {
		DocumentCommand command = ConversioTipusHelper.convertir(
				dto,
				DocumentCommand.class);
		if (dto.getPare() != null)
			command.setPareId(dto.getPare().getId());
		if (dto.getMetaNode() != null)
			command.setMetaNodeId(dto.getMetaNode().getId());
		return command;
	}
	public static DocumentDto asDto(DocumentCommand command) throws IOException{
		DocumentDto dto = ConversioTipusHelper.convertir(
				command,
				DocumentDto.class);
			
		if (command.getArxiu() != null && !command.getArxiu().isEmpty()) {
			dto.setFitxerNom(command.getArxiu().getOriginalFilename());
			dto.setFitxerContentType(command.getArxiu().getContentType());
			dto.setFitxerContingut(command.getArxiu().getBytes());
		} else {
			dto.setFitxerNom(command.getFitxerNom());
			dto.setFitxerContentType(command.getFitxerContentType());
			dto.setFitxerContingut(command.getFitxerContingut());
		}

		if (command.getArxiu() != null && !command.getArxiu().isEmpty() && command.isAmbFirma()) {
			dto.setFirmaNom(command.getFirma().getOriginalFilename());
			dto.setFirmaContentType(command.getFirma().getContentType());
			dto.setFirmaContingut(command.getFirma().getBytes());
		} else if ((command.getArxiu() == null || command.getArxiu().isEmpty()) && command.isAmbFirma()){
			dto.setFirmaNom(command.getFitxerNom());
			dto.setFirmaContentType(command.getFitxerContentType());
			dto.setFirmaContingut(command.getFitxerContingut());
		}
//		else {
//			dto.setFitxerNom(command.getFitxerNom());
//			dto.setFitxerContentType(command.getFitxerContentType());
//			dto.setFitxerContingut(command.getFitxerContingut());
//		}
		
		dto.setFirmaSeparada(command.getTipusFirma() == DocumentTipusFirmaEnumDto.SEPARAT ? true : false);

		if (command.getMetaNodeId() != null) {
			MetaDocumentDto metaDocument = new MetaDocumentDto();
			metaDocument.setId(command.getMetaNodeId());
			dto.setMetaNode(metaDocument);
		}
		return dto;
	}

	public interface CreateDigital {}
	public interface UpdateDigital {}
	public interface CreateFisic {}
	public interface UpdateFisic {}
	
	public interface CreateFirmaSeparada {}
	public interface UpdateFirmaSeparada {}
	
	public enum DocumentFisicOrigenEnum {
		DISC,
		ESCANER
	}
}
