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
import org.springframework.web.multipart.MultipartFile;

import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.CodiMetaDocumentNoRepetit;
import es.caib.ripea.war.validation.PortafirmesDocumentTipusNotEmpty;
import es.caib.ripea.war.validation.ResponsableNotEmpty;

/**
 * Command per al manteniment de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ResponsableNotEmpty
@PortafirmesDocumentTipusNotEmpty
@CodiMetaDocumentNoRepetit(
		campId = "id",
		campCodi = "codi",
		campEntitatId = "entitatId",
		campMetaExpedientId = "metaExpedientId")
public class MetaDocumentCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	@Size(max=1024)
	private String descripcio;
	private boolean globalExpedient;
	@NotNull
	private MultiplicitatEnumDto multiplicitat;
	private boolean firmaPortafirmesActiva;
	@Size(max=64)
	private String portafirmesDocumentTipus;
	@Size(max=64)
	private String portafirmesFluxId;
	private String[] portafirmesResponsables;
	private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;
	@Size(max=64)
	private String portafirmesCustodiaTipus;
	private boolean firmaPassarelaActiva;
	@Size(max=64)
	private String firmaPassarelaCustodiaTipus;
	protected MultipartFile plantilla;
	private Long entitatId;
	private Long metaExpedientId;
	@NotNull
	private NtiOrigenEnumDto ntiOrigen;
	@NotNull
	private String ntiTipoDocumental;
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;	
	private boolean firmaBiometricaActiva;
	private boolean biometricaLectura;
	private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
	
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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public boolean isGlobalExpedient() {
		return globalExpedient;
	}
	public void setGlobalExpedient(boolean globalExpedient) {
		this.globalExpedient = globalExpedient;
	}
	public MultiplicitatEnumDto getMultiplicitat() {
		return multiplicitat;
	}
	public void setMultiplicitat(MultiplicitatEnumDto multiplicitat) {
		this.multiplicitat = multiplicitat;
	}
	public boolean isFirmaPortafirmesActiva() {
		return this.firmaPortafirmesActiva;
	}
	public void setFirmaPortafirmesActiva(boolean firmaPortafirmesActiva) {
		this.firmaPortafirmesActiva = firmaPortafirmesActiva;
	}
	public String getPortafirmesDocumentTipus() {
		return portafirmesDocumentTipus;
	}
	public void setPortafirmesDocumentTipus(String portafirmesDocumentTipus) {
		this.portafirmesDocumentTipus = portafirmesDocumentTipus;
	}
	public String getPortafirmesFluxId() {
		return portafirmesFluxId;
	}
	public void setPortafirmesFluxId(String portafirmesFluxId) {
		this.portafirmesFluxId = portafirmesFluxId;
	}
	public String[] getPortafirmesResponsables() {
		return portafirmesResponsables;
	}
	public void setPortafirmesResponsables(String[] portafirmesResponsables) {
		this.portafirmesResponsables = portafirmesResponsables;
	}
	public MetaDocumentFirmaSequenciaTipusEnumDto getPortafirmesSequenciaTipus() {
		return portafirmesSequenciaTipus;
	}
	public void setPortafirmesSequenciaTipus(MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus) {
		this.portafirmesSequenciaTipus = portafirmesSequenciaTipus;
	}
	public String getPortafirmesCustodiaTipus() {
		return portafirmesCustodiaTipus;
	}
	public void setPortafirmesCustodiaTipus(String portafirmesCustodiaTipus) {
		this.portafirmesCustodiaTipus = portafirmesCustodiaTipus;
	}
	public boolean isFirmaPassarelaActiva() {
		return firmaPassarelaActiva;
	}
	public void setFirmaPassarelaActiva(boolean firmaPassarelaActiva) {
		this.firmaPassarelaActiva = firmaPassarelaActiva;
	}
	public String getFirmaPassarelaCustodiaTipus() {
		return firmaPassarelaCustodiaTipus;
	}
	public void setFirmaPassarelaCustodiaTipus(String firmaPassarelaCustodiaTipus) {
		this.firmaPassarelaCustodiaTipus = firmaPassarelaCustodiaTipus;
	}
	public MultipartFile getPlantilla() {
		return plantilla;
	}
	public void setPlantilla(MultipartFile plantilla) {
		this.plantilla = plantilla;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public Long getMetaExpedientId() {
		return metaExpedientId;
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	public NtiOrigenEnumDto getNtiOrigen() {
		return ntiOrigen;
	}
	public void setNtiOrigen(NtiOrigenEnumDto ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	public String getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public void setNtiTipoDocumental(String ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}
	public DocumentNtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public void setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}
	public boolean isFirmaBiometricaActiva() {
		return firmaBiometricaActiva;
	}
	public void setFirmaBiometricaActiva(boolean firmaBiometricaActiva) {
		this.firmaBiometricaActiva = firmaBiometricaActiva;
	}
	public boolean isBiometricaLectura() {
		return biometricaLectura;
	}
	public void setBiometricaLectura(boolean biometricaLectura) {
		this.biometricaLectura = biometricaLectura;
	}
	public MetaDocumentFirmaFluxTipusEnumDto getPortafirmesFluxTipus() {
		return portafirmesFluxTipus;
	}
	public void setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus) {
		this.portafirmesFluxTipus = portafirmesFluxTipus;
	}
	
	public static List<MetaDocumentCommand> toEntitatCommands(
			List<MetaDocumentDto> dtos) {
		List<MetaDocumentCommand> commands = new ArrayList<MetaDocumentCommand>();
		for (MetaDocumentDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							MetaDocumentCommand.class));
		}
		return commands;
	}

	public static MetaDocumentCommand asCommand(MetaDocumentDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				MetaDocumentCommand.class);
	}
	public static MetaDocumentDto asDto(MetaDocumentCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				MetaDocumentDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
